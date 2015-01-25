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

import java.awt.BorderLayout;
import java.awt.Component;
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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMThread;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.edonkey.ED2KServerLink;
import org.jmule.core.servermanager.Server;
import org.jmule.core.servermanager.ServerManager;
import org.jmule.core.servermanager.ServerManagerSingleton;
import org.jmule.core.utils.GeneralComparator;
import org.jmule.countrylocator.CountryLocator;
import org.jmule.ui.FlagPack;
import org.jmule.ui.IDialog;
import org.jmule.ui.UIConstants;
import org.jmule.ui.localizer._;
import org.jmule.ui.maintabs.serverlist.INewServerDialog;
import org.jmule.ui.swing.ImgRep;
import org.jmule.ui.swing.SwingGUIUpdater;
import org.jmule.ui.swing.SwingUtils;
import org.jmule.ui.swing.maintabs.serverlist.SNewServerDialog;
import org.jmule.ui.swing.maintabs.serverlist.ServerDetailsDialog;
import org.jmule.ui.swing.models.ServerListTableModel;
import org.jmule.ui.utils.NumberFormatter;


/**
 * 
 * @author javajox
 * @version $$Revision: 1.7 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2009/09/27 14:20:00 $$
 */
public class ServerListTable extends JMTable {

	CountryLocator country_locator = CountryLocator.getInstance();
	
