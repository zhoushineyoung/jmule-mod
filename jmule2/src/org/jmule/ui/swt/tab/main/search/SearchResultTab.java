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
package org.jmule.ui.swt.tab.main.search;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jmule.core.JMuleCore;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.downloadmanager.DownloadManagerException;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.searchmanager.SearchQuery;
import org.jmule.core.searchmanager.SearchResult;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.core.sharingmanager.FileQuality;
import org.jmule.core.sharingmanager.SharingManager;
import org.jmule.core.utils.Misc;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTConstants;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.tables.JMTable;
import org.jmule.ui.utils.FileFormatter;

/**
 * Created on Aug 15, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2011/03/27 16:51:29 $
 */
public class SearchResultTab {
	private boolean searchCompleted = false;
	private SWTPreferences swt_preferences = null;
	private CTabItem search_tab;
	private SearchQuery query;
	private JMTable<SearchResultItem> search_results;

	private Set<FileHash> result_hashes = new HashSet<FileHash>(); 
	private Menu no_items_menu, pop_up_menu;
	private MenuItem download_item;
	private MenuItem properties_item;
	private JMuleCore _core;

	private Color color_red = SWTThread.getDisplay().getSystemColor(SWT.COLOR_RED);
	private Color color_green = SWTThread.getDisplay().getSystemColor(SWT.COLOR_GREEN);

