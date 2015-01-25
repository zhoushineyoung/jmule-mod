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
package org.jmule.ui.swt.tab.main.serverlist;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.jmule.core.JMuleCore;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.GUIUpdater;
import org.jmule.ui.swt.common.SashControl;
import org.jmule.ui.swt.skin.SWTSkin;
import org.jmule.ui.swt.tab.MainTab;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2011/03/27 16:51:29 $$
 */
public class ServerListTab extends MainTab {

	private Composite server_info_panel;
	private Composite server_table_panel;
	private ConnectionInfo connection_info;
	private ServerList server_list;
	private Group server_list_group;
	
	public ServerListTab(Composite ctab,JMuleCore core) {
		super(ctab,"Servers");
	
		SWTSkin skin = null;
		try {
			
		    skin = (SWTSkin)JMuleUIManager.getJMuleUI().getSkin();
		
		}catch(Throwable t) {}
		FormLayout form = new FormLayout ();
		tab_content.setLayout (form);
		
		server_table_panel = new Composite(tab_content, SWT.NONE);
		server_table_panel.setLayout(new FillLayout());
		
		server_info_panel = new Composite(tab_content,SWT.BORDER);
		
		
		SashControl.createHorizontalSash(20, 50, tab_content, server_table_panel, server_info_panel);
		
		server_info_panel.setLayout(new FormLayout());
		
		Composite server_messages = new Composite(server_info_panel,SWT.NONE);
		server_messages.setLayout(new FillLayout());
		
		Composite peer_info = new Composite(server_info_panel,SWT.NONE);
		peer_info.setLayout(new FillLayout());
		
		SashControl.createVerticalSash(20, 70, server_info_panel, server_messages, peer_info);

		// Server List
		server_list_group = new Group(server_table_panel,SWT.NONE);
		server_list_group.setLayout(new FillLayout());
		
		setServerCount(core.getServerManager().getServersCount());
		
		server_list = new ServerList(server_list_group,core.getServerManager());
		
		//Server Messages
		new ServerMessages(server_messages);
		
		connection_info = new ConnectionInfo(peer_info,core);
		
		connection_info.setFont(skin.getDefaultFont());
		
		SWTServerListWrapper.getInstance().setServerListTab(this);
	}
	
	public void setServerCount(int count) {
		server_list_group.setText(_._("mainwindow.serverlisttab.group.servers") + "(" +count +")");
		tab_content.layout();
	}

	public JMULE_TABS getTabType() {
		return JMULE_TABS.SERVERLIST;
	}
	
	public void lostFocus() {
		//System.out.println("Serverlist :: lostFocus");
		GUIUpdater.getInstance().removeRefreshable(server_list);
		GUIUpdater.getInstance().removeRefreshable(connection_info);
	}

	public void obtainFocus() {
		//System.out.println("Serverlist :: obtainFocus");
		GUIUpdater.getInstance().addRefreshable(server_list);
		GUIUpdater.getInstance().addRefreshable(connection_info);
	}

}
