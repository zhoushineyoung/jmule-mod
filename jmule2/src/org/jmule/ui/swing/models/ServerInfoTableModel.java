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
import org.jmule.core.servermanager.Server;
import org.jmule.core.servermanager.ServerManager;
import org.jmule.ui.utils.NumberFormatter;

/**
 *
 * Created on Oct 2, 2008
 * @author javajox
 * @version $Revision: 1.2 $
 * Last changed by $Author: javajox $ on $Date: 2009/09/22 19:08:43 $
 */
public class ServerInfoTableModel extends AbstractTableModel {

	public final static int NAME = 0;
	public final static int IP = 1;
	public final static int DESCRIPTION = 2;
	public final static int PING = 3;
	public final static int FILES = 4;
	public final static int USERS = 5;
	public final static int SOFT_LIMIT = 6;
	public final static int HARD_LIMIT = 7;
	public final static int VERSION = 8;
	
	private final static String row_names[] = {
		                        "  Name",
		                        "  IP",
		                        "  Description",
		                        "  Ping",
		                        "  Files",
		                        "  Users",
		                        "  Soft limit",
		                        "  Hard limit",
		                        "  Version"
	};
	
	JMuleCore _core = JMuleCoreFactory.getSingleton();
	ServerManager _server_manager = _core.getServerManager();
	
	public int getColumnCount() {
		
		return 2;
	}

	public int getRowCount() {
		
		return row_names.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		
		Server c_server = _server_manager.getConnectedServer();
		
		if( columnIndex == 0 ) {
            return row_names[rowIndex];
		}
		
		if( c_server != null ) {
		   if( columnIndex == 1 ) {
		      switch(rowIndex) {
		         case NAME        :    return " " + c_server.getName();
		         case IP          :    return " " + c_server.getAddress();
		         case DESCRIPTION :    return " " + c_server.getDesc();
		         case PING        :    return " " + c_server.getPing();
		         case FILES       :    return " " + NumberFormatter.formatSizeHumanReadable(c_server.getNumFiles());
		         case USERS       :    return " " + NumberFormatter.formatSizeHumanReadable(c_server.getNumUsers());
		         case SOFT_LIMIT  :    return " " + NumberFormatter.formatSizeHumanReadable(c_server.getSoftLimit());
		         case HARD_LIMIT  :    return " " + NumberFormatter.formatSizeHumanReadable(c_server.getHardLimit());
		         case VERSION     :    return " " + c_server.getVersion();
		      }
		  }
		}
		
		return "";
	}
	
	public String getColumnName(int col) {
		
		return " ";
	}
	
	public Class getColumnClass(int col) {
		
		return Object.class;
	}

}
