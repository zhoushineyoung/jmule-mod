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
package org.jmule.ui.swing.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jmule.core.JMRunnable;
import org.jmule.core.JMThread;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.ui.IDialog;

/**
 *
 * Created on Sep 27, 2008
 * @author javajox
 * @version $Revision: 1.2 $
 * Last changed by $Author: javajox $ on $Date: 2009/09/22 19:08:43 $
 */
public class AdjustSpeedLimitsDialog extends JDialog implements IDialog {

	private JLabel download_speed_label = new JLabel();
	private JRadioButton up_mbytes_s = new JRadioButton();
	private JRadioButton up_kbytes_s = new JRadioButton();
	private JRadioButton down_mbytes_s = new JRadioButton();
	private JRadioButton down_kbytes_s = new JRadioButton();
	private JTextField upload_speed_text_field = new JTextField();
	private JTextField download_speed_text_field = new JTextField();
	private JButton cancel_button = new JButton();
	private JButton ok_button = new JButton();
	private JLabel upload_speed_label = new JLabel();
	private ButtonGroup download_group = new ButtonGroup();
	private ButtonGroup upload_group = new ButtonGroup();
	private JCheckBox enabled_upload_limit_check_box = new JCheckBox();
	private JCheckBox enabled_download_limit_check_box = new JCheckBox();
	
	private DialogAction dialog_action = DialogAction.CANCEL;
	
	private static String kbytes_s = "KBytes/s";
	private static String mbytes_s = "MBytes/s";
	
	JMuleCore _core = JMuleCoreFactory.getSingleton();
	ConfigurationManager _config_manager = _core.getConfigurationManager();
	
	JRadioButton d_prec;
	JRadioButton u_prec;
	
	DecimalFormat format = new DecimalFormat("0.00");
	
