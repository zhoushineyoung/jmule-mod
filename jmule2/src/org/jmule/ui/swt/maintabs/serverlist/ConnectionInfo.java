/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2008 JMule team ( jmule@jmule.org / http://jmule.org )
 *
 *  Any parts of this program derived from other projects, or contributed
 *  by third-party developers are copyrighted by their respective authors.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version_label 2
 *  of the License, or (at your option) any later version_label.
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMuleCore;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.servermanager.Server;
import org.jmule.core.utils.Convert;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.swt.Refreshable;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.6 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/20 09:05:14 $$
 */
public class ConnectionInfo extends CTabFolder implements Refreshable {

	private Label my_info_status,my_info_client_id,my_info_nickname,
					my_info_user_ip,my_info_sharedcount;
	private Label server_address,server_name,server_desc,server_users,
					server_files,server_ping,server_hard_limit,server_soft_limit,server_version;
	private CTabItem my_info,server_info;
	
	private JMuleCore _core;
	
	public ConnectionInfo(Composite parent,JMuleCore core) {
		super(parent,SWT.BORDER);
		_core = core;
		SWTServerListWrapper.getInstance().setConnectionInfo(this);
		SWTSkin skin = null;
		try {
			
		    skin = (SWTSkin) JMuleUIManager.getJMuleUI().getSkin();
		
		}catch(Throwable t) {
		}
		
		setSimple(false);
		
		my_info = new CTabItem(this, SWT.NONE);
		my_info.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.myinfo"));
		
		server_info = new CTabItem(this, SWT.NONE);
		server_info.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.serverinfo"));
		
		setSelection(my_info);

		Composite my_info_content = new Composite(this,SWT.NONE);
		my_info.setControl(my_info_content);
		my_info_content.setLayout(new GridLayout(2,false));
		
		Label label;
		label = new Label(my_info_content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.myinfo.status")+" : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		my_info_status = new Label(my_info_content,SWT.NONE);
		my_info_status.setFont(skin.getLabelFont());
		my_info_status.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(my_info_content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.myinfo.id")+ " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		my_info_client_id = new Label(my_info_content,SWT.NONE);
		my_info_client_id.setFont(skin.getLabelFont());
		my_info_client_id.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(my_info_content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.myinfo.ip") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		my_info_user_ip = new Label(my_info_content,SWT.NONE);
		my_info_user_ip.setFont(skin.getLabelFont());
		my_info_user_ip.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(my_info_content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.myinfo.nickname") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		my_info_nickname = new Label(my_info_content,SWT.NONE);
		my_info_nickname.setFont(skin.getLabelFont());
		my_info_nickname.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		try {
			my_info_nickname.setText(_core.getConfigurationManager().getNickName());
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
		}
		_core.getConfigurationManager().addConfigurationListener(new ConfigurationAdapter() {
			public void nickNameChanged(String nickName) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						try {
							my_info_nickname.setText(_core.getConfigurationManager().getNickName());
						} catch (ConfigurationManagerException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		
		label = new Label(my_info_content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.myinfo.sharedfiles") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		my_info_sharedcount = new Label(my_info_content,SWT.NONE);
		my_info_sharedcount.setFont(skin.getLabelFont());
		my_info_sharedcount.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		my_info_sharedcount.setText(_core.getSharingManager().getFileCount()+"");
		//my_info_scroll.setContent(my_info_content);
		
		//ScrolledContent server_info_scroll = new ScrolledContent(this);
		//server_info.setControl(server_info_scroll);

		Composite server_info_content = new Composite(this,SWT.NONE);
		server_info_content.setLayout(new GridLayout(2,false));
		server_info.setControl(server_info_content);
		label = new Label(server_info_content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.serverinfo.address")+" : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		server_address = new Label(server_info_content,SWT.NONE);
		server_address.setFont(skin.getLabelFont());
		server_address.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(server_info_content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.serverinfo.name") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		server_name = new Label(server_info_content,SWT.NONE);
		server_name.setFont(skin.getLabelFont());
		server_name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(server_info_content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.serverinfo.desc") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		server_desc = new Label(server_info_content,SWT.NONE);
		server_desc.setFont(skin.getLabelFont());
		server_desc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(server_info_content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.serverinfo.users") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		server_users = new Label(server_info_content,SWT.NONE);
		server_users.setFont(skin.getLabelFont());
		server_users.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(server_info_content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.serverinfo.files") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		server_files = new Label(server_info_content,SWT.NONE);
		server_files.setFont(skin.getLabelFont());
		server_files.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(server_info_content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.serverinfo.ping") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		server_ping = new Label(server_info_content,SWT.NONE);
		server_ping.setFont(skin.getLabelFont());
		server_ping.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(server_info_content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.serverinfo.hardlimit") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		server_hard_limit = new Label(server_info_content,SWT.NONE);
		server_hard_limit.setFont(skin.getLabelFont());
		server_hard_limit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(server_info_content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.serverinfo.softlimit") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		server_soft_limit = new Label(server_info_content,SWT.NONE);
		server_soft_limit.setFont(skin.getLabelFont());
		server_soft_limit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(server_info_content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.serverinfo.version") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		server_version = new Label(server_info_content,SWT.NONE);
		server_version.setFont(skin.getLabelFont());
		server_version.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		//server_info_scroll.setContent(server_info_content);
		
		setStatusDisconnected();

	}
	
	public void setStatusDisconnected() {
		my_info_status.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.myinfo.disconnected"));
        my_info_client_id.setText("");
        my_info_client_id.setToolTipText("");
        my_info_user_ip.setText("");
        
        server_address.setText("");
        server_ping.setText("");
        server_desc.setText("");
        server_name.setText("");
        server_users.setText("");
        server_files.setText("");
        server_hard_limit.setText("");
        server_soft_limit.setText("");
        server_version.setText("");
        
	}
	
	public void setStatusConnecting(Server server) {
		my_info_status.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.myinfo.connecting"));
        my_info_client_id.setText("");
        my_info_client_id.setToolTipText("");
        my_info_user_ip.setText("");
        
        if (server!=null)
        	server_address.setText(server.getAddress()+" : "+server.getPort());
        else 
        	server_address.setText("");
        server_ping.setText("");
        server_desc.setText("");
        server_name.setText("");
        server_users.setText("");
        server_files.setText("");
        server_hard_limit.setText("");
        server_soft_limit.setText("");
        server_version.setText("");
		
	}
	
	public void setStatusConnected( Server server) {
		my_info_status.setText(Localizer._("mainwindow.serverlisttab.connectioninfo.myinfo.connected"));
		long client_id = Convert.intToLong(server.getClientID().hashCode());
        my_info_client_id.setText(client_id + " " + (server.getClientID().isHighID() ? Localizer._("mainwindow.serverlisttab.connectioninfo.myinfo.high_id") : Localizer._("mainwindow.serverlisttab.connectioninfo.myinfo.low_id") ));
        my_info_client_id.setToolTipText("");
        my_info_user_ip.setText(server.getClientID().getAsString() + "");
        
        server_address.setText(server.getAddress() + ":" + server.getPort());
        server_name.setText(server.getName());
        server_desc.setText(server.getDesc());
        server_ping.setText(server.getPing() + "");
        server_users.setText(server.getNumUsers()+"");
        server_files.setText(server.getNumFiles()+"");
        server_hard_limit.setText(server.getNumUsers()+"");
        server_soft_limit.setText(server.getSoftLimit()+"");
        server_version.setText(server.getVersion()+"");
		
	}
	
	
	protected void checkSubclass() {}

	public void refresh() {
		if (isDisposed()) return ;
		my_info_sharedcount.setText(_core.getSharingManager().getFileCount()+"");
	}

	
}
