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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationManager;


/**
 * 
 * @author javajox
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2009/09/22 19:08:43 $$
 */
public class NetworkBandwidthSelection extends WizardPanel {

    private ButtonGroup button_group;
    private JLabel true_download_bandwidth_label;
    private JLabel true_upload_bandwidth_label;
    private JPanel connection_type_panel;
    private JRadioButton kbit_s_radio;
    private JRadioButton kbyte_s_radio;
    private JScrollPane connection_type_jscroll_panel;
    private JTable connection_select_table;
    private JTextField true_download_bandwidth;
    private JTextField true_upload_bandwidth;
	
	DecimalFormat format = new DecimalFormat("0.00");
    
    JRadioButton prec_radio;
    ActionListener radion_action_listener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == prec_radio) return;
			prec_radio = (JRadioButton)e.getSource();
			float down_speed = Float.parseFloat(true_download_bandwidth.getText());
			float up_speed = Float.parseFloat(true_upload_bandwidth.getText()); 
			if(kbit_s_radio.isSelected()) {
				down_speed *= 1024;
				down_speed *= 8;
				down_speed /= 1024;
				
				up_speed *= 1024;
				up_speed *= 8;
				up_speed /= 1024;
			}
			else { 
				down_speed *= 1024;
				down_speed /= 8;
				down_speed /= 1024;
				
				up_speed *= 1024;
				up_speed /= 8;
				up_speed /= 1024;
			}
			true_download_bandwidth.setText( format.format(down_speed) );
			true_upload_bandwidth.setText( format.format(up_speed) );
		}
	};
    
    private JMuleCore _core = JMuleCoreFactory.getSingleton();
    private ConfigurationManager _config = _core.getConfigurationManager();
    
    public NetworkBandwidthSelection() {
        initComponents();
        disableCustomSpeedSettings( true );
    }

    private void initComponents() {
    	
    	button_group = new javax.swing.ButtonGroup();
        connection_type_panel = new javax.swing.JPanel();
        connection_type_jscroll_panel = new javax.swing.JScrollPane();
        connection_select_table = new javax.swing.JTable();
        true_download_bandwidth_label = new javax.swing.JLabel();
        true_upload_bandwidth_label = new javax.swing.JLabel();
        true_download_bandwidth = new javax.swing.JTextField();
        true_upload_bandwidth = new javax.swing.JTextField();
        kbit_s_radio = new javax.swing.JRadioButton();
        kbyte_s_radio = new javax.swing.JRadioButton();

        prec_radio = kbit_s_radio;
        
        true_download_bandwidth_label.setFont( skin.getLabelFont() );
        true_upload_bandwidth_label.setFont( skin.getLabelFont() );
        kbit_s_radio.setFont( skin.getDefaultFont() );
        kbyte_s_radio.setFont( skin.getDefaultFont() );
        
        connection_type_panel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Connection Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        this.setPreferredSize(new Dimension(485, 291));
        
        connection_select_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Unknown", null, null},
                {"Custom", "(enter below)", "(enter below)"},
                {"56-k Model", "56", "56"},
                {"ISDN", "64", "64"},
                {"ISDN 2x", "128", "128"},
                {"T DSL 1000 (T, Arcor,Freenet,1&1)", "1024", "128"},
                {"T DSL 1500 (T)", "1536", "192"},
                {"T DSL 2000 (T,Arcor,Freenet,Tiscali,Alice)", "2048", "192"},
                {"Versatle DSL 2000", "2048", "384"},
                {"T-DSL 3000 (T,Arcor)", "3072", "384"},
                {"T DSL 6000 (T,Arcor)", "6016", "576"},
                {"   DSL 6000 (Tiscali,Freenet,1&1)", "6016", "572"},
                {"   DSL 6000 (Lycos,Alice)", "6016", "512"},
                {"Versatel DSL 6000", "6144", "512"},
                {"Cable", "187", "32"},
                {"Cable", "187", "64"},
                {"T1", "1500", "1500"}
              //  {"T3+", "44 Mbps", "44 Mbps"}
            },
            new String [] {
                "Connection", "Down (KBit/s)", "Up (KBit/s)"
            }
        ));

        connection_select_table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        connection_select_table.setShowGrid( false );
        // connection_select_table.setCellSelectionEnabled( true );
        
        // when we click on the speed selection table we see the down/up values below
        final JTable cst = connection_select_table;
        connection_select_table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				// int col = cst.getSelectedColumn();
				int row = cst.getSelectedRow();
				String down_speed = (String)cst.getModel().getValueAt(row, 1);
				String up_speed = (String)cst.getModel().getValueAt(row, 2);
				switch(row) {
				   case 0  : disableCustomSpeedSettings( true );
					         //true_download_bandwidth.setText("0");
				             //true_upload_bandwidth.setText("0");
				             break;
				   case 1  : //true_download_bandwidth.setText("");
				             //true_upload_bandwidth.setText("");
					         disableCustomSpeedSettings( false ); 
				             break;
				   default : disableCustomSpeedSettings( true );
					         true_download_bandwidth.setText( down_speed );
				             true_upload_bandwidth.setText( up_speed );
				}
		   }
		 });
        
        connection_type_jscroll_panel.setViewportView(connection_select_table);
        true_download_bandwidth_label.setText("True Download Bandwidth");
        true_upload_bandwidth_label.setText("True Upload Bandwidth");
        kbit_s_radio.setText("Kbit/s");
        kbyte_s_radio.setText("Kbyte/s");
        button_group.add(kbit_s_radio);
        button_group.add(kbyte_s_radio);
        
        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(connection_type_panel);
        connection_type_panel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(connection_type_jscroll_panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(37, 37, 37)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(true_download_bandwidth_label)
                            .add(true_upload_bandwidth_label))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(true_upload_bandwidth)
                            .add(true_download_bandwidth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(kbyte_s_radio)
                            .add(kbit_s_radio))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(connection_type_jscroll_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 178, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(true_download_bandwidth_label)
                    .add(true_download_bandwidth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(kbit_s_radio))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(true_upload_bandwidth_label)
                    .add(true_upload_bandwidth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(kbyte_s_radio))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(connection_type_panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(connection_type_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );
        kbit_s_radio.addActionListener(radion_action_listener);
        kbyte_s_radio.addActionListener(radion_action_listener);
        initBandwidths();
    }
    
    private void disableCustomSpeedSettings(boolean state) {
    	true_download_bandwidth.setEnabled( state ? false : true );
    	true_upload_bandwidth.setEnabled( state ? false : true );
    	kbit_s_radio.setEnabled( state ? false : true );
    	kbyte_s_radio.setEnabled( state ? false : true );
        kbit_s_radio.setSelected( true );
    }
    
    private void initBandwidths() {
      try {
    	true_download_bandwidth.setText( (_config.getDownloadBandwidth() * 8 / 1024) + "");
    	true_upload_bandwidth.setText( (_config.getUploadBandwidth() * 8 / 1024) + "");
      }catch( Throwable cause ) {
    	  cause.printStackTrace();
      }
    }
    
    public long getDownloadBandwidth() {
    	
    	int down_speed = Integer.parseInt(true_download_bandwidth.getText());
    	
    	return Math.round(  kbit_s_radio.isSelected() ?  (down_speed * 1024) / 8  : (down_speed * 1024) );
    }
    
    public long getUploadBandwidth() {
    	
    	int up_speed = Integer.parseInt(true_upload_bandwidth.getText());
    	
    	return Math.round( kbit_s_radio.isSelected() ? (up_speed * 1024 ) / 8 : (up_speed * 1024) );
    }

}
