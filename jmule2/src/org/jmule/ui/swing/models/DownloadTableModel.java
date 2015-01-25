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
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.table.AbstractTableModel;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.edonkey.FileHash;

/**
 *
 * Created on Sep 28, 2008
 * @author javajox
 * @version $Revision: 1.3 $
 * Last changed by $Author: javajox $ on $Date: 2009/09/27 14:20:00 $
 */
public class DownloadTableModel extends AbstractTableModel {

	private JMuleCore _core = JMuleCoreFactory.getSingleton();
	private DownloadManager _download_manager = _core.getDownloadManager();
	private List<FileHash> downloads_order = new CopyOnWriteArrayList<FileHash>();
	
	//public final static int ORDER = 0;
	public final static int FILE_NAME = 0;
	public final static int FILE_SIZE = 1;
	public final static int TRANSFERRED = 2;
	public final static int DOWNLOAD_SPEED = 3;
	public final static int UPLOAD_SPEED = 4;
	public final static int PIECES = 5;
	public final static int DONE = 6;
	public final static int COMPLETE_SOURCES = 7;
	public final static int PARTIAL_SOURCES = 8;
	public final static int ETA = 9;
	public final static int STATUS = 10;
	public final static int PROGRESS_BAR = 11;
	
	private final static String column_names[] = {
		     //             "#",
		                  "File name",
		                  "File size",
		                  "Transferred",
		                  "Download speed",
		                  "Upload speed",
		                  "Pieces",
		                  "Done",
		                  "Complete sources",
		                  "Partial sources",
		                  "ETA",
		                  "Status",
		                  "Progress"
	};
	
	public DownloadTableModel() {
		/*_download_manager.addDownloadManagerListener(new DownloadManagerListener() {
			public void downloadAdded(FileHash fileHash) {
				downloads_order.add(fileHash);
			}            
			public void downloadStopped(FileHash fileHash) {
				downloads_order.remove(fileHash);
			}
			public void downloadRemoved(FileHash fileHash) {}
			public void downloadStarted(FileHash fileHash) {}
			
		});*/
	}
	
	public Class getColumnClass(int col) {
		
		return DownloadSession.class;
	}
	
	public int getColumnCount() {
		
		return column_names.length;
	}

	public int getRowCount() {

		return _download_manager.getDownloadCount();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
       // if(columnIndex == ORDER) {
        //	DownloadSession session = _download_manager.getDownload(rowIndex);
        	//for(int i=0; i<downloads_order.size(); i++)
        	//	if( downloads_order.get(i) == session.getFileHash() )
        	//return i + "";downloads_order.
        //	return downloads_order.indexOf(session.getFileHash()) + "";
       // }
		return _download_manager.getDownloads().get(rowIndex);
	}
	
	public String getColumnName(int col) {
		  
	    return column_names[col];
	}

	//public void addSessionOrder(DownloadSession downloadSession) {
		
	//	downloads_order.add(downloadSession);
	//}
}
