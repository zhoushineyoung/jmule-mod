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
package org.jmule.ui.swt.maintabs.serverlist;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMThread;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.edonkey.ED2KServerLink;
import org.jmule.core.servermanager.Server;
import org.jmule.core.servermanager.ServerManager;
import org.jmule.core.servermanager.ServerManagerException;
import org.jmule.core.utils.Misc;
import org.jmule.countrylocator.CountryLocator;
import org.jmule.ui.FlagPack.FlagSize;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.Refreshable;
import org.jmule.ui.swt.SWTConstants;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.common.CountryFlagPainter;
import org.jmule.ui.swt.mainwindow.MainWindow;
import org.jmule.ui.swt.tables.BufferedTableRow;
import org.jmule.ui.swt.tables.JMTable;
import org.jmule.ui.swt.tables.TableItemCountryFlag;
import org.jmule.ui.utils.NumberFormatter;
/**
 * 
 * @author binary256
 * @version $$Revision: 1.11 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/10/27 20:44:58 $$
 */
public class ServerList extends JMTable<Server> implements Refreshable {
	
	private ServerManager servers_manager;
	
	private Color server_down_color      = new Color(getDisplay(),178,178,178);
	private Color server_connected_color = new Color(getDisplay(),124,152,225);
	private Color server_default_color   = new Color(getDisplay(),0,0,0);
	
	private static final FlagSize default_flag_size = FlagSize.S25x15;
	
	private Menu no_servers_menu;
	private Menu no_selected_servers_menu;
	private Menu selected_server_menu;
	private Menu selected_multiple_servers_menu;
	
	private MenuItem selected_server_connect;
	private MenuItem selected_server_disconnect;
	private MenuItem no_selection_menu_disconnect;
	private MenuItem server_disconnect_multiselect,server_connect_multiselect;
	private MenuItem server_remove_all,server_remove_all_multiselect;
	private MenuItem add_to_static_list,remove_from_static_list;
	private MenuItem multisel_add_to_static_list,multisel_remove_from_static_list;
	
	private enum ServerListStatus { NO_SERVERS,NO_SERVERS_SELECTED_DISCONNECTED,NO_SERVERS_SELECTED_CONNECTED,SELECTED_DISCONNECTED_SERVER_NOT_CONNECTED,
							 		  SELECTED_MULTIPLE_SERVERS_NOT_CONNECTED, SELECTED_CONNECTED_SERVER,
							 		  SELECTED_MULTIPLE_SERVERS_WITH_ONE_CONNECTED,SELECTED_MULTIPLE_SERVERS_CONNECTED,
							 		  SELECTED_DISCONNECTED_SERVER_CONNECTED }
	
