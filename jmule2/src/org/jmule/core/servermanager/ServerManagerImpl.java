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
package org.jmule.core.servermanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.configmanager.InternalConfigurationManager;
import org.jmule.core.downloadmanager.DownloadManagerSingleton;
import org.jmule.core.downloadmanager.InternalDownloadManager;
import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.ED2KConstants;
import org.jmule.core.edonkey.ED2KServerLink;
import org.jmule.core.edonkey.ED2KConstants.ServerFeatures;
import org.jmule.core.edonkey.metfile.ServerMet;
import org.jmule.core.edonkey.metfile.ServerMetException;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerException;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.servermanager.Server.ServerStatus;
import org.jmule.core.sharingmanager.InternalSharingManager;
import org.jmule.core.sharingmanager.SharingManagerSingleton;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.core.statistics.JMuleCoreStatsProvider;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.timer.JMTimer;
import org.jmule.core.utils.timer.JMTimerTask;

/**
 * 
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.23 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/08/31 10:20:29 $$
 */
public class ServerManagerImpl extends JMuleAbstractManager implements InternalServerManager  {
		
	private Status status = Status.DISCONNECTED;
	
	private Collection<Server> server_list = new ConcurrentLinkedQueue<Server>();
	private Server connected_server = null;

	private InternalNetworkManager _network_manager;
	private InternalSharingManager _sharing_manager;
	private InternalConfigurationManager _config_manager;

	private ServerMet server_met;

	private List<ServerManagerListener> listener_list = new LinkedList<ServerManagerListener>();

	private boolean auto_connect_started = false;
	private List<Server> candidate_servers = new ArrayList<Server>();

	private InternalDownloadManager _download_manager;
	
	private boolean reconnect_to_server = false;
	private int reconnect_count = 0;
	
	private Comparator<Server> compare_servers_by_ping = new Comparator<Server>() {
		public int compare(Server object1, Server object2) {
			if (object1.getPing() < object2.getPing()) {
				return -1;
			} else if (object1.getPing() > object2.getPing()) {
				return +1;
			}
			return 0;
		}
	};
	
	private JMTimer server_manager_timer;
	private JMTimerTask servers_udp_query;
	private JMTimerTask store_metadata_task;
	private JMTimerTask reconnect_task;

