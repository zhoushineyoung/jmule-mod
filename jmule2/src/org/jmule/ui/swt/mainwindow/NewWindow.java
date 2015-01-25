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
package org.jmule.ui.swt.mainwindow;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.jmule.core.JMConstants;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.downloadmanager.DownloadManagerException;
import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.edonkey.ED2KLinkMalformedException;
import org.jmule.core.edonkey.ED2KServerLink;
import org.jmule.core.servermanager.ServerManager;
import org.jmule.core.servermanager.ServerManagerException;
import org.jmule.core.sharingmanager.SharingManager;
import org.jmule.core.utils.FileUtils;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.skin.Skin;
import org.jmule.ui.swt.SWTConstants;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.skin.SWTSkin;
import org.jmule.ui.swt.tab.main.shared.AlreadyExistDirsWindow;
import org.jmule.ui.swt.tables.JMTable;
import org.jmule.ui.utils.FileFormatter;

/**
 * Created on Sep 16, 2008
 * @author binary256
 * @version $Revision: 1.7 $
 * Last changed by $Author: binary255 $ on $Date: 2011/03/27 16:51:29 $
 */
public class NewWindow implements JMuleUIComponent {
	public static enum WindowType { DOWNLOAD, SERVER, SHARED_DIR };
	
	private WindowType type;
	private Shell shell;
	private SharingManager  sharing_manager;
	private DownloadManager download_manager;
	private ServerManager   server_manager;
	private ConfigurationManager config_manager;
	private GenericTable table;
	
	public NewWindow(WindowType windowType) {
		type = windowType;
		getCoreComponents();
		initUIComponents();
	}
	
	public void getCoreComponents() {
		JMuleCore core = JMuleCoreFactory.getSingleton();
		sharing_manager  = core.getSharingManager();
		download_manager = core.getDownloadManager();
		server_manager   = core.getServerManager();
		config_manager   = core.getConfigurationManager();
	}