	public ServerList(Composite composite, final ServerManager server_manager) {
		super(composite, SWT.NONE);
		
		SWTServerListWrapper.getInstance().setServerList(this);
		
		this.servers_manager = server_manager;
		int width;
		width = swt_preferences.getColumnWidth(SWTConstants.SERVER_LIST_NAME_COLUMN_ID);
		addColumn(SWT.LEFT, SWTConstants.SERVER_LIST_NAME_COLUMN_ID,Localizer._("mainwindow.serverlisttab.serverlist.column.name"), "",width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.SERVER_LIST_CC_COLUMN_ID);
		addColumn(SWT.CENTER, SWTConstants.SERVER_LIST_CC_COLUMN_ID,Localizer._("mainwindow.serverlisttab.serverlist.column.country_code"), _._("mainwindow.serverlisttab.serverlist.column.country_code.desc"),width);
		if (CountryLocator.getInstance().isServiceDown()) 
			disableColumn(SWTConstants.SERVER_LIST_CC_COLUMN_ID);
		
		width = swt_preferences.getColumnWidth(SWTConstants.SERVER_LIST_FLAG_COLUMN_ID);
		addColumn(SWT.LEFT, SWTConstants.SERVER_LIST_FLAG_COLUMN_ID,Localizer._("mainwindow.serverlisttab.serverlist.column.country"), "",width);
		if (CountryLocator.getInstance().isServiceDown()) 
			disableColumn(SWTConstants.SERVER_LIST_FLAG_COLUMN_ID);
		
		width = swt_preferences.getColumnWidth(SWTConstants.SERVER_LIST_IP_COLUMN_ID);
		addColumn(SWT.LEFT, SWTConstants.SERVER_LIST_IP_COLUMN_ID, Localizer._("mainwindow.serverlisttab.serverlist.column.address"), "",width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.SERVER_LIST_DESCRIPTION_COLUMN_ID);
		addColumn(SWT.LEFT, SWTConstants.SERVER_LIST_DESCRIPTION_COLUMN_ID,Localizer._("mainwindow.serverlisttab.serverlist.column.description"), "",width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.SERVER_LIST_PING_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.SERVER_LIST_PING_COLUMN_ID,Localizer._("mainwindow.serverlisttab.serverlist.column.ping"), "",width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.SERVER_LIST_USERS_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.SERVER_LIST_USERS_COLUMN_ID,Localizer._("mainwindow.serverlisttab.serverlist.column.users"), "",width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.SERVER_LIST_MAX_USERS_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.SERVER_LIST_MAX_USERS_COLUMN_ID,Localizer._("mainwindow.serverlisttab.serverlist.column.max_users"), _._("mainwindow.serverlisttab.serverlist.column.maxusers.desc"),width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.SERVER_LIST_FILES_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.SERVER_LIST_FILES_COLUMN_ID,Localizer._("mainwindow.serverlisttab.serverlist.column.files"), "",width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.SERVER_LIST_SOFT_LIMIT_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.SERVER_LIST_SOFT_LIMIT_COLUMN_ID,Localizer._("mainwindow.serverlisttab.serverlist.column.soft_limit"), _._("mainwindow.serverlisttab.serverlist.column.soft_limit.desc"),width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.SERVER_LIST_HARD_LIMIT_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.SERVER_LIST_HARD_LIMIT_COLUMN_ID,Localizer._("mainwindow.serverlisttab.serverlist.column.hard_limit"), _._("mainwindow.serverlisttab.serverlist.column.hard_limit.desc"),width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.SERVER_LIST_VERSION_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.SERVER_LIST_VERSION_COLUMN_ID,Localizer._("mainwindow.serverlisttab.serverlist.column.software"), "",width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.SERVER_LIST_STATIC_COLUMN_ID);
		addColumn(SWT.LEFT, SWTConstants.SERVER_LIST_STATIC_COLUMN_ID,Localizer._("mainwindow.serverlisttab.serverlist.column.static"), "",width);
		
		updateColumnOrder();
		updateColumnVisibility();
		
		final SWTServerListWrapper wrapper = SWTServerListWrapper.getInstance();
		
		// No servers
		no_servers_menu = new Menu(this);
		
		MenuItem no_servers_server_add = new MenuItem (no_servers_menu, SWT.PUSH);
		no_servers_server_add.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.add_server"));
		no_servers_server_add.setImage(SWTImageRepository.getImage("server_add.png"));
		no_servers_server_add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				showServerAddWindow();
			}
		});
		
		new MenuItem (no_servers_menu, SWT.SEPARATOR);
		
		MenuItem no_server_paste_ed2k_links = new MenuItem (no_servers_menu, SWT.PUSH);
		no_server_paste_ed2k_links.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.paste_ed2k_links"));
		no_server_paste_ed2k_links.setImage(SWTImageRepository.getImage("ed2k_link_paste.png"));
		no_server_paste_ed2k_links.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				pasteED2KLinks();
			}
		});
		
		new MenuItem (no_servers_menu, SWT.SEPARATOR);
		MenuItem no_server_column_setup = new MenuItem (no_servers_menu, SWT.PUSH);
		no_server_column_setup.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.column_setup"));
		no_server_column_setup.setImage(SWTImageRepository.getImage("columns_setup.png"));
		no_server_column_setup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				showColumnEditorWindow();
			}
		});
		
		no_selected_servers_menu = new Menu(this);
		MenuItem no_selection_menu_add = new MenuItem(no_selected_servers_menu,SWT.PUSH);
		no_selection_menu_add.setText(Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.add_server"));
		no_selection_menu_add.setImage(SWTImageRepository.getImage("server_add.png"));
		no_selection_menu_add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				showServerAddWindow();
			} 
		});
		
		new MenuItem(no_selected_servers_menu,SWT.SEPARATOR);
		
		no_selection_menu_disconnect = new MenuItem(no_selected_servers_menu,SWT.PUSH);
		no_selection_menu_disconnect.setText(Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.disconnect_from"));
		no_selection_menu_disconnect.setImage(SWTImageRepository.getImage("server_disconnect.png"));
		no_selection_menu_disconnect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				wrapper.disconnect();
			} 
		});
		
		new MenuItem(no_selected_servers_menu,SWT.SEPARATOR);
		
		MenuItem no_selection_menu_remove_all = new MenuItem(no_selected_servers_menu,SWT.PUSH);
		no_selection_menu_remove_all.setText(Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.remove_all"));
		no_selection_menu_remove_all.setImage(SWTImageRepository.getImage("remove_all.png"));
		no_selection_menu_remove_all.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				clearServerList();
			} 
		});
		
		new MenuItem(no_selected_servers_menu,SWT.SEPARATOR);	
		
		MenuItem no_selection_menu_paste_ed2k = new MenuItem(no_selected_servers_menu,SWT.PUSH);
		no_selection_menu_paste_ed2k.setText(Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.paste_ed2k_links"));
		no_selection_menu_paste_ed2k.setImage(SWTImageRepository.getImage("ed2k_link_paste.png"));
		no_selection_menu_paste_ed2k.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				pasteED2KLinks();
			} 
		});
		
		new MenuItem(no_selected_servers_menu,SWT.SEPARATOR);
		
		MenuItem no_selection_menu_column_setup = new MenuItem(no_selected_servers_menu,SWT.PUSH);
		no_selection_menu_column_setup.setText(Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.column_setup"));
		no_selection_menu_column_setup.setImage(SWTImageRepository.getImage("columns_setup.png"));
		no_selection_menu_column_setup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				showColumnEditorWindow();
			} 
		});
		
		selected_server_menu =  new Menu(this);
		
		selected_server_connect = new MenuItem (selected_server_menu, SWT.PUSH);
		selected_server_connect.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.connect_to"));
		selected_server_connect.setImage(SWTImageRepository.getImage("server_connect.png"));
		selected_server_connect.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(final SelectionEvent e) {
			Server selected_server = (Server) getSelectedObject();
			wrapper.connectTo(selected_server);
		}} );
		
		selected_server_disconnect = new MenuItem (selected_server_menu, SWT.PUSH);
		selected_server_disconnect.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.disconnect_from"));
		selected_server_disconnect.setImage(SWTImageRepository.getImage("server_disconnect.png"));
		selected_server_disconnect.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(final SelectionEvent e) {
			if (wrapper.isAutoconnecting())
				wrapper.stopConnecting();
			else
				wrapper.disconnect();
		}} );
		
		new MenuItem (selected_server_menu, SWT.SEPARATOR);
		MenuItem server_add = new MenuItem (selected_server_menu, SWT.PUSH);
		server_add.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.add_server"));
		server_add.setImage(SWTImageRepository.getImage("server_add.png"));
		server_add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				showServerAddWindow();
			}
		});
		final MenuItem server_remove = new MenuItem (selected_server_menu, SWT.PUSH);
		server_remove.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.remove_server"));
		server_remove.setImage(SWTImageRepository.getImage("server_delete.png"));
		server_remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				removeSelectedServers();
			}
		});
		server_remove_all = new MenuItem (selected_server_menu, SWT.PUSH);
		server_remove_all.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.remove_all"));
		server_remove_all.setImage(SWTImageRepository.getImage("remove_all.png"));
		server_remove_all.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				clearServerList();
			}
		});
		new MenuItem (selected_server_menu, SWT.SEPARATOR);
		final MenuItem server_copy_ed2k_link = new MenuItem (selected_server_menu, SWT.PUSH);
		server_copy_ed2k_link.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.copy_ed2k_link"));
		server_copy_ed2k_link.setImage(SWTImageRepository.getImage("ed2k_link.png"));
		server_copy_ed2k_link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				copyED2KLinks();
			}
		});
		
		MenuItem server_paste_ed2k_links = new MenuItem (selected_server_menu, SWT.PUSH);
		server_paste_ed2k_links.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.paste_ed2k_links"));
		server_paste_ed2k_links.setImage(SWTImageRepository.getImage("ed2k_link_paste.png"));
		server_paste_ed2k_links.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
					pasteED2KLinks();
			}
			
		});
		
		new MenuItem (selected_server_menu, SWT.SEPARATOR);
		
		add_to_static_list = new MenuItem (selected_server_menu, SWT.PUSH);
		add_to_static_list.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.add_to_static_list"));
		add_to_static_list.setImage(SWTImageRepository.getImage("list_add.png"));
		add_to_static_list.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				addToStaticList();
			}
			
		});
		
		remove_from_static_list = new MenuItem (selected_server_menu, SWT.PUSH);
		remove_from_static_list.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.remove_from_static_list"));
		remove_from_static_list.setImage(SWTImageRepository.getImage("list_remove.png"));
		remove_from_static_list.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				removeFromStaticList();
			}
			
		});
		
		new MenuItem (selected_server_menu, SWT.SEPARATOR);
		MenuItem column_setup = new MenuItem (selected_server_menu, SWT.PUSH);
		column_setup.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.column_setup"));
		column_setup.setImage(SWTImageRepository.getImage("columns_setup.png"));
		column_setup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				showColumnEditorWindow();
			}
		});
		
		new MenuItem (selected_server_menu, SWT.SEPARATOR);
		final MenuItem server_properties = new MenuItem (selected_server_menu, SWT.PUSH);
		server_properties.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.server_properties"));
		server_properties.setImage(SWTImageRepository.getImage("server_properties.png"));
		server_properties.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				ServerPropertiesWindow properties_window = new ServerPropertiesWindow(getSelectedObject());
				properties_window.getCoreComponents();
				properties_window.initUIComponents();
			}
		});
		
		// Multiple servers selected
		selected_multiple_servers_menu = new Menu (this);
				
		MenuItem server_remove_selected = new MenuItem (selected_multiple_servers_menu, SWT.PUSH);
		server_remove_selected.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.remove_selected"));
		server_remove_selected.setImage(SWTImageRepository.getImage("server_delete.png"));
		server_remove_selected.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				removeSelectedServers();
			}
		});
		
		new MenuItem (selected_multiple_servers_menu, SWT.SEPARATOR);
		
		server_remove_all_multiselect = new MenuItem (selected_multiple_servers_menu, SWT.PUSH);
		server_remove_all_multiselect.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.remove_all"));
		server_remove_all_multiselect.setImage(SWTImageRepository.getImage("remove_all.png"));
		server_remove_all_multiselect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				clearServerList();
			}
			
		});
		new MenuItem (selected_multiple_servers_menu, SWT.SEPARATOR);
		
		server_connect_multiselect = new MenuItem (selected_multiple_servers_menu, SWT.PUSH);
		server_connect_multiselect.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.connect"));
		server_connect_multiselect.setImage(SWTImageRepository.getImage("server_connect.png"));
		server_connect_multiselect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				wrapper.startAutoConnect();
			}
		});
		
		server_disconnect_multiselect = new MenuItem (selected_multiple_servers_menu, SWT.PUSH);
		server_disconnect_multiselect.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.disconnect_from"));
		server_disconnect_multiselect.setImage(SWTImageRepository.getImage("server_disconnect.png"));
		server_disconnect_multiselect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (wrapper.isAutoconnecting())
					wrapper.stopConnecting();
				else
					wrapper.disconnect();
			}
		});
		
		new MenuItem (selected_multiple_servers_menu, SWT.SEPARATOR);
		
		MenuItem multi_select_menu_copy_ed2k_links = new MenuItem (selected_multiple_servers_menu, SWT.PUSH);
		multi_select_menu_copy_ed2k_links.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.copy_ed2k_links"));
		multi_select_menu_copy_ed2k_links.setImage(SWTImageRepository.getImage("ed2k_link.png"));
		multi_select_menu_copy_ed2k_links.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				copyED2KLinks();
			}
			
		});
		
		MenuItem multi_select_menu_paste_ed2k_links = new MenuItem (selected_multiple_servers_menu, SWT.PUSH);
		multi_select_menu_paste_ed2k_links.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.paste_ed2k_links"));
		multi_select_menu_paste_ed2k_links.setImage(SWTImageRepository.getImage("ed2k_link_paste.png"));
		multi_select_menu_paste_ed2k_links.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				pasteED2KLinks();
			}
		});
		
		new MenuItem (selected_multiple_servers_menu, SWT.SEPARATOR);
		
		multisel_add_to_static_list = new MenuItem (selected_multiple_servers_menu, SWT.PUSH);
		multisel_add_to_static_list.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.add_to_static_list"));
		multisel_add_to_static_list.setImage(SWTImageRepository.getImage("list_add.png"));
		multisel_add_to_static_list.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				addToStaticList();
			}
			
		});
		
		multisel_remove_from_static_list = new MenuItem (selected_multiple_servers_menu, SWT.PUSH);
		multisel_remove_from_static_list.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.remove_from_static_list"));
		multisel_remove_from_static_list.setImage(SWTImageRepository.getImage("list_remove.png"));
		multisel_remove_from_static_list.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				removeFromStaticList();
			}
			
		});
		
		new MenuItem (selected_multiple_servers_menu, SWT.SEPARATOR);
		
		column_setup = new MenuItem (selected_multiple_servers_menu, SWT.PUSH);
		column_setup.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.column_setup"));
		column_setup.setImage(SWTImageRepository.getImage("columns_setup.png"));
		column_setup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				showColumnEditorWindow();
			}
		});
		
		new JMThread(new JMRunnable() {
			Server x;
			public void JMRun() {
				for(Server server : server_manager.getServers()) {
					x = server;
					SWTThread.getDisplay().syncExec(new JMRunnable() {
						public void JMRun() {
							addServer(x);
						}});
				}
		    }
		}).start();;
	    
	}
	
	private ServerListStatus getSelectionStatus() {
		
		if ((getItems().length>0)&&(getSelectionCount()==0))
			return ServerListStatus.NO_SERVERS_SELECTED_DISCONNECTED;
			
		if ((getSelectionCount()==1)&&(servers_manager.getConnectedServer()==null))
			return ServerListStatus.SELECTED_DISCONNECTED_SERVER_NOT_CONNECTED; 

		if ((getSelectionCount()==1)&&(!getSelectedObject().isConnected())&&(servers_manager.getConnectedServer()!=null))
			return ServerListStatus.SELECTED_DISCONNECTED_SERVER_CONNECTED; 

		
		if ((getSelectionCount()==1)&&(getSelectedObject().isConnected()))
			return ServerListStatus.SELECTED_CONNECTED_SERVER;
		
		if ((getSelectionCount()>1)&&(servers_manager.getConnectedServer()==null))
			return ServerListStatus.SELECTED_MULTIPLE_SERVERS_NOT_CONNECTED;
		
		if (getSelectionCount()>1) {
			
			for(Server server : getSelectedObjects())
				if (server.isConnected())
					return ServerListStatus.SELECTED_MULTIPLE_SERVERS_WITH_ONE_CONNECTED;
			
			return ServerListStatus.SELECTED_MULTIPLE_SERVERS_CONNECTED;
		}
		
		return ServerListStatus.NO_SERVERS;
	}
	
	protected Menu getPopUpMenu() {
		ServerListStatus status = getSelectionStatus();
		
		SWTServerListWrapper wrapper = SWTServerListWrapper.getInstance();
		
		Menu result;
		
		switch (status) {
			
		case NO_SERVERS_SELECTED_DISCONNECTED : {
			no_selection_menu_disconnect.setEnabled(false);
			result =  no_selected_servers_menu;
			break;
		}
		case NO_SERVERS_SELECTED_CONNECTED : {
			no_selection_menu_disconnect.setEnabled(true);
			result = no_selected_servers_menu;
			break;
		}
		case SELECTED_DISCONNECTED_SERVER_NOT_CONNECTED :  {
			selected_server_connect.setEnabled(true);
			selected_server_disconnect.setEnabled(false);
			
			result = selected_server_menu;
			break;
		}
		case SELECTED_CONNECTED_SERVER : {
			selected_server_connect.setEnabled(false);
			selected_server_disconnect.setEnabled(true);
			result = selected_server_menu;
			break;
		}
		case SELECTED_DISCONNECTED_SERVER_CONNECTED : {
			selected_server_connect.setEnabled(true);
			selected_server_disconnect.setEnabled(true);
			result = selected_server_menu;
			break;
		}
		
		case SELECTED_MULTIPLE_SERVERS_NOT_CONNECTED : {
			server_connect_multiselect.setEnabled(true);
			server_disconnect_multiselect.setEnabled(false);
			
			result = selected_multiple_servers_menu;
			break;
		}
		
		case SELECTED_MULTIPLE_SERVERS_CONNECTED : {
			server_connect_multiselect.setEnabled(false);
			server_disconnect_multiselect.setEnabled(true);
			result = selected_multiple_servers_menu;
			break;
		}
		
		case SELECTED_MULTIPLE_SERVERS_WITH_ONE_CONNECTED : {
			server_connect_multiselect.setEnabled(false);
			server_disconnect_multiselect.setEnabled(true);
			result = selected_multiple_servers_menu;
			break;
		}
		default : result = no_servers_menu;
		
		}
		
		boolean contain_static = false, contain_non_static = false;
		
		for(Server server : getSelectedObjects())
			if (server.isStatic()) 
				contain_static = true;
			else
				contain_non_static = true;
		
		add_to_static_list.setEnabled(false);
		remove_from_static_list.setEnabled(false);
		multisel_add_to_static_list.setEnabled(false);
		multisel_remove_from_static_list.setEnabled(false);
		
		if (contain_static) {
			remove_from_static_list.setEnabled(true);
			multisel_remove_from_static_list.setEnabled(true);
		}
		
		if (contain_non_static) {
			add_to_static_list.setEnabled(true);
			multisel_add_to_static_list.setEnabled(true);
		}
		
		if (wrapper.isAutoconnecting()) {
			server_remove_all.setEnabled(false);
			server_remove_all_multiselect.setEnabled(false);
			selected_server_disconnect.setEnabled(true);
			selected_server_disconnect.setImage(SWTImageRepository.getImage("auto_connect_cancel.png"));
			selected_server_disconnect.setText(Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.cancel"));
			
			server_disconnect_multiselect.setEnabled(true);
			server_disconnect_multiselect.setImage(SWTImageRepository.getImage("auto_connect_cancel.png"));
			server_disconnect_multiselect.setText(Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.cancel"));
		} else {
			server_remove_all.setEnabled(true);
			server_remove_all_multiselect.setEnabled(true);
			selected_server_disconnect.setText(Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.disconnect_from"));
			selected_server_disconnect.setImage(SWTImageRepository.getImage("server_disconnect.png"));
			
			server_disconnect_multiselect.setText(Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.disconnect_from"));
			server_disconnect_multiselect.setImage(SWTImageRepository.getImage("server_disconnect.png"));
		}
		return result;

	}
	

	protected int compareObjects(Server object1, Server object2, int columnID, boolean order) {
		
		if (columnID == SWTConstants.SERVER_LIST_NAME_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getName", order);
		}
		
		if ((columnID == SWTConstants.SERVER_LIST_CC_COLUMN_ID)||(columnID == SWTConstants.SERVER_LIST_FLAG_COLUMN_ID)) {
			String country1 = CountryLocator.getInstance().getCountryName(object1.getAddress());
			String country2 = CountryLocator.getInstance().getCountryName(object2.getAddress());
			int result = country1.compareTo(country2);
			if (order)
				return result;
			else
				return Misc.reverse(result);
		}

		if (columnID == SWTConstants.SERVER_LIST_DESCRIPTION_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getDesc", order);
		}

		if (columnID == SWTConstants.SERVER_LIST_IP_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getAddress", order);
		}
		
		if (columnID == SWTConstants.SERVER_LIST_PING_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getPing", order);
		}
		
		if (columnID == SWTConstants.SERVER_LIST_USERS_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getNumUsers", order);
		}
		
		if (columnID == SWTConstants.SERVER_LIST_MAX_USERS_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getMaxUsers", order);
		}
		
		if (columnID == SWTConstants.SERVER_LIST_FILES_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getNumFiles", order);
		}
		
		if (columnID == SWTConstants.SERVER_LIST_SOFT_LIMIT_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getSoftLimit", order);
		}
	
		if (columnID == SWTConstants.SERVER_LIST_HARD_LIMIT_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getHardLimit", order);
		}
		
		if (columnID == SWTConstants.SERVER_LIST_VERSION_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getVersion", order);
		}
		
		if (columnID == SWTConstants.SERVER_LIST_STATIC_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "isStatic", order);
		}
		
		return 0;
	}

	private void addToStaticList() {
		final List<Server> list = getSelectedObjects();
		new JMThread(new JMRunnable() {
			public void JMRun() {
				for(Server server : list)
					server.setStatic(true);
				try {
					servers_manager.storeServerList();
				} catch (ServerManagerException e1) {
					SWTThread.getDisplay().asyncExec(new JMRunnable() {
						public void JMRun() {
							Utils.showWarningMessage(getShell(), Localizer._("mainwindow.serverlisttab.serverlist.server_list_store_error_title"), 
									Localizer._("mainwindow.serverlisttab.serverlist.server_list_store_error"));
							MainWindow.getLogger().error(Localizer._("mainwindow.logtab.error_serverlist_save_failed"));
						}
					});
				}}}).start();
	}
	
	private void removeFromStaticList() {
		final List<Server> list = getSelectedObjects();
		new JMThread(new JMRunnable() {
			public void JMRun() {
				
				for(Server server : list)
					server.setStatic(false);
				try {
					servers_manager.storeServerList();
				} catch (ServerManagerException e1) {
					SWTThread.getDisplay().asyncExec(new JMRunnable() {
						public void JMRun() {
							Utils.showWarningMessage(getShell(), Localizer._("mainwindow.serverlisttab.serverlist.server_list_store_error_title"), 
									Localizer._("mainwindow.serverlisttab.serverlist.server_list_store_error"));
							MainWindow.getLogger().error(Localizer._("mainwindow.logtab.error_serverlist_save_failed"));
						}
					});
				}}}).start();
	}
	
	private void removeSelectedServers() {
		final List<Server> list = getSelectedObjects();
		boolean result;
		if ( list.size()== 1)
			result = Utils.showConfirmMessage(getShell(),Localizer._("mainwindow.serverlisttab.serverlist.server_delete_confirm_title"), Localizer._("mainwindow.serverlisttab.serverlist.server_delete_confirm"));
		else
			result = Utils.showConfirmMessage(getShell(),Localizer._("mainwindow.serverlisttab.serverlist.server_delete_confirm_title"), Localizer._("mainwindow.serverlisttab.serverlist.servers_delete_confirm"));
		if (result) 
			new JMThread(new JMRunnable() {
				public void JMRun() {
					SWTServerListWrapper.getInstance().removeServer(list);
				}
				
			}).start();
			
	}
	
	private void clearServerList() {
		boolean returnvalue = Utils.showConfirmMessage(getShell(),Localizer._("mainwindow.serverlisttab.serverlist.clear_confirm_title"), Localizer._("mainwindow.serverlisttab.serverlist.clear_confirm"));
		if (returnvalue) {
			new JMThread( new JMRunnable() {
				public void JMRun() {
					SWTServerListWrapper.getInstance().clearServerList();
				}
			}).start();
		}
	}
	
	private void copyED2KLinks() {
		String str = "";
		List<Server> selected_servers = getSelectedObjects();
		for(Server server : selected_servers) {
			str+=server.getServerLink().getAsString()+System.getProperty("line.separator"); 
		}
		Utils.setClipBoardText(str);
	}
	
	private void pasteED2KLinks() {
		final String clipboard_content = Utils.getClipboardText();
		new JMThread( new JMRunnable() {
			public void JMRun() {
				List<ED2KServerLink> server_links = ED2KServerLink.extractLinks(clipboard_content);
				for(ED2KServerLink ed2k_link : server_links) {
					try {
						JMuleCoreFactory.getSingleton().getServerManager().newServer(ed2k_link);
					} catch (ServerManagerException e) {
						e.printStackTrace();
					}
					
				}
			}
		}).start();
		
	}
	
	
	private void showServerAddWindow() {
		ServerAddWindow add_server_window = new ServerAddWindow();
		add_server_window.getCoreComponents();
		add_server_window.initUIComponents();
	}
	
	public void addServer(final Server server) {
		addRow(server);
		
		if (!CountryLocator.getInstance().isServiceDown()) {
				Image image = SWTImageRepository.getFlagByAddress(server.getAddress(),default_flag_size);
				
				CountryFlagPainter painter = new CountryFlagPainter(image);
				
				TableItemCountryFlag table_item_painter = new TableItemCountryFlag(SWTPreferences.getDefaultColumnOrder(SWTConstants.SERVER_LIST_FLAG_COLUMN_ID),painter);
				addCustumControl(getItemCount()-1, table_item_painter);
		}
		
		//updateLine(server);		
	}
	
	public void removeServer(Server server) {
		removeRow(server);
	}
	
	public void serverDisconnected(Server server) {
		updateRow(server);
	}
	
	public void updateRow(Server server) {
		if (server.isDown()) {
			setRowImage(server,SWTConstants.SERVER_LIST_NAME_COLUMN_ID, SWTImageRepository.getImage("server_error.png"));
			setForegroundColor(server, server_down_color);
			
			int id = getObjectID(server);
			if ((id)%2==0)
				setBackgroundColor(server, ROW_ALTERNATE_COLOR_2);
			else
				setBackgroundColor(server, ROW_ALTERNATE_COLOR_1);
		} else
		if (server.isConnected()) {
				setRowImage(server,SWTConstants.SERVER_LIST_NAME_COLUMN_ID,SWTImageRepository.getImage("server_connected.png"));
				setForegroundColor(server,SWTThread.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				setBackgroundColor(server,server_connected_color);
			}
			else {
				setForegroundColor(server,server_default_color);
				
				int id = getObjectID(server);
				if ((id)%2==0)
					setBackgroundColor(server, ROW_ALTERNATE_COLOR_2);
				else
					setBackgroundColor(server, ROW_ALTERNATE_COLOR_1);
				
				
				setRowImage(server,SWTConstants.SERVER_LIST_NAME_COLUMN_ID, SWTImageRepository.getImage("server.png"));
			}
				
		setRowText(server, SWTConstants.SERVER_LIST_IP_COLUMN_ID, server.getAddress()+":"+server.getPort());
		if (!CountryLocator.getInstance().isServiceDown()) {
			String country_code = CountryLocator.getInstance().getCountryCode(server.getAddress());
			setRowText(server, SWTConstants.SERVER_LIST_CC_COLUMN_ID, country_code);
		}
		
		setRowText(server, SWTConstants.SERVER_LIST_NAME_COLUMN_ID, server.getName());
		setRowText(server, SWTConstants.SERVER_LIST_DESCRIPTION_COLUMN_ID,server.getDesc());
		setRowText(server, SWTConstants.SERVER_LIST_PING_COLUMN_ID,server.getPing()+"");
		setRowText(server, SWTConstants.SERVER_LIST_USERS_COLUMN_ID,NumberFormatter.formatSizeHumanReadable(server.getNumUsers()));
		setRowText(server, SWTConstants.SERVER_LIST_MAX_USERS_COLUMN_ID,NumberFormatter.formatSizeHumanReadable(server.getMaxUsers()));
		setRowText(server, SWTConstants.SERVER_LIST_FILES_COLUMN_ID,NumberFormatter.formatSizeHumanReadable(server.getNumFiles()));
		setRowText(server, SWTConstants.SERVER_LIST_SOFT_LIMIT_COLUMN_ID,NumberFormatter.formatSizeHumanReadable(server.getSoftLimit()));
		setRowText(server, SWTConstants.SERVER_LIST_HARD_LIMIT_COLUMN_ID,NumberFormatter.formatSizeHumanReadable(server.getHardLimit()));
		setRowText(server, SWTConstants.SERVER_LIST_VERSION_COLUMN_ID,server.getVersion()+"");
		
		if (server.isStatic())
			setRowText(server, SWTConstants.SERVER_LIST_STATIC_COLUMN_ID,_._("mainwindow.serverlisttab.serverlist.column.static.yes"));
		else
			setRowText(server, SWTConstants.SERVER_LIST_STATIC_COLUMN_ID,_._("mainwindow.serverlisttab.serverlist.column.static.no"));
	}
	
	public void refresh() {
		// Refresh server's data
		if (isDisposed()) return ;
		for(Server server : servers_manager.getServers()) {
			BufferedTableRow row = getRow(server);
			if (row == null) continue;
			if (!row.isVisible()) continue;
			if (is_sorted)
				sortColumn(last_sort_column,last_sort_dir);
			updateRow(server);
		}

	}	
}
