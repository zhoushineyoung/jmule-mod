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

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.utils.GeneralComparator;
import org.jmule.core.utils.Misc;
import org.jmule.ui.UIConstants;
import org.jmule.ui.swing.models.SharedFilesTableModel;
import org.jmule.ui.utils.FileFormatter;
import org.jmule.ui.utils.NumberFormatter;

/**
 *
 * Created on Oct 2, 2008
 * @author javajox
 * @version $Revision: 1.4 $
 * Last changed by $Author: javajox $ on $Date: 2009/09/22 19:08:43 $
 */
public class SharedFilesTable extends JMTable {
	
	// ---------------- Table cell renderers ----------------------
	
	class FileNameTableCellRenderer extends SharedFilesTableCellRenderer  {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.LEFT);
			String fn = shared_file.getSharingName();
			this.setText(" " + fn);
			this.setIcon(new ImageIcon(UIConstants.getMimeURLByExtension(Misc.getFileExtension(fn))));
			return this;
		}
	}
	
	class FileSizeTableCellRenderer extends SharedFilesTableCellRenderer  {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			this.setText(FileFormatter.formatFileSize(shared_file.length()) + " ");
			return this;
		}
	}
	
	class FileTypeTableCellRenderer extends SharedFilesTableCellRenderer  {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.LEFT);
			this.setText(" " + FileFormatter.formatMimeType(shared_file.getMimeType()));
			return this;
		}
	}
	
	class FileHashTableCellRenderer extends SharedFilesTableCellRenderer  {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setText(shared_file.getFileHash().getAsString());
			return this;
		}
	}
	
	class CompletedTableCellRenderer extends SharedFilesTableCellRenderer  {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			if(!shared_file.isCompleted()) 
				this.setText( NumberFormatter.formatProgress(((PartialFile)shared_file).getPercentCompleted()) + " ");
			else
				this.setText( NumberFormatter.formatProgress(100) + " " );
			return this;
		}
	}
	
	private List<SharedFile> sharedFiles;
	
	public SharedFilesTable(List<SharedFile> sharedFiles) {
		
		this.sharedFiles = sharedFiles;
				
		init();
	}
	
	private void init() {
		
		JMTableColumn file_name = new JMTableColumn();
		file_name.setIdentifier(UIConstants.SHARED_LIST_FILE_NAME_COLUMN_ID);
		file_name.setModelIndex(SharedFilesTableModel.NAME);
		file_name.setVisible(_pref.isColumnVisible(UIConstants.SHARED_LIST_FILE_NAME_COLUMN_ID));
		file_name.setHeaderValue("File name");
		file_name.setCellRenderer(new FileNameTableCellRenderer());
		file_name.setComparator(new GeneralComparator("getSharingName"));
		
		table_columns.add(file_name);
		
		JMTableColumn file_size = new JMTableColumn();
		file_size.setIdentifier(UIConstants.SHARED_LIST_FILE_SIZE_COLUMN_ID);
		file_size.setModelIndex(SharedFilesTableModel.SIZE);
		file_size.setVisible(_pref.isColumnVisible(UIConstants.SHARED_LIST_FILE_SIZE_COLUMN_ID));
		file_size.setHeaderValue("Size");
		file_size.setCellRenderer(new FileSizeTableCellRenderer());
		file_size.setComparator(new GeneralComparator("length"));
		
		table_columns.add(file_size);
		
		JMTableColumn file_type = new JMTableColumn();
		file_type.setIdentifier(UIConstants.SHARED_LIST_FILE_TYPE_COLUMN_ID);
		file_type.setModelIndex(SharedFilesTableModel.TYPE);
		file_type.setVisible(_pref.isColumnVisible(UIConstants.SHARED_LIST_FILE_TYPE_COLUMN_ID));
		file_type.setHeaderValue("Type");
		file_type.setCellRenderer(new FileTypeTableCellRenderer());
		file_type.setComparator(new Comparator() {
			public int compare(Object o1, Object o2) {
				String file_type1 = FileFormatter.formatMimeType(((SharedFile)o1).getMimeType());
				String file_type2 = FileFormatter.formatMimeType(((SharedFile)o2).getMimeType());
				return Misc.compareAllObjects(file_type1, file_type2, "toString", true);
			}			
		});
		
		table_columns.add(file_type);
		
		JMTableColumn file_hash = new JMTableColumn();
		file_hash.setIdentifier(UIConstants.SHARED_LIST_FILE_ID_COLUMN_ID);
		file_hash.setModelIndex(SharedFilesTableModel.HASH);
		file_hash.setVisible(_pref.isColumnVisible(UIConstants.SHARED_LIST_FILE_ID_COLUMN_ID));
		file_hash.setHeaderValue("Hash");
		file_hash.setCellRenderer(new FileHashTableCellRenderer());
		file_hash.setComparator(new GeneralComparator("getFileHash"));
		
		table_columns.add(file_hash);
		
		JMTableColumn completed = new JMTableColumn();
		completed.setIdentifier(UIConstants.SHARED_LIST_COMPLETED_COLUMN_ID);
		completed.setModelIndex(SharedFilesTableModel.COMPLETED);
		completed.setVisible(_pref.isColumnVisible(UIConstants.SHARED_LIST_COMPLETED_COLUMN_ID));
		completed.setHeaderValue("Completed");
		completed.setCellRenderer(new CompletedTableCellRenderer());
		completed.setComparator(new GeneralComparator("getPercentCompleted"));
		completed.setComparator(new Comparator() {
			public int compare(Object o1, Object o2) {
				double completed1 = 100;
				double completed2 = 100;
				SharedFile shared_file1 = (SharedFile)o1;
				SharedFile shared_file2 = (SharedFile)o2;
				if(!shared_file1.isCompleted()) 
					completed1 = ((PartialFile)shared_file1).getPercentCompleted();
				if(!shared_file2.isCompleted()) 
					completed2 = ((PartialFile)shared_file2).getPercentCompleted();
                if(completed1 == completed2) return 0;
				if(completed1 > completed2) return 1;
				return -1;
			}
		});
		
		table_columns.add(completed);
		
		super.buildColumns(new SharedFilesTableModel(sharedFiles));
		
        class PopupListener extends MouseAdapter {
			
			JMenuItem column_setup;
			
			public PopupListener() {
				 
				 column_setup = new JMenuItem("Column setup");
			}
			
			public void mousePressed(MouseEvent e) {
				showPopup(e);
			}
			
			public void mouseReleased(MouseEvent e) {
				showPopup(e);
			}
			
			private void showPopup(MouseEvent e) {
				 
				 if (e.isPopupTrigger()) {
					 
					 JPopupMenu popup_menu = new JPopupMenu();
					
					 popup_menu.add(column_setup);
					 
					 popup_menu.show(e.getComponent(), e.getX(), e.getY());
				 }
			}	
			
		}
		
		//this.addMouseListener(new PopupListener());
		
	}
	
	/*private SharedFile[] getFilesByIndexes(int[] indexes) {
		//for(int index : indexes) {
		//	System.out.println("Index=>" + index);
		//}
		SharedFile[] result = new SharedFile[indexes.length];
		int k = 0;
		for(int i : indexes) {
			int j = 0;
			for(Server server : _server_manager.getServers()) {
				if( j == this.convertRowIndexToModel(i) ) {
					result[k++] = server;
					break;
				}
				++j;
			}
		}
		//for(Server server : result) {
		//	System.out.println(server);
		//}
		return result;
	}*/
	

}
