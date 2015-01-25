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

import org.jmule.core.sharingmanager.SharedFile;

/**
 *
 * Created on Oct 14, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/16 17:35:11 $
 */
public class SharedFilesTableModel extends AbstractTableModel {

	public static final int NAME = 0;
	public static final int SIZE = 1;
	public static final int TYPE = 2;
	public static final int HASH = 3;
	public static final int COMPLETED = 4;
	
	private static final String column_names[] = {
		                           "File name",
		                           "Size",
		                           "Type",
		                           "Hash",
		                           "Completed"
	                            };
	
	private List<SharedFile> sharedFiles;
	
	public SharedFilesTableModel(List<SharedFile> sharedFiles) {
		
	    this.sharedFiles = sharedFiles;	
	}
	
	public int getColumnCount() {

		return column_names.length;
	}

	public int getRowCount() {

		return sharedFiles.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {

		return sharedFiles.get(rowIndex);
	}

}
