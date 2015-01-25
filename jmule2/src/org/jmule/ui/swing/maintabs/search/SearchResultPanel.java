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
package org.jmule.ui.swing.maintabs.search;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXBusyLabel;
import org.jmule.core.searchmanager.SearchResult;
import org.jmule.ui.swing.tables.SearchResultTable;

/**
 *
 * Created on Oct 14, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/16 17:35:11 $
 */
public class SearchResultPanel extends JPanel  {
	private JFrame parent;
	JXBusyLabel busy_label = new JXBusyLabel();
	private JTabbedPane tabbed_panel;
	public SearchResultPanel(JFrame parent, JTabbedPane tabbed_panel) {
		this.parent = parent; 
		this.setLayout(new GridLayout(1,1));
		this.tabbed_panel = tabbed_panel; 
		busy_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		//busy_label.setPreferredSize(new Dimension(250,250));
		//busy_label.setMinimumSize(new Dimension(250,250));
		//busy_label.setMaximumSize(new Dimension(250,250));
		//busy_label.setSize(250,250);
		this.add(busy_label);
		busy_label.setBusy(true);
	}
	public void setSearchResult(SearchResult searchResult) {
        JScrollPane scroll_pane = new JScrollPane();
        SearchResultTable search_result_table = new SearchResultTable(parent, searchResult, tabbed_panel, this);
        scroll_pane.setViewportView(search_result_table);
		busy_label.setBusy(false);
		this.remove(busy_label);
        this.add(scroll_pane);
        this.updateUI();
	}
}
