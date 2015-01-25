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
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.core.uploadmanager.UploadSession;

/**
 *
 * Created on Oct 7, 2008
 * @author javajox
 * @version $Revision: 1.3 $
 * Last changed by $Author: binary255 $ on $Date: 2009/11/17 14:53:43 $
 */
public class UploadPeersModel extends AbstractTableModel {

	private JMuleCore _core = JMuleCoreFactory.getSingleton();
	private UploadManager _upload_manager = _core.getUploadManager();
	private UploadSession session;
	
	public final static int NICK_NAME    =  0;
	public final static int CC           =  1;
	public final static int FLAG         =  2;
	public final static int IP           =  3;
	public final static int UP_SPEED     =  4;
	public final static int CLIENT       =  5;
	public final static int STATUS       =  6;
	
	private static String[] column_names = {
		          "Nick name",
		          "CC",
		          "Flag",
		          "IP",
		          "Up speed",
		          "Client",
		          "Status"
	};  
	
	public UploadPeersModel(UploadSession session) {
		this.session = session;
	}
	
	public int getColumnCount() {

		return column_names.length;
	}

	public int getRowCount() {
	
		return session.getPeerCount();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex==STATUS) 
        	return new Object[] { session, session.getPeers().get(rowIndex) };
		return session.getPeers().get(rowIndex);
	}

}
