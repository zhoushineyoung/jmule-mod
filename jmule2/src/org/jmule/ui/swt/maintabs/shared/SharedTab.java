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
package org.jmule.ui.swt.maintabs.shared;

import java.io.File;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jmule.core.JMIterable;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMThread;
import org.jmule.core.JMuleCore;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationListener;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.sharingmanager.CompletedFile;
import org.jmule.core.sharingmanager.FileQuality;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.sharingmanager.SharingManager;
import org.jmule.core.utils.FileUtils;
import org.jmule.core.utils.Misc;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.GUIUpdater;
import org.jmule.ui.swt.Refreshable;
import org.jmule.ui.swt.SWTConstants;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.common.SashControl;
import org.jmule.ui.swt.maintabs.AbstractTab;
import org.jmule.ui.swt.skin.SWTSkin;
import org.jmule.ui.swt.tables.JMTable;
import org.jmule.ui.utils.FileFormatter;

/**
 * @author binary256
 * @version $$Revision: 1.16 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/01/05 17:39:15 $$
 */
public class SharedTab extends AbstractTab {

	private int no_remove_id_start = 0;
	private int no_remove_id_end = 2;
	
	private JMTable<SharedFile> shared_files_table;
	
	private JMuleCore _core;
	private SharingManager sharing_manager;
	private ConfigurationManager config_manager;
	
	private List shared_dir_list;
	
	private Button remove_button;
	private Button clear_button;
	
	private Menu no_items_menu;
	private Menu select_completed_file_menu;
	private MenuItem properties_menu_item;
	private MenuItem open_selected_menu_item;
	private MenuItem copy_ed2k_link_menu_item;
	private MenuItem remove_from_disk_menu_item;
	
	private Menu rating_menu;
	private MenuItem rating_menu_item;
	private MenuItem file_not_rated;
	private MenuItem file_fake;
	private MenuItem file_poor;
	private MenuItem file_fair;
	private MenuItem file_good;
	private MenuItem file_excellent;
	
	private String last_selection = "";
	
	private Refreshable refreshable;
	
	private Group shared_files_group;
	private Group dir_list_content;
	
	private ConfigurationListener config_listener;
	
