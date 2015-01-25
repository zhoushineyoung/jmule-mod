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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jmule.ui.CommonUIPreferences;
import org.jmule.ui.JMuleUIManager;

/**
 *
 * Created on Aug 26, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/09/09 11:42:29 $
 */
public class UIChooserWizad extends JDialog {
	
	private UIChooser ui_chooser;
	private JFrame parent;
	
	public class BottomPanel extends javax.swing.JPanel {

	    private javax.swing.JButton cancelButton;
	    private javax.swing.JButton okButton;
		
	    public BottomPanel() {
	        initComponents();
	    }

	    private void initComponents() {

	        cancelButton = new javax.swing.JButton();
	        okButton = new javax.swing.JButton();

	        setBorder(javax.swing.BorderFactory.createEtchedBorder());
	        
	        cancelButton.setText("Cancel");

	        okButton.setText("OK");

	        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
	        this.setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
	            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
	                .addContainerGap(196, Short.MAX_VALUE)
	                .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
	                .add(18, 18, 18)
	                .add(cancelButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
	                .addContainerGap())
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
	            .add(layout.createSequentialGroup()
	                .addContainerGap()
	                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
	                    .add(cancelButton)
	                    .add(okButton))
	                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );
	    }

		public javax.swing.JButton getCancelButton() {
			return cancelButton;
		}

		public javax.swing.JButton getOkButton() {
			return okButton;
		}

	}
	
	public UIChooserWizad(JFrame parent) {
		
		super(parent,true);
		
		this.setTitle("UI Chooser");
		
		this.setLayout(new BorderLayout());
		
		ui_chooser = new UIChooser();
		
		this.add(ui_chooser, BorderLayout.CENTER);
		
		final BottomPanel bottom_panel = new BottomPanel();
		
		this.add(bottom_panel, BorderLayout.SOUTH);
		
		final UIChooserWizad _this = this;
		
		bottom_panel.getOkButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if( JMuleUIManager.getCurrentUIType() != ui_chooser.getChosenUI() ) {
					CommonUIPreferences.getSingleton().setUIType( ui_chooser.getChosenUI() );
					CommonUIPreferences.getSingleton().save();
					JOptionPane.showMessageDialog(_this, "Please restart JMule to apply the changes");
				}
                _this.setVisible(false);
			}
		});
		
		bottom_panel.getCancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
                _this.setVisible(false);
			}
		});
		
	}
	
}
