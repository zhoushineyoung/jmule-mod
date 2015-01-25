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
package org.jmule.ui.swing.maintabs.shared;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileSystemView;

import org.jmule.core.JMThread;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.sharingmanager.SharingManager;
import org.jmule.core.utils.FileUtils;
import org.jmule.ui.swing.ImgRep;
import org.jmule.ui.swing.Refreshable;
import org.jmule.ui.swing.SwingGUIUpdater;
import org.jmule.ui.swing.maintabs.AbstractTab;
import org.jmule.ui.swing.tables.SharedFilesTable;
import org.jmule.ui.swing.wizards.ChosenFolders;
import org.jmule.ui.swing.wizards.ExistedFoldersDialog;
import org.jmule.ui.utils.NumberFormatter;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2009/09/22 19:08:43 $$
 */
public class SharedTab extends AbstractTab {

	JMuleCore _core = JMuleCoreFactory.getSingleton();
	SharingManager _sharing_manager = _core.getSharingManager();
	ConfigurationManager _config_manager = _core.getConfigurationManager();
	SwingGUIUpdater _updater = SwingGUIUpdater.getInstance(); 
	
	private enum FilesCategory {
		INCOMING_FILES { public String toString() { return "Incoming files"; } },
		INCOMPLETE_FILES { public String toString() { return "Incomplete files"; } },
		ALL_FILES { public String toString() { return "All files"; } }
	}
	
	class SharedFoldersListModel extends AbstractListModel {
		
		public SharedFoldersListModel() {
			_config_manager.addConfigurationListener(new ConfigurationAdapter() {
				public void sharedDirectoriesChanged(List<File> sharedDirs) {
					fireContentsChanged(this,2,sharedDirs.size()-1);
				}
			});
		}
		
		public Object getElementAt(int index) {
            if(index == 0) return FilesCategory.INCOMING_FILES;
            if(index == 1) return FilesCategory.INCOMPLETE_FILES;
            if(index == 2) return FilesCategory.ALL_FILES;
            Object result = null;
            try {
               result = _config_manager.getSharedFolders().get(index-3);
            }catch( Throwable cause ) {
            	cause.printStackTrace();
            }
			return result;
		}

		public int getSize() {
		   int result = 0;
		   try {	
			 if(_config_manager.getSharedFolders() == null) result = 3;
			 result = _config_manager.getSharedFolders().size() + 3;
		   }catch( Throwable cause ) {
			   cause.printStackTrace();
		   }
		   return result;
		}
		
	}
	
