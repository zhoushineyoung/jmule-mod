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
package org.jmule.core.downloadmanager;

import static org.jmule.core.downloadmanager.PeerDownloadStatus.ACTIVE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.JMThread;
import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.configmanager.InternalConfigurationManager;
import org.jmule.core.edonkey.ED2KConstants;
import org.jmule.core.edonkey.ED2KConstants.ServerFeatures;
import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.InternalJKadManager;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.JKadException;
import org.jmule.core.jkad.JKadManagerSingleton;
import org.jmule.core.jkad.indexer.Source;
import org.jmule.core.jkad.search.Search;
import org.jmule.core.jkad.search.SearchResultListener;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.peermanager.InternalPeerManager;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.peermanager.Peer.PeerSource;
import org.jmule.core.peermanager.PeerManagerException;
import org.jmule.core.peermanager.PeerManagerSingleton;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.core.servermanager.InternalServerManager;
import org.jmule.core.servermanager.Server;
import org.jmule.core.servermanager.ServerManagerSingleton;
import org.jmule.core.sharingmanager.JMuleBitSet;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.core.statistics.JMuleCoreStatsProvider;
import org.jmule.core.uploadmanager.InternalUploadManager;
import org.jmule.core.uploadmanager.UploadManagerSingleton;
import org.jmule.core.utils.timer.JMTimer;
import org.jmule.core.utils.timer.JMTimerTask;

/**
 * Created on 2008-Jul-08
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.49 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2011/04/25 11:20:51 $$
 */
class DownloadManagerImpl extends JMuleAbstractManager implements InternalDownloadManager {

	private Map<FileHash, DownloadSession> session_list = new ConcurrentHashMap<FileHash, DownloadSession>();

	private List<DownloadManagerListener> download_manager_listeners = new LinkedList<DownloadManagerListener>();
	private List<NeedMorePeersListener> need_more_peers_listeners = new LinkedList<NeedMorePeersListener>();
	
	// this data structure is the helper for addAndStartSilentDownloads and addAndStartSilentDownloads methods 
    private List<ED2KFileLink> ed2k_links_add_helper = new CopyOnWriteArrayList<ED2KFileLink>();
	
	private Timer need_more_peers_timer;
	
	private static final long   NEED_MORE_PEERS_INTERVAL           =    60*1000;
	private static final float  MEDIUM_SPEED_CONSIDERED_AS_SMALL   =    10*1000;
	
	private final static long PEER_RESEND_PACKET_INTERVAL 	= 1000 * 10;
	private final static long UNUSED_PEER_ACTIVATION 		= 1000 * 10;
	private final static int DOWNLOAD_PEERS_MONITOR_INTERVAL= 1000;
	private final static long QUEUE_PEERS_CHECK_TIMEOUT 	= 1000 * 60 * 15;
	private final static long QUEUE_PEERS_DROP_TIMEOUT 	= 1000 * 60 * 30;
	
	private InternalNetworkManager _network_manager = null;
	private InternalJKadManager _jkad = null;
	private InternalPeerManager _peer_manager = null;
	private InternalUploadManager _upload_manager = null;
	private InternalServerManager _server_manager = null;
	private InternalConfigurationManager _config_manager = null;
	
	private JMTimer maintenance_tasks = new JMTimer();
	private long last_server_sources_request = 0;
	private JMTimerTask server_sources_query = null;
	private JMTimerTask kad_source_search_task = null;
	private JMTimerTask pex_source_search_task = null;
	private JMTimerTask global_source_search_task = null;
	private JMTimerTask download_peers_monitor = null;
	
	private Queue<FileHash> server_sources_queue = new ConcurrentLinkedQueue<FileHash>();
	private Queue<FileHash> kad_sources_queue = new ConcurrentLinkedQueue<FileHash>();
	private Queue<FileHash> pex_sources_queue = new ConcurrentLinkedQueue<FileHash>();
	private Queue<FileHash> global_sources_queue = new ConcurrentLinkedQueue<FileHash>();
	
	private Queue<DownloadSession> file_part_to_check = new ConcurrentLinkedQueue<DownloadSession>();
	private Queue<DownloadSession> complete_file_to_check = new ConcurrentLinkedQueue<DownloadSession>();
	
	private DownloadFileChecker download_file_checker;
	
