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
package org.jmule.ui.swing.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jmule.core.JMConstants;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.ui.IDialog;
import org.jmule.ui.swing.SwingConstants;
import org.jmule.ui.swing.SwingPreferences;
import org.jmule.ui.swing.common.IntegerTextField;
import org.jmule.ui.swing.common.PortTextField;

/**
 *
 * Created on Sep 21, 2008
 * @author javajox
 * @version $Revision: 1.4 $
 * Last changed by $Author: javajox $ on $Date: 2009/09/22 19:08:43 $
 */
public class SettingsDialog extends JDialog implements IDialog {

	private JLabel nickname_label;
	private JTextField nickname_text_field;
	private JPanel limits_panel;
	private JCheckBox enable_udp_checkbox;
	private JButton apply_button;
	private JButton cancel_button;
	private JButton ok_button;
	private JPanel operations_panel;
	private JRadioButton capacity_k_byte_s_radiobox;
	private JRadioButton capacity_k_bit_s_radiobox;
	private IntegerTextField upload_capacity_text_field;
	private PortTextField udp_text_field;
	private PortTextField tcp_text_field;
	private JLabel udp_label;
	private JLabel tcp_label;
	private JPanel ports_panel;
	private IntegerTextField download_capacity_text_field;
	private JLabel upload_capacity_label;
	private JLabel download_capacity_label;
	private JPanel capacities_panel;
	private JCheckBox enabled_upload_limit_checkbox;
	private JCheckBox enabled_download_limit_checkbox;
	private JLabel upload_kb_s_label;
	private JLabel download_kb_s_label;
	private IntegerTextField upload_limit_text_field;
	private IntegerTextField download_limit_text_field;
	private JLabel upload_limit_label;
	private JLabel download_limit_label;
	private JCheckBox nightly_build_warning_checkbox;
	private JCheckBox check_for_updates_checkbox;
	private JCheckBox update_servelist_checkbox;
	private JCheckBox prompt_on_exit_checkbox;
	private ButtonGroup button_group;
	private DialogAction dialog_action;
	private boolean already_marked_k_byte = true;
	private boolean already_marked_k_bit = false;

	private ConfigurationManager _config = JMuleCoreFactory.getSingleton().getConfigurationManager();
	private SwingPreferences _ui_pref = SwingPreferences.getSingleton();
	
	public SettingsDialog(JFrame frame) {
	   super(frame);
	   nickname_label = new JLabel();
	   nickname_text_field = new JTextField();
	   prompt_on_exit_checkbox = new JCheckBox();
	   update_servelist_checkbox = new JCheckBox();
	   check_for_updates_checkbox = new JCheckBox();
	   nightly_build_warning_checkbox = new JCheckBox();
	   tcp_label = new JLabel();
	   udp_label = new JLabel();
	   tcp_text_field = new PortTextField();
	   udp_text_field = new PortTextField();
	   download_limit_label = new JLabel();
	   upload_limit_label = new JLabel();
	   download_limit_text_field = new IntegerTextField();
	   upload_limit_text_field = new IntegerTextField();
	   download_kb_s_label = new JLabel();
	   upload_kb_s_label = new JLabel();
	   enabled_download_limit_checkbox = new JCheckBox();
	   enabled_upload_limit_checkbox = new JCheckBox();
	   enable_udp_checkbox = new JCheckBox();
	   download_capacity_label = new JLabel();
	   upload_capacity_label = new JLabel();
	   download_capacity_text_field = new IntegerTextField();
	   upload_capacity_text_field = new IntegerTextField();
	   capacity_k_bit_s_radiobox = new JRadioButton();
	   capacity_k_byte_s_radiobox = new JRadioButton();
	   ok_button = new JButton();
	   cancel_button = new JButton();
	   apply_button = new JButton();
	   button_group = new ButtonGroup();
	   
	   init();	
	   
	   // set the values from SettingsManager
	   setInitData();
	}
	
