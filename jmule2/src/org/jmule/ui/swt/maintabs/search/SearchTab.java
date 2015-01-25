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
package org.jmule.ui.swt.maintabs.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMuleCore;
import org.jmule.core.searchmanager.SearchManager;
import org.jmule.core.searchmanager.SearchQuery;
import org.jmule.core.searchmanager.SearchQueryType;
import org.jmule.core.searchmanager.SearchResult;
import org.jmule.core.searchmanager.SearchResultListener;
import org.jmule.core.sharingmanager.FileType;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.maintabs.AbstractTab;
import org.jmule.ui.swt.mainwindow.MainWindow;
import org.jmule.ui.utils.FileFormatter;
/**
 * Created on Jul 31, 2008
 * @author binary256
 * @version $$Revision: 1.13 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/07/15 13:26:07 $$
 */
public class SearchTab extends AbstractTab{

	private CTabFolder search_query_tab_list;
	
	private Text search_query;
	
	private JMuleCore _core;
	
	private List<SearchResultTab> search_tabs = new ArrayList<SearchResultTab>();
	
	private Label advanced_info_search_label;
	private Label clear_advanced_options;
	
	private long minFileSize = 0,maxFileSize = 1024, availableSources, completedSources;
	private String extension="";
	private FileType fileType;
	private Combo searchType;
	private boolean show_advanced_options = false;
	
	SearchResultListener listener;
	static SearchTab search_tab;
	
