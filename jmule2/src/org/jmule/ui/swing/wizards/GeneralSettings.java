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
package org.jmule.ui.swing.wizards;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.ui.CommonUIPreferences;
import org.jmule.ui.swing.SwingPreferences;
import org.jmule.ui.swing.common.PortTextField;
import org.jmule.ui.swt.SWTPreferences;

/**
 * Created on 07-19-2008
 * @author javajox
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2009/09/22 19:08:43 $$
 */
public class GeneralSettings extends WizardPanel {

    private JCheckBox connect_at_start_up;
    private JPanel connection_panel;
    private JCheckBox disable_udp_port;
    private JPanel general_panel;
    private PortTextField tcp_port;
    private JLabel tcp_port_caption;
    private JLabel tcp_port_desc;
    private PortTextField udp_port;
    private JLabel udp_port_caption;
    private JLabel udp_port_desc;
    private JTextField user_name;
    private JLabel user_name_desc;
    
    JMuleCore _core = JMuleCoreFactory.getSingleton();
	
    public GeneralSettings() {
        initComponents();
    }

    private void initComponents() {

        general_panel = new JPanel();
        user_name_desc = new JLabel();
        user_name = new JTextField();
        connect_at_start_up = new JCheckBox();
        connection_panel = new JPanel();
        tcp_port_desc = new JLabel();
        tcp_port_caption = new JLabel();
        tcp_port = new PortTextField();
        udp_port_caption = new JLabel();
        udp_port = new PortTextField();
        udp_port_desc = new JLabel();
        disable_udp_port = new JCheckBox();
 
        // apply skin
        user_name_desc.setFont( skin.getLabelFont() );
        tcp_port_caption.setFont( skin.getLabelFont() );
        udp_port_desc.setFont( skin.getLabelFont() );
        connect_at_start_up.setFont( skin.getDefaultFont() );
        disable_udp_port.setFont( skin.getDefaultFont() );

        general_panel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "General"));

        user_name_desc.setText("Please enter your user name:");

        try {
          user_name.setText( _config.getNickName() );
        }catch( Throwable cause ) {
        	cause.printStackTrace();
        }

        connect_at_start_up.setText("Enable this option if you want JMule to connect at start up");

        connect_at_start_up.setSelected(false);

        CommonUIPreferences _pref = CommonUIPreferences.getSingleton();
        if(_pref.getUIType().equals("SWT")) {
        	if(SWTPreferences.getInstance().isConnectAtStartup())
        		connect_at_start_up.setSelected(true);
        	else 
        		connect_at_start_up.setSelected(false);
        } else if(_pref.getUIType().equals("SWING")) {
        	if(SwingPreferences.getSingleton().isConnectAtStartup()) 
        		connect_at_start_up.setSelected(true);
        	else 
        	    connect_at_start_up.setSelected(false);
        } 
        	
        
        org.jdesktop.layout.GroupLayout general_panelLayout = new org.jdesktop.layout.GroupLayout(general_panel);
        general_panel.setLayout(general_panelLayout);
        general_panelLayout.setHorizontalGroup(
            general_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(general_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(general_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(user_name, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 227, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(user_name_desc)
                    .add(connect_at_start_up))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        general_panelLayout.setVerticalGroup(
            general_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(general_panelLayout.createSequentialGroup()
                .add(user_name_desc)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(user_name, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 14, Short.MAX_VALUE)
                .add(connect_at_start_up)
                .addContainerGap())
        );

        connection_panel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Connection"));
        try {
	        tcp_port_desc.setText("<html>This is the main port that must be available to<br> ensure functionality of JMule</html");
	
	        tcp_port_caption.setText("TCP port");
	
	        //tcp_port.setText("11132");
	        tcp_port.setText(_config.getTCP()+"");
	        
	        udp_port_caption.setText("UDP port");
	
	        //udp_port.setText("51985");
	        udp_port.setText(_config.getUDP()+"");
	
	        udp_port_desc.setText("<html>UDP port is used for additional functionalities<br>(for better work the port must be enabled and available)</html>");
	        
	        disable_udp_port.setText("Disable");
	        
	        disable_udp_port.setSelected(!_config.isUDPEnabled());
	        
	        udp_port.setEnabled( _config.isUDPEnabled() ); 
        }catch( Throwable cause ) {
        	cause.printStackTrace();
        }
        disable_udp_port.addMouseListener(new MouseAdapter() {
           	public void mouseClicked(MouseEvent evt) {
        		 if(disable_udp_port.isSelected()) udp_port.setEnabled(false);
        		 else udp_port.setEnabled(true);
        	}
        });

        org.jdesktop.layout.GroupLayout connection_panelLayout = new org.jdesktop.layout.GroupLayout(connection_panel);
        connection_panel.setLayout(connection_panelLayout);
        connection_panelLayout.setHorizontalGroup(
            connection_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(connection_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(connection_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(connection_panelLayout.createSequentialGroup()
                        .add(connection_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(tcp_port, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(tcp_port_caption))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tcp_port_desc))
                    .add(connection_panelLayout.createSequentialGroup()
                        .add(connection_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(udp_port_caption)
                            .add(udp_port, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(udp_port_desc))
                    .add(disable_udp_port))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        connection_panelLayout.setVerticalGroup(
            connection_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(connection_panelLayout.createSequentialGroup()
                .add(connection_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(connection_panelLayout.createSequentialGroup()
                        .add(tcp_port_caption)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tcp_port, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(tcp_port_desc))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(connection_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(connection_panelLayout.createSequentialGroup()
                        .add(udp_port_caption)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(udp_port, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(udp_port_desc))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 6, Short.MAX_VALUE)
                .add(disable_udp_port)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(general_panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, connection_panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(general_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(connection_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );
    }
    
    public String getNickName() {
    	
    	return user_name.getText();
    }
    
    public String getTCP() {
    
    	return tcp_port.getText();
    }
    
    public String getUDP() {
    	
    	return udp_port.getText();
    }
    
    public boolean isUDPEnabled() {
    	
    	return !disable_udp_port.isSelected();
    }
    
    public boolean isConnectAtStartup() {
    	
    	return connect_at_start_up.isSelected();
    }

}
