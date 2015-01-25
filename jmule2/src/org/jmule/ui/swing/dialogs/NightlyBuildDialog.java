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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.ui.swing.SwingPreferences;
import org.jmule.ui.swing.common.NightlyBuildWarning;


/**
 *
 * Created on Sep 25, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/16 17:35:11 $
 */
public class NightlyBuildDialog extends JDialog {

	JPanel bottom_panel = new JPanel();
	JCheckBox show_option_checkbox = new JCheckBox("Don't show this window again");
	JButton ok_button = new JButton("OK");
	
	//JMuleCore _core = JMuleCoreFactory.getSingleton();
	SwingPreferences _pref = SwingPreferences.getSingleton();
	
	public NightlyBuildDialog(JFrame parent) {
		
		super(parent,true);
		
		this.setTitle("Warning !!!");
		this.setLayout(new BorderLayout());
		this.add(new NightlyBuildWarning(), BorderLayout.CENTER);

		show_option_checkbox.setSelected(!_pref.isNightlyBuildWarning());

		GridBagLayout grid_bag_layout = new GridBagLayout();
		bottom_panel.setPreferredSize(new java.awt.Dimension(399, 57));
		grid_bag_layout.rowWeights = new double[] {0.1};
		grid_bag_layout.rowHeights = new int[] {7};
		grid_bag_layout.columnWeights = new double[] {0.0, 0.0, 0.1, 0.0};
		grid_bag_layout.columnWidths = new int[] {231, 44, 7, 7};
		bottom_panel.setLayout(grid_bag_layout);
		bottom_panel.add(show_option_checkbox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		bottom_panel.add(ok_button, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.add(bottom_panel, BorderLayout.SOUTH);
		this.setSize(450,340);
		ok_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if( show_option_checkbox.isSelected())
					_pref.setNightlyBuildWarning(false);
				setVisible(false);
			}
		});
	}
}
