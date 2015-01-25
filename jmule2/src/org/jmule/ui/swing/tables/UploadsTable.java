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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.core.uploadmanager.UploadSession;
import org.jmule.core.utils.GeneralComparator;
import org.jmule.core.utils.Misc;
import org.jmule.ui.UIConstants;
import org.jmule.ui.swing.ImgRep;
import org.jmule.ui.swing.SwingUtils;
import org.jmule.ui.swing.maintabs.transfers.UploadDetailsDialog;
import org.jmule.ui.swing.models.UploadTableModel;
import org.jmule.ui.utils.FileFormatter;
import org.jmule.ui.utils.SpeedFormatter;
import org.jmule.ui.utils.TimeFormatter;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/11/17 14:53:43 $$
 */
public class UploadsTable extends JMTable {

	// =========================== Table cell renderers ======================
	
	class FileNameTableCellRenderer extends UploadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			String fn = session.getSharingName();
            this.setText(fn);
            this.setIcon(new ImageIcon(UIConstants.getMimeURLByExtension(Misc.getFileExtension(fn))));
			return this;
		}
	}
	
	class FileSizeTableCellRenderer extends UploadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setText(FileFormatter.formatFileSize(session.getFileSize()));
            this.setHorizontalAlignment(SwingConstants.RIGHT);
			return this;
		}
	}
	
	class UploadSpeedTableCellRenderer extends UploadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setText(SpeedFormatter.formatSpeed(session.getSpeed()));
            this.setHorizontalAlignment(SwingConstants.RIGHT);
			return this;
		}
	}
	
	class PeersTableCellRenderer extends UploadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setText(session.getPeerCount() + "");
            this.setHorizontalAlignment(SwingConstants.CENTER);
			return this;
		}
	}
	
	class ETATableCellRenderer extends UploadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setText(TimeFormatter.format(session.getETA()));
            this.setHorizontalAlignment(SwingConstants.RIGHT);
			return this;
		}
	}
	
	class UploadedTableCellRenderer extends UploadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setText(FileFormatter.formatFileSize(session.getTransferredBytes()));
            this.setHorizontalAlignment(SwingConstants.RIGHT);
			return this;
		}
	}
	
	JMuleCore _core = JMuleCoreFactory.getSingleton();
	DownloadManager _download_manager = _core.getDownloadManager();
	UploadManager _upload_manager = _core.getUploadManager();
	
	public UploadsTable(JFrame parent) {
		super(parent);
		init();
	}
	
	private void init() {
		
		JMTableColumn file_name = new JMTableColumn();
		file_name.setIdentifier(UIConstants.UPLOAD_LIST_FILE_NAME_COLUMN_ID);
		file_name.setModelIndex(UploadTableModel.FILE_NAME);
		file_name.setVisible(_pref.isColumnVisible(UIConstants.UPLOAD_LIST_FILE_NAME_COLUMN_ID));
		file_name.setHeaderValue("File name");
		file_name.setCellRenderer(new FileNameTableCellRenderer());
		file_name.setComparator(new GeneralComparator("getSharingName"));
		
		table_columns.add(file_name);
		
		JMTableColumn file_size = new JMTableColumn();
		file_size.setIdentifier(UIConstants.UPLOAD_LIST_FILE_SIZE_COLUMN_ID);
		file_size.setModelIndex(UploadTableModel.FILE_SIZE);
		file_size.setVisible(_pref.isColumnVisible(UIConstants.UPLOAD_LIST_FILE_SIZE_COLUMN_ID));
		file_size.setHeaderValue("File size");
		file_size.setCellRenderer(new FileSizeTableCellRenderer());
		file_size.setComparator(new GeneralComparator("getFileSize"));
		
		table_columns.add(file_size);
		
		JMTableColumn upload_speed = new JMTableColumn();
		upload_speed.setIdentifier(UIConstants.UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID);
		upload_speed.setModelIndex(UploadTableModel.UPLOAD_SPEED);
		upload_speed.setVisible(_pref.isColumnVisible(UIConstants.UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID));
		upload_speed.setHeaderValue("Upload speed");
		upload_speed.setCellRenderer(new UploadSpeedTableCellRenderer());
		upload_speed.setComparator(new GeneralComparator("getSpeed"));
		
		table_columns.add(upload_speed);
		
		JMTableColumn peers = new JMTableColumn();
		peers.setIdentifier(UIConstants.UPLOAD_LIST_PEERS_COLUMN_ID);
		peers.setModelIndex(UploadTableModel.PEERS);
		peers.setVisible(_pref.isColumnVisible(UIConstants.UPLOAD_LIST_PEERS_COLUMN_ID));
		peers.setHeaderValue("Peers");
		peers.setCellRenderer(new PeersTableCellRenderer());
		peers.setComparator(new GeneralComparator("getPeersCount"));
		
		table_columns.add(peers);
		
		JMTableColumn eta = new JMTableColumn();
		eta.setIdentifier(UIConstants.UPLOAD_LIST_ETA_COLUMN_ID);
		eta.setModelIndex(UploadTableModel.ETA);
		eta.setVisible(_pref.isColumnVisible(UIConstants.UPLOAD_LIST_ETA_COLUMN_ID));
		eta.setHeaderValue("ETA");
		eta.setCellRenderer(new ETATableCellRenderer());
		eta.setComparator(new GeneralComparator("getETA"));
		
		table_columns.add(eta);
		
		JMTableColumn uploaded = new JMTableColumn();
		uploaded.setIdentifier(UIConstants.UPLOAD_LIST_UPLOADED_COLUMN_ID);
		uploaded.setModelIndex(UploadTableModel.UPLOADED);
		uploaded.setVisible(_pref.isColumnVisible(UIConstants.UPLOAD_LIST_UPLOADED_COLUMN_ID));
		uploaded.setHeaderValue("Uploaded");
		uploaded.setCellRenderer(new UploadedTableCellRenderer());
		uploaded.setComparator(new GeneralComparator("getTransferredBytes"));
		
		table_columns.add(uploaded);
		
		this.buildColumns(new UploadTableModel());
		
		class PopupListener extends MouseAdapter {
			
			JMenuItem column_setup, properties;
			
			public PopupListener() {
				 
				 column_setup = new JMenuItem("Column setup");
				 column_setup.setIcon(ImgRep.getIcon("columns_setup.png"));
				 column_setup.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						 
					 }
				 });
				 
				 properties = new JMenuItem("Properties");
				 properties.setIcon(ImgRep.getIcon("info.png"));
				 properties.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						 UploadSession sessions[] = getSelectedUploadSessions();
						 UploadDetailsDialog udd = new UploadDetailsDialog(parent, sessions[0]);
						 SwingUtils.setWindowLocationRelativeTo(udd, parent);
						 udd.setVisible(true);
					 }
				 });
			}
			
			public void mousePressed(MouseEvent e) {
				showPopup(e);
			}
			
			public void mouseReleased(MouseEvent e) {
				showPopup(e);
			}
			
			private void showPopup(MouseEvent e) {
				 
				 if (e.isPopupTrigger()) {
					 
					 JPopupMenu popup_menu = null;
					
					 switch( whichCondition() ) {
					   
					    case  A  :  popup_menu = new JPopupMenu();
					                //popup_menu.add(column_setup);
					                //popup_menu.addSeparator();
					                popup_menu.add(properties);
					                break;
					                
					    case B   :  //popup_menu = new JPopupMenu();
					                //popup_menu.add(column_setup);
					                break;
					                
					    case C   : // popup_menu = new JPopupMenu();
		                           // popup_menu.add(column_setup);
		                            break;
					 }
					 
					 popup_menu.show(e.getComponent(), e.getX(), e.getY());
				 }
			}	
			
		}
		
		this.addMouseListener(new PopupListener());
	}
	
	private UploadSession[] getUploadSessionsByIndexes(int[] indexes) {

		UploadSession[] result = new UploadSession[indexes.length];
		int k = 0;
		for(int i : indexes) {
			int j = 0;
			for(UploadSession session : _upload_manager.getUploads()) {
				if( j == this.convertRowIndexToModel(i) ) {
					result[k++] = session;
					break;
				}
				++j;
			}
		}

		return result;
	}
	
	private UploadSession[] getSelectedUploadSessions() {
		
		return getUploadSessionsByIndexes(  this.getSelectedRows() );
	}
	
	/*  -----------------------------------------------------------------
	 *  |                        |  STARTED
	 *  -----------------------------------------------------------------
	 *  |  ONE_SELECTED          |   A
	 *  |                        | 
	 *  |  MULTIPLE_SELECTED     |   B
	 *  |                        |
	 *  |  VOID_LIST             |   *
	 *  
	 *       C - void list
	 *       
	 *    Ac1 | Properties
	 *    Ac2 | Column setup
	 *    
	 *    ---------------------------------------------------
	 *         |    Ac1     |   Ac2    | 
	 *    ---------------------------------------------------
	 *         |            |          |
	 *      A  |   Yes      |   Yes    |
	 *         |            |          |
	 *      B  |   No       |   Yes    |
	 *         |            |          |
	 *      C  |   No       |   Yes    |
	 */
	
	public ConditionType whichCondition() {
		
		if(getSelectedUploadSessions().length == 1) return ConditionType.A;
		
		if(getSelectedUploadSessions().length > 1 ) return ConditionType.B;
		
		if(getSelectedUploadSessions().length == 0 ) return ConditionType.C;
		
		return null;
	}
	
	private enum ConditionType {
		A,B,C
	}
}
