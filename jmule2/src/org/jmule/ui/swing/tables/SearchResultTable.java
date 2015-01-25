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
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.table.TableColumnExt;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.searchmanager.SearchManager;
import org.jmule.core.searchmanager.SearchResult;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.core.searchmanager.SearchResultItemList;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.sharingmanager.SharingManager;
import org.jmule.core.utils.GeneralComparator;
import org.jmule.core.utils.Misc;
import org.jmule.ui.UIConstants;
import org.jmule.ui.localizer._;
import org.jmule.ui.swing.ImgRep;
import org.jmule.ui.swing.SwingPreferences;
import org.jmule.ui.swing.maintabs.search.SearchResultPanel;
import org.jmule.ui.swing.models.SearchResultTableModel;
import org.jmule.ui.utils.FileFormatter;


/**
 *
 * Created on Sep 10, 2008
 * @author javajox
 * @version $Revision: 1.4 $
 * Last changed by $Author: javajox $ on $Date: 2009/09/22 19:08:43 $
 */
public class SearchResultTable extends JMTable {

	// =========================== Table cell renderers ============================================
	
	class FileNameTableCellRenderer extends SearchResultTableCellRenderer {		
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			String fn = search_result.getFileName();
            this.setHorizontalAlignment(SwingConstants.LEFT);
            this.setText(" " + fn);
            this.setToolTipText(fn);
            this.setIcon(new ImageIcon(UIConstants.getMimeURLByExtension(Misc.getFileExtension(fn))));
			return this;
		}
	}
	
	class FileSizeTableCellRenderer extends SearchResultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			this.setText(FileFormatter.formatFileSize(search_result.getFileSize()) + " ");
			return this;
		}
	}
	
	class AvailabilityTableCellRenderer extends SearchResultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setText(search_result.getFileAviability()+"");
			return this;
		}
	}
	
	class CompleteSourcesTableCellRenderer extends SearchResultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setText(search_result.getFileCompleteSrc()+"");
			return this;
		}
	}
	
	class TypeTableCellRenderer extends SearchResultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			byte[] fileType = search_result.getMimeType();
			this.setText(" " + FileFormatter.formatMimeType(fileType));
			return this;
		}
	}
	
	class FileIDTableCellRenderer extends SearchResultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			this.setHorizontalAlignment(SwingConstants.LEFT);
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setText(search_result.getFileHash().getAsString());
			return this;
		}
	}
	
	// ================================== end table cell renderers ======================================
	
	private JMuleCore _core = JMuleCoreFactory.getSingleton();
	private SearchManager _search_manager = _core.getSearchManager();
	private DownloadManager _download_manager = _core.getDownloadManager();
	private SharingManager _sharing_manager = _core.getSharingManager();
	private SwingPreferences _pref = SwingPreferences.getSingleton();
	private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	private JFrame parent;
	private SearchResult searchResult;
	private SearchResultItemList search_result_file_list;
	private JTabbedPane tabbedPanel;
	private SearchResultPanel search_result_pane;
	
	public SearchResultTable(JFrame parent, SearchResult searchResult, JTabbedPane tabbedPanel, SearchResultPanel search_result_pane) {
		this.parent = parent;
		this.searchResult = searchResult; 
		this.tabbedPanel = tabbedPanel; 
		this.search_result_pane = search_result_pane; 
		search_result_file_list = this.searchResult.getSearchResultItemList(); 
		init();
	}
	
	private void init() {
		
		TableColumnExt file_name = new TableColumnExt();
		file_name.setIdentifier(UIConstants.SEARCH_FILENAME_COLUMN_ID);
		file_name.setModelIndex(SearchResultTableModel.FILE_NAME);
		file_name.setVisible(_pref.isColumnVisible(UIConstants.SEARCH_FILENAME_COLUMN_ID));
		file_name.setHeaderValue(_._("mainwindow.searchtab.column.filename"));
		file_name.setCellRenderer(new FileNameTableCellRenderer());
		file_name.setComparator(new GeneralComparator("getFileName"));
		
		table_columns.add(file_name);
		
		TableColumnExt file_size = new TableColumnExt();
		file_size.setIdentifier(UIConstants.SEARCH_FILESIZE_COLUMN_ID);
		file_size.setModelIndex(SearchResultTableModel.FILE_SIZE);
		file_size.setVisible(_pref.isColumnVisible(UIConstants.SEARCH_FILESIZE_COLUMN_ID));
		file_size.setHeaderValue(_._("mainwindow.searchtab.column.filesize"));
		file_size.setCellRenderer(new FileSizeTableCellRenderer());
		file_size.setComparator(new GeneralComparator("getFileSize"));
		
		table_columns.add(file_size);
		
		TableColumnExt availability = new TableColumnExt();
		availability.setIdentifier(UIConstants.SEARCH_AVAILABILITY_COLUMN_ID);
		availability.setModelIndex(SearchResultTableModel.AVAILABILITY);
		availability.setVisible(_pref.isColumnVisible(UIConstants.SEARCH_AVAILABILITY_COLUMN_ID));
		availability.setHeaderValue(_._("mainwindow.searchtab.column.availability"));
		availability.setCellRenderer(new AvailabilityTableCellRenderer());
		availability.setComparator(new GeneralComparator("getFileAviability"));
		
		table_columns.add(availability);
		
		TableColumnExt complete_sources = new TableColumnExt();
		complete_sources.setIdentifier(UIConstants.SEARCH_COMPLETESRC_COLUMN_ID);
		complete_sources.setModelIndex(SearchResultTableModel.COMPLETE_SOURCES);
		complete_sources.setVisible(_pref.isColumnVisible(UIConstants.SEARCH_COMPLETESRC_COLUMN_ID));
		complete_sources.setHeaderValue(_._("mainwindow.searchtab.column.completesrcs"));
		complete_sources.setCellRenderer(new CompleteSourcesTableCellRenderer());
		complete_sources.setComparator(new GeneralComparator("getFileCompleteSrc"));
		
		table_columns.add(complete_sources);
		
		TableColumnExt type = new TableColumnExt();
		type.setIdentifier(UIConstants.SEARCH_FILE_TYPE_COLUMN_ID);
		type.setModelIndex(SearchResultTableModel.TYPE);
		type.setVisible(_pref.isColumnVisible(UIConstants.SEARCH_FILE_TYPE_COLUMN_ID));
		type.setHeaderValue(_._("mainwindow.searchtab.column.filetype"));
		type.setCellRenderer(new TypeTableCellRenderer());
		type.setComparator(new Comparator() {
			public int compare(Object o1, Object o2) {
				String file_type1 = FileFormatter.formatMimeType(((SearchResultItem)o1).getMimeType());
				String file_type2 = FileFormatter.formatMimeType(((SearchResultItem)o2).getMimeType());
				return Misc.compareAllObjects(file_type1, file_type2, "toString", true);
			}			
		});
		
		table_columns.add(type);
		
		TableColumnExt file_id = new TableColumnExt();
		file_id.setIdentifier(UIConstants.SEARCH_FILE_ID_COLUMN_ID);
		file_id.setModelIndex(SearchResultTableModel.FILE_ID);
		file_id.setVisible(_pref.isColumnVisible(UIConstants.SEARCH_FILE_ID_COLUMN_ID));
		file_id.setHeaderValue(_._("mainwindow.searchtab.column.fileid"));
		file_id.setCellRenderer(new FileIDTableCellRenderer());
		file_id.setComparator(new GeneralComparator("getFileHash"));
		
		table_columns.add(file_id);
		
		SearchResultTableModel search_result_table_model = new SearchResultTableModel(searchResult);
		//SearchResultTableModel search_result_table_model;
		//SearchResultTableModel.setSearchResult(searchResult);
		//search_result_table_model = new SearchResultTableModel(searchResult);
		super.buildColumns(search_result_table_model);
		
		class PopupListener extends MouseAdapter {
			JMenuItem start_download, try_again, copy_ed2k_links, close,
			          column_setup, properties;
			public PopupListener() {
				start_download = new JMenuItem("Download");
				start_download.setIcon(ImgRep.getIcon("start_download.png"));
				start_download.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent event) {
                         List<SearchResultItem> new_files = getFilesByStatus(FileStatus.NEW);
                         for(SearchResultItem result : new_files) {
                        	try { 
                        	 _download_manager.addDownload(result);
                        	 _download_manager.startDownload(result.getFileHash());
                        	}catch(Throwable cause) {
                        		cause.printStackTrace();
                        	}
                         }
					 }
				});
				try_again = new JMenuItem("Try again");
				try_again.setIcon(ImgRep.getIcon("refresh.png"));
				try_again.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
					     System.out.println("Try again action performed");	
					}
				});
				copy_ed2k_links = new JMenuItem("Copy ED2K link(s)");
				copy_ed2k_links.setIcon(ImgRep.getIcon("ed2k_link.png"));
				copy_ed2k_links.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						clipboard.setContents(new Transferable() {
							public Object getTransferData(DataFlavor flavor)
									throws UnsupportedFlavorException,
									IOException {
								String ed2k_links = "";
								SearchResultItem[] result_items = getSelectedFiles();
								for(SearchResultItem result_item : result_items) {
									ed2k_links += result_item.getAsED2KLink().getAsString() + System.getProperty("line.separator");
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
				close = new JMenuItem("Close");
				close.setIcon(ImgRep.getIcon("cancel.png"));
				close.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						tabbedPanel.remove(search_result_pane);
					}
				});
				column_setup = new JMenuItem("Column setup");
				column_setup.setIcon(ImgRep.getIcon("columns_setup.png"));
				column_setup.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
					    System.out.println("Column setup action performed");	
					}
				});
				properties = new JMenuItem("Properties");
				properties.setIcon(ImgRep.getIcon("info.png"));
				properties.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						System.out.println("Properties action performed");
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
					 
					   case A : popup_menu = new JPopupMenu();
						        popup_menu.add(try_again); 
						        popup_menu.add(copy_ed2k_links);
						        popup_menu.add(close);
						        popup_menu.add(column_setup);
						        popup_menu.add(properties);
						        break;
						        
					   case B : popup_menu = new JPopupMenu();
				                popup_menu.add(try_again); 
				                popup_menu.add(copy_ed2k_links);
				                popup_menu.add(close);
				                popup_menu.add(column_setup);
				                break;
				                
					   case C : popup_menu = new JPopupMenu();
				                popup_menu.add(try_again); 
				                popup_menu.add(copy_ed2k_links);
				                popup_menu.add(close);
				                popup_menu.add(column_setup);
				                popup_menu.add(properties);
				                break; 
				                
					   case D : popup_menu = new JPopupMenu();
		                        popup_menu.add(try_again); 
		                        popup_menu.add(copy_ed2k_links);
		                        popup_menu.add(close);
		                        popup_menu.add(column_setup);
		                        break;
		               
					   case E : popup_menu = new JPopupMenu();
					            popup_menu.add(start_download);
                                popup_menu.add(try_again); 
                                popup_menu.add(copy_ed2k_links);
                                popup_menu.add(close);
                                popup_menu.add(column_setup);
                                break;
                                
					   case F : popup_menu = new JPopupMenu();
			                    popup_menu.add(start_download);
                                popup_menu.add(try_again); 
                                popup_menu.add(copy_ed2k_links);
                                popup_menu.add(close);
                                popup_menu.add(column_setup);
                                break;
                                
					   case G : popup_menu = new JPopupMenu();
			                    popup_menu.add(start_download);
                                popup_menu.add(try_again); 
                                popup_menu.add(copy_ed2k_links);
                                popup_menu.add(close);
                                popup_menu.add(column_setup);
                                break;
                                
					   case H : popup_menu = new JPopupMenu();
	                            popup_menu.add(start_download);
                                popup_menu.add(try_again); 
                                popup_menu.add(copy_ed2k_links);
                                popup_menu.add(close);
                                popup_menu.add(column_setup);
                                popup_menu.add(properties);
                                break;
                                
					   case I : popup_menu = new JPopupMenu();
                                popup_menu.add(start_download);
                                popup_menu.add(try_again); 
                                popup_menu.add(copy_ed2k_links);
                                popup_menu.add(close);
                                popup_menu.add(column_setup);
                                break;
                                
					   case J : popup_menu = new JPopupMenu();
					            popup_menu.add(close);
					            popup_menu.add(column_setup);
					            break;
						   
					 }
					 popup_menu.show(e.getComponent(), e.getX(), e.getY());
				 }
			}
		}
		this.addMouseListener(new PopupListener());
	}
	
	private SearchResultItem[] getSelectedFiles() {
		
		return getFilesByIndexes( this.getSelectedRows() );
	}
	
	private SearchResultItem[] getFilesByIndexes(int[] indexes) {
		SearchResultItem[] result = new SearchResultItem[indexes.length];
		int k = 0;
		for(int i : indexes) {
			int j = 0;
			for(SearchResultItem file : search_result_file_list) {
				if( j == this.convertRowIndexToModel(i) ) {
					result[k++] = file;
					break;
				}
				++j;
			}
		}
		return result;
	}
	
	/*
	 * =========================================================================================================================================================
	 * |                   |  Downloading  | On Shared  |  Downloading & New files  |  Shared & New files  |  Shared & Downloading & New files  |  New Files   |
	 * =========================================================================================================================================================
	 * |                                                                                                                                                       |
	 * | ONE_SELECTED           A               C                   X                         X                         X                             H        |
	 * |                                                                                                                                                       |
	 * |-------------------------------------------------------------------------------------------------------------------------------------------------------|
	 * |                                                                                                                                                       |
	 * | MULTIPLE_SELECTED      B               D                   E                         F                         G                             I        |
	 * |                                                                                                                                                       |
	 * |--------------------------------------------------------------------------------------------------------------------------------------------------------
	 *    J - no search results
	 *    A,B,C,D,E,F,G,H,I,J - conditions
	 *    X - can't be
	 *    
	 *   Start download Ac1
	 *   Try again Ac2
	 *   Copy ED2K Links Ac3
	 *   Close Ac5
	 *   Column setup Ac6
	 *   Properties Ac7
	 * 
	 *     |  Ac1   Ac2   Ac3   Ac5   Ac6   Ac7
	 * --------------------------------------------      
	 *   A |  -     yes   yes   yes   yes   yes
	 *     |
	 *   B |  -     yes   yes   yes   yes   -
	 *     |
	 *   C |  -     yes   yes   yes   yes   yes
	 *     |
	 *   D |  -     yes   yes   yes   yes   -
	 *     |
	 *   E |  yes   yes   yes   yes   yes   -
	 *     |
	 *   F |  yes   yes   yes   yes   yes   -
	 *     |
	 *   G |  yes   yes   yes   yes   yes   -
	 *     |
	 *   H |  yes   yes   yes   yes   yes   yes
	 *     |
	 *   I |  yes   yes   yes   yes   yes   -
	 *     |
	 *   J |  -     -     -     yes   yes   - 
	 * 
	 */
	
	private ConditionType whichCondition() {

		int[] selected_rows = this.getSelectedRows();
		SearchResultItem[] results = getSelectedFiles();
		
		if( ( selected_rows.length == 1 ) && _download_manager.hasDownload(results[0].getFileHash()) ) return ConditionType.A;
		if( selected_rows.length > 1 ) {
			boolean all_are_downloading = true;
			for(SearchResultItem result : results) {
				if( !_download_manager.hasDownload(result.getFileHash()) ) {
					all_are_downloading = false;
					break;
				}
			}
			if(all_are_downloading) return ConditionType.B;
		}
		if( ( selected_rows.length == 1 ) && _sharing_manager.hasFile(results[0].getFileHash()) ) return ConditionType.C;
		if( selected_rows.length > 1 ) {
			boolean all_are_shared = true;
			for(SearchResultItem result : results) {
				if( !_sharing_manager.hasFile(result.getFileHash()) ) {
					all_are_shared = false;
					break;
				}
			}
			if(all_are_shared) return ConditionType.D;
		}
		if( selected_rows.length > 1 ) {
			boolean some_downloading = false;
			boolean some_new = false;
			for(SearchResultItem result : results) {
				if( _download_manager.hasDownload(result.getFileHash()) ) some_downloading = true;
				if( !_sharing_manager.hasFile(result.getFileHash()) &&
					!_download_manager.hasDownload(result.getFileHash())) some_new = true;
			}
			if( some_downloading && some_new ) return ConditionType.E;
		}
		if( selected_rows.length > 1 ) {
			boolean some_shared = false;
			boolean some_new = false;
			for(SearchResultItem result : results) {
				if( _sharing_manager.hasFile(result.getFileHash()) ) some_shared = true;
				if( !_sharing_manager.hasFile(result.getFileHash()) &&
					!_download_manager.hasDownload(result.getFileHash())) some_new = true;
			}
			if( some_shared && some_new ) return ConditionType.F;
		}
		if( selected_rows.length > 1 ) {
			boolean some_shared = false;
			boolean some_downloading = false;
			boolean some_new = false;
			for(SearchResultItem result : results) {
				if( _sharing_manager.hasFile(result.getFileHash()) ) some_shared = true;
				if( _download_manager.hasDownload(result.getFileHash()) ) some_downloading = true;
				if( !_sharing_manager.hasFile(result.getFileHash()) &&
					!_download_manager.hasDownload(result.getFileHash()) ) some_new = true;
			}
			if( some_shared && some_downloading && some_new ) return ConditionType.G;
		}
		if( ( selected_rows.length == 1 ) && ( !_sharing_manager.hasFile(results[0].getFileHash()) &&
				                               !_download_manager.hasDownload(results[0].getFileHash())) ) return ConditionType.H;
		if( selected_rows.length > 1 ) {
			boolean all_are_new = true;
			for(SearchResultItem result : results) {
				if( _sharing_manager.hasFile(result.getFileHash()) || 
				    _download_manager.hasDownload(result.getFileHash())) {
					all_are_new = false;
					break;
				}
			}
			if( all_are_new ) return ConditionType.I;
		}
		if( this.getRowCount() == 0 ) return ConditionType.J;
		
		return null;
	}
	
	public List<SearchResultItem> getFilesByStatus(FileStatus fileStatus) {
		List<SearchResultItem> result = new LinkedList<SearchResultItem>();
		SearchResultItem[] selected_results = getSelectedFiles();
		for(SearchResultItem search_result_item : selected_results) {
		  switch(fileStatus) {
		     case  SHARING   :  if(_sharing_manager.hasFile(search_result_item.getFileHash()))
		    	                	 result.add(search_result_item); break;
		     case DOWNLOADING:  if(_download_manager.hasDownload(search_result_item.getFileHash()))
			                    	 result.add(search_result_item); break;
		     case NEW :         if(!_download_manager.hasDownload(search_result_item.getFileHash()) &&
			                       !_sharing_manager.hasFile(search_result_item.getFileHash()))
			                    	 result.add(search_result_item); break;
		  }
		}
		return result;
	}
	
	private enum FileStatus {
		SHARING,
		DOWNLOADING,
		NEW
	}
	
	private enum ConditionType {
		A,B,C,D,E,F,G,H,I,J
	}
	
}
