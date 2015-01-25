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

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.peermanager.Peer;

/**
 *
 * Created on Oct 6, 2008
 * @author javajox
 * @version $Revision: 1.2 $
 * Last changed by $Author: javajox $ on $Date: 2009/09/22 19:08:43 $
 */
public class DownloadPeersModel extends AbstractTableModel {

	private JMuleCore _core = JMuleCoreFactory.getSingleton();
	private DownloadManager _download_manager = _core.getDownloadManager();
	private DownloadSession session;
		
	public final static int NICK_NAME       =   0;
	public final static int CC              =   1;
	public final static int FLAG            =   2;
	public final static int ADDRESS         =   3;
	public final static int DOWN_SPEED      =   4;
	public final static int UP_SPEED        =   5;
	public final static int CLIENT_NAME     =   6;
	public final static int STATUS          =   7;
	
	private static String[] column_names = {
		                   "Nick name",
		                   "CC",
		                   "Flag",
		                   "Address",
		                   "Down speed",
		                   "Up speed",
		                   "Client",
		                   "Status"
	};
	
	public DownloadPeersModel(DownloadSession session) {
		super();
		this.session = session;
		//session.getPeers().
	}
	
	public Class getColumnClass() {
		
		return Peer.class;
	}
	
	public int getColumnCount() {

		return column_names.length;
	}

	public int getRowCount() {

		return session.getPeers().size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
        List<Peer> peer_list = session.getPeers();
        Peer peer = peer_list.get(rowIndex);
        //ugly hack
        if(columnIndex == STATUS)
        	return new Object[] {peer, session};
        
		return peer;
	}
	
	public String getColumnName(int col) {
		  
	    return column_names[col];
	}

}
