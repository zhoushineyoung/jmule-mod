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
package org.jmule.ui.swing.maintabs.transfers;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.ui.swing.common.OpPanel;
import org.jmule.ui.swing.dialogs.RefreshableDialog;
import org.jmule.ui.swing.tables.DownloadPeersTable;

/**
 *
 * Created on Oct 5, 2008
 * @author javajox
 * @version $Revision: 1.2 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/18 12:30:19 $
 */
public class DownloadDetailsDialog extends RefreshableDialog {

	private JTabbedPane tabbed_panel = new JTabbedPane();
	private OpPanel op_panel = new OpPanel(new OpPanel.button_type[] 
	                                            { OpPanel.button_type.CLOSE } );
	private BorderLayout border_layout = new BorderLayout();
	private DownloadDetailsPanel download_details_panel; 
	private DownloadPeersTable download_peers_table;
	private JScrollPane peers_scroll_panel = new JScrollPane();
	
	private DownloadSession session;
	
	public DownloadDetailsDialog(JFrame parent, DownloadSession session) {
		super(parent, "Download details", true);
		this.session = session;
		download_details_panel = new DownloadDetailsPanel(session);
		download_peers_table = new DownloadPeersTable(parent, session);
		peers_scroll_panel.setViewportView(download_peers_table);
		this.setSize(567, 521);
		init();
	}
	
	private void init() {
		this.setLayout(border_layout);
		this.add(tabbed_panel, BorderLayout.CENTER);
		this.add(op_panel, BorderLayout.SOUTH);
		op_panel.getCloseButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			    setVisible(false);  	
			} 
		});
		tabbed_panel.addTab("General", download_details_panel);
		tabbed_panel.addTab("Peers", peers_scroll_panel);
	}
	
    public void refresh() {
    	if( tabbed_panel.getSelectedComponent() == download_details_panel )
    	  download_details_panel.updateData();
    	else 
    	  download_peers_table.repaint();
    }

}
