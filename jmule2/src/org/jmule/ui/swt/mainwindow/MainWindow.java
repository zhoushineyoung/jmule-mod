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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.jmule.core.EventDescriptor;
import org.jmule.core.JMConstants;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMThread;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreEvent;
import org.jmule.core.JMuleCoreEventListener;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.NotEnoughSpaceDownloadingFile;
import org.jmule.core.servermanager.ServerManagerException;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.JMuleUIManagerException;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.Logger;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.common.NightlyBuildWarningWindow;
import org.jmule.ui.swt.tab.AbstractTab;
import org.jmule.ui.swt.tab.AbstractTab.JMULE_TABS;
import org.jmule.ui.swt.tab.MainTab;
import org.jmule.ui.swt.tab.main.kad.KadTab;
import org.jmule.ui.swt.tab.main.logs.LogsTab;
import org.jmule.ui.swt.tab.main.search.SearchTab;
import org.jmule.ui.swt.tab.main.serverlist.ServerListTab;
import org.jmule.ui.swt.tab.main.shared.SharedTab;
import org.jmule.ui.swt.tab.main.statistics.StatisticsTab;
import org.jmule.ui.swt.tab.main.transfers.TransfersTab;
import org.jmule.ui.swt.updaterwindow.UpdaterWindow;
import org.jmule.ui.utils.FileFormatter;
import org.jmule.updater.JMUpdater;
import org.jmule.updater.JMUpdaterException;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.14 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2011/04/25 10:55:30 $$
 */
public class MainWindow implements JMuleUIComponent {

	private Map<JMULE_TABS, MainTab> main_tabs = new HashMap<JMULE_TABS, MainTab>();
	private List<AbstractTab> opened_tabs = new ArrayList<AbstractTab>();
	
	private ScrolledComposite window_content;
	private CTabFolder tab_folder;
	private Shell shell;
	
	private JMuleCore _core;
	private Toolbar toolbar;
	private MainMenu main_menu;
	private StatusBar status_bar;
	
	private static Logger logger;
	
	public MainWindow() {	
	}
	
	public static Logger getLogger() {
		return logger;
	}
	
	public Composite getTabParent() {
		return window_content;
	}
	
	private Composite empty_tabs_content;
	