	public SearchResultTab(CTabFolder parent, SearchQuery searchRequest, JMuleCore core) {
		swt_preferences = SWTPreferences.getInstance();
		search_tab = new CTabItem(parent, SWT.CLOSE);
		_core = core;
		query = searchRequest;
		search_tab.setText(getTabTitle(query.getQuery()));
		search_tab.setToolTipText(query.getQuery());
		search_tab.setImage(SWTImageRepository.getImage("search_queue.png"));

		Composite content = new Composite(parent, SWT.NONE);

		search_tab.setControl(content);
		content.setLayout(new FillLayout());

		search_results = new JMTable<SearchResultItem>(content, SWT.NONE) {

			protected int compareObjects(SearchResultItem object1,
					SearchResultItem object2, int columnID, boolean order) {

				if (columnID == SWTPreferences.SEARCH_FILENAME_COLUMN_ID) {
					return Misc.compareAllObjects(object1, object2,
							"getFileName", order);
				}

				if (columnID == SWTPreferences.SEARCH_FILESIZE_COLUMN_ID)
					return Misc.compareAllObjects(object1, object2,
							"getFileSize", order);

				if (columnID == SWTPreferences.SEARCH_FILEQUALITY_COLUMN_ID) {
					FileQuality q1 = object1.getFileQuality();
					FileQuality q2 = object2.getFileQuality();
					int result = 0;
					if (q1.getAsInt() > q2.getAsInt())
						result = 1;
					else if (q1.getAsInt() < q2.getAsInt())
						result = -1;
					if (order)
						return result;
					return Misc.reverse(result);
				}

				if (columnID == SWTPreferences.SEARCH_AVAILABILITY_COLUMN_ID)
					return Misc.compareAllObjects(object1, object2,
							"getFileAviability", order);

				if (columnID == SWTPreferences.SEARCH_COMPLETESRC_COLUMN_ID)
					return Misc.compareAllObjects(object1, object2,
							"getFileCompleteSrc", order);

				if (columnID == SWTPreferences.SEARCH_FILE_TYPE_COLUMN_ID) {
					String type1 = new String(object1.getMimeType());
					String type2 = new String(object2.getMimeType());

					int result = type1.compareTo(type2);

					if (order)
						return result;

					return Misc.reverse(result);
				}

				if (columnID == SWTPreferences.SEARCH_FILE_ID_COLUMN_ID) {
					String id1 = object1.getFileHash().getAsString();
					String id2 = object2.getFileHash().getAsString();
					int result = id1.compareTo(id2);
					if (order)
						return result;
					return Misc.reverse(result);
				}

				return 0;
			}

			final static int NO_FILE = 0x01;
			final static int DOWNLOAD_FILE = 0x02;
			final static int SHARED_FILE = 0x03;

			protected Menu getPopUpMenu() {
				if (getItemCount() == 0)
					return no_items_menu;
				int download_status = NO_FILE;

				SharingManager sharing_manager = _core.getSharingManager();
				DownloadManager download_manager = _core.getDownloadManager();

				for (SearchResultItem item : getSelectedObjects()) {
					if (download_manager.hasDownload(item.getFileHash())) {
						try {
							if (!download_manager.getDownload(
									item.getFileHash()).isStarted()) {
								download_status = DOWNLOAD_FILE;
							} else {
								download_status = SHARED_FILE;
							}
						} catch (DownloadManagerException e) {
							e.printStackTrace();
						}
						break;
					}
					if (sharing_manager.hasFile(item.getFileHash())) {
						download_status = SHARED_FILE;
					}
				}
				switch (download_status) {
				case NO_FILE: {
					download_item.setEnabled(true);
					break;
				}

				case SHARED_FILE: {
					download_item.setEnabled(false);
					break;
				}

				case DOWNLOAD_FILE: {
					download_item.setEnabled(true);
					break;
				}
				}
				if (getSelectionCount() > 1)
					properties_item.setEnabled(false);
				else
					properties_item.setEnabled(true);

				return pop_up_menu;
			}

			public void updateRow(SearchResultItem object) {

				FileHash fileHash = object.getFileHash();
				if (_core.getDownloadManager().hasDownload(fileHash)) {
					setForegroundColor(object, color_red);
				}
				
				else if (_core.getSharingManager().hasFile(fileHash))
				  setForegroundColor(object, color_green);
				 

				setRowText(object, SWTConstants.SEARCH_FILENAME_COLUMN_ID,
						object.getFileName());
				Image file_icon = SWTImageRepository.getIconByExtension(object
						.getFileName());
				setRowImage(object, SWTPreferences.SEARCH_FILENAME_COLUMN_ID,
						file_icon);

				Image file_quality_img = SWTImageRepository.getImage(object
						.getFileQuality());
				setRowImage(object,
						SWTPreferences.SEARCH_FILEQUALITY_COLUMN_ID,
						file_quality_img);

				setRowText(object, SWTPreferences.SEARCH_FILESIZE_COLUMN_ID,
						FileFormatter.formatFileSize(object.getFileSize()));
				setRowText(object,
						SWTPreferences.SEARCH_AVAILABILITY_COLUMN_ID, object
								.getFileAviability()
								+ "");
				setRowText(object, SWTPreferences.SEARCH_COMPLETESRC_COLUMN_ID,
						object.getFileCompleteSrc() + "");
				byte[] fileType = object.getMimeType();

				setRowText(object, SWTPreferences.SEARCH_FILE_TYPE_COLUMN_ID,
						FileFormatter.formatMimeType(fileType));
				setRowText(object, SWTPreferences.SEARCH_FILE_ID_COLUMN_ID,
						object.getFileHash().getAsString());

			}

		};
		int width;

		width = swt_preferences
				.getColumnWidth(SWTConstants.SEARCH_FILENAME_COLUMN_ID);
		search_results.addColumn(SWT.LEFT,
				SWTConstants.SEARCH_FILENAME_COLUMN_ID, _
						._("mainwindow.searchtab.column.filename"), "", width);

		width = swt_preferences
				.getColumnWidth(SWTConstants.SEARCH_FILESIZE_COLUMN_ID);
		search_results.addColumn(SWT.RIGHT,
				SWTConstants.SEARCH_FILESIZE_COLUMN_ID, _
						._("mainwindow.searchtab.column.filesize"), "", width);

		width = swt_preferences
				.getColumnWidth(SWTConstants.SEARCH_FILEQUALITY_COLUMN_ID);
		search_results.addColumn(SWT.CENTER,
				SWTConstants.SEARCH_FILEQUALITY_COLUMN_ID, "", _
						._("mainwindow.searchtab.column.desc.filequality"),
				width);

		width = swt_preferences
				.getColumnWidth(SWTConstants.SEARCH_AVAILABILITY_COLUMN_ID);
		search_results.addColumn(SWT.RIGHT,
				SWTConstants.SEARCH_AVAILABILITY_COLUMN_ID, _
						._("mainwindow.searchtab.column.availability"), "",
				width);

		width = swt_preferences
				.getColumnWidth(SWTConstants.SEARCH_COMPLETESRC_COLUMN_ID);
		search_results.addColumn(SWT.RIGHT,
				SWTConstants.SEARCH_COMPLETESRC_COLUMN_ID, _
						._("mainwindow.searchtab.column.completesrcs"), "",
				width);

		width = swt_preferences
				.getColumnWidth(SWTConstants.SEARCH_FILE_TYPE_COLUMN_ID);
		search_results.addColumn(SWT.LEFT,
				SWTConstants.SEARCH_FILE_TYPE_COLUMN_ID, _
						._("mainwindow.searchtab.column.filetype"), "", width);

		width = swt_preferences
				.getColumnWidth(SWTConstants.SEARCH_FILE_ID_COLUMN_ID);
		search_results.addColumn(SWT.LEFT,
				SWTConstants.SEARCH_FILE_ID_COLUMN_ID, _
						._("mainwindow.searchtab.column.fileid"), "", width);

		search_results.updateColumnSettings();

		no_items_menu = new Menu(search_results);

		MenuItem no_items_close_tab = new MenuItem(no_items_menu, SWT.PUSH);
		no_items_close_tab.setText(_._("mainwindow.searchtab.popupmenu.close"));
		no_items_close_tab.setImage(SWTImageRepository.getImage("cancel.png"));
		no_items_close_tab.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				search_tab.dispose();
			}

		});

		MenuItem no_items_column_setup_item = new MenuItem(no_items_menu,
				SWT.PUSH);
		no_items_column_setup_item.setText(_
				._("mainwindow.searchtab.popupmenu.column_setup"));
		no_items_column_setup_item.setImage(SWTImageRepository
				.getImage("columns_setup.png"));
		no_items_column_setup_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				search_results.showColumnEditorWindow();
			}
		});

		pop_up_menu = new Menu(search_results);

		download_item = new MenuItem(pop_up_menu, SWT.PUSH);
		download_item.setText(_._("mainwindow.searchtab.popupmenu.download"));
		download_item.setImage(SWTImageRepository
				.getImage("start_download.png"));
		download_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				startDownload();
			}
		});

		MenuItem try_again = new MenuItem(pop_up_menu, SWT.PUSH);
		try_again.setText(_._("mainwindow.searchtab.popupmenu.try_again"));
		try_again.setImage(SWTImageRepository.getImage("refresh.png"));
		try_again.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				retry();
			}
		});

		MenuItem copy_ed2k_item = new MenuItem(pop_up_menu, SWT.PUSH);
		copy_ed2k_item.setText(_
				._("mainwindow.searchtab.popupmenu.copy_ed2k_link"));
		copy_ed2k_item.setImage(SWTImageRepository.getImage("ed2k_link.png"));
		copy_ed2k_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				copyED2KLinks();
			}
		});

		MenuItem close_item = new MenuItem(pop_up_menu, SWT.PUSH);
		close_item.setText(_._("mainwindow.searchtab.popupmenu.close"));
		close_item.setImage(SWTImageRepository.getImage("cancel.png"));
		close_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				search_tab.dispose();
			}

		});

		MenuItem column_setup_item = new MenuItem(pop_up_menu, SWT.PUSH);
		column_setup_item.setText(_
				._("mainwindow.searchtab.popupmenu.column_setup"));
		column_setup_item.setImage(SWTImageRepository
				.getImage("columns_setup.png"));
		column_setup_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				search_results.showColumnEditorWindow();
			}

		});

		new MenuItem(pop_up_menu, SWT.SEPARATOR);

		properties_item = new MenuItem(pop_up_menu, SWT.PUSH);
		properties_item.setText(_
				._("mainwindow.searchtab.popupmenu.properties"));
		properties_item.setImage(SWTImageRepository.getImage("info.png"));
		properties_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				SearchPropertiesWindow window = new SearchPropertiesWindow(
						search_results.getSelectedObject());
				window.getCoreComponents();
				window.initUIComponents();
			}

		});
		final SearchResultTab tab_instance = this;
		search_tab.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				_core.getSearchManager().removeSearch(query);
				SearchTab.search_tab.tabClosed(tab_instance);
			}

		});

	}

	public SearchQuery getSerchQuery() {
		return query;
	}

	public CTabItem getSearchTab() {
		return search_tab;
	}

	public void addSearchResult(SearchResult searchResult) {
		for (SearchResultItem item : searchResult.getSearchResultItemList()) {
			if (!result_hashes.contains(item.getFileHash())) {
				result_hashes.add(item.getFileHash());
				search_results.addRow(item);
			}
		}
		String query = searchResult.getSearchQuery().getQuery();
		String title = getTabTitle(query) + " (" + (search_results.getItemCount()) + ")";
		search_tab.setText(title);
	}

	private void startDownload() {
		List<SearchResultItem> list = search_results.getSelectedObjects();
		DownloadManager download_manager = _core.getDownloadManager();
		for (SearchResultItem item : list) {
			try {
				if (download_manager.hasDownload(item.getFileHash())) {
					if (!download_manager.getDownload(item.getFileHash())
							.isStarted())
						download_manager.startDownload(item.getFileHash());
					continue;
				}
				download_manager.addDownload(item);
				download_manager.startDownload(item.getFileHash());
				search_results.updateRow(item);
			} catch (DownloadManagerException e) {
				e.printStackTrace();
			}
		}
	}

	private void retry() {
		searchCompleted = false;
		_core.getSearchManager().removeSearch(query);
		search_results.clear();
		_core.getSearchManager().search(query);
		search_tab.setText(getTabTitle(query.getQuery()));
		search_tab.setImage(SWTImageRepository.getImage("search_queue.png"));
	}

	private void copyED2KLinks() {
		List<SearchResultItem> list = search_results.getSelectedObjects();
		String str = "";
		String separator = System.getProperty("line.separator");
		for (SearchResultItem item : list)
			str += item.getAsED2KLink().getAsString() + separator;
		Utils.setClipBoardText(str);
	}

	private String getTabTitle(String query) {
		String result = query;
		if (result.length() > 10)
			result = result.substring(0, 10) + "...";
		return result;
	}

	public boolean isSearchCompleted() {
		return searchCompleted;
	}

	public void searchStarted() {
		search_tab.setImage(SWTImageRepository.getImage("search_loading.png"));
	}

	public void completeSearch() {
		search_tab.setImage(null);
		searchCompleted = true;
	}

}
