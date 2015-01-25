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
package org.jmule.ui.swt.tab.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.jmule.core.JMConstants;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTPreferences;

/**
 * Created on Aug 19, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2011/03/27 16:51:29 $
 */
public class GeneralTab extends AbstractSettingsTreeItem {

	private Text nick_name_text;
	private Button prompt_on_exit_check, server_list_update;
	private Button show_nightly_build_warning = null;
	private Button connect_at_startup = null;
	private Button kad_enabled = null;
	
	private ConfigurationManager config_manager;
	private SWTPreferences swt_preferences;
	
	private int EDIT_FIELD_WIDTH = 60;
	
	private Spinner tcp_port, udp_port;
	private Button enable_udp, kbit_button, kbyte_button, enable_download_limit, enable_upload_limit, startup_update_check;
	private Text download_limit, upload_limit, download_capacity, upload_capacity;
	
	private boolean kbyte_selected = true;
	
	public GeneralTab(Composite parent) {
		super(parent);
	
		Listener number_filter = new Listener() {
			public void handleEvent(Event e) {
		        String text = e.text;

		        char[] chars = new char[text.length()];
		        text.getChars(0, chars.length, chars, 0);
		        for (int i = 0; i < chars.length; i++) {
		          if (!('0' <= chars[i] && chars[i] <= '9')) {
			            e.doit = false;
			            return;
		          }
				}
			}
		};
		
		GridData layout_data;
		GridLayout layout;
		Composite container;
		
		config_manager = _core.getConfigurationManager();
		swt_preferences = SWTPreferences.getInstance();
		
		content.setLayout(new GridLayout(2,false));
		Label label;
		
		label = new Label(content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("settingswindow.tab.general.label.nickname") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		nick_name_text = new Text(content,SWT.BORDER);
		nick_name_text.setFont(skin.getDefaultFont());
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		nick_name_text.setLayoutData(layout_data);
		
		try {
			nick_name_text.setText(_core.getConfigurationManager().getNickName());
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		
		prompt_on_exit_check = new Button(content,SWT.CHECK);
		prompt_on_exit_check.setText(_._("settingswindow.tab.general.checkbox.prompt_on_exit"));
		prompt_on_exit_check.setSelection(swt_preferences.promptOnExit());
		
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.horizontalSpan = 2;
		prompt_on_exit_check.setLayoutData(layout_data);
		
		server_list_update = new Button(content,SWT.CHECK);
		server_list_update.setText(_._("settingswindow.tab.general.checkbox.update_server_list"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.horizontalSpan = 2;
		server_list_update.setLayoutData(layout_data);
		boolean update = false;
		try {
			update = config_manager.updateServerListAtConnect();
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		server_list_update.setSelection(update);

		startup_update_check = new Button(content,SWT.CHECK);
		startup_update_check.setText(_._("settingswindow.tab.general.checkbox.startup_update_check"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.horizontalSpan = 2;
		startup_update_check.setLayoutData(layout_data);
		startup_update_check.setSelection(swt_preferences.updateCheckAtStartup());
		
		connect_at_startup = new Button(content,SWT.CHECK);
		connect_at_startup.setText(_._("settingswindow.tab.general.checkbox.connect_at_startup"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.horizontalSpan = 2;
		connect_at_startup.setLayoutData(layout_data);
		connect_at_startup.setSelection(swt_preferences.isConnectAtStartup());
		
		kad_enabled = new Button(content,SWT.CHECK);
		kad_enabled.setText(_._("settingswindow.tab.general.checkbox.enable_kad"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.horizontalSpan = 2;
		kad_enabled.setLayoutData(layout_data);
		try {
			kad_enabled.setSelection(config_manager.isJKadAutoconnectEnabled());
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		
		
		if (JMConstants.IS_NIGHTLY_BUILD) {
			show_nightly_build_warning = new Button(content,SWT.CHECK);
			show_nightly_build_warning.setText(_._("settingswindow.tab.general.checkbox.show_nightly_build_warning"));
			layout_data = new GridData(GridData.FILL_HORIZONTAL);
			layout_data.horizontalSpan = 2;
			show_nightly_build_warning.setLayoutData(layout_data);
			show_nightly_build_warning.setSelection(swt_preferences.isNightlyBuildWarning());
		}
		
		Group ports = new Group(content,SWT.NONE);
		ports.setText(_._("settingswindow.tab.general.group.ports"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.horizontalSpan = 2;
		ports.setLayoutData(layout_data);
		
		ports.setLayout(new GridLayout(2,false));
		
		label = new Label(ports,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setForeground(skin.getDefaultColor());
		label.setText(_._("settingswindow.tab.connection.label.tcp_port") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		tcp_port = new Spinner (ports, SWT.BORDER);
		tcp_port.setMinimum(1);
		tcp_port.setMaximum(65535);
		try {
			tcp_port.setSelection(config_manager.getTCP());
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		tcp_port.setIncrement(1);
		tcp_port.setPageIncrement(100);

		label = new Label(ports,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setForeground(skin.getDefaultColor());
		label.setText(_._("settingswindow.tab.connection.label.udp_port") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Composite container1 = new Composite(ports,SWT.NONE);
		container1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout(2,false);
		layout.marginWidth=0;
		layout.marginHeight=0;
		container1.setLayout(layout);
		
		udp_port = new Spinner (container1, SWT.BORDER);
		udp_port.setMinimum(1);
		udp_port.setMaximum(65535);
		try {
			udp_port.setSelection(config_manager.getUDP());
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		udp_port.setIncrement(1);
		udp_port.setPageIncrement(100);
		
		enable_udp = new Button(container1,SWT.CHECK);
		enable_udp.setText(_._("settingswindow.tab.connection.button.enabled"));
		enable_udp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateUDPControls();
			}
			
		});
		try {
			enable_udp.setSelection(config_manager.isUDPEnabled());
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		
		updateUDPControls();
		
		Group limits = new Group(content,SWT.NONE);
		limits.setText(_._("settingswindow.tab.general.group.limits"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.horizontalSpan = 2;
		limits.setLayoutData(layout_data);
		limits.setLayout(new GridLayout(2,false));
		
		label = new Label(limits,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setForeground(skin.getDefaultColor());
		label.setText(_._("settingswindow.tab.connection.label.download_limit") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Composite container2 = new Composite(limits,SWT.NONE);
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		container2.setLayoutData(layout_data);
		layout = new GridLayout(3,false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container2.setLayout(layout);
		
		download_limit = new Text(container2,SWT.BORDER );
		download_limit.addListener(SWT.Verify, number_filter);
		layout_data = new GridData();
		layout_data.widthHint = EDIT_FIELD_WIDTH;
		download_limit.setLayoutData(layout_data);
		try {
			download_limit.setText((config_manager.getDownloadLimit()/1024)+"");
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		
		new Label(container2,SWT.NONE).setText(_._("settingswindow.tab.connection.label.kb_s"));
		
		enable_download_limit = new Button(container2,SWT.CHECK);
		enable_download_limit.setText(_._("settingswindow.tab.connection.button.enabled"));
		enable_download_limit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateDownloadLimitControls();
			}
		});
		
		label = new Label(limits,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setForeground(skin.getDefaultColor());
		label.setText(_._("settingswindow.tab.connection.label.upload_limit") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Composite container3 = new Composite(limits,SWT.NONE);
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		container3.setLayoutData(layout_data);
		layout = new GridLayout(3,false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container3.setLayout(layout);
		
		upload_limit = new Text(container3,SWT.BORDER );
		upload_limit.addListener(SWT.Verify, number_filter);
		layout_data = new GridData();
		layout_data.widthHint = EDIT_FIELD_WIDTH;
		upload_limit.setLayoutData(layout_data);
		try {
			upload_limit.setText((config_manager.getUploadLimit()/1024)+"");
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		
		new Label(container3,SWT.NONE).setText(_._("settingswindow.tab.connection.label.kb_s"));
		
		enable_upload_limit = new Button(container3,SWT.CHECK);
		enable_upload_limit.setText(_._("settingswindow.tab.connection.button.enabled"));
		enable_upload_limit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateUploadLimitControls();
			}
		});
		
		boolean enable = false;
		try {
			enable = config_manager.getDownloadLimit()==0 ? false : true;
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		enable_download_limit.setSelection(enable);
		
		try {
			enable = config_manager.getUploadLimit() == 0 ? false : true;
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		enable_upload_limit.setSelection(enable);
		
		updateDownloadLimitControls();
		updateUploadLimitControls();
		
		Group capacities = new Group(content,SWT.NONE);
		capacities.setText(_._("settingswindow.tab.general.group.capacities"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.horizontalSpan = 2;
		capacities.setLayoutData(layout_data);
		capacities.setLayout(new GridLayout(2,false));
		
		label = new Label(capacities,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setForeground(skin.getDefaultColor());
		label.setText(_._("settingswindow.tab.connection.label.download_capacity") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		download_capacity = new Text(capacities,SWT.BORDER );
		download_capacity.addListener(SWT.Verify, number_filter);
		layout_data = new GridData();
		layout_data.widthHint = EDIT_FIELD_WIDTH;
		download_capacity.setLayoutData(layout_data);
		
		label = new Label(capacities,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setForeground(skin.getDefaultColor());
		label.setText(_._("settingswindow.tab.connection.label.upload_capacity") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		upload_capacity = new Text(capacities,SWT.BORDER );
		upload_capacity.addListener(SWT.Verify, number_filter);
		
		layout_data = new GridData();
		layout_data.widthHint = EDIT_FIELD_WIDTH;
		upload_capacity.setLayoutData(layout_data);
		
		new Label(capacities,SWT.NONE);
		
		container = new Composite(capacities,SWT.NONE);
		layout_data = new GridData();

		container.setLayoutData(layout_data);
		layout = new GridLayout(2,false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container.setLayout(layout);
		
		kbit_button = new Button(container,SWT.RADIO);
		kbit_button.setText(_._("settingswindow.tab.connection.button.k_bit"));
		
		kbit_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (kbit_button.getSelection()) {
					if (!kbyte_selected) return ;
					kbyte_selected = false;
					long download = Long.parseLong(download_capacity.getText());
					long upload = Long.parseLong(upload_capacity.getText());
					download*=8;
					upload*=8;
					
					download_capacity.setText(download+"");
					upload_capacity.setText(upload+"");
				}
			}
		});
		
		kbyte_button = new Button(container,SWT.RADIO);
		kbyte_button.setText(_._("settingswindow.tab.connection.button.k_byte"));
		kbyte_button.setSelection(true);
		
		kbyte_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (kbyte_button.getSelection()) {
					if (kbyte_selected) return ;
					kbyte_selected = true;
					long download = Long.parseLong(download_capacity.getText());
					long upload = Long.parseLong(upload_capacity.getText());
					download/=8;
					upload/=8;
					
					download_capacity.setText(download+"");
					upload_capacity.setText(upload+"");
				}
			}
		});
		
		try {
			download_capacity.setText((config_manager.getDownloadBandwidth()/1024)+"");
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		try {
			upload_capacity.setText((config_manager.getUploadBandwidth()/1024)+"");
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}

		
	}

	public String getTabName() {
		return _._("settingswindow.tab.general.name");
	}

	public boolean checkFields() {
		String nickname = nick_name_text.getText();
		nickname = nickname.trim();
		if (nickname.length()==0) {
			nickname = ConfigurationManager.NICK_NAME;
			nick_name_text.setText(nickname);
		}
			
		if (download_limit.getText().length()==0)
			download_limit.setText("0");
		if (upload_limit.getText().length()==0)
			upload_limit.setText("0");

		
		if (download_capacity.getText().length()==0)
			download_capacity.setText("0");
		
		if (upload_capacity.getText().length()==0) 
			upload_capacity.setText("0");
		
		long download_c = Long.parseLong(download_capacity.getText());
		long upload_c = Long.parseLong(download_capacity.getText());
		if (kbit_button.getSelection()) {
			download_c/=8;
			upload_c/=8;
		}
		
		
		if (download_c==0) {
			try {
				download_capacity.setText(config_manager.getDownloadBandwidth()+"");
			} catch (ConfigurationManagerException e) {
				e.printStackTrace();
			}
			kbyte_selected = true;
			kbyte_button.setSelection(true);
			kbit_button.setSelection(false);
		}
		if (upload_c==0) {
			try {
				upload_capacity.setText(config_manager.getUploadBandwidth()+"");
			} catch (ConfigurationManagerException e) {
				e.printStackTrace();
			}
			kbyte_selected = true;
			kbyte_button.setSelection(true);
			kbit_button.setSelection(false);
		}
			
		return true;
	}

	public void save() {
		try {
			config_manager.setNickName(nick_name_text.getText());
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		try {
			config_manager.setUpdateServerListAtConnect(server_list_update.getSelection());
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
		}
		
		swt_preferences.setPromprtOnExit(prompt_on_exit_check.getSelection());
		swt_preferences.setUpdateCheckAtStartup(startup_update_check.getSelection());
		swt_preferences.setConnectAtStartup(connect_at_startup.getSelection());
		
		if (JMConstants.IS_NIGHTLY_BUILD) {
			boolean selection = show_nightly_build_warning.getSelection();
			swt_preferences.setNightlyBuildWarning(selection);
		}
		
		int tcp = tcp_port.getSelection();
		try {
			if (config_manager.getTCP() != tcp)
				config_manager.setTCP(tcp);
			int udp = udp_port.getSelection();
			if (config_manager.getUDP()!=udp)
				config_manager.setUDP(udp);
			boolean udp_status = enable_udp.getSelection();
			if (config_manager.isUDPEnabled() != udp_status)
				config_manager.setUDPEnabled(udp_status);
			long download_l = enable_download_limit.getSelection() ? Long.parseLong(download_limit.getText()) : 0;
			config_manager.setDownloadLimit(download_l*1024);
			long upload_l   = enable_upload_limit.getSelection() ? Long.parseLong(upload_limit.getText()) : 0;
			config_manager.setUploadLimit(upload_l * 1024);
			long download_c = Long.parseLong(download_capacity.getText());
			long upload_c = Long.parseLong(download_capacity.getText());
			if (kbit_button.getSelection()) {
				download_c/=8;
				upload_c/=8;
			}
			config_manager.setDownloadBandwidth(download_c*1024);
			config_manager.setUploadBandwidth(upload_c*1024);
			config_manager.setAutoconnectJKad(kad_enabled.getSelection());
		}catch(ConfigurationManagerException e) {
			e.printStackTrace();
		}
	}
	
	public void reset() {
		try {
			nick_name_text.setText(_core.getConfigurationManager().getNickName());
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		boolean update = false;
		try {
			update = config_manager.updateServerListAtConnect();
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		server_list_update.setSelection(update);
		startup_update_check.setSelection(swt_preferences.updateCheckAtStartup());
		connect_at_startup.setSelection(swt_preferences.isConnectAtStartup());
		prompt_on_exit_check.setSelection(swt_preferences.promptOnExit());
		try {
			kad_enabled.setSelection(config_manager.isJKadAutoconnectEnabled());
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		
		try {
			tcp_port.setSelection(config_manager.getTCP());
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		
		try {
			udp_port.setSelection(config_manager.getUDP());
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		
		try {
			enable_udp.setSelection(config_manager.isUDPEnabled());
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		
		try {
			download_limit.setText((config_manager.getDownloadLimit()/1024)+"");
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		
		try {
			upload_limit.setText((config_manager.getUploadLimit()/1024)+"");
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		
		boolean enable = false;
		try {
			enable = config_manager.getDownloadLimit()==0 ? false : true;
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		enable_download_limit.setSelection(enable);
		
		try {
			enable = config_manager.getUploadLimit() == 0 ? false : true;
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		enable_upload_limit.setSelection(enable);
		
		try {
			download_capacity.setText((config_manager.getDownloadBandwidth()/1024)+"");
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		try {
			upload_capacity.setText((config_manager.getUploadBandwidth()/1024)+"");
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}
		updateUDPControls();
		updateDownloadLimitControls();
		updateUploadLimitControls();
	}

	private void updateUDPControls() {
		boolean status = enable_udp.getSelection();
		udp_port.setEnabled(status);
	}
	
	private void updateDownloadLimitControls() {
		boolean status = enable_download_limit.getSelection();
		download_limit.setEnabled(status);
	}
	
	private void updateUploadLimitControls() {
		boolean status = enable_upload_limit.getSelection();
		upload_limit.setEnabled(status);
	}
	
}
