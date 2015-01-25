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
package org.jmule.ui.swing.tables;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.core.sharingmanager.SharingManager;

/**
 *
 * Created on Sep 11, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/16 17:35:11 $
 */
public class SearchResultTableCellRenderer extends JMTableCellRenderer {

	protected SearchResultItem search_result;
	private JMuleCore _core = JMuleCoreFactory.getSingleton();
	private SharingManager _sharing_manager = _core.getSharingManager();
	private DownloadManager _download_manager = _core.getDownloadManager();
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        search_result = (SearchResultItem)value;
        SearchResultItem search_result = (SearchResultItem)value;
       // if( _download_manager.hasDownload(search_result.getFileHash()) ) 
		//	this.setForeground(Color.GREEN);
		// else 
		if( _sharing_manager.hasFile(search_result.getFileHash()) ) 
		    this.setForeground(Color.RED);
		 else this.setForeground(Color.BLACK);
		return this;
	}
	
}