	public SearchTab(Composite parent, JMuleCore core) {
		super(parent);
		this.search_tab = this;
		GridLayout layout;
		_core = core;
		listener = new SearchResultListener() {
			public void resultArrived(final SearchResult searchResult) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						SearchResultTab tab = getSearchResultTab(searchResult.getSearchQuery());
						
						if (tab != null) {
							tab.addSearchResult(searchResult);
							MainWindow.getLogger().fine(Localizer._("mainwindow.logtab.message_search_result_arrived",
									searchResult.getSearchQuery().getQuery(),searchResult.getSearchResultItemList().size()+""));
						}
					}
				}); 
			}

			public void searchCompleted(final SearchQuery query) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						SearchResultTab tab = getSearchResultTab(query);
						if (tab==null) return; // tab closed
						tab.completeSearch();
					}
				}); 
			}

			public void searchStarted(final SearchQuery query) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						SearchResultTab tab = getSearchResultTab(query);
						if (tab==null) return; // tab closed
						tab.searchStarted();
					}
				}); 
			}

			public void searchFailed(SearchQuery query) {
				
			}

		};
		_core.getSearchManager().addSeachResultListener(listener);
		setLayout(new GridLayout(1,true));
		
		Composite search_bar_composite = new Composite(this,SWT.NONE);
		search_bar_composite.setLayout(new FillLayout());
		search_bar_composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group search_bar = new Group(search_bar_composite,SWT.NONE);
		search_bar.setLayout(new GridLayout(1,true));
		
		Composite basic_search_controls = new Composite(search_bar,SWT.NONE);
		GridData layout_data = new GridData();
		layout_data.grabExcessHorizontalSpace = true;
		layout_data.horizontalAlignment = GridData.CENTER;
		basic_search_controls.setLayoutData(layout_data);
		
		layout = new GridLayout(6,false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		basic_search_controls.setLayout(layout);
		
		Label label = new Label(basic_search_controls,SWT.NONE);
		label.setText(_._("mainwindow.searchtab.label.search") + " : ");
		
		search_query = new Text(basic_search_controls,SWT.SINGLE | SWT.BORDER);
		layout_data = new GridData();
		layout_data.widthHint = 300;
		search_query.setLayoutData(layout_data);
		
		Button search_button = new Button(basic_search_controls,SWT.PUSH);
		
		search_button.setText(Localizer._("mainwindow.searchtab.button.search"));
		search_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				search();
			}
		});
		
		searchType = new Combo (basic_search_controls, SWT.READ_ONLY);
		searchType.add(_._("mainwindow.searchtab.search_type.server"));
		searchType.add(_._("mainwindow.searchtab.search_type.kad"));
		searchType.add(_._("mainwindow.searchtab.search_type.kad_server"));
		searchType.add(_._("mainwindow.searchtab.search_type.global"));
		searchType.select(0);
		
		Label adv_search = new Label(basic_search_controls,SWT.NONE);
		adv_search.setText(_._("mainwindow.searchtab.label.advanced"));

		Utils.formatAsLink(adv_search, new MouseAdapter() {
			public void mouseUp(MouseEvent arg0) {
				if (show_advanced_options) {
					new AdvancedSearchWindow(minFileSize,maxFileSize,fileType,extension,availableSources,completedSources){
						public void save(long minSize, long maxSize,
								FileType fileType, String extension,
								long availableSources, long completedSources) {	
								maxFileSize = maxSize;
								minFileSize = minSize;
								
								SearchTab.this.availableSources = availableSources;
								SearchTab.this.completedSources = completedSources;
								SearchTab.this.fileType = fileType;
								SearchTab.this.extension = extension;
								
								showAdvancedOptions();
						}
					};
				}else {
					new AdvancedSearchWindow(){
						public void save(long minSize, long maxSize,
								FileType fileType, String extension,
								long availableSources, long completedSources) {	
								maxFileSize = maxSize;
								minFileSize = minSize;
								
								SearchTab.this.availableSources = availableSources;
								SearchTab.this.completedSources = completedSources;
								SearchTab.this.fileType = fileType;
								SearchTab.this.extension = extension;
								
								showAdvancedOptions();
						}
					};
					
				}
			}
		});
				
		search_query.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent arg0) {
				if (arg0.keyCode == SWT.CR) {
					search();
				}
			}
		} );
		
		clear_advanced_options = new Label(basic_search_controls,SWT.NONE);
		clear_advanced_options.setText(_._("mainwindow.searchtab.label.clear"));
		clear_advanced_options.setVisible(false);
		
		Utils.formatAsLink(clear_advanced_options, new MouseAdapter() {
			public void mouseUp(MouseEvent arg0) {
				hideAdvancedOptions();
			}				
		});
		
		Composite advanced_options = new Composite(search_bar,SWT.NONE);
		layout_data = new GridData();
		layout_data.grabExcessHorizontalSpace = true;
		layout_data.horizontalAlignment = GridData.FILL;
		advanced_options.setLayoutData(layout_data);
		advanced_options.setLayout(new GridLayout(1,false));
		
		advanced_info_search_label = new Label(advanced_options,SWT.NONE);
		layout_data = new GridData();
		layout_data.horizontalAlignment = GridData.CENTER;
		layout_data.grabExcessHorizontalSpace = true;
		advanced_info_search_label.setLayoutData(layout_data);
		advanced_info_search_label.setVisible(false);
		
		search_query_tab_list = new CTabFolder(this, SWT.BORDER);
		search_query_tab_list.setLayoutData(new GridData(GridData.FILL_BOTH));
		search_query_tab_list.setLayout(new FillLayout());
		search_query_tab_list.setSimple(false);
		search_query_tab_list.setUnselectedImageVisible(true);
		search_query_tab_list.setUnselectedCloseVisible(false);
		
	}
	
	private SearchResultTab getSearchResultTab(SearchQuery searchQuery){
		for(SearchResultTab tab : search_tabs) {
			if (tab.getSerchQuery().equals(searchQuery))
				if (!tab.isSearchCompleted())
					if (tab.getSearchTab().isDisposed()) return null;
					else
					return tab;
		}
		return null;
	}
	
	public JMULE_TABS getTabType() {
		
		return JMULE_TABS.SEARCH;
	}

	public void lostFocus() {
		
	}

	public void obtainFocus() {
		
	}

	public void disposeTab() {
		for(SearchResultTab tab : search_tabs) {
			tab.getSearchTab().dispose();
		}
	}


	private void search() {
		if ((searchType.getSelectionIndex()==0)&&(!_core.getServerManager().isConnected())) {
			Utils.showWarningMessage(getShell(), Localizer._("mainwindow.searchtab.not_connected_to_server_title"), Localizer._("mainwindow.searchtab.not_connected_to_server"));
			return;
		}
		if ((searchType.getSelectionIndex()==1)&&(!_core.getJKadManager().isConnected())) {
			Utils.showWarningMessage(getShell(), _._("mainwindow.searchtab.not_connected_to_kad_title"),_._("mainwindow.searchtab.not_connected_to_kad"));
			return;
		}		
		if ((searchType.getSelectionIndex()==2)&&(!_core.getJKadManager().isConnected())) {
			Utils.showWarningMessage(getShell(), _._("mainwindow.searchtab.not_connected_to_kad_title"),_._("mainwindow.searchtab.not_connected_to_kad"));
			return;
		}	
		
		if ((searchType.getSelectionIndex()==2)&&(!_core.getServerManager().isConnected())) {
			Utils.showWarningMessage(getShell(), Localizer._("mainwindow.searchtab.not_connected_to_server_title"), Localizer._("mainwindow.searchtab.not_connected_to_server"));
			return;
		}
		
		String query = search_query.getText();
		search_query.setText("");
		if (query.length()==0) return ;
		SearchQuery search_query = new SearchQuery(query+"");
		
		if ((searchType.getSelectionIndex()==0))
			search_query.setQueryType(SearchQueryType.SERVER);
		
		if ((searchType.getSelectionIndex()==1))
			search_query.setQueryType(SearchQueryType.KAD);
		
		if ((searchType.getSelectionIndex()==2))
			search_query.setQueryType(SearchQueryType.SERVER_KAD);
		
		if ((searchType.getSelectionIndex()==3))
			search_query.setQueryType(SearchQueryType.GLOBAL);
		
		if (show_advanced_options) {
			if (fileType != FileType.ANY)
				search_query.setFileType(fileType);
			if (extension.length()!=0)
				search_query.setExtension(extension);
			if (availableSources!=0)
				search_query.setMinAvailability(availableSources);
			if (completedSources!=0)
				search_query.setMinCompleteSources(completedSources);
			if ((minFileSize!=0)||(maxFileSize!=0)) {
				search_query.setMinimalSize(minFileSize);
				search_query.setMaximalSize(maxFileSize);
			}
				
		}
		
		SearchManager manager = _core.getSearchManager();
		manager.search(search_query);
		
		final SearchResultTab tab = new SearchResultTab(search_query_tab_list,search_query,_core);
		
		search_query_tab_list.setSelection(tab.getSearchTab());
		
		search_tabs.add(tab);
		
	}
	
	void tabClosed(SearchResultTab tab) {
		search_tabs.remove(tab);
		SearchManager manager = _core.getSearchManager();
		manager.removeSearch(tab.getSerchQuery());
	}
	
	private void showAdvancedOptions() {
		
		String text = "";
		if ((minFileSize != 0)||(maxFileSize!=0))
			text = " "+_._("mainwindow.searchtab.label.min_file_size")+" : "+FileFormatter.formatFileSize(minFileSize)+" "
			+_._("mainwindow.searchtab.label.max_file_size")+" : "+ FileFormatter.formatFileSize(maxFileSize)+" ";
		if (extension.length()!=0)
			text += _._("mainwindow.searchtab.label.file_extension") + " : "+extension+" ";
		
		if (availableSources!=0)
			text +=_._("mainwindow.searchtab.label.file_availability") + " : " + availableSources+" ";
		
		if (completedSources!=0)
			text+=_._("mainwindow.searchtab.label.file_sources") + " : "+completedSources + " ";
		if (fileType != FileType.ANY)
			text += _._("mainwindow.searchtab.label.file_type") + " : " + fileType + " ";
		if (text.length()!=0) 
			show_advanced_options = true;
		else 
			show_advanced_options = false;
		if (show_advanced_options) {
			advanced_info_search_label.setText(text);
			advanced_info_search_label.setVisible(true);
			clear_advanced_options.setVisible(true);
			advanced_info_search_label.getParent().layout();
			clear_advanced_options.getParent().layout();
		}
	}
	
	private void hideAdvancedOptions() {
		show_advanced_options = false;
		advanced_info_search_label.setVisible(false);
		clear_advanced_options.setVisible(false);
		
		advanced_info_search_label.getParent().layout();
		clear_advanced_options.getParent().layout();
	}
	

}
