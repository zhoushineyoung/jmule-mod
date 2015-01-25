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
package org.jmule.ui.swt.tab.main.serverlist;

import java.util.List;

import org.jmule.core.JMRunnable;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.servermanager.Server;
import org.jmule.core.servermanager.ServerManager;
import org.jmule.core.servermanager.ServerManagerException;
import org.jmule.core.servermanager.ServerManagerListener;
import org.jmule.core.servermanager.ServerManager.Status;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.common.ConnectButton;
import org.jmule.ui.swt.mainwindow.MainWindow;
import org.jmule.ui.swt.mainwindow.StatusBar;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2011/03/27 16:51:29 $$
 */
public class SWTServerListWrapper {
	
	private static SWTServerListWrapper instance = null;
	private ServerManager server_manager = null;
	
	private ConnectionInfo connection_info;
	private ServerList server_list;
	private ServerMessages server_messages;
	private StatusBar status_bar;
	private ConnectButton connect_button;
	private ServerListTab server_list_tab;
	
	private boolean is_autoconnect = false;
	
	private boolean single_connect = false;
	
	public static SWTServerListWrapper getInstance() {
		if (instance == null)
			instance = new SWTServerListWrapper(JMuleCoreFactory.getSingleton().getServerManager());
		return instance;
	}
	
	private SWTServerListWrapper(ServerManager serverManager) {
		server_manager = serverManager;
		server_manager.addServerListListener(new ServerManagerListener() {

			public void connected(Server server) {	
				if (is_autoconnect) {
					is_autoconnect = false;
					setUIConnected(server);
				} else 
					if (single_connect) {
						single_connect = false;
						setUIConnected(server);
					}
			}

			public void disconnected(Server server) {
				if (!is_autoconnect) {
					setUIDisconnected(server);
				}
			}
			public void serverMessage(Server server,final String message) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						server_messages.addText(message);
					}});
			}

			public void autoConnectFailed() {
				if (is_autoconnect) {
					is_autoconnect = false;
					setUIDisconnected(null);
				}
			}

			public void autoConnectStarted() {
				if ( is_autoconnect == false ) {
					is_autoconnect = true;
					setUIConnecting();
				}
			}

			public void isConnecting(Server server) {
				setUIConnecting(server);
			}

			public void serverAdded(final Server server) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						MainWindow.getLogger().fine(Localizer._("mainwindow.logtab.message_server_added",server.getAddress()+":"+server.getPort()));
						server_list.addServer(server);
						server_list_tab.setServerCount(server_manager.getServersCount());
				}});
			}

			public void serverConnectingFailed(Server server, Throwable cause) {
				setUIDisconnected(server);
			}

			public void serverListCleared() {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						MainWindow.getLogger().fine(Localizer._("mainwindow.logtab.message_server_list_cleared"));
						server_list.clear();
						server_list_tab.setServerCount(server_manager.getServersCount());
				}});
			}

			public void serverRemoved(final Server server) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						MainWindow.getLogger().fine(Localizer._("mainwindow.logtab.message_server_removed",server.getAddress()+":"+server.getPort()));
						server_list.removeServer(server);
						server_list_tab.setServerCount(server_manager.getServersCount());
				}});
			}

			
		});
	}
	
	private void setUIConnecting() {
		if (SWTThread.getDisplay().isDisposed()) return ;
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				if (!connect_button.isDisposed())
					connect_button.setConnecting();
				if (!status_bar.isDisposed())
					status_bar.setStatusConnecting();
				if (!connection_info.isDisposed())
					connection_info.setStatusConnecting(null);
			}
		});
	}
	
	private void setUIConnecting(final Server server) {
		if (SWTThread.getDisplay().isDisposed()) return ;
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				MainWindow.getLogger().fine(_._("mainwindow.logtab.message_connecting_to",server.getAddress()+":"+server.getPort()));
				if (!connect_button.isDisposed())
					connect_button.setConnecting();
				if (!status_bar.isDisposed())
					status_bar.setStatusConnecting();
				if (!connection_info.isDisposed())
					connection_info.setStatusConnecting(server);
			}});
	}
	
	private void setUIDisconnected(final Server server) {
		if (SWTThread.getDisplay().isDisposed()) return ;
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				if (!connect_button.isDisposed())
					connect_button.setDisconnected();
				if (!status_bar.isDisposed())
					status_bar.setStatusDisconnected();
				if (!connection_info.isDisposed())
					connection_info.setStatusDisconnected();
				if (!server_list.isDisposed()) 
						if (server!=null)
							server_list.serverDisconnected(server);
				if (server != null)
					MainWindow.getLogger().warning(Localizer.getString("mainwindow.logtab.message_disconnected", server.getAddress()+":"+server.getPort()));
			}});
	}
	
	private void setUIConnected(final Server server) {
		if (SWTThread.getDisplay().isDisposed()) return ;
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				if (!connect_button.isDisposed())
					connect_button.setConnected();
				if (!status_bar.isDisposed())
					status_bar.setStatusConnected(server);
				if (!connection_info.isDisposed())
					connection_info.setStatusConnected(server);
				server_list.refresh();
				MainWindow.getLogger().fine(Localizer.getString("mainwindow.logtab.message_connected_to", server.getAddress()+":"+server.getPort()));
			}});
	}
	
	public void startAutoConnect() {
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				try {
					server_manager.connect();
				} catch (ServerManagerException e) {
					e.printStackTrace();
					setUIDisconnected(null);
				}
			}
		});
	}

	public void stopConnecting() {
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				if (is_autoconnect) {
					is_autoconnect = false;
					try {
						server_manager.disconnect();
					} catch (ServerManagerException e) {
						e.printStackTrace();
					}
				}
				if (single_connect) {
					single_connect = false;
					try {
						server_manager.disconnect();
					} catch (ServerManagerException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
	}
	
	public void disconnect() {
		try {
			server_manager.disconnect();
		} catch (ServerManagerException e) {
			e.printStackTrace();
		}
	}
	
	public void connectTo(Server server) {
		single_connect = true;
		try {
			if (server_manager.getStatus()!=Status.DISCONNECTED)
				server_manager.disconnect();
			server_manager.connect(server);
		} catch (ServerManagerException e) {
			
		}
	}
	
	public boolean isAutoconnecting() {
		return is_autoconnect;
	}
	
	public void removeServer(List<Server> servers) {
		try {
			server_manager.removeServer(servers);
		} catch (ServerManagerException e) {
			e.printStackTrace();
		}
	}
	
	public void removeServer(Server... server) {
		try {
			server_manager.removeServer(server);
		} catch (ServerManagerException e) {
			e.printStackTrace();
		}
	}
	
	public void clearServerList() {
		server_manager.clearServerList();
	}
	
	public void setServerManager(ServerManager serverManager) {
		this.server_manager = serverManager;
	}
	
	public void setServerListTab(ServerListTab tab) {
		server_list_tab = tab;
	}

	public void setConnectionInfo(ConnectionInfo connectionInfo) {
		this.connection_info = connectionInfo;
	}

	public void setServerList(ServerList serverList) {
		this.server_list = serverList;
	}

	public void setServerMessages(ServerMessages serverMessages) {
		this.server_messages = serverMessages;
	}
	
	public void setStatusBar(StatusBar statusBar) {
		this.status_bar = statusBar;
	}
	
	public void setConnectButton(ConnectButton connectButton) {
		this.connect_button = connectButton;
	}
	
}
