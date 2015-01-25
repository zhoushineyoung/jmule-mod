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
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.edonkey.ED2KLink;
import org.jmule.core.edonkey.ED2KServerLink;
import org.jmule.ui.IDialog;

/**
 *
 * Created on Sep 17, 2008
 * @author javajox
 * @version $Revision: 1.2 $
 * Last changed by $Author: javajox $ on $Date: 2009/09/22 19:08:43 $
 */
public class NewED2KLinksPanel extends JDialog implements IDialog {

	private ED2KLinkType ed2k_link_type;
	private List<ED2KLink> ed2k_links;
	private JTable table;
	private JScrollPane scroll_pane;
	private JPanel central_panel;
	private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	public class OpPanel extends JPanel {
		private JButton pasteED2KLinksButton;
		private JButton cancelButton;
		private JButton okButton;
		private JButton clearButton;

		public OpPanel() {
			super();
			init();
		}
		
		private void init() {
			GridBagLayout thisLayout = new GridBagLayout();
			this.setPreferredSize(new java.awt.Dimension(438, 45));
			thisLayout.rowWeights = new double[] {0.0};
			thisLayout.rowHeights = new int[] {41};
			thisLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.1};
			thisLayout.columnWidths = new int[] {0, 87, 106, 133, 20};
			this.setLayout(thisLayout);
			{
				clearButton = new JButton();
				this.add(clearButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
				clearButton.setName("clear_button");
			}
			{
				okButton = new JButton();
				this.add(okButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
				okButton.setName("ok_button");
			}
			{
				cancelButton = new JButton();
				this.add(cancelButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
				cancelButton.setName("cancel_button");
			}
		}
		
		public JButton getPasteED2KLinksButton() {
			return pasteED2KLinksButton;
		}

		public JButton getCancelButton() {
			return cancelButton;
		}

		public JButton getOkButton() {
			return okButton;
		}

		public JButton getClearButton() {
			return clearButton;
		}
	}
	
	public enum ED2KLinkType {
		FILE,
		SERVER
	}
	
	public NewED2KLinksPanel(JFrame parent, ED2KLinkType linkType) {
		super(parent);
		this.ed2k_link_type = linkType;
		init();
	}
	
	private void init() {
		BorderLayout border_layout = new BorderLayout();
		JButton paste_ed2k_links_button = new JButton("Paste ed2k link(s)");
		// for the central panel
		GridBagLayout grid_bag_layout = new GridBagLayout();
		grid_bag_layout.rowWeights = new double[] {0.1, 0.1, 0.1};
		grid_bag_layout.rowHeights = new int[] {7, 7, 7};
		grid_bag_layout.columnWeights = new double[] {0.1};
		grid_bag_layout.columnWidths = new int[] {7};
		central_panel = new JPanel();
		central_panel.setLayout(grid_bag_layout);
		central_panel.add(paste_ed2k_links_button, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 20), 0, 0));
		this.setLayout(border_layout);
		OpPanel op_panel = new OpPanel();
		this.add(op_panel, BorderLayout.SOUTH);
		this.add(central_panel,BorderLayout.CENTER);
		op_panel.getPasteED2KLinksButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				  try { 	
				    Object clipboard_contents = clipboard.getData(DataFlavor.stringFlavor);
					switch(ed2k_link_type) {
					    case SERVER : List<ED2KServerLink> server_links = ED2KServerLink.extractLinks(clipboard_contents.toString());
					                  ed2k_links = new LinkedList<ED2KLink>();
					                  for(ED2KServerLink server_link : server_links) {
					                	  ed2k_links.add(server_link);
					                  }
					    	          //ed2k_links = ED2KServerLink.extractLinks(clipboard_contents.toString());
					                  break;
					    case FILE : List<ED2KFileLink> file_links = ED2KFileLink.extractLinks(clipboard_contents.toString());
					                ed2k_links = new LinkedList<ED2KLink>();
					                for(ED2KFileLink file_link : file_links) {
					                	ed2k_links.add(file_link);
					                }
					                break;
					}
					
				  }catch(Throwable t) {
					 t.printStackTrace();
				  }
			}
		});
		this.setSize(300, 300);
	}
	
	public ED2KLinkType getED2KLinkType() {
		
		return ed2k_link_type;
	}
	
	public DialogAction getDialogAction() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String args[]) {
		
		JFrame frame = new JFrame("Super- mega- test system");
		NewED2KLinksPanel links_panel = new NewED2KLinksPanel(frame, NewED2KLinksPanel.ED2KLinkType.FILE); 
		frame.setSize(300, 400);
		frame.setVisible(true);
		links_panel.setVisible(true);
		
	}

}
