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
package org.jmule.ui.swing.mainwindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.ColorUIResource;

import org.jmule.core.EventDescriptor;
import org.jmule.core.JMConstants;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMThread;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreEvent;
import org.jmule.core.JMuleCoreEventListener;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.NotEnoughSpaceDownloadingFile;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.networkmanager.NetworkManager;
import org.jmule.core.peermanager.PeerManager;
import org.jmule.core.servermanager.Server;
import org.jmule.core.servermanager.ServerManager;
import org.jmule.core.servermanager.ServerManagerException;
import org.jmule.core.servermanager.ServerManagerListener;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swing.BrowserLauncher;
import org.jmule.ui.swing.ImgRep;
import org.jmule.ui.swing.Refreshable;
import org.jmule.ui.swing.SwingGUIUpdater;
import org.jmule.ui.swing.SwingPreferences;
import org.jmule.ui.swing.SwingUtils;
import org.jmule.ui.swing.UISwingImageRepository;
import org.jmule.ui.swing.dialogs.AboutDialog;
import org.jmule.ui.swing.dialogs.AdjustSpeedLimitsDialog;
import org.jmule.ui.swing.maintabs.AbstractTab;
import org.jmule.ui.swing.maintabs.LogTab;
import org.jmule.ui.swing.maintabs.search.SearchTab;
import org.jmule.ui.swing.maintabs.serverlist.ServerListTab;
import org.jmule.ui.swing.maintabs.shared.SharedTab;
import org.jmule.ui.swing.maintabs.statistics.StatisticsTabs;
import org.jmule.ui.swing.maintabs.transfers.TransfersTab;
import org.jmule.ui.swing.settings.SettingsDialog;
import org.jmule.ui.swing.versionchecker.VersionChecker;
import org.jmule.ui.swing.wizards.SetupWizard;
import org.jmule.ui.swing.wizards.UIChooserWizad;
import org.jmule.ui.utils.FileFormatter;
import org.jmule.ui.utils.SpeedFormatter;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.10 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/05/15 18:19:58 $$
 */
public class MainWindow extends JFrame implements WindowListener  {
	private JMenuBar main_menu_bar;
	private JMenu file, fnew, view, tools, help, tabs;
	private JMenuItem new_downloads, new_servers,
	                  exit, import_server_list, ui_chooser, config_wizard, 
	                  options, open_support,
	                  project_forums, bug_tracker, check_for_updates, about;
	private JRadioButtonMenuItem servers, transfers, search, shared_files,stats,logs;
	private JCheckBoxMenuItem tool_bar_visibility, status_bar_visibility;
	private BorderLayout border_layout0;
	private JToolBar main_buttons_bar;
	// main tabs 
	private ServerListTab server_list_tab = new ServerListTab(this);
	private SearchTab search_tab = new SearchTab(this);
	private SharedTab shared_tab = new SharedTab(this);
	private TransfersTab transfers_tab = new TransfersTab(this); 
	private StatisticsTabs statistic_tab = new StatisticsTabs(this);
	private LogTab log_tab = new LogTab(this);
	StatusBar status_bar;
	JPanel the_current_view = server_list_tab;
	private JPanel center_panel = new JPanel();
	JButton connect_button;
	
	private JMuleCore _core = JMuleCoreFactory.getSingleton();
	private ServerManager _server_manager = _core.getServerManager();
	private SwingGUIUpdater _ui_updater = SwingGUIUpdater.getInstance();
	private PeerManager _peer_manager = _core.getPeerManager();
	private SwingPreferences _pref = SwingPreferences.getSingleton();
	private ConfigurationManager _config = _core.getConfigurationManager(); 
	private NetworkManager _network_manager = _core.getNetworkManager();
	
	private AbstractTab previous_panel;
	
	class JMToggleButton extends JToggleButton {
		public JMToggleButton() {
			 this.setSize(new Dimension(109,61));
			 this.setPreferredSize(new Dimension(109,61));			 
		     this.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		     this.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		 }
	}
	
