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
package org.jmule.ui.swing.maintabs.serverlist;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.jmule.core.utils.AddressUtils;
import org.jmule.ui.maintabs.serverlist.INewServerDialog;
import org.jmule.ui.swing.common.IPTextField;
import org.jmule.ui.swing.common.PortTextField;

/**
 *
 * Created on Sep 14, 2008
 * @author javajox
 * @version $Revision: 1.3 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/09 10:21:22 $
 */
public class SNewServerDialog extends JDialog implements INewServerDialog<String, Integer> {

	private JPanel south_panel;
	private JLabel ip_label;
	private JLabel port_label;
	private PortTextField port_text_field;
	private IPTextField ip_text_field;
	private JPanel center_panel;
	private JButton cancel_button;
	private JButton ok_button;
	private DialogAction dialog_action;
	
	public SNewServerDialog(JFrame frame) {
		super(frame);
		this.setTitle("New server");
		this.setModal(true);
		init();
	}
	
	private void init() {
        BorderLayout thisLayout = new BorderLayout();
	    getContentPane().setLayout(thisLayout);
		south_panel = new JPanel();
		GridBagLayout south_panelLayout = new GridBagLayout();
		south_panelLayout.columnWidths = new int[] {156, 20, 20};
		south_panelLayout.rowHeights = new int[] {7};
		south_panelLayout.columnWeights = new double[] {0.0, 0.1, 0.1};
		south_panelLayout.rowWeights = new double[] {0.1};
		getContentPane().add(south_panel, BorderLayout.SOUTH);
		south_panel.setLayout(south_panelLayout);
		south_panel.setPreferredSize(new java.awt.Dimension(308, 52));
		south_panel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));		
		ok_button = new JButton();
		south_panel.add(ok_button, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));
		ok_button.setText("OK");		
		cancel_button = new JButton();
		south_panel.add(cancel_button, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));
		cancel_button.setText("Cancel");
		center_panel = new JPanel();
		GridBagLayout center_panelLayout = new GridBagLayout();
		center_panelLayout.columnWidths = new int[] {73, 18, 158, 7};
		center_panelLayout.rowHeights = new int[] {45, 48, 7};
		center_panelLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		center_panelLayout.rowWeights = new double[] {0.0, 0.0, 0.1};
		getContentPane().add(center_panel, BorderLayout.CENTER);
		center_panel.setLayout(center_panelLayout);
		center_panel.setPreferredSize(new java.awt.Dimension(308, 115));		
		ip_label = new JLabel();
		center_panel.add(ip_label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		ip_label.setText("IP");		
		port_label = new JLabel();
		center_panel.add(port_label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		port_label.setText("Port");
		ip_text_field = new IPTextField();
		center_panel.add(ip_text_field, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));	
		port_text_field = new PortTextField();
		port_text_field.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		center_panel.add(port_text_field, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		final SNewServerDialog _this = this;
		ok_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String server_port = port_text_field.getText();
				if( !AddressUtils.isPortInRange(server_port) ) {
					JOptionPane.showMessageDialog(null, "The number " + server_port + " is not a valid server port" +
							                             "\n(the server port is a number between 1 and 65535)", 
							                             "Wrong server port", JOptionPane.ERROR_MESSAGE); 
				} else if( !AddressUtils.isValidPort(server_port) ) {
					JOptionPane.showMessageDialog(null, "The port " + server_port + " is reserved for other services", 
                                                        "Wrong server port", JOptionPane.ERROR_MESSAGE); 
				} else {
					dialog_action = DialogAction.OK;
					_this.setVisible(false);
				}
			}
		});
		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			       dialog_action = DialogAction.CANCEL;	
			       _this.setVisible(false);
			}
		});
	    this.setSize(318, 188);
	}
	
	public String getServerIP() {
		
		return ip_text_field.getIPString();
	}

	public Integer getServerPort() {
		
		return new Integer(port_text_field.getText());
	}

	public void setServerIP(String serverIP) {
				
		ip_text_field.setIPString(serverIP);
	}

	public void setServerPort(Integer serverPort) {
		
		port_text_field.setText(serverPort.toString());
	}
	
	public DialogAction getDialogAction() {

		return dialog_action;
	}

}
