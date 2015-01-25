/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2008 JMule team ( jmule@jmule.org / http://jmule.org )
 *
 *  Any parts of this program derived from other projects, or contributed
 *  by third-party developers are copyrighted by their respective authors.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package org.jmule.core.peermanager;

import static org.jmule.core.JMConstants.KEY_SEPARATOR;

import java.io.File;
import java.nio.ByteBuffer;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.bccrypto.JDKKeyFactory;
import org.jmule.core.bccrypto.SHA1WithRSAEncryption;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.configmanager.InternalConfigurationManager;
import org.jmule.core.downloadmanager.DownloadManagerSingleton;
import org.jmule.core.downloadmanager.InternalDownloadManager;
import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.ED2KConstants;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.edonkey.ED2KConstants.PeerFeatures;
import org.jmule.core.edonkey.metfile.ClientsMet;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.utils.Utils;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerException;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.peermanager.Peer.PeerSource;
import org.jmule.core.peermanager.Peer.PeerStatus;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.core.statistics.JMuleCoreStatsProvider;
import org.jmule.core.uploadmanager.InternalUploadManager;
import org.jmule.core.uploadmanager.UploadManagerSingleton;
import org.jmule.core.utils.Misc;
import org.jmule.core.utils.timer.JMTimer;
import org.jmule.core.utils.timer.JMTimerTask;

/**
 * 
 * @author binary256
 * @author javajox
 * @version $$Revision: 1.37 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/08/22 14:27:59 $$
 */
public class PeerManagerImpl extends JMuleAbstractManager implements InternalPeerManager {
	
	private static int STORE_CLIENTS_INTERVAL = 60000;
	private static int CREDITS_UPDATE_INTERVAL = 1000;
	
	private Map<String, Peer> peers  = new ConcurrentHashMap<String, Peer>();
	private Map<UserHash,Peer> peers_with_hash = new ConcurrentHashMap<UserHash, Peer>(); //used to optimize peer access
	private InternalNetworkManager _network_manager;
	
	private List<PeerManagerListener> listener_list = new LinkedList<PeerManagerListener>();
	
	private Map<UserHash, PeerCredit> credits = new ConcurrentHashMap<UserHash, PeerCredit>();
	private Map<UserHash, byte[]> sended_challenges = new ConcurrentHashMap<UserHash, byte[]>();
	private Map<UserHash, byte[]> challenge_to_send_peers = new ConcurrentHashMap<UserHash, byte[]>();
	
	private Map<UserHash, Long> last_download_bytes = new ConcurrentHashMap<UserHash, Long>();
	private Map<UserHash, Long> last_upload_bytes = new ConcurrentHashMap<UserHash, Long>();
	
	private Random challengeGenerator = new Random();
	
	private InternalDownloadManager _download_manager = null;
	private InternalUploadManager _upload_manager 	  = null;
	private InternalConfigurationManager _config_manager = null;
	
	private JMTimer maintenance_tasks = new JMTimer();
	
	PeerManagerImpl() {
	}
	
	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		Set<String> types = new HashSet<String>();	
		   types.add(JMuleCoreStats.ST_NET_PEERS_COUNT);
		   JMuleCoreStats.registerProvider(types, new JMuleCoreStatsProvider() {
			public void updateStats(Set<String> types, Map<String, Object> values) {
				if (types.contains(JMuleCoreStats.ST_NET_PEERS_COUNT)) {
					values.put(JMuleCoreStats.ST_NET_PEERS_COUNT, peers.size());
				}
			}
		   });
		_network_manager = (InternalNetworkManager) NetworkManagerSingleton
				.getInstance();
		_download_manager = (InternalDownloadManager) DownloadManagerSingleton
				.getInstance();
		_upload_manager = (InternalUploadManager) UploadManagerSingleton
				.getInstance();
		_config_manager = (InternalConfigurationManager) ConfigurationManagerSingleton.getInstance();
		File checkFile = new File(ConfigurationManager.CLIENTS_FILE);
		if (!checkFile.exists()) {
			try {
				ClientsMet clients_met = new ClientsMet(ConfigurationManager.CLIENTS_FILE);
				clients_met.store(credits.values());
			}catch(Throwable cause) { }
		} else {
			try {
				ClientsMet clients_met = new ClientsMet(ConfigurationManager.CLIENTS_FILE);
				Map<UserHash, PeerCredit> file_credits = clients_met.load();
				credits.putAll(file_credits);
			} catch (Throwable e) {
				e.printStackTrace();
			} 
		}
	}

	public void shutdown() {
		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return ;
		}
		maintenance_tasks.cancelAllTasks();
		for(Peer peer : peers.values()) {
			try {
				if (peer.isConnected())
					disconnect(peer);
			}catch(PeerManagerException e) {
				e.printStackTrace();
			}
		}
		
	}


	public void start() {
		try {
			super.start();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return ;
		}
		JMTimerTask peer_dropper = new JMTimerTask() {
			public void run() {
				for (Peer peer : peers.values()) {
					if (peer.getStatus() == PeerStatus.CONNECTING) {
						if (peer.isHighID()) {
							if (System.currentTimeMillis() - peer.getLastSeen() > ConfigurationManager.DROP_CONNECTING_HIGH_ID_PEER_TIMEOUT) {
								try {
									disconnect(peer);
								} catch (PeerManagerException e) {
									e.printStackTrace();
								}
							} } else if (System.currentTimeMillis() - peer.getLastSeen() > ConfigurationManager.DROP_CONNECTING_LOW_ID_PEER_TIMEOUT) {
								try {
									removePeer(peer);
								} catch (PeerManagerException e) {
									e.printStackTrace();
								}
							}

					} else if (!_download_manager.hasPeer(peer))
						if (!_upload_manager.hasPeer(peer)) {
							if (peer.isConnected()) {
								if (System.currentTimeMillis()- peer.getLastSeen() > ConfigurationManager.DROP_CONNECTED_PEER_TIMEOUT) {
									try {
										disconnect(peer);
									} catch (PeerManagerException e) {
										e.printStackTrace();
									}
								}
							} else if (System.currentTimeMillis() - peer.getLastSeen() > ConfigurationManager.DROP_DISCONNECTED_PEER_TIMEOUT) {
								try {
									removePeer(peer);
								} catch (PeerManagerException e) {
									e.printStackTrace();
								}
							}
						}
				}
			}
		};
		maintenance_tasks.addTask(peer_dropper, ConfigurationManager.PEERS_ACTIVITY_CHECK_INTERVAL, true);
		
		JMTimerTask cleanup_and_store_clients= new JMTimerTask() {
			public void run() {
				for(UserHash userHash : credits.keySet()) {
					PeerCredit credit = credits.get(userHash);
					if (System.currentTimeMillis() - credit.getLastSeen() >= ED2KConstants.PEER_CLIENTS_MET_EXPIRE_TIME)
						credits.remove(userHash);
				}
				try {
					ClientsMet clients_met = new ClientsMet(ConfigurationManager.CLIENTS_FILE);
					clients_met.store(credits.values());
				} catch (Throwable cause) {
					cause.printStackTrace();
				}
			}
		};
		maintenance_tasks.addTask(cleanup_and_store_clients, STORE_CLIENTS_INTERVAL, true);
		
		JMTimerTask credits_updater = new JMTimerTask() {
			
			public void run() {
				for(UserHash userHash : credits.keySet()) {
					Peer peer = getPeer(userHash);
					if (peer == null) continue;
					if (!peer.isConnected()) continue;
					long last_check_download_bytes = 0;
					long last_check_upload_bytes = 0;
					if (last_download_bytes.containsKey(userHash))
						last_check_download_bytes = last_download_bytes.get(userHash);
					if (last_upload_bytes.containsKey(userHash))
						last_check_upload_bytes = last_upload_bytes.get(userHash);
					
					long downloaded = _network_manager.getFileDownloadedBytes(peer.getIP(),peer.getPort());
					long uploaded   = _network_manager.getFileUploadedBytes(peer.getIP(),peer.getPort());
					
					long d = downloaded - last_check_download_bytes;
					if (d < 0) 
						d = downloaded;
					long u = uploaded - last_check_upload_bytes;
					if (u < 0)
						u = uploaded;
					
					PeerCredit credit = credits.get(userHash);
					credit.addDownload(d);
					credit.addUpload(u);
					credit.setLastSeen(System.currentTimeMillis());
					
					last_download_bytes.put(userHash, downloaded);
					last_upload_bytes.put(userHash, uploaded);
				}
			}
		};
				
		maintenance_tasks.addTask(credits_updater, CREDITS_UPDATE_INTERVAL, true);
	}

	protected boolean iAmStoppable() {
		return false;
	}
	
	public float getCredit(Peer peer) {
		if (!credits.containsKey(peer.getUserHash()))
			return 1;
		PeerCredit credit = credits.get(peer.getUserHash());
		long upload = credit.getUpload();
		long download = credit.getDownload();
		if ((upload / (1024 * 1024)) < 1f) return 1;
		if (download == 0) return 10;
		
		float ratio1 = upload * 2 / download;
		float ratio2 = (float) Math.sqrt((double)(upload + 2));
		
		float ratio = ratio1;
		if (ratio > ratio2)
			ratio = ratio2;
		if (ratio > 10)
			ratio = 10;
		if (ratio < 1)
			ratio = 1;
		return ratio;
	}
	
	public byte[] getPublicKey(Peer peer) {
		if (!credits.containsKey(peer.getUserHash()))
			return null;
		PeerCredit credit = credits.get(peer.getUserHash());
		byte[] public_key = credit.getAbySecureIdent();
		if (public_key == null) return null;
		if (Misc.isEmpty(public_key))
			return null;
		return public_key;
	}
	
	public boolean hasPublicKey(Peer peer) {
		if (!credits.containsKey(peer.getUserHash()))
			return false;
		PeerCredit credit = credits.get(peer.getUserHash());
		byte[] public_key = credit.getAbySecureIdent();
		if (public_key == null) return false;
		if (Misc.isEmpty(public_key))
			return false;
		return true;
	}
	
	public boolean isSecured(Peer peer) {
		if (!credits.containsKey(peer.getUserHash()))
			return false;
		PeerCredit credit = credits.get(peer.getUserHash());
		return credit.isVerified();
	}
	
	public void setSecured(Peer peer, boolean verified) {
		if (!credits.containsKey(peer.getUserHash()))
			return;
		PeerCredit credit = credits.get(peer.getUserHash());
		credit.setVerified(verified);
	}
	
	private void addIfNeedNewPeerCreditInfo(Peer peer) {
		if (credits.containsKey(peer.getUserHash()))
			return;
		PeerCredit credit_info = new PeerCredit(peer.getUserHash());
		credits.put(peer.getUserHash(), credit_info);
	}
	
	public boolean isVerified(Peer peer) {
		if (!credits.containsKey(peer.getUserHash()))
			return false;
		PeerCredit credit = credits.get(peer.getUserHash());
		return credit.isVerified();
	}
	
	public void receivedPublicKey(String peerIP, int peerPort, byte[] key) {
		try {
			Peer peer = getPeer(peerIP, peerPort);
			if (getPublicKey(peer)==null) {
				addIfNeedNewPeerCreditInfo(peer);
				PeerCredit credit_info = credits.get(peer.getUserHash());
				credit_info.setAbySecureIdent(key);
				if (challenge_to_send_peers.containsKey(peer.getUserHash())) {
					byte[] challenge = challenge_to_send_peers.get(peer.getUserHash());
					challenge_to_send_peers.remove(peer.getUserHash());
					
					_network_manager.sendSignaturePacket(peerIP, peerPort, challenge);
				}
			} else {
				// public key is already known, must ban peer
			}
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}
	
	public void receivedSignature(String peerIP, int peerPort, byte[] signature) {
		try {
			Peer peer = getPeer(peerIP, peerPort);
			if (!sended_challenges.containsKey(peer.getUserHash())) {
				return ;
			}
			byte[] random_challenge = sended_challenges.get(peer.getUserHash());
			sended_challenges.remove(peer.getUserHash());
			byte[] public_key = getPublicKey(peer);
			if (public_key == null) {
				return ;
			}
			SHA1WithRSAEncryption verifier = new SHA1WithRSAEncryption();
			JDKKeyFactory.RSA rsa_key_factory = new JDKKeyFactory.RSA();
			X509EncodedKeySpec public_key_spec = new X509EncodedKeySpec(public_key);
			verifier.initVerify(rsa_key_factory.engineGeneratePublic(public_key_spec));
			verifier.update(_config_manager.getPublicKey().getEncoded());
			verifier.update(random_challenge);
			boolean verified = verifier.verify(signature);
			if (verified) {
				PeerCredit info = credits.get(peer.getUserHash());
				info.setVerified(true);
			} else {
				
			}
		}catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void receivedSecIdentState(String peerIP, int peerPort, byte state, byte[] challenge) {
		try {
			Peer peer = getPeer(peerIP, peerPort);
			if (state == 1) {
				if (hasPublicKey(peer))
					_network_manager.sendSignaturePacket(peerIP, peerPort, challenge);
				else
					challenge_to_send_peers.put(peer.getUserHash(), challenge);
			} 
			if (state == 2){
				_network_manager.sendPublicKeyPacket(peerIP, peerPort);
				if (hasPublicKey(peer))
					_network_manager.sendSignaturePacket(peerIP, peerPort, challenge);
				else
					challenge_to_send_peers.put(peer.getUserHash(), challenge);
			}
		}catch(Throwable cause) { cause.printStackTrace();}
	}
	
	public List<Peer> getPeers() {
		List<Peer> list = new ArrayList<Peer>();
		list.addAll(peers.values());
		return list;
	}
	
	public Peer getPeer(String ip, int port) throws PeerManagerException {
		Peer peer = peers.get(ip + KEY_SEPARATOR + port);
		if (peer == null) 
			throw new PeerManagerException("Peer " + ip + KEY_SEPARATOR + port + " not found");
		return peer;
	}
	
	private Peer getPeer(UserHash userHash) {
		return peers_with_hash.get(userHash);
	}
	
	public Peer newPeer(String ip, int port, PeerSource source) throws PeerManagerException {
		if (hasPeer(ip, port))
			throw new PeerManagerException("Peer already exists");
		Peer peer = new Peer(ip, port, source);
		peers.put(ip + KEY_SEPARATOR + port, peer);
		peer.setStatus(PeerStatus.DISCONNECTED);
		notifyNewPeer(peer);		
		return peer;
	}
	
	public void removePeer(Peer peer) throws PeerManagerException {
		String ip = peer.getIP();
		int port = peer.getPort();
		if (! hasPeer(ip, port)) 
			throw new PeerManagerException(" Peer " + ip + " : " + port + " not found");
		if(peer.isHighID())
			if (peer.getStatus()!= PeerStatus.DISCONNECTED)
				_network_manager.disconnectPeer(ip, port);
		
		System.out.println("Peer manager :: remove peer :: " + peer);
		
		peers.remove(ip + KEY_SEPARATOR + port);
		
		_download_manager.peerRemoved(peer);
		_upload_manager.peerRemoved(peer);
		
		notifyPeerRemoved(peer);
	}
	
	public Peer newIncomingPeer(String ip, int port) throws PeerManagerException {
		Peer peer;
		
		if (hasPeer(ip, port)) {
			peer = getPeer(ip, port);
		} else {
			peer = new Peer(ip, port, PeerSource.INCOMING);
		}
		peer.setStatus(PeerStatus.CONNECTING);
		peers.put(ip + KEY_SEPARATOR + port, peer);
		
		notifyNewPeer(peer);
		return peer;
		
	}

	/**
	 * Search replication peer and replace it
	 * @param peer
	 * @return
	 */
	private boolean replaceLowIDPeer(Peer peer) {
		Peer founded_peer = null;
		for(Peer stored_peer : peers.values()) {
			if (stored_peer.getClientID().equals(peer.getClientID()))
				if (!stored_peer.isConnected()) {
					founded_peer = stored_peer;
					break;
				}
		}
		//System.out.println("Founded low ID peer : " + founded_peer +"\n for peer : " + peer);
		if (founded_peer != null) {
			String founded_peer_key = founded_peer.getIP() + KEY_SEPARATOR + founded_peer.getPort();
			
			//System.out.println("replaceLowIDPeer");
			//System.out.println("Search key : " + founded_peer_key);
			//System.out.println("Replace : " + peer + "\n"+founded_peer);
			founded_peer.copyFields(peer);
			String remove_peer_key = peer.getIP() + KEY_SEPARATOR + peer.getPort();
			peers.remove(remove_peer_key);
			peers.remove(founded_peer_key);
			peers.put(remove_peer_key, founded_peer);
			return true;
		}
		return false;
	}
	
	public void helloAnswerFromPeer(String peerIP, int peerPort,
			UserHash userHash, ClientID clientID, int peerListenPort,
			TagList tagList, String serverIP, int serverPort) {
		Peer peer = null;
		try {
			peer = getPeer(peerIP, peerPort);
		} catch (PeerManagerException e) {
			e.printStackTrace();
			return ;
		}
		if (peerListenPort != 0) {
			peers.remove(peerIP + KEY_SEPARATOR + peerPort);
			if (hasPeer(peerIP, peerListenPort)) {
				peers.get(peerIP + KEY_SEPARATOR + peerListenPort).copyFields(peer);
				try {
					peer = getPeer(peerIP, peerListenPort);
				} catch (PeerManagerException e) {
					e.printStackTrace();
				}
			} else
				peers.put(peerIP + KEY_SEPARATOR + peerListenPort, peer);
			peer.setListenPort(peerListenPort);
		}
		
		peer.setStatus(PeerStatus.CONNECTED);
		peer.setUserHash(userHash);
		peer.setClientID(clientID);
		peer.setTagList(tagList);
		peer.setServer(serverIP, serverPort);
		if (!peer.isHighID())
		if (replaceLowIDPeer(peer)) {
			try {
				peer = getPeer(peerIP, peerListenPort);
				peer.setStatus(PeerStatus.CONNECTED);
			} catch (PeerManagerException e) {
				e.printStackTrace();
				return ;
			}
		}
		
		_download_manager.peerConnected(peer);
		_upload_manager.peerConnected(peer);
		
		peers_with_hash.put(userHash, peer);
		notifyPeerConnected(peer);
	}

	public void helloFromPeer(String peerIP, int peerPort, UserHash userHash,
			ClientID clientID, int peerListenPort, TagList tagList,
			String serverIP, int serverPort) {
		Peer peer = null;
		try {
			peer = getPeer(peerIP, peerPort);
		} catch (PeerManagerException e) {
			e.printStackTrace();
			return ;
		}
				
		if (peerListenPort != 0) {
			peers.remove(peerIP + KEY_SEPARATOR + peerPort);
			if (hasPeer(peerIP, peerListenPort)) {
				peers.get(peerIP + KEY_SEPARATOR + peerListenPort).copyFields(peer);
				try {
					peer = getPeer(peerIP, peerListenPort);
				} catch (PeerManagerException e) {
					e.printStackTrace();
				}
			} else
				peers.put(peerIP + KEY_SEPARATOR + peerListenPort, peer);
			peer.setListenPort(peerListenPort);
		}
		
		peer.setStatus(PeerStatus.CONNECTED);
		peer.setUserHash(userHash);
		peer.setClientID(clientID);
		peer.setTagList(tagList);
		peer.setServer(serverIP, serverPort);
		if (!peer.isHighID())
		if (replaceLowIDPeer(peer)) {
			try {
				peer = getPeer(peerIP, peerListenPort);
				peer.setStatus(PeerStatus.CONNECTED);
			} catch (PeerManagerException e) {
				e.printStackTrace();
				return ;
			}
		}
		
		_download_manager.peerConnected(peer);
		_upload_manager.peerConnected(peer);
		
		peers_with_hash.put(userHash, peer);
		
		notifyPeerConnected(peer);
	}
	
	public void connect(Peer peer) throws PeerManagerException {
		String ip = peer.getIP();
		int port  = peer.getPort();
		if (!hasPeer(ip, port))
			throw new PeerManagerException("Peer " + ip + KEY_SEPARATOR + port + " not found");
		try {
			
			_network_manager.addPeer(ip, port);
		}catch(NetworkManagerException cause) {
			throw new PeerManagerException(cause);
		}
		peer.setStatus(PeerStatus.CONNECTING);
		notifyPeerConnecting(peer);	
	}

	public void disconnect(Peer peer) throws PeerManagerException {
		String ip = peer.getIP();
		int port  = peer.getPort(); 
		if (!hasPeer(ip, port))
			throw new PeerManagerException("Peer " + ip + KEY_SEPARATOR + port + " not found");
		
		_network_manager.disconnectPeer(ip, port);
		peer.setStatus(PeerStatus.DISCONNECTED);
		_download_manager.peerDisconnected(peer);
		_upload_manager.peerDisconnected(peer);
		
		notifyPeerDisconnected(peer);
		
		
	}
	
	public void peerConnectingFailed(String ip, int port, Throwable cause) {
		Peer peer = null;
		try {
			peer = getPeer(ip, port);
		} catch (PeerManagerException e) {
			e.printStackTrace();
			return ;
		}
		peer.setStatus(PeerStatus.DISCONNECTED);
		_download_manager.peerConnectingFailed(peer, cause);
		_upload_manager.peerConnectingFailed(peer, cause);
		notifyPeerConnectingFailed(peer, cause);
	}
	
	public void peerDisconnected(String ip, int port) {
		Peer peer;
		try {
			peer = getPeer(ip, port);
		} catch (PeerManagerException e) {			
			e.printStackTrace();
			return;
		}
		peer.setStatus(PeerStatus.DISCONNECTED);
		_download_manager.peerDisconnected(peer);
		_upload_manager.peerDisconnected(peer);
		if (peer.getUserHash()!=null)
			peers_with_hash.remove(peer.getUserHash());
		notifyPeerDisconnected(peer);
	}

	public boolean hasPeer(String ip, int port) {
		return peers.containsKey(ip + KEY_SEPARATOR + port);
	}

	public void callBackRequestFailed() {
	
	}

	public void receivedCallBackRequest(String ip, int port, PeerSource source) {
		try {
			Peer peer = newPeer(ip, port, source);
			connect(peer);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
		
	}

	public void receivedEMuleHelloFromPeer(String ip, int port,
			byte clientVersion, byte protocolVersion, TagList tagList) {
		try {
			Peer peer = getPeer(ip, port);
			Map<PeerFeatures,Integer> peer_features = Utils.scanTagListPeerFeatures(tagList);
			peer_features.put(PeerFeatures.ProtocolVersion, (int)protocolVersion);
			peer.peer_features.putAll(peer_features);
			
			Tag udp_port = tagList.getTag(ED2KConstants.ET_UDPPORT);
			if (udp_port != null)
				peer.tag_list.addTag(udp_port);
			
			if (_config_manager.isSecurityIdenficiationEnabled()) {
				//Integer secident = peer_features.get(PeerFeatures.SupportSecIdent);
				//if (secident == null)
				//	return;
				//if (secident != 0)
				//	return;
				if (isSecured(peer))
					return;
				
				sendSecIdentState(peer);
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void receivedEMuleHelloAnswerFromPeer(String ip, int port,
			byte clientVersion, byte protocolVersion, TagList tagList) {
		try {
			Peer peer = getPeer(ip, port);
			Map<PeerFeatures,Integer> peer_features = Utils.scanTagListPeerFeatures(tagList);
			peer_features.put(PeerFeatures.ProtocolVersion, (int)protocolVersion);
			peer.peer_features.putAll(peer_features);
			
			Tag udp_port = tagList.getTag(ED2KConstants.ET_UDPPORT);
			if (udp_port != null)
				peer.tag_list.addTag(udp_port);
			
			if (_config_manager.isSecurityIdenficiationEnabled()) {
				//Integer secident = peer_features.get(PeerFeatures.SupportSecIdent);
				//if (secident == null)
				//	return;
				//if (secident == 0)
				//	return;
				if (isSecured(peer))
					return;
				
				sendSecIdentState(peer);
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void sendSecIdentState(Peer peer) {
		byte[] challenge = new byte[4];
		challengeGenerator.nextBytes(challenge);
		sended_challenges.put(peer.getUserHash(), challenge);
		boolean need_key = !hasPublicKey(peer);
		_network_manager.sendSecIdentStatePacket(peer.getIP(), peer.getPort(), need_key,
				challenge);
	}
	
	public void addPeerManagerListener(PeerManagerListener listener) {
		listener_list.add(listener);
	}
	
	public void removePeerManagerListener(PeerManagerListener listener) {
		listener_list.remove(listener);
	}

	public List<Peer> createPeerList(List<String> peerIPList, List<Integer> peerPort, boolean addKnownPeersInList, String serverIP, int serverPort, PeerSource peerSource) {
		List<Peer> result = new ArrayList<Peer>();
		
		for (int i = 0; i < peerIPList.size(); i++) {
			String peer_ip = peerIPList.get(i);
			int peer_port = peerPort.get(i);
			if (hasPeer(peer_ip, peer_port)) { 
				if (addKnownPeersInList) {
					try {
						Peer peer = getPeer(peer_ip, peer_port);
						result.add(peer);
					} catch (PeerManagerException e) {
						e.printStackTrace();
					}
				}
				continue;
			}
			try {
				Peer peer = newPeer(peer_ip, peer_port, peerSource);
				peer.setServer(serverIP, serverPort);
				result.add(peer);
			} catch (PeerManagerException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public void sendMessage(Peer peer, String message) throws PeerManagerException{
		if (!peer.isConnected())
			throw new PeerManagerException("Peer not connected");
		_network_manager.sendMessage(peer.getIP(), peer.getPort(), message);
	}
	
	public void receivedMessage(String ip, int port, String message) {
		Peer peer;
		try {
			peer = getPeer(ip, port);
			notifyPeerMessage(peer, message);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}
	
	public void receivedCaptchaImage(String ip, int port, ByteBuffer image) {
		Peer peer;
		try {
			peer = getPeer(ip, port);
			notifyPeerCaptchaImage(peer, image);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}
	
	public void receivedCaptchaStatusAnswer(String ip, int port, byte answer) {
		Peer peer;
		try {
			peer = getPeer(ip, port);
			notifyPeerCaptchaStatusAnswer(peer, answer);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		String result = "";
		
		for(String key : peers.keySet()) {
			result += "[" + key +"]= " + "["+peers.get(key)+"]\n";
		} 
		
		return result;
	}
	
	private void notifyNewPeer(Peer peer) {
		for(PeerManagerListener listener : listener_list) 
			try {
				listener.newPeer(peer);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}

	
	private void notifyPeerRemoved(Peer peer) {
		for(PeerManagerListener listener : listener_list) 
			try {
				listener.peerRemoved(peer);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyPeerConnecting(Peer peer) {
		for(PeerManagerListener listener : listener_list) 
			try {
				listener.peerConnecting(peer);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyPeerConnected(Peer peer) {
		for(PeerManagerListener listener : listener_list) 
			try {
				listener.peerConnected(peer);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyPeerDisconnected(Peer peer) {
		for(PeerManagerListener listener : listener_list) 
			try {
				listener.peerDisconnected(peer);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyPeerConnectingFailed(Peer peer, Throwable cause) {
		for(PeerManagerListener listener : listener_list) 
			try {
				listener.peerConnectingFailed(peer, cause);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	
	private void notifyPeerMessage(Peer peer, String message) {
		for(PeerManagerListener listener : listener_list) 
			try {
				listener.peerMessage(peer, message);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyPeerCaptchaImage(Peer peer, ByteBuffer image) {
		for(PeerManagerListener listener : listener_list) 
			try {
				listener.peerCaptchaImage(peer, image);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyPeerCaptchaStatusAnswer(Peer peer, byte answer) {
		for(PeerManagerListener listener : listener_list) 
			try {
				listener.peerCaptchaStatusAnswer(peer, answer);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	public void bannedNode(int ipAsInt) {
		Collection<Peer> ps = peers.values();
		for( Peer p : ps )
			if( p.getIPAsInt() == ipAsInt ) {
				try {
				   this.disconnect( p );
				}catch( Throwable cause ) {
					cause.printStackTrace();
				}
			}
	}
}
