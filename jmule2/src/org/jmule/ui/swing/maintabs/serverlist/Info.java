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
package org.jmule.ui.swing.maintabs.serverlist;

import javax.swing.JTabbedPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.servermanager.Server;
import org.jmule.core.servermanager.ServerManager;
import org.jmule.core.servermanager.ServerManagerListener;
import org.jmule.ui.swing.tables.MyInfoTable;
import org.jmule.ui.swing.tables.ServerInfoTable;

/**
 *
 * Created on Oct 1, 2008
 * @author javajox
 * @version $Revision: 1.3 $
 * Last changed by $Author: javajox $ on $Date: 2009/09/27 14:20:00 $
 */
public class Info extends JTabbedPane {
	
	private MyInfoTable my_info_table = new MyInfoTable();
	private ServerInfoTable server_info_table = new ServerInfoTable(); 
	
	private JMuleCore _core = JMuleCoreFactory.getSingleton();
	private ServerManager _server_manager = _core.getServerManager();
	
	public Info() {
		init();
		_server_manager.addServerListListener(new ServerManagerListener() {
			
			 public void connected(Server server) {
				 updateTables();
			 }
			 
			 public void disconnected(Server server) {
				 updateTables();
			 }

			public void autoConnectFailed() {
				
			}

			public void autoConnectStarted() {
			
			}

			public void isConnecting(Server server) {
			
			}

			public void serverAdded(Server server) {
	
			}

			public void serverConnectingFailed(Server server, Throwable cause) {

			}

			public void serverListCleared() {

			}

			public void serverMessage(Server server, String message) {
	
			}

			public void serverRemoved(Server server) {
	
			}
		});
	}
	
	private void updateTables() {
		 TableModel model = my_info_table.getModel();
		 ((AbstractTableModel)model).fireTableRowsUpdated(
                    0, model.getRowCount() );
		 
		 TableModel model2 = server_info_table.getModel();
		 ((AbstractTableModel)model2).fireTableRowsUpdated(
                    0, model2.getRowCount() );
	}
	
	private void init() {
		this.addTab("My info", my_info_table);
		this.addTab("Server info", server_info_table);
	}
	
}