	ServerManagerImpl() {
	}
	
	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}

		_network_manager = (InternalNetworkManager) NetworkManagerSingleton
				.getInstance();
		_sharing_manager = (InternalSharingManager) SharingManagerSingleton
				.getInstance();
		try {
			server_met = new ServerMet(ConfigurationManager.SERVER_MET);
		} catch (ServerMetException e) {
		}
		
		Set<String> types = new HashSet<String>();
		types.add(JMuleCoreStats.ST_NET_SERVERS_COUNT);
		types.add(JMuleCoreStats.ST_NET_SERVERS_DEAD_COUNT);
		types.add(JMuleCoreStats.ST_NET_SERVERS_ALIVE_COUNT);
		
		JMuleCoreStats.registerProvider(types, new JMuleCoreStatsProvider() {
			public void updateStats(Set<String> types,Map<String, Object> values) {
				if (types.contains(JMuleCoreStats.ST_NET_SERVERS_COUNT)) {
					values.put(JMuleCoreStats.ST_NET_SERVERS_COUNT, getServersCount());
				}
				if (types.contains(JMuleCoreStats.ST_NET_SERVERS_DEAD_COUNT)) {
					int dead_servers_count = 0;
					for(Server server : server_list)
						if (server.isDown())  dead_servers_count++;
					values.put(JMuleCoreStats.ST_NET_SERVERS_DEAD_COUNT, dead_servers_count);
				}
				if (types.contains(JMuleCoreStats.ST_NET_SERVERS_ALIVE_COUNT)) {
					int alive_servers_count = 0;
					for(Server server : server_list)
						if (!server.isDown())  alive_servers_count++;
					values.put(JMuleCoreStats.ST_NET_SERVERS_ALIVE_COUNT, alive_servers_count);
				}
				
			}
		});

		server_manager_timer = new JMTimer();
		servers_udp_query = new JMTimerTask() {
			Random random = new Random();
			public void run() {
				try {
					if (!_config_manager.isUDPEnabled())
						return;
				} catch (ConfigurationManagerException e) {
					e.printStackTrace();
					return;
				}
				
				for(Server server : server_list) {
					int challenge = (int)((random.nextInt() << 16) + ED2KConstants.INVALID_SERVER_DESC_LENGTH);
					byte[] byte_challenge = Convert.intToByteArray(challenge);
					
					challenge = Convert.byteToInt(byte_challenge);
					server.setChallenge(challenge);
					_network_manager.sendServerUDPStatusRequest(server.getAddress(), server.getUDPPort(), challenge);
					_network_manager.sendServerUDPDescRequest(server.getAddress(), server.getUDPPort());
				}
			}
		};
		
		store_metadata_task = new JMTimerTask() {
			public void run() {
				try {
					storeServerList();
				} catch (ServerManagerException e) {
					e.printStackTrace();
				}
			}
		}; 
		
		_download_manager = (InternalDownloadManager) DownloadManagerSingleton.getInstance();
		_config_manager = (InternalConfigurationManager) ConfigurationManagerSingleton.getInstance();
	}

	public void start() {
		try {
			super.start();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		server_manager_timer.addTask(servers_udp_query,ConfigurationManager.SERVER_UDP_QUERY_INTERVAL, true);
		server_manager_timer.addTask(store_metadata_task, ConfigurationManager.SERVER_LIST_STORE_INTERVAL, true);
	}
	
	public void shutdown() {
		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		server_manager_timer.cancelAllTasks();
	}

	public Status getStatus() {
		return status;
	}
	
	public void addServerListListener(ServerManagerListener serverListListener) {
		listener_list.add(serverListListener);
	}

	public void clearServerList() {
		server_list.clear();
		notifyServerListCleared();
	}

	public void connect() throws ServerManagerException {
		auto_connect_started = true;
		reconnect_to_server = false;
		candidate_servers.addAll(server_list);
		requestNextAutoConnectServer();
		status = Status.CONNECTING;
		notifyAutoConnectStarted();
	}

	public void connect(Server server) throws ServerManagerException {
		if (connected_server != null)
			throw new ServerManagerException(
					"JMule is already connected (connecting) to another server");
		if (reconnect_task != null)
			server_manager_timer.removeTask(reconnect_task);
		String ip = server.getAddress();
		int port = server.getPort();
		if (hasServer(ip, port)) {
			connected_server = server;
			connected_server.setStatus(ServerStatus.CONNECTING);
			status = Status.CONNECTING;
			try {
				_network_manager.connectToServer(ip, port);
			} catch (NetworkManagerException e) {
				throw new ServerManagerException(e);
			}
			notifyIsConnecting(connected_server);
		}

	}

	public void disconnect() throws ServerManagerException {
		if (connected_server == null)
			throw new ServerManagerException("JMule is not connected to server");
		if (reconnect_task != null)
			server_manager_timer.removeTask(reconnect_task);
		reconnect_to_server = false;
		reconnect_count = 0;
		try {
			_network_manager.disconnectFromServer();
		} catch (NetworkManagerException e) {
			throw new ServerManagerException(e);
		}
		
		connected_server.setStatus(ServerStatus.DISCONNECTED);
		status = Status.DISCONNECTED;
		notifyDisconnected(connected_server);
		connected_server = null;
	}

	public Server getConnectedServer() {
		return connected_server;
	}

	public Server getServer(String ip, int port) {
		for (Server server : server_list)
			if (server.getAddress().equals(ip))
				if (server.getPort() == port)
					return server;
		return null;
	}
	
	private Server getServer(String ip) {
		for (Server server : server_list)
			if (server.getAddress().equals(ip))
					return server;
		return null;
	}

	public List<Server> getServers() {
		List<Server> result = new LinkedList<Server>();
		result.addAll(server_list);
		return result;
	}

	public int getServersCount() {
		return server_list.size();
	}

	public boolean hasServer(String address, int port) {
		for (Server server : server_list)
			if (server.getAddress().equals(address))
				if (server.getPort() == port)
					return true;
		return false;
	}
	
	public boolean hasServer(Server server) {
		if (server_list.contains(server))
			return true;
		else
			return false;
	}

	protected boolean iAmStoppable() {
		return false;
	}

	public void importList(String fileName) throws ServerManagerException {
		ServerMet server_met;
		try {
			server_met = new ServerMet(fileName);
			server_met.load();
		} catch (ServerMetException cause) {
			throw new ServerManagerException(cause);
		}

		List<String> ip_list = server_met.getIPList();
		List<Integer> port_list = server_met.getPortList();
		List<TagList> tag_list = server_met.getTagList();

		for (int i = 0; i < ip_list.size(); i++) {
			try {
				Server server = newServer(ip_list.get(i), port_list.get(i));
				server.setTagList(tag_list.get(i));
			} catch (ServerManagerException cause) {
				cause.printStackTrace();
			}
		}
	}
	
	public boolean isConnected() {
		return connected_server != null;
	}

	public void loadServerList() throws ServerManagerException {
		try {
			server_met.load();
			List<String> ip_list = server_met.getIPList();
			List<Integer> port_list = server_met.getPortList();
			List<TagList> tag_list = server_met.getTagList();

			for (int i = 0; i < ip_list.size(); i++) {
				try {
					Server server = newServer(ip_list.get(i), port_list.get(i));
					server.setTagList(tag_list.get(i));
				} catch (ServerManagerException cause) {
					cause.printStackTrace();
				}
			}
		} catch (Throwable cause) {
			throw new ServerManagerException(cause);
		}
	}

	public Server newServer(ED2KServerLink serverLink)
			throws ServerManagerException {
		String serverIP = serverLink.getServerAddress();
		int serverPort = serverLink.getServerPort();
		if (hasServer(serverIP, serverPort))
			throw new ServerManagerException("Server " + serverIP + " : "
					+ serverPort + " already exists");
		Server server = new Server(serverLink);
		server_list.add(server);
		notifyServerAdded(server);
		return server;
	}

	public Server newServer(String serverIP, int serverPort)
			throws ServerManagerException {
		if (hasServer(serverIP, serverPort))
			throw new ServerManagerException("Server " + serverIP + " : "
					+ serverPort + " already exists");
		Server server = new Server(serverIP, serverPort);
		server_list.add(server);
		notifyServerAdded(server);
		return server;
	}

	private void notifyAutoConnectStarted() {
		for (ServerManagerListener listener : listener_list)
			try {
				listener.autoConnectStarted();
			} catch (Throwable cause) {
				cause.printStackTrace();
			}
	}

	private void notifyAutoConnectFailed() {
		for (ServerManagerListener listener : listener_list)
			try {
				listener.autoConnectFailed();
			} catch (Throwable cause) {
				cause.printStackTrace();
			}
	}

	private void notifyConnected(Server server) {
		for (ServerManagerListener listener : listener_list)
			try {
				listener.connected(server);
			} catch (Throwable cause) {
				cause.printStackTrace();
			}
	}

	private void notifyDisconnected(Server server) {
		for (ServerManagerListener listener : listener_list)
			try {
				listener.disconnected(server);
			} catch (Throwable cause) {
				cause.printStackTrace();
			}
	}

	private void notifyIsConnecting(Server server) {
		for (ServerManagerListener listener : listener_list)
			try {
				listener.isConnecting(server);
			} catch (Throwable cause) {
				cause.printStackTrace();
			}
	}

	private void notifyMessage(Server server, String message) {
		for (ServerManagerListener listener : listener_list)
			try {
				listener.serverMessage(server, message);
			} catch (Throwable cause) {
				cause.printStackTrace();
			}
	}

	private void notifyServerAdded(Server server) {
		for (ServerManagerListener listener : listener_list)
			try {
				listener.serverAdded(server);
			} catch (Throwable cause) {
				cause.printStackTrace();
			}
	}

	private void notifyServerConnectingFailed(Server server, Throwable cause) {
		for (ServerManagerListener listener : listener_list)
			try {
				listener.serverConnectingFailed(server, cause);
			} catch (Throwable cause1) {
				cause1.printStackTrace();
			}
	}

	private void notifyServerListCleared() {
		for (ServerManagerListener listener : listener_list)
			try {
				listener.serverListCleared();
			} catch (Throwable cause) {
				cause.printStackTrace();
			}
	}

	private void notifyServerRemoved(Server server) {
		for (ServerManagerListener listener : listener_list)
			try {
				listener.serverRemoved(server);
			} catch (Throwable cause) {
				cause.printStackTrace();
			}
	}

	public void receivedIDChange(ClientID clientID,
			Set<ServerFeatures> serverFeatures) {
		if (connected_server == null) {
			// log, must newer get here
			return;
		}
		reconnect_to_server = true;
		reconnect_count = 0;
		
		connected_server.setClientID(clientID);
		connected_server.setFeatures(serverFeatures);

		connected_server.setStatus(ServerStatus.CONNECTED);

		if (auto_connect_started == true) {
			auto_connect_started = false;
			candidate_servers.clear();
		}

		_sharing_manager.startSharingFilesToServer();
		_download_manager.connectedToServer(connected_server);
		
		status = Status.CONNECTED;
		
		notifyConnected(connected_server);

	}

	public void receivedMessage(String message) {
		notifyMessage(connected_server, message);
		for(String error_message : ED2KConstants.SERVER_ERROR_MESSAGES) {
			if (message.contains(error_message))
				try {
					disconnect();
				} catch (ServerManagerException e) {
					e.printStackTrace();
				}
		}
	}

	public void receivedNewServerDescription(String ip, int port,
			int challenge, TagList tagList) {		
		Server server = getServer(ip, port);
		if (server == null)
			return;
		server.setPing(challenge);
		
		server.getTagList().addTag(tagList, true);
	}

	public void receivedServerDescription(String ip, int port, String name,
			String description) {
		Server server = getServer(ip, port);
		if (server == null)
			return;
		server.setName(name);
		server.setDesc(description);
	}

	public void receivedServerList(List<String> ipList, List<Integer> portList) {
		for (int i = 0; i < ipList.size(); i++) {
			try {
				newServer(ipList.get(i), portList.get(i));
			} catch (ServerManagerException e) {
				e.printStackTrace();
			}
		}
	}

	public void receivedServerStatus(int userCount, int fileCount) {
		if (connected_server == null)
			return;
		connected_server.setNumUsers(userCount);
		connected_server.setNumFiles(fileCount);
	}

	public void receivedOldServerStatus(String ip, int port, int challenge, long userCount, long fileCount) {
		Server server = getServer(ip);
		if (server == null) {
			return;
		}
		server.setPing(challenge);
		server.setNumUsers(userCount);
		server.setNumFiles(fileCount);
	}
	
	public void receivedServerStatus(String ip, int port, int challenge,
			long userCount, long fileCount, long softLimit, long hardLimit,
			Set<ServerFeatures> serverFeatures) {
		Server server = getServer(ip);
		if (server == null) {
			return;
		}
		server.setPing(challenge);
		server.setNumUsers(userCount);
		server.setNumFiles(fileCount);
		server.setSoftLimit(Convert.longToInt(softLimit));
		server.setHardLimit(Convert.longToInt(hardLimit));
		server.setFeatures(serverFeatures);
	}

	public void removeServer(Server... servers) throws ServerManagerException {
		for (Server server : servers) {
			if (!hasServer(server.getAddress(), server.getPort()))
				throw new ServerManagerException("Server " + server.getAddress() + " : " + server.getPort() + " not found");
			if (connected_server != null)
				if (connected_server.equals(server))
					disconnect();
			server_list.remove(server);
			notifyServerRemoved(server);
		}
		storeServerList();
	}
	
	public void removeServer(List<Server> servers) throws ServerManagerException {
		for (Server server : servers) {
			if (!hasServer(server.getAddress(), server.getPort()))
				throw new ServerManagerException("Server " + server.getAddress() + " : " + server.getPort() + " not found");
			if (connected_server != null)
				if (connected_server.equals(server))
					disconnect();
			server_list.remove(server);
			notifyServerRemoved(server);
		}
		storeServerList();
	}

	public void removeServerListListener(
			ServerManagerListener serverListListener) {
		listener_list.remove(serverListListener);
	}

	public void serverConnectingFailed(String ip, int port, Throwable cause) {
		final Server failed_server = connected_server;
		connected_server = null;
		notifyServerConnectingFailed(failed_server, cause);
		if (auto_connect_started) {
			try {
				requestNextAutoConnectServer();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		} else
		
		if (reconnect_to_server)
			if (reconnect_count < ConfigurationManager.SERVER_RECONNECT_COUNT) {
			reconnect_task = new JMTimerTask() {
				public void run() {
					if (!reconnect_to_server) return;
					reconnect_count++;
					Server cserver = failed_server;
					reconnect_task = null;
					try {
						connect(cserver);
					} catch (ServerManagerException e) {
						e.printStackTrace();
					}
				}
			};
			server_manager_timer.addTask(reconnect_task, ConfigurationManager.SERVER_RECONNECT_TIMEOUT);	
				
			} else {
				_download_manager.disconnectedFromServer(connected_server);
				_sharing_manager.stopSharingFilesToServer();
			}
	}

	public void serverDisconnected(String ip, int port) {
		if (connected_server == null)
			return;
		if (connected_server.getStatus() == ServerStatus.DISCONNECTED)
			return;
		
		if (connected_server.getStatus()==ServerStatus.CONNECTED) {
			_download_manager.disconnectedFromServer(connected_server);
			_sharing_manager.stopSharingFilesToServer();
		}
		
		connected_server.setStatus(ServerStatus.DISCONNECTED);
		status = Status.DISCONNECTED;
		notifyDisconnected(connected_server);

		if (auto_connect_started) {
			try {
				requestNextAutoConnectServer();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		} else {
			/*if (reconnect_to_server)
				if (reconnect_count < ConfigurationManager.SERVER_RECONNECT_COUNT) {
					reconnect_count++;
					Server cserver = connected_server;
					connected_server = null;
					try {
						connect(cserver);
					} catch (ServerManagerException e) {
						e.printStackTrace();
					}
					return;
				}*/
			
		}

		connected_server = null;
	}

	public void storeServerList() throws ServerManagerException {
		List<String> ip_list = new LinkedList<String>();
		List<Integer> port_list = new LinkedList<Integer>();
		List<TagList> tag_list = new LinkedList<TagList>();

		for (Server server : server_list) {
			ip_list.add(server.getAddress());
			port_list.add(server.getPort());
			tag_list.add(server.getTagList());
		}
		server_met.setIPList(ip_list);
		server_met.setPortList(port_list);
		server_met.setTagList(tag_list);
		try {
			server_met.store();
		} catch (ServerMetException cause) {
			throw new ServerManagerException(cause);
		}
	}

	private void requestNextAutoConnectServer() throws ServerManagerException {
		if (candidate_servers.size() == 0) {
			auto_connect_started = false;
			notifyAutoConnectFailed();
			return;
		}
		Server server = (Server) Collections.min(candidate_servers,
				compare_servers_by_ping);
		candidate_servers.remove(server);
		connect(server);
	}
}