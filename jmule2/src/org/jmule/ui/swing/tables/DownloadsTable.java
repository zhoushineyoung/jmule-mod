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
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.table.TableColumnExt;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMThread;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.downloadmanager.DownloadSession.DownloadStatus;
import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.sharingmanager.Gap;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.core.uploadmanager.UploadSession;
import org.jmule.core.utils.GeneralComparator;
import org.jmule.core.utils.Misc;
import org.jmule.ui.UIConstants;
import org.jmule.ui.swing.ImgRep;
import org.jmule.ui.swing.SwingGUIUpdater;
import org.jmule.ui.swing.SwingUtils;
import org.jmule.ui.swing.maintabs.transfers.DownloadDetailsDialog;
import org.jmule.ui.swing.models.DownloadTableModel;
import org.jmule.ui.utils.FileFormatter;
import org.jmule.ui.utils.NumberFormatter;
import org.jmule.ui.utils.SpeedFormatter;
import org.jmule.ui.utils.TimeFormatter;


/**
 * 
 * @author javajox
 * @version $$Revision: 1.10 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2010/01/10 14:20:44 $$
 */
public class DownloadsTable extends JMTable {

	JMuleCore _core = JMuleCoreFactory.getSingleton();
	UploadManager _upload_manager = _core.getUploadManager();
	DownloadManager _download_manager = _core.getDownloadManager();
	
	// ================== Table cell renderers ===================
	