	public void initUIComponents() {
		final Display display = SWTThread.getDisplay();
		final MainWindow instance = this;
		display.asyncExec(new JMRunnable() {
            public void JMRun() {
		
            	shell = new Shell(display);
            	
				shell.setSize(800, 500); 	
            	Utils.centreWindow(shell);
            	
            	shell.setText(JMConstants.JMULE_FULL_NAME);
            	shell.setImage(SWTImageRepository.getImage("jmule.png"));
            	
            	GridLayout gridLayout = new GridLayout(1,true);
            	gridLayout.marginHeight = 2;
            	gridLayout.marginWidth = 2;
            	shell.setLayout(gridLayout);
            	//Setup main_menu
            	main_menu = new MainMenu(shell,instance);
            	//Setup tool bar
            	toolbar = new Toolbar(shell,_core,instance);
		
            	/*window_content = new CTabFolder(shell,SWT.BORDER); //new ScrolledComposite(shell,SWT.NONE);
            	FillLayout fill_layout = new FillLayout();
            	fill_layout.marginHeight = fill_layout.marginWidth = 0;
            	window_content.setLayout(fill_layout);
            	window_content.setSimple(false);
            	GridData gridData = new GridData(GridData.FILL_BOTH);
            	window_content.setLayoutData(gridData);*/
            	
            	window_content = new ScrolledComposite(shell,SWT.NONE);
            	window_content.setExpandHorizontal(true);
            	window_content.setExpandVertical(true);
            	window_content.setLayout(new FillLayout());
            	GridData gridData = new GridData(GridData.FILL_BOTH);
            	window_content.setLayoutData(gridData);
            	
            	tab_folder = new CTabFolder(window_content,SWT.BORDER);
            	FillLayout fill_layout = new FillLayout();
            	fill_layout.marginHeight = fill_layout.marginWidth = 0;
            	tab_folder.setLayout(fill_layout);
            	tab_folder.setSimple(false);
            	
            	tab_folder.addCTabFolder2Listener(new CTabFolder2Adapter() {
					
					@Override
					public void close(CTabFolderEvent event) {
						AbstractTab tab = (AbstractTab) ((CTabItem)event.item).getControl().getData(AbstractTab.TAB_KEY);
						closeTab(tab);
						event.doit = true;
					}
				});
            	
            	tab_folder.addSelectionListener(new SelectionAdapter() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						AbstractTab tab = (AbstractTab) ((CTabItem)e.item).getControl().getData(AbstractTab.TAB_KEY);
						if (opened_tabs.size()==0)
							return;
						if (selected_tab!=null)
							if (tab != selected_tab)
								selected_tab.lostFocus();
						selected_tab = tab;
						selected_tab.obtainFocus();
					}
					
					
				});
            	
		
            	new Label(shell,SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            	
            	status_bar = new StatusBar(shell,_core);
            	
            	LogsTab logs_tab = new LogsTab(window_content);
            	logger = logs_tab;
            	main_tabs.put(JMULE_TABS.LOGS, logs_tab);
            	main_tabs.put(JMULE_TABS.SERVERLIST, new ServerListTab(window_content,_core));
            	main_tabs.put(JMULE_TABS.KAD, new KadTab(window_content,_core));
            	main_tabs.put(JMULE_TABS.TRANSFERS, new TransfersTab(window_content,_core));
            	main_tabs.put(JMULE_TABS.SEARCH, new SearchTab(window_content,_core));
            	main_tabs.put(JMULE_TABS.SHARED, new SharedTab(window_content,_core));
            	main_tabs.put(JMULE_TABS.STATISTICS, new StatisticsTab(window_content));
            	setCurrentTab(JMULE_TABS.SERVERLIST);
            	
        		empty_tabs_content = new Composite(window_content, SWT.BORDER);
        		empty_tabs_content.setLayout(new GridLayout(1,true));
        		Label info_text = new Label(empty_tabs_content,SWT.CENTER);
        		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
        		data.grabExcessVerticalSpace = true;
        		info_text.setLayoutData(data);
        		info_text.setText(_._("mainwindow.no_tabs"));
            	
            	if (!SWTPreferences.getInstance().isStatusBarVisible())
        			statusBarToogleVisibility();
            	
        		if (!SWTPreferences.getInstance().isToolBarVisible())
        			toolbarToogleVisibility();

            	shell.open();

            	if (SWTPreferences.getInstance().isConnectAtStartup()) {
	            	try {
						_core.getServerManager().connect();
					} catch (ServerManagerException e1) {
						e1.printStackTrace();
					}	
            	}
            	
            	if (JMConstants.IS_NIGHTLY_BUILD) 
            		if (SWTPreferences.getInstance().isNightlyBuildWarning()){
            			NightlyBuildWarningWindow warning_window = new NightlyBuildWarningWindow(shell);
            			warning_window.getCoreComponents();
            			warning_window.initUIComponents();
            	}
            	
            	// show log messages
            	new JMThread(new JMRunnable() {
            		public void JMRun() {
            			logger.fine(Localizer._("mainwindow.logtab.message_jmule_started",  JMConstants.JMULE_FULL_NAME));
            			logger.fine(Localizer._("mainwindow.logtab.message_servers_loaded", _core.getServerManager().getServersCount() + ""));
            			logger.fine(Localizer._("mainwindow.logtab.message_shared_loaded", _core.getSharingManager().getFileCount() + ""));
            			logger.fine(Localizer._("mainwindow.logtab.message_partial_loaded", _core.getDownloadManager().getDownloadCount() + ""));
            		}}).start();
            	
            	_core.addEventListener(new JMuleCoreEventListener() {
					public void eventOccured(JMuleCoreEvent event, final EventDescriptor eventDescriptor) {
						if( event == JMuleCoreEvent.NOT_ENOUGH_SPACE ) {
							display.syncExec(new JMRunnable() {
								NotEnoughSpaceDownloadingFile nes = (NotEnoughSpaceDownloadingFile)eventDescriptor;
								public void JMRun() {
									Utils.showErrorMessage(shell, 
											_._("mainwindow.not_enough_space_dialog.title"),
											_._("mainwindow.not_enough_space_dialog.message", 
													nes.getFileName(),
													FileFormatter.formatFileSize( nes.getTotalSpace() ),
													FileFormatter.formatFileSize( nes.getFreeSpace() )));
								}
							});
						}
					}
            	});
            	// Update checker
            	if (SWTPreferences.getInstance().updateCheckAtStartup()) {
            		new JMThread(new JMRunnable() {
            			public void JMRun() {
            				try {
								JMUpdater.getInstance().checkForUpdates();
								if (JMUpdater.getInstance().isNewVersionAvailable())
									SWTThread.getDisplay().asyncExec(new JMRunnable() {
				            			public void JMRun() {
				            				if (JMUpdater.getInstance().isNewVersionAvailable()) {
												if (Utils.showConfirmMessage(shell, _._("mainwindow.new_version_available.title"), _._("mainwindow.new_version_available"))) {
													UpdaterWindow window = new UpdaterWindow();
													window.getCoreComponents();
													window.initUIComponents();
												} }
				            		}});
							} catch (JMUpdaterException e) {}
            			}
            		}).start();
            	}
            	shell.addListener(SWT.Close, new Listener() {
					public void handleEvent(Event arg0) {
						boolean exit = SWTPreferences.getInstance().promptOnExit() ? 
								Utils.showConfirmMessage(shell, _._("mainwindow.exit_prompt_title"), _._("mainwindow.exit_prompt")) : true;
						arg0.doit = exit;
					}
            	});
            	
            	shell.addDisposeListener(new DisposeListener() {
					public void widgetDisposed(DisposeEvent arg0) {
						new JMThread(new JMRunnable() {
							public void JMRun() {
								try {
									JMuleUIManager.getSingleton().shutdown();
								} catch (JMuleUIManagerException e) {
									e.printStackTrace();
								}
							}
						}).start();
						try {
							JMuleUIManager.getJMuleUI().shutdown();
						} catch (JMuleUIManagerException e) {
							e.printStackTrace();
						}
					}
            	});
            }});
		
	} 
	
	public void statusBarToogleVisibility() {
		status_bar.toogleVisibility();
		shell.layout();
	}
	
	public void toolbarToogleVisibility() {
		toolbar.toogleVisibility();
		shell.layout();
	}
	
	public Shell getShell() {
		return this.shell;
	}
	
	private MainTab opened_main_tab = null;
	private AbstractTab selected_tab = null;
	
	public boolean isTabOpen(JMULE_TABS tab) {
		for(AbstractTab t : opened_tabs)
			if (t.getTabType() == tab)
				return true;
		return false;
	}
	
	public void openTab(AbstractTab tab) {
		boolean tab_folder_created = false;
		if (opened_tabs.size()==0) {
			if (tab instanceof MainTab) {
				if (opened_main_tab != null) {
					opened_main_tab.lostFocus();
				}
				Composite tab_content = tab.getContent();
				tab_content.setParent(window_content);
				window_content.setContent(tab_content);
				tab_content.setVisible(true);
					
				opened_main_tab = (MainTab)tab;
				
				selected_tab = tab;
			} else {
				window_content.setContent(tab_folder);
				tab_folder.setParent(window_content);
				tab_folder.setVisible(true);
				tab_folder_created = true;
				
				if (opened_main_tab != null) {
					CTabItem item = opened_main_tab.createIfNeedCTabItem(tab_folder);
					Composite tab_content = opened_main_tab.getContent();
					tab_content.setParent(tab_folder);
					item.setControl(tab_content);
					tab_content.setVisible(true);
				}
			}
		}
		if ((opened_tabs.size()!=0) || tab_folder_created) 
		if (tab instanceof MainTab) {
			CTabItem item = tab.createIfNeedCTabItem(tab_folder);
			Composite tab_content = tab.getContent();
			tab_content.setParent(tab_folder);
			item.setControl(tab_content);
			tab_content.setVisible(true);
			tab_folder.setSelection(item);
			
			if (opened_main_tab!=null) {
				CTabItem item2 = opened_main_tab.createIfNeedCTabItem(tab_folder);
				item2.dispose();
			}
			opened_main_tab = (MainTab)tab;
			if (selected_tab != null)
				selected_tab.lostFocus();
			selected_tab = tab;
		} else {
				opened_tabs.add(tab);
				CTabItem item = tab.createIfNeedCTabItem(tab_folder);
				Composite tab_content = tab.getContent();
				tab_content.setParent(tab_folder);
				item.setControl(tab_content);
				tab_content.setVisible(true);
				tab_folder.setSelection(item);
				if (selected_tab != null) {
					selected_tab.lostFocus();
				}
				selected_tab = tab;
				selected_tab.obtainFocus();
			}
	}
	
	public void closeTab(AbstractTab tab) {
		if (tab instanceof MainTab) {
			toolbar.setSelection(null);
			main_menu.setSelectedTab(null);
			opened_main_tab = null;
			//opened_tabs.remove(tab);
		}
		else {
			opened_tabs.remove(tab);
			if (selected_tab == tab)
				tab.lostFocus();
			tab.disposeTab();
		}
		if (opened_tabs.size()==0)
		if (opened_main_tab != null) {
				if (tab == selected_tab) {
					selected_tab.createIfNeedCTabItem(tab_folder).dispose();
					opened_main_tab.createIfNeedCTabItem(tab_folder).dispose();
				}
				Composite tab_content = opened_main_tab.getContent();
				tab_content.setParent(window_content);
				window_content.setContent(tab_content);
				tab_content.setVisible(true);
				tab_folder.setVisible(false);
				if (opened_main_tab != selected_tab)
					opened_main_tab.obtainFocus();
				selected_tab = opened_main_tab;
			} else {
				// no more tabs
				//window_content.setContent(null);
				window_content.setContent(empty_tabs_content);
				
				selected_tab = null;
			}
	}
	
	public void setCurrentTab(JMULE_TABS tabID) {
		//System.out.println(opened_main_tab.getTabType().toString() + " " + tabID.toString());
		if (opened_main_tab!=null)
			if (opened_main_tab.getTabType()==tabID)
				return;
		openTab(main_tabs.get(tabID));
		toolbar.setSelection(tabID);
		main_menu.setSelectedTab(tabID);
		main_tabs.get(tabID).obtainFocus();
	}
	

	public void close() {
		shell.close();
	}
	
	public void getCoreComponents() {
		_core = JMuleCoreFactory.getSingleton();
	}

}
