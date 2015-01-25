/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2010 JMule team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.networkmanager;

import static org.jmule.core.JMConstants.*;
import static org.jmule.core.edonkey.ED2KConstants.*;
import static org.jmule.core.edonkey.ED2KConstants.OP_ANSWERSOURCES;
import static org.jmule.core.edonkey.ED2KConstants.OP_CHATCAPTCHAREQ;
import static org.jmule.core.edonkey.ED2KConstants.OP_CHATCAPTCHARES;
import static org.jmule.core.edonkey.ED2KConstants.OP_COMPRESSEDPART;
import static org.jmule.core.edonkey.ED2KConstants.OP_EMULEHELLOANSWER;
import static org.jmule.core.edonkey.ED2KConstants.OP_EMULE_HELLO;
import static org.jmule.core.edonkey.ED2KConstants.OP_EMULE_QUEUERANKING;
import static org.jmule.core.edonkey.ED2KConstants.OP_END_OF_DOWNLOAD;
import static org.jmule.core.edonkey.ED2KConstants.OP_FILEREQANSNOFILE;
import static org.jmule.core.edonkey.ED2KConstants.OP_FILEREQANSWER;
import static org.jmule.core.edonkey.ED2KConstants.OP_FILENAMEREQUEST;
import static org.jmule.core.edonkey.ED2KConstants.OP_FILESTATREQ;
import static org.jmule.core.edonkey.ED2KConstants.OP_FILESTATUS;
import static org.jmule.core.edonkey.ED2KConstants.OP_HASHSETANSWER;
import static org.jmule.core.edonkey.ED2KConstants.OP_HASHSETREQUEST;
import static org.jmule.core.edonkey.ED2KConstants.OP_MESSAGE;
import static org.jmule.core.edonkey.ED2KConstants.OP_PEERHELLO;
import static org.jmule.core.edonkey.ED2KConstants.OP_PEERHELLOANSWER;
import static org.jmule.core.edonkey.ED2KConstants.OP_PUBLICKEY;
import static org.jmule.core.edonkey.ED2KConstants.OP_REQUESTPARTS;
import static org.jmule.core.edonkey.ED2KConstants.OP_REQUESTSOURCES;
import static org.jmule.core.edonkey.ED2KConstants.OP_SECIDENTSTATE;
import static org.jmule.core.edonkey.ED2KConstants.OP_SENDINGPART;
import static org.jmule.core.edonkey.ED2KConstants.OP_SERVERLIST;
import static org.jmule.core.edonkey.ED2KConstants.OP_SIGNATURE;
import static org.jmule.core.edonkey.ED2KConstants.OP_SLOTGIVEN;
import static org.jmule.core.edonkey.ED2KConstants.OP_SLOTRELEASE;
import static org.jmule.core.edonkey.ED2KConstants.OP_SLOTREQUEST;
import static org.jmule.core.edonkey.ED2KConstants.OP_SLOTTAKEN;
import static org.jmule.core.edonkey.ED2KConstants.PACKET_CALLBACKFAILED;
import static org.jmule.core.edonkey.ED2KConstants.PACKET_CALLBACKREQUESTED;
import static org.jmule.core.edonkey.ED2KConstants.PACKET_SRVFOUNDSOURCES;
import static org.jmule.core.edonkey.ED2KConstants.PACKET_SRVIDCHANGE;
import static org.jmule.core.edonkey.ED2KConstants.PACKET_SRVMESSAGE;
import static org.jmule.core.edonkey.ED2KConstants.PACKET_SRVSEARCHRESULT;
import static org.jmule.core.edonkey.ED2KConstants.PACKET_SRVSTATUS;
import static org.jmule.core.edonkey.ED2KConstants.PROTO_EDONKEY_TCP;
import static org.jmule.core.edonkey.ED2KConstants.PROTO_EMULE_COMPRESSED_TCP;
import static org.jmule.core.edonkey.ED2KConstants.PROTO_EMULE_EXTENDED_TCP;
import static org.jmule.core.edonkey.ED2KConstants.SERVER_SEARCH_RATIO;
import static org.jmule.core.utils.Misc.getByteBuffer;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.DataFormatException;

import org.jmule.core.JMException;
import org.jmule.core.JMThread;
import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.bccrypto.SHA1WithRSAEncryption;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.configmanager.InternalConfigurationManager;
import org.jmule.core.downloadmanager.DownloadManagerSingleton;
import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.downloadmanager.InternalDownloadManager;
import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.ED2KConstants;
import org.jmule.core.edonkey.ED2KConstants.ServerFeatures;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.edonkey.packet.PacketFactory;
import org.jmule.core.edonkey.packet.UDPPacket;
import org.jmule.core.edonkey.packet.UDPPacketFactory;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.packet.tag.TagScanner;
import org.jmule.core.edonkey.utils.Utils;
import org.jmule.core.ipfilter.IPFilterSingleton;
import org.jmule.core.ipfilter.InternalIPFilter;
import org.jmule.core.ipfilter.IPFilter.BannedReason;
import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.InternalJKadManager;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.JKadManagerSingleton;
import org.jmule.core.jkad.packet.KadPacket;
import org.jmule.core.networkmanager.JMConnection.ConnectionStatus;
import org.jmule.core.networkmanager.JMPeerConnection.InterestedOPS;
import org.jmule.core.peermanager.InternalPeerManager;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.peermanager.Peer.PeerSource;
import org.jmule.core.peermanager.PeerManagerException;
import org.jmule.core.peermanager.PeerManagerSingleton;
import org.jmule.core.searchmanager.InternalSearchManager;
import org.jmule.core.searchmanager.SearchManagerSingleton;
import org.jmule.core.searchmanager.SearchQuery;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.core.searchmanager.SearchResultItemList;
import org.jmule.core.servermanager.InternalServerManager;
import org.jmule.core.servermanager.Server;
import org.jmule.core.servermanager.ServerManagerSingleton;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.sharingmanager.InternalSharingManager;
import org.jmule.core.sharingmanager.JMuleBitSet;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.sharingmanager.SharingManagerSingleton;
import org.jmule.core.speedmanager.BandwidthController;
import org.jmule.core.speedmanager.SpeedManagerSingleton;
import org.jmule.core.uploadmanager.FileChunkRequest;
import org.jmule.core.uploadmanager.InternalUploadManager;
import org.jmule.core.uploadmanager.UploadManagerSingleton;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.JMuleZLib;
import org.jmule.core.utils.Misc;
import org.jmule.core.utils.timer.JMTimer;
import org.jmule.core.utils.timer.JMTimerTask;

/**
 * Created on Aug 14, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.52 $
 * Last changed by $Author: binary255 $ on $Date: 2010/10/23 05:52:18 $
 */
public class NetworkManagerImpl extends JMuleAbstractManager implements InternalNetworkManager {
	private static final long CONNECTION_SPEED_SYNC_INTERVAL 		= 1000;
	private static final long NETWORK_SPEED_UPDATE_INTERVAL 		= 1000;
	private static final long DROP_SEND_QUEUE_TIMEOUT 				= 1000 * 60;
	private static final long SEND_QUEUE_SCAN_INTERVAL				= 1000 * 15;
	private static final long PACKET_PROCESSOR_DROP_TIMEOUT 		= 1000 * 30;
	private static final long PACKET_PROCESSOR_QUEUE_SCAN_INTERVAL 	= 1000 * 15;
	private static final long PEER_CONNECTIONS_MONITOR_SELECT		= 50;
	private static final long SERVER_CONNECTION_MONITOR_SELECT		= 5000;
	private static final long UDP_CONNECTION_MONITOR_SELECT			= 1000;

	private enum PacketType {
		SERVER_UDP, PEER_UDP, KAD, KAD_COMPRESSED
	};
	
	private Map<String, JMPeerConnection> peer_connections = new ConcurrentHashMap<String, JMPeerConnection>();

	private InternalPeerManager _peer_manager;
	private InternalServerManager _server_manager;
	private InternalConfigurationManager _config_manager;
	private InternalDownloadManager _download_manager;
	private InternalUploadManager _upload_manager;
	private InternalJKadManager _jkad_manager;
	private InternalSearchManager _search_manager;
	private InternalSharingManager _sharing_manager;
	private InternalIPFilter _ip_filter;

	private JMUDPConnection udp_connection;
	
	private IncomingConnectionReceiver connection_receiver;
	private PeerConnectionsMonitor peer_connections_monitor;
	private ServerConnectionMonitor server_connection_monitor;
		
	private PeerPacketProcessor peer_packet_processor;
	private ServerPacketProcessor server_packet_processor;
	
	private JMTimer connections_maintenance = new JMTimer();
	private JMTimerTask connection_speed_sync;
	private JMTimerTask network_speed_updater;
	
	private float upload_speed, download_speed;
	
	private BandwidthController upload_controller, download_controller;
	
	NetworkManagerImpl() {
	}
	
	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		_peer_manager = (InternalPeerManager) PeerManagerSingleton.getInstance();
		_server_manager = (InternalServerManager) ServerManagerSingleton.getInstance();
		_config_manager = (InternalConfigurationManager) ConfigurationManagerSingleton.getInstance();
		_download_manager = (InternalDownloadManager) DownloadManagerSingleton.getInstance();
		_upload_manager = (InternalUploadManager) UploadManagerSingleton.getInstance();
		_jkad_manager = (InternalJKadManager) JKadManagerSingleton.getInstance();
		_search_manager = (InternalSearchManager) SearchManagerSingleton.getInstance();
		_sharing_manager = (InternalSharingManager) SharingManagerSingleton.getInstance();
		_ip_filter = (InternalIPFilter) IPFilterSingleton.getInstance();
		
		upload_controller = SpeedManagerSingleton.getInstance().getUploadController();
		download_controller = SpeedManagerSingleton.getInstance().getDownloadController();
		
		connection_speed_sync = new JMTimerTask() {
			public void run() {
				for(JMPeerConnection connection : peer_connections.values()) {
					if (connection.getStatus() != ConnectionStatus.CONNECTED) continue;
					JMuleSocketChannel channel = connection.getJMConnection();
					channel.file_transfer_trafic.syncSpeed();
					channel.service_trafic.syncSpeed();
				}
			}
		};
		