	private void init() {
		
		// setup buttons
		ok_button.setText("OK");
		cancel_button.setText("Cancel");
		apply_button.setText("Apply");
		
		// setup labels
		nickname_label.setText("Nickname");
		tcp_label.setText("TCP");
		udp_label.setText("UDP");
		download_limit_label.setText("Download");
		upload_limit_label.setText("Upload");
		download_kb_s_label.setText("KB/s");
		upload_kb_s_label.setText("KB/s");
		download_capacity_label.setText("Download");
		upload_capacity_label.setText("Upload");
		
		// setup radioboxes
		capacity_k_bit_s_radiobox.setText("KBit/s");
		capacity_k_byte_s_radiobox.setText("KByte/s");
		capacity_k_byte_s_radiobox.setSelected(true);
		
		capacity_k_bit_s_radiobox.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				if(capacity_k_bit_s_radiobox.isSelected() && !already_marked_k_bit) {
					long down = Integer.parseInt(download_capacity_text_field.getText());
					long up = Integer.parseInt(upload_capacity_text_field.getText());
					download_capacity_text_field.setText((down*=8) + "");
					upload_capacity_text_field.setText((up*=8) + "");
					already_marked_k_bit = true;
					already_marked_k_byte = false;
				}
			}
		});
		capacity_k_byte_s_radiobox.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				if(capacity_k_byte_s_radiobox.isSelected() && !already_marked_k_byte) {
					long down = Integer.parseInt(download_capacity_text_field.getText());
					long up = Integer.parseInt(upload_capacity_text_field.getText());
					download_capacity_text_field.setText((down/=8) + "");
					upload_capacity_text_field.setText((up/=8) + "");
					already_marked_k_byte = true;
					already_marked_k_bit = false;
				}
			}
		});
		
		// setup checkboxes
		update_servelist_checkbox.setText("Update server list on connect to server");
		prompt_on_exit_checkbox.setText("Prompt on exit");
		check_for_updates_checkbox.setText("Check for updates at startup");
		nightly_build_warning_checkbox.setText("Show nightly build warning at startup");
		enable_udp_checkbox.setText("Enabled");
		enabled_download_limit_checkbox.setText("Enabled");
		enabled_upload_limit_checkbox.setText("Enabled");
		
		//enable_udp_checkbox.setSelected(true);
		enable_udp_checkbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			    if(enable_udp_checkbox.isSelected()) udp_text_field.setEnabled(true);
			    else udp_text_field.setEnabled(false);
			}
		});		
		enabled_download_limit_checkbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			   if(enabled_download_limit_checkbox.isSelected()) download_limit_text_field.setEnabled(true);
			   else download_limit_text_field.setEnabled(false);
			}
		});
		enabled_upload_limit_checkbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			   if(enabled_upload_limit_checkbox.isSelected()) upload_limit_text_field.setEnabled(true);
			   else upload_limit_text_field.setEnabled(false);
			}
		});
		
		// setup textfields
		download_limit_text_field.setEnabled(false);
		upload_limit_text_field.setEnabled(false);
		
		
		try {
			{
				GridBagLayout thisLayout = new GridBagLayout();
				thisLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1};
				thisLayout.rowHeights = new int[] {7, 30, 29, 31, 29, 30, 78, 80, 103, 7};
				thisLayout.columnWeights = new double[] {0.1, 0.1, 0.1, 0.1};
	            thisLayout.columnWidths = new int[] {7, 7, 7, 7};
				getContentPane().setLayout(thisLayout);
				getContentPane().add(nickname_label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				getContentPane().add(nickname_text_field, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				getContentPane().add(prompt_on_exit_checkbox, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 25, 0, 0), 0, 0));
                getContentPane().add(update_servelist_checkbox, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 25, 0, 0), 0, 0));
				getContentPane().add(check_for_updates_checkbox, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 25, 0, 0), 0, 0));
				getContentPane().add(nightly_build_warning_checkbox, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 25, 0, 0), 0, 0));
				{
					ports_panel = new JPanel();
					GridBagLayout ports_panelLayout = new GridBagLayout();
					ports_panelLayout.columnWidths = new int[] {7, 7, 94, 7};
					ports_panelLayout.rowHeights = new int[] {7, 7};
					ports_panelLayout.columnWeights = new double[] {0.1, 0.1, 0.0, 0.1};
					ports_panelLayout.rowWeights = new double[] {0.1, 0.1};
					getContentPane().add(ports_panel, new GridBagConstraints(0, 6, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
					ports_panel.setLayout(ports_panelLayout);
					ports_panel.setBorder(BorderFactory.createTitledBorder("Ports"));
					ports_panel.add(tcp_label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                    ports_panel.add(udp_label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                    ports_panel.add(tcp_text_field, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 0, 15), 0, 0));
					ports_panel.add(udp_text_field, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 0, 15), 0, 0));
					ports_panel.add(enable_udp_checkbox, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                }
				{
					limits_panel = new JPanel();
					GridBagLayout limits_panelLayout = new GridBagLayout();
					limits_panelLayout.columnWidths = new int[] {106, 92, 55, 7};
					limits_panelLayout.rowHeights = new int[] {7, 7};
					limits_panelLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
					limits_panelLayout.rowWeights = new double[] {0.1, 0.1};
					getContentPane().add(limits_panel, new GridBagConstraints(0, 7, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
					limits_panel.setLayout(limits_panelLayout);
					limits_panel.setBorder(BorderFactory.createTitledBorder("Limits"));
					limits_panel.add(download_limit_label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					limits_panel.add(upload_limit_label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					limits_panel.add(download_limit_text_field, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 0, 15), 0, 0));
					limits_panel.add(upload_limit_text_field, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 0, 15), 0, 0));
					limits_panel.add(download_kb_s_label, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					limits_panel.add(upload_kb_s_label, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					limits_panel.add(enabled_download_limit_checkbox, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					limits_panel.add(enabled_upload_limit_checkbox, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                }
				{
					capacities_panel = new JPanel();
					GridBagLayout capacities_panelLayout = new GridBagLayout();
					capacities_panelLayout.columnWidths = new int[] {7, 7, 7, 7};
					capacities_panelLayout.rowHeights = new int[] {7, 7, 20};
					capacities_panelLayout.columnWeights = new double[] {0.1, 0.1, 0.1, 0.1};
					capacities_panelLayout.rowWeights = new double[] {0.1, 0.1, 0.1};
					getContentPane().add(capacities_panel, new GridBagConstraints(0, 8, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
					capacities_panel.setLayout(capacities_panelLayout);
					capacities_panel.setBorder(BorderFactory.createTitledBorder("Capacities"));
					capacities_panel.add(download_capacity_label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                    capacities_panel.add(upload_capacity_label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                    capacities_panel.add(download_capacity_text_field, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					capacities_panel.add(upload_capacity_text_field, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					capacities_panel.add(capacity_k_bit_s_radiobox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                    capacities_panel.add(capacity_k_byte_s_radiobox, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                    button_group.add(capacity_k_bit_s_radiobox);
					button_group.add(capacity_k_byte_s_radiobox);
					
				}
				{
					operations_panel = new JPanel();
					GridBagLayout operations_panelLayout = new GridBagLayout();
					getContentPane().add(operations_panel, new GridBagConstraints(0, 9, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
					operations_panelLayout.rowWeights = new double[] {0.1};
					operations_panelLayout.rowHeights = new int[] {7};
					operations_panelLayout.columnWeights = new double[] {0.1, 0.1, 0.1, 0.1};
					operations_panelLayout.columnWidths = new int[] {7, 7, 7, 7};
					operations_panel.setLayout(operations_panelLayout);
					operations_panel.add(ok_button, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
					operations_panel.add(cancel_button, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
					operations_panel.add(apply_button, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ok_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				saveSettings();
				setVisible(false);
			}
		});
		
		apply_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				saveSettings();
			}
		});
		
		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				setVisible(false);
			}
		});
	}
	
	public DialogAction getDialogAction() {

		return dialog_action;
	}
	
	public String getNickname() {
		
		return nickname_text_field.getText();
	}
	
	public int getTCP() {
		
		return Integer.parseInt(tcp_text_field.getText());
	}
	
	public int getUDP() {
		
		return Integer.parseInt(udp_text_field.getText());
	}
	
	public boolean isUDPEnabled() {
		
		return enable_udp_checkbox.isSelected();
	}
	
	public long getDownloadLimit() {
		
		return Long.parseLong(download_limit_text_field.getText()) * 1024;
	}
	
	public long getUploadLimit() {
		
		return Long.parseLong(upload_limit_text_field.getText()) * 1024;
	}
	
	public boolean isPromptOnExitEnabled() {
		
		return prompt_on_exit_checkbox.isSelected();
	}
	
	public boolean isUpdateServerListOnConnect() {
		
		return update_servelist_checkbox.isSelected();
	}
	
	public boolean isCheckForUpdatesEnabled() {
		
		return check_for_updates_checkbox.isSelected();
	}
	
	public boolean isNightlyBuildWarning() {
		
		return nightly_build_warning_checkbox.isSelected();
	}
	
	public boolean isDownloadLimitEnabled() {
		
		return enabled_download_limit_checkbox.isSelected();
	}
	
	public boolean isUploadLimitEnabled() {
		
		return enabled_upload_limit_checkbox.isSelected();
	}
	
	public long getDownloadCapacity() {
		
		long row_download = Long.parseLong(download_capacity_text_field.getText());
		if(capacity_k_bit_s_radiobox.isSelected()) 
		  return (row_download/8)*1024;
		return row_download*1024;
	}
	
	public long getUploadCapacity() {
		
		long row_upload = Long.parseLong(upload_capacity_text_field.getText());
		if(capacity_k_bit_s_radiobox.isSelected()) 
		  return (row_upload/8)*1024;
		return row_upload*1024;
	}
	
	public void setUDP(int udp) {
		
		udp_text_field.setText(udp + "");
	}
	
	public void setTCP(int tcp) {
		
		tcp_text_field.setText(tcp + "");
	}
	
	public void setDownloadBandwidth(long downloadBandwidth) {
		
		download_capacity_text_field.setText(downloadBandwidth + "");
	}
	
	public void setUploadBandwidth(long uploadBandwidth) {
		
		upload_capacity_text_field.setText(uploadBandwidth + "");
	}
	
	public void setDownloadLimitEnabled(boolean enabled) {
		
		enabled_download_limit_checkbox.setSelected(enabled);
		download_limit_text_field.setEnabled(enabled);
	}
	
	public void setUploadLimitEnabled(boolean enabled) {
		
		enabled_upload_limit_checkbox.setSelected(enabled);
		upload_limit_text_field.setEnabled(enabled);
	}
	
	public void setDownloadLimit(long downloadLimit) {
		
		download_limit_text_field.setText(downloadLimit + "");
	}
	
	public void setUploadLimit(long uploadLimit) {
		
		upload_limit_text_field.setText(uploadLimit + "");
	}
	
	public void setNickname(String nickName) {
		
		nickname_text_field.setText(nickName);
	}
	
	public void setUpdateServerListEnabled(boolean value) {
		
		update_servelist_checkbox.setSelected(value);
	}
	
	public void setCheckForUpdatesEnabled(boolean value) {
		
		check_for_updates_checkbox.setSelected(value);
	}
	
	public void setPromptOnExitEnabled(boolean value) {
		
		prompt_on_exit_checkbox.setSelected(value);
	}
	
	public void setNightlyBuildWarningEnabled(boolean value) {
		
		nightly_build_warning_checkbox.setSelected(value);
	}
	
	private void setInitData() {
		try {
		   // core settings
		   this.setNickname(_config.getNickName());
		   this.setTCP(_config.getTCP());
		   this.setUDP(_config.getUDP());
		   this.enable_udp_checkbox.setSelected(_config.isUDPEnabled());
		   this.udp_text_field.setEnabled(_config.isUDPEnabled());			
		   this.setDownloadBandwidth(_config.getDownloadBandwidth()/1024);
		   this.setUploadBandwidth(_config.getUploadBandwidth()/1024);
		   if( _config.getDownloadLimit() != 0 ) {
			    this.setDownloadLimitEnabled(true);
			    this.setDownloadLimit(_config.getDownloadLimit()/1024);
		   } else {
				this.setDownloadLimitEnabled(false);
				this.setDownloadLimit(0);
		   }
		   if( _config.getUploadLimit() !=0 ) {
				this.setUploadLimitEnabled(true);
				this.setUploadLimit(_config.getUploadLimit()/1024);
		   } else {
				this.setUploadLimitEnabled(false);
				this.setUploadLimit(0);
		   }
		   // ui settings
		   //setUpdateServerListEnabled(_ui_pref.isUpdate)
		   this.setCheckForUpdatesEnabled(_ui_pref.isCheckForUpdatesAtStartup());
		   this.setPromptOnExitEnabled(_ui_pref.isPromptOnExitEnabled());
		   //this.setNightlyBuildWarningEnabled(_ui_pref.  setNightlyBuildWarningEnabled(SwingConstants.SWING_ROOT)));
		   this.setUpdateServerListEnabled(_config.updateServerListAtConnect());
		   if(JMConstants.IS_NIGHTLY_BUILD)
			  this.setNightlyBuildWarningEnabled(_ui_pref.isNightlyBuildWarning());
		}catch(Throwable cause) {
			cause.printStackTrace();
		}
	}
	
	private void saveSettings() {
		try {
			// core settings
			if( _config.getTCP() != this.getTCP() ) _config.setTCP(this.getTCP());
			if( _config.getUDP() != this.getUDP() ) _config.setUDP(this.getUDP());
			if( _config.isUDPEnabled() != this.isUDPEnabled() ) _config.setUDPEnabled(this.isUDPEnabled());
			if( !_config.getNickName().equals(this.getNickname()) )  _config.setNickName(this.getNickname());
			if( _config.getDownloadBandwidth() != this.getDownloadCapacity() )_config.setDownloadBandwidth(this.getDownloadCapacity());
			if( _config.getUploadBandwidth() != this.getUploadCapacity()) _config.setUploadBandwidth(getUploadCapacity());
			if( this.isDownloadLimitEnabled() ) _config.setDownloadLimit(getDownloadLimit());
			else _config.setDownloadLimit(0);
			if( this.isUploadLimitEnabled() ) _config.setUploadLimit(getUploadLimit());
			else _config.setUploadLimit(0);
			_config.setUpdateServerListAtConnect(isUpdateServerListOnConnect());
			
			// swing ui settings
			//if(_ui_pref.is)
			if( _ui_pref.isPromptOnExitEnabled() != isPromptOnExitEnabled() ) 
				_ui_pref.setPromptOnExit(isPromptOnExitEnabled());
			if( _ui_pref.isCheckForUpdatesAtStartup() != this.isCheckForUpdatesEnabled() ) 
				_ui_pref.setCheckForUpdatesAtStartup(isCheckForUpdatesEnabled());
		    if(JMConstants.IS_NIGHTLY_BUILD) 			
				if( _ui_pref.isNightlyBuildWarning() != this.isNightlyBuildWarning() )
					_ui_pref.setNightlyBuildWarning(this.isNightlyBuildWarning());
		}catch( Throwable cause ) {
			cause.printStackTrace();
		}
		
	}

}