	class OrderTableCellRenderer extends DownloadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if(value instanceof String)
            	this.setText(value.toString());
			return this;
		}
	}
	
	class FileNameTableCellRenderer extends DownloadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.LEFT);
			String fn = session.getSharingName();
            this.setText(fn);
            URL icon_url = UIConstants.getMimeURLByExtension(Misc.getFileExtension(fn));
            this.setIcon(new ImageIcon(icon_url));
            String size = FileFormatter.formatFileSize(session.getFileSize());
            String d_speed = SpeedFormatter.formatSpeed(session.getSpeed());
            String u_speed = null;
            try {
              if(_upload_manager.hasUpload(session.getFileHash())) 
            	u_speed = SpeedFormatter.formatSpeed(_upload_manager.getUpload(session.getFileHash()).getSpeed());
              else u_speed = SpeedFormatter.formatSpeed(0);
            }catch( Throwable cause ) {
            	cause.printStackTrace();
            }  
            String done = NumberFormatter.formatProgress(session.getPercentCompleted());
            String eta = TimeFormatter.format(session.getETA());
            
            String str = "<html> " +
            		"<table><tr><td><img src=\"" + icon_url + "\"></td><td><b>" + fn + "</b></td><tr></table>" +
            		"<hr>" +
            		"<table>" +
            		"<tr><td>Size</td><td>" + size + "</td></tr>" +
            		"<tr><td>Done</td><td>" + done + "</td></tr>" +
            		"<tr><td>Down speed</td><td>" + d_speed + "</td></tr>" +
            		"<tr><td>Up speed</td><td>" + u_speed + "</td></tr>" +
            		"<tr><td>ETA</td><td>" + eta + "</td></tr>" +
            		"</table>" +
            		"</html>";
            this.setToolTipText(str);
			return this;
		}
	}
	
	class FileSizeTableCellRenderer extends DownloadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setHorizontalAlignment(SwingConstants.RIGHT);
            this.setText(FileFormatter.formatFileSize(session.getFileSize()) + " ");
			return this;
		}
	} 
	
	class TransferredTableCellRenderer extends DownloadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setHorizontalAlignment(SwingConstants.RIGHT);
            this.setText(FileFormatter.formatFileSize(session.getTransferredBytes()) + " ");
			return this;
		}
	}
	
	class DownloadSpeedTableCellRenderer extends DownloadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setHorizontalAlignment(SwingConstants.RIGHT);
            this.setText(SpeedFormatter.formatSpeed(session.getSpeed()) + " ");
			return this;
		}
	}
	
	class UploadSpeedTableCellRenderer extends DownloadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        	this.setHorizontalAlignment(SwingConstants.RIGHT);
        	try {
              if(_upload_manager.hasUpload(session.getFileHash())) 
            	this.setText(SpeedFormatter.formatSpeed(_upload_manager.getUpload(session.getFileHash()).getSpeed()) + " ");
              else this.setText(SpeedFormatter.formatSpeed(0) + " ");
        	}catch(Throwable cause) {
        		cause.printStackTrace();
        	}
			return this;
		}
	}
	
	class PiecesTableCellRenderer  extends JPanel implements TableCellRenderer { //extends DefaultTableCellRenderer {

		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			DownloadSession session = (DownloadSession)value;
			this.setFileSize(session.getFileSize());
			this.setGapList(session.getGapList());
			this.setTransferredBytesCount(session.getTransferredBytes());
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(1,1));
			//panel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
			panel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
			if(isSelected) {
				  panel.setForeground(table.getSelectionForeground());
				  panel.setBackground(table.getSelectionBackground());
			} else {
				  panel.setForeground(table.getForeground());
				  panel.setBackground(table.getBackground());
			}
			this.setOpaque(true);
			panel.add(this);
			return panel;
		}

		class Pair {
			private int x;
			private int y;
			
			public Pair(int x, int y) {
				this.x = x;
				this.y = y;
			}
			
			public int getX() {
				return x;
			}
			
			public int getY() {
				return y;
			}
			
			public void setX(int x) {
				this.x = x;
			}
			
			public void setY(int y) {
				this.y = y;
			}
		}
		
		private ArrayList<Pair> pairs = new ArrayList();
		private GapList gap_list;
		private long file_size;
		private long transferred_bytes_count;
		
		public PiecesTableCellRenderer() {
			pairs.add(new Pair(10,13));
			pairs.add(new Pair(22,50));
			pairs.add(new Pair(56,60));
			pairs.add(new Pair(80,90));
			pairs.add(new Pair(100,101));
			//pairs.add(new Pair(0,1300));
			this.setBackground(new Color(0,128,255));
			this.setForeground(Color.BLACK);
		}
		
		public PiecesTableCellRenderer(GapList gapList, Long fileSize) {
			setGapList(gapList);
			setFileSize(fileSize);
		}
		
		public void setGapList(GapList gapList) {
			this.gap_list = gapList;
		}
		
		public void setFileSize(Long fileSize) {
			this.file_size = fileSize;
		}
		
		public void setTransferredBytesCount(long transferredBytesCount) {
			this.transferred_bytes_count = transferredBytesCount;
		}
		
		public void fillRect(int x, int length) {
			
		}
		
		public void paint(Graphics g) {
			  super.paint(g);
			 
			 // g = this.getGraphics();
			 
			 //g.drawLine(50, 50, 200, 200);
			 //g.fillRect(60, 0, 10, this.getHeight());
			 //g.fillRect(60+this.getWidth()-300+50, 0, this.getWidth()-300-120, this.getHeight());
			 //g.fillRect(x, y, width, height)
			 
			// float file_size = 1300; // in bytes
			 float total_length = this.getWidth();
			 float pixels_for_byte = total_length / (float)file_size; 
			 
			 g.setColor(Color.WHITE);
			 
		     g.fillRect(0, 0, this.getWidth(), 4);
			 
		     g.setColor(new Color(0,100,199)); // black blue
		     
		     g.fillRect(0, 0, Math.round((transferred_bytes_count * this.getWidth())/file_size), 4);
		     
			 //g.setColor(Color.GRAY);
			 //System.out.println(pixels_for_byte);
			  //g.drawLine(0, 6, this.getWidth(), 6);
				// g.fillRect(2, 2, this.getWidth(), 7);
			  g.setColor(Color.WHITE);
			  //g.fillRect(0, 1, this.getWidth(), 1);
			  g.drawLine(0, 3, this.getWidth(), 3);
			  for(Gap gap : gap_list.getGaps()) {
				  g.fillRect( Math.round(gap.getStart()*pixels_for_byte), 
						     4, 
						     Math.round(gap.getEnd()*pixels_for_byte - gap.getStart()*pixels_for_byte), 
						     this.getHeight());
			  }
			  /*for(Pair p : pairs) {
				  g.fillRect( Math.round(p.getX()*pixels_for_byte), 
						     5, 
						     Math.round(p.getY()*pixels_for_byte - p.getX()*pixels_for_byte), 
						     this.getHeight());
				  // ---------------------------------------------------------
				  System.out.println(Math.round(p.getX()*pixels_for_byte));
				  System.out.println(Math.round(p.getY()*pixels_for_byte - p.getX()*pixels_for_byte));
			  }*/
		}
		
		
		
	}

	
	class DoneTableCellRenderer extends DownloadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setHorizontalAlignment(SwingConstants.RIGHT);
            this.setText(NumberFormatter.formatProgress(session.getPercentCompleted()) + " ");
			return this;
		}
	}
	
	class CompletedSourcesTableCellRenderer extends DownloadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setHorizontalAlignment(SwingConstants.CENTER);
            this.setText(session.getCompletedSources()+"");
			return this;
		}
	}
	
	class PartialSourcesTableCellRenderer extends DownloadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setHorizontalAlignment(SwingConstants.CENTER);
            this.setText(session.getPartialSources()+"");
			return this;
		}
	}
	
	class ETATableCellRenderer extends DownloadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setHorizontalAlignment(SwingConstants.RIGHT);
            this.setText(TimeFormatter.format(session.getETA()) + " ");
			return this;
		}
	}
	
	class StatusTableCellRenderer extends DownloadTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.LEFT);
			if( session.getStatus() == DownloadStatus.STARTED ) this.setText(" started");
			else if( session.getStatus() == DownloadStatus.STOPPED ) this.setText(" stopped");
			return this;
		}
	}
	
	class ProgressBarTableCellRenderer extends JProgressBar implements  TableCellRenderer { //extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			DownloadSession session = (DownloadSession)value;
			//JProgressBar progress_bar = new JProgressBar();
			this.setMinimum(0);
			this.setMaximum((int)session.getFileSize());
			this.setValue((int)session.getTransferredBytes());
			this.setStringPainted(true);
			this.setBackground(Color.WHITE);
			this.setForeground(new Color(0,100,199));
			//this.setBorder(javax.swing.BorderFactory.createEtchedBorder());
			this.setBorder(new javax.swing.border.LineBorder(java.awt.Color.lightGray, 1, true));
			//this.setBorderPainted(false);
			//this.setLayout(new GridLayout(1,1));
			//if(isSelected) {
			//	  this.setForeground(table.getSelectionForeground());
			//	  this.setBackground(table.getSelectionBackground());
			//} else {
			//	  this.setForeground(table.getForeground());
			//	  this.setBackground(table.getBackground());
			//}
			//this.setOpaque(true);
			//this.add(progress_bar);
			return this;
		}
	}
	
	private SwingGUIUpdater _gui_updater = SwingGUIUpdater.getInstance();
	protected final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	public DownloadsTable(JFrame parent) {
		super(parent);
		init();		
	}
	
	private void init() {
		
		//JMTableColumn order = new JMTableColumn();
		//order.setIdentifier(UIConstants.DOWNLOAD_LIST_ORDER_ID);
		//order.setModelIndex(DownloadTableModel.ORDER);
		//order.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_LIST_ORDER_ID));
		//order.setHeaderValue("#");
		//order.setCellRenderer(new OrderTableCellRenderer());
		
		//table_columns.add(order);
		
		TableColumnExt file_name = new TableColumnExt();
		file_name.setIdentifier(UIConstants.DOWNLOAD_LIST_FILE_NAME_COLUMN_ID);
		file_name.setModelIndex(DownloadTableModel.FILE_NAME);
		file_name.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_LIST_FILE_NAME_COLUMN_ID));
		file_name.setHeaderValue("File name");
		file_name.setCellRenderer(new FileNameTableCellRenderer());
		file_name.setComparator(new GeneralComparator("getSharingName"));
		
		table_columns.add(file_name);
		
		TableColumnExt file_size = new TableColumnExt();
		file_size.setIdentifier(UIConstants.DOWNLOAD_LIST_SIZE_COLUMN_ID);
		file_size.setModelIndex(DownloadTableModel.FILE_SIZE);
		file_size.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_LIST_SIZE_COLUMN_ID));
		file_size.setHeaderValue("Size");
		file_size.setCellRenderer(new FileSizeTableCellRenderer());
		file_size.setComparator(new GeneralComparator("getFileSize"));
		
		table_columns.add(file_size);
		
		TableColumnExt transferred = new TableColumnExt();
		transferred.setIdentifier(UIConstants.DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID);
		transferred.setModelIndex(DownloadTableModel.TRANSFERRED);
		transferred.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID));
		transferred.setHeaderValue("Transferred");
		transferred.setCellRenderer(new TransferredTableCellRenderer());
		transferred.setComparator(new GeneralComparator("getTransferredBytes"));
		
		table_columns.add(transferred);
		
		TableColumnExt download_speed = new TableColumnExt();
		download_speed.setIdentifier(UIConstants.DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID);
		download_speed.setModelIndex(DownloadTableModel.DOWNLOAD_SPEED);
		download_speed.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID));
		download_speed.setHeaderValue("Down speed");
		download_speed.setCellRenderer(new DownloadSpeedTableCellRenderer());
		download_speed.setComparator(new GeneralComparator("getSpeed"));
		
		table_columns.add(download_speed);
		
		TableColumnExt upload_speed = new TableColumnExt();
		upload_speed.setIdentifier(UIConstants.DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID);
		upload_speed.setModelIndex(DownloadTableModel.UPLOAD_SPEED);
		upload_speed.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID));
		upload_speed.setHeaderValue("Up speed");
		upload_speed.setCellRenderer(new UploadSpeedTableCellRenderer());
		upload_speed.setComparator(new Comparator() {
			public int compare(Object o1, Object o2) {
				Float u_speed1 = null;
				Float u_speed2 = null;
				try {
				  DownloadSession d_session1 = (DownloadSession)o1;
				  DownloadSession d_session2 = (DownloadSession)o2;
				  UploadSession u_session1 = null;
				  UploadSession u_session2 = null;
				  if(_upload_manager.hasUpload(d_session1.getFileHash()))
						u_session1 = _upload_manager.getUpload(d_session1.getFileHash());
				   if(_upload_manager.hasUpload(d_session2.getFileHash()))
						u_session2 = _upload_manager.getUpload(d_session2.getFileHash());
				   u_speed1 = new Float((u_session1!=null)?u_session1.getSpeed():0.0f);
				   u_speed2 = new Float((u_session2!=null)?u_session2.getSpeed():0.0f);
				}catch(Throwable cause) {
					cause.printStackTrace();
				}
		 		return Misc.compareAllObjects(u_speed1, u_speed2, "floatValue", true);
			}	
		});
		
		table_columns.add(upload_speed);
		
		TableColumnExt pieces = new TableColumnExt();
		pieces.setIdentifier(UIConstants.DOWNLOAD_LIST_PROGRESS_COLUMN_ID);
		pieces.setModelIndex(DownloadTableModel.PIECES);
		pieces.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_LIST_PROGRESS_COLUMN_ID));
		pieces.setHeaderValue("Pieces");
		pieces.setCellRenderer(new PiecesTableCellRenderer());
		pieces.setComparator(new GeneralComparator("getPercentCompleted"));
		
		table_columns.add(pieces);
		
		TableColumnExt done = new TableColumnExt();
		done.setIdentifier(UIConstants.DOWNLOAD_LIST_COMPLETED_COLUMN_ID);
		done.setModelIndex(DownloadTableModel.DONE);
		done.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_LIST_COMPLETED_COLUMN_ID));
		done.setHeaderValue("Done");
		done.setCellRenderer(new DoneTableCellRenderer());
		done.setComparator(new GeneralComparator("getPercentCompleted"));
		
		table_columns.add(done);
		
		TableColumnExt progress_bar = new TableColumnExt();
		progress_bar.setIdentifier(UIConstants.DOWNLOAD_LIST_PROGRESS_BAR_COLUMN_ID);
		progress_bar.setModelIndex(DownloadTableModel.PROGRESS_BAR);
		progress_bar.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_LIST_PROGRESS_BAR_COLUMN_ID));
		progress_bar.setHeaderValue("Progress");
		progress_bar.setCellRenderer(new ProgressBarTableCellRenderer());
		progress_bar.setComparator(new GeneralComparator("getPercentCompleted"));
		
		table_columns.add(progress_bar);
		
		TableColumnExt complete_sources = new TableColumnExt();
		complete_sources.setIdentifier(UIConstants.DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID);
		complete_sources.setModelIndex(DownloadTableModel.COMPLETE_SOURCES);
		complete_sources.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID));
		complete_sources.setVisible(true);
		complete_sources.setHeaderValue("Complete sources");
		complete_sources.setCellRenderer(new CompletedSourcesTableCellRenderer());
		complete_sources.setComparator(new GeneralComparator("getCompletedSources"));
		
		table_columns.add(complete_sources);
		
		TableColumnExt partial_sources = new TableColumnExt();
		partial_sources.setIdentifier(UIConstants.DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID);
		partial_sources.setModelIndex(DownloadTableModel.PARTIAL_SOURCES);
		partial_sources.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID));
		partial_sources.setVisible(true);
		partial_sources.setHeaderValue("Partial sources");
		partial_sources.setCellRenderer(new PartialSourcesTableCellRenderer());
		partial_sources.setComparator(new GeneralComparator("getPartialSources"));
		
		table_columns.add(partial_sources);
		
		TableColumnExt eta = new TableColumnExt();
		eta.setIdentifier(UIConstants.DOWNLOAD_LIST_REMAINING_COLUMN_ID);
		eta.setModelIndex(DownloadTableModel.ETA);
		eta.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_LIST_REMAINING_COLUMN_ID));
		eta.setHeaderValue("ETA");
		eta.setCellRenderer(new ETATableCellRenderer());
		eta.setComparator(new GeneralComparator("getETA"));
		
		table_columns.add(eta);
		
		TableColumnExt status = new TableColumnExt();
		status.setIdentifier(UIConstants.DOWNLOAD_LIST_STATUS_COLUMN_ID);
		status.setModelIndex(DownloadTableModel.STATUS);
		status.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_LIST_STATUS_COLUMN_ID));
		status.setHeaderValue("Status");
		status.setCellRenderer(new StatusTableCellRenderer());
		status.setComparator(new GeneralComparator("getStatus"));
		
		table_columns.add(status);
		
		super.buildColumns(new DownloadTableModel());
		
		class PopupListener extends MouseAdapter {
			
			JMenuItem start_download, stop_download, cancel_download,
	                  paste_ed2k_links, copy_ed2k_links, column_setup, properties;
			
			public PopupListener() {
				 start_download = new JMenuItem("Start download");
				 start_download.setIcon(ImgRep.getIcon("start_download.png"));
				 start_download.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						for(DownloadSession session : getSelectedDownloadSessions()) 
						  if(session.getStatus() == DownloadStatus.STOPPED)	
							try {
							  _download_manager.startDownload(session.getFileHash());
							}catch(Throwable cause) {
								cause.printStackTrace();
							}
					}
				 });
				 stop_download = new JMenuItem("Stop download");
				 stop_download.setIcon(ImgRep.getIcon("stop_download.png"));
				 stop_download.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						for(DownloadSession session : getSelectedDownloadSessions()) 
						  if(session.getStatus() == DownloadStatus.STARTED)	
						   try {
							_download_manager.startDownload(session.getFileHash());
						   }catch( Throwable cause ) {
							   cause.printStackTrace();
						   }
					}
				 });
				 cancel_download = new JMenuItem("Cancel download");
				 cancel_download.setIcon(ImgRep.getIcon("cancel.png"));
				 cancel_download.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						for(DownloadSession session : getSelectedDownloadSessions())
						  try {
							_download_manager.cancelDownload(session.getFileHash());
						  }catch( Throwable cause ) {
							  cause.printStackTrace();
						  }
					}
				 });
				 paste_ed2k_links = new JMenuItem("Paste ed2k link(s)");
				 paste_ed2k_links.setIcon(ImgRep.getIcon("ed2k_link_paste.png"));
				 paste_ed2k_links.addActionListener(new ActionListener() {
					   public void actionPerformed(ActionEvent event) {
						      (new JMThread(new JMRunnable() {
						    	   public void JMRun() {
						    		 try {  
						    		   Object clipboard_contents = clipboard.getData(DataFlavor.stringFlavor);
						    		   List<ED2KFileLink> clipboard_file_links = ED2KFileLink.extractLinks(clipboard_contents.toString());
									   if( clipboard_file_links.size() != 0 )  
										 for(ED2KFileLink file_link : clipboard_file_links) {  
	                                        _download_manager.addDownload(file_link); 
										  }
						    		 }catch(Throwable t) {
						    			 t.printStackTrace();
						    		 }
								   }
						      })).start();
					   }
				   });
				 copy_ed2k_links = new JMenuItem("Copy ed2k link(s)");
				 copy_ed2k_links.setIcon(ImgRep.getIcon("ed2k_link.png"));
				 copy_ed2k_links.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						clipboard.setContents(new Transferable() {
							public Object getTransferData(DataFlavor flavor)
									throws UnsupportedFlavorException,
									IOException {
								String ed2k_links = "";
								DownloadSession[] download_sessions = getSelectedDownloadSessions();
								for(DownloadSession session : download_sessions) {
									ed2k_links += session.getED2KLink().getAsString() + System.getProperty("line.separator");
								}
								return ed2k_links;
							}

							public DataFlavor[] getTransferDataFlavors() {
								DataFlavor[] data_flavours = new DataFlavor[1];
								data_flavours[0] = DataFlavor.stringFlavor;
								return data_flavours;
							}

							public boolean isDataFlavorSupported(
									DataFlavor flavor) {
							    return flavor.isFlavorTextType();
							}
							
						}, new ClipboardOwner() {

							public void lostOwnership(Clipboard clipboard, Transferable contents) {
								
							}
							
						});
					}
				 });
				 column_setup = new JMenuItem("Column setup");
				 column_setup.setIcon(ImgRep.getIcon("columns_setup.png"));
				 column_setup.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.out.println("Column setup performed");
					}
				 });
				 properties = new JMenuItem("Properties");
				 properties.setIcon(ImgRep.getIcon("info.png"));
				 properties.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						DownloadSession sessions[] = getSelectedDownloadSessions();
						DownloadDetailsDialog ddd = new DownloadDetailsDialog(parent, sessions[0]);
						SwingUtils.setWindowLocationRelativeTo(ddd, parent);
						ddd.setVisible(true);
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
					 
					     case A  :  popup_menu = new JPopupMenu();
					                popup_menu.add(stop_download);
					                popup_menu.add(cancel_download);
					                popup_menu.addSeparator();
					                popup_menu.add(copy_ed2k_links);
					                popup_menu.add(paste_ed2k_links);
					                popup_menu.addSeparator();
					           //     popup_menu.add(column_setup);
					                popup_menu.add(properties);
					                break;
					                
					     case B :   popup_menu = new JPopupMenu();
			                        popup_menu.add(stop_download);
			                        popup_menu.add(cancel_download);
			                        popup_menu.addSeparator();
			                        popup_menu.add(copy_ed2k_links);
			                        popup_menu.add(paste_ed2k_links);
			                        popup_menu.addSeparator();
			                 //       popup_menu.add(column_setup);
			                        popup_menu.add(properties);
			                        break;
			                        
					     case C :   popup_menu = new JPopupMenu();
					                popup_menu.add(start_download);
	                                popup_menu.add(cancel_download);
	                                popup_menu.addSeparator();
	                                popup_menu.add(copy_ed2k_links);
	                                popup_menu.add(paste_ed2k_links);
	                                popup_menu.addSeparator();
	                          //      popup_menu.add(column_setup);
	                                popup_menu.add(properties);
	                                break;
	                                
					     case D :   popup_menu = new JPopupMenu();
			                        popup_menu.add(start_download);
                                    popup_menu.add(cancel_download);
                                    popup_menu.addSeparator();
                                    popup_menu.add(copy_ed2k_links);
                                    popup_menu.add(paste_ed2k_links);
                                    popup_menu.addSeparator();
                             //       popup_menu.add(column_setup);
                                    popup_menu.add(properties);
                                    break;
                                  
					     case E :   popup_menu = new JPopupMenu();
					                //popup_menu.add(copy_ed2k_links);
                                    popup_menu.add(paste_ed2k_links);
                            //        popup_menu.addSeparator();
                            //        popup_menu.add(column_setup);
                                    break;
                                    
					     case F :   popup_menu = new JPopupMenu();
					                popup_menu.add(start_download);
			                        popup_menu.add(stop_download);
			                        popup_menu.add(cancel_download);
			                        popup_menu.addSeparator();
			                        popup_menu.add(copy_ed2k_links);
			                        popup_menu.add(paste_ed2k_links);
			                 //       popup_menu.addSeparator();
			                 //       popup_menu.add(column_setup);
			                        break;
					 }
					 
					 popup_menu.show(e.getComponent(), e.getX(), e.getY());
				 }
			}	
			
		}
		
		this.addMouseListener(new PopupListener());
	}
	
	private DownloadSession[] getDownloadSessionsByIndexes(int[] indexes) {

		DownloadSession[] result = new DownloadSession[indexes.length];
		int k = 0;
		for(int i : indexes) {
			int j = 0;
			for(DownloadSession session : _download_manager.getDownloads()) {
				if( j == this.convertRowIndexToModel(i) ) {
					result[k++] = session;
					break;
				}
				++j;
			}
		}

		return result;
	}
	
	private DownloadSession[] getSelectedDownloadSessions() {
		
		return getDownloadSessionsByIndexes(  this.getSelectedRows() );
	}
	

	/*
	 *  --------------------------------------------------------------------------------------
	 *                    |     STARTED    |         STOPPED    |       STARTED & STOPPED
	 *  ---------------------------------------------------------------------------------------
	 *  ONE_SELECTED      |       A                   C                     *
	 *                    |
	 *  MULTIPLE_SELECTED |       B                   D                     F
	 *                    |
	 *  VOID_LIST         |       *                   *                     *
	 *                    |
	 * 
	 *          E - void list
	 *          * - can't be
	 *          
	 *   Ac1 | Start download
	 *   Ac2 | Stop download
	 *   Ac3 | Cancel download
	 *   Ac4 | Copy ed2k link(s)
	 *   Ac5 | Paste ed2k link(s)
	 *   Ac6 | Column setup
	 *   Ac7 | Details
	 *   
	 *     |  Ac1    Ac2    Ac3    Ac4    Ac5    Ac6    Ac7
	 *  --------------------------------------------------------  
	 *   A |   -     Yes    Yes    Yes    Yes    Yes    Yes
	 *     |
	 *   B |   -     Yes    Yes    Yes    Yes    Yes    Yes
	 *     |
	 *   C |  Yes     -     Yes    Yes    Yes    Yes    Yes
	 *     |
	 *   D |  Yes     -     Yes    Yes    Yes    Yes    Yes
	 *     |
	 *   E |   -      -      -     -     Yes    Yes     -
	 *     |
	 *   F |  Yes    Yes    Yes    Yes    Yes    Yes     -
	 */
	
	private ConditionType whichCondition() {
		
		DownloadSession[] download_sessions = getSelectedDownloadSessions();
		
		if( ( download_sessions.length == 1 ) && 
			( download_sessions[0].getStatus() == DownloadStatus.STARTED ) )
			return ConditionType.A;
		
		if( download_sessions.length > 1) {
			boolean b = true;
			for(DownloadSession session : download_sessions) {
				if( session.getStatus() != DownloadStatus.STARTED ) {
					b = false;
					break;
				}
			}
			if(b) return ConditionType.B;		
		}
		
		if( ( download_sessions.length == 1 ) &&
			( download_sessions[0].getStatus() == DownloadStatus.STOPPED ) )
			return ConditionType.C;
		
		if( download_sessions.length > 1 ) {
			boolean b = true;
			for(DownloadSession session : download_sessions) {
				if( session.getStatus() != DownloadStatus.STOPPED ) { 
					 b = false;
					 break;
				}
			}
			if(b) return ConditionType.D;	
		}
		
		if( download_sessions.length == 0 ) return ConditionType.E;
		
		if( download_sessions.length > 1 ) {
			boolean some_started = false;
			boolean some_stopped = false;
			for(DownloadSession session : download_sessions) {
				if(session.getStatus() == DownloadStatus.STARTED) some_started = true;
				if(session.getStatus() == DownloadStatus.STOPPED) some_stopped = true;
			}
			if( some_started && some_stopped ) 
				return ConditionType.F;
		}
		
		return null;
	}
	
	private enum ConditionType {
		A,B,C,D,E,F
	}
	
}
