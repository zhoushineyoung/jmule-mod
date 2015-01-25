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
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.jmule.core.servermanager.Server;
import org.jmule.ui.swing.dialogs.RefreshableDialog;
import org.jmule.ui.utils.NumberFormatter;

/**
 *
 * Created on Oct 4, 2008
 * @author javajox
 * @version $Revision: 1.2 $
 * Last changed by $Author: javajox $ on $Date: 2009/09/22 19:08:43 $
 */
public class ServerDetailsDialog extends RefreshableDialog {
	
	class BottomPanel extends JPanel {
		private JButton close_button;
		
		public BottomPanel() {
			
			init();
		}
		
		private void init() {
			GridBagLayout thisLayout = new GridBagLayout();
			thisLayout.rowWeights = new double[] {0.1};
			thisLayout.rowHeights = new int[] {7};
			thisLayout.columnWeights = new double[] {0.0, 0.0, 0.1};
			thisLayout.columnWidths = new int[] {230, 126, 20};
			this.setPreferredSize(new java.awt.Dimension(375, 44));
			this.setLayout(thisLayout);
			{
				close_button = new JButton("Close");
				this.add(close_button, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				close_button.setName("close_button");
			}
		}
		
		public JButton getCloseButton() {
			return close_button;
		}
	}
	
	private JLabel server_name_label = new JLabel("Name");
	private JLabel ip_label = new JLabel("IP");
	private JLabel files_value = new JLabel();
	private JLabel connected_to_value = new JLabel();
	private JLabel down_value = new JLabel();
	private JLabel static_value = new JLabel();
	private JLabel users_value = new JLabel();
	private JLabel ping_value = new JLabel();
	private JLabel ed2k_link_value = new JLabel();
	private JLabel ip_value = new JLabel();
	private JLabel server_name_value = new JLabel();
	private JTextArea description_text_area = new JTextArea();
	private JScrollPane description_scroll_pane = new JScrollPane();
	private JLabel description_label = new JLabel("Description");
	private JLabel connected_to_label = new JLabel("Connected to");
	private JLabel down_label = new JLabel("Down");
	private JLabel static_label = new JLabel("Static");
	private JLabel files_label = new JLabel("Files");
	private JLabel users_label = new JLabel("Users");
	private JLabel ping_label = new JLabel("Ping");
	private JLabel ed2k_link_label = new JLabel("ED2K Link");
	
	private JPanel center_panel = new JPanel();
	//private JPanel north_panel = new JPanel();
    private GridBagLayout grid_bag_layout = new GridBagLayout();
    private BorderLayout border_layout = new BorderLayout();
    
    private BottomPanel bottom_panel = new BottomPanel();
    private final static Font label_value_font = new java.awt.Font("Dialog", 0, 12);
    private final static Font lable_font = new java.awt.Font("Dialog", 1, 12);
    
    private Server server;
	
    private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    
	public ServerDetailsDialog(JFrame parent, Server server) {
		super(parent, "Server details", true);
	    this.server = server;	
		init();
	}
	
	private void init() {
        this.setLayout(border_layout);
		this.setSize(new java.awt.Dimension(381, 442));
		this.setResizable(false);
        server_name_label.setHorizontalAlignment(SwingConstants.RIGHT);
        ip_label.setHorizontalAlignment(SwingConstants.RIGHT);
        description_label.setHorizontalAlignment(SwingConstants.RIGHT);
        connected_to_label.setHorizontalAlignment(SwingConstants.RIGHT);
        down_label.setHorizontalAlignment(SwingConstants.RIGHT);
        static_label.setHorizontalAlignment(SwingConstants.RIGHT);
        files_label.setHorizontalAlignment(SwingConstants.RIGHT);
        users_label.setHorizontalAlignment(SwingConstants.RIGHT);
        ping_label.setHorizontalAlignment(SwingConstants.RIGHT);
        ed2k_link_label.setHorizontalAlignment(SwingConstants.RIGHT);
        
		grid_bag_layout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1};
		grid_bag_layout.rowHeights = new int[] {31, 29, 29, 29, 29, 31, 29, 29, 31, 90, 7};
		grid_bag_layout.columnWeights = new double[] {0.0, 0.0, 0.1};
		grid_bag_layout.columnWidths = new int[] {92, 10, 7};
		center_panel.setLayout(grid_bag_layout);
		
