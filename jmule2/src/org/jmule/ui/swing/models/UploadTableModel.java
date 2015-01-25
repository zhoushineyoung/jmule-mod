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
 * Created on Sep 28, 2008
 * @author javajox
 * @version $Revision: 1.2 $
 * Last changed by $Author: javajox $ on $Date: 2009/09/27 14:20:00 $
 */
public class UploadTableModel extends AbstractTableModel {

	JMuleCore _core = JMuleCoreFactory.getSingleton();
	UploadManager _upload_manager = _core.getUploadManager();
	
	public final static int FILE_NAME    =  0;
	public final static int FILE_SIZE    =  1;
	public final static int UPLOAD_SPEED =  2;
	public final static int PEERS        =  3;
	public final static int ETA          =  4;
	public final static int UPLOADED     =  5;
	
	private final static String column_names[] = {
		                   "File name",
		                   "File size",
		                   "Upload speed",
		                   "Peers",
		                   "ETA",
		                   "Uploaded"
	};
	
	public Class getColumnClass(int col) {
		
		return UploadSession.class;
	}
	
	public int getColumnCount() {

		return column_names.length;
	}

	public int getRowCount() {

		return _upload_manager.getUploadCount();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		
		return _upload_manager.getUploads().get(rowIndex);
	}
	
	public String getColumnName(int col) {
		  
	    return column_names[col];
	}

}
