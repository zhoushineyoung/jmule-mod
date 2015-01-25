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
package org.jmule.ui.swing.models;

import javax.swing.table.AbstractTableModel;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.edonkey.ClientID;
import org.jmule.core.servermanager.ServerManager;
import org.jmule.core.sharingmanager.SharingManager;

/**
 *
 * Created on Oct 1, 2008
 * @author javajox
 * @version $Revision: 1.2 $
 * Last changed by $Author: javajox $ on $Date: 2009/09/22 19:08:43 $
 */
public class MyInfoTableModel extends AbstractTableModel {

	private JMuleCore _core = JMuleCoreFactory.getSingleton();
	private ServerManager _server_manager = _core.getServerManager();
	private ConfigurationManager _config_manager = _core.getConfigurationManager();
	private SharingManager _sharing_manager = _core.getSharingManager();
	
	public final static int STATUS = 0;
	public final static int NICKNAME = 1;
	public final static int IP = 2;
	public final static int ID = 3;
	public final static int SHARED_FILES = 4;
	
	private final static String row_names[] = {
		                  "Status",
		                  "Nickname",
		                  "IP",
		                  "ID",
		                  "Shared files"
	};
	
	public int getColumnCount() {
		
		return 2;
	}

	public int getRowCount() {

		return row_names.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		
		try {
			if( columnIndex == 0 ) {
				return row_names[rowIndex];
			}
			
			if( columnIndex == 1 ) {
				switch(rowIndex) {
				   case STATUS       : return _server_manager.isConnected() ? "Connected" : "Disconnected";
				   case NICKNAME     : return _config_manager.getNickName();
				   case IP           : return _server_manager.isConnected() ?
				                              _server_manager.getConnectedServer().getClientID().getAsString() : " ";
				   case ID           : boolean is_connected = _server_manager.isConnected();
				                       ClientID client_id = is_connected ? _server_manager.getConnectedServer().getClientID() : null;
					                   return  is_connected ?
						                      ( client_id.toString() + (client_id.isHighID() ? " (High)" : " (Low)" )) : " ";
				   case SHARED_FILES : return _sharing_manager.getFileCount() + " ";
				}
			}
		}catch( Throwable cause ) {
			cause.printStackTrace();
		}
		
		return null;
	}
	
	public String getColumnName(int col) {
		  
	    return " ";
	}
	
	public Class getColumnClass(int col) {
		
		return Object.class;
	}

}
