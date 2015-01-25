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
package org.jmule.ui.swt.maintabs.transfers;

import java.text.DecimalFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMuleCore;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.downloadmanager.DownloadManagerException;
import org.jmule.core.downloadmanager.DownloadManagerListener;
import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.downloadmanager.DownloadSession.DownloadStatus;
import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.edonkey.ED2KLinkMalformedException;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.core.uploadmanager.UploadManagerException;
import org.jmule.core.uploadmanager.UploadSession;
import org.jmule.core.utils.Misc;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.Refreshable;
import org.jmule.ui.swt.SWTConstants;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.common.GapListPainter;
import org.jmule.ui.swt.mainwindow.MainWindow;
import org.jmule.ui.swt.tables.BufferedTableRow;
import org.jmule.ui.swt.tables.JMTable;
import org.jmule.ui.swt.tables.TableItemGapList;
import org.jmule.ui.utils.FileFormatter;
import org.jmule.ui.utils.SpeedFormatter;
import org.jmule.ui.utils.TimeFormatter;

/**
 * Created on Aug 02 2008
 * @author binary256
 * @version $$Revision: 1.20 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/04/12 16:38:20 $$
 */
public class DownloadList extends JMTable<DownloadSession> implements Refreshable,DownloadManagerListener {

	private DownloadManager download_manager;
	private UploadManager   upload_manager;
	
	private Menu no_downloads_menu;
	private Menu single_download_selected;
	private Menu multiple_downloads_selected;
	
	private MenuItem set_download_status;
	private MenuItem multiple_start_download;
	private MenuItem multiple_stop_download;
	private enum DownloadListStatus { NO_DOWNLOADS, NO_DOWNLOADS_SELECTED, STOPPED_DOWNLOAD_SELECTED,
									  STARTED_DOWNLOAD_SELECTED, MULTIPLE_STARTED_DOWNLOADS_SELECTED,
									  MULTIPLE_STOPPED_DOWNLOADS_SELECTED, MULTIPLE_MISC_DOWNLOADS_SELECTED };
		  
	public DownloadList(Composite composite, JMuleCore core) {
		super(composite, SWT.NONE);

		download_manager = core.getDownloadManager();
		upload_manager = core.getUploadManager();
		
		int width;
		
		width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_LIST_FILE_NAME_COLUMN_ID); 
		addColumn(SWT.LEFT, SWTConstants.DOWNLOAD_LIST_FILE_NAME_COLUMN_ID, _._("mainwindow.transferstab.downloads.column.filename"),"", width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_LIST_SIZE_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.DOWNLOAD_LIST_SIZE_COLUMN_ID, 	_._("mainwindow.transferstab.downloads.column.size"),"", width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID, _._("mainwindow.transferstab.downloads.column.transferred"),"", width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID, _._("mainwindow.transferstab.downloads.column.download_speed"), "", width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID, _._("mainwindow.transferstab.downloads.column.upload_speed"),"", width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_LIST_PROGRESS_COLUMN_ID);
		addColumn(SWT.LEFT, SWTConstants.DOWNLOAD_LIST_PROGRESS_COLUMN_ID, _._("mainwindow.transferstab.downloads.column.progress"),"", width);

