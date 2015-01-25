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

import static org.jmule.ui.localizer._._;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jmule.core.JMuleCore;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.common.ConnectButton;
import org.jmule.ui.swt.tab.AbstractTab.JMULE_TABS;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.7 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2011/04/25 10:55:30 $$
 */
public class Toolbar extends ToolBar {

	private List<ToolItem> button_list = new ArrayList<ToolItem>();
	private GridData grid_data;
	private MainWindow main_window;
	
	private static final String 	DATA_KEY		=  "DataKey";
	
	private  static Toolbar toolBarInstance = null;
	
	public Toolbar(Composite shell,JMuleCore core, MainWindow mainWindow) {
		super(shell, SWT.FLAT);
		toolBarInstance = this;
		main_window = mainWindow;
		
		grid_data = new GridData(GridData.FILL_HORIZONTAL);
		
		setLayoutData(grid_data);
		
		new ConnectButton(this);
		
		new ToolItem(this,SWT.SEPARATOR);
		
		final ToolItem servers_item = new ToolItem(this, SWT.CHECK);
		servers_item.setText(_("mainwindow.toolbar.servers_item"));
		servers_item.setSelection(true);
		servers_item.setData(DATA_KEY, JMULE_TABS.SERVERLIST);
		servers_item.setImage(SWTImageRepository.getImage("servers.png"));
		
		button_list.add(servers_item);
		
		servers_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				main_window.setCurrentTab(JMULE_TABS.SERVERLIST);
			}
		});
		
		ToolItem kad_item = new ToolItem(this, SWT.CHECK);
		kad_item.setText(_("mainwindow.toolbar.kad_item"));
		kad_item.setData(DATA_KEY, JMULE_TABS.KAD);
		kad_item.setImage(SWTImageRepository.getImage("kad.png"));
		button_list.add(kad_item);
		kad_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				main_window.setCurrentTab(JMULE_TABS.KAD);
			}
		});
		
		final ToolItem transfers_item = new ToolItem(this, SWT.CHECK);
		transfers_item.setText(_("mainwindow.toolbar.transfers_item"));
		transfers_item.setData(DATA_KEY, JMULE_TABS.TRANSFERS);
		transfers_item.setImage(SWTImageRepository.getImage("transfer.png"));		
		button_list.add(transfers_item);
		
		transfers_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				main_window.setCurrentTab(JMULE_TABS.TRANSFERS);
			}
		});
		
		final ToolItem search_item = new ToolItem(this, SWT.CHECK);
		search_item.setText(_("mainwindow.toolbar.search_item"));
		search_item.setData(DATA_KEY, JMULE_TABS.SEARCH);
		search_item.setImage(SWTImageRepository.getImage("search.png"));
		button_list.add(search_item);
		
		search_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				main_window.setCurrentTab(JMULE_TABS.SEARCH);
			}
		});
		
		final ToolItem shared_item = new ToolItem(this, SWT.CHECK);
		shared_item.setText(_("mainwindow.toolbar.shared_item"));
		shared_item.setData(DATA_KEY, JMULE_TABS.SHARED);
		shared_item.setImage(SWTImageRepository.getImage("shared_files.png"));
		button_list.add(shared_item);
		
		shared_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				main_window.setCurrentTab(JMULE_TABS.SHARED);
			}
		});
		
		final ToolItem statistics_item = new ToolItem(this, SWT.CHECK);
		statistics_item.setText(_("mainwindow.toolbar.statistics_item"));
		statistics_item.setData(DATA_KEY, JMULE_TABS.STATISTICS);
		statistics_item.setImage(SWTImageRepository.getImage("statistics.png"));
		button_list.add(statistics_item);
		
		statistics_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				main_window.setCurrentTab(JMULE_TABS.STATISTICS);
			}
		});
		
		final ToolItem logs_item = new ToolItem(this, SWT.CHECK);
		logs_item.setText(_("mainwindow.toolbar.logs_item"));
		logs_item.setData(DATA_KEY, JMULE_TABS.LOGS);
		logs_item.setImage(SWTImageRepository.getImage("logs.png"));
		button_list.add(logs_item);
		
		logs_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				main_window.setCurrentTab(JMULE_TABS.LOGS);
			}
		});	
	}

	public static Toolbar getInstance() {
		return toolBarInstance;
	}
	
	public void setSelection(JMULE_TABS selectedTab) {
		if (selectedTab == null) {
			for(ToolItem item : button_list) 
				item.setSelection(false);
			return ;
		}
		for(ToolItem item : button_list) {
			JMULE_TABS tab = (JMULE_TABS) item.getData(DATA_KEY);
			if (tab == selectedTab)
				item.setSelection(true);
			else 
				item.setSelection(false);
		}
		
	}
	
	public void toogleVisibility() {
		setVisible(!getVisible());
		SWTPreferences.getInstance().setToolBarVisible(getVisible());
		grid_data.exclude = !grid_data.exclude;
		setLayoutData(grid_data);
		layout();
	}
	
	protected void checkSubclass() {
    }
	
}
