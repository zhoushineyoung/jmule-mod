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
package org.jmule.ui.swing.mainwindow;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;


/**
 *
 * Created on Sep 18, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/16 17:35:12 $
 */
public class NewServersDialog extends NewED2KDialog {

	private JButton add_server_button = new JButton("Add server");
	GridBagLayout grid_bag_layout1 = new GridBagLayout();
	GridBagLayout grid_bag_layout2 = new GridBagLayout();
	
	public NewServersDialog(JFrame parent) {
		super(parent);
		init();
	}
	
	private void init() {
		// ----------------------------------------------------------
		grid_bag_layout1.rowWeights = new double[] {0.1, 0.1, 0.1};
		grid_bag_layout1.rowHeights = new int[] {7, 7, 7};
		grid_bag_layout1.columnWeights = new double[] {0.0, 0.1};
		grid_bag_layout1.columnWidths = new int[] {134, 7};
		// -----------------------------------------------------------
		grid_bag_layout2.rowWeights = new double[] {0.0, 0.0, 0.1};
		grid_bag_layout2.rowHeights = new int[] {156, 54, 7};
		grid_bag_layout2.columnWeights = new double[] {0.1, 0.1, 0.1, 0.1};
		grid_bag_layout2.columnWidths = new int[] {7, 7, 7, 7};
		// ----------------------------------------------------------
		central_panel.setLayout(grid_bag_layout1);
		central_panel.add(add_server_button, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		central_panel.add(paste_ed2k_links, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 20), 0, 0));
		central_panel.add(learn_about_ed2k_links_label, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		paste_ed2k_links.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent event) {
			   serversPanel();
		   }
		});
		clearButton.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent event) {
			   emptyPanel();
		   }
		 });
	}
	
	private void emptyPanel() {
		central_panel.remove(add_server_button);
		central_panel.remove(paste_ed2k_links);
		central_panel.remove(scroll_panel);
		central_panel.setLayout(grid_bag_layout1);
		central_panel.add(add_server_button, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		central_panel.add(paste_ed2k_links, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 20), 0, 0));
		central_panel.add(learn_about_ed2k_links_label, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		central_panel.updateUI();
		this.repaint();
	}
	
	private void serversPanel() {
		central_panel.remove(add_server_button);
		central_panel.remove(paste_ed2k_links);
		central_panel.remove(learn_about_ed2k_links_label);
		central_panel.setLayout(grid_bag_layout2);
		central_panel.add(add_server_button, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 20), 0, 0));
		central_panel.add(paste_ed2k_links, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		central_panel.add(scroll_panel, new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		central_panel.updateUI();
		this.repaint();
	}
	
	public static void main(String args[]) {
		JFrame frame = new JFrame("T e s t i n g   ! ! !");
		NewServersDialog new_server_dialog = new NewServersDialog(frame);
		frame.setSize(300,400);
		new_server_dialog.setSize(300, 400);
		frame.setVisible(true);
		new_server_dialog.setVisible(true);
	}
}
