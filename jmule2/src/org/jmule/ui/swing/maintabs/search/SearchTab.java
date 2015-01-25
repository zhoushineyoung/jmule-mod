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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.searchmanager.SearchManager;
import org.jmule.core.searchmanager.SearchQuery;
import org.jmule.core.searchmanager.SearchResult;
import org.jmule.core.searchmanager.SearchResultListener;
import org.jmule.core.servermanager.ServerManager;
import org.jmule.ui.swing.common.CloseableTabbedPane;
import org.jmule.ui.swing.common.CloseableTabbedPaneListener;
import org.jmule.ui.swing.maintabs.AbstractTab;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2009/09/22 19:08:43 $$
 */
public class SearchTab extends AbstractTab implements SearchResultListener {


	
	private BorderLayout border_layout = new BorderLayout(); // the main layout for search tab
	private JPanel top_panel = new JPanel(); // where the search text field & go button are located
	final CloseableTabbedPane tabbed_panel = new CloseableTabbedPane(); // each search result is located on this tabbed panel
	private JTextField search_field;
	private JButton start_button;
	private JLabel search_label;
	private JMuleCore _core = JMuleCoreFactory.getSingleton();
	private SearchManager _search_manager = _core.getSearchManager();
	private ServerManager _server_manager = _core.getServerManager();
	private final List<Object[]> search_results_panels = new LinkedList<Object[]>();
	private JTabbedPane tabbedPanel;
	
	public SearchTab(final JFrame parent) {
		super(parent);
		this.parent = parent;
		//this.tabbedPanel = tabbedPanel;
		this.setLayout(border_layout);
		this.add(top_panel, BorderLayout.NORTH);
		this.add(tabbed_panel, BorderLayout.CENTER);
		//tabbed_panel.add(new JPanel(),"Linux OS");
		//tabbed_panel.add(new JPanel(),"Solaris");
		GridBagLayout thisLayout = new GridBagLayout();
		//this.setPreferredSize(new java.awt.Dimension(569, 76));
		thisLayout.rowWeights = new double[] {0.0, 0.1, 0.1};
		thisLayout.rowHeights = new int[] {23, 7, 20};
		thisLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		thisLayout.columnWidths = new int[] {72, 318, 108, 7};
		top_panel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
		top_panel.setLayout(thisLayout);
		search_field = new JTextField();
		top_panel.add(search_field, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		start_button = new JButton();
		top_panel.add(start_button, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 7, 0, 0), 0, 0));
        start_button.setText("Start");
	    search_label = new JLabel();
		top_panel.add(search_label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		search_label.setText("Search");	
		_search_manager.addSeachResultListener(this);
		
		tabbed_panel.addCloseableTabbedPaneListener(new CloseableTabbedPaneListener() {
			public boolean closeTab(int tabIndexToClose) {
				SearchResultPanel panel = (SearchResultPanel)tabbed_panel.getComponentAt(tabIndexToClose);
				for(Object[] obj : search_results_panels ) {
					if(obj[1] == panel) {
						search_results_panels.remove(obj);
						break;
					}
				}
				return true;
			}
		});
		
		start_button.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent event) {
			  if(!_server_manager.isConnected()) {
				  JOptionPane.showMessageDialog(null, "The system is not connected to server", "Warning", JOptionPane.WARNING_MESSAGE);
				  return;
			  }
			  SearchResultPanel search_panel = new SearchResultPanel(parent, tabbed_panel);	
			  String search_str = search_field.getText();
              _search_manager.search(search_str);
                                                    // string     JPanel       boolean - tells if the result has been shown
              search_results_panels.add(new Object[]{search_str, search_panel, false});
              tabbed_panel.add(search_panel, search_str);
		   }
		});
	}
	
	public void resultArrived(SearchResult searchResult) { 
		for(Object[] obj : search_results_panels ) {
		    if(searchResult.
		       getSearchQuery().getQuery().
		         equalsIgnoreCase((String)obj[0])) { 
                 if(Boolean.parseBoolean(obj[2].toString()) == false) {
                     ((SearchResultPanel)obj[1]).setSearchResult(searchResult); 
                     obj[2] = true;
                 }
		    }
		}
	}


	public void searchCompleted(SearchQuery query) {

		
	}


	public void searchStarted(SearchQuery query) {

		
	}

	public void searchFailed(SearchQuery query) {

		
	}
}