		server_name_value.setFont(label_value_font);
		ip_value.setFont(label_value_font);
		ed2k_link_value.setFont(label_value_font);
		ed2k_link_value.setForeground(Color.BLUE);
		ed2k_link_value.setToolTipText("Click to copy ED2K link");
		ping_value.setFont(label_value_font);
		users_value.setFont(label_value_font);
		files_value.setFont(label_value_font);
		static_value.setFont(label_value_font);
		down_value.setFont(label_value_font);
		connected_to_value.setFont(label_value_font);
		description_text_area.setFont(label_value_font);
		
		server_name_label.setFont(lable_font);
		ip_label.setFont(lable_font);
		ed2k_link_label.setFont(lable_font);
		ping_label.setFont(lable_font);
		users_label.setFont(lable_font);
		files_label.setFont(lable_font);
		static_label.setFont(lable_font);
		down_label.setFont(lable_font);
		connected_to_label.setFont(lable_font);
		description_label.setFont(lable_font);
		
		ed2k_link_value.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				clipboard.setContents(new Transferable() {
					public Object getTransferData(DataFlavor flavor)
							throws UnsupportedFlavorException,
							IOException {
						return server.getServerLink().getAsString();
					}

					public DataFlavor[] getTransferDataFlavors() {
						DataFlavor[] data_flavours = new DataFlavor[1];
						data_flavours[0] = DataFlavor.stringFlavor;
						return data_flavours;
					}

					public boolean isDataFlavorSupported(
							DataFlavor flavor) {
					    return flavor.isFlavorTextType();
					}
					
				}, new ClipboardOwner() {

					public void lostOwnership(Clipboard clipboard, Transferable contents) {
						
					}
					
				});
			}
		});
		
		center_panel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED));
		center_panel.add(server_name_label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(ip_label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(ed2k_link_label, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(ping_label, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(users_label, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(files_label, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(static_label, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(down_label, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(connected_to_label, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(description_label, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(description_scroll_pane, new GridBagConstraints(2, 9, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(4, 0, 0, 15), 0, 0));
		description_scroll_pane.setViewportView(description_text_area);
		description_text_area.setEditable(false);
		description_text_area.setBackground(this.getBackground());
		center_panel.add(server_name_value, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(ip_value, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(ed2k_link_value, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(ping_value, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(users_value, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(files_value, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(static_value, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(down_value, new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		center_panel.add(connected_to_value, new GridBagConstraints(2, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		update_server_info();
		this.add(center_panel,BorderLayout.CENTER);
		this.add(bottom_panel, BorderLayout.SOUTH);
		final JDialog _this = this;
		bottom_panel.getCloseButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			   _this.setVisible(false);    	
			}
		});
	}
	
	public void refresh() {
		
		update_server_info();
	}
	
	private void update_server_info() {
		server_name_value.setText(server.getName());
		server_name_value.setToolTipText(server.getName());
		ip_value.setText(server.getAddress() + " : " + server.getPort());
		ed2k_link_value.setText(server.getServerLink().getAsString());
		ping_value.setText(server.getPing() + "");
		String users = NumberFormatter.formatSizeHumanReadable(server.getNumUsers()) +
		               " (max " + NumberFormatter.formatSizeHumanReadable(server.getMaxUsers()) + ")";
		users_value.setText(users);
		String files = NumberFormatter.formatSizeHumanReadable(server.getNumFiles()) +
		               " limits [soft: " + NumberFormatter.formatSizeHumanReadable(server.getSoftLimit()) +
		               " hard: " + NumberFormatter.formatSizeHumanReadable(server.getHardLimit()) + "]";
		files_value.setText(files);
		static_value.setText(server.isStatic()?"Yes":"No");
		boolean is_down = server.isDown();
		if(is_down) down_value.setForeground(Color.RED);
		else down_value.setForeground(Color.BLACK);
		down_value.setText(is_down?"Yes":"No");
		connected_to_value.setText(server.isConnected()?"Yes":"No");
		description_text_area.setText(server.getDesc());
	}
	
	public DialogAction getDialogAction() {

		return null;
	}
}