	public DownloadManagerImpl() {
		
	}
	private Map<FileHash, Long> last_download_check = new HashMap<FileHash, Long>();
	private long total_downloaded_bytes = 0;
	private JMTimerTask download_bytes_counter;
	
	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		
		_network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		_jkad = (InternalJKadManager) JKadManagerSingleton.getInstance();
		_peer_manager = (InternalPeerManager) PeerManagerSingleton.getInstance();
		_upload_manager = (InternalUploadManager) UploadManagerSingleton.getInstance();
		_server_manager = (InternalServerManager) ServerManagerSingleton.getInstance();
		_config_manager = (InternalConfigurationManager) ConfigurationManagerSingleton.getInstance();
		
		Set<String> types = new HashSet<String>();
		types.add(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_BYTES);
		types.add(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_COUNT);
		types.add(JMuleCoreStats.ST_NET_PEERS_DOWNLOAD_COUNT);
		JMuleCoreStats.registerProvider(types, new JMuleCoreStatsProvider() {
			public void updateStats(Set<String> types,
					Map<String, Object> values) {
				if (types.contains(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_BYTES)) {
					
					/*for (DownloadSession session : session_list.values()) {
						total_downloaded_bytes += session.getTransferredBytes();
					}*/
					values.put(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_BYTES,
							total_downloaded_bytes);
				}
				if (types.contains(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_COUNT)) {
					values.put(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_COUNT,
							session_list.size());
				}
				if (types.contains(JMuleCoreStats.ST_NET_PEERS_DOWNLOAD_COUNT)) {
					int download_peers_count = 0;
					for (DownloadSession session : session_list.values()) {
						download_peers_count += session.getPeerCount();
					}
					values.put(JMuleCoreStats.ST_NET_PEERS_DOWNLOAD_COUNT, download_peers_count);
				}
			}
		});
		
		download_bytes_counter = new JMTimerTask() {
			
			@Override
			public void run() {
				List<FileHash> to_remove = new ArrayList<FileHash>();
				for(FileHash hash : last_download_check.keySet()) {
					if (hasDownload(hash)) {
						try {
							DownloadSession session = getDownload(hash);
							long downloaded_bytes = session.getTransferredBytes() - last_download_check.get(hash);
							last_download_check.put(hash, session.getTransferredBytes());
							total_downloaded_bytes += downloaded_bytes;
						} catch (DownloadManagerException e) {
							e.printStackTrace();
						}
					} else
						to_remove.add(hash);
				}
				
				for(FileHash hash : to_remove)
					last_download_check.remove(hash);
				
				for(DownloadSession session : session_list.values()) {
					if (!last_download_check.containsKey(session.getFileHash())) {
						total_downloaded_bytes += session.getTransferredBytes();
						last_download_check.put(session.getFileHash(), session.getTransferredBytes());
					}
				}
				
			}
		};
		