		width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_LIST_COMPLETED_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.DOWNLOAD_LIST_COMPLETED_COLUMN_ID, _._("mainwindow.transferstab.downloads.column.completed"),"", width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_LIST_SOURCES_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.DOWNLOAD_LIST_SOURCES_COLUMN_ID, _._("mainwindow.transferstab.downloads.column.sources"),"", width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_LIST_REMAINING_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.DOWNLOAD_LIST_REMAINING_COLUMN_ID, _._("mainwindow.transferstab.downloads.column.eta"), _._("mainwindow.transferstab.downloads.column.eta.desc"), width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_LIST_STATUS_COLUMN_ID);
		addColumn(SWT.LEFT, SWTConstants.DOWNLOAD_LIST_STATUS_COLUMN_ID, _._("mainwindow.transferstab.downloads.column.status"),"", width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID, _._("mainwindow.transferstab.downloads.column.completed_src"), "", width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID, _._("mainwindow.transferstab.downloads.column.partial_src"), "", width);
		
		updateColumnOrder();
		updateColumnVisibility();
		
		no_downloads_menu = new Menu(this);
		
		MenuItem paste_ed2k_file_links = new MenuItem(no_downloads_menu,SWT.PUSH);
		paste_ed2k_file_links.setText(_._("mainwindow.transferstab.downloads.popupmenu.paste_ed2k_links"));
		paste_ed2k_file_links.setImage(SWTImageRepository.getImage("ed2k_link_paste.png"));
		paste_ed2k_file_links.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e ) {
				pasteED2KLink();
		}});

		new MenuItem(no_downloads_menu,SWT.SEPARATOR);
		
		MenuItem column_setup = new MenuItem(no_downloads_menu,SWT.PUSH);
		column_setup.setText(_._("mainwindow.transferstab.downloads.popupmenu.column_setup"));
		column_setup.setImage(SWTImageRepository.getImage("columns_setup.png"));
		column_setup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e ) {
				showColumnEditorWindow();
			}
		});
		
		single_download_selected = new Menu(this);
		
		set_download_status = new MenuItem(single_download_selected,SWT.PUSH);
		set_download_status.setText(_._("mainwindow.transferstab.downloads.popupmenu.start_download"));
		set_download_status.setImage(SWTImageRepository.getImage("start_download.png"));
		set_download_status.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e ) {
				if (getSelectedObject().isStarted())
					stopSelectedDownloads();
				else
					startSelectedDownloads();
		}});
		
		MenuItem single_cancel_download = new MenuItem(single_download_selected,SWT.PUSH);
		single_cancel_download.setText(_._("mainwindow.transferstab.downloads.popupmenu.cancel_download"));
		single_cancel_download.setImage(SWTImageRepository.getImage("cancel.png"));
		single_cancel_download.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e ) {
				cancelSelectedDownloads();
		}});
			
		new MenuItem(single_download_selected,SWT.SEPARATOR);
		
		MenuItem copy_ed2k_link = new MenuItem(single_download_selected,SWT.PUSH);
		copy_ed2k_link.setText(_._("mainwindow.transferstab.downloads.popupmenu.copy_ed2k_link"));
		copy_ed2k_link.setImage(SWTImageRepository.getImage("ed2k_link.png"));
		copy_ed2k_link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				copyED2KLink();
			}
		});
		
		
		MenuItem paste_ed2k_link = new MenuItem(single_download_selected,SWT.PUSH);
		paste_ed2k_link.setText(_._("mainwindow.transferstab.downloads.popupmenu.paste_ed2k_link"));
		paste_ed2k_link.setImage(SWTImageRepository.getImage("ed2k_link_paste.png"));
		paste_ed2k_link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e ) {
				pasteED2KLink();
		}});
		
		new MenuItem(single_download_selected,SWT.SEPARATOR);
		
		column_setup = new MenuItem(single_download_selected,SWT.PUSH);
		column_setup.setText(_._("mainwindow.transferstab.downloads.popupmenu.column_setup"));
		column_setup.setImage(SWTImageRepository.getImage("columns_setup.png"));
		column_setup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e ) {
				showColumnEditorWindow();
		}});
		
		new MenuItem(single_download_selected,SWT.SEPARATOR);
		
		MenuItem download_details = new MenuItem(single_download_selected,SWT.PUSH);
		download_details.setText(_._("mainwindow.transferstab.downloads.popupmenu.details"));
		download_details.setImage(SWTImageRepository.getImage("info.png"));
		
		download_details.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e ) {
				try {
					DownloadInfoWindow download_info_window = new DownloadInfoWindow(getSelectedObject());
					download_info_window.getCoreComponents();
					download_info_window.initUIComponents();
				}catch(Throwable es) {
					es.printStackTrace();
				}
		}});
		
		multiple_downloads_selected = new Menu(this);
		
		multiple_start_download = new MenuItem(multiple_downloads_selected,SWT.PUSH);
		multiple_start_download.setText(_._("mainwindow.transferstab.downloads.popupmenu.start_downloads"));
		multiple_start_download.setImage(SWTImageRepository.getImage("start_download.png"));
		multiple_start_download.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e ) {
				startSelectedDownloads();
		}});
		
		multiple_stop_download = new MenuItem(multiple_downloads_selected,SWT.PUSH);
		multiple_stop_download.setText(_._("mainwindow.transferstab.downloads.popupmenu.stop_downloads"));
		multiple_stop_download.setImage(SWTImageRepository.getImage("stop_download.png"));
		multiple_stop_download.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e ) {
				stopSelectedDownloads();
		}});
		
		MenuItem multiple_cancel_download = new MenuItem(multiple_downloads_selected,SWT.PUSH);
		multiple_cancel_download.setText(_._("mainwindow.transferstab.downloads.popupmenu.cancel_downloads"));
		multiple_cancel_download.setImage(SWTImageRepository.getImage("cancel.png"));
		multiple_cancel_download.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e ) {
				cancelSelectedDownloads();
		}});	
		
		new MenuItem(multiple_downloads_selected,SWT.SEPARATOR);
		
		MenuItem multiple_copy_ed2k_links = new MenuItem(multiple_downloads_selected, SWT.PUSH);
		multiple_copy_ed2k_links.setText(_._("mainwindow.transferstab.downloads.popupmenu.copy_ed2k_links"));
		multiple_copy_ed2k_links.setImage(SWTImageRepository.getImage("ed2k_link_paste.png"));
		multiple_copy_ed2k_links.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				copyED2KLink();
			}
		});
		
		new MenuItem(multiple_downloads_selected,SWT.SEPARATOR);
		
		column_setup = new MenuItem(multiple_downloads_selected,SWT.PUSH);
		column_setup.setText(_._("mainwindow.transferstab.downloads.popupmenu.column_setup"));
		column_setup.setImage(SWTImageRepository.getImage("columns_setup.png"));
		column_setup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e ) {
				showColumnEditorWindow();
		}});
		
		for(DownloadSession session : download_manager.getDownloads() ) {
			addDownlaodSession(session);
		}
		
		download_manager.addDownloadManagerListener(this);
		
	}
	
	private DownloadListStatus getSelectionStatus() {
		
		if ((getSelectionCount()==0)&&(getItems().length>0))
			return DownloadListStatus.NO_DOWNLOADS_SELECTED;
		
		if ((getSelectionCount()==1)&&(getSelectedObject().isStarted())) 
			return DownloadListStatus.STARTED_DOWNLOAD_SELECTED;
		
		if ((getSelectionCount()==1)&&(!getSelectedObject().isStarted())) 
			return DownloadListStatus.STOPPED_DOWNLOAD_SELECTED;
		
		if (getSelectionCount()>1) {
			boolean contain_started = false;
			boolean contain_stopped = false;
			for(DownloadSession session : getSelectedObjects()) {
				if (session.isStarted())
					contain_started = true;
				
				if (!session.isStarted())
					contain_stopped = true;
				
				if ((contain_started)&&(contain_stopped))
					break;
			}
			
			if ((contain_started)&&(!contain_stopped))
				return DownloadListStatus.MULTIPLE_STARTED_DOWNLOADS_SELECTED;
			
			if ((!contain_started)&&(contain_stopped))
				return DownloadListStatus.MULTIPLE_STOPPED_DOWNLOADS_SELECTED;
			
			return DownloadListStatus.MULTIPLE_MISC_DOWNLOADS_SELECTED;
		}
		
		return DownloadListStatus.NO_DOWNLOADS;
	}

	protected Menu getPopUpMenu() {
		DownloadListStatus status =  getSelectionStatus();
		
		switch (status) {
		
		case NO_DOWNLOADS_SELECTED : return no_downloads_menu;
		
		case STARTED_DOWNLOAD_SELECTED : {
			set_download_status.setImage(SWTImageRepository.getImage("stop_download.png"));
			set_download_status.setText(_._("mainwindow.transferstab.downloads.popupmenu.stop_download"));
			return single_download_selected;
		}
		
		case STOPPED_DOWNLOAD_SELECTED : {
			set_download_status.setImage(SWTImageRepository.getImage("start_download.png"));
			set_download_status.setText(_._("mainwindow.transferstab.downloads.popupmenu.start_download"));
			return single_download_selected;
		}
		
		case MULTIPLE_STARTED_DOWNLOADS_SELECTED : {
			multiple_start_download.setEnabled(false);
			multiple_stop_download.setEnabled(true);
			return multiple_downloads_selected;
		}
		
		case MULTIPLE_STOPPED_DOWNLOADS_SELECTED : {
			multiple_start_download.setEnabled(true);
			multiple_stop_download.setEnabled(false);
			return multiple_downloads_selected;
		}
		
		case MULTIPLE_MISC_DOWNLOADS_SELECTED : {
			multiple_start_download.setEnabled(true);
			multiple_stop_download.setEnabled(true);
			return multiple_downloads_selected;
		}
		
		default : return no_downloads_menu;
		}
	}
	
	protected int compareObjects(DownloadSession object1,
			DownloadSession object2, int columnID, boolean order) {
		
		if (columnID == SWTConstants.DOWNLOAD_LIST_FILE_NAME_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getSharingName", order);
		}

		if (columnID == SWTConstants.DOWNLOAD_LIST_SIZE_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getFileSize", order);
		}
		
		if (columnID == SWTConstants.DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getTransferredBytes", order);
		}
		
		if (columnID == SWTConstants.DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getSpeed", order);
		}
		
		if (columnID == SWTConstants.DOWNLOAD_LIST_PROGRESS_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getPercentCompleted", order);
		}
		
		if (columnID == SWTConstants.DOWNLOAD_LIST_COMPLETED_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getPercentCompleted", order);
		}
		
		if (columnID == SWTConstants.DOWNLOAD_LIST_SOURCES_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getPeerCount", order);
		}
		
		if (columnID == SWTConstants.DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getCompletedSources", order);
		}
		
		if (columnID == SWTConstants.DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getPartialSources", order);
		}
		
		if (columnID == SWTConstants.DOWNLOAD_LIST_REMAINING_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getETA", order);
		}
		
		if (columnID == SWTConstants.DOWNLOAD_LIST_STATUS_COLUMN_ID) {
			return Misc.compareAllObjects(object1, object2, "getStatus", order);
		}
		
		return 0;
	}

	public void updateRow(DownloadSession session) {
		String file_name = session.getSharingName();
		Image image = SWTImageRepository.getIconByExtension(file_name);
		
		setRowImage(session, SWTConstants.DOWNLOAD_LIST_FILE_NAME_COLUMN_ID, image);
		
		if (getRowText(session, SWTConstants.DOWNLOAD_LIST_FILE_NAME_COLUMN_ID).isEmpty())
			setRowText(session,  SWTConstants.DOWNLOAD_LIST_FILE_NAME_COLUMN_ID, file_name);
		
		if (getRowText(session, SWTConstants.DOWNLOAD_LIST_SIZE_COLUMN_ID).isEmpty())
			setRowText(session,  SWTConstants.DOWNLOAD_LIST_SIZE_COLUMN_ID, FileFormatter.formatFileSize(session.getFileSize()));
		
		setRowText(session,  SWTConstants.DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID, FileFormatter.formatFileSize(session.getTransferredBytes()));
		
		DecimalFormat formatter = new DecimalFormat("0.00");
		setRowText(session,  SWTConstants.DOWNLOAD_LIST_COMPLETED_COLUMN_ID, formatter.format(session.getPercentCompleted())+"%");
		
		String speed = SpeedFormatter.formatSpeed(session.getSpeed());
		setRowText(session,  SWTConstants.DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID,speed);
		
		FileHash file_hash = session.getFileHash();
		float upload_speed = 0;
		if (upload_manager.hasUpload(file_hash))
			try {
				upload_speed = upload_manager.getUpload(file_hash).getSpeed();
			} catch (UploadManagerException e) {
				e.printStackTrace();
			}
		
		setRowText(session,  SWTConstants.DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID,SpeedFormatter.formatSpeed(upload_speed));
		
		int peerCount = session.getPeerCount();
		int partialSources = session.getPartialSources();
		UploadSession upload_session;
		try {
			if (upload_manager.hasUpload(session.getFileHash())) {
				upload_session = upload_manager.getUpload(session.getFileHash());
				peerCount += upload_session.getPeerCount();
				partialSources += upload_session.getPeerCount();
			}
		} catch (UploadManagerException e) {
			e.printStackTrace();
			return ;
		}
		setRowText(session, SWTConstants.DOWNLOAD_LIST_SOURCES_COLUMN_ID,peerCount+"");
		setRowText(session, SWTConstants.DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID, session.getCompletedSources()+"");
		setRowText(session, SWTConstants.DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID, partialSources+"");
		
		String time = TimeFormatter.formatColon(session.getETA());
		setRowText(session, SWTConstants.DOWNLOAD_LIST_REMAINING_COLUMN_ID,time);
		String status;
		if (session.getStatus() == DownloadStatus.STARTED)
			status = _._("mainwindow.transferstab.downloads.column.status.started");
		else
			status = _._("mainwindow.transferstab.downloads.column.status.stopped");
		setRowText(session, SWTConstants.DOWNLOAD_LIST_STATUS_COLUMN_ID,status);
	}
	
	public void addDownlaodSession(DownloadSession session) {
		addRow(session);
		GapListPainter gap_list_painter = new GapListPainter(session.getGapList(),session.getFileSize());
		TableItemGapList painter = new TableItemGapList(SWTPreferences.getDefaultColumnOrder(SWTConstants.DOWNLOAD_LIST_PROGRESS_COLUMN_ID),gap_list_painter);
		addCustumControl(getItemCount()-1, painter);
	}

	public void refresh() {
		if (isDisposed()) return ;
		//redraw();
		for(DownloadSession session : download_manager.getDownloads()) {
			if (!hasObject(session)) continue;
			if (getRow(session).isVisible())
				updateRow(session);
		}
	}
	
	private void startSelectedDownloads() {
		//if (_core.getServerManager().getConnectedServer()==null) {
		//	Utils.showWarningMessage(getShell(), _._("mainwindow.transferstab.downloads.not_connected.title"), _._("mainwindow.transferstab.downloads.not_connected"));
		//	return ;
		//}
		List<DownloadSession> list = getSelectedObjects();
		for(DownloadSession downloadSession : list)
			if (downloadSession.getStatus() == DownloadStatus.STOPPED)
				try {
					download_manager.startDownload(downloadSession.getFileHash());
				} catch (DownloadManagerException e) {
					e.printStackTrace();
				}
	}
	
	private void stopSelectedDownloads() {
		List<DownloadSession> list = getSelectedObjects();
		for(DownloadSession downloadSession : list)
			if (downloadSession.getStatus() == DownloadStatus.STARTED)
				try {
					download_manager.stopDownload(downloadSession.getFileHash());
				} catch (DownloadManagerException e) {
					e.printStackTrace();
				}
	}
	
	public void cancelSelectedDownloads() {
		boolean result;
		List<DownloadSession> list = getSelectedObjects();
		if (list.size()==1)
			result = Utils.showConfirmMessage(getShell(),_._("mainwindow.transferstab.downloads.confirm_cancel.title"), _._("mainwindow.transferstab.downloads.confirm_cancel"));
		else
			result = Utils.showConfirmMessage(getShell(), _._("mainwindow.transferstab.downloads.confirm_cancel.title") , _._("mainwindow.transferstab.downloads.confirm_cancel_multi"));
		if (result)
			for(DownloadSession downloadSession : list)
				try {
					download_manager.cancelDownload(downloadSession.getFileHash());
				} catch (DownloadManagerException e) {
					e.printStackTrace();
				}
	}
	
	private void pasteED2KLink() {
		String clipboard_text = Utils.getClipboardText();
		List<ED2KFileLink> links;
		try {
			links = ED2KFileLink.extractLinks(clipboard_text);
		} catch (ED2KLinkMalformedException e) {
			e.printStackTrace();
			return ;
		}
		
		String failed ="";
		for(ED2KFileLink link : links) {
			if (!download_manager.hasDownload(link.getFileHash()))
				try {
					download_manager.addDownload(link);
					download_manager.startDownload(link.getFileHash());
				} catch (DownloadManagerException e) {
					e.printStackTrace();
				}
			else
				failed+=link.getFileName()+"\n";
		}
		if (failed.length()!=0)
			Utils.showWarningMessage(getShell(), _._("mainwindow.transferstab.downloads.ed2k_paste_failed_already_exist.title"),_._("mainwindow.transferstab.downloads.ed2k_paste_failed_already_exist")+" : \n"+failed + "\n\n"+_._("mainwindow.transferstab.downloads.ed2k_paste_failed_already_exist2"));
		
	}

	private void copyED2KLink() {
		List<DownloadSession> selected_sessions = getSelectedObjects();
		String ed2k_links = "";
		for(DownloadSession session : selected_sessions)
			ed2k_links+=session.getED2KLink() + System.getProperty("line.separator"); 
		Utils.setClipBoardText(ed2k_links);
	}
	
	public void downloadAdded(final FileHash fileHash) {
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				DownloadSession session;
				try {
					session = download_manager.getDownload(fileHash);
					addDownlaodSession(session);
					MainWindow.getLogger().fine(_._("mainwindow.logtab.message_download_added",session.getSharingName()));
				} catch (DownloadManagerException e) {
					e.printStackTrace();
				}
				
			}
		});
		
	}

	public void downloadRemoved(final FileHash fileHash) {
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				DownloadSession session = null;
				
				for(BufferedTableRow s : line_list) {
					DownloadSession x = (DownloadSession) s.getData(SWTConstants.ROW_OBJECT_KEY);
					if (x.getFileHash().equals(fileHash)) { 
						session = x;
						break;
					}
				}
				if (session == null) return ;
				removeRow(session);
				refresh();
				if (session.getPercentCompleted()!=100)
					MainWindow.getLogger().fine(_._("mainwindow.logtab.message_download_removed",session.getSharingName()));
				else
					MainWindow.getLogger().fine(_._("mainwindow.logtab.message_download_finished",session.getSharingName()));
			}
		});
	}

	public void downloadStarted(final FileHash fileHash) {
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				DownloadSession downloadSession;
				try {
					downloadSession = download_manager.getDownload(fileHash);
				} catch (DownloadManagerException e) {
					e.printStackTrace();
					return ;
				}
				updateRow(downloadSession);
				MainWindow.getLogger().fine(_._("mainwindow.logtab.message_download_started",downloadSession.getSharingName()));
		}});
		
	}

	public void downloadStopped(final FileHash fileHash) {
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				DownloadSession downloadSession;
				try {
					downloadSession = download_manager.getDownload(fileHash);
				} catch (DownloadManagerException e) {
					e.printStackTrace();
					return ;
				}
				updateRow(downloadSession);
				MainWindow.getLogger().fine(_._("mainwindow.logtab.message_download_stopped",downloadSession.getSharingName()));
		}});
	}

}