	public void initUIComponents() {
		if (type == WindowType.SHARED_DIR) {
			shell = new Shell(SWTThread.getDisplay());
			DirectoryDialog dir_dialog = new DirectoryDialog (shell,SWT.MULTI | SWT.OPEN);
			dir_dialog.setText(_._("newwindow.title.shared_dir"));
			String directory = dir_dialog.open();
			if (directory == null) return ;
			
			List<File> shared_dirs = null;
			try {
				shared_dirs = config_manager.getSharedFolders();
			} catch (ConfigurationManagerException e1) {
				e1.printStackTrace();
			}
			List<File> newDirs = new LinkedList<File>();
			if (shared_dirs == null)
				shared_dirs = new CopyOnWriteArrayList<File>();
			else
				shared_dirs = new CopyOnWriteArrayList<File>(shared_dirs);
			
			List<File> already_exist_list = FileUtils.extractNewFolders(new File[]{new File(directory)},shared_dirs,newDirs);
			
			if (already_exist_list.size()!=0) {
				AlreadyExistDirsWindow window = new AlreadyExistDirsWindow(already_exist_list);
				window.getCoreComponents();
				window.initUIComponents();
			} 
			shared_dirs.addAll(newDirs);
			try {
				config_manager.setSharedFolders(shared_dirs);
			} catch (ConfigurationManagerException e1) {
				e1.printStackTrace();
			}
			
			return ;
		}
		SWTSkin skin = null;
		try {
		    skin = (SWTSkin)JMuleUIManager.getJMuleUI().getSkin();
		}catch(Throwable t) {}
		Display display = SWTThread.getDisplay();
		GridData grid_data;
		
		final Shell shell1=new Shell(display,SWT.ON_TOP);
		shell=new Shell(shell1,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		if	(type == WindowType.DOWNLOAD) {
			shell.setText(_._("newwindow.title.download"));
			shell.setImage(SWTImageRepository.getMenuImage("folder_down.png"));
		}
		else {
			shell.setText(_._("newwindow.title.server"));
			shell.setImage(SWTImageRepository.getMenuImage("server_add.png"));
		}
		shell.setSize(600, 300);
		shell.setLayout(new GridLayout(1,false));
		
		Composite content = new Composite(shell,SWT.NONE);
		content.setLayoutData(new GridData(GridData.FILL_BOTH));
		content.setLayout(new FillLayout());
		if	(type == WindowType.DOWNLOAD)
			table = new DownloadList(content);
		else
			table = new ServerList(content);
		
		CLabel learn_more_link = new CLabel(shell,SWT.NONE);
		learn_more_link.setText(_._("newwindow.label.about_ed2k_links"));
		learn_more_link.setData(JMConstants.ABOUT_ED2K_LINKS);
		learn_more_link.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		learn_more_link.setCursor(new Cursor(SWTThread.getDisplay(),SWT.CURSOR_HAND));
		learn_more_link.addMouseListener(new MouseAdapter() {	
				public void mouseDoubleClick(MouseEvent arg0) {
					String path = (String) ((CLabel) arg0.widget).getData();
					if (!Utils.launchProgram(path)) 
						Utils.showWarningMessage(shell, _._("newwindow.error_open_url.title")
								, Localizer._("newwindow.error_open_url",path));
				}
				public void mouseDown(MouseEvent arg0) {
					String path = (String) ((CLabel) arg0.widget).getData();
					if (!Utils.launchProgram(path)) 
						Utils.showWarningMessage(shell, _._("newwindow.error_open_url.title")
								, Localizer._("newwindow.error_open_url",path));
				}
		});
		
		Composite buttons_composite = new Composite(shell,SWT.BORDER);
		buttons_composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout grid_layout = new GridLayout(4,false);
		buttons_composite.setLayout(grid_layout);
		
		Button button_paste_ed2k_link = new Button(buttons_composite,SWT.NONE);
		button_paste_ed2k_link.setFont(skin.getButtonFont());
		button_paste_ed2k_link.setText(_._("newwindow.button.paste_ed2k_link"));
		button_paste_ed2k_link.setImage(SWTImageRepository.getImage("ed2k_link_paste.png"));
		button_paste_ed2k_link.forceFocus();
		button_paste_ed2k_link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				table.paste();
			}
		});
		
		Button button_clear = new Button(buttons_composite,SWT.NONE);
		button_clear.setFont(skin.getButtonFont());
		button_clear.setText(_._("newwindow.button.clear"));
		button_clear.setImage(SWTImageRepository.getImage("remove_all.png"));
		button_clear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				table.clear();
			}
		});
		
		grid_data = new GridData();
		grid_data.widthHint = 70;
		grid_data.horizontalAlignment = GridData.END;
		grid_data.grabExcessHorizontalSpace = true;
		
		Button button_ok = new Button(buttons_composite,SWT.NONE);
		button_ok.setText(_._("newwindow.button.ok"));
		button_ok.setFont(skin.getButtonFont());
		button_ok.setImage(skin.getButtonImage(Skin.OK_BUTTON_IMAGE));
		button_ok.setLayoutData(grid_data);
		button_ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				table.save();
				shell.close();
			}
		});
		
		Button button_cancel = new Button(buttons_composite,SWT.NONE);
		button_cancel.setFont(skin.getButtonFont());
		button_cancel.setImage(skin.getButtonImage(Skin.CANCEL_BUTTON_IMAGE));
		button_cancel.setText(_._("newwindow.button.cancel"));
		
		button_cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		
		Utils.centreWindow(shell);
		
		shell.open();
	}
	
	private interface GenericTable {
		public void save();
		
		public void paste();
		
		public void clear();
		
	}
	
	private class DownloadList extends JMTable<ED2KFileLink> implements GenericTable {
		private List<Integer> to_set_list = new LinkedList<Integer>();
		private Menu popup_menu;
		
		public DownloadList(Composite composite) {
			super(composite,SWT.CHECK);
			
			addColumn(SWT.LEFT, SWTConstants.NEW_DOWNLOAD_LIST_FILE_NAME_COLUMN_ID, _._("newwindow.column.file_name"), _._("newwindow.column.file_name.desc"), SWTPreferences.getInstance().getColumnWidth(SWTConstants.NEW_DOWNLOAD_LIST_FILE_NAME_COLUMN_ID));
			addColumn(SWT.LEFT, SWTConstants.NEW_DOWNLOAD_LIST_FILE_SIZE_COLUMN_ID, _._("newwindow.column.file_size"), _._("newwindow.column.file_size.desc"), SWTPreferences.getInstance().getColumnWidth(SWTConstants.NEW_DOWNLOAD_LIST_FILE_SIZE_COLUMN_ID));
			addColumn(SWT.LEFT, SWTConstants.NEW_DOWNLOAD_LIST_FILE_ID_COLUMN_ID,   _._("newwindow.column.file_id"), _._("newwindow.column.file_id.desc"), 	   SWTPreferences.getInstance().getColumnWidth(SWTConstants.NEW_DOWNLOAD_LIST_FILE_ID_COLUMN_ID));
			setSorting(false);
			
			addListener(SWT.SetData, new Listener() {
				public void handleEvent(Event arg0) {
					TableItem item = (TableItem) arg0.item;
					int index = indexOf(item);
					if (to_set_list.contains(index))
						to_set_list.remove((Integer)index);
					item.setChecked(true);
				}
			});
			
			popup_menu = new Menu(this);
			MenuItem paste_ed2k = new MenuItem(popup_menu,SWT.PUSH);
			paste_ed2k.setText(_._("newwindow.menu.paste_ed2k_link"));
			paste_ed2k.setImage(SWTImageRepository.getImage("ed2k_link_paste.png"));
			paste_ed2k.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					paste();
				}
			});
		}

		public void clear() {
			super.clear();
			to_set_list.clear();
		}
		
		protected int compareObjects(ED2KFileLink object1,
				ED2KFileLink object2, int columnID, boolean order) {
			return 0;
		}

		protected Menu getPopUpMenu() {
			return popup_menu;
		}

		public void updateRow(ED2KFileLink object) {
			setRowText(object, SWTConstants.NEW_DOWNLOAD_LIST_FILE_NAME_COLUMN_ID, object.getFileName());
			setRowText(object, SWTConstants.NEW_DOWNLOAD_LIST_FILE_SIZE_COLUMN_ID, FileFormatter.formatFileSize(object.getFileSize()));
			setRowText(object, SWTConstants.NEW_DOWNLOAD_LIST_FILE_ID_COLUMN_ID, object.getFileHash().getAsString());
		}

		public void paste() {
			String clip_board_text = Utils.getClipboardText();
			List<ED2KFileLink> link_list;
			try {
				link_list = ED2KFileLink.extractLinks(clip_board_text);
				for(ED2KFileLink link : link_list) {
					if (!hasObject(link))
						addRow(link);
				}
			} catch (ED2KLinkMalformedException e) {
				e.printStackTrace();
			}
			
		}

		public void save() {
			for(TableItem item : getItems()) {
				if (item.getChecked()) {
					int id = indexOf(item);
					ED2KFileLink link = getData(id);
					if (sharing_manager.hasFile(link.getFileHash())) continue;
					try {
						download_manager.addDownload(link);
					} catch (DownloadManagerException e) {
						e.printStackTrace();
					}
				}
			}
		}	
	}
	
	private class ServerList extends JMTable<ED2KServerLink> implements GenericTable {

		private List<Integer> to_set_list = new LinkedList<Integer>();
		private Menu popup_menu;
		
		public ServerList(Composite composite) {
			super(composite, SWT.CHECK);
			addColumn(SWT.LEFT, SWTConstants.NEW_SERVER_LIST_NAME_COLUMN_ID, _._("newwindow.column.server_name"), _._("newwindow.column.server_name.desc"), SWTPreferences.getInstance().getColumnWidth(SWTConstants.NEW_SERVER_LIST_NAME_COLUMN_ID));
			addColumn(SWT.LEFT, SWTConstants.NEW_SERVER_LIST_PORT_COLUMN_ID, _._("newwindow.column.server_port"), _._("newwindow.column.server_port"), SWTPreferences.getInstance().getColumnWidth(SWTConstants.NEW_SERVER_LIST_PORT_COLUMN_ID));
			
			setSorting(false);
			
			addListener(SWT.SetData, new Listener() {
				public void handleEvent(Event arg0) {
					TableItem item = (TableItem) arg0.item;
					int index = indexOf(item);
					if (to_set_list.contains(index))
						to_set_list.remove((Integer)index);
					item.setChecked(true);
				}
			});
			
			popup_menu = new Menu(this);
			MenuItem paste_ed2k = new MenuItem(popup_menu,SWT.PUSH);
			paste_ed2k.setText(_._("newwindow.menu.paste_ed2k_link"));
			paste_ed2k.setImage(SWTImageRepository.getImage("ed2k_link_paste.png"));
			paste_ed2k.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					paste();
				}
			});	
		}

		protected int compareObjects(ED2KServerLink object1,
				ED2KServerLink object2, int columnID, boolean order) {
			return 0;
		}

		protected Menu getPopUpMenu() {
			return popup_menu;
		}

		public void updateRow(ED2KServerLink object) {
			setRowText(object, SWTConstants.NEW_SERVER_LIST_NAME_COLUMN_ID, object.getServerAddress());
			setRowText(object, SWTConstants.NEW_SERVER_LIST_PORT_COLUMN_ID, object.getServerPort()+"");
		}

		public void paste() {
			String clip_board_text = Utils.getClipboardText();
			List<ED2KServerLink> link_list = ED2KServerLink.extractLinks(clip_board_text);
			for(ED2KServerLink link : link_list) {
				if (!hasObject(link))
					addRow(link);
			}
		}

		public void save() {
			for(TableItem item : getItems()) {
				if (item.getChecked()) {
					int id = indexOf(item);
					ED2KServerLink link = getData(id);
					if (!server_manager.hasServer(link.getServerAddress(), link.getServerPort())) {						
						try {
							server_manager.newServer(link);
						} catch (ServerManagerException e) {
							e.printStackTrace();
						}
					}
						
				}
			}
		}
		
		public void clear() {
			super.clear();
			to_set_list.clear();
		}
		
	}
}