	public SharedTab(Composite shell,JMuleCore core) {
		super(shell);
		
		_core = core;
		sharing_manager = _core.getSharingManager();
		config_manager = _core.getConfigurationManager();
		SWTSkin skin = null;
		try {
		    skin = (SWTSkin)JMuleUIManager.getJMuleUI().getSkin();
		}catch(Throwable t) {}
		
		setLayout(new FormLayout());
		
		Composite dir_list = new Composite(this,SWT.NONE);
		Composite shared_files = new Composite(this,SWT.NONE);
		
		SashControl.createHorizontalSash(50, 50, this, dir_list, shared_files);
		
		GridLayout layout;
		
		dir_list.setLayout(new FillLayout());
		dir_list_content = new Group(dir_list,SWT.NONE);
		java.util.List<File> dir = null;
		try {
			dir = _core.getConfigurationManager().getSharedFolders();
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
		}
		if (dir != null)
			dir_list_content.setText(_._("mainwindow.sharedtab.group.shared_dirs")+"("+dir.size()+")");
		else
			dir_list_content.setText(_._("mainwindow.sharedtab.group.shared_dirs")+"(0)");
		
		config_listener = new ConfigurationAdapter() {
			public void sharedDirectoriesChanged(java.util.List<File> sharedDirs) {
				dir_list_content.setText(_._("mainwindow.sharedtab.group.shared_dirs")+"("+sharedDirs.size()+")");
			}
		};
		
		layout = new GridLayout(1,false);
		layout.marginWidth  = 2;
		layout.marginHeight = 2;
		dir_list_content.setLayout(layout);
		
		Composite control_block = new Composite(dir_list_content,SWT.NONE);
		layout = new GridLayout(4,false);
		layout.marginHeight = 0;
		layout.marginWidth  = 0;
		control_block.setLayout(layout);
		GridData data = new GridData();
		data.widthHint = 400;
		control_block.setLayoutData(data);
		
		Button add_button = new Button(control_block,SWT.NONE);
		add_button.setFont(skin.getButtonFont());
		add_button.setImage(SWTImageRepository.getImage("add.png"));
		add_button.setText(_._("mainwindow.sharedtab.button.add"));
		add_button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		add_button.setAlignment(SWT.LEFT);
		add_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dir_dialog = new DirectoryDialog (getShell(),SWT.MULTI | SWT.OPEN);
				String directory = dir_dialog.open();
				if (directory == null) return ;
				java.util.List<File> shared_dirs = null;
				try {
					shared_dirs = config_manager.getSharedFolders();
				} catch (ConfigurationManagerException e) {
					e.printStackTrace();
				}
				java.util.List<File> newDirs = new LinkedList<File>();
				if (shared_dirs == null)
					shared_dirs = new CopyOnWriteArrayList<File>();
				else
					shared_dirs = new CopyOnWriteArrayList<File>(shared_dirs);
				
				java.util.List<File> already_exist_list = org.jmule.core.utils.FileUtils.extractNewFolders(new File[]{new File(directory)},shared_dirs,newDirs);
									 
				if (already_exist_list.size()!=0) {
					AlreadyExistDirsWindow window = new AlreadyExistDirsWindow(already_exist_list);
					window.getCoreComponents();
					window.initUIComponents();
				} 
				shared_dirs.addAll(newDirs);
				try {
					config_manager.setSharedFolders(shared_dirs);
				} catch (ConfigurationManagerException e) {
					e.printStackTrace();
				}
		} });
		
		
		remove_button = new Button(control_block,SWT.NONE);
		remove_button.setFont(skin.getButtonFont());
		remove_button.setText(_._("mainwindow.sharedtab.button.remove"));
		remove_button.setImage(SWTImageRepository.getImage("remove.png"));
		remove_button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		remove_button.setAlignment(SWT.LEFT);
		remove_button.setEnabled(false);
		remove_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				removeSelectedDir();
		} });
		
		clear_button = new Button(control_block,SWT.NONE);
		clear_button.setFont(skin.getButtonFont());
		clear_button.setText(_._("mainwindow.sharedtab.button.clear"));
		clear_button.setImage(SWTImageRepository.getImage("remove_all.png"));
		clear_button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		clear_button.setAlignment(SWT.LEFT);
		clear_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				clearDirList();
		} });
		
		Button reload_button = new Button(control_block,SWT.NONE);
		reload_button.setFont(skin.getButtonFont());
		reload_button.setText(_._("mainwindow.sharedtab.button.reload"));
		reload_button.setImage(SWTImageRepository.getImage("refresh.png"));
		reload_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				sharing_manager.loadCompletedFiles();
			}
		});
		
		shared_dir_list = new List (dir_list_content, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		shared_dir_list.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		shared_dir_list.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String selected_dir = shared_dir_list.getSelection()[0];
				if (selected_dir.equals(last_selection)) return ;
				last_selection = selected_dir;
				updateFileList(selected_dir);
				
				int selection_id = shared_dir_list.getSelectionIndex();
				if ((selection_id>=no_remove_id_start)&&(selection_id<=no_remove_id_end)) {
					remove_button.setEnabled(false);
					return;
				}
				remove_button.setEnabled(true);
			}
		});

		shared_files.setLayout(new FillLayout());
		shared_files_group = new Group(shared_files,SWT.NONE);
		shared_files_group.setText(_._("mainwindow.sharedtab.group.shared_files"));
		shared_files_group.setLayout(new FillLayout());
		shared_files_table = new JMTable<SharedFile>(shared_files_group, SWT.NONE){
			protected int compareObjects(SharedFile object1, SharedFile object2,
					int columnID, boolean order) {
				if (columnID == SWTConstants.SHARED_LIST_FILE_NAME_COLUMN_ID) {
					return Misc.compareAllObjects(object1, object2, "getSharingName", order);
				}
				
				if (columnID == SWTConstants.SHARED_LIST_FILE_SIZE_COLUMN_ID) {
					return Misc.compareAllObjects(object1, object2, "length", order);
				}
				
				if (columnID == SWTConstants.SHARED_LIST_FILE_TYPE_COLUMN_ID) {
					String type1 = FileFormatter.formatMimeType(object1.getMimeType());
					String type2 = FileFormatter.formatMimeType(object2.getMimeType());
					int result = type1.compareTo(type2);
					if (order)
						return result;
					else
						return Misc.reverse(result);
				}
				
				if (columnID == SWTConstants.SHARED_LIST_COMPLETED_COLUMN_ID) {
					double value1 = 100;
					double value2 = 100;
					if (object1 instanceof PartialFile)
						value1 = ((PartialFile)object1).getPercentCompleted();
					if (object2 instanceof PartialFile)
						value2 = ((PartialFile)object2).getPercentCompleted();
					int result = 0;
					if (value1>value2)
						result = 1;
					if (value2>value1)
						result = -1;
					if (order) 
						return result;
					else
						return Misc.reverse(result);
				}
				
				if (columnID == SWTConstants.SHARED_LIST_FILE_ID_COLUMN_ID) {
					String hash1 = object1.getFileHash().getAsString();
					String hash2 = object2.getFileHash().getAsString();
					int result = hash1.compareTo(hash2);
					if (order) 
						return result;
					else
						return Misc.reverse(result);
					
				}
				
				return 0;
			}

			protected Menu getPopUpMenu() {
				if (getSelectionCount()<1)
					return no_items_menu;
				
				SharedFile shared_file = shared_files_table.getSelectedObject();
				if (shared_file.isCompleted()) {
					rating_menu.setEnabled(true);
					rating_menu.setVisible(true);
					file_not_rated.setSelection(false);
					file_fake.setSelection(false);
					file_poor.setSelection(false);
					file_fair.setSelection(false);
					file_good.setSelection(false);
					file_excellent.setSelection(false);
					
					switch (shared_file.getFileQuality()) {
						case FAKE : file_fake.setSelection(true); break;
						case POOR : file_poor.setSelection(true); break;
						case FAIR : file_fair.setSelection(true); break;
						case GOOD : file_good.setSelection(true); break;
						case EXCELLENT : file_excellent.setSelection(true); break;
						default : file_not_rated.setSelection(true); break;
					}
					
				} else  { rating_menu.setEnabled(false); }
				
				
				
				properties_menu_item.setEnabled(true);
				copy_ed2k_link_menu_item.setEnabled(true);
				remove_from_disk_menu_item.setEnabled(true);
				if (getSelectedObject().getHashSet() == null) {
					properties_menu_item.setEnabled(false);
					copy_ed2k_link_menu_item.setEnabled(false);
					remove_from_disk_menu_item.setEnabled(false);
				}
				if (getSelectionCount()>1)
					properties_menu_item.setEnabled(false);
				open_selected_menu_item.setEnabled(false);
				if (selectedRunnableFiles())
					open_selected_menu_item.setEnabled(true);
				return select_completed_file_menu;
			}

			int HASHED 		= 0x01;
			int UNHASHED 	= 0x02;
			int HASHING		= 0x03;
			
			public void updateRow(SharedFile object) {
				int status = HASHED;
				
				java.util.List<CompletedFile> file_list = sharing_manager.getUnhashedFiles();
				if (file_list != null) {
					for(CompletedFile unhashed_file : file_list) {
						if (unhashed_file.getFile().equals(object.getFile())) {
							status = UNHASHED;
							break;
						}
					}
				}
				
				SharedFile hashing_file = sharing_manager.getCurrentHashingFile();
				if (hashing_file != null)
					if (object.getFile().equals(hashing_file.getFile()))
						status = HASHING;

				Image icon = SWTImageRepository.getIconByExtension(object.getSharingName());
				setRowImage(object, SWTConstants.SHARED_LIST_FILE_NAME_COLUMN_ID, icon);
				
				String text = object.getSharingName();
				
				setRowText(object,  SWTConstants.SHARED_LIST_FILE_NAME_COLUMN_ID, text);
				long file_size = object.getFile().length();
				setRowText(object, SWTConstants.SHARED_LIST_FILE_SIZE_COLUMN_ID, FileFormatter.formatFileSize(file_size));
				String mime_type = FileFormatter.formatMimeType(object.getMimeType());
				setRowText(object,SWTConstants.SHARED_LIST_FILE_TYPE_COLUMN_ID,mime_type);
				double percent_completed = 100d;
				if (object instanceof PartialFile) {
					PartialFile partial_file = (PartialFile)object;
					percent_completed = partial_file.getPercentCompleted();
				}
				if (status == HASHED) {
				    setRowText(object,SWTConstants.SHARED_LIST_COMPLETED_COLUMN_ID,FileFormatter.formatProgress(percent_completed));
				    setRowText(object,SWTConstants.SHARED_LIST_FILE_ID_COLUMN_ID, object.getFileHash().getAsString());
				} else
					if (status == HASHING)
						setRowText(object,SWTConstants.SHARED_LIST_FILE_ID_COLUMN_ID, _._("mainwindow.sharedtab.label.hashing"));
					else
						setRowText(object,SWTConstants.SHARED_LIST_FILE_ID_COLUMN_ID, _._("mainwindow.sharedtab.label.waiting_to_hash"));
			}
		
		};

	  int width;
	  
	  width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SHARED_LIST_FILE_NAME_COLUMN_ID);
	  shared_files_table.addColumn(SWT.LEFT,  SWTConstants.SHARED_LIST_FILE_NAME_COLUMN_ID, _._("mainwindow.sharedtab.column.filename"), "", width);
	  
	  width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SHARED_LIST_FILE_SIZE_COLUMN_ID);
	  shared_files_table.addColumn(SWT.RIGHT, SWTConstants.SHARED_LIST_FILE_SIZE_COLUMN_ID, _._("mainwindow.sharedtab.column.filesize"), "", width);
	  
	  width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SHARED_LIST_FILE_TYPE_COLUMN_ID);
	  shared_files_table.addColumn(SWT.LEFT,  SWTConstants.SHARED_LIST_FILE_TYPE_COLUMN_ID, _._("mainwindow.sharedtab.column.filetype"), "",width);
	  
	  width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SHARED_LIST_FILE_ID_COLUMN_ID);
	  shared_files_table.addColumn(SWT.LEFT,  SWTConstants.SHARED_LIST_FILE_ID_COLUMN_ID,   _._("mainwindow.sharedtab.column.fileid"), "", width);
	  
	  width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SHARED_LIST_COMPLETED_COLUMN_ID);
	  shared_files_table.addColumn(SWT.RIGHT,  SWTConstants.SHARED_LIST_COMPLETED_COLUMN_ID, _._("mainwindow.sharedtab.column.completed"), "", width);
	  
	  shared_files_table.updateColumnSettings();
	  
	  no_items_menu = new Menu(shared_files_table);
	  
	  MenuItem column_setup_menu_item = new MenuItem(no_items_menu,SWT.PUSH);
	  column_setup_menu_item.setText(_._("mainwindow.sharedtab.popupmenu.column_setup"));
	  column_setup_menu_item.setImage(SWTImageRepository.getImage("columns_setup.png"));
	  column_setup_menu_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shared_files_table.showColumnEditorWindow();
	  }});
	  
	  select_completed_file_menu = new Menu(shared_files_table);
	  
	  open_selected_menu_item = new MenuItem(select_completed_file_menu,SWT.PUSH);
	  open_selected_menu_item.setText(_._("mainwindow.sharedtab.popupmenu.open"));
	  open_selected_menu_item.setImage(SWTImageRepository.getImage("open_file.png"));
	  open_selected_menu_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				openSelected();
	  }});
	  
	  copy_ed2k_link_menu_item = new MenuItem(select_completed_file_menu,SWT.PUSH);
	  copy_ed2k_link_menu_item.setText(_._("mainwindow.sharedtab.popupmenu.copy_ed2k_link"));
	  copy_ed2k_link_menu_item.setImage(SWTImageRepository.getImage("ed2k_link.png"));
	  copy_ed2k_link_menu_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				copyED2KLink();
	  }});

	  remove_from_disk_menu_item = new MenuItem(select_completed_file_menu,SWT.PUSH);
	  remove_from_disk_menu_item.setText(_._("mainwindow.sharedtab.popupmenu.remove_from_disk"));
	  remove_from_disk_menu_item.setImage(SWTImageRepository.getImage("cancel.png"));
	  remove_from_disk_menu_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				removeFromDisk();
	  }});
	  
	  MenuItem selected_column_setup_menu_item = new MenuItem(select_completed_file_menu,SWT.PUSH);
	  selected_column_setup_menu_item.setText(_._("mainwindow.sharedtab.popupmenu.column_setup"));
	  selected_column_setup_menu_item.setImage(SWTImageRepository.getImage("columns_setup.png"));
	  selected_column_setup_menu_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shared_files_table.showColumnEditorWindow();
	  }});

	  
	  rating_menu_item = new MenuItem(select_completed_file_menu, SWT.CASCADE);
	  rating_menu_item.setText(_._("mainwindow.searchtab.popupmenu.rating_submenu"));
	  rating_menu = new Menu(select_completed_file_menu);
	  rating_menu_item.setMenu(rating_menu);
	  
	  file_not_rated = new MenuItem(rating_menu, SWT.RADIO);
	  file_not_rated.setText(_._("mainwindow.searchtab.popupmenu.rating_submenu.not_rated"));
	  file_not_rated.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						file_fake.setSelection(false);
						file_poor.setSelection(false);
						file_fair.setSelection(false);
						file_good.setSelection(false);
						file_excellent.setSelection(false);
						SharedFile shared_file = shared_files_table.getSelectedObject();
						shared_file.setFileQuality(FileQuality.NOTRATED);
					}
				});
	  }});
	  
	  file_fake = new MenuItem(rating_menu, SWT.RADIO);
	  file_fake.setText(_._("mainwindow.searchtab.popupmenu.rating_submenu.fake"));
	  file_fake.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
				file_not_rated.setSelection(false);
				file_poor.setSelection(false);
				file_fair.setSelection(false);
				file_good.setSelection(false);
				file_excellent.setSelection(false);
				SharedFile shared_file = shared_files_table.getSelectedObject();
				shared_file.setFileQuality(FileQuality.FAKE);
					}});
	  }});

	  file_poor = new MenuItem(rating_menu, SWT.RADIO);
	  file_poor.setText(_._("mainwindow.searchtab.popupmenu.rating_submenu.poor"));
	  file_poor.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
				file_not_rated.setSelection(false);
				file_fake.setSelection(false);
				file_fair.setSelection(false);
				file_good.setSelection(false);
				file_excellent.setSelection(false);	
				SharedFile shared_file = shared_files_table.getSelectedObject();
				shared_file.setFileQuality(FileQuality.POOR);
					}});
	  }});
	  
	  file_fair = new MenuItem(rating_menu, SWT.RADIO);
	  file_fair.setText(_._("mainwindow.searchtab.popupmenu.rating_submenu.fair"));
	  file_fair.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
				file_not_rated.setSelection(false);
				file_fake.setSelection(false);
				file_poor.setSelection(false);
				file_good.setSelection(false);
				file_excellent.setSelection(false);
				SharedFile shared_file = shared_files_table.getSelectedObject();
				shared_file.setFileQuality(FileQuality.FAIR);
					}});
	  }});
	  
	  file_good = new MenuItem(rating_menu, SWT.RADIO);
	  file_good.setText(_._("mainwindow.searchtab.popupmenu.rating_submenu.good"));
	  file_good.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
				file_not_rated.setSelection(false);
				file_fake.setSelection(false);
				file_poor.setSelection(false);
				file_fair.setSelection(false);
				file_excellent.setSelection(false);	
				SharedFile shared_file = shared_files_table.getSelectedObject();
				shared_file.setFileQuality(FileQuality.GOOD);
					}});
	  }});
	  
	  file_excellent = new MenuItem(rating_menu, SWT.RADIO);
	  file_excellent.setText(_._("mainwindow.searchtab.popupmenu.rating_submenu.excellent"));
	  file_excellent.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
				file_not_rated.setSelection(false);
				file_fake.setSelection(false);
				file_poor.setSelection(false);
				file_fair.setSelection(false);
				file_good.setSelection(false);
				SharedFile shared_file = shared_files_table.getSelectedObject();
				shared_file.setFileQuality(FileQuality.EXCELLENT);
					}});
	  }});
	  new MenuItem(select_completed_file_menu,SWT.SEPARATOR);
	  properties_menu_item = new MenuItem(select_completed_file_menu,SWT.PUSH);
	  properties_menu_item.setText(_._("mainwindow.sharedtab.popupmenu.properties"));
	  properties_menu_item.setImage(SWTImageRepository.getImage("info.png"));
	  properties_menu_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				SharedFile shared_file = shared_files_table.getSelectedObject();
				SharedFilePropertiesWindow window = new SharedFilePropertiesWindow(shared_file);
				window.getCoreComponents();
				window.initUIComponents();
	  }});
	  config_manager.addConfigurationListener(new ConfigurationAdapter() {
		  public void sharedDirectoriesChanged(java.util.List<File> sharedDirs) {
			  updateDirList(sharedDirs);	
		}
	  });
	  try {
		updateDirList(config_manager.getSharedFolders());
	} catch (ConfigurationManagerException e) {
		e.printStackTrace();
	}
	refreshable = new Refreshable() {
		public void refresh() {
			if (isDisposed()) return;
			String text = _._("mainwindow.sharedtab.group.shared_files");
			SharedFile hashing_file = sharing_manager.getCurrentHashingFile();
			if (hashing_file != null) {
				text +=" Hashing [";
				DecimalFormat formatter = new DecimalFormat("0.00");
				String file_name = hashing_file.getSharingName();
				if (file_name.length()>20)
					file_name = file_name.substring(0, 20) + "...";
				text +=file_name+" : " + formatter.format(sharing_manager.getCurrentHashingFilePercent())+"%";
				text +="]";
			}
	
			shared_files_group.setText(text);
		
			for(SharedFile shared_file : shared_files_table.getObjects()) {
				if (shared_files_table.getRow(shared_file).isVisible()) 
					shared_files_table.updateRow(shared_file);
			}
		}
	  };
	}
	
	private void openSelected() {
		java.util.List<SharedFile> selected_files = shared_files_table.getSelectedObjects();
		for(SharedFile file : selected_files) {
			if (!file.isCompleted()) continue;
			String extension = FileFormatter.getFileExtension(file.getSharingName());
			Program program = Program.findProgram(extension);
			if (program == null) continue;
			program.execute(file.getAbsolutePath());
		}
	}
	
	private boolean selectedRunnableFiles() {
		java.util.List<SharedFile> sharedFiles = shared_files_table.getSelectedObjects();
		for(SharedFile file : sharedFiles) {
			if (!file.isCompleted()) continue;
			String extension = FileFormatter.getFileExtension(file.getSharingName());
			Program program = Program.findProgram(extension);
			if (program != null) return true;
		}
		return false;
	}
	
	private void removeSelectedDir() {
		if (!Utils.showConfirmMessage(getShell(), _._("mainwindow.sharedtab.confirm_remove_shared_dir_title"), 
				 _._("mainwindow.sharedtab.confirm_remove_shared_dir"))) return ;
		
		if (shared_dir_list.getSelectionIndex() == -1) return ;
		shared_dir_list.remove(shared_dir_list.getSelectionIndex());
		writeSharedDirs();
		shared_files_table.clear();
		remove_button.setEnabled(false);
	}
	
	private void clearDirList() {
		if (!Utils.showConfirmMessage(getShell(), _._("mainwindow.sharedtab.confirm_clear_dir_list_title"), 
				 _._("mainwindow.sharedtab.confirm_clear_dir_list"))) return ;
		while(shared_dir_list.getItemCount()-1>no_remove_id_end) {
			shared_dir_list.remove(no_remove_id_end+1);
		}
		writeSharedDirs();
		shared_files_table.clear();
		remove_button.setEnabled(false);
	}

	private void writeSharedDirs() {
		java.util.List<File> shared_list = new java.util.ArrayList<File>();
		for(int i = 0; i<shared_dir_list.getItemCount();i++) {
			if (i<=no_remove_id_end) continue;
			shared_list.add(new File(shared_dir_list.getItem(i)));
		}
		try {
			_core.getConfigurationManager().setSharedFolders(shared_list);
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
		}
	}
	
	private void copyED2KLink() {
		java.util.List<SharedFile> list = shared_files_table.getSelectedObjects();
		String text="";
		for(SharedFile file : list) {
			text += file.getED2KLink().getAsString() + System.getProperty("line.separator");
		}
		Utils.setClipBoardText(text);
	}
	
	private void removeFromDisk() {
		if (!Utils.showConfirmMessage(getShell(), _._("mainwindow.sharedtab.confirm_remove_from_disk_title"), 
									 _._("mainwindow.sharedtab.confirm_remove_from_disk"))) return ;
		for(SharedFile shared_file : shared_files_table.getSelectedObjects()) {
			shared_file.getFile().delete();
			sharing_manager.removeSharedFile(shared_file.getFileHash());
			shared_files_table.removeRow(shared_file);
		}
	}

	private void updateDirList(final java.util.List<File> directoryList) {
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				shared_dir_list.removeAll();
				shared_dir_list.add(_._("mainwindow.sharedtab.shared_dirs.incoming"));
				shared_dir_list.add(_._("mainwindow.sharedtab.shared_dirs.partial"));
				shared_dir_list.add(_._("mainwindow.sharedtab.shared_dirs.all"));
				clear_button.setEnabled(false);
				if (directoryList == null)
					return ;
				
				for(File file : directoryList) {
					shared_dir_list.add(file.getAbsolutePath());
				}
				clear_button.setEnabled(true);
			}
		});
	}
	
	private JMThread file_list_updater;
	
	private void updateFileList(final String dir) {
		
		if (file_list_updater!=null)
			if (file_list_updater.isAlive()) {
				file_list_updater.JMStop();
			}

		file_list_updater = new JMThread() {
			private boolean stop = false;
			
			public void JMStop() {
				stop = true;
				interrupt();
			}
			
			public void run() {
				File shared_dir = null;
				SWTThread.getDisplay().syncExec(new JMRunnable() {
					public void JMRun() {
						shared_files_table.clear();
				}});
				
				if (dir.equals(_._("mainwindow.sharedtab.shared_dirs.partial"))) {			
					final java.util.List<PartialFile> partial_file_list = sharing_manager.getPartialFiles();
							for(final SharedFile partial_file : partial_file_list ) {
								if (stop) return ;
								SWTThread.getDisplay().syncExec(new JMRunnable() {
									public void JMRun() {
										shared_files_table.addRow(partial_file);
									}}); 
								}
							return ;
					}
				
				if (dir.equals(_._("mainwindow.sharedtab.shared_dirs.incoming"))) 
					shared_dir = new File(ConfigurationManager.INCOMING_DIR);	
					
				if (!dir.equals(_._("mainwindow.sharedtab.shared_dirs.all"))) {
					
						// files from selected dir
						if (shared_dir == null)
							shared_dir = new File(dir);
						
						java.util.List<File> list_of_files = FileUtils.traverseDirAndReturnListOfFiles( shared_dir );
						if (stop) return ;
						
						for(File shared_file : list_of_files) {
							if (stop) return ;
							
							if (shared_file.isDirectory()) continue;
							final SharedFile file = sharing_manager.getSharedFile(shared_file);
							if (file!=null)
								SWTThread.getDisplay().asyncExec(new JMRunnable() {
									public void JMRun() {
										shared_files_table.addRow(file);
									}
								});
							
							
							java.util.List<CompletedFile> unhashed_file_list = sharing_manager.getUnhashedFiles();
							if (unhashed_file_list == null) continue ;
							for(final SharedFile unhashed_file : unhashed_file_list) {
								if (stop) return ;
								if (unhashed_file.getFile().equals(shared_file)) {
									SWTThread.getDisplay().syncExec(new JMRunnable() {
										  public void JMRun() {
											shared_files_table.addRow(unhashed_file);
									}});
									break;
								}
							}
						  } 
						
					} else { // all files
						java.util.List<CompletedFile> unhashed_file_list = sharing_manager.getUnhashedFiles();
						if (unhashed_file_list != null) {
							for(final SharedFile file : unhashed_file_list) {
								SWTThread.getDisplay().syncExec(new JMRunnable() {
									public void JMRun() {
										if (isDisposed()) return ;
										if (stop) return;
										shared_files_table.addRow(file);
									}
								});
							}
						}
						
						if (stop) return ;
						final JMIterable<SharedFile> shared_files = sharing_manager.getSharedFiles();
						for(final SharedFile file : shared_files) {
							if (stop) return ;
							SWTThread.getDisplay().syncExec(new JMRunnable() {
								  public void JMRun() {
									shared_files_table.addRow(file);
							 }}); 
						}
					}
				}
			};
		file_list_updater.setName("File list updater");
		file_list_updater.start();
	}
	
	public JMULE_TABS getTabType() {

		return JMULE_TABS.SHARED;
	}

	public void lostFocus() {
		GUIUpdater.getInstance().removeRefreshable(refreshable);
		_core.getConfigurationManager().removeConfigurationListener(config_listener);
	}

	public void obtainFocus() {
		GUIUpdater.getInstance().addRefreshable(refreshable);
		_core.getConfigurationManager().addConfigurationListener(config_listener);
	}

	public void disposeTab() {
		GUIUpdater.getInstance().removeRefreshable(refreshable);
		_core.getConfigurationManager().removeConfigurationListener(config_listener);
	}

}