	final JMToggleButton server_list_button = new JMToggleButton();
	final JMToggleButton transfers_button = new JMToggleButton();
	final JMToggleButton search_button = new JMToggleButton();
	final JMToggleButton shared_files_button = new JMToggleButton();
	final JMToggleButton statistics_button = new JMToggleButton();
	final JMToggleButton log_button = new JMToggleButton();
	final MainWindow _this = this;
	
	public MainWindow() {
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		this.setIconImage(UISwingImageRepository.getImage("jmule_swing.png"));
		initUIComponents();
        setMainMenu();
        setMainButtonsBar();    
        server_list_button.doClick();
        setStatusBar();

        final ActionListener start_auto_connect_action = new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
				try {
					_server_manager.connect();
				} catch (ServerManagerException e) {
					e.printStackTrace();
				}
        	}
        };
        
        final ActionListener stop_auto_connect_action = new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
				try {
					_server_manager.disconnect();
				} catch (Throwable e) {
					e.printStackTrace();
				}
        	}
        };
        
        final ActionListener disconnect_action = new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		try {
        			_server_manager.disconnect();
        		}catch(Throwable t) {
        			t.printStackTrace();
        		}
        	}
        };
        
		_server_manager.addServerListListener(new ServerManagerListener() {

			public void connected(Server server) {
				LogTab.getLogInstance().addMessage("The system is connected to server = " + server);
                status_bar.setServerStatus(ServerStatusTypes.CONNECTED, server.getAddress() + ":" + server.getPort());
                status_bar.setClientIDType( ( server.getClientID().isHighID() ? ClientIDTypes.HIGH_ID : ClientIDTypes.LOW_ID ), server.getClientID().toString());
                connect_button.removeActionListener(start_auto_connect_action);
                connect_button.removeActionListener(stop_auto_connect_action);
                connect_button.addActionListener(disconnect_action);
                connect_button.setText("Disconnect");
                connect_button.setIcon(ImgRep.getIcon("connect_drop.png"));
			}

			public void disconnected(Server server) {
				LogTab.getLogInstance().addMessage("The system has been disconnected from server = " + server);
				status_bar.setClientIDType(ClientIDTypes.NO_ID, "");
                status_bar.setServerStatus(ServerStatusTypes.DISCONNECTED, null);
                connect_button.removeActionListener(disconnect_action);
                connect_button.removeActionListener(stop_auto_connect_action);
                connect_button.addActionListener(start_auto_connect_action);
                connect_button.setText("Connect");
                connect_button.setIcon(ImgRep.getIcon("connect_do.png"));
			}

			public void isconnecting(Server server) {
				LogTab.getLogInstance().addMessage("The system is connecting to server = " + server);
				status_bar.setClientIDType(ClientIDTypes.NO_ID, "");
                status_bar.setServerStatus(ServerStatusTypes.CONNECTING, server.getAddress() + ":" + server.getPing());
                connect_button.removeActionListener(disconnect_action);
                connect_button.removeActionListener(start_auto_connect_action);
                connect_button.addActionListener(stop_auto_connect_action);
                connect_button.setText("Cancel");
                connect_button.setIcon(ImgRep.getIcon("connect_stop.png"));
			}

			public void serverMessage(Server server, String message) {
				server_list_tab.setServerMessage(message);
			}

			public void autoConnectFailed() {
				
			}

			public void autoConnectStarted() {
				
			}

			public void isConnecting(Server server) {
				
			}

			public void serverAdded(Server server) {
				
			}

			public void serverConnectingFailed(Server server, Throwable cause) {
				
			}

			public void serverListCleared() {
				
			}

			public void serverRemoved(Server server) {
				
			}
			
		});
		
		connect_button.addActionListener(start_auto_connect_action);
		
		if(_pref.isConnectAtStartup()) {
			try {
			  _server_manager.connect();
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
		_core.addEventListener(new JMuleCoreEventListener() {
			public void eventOccured(JMuleCoreEvent event, final EventDescriptor eventDescriptor) {
			  if(event == JMuleCoreEvent.NOT_ENOUGH_SPACE) {	
			    SwingUtilities.invokeLater(new Runnable() {
			    	NotEnoughSpaceDownloadingFile nes = (NotEnoughSpaceDownloadingFile)eventDescriptor;
				    public void run() {
				         JOptionPane.showMessageDialog(_this, 
				            _._("mainwindow.not_enough_space_dialog.message", 
											nes.getFileName(),
											FileFormatter.formatFileSize( nes.getTotalSpace() ),
											FileFormatter.formatFileSize( nes.getFreeSpace() )),
				            _._("mainwindow.not_enough_space_dialog.title"),JOptionPane.ERROR_MESSAGE);	
				    }
			    });
			  }
			}
		});
		LogTab.getLogInstance().addMessage("System started");
	}

	public void initUIComponents() {
		
		String title = JMConstants.JMULE_FULL_NAME;
		this.setTitle(title);
		//TODO get this from CfgManager
		this.setSize(800, 470);
		this.setPreferredSize(new Dimension(800, 470));
		//this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("resources/jmule.png")).getImage());
		//place the main windows in the middle of display
		this.setLocation(getToolkit().getScreenSize().width/2 - getWidth()/2,
                         getToolkit().getScreenSize().height/2 - getHeight()/2);
		//set the main layout
		border_layout0 = new BorderLayout();
        this.setLayout( border_layout0 );
        
        center_panel.setLayout(new GridLayout(1,1));
        this.add(center_panel, BorderLayout.CENTER);
		
	}
	//----------------------------------------------------------------------
	private void setMainMenu() {
		main_menu_bar = new JMenuBar();
		file = new JMenu();
		fnew = new JMenu();
		view = new JMenu();
		tools = new JMenu();
		help = new JMenu();
		tabs = new JMenu();		
		exit = new JMenuItem();
		new_downloads = new JMenuItem();
		new_servers = new JMenuItem();
		import_server_list = new JMenuItem();
		ButtonGroup radio_button_menu_items_group = new ButtonGroup();
		servers = new JRadioButtonMenuItem();
		transfers = new JRadioButtonMenuItem();
		search = new JRadioButtonMenuItem();
		shared_files = new JRadioButtonMenuItem();
		stats = new JRadioButtonMenuItem();
		logs = new JRadioButtonMenuItem();
		radio_button_menu_items_group.add(servers);
		radio_button_menu_items_group.add(transfers);
		radio_button_menu_items_group.add(search);
		radio_button_menu_items_group.add(shared_files);
		radio_button_menu_items_group.add(stats);
		radio_button_menu_items_group.add(logs);
		servers.setSelected(true);
		ui_chooser = new JMenuItem();
		config_wizard = new JMenuItem();
		options = new JMenuItem();
        open_support = new JMenuItem();
        project_forums = new JMenuItem();
        bug_tracker = new JMenuItem();
        check_for_updates = new JMenuItem();
		about = new JMenuItem();
		
		tool_bar_visibility = new JCheckBoxMenuItem();
		status_bar_visibility = new JCheckBoxMenuItem();
		tool_bar_visibility.setText("Tool bar");
        status_bar_visibility.setText("Status bar");
        tool_bar_visibility.setSelected(true);
        status_bar_visibility.setSelected(true);
		
        status_bar_visibility.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		if(status_bar_visibility.isSelected()) status_bar.setVisible(true);
        		else status_bar.setVisible(false);
        	}
        });
        
        tool_bar_visibility.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        	    if(tool_bar_visibility.isSelected()) main_buttons_bar.setVisible(true);
        	    else  main_buttons_bar.setVisible(false);
        	}
        });
        
        about.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		AboutDialog about_dialog = new AboutDialog(_this,true);
        		SwingUtils.setWindowLocationRelativeTo(about_dialog,_this);
        		about_dialog.setVisible(true);
        	}
        });
        
		file.setText(_._("mainwindow.mainmenu.file"));
		fnew.setText("New");
		view.setText(_._("mainwindow.mainmenu.view"));
		tabs.setText(_._("mainwindow.mainmenu.view.tabs"));
		tools.setText(_._("mainwindow.mainmenu.tools"));
		help.setText(_._("mainwindow.mainmenu.help"));
		
		exit.setText(_._("mainwindow.mainmenu.file.exit"));
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				shutdownTheSystem();
			}
		});
		new_downloads.setText("Download(s)");
		new_downloads.setIcon(ImgRep.getIcon("menuicons/folder_down.png"));
		new_downloads.setMnemonic('d');
		new_downloads.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
		new_servers.setText("Server(s)");
		new_servers.setIcon(ImgRep.getIcon("menuicons/server_add.png"));
		new_servers.setMnemonic('s');
		new_servers.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
		exit.setMnemonic('x');
		exit.setIcon(ImgRep.getIcon("menuicons/door_in.png"));
		import_server_list.setText(_._("mainwindow.mainmenu.file.import"));
		import_server_list.setIcon(ImgRep.getIcon("import.png"));
		servers.setText(_._("mainwindow.mainmenu.view.tabs.servers"));
		servers.setIcon(ImgRep.getIcon("menuicons/servers.png"));
		servers.setMnemonic('s');
		servers.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_MASK));
		transfers.setText(_._("mainwindow.mainmenu.view.tabs.transfers"));
		transfers.setIcon(ImgRep.getIcon("menuicons/transfer.png"));
		transfers.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_MASK));
		search.setText(_._("mainwindow.mainmenu.view.tabs.search"));
		search.setIcon(ImgRep.getIcon("menuicons/search.png"));
		search.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_MASK));
		shared_files.setText(_._("mainwindow.mainmenu.view.tabs.shared"));
		shared_files.setIcon(ImgRep.getIcon("menuicons/shared_files.png"));
		shared_files.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, java.awt.event.InputEvent.CTRL_MASK));
		stats.setText(_._("mainwindow.mainmenu.view.tabs.stats"));
		stats.setIcon(ImgRep.getIcon("menuicons/statistics.png"));
		stats.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, java.awt.event.InputEvent.CTRL_MASK));
		logs.setText(_._("mainwindow.mainmenu.view.tabs.logs"));
		logs.setIcon(ImgRep.getIcon("menuicons/logs.png"));
		logs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, java.awt.event.InputEvent.CTRL_MASK));
		ui_chooser.setText(_._("mainwindow.mainmenu.tools.uichooser"));
		ui_chooser.setIcon(ImgRep.getIcon("menuicons/switchui.png"));
		ui_chooser.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
		ui_chooser.setMnemonic('u');
		config_wizard.setText(_._("mainwindow.mainmenu.tools.wizard"));
		config_wizard.setIcon(ImgRep.getIcon("menuicons/wizard.png"));
		config_wizard.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
		config_wizard.setMnemonic('w');
		options.setText(_._("mainwindow.mainmenu.tools.options"));
		options.setIcon(ImgRep.getIcon("menuicons/cog_edit.png"));
		options.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
		options.setMnemonic('o');
		
		open_support.setText("Open support");
		open_support.setIcon(ImgRep.getIcon("menuicons/world_link.png"));
		project_forums.setText("Project forums");
		project_forums.setIcon(ImgRep.getIcon("menuicons/world_link.png"));
		bug_tracker.setText("Bug tracker");
		bug_tracker.setIcon(ImgRep.getIcon("menuicons/world_link.png"));
		check_for_updates.setText("Check for updates");
		check_for_updates.setIcon(ImgRep.getIcon("updater.png"));
		about.setText(_._("mainwindow.mainmenu.help.about"));
		about.setIcon(ImgRep.getIcon("menuicons/information.png"));
		
		tool_bar_visibility.setText(_._("mainwindow.mainmenu.view.toolbar"));
        status_bar_visibility.setText(_._("mainwindow.mainmenu.view.statusbar"));
		
		main_menu_bar.add( file );
		main_menu_bar.add( view );
		main_menu_bar.add( tools );
		main_menu_bar.add( help );
		
		//file.add( fnew );
		//file.addSeparator();
		file.add( import_server_list );
	    file.addSeparator();
		file.add( exit );
		
		fnew.add( new_downloads );
		fnew.add( new_servers );
		
		tabs.add( servers );
		tabs.add( transfers );
		tabs.add( search );
		tabs.add( shared_files );
		tabs.add( stats );
		view.add(tabs);
		
		view.add(tool_bar_visibility);
		view.add(status_bar_visibility);
		
		tools.add( ui_chooser );
		tools.add( config_wizard );
		tools.add( new JSeparator() );
		tools.add( options );
		
		help.add( open_support );
        help.add( bug_tracker );
        help.add( project_forums );
        help.addSeparator();
        help.add( check_for_updates );
        help.addSeparator();
		help.add( about );
		this.setJMenuBar( main_menu_bar );
	}
	
	private void setActiveMainTab(AbstractTab active_tab, JRadioButtonMenuItem menu_item_button) {
		if(active_tab != previous_panel) {
		   if(previous_panel != null) {
			   previous_panel.setVisible(false);
			   previous_panel.deregisterAllRefreshables();
		   }
		   active_tab.setVisible(true);
		   setView( active_tab );
		   active_tab.registerAllRefreshables();
		   previous_panel = active_tab;
		   menu_item_button.setSelected(true);
		}
	}
	
	//TODO extract this for a new class -> MainButtonsBar.java
	private void setMainButtonsBar() {
        main_buttons_bar = new JToolBar();
		this.getContentPane().add( main_buttons_bar, BorderLayout.NORTH );
		//main_buttons_bar.setPreferredSize( new java.awt.Dimension(425, 50) );
		main_buttons_bar.setFloatable( false );
		
		ButtonGroup button_group = new ButtonGroup();
		
		connect_button = new JButton();
		connect_button.setIcon( UISwingImageRepository.getIcon("connect_do.png") );
		connect_button.setText("Connect");
		connect_button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		connect_button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		main_buttons_bar.add(connect_button);

		main_buttons_bar.addSeparator();
		
 
		server_list_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setActiveMainTab(server_list_tab, servers);
				// if(previous_panel != null) previous_panel.setVisible(false);
				// server_list_tab.setVisible(true);
				// setView( server_list_tab );
				// previous_panel = server_list_tab;
				// servers.setSelected(true);
			}			
		});
		server_list_button.setIcon( UISwingImageRepository.getIcon("servers.png") );
		server_list_button.setText("Servers");
		button_group.add(server_list_button);
		main_buttons_bar.add(server_list_button);
		
		transfers_button.addActionListener( new ActionListener() {			
			 public void actionPerformed(ActionEvent e) {
				setActiveMainTab(transfers_tab, transfers); 
				// if(previous_panel != null) previous_panel.setVisible(false);
				// transfers_tab.setVisible(true);
				// setView( transfers_tab );
				// previous_panel = transfers_tab;
				// transfers.setSelected(true);
			 }
		});
		transfers_button.setIcon( UISwingImageRepository.getIcon("transfer.png") );
		transfers_button.setText("Transfers");
		button_group.add( transfers_button );
		main_buttons_bar.add( transfers_button );
		
		search_button.addActionListener(new ActionListener() {			
			 public void actionPerformed(ActionEvent e) {
				 setActiveMainTab(search_tab, search);
				 //if(previous_panel != null) previous_panel.setVisible(false);
				 //search_tab.setVisible(true);
				 //setView( search_tab );
				 //previous_panel = search_tab;
				 //search.setSelected(true);
			 }
		});		
		search_button.setIcon( UISwingImageRepository.getIcon("search.png") );
		search_button.setText("Search Files");
		button_group.add( search_button );
		main_buttons_bar.add( search_button );
		
		shared_files_button.addActionListener(new ActionListener() {			
			 public void actionPerformed(ActionEvent e) {
				 setActiveMainTab(shared_tab, shared_files);
				// if(previous_panel != null) previous_panel.setVisible(false);
				// shared_tab.setVisible(true);
				// setView( shared_tab );
				// previous_panel = shared_tab;
				// shared_files.setSelected(true);
			 }
		});
		shared_files_button.setIcon( UISwingImageRepository.getIcon("shared_files.png") );
		shared_files_button.setText("Shared Files");
		button_group.add( shared_files_button );
		main_buttons_bar.add( shared_files_button );
		
		statistics_button.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				setActiveMainTab(statistic_tab, stats);
				//if(previous_panel != null) previous_panel.setVisible(false);
				//statistic_tab.setVisible(true);
				//setView( statistic_tab );
				//previous_panel = statistic_tab;
				//stats.setSelected(true);
			}
		}); 
		statistics_button.setText("Statistics");
		statistics_button.setIcon( UISwingImageRepository.getIcon("statistics.png") );
		button_group.add( statistics_button );
		main_buttons_bar.add( statistics_button );
		
		log_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setActiveMainTab(log_tab, logs);
				// if(previous_panel != null) previous_panel.setVisible(false);
				// log_tab.setVisible(true);
			    // setView( log_tab );
			    // previous_panel = log_tab;
			    // logs.setSelected(true);
			}
		});
		log_button.setText("Log");
		log_button.setIcon(ImgRep.getIcon("logs.png"));
		button_group.add( log_button );
		main_buttons_bar.add( log_button );
		
		// sets the actions to menu items
		servers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				server_list_button.doClick();
			}
		});
		transfers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				transfers_button.doClick();
			}
		});
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				search_button.doClick();
			}
		});
		shared_files.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				shared_files_button.doClick();
			}
		});
		stats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				statistics_button.doClick(); 
			}
		});
		logs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				log_button.doClick();
			}
		});
		
		// ------------------------------------------------------------
		final MainWindow _this = this;
		
		final JFileChooser file_chooser;
	    FileSystemView file_system_view;
        file_system_view = FileSystemView.getFileSystemView(); 
        file_chooser = new JFileChooser( file_system_view );
        file_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        file_chooser.setMultiSelectionEnabled(false);
        file_chooser.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        	  if(event.getActionCommand() == JFileChooser.APPROVE_SELECTION) {	
                  final File file = file_chooser.getSelectedFile();
                  (new JMThread(new JMRunnable() {
                	 public void JMRun() {
                		try {
     					    _server_manager.importList(file.getAbsolutePath());
     				    } catch (Throwable e) {
     				    	e.printStackTrace();
     				        SwingUtilities.invokeLater(new Runnable() {
     				        	public void run() {
     				        		 JOptionPane.showMessageDialog(_this, "An error occured, most probably the file format is wrong", "Error Message",JOptionPane.ERROR_MESSAGE);	
     				        	}
     				        });	
     				      }	 
                	 }
                  })).start();

        	  }
        	}
        });
        
		
		import_server_list.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent event) {
				 System.out.println("Import server list entered");
				 file_chooser.showDialog(_this, "Choose");   
			 }
		});
		
		new_downloads.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			    System.out.println("New downloads menu item pressed");	
			}
		});
		new_servers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			    System.out.println("New serververs menu item pressed");	
			}
		});
		ui_chooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			    final UIChooserWizad ui_chooser = new UIChooserWizad(_this); 
			    ui_chooser.setSize(500, 400);
			    SwingUtils.setWindowLocationRelativeTo(ui_chooser,_this);
				ui_chooser.setVisible(true);
			}
		});
		
		config_wizard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				SetupWizard setup_wizard = new SetupWizard();
				setup_wizard.pack();
				setup_wizard.setVisible(true);
			}
		});
		
		options.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				SettingsDialog settings_dialog = new SettingsDialog(_this);
				settings_dialog.setSize(379, 495);
				SwingUtils.setWindowLocationRelativeTo(settings_dialog,_this);
				settings_dialog.setVisible(true);
			}
		});
		
		// --------------------------------------------------------------
		
		open_support.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				BrowserLauncher.openURL(JMConstants.OPEN_SUPPORT);
			}
		});
		
		project_forums.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				BrowserLauncher.openURL(JMConstants.JMULE_FORUMS);
			}
		});
		
		bug_tracker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				BrowserLauncher.openURL(JMConstants.JMULE_BUG_TRACKER);
			}
		});
		
		check_for_updates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VersionChecker version_checker = new VersionChecker(_this);
			    SwingUtils.setWindowLocationRelativeTo(version_checker, _this);
			    version_checker.setVisible(true);	
			}
		});
	}
	
	private void setView(JPanel new_view) {
		center_panel.remove( the_current_view );
		center_panel.add( new_view );
		center_panel.updateUI();
		the_current_view = new_view;
	}

	public enum ServerStatusTypes {
		DISCONNECTED { public String toString() { return _._("mainwindow.statusbar.label.disconnected"); } },
		CONNECTING { public String toString() { return _._("mainwindow.statusbar.label.connecting"); }	},
		CONNECTED { public String toString() { return _._("mainwindow.statusbar.label.connected"); } };
		public abstract String toString();
	}
	
	public enum ClientIDTypes {
		LOW_ID { public String toString() { return _._("mainwindow.statusbar.label.low_id"); } },
		HIGH_ID { public String toString() { return _._("mainwindow.statusbar.label.high_id"); } },
		// when the system is disconnected
		NO_ID { public String toString() { return ""; } };
	    public abstract String toString();	
	}
	
	class StatusBar extends JPanel implements Refreshable {
		
		private JLabel server_status = new JLabel(_._("mainwindow.statusbar.label.disconnected"));
		private JLabel client_id = new JLabel();
		private JLabel download_speed = new JLabel();
		private JLabel upload_speed = new JLabel();
		private JFrame frame;
		private AdjustSpeedLimitsDialog speed_limits_dialog;
		
		private JPopupMenu popup_menu = new JPopupMenu();
		
		class MousePopupListener extends MouseAdapter {
		    public void mousePressed(MouseEvent e) {
		      checkPopup(e);
		    }

		    public void mouseClicked(MouseEvent e) {
		      checkPopup(e);
		    }

		    public void mouseReleased(MouseEvent e) {
		      checkPopup(e);
		    }

		    private void checkPopup(MouseEvent e) {
		      if (e.isPopupTrigger()) {
		        popup_menu.show(e.getComponent(), e.getX(), e.getY());
		      }
		    }
		 }
		
		private String download_speed_limit="";
		private String upload_speed_limit="";
		
		String downloadSpeed;
		String uploadSpeed;
		
		public StatusBar(final JFrame frame) {
			this.frame = frame;
			speed_limits_dialog = new AdjustSpeedLimitsDialog(frame);
			this.setPreferredSize(new java.awt.Dimension(100, 25));
			this.setLayout(new BorderLayout());
			this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
			JPanel left_panel = new JPanel();
			left_panel.setLayout(new GridLayout(1,2,10,0));
			server_status.setIcon(ImgRep.getIcon("toolbar_disconnected.png"));
			left_panel.add(server_status);
			left_panel.add(client_id);
			JPanel right_panel = new JPanel();
			right_panel.setLayout(new GridLayout(1,2,10,0));
			download_speed.setIcon(ImgRep.getIcon("down.gif"));
			upload_speed.setIcon(ImgRep.getIcon("up.gif"));
			right_panel.add(download_speed);
			right_panel.add(upload_speed);
			this.add(left_panel,BorderLayout.WEST);
			this.add(right_panel,BorderLayout.EAST);
			
			JMenuItem adjust_speeds = new JMenuItem("Adjust speed limits");
			adjust_speeds.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					SwingUtils.setWindowLocationRelativeTo(speed_limits_dialog, frame);
					speed_limits_dialog.setVisible(true);					
				}
			});
			popup_menu.add(adjust_speeds);
			this.addMouseListener(new MousePopupListener());
		}
		
		public void setServerStatus(ServerStatusTypes statusType, String server) {
			switch(statusType) {
			   case DISCONNECTED : server_status.setText(ServerStatusTypes.DISCONNECTED.toString());
			                       server_status.setToolTipText("");
			                       server_status.setIcon(ImgRep.getIcon("toolbar_disconnected.png")); break;
			   case CONNECTING   : server_status.setText(ServerStatusTypes.CONNECTING.toString());
			                       server_status.setToolTipText("Connecting to " + server); break;
			   case CONNECTED    : server_status.setText(ServerStatusTypes.CONNECTED.toString());
			                       server_status.setToolTipText(server);
			                       server_status.setIcon(ImgRep.getIcon("toolbar_connected.png")); break;
			}
		}
		
		public void setClientIDType(ClientIDTypes clientIDType, String clientID) {
			switch(clientIDType) {
			   case LOW_ID       : client_id.setForeground(Color.RED);
				                   client_id.setText("LOW ID");
			                       client_id.setToolTipText(clientID); break;
			   case HIGH_ID      : client_id.setForeground(Color.BLACK);
				                   client_id.setText("HIGH ID");
			                       client_id.setToolTipText(clientID); break;
			   case NO_ID        : client_id.setText("");
	                               client_id.setToolTipText(""); break;
			}
		}
		
		public void setDownloadSpeedLimit(String downloadSpeedLimit) {
			download_speed_limit = downloadSpeedLimit;
		}
		
		public void setUploadSpeedLimit(String uploadSpeedLimit) {
			upload_speed_limit = uploadSpeedLimit;
		}
		
		public void refresh() {
		  try {	
			downloadSpeed = SpeedFormatter.formatSpeed( _network_manager.getDownloadSpeed() );
			uploadSpeed = SpeedFormatter.formatSpeed( _network_manager.getUploadSpeed() );
			download_speed.setText( ( download_speed_limit != "" ? "[" : "" ) + 
					                download_speed_limit + 
					                ( download_speed_limit != "" ? "] " : "" ) + 
					                downloadSpeed + " ");
			upload_speed.setText( ( upload_speed_limit != "" ? "[" : "" ) + 
					             upload_speed_limit + 
					              ( upload_speed_limit != "" ? "] " : "" ) + 
					              uploadSpeed + " ");
		  }catch( Throwable cause ) {
			  cause.printStackTrace();
		  }
		}

	}
	
	private void setStatusBar() {
		status_bar = new StatusBar(this);
		//status_bar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		this.getContentPane().add(status_bar, BorderLayout.SOUTH);
		_ui_updater.addRefreshable(status_bar);
		
		try {
		  if( _config.getDownloadLimit() != 0 ) status_bar.setDownloadSpeedLimit(SpeedFormatter.formatSpeed(_config.getDownloadLimit()));
		  if( _config.getUploadLimit() != 0 ) status_bar.setUploadSpeedLimit(SpeedFormatter.formatSpeed(_config.getUploadLimit()));
		}catch( Throwable cause ) {
			cause.printStackTrace();
		}
		
		_config.addConfigurationListener(new ConfigurationAdapter() {
			public void downloadLimitChanged(long downloadLimit) {
			   if(downloadLimit != 0)	
				   status_bar.setDownloadSpeedLimit(SpeedFormatter.formatSpeed(downloadLimit));
			   else 
				   status_bar.setDownloadSpeedLimit("");
			}
			
			public void uploadLimitChanged(long uploadLimit) {
				if(uploadLimit != 0)
				   status_bar.setUploadSpeedLimit(SpeedFormatter.formatSpeed(uploadLimit));
				else
				   status_bar.setUploadSpeedLimit("");
			}
		});
	}
	
	// window listener methods
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {
		shutdownTheSystem();
	}
    public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	
	public void windowOpened(WindowEvent e) {}
	
	private void shutdownTheSystem() {
		if(_pref.isPromptOnExitEnabled()) {
		   int value = JOptionPane.showConfirmDialog(null,"Are you sure you want to exit ?", "JMule", JOptionPane.YES_NO_OPTION);
		   if(value == JOptionPane.YES_OPTION) {
			  try {
			      JMuleUIManager.getSingleton().shutdown();
			  }catch(Throwable ex) {
				  ex.printStackTrace();
			  }
			this.setVisible(false);
		   } else return;
		} else {

			try {
				JMuleUIManager.getSingleton().shutdown();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

		}
	}
	
	public static void main(String args[]) {
		
		try { 
			
		  JMuleCoreFactory.create().start();
		  
		}catch(Throwable t) {
			
			t.printStackTrace();
		}

		SwingGUIUpdater.getInstance().start();
		Localizer.initialize();
		UIManager.put("ToolTip.foreground", new ColorUIResource(Color.BLACK));
		UIManager.put("ToolTip.background", new ColorUIResource(0Xfdf7c2));
		MainWindow mw = new MainWindow();
		mw.pack();
		
		mw.setVisible( true );
		//Toolkit.getDefaultToolkit().beep();
		
	}

}