	// =============== Column cell renderers ==============================
	class ServerNameTableCellRenderer extends ServerListTableCellRenderer  {

		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {

			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
	        if(!server.isDown()) 
	          if(server.isConnected()) this.setIcon(ImgRep.getIcon("server_connected.png"));	
	          else this.setIcon(ImgRep.getIcon("server.png"));
	        else this.setIcon(ImgRep.getIcon("server_error.png"));
	        
			URL flag_icon_url = FlagPack.getFlagAsURLByIP(server.getAddress(), FlagPack.FlagSize.S18x25);
			String name         = ( server.getName() != "" ) ? server.getName() : null;
			String ip           = ( server.getAddress() != "" ) ? server.getAddress() : null;
			String ver          = ( server.getVersion() != "") ? " ver. " + server.getVersion() + "<br>" : null;
			String desc         = ( server.getDesc() != "" ) ? server.getDesc() : null;
			String ping         = ( server.getPing() > 0 ) ? "<tr><td>Ping</td><td>" + server.getPing() + "</td></tr>" + "" : null;
			String users        = ( server.getNumUsers() > 0 ) ?  "<tr><td>Users</td><td>" + NumberFormatter.formatSizeHumanReadable(server.getNumUsers()) + " (max " + NumberFormatter.formatSizeHumanReadable(server.getMaxUsers()) +")"+ "</td></tr>" + "" : null;
			//String max_users    = ( server.getMaxUsers() > 0 ) ? server.getMaxUsers() + "" : null;
			String files        = ( server.getNumFiles() > 0 ) ? "<tr><td>Files</td><td>" + NumberFormatter.formatSizeHumanReadable(server.getNumFiles()) + " Limits [soft: " + NumberFormatter.formatSizeHumanReadable(server.getSoftLimit()) + ", hard: " + NumberFormatter.formatSizeHumanReadable(server.getHardLimit()) +"]" +"</td></tr>" + "" : null;
			//String soft_limit   = ( server.getSoftLimit() > 0 ) ? server.getSoftLimit() + "" : null;
			//String hard_limit   = ( server.getHardLimit() > 0 ) ? server.getHardLimit() + "" : null;
			//String down         = ( server.isDown() ? "<tr><td>Status</td><td><font color=\"red\"><b>DOWN</b></font></td></tr>" : null );

			this.setText(" " + server.getName());
			String text =   "<html>" + "<body " + (server.isDown()?"color=\"#c0c0c0\"":"") + ">" +
			                           ( name != null ? name : "" ) + 
			                           ( ver != null ? ver : "" ) +
			                           ( desc != null ? desc : "" ) +
			              "<hr>" +
			              "<table>" + ( ( ip != null || flag_icon_url != null ) ? ("<tr><td>IP</td><td>" + ( flag_icon_url != null ? "<img src=\""+flag_icon_url+"\">&nbsp;" : "" ) + ( ip != null ? ip : "") + "</td></tr>") : "" ) +
			                          ( ping != null ? ping  : "" ) +
			                          ( users != null ? users : "" ) +
			                          ( files != null ? files : "" ) + 
			              "</table>"+
			              "</body>" +
			              "</html>";
			//System.out.println(text);
			this.setToolTipText(text);
			this.setHorizontalAlignment(SwingConstants.LEFT);
			return this;
		}
	}
	// --------------------------------------------------------------------------
	class CCTableCellRenderer extends ServerListTableCellRenderer  {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setText(!country_locator.isServiceDown() ? 
					      country_locator.getCountryCode(server.getAddress()) : "Unknown");
			if( !country_locator.isServiceDown() )
				 this.setToolTipText(country_locator.getCountryName(server.getAddress()));
			return this;
		}
	}
	// ---------------------------------------------------------------------------
	class FlagTableCellRenderer extends ServerListTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			Icon flag_icon = FlagPack.getFlagAsIconByIP(server.getAddress(), FlagPack.FlagSize.S25x15);
			if( flag_icon != null ) this.setIcon(flag_icon);
			if( !country_locator.isServiceDown() )
				 this.setToolTipText(country_locator.getCountryName(server.getAddress()));
			return this;
		}
	}
	// --------------------------------------------------------------------------
	class StaticTableCellRenderer extends ServerListTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			this.setText(server.isStatic()?"Yes":"No");
			return this;
		}
	}
	// ---------------------------------------------------------------------------
	class DescTableCellRenderer extends ServerListTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.LEFT);
			this.setText(" " + server.getDesc());
			this.setToolTipText(server.getDesc());
			return this;
		}
	}
	// ---------------------------------------------------------------------------
	class PingTableCellRenderer extends ServerListTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setText(server.getPing() + " ");
            this.setHorizontalAlignment(SwingConstants.RIGHT);
			return this;
		}
	}
	// -----------------------------------------------------------------------------
	class UsersTableCellRenderer extends ServerListTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setText(NumberFormatter.formatSizeHumanReadable(server.getNumUsers()) + " ");
            this.setHorizontalAlignment(SwingConstants.RIGHT);
			return this;
		}
	}
	// -------------------------------------------------------------------------------
	class MaxUsersTableCellRenderer extends ServerListTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setText(NumberFormatter.formatSizeHumanReadable(server.getMaxUsers()) + " ");
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			return this;
		}
	}
	// --------------------------------------------------------------------------------
	class FilesTableCellRenderer extends ServerListTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setText(NumberFormatter.formatSizeHumanReadable(server.getNumFiles()) + " ");
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			return this;
		}
	}
	// --------------------------------------------------------------------------------
	class SoftLimitTableCellRenderer extends ServerListTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setText(NumberFormatter.formatSizeHumanReadable(server.getSoftLimit()) + " ");
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			return this;
		}
	}
	// ----------------------------------------------------------------------------------
	class HardLimitTableCellRenderer extends ServerListTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setText(NumberFormatter.formatSizeHumanReadable(server.getHardLimit()) + " ");
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			return this;
		}
	}
	// -----------------------------------------------------------------------------------
	class VersionTableCellRenderer extends ServerListTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.LEFT);
            this.setText(" "+server.getVersion());
			return this;
		}
	}
	// ------------------------------------------------------------------------------------
    class IPTableCellRenderer extends ServerListTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.LEFT);
            this.setText(" "+server.getAddress() + ":" + server.getPort());
			return this;
		}
	}
    // --------------------------------------------------------------------------------------
	class DownTableCellRenderer extends ServerListTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setText(server.isDown()?"Yes":"No");
            this.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			return this;
		}
	}
	
	private JMuleCore _core = JMuleCoreFactory.getSingleton();
	private ServerManager _server_manager = _core.getServerManager();
	private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	private ServerListTableModel server_list_model;
	private SwingGUIUpdater _gui_updater = SwingGUIUpdater.getInstance();
	private final JXTable _this;
	private ServerDetailsDialog server_details;
	
	public ServerListTable(JFrame parent) {
        super(parent);
		init();
		_this = this;
	}
	
	//public void refresh() {
	
			 //_this.updateUI();
			// TableModel model = _this.getModel();
			 //((AbstractTableModel)model).fireTableDataChanged();
			 //TableModel model = _this.getModel();
			 //((AbstractTableModel)model).fireTableRowsUpdated(
	         //           0, model.getRowCount() );
		//	 repaint();
		
	//}
	
	private void init() {
        
		// Create columns for server manager
		TableColumnExt name = new TableColumnExt();
		name.setIdentifier(UIConstants.SERVER_LIST_NAME_COLUMN_ID);
		name.setModelIndex(ServerListTableModel.SERVER_NAME);
		name.setVisible(_pref.isColumnVisible(UIConstants.SERVER_LIST_NAME_COLUMN_ID));
		name.setHeaderValue("Name");	
		name.setCellRenderer(new ServerNameTableCellRenderer());
		name.setComparator(new GeneralComparator("getName"));
		
		table_columns.add(name);
		//server_tab_column_model.addColumn( name );
		
		TableColumnExt cc = new TableColumnExt();
		cc.setIdentifier(UIConstants.SERVER_LIST_CC_COLUMN_ID);
		cc.setModelIndex(ServerListTableModel.CC);
		cc.setVisible(_pref.isColumnVisible(UIConstants.SERVER_LIST_CC_COLUMN_ID));
		cc.setHeaderValue("CC");
		cc.setCellRenderer(new CCTableCellRenderer());
		
		table_columns.add(cc);
		
		TableColumnExt flag = new TableColumnExt();
		flag.setIdentifier(UIConstants.SERVER_LIST_FLAG_COLUMN_ID);
		flag.setModelIndex(ServerListTableModel.FLAG);
		flag.setVisible(_pref.isColumnVisible(UIConstants.SERVER_LIST_FLAG_COLUMN_ID));
		flag.setHeaderValue("Flag");
		flag.setCellRenderer(new FlagTableCellRenderer());
		
		table_columns.add(flag);
		
		TableColumnExt ip = new TableColumnExt();
		ip.setModelIndex(ServerListTableModel.SERVER_IP);
		ip.setIdentifier(UIConstants.SERVER_LIST_IP_COLUMN_ID);
		ip.setVisible(_pref.isColumnVisible(UIConstants.SERVER_LIST_IP_COLUMN_ID));
		ip.setHeaderValue("IP");
		ip.setCellRenderer(new IPTableCellRenderer());
		ip.setComparator(new GeneralComparator("getAddressAsInt"));

		table_columns.add(ip);
		//server_tab_column_model.addColumn( ip );
		
		TableColumnExt desc = new TableColumnExt();
		desc.setModelIndex(ServerListTableModel.DESCRIPTION);
		desc.setIdentifier(UIConstants.SERVER_LIST_DESCRIPTION_COLUMN_ID);
		desc.setVisible(_pref.isColumnVisible(UIConstants.SERVER_LIST_DESCRIPTION_COLUMN_ID));
		desc.setHeaderValue("Description");
		desc.setCellRenderer(new DescTableCellRenderer());
		desc.setComparator(new GeneralComparator("getDesc"));
		
		table_columns.add(desc);
		//server_tab_column_model.addColumn( desc );
		
		TableColumnExt ping = new TableColumnExt();
		ping.setModelIndex(ServerListTableModel.PING);
		ping.setIdentifier(UIConstants.SERVER_LIST_PING_COLUMN_ID);
		ping.setVisible(_pref.isColumnVisible(UIConstants.SERVER_LIST_PING_COLUMN_ID));
		ping.setHeaderValue("Ping");
		ping.setCellRenderer(new PingTableCellRenderer());
		ping.setComparator(new GeneralComparator("getPing"));
		
		table_columns.add(ping);
		//server_tab_column_model.addColumn( ping );
		
		TableColumnExt users = new TableColumnExt();
		users.setModelIndex(ServerListTableModel.USERS);
		users.setIdentifier(UIConstants.SERVER_LIST_USERS_COLUMN_ID);
		users.setVisible(_pref.isColumnVisible(UIConstants.SERVER_LIST_USERS_COLUMN_ID));
		users.setHeaderValue("Users");
		users.setCellRenderer(new UsersTableCellRenderer());
		users.setComparator(new GeneralComparator("getNumUsers"));
		
		table_columns.add(users);
		//server_tab_column_model.addColumn( users );
		
		TableColumnExt max_users = new TableColumnExt();
		max_users.setModelIndex(ServerListTableModel.MAX_USERS);
		max_users.setIdentifier(UIConstants.SERVER_LIST_MAX_USERS_COLUMN_ID);
		max_users.setVisible(_pref.isColumnVisible(UIConstants.SERVER_LIST_MAX_USERS_COLUMN_ID));
		max_users.setHeaderValue("Max users");
		max_users.setCellRenderer(new MaxUsersTableCellRenderer());
		max_users.setComparator(new GeneralComparator("getMaxUsers"));
		
		table_columns.add(max_users);
		//server_tab_column_model.addColumn(max_users);
		
		TableColumnExt files = new TableColumnExt();
		files.setModelIndex(ServerListTableModel.FILES);
		files.setIdentifier(UIConstants.SERVER_LIST_FILES_COLUMN_ID);
		files.setVisible(_pref.isColumnVisible(UIConstants.SERVER_LIST_FILES_COLUMN_ID));
		files.setHeaderValue("Files");
		files.setCellRenderer(new FilesTableCellRenderer());
		files.setComparator(new GeneralComparator("getNumFiles"));
		
		table_columns.add(files);
		//server_tab_column_model.addColumn(files);
		
		TableColumnExt soft_limit = new TableColumnExt();
		soft_limit.setModelIndex(ServerListTableModel.SOFT_LIMIT);
		soft_limit.setIdentifier(UIConstants.SERVER_LIST_SOFT_LIMIT_COLUMN_ID);
		soft_limit.setVisible(_pref.isColumnVisible(UIConstants.SERVER_LIST_SOFT_LIMIT_COLUMN_ID));
		soft_limit.setHeaderValue("Soft limit");
		soft_limit.setCellRenderer(new SoftLimitTableCellRenderer());
		soft_limit.setComparator(new GeneralComparator("getSoftLimit"));
		
		table_columns.add(soft_limit);
		//server_tab_column_model.addColumn(soft_limit);
		
		TableColumnExt hard_limit = new TableColumnExt();
		hard_limit.setModelIndex(ServerListTableModel.HARD_LIMIT);
		hard_limit.setIdentifier(UIConstants.SERVER_LIST_HARD_LIMIT_COLUMN_ID);
		hard_limit.setVisible(_pref.isColumnVisible(UIConstants.SERVER_LIST_HARD_LIMIT_COLUMN_ID));
		hard_limit.setHeaderValue("Hard limit");
		hard_limit.setCellRenderer(new HardLimitTableCellRenderer());
		hard_limit.setComparator(new GeneralComparator("getHardLimit"));
		
		table_columns.add(hard_limit);
		//server_tab_column_model.addColumn(hard_limit);
		
		TableColumnExt version = new TableColumnExt();
		version.setModelIndex(ServerListTableModel.VERSION);
		version.setIdentifier(UIConstants.SERVER_LIST_VERSION_COLUMN_ID);
		version.setVisible(_pref.isColumnVisible(UIConstants.SERVER_LIST_VERSION_COLUMN_ID));
		version.setHeaderValue("Version");
		version.setCellRenderer(new VersionTableCellRenderer());
		version.setComparator(new GeneralComparator("getVersion"));
	
		//version.setVisible(false);
		
		table_columns.add(version);
		//server_tab_column_model.addColumn(version);
		
		TableColumnExt static_col = new TableColumnExt();
		static_col.setModelIndex(ServerListTableModel.STATIC);
		static_col.setIdentifier(UIConstants.SERVER_LIST_STATIC_COLUMN_ID);
		static_col.setVisible(_pref.isColumnVisible(UIConstants.SERVER_LIST_STATIC_COLUMN_ID));
		static_col.setHeaderValue("Static");
		static_col.setCellRenderer(new StaticTableCellRenderer());
		static_col.setComparator(new GeneralComparator("isStatic"));
		
		table_columns.add(static_col);
		
		TableColumnExt down = new TableColumnExt();
		down.setModelIndex(ServerListTableModel.DOWN);
		down.setIdentifier(UIConstants.SERVER_LIST_DOWN_COLUMN_ID);
		down.setVisible(_pref.isColumnVisible(UIConstants.SERVER_LIST_DOWN_COLUMN_ID));
		down.setHeaderValue("Down");
		down.setCellRenderer(new DownTableCellRenderer());
		down.setComparator(new GeneralComparator("isDown"));
		
		table_columns.add(down);
		
		super.buildColumns(new ServerListTableModel());
				
		//TableColumnModel server_tab_column_model = new DefaultTableColumnModel();
		
		//for(TableColumnExt column : table_columns) {
		//	server_tab_column_model.addColumn(column);
		//}
		
		//server_list_model = new ServerListTableModel();
		//this.setModel(server_list_model);
		//this.setColumnModel(server_tab_column_model);
		
		//TableColumnExt column = (TableColumnExt)this.getColumnModel().getColumn(1);
		//column.setVisible(false);
		//this.setColumnControlVisible(true);
		//this.getColumnExt((Object)UIConstants.SERVER_LIST_VERSION_COLUMN_ID).setVisible(false);
		//this.getColumnExt(2).setVisible(false);
		final TableColumnModel column_model = this.getColumnModel();
		class PopupListener extends MouseAdapter {
			 JMenuItem connect_to, disconnect, add, remove_selected, remove_all, properties,
			           add_to_static_list, remove_from_static_list,
			           paste_ed2k_links, copy_ed2k_links, column_setup;
			 public PopupListener() {
				    connect_to = new JMenuItem("Connect to",ImgRep.getIcon("server_connect2.png"));
					connect_to.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							executeSelectedServersOp(ServersOp.CONNECT_TO);
						}
					});
					
					disconnect = new JMenuItem("Disconnect",ImgRep.getIcon("server_connect2.png"));
					disconnect.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							System.out.println("Disconnect to selected");
						}
					});
					
					add = new JMenuItem("Add",ImgRep.getIcon("server_add.png"));
					add.addActionListener(new ActionListener() {	
						public void actionPerformed(ActionEvent e) {
							INewServerDialog<String, Integer> server_dialog = new SNewServerDialog(parent);
							SwingUtils.setWindowLocationRelativeTo((JDialog)server_dialog,parent);
							server_dialog.setVisible(true);
							if(server_dialog.getDialogAction()==IDialog.DialogAction.OK) {
							   try {	
									  _server_manager.newServer(server_dialog.getServerIP(), 
										                        server_dialog.getServerPort());
							   }catch(Throwable cause) {
								   cause.printStackTrace();
							   }
							}
						}
					});
					
					remove_selected = new JMenuItem("Remove selected",ImgRep.getIcon("server_delete.png"));
					remove_selected.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							executeSelectedServersOp(ServersOp.REMOVE_SELECTED_FROM_LIST);
						}
					});
					
					remove_all = new JMenuItem("Remove all", ImgRep.getIcon("remove_all.png"));
					remove_all.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							executeSelectedServersOp(ServersOp.REMOVE_ALL_FROM_LIST);
						}
					});
					
					add_to_static_list = new JMenuItem(_._("mainwindow.serverlisttab.serverlist.popupmenu.add_to_static_list"),
							                 ImgRep.getIcon("list_add.png"));
					add_to_static_list.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							executeSelectedServersOp(ServersOp.MAKE_SELECTED_STATIC);
						}
					});
					
					remove_from_static_list = new JMenuItem(_._("mainwindow.serverlisttab.serverlist.popupmenu.remove_from_static_list"),
	                                              ImgRep.getIcon("list_remove.png"));
					remove_from_static_list.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							executeSelectedServersOp(ServersOp.MAKE_SELECTED_NON_STATIC);
						}
					});
					
					properties = new JMenuItem("Properties", ImgRep.getIcon("properties.png"));
					properties.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Server[] servers = getSelectedServers();
							server_details = new ServerDetailsDialog(parent, servers[0]);
							SwingUtils.setWindowLocationRelativeTo(server_details, parent);
							server_details.setVisible(true);
						}
					});
					
					paste_ed2k_links = new JMenuItem("Paste ED2K links", ImgRep.getIcon("ed2k_link_paste.png"));
					paste_ed2k_links.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
						  try { 	
							 Object clipboard_contents = clipboard.getData(DataFlavor.stringFlavor);
							 //System.out.println(clipboard_contents);
							 List<ED2KServerLink> server_links = ED2KServerLink.extractLinks(clipboard_contents.toString());
							 for(ED2KServerLink server_link : server_links) {
								 //System.out.println(server_link);
								 _server_manager.newServer(server_link);
							 }
						  }catch(Throwable t) {
							  t.printStackTrace();
						  }
						}
					});
					
					copy_ed2k_links = new JMenuItem("Copy ED2K links", ImgRep.getIcon("ed2k_link.png"));
					copy_ed2k_links.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							clipboard.setContents(new Transferable() {
								public Object getTransferData(DataFlavor flavor)
										throws UnsupportedFlavorException,
										IOException {
									String ed2k_links = "";
									Server[] selected_servers = getSelectedServers();
									for(Server server : selected_servers) {
										ed2k_links += server.getServerLink().getAsString() + System.getProperty("line.separator");
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
					
					// -------------------- Column setup ----------------------------------------------
					column_setup = new JMenuItem("Column setup", ImgRep.getIcon("columns_setup.png"));
					column_setup.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							JDialog col_set_frame = new JDialog(parent,"Column setup",true);
							JPanel right_panel = new JPanel(); // where the up & down buttons are located
							JPanel bottom_panel = new JPanel(); // Cancel, Defaults, Ok buttons
							JPanel center_panel = new JPanel(); // the jtable
							JButton up_button = new JButton("Up");
							JButton down_button = new JButton("Down");
							JButton ok_button = new JButton("OK");
							JButton cancel_button = new JButton("Cancel");
							JButton apply_button = new JButton("Apply");
							right_panel.setLayout(new GridLayout(2,1));
							right_panel.add(up_button);
							right_panel.add(down_button);
							bottom_panel.setLayout(new GridLayout(1,3));
							bottom_panel.add(ok_button);
							bottom_panel.add(apply_button);
							bottom_panel.add(cancel_button);
							center_panel.setLayout(new GridLayout(1,1));
							col_set_frame.setSize(400, 300);
							col_set_frame.setLayout(new BorderLayout());
							col_set_frame.add(center_panel,BorderLayout.CENTER);
							col_set_frame.add(right_panel, BorderLayout.EAST);
							col_set_frame.add(bottom_panel, BorderLayout.SOUTH);
							final JTable col_set_table = new JTable();
							//DefaultTableModel col_set_table_model = new DefaultTableModel();
							Object[][] col_set_rows_cols = new Object[14][3];
							int i = 0;
							// col name -> col obj mapping (easy way to map col names to master cols)
							final Map<String,TableColumnExt> cols = new Hashtable<String,TableColumnExt>();
							for(TableColumnExt column : table_columns) {
								if(column.isVisible()) col_set_rows_cols[i][0] = true;
								else col_set_rows_cols[i][0] = false;
								col_set_rows_cols[i][1] = column.getHeaderValue();
								cols.put(column.getHeaderValue().toString(), column);
								++i;
							}
							col_set_table.setModel(new DefaultTableModel(col_set_rows_cols,
									new String[] {
									"Visibility", "Name", "Description"
							})
							{
					            Class[] types = new Class [] {
					                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class
					            };

					            public Class getColumnClass(int columnIndex) {
					                return types [columnIndex];
					            }
					        });
							JScrollPane scroll_pane = new JScrollPane();
							scroll_pane.setViewportView(col_set_table);
							center_panel.add(scroll_pane);
							SwingUtils.setWindowLocationRelativeTo(col_set_frame, parent);
							apply_button.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent event) {
                                    for(int i=0; i<14; i++) {
                                    	boolean visibility = Boolean.parseBoolean(col_set_table.getModel().getValueAt(i, 0).toString());
                                    	String column_name = col_set_table.getModel().getValueAt(i, 1).toString();
                                    	//cols.get(column_name).setVisible(visibility);
                                    	if(visibility == false) column_model.removeColumn(cols.get(column_name));
                                    	System.out.println("Column from set " + cols.get(column_name).getHeaderValue());
                                    	System.out.println("<--->" + column_name + "<---->" + visibility);
                                    }
								}
							});
							col_set_frame.setVisible(true);
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
			    	 //ServerManager _server_manager = ServerManagerFactory.getInstance();
			    	// Server connected_server = _server_manager.getConnectedServer();
			    	 //int[] selected_rows = ( _server_manager.getServersCount() !=0 ) ? getSelectedRows() : new int[0];
			    	 //int[] selected_rows = getSelectedRows();
			    	 //for(int i : selected_rows) {
			    	//	 System.out.println("Index of selected row = " + i);
			    		// System.out.println("Row model index = " + convertRowIndexToModel(i));
			    	// }
			    	// Server[] servers = getServersByIndexes(selected_rows);
			    	// for(Server server : servers) {
			    	//	 System.out.println(server);
			    	// }
			    	JPopupMenu popup_menu = null;
			 		switch( whichCondition() ) {
			 		
			 		  case A : popup_menu = new JPopupMenu();
	 		                   popup_menu.add(connect_to);
			 		           popup_menu.add(disconnect);
			 		           popup_menu.addSeparator();
			 		           popup_menu.add(add);
	 		                   popup_menu.add(remove_selected);
	 		                   popup_menu.add(remove_all);
			 		           popup_menu.addSeparator();
			 		           popup_menu.add(copy_ed2k_links);
			 		           popup_menu.add(paste_ed2k_links);
			 		           popup_menu.addSeparator();
			 		           popup_menu.add(add_to_static_list);
			 		           popup_menu.add(remove_from_static_list);
			 		          // popup_menu.addSeparator();
			 		          // popup_menu.add(column_setup);
			 		           popup_menu.addSeparator();
			 		           popup_menu.add(properties); break;
			 		  case B : popup_menu = new JPopupMenu();
	 		                   popup_menu.add(remove_selected);
	 		                   popup_menu.add(remove_all);
	 		                   popup_menu.add(add);
	 		                   popup_menu.add(disconnect);
			 		           popup_menu.addSeparator();
	 		                   popup_menu.add(copy_ed2k_links);
	 		                   popup_menu.add(paste_ed2k_links);
			 		           popup_menu.addSeparator();
			 		           popup_menu.add(add_to_static_list);
			 		           popup_menu.add(remove_from_static_list);
			 		         //  popup_menu.addSeparator();
	 		                 //  popup_menu.add(column_setup); 
			 		           break; 
			 		  case D : popup_menu = new JPopupMenu();
			 		           popup_menu.add(connect_to);
			 		           popup_menu.addSeparator();
	 		                   popup_menu.add(add);
	 		                   popup_menu.add(remove_selected);
	 		                   popup_menu.add(remove_all);
	 		                   popup_menu.addSeparator();
	 		                   popup_menu.add(copy_ed2k_links);
	 		                   popup_menu.add(paste_ed2k_links);	 		                   
	 		                   popup_menu.addSeparator();
			 		           popup_menu.add(add_to_static_list);
			 		           popup_menu.add(remove_from_static_list);
			 		        //   popup_menu.addSeparator();
	 		                //   popup_menu.add(column_setup);
	 		                   popup_menu.addSeparator();
	 		                   popup_menu.add(properties); break;
			 		  case E : popup_menu = new JPopupMenu();
			 		           popup_menu.add(remove_selected);
	 		                   popup_menu.add(add);
	 		                   popup_menu.add(remove_all);
	 		                   popup_menu.addSeparator();
	 		                   popup_menu.add(copy_ed2k_links);
	 		                   popup_menu.add(paste_ed2k_links);
	 		                   popup_menu.addSeparator();
	 		                   popup_menu.add(add_to_static_list);
			 		           popup_menu.add(remove_from_static_list);
			 		        //   popup_menu.addSeparator();
	 		               //    popup_menu.add(column_setup); 
			 		           break;
			 		  case F : popup_menu = new JPopupMenu();
	 		                   popup_menu.add(add);
	 		                   popup_menu.add(paste_ed2k_links);
	 		                   popup_menu.addSeparator();
	 		                   popup_menu.add(column_setup); break;
			 		  case G : popup_menu = new JPopupMenu();
			 		           popup_menu.add(remove_selected);
	 		                   popup_menu.add(add);
	 		                   popup_menu.add(remove_all);
	 		                   popup_menu.add(disconnect);
	 		                   popup_menu.addSeparator();
	 		                   popup_menu.add(copy_ed2k_links);
	 		                   popup_menu.add(paste_ed2k_links);
	 		                   popup_menu.addSeparator();
			 		           popup_menu.add(add_to_static_list);
			 		           popup_menu.add(remove_from_static_list);
			 		        //   popup_menu.addSeparator();
	 		                 //  popup_menu.add(column_setup);
	 		                   popup_menu.addSeparator();
	 		                   popup_menu.add(properties); break;
			 		  case H : popup_menu = new JPopupMenu();
			 		           popup_menu.add(remove_selected);
	 		                   popup_menu.add(add);
	 		                   popup_menu.add(remove_all);
	 		                   popup_menu.add(disconnect);
	 		                   popup_menu.addSeparator();
	 		                   popup_menu.add(copy_ed2k_links);
	 		                   popup_menu.add(paste_ed2k_links);
	 		                   popup_menu.addSeparator();
			 		           popup_menu.add(add_to_static_list);
			 		           popup_menu.add(remove_from_static_list);
			 		        //   popup_menu.addSeparator();
	 		                //   popup_menu.add(column_setup); break;
			 		 
			 		}
			 		
			 		// set enabled/disabled for static list commands
			 		Server[] selected_servers = getSelectedServers();
			 		add_to_static_list.setEnabled(true);
			 		remove_from_static_list.setEnabled(true);
			 		boolean add_to_static_enabled = true;
			 		boolean remove_from_static_enabled = true;
			 		for(Server server : selected_servers) {
			 			if(server.isStatic()) {
			 				add_to_static_enabled = false;
			 			} else {
			 				remove_from_static_enabled = false;
			 			}
			 		}
			 		if( ( add_to_static_enabled == false ) && ( remove_from_static_enabled != false ) ) {
	
			 			add_to_static_list.setEnabled(false);
			 		}
			 		if( ( add_to_static_enabled != false ) && ( remove_from_static_enabled == false ) ) {

			 			remove_from_static_list.setEnabled(false);
			 		}
			    	popup_menu.show(e.getComponent(), e.getX(), e.getY());
			     }
			 }
		}
		this.addMouseListener(new PopupListener());
	
		packColumns(this,2);
		
		if(this.getModel().getRowCount() != 0 ) this.setRowSelectionInterval(0,0);
	}
	
	private enum ServersOp {
		MAKE_SELECTED_STATIC,
		MAKE_SELECTED_NON_STATIC,
		REMOVE_SELECTED_FROM_LIST,
		REMOVE_ALL_FROM_LIST,
		CONNECT_TO
	}
	
	private void executeSelectedServersOp(ServersOp operation) {
	  final ServersOp foperation = operation;	
	  (new JMThread(new JMRunnable() {
		public void JMRun() {
		  if(foperation == ServersOp.REMOVE_ALL_FROM_LIST) {
			  _server_manager.clearServerList();
			  return;
		  }
		  Server[] selected_servers = getSelectedServers();
		  // if the foperation == ServersOp.CONNECT_TO we suppose that only one server is selected
		  // that is located at selected_servers[0]
		  if(foperation == ServersOp.CONNECT_TO) {
			  try {
				_server_manager.connect(selected_servers[0]);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		  }
		  for(Server server : selected_servers) {
			 try {
			  switch( foperation ) {
			    case MAKE_SELECTED_STATIC : if( !server.isStatic() ) server.setStatic(true); break;
			    case MAKE_SELECTED_NON_STATIC : if( server.isStatic() ) server.setStatic(false); break;
		        case REMOVE_SELECTED_FROM_LIST : _server_manager.removeServer(server); break;
	          }
			 }catch( Throwable cause ) {
				 cause.printStackTrace();
			 }
	      }
	      if( ( foperation == ServersOp.MAKE_SELECTED_STATIC ) || 
	    	  ( foperation == ServersOp.MAKE_SELECTED_NON_STATIC ) ) {
			  try {
				 _server_manager.storeServerList();
			  }catch(Throwable t) {
				 t.printStackTrace();
			  }
	      }
	   }
	  })).start();
	}
	
	// we override the getSelectedRows from JTable because it returns the last selected rows when the table has 0 rows
	// if the JTable is cleared, it should return 0 selected rows, maybe a bug or my misunderstanding
	public int[] getSelectedRows() {
		return ( ( _server_manager.getServersCount() !=0 ) ? super.getSelectedRows() : new int[0] );
	}
	
	private Server[] getServersByIndexes(int[] indexes) {
		//for(int index : indexes) {
		//	System.out.println("Index=>" + index);
		//}
		Server[] result = new Server[indexes.length];
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
	}
	
	private boolean connectedServerIn(Server[] servers) {
		Server connected_server = _server_manager.getConnectedServer();
		for(Server server : servers) {
			if( server == connected_server ) return true;
		}
		return false;
	}
	
	private Server[] getSelectedServers() {
		
		return getServersByIndexes(  this.getSelectedRows() );
	}
	
	/*
	 * ------------------------------------------------------------------------------------
	 *                    |   CONNECTED       DISCONNECTED      CONNECTED SELECTED
	 * ------------------------------------------------------------------------------------          
	 *  ONE_SELECTED      |      A               D                   G
	 *                    |
	 *  MULTIPLE_SELECTED |      B               E                   H
	 *                    |
	 *  VOID_LIST         |      *               F                   *
	 *                    |
	 * 
	 *   A,B,D,E,F,G,H - conditions
	 *   * - can't be
	 *   
	 *   Add Ac1
	 *   Remove selected Ac2
	 *   Remove all Ac3
	 *   Copy ED2K Links Ac4
	 *   Paste ED2K Links Ac5 
	 *   Connect Ac6
	 *   Disconnect Ac7
	 *   Column setup Ac8
	 *   Properties Ac9
	 * 
	 *     |  Ac1   Ac2   Ac3   Ac4   Ac5   Ac6   Ac7   Ac8   Ac9
	 * --------------------------------------------------------------  
	 *   A |  Yes   Yes   Yes   Yes   Yes   Yes   Yes   Yes   Yes
	 *     |
	 *   B |  Yes   Yes   Yes   Yes   Yes    -    Yes   Yes    -
	 *     |
	 *   D |  Yes   Yes   Yes   Yes   Yes   Yes    -    Yes   Yes
	 *     |
	 *   E |  Yes   Yes   Yes   Yes   Yes    -     -    Yes    -
	 *     |
	 *   F |  Yes    -     -     -    Yes    -     -    Yes    -
	 *     |
	 *   G |  Yes   Yes   Yes   Yes   Yes    -    Yes   Yes   Yes
	 *     |
	 *   H |  Yes   Yes   Yes   Yes   Yes    -    Yes   Yes    -
	 *     |
	 */    
	
	private ConditionType whichCondition() {
		ServerManager _server_manager = ServerManagerSingleton.getInstance();
		Server connected_server = _server_manager.getConnectedServer();
		int[] selected_rows = this.getSelectedRows();
		Server[] servers = getServersByIndexes( selected_rows );
		
		if( ( connected_server != null ) && ( selected_rows.length == 1 ) && ( servers[0] != connected_server ) )
		    return ConditionType.A;
	    if( ( connected_server != null ) && ( selected_rows.length > 1 ) && ( !connectedServerIn(servers) ) ) 
			return ConditionType.B;
		if( ( connected_server == null ) && ( selected_rows.length == 1) ) 
			return ConditionType.D;    
		if( ( connected_server == null ) && ( selected_rows.length > 1 ) )
			return ConditionType.E;   
		if( ( connected_server == null ) && ( this.getRowCount() == 0 ) ) 
			return ConditionType.F;
		if( ( connected_server != null ) && ( selected_rows.length == 1 ) && ( servers[0] == connected_server ) )
			return ConditionType.G;
		if( ( connected_server != null ) && ( selected_rows.length > 1 ) && ( connectedServerIn(servers) ) )
			return ConditionType.H;
		
		return null;
	}
	
	private enum ConditionType  { 
          A, B, D, E, F, G, H
	}
	
	/**
	 * @see http://jug.org.ua/wiki/display/JavaAlmanac/Packing+a+Column+of+a+JTable+Component
	 */
	public void packColumns(JTable table, int margin) {
        for (int c=0; c<table.getColumnCount(); c++) {
            packColumn(table, c, 2);
        }
    }
    
    // Sets the preferred width of the visible column specified by vColIndex. The column
    // will be just wide enough to show the column head and the widest cell in the column.
    // margin pixels are added to the left and right
    // (resulting in an additional width of 2*margin pixels).
    public void packColumn(JTable table, int vColIndex, int margin) {
        TableModel model = table.getModel();
        DefaultTableColumnModel colModel = (DefaultTableColumnModel)table.getColumnModel();
        TableColumn col = colModel.getColumn(vColIndex);
        int width = 0;
    
        // Get width of column header
        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }
        Component comp = renderer.getTableCellRendererComponent(
            table, col.getHeaderValue(), false, false, 0, 0);
        width = comp.getPreferredSize().width;
    
        // Get maximum width of column data
        for (int r=0; r<table.getRowCount(); r++) {
            renderer = table.getCellRenderer(r, vColIndex);
            comp = renderer.getTableCellRendererComponent(
                table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
            width = Math.max(width, comp.getPreferredSize().width);
        }
    
        // Add margin
        width += 2*margin;
    
        // Set the width
        col.setPreferredWidth(width);
    }
	
}