	ActionListener d_action_listener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == d_prec) return;
			d_prec = (JRadioButton)e.getSource();
			float down_speed = Float.parseFloat(download_speed_text_field.getText());
			if(down_kbytes_s.isSelected()) 
				down_speed *= 1024;
			else 
				down_speed /= 1024;
			download_speed_text_field.setText( format.format(down_speed) );
		}
	};
	
	ActionListener u_action_listener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == u_prec) return;
			u_prec = (JRadioButton)e.getSource();
			float up_speed = Float.parseFloat(upload_speed_text_field.getText());
			if(up_kbytes_s.isSelected()) 
				up_speed *= 1024;
			else 
				up_speed /= 1024;
			upload_speed_text_field.setText( format.format(up_speed) );
		}
	};
	
	public AdjustSpeedLimitsDialog(JFrame frame) {
		super(frame, true);	
		
		this.setTitle("Set download/upload limits");
		
		enabled_upload_limit_check_box.setText("Enabled");
		enabled_download_limit_check_box.setText("Enabled");
		
		download_speed_label.setText("Download");
		upload_speed_label.setText("Upload");
		
		up_kbytes_s.setText(kbytes_s);
		up_mbytes_s.setText(mbytes_s);
		
		up_kbytes_s.addActionListener(u_action_listener);
		up_mbytes_s.addActionListener(u_action_listener);
		
		down_kbytes_s.setText(kbytes_s);
		down_mbytes_s.setText(mbytes_s);
		
		down_kbytes_s.addActionListener(d_action_listener);
		down_mbytes_s.addActionListener(d_action_listener);
		
		ok_button.setText("OK");
		cancel_button.setText("Cancel");
		
		GridBagLayout grid_bag_layout = new GridBagLayout();
		grid_bag_layout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.1, 0.0};
		grid_bag_layout.rowHeights = new int[] {7, 23, 30, 32, 30, 20, 7};
		grid_bag_layout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1};

		grid_bag_layout.columnWidths = new int[] {7, 179, 116, 83, 103, 50, 83, 7};
		getContentPane().setLayout(grid_bag_layout);
				
		getContentPane().add(download_speed_label, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 10), 0, 0));
        getContentPane().add(upload_speed_label, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 10), 0, 0));
        getContentPane().add(ok_button, new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(cancel_button, new GridBagConstraints(4, 5, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 30), 0, 0));
        getContentPane().add(download_speed_text_field, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().add(upload_speed_text_field, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().add(down_kbytes_s, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		down_kbytes_s.setSelected(true);
		getContentPane().add(down_mbytes_s, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().add(up_kbytes_s, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		up_kbytes_s.setSelected(true);
		getContentPane().add(up_mbytes_s, new GridBagConstraints(4, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().add(enabled_download_limit_check_box, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().add(enabled_upload_limit_check_box, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		download_group.add(down_kbytes_s);
		download_group.add(down_mbytes_s);
		
		upload_group.add(up_kbytes_s);
		upload_group.add(up_mbytes_s);
		
		final AdjustSpeedLimitsDialog _this = this;
		ok_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				dialog_action = DialogAction.OK;
				(new JMThread(new JMRunnable() {
					public void JMRun() {
						float d_limit = _this.getDownloadLimit();
						float u_limit = _this.getUploadLimit();
						try {
							if( _config_manager.getDownloadLimit() != d_limit ) 
								_config_manager.setDownloadLimit(Math.round(d_limit));
					        if( _config_manager.getUploadLimit() != u_limit )
							    _config_manager.setUploadLimit(Math.round(u_limit));
						}catch( Throwable cause ) {
							cause.printStackTrace();
						}
					}
				})).start();
				_this.setVisible(false);
			}
		});
		
		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			    _this.setVisible(false);	
			}
		});
		
		enabled_download_limit_check_box.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			      if( enabled_download_limit_check_box.isSelected() ) 
			    	    setDownloadSpeedEnabled(true);
			      else  setDownloadSpeedEnabled(false);
			}
		});
		
		enabled_upload_limit_check_box.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			      if( enabled_upload_limit_check_box.isSelected() )
			    	   setUploadSpeedEnabled(true);
			      else setUploadSpeedEnabled(false);
			}
		});
		
		/*_config_manager.addConfigurationListener(new ConfigurationAdapter() {
			public void downloadLimitChanged(final long downloadLimit) {			  
			  SwingUtilities.invokeLater( new Runnable() {
				   public void run() {
						 if( downloadLimit == 0 ) 
							   setDownloadSpeedEnabled(false);
						 else {
							   setDownloadSpeedEnabled(true);
							   setDownloadLimit(downloadLimit);
						 }
				   }
			  });
			}
			public void uploadLimitChanged(final long uploadLimit) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if( uploadLimit == 0 )
							   setUploadSpeedEnabled(false);
						else {
							  setUploadSpeedEnabled(true);
							  setUploadLimit(uploadLimit);
						}	
					}
				});
			}
		});*/
		
		setInitData();
		
		this.setResizable(false);
		this.setSize(451, 201);
        
    }
	
	private void setInitData() {
		try {
		  if(_config_manager.getDownloadLimit() == 0) 
			 setDownloadSpeedEnabled(false);
		  else {
			 setDownloadSpeedEnabled(true);
			 setDownloadLimit(_config_manager.getDownloadLimit() );
		  }
		  if(_config_manager.getUploadLimit() == 0) 
			setUploadSpeedEnabled(false);
		  else {
			 setUploadSpeedEnabled(true);
			 setUploadLimit(_config_manager.getUploadLimit());
		  }
		}catch( Throwable cause ) {
			cause.printStackTrace();
		}
	}
	
	public void setDownloadLimit(long downloadLimit) {
		
		if(downloadLimit >= 1024 * 1024) {
			downloadLimit /= 1024;
			downloadLimit /= 1024;
			down_mbytes_s.setSelected(true);
			d_prec = down_mbytes_s;
		} else {
			downloadLimit /= 1024;
			down_kbytes_s.setSelected(true);
			d_prec = down_kbytes_s;
		}
		download_speed_text_field.setText(format.format(downloadLimit));
	}
	
	public void setUploadLimit(long uploadLimit) {
		
		if(uploadLimit >= 1024 * 1024) {
			uploadLimit /= 1024;
			uploadLimit /= 1024;
			up_mbytes_s.setSelected(true);
			u_prec = up_mbytes_s;
		} else {
			uploadLimit /= 1024;
			up_kbytes_s.setSelected(true);
			u_prec = up_kbytes_s;
		}
		upload_speed_text_field.setText(format.format(uploadLimit));
	}
	
	public float getDownloadLimit() {
		
		Float d_limit = Float.parseFloat(download_speed_text_field.getText()) * 1024;
		if(down_mbytes_s.isSelected()) d_limit*=1024;
		else return d_limit;
		return d_limit;
	}
	
	public float getUploadLimit() {
		
		Float u_limit = Float.parseFloat(upload_speed_text_field.getText()) * 1024;
		if(up_mbytes_s.isSelected()) u_limit*=1024;
		else return u_limit;
		return u_limit;
	}
	
	private void setDownloadSpeedEnabled(boolean value) {
		enabled_download_limit_check_box.setSelected(value);
		download_speed_label.setEnabled(value);
		down_mbytes_s.setEnabled(value);
		down_kbytes_s.setEnabled(value);
		download_speed_text_field.setEnabled(value);
		download_speed_text_field.setText(format.format(0));
	}
	
	private void setUploadSpeedEnabled(boolean value) {
		enabled_upload_limit_check_box.setSelected(value);
		upload_speed_label.setEnabled(value);
		up_mbytes_s.setEnabled(value);
		up_kbytes_s.setEnabled(value);
		upload_speed_text_field.setEnabled(value);
		upload_speed_text_field.setText(format.format(0));
	}
	
	public DialogAction getDialogAction() {
		
		return dialog_action;
	}
	
}