		server_sources_query = new JMTimerTask() {	
	
			public void run() {
				if (server_sources_queue.isEmpty()) {
					return;
				}
				
				last_server_sources_request = System.currentTimeMillis();
				
				List<FileHash> sessions_to_request = new ArrayList<FileHash>();
				while ((!server_sources_queue.isEmpty()) && (sessions_to_request.size() < ED2KConstants.SERVER_FILE_SOURCES_QUERY_ITERATION)) {
					FileHash hash = server_sources_queue.poll();
					sessions_to_request.add(hash);
				}
				
				server_sources_queue.addAll(sessions_to_request);
				for(FileHash hash : sessions_to_request) { 
					DownloadSession session;
					try {
						session = getDownload(hash);
						_network_manager.requestSourcesFromServer(session.getFileHash(), session.getSharedFile().length());
					} catch (DownloadManagerException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		global_source_search_task = new JMTimerTask() {
			
			Map<FileHash, Long> last_global_sarch = new HashMap<FileHash, Long>();
			
			public void run() {
				Collection<FileHash> to_remove = new ArrayList<FileHash>();
				for(FileHash hash : last_global_sarch.keySet()) 
					if (System.currentTimeMillis() - last_global_sarch.get(hash) > ED2KConstants.TIME_15_MINS)
						to_remove.add(hash);
				for(FileHash hash : to_remove)
					last_global_sarch.remove(hash);
				
				try {
					if (!_config_manager.isUDPEnabled())
						return;
				} catch (ConfigurationManagerException e1) {
					e1.printStackTrace();
				}
				List<FileHash> hash_list = new ArrayList<FileHash>();
				List<FileHash> used_hash = new ArrayList<FileHash>();
				List<Long> size_list = new ArrayList<Long>();
				while ((!global_sources_queue.isEmpty()) && (hash_list.size() < ED2KConstants.GLOBAL_FILE_SOURCES_QUERY_ITERATION)) {
					FileHash hash = global_sources_queue.poll();
					used_hash.add(hash);
					if (!last_global_sarch.containsKey(hash)) {
						try {
							hash_list.add(hash);
							size_list.add(getDownload(hash).getFileSize());
						} catch (DownloadManagerException e) {
							e.printStackTrace();
						}
					}
				}
				
				global_sources_queue.addAll(used_hash);
				if (hash_list.isEmpty())
					return;
				List<Server> server_list = _server_manager.getServers();
				for(Server server : server_list) {
					if (!server.getFeatures().contains(ServerFeatures.GetSources2))
							_network_manager.sendServerUDPSourcesRequest(server.getAddress(), server.getUDPPort(), hash_list);
					else
					if (server.getFeatures().contains(ServerFeatures.GetSources2))
						_network_manager.sendServerUDPSources2Request(server.getAddress(), server.getUDPPort(), hash_list, size_list);
				}
				
				for(FileHash hash : hash_list)
					last_global_sarch.put(hash, System.currentTimeMillis());
			}
		};
		
		kad_source_search_task = new JMTimerTask() {
			Int128 error_id = null;
			Int128 prev_search = null;
			
			Map<FileHash, Long> last_file_search = new HashMap<FileHash, Long>();
			public void run() { 
				error_id = null;
				Search search = _jkad.getSearch();
				
				Collection<FileHash> to_remove = new ArrayList<FileHash>();
				
				for(FileHash hash : last_file_search.keySet()) 
					if (System.currentTimeMillis() - last_file_search.get(hash) > ED2KConstants.TIME_15_MINS)
						to_remove.add(hash);
				
				for(FileHash hash : to_remove)
					last_file_search.remove(hash);
				
				while(true) {
					if (prev_search != null)
						if (_jkad.getLookup().hasTask(prev_search))
							return;
					
					final FileHash fileHash = kad_sources_queue.poll();
					if (fileHash == null) {
						prev_search = null;
						return;
					}
					if (!hasDownload(fileHash)) {
						continue;
					}
					kad_sources_queue.offer(fileHash);
					
					if (last_file_search.containsKey(fileHash))
						return;
					
					byte[] hash = fileHash.getHash().clone();
					org.jmule.core.jkad.utils.Convert.updateSearchID(hash);
					final Int128 search_id = new Int128(hash);
					if (error_id != null)
						if (error_id.equals(search_id))
							return;
					if (_jkad.getLookup().hasTask(search_id)) {
						error_id = search_id;
						continue;
					}
					
					final DownloadSession download_session = session_list.get(fileHash);
					try {
						search.searchSources(search_id,new SearchResultListener() {
									public void processNewResults(List<Source> result) {
										List<Peer> peer_list = new ArrayList<Peer>();
										for (Source source : result) {
											String address = source.getAddress().toString();
											int tcpPort = source.getTCPPort();
											if (tcpPort == -1) continue;
											if (download_session.hasPeer(address, tcpPort)) continue;
											Peer peer;
											if (!_peer_manager.hasPeer(address, tcpPort)) {
												try {
													peer = _peer_manager.newPeer(address, tcpPort,PeerSource.KAD);
													peer_list.add(peer);
												} catch (PeerManagerException e) {
													e.printStackTrace();
													continue;
												} 
											} else {
												try {
													peer = _peer_manager.getPeer(address, tcpPort);
													peer_list.add(peer);
												} catch (PeerManagerException e) {
													e.printStackTrace();
													continue;
												}
											}
											
											
										}
										download_session.addDownloadPeers(peer_list);
									}

									public void searchFinished() {
										last_file_search.put(fileHash, System.currentTimeMillis());
									}

									public void searchStarted() {
									}

								},download_session.getSharedFile().length());
						prev_search = search_id;
					} catch (JKadException e) {
						e.printStackTrace();
						error_id = search_id;
						continue;
					}
					return;
				}
			}
		};
		
		pex_source_search_task = new JMTimerTask() {
			Random random_peer_check = new Random();
			public void run() {
				while(true) {
					FileHash hash = pex_sources_queue.poll();
					if (hash == null) {
						return;
					}
					if (!hasDownload(hash)) {
						continue;
					}
					pex_sources_queue.offer(hash);
					DownloadSession session = session_list.get(hash);
					if (session.getPeerCount() > ED2KConstants.RARE_SHARED_SOURCES_FILE) {
						// queue only one peer for sources
						List<Peer> session_peers = session.download_status_list.getPeersByStatus(PeerDownloadStatus.IN_QUEUE,PeerDownloadStatus.ACTIVE, PeerDownloadStatus.ACTIVE_UNUSED);
						List<Peer> used_peers = new ArrayList<Peer>();
						while(true) {
							if (used_peers.size()>=session_peers.size())
								return;
							int peer_id = random_peer_check.nextInt(session_peers.size());
							Peer peer = session_peers.get(peer_id);
							used_peers.add(peer);
							if (!peer.isConnected()) continue;
							if (!peer.isHighID()) continue;
							_network_manager.sendSourcesRequest(peer.getIP(), peer.getPort(), session.getFileHash());
							return;
						}
					} else {
						// rare file, queue all known sources
						List<Peer> session_peers = session.download_status_list.getPeersByStatus(PeerDownloadStatus.IN_QUEUE,PeerDownloadStatus.ACTIVE, PeerDownloadStatus.ACTIVE_UNUSED);
						for(Peer peer : session_peers) {
							if (!peer.isConnected()) continue;
							if (!peer.isHighID()) continue;
							_network_manager.sendSourcesRequest(peer.getIP(), peer.getPort(), session.getFileHash());
						}
						return;
					}
				}
			}
		};
		
		download_peers_monitor = new JMTimerTask() {
			@Override
			public void run() {

				for(DownloadSession session : session_list.values()) {
					List<Peer> status_not_reponse_list = session.download_status_list.getPeersWithInactiveTime(PEER_RESEND_PACKET_INTERVAL, PeerDownloadStatus.FILE_STATUS_REQUEST);
					for(Peer peer : status_not_reponse_list)
						if (peer.isConnected())
							session.requestStatusFileName(peer);
						else
							session.download_status_list.setPeerStatus(peer, PeerDownloadStatus.DISCONNECTED);
					
					
					List<Peer> frenzed_list = session.download_status_list.getPeersWithInactiveTime(PEER_RESEND_PACKET_INTERVAL, ACTIVE);
					for (Peer peer : frenzed_list) {
						session.file_request_list.remove(peer);
						session.fileChunkRequest(peer);
					}
					List<Peer> peer_list = session.download_status_list.getPeersWithInactiveTime(UNUSED_PEER_ACTIVATION, PeerDownloadStatus.ACTIVE_UNUSED);
					for (Peer peer : peer_list) {
						if (peer.isConnected())
							session.fileChunkRequest(peer);
						else
							session.download_status_list.setPeerStatus(peer, PeerDownloadStatus.DISCONNECTED);
					}
					
					List<Peer> drop_queue_peer = session.download_status_list.getPeersWithInactiveTime(QUEUE_PEERS_DROP_TIMEOUT, PeerDownloadStatus.IN_QUEUE);
					for(Peer peer : drop_queue_peer) {
						session.removePeer(peer);
					}
					
					List<Peer> check_queue_position = session.download_status_list.getPeersWithLastSeenTime(QUEUE_PEERS_CHECK_TIMEOUT, PeerDownloadStatus.IN_QUEUE);
					for(Peer peer : check_queue_position) {
						if (peer.isDisconnected()) {
							try {
								_peer_manager.connect(peer);
							} catch (PeerManagerException e) {
								e.printStackTrace();
							}
						}
					}
					
				}
					
				
			}
		};
		download_file_checker = new DownloadFileChecker();
	}

	public void start() {
		try {
			super.start();	
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		_network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		
		need_more_peers_timer = new Timer( "Need more peers timer", true );
		need_more_peers_timer.scheduleAtFixedRate( new TimerTask() {
			@Override
			public void run() {
				Set<FileHash> file_hashes = session_list.keySet();
				List<FileHash> file_hashes_needed_help = new ArrayList<FileHash>();
				for(FileHash file_hash : file_hashes) {
					DownloadSession download_session = session_list.get( file_hash );
					if( download_session.isStarted() && 
					    ( download_session.getSpeed() <= MEDIUM_SPEED_CONSIDERED_AS_SMALL ) ) 
						   file_hashes_needed_help.add( file_hash );
				}
				notifyNeedMorePeersForFiles( file_hashes_needed_help );
			}
		}, (long)1, NEED_MORE_PEERS_INTERVAL);
		
		
		maintenance_tasks.addTask(pex_source_search_task,ConfigurationManager.PEX_SOURCES_QUERY_INTERVAL,true);
		maintenance_tasks.addTask(global_source_search_task,ConfigurationManager.GLOBAL_SOURCES_QUERY_INTERVAL,true);
		maintenance_tasks.addTask(download_peers_monitor, DOWNLOAD_PEERS_MONITOR_INTERVAL, true);
		maintenance_tasks.addTask(download_bytes_counter, 1000, true);
		
		download_file_checker.start();
		
	}
	
	public void shutdown() {

		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}

		need_more_peers_timer.cancel();
		maintenance_tasks.cancelAllTasks();
		
		for (DownloadSession download_session : session_list.values())
			if (download_session.isStarted())
				download_session.stopDownload(false);
		
		download_file_checker.JMStop();
	}
	
	public void addDownload(SearchResultItem searchResult) throws DownloadManagerException {
		if (hasDownload(searchResult.getFileHash()))
			throw new DownloadManagerException("Download " + searchResult.getFileHash() + " already exists");
		DownloadSession download_session = new DownloadSession(searchResult);
		session_list.put(searchResult.getFileHash(), download_session);
		
		notifyDownloadAdded(searchResult.getFileHash());
	}

	public void addDownload(ED2KFileLink fileLink) throws DownloadManagerException {
		if (hasDownload(fileLink.getFileHash()))
			throw new DownloadManagerException("Download "
					+ fileLink.getFileHash() + " already exists");
		DownloadSession download_session = new DownloadSession(fileLink);
		session_list.put(fileLink.getFileHash(), download_session);
		
		notifyDownloadAdded(fileLink.getFileHash());
	}
	
	public void addDownload(String ed2kLinkAsString) throws DownloadManagerException {
		try {
		  ED2KFileLink ed2k_file_link = new ED2KFileLink( ed2kLinkAsString ); 
		  this.addDownload( ed2k_file_link );
		}catch( Throwable cause ) {
			throw new DownloadManagerException( cause );
		}
	}

	public void addAndStartSilentDownload(String ed2kLinkAsString) {
		try {
			this.addAndStartSilentDownload(new ED2KFileLink(ed2kLinkAsString));
		}catch( Throwable cause ) {
			cause.printStackTrace();
		}
	}
	
	public void addAndStartSilentDownload(ED2KFileLink ed2kLink) {
		try {
		  this.addDownload( ed2kLink );
		  this.startDownload( ed2kLink.getFileHash() );
		}catch( Throwable cause ) {
			cause.printStackTrace();
		}
	}
	
	public DownloadManager addAndStartSilentDownloads(String edk2LinkAsString) {
		try {
			ed2k_links_add_helper.add(new ED2KFileLink( edk2LinkAsString ) );
		}catch(Throwable cause) {
			cause.printStackTrace();
		}
		return this;
	}
	
	public void finishAddAndStartSilentDownloads() {
		for(ED2KFileLink file_link : ed2k_links_add_helper) 
	      try {
	    	  this.addAndStartSilentDownload( file_link );
	    	  ed2k_links_add_helper.remove(file_link);
	      }catch(Throwable cause) {
	    	  cause.printStackTrace();
	      }
	}
	
	public void addDownload(PartialFile partialFile) throws DownloadManagerException {
		if (hasDownload(partialFile.getFileHash()))
			throw new DownloadManagerException("Download "
					+ partialFile.getFileHash() + " already exists");
		DownloadSession download_session = new DownloadSession(partialFile);
		session_list.put(partialFile.getFileHash(), download_session);
		notifyDownloadAdded(partialFile.getFileHash());
		if (partialFile.isDownloadStarted())
			startDownload(partialFile.getFileHash());
	}

	public void cancelDownload(FileHash fileHash) throws DownloadManagerException {
		if (!hasDownload(fileHash))
			throw new DownloadManagerException("Download " + fileHash
					+ " not found ");
		DownloadSession download_session = getDownload(fileHash);
		
		if (download_session.getPercentCompleted() != 100d)
			download_session.cancelDownload(); 
		
		session_list.remove(fileHash);
		
		server_sources_queue.remove(fileHash);
		kad_sources_queue.remove(fileHash);
		pex_sources_queue.remove(fileHash);
		
		byte[] hash = fileHash.getHash().clone();
		org.jmule.core.jkad.utils.Convert.updateSearchID(hash);
		Int128 search_id = new Int128(hash);
		
		_jkad.getSearch().cancelSearch(search_id);
		
		notifyDownloadRemoved(fileHash);
	}

	public void cancelSilentDownloads() {
		try {
			Collection<DownloadSession> ds_collection = session_list.values();
			for(DownloadSession ds : ds_collection)
				this.cancelDownload( ds.getFileHash() );
		}catch( Throwable cause ) {
			cause.printStackTrace();
		}
	}
	
	public void startDownload(FileHash fileHash) throws DownloadManagerException {
		if (!hasDownload(fileHash))
			throw new DownloadManagerException("Download " + fileHash
					+ " not found ");
		DownloadSession download_session = session_list.get(fileHash);
		if (download_session.isStarted())
			throw new DownloadManagerException("Download " + fileHash+" is already started");
		
		server_sources_queue.offer(fileHash);
		kad_sources_queue.offer(fileHash);
		pex_sources_queue.offer(fileHash);
		global_sources_queue.offer(fileHash);
		
		if (_upload_manager.hasUpload(fileHash))
			_upload_manager.removeUpload(fileHash);
		
		download_session.startDownload();
		
		 //_network_manager.requestSourcesFromServer(download_session.getFileHash(), download_session.getSharedFile().length());
		
		notifyDownloadStarted(fileHash);
		
		if (last_server_sources_request == 0)
			if (_server_manager.isConnected())
				server_sources_query.run();
		
	}

	public void stopDownload(FileHash fileHash)  throws DownloadManagerException {
		if (!hasDownload(fileHash))
			throw new DownloadManagerException("Download " + fileHash
					+ " not found ");
		DownloadSession download_session = session_list.get(fileHash);
		if (!download_session.isStarted())
			throw new DownloadManagerException("Download " + fileHash + " is already stopped");
		
		byte[] hash = fileHash.getHash().clone();
		org.jmule.core.jkad.utils.Convert.updateSearchID(hash);
		Int128 search_id = new Int128(hash);
		
		_jkad.getSearch().cancelSearch(search_id);
		
		server_sources_queue.remove(fileHash);
		kad_sources_queue.remove(fileHash);
		pex_sources_queue.remove(fileHash);
		global_sources_queue.remove(fileHash);
		
		
		if (_upload_manager.hasUpload(fileHash))
			_upload_manager.removeUpload(fileHash);
		
		download_session.stopDownload();
		notifyDownloadStopped(fileHash);
	}

	public int getDownloadCount() {
		return session_list.size();
	}

	public boolean hasDownload(FileHash fileHash) {
		return session_list.containsKey(fileHash);
	}

	public List<DownloadSession> getDownloads() {
		List<DownloadSession> result = new ArrayList<DownloadSession>();
		result.addAll(session_list.values());
		return result;
	}
	
	public void startDownload() {
		for(DownloadSession session : session_list.values()) {
			if (!session.isStarted()) {
				
				server_sources_queue.offer(session.getFileHash());
				kad_sources_queue.offer(session.getFileHash());
				pex_sources_queue.offer(session.getFileHash());
				global_sources_queue.offer(session.getFileHash());
				
				session.startDownload();
			}
		}
	}
	
	public void stopDownload() {
		for(DownloadSession session : session_list.values()) {
			if (session.isStarted()) {
				server_sources_queue.remove(session.getFileHash());
				kad_sources_queue.remove(session.getFileHash());
				pex_sources_queue.remove(session.getFileHash());
				global_sources_queue.remove(session.getFileHash());
				
				session.stopDownload(true);
			}
		}
	}
	
	public void jKadConnected() {
		maintenance_tasks.addTask(kad_source_search_task, JKadConstants.KAD_SOURCES_SEARCH_INTERVAL, true);
	}
	
	public void jKadDisconnected() {
		maintenance_tasks.removeTask(kad_source_search_task);
	}

	public void addDownloadPeers(FileHash fileHash, List<Peer> peerList) {
		DownloadSession downloadSession = session_list.get(fileHash);
		if (downloadSession != null)
			downloadSession.addDownloadPeers(peerList);
	}

	public DownloadSession getDownload(FileHash fileHash) throws DownloadManagerException {
		if (!hasDownload(fileHash))
			throw new DownloadManagerException("Download session " + fileHash + " not found");
		return session_list.get(fileHash);
	}

	public void addDownloadManagerListener(DownloadManagerListener listener) {
		download_manager_listeners.add(listener);
	}

	public void removeDownloadMangerListener(DownloadManagerListener listener) {
		download_manager_listeners.add(listener);
	}

	protected boolean iAmStoppable() {
		return false;
	}
	
	public void peerConnected(Peer peer) {
		for(DownloadSession session : session_list.values())
			if (session.hasPeer(peer)) {
				try {
					session.peerConnected(peer);
				}catch(Throwable cause) { cause.printStackTrace(); }
				return ;
			}
	}

	public void peerDisconnected(Peer peer) {
		for(DownloadSession session : session_list.values())
			if (session.hasPeer(peer)) {
				try {
					session.peerDisconnected(peer);
				}catch(Throwable cause) { cause.printStackTrace(); }
				return ;
			}
	}
	
	public void peerRemoved(Peer peer) {
		for(DownloadSession session : session_list.values())
			if (session.hasPeer(peer)) {
				try {
					session.removePeer(peer);
				}catch(Throwable cause) { cause.printStackTrace(); }
				return ;
			}
	}
	
	public void peerConnectingFailed(Peer peer, Throwable cause) {
		for(DownloadSession session : session_list.values())
			if (session.hasPeer(peer)) {
				try {
					session.peerConnectingFailed(peer, cause);
				}catch(Throwable fail_cause) { fail_cause.printStackTrace(); }
				return ;
			}
	}

	public void receivedCompressedFileChunk(Peer sender,
			FileHash fileHash, FileChunk compressedFileChunk) {
		DownloadSession session;
		try {
			session = getDownload(fileHash);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return;
		}
		session.receivedCompressedFileChunk(sender,
				compressedFileChunk);
	}

	public void receivedFileNotFoundFromPeer(Peer sender,
			FileHash fileHash) {
		DownloadSession session;
		if (hasDownload(fileHash)) {
			try {
				session = getDownload(fileHash);
			} catch (DownloadManagerException e) {
				e.printStackTrace();
				return;
			}
			session.receivedFileNotFoundFromPeer(sender);
		}
	}

	public void receivedFileRequestAnswerFromPeer(Peer sender, FileHash fileHash, String fileName) {
		DownloadSession session;
		try {
			session = getDownload(fileHash);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return;
		}
		session.receivedFileRequestAnswerFromPeer(sender, fileName);
	}

	public void receivedFileStatusResponseFromPeer(Peer sender,
			FileHash fileHash, JMuleBitSet partStatus) {
		DownloadSession session;
		try {
			session = getDownload(fileHash);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return;
		}
		session.receivedFileStatusResponseFromPeer(sender, fileHash,
				partStatus);
	}

	public void receivedHashSetResponseFromPeer(Peer sender,
			FileHash fileHash, PartHashSet partHashSet) {
		DownloadSession session;
		try {
			session = getDownload(fileHash);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return;
		}
		session.receivedHashSetResponseFromPeer(sender, partHashSet);
	}

	public void receivedQueueRankFromPeer(Peer sender,int queueRank) {
		DownloadSession session;
		try {
			session = getDownloadSession(sender);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return ;
		}	
		session.receivedQueueRankFromPeer(sender, queueRank);
	}

	public void receivedRequestedFileChunkFromPeer(Peer sender, FileHash fileHash, FileChunk chunk) {
		DownloadSession session;
		try {
			session = getDownload(fileHash);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return;
		}
		session.receivedRequestedFileChunkFromPeer(sender, fileHash,chunk);
	}

	public void receivedSlotGivenFromPeer(Peer sender) {
		DownloadSession session;
		try {
			session = getDownloadSession(sender);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return;
		}
		session.receivedSlotGivenFromPeer(sender);
	}

	public void receivedSlotTakenFromPeer(Peer sender) {
		DownloadSession session;
		try {
			session = getDownloadSession(sender);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return ;
		}
		session.receivedSlotTakenFromPeer(sender);
	}

	private DownloadSession getDownloadSession(Peer peer) throws DownloadManagerException {
		for(DownloadSession session : session_list.values())
			if (session.hasPeer(peer)) 
				return session;
		throw new DownloadManagerException("Download session with " + peer + " not found");
	}

	public boolean hasPeer(Peer peer) {
		for(DownloadSession session : session_list.values())
			if (session.hasPeer(peer)) 
				return true;
		return false;
	}
	
	private void notifyDownloadStarted(FileHash fileHash) {
		for(DownloadManagerListener listener : download_manager_listeners)
			try {
				listener.downloadStarted(fileHash);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyDownloadStopped(FileHash fileHash) {
		for(DownloadManagerListener listener : download_manager_listeners)
			try {
				listener.downloadStopped(fileHash);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyDownloadAdded(FileHash fileHash) {
		for(DownloadManagerListener listener : download_manager_listeners)
			try {
				listener.downloadAdded(fileHash);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyDownloadRemoved(FileHash fileHash) {
		for(DownloadManagerListener listener : download_manager_listeners)
			try {
				listener.downloadRemoved(fileHash);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}

	public void receivedSourcesAnswerFromPeer(Peer peer, FileHash fileHash,
			List<Peer> peerList) {
		try {
			DownloadSession session = getDownload(fileHash);
			session.receivedSourcesAnswerFromPeer(peer, peerList);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
		}
	}
	
	public void connectedToServer(Server server) {
		maintenance_tasks.addTask(server_sources_query, ConfigurationManager.SERVER_SOURCES_QUERY_INTERVAL, true);
		server_sources_query.run();
		new Thread(new Runnable() {
			public void run() {
				global_source_search_task.run();
			}
		}).start();
		
	}
	
	@Override
	public void scheduleForPartCheck(DownloadSession session) {
		if (!file_part_to_check.contains(session)) {
			file_part_to_check.add(session);
			if (download_file_checker.isSleeping())
				download_file_checker.wakeUp();
		}
	}

	@Override
	public void scheduleToComplete(DownloadSession session) {
		if (!complete_file_to_check.contains(session)) {
			complete_file_to_check.add(session);
			if (download_file_checker.isSleeping())
				download_file_checker.wakeUp();
		}
	}
	
	public void disconnectedFromServer(Server server) {
		maintenance_tasks.removeTask(server_sources_query);
	}
	
	public void addNeedMorePeersListener(NeedMorePeersListener listener) {
		need_more_peers_listeners.add( listener );
	}

	public void removeNeedMorePeersListener(NeedMorePeersListener listener) {
		need_more_peers_listeners.remove( listener );
	}
	
	private void notifyNeedMorePeersForFiles(List<FileHash> fileHashes) {
		for( NeedMorePeersListener listener : need_more_peers_listeners ) 
		   try {
			   listener.needMorePeersForFiles( fileHashes );
		   }catch( Throwable cause ) {
			   cause.printStackTrace();
		   }
	}

	class DownloadFileChecker extends JMThread {
		public DownloadFileChecker() {
			super("Download file checker");
		}
		
		private boolean is_sleeping = false;
		private boolean loop = true;
		
		public void run() {
			while(loop) {
				boolean process = false;
				is_sleeping = false;
				if (file_part_to_check.size() != 0)
					process = true;
				if (complete_file_to_check.size() != 0)
					process = true;
				
				if (!process) {
					is_sleeping = true;
					synchronized(this) {
						try {
							this.wait();
						} catch (InterruptedException e) {
						}
					}
					continue;
				}
				
				while(!file_part_to_check.isEmpty()) {
					DownloadSession session =  file_part_to_check.poll();
					
					List<Integer> broken_parts = session.sharedFile.checkFilePartsIntegrity();
					if (broken_parts.size()!=0)
						session.download_strategy.processPartCheckResult(broken_parts);
				}
				
				while(!complete_file_to_check.isEmpty()) {
					DownloadSession session = complete_file_to_check.poll();
					
					List<Integer> result = session.sharedFile.checkFullFileIntegrity();
					if (result.size() != 0)
						session.download_strategy.processFileCheckResult(result);
					else
						session.completeDownload();
				}
				
			}
		}
		
		public boolean isSleeping() {
			return is_sleeping;
		}
		
		public void wakeUp() {
			synchronized(this) {
				this.notify();
			}
		}
		
		public void JMStop() {
			loop = false;
			if (isSleeping())
				wakeUp();
			this.interrupt();
		}
		
	}
	
}