	class SharedFoldersListCellRenderer extends JLabel implements ListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if(value instanceof String) this.setText((String)value);
			else if(value instanceof File) {
				this.setFont(new java.awt.Font("Dialog", 0, 12));
				this.setText(((File)value).getAbsolutePath());
			} else if(value instanceof FilesCategory) {
				FilesCategory files_category = (FilesCategory)value;
				this.setFont(new java.awt.Font("Dialog", 1, 12));
				this.setText(files_category.toString());
			}
			setEnabled(list.isEnabled());
			//setFont(list.getFont());
		    setOpaque(true);
			if (isSelected) {
		         setBackground(list.getSelectionBackground());
			     setForeground(list.getSelectionForeground());
			} else {
			  	// if ( (index % 2) == 0 )
			    //  this.setBackground(new Color(255,255,255));
			   //  else
			   //    this.setBackground(new Color(240,240,240));
			       setBackground(list.getBackground());
			       setForeground(list.getForeground());
			}
			return this;
		}		
	}
	
	class TopPanel extends JPanel {
		
		private JButton add_button = new JButton("Add");
		private JButton remove_button = new JButton("Remove");
		private JButton rescan_button = new JButton("Rescan");
		private JButton clear_button = new JButton("Clear");
		
		public TopPanel() {
			init();
		}
		
		private void init() {
			GridBagLayout thisLayout = new GridBagLayout();
			this.setPreferredSize(new java.awt.Dimension(467, 56));
			thisLayout.rowWeights = new double[] {0.1};
			thisLayout.rowHeights = new int[] {7};
			thisLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
			thisLayout.columnWidths = new int[] {6, 107, 108, 107, 122, 7};
			this.setLayout(thisLayout);
			this.add(add_button, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
            this.add(remove_button, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
            this.add(clear_button, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
            this.add(rescan_button, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
            add_button.setIcon(ImgRep.getIcon("add.png"));
            remove_button.setIcon(ImgRep.getIcon("remove.png"));
            clear_button.setIcon(ImgRep.getIcon("remove_all.png"));
            rescan_button.setIcon(ImgRep.getIcon("refresh.png"));
		}
		
		public JButton getAddButton() {
			
			return add_button;
		}
		
		public JButton getRemoveButton() {
			
			return remove_button;
		}
		
		public JButton getClearButton() {
			
			return clear_button;
		}
		
		public JButton getRescanButton() {
			
			return rescan_button;
		}
	}
	
	private JSplitPane split_pane;
	private TopPanel top_panel;
	private JScrollPane shared_folders_scroll_pane;
	private JScrollPane shared_files_scroll_pane;
	private JList shared_folders_list;
	private TitledBorder shared_folders_border;
	private TitledBorder shared_files_border;
	private SharedFoldersListModel shared_folders_list_model;
	private SharedFilesTable shared_files_table;
	
    private JFileChooser file_chooser;
    private FileSystemView file_system_view;
    
 // current chosen folders 
    private File[] current_chosen_folders;
    // all folders chosen during this session
    private ChosenFolders chosen_folders;
	//private JDialog parent;
	
	public SharedTab(final JFrame parent) {
		super(parent);
		init();
		file_system_view = FileSystemView.getFileSystemView(); 
        file_chooser = new JFileChooser( file_system_view );
        file_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        file_chooser.setMultiSelectionEnabled(true);
        
        file_chooser.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		if(event.getActionCommand() == JFileChooser.APPROVE_SELECTION) {
        			chosen_folders = new ChosenFolders();
        	        
        			List<File> shared_folders = null;
        			try {
        	           shared_folders = _config_manager.getSharedFolders();
        			}catch( Throwable cause ) {
        				cause.printStackTrace();
        			}
        	        
        	        if(shared_folders != null) {      	
        	        	for(File folder : shared_folders) {
        	        	    chosen_folders.add(folder);	
        	        	}
        	        }
        			current_chosen_folders = file_chooser.getSelectedFiles();
        			LinkedList<File> newFolders = new LinkedList<File>();
        			final List<File> already_existed_folders = 
        				org.jmule.core.utils.FileUtils.extractNewFolders(current_chosen_folders, 
        						                                         chosen_folders.getFoldersList(), 
        						                                         newFolders);
        			for(File file : newFolders)
        				chosen_folders.add(file);
        			if(already_existed_folders.size() != 0) {
        			   ExistedFoldersDialog existed_folders_dialog = new ExistedFoldersDialog(parent, true, new AbstractListModel() {
        				   public int getSize() { 
        					
        					   return already_existed_folders.size();
        				   }
        				   public Object getElementAt(int i) {
        					
        					   return already_existed_folders.get( i );
        				   }
        			   });
        			   existed_folders_dialog.setVisible(true);
        			}
        			//List<File> shared_folders2 = _config_manager.getSharedFolders();
        			//if(shared_folders2 == null) shared_folders2 = new LinkedList<File>();
                    List<File> folders_for_config_mg = new LinkedList<File>();
        			for(File file : chosen_folders) 
        				folders_for_config_mg.add(file);
        			try {
        			   _config_manager.setSharedFolders(folders_for_config_mg);
        			}catch( Throwable cause ) {
        				cause.printStackTrace();
        			}
        		}
        	}
        });
        final SharedTab shared_tab = this;
        top_panel.getAddButton().addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		file_chooser.showDialog(shared_tab,"Choose");	
        	}
        });
        top_panel.getRemoveButton().addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		Object object = shared_folders_list.getSelectedValue();
        		if(object instanceof File) {
        		   try {	 
        			 List<File> shared_folders = _config_manager.getSharedFolders();
                     shared_folders.remove(object);
                     _config_manager.setSharedFolders(shared_folders);
        		   }catch( Throwable cause ) {
        			   cause.printStackTrace();
        		   }
        		}
        	}
        });
        top_panel.getClearButton().addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		try {
        		  _config_manager.setSharedFolders(null);
        		}catch( Throwable cause ) {
        			cause.printStackTrace();
        		}
        	}
        });
        top_panel.getRescanButton().addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		_sharing_manager.loadCompletedFiles();
        	}
        });
       // SwingGUIUpdater.getInstance().addRefreshable(new Refreshable() {
        //	public void refresh() {
        //		shared_folders_list.repaint();
        //	}
        //});
        _updater.addRefreshable(new Refreshable() {
        	 public void refresh() {
        		String str;
        		SharedFile shared_file = _sharing_manager.getCurrentHashingFile();
        		double percent_complete = 0;
        	    if(shared_file != null) {
        		    percent_complete = _sharing_manager.getCurrentHashingFilePercent();
        		    str = " [Hashing : " + shared_file.getSharingName() + " (" + NumberFormatter.formatProgress(percent_complete) + ")]";
        		    shared_files_border.setTitle(nr_of_files + str);
            		//shared_files_border.setTitle(current_title + str);
        		    //setSharedFilesBorderTitle(current_title  + str);
        		} else {
        	       shared_files_border.setTitle(nr_of_files);
        		}
        		shared_files_scroll_pane.repaint();
        	 }
        });
	}
	
	private String nr_of_files = "";
	
	private void init() {
		
		split_pane = new JSplitPane();
		shared_folders_scroll_pane = new JScrollPane();
		shared_files_scroll_pane = new JScrollPane();
		shared_folders_list = new JList();
		shared_folders_list_model = new SharedFoldersListModel();
		top_panel = new TopPanel();
		
		
		shared_folders_list.setModel(shared_folders_list_model);
		shared_folders_list.setCellRenderer(new SharedFoldersListCellRenderer());
		
		shared_folders_border = javax.swing.BorderFactory.createTitledBorder("");
		shared_files_border = javax.swing.BorderFactory.createTitledBorder(""); 
		
		
		
		shared_folders_scroll_pane.setBorder(shared_folders_border);
		shared_files_scroll_pane.setBorder(shared_files_border);
		
		shared_folders_scroll_pane.setViewportView(shared_folders_list);
		
		split_pane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		split_pane.setTopComponent(shared_folders_scroll_pane);
		split_pane.setBottomComponent(shared_files_scroll_pane);
		
		this.setLayout(new BorderLayout());
		this.add(top_panel, BorderLayout.NORTH);
		this.add(split_pane, BorderLayout.CENTER);
		
		class FileScanner extends JMThread {
			
			private boolean stop = false;
			private Object object;
			
			public void JMStop() {
				stop = true;
				interrupt();
			}
		
			public FileScanner() {

			}
			
			public void setSharedFolder(Object object) {
				this.object = object;
			}
			
			public void run() {
				//if( shared_files_scroll_pane. )
				final List<SharedFile> shared_files = new CopyOnWriteArrayList<SharedFile>();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
					   //if(shared_files_scroll_pane.getViewport().getComponentCount() !=0 ) {
						//	shared_files_scroll_pane.remove(1);
					  // }
					   shared_files_table = new SharedFilesTable(shared_files);
					   shared_files_scroll_pane.setViewportView(shared_files_table);
					}
				});
				//Iterator<File> i = null;
				List<File> list_of_files = null;
				boolean is_iterator = false;
				if(object instanceof FilesCategory) {
					FilesCategory files_category = (FilesCategory)object;
					switch(files_category) {
					    case ALL_FILES          : for(SharedFile shared_file : _sharing_manager.getSharedFiles())
					    	                           shared_files.add(shared_file);
					                              break;
					    case INCOMPLETE_FILES   : for(PartialFile partial_file : _sharing_manager.getPartialFiles())
					    	                            shared_files.add(partial_file);
					    	                      break;  
					    case INCOMING_FILES     : list_of_files = FileUtils.traverseDirAndReturnListOfFiles( new File(ConfigurationManager.INCOMING_DIR ));
					    	                      //i = FileUtils.iterateFiles(new File(ConfigurationManager.INCOMING_DIR), null, true);
					                              is_iterator = true;
					}
				} else if( object instanceof File ) {
					//i = FileUtils.iterateFiles((File)object, null, true);
					list_of_files = FileUtils.traverseDirAndReturnListOfFiles( (File)object );
					is_iterator = true;
				}
				
				if(is_iterator) {
				  //while(i.hasNext()) {
					for(File file : list_of_files) {
					  if(stop) return;
					 // File file = i.next();
					  if(file.isDirectory()) continue;
					  SharedFile shared_file = _sharing_manager.getSharedFile(file);
					  if(shared_file != null) shared_files.add(shared_file);
					}
				  //}
				}
				nr_of_files = "Shared files(" + shared_files.size() + ")";
		    }
		}
		
		//final FileScanner file_scanner = new FileScanner();
		
		shared_folders_list.addMouseListener(new MouseAdapter() {
			 public void mouseClicked(MouseEvent e) {
				  if(scanner != null && scanner.isAlive()) {
					  scanner.JMStop();
					  scanner = null;
				  }	
				  Object object = shared_folders_list.getSelectedValue();
				  //File file = (File)shared_folders_list.getSelectedValue();
                  FileScanner file_scanner = new FileScanner();
				  file_scanner.setSharedFolder(object);
				  file_scanner.start();
				  scanner = file_scanner;
				 // SharedFilesTable shared_files_table = new SharedFilesTable()
			 }
		});
	}
	
	JMThread scanner;
	
}