		network_speed_updater = new JMTimerTask() {
			public void run() {
				float tmpUploadSpeed = 0;
				float tmpDownloadSpeed = 0;
				for (JMPeerConnection peer_connection : peer_connections.values())
					if (peer_connection.getStatus() == ConnectionStatus.CONNECTED) {
						tmpDownloadSpeed += peer_connection.getJMConnection().getDownloadSpeed();
						tmpUploadSpeed += peer_connection.getJMConnection().getUploadSpeed();
					}
				upload_speed = tmpUploadSpeed;
				download_speed = tmpDownloadSpeed;
			}
		};
	}
	
	public void start() {
		try {
			super.start();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		udp_connection = new JMUDPConnection();
		udp_connection.startConnection();
		try {
			if (_config_manager.isUDPEnabled()) {
				udp_connection.open();
				udp_connection.wakeUp();
			}
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
		}
		
		connection_receiver = new IncomingConnectionReceiver();
		connection_receiver.startReceiver();
		
		peer_packet_processor = new PeerPacketProcessor();
		peer_packet_processor.startProcessor();
				
		peer_connections_monitor = new PeerConnectionsMonitor();
		peer_connections_monitor.startMonitor();
				
		
		connections_maintenance.addTask(connection_speed_sync, CONNECTION_SPEED_SYNC_INTERVAL, true);
		connections_maintenance.addTask(network_speed_updater, NETWORK_SPEED_UPDATE_INTERVAL, true);
	}
	
	public void shutdown() {
		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}

		try {
			if (_config_manager.isUDPEnabled())
				udp_connection.stopThread();
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
		}

		connection_receiver.stopReceiver();
		peer_connections_monitor.stopMonitor();

		peer_packet_processor.JMStop();
		
		if (server_connection_monitor != null)
			if (server_connection_monitor.isAlive())
				server_connection_monitor.JMStop();
		
		if (server_packet_processor != null)
			if (server_packet_processor.isAlive())
				server_packet_processor.JMStop();
		
		for (JMPeerConnection connection : peer_connections.values())
			try {
				connection.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		if (server_connection_monitor != null)
			server_connection_monitor.JMStop();
		
		connections_maintenance.cancelAllTasks();
	}
	
	public String toString() {
		String result = "";
//		result += "Peer connections : \n";
//		for (String key_connection : peer_connections.keySet())
//			result += "[" + key_connection + "] = " + "["+ peer_connections.get(key_connection) + "]\n";
		
		result += "\nPeer connection monitor : \n" + peer_connections_monitor;
		result += "\nPeerPacketProcessor : \n " + peer_packet_processor;
		result += "\nUDPConnection : \n" + udp_connection;
		return result;
	}
	
	public float getDownloadSpeed() {
		return download_speed;
	}
	
	public float getUploadSpeed() {
		return upload_speed;
	}

	public void addPeer(JMPeerConnection peerConnection) throws NetworkManagerException {
		peer_connections.put(peerConnection.getIPAddress() + KEY_SEPARATOR+ peerConnection.getPort(), peerConnection);
		try {
			_peer_manager.newIncomingPeer(peerConnection.getIPAddress(),peerConnection.getPort());
		} catch (PeerManagerException cause) {
			cause.printStackTrace();
		}
		peer_connections_monitor.installMonitor(peerConnection);
	}

	public void addPeer(String ip, int port) throws NetworkManagerException {
		Peer peer;
		try {
			peer = _peer_manager.getPeer(ip, port);
			if (!peer.isHighID()) {
				if (peer.getSource()!=PeerSource.GLOBAL) {
					if (server_connection_monitor!=null)
						if (server_connection_monitor.getServerConnection().getStatus() == ConnectionStatus.CONNECTED) {
							if (_server_manager.getConnectedServer().getClientID().isHighID())
								callBackRequest(peer.getClientID());
							return ;
						}
				} else
				if (peer.getSource()==PeerSource.GLOBAL) {
					sendServerUDPCallBackRequest(peer.getServerIP(),peer.getServerPort(), peer.getClientID());
				}
			}
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
		
		JMPeerConnection connection = new JMPeerConnection(ip, port);
		peer_connections.put(ip + KEY_SEPARATOR + port, connection);
		peer_connections_monitor.installMonitor(connection);
	}

	public void callBackRequest(ClientID clientID) {
		Packet packet = PacketFactory.getCallBackRequestPacket(clientID);
		try {
			server_connection_monitor.sendPacket(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void connectToServer(String ip, int port)
			throws NetworkManagerException {
		
		if (server_connection_monitor != null)
			throw new NetworkManagerException("Already connected to "
					+ server_connection_monitor.getServerConnection().getIPAddress() + " "
					+ server_connection_monitor.getServerConnection().getPort());
		
		JMServerConnection server_connection = new JMServerConnection(ip, port);
		
		server_connection_monitor = new ServerConnectionMonitor();
		server_connection_monitor.startMonitor(server_connection);
	}

	public void disconnectFromServer() throws NetworkManagerException {
		if (server_connection_monitor == null)
			throw new NetworkManagerException("Not connected to server");
		server_connection_monitor.JMStop();
		server_connection_monitor = null;
	}

	public void disconnectPeer(String ip, int port) {
		JMPeerConnection connection = null;
		try {
			connection = getPeerConnection(ip, port);
		} catch (NetworkManagerException e) {
			e.printStackTrace();
			return;
		}
		try {
			connection.disconnect();
			peer_connections_monitor.send_queue.remove(connection);
			peer_connections.remove(ip + KEY_SEPARATOR + port);
			connection = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doSearchOnServer(SearchQuery searchQuery) {
		Packet search_packet = PacketFactory.getSearchPacket(searchQuery);
		try {
			server_connection_monitor.sendPacket(search_packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public boolean hasPeer(String peerIP, int peerPort) {
		return peer_connections.containsKey(peerIP+ KEY_SEPARATOR + peerPort);
	}
	
	private JMPeerConnection getPeerConnection(String peerIP, int peerPort)
			throws NetworkManagerException {
		JMPeerConnection peer_connection = peer_connections.get(peerIP + KEY_SEPARATOR + peerPort);
		if (peer_connection == null)
			throw new NetworkManagerException("Peer " + peerIP + KEY_SEPARATOR
					+ peerPort + " not found   ");
		return peer_connection;
	}

	public float getPeerDownloadServiceSpeed(String peerIP, int peerPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			return peer_connection.getJMConnection().getServiceDownloadSpeed();
		} catch (Throwable cause) {
			return 0f;
		}
	}

	public float getPeerDownloadSpeed(String peerIP, int peerPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			if (peer_connection == null)  {
				return 0;
			}
			return peer_connection.getJMConnection().getDownloadSpeed();
		} catch (Throwable cause) {
			return 0f;
		}
	}

	public float getPeerUploadServiceSpeed(String peerIP, int peerPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			return peer_connection.getJMConnection().getServiceUploadSpeed();
		} catch (Throwable cause) {
			return 0f;
		}
	}

	public float getPeerUploadSpeed(String peerIP, int peerPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			return peer_connection.getJMConnection().getUploadSpeed();
		} catch (Throwable cause) {
			return 0f;
		}
	}

	public long getFileDownloadedBytes(String peerIP, int peerPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			return peer_connection.getJMConnection().getDownloadedBytes();
		} catch (Throwable cause) {
			return 0;
		}
	}
	
	public long getFileUploadedBytes(String peerIP, int peerPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			return peer_connection.getJMConnection().getUploadBytes();
		} catch (Throwable cause) {
			return 0;
		}
	}
	
	public long getServiceDownloadedBytes(String peerIP, int peerPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			return peer_connection.getJMConnection().getServiceDownloadBytes();
		} catch (Throwable cause) {
			return 0;
		}
	}
	
	public long getServiceUploadedBytes(String peerIP, int peerPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			return peer_connection.getJMConnection().getServiceUploadBytes();
		} catch (Throwable cause) {
			return 0;
		}
	}
	
	public ConnectionStatus getServerConnectionStatus() {
		if (server_connection_monitor == null)
			return ConnectionStatus.DISCONNECTED;
		return server_connection_monitor.getServerConnection().getStatus();
	}

	protected boolean iAmStoppable() {
		return false;
	}

	public void offerFilesToServer(ClientID userID,
			List<SharedFile> filesToShare) {
		try {
			Packet packet = PacketFactory.getOfferFilesPacket(userID,filesToShare);
			server_connection_monitor.sendPacket(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}
	
	public void peerConnected(String ip, int port) {
		try {
			JMPeerConnection connection = getPeerConnection(ip, port);
			Server connected_server = _server_manager.getConnectedServer();
			byte[] server_ip = null;
			int server_port = 0;
			ClientID client_id = null;
			if (connected_server != null) {
				server_ip = connected_server.getAddressAsByte();
				server_port = connected_server.getPort();
				client_id = connected_server.getClientID();
			}
			Packet packet = PacketFactory.getPeerHelloPacket(_config_manager
					.getUserHash(), client_id, _config_manager.getTCP(),
					server_ip, server_port, _config_manager.getNickName(),
					ED2KConstants.DefaultJMuleFeatures);
			sendPacket(connection, packet);

		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void peerConnectingFailed(String ip, int port, Throwable cause) {
		_peer_manager.peerConnectingFailed(ip, port, cause);
		JMPeerConnection connection = peer_connections.get(ip + KEY_SEPARATOR + port);
		if (connection != null)
			connection.setStatus(ConnectionStatus.DISCONNECTED);
		peer_connections.remove(ip + KEY_SEPARATOR + port);
	}

	public void peerDisconnected(String ip, int port) {
		
		_peer_manager.peerDisconnected(ip, port);
		JMPeerConnection connection = null;
		try {
			connection = getPeerConnection(ip, port);
		} catch (NetworkManagerException e) {
			e.printStackTrace();
			return;
		}
		try {
			connection.disconnect();
			peer_connections_monitor.send_queue.remove(connection);
			peer_connections.remove(ip + KEY_SEPARATOR + port);
			connection = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//disconnectPeer(ip, port);
		
		/*_peer_manager.peerDisconnected(ip, port);
		JMPeerConnection connection = peer_connections.get(ip + KEY_SEPARATOR + port);
		if (connection != null) {
			peer_connections_monitor.send_queue.remove(connection);
			connection.setStatus(ConnectionStatus.DISCONNECTED);
		}
		peer_connections.remove(ip + KEY_SEPARATOR + port);*/
	}
	
	public void peerIOError(String ip, int port, Throwable cause) {
		_peer_manager.peerDisconnected(ip, port);
		JMPeerConnection connection = null;
		try {
			connection = getPeerConnection(ip, port);
		} catch (NetworkManagerException e) {
			e.printStackTrace();
			return;
		}
		try {
			connection.disconnect();
			peer_connections_monitor.send_queue.remove(connection);
			peer_connections.remove(ip + KEY_SEPARATOR + port);
			connection = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//disconnectPeer(ip, port);
		/*_peer_manager.peerDisconnected(ip, port);
		JMPeerConnection connection = peer_connections.get(ip + KEY_SEPARATOR + port);
		if (connection != null) {
			peer_connections_monitor.send_queue.remove(connection);
			connection.setStatus(ConnectionStatus.DISCONNECTED);
		}
		peer_connections.remove(ip + KEY_SEPARATOR + port);*/
	}

	public void receivedCallBackFailed() {
		_peer_manager.callBackRequestFailed();
	}

	public void receivedCallBackRequest(String ip, int port) {
		_peer_manager.receivedCallBackRequest(ip, port, PeerSource.SERVER);
	}

	public void receivedCompressedFileChunkFromPeer(String peerIP,
			int peerPort, FileHash fileHash, FileChunk compressedFileChunk) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedCompressedFileChunk(sender,fileHash, compressedFileChunk);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
		
	}

	public void receivedEndOfDownloadFromPeer(String peerIP, int peerPort) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_upload_manager.endOfDownload(sender);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedFileChunkRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash, List<FileChunkRequest> requestedChunks) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_upload_manager.receivedFileChunkRequestFromPeer(sender,fileHash, requestedChunks);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
		
	}

	public void receivedFileNotFoundFromPeer(String peerIP, int peerPort,
			FileHash fileHash) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedFileNotFoundFromPeer(sender,fileHash);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedFileRequestAnswerFromPeer(String peerIP, int peerPort,
			FileHash fileHash, String fileName) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedFileRequestAnswerFromPeer(sender,fileHash, fileName);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedFileRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_upload_manager.receivedFileRequestFromPeer(sender,  fileHash);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedFileStatusRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_upload_manager.receivedFileStatusRequestFromPeer(sender,fileHash);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedFileStatusResponseFromPeer(String peerIP, int peerPort,
			FileHash fileHash, JMuleBitSet partStatus) {
		Peer sender;
		try {			
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedFileStatusResponseFromPeer(sender,fileHash, partStatus);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedHashSetRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_upload_manager.receivedHashSetRequestFromPeer(sender,fileHash);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedHashSetResponseFromPeer(String peerIP, int peerPort,
			PartHashSet partHashSet) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedHashSetResponseFromPeer(sender,partHashSet.getFileHash(), partHashSet);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
		
	}

	public void receivedHelloAnswerFromPeer(String peerIP, int peerPort,
			UserHash userHash, ClientID clientID, int peerPacketPort,
			TagList tagList, String serverIP, int serverPort) {
		JMPeerConnection connection = null;
		try {
			connection = getPeerConnection(peerIP, peerPort);
			peer_connections.remove(peerIP + KEY_SEPARATOR + peerPort);
			
			peer_connections.put(peerIP + KEY_SEPARATOR + peerPacketPort, connection);
			connection.setUsePort(peerPacketPort);
			getPeerConnection(peerIP, peerPacketPort);
		} catch (NetworkManagerException e) {
			e.printStackTrace();
			return;
		}
		
		_peer_manager.helloAnswerFromPeer(peerIP, peerPort, userHash, clientID,
				peerPacketPort, tagList, serverIP, serverPort);
		
		sendEMuleHelloPacket(connection.getIPAddress(), connection.getUsePort());
	}

	public void receivedHelloFromPeerAndRespondTo(String peerIP, int peerPort,
			UserHash userHash, ClientID clientID, int peerListenPort,
			TagList tagList, String serverIP, int serverPort) {
		try {
			JMPeerConnection connection = getPeerConnection(peerIP, peerPort);
			if (peerListenPort != 0) {
				peer_connections.remove(peerIP + KEY_SEPARATOR + peerPort);
			
				peer_connections.put(peerIP + KEY_SEPARATOR + peerListenPort, connection);
				connection.setUsePort(peerListenPort);
			}			
			
			Server connected_server = _server_manager.getConnectedServer();
			byte[] server_ip = null;
			int server_port = 0;
			ClientID client_id = null;
			if (connected_server != null) {
				server_ip = connected_server.getAddressAsByte();
				server_port = connected_server.getPort();
				client_id = connected_server.getClientID();
			}
			
			Packet packet = PacketFactory.getPeerHelloAnswerPacket(
					_config_manager.getUserHash(), client_id, _config_manager
							.getTCP(), _config_manager.getNickName(),
					server_ip, server_port, ED2KConstants.DefaultJMuleFeatures);
			sendPacket(connection, packet);
			
			sendEMuleHelloPacket(connection.getIPAddress(), connection.getUsePort());
			

			_peer_manager.helloFromPeer(peerIP, peerPort, userHash, clientID,
					peerListenPort, tagList, serverIP, serverPort);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}

	}

	public void receivedIDChangeFromServer(ClientID clientID,
			Set<ServerFeatures> serverFeatures) {
		_server_manager.receivedIDChange(clientID, serverFeatures);
	}

	public void receivedMessageFromServer(String message) {
		_server_manager.receivedMessage(message);
	}

	public void receivedNewServerDescription(String ip, int port,
			int challenge, TagList tagList) {
		_server_manager.receivedNewServerDescription(ip, port, challenge, tagList);
	}

	public void receivedQueueRankFromPeer(String peerIP, int peerPort,
			int queueRank) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedQueueRankFromPeer(sender, queueRank);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}

	}

	public void receivedRequestedFileChunkFromPeer(String peerIP, int peerPort,
			FileHash fileHash, FileChunk chunk) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedRequestedFileChunkFromPeer(sender,fileHash, chunk);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}

	}

	public void receivedSearchResult(SearchResultItemList resultList) {
		_search_manager.receivedServerSearchResult(resultList);
	}

	public void receivedServerDescription(String ip, int port, String name,
			String description) {
		_server_manager.receivedServerDescription(ip, port, name, description);
	}

	public void receivedServerList(List<String> ipList, List<Integer> portList) {
		_server_manager.receivedServerList(ipList, portList);
	}

	public void receivedServerStatus(int userCount, int fileCount) {
		_server_manager.receivedServerStatus(userCount, fileCount);
	}

	public void receivedOldServerStatus(String ip, int port, int challenge, long userCount, long fileCount) {
		_server_manager.receivedOldServerStatus(ip, port, challenge, userCount, fileCount);
	}
	
	public void receivedServerStatus(String ip, int port, int challenge,
			long userCount, long fileCount, long softLimit, long hardLimit,
			Set<ServerFeatures> serverFeatures) {
		_server_manager.receivedServerStatus(ip, port, challenge, userCount,
				fileCount, softLimit, hardLimit, serverFeatures);
	}

	public void receivedSlotGivenFromPeer(String peerIP, int peerPort) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedSlotGivenFromPeer(sender);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedSlotReleaseFromPeer(String peerIP, int peerPort) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_upload_manager.receivedSlotReleaseFromPeer(sender);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
		if (hasPeer(peerIP, peerPort)) {
			try {
				JMPeerConnection connection = getPeerConnection(peerIP, peerPort);
				connection.resetUploadedFileBytes();
			} catch (NetworkManagerException e1) {
				e1.printStackTrace();
			} 
		}
	}

	public void receivedSlotRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_upload_manager.receivedSlotRequestFromPeer(sender, fileHash);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
		
	}

	public void receivedSlotTakenFromPeer(String peerIP, int peerPort) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedSlotTakenFromPeer(sender);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedSourcesFromServer(FileHash fileHash, List<String> clientIPList, List<Integer> portList) {
		String server_ip = null;
		int server_port = 0;
		if (_server_manager.isConnected()) {
			server_ip = _server_manager.getConnectedServer().getAddress();
			server_port = _server_manager.getConnectedServer().getPort();
		}
		List<Peer> peer_list = _peer_manager.createPeerList(clientIPList, portList,  false, server_ip, server_port, PeerSource.SERVER);
		_download_manager.addDownloadPeers(fileHash, peer_list);
	}

	public void receiveKadPacket(KadPacket packet) {
		_jkad_manager.receivePacket(packet);
	}
	
	public void receivedEMuleHelloFromPeer(String ip, int port,
			byte clientVersion, byte protocolVersion, TagList tagList) {
		try {
			JMPeerConnection connection = getPeerConnection(ip, port);
			Packet response = PacketFactory.getEMulePeerHelloAnswerPacket();
			sendPacket(connection, response);
		} catch (Throwable e) {
			e.printStackTrace();
			return;
		}
		_peer_manager.receivedEMuleHelloFromPeer(ip, port, clientVersion,
				protocolVersion, tagList);
	}
	
	public void receivedEMuleHelloAnswerFromPeer(String ip, int port,
			byte clientVersion, byte protocolVersion, TagList tagList) {
		
		_peer_manager.receivedEMuleHelloAnswerFromPeer(ip, port, clientVersion,
				protocolVersion, tagList);
	}
	
	public void receivedSourcesRequestFromPeer(String peerIP, int peerPort, FileHash fileHash) {
		try {
			Peer peer = _peer_manager.getPeer(peerIP, peerPort);
			
			_sharing_manager.receivedSourcesRequestFromPeer(peer, fileHash);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}
	
	public void receivedSourcesAnswerFromPeer(String peerIP, int peerPort, FileHash fileHash, List<String> ipList, List<Integer> portList) {
		try {
			Peer peer = _peer_manager.getPeer(peerIP, peerPort);
			List<Peer> peer_list = _peer_manager.createPeerList(ipList, portList, true, null,0, PeerSource.PEX);
			_download_manager.receivedSourcesAnswerFromPeer(peer, fileHash, peer_list);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}
	
	public void receivedMultipacketRequest(String peerIP, int peerPort, FileHash fileHash, long fileSize, Collection<Byte> opCodeList) {
		try {
			Peer peer = _peer_manager.getPeer(peerIP, peerPort);
			Collection<ByteBuffer> response_entries = new ArrayList<ByteBuffer>();
			if (!_sharing_manager.hasFile(fileHash)) {
				response_entries.add(PacketFactory.getFileNotFoundResponseEntry());
			} else {
				SharedFile shared_file = _sharing_manager.getSharedFile(fileHash);
				
				if (shared_file.length()!=fileSize)
					response_entries.add(PacketFactory.getFileNotFoundResponseEntry());
				else
				for(Byte opCode : opCodeList) {
					if (opCode == OP_FILENAMEREQUEST)
						response_entries.add(PacketFactory.getFileNameResponseEntry( shared_file.getSharingName() ));
					else
						if (opCode == OP_FILESTATREQ) {
							if (shared_file.isCompleted())
								response_entries.add( PacketFactory.getCompletedFileStatusResponseEntry() );
							else
								response_entries.add( PacketFactory.getFileStatusResponseEntry( ((PartialFile)shared_file).getGapList(), shared_file.getHashSet().size(), shared_file.length() ) );
						} else
							if (opCode == OP_REQUESTSOURCES)
								sendSourcesResponse(peerIP, peerPort, fileHash, _sharing_manager.getFileSources(peer, fileHash));		
				}
			}
			JMPeerConnection peer_connection = getPeerConnection(peerIP,peerPort);
			Packet packet = PacketFactory.getMultipacketResponse(fileHash, response_entries);
			sendPacket(peer_connection, packet);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		} catch (NetworkManagerException e) {
			e.printStackTrace();
		}
		
	}

	public void requestSourcesFromServer(FileHash fileHash, long fileSize) {
		Packet packet = PacketFactory.getSourcesRequestPacket(fileHash,
				fileSize);
		server_connection_monitor.sendPacket(packet);
	}
	
	public void sendMessage(String peerIP, int peerPort, String message) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,peerPort);
			Packet packet = PacketFactory.getMessagePacket(message);
			sendPacket(peer_connection, packet);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void sendCallBackRequest(String peerIP, int peerPort,
			Int128 clientID, FileHash fileHash, IPAddress buddyIP,
			short buddyPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getKadCallBackRequest(clientID,
					fileHash, buddyIP, buddyPort);
			sendPacket(peer_connection, packet);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void sendEndOfDownload(String peerIP, int peerPort, FileHash fileHash) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,peerPort);
			Packet packet = PacketFactory.getEndOfDownloadPacket(fileHash);
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendFileChunk(String peerIP, int peerPort, FileHash fileHash,
			FileChunk fileChunk) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,peerPort);
			Packet packet = PacketFactory.getFilePartSendingPacket(fileHash,fileChunk);
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}

	}

	public void sendFileHashSetAnswer(String peerIP, int peerPort,
			PartHashSet partHashSet) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getFileHashReplyPacket(partHashSet);
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendFileNotFound(String peerIP, int peerPort, FileHash fileHash) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,peerPort);
			Packet packet = PacketFactory.getFileNotFoundPacket(fileHash);
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendFilePartsRequest(String peerIP, int peerPort,
			FileHash fileHash, FileChunkRequest... partsData) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getPeerRequestFileParts(fileHash,
					partsData);
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendFileNameRequest(String peerIP, int peerPort, FileHash fileHash) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP, peerPort);
			Packet packet = PacketFactory.getFileNameRequestPacket(fileHash);
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}
		
	public void sendFileNameRequest(String peerIP, int peerPort, FileHash fileHash, GapList fileGapList, long fileSize, int sourceCount) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP, peerPort);
			Packet packet = PacketFactory.getFileNameRequestPacket(fileHash, fileGapList, fileSize, sourceCount);
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendFileRequestAnswer(String peerIP, int peerPort,
			FileHash fileHash, String fileName) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getFileRequestAnswerPacket(fileHash,
					fileName);
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendFileStatusAnswer(String peerIP, int peerPort,
			PartHashSet partHashSet, long fileSize, GapList gapList) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getFileStatusReplyPacket(partHashSet,fileSize, gapList);
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendFileStatusRequest(String peerIP, int peerPort,
			FileHash fileHash) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getFileStatusRequestPacket(fileHash);
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendKadPacket(KadPacket packet, IPAddress address, int port) {
		try {
			udp_connection.sendPacket(packet, address, port);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}

	public void sendPartHashSetRequest(String peerIP, int peerPort,
			FileHash fileHash) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getRequestPartHashSetPacket(fileHash);
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendQueueRanking(String peerIP, int peerPort, int queueRank) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getQueueRankingPacket(queueRank);
			sendPacket(peer_connection, packet);
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public void sendSlotGiven(String peerIP, int peerPort, FileHash fileHash) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getAcceptUploadPacket(fileHash);
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendSlotRelease(String peerIP, int peerPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getSlotReleasePacket();
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendUploadRequest(String peerIP, int peerPort, FileHash fileHash) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getUploadReuqestPacket(fileHash);
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}
	
	public void sendSourcesRequest(String peerIP, int peerPort, FileHash fileHash) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getSourcesRequestPacket(fileHash);
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}
	
	public void sendSourcesResponse(String peerIP, int peerPort, FileHash fileHash, Collection<Peer> peer_list) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP, peerPort);
			Packet packet = PacketFactory.getSourcesAnswerPacket(fileHash, peer_list);
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}
	
	public void sendEMuleHelloPacket(String peerIP, int peerPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP, peerPort);
			Packet packet = PacketFactory.getEMulePeerHelloPacket();
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}
	
	public void sendEMuleHelloAnswerPacket(String peerIP, int peerPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP, peerPort);
			Packet packet = PacketFactory.getEMulePeerHelloAnswerPacket();
			sendPacket(peer_connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}
	
	
	public void sendMultiPacketRequest(String peerIP, int peerPort, FileHash fileHash, Collection<Byte> entries) {
		Collection<ByteBuffer> entries_content = new ArrayList<ByteBuffer>();
		for(Byte entryID : entries) {
			if (entryID == OP_FILENAMEREQUEST)
				entries_content.add(PacketFactory.getFileNameRequestEntry());
			else
				if (entryID == OP_REQUESTSOURCES)
					entries_content.add(PacketFactory.getFileSourcesRequestEntry());
			else
				if (entryID == OP_FILESTATREQ)
					entries_content.add(PacketFactory.getFileStatusRequestEntry());
		}
		Packet packet = PacketFactory.getMultipacketRequest(fileHash, entries_content);
		JMPeerConnection peer_connection;
		try {
			peer_connection = getPeerConnection(peerIP, peerPort);
			sendPacket(peer_connection, packet);
		} catch (NetworkManagerException e) {
			e.printStackTrace();
		}	
	}

	public void sendMultiPacketExtRequest(String peerIP, int peerPort, FileHash fileHash, long fileSize, Collection<Byte> entries) {
		Collection<ByteBuffer> entries_content = new ArrayList<ByteBuffer>();
		for(Byte entryID : entries) {
			if (entryID == OP_FILENAMEREQUEST)
				entries_content.add(PacketFactory.getFileNameRequestEntry());
			else
				if (entryID == OP_REQUESTSOURCES)
					entries_content.add(PacketFactory.getFileSourcesRequestEntry());
			else
				if (entryID == OP_FILESTATREQ)
					entries_content.add(PacketFactory.getFileStatusRequestEntry());
		}
		Packet packet = PacketFactory.getMultipacketExtRequest(fileHash, fileSize, entries_content);
		JMPeerConnection peer_connection;
		try {
			peer_connection = getPeerConnection(peerIP, peerPort);
			sendPacket(peer_connection, packet);
		} catch (NetworkManagerException e) {
			e.printStackTrace();
		}	
	}

	public void serverConnected() {
		try {
						
			if ((server_packet_processor == null)||(!server_packet_processor.isAlive())) {
				server_packet_processor = new ServerPacketProcessor();
				server_packet_processor.startProcessor();
			}
			
			Packet login_packet = PacketFactory.getServerLoginPacket(
					_config_manager.getUserHash(), _config_manager.getTCP(),
					_config_manager.getNickName());
			server_connection_monitor.sendPacket(login_packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void serverConnectingFailed(Throwable cause) {
		ServerConnectionMonitor old_monitor = server_connection_monitor;
		server_connection_monitor=null;
		old_monitor.JMStop();
		if (server_packet_processor != null)
			server_packet_processor.JMStop();
		_server_manager.serverConnectingFailed(old_monitor.getServerConnection().getIPAddress(),old_monitor.getServerConnection().getPort(), cause);
		old_monitor = null;
	}

	public void serverDisconnected() {
		ServerConnectionMonitor old_monitor = server_connection_monitor;
		server_connection_monitor = null;
		old_monitor.JMStop();
		
		_server_manager.serverDisconnected(old_monitor.getServerConnection().getIPAddress(),old_monitor.getServerConnection().getPort());
		old_monitor = null;
	}

	public void serverListRequest() {
		Packet packet = PacketFactory.getGetServerListPacket();
		try {
			server_connection_monitor.sendPacket(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}
	
	public long getUploadedFileBytes(String peerIP, int peerPort) {
		JMPeerConnection connection;
		try {
			connection = getPeerConnection(peerIP, peerPort);
			return connection.getUploadedFileBytes();
		} catch (NetworkManagerException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public void resetUploadedFileBytes(String peerIP, int peerPort) {
		JMPeerConnection connection;
		try {
			connection = getPeerConnection(peerIP, peerPort);
			connection.resetUploadedFileBytes();
		} catch (NetworkManagerException e) {
			e.printStackTrace();
		}
	}
	
	public void receivedPublicKey(String peerIP, int peerPort, byte[] key) {
		try {
			if (!_config_manager.isSecurityIdenficiationEnabled())
				return;
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
			return;
		}
		_peer_manager.receivedPublicKey(peerIP, peerPort, key);
	}
	
	public void receivedSignature(String peerIP, int peerPort, byte[] signature) {
		try {
			if (!_config_manager.isSecurityIdenficiationEnabled())
				return;
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
			return;
		}
		_peer_manager.receivedSignature(peerIP, peerPort, signature);
	}
	
	public void receivedSecIdentState(String peerIP, int peerPort, byte state, byte[] challenge) {
		try {
			if (!_config_manager.isSecurityIdenficiationEnabled())
				return;
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
			return;
		}
		_peer_manager.receivedSecIdentState(peerIP,peerPort,state,challenge);
	}
	
	public void sendPublicKeyPacket(String peerIP, int peerPort) {
		JMPeerConnection connection;
		try {
			connection = getPeerConnection(peerIP, peerPort);
			PublicKey public_key = _config_manager.getPublicKey();
			byte[] key = public_key.getEncoded();
			Packet packet = PacketFactory.getPublicKeyPacket(key);
			sendPacket(connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
		
	}
	
	public void sendSignaturePacket(String peerIP, int peerPort, byte[] challenge) {
		JMPeerConnection connection;
		try {
			connection = getPeerConnection(peerIP, peerPort);
			Peer peer = _peer_manager.getPeer(peerIP, peerPort);
			byte[] public_key = _peer_manager.getPublicKey(peer);
			if (public_key == null)
				throw new JMException("Don't have public key for peer "
						+ peerIP + ":" + peerPort);
			//Signature signature = Signature.getInstance("SHA1withRSA");
			SHA1WithRSAEncryption signature = new SHA1WithRSAEncryption();
			signature.initSign(_config_manager.getPrivateKey());
			signature.update(public_key);
			signature.update(challenge);
			byte[] sign = signature.sign();
			Packet packet = PacketFactory.getSignaturePacket(sign);
			sendPacket(connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
		
	}
	
	public void sendSecIdentStatePacket(String peerIP, int peerPort, boolean isPublicKeyNeeded, byte[] challenge) {
		JMPeerConnection connection;
		try {
			connection = getPeerConnection(peerIP, peerPort);
			Packet packet = PacketFactory.getSecureIdentificationPacket(challenge, isPublicKeyNeeded);
			sendPacket(connection, packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}
	
	public void sendServerUDPStatusRequest(String serverIP, int serverPort, int clientTime) {
		UDPPacket packet = UDPPacketFactory.getUDPStatusRequest(clientTime);
		try {
			udp_connection.sendPacket(packet, serverIP, serverPort);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}
	
	public void sendServerUDPDescRequest(String serverIP, int serverPort) {
		UDPPacket packet = UDPPacketFactory.getUDPServerDescRequest();
		try {
			udp_connection.sendPacket(packet, serverIP, serverPort);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}
	
	public void sendServerUDPSourcesRequest(String serverIP, int serverPort, Collection<FileHash> fileHashList) {
		UDPPacket packet = UDPPacketFactory.getSourcesRequest(fileHashList);
		try {
			udp_connection.sendPacket(packet, serverIP, serverPort);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}
	
	public void sendServerUDPSources2Request(String serverIP, int serverPort, List<FileHash> fileHashList, List<Long> fileSizeList) {
		UDPPacket packet = UDPPacketFactory.getSources2Request(fileHashList, fileSizeList);
		try {
			udp_connection.sendPacket(packet, serverIP, serverPort);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}
	
	
	public void sendServerUDPSearchRequest(String serverIP, int serverPort, SearchQuery searchQuery) {
		UDPPacket packet = UDPPacketFactory.getSearchPacket(searchQuery);
		try {
			udp_connection.sendPacket(packet, serverIP, serverPort);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}
	
	public void sendServerUDPSearch2Request(String serverIP, int serverPort, SearchQuery searchQuery) {
		UDPPacket packet = UDPPacketFactory.getSearch2Packet(searchQuery);
		try {
			udp_connection.sendPacket(packet, serverIP, serverPort);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}
	
	public void sendServerUDPSearch3Request(String serverIP, int serverPort, SearchQuery searchQuery) {
		UDPPacket packet = UDPPacketFactory.getSearch3Packet(searchQuery);
		try {
			udp_connection.sendPacket(packet, serverIP, serverPort);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}
	
	public void sendServerUDPReaskFileRequest(String serverIP, int serverPort, FileHash fileHash) {
		UDPPacket packet = UDPPacketFactory.getUDPReaskFilePacket(fileHash);
		try {
			udp_connection.sendPacket(packet, serverIP, serverPort);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}
	
	public void sendServerUDPCallBackRequest(String serverIP, int serverPort, ClientID clientID) {
		UDPPacket packet = UDPPacketFactory.getCallBackRequest(clientID);
		try {
			udp_connection.sendPacket(packet, serverIP, serverPort);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}
	

	public void receivePeerPacket(PacketFragment container) {
		peer_packet_processor.addPacket(container);	
	}
	
	public void receiveServerPacket(PacketFragment container) {
		server_packet_processor.addPacket(container);
	}
	
	public void tcpPortChanged() {
		connection_receiver.stopReceiver();
		connection_receiver = new IncomingConnectionReceiver();
		connection_receiver.startReceiver();
	}
	
	public void udpPortChanged() {
		udp_connection.reOpenConnection();
	}
	
	public void udpPortStatusChanged() {
		try {
			if (ConfigurationManagerSingleton.getInstance().isUDPEnabled()) {
				udp_connection.open();
				udp_connection.wakeUp();
			}
			else {
				udp_connection.haltThread();
				udp_connection.close();
			}
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Packet processors
	 */
	int getPacketCount(PacketFragment container) {
		ByteBuffer packetContent = container.getContent();
		packetContent.position(0);
		int count = 0;
		while(true) {

			if (packetContent.position()+1+4+1 > packetContent.capacity())
				break;
			byte header = packetContent.get();
			ByteBuffer i = Misc.getByteBuffer(4);
			packetContent.get(i.array());
			
			int packetLength = Convert.byteToInt(i.array());
			
			byte opCode = packetContent.get();
			
			packetLength--; // decrement opCode 
			
			int newPos = packetContent.position() + packetLength; // byte [packetContent.position() + packetLength] is from next packet
			
		
			if (packetLength < 0) {

				break;
			}
			
			if (newPos > container.getPacketLimit())				
				break;
			
			if (newPos > packetContent.capacity())
				break;
			
			count++;
			
			packetContent.position(newPos);
		
			
		}
		return count;
	}
	
	private void processServerPackets(PacketFragment container) throws UnknownPacketException, DataFormatException, MalformattedPacketException {
		
			ByteBuffer packetContent = container.getContent();
			packetContent.position(0);
			
			byte header = packetContent.get();
			
			ByteBuffer bytelen = Misc.getByteBuffer(4);
			packetContent.get(bytelen.array());
			int packetLength = Convert.byteToInt(bytelen.array());

			byte opCode = packetContent.get();
			packetLength--; //decrement opcode
			
			ByteBuffer rawPacket = Misc.getByteBuffer(packetLength);
			packetContent.get(rawPacket.array());
			
			rawPacket.position(0);
			
			
			container.moveUnusedBytes(packetContent.position());
			
			if (header == PROTO_EMULE_COMPRESSED_TCP) {
				rawPacket = JMuleZLib.decompressData(rawPacket);
				header = PROTO_EDONKEY_TCP;
				rawPacket.position(0);
			}
			try {
				switch (opCode) {
				case PACKET_SRVMESSAGE: {
					String server_message = readString(rawPacket);
					receivedMessageFromServer(server_message);

					return;
				}
				case PACKET_SRVIDCHANGE: {
					byte client_id[] = new byte[4];
					rawPacket.get(client_id);
					ClientID clientID = new ClientID(client_id);
					int server_features = rawPacket.getInt();
					Set<ServerFeatures> features = Utils.scanTCPServerFeatures(server_features);
					receivedIDChangeFromServer(clientID, features);
					return;
				}
				case PACKET_SRVSTATUS: {
					int user_count = rawPacket.getInt();
					int file_count = rawPacket.getInt();
					receivedServerStatus(user_count, file_count);
					return;
				}

				case OP_SERVERLIST: {
					int server_count = rawPacket.get();
					List<String> ip_list = new ArrayList<String>();
					List<Integer> port_list = new ArrayList<Integer>();
					for (int i = 0; i < server_count; i++) {
						byte address[] = new byte[4];
						int port;

						rawPacket.get(address);
						port = Convert.shortToInt(rawPacket.getShort());
						ip_list.add(Convert.IPtoString(address));
						port_list.add(port);
					}

					receivedServerList(ip_list, port_list);
					return;
				}

				case PACKET_SRVSEARCHRESULT: {
					int result_count = rawPacket.getInt();
					SearchResultItemList searchResults = new SearchResultItemList();
					for (int i = 0; i < result_count; i++) {
						byte fileHash[] = new byte[16];
						rawPacket.get(fileHash);

						byte clientID[] = new byte[4];
						rawPacket.get(clientID);

						short clientPort = rawPacket.getShort();

						SearchResultItem result = new SearchResultItem(
								new FileHash(fileHash), new ClientID(clientID),
								clientPort);
						int tag_count = rawPacket.getInt();

						for (int j = 0; j < tag_count; j++) {
							Tag tag = TagScanner.scanTag(rawPacket);
							result.addTag(tag);
						}
						// transform Server's file rating into eMule file rating
						if (result.hasTag(FT_FILERATING)) {
							Tag tag = result.getTag(FT_FILERATING);
							try {
								int data = (Integer) tag.getValue();
								data = Convert.byteToInt(Misc.getByte(data, 0));
								int rating_value = data / SERVER_SEARCH_RATIO;
								tag.setValue(rating_value);
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
						searchResults.add(result);

					}
					receivedSearchResult(searchResults);
					return;
				}

				case PACKET_SRVFOUNDSOURCES: {
					byte[] file_hash = new byte[16];
					rawPacket.get(file_hash);

					int source_count = Convert.byteToInt(rawPacket.get());
					List<String> client_ip_list = new ArrayList<String>();
					List<Integer> port_list = new ArrayList<Integer>();

					byte[] peerID = new byte[4];
					int peerPort;
					ByteBuffer data = Misc.getByteBuffer(4);
					for (int i = 0; i < source_count; i++) {
						for (int j = 0; j < 4; j++) {
							data.clear();
							data.rewind();
							byte b = rawPacket.get();
							data.put(b);
							peerID[j] = Convert.intToByte(data.getInt(0));
						}

						byte[] portArray = new byte[2];
						rawPacket.get(portArray);

						ByteBuffer tmpData = Misc.getByteBuffer(4);
						tmpData.put(portArray);
						tmpData.position(0);
						peerPort = tmpData.getInt();

						ClientID cid = new ClientID(peerID);
						// if (PeerManagerFactory.getInstance().hasPeer(cid))
						// continue;
						// if (PeerManagerFactory.getInstance().isFull()) continue;
						client_ip_list.add(cid.getAsString());
						port_list.add(peerPort);
					}
					receivedSourcesFromServer(new FileHash(file_hash), client_ip_list, port_list);
					return;
				}

				case PACKET_CALLBACKREQUESTED: {
					byte[] ip_address = new byte[4];
					int port;
					rawPacket.get(ip_address);
					port = Convert.shortToInt(rawPacket.getShort());
					receivedCallBackRequest(Convert.IPtoString(ip_address), port);
					return;
				}

				case PACKET_CALLBACKFAILED: {
					receivedCallBackFailed();
					return;
				}
				default:
					throw new UnknownPacketException(server_connection_monitor.serverConnection.getIPAddress(), server_connection_monitor.serverConnection.getPort(), header, opCode, rawPacket.array());
				}
			} catch (Throwable cause) {
				if (cause instanceof UnknownPacketException)
					throw (UnknownPacketException) cause;
				throw new MalformattedPacketException(server_connection_monitor.serverConnection.getIPAddress(), server_connection_monitor.serverConnection.getPort(), header, opCode, rawPacket.array(), cause);
			}
	//	}
	}
	
	private void processClientPackets(PacketFragment container) throws UnknownPacketException, DataFormatException, MalformattedPacketException {
		//for (PacketContainer container : clientPackets) {
			JMPeerConnection connection = (JMPeerConnection) container.getConnection();
			ByteBuffer packetContent = container.getContent();
			packetContent.position(0);

			byte header = packetContent.get();
			ByteBuffer bytelen = Misc.getByteBuffer(4);
			packetContent.get(bytelen.array());
			int packetLength = Convert.byteToInt(bytelen.array());
			byte opCode = packetContent.get();
			packetLength--; // decrement opcode
					
			ByteBuffer rawPacket = Misc.getByteBuffer(packetLength);
			packetContent.get(rawPacket.array());
			
			container.moveUnusedBytes(packetContent.position());

			if (header == PROTO_EMULE_COMPRESSED_TCP) {
				rawPacket = JMuleZLib.decompressData(rawPacket);
				rawPacket.position(0);
				header = PROTO_EDONKEY_TCP;
			}
			
			int peerPort = connection.getUsePort();
			String peerIP = (connection.getIPAddress());
			
			//drop packets from disconnected peers
			if (!hasPeer(peerIP, peerPort)) {
				return;
			}
			
			try {
				if (header == PROTO_EDONKEY_TCP)
					switch (opCode) {
					case OP_PEERHELLO: {
						byte[] data = new byte[16];
						rawPacket.get(); // skip user hash's length
						rawPacket.get(data);
						UserHash userHash = new UserHash(data);

						byte client_id[] = new byte[4];
						rawPacket.get(client_id);
						ClientID clientID = new ClientID(client_id);

						int tcpPort = Convert.shortToInt(rawPacket.getShort());

						TagList tag_list = readTagList(rawPacket);

						byte[] server_ip_array = new byte[4];
						rawPacket.get(server_ip_array);
						String server_ip = Convert.IPtoString(server_ip_array);
						int server_port;
						server_port = Convert.shortToInt(rawPacket.getShort());

						receivedHelloFromPeerAndRespondTo(peerIP,
								peerPort, userHash, clientID, tcpPort, tag_list,
								server_ip, server_port);

						break;
					}

					case OP_PEERHELLOANSWER: {
						byte[] data = new byte[16];
						rawPacket.get(data);
						UserHash userHash = new UserHash(data);

						byte client_id[] = new byte[4];
						rawPacket.get(client_id);
						ClientID clientID = new ClientID(client_id);

						int tcpPort = Convert.shortToInt(rawPacket.getShort());

						TagList tag_list = readTagList(rawPacket);

						byte[] server_ip_array = new byte[4];
						rawPacket.get(server_ip_array);
						String server_ip = Convert.IPtoString(server_ip_array);
						int server_port;
						server_port = Convert.shortToInt(rawPacket.getShort());

						receivedHelloAnswerFromPeer(peerIP,
								peerPort, userHash, clientID, tcpPort, tag_list,
								server_ip, server_port);

						break;
					}

					case OP_SENDINGPART: {
						byte[] file_hash = new byte[16];
						rawPacket.get(file_hash);
						long chunk_start = Convert.intToLong(rawPacket.getInt());
						long chunk_end = Convert.intToLong(rawPacket.getInt());
						ByteBuffer chunk_content = Misc.getByteBuffer(chunk_end
								- chunk_start);
						rawPacket.get(chunk_content.array());
						receivedRequestedFileChunkFromPeer(peerIP,
								peerPort, new FileHash(file_hash), new FileChunk(
										chunk_start, chunk_end, chunk_content));

						break;
					}

					case OP_REQUESTPARTS: {
						byte[] file_hash = new byte[16];
						rawPacket.get(file_hash);
						long[] startPos = new long[3];
						long[] endPos = new long[3];
						List<FileChunkRequest> chunks = new ArrayList<FileChunkRequest>();
						for (int i = 0; i < 3; i++)
							startPos[i] = Convert.intToLong(rawPacket.getInt());

						for (int i = 0; i < 3; i++)
							endPos[i] = Convert.intToLong(rawPacket.getInt());

						for (int i = 0; i < 3; i++) {
							if ((startPos[i] == endPos[i]) && (startPos[i] == 0))
								break;
							chunks
									.add(new FileChunkRequest(startPos[i],
											endPos[i]));
						}

						receivedFileChunkRequestFromPeer(peerIP,
								peerPort, new FileHash(file_hash), chunks);

						break;
					}

					case OP_END_OF_DOWNLOAD: {
						receivedEndOfDownloadFromPeer(peerIP,peerPort);
						break;
					}

					case OP_HASHSETREQUEST: {
						byte[] file_hash = new byte[16];
						rawPacket.get(file_hash);

						receivedHashSetRequestFromPeer(peerIP,peerPort, new FileHash(file_hash));
						break;
					}

					case OP_HASHSETANSWER: {
						byte[] file_hash = new byte[16];
						rawPacket.get(file_hash);
						int partCount = Convert.shortToInt(rawPacket.getShort());
						PartHashSet partSet = new PartHashSet(new FileHash(
								file_hash));
						byte[] partHash = new byte[16];

						for (short i = 1; i <= partCount; i++) {
							rawPacket.get(partHash);
							partSet.add(partHash);
						}
						receivedHashSetResponseFromPeer(peerIP,peerPort, partSet);
						break;
					}

					case OP_SLOTREQUEST: {
						byte[] file_hash = new byte[16];
						rawPacket.get(file_hash);
						receivedSlotRequestFromPeer(peerIP,
								peerPort, new FileHash(file_hash));
						break;
					}

					case OP_SLOTGIVEN: {
						receivedSlotGivenFromPeer(peerIP, peerPort);
						break;
					}

					case OP_SLOTRELEASE: {
						receivedSlotReleaseFromPeer(peerIP,peerPort);
						break;
					}

					case OP_SLOTTAKEN: {
						receivedSlotTakenFromPeer(peerIP, peerPort);
						break;
					}

					case OP_FILENAMEREQUEST: {
						byte[] file_hash = new byte[16];
						rawPacket.get(file_hash);

						receivedFileRequestFromPeer(peerIP,
								peerPort, new FileHash(file_hash));
						break;
					}

					case OP_FILEREQANSWER: {
						byte[] file_hash = new byte[16];
						rawPacket.get(file_hash);
						int name_length = Convert.shortToInt(rawPacket.getShort());
						ByteBuffer str_bytes = Misc.getByteBuffer(name_length);
						rawPacket.get(str_bytes.array());
						receivedFileRequestAnswerFromPeer(peerIP,
								peerPort, new FileHash(file_hash), new String(
										str_bytes.array()));
						break;
					}

					case OP_FILEREQANSNOFILE: {
						byte[] file_hash = new byte[16];
						rawPacket.get(file_hash);

						receivedFileNotFoundFromPeer(peerIP,peerPort, new FileHash(file_hash));
						break;
					}

					case OP_FILESTATREQ: {
						byte[] file_hash = new byte[16];
						rawPacket.get(file_hash);

						receivedFileStatusRequestFromPeer(peerIP, peerPort, new FileHash(file_hash));

						break;
					}

					case OP_FILESTATUS: {
						byte[] file_hash = new byte[16];
						rawPacket.get(file_hash);
						short partCount = rawPacket.getShort();
						int count = (partCount + 7) / 8;
						// if (((partCount + 7) / 8) != 0)
						// if (count == 0)
						// count++;

						byte[] data = new byte[count];
						for (int i = 0; i < count; i++)
							data[i] = rawPacket.get();

						JMuleBitSet bitSet;
						bitSet = Convert.byteToBitset(data);
						bitSet.setBitCount(partCount);

						receivedFileStatusResponseFromPeer(peerIP,
								peerPort, new FileHash(file_hash), bitSet);
						break;
					}

					case OP_MESSAGE: {
						int message_length = Convert.shortToInt(rawPacket
								.getShort());
						ByteBuffer message_bytes = Misc
								.getByteBuffer(message_length);
						rawPacket.get(message_bytes.array());
						String message = new String(message_bytes.array());
						message_bytes.clear();
						message_bytes = null;
						_peer_manager.receivedMessage(peerIP, peerPort, message);
						break;
					}

					default: {
						throw new UnknownPacketException( peerIP, peerPort, header, opCode, rawPacket.array());
					}
					}
				else if (header == PROTO_EMULE_EXTENDED_TCP)
					switch (opCode) {
					case OP_EMULE_HELLO: {
						byte client_version = rawPacket.get();
						byte protocol_version = rawPacket.get();
						TagList tag_list = readTagList(rawPacket);
						receivedEMuleHelloFromPeer(peerIP,
								peerPort, client_version, protocol_version,
								tag_list);
						break;
					}

					case OP_EMULEHELLOANSWER: {
						byte client_version = rawPacket.get();
						byte protocol_version = rawPacket.get();
						TagList tag_list = readTagList(rawPacket);
						receivedEMuleHelloAnswerFromPeer(peerIP,
								peerPort, client_version, protocol_version,
								tag_list);
						break;
					}

					case OP_COMPRESSEDPART: {
						byte[] file_hash = new byte[16];
						rawPacket.get(file_hash);

						long chunkStart = Convert.intToLong(rawPacket.getInt());
						long chunkEnd = Convert.intToLong(rawPacket.getInt());
						long compressedSize = rawPacket.capacity()
								- rawPacket.position();
						ByteBuffer data = Misc.getByteBuffer(compressedSize);
						rawPacket.get(data.array());

						receivedCompressedFileChunkFromPeer(
								peerIP, peerPort, new FileHash(file_hash),
								new FileChunk(chunkStart, chunkEnd, data));
						break;
					}

					case OP_EMULE_QUEUERANKING: {
						short queue_rank = rawPacket.getShort();
						receivedQueueRankFromPeer(peerIP,
								peerPort, Convert.shortToInt(queue_rank));
						break;
					}

					case OP_REQUESTSOURCES: {
						byte[] hash = new byte[16];
						rawPacket.get(hash);
						receivedSourcesRequestFromPeer(peerIP,
								peerPort, new FileHash(hash));
						break;
					}

					case OP_ANSWERSOURCES: {
						byte[] hash = new byte[16];
						rawPacket.get(hash);
						int source_count = Convert.shortToInt(rawPacket
								.getShort());
						List<String> ip_list = new ArrayList<String>();
						List<Integer> port_list = new ArrayList<Integer>();
						byte[] ip = new byte[4];
						short port;
						for (int k = 0; k < source_count; k++) {
							rawPacket.get(ip);
							port = rawPacket.getShort();

							ip_list.add(Convert.IPtoString(ip));
							port_list.add(Convert.shortToInt(port));
						}
						receivedSourcesAnswerFromPeer(peerIP,
								peerPort, new FileHash(hash), ip_list, port_list);
						break;
					}

					case OP_CHATCAPTCHAREQ: {
						rawPacket.get();

						ByteBuffer image_data = Misc.getByteBuffer( - 2);
						image_data.position(0);
						rawPacket.get(image_data.array());
						image_data.position(0);
						_peer_manager.receivedCaptchaImage(peerIP, peerPort,
								image_data);
						break;
					}

					case OP_CHATCAPTCHARES: {
						byte response = rawPacket.get();
						_peer_manager.receivedCaptchaStatusAnswer(peerIP, peerPort,
								response);
						break;
					}
					
					case OP_PUBLICKEY : {
						int key_length = Convert.byteToInt(rawPacket.get());
						byte[] public_key = new byte[key_length];
						rawPacket.get(public_key);
						receivedPublicKey(peerIP, peerPort, public_key);
						break;
					}
					
					case OP_SIGNATURE : {
						int sig_length = Convert.byteToInt(rawPacket.get());
						byte[] signature = new byte[sig_length];
						rawPacket.get(signature);
						receivedSignature(peerIP, peerPort, signature);
						break;
					}
					
					case OP_SECIDENTSTATE: {
						byte state = rawPacket.get();
						byte[] challenge = new byte[4];
						rawPacket.get(challenge);
						receivedSecIdentState(peerIP, peerPort, state, challenge);
						break;
					}
					
					case OP_MULTIPACKET : {
						byte[] hash = new byte[16];
						rawPacket.get(hash);
						FileHash fileHash = new FileHash(hash);
						Collection<Byte> entries = new ArrayList<Byte>();
						while(rawPacket.hasRemaining()) {
							entries.add(rawPacket.get());
						}
						
						long fileSize = 0;
						if (_sharing_manager.hasFile(fileHash))
							fileSize = _sharing_manager.getSharedFile(fileHash).length();
						
						receivedMultipacketRequest(peerIP,peerPort,fileHash,fileSize,entries);
						break;
					}
					
					case OP_MULTIPACKET_EXT : {
						byte[] hash = new byte[16];
						rawPacket.get(hash);
						FileHash fileHash = new FileHash(hash);
						long fileSize = rawPacket.getLong();
						Collection<Byte> entries = new ArrayList<Byte>();
						while(rawPacket.hasRemaining()) {
							entries.add(rawPacket.get());
						}

						receivedMultipacketRequest(peerIP,peerPort,fileHash,fileSize,entries);
						break;
					}
					
					case OP_MULTIPACKETANSWER : {
						byte[] hash = new byte[16];
						rawPacket.get(hash);
						FileHash fileHash = new FileHash(hash);
						while(rawPacket.hasRemaining()) {
							byte entryCode = rawPacket.get();
							if (entryCode == OP_FILEREQANSNOFILE) {
								receivedFileNotFoundFromPeer(peerIP, peerPort, fileHash);
								break;
							}
							if (entryCode == OP_FILEREQANSWER) {
								int name_length = Convert.shortToInt(rawPacket.getShort());
								ByteBuffer str_bytes = Misc.getByteBuffer(name_length);
								rawPacket.get(str_bytes.array());
								receivedFileRequestAnswerFromPeer(peerIP,peerPort, fileHash, new String(str_bytes.array()));
								continue;
							}
							if (entryCode == OP_FILESTATUS) {
								short partCount = rawPacket.getShort();
								int count = (partCount + 7) / 8;
								// if (((partCount + 7) / 8) != 0)
								// if (count == 0)
								// count++;

								byte[] data = new byte[count];
								for (int i = 0; i < count; i++)
									data[i] = rawPacket.get();

								JMuleBitSet bitSet;
								bitSet = Convert.byteToBitset(data);
								bitSet.setBitCount(partCount);

								receivedFileStatusResponseFromPeer(peerIP,peerPort, fileHash, bitSet);
								continue;
							}
						}
						break;
					}
					
					default: {
						throw new UnknownPacketException(peerIP, peerPort,header, opCode, rawPacket.array());
					}
					}
			} catch (Throwable cause) {
				if (cause instanceof UnknownPacketException)
					throw (UnknownPacketException) cause;
				throw new MalformattedPacketException(peerIP, peerPort, header,opCode, rawPacket.array(), cause);
			}
			if ((opCode == OP_SENDINGPART)
					|| (opCode == OP_COMPRESSEDPART)) {
				connection.getJMConnection().file_transfer_trafic
						.addReceivedBytes(connection.getJMConnection().transferred_bytes);
			} else {
				connection.getJMConnection().service_trafic.addReceivedBytes(connection.getJMConnection().transferred_bytes);
			}
			connection.getJMConnection().transferred_bytes = 0;
			
			
		//}
	}
	
	private void processUDPPacket(UDPPacket packet) throws UnknownPacketException, MalformattedPacketException, NetworkManagerException {
		
		boolean kad_enabled = false;
		PacketType type;
		try {
			kad_enabled = ConfigurationManagerSingleton.getInstance().isJKadAutoconnectEnabled();
		} catch (ConfigurationManagerException cause) {
			cause.printStackTrace();
		}
		
		ByteBuffer packet_content = packet.getAsByteBuffer();
		InetSocketAddress packet_sender = packet.getAddress();
					
			
		if (packet_content.get(0) == PROTO_EDONKEY_SERVER_UDP)
			type = PacketType.SERVER_UDP;
		else if (packet_content.get(0) == PROTO_EDONKEY_PEER_UDP)
			type = PacketType.PEER_UDP;
		else if (packet_content.get(0) == JKadConstants.PROTO_KAD_UDP) {
			if (!kad_enabled)
				return;
			type = PacketType.KAD;
		} else if (packet_content.get(0) == JKadConstants.PROTO_KAD_COMPRESSED_UDP) {
			if (!kad_enabled)
				return;
			type = PacketType.KAD_COMPRESSED;
		} else
			throw new NetworkManagerException("Unknown UDP packet header : " + Convert.byteToHex(packet_content.get(0)));
		
		InternalNetworkManager _network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		InternalIPFilter _ipfilter = (InternalIPFilter) IPFilterSingleton.getInstance();
		if ((type == PacketType.KAD) || (type == PacketType.KAD_COMPRESSED)) {
			_network_manager.receiveKadPacket(new KadPacket(packet_content,packet_sender));
			return;
		}
		//while(packet_content.hasRemaining()) {
		byte packet_protocol = packet_content.get(0);
		byte packet_op_code = packet_content.get(1);
		packet_content.position(1 + 1);
		String ip = packet_sender.getAddress().getHostAddress();
		int port = packet_sender.getPort();
		if (_ipfilter.isServerBanned(ip)) {
			return;
		}

		// System.out.println("UDP Packet  " + ip + ":" + port + " Protocol : "
		// + Convert.byteToHex(packet_protocol)+"  OpCode : " +
		// Convert.byteToHex(packet_op_code));

		try {
			if (packet_protocol == PROTO_EDONKEY_SERVER_UDP) {
				switch (packet_op_code) {
				case OP_GLOBSERVSTATUS: {
					if (packet_content.limit() < 15) {
						int challenge = packet_content.getInt();
						long user_count = Convert.intToLong(packet_content.getInt());
						long files_count = Convert.intToLong(packet_content.getInt());
						_network_manager.receivedOldServerStatus(ip, port, challenge, user_count, files_count);
						break;
					}
					int challenge = packet_content.getInt();
					long user_count = Convert.intToLong(packet_content.getInt());
					long files_count = Convert.intToLong(packet_content.getInt());
					long soft_limit = Convert.intToLong(packet_content.getInt());
					long hard_limit = Convert.intToLong(packet_content.getInt());
					int udp_flags = packet_content.getInt();
					Set<ServerFeatures> server_features = Utils.scanUDPServerFeatures(udp_flags);
									
					_network_manager.receivedServerStatus(ip, port, challenge, user_count, files_count, soft_limit, hard_limit, server_features);
					break;
				}
				case OP_SERVER_DESC_ANSWER: {
					boolean new_packet = false;
					short test_short = packet_content.getShort();
					
					if (test_short == INVALID_SERVER_DESC_LENGTH)
						new_packet = true;
					
					packet_content.position(packet_content.position() - 2);
					if (new_packet) {
						int challenge = packet_content.getInt();
						TagList tag_list = readTagList(packet_content);
						_network_manager.receivedNewServerDescription(ip, port,challenge, tag_list);
						break;
					}
					
					String server_name = readString(packet_content);
					String server_desc = readString(packet_content);
					_network_manager.receivedServerDescription(ip, port, server_name, server_desc);
					break;
				}
				
				case OP_GLOBSEARCHRES : {
					SearchResultItemList search_results = new SearchResultItemList();
					//while(packet_content.hasRemaining()) {
						byte[] byte_file_hash = new byte[16];
						packet_content.get(byte_file_hash);
						byte[] client_id = new byte[4];
						packet_content.get(client_id);
						short clientPort = packet_content.getShort();
						SearchResultItem result = new SearchResultItem(new FileHash(byte_file_hash), new ClientID(client_id), clientPort);
						int tag_count = packet_content.getInt();
						for (int j = 0; j < tag_count; j++) {
							Tag tag = TagScanner.scanTag(packet_content);
							result.addTag(tag);
						}
						// transform Server's file rating into eMule file rating
						if (result.hasTag(FT_FILERATING)) {
							Tag tag = result.getTag(FT_FILERATING);
							try {
								int data = (Integer) tag.getValue();
								data = Convert.byteToInt(Misc.getByte(data, 0));
								int rating_value = data / SERVER_SEARCH_RATIO;
								tag.setValue(rating_value);
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
						search_results.add(result);
					//}
					_search_manager.receivedServerUDPSearchResult(search_results);
					break;
				}
				
				case OP_GLOBFOUNDSOURCES : {
					byte[] byte_file_hash = new byte[16];
					packet_content.get(byte_file_hash);
					int source_count = Convert.byteToInt(packet_content.get());
					List<String> client_id_list = new ArrayList<String>();
					List<Integer> client_port_list = new ArrayList<Integer>();
					for (int i = 0; i < source_count; i++) {
						byte[] client_id = new byte[4];
						packet_content.get(client_id);
						client_id_list.add(new ClientID(client_id).getAsString());
						
						ByteBuffer port_byte = Misc.getByteBuffer(2);
						packet_content.get(port_byte.array());
						int client_port = Convert.byteToInt(port_byte.array());
						client_port_list.add(client_port);
					}
					List<Peer> peer_list = _peer_manager.createPeerList(client_id_list, client_port_list, false, ip, port, PeerSource.GLOBAL);
					_download_manager.addDownloadPeers(new FileHash(byte_file_hash), peer_list);
					
					break;
				}
				
				case OP_CALLBACKREQUESTED : {
					byte[] client_ip_byte = new byte[4];
					packet_content.get(client_ip_byte);
					String peer_ip = new ClientID(client_ip_byte).getAsString();
					ByteBuffer port_byte = Misc.getByteBuffer(2);
					packet_content.get(port_byte.array());
					int client_port = Convert.byteToInt(port_byte.array());
					_peer_manager.receivedCallBackRequest(peer_ip, client_port, PeerSource.GLOBAL);
					break;
				}
	
				default: {
					throw new UnknownPacketException(packet.getAddress().getAddress().getHostAddress(), packet.getAddress().getPort(), packet_protocol, packet_op_code, udp_packet_buffer.array());
				}
				}
			}
		
		} catch (Throwable cause) {
			if (cause instanceof UnknownPacketException)
				throw (UnknownPacketException) cause;
			_ipfilter.addServer(ip, BannedReason.BAD_PACKETS, _network_manager);
			throw new MalformattedPacketException(packet.getAddress().getAddress().getHostAddress(), packet.getAddress().getPort(), packet_protocol, packet_op_code, udp_packet_buffer.array(), cause);
			//throw new MalformattedPacketException(udp_packet_buffer, cause);
		}
	}

	private String readString(ByteBuffer packet) {
		int message_length = (packet.getShort());
		ByteBuffer bytes = Misc.getByteBuffer(message_length);
		bytes.position(0);
		for (int i = 0; i < message_length; i++)
			bytes.put(packet.get());
		return new String(bytes.array());
	}
	
	private TagList readTagList(ByteBuffer packet) {
		int tag_count = packet.getInt();
		TagList tag_list = new TagList();
		for (int i = 0; i < tag_count; i++) {
			Tag Tag = TagScanner.scanTag(packet);
			tag_list.addTag(Tag);
		}
		return tag_list;
	}
	
	/*
	 * Network process threads 
	 */
	private class IncomingConnectionReceiver extends JMThread {
		private ServerSocketChannel listenSocket = null;
		private Selector receiverSelector;
		private boolean loop = true;
		
		public IncomingConnectionReceiver() {
			super("Incoming connection receiver");
		}
		
		public void startReceiver() {
			try {
				receiverSelector = Selector.open();
				listenSocket = ServerSocketChannel.open();
				int tcp_port = 0;
				try {			
					tcp_port = ConfigurationManagerSingleton.getInstance().getTCP();
					listenSocket.socket().bind(new InetSocketAddress( tcp_port ));
					listenSocket.configureBlocking(false);
					loop = true;
					start();
				} catch (Throwable cause) {
					if (cause instanceof BindException) {
						System.out.println("Port " + tcp_port +" is already in use by other application");
					}
					cause.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void stopReceiver() {
			loop = false;
			receiverSelector.wakeup();
		}
		
		public void run() {
			try {
				listenSocket.register(receiverSelector, SelectionKey.OP_ACCEPT);
			} catch (ClosedChannelException e) {
				e.printStackTrace();
			}
			
			while(loop) {
				int selectedKeys;
				try {
					selectedKeys = receiverSelector.select(99999);
					if (selectedKeys == 0) continue;
					Iterator<SelectionKey> keys = receiverSelector.selectedKeys().iterator();
					while(keys.hasNext()) {
						SelectionKey key = keys.next();
						keys.remove();
						
						if (key.isAcceptable()) {
							SocketChannel client_channel = ((ServerSocketChannel)key.channel()).accept();
							InetSocketAddress address = (InetSocketAddress) client_channel.socket().getRemoteSocketAddress();
							String ip_address = address.getAddress().getHostAddress();
							if (_ip_filter.isPeerBanned(ip_address)) {
								client_channel.close();
								continue;
							}
							JMPeerConnection peer_connection = new JMPeerConnection(new JMuleSocketChannel(client_channel));
							addPeer(peer_connection);
						}
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
				
			}
			try {
				receiverSelector.close();
				listenSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void sendPacket(JMPeerConnection connection, Packet packet) {
		if (connection.getStatus() != ConnectionStatus.CONNECTED)
			return;
		
		if ((!ED2KConstants.PEER_PACKETS_NOT_ALLOWED_TO_COMPRESS.contains(packet.getCommand())) && (packet.getLength() >= ED2KConstants.PACKET_SIZE_TO_COMPRESS)) {
			byte op_code = packet.getCommand(); 
			ByteBuffer raw_data = Misc.getByteBuffer(packet.getLength()-1-4-1);
			ByteBuffer data = packet.getAsByteBuffer();
			data.position(1 + 4 + 1);
			raw_data.put(data);
			raw_data.position(0);
			ByteBuffer compressedData;
			compressedData = JMuleZLib.compressData(raw_data);
			if (compressedData.capacity() < raw_data.capacity()) {
				raw_data.clear();
				raw_data = null;
				packet = new Packet(compressedData.capacity(), ED2KConstants.PROTO_EMULE_COMPRESSED_TCP);
				packet.setCommand(op_code);
				compressedData.position(0);
				packet.insertData(compressedData);
			} else {
				raw_data.clear();
				raw_data = null;
				
				compressedData.clear();
				compressedData = null;
			}
		}
		packet.getAsByteBuffer().position(0);
		
		SendPacketContainer container = new SendPacketContainer(packet, connection);
		Queue<SendPacketContainer> peerQueue = peer_connections_monitor.send_queue.get(connection);
		if (peerQueue == null) {
			peerQueue = new ConcurrentLinkedQueue<SendPacketContainer>();
			peer_connections_monitor.send_queue.put(connection, peerQueue);
		}
		peerQueue.offer(container);
		if (!channelsToWrite.contains(connection)) {
			channelsToWrite.offer(connection);
		}
		
		if (peer_connections_monitor.isSelecting) {
			peerSelector.wakeup();
		} else {
			//processWriteConnections();
		}
	}
	
	void processWriteConnections() {
		for (JMPeerConnection connection : channelsToWrite) {
			SocketChannel channel = connection.getJMConnection().getChannel();
			channelsToWrite.remove(connection);
			if (connection.isInterestedOPS(InterestedOPS.OP_WRITE)) continue;
			try {
				channel.register(peerSelector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, connection);
				connection.installOPS(InterestedOPS.OP_WRITE,InterestedOPS.OP_READ);
			} catch (ClosedChannelException e) {
				e.printStackTrace();
				try {
					connection.disconnect();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				peerDisconnected(connection.getIPAddress(), connection.getUsePort());
			}
		}
	}
	
	
	class SendPacketContainer {
		public Packet packet;
		public JMPeerConnection peer_connection;
		public byte opCode;
		public long lastUpdate;
		public SendPacketContainer(Packet packet, JMPeerConnection peerConnection) {
			this.packet = packet;
			this.peer_connection = peerConnection;
			this.opCode = packet.getCommand();
			this.lastUpdate = System.currentTimeMillis();
			packet.getAsByteBuffer().position(0);
		}
		
		public String toString() {
			return " capacity : " + packet.getAsByteBuffer().capacity() + " remaining : " + packet.getAsByteBuffer().remaining() + " position : " + packet.getAsByteBuffer().position();
		}
	}
	
	private Selector peerSelector;
	private Queue<JMPeerConnection> channelsToConnect = new ConcurrentLinkedQueue<JMPeerConnection>();
	private Queue<JMPeerConnection> channelsToWrite = new ConcurrentLinkedQueue<JMPeerConnection>();
		
	private class PeerConnectionsMonitor extends JMThread {
		
		private boolean loop = true;
		volatile boolean isSelecting = false;
		
	    private static final int DISABLE_READ = ~SelectionKey.OP_READ;
	    private static final int DISABLE_WRITE = ~SelectionKey.OP_WRITE;
		
	    private Collection<SelectionKey> must_install_read = new ConcurrentLinkedQueue<SelectionKey>();
		private Collection<SelectionKey> must_install_write = new ConcurrentLinkedQueue<SelectionKey>();
		
		public String toString() {
			String result = "must_install_read : " + must_install_read.size()+"\n";
			result += "must_install_write : " + must_install_write.size()+"\n";
			String k = "";
			for(JMPeerConnection key : send_queue.keySet()) {
				String s = "";
				for(SendPacketContainer container : send_queue.get(key)) 
					s += " <" + container+"> ";
				k += key.getIPAddress() + " : " + key.getPort() + "  ::  size : " + send_queue.get(key).size() + " :: " + s +"\n";
				
			}
			result += "send queue : \n" + k + " ";
			
			return result;
		}
		
		public PeerConnectionsMonitor() {
			super("Peer connections monitor");
		}
		
		public void startMonitor() {
			try {
				peerSelector = Selector.open();
				loop = true;
				
				start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void stopMonitor() {
			loop = false;
			try {
				peerSelector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
				
		public synchronized void installMonitor(JMPeerConnection peerConnection) {
			boolean needToConnect = false;
			if (channelsToConnect.isEmpty())
				needToConnect = true;
			channelsToConnect.offer(peerConnection);
			
			if (needToConnect)
				if (isSelecting) 
					peerSelector.wakeup();
		}
		
		private void processIncomingConnections() {
			while (!channelsToConnect.isEmpty()) {
				JMPeerConnection connection = channelsToConnect.poll();
				if (connection == null) continue;
				if (connection.getStatus() == ConnectionStatus.CONNECTED) {
					SocketChannel peerChannel = connection.getJMConnection().getChannel();
					try {
						peerChannel.socket().setKeepAlive(true);
						peerChannel.configureBlocking(false);
						peerChannel.register(peerSelector,SelectionKey.OP_READ, connection);
						connection.installOPS(InterestedOPS.OP_READ);
					} catch (IOException e) {
						e.printStackTrace();
						peerIOError(connection.getIPAddress(), connection.getPort(), e);
					}
				} else if (connection.getStatus() == ConnectionStatus.DISCONNECTED) {
					try {
						connection.setJMConnection(new JMuleSocketChannel(SocketChannel.open()));
						SocketChannel peerChannel = connection.getJMConnection().getChannel();
						peerChannel.configureBlocking(false);
						peerChannel.socket().setKeepAlive(true);
						peerChannel.register(peerSelector,SelectionKey.OP_CONNECT, connection);

						connection.setStatus(ConnectionStatus.CONNECTING);
						connection.connect();
					} catch (IOException e) {
						e.printStackTrace();
						peerConnectingFailed(connection.getIPAddress(), connection.getPort(), e);
					}
				}
			}

		}
		
	    void processPendingKeys() {
			for (SelectionKey key : must_install_read) {
				if (download_controller.getAvailableByteCount(false) == 0)
					break;
				must_install_read.remove(key);
				if (!key.isValid())
					continue;
				JMPeerConnection connection = (JMPeerConnection) key.attachment();
				if (connection.isInterestedOPS(InterestedOPS.OP_READ))
					continue;
				try {
					key.interestOps(SelectionKey.OP_READ | key.interestOps());
					connection.installOPS(InterestedOPS.OP_READ);
				} catch (Throwable t) {
					System.out.println("Error at key : " + key.attachment());
					peerIOError(connection.getIPAddress(),connection.getUsePort(), t);
					t.printStackTrace();
				}
			}
			
			for (SelectionKey key : must_install_write) {
				if (upload_controller.getAvailableByteCount(false) == 0)
					break;
				must_install_write.remove(key);
				if (!key.isValid())
					continue;
				JMPeerConnection connection = (JMPeerConnection) key.attachment();
				if (connection.isInterestedOPS(InterestedOPS.OP_WRITE))
					continue;
				try {
					key.interestOps(SelectionKey.OP_WRITE | key.interestOps());
					connection.installOPS(InterestedOPS.OP_WRITE);
				} catch (Throwable t) {
					System.out.println("Error at key : " + key.attachment());
					peerIOError(connection.getIPAddress(),connection.getUsePort(), t);
					t.printStackTrace();
				}
			}
	    }
	    
	    private void read(JMPeerConnection connection) {
			try {
				PacketFragment container = new PacketFragment(connection, ConfigurationManager.NETWORK_READ_BUFFER);
				int bytes = connection.getJMConnection().read(container.getContent());
				container.upadateLength();
				if (bytes != 0) {
					container.setPacketLimit(bytes);
					receivePeerPacket(container);
				}
			} catch (JMEndOfStreamException e) {
				System.out.println("Exception in : " + connection);
				e.printStackTrace();
				try {
					connection.getJMConnection().getChannel().register(peerSelector, 0, connection);
					connection.clearInterestedOPS();
				} catch (ClosedChannelException e1) {
					e1.printStackTrace();
				}
				try {
					connection.disconnect();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				peerDisconnected(connection.getIPAddress(), connection.getUsePort());
			} catch (IOException e) {
				System.out.println("Exception in : " + connection);
				connection.incrementIOErrors();
				if (connection.getIoErrors() > 3)
					disconnectPeer(connection.getIPAddress(), connection.getUsePort());
				e.printStackTrace();
			}
		}
	    
	    private Map<JMPeerConnection,Queue<SendPacketContainer>> send_queue = new ConcurrentHashMap<JMPeerConnection, Queue<SendPacketContainer>>();
	    
	    private void write(JMPeerConnection connection) {
			Queue<SendPacketContainer> peer_queue = send_queue.get(connection);
			
			if ((peer_queue==null) || (peer_queue.isEmpty())) {
				try {
					send_queue.remove(connection);
					connection.getJMConnection().getChannel().register(peerSelector, SelectionKey.OP_READ, connection);
					connection.removeOPS(InterestedOPS.OP_WRITE);
				} catch (ClosedChannelException e) {
					e.printStackTrace();
					try {
						connection.disconnect();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					peerDisconnected(connection.getIPAddress(), connection.getUsePort());
				}
				return;
			}

			SendPacketContainer container = peer_queue.peek();
			Packet packet = container.packet;
			int transferredBytes;
			try {
				container.lastUpdate = System.currentTimeMillis();
				transferredBytes = connection.getJMConnection().write(packet.getAsByteBuffer());
				
				if ((container.opCode == OP_SENDINGPART) || (container.opCode == OP_COMPRESSEDPART)) {
					connection.getJMConnection().file_transfer_trafic.addSendBytes(transferredBytes);
					connection.addUploadedBytes(transferredBytes);
				} else {
					connection.getJMConnection().service_trafic.addSendBytes(transferredBytes);
				}
			} catch (JMEndOfStreamException e) {
				System.out.println("Exception in : " + connection);
				e.printStackTrace();
				connection.setStatus(ConnectionStatus.DISCONNECTED);
				try {
					connection.getJMConnection().getChannel().register(peerSelector, 0,connection);
					connection.clearInterestedOPS();
				} catch (ClosedChannelException e1) {
					System.out.println("Exception in : " + connection);
					e1.printStackTrace();
				}
				try {
					connection.disconnect();
				} catch (IOException e1) {
					System.out.println("Exception in : " + connection);
					e1.printStackTrace();
				}
				peerDisconnected(connection.getIPAddress(), connection.getUsePort());
			} catch (IOException e) {
				System.out.println("Exception in : " + connection);
				e.printStackTrace();
				connection.incrementIOErrors();
				if (connection.getIoErrors() > 3)
					disconnectPeer(connection.getIPAddress(), connection.getUsePort());
			}
			//System.out.println("Sended :: " + connection + " :: capacity : " + packet.getAsByteBuffer().capacity() + " : remaining : " + packet.getAsByteBuffer().remaining() + " : position :: " + packet.getAsByteBuffer().position() + " :: " + peer_queue.size() );
			if (!packet.getAsByteBuffer().hasRemaining()) {
				SendPacketContainer c = peer_queue.poll();
				c.packet.clear();
			}
			
				//System.out.println(" Remaining packets :: " + connection + " :: "  + peer_queue.size() );
				
				if (peer_queue.isEmpty()) {
					//System.out.println("Disable OP_WRITE :: " + System.currentTimeMillis() + " :: " + connection +" :: send_queue :: " + peer_queue.size());
					try {
						send_queue.remove(connection);
						connection.getJMConnection().getChannel().register(peerSelector, SelectionKey.OP_READ, connection);
						connection.removeOPS(InterestedOPS.OP_WRITE);
					} catch (ClosedChannelException e) {
						e.printStackTrace();
						try {
							connection.disconnect();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						peerDisconnected(connection.getIPAddress(), connection.getUsePort());
					}
				}
		}
	    
	    JMTimerTask cleaner_task;
	    
	    
		public void run() {
			
			cleaner_task = new JMTimerTask() {
				@Override
				public void run() {
					boolean needGC = false;
					/*for(JMPeerConnection key : send_queue.keySet()) {
						Queue<SendPacketContainer> queue = send_queue.get(key);
						for (SendPacketContainer container : queue)
							if (System.currentTimeMillis() - container.lastUpdate >= DROP_SEND_QUEUE_TIMEOUT) {
								System.out.println("Send queue cleaner :: Drop packet for :: " + (System.currentTimeMillis() - container.lastUpdate) + " :: "+ key);
								queue.remove(container);
								container.packet.clear();
								needGC = true;
							}
						if (queue.isEmpty()) {
							send_queue.remove(key);
							queue = null;
						}
					}*/
					if (needGC)
						System.gc();
				}
			};
			connections_maintenance.addTask(cleaner_task,SEND_QUEUE_SCAN_INTERVAL, true);
			while (loop) {

				if (!channelsToConnect.isEmpty()) {
					processIncomingConnections();
				}

				if (!channelsToWrite.isEmpty()) {
					processWriteConnections();
				}
				
				processPendingKeys();
				
				int selectedConnections = 0;
				try {
					selectedConnections = peerSelector.selectNow();
					if (selectedConnections == 0) {
						isSelecting = true;
						selectedConnections = peerSelector.select(PEER_CONNECTIONS_MONITOR_SELECT);
						isSelecting = false;
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				if (!channelsToConnect.isEmpty()) {
					processIncomingConnections();
				}

				if (!channelsToWrite.isEmpty()) {
					processWriteConnections();
				}

				processPendingKeys();
				
				if (selectedConnections == 0)
					continue;

				Iterator<SelectionKey> keys = peerSelector.selectedKeys().iterator();
				long x = System.currentTimeMillis();
				while (keys.hasNext()) {
					long a = System.currentTimeMillis();
					try {
						SelectionKey key = keys.next();
						keys.remove();
						JMPeerConnection connection = (JMPeerConnection) key.attachment();
						if (!key.isValid()) {
							continue;
						}
						SocketChannel peerChannel = connection.getJMConnection().getChannel();
						if (key.isConnectable()) {
							if (peerChannel.isConnectionPending()) {
								try {
									peerChannel.finishConnect();
									connection.setStatus(ConnectionStatus.CONNECTED);
									peerChannel.register(peerSelector,SelectionKey.OP_READ, connection);
									connection.installOPS(InterestedOPS.OP_READ);
									peerConnected(connection.getIPAddress(),connection.getUsePort());
								} catch (IOException e) {
									e.printStackTrace();
									peerConnectingFailed(connection.getIPAddress(),connection.getUsePort(), e);
								}
								continue;
							}
							continue;
						}
						
						
						if (key.isValid())
							if (key.isReadable()) {
								if (download_controller.getThrottlingRate() != 0) {
									if (download_controller.getAvailableByteCount(false) == 0) {
										try {
											key.interestOps(DISABLE_READ & key.interestOps());
											key.attach(connection);
											connection.removeOPS(InterestedOPS.OP_READ);
											must_install_read.add(key);
										} catch (Throwable t) {
											t.printStackTrace();
											connection.setStatus(ConnectionStatus.DISCONNECTED);
											peerIOError(connection.getIPAddress(), connection.getUsePort(),t);
										}
									} else
										read(connection);
								} else
									read(connection);
							}
						if (key.isValid())
							if (key.isWritable()) {
								if (upload_controller.getThrottlingRate() != 0) {
									if (upload_controller.getAvailableByteCount(false) == 0) {
										try {
											key.interestOps(DISABLE_WRITE & key.interestOps());
											key.attach(connection);
											connection.removeOPS(InterestedOPS.OP_WRITE);
											//System.out.println("Must install write :: " + System.currentTimeMillis() + " :: " + key.attachment());
											must_install_write.add(key);
										} catch (Throwable t) {
											t.printStackTrace();
											connection.setStatus(ConnectionStatus.DISCONNECTED);
											peerIOError(connection.getIPAddress(), connection.getUsePort(),t);
										}
									} else
										write(connection);
								} else
									write(connection);
							}
						long b = System.currentTimeMillis();
						//System.out.println("NetworkManager TCP iteration :: " + (b - a) + "  " + key.attachment());
					}catch(Throwable cause) {
						cause.printStackTrace();
						if (!loop)
							break;
					}
				}
				long y = System.currentTimeMillis();
				//System.out.println("NetworkManager TCP fullIteration :: " + (y - x));
			}

			try {
				peerSelector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private class PeerPacketProcessor extends JMThread {
		private Queue<PacketFragment> packetsToProcess = new ConcurrentLinkedQueue<PacketFragment>();
		private boolean loop = true;
		private boolean isSleeping = false;
		public PeerPacketProcessor() {
			super("Peer packet processor ");
		}
		
		public void addPacket(PacketFragment container) {
			
			JMPeerConnection peer_connection = (JMPeerConnection) container.getConnection();
			String key = peer_connection.getIPAddress() + ":" + peer_connection.getUsePort();
			PacketFragment beginPacket = fragmentMap.get(key);
			fragmentMap.remove(key);
			
			if (beginPacket!=null) {
				try {
					beginPacket.concat(container);
					container = beginPacket;
					
				}catch(Throwable t) {
					t.printStackTrace();
					
				}
				
			}
			boolean stopLoop = false;
			do {
										
				stopLoop = false;
				byte first = container.getHead();
				if (first != ED2KConstants.PROTO_EMULE_COMPRESSED_TCP)
					if (first != ED2KConstants.PROTO_EDONKEY_TCP)
						if (first != ED2KConstants.PROTO_EMULE_EXTENDED_TCP) {
							stopLoop = true;
						}
				int packetLength = container.getLength();
				
				if (packetLength < 0)
					stopLoop = true;
				
				if (packetLength > ED2KConstants.MAXPACKETSIZE)
					stopLoop = true;
				
				if (stopLoop) {
					container.moveUnusedBytes(1);
				}
				if (container.getPacketLimit() < 5) {
					break;
				}
			}while(stopLoop);
				int count = 0;
				try {
					count = getPacketCount(container);
				}catch(Throwable t) {
					t.printStackTrace();
				}
				if (count != 0)
				for(int packetID = 0;packetID<count;packetID++) {
					try {
						processClientPackets(container);
					} catch (UnknownPacketException e) {
						e.printStackTrace();
						break;
					} catch (MalformattedPacketException e) {
						e.printStackTrace();
					} catch (DataFormatException e) {
						e.printStackTrace();
					}
				}
				
				if (!container.isFullUsed()) {
					container.getContent().position(0);
					fragmentMap.put(key, container);
				} else {
					container.clear();
				}
			
			
			/*packetsToProcess.offer(container);
			if (isSleeping())
				wakeUp();*/
		}
		
		private JMTimerTask readQueueCleaner = null;		
		public void startProcessor() {
			loop = true;
			
			readQueueCleaner = new JMTimerTask() {
				@Override
				public void run() {
					boolean needGC = false;
					for(String key : fragmentMap.keySet()) {
						PacketFragment fragment = fragmentMap.get(key);
						if (System.currentTimeMillis() - fragment.getLastUpdate() >= PACKET_PROCESSOR_DROP_TIMEOUT) {
							//System.out.println("Drop partial packet for : " + fragment.getConnection());
							fragmentMap.remove(key);
							fragment.clear();
							needGC = true;
						}
					}
					
					for(PacketFragment fragment : packetsToProcess)
						if (System.currentTimeMillis() - fragment.getLastUpdate() >= PACKET_PROCESSOR_DROP_TIMEOUT) {
							//System.out.println("Drop completed packet for : " + fragment.getConnection());
							packetsToProcess.remove(fragment);
							fragment.clear();
							needGC = true;
					}
					if (needGC)
						System.gc();
				}
			};
			
			connections_maintenance.addTask(readQueueCleaner, PACKET_PROCESSOR_QUEUE_SCAN_INTERVAL, true);
			start();
		}
		
		public void JMStop() {
			loop = false;
			connections_maintenance.removeTask(readQueueCleaner);
			if (isSleeping())
				wakeUp();
		}
		
		public boolean isSleeping() {return isSleeping; }
		
		public void wakeUp() {
			synchronized(this) {
				this.notify();
			}
		}
		
		Map<String, PacketFragment> fragmentMap = new ConcurrentHashMap<String, PacketFragment>();
		
		public String toString() {
			String result = "";
			
			result += " packetsToProcess :: " + packetsToProcess.size();
			
			/*result += " packetsToProcess : \n";
			for(PacketFragment f : packetsToProcess) {
				result += f.getConnection() + " : " + f.getContent().capacity() + "   " + f.getPacketLimit() + " " + Convert.byteToHexString(f.getContent().array(), 0, 10) + "   getLastUpdate : " + f.getLastUpdate() +" \n";
			}*/
			
			/*result +="\nFragment map : \n";
			
			for(String k : fragmentMap.keySet()) {
				String s = " NULL ";
				PacketFragment pf = fragmentMap.get(k);
				String cont = "";
				if (pf != null) {
					ByteBuffer b = pf.getContent();
					
					if (b != null)
						s = b.capacity()+"";
					
					cont = Convert.byteToHexString(b.array(), 0, 6);
				}
				result += k + " : " + s + " lastUpdate : " + pf.getLastUpdate()+  " len : " + pf.getLength() + " content :  " + cont + " \n";
			}*/
			
			return result;
		}
		
		public void run() {
			while(loop) {
				isSleeping = false;
				if (packetsToProcess.isEmpty()) {
					isSleeping = true;
					synchronized(this) {
						try {
							this.wait();
						} catch (InterruptedException e) { }
					}
					continue;
				}
					PacketFragment container = packetsToProcess.poll();
					JMPeerConnection peer_connection = (JMPeerConnection) container.getConnection();
					String key = peer_connection.getIPAddress() + ":" + peer_connection.getUsePort();
					PacketFragment beginPacket = fragmentMap.get(key);
					fragmentMap.remove(key);
					
					if (beginPacket!=null) {
						try {
							beginPacket.concat(container);
							container = beginPacket;
							
						}catch(Throwable t) {
							t.printStackTrace();
							
						}
						
					}
					boolean stopLoop = false;
					do {
												
						stopLoop = false;
						byte first = container.getHead();
						if (first != ED2KConstants.PROTO_EMULE_COMPRESSED_TCP)
							if (first != ED2KConstants.PROTO_EDONKEY_TCP)
								if (first != ED2KConstants.PROTO_EMULE_EXTENDED_TCP) {
									stopLoop = true;
								}
						int packetLength = container.getLength();
						
						if (packetLength < 0)
							stopLoop = true;
						
						if (packetLength > ED2KConstants.MAXPACKETSIZE)
							stopLoop = true;
						
						if (stopLoop) {
							container.moveUnusedBytes(1);
						}
						if (container.getPacketLimit() < 5) {
							break;
						}
					}while(stopLoop);
						int count = 0;
						try {
							count = getPacketCount(container);
						}catch(Throwable t) {
							t.printStackTrace();
						}
						if (count != 0)
						for(int packetID = 0;packetID<count;packetID++) {
							try {
								processClientPackets(container);
							} catch (UnknownPacketException e) {
								e.printStackTrace();
								break;
							} catch (MalformattedPacketException e) {
								e.printStackTrace();
							} catch (DataFormatException e) {
								e.printStackTrace();
							}
						}
						
						if (!container.isFullUsed()) {
							container.getContent().position(0);
							fragmentMap.put(key, container);
						} else {
							container.clear();
						}

			}
		}
	}
	
	private class ServerConnectionMonitor extends JMThread {
		private Selector serverSelector;
		private volatile boolean loop = true;
		JMServerConnection serverConnection;
		
		private Queue<Packet> send_queue = new ConcurrentLinkedQueue<Packet>();
		private volatile boolean sendSequence = false;
		
		public ServerConnectionMonitor() {
			super("Server connection monitor");
		}
		
		public JMServerConnection getServerConnection() {
			return serverConnection;
		}
		
		public void sendPacket(Packet packet) {
			if (!loop) return ; //don't send packets when stopped
			if (!ED2KConstants.SERVER_PACKETS_NOT_ALLOWED_TO_COMPRESS.contains(packet.getCommand()) 
					&& (packet.getLength() >= ED2KConstants.PACKET_SIZE_TO_COMPRESS)) {
				byte op_code = packet.getCommand(); 
				ByteBuffer raw_data = Misc.getByteBuffer(packet.getLength()-1-4-1);
				ByteBuffer data = packet.getAsByteBuffer();
				data.position(1 + 4 + 1);
				raw_data.put(data);
				raw_data.position(0);
				ByteBuffer compressedData = JMuleZLib.compressData(raw_data);
				if (compressedData.capacity()<raw_data.capacity()) {
					raw_data.clear();
					raw_data.rewind();
					raw_data = null;
					packet = new Packet(compressedData.capacity(),ED2KConstants.PROTO_EMULE_COMPRESSED_TCP);
					packet.setCommand(op_code);
					compressedData.position(0);
					packet.insertData(compressedData);
				} else {
					compressedData.clear();
					compressedData.rewind();
					compressedData = null;
					
					raw_data.clear();
					raw_data.rewind();
					raw_data = null;
				}
			}
			packet.getAsByteBuffer().position(0);
			send_queue.offer(packet);
			if (!sendSequence) {
				sendSequence = true;
				serverSelector.wakeup();
			}
				
		}
		
		public void startMonitor(JMServerConnection connection) {
			try {
				serverSelector = Selector.open();
				this.serverConnection = connection;
				loop = true;
				connection.setStatus(ConnectionStatus.CONNECTING);
				JMuleSocketChannel serverChannel = new JMuleSocketChannel(SocketChannel.open());
				connection.setJMConnection(serverChannel);
				
				serverChannel.getChannel().configureBlocking(false);
				serverChannel.getChannel().register(serverSelector, SelectionKey.OP_CONNECT);
				serverChannel.getChannel().connect(connection.getRemoteInetSocketAddress());
				start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		public void JMStop() {
			loop = false;
			serverSelector.wakeup();
			try {
				serverSelector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			while (loop) {
				int selectCount = 0;
				
				if (sendSequence) {
					try {
						serverConnection.getJMChannel().getChannel().register(serverSelector, SelectionKey.OP_WRITE);
					} catch (ClosedChannelException e) {
						e.printStackTrace();
					}
				}
				
				try {
					selectCount = serverSelector.select(SERVER_CONNECTION_MONITOR_SELECT);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				if (sendSequence) {
					try {
						serverConnection.getJMChannel().getChannel().register(serverSelector, SelectionKey.OP_WRITE);
					} catch (ClosedChannelException e) {
						e.printStackTrace();
					}
				}

				if (selectCount == 0)
					continue;
				Iterator<SelectionKey> keys = serverSelector.selectedKeys().iterator();
				while (keys.hasNext()) {
					SelectionKey key = keys.next();
					keys.remove();
					JMuleSocketChannel channel = serverConnection.getJMChannel();

					if (key.isConnectable()) {
						if (serverConnection.getJMChannel().getChannel().isConnectionPending()) {
							try {
								serverConnection.getJMChannel().getChannel().finishConnect();
								serverConnection.setStatus(ConnectionStatus.CONNECTED);
								serverConnection.getJMChannel().getChannel().register(serverSelector,SelectionKey.OP_READ);
								serverConnected();
								continue;
							} catch (IOException e) {
								e.printStackTrace();
								serverDisconnected();
								continue;
							}
						}
					} else

					if (key.isReadable()) {

						try {

							PacketFragment container;
							container = new PacketFragment(serverConnection,ConfigurationManager.NETWORK_READ_BUFFER);

							int readedBytes = channel.read(container
									.getContent());
							if (readedBytes != 0) {
								container.setPacketLimit(readedBytes);
								receiveServerPacket(container);
							}
						} catch (JMEndOfStreamException e) {
							e.printStackTrace();
							serverDisconnected();
							continue;
						} catch (IOException e) {
							e.printStackTrace();
						}

					} else

					if (key.isWritable()) {

						Packet packet = send_queue.peek();
						if (packet == null) {
							sendSequence = false;
							try {
								serverConnection.getJMChannel().getChannel().register(serverSelector,SelectionKey.OP_READ);
							} catch (ClosedChannelException e) {
								e.printStackTrace();
							}
							continue;
						}

						if (!packet.getAsByteBuffer().hasRemaining()) {
							send_queue.poll();
							packet = send_queue.peek();
							if (packet == null) {
								sendSequence = false;
								try {
									serverConnection.getJMChannel().getChannel().register(serverSelector,SelectionKey.OP_READ);
								} catch (ClosedChannelException e) {
									e.printStackTrace();
								}
								continue;
							}
							// don't check remaining, new packet
						}

						int transferredBytes;
						try {
							transferredBytes = serverConnection.getJMChannel().write(packet.getAsByteBuffer());

							if (!packet.getAsByteBuffer().hasRemaining()) {
								send_queue.poll();
							}
						} catch (JMEndOfStreamException e) {
							e.printStackTrace();
							serverDisconnected();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			try {
				serverConnection.disconnect();
			} catch (NetworkManagerException e) {
				e.printStackTrace();
			}
			serverConnection.setStatus(ConnectionStatus.DISCONNECTED);
		}
	}
	
	private class ServerPacketProcessor extends JMThread {
		private Queue<PacketFragment> packetsToProcess = new ConcurrentLinkedQueue<PacketFragment>();
		private boolean loop = true;
		private boolean isSleeping = false;
		public ServerPacketProcessor() {
			super("Server packet processor ");
		}
		
		public void addPacket(PacketFragment container) {
			packetsToProcess.offer(container);
			if (isSleeping())
				wakeUp();
		}
		
		public void startProcessor() {
			loop = true;
			start();
		}
		
		public void JMStop() {
			loop = false;
			if (isSleeping())
				wakeUp();
		}
		
		public boolean isSleeping() {return isSleeping; }
		
		public void wakeUp() {
			synchronized(this) {
				this.notify();
			}
		}
		
		private PacketFragment beginFragment = null;
		public void run() {
			while(loop) {
				isSleeping = false;
				if (packetsToProcess.isEmpty()) {
					isSleeping = true;
					synchronized(this) {
						try {
							this.wait();
						} catch (InterruptedException e) { }
					}
					continue;
				}
				PacketFragment container = packetsToProcess.poll();
				if (beginFragment!=null) {
					beginFragment.concat(container);
					container = beginFragment;
					beginFragment = null;
				}
				
				boolean stopLoop = false;
				do {
					stopLoop = false;
					byte first = container.getHead();
					if (first != ED2KConstants.PROTO_EMULE_COMPRESSED_TCP)
						if (first != ED2KConstants.PROTO_EDONKEY_TCP)
							if (first != ED2KConstants.PROTO_EMULE_EXTENDED_TCP) {
								stopLoop = true;
								container.moveUnusedBytes(1);
							}
				}while(stopLoop);
				
				try {
					int count =  getPacketCount(container);
					
					for(int packetID = 0;packetID<count;packetID++) {
						processServerPackets(container);
					}
					if (!container.isFullUsed()) {
						beginFragment = container;
					} else {
						container.clear();
						container = null;
					}
				} catch (UnknownPacketException e) {
					e.printStackTrace();
				} catch (MalformattedPacketException e) {
					e.printStackTrace();
				} catch (DataFormatException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private enum UDPConnectionStatus { OPEN, CLOSED};
	private static ByteBuffer udp_packet_buffer = Misc.getByteBuffer(ConfigurationManager.MAX_UDP_PACKET_SIZE);
	private class JMUDPConnection extends JMThread {
				
		private DatagramChannel listenChannel = null;
		private UDPConnectionStatus connectionStatus = UDPConnectionStatus.CLOSED;
		private Queue<UDPPacket> sendQueue = new ConcurrentLinkedQueue<UDPPacket>();
		private Selector selector = null;
		
		JMUDPConnection(){
			super("UDP Connection");
		}
		
		public String toString() {
			String result = "UDP send queue size : " + sendQueue.size();
			result += "\nSend addresses : \n";
			for(UDPPacket packet : sendQueue)
				result += packet.getAddress() + "\n";
			result +="\n";
			return result;
		}
		
		public void startConnection() {
			must_sleep = true;
						
			start();
		}
		
		
		public void open() {
			try {
				int listenPort = ConfigurationManagerSingleton.getInstance().getUDP();
				listenChannel = DatagramChannel.open();
				listenChannel.socket().bind(new InetSocketAddress(listenPort));
				listenChannel.configureBlocking(false);
				
				selector = Selector.open();
				listenChannel.register(selector, SelectionKey.OP_READ);
				
				connectionStatus = UDPConnectionStatus.OPEN;
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		private volatile boolean loop = true;
		private volatile boolean must_sleep = false;
		private volatile boolean is_selecting = false;
		private volatile boolean must_install_write = false;
		private volatile boolean write_installed = false;
		
		public void run() {
			while(loop) {
				if (must_sleep) {
					synchronized(this) {
						try {
							this.wait();
						} catch (InterruptedException e) { }
					}
					continue;
				}
				
				int select_count = 0;
				
				try {
					select_count = selector.selectNow();
					if (select_count == 0) {
						is_selecting = true;
						select_count = selector.select(UDP_CONNECTION_MONITOR_SELECT);
						is_selecting = false;
					}
					
					if (!loop)
						return;
					
					if (must_install_write) {
						listenChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
						must_install_write = false;
						write_installed = true;
					}
					
					Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
					long x = System.currentTimeMillis();
					while(keys.hasNext()) {
						long a = System.currentTimeMillis();
						if (!loop)
							return;
						
						SelectionKey key = keys.next();
						keys.remove();
						
						if (key.isReadable())
							if (key.isValid()) {
								try {
									udp_packet_buffer.clear();
									InetSocketAddress packetSender = (InetSocketAddress) listenChannel.receive(udp_packet_buffer);
									
									ByteBuffer packet_content = getByteBuffer(udp_packet_buffer.position());

									udp_packet_buffer.limit(udp_packet_buffer.position());
									udp_packet_buffer.position(0);
									packet_content.position(0);
									packet_content.put(udp_packet_buffer);

									packet_content.position(0);
									
									UDPPacket packet = new UDPPacket(packet_content, packetSender);
									processUDPPacket(packet);
									packet.clear();
								} catch (Throwable e) {
									e.printStackTrace();
								}
							}
						if (key.isWritable())
							if (key.isValid()) {
								UDPPacket packet = sendQueue.poll();
								if (packet==null) {
									listenChannel.register(selector, SelectionKey.OP_READ);
									write_installed = false;
									continue;
								}
								InetSocketAddress destination = packet.getAddress();
								try {
									listenChannel.send(packet.getAsByteBuffer(), destination);
								} catch (IOException cause) {
									if (!isOpen()) return ;
									cause.printStackTrace();
								}
								
								packet.clear();
								
								if (sendQueue.isEmpty()) {
									listenChannel.register(selector, SelectionKey.OP_READ);
									write_installed = false;
								}
							}
						long b = System.currentTimeMillis();
						//System.out.println("NetworkManager UDP Iteration :: " + (b - a));
					}
					
					long y = System.currentTimeMillis();
					//System.out.println("NetworkManager UDP FullIteration :: " + (y - x));
										
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		
		public boolean isSleeping() {
			return must_sleep;
		}
		
		public void haltThread() {
			must_sleep = true;
			if (is_selecting)
				selector.wakeup();
			
		}
		
		public void wakeUp() {
			if (must_sleep==false) return;
			must_sleep = false;
			synchronized (this) {
				this.notify();
			}
		}
		
		public void stopThread() {
			loop = false;
			if (isSleeping())
				wakeUp();
			if (is_selecting)
				selector.wakeup();
			close();
		}
		
		public boolean isOpen() {
			return connectionStatus == UDPConnectionStatus.OPEN;
		}
		
		public void close() {		
			if (!isOpen()) return;
			try {
				connectionStatus = UDPConnectionStatus.CLOSED;
				listenChannel.close();
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void reOpenConnection() {
			haltThread();
			if (isOpen())
				close();
			open();
			wakeUp();
		}
		
		public void sendPacket(UDPPacket packet ) throws JMException {
			if (!isOpen())
				throw new JMException("UDP socket is not open");
			sendQueue.offer(packet);
						
			if (write_installed == false) {
				must_install_write = true;
				if (is_selecting)
					selector.wakeup();
			}
				
		}
		
		public void sendPacket(UDPPacket packet, IPAddress address, int port) throws JMException {
			InetSocketAddress ip_address = new InetSocketAddress(address.toString(), port);
			sendPacket(packet, ip_address);
		}
		
		public void sendPacket(UDPPacket packet, InetSocketAddress destination) throws JMException {
			packet.setAddress(destination);
			sendPacket(packet);
		}
		
		public void sendPacket(UDPPacket packet, String address, int port) throws JMException {
			InetSocketAddress ip_address = new InetSocketAddress(address, port);
			sendPacket(packet, ip_address);
		}
		
	}
}