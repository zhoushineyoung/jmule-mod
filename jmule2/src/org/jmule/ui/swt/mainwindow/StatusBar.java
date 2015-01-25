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
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jmule.core.JMConstants;
import org.jmule.core.JMuleCore;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.edonkey.ClientID;
import org.jmule.core.networkmanager.NetworkManager;
import org.jmule.core.servermanager.Server;
import org.jmule.core.utils.Convert;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.GUIUpdater;
import org.jmule.ui.swt.Refreshable;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.common.SpeedScaleShell;
import org.jmule.ui.swt.skin.SWTSkin;
import org.jmule.ui.swt.tab.main.serverlist.SWTServerListWrapper;
import org.jmule.ui.utils.SpeedFormatter;

/**
 * 
 * @author binary
 * @version $Revision: 1.10 $
 * Last changed by $Author: binary255 $ on $Date: 2011/03/27 16:51:29 $
 */
public class StatusBar extends Composite {

	private GridData grid_data;
	
	private Label img_label,connection_status_label,client_id_label,downimg_label,downspeed_label,upimg_label,upspeed_label;
	
	private JMuleCore _core;
	
	private ConfigurationManager config_manager ;
	private NetworkManager network_manager ;
	
	public StatusBar(Composite parent,JMuleCore core) {
		super(parent, SWT.NONE);
		
		_core = core;
		
		config_manager = _core.getConfigurationManager();
		network_manager = _core.getNetworkManager();
		
		SWTServerListWrapper.getInstance().setStatusBar(this);
		
		SWTSkin skin = null;
		try {
			skin = (SWTSkin)JMuleUIManager.getJMuleUI().getSkin();
		}catch(Throwable t) {}
		
		
		
		grid_data = new GridData(GridData.FILL_HORIZONTAL);
		grid_data.heightHint = 16;
		
		setLayoutData(grid_data);

		GridLayout layout = new GridLayout(8,false);
		
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		setLayout(layout);

		img_label = new Label(this,SWT.NONE);
		Image img = SWTImageRepository.getImage("toolbar_disconnected.png");
		img_label.setImage(img);
		
		connection_status_label = new Label(this,SWT.NONE);
		connection_status_label.setFont(skin.getLabelFont());
		connection_status_label.setText(Localizer._("mainwindow.statusbar.label.disconnected"));

		GridData data = new GridData();
		data.heightHint = 16;
		
		new Label(this,SWT.SEPARATOR | SWT.VERTICAL).setLayoutData(data);
		
		client_id_label = new Label(this,SWT.NONE);
		client_id_label.setFont(skin.getLabelFont());
		client_id_label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
		
		downimg_label = new Label(this,SWT.NONE);
		downimg_label.setImage(SWTImageRepository.getImage("down.gif"));
		downimg_label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		downspeed_label = new Label(this,SWT.NONE);
		downspeed_label.setFont(skin.getLabelFont());
		downspeed_label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		downspeed_label.setText("");

		upimg_label = new Label(this,SWT.NONE);
		upimg_label.setImage(SWTImageRepository.getImage("up.gif"));
		upimg_label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		upspeed_label = new Label(this,SWT.NONE);
		upspeed_label.setFont(skin.getLabelFont());
		upspeed_label.setText("");
		upspeed_label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		setStatusDisconnected();
		
		downimg_label.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent arg0) {
				showDownSpeedLimitScaleWindow();
			}
		});
		
		downspeed_label.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent arg0) {
				showDownSpeedLimitScaleWindow();
			}
		});
		
		upimg_label.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent arg0) {
				showUpSpeedLimitScaleWindow();
			}
		});
		
		upspeed_label.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent arg0) {
				showUpSpeedLimitScaleWindow();
			}
		});
		
		config_manager.addConfigurationListener(new ConfigurationAdapter() {
			
			public void uploadLimitChanged(long uploadLimit) {
				String up_limit = "";
				if (uploadLimit!=0)
					try {
						up_limit = "["+(SpeedFormatter.formatByteCountToKiBEtcPerSec(config_manager.getUploadLimit(),true))+"] ";
					} catch (ConfigurationManagerException e) {
						e.printStackTrace();
					}
				String up_speed = SpeedFormatter.formatSpeed(network_manager.getUploadSpeed());
				upspeed_label.setText(up_limit + up_speed);
				layout();
			}
			
			public void downloadLimitChanged(long downloadLimit) {
				String down_limit = "";
				if (downloadLimit!=0)
					try {
						down_limit = "["+(SpeedFormatter.formatByteCountToKiBEtcPerSec(config_manager.getDownloadLimit(),true))+"] ";
					} catch (ConfigurationManagerException e) {
						e.printStackTrace();
					}
				String down_speed = SpeedFormatter.formatSpeed(network_manager.getDownloadSpeed());
				downspeed_label.setText(down_limit + down_speed);
				layout();
			}
			
		});
		
		GUIUpdater.getInstance().addRefreshable(new Refreshable() {
			public void refresh() {
				if (isDisposed()) return ;
				String down_limit = "";
				String up_limit = "";
				try {
					if (config_manager.getDownloadLimit()!=0)
						down_limit = "["+(SpeedFormatter.formatByteCountToKiBEtcPerSec(config_manager.getDownloadLimit(),true))+"] ";
					
					if (config_manager.getUploadLimit()!=0)
						up_limit = "["+(SpeedFormatter.formatByteCountToKiBEtcPerSec(config_manager.getUploadLimit(),true))+"] ";
				}catch(ConfigurationManagerException e) {
					e.printStackTrace();
				}
				String down_speed = SpeedFormatter.formatSpeed(network_manager.getDownloadSpeed());
				String up_speed = SpeedFormatter.formatSpeed(network_manager.getUploadSpeed());
				
				downspeed_label.setText(down_limit + down_speed + " ");
				upspeed_label.setText(up_limit + up_speed);
				layout();
			}
			
		});
	}
	

	public void setStatusDisconnected() {
		connection_status_label.setText(Localizer._("mainwindow.statusbar.label.disconnected"));
		Image img = SWTImageRepository.getImage("toolbar_disconnected.png");
		img_label.setImage(img);
		client_id_label.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		client_id_label.setText("");
		client_id_label.setToolTipText("");
		layout();
	}
	
	public void setStatusConnecting() {
		connection_status_label.setText(Localizer._("mainwindow.statusbar.label.connecting"));
		Image img = SWTImageRepository.getImage("toolbar_disconnected.png");
		img_label.setImage(img);
		client_id_label.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		client_id_label.setText("");
		layout();
	}
	
	public void setStatusConnected(Server server) {
		ClientID client_id = server.getClientID();
		connection_status_label.setText(Localizer._("mainwindow.statusbar.label.connected"));
		connection_status_label.setToolTipText(server.getAddress() + ":" + server.getPort());
		client_id_label.setText(client_id.isHighID() ? Localizer._("mainwindow.statusbar.label.high_id") :Localizer._("mainwindow.statusbar.label.low_id"));
		if (!client_id.isHighID())
			client_id_label.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_RED));
		else
			client_id_label.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		long id = Convert.intToLong(client_id.hashCode());
		client_id_label.setToolTipText(id+"");
		Image img = SWTImageRepository.getImage("toolbar_connected.png");
		img_label.setImage(img);
		layout();
	}
	
	public void toogleVisibility() {
		setVisible(!getVisible());
		SWTPreferences.getInstance().setStatusBarVisible(getVisible());
		grid_data.exclude = !grid_data.exclude;
		setLayoutData(grid_data);
		layout();
	}

	protected void checkSubclass() {
    }
	
	private void showDownSpeedLimitScaleWindow() {
		SpeedScaleShell speedScaleWidget = new SpeedScaleShell(_._("mainwindow.statusbar.speed_scale.download") + " :");
		long down_limit = 0;
		try {
			down_limit = config_manager.getDownloadLimit() / 1024;
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
		}
		speedScaleWidget.setMaxValue(down_limit+500);
		speedScaleWidget.setMaxTextValue(down_limit+500);
		speedScaleWidget.addOption(_._("mainwindow.statusbar.speed_scale.no_limit"), 0);
		
		List<Long> sets = getDefaultSpeedValues(down_limit);
		
		for(Long v : sets) {
			speedScaleWidget.addOption(SpeedFormatter.formatByteCountToKiBEtcPerSec(v * 1024,true), v);
		}
		
		boolean result = speedScaleWidget.open(down_limit, JMConstants.isWindows);
		if (result) {
			long value = speedScaleWidget.getValue();
			value*=1024;
			try {
				config_manager.setDownloadLimit(value);
			} catch (ConfigurationManagerException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void showUpSpeedLimitScaleWindow() {
		SpeedScaleShell speedScaleWidget = new SpeedScaleShell(_._("mainwindow.statusbar.speed_scale.upload") + " :");
		long up_limit = 0;
		try {
			up_limit = config_manager.getUploadLimit() / 1024;
		} catch (ConfigurationManagerException e) {
		
			e.printStackTrace();
		}
		speedScaleWidget.setMaxValue(up_limit+500);
		speedScaleWidget.setMaxTextValue(up_limit+500);
		speedScaleWidget.addOption(_._("mainwindow.statusbar.speed_scale.no_limit"), 0);
		
		List<Long> sets = getDefaultSpeedValues(up_limit);
		
		for(Long v : sets) {
			speedScaleWidget.addOption(SpeedFormatter.formatByteCountToKiBEtcPerSec(v * 1024,true), v);
		}
		
		boolean result = speedScaleWidget.open(up_limit, JMConstants.isWindows);
		if (result) {
			long value = speedScaleWidget.getValue();
			value*=1024;
			try {
				config_manager.setUploadLimit(value);
			} catch (ConfigurationManagerException e) {
				e.printStackTrace();
			}
		}
	}
	
	private List<Long> getDefaultSpeedValues(long speed) {
		List<Long> speeds = new ArrayList<Long>();
	
		if (speed<=128) {
			speeds.add(5L);
			speeds.add(10L);
			speeds.add(16L);
			speeds.add(32L);
			speeds.add(64L);
			speeds.add(128L);
		} else
			if ((speed>128)&&(speed<=512)) {
				speeds.add(64L);
				speeds.add(128L);
				speeds.add(256L);
				speeds.add(512L);
			} else {
				speeds.add(speed - 10);
				speeds.add(speed - 20);
				speeds.add(speed + 10);
				speeds.add(speed + 20);
			}
		
		return speeds;
	}
	
}
