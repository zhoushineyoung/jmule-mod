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

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.servermanager.Server;
import org.jmule.core.servermanager.ServerManager;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2009/09/22 19:08:43 $$
 */
public class ServerListTableModel extends JMTableModel {
	
	  private JMuleCore _core = JMuleCoreFactory.getSingleton();
	  private ServerManager _server_manager = _core.getServerManager();
	
	  public final static int SERVER_NAME = 0;
	  public final static int CC = 1;
	  public final static int FLAG = 2;
	  public final static int SERVER_IP = 3;
	  public final static int DESCRIPTION = 4;
	  public final static int PING = 5;
	  public final static int USERS = 6;
	  public final static int MAX_USERS = 7;
	  public final static int FILES = 8;
	  public final static int SOFT_LIMIT = 9;
	  public final static int HARD_LIMIT = 10;
	  public final static int VERSION = 11;
	  public final static int STATIC = 12;
	  public final static int DOWN = 13;
	  
	  //TODO get the strings from Localizer
	  private final static String[] column_names = {
		                        "Name",
		                        "CC",
		                        "Flag",
		                        "IP",
		                        "Description",
		                        "Ping",
		                        "Users",
		                        "Max users",
		                        "Files",
		                        "Soft limit",
		                        "Hard limit",
		                        "Version",
		                        "Static",
		                        "Down"
	                          };
	  
	  public ServerListTableModel() {

	  }
	  
	  public Class getColumnClass(int col) {
		  
			/*switch (col) {
			  case 0  : return Server.class;
			  case 1  : return String.class;
			  case 2  : return String.class;
			  case 3  : return String.class;
			  case 4  : return String.class;
			  case 5  : return Integer.class;
			  case 6  : return Integer.class;
			  case 7  : return Integer.class;
			  case 8  : return Integer.class;
			  case 9  : return Integer.class;
			  case 10  : return Integer.class;
			  case 11  : return String.class;
			  case 12 : return String.class;  
			  default : return Object.class;
			}*/
		  return Server.class;
	  }
	  
	  public int getColumnCount() {
		  
		  return column_names.length;
	  }

	  public int getRowCount() {

		return JMuleCoreFactory.getSingleton().getServerManager().getServersCount();
	  }

	  public Object getValueAt(int row, int col) {
		  
		  int i = 0;
		  
		  for(Server server : _server_manager.getServers()) {
			  
			  if( i == row) {
				
				 // if( col == 1 ) {
				//	  return !CountryLocator.getInstance().isServiceDown() ? 
		    	//	          CountryLocator.getInstance().getCountryCode(server.getAddress()) : "Unknown";
				//  } else {
					  return server;
				//  }
				  
				 /* switch( col ) {
				  
				    case 0  : return server; 
				    case 1  : return !CountryLocator.getInstance().isServiceDown() ? 
				    		          CountryLocator.getInstance().getCountryCode(server.getAddress()) : "Unknown";
				    case 2  : return server.getAddress();
				    case 3  : return server.getAddress() + ":" + server.getPort(); 
				    case 4  : return server;
				    case 5  : return server.getPing();
				    case 6  : return server.getNumUsers();
				    case 7  : return server.getMaxUsers();
				    case 8  : return server.getNumFiles();
				    case 9  : return server.getSoftLimit();
				    case 10  : return server.getHardLimit();
				    case 11  : return server.getVersion();
				    case 12 : return ( server.isStatic() ? "Yes" : "No");
				  }*/
				  
			  }
			  
			  ++i;
		  }
		  
		  return null;
	  }
		
	  public String getColumnName(int col) {
		  return column_names[col];
	  }
	  
}
