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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;

import org.jmule.ui.IDialog;

/**
 *
 * Created on Sep 18, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/16 17:35:11 $
 */
public class NewED2KDialog extends JDialog implements IDialog {

	protected JButton clearButton = new JButton("Clear");
	protected JButton okButton = new JButton("OK");
	protected JButton cancelButton = new JButton("Cancel");
	private JPanel bottom_panel = new JPanel(); // where the clear, ok and cancel buttons are located
	protected JPanel central_panel = new JPanel();
	private GridBagLayout grid_bag_layout = new GridBagLayout(); // for bottom_panel
	protected JScrollPane scroll_panel = new JScrollPane();
	protected JTable table = new JTable();
	protected JLabel learn_about_ed2k_links_label = new JLabel("Learn more about ED2K links");
	protected JButton paste_ed2k_links = new JButton("Paste ED2K link(s)");
	protected final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	public NewED2KDialog(JFrame parent) {
		super(parent);
        init();
	}
	
	private void init() {
		//this.setPreferredSize(new java.awt.Dimension(438, 45));
		central_panel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
		grid_bag_layout.rowWeights = new double[] {0.0};
		grid_bag_layout.rowHeights = new int[] {41};
		grid_bag_layout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.1};
		grid_bag_layout.columnWidths = new int[] {0, 87, 106, 133, 20};
		bottom_panel.setLayout(grid_bag_layout);
        bottom_panel.add(clearButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
		clearButton.setName("clear_button");
        bottom_panel.add(okButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
        bottom_panel.add(cancelButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
        scroll_panel.setViewportView(table);
        this.add(bottom_panel,BorderLayout.SOUTH);
        this.add(central_panel,BorderLayout.CENTER);
	}
	
	public DialogAction getDialogAction() {
		// TODO Auto-generated method stub
		return null;
	}

}
