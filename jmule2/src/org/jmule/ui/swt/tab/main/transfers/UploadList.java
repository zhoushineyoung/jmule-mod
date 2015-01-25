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
package org.jmule.ui.swt.tab.main.transfers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMuleCore;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.core.uploadmanager.UploadManagerException;
import org.jmule.core.uploadmanager.UploadManagerListener;
import org.jmule.core.uploadmanager.UploadSession;
import org.jmule.core.utils.Misc;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.Refreshable;
import org.jmule.ui.swt.SWTConstants;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.mainwindow.MainWindow;
import org.jmule.ui.swt.tables.BufferedTableRow;
import org.jmule.ui.swt.tables.JMTable;
import org.jmule.ui.utils.FileFormatter;
import org.jmule.ui.utils.SpeedFormatter;
import org.jmule.ui.utils.TimeFormatter;


/**
 * Created on Aug 10, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2011/03/27 16:51:29 $
 */
public class UploadList extends JMTable<UploadSession> implements Refreshable,UploadManagerListener{

	private UploadManager   upload_manager;
	
	private enum UploadListStatus { NO_UPLOADS,NO_UPLOADS_SELECTED,UPLOAD_SELECTED,MULTIPLE_UPLOADS_SELECTED };
	
	private Menu no_uploads_menu;
	private Menu upload_selected_menu;
	MenuItem open_shared_file;
	
	private boolean selectedRunnableFiles() {
		java.util.List<UploadSession> selectedSessions = getSelectedObjects();
		for(UploadSession session : selectedSessions) {
			SharedFile file = session.getSharedFile();
			String extension = FileFormatter.getFileExtension(file.getSharingName());
			Program program = Program.findProgram(extension);
			if (program != null) return true;
		}
		return false;
	}
	
	public UploadList(Composite composite,JMuleCore core) {
		super(composite, SWT.NONE);
		
		upload_manager   = core.getUploadManager();
		
		upload_manager.addUploadManagerListener(this);
		
		int width;
		
		width = swt_preferences.getColumnWidth(SWTConstants.UPLOAD_LIST_FILE_NAME_COLUMN_ID);
		addColumn(SWT.LEFT, SWTConstants.UPLOAD_LIST_FILE_NAME_COLUMN_ID, Localizer._("mainwindow.transferstab.uploads.column.filename"), "", width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.UPLOAD_LIST_FILE_SIZE_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.UPLOAD_LIST_FILE_SIZE_COLUMN_ID, Localizer._("mainwindow.transferstab.uploads.column.filesize"), "", width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID, Localizer._("mainwindow.transferstab.uploads.column.uploadspeed"), "", width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.UPLOAD_LIST_PEERS_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.UPLOAD_LIST_PEERS_COLUMN_ID, Localizer._("mainwindow.transferstab.uploads.column.peers"), "", width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.UPLOAD_LIST_ETA_COLUMN_ID); 
		addColumn(SWT.RIGHT, SWTConstants.UPLOAD_LIST_ETA_COLUMN_ID, Localizer._("mainwindow.transferstab.uploads.column.eta"), _._("mainwindow.transferstab.uploads.column.eta.desc"), width);
		
		width = swt_preferences.getColumnWidth(SWTConstants.UPLOAD_LIST_UPLOADED_COLUMN_ID);
		addColumn(SWT.RIGHT, SWTConstants.UPLOAD_LIST_UPLOADED_COLUMN_ID, Localizer._("mainwindow.transferstab.uploads.column.uploaded"), "", width);
		
		updateColumnOrder();
		updateColumnVisibility();
		
		no_uploads_menu = new Menu(this);
		
		MenuItem column_setup = new MenuItem(no_uploads_menu,SWT.PUSH);
		column_setup.setText(Localizer._("mainwindow.transferstab.uploads.popupmenu.columnsetup"));
		column_setup.setImage(SWTImageRepository.getImage("columns_setup.png"));
		column_setup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e ) {
				showColumnEditorWindow();
			}
		});
		
		upload_selected_menu = new Menu(this);
		
		open_shared_file = new MenuItem(upload_selected_menu,SWT.PUSH);
		open_shared_file.setText(_._("mainwindow.sharedtab.popupmenu.open"));
		open_shared_file.setImage(SWTImageRepository.getImage("open_file.png"));
		open_shared_file.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e ) {
				SharedFile shared_file = getSelectedObject().getSharedFile();
				String extension = FileFormatter.getFileExtension(shared_file.getSharingName());
				Program program = Program.findProgram(extension);
				if (program == null) return;
				program.execute(shared_file.getAbsolutePath());
			}
		});
		
		new MenuItem(upload_selected_menu,SWT.SEPARATOR);
		
		MenuItem upload_details = new MenuItem(upload_selected_menu,SWT.PUSH);
		upload_details.setText(Localizer._("mainwindow.transferstab.uploads.popupmenu.details"));
		upload_details.setImage(SWTImageRepository.getImage("info.png"));
		upload_details.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e ) {
				UploadInfoWindow window = new UploadInfoWindow(getSelectedObject());
				window.getCoreComponents();
				window.initUIComponents();
			}
		});
		
		new MenuItem(upload_selected_menu,SWT.SEPARATOR);
		column_setup = new MenuItem(upload_selected_menu,SWT.PUSH);
		column_setup.setText(Localizer._("mainwindow.transferstab.uploads.popupmenu.columnsetup"));
		column_setup.setImage(SWTImageRepository.getImage("columns_setup.png"));
		column_setup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e ) {
				showColumnEditorWindow();
			}
		});
		
		for(UploadSession session : upload_manager.getUploads()) {
			if (session.sharingCompleteFile()) {
				addRow(session);
			}
		}
		
	}
	
	private UploadListStatus getMenuStatus() {
		if ((getSelectionCount() == 0) && (getItems().length > 0))
			return UploadListStatus.NO_UPLOADS_SELECTED;
		if (getSelectionCount() == 1)
			return UploadListStatus.UPLOAD_SELECTED;
		if (getSelectionCount() >= 1)
			return UploadListStatus.MULTIPLE_UPLOADS_SELECTED;
		return UploadListStatus.NO_UPLOADS;
	}
	
	protected Menu getPopUpMenu() {
		UploadListStatus status = getMenuStatus();
		switch(status) {
		case NO_UPLOADS_SELECTED : return no_uploads_menu;
		case UPLOAD_SELECTED : {
			if (selectedRunnableFiles()) {
				open_shared_file.setEnabled(true);
			} else 
				open_shared_file.setEnabled(false);
			
			return upload_selected_menu; }
		case MULTIPLE_UPLOADS_SELECTED : return no_uploads_menu;
		default : return no_uploads_menu;
		
		}
	}
	
	protected int compareObjects(UploadSession object1, UploadSession object2,
			int columnID, boolean order) {
		if (columnID == SWTConstants.UPLOAD_LIST_FILE_NAME_COLUMN_ID)
			return Misc.compareAllObjects(object1, object2, "getSharingName",order);
		if (columnID == SWTConstants.UPLOAD_LIST_FILE_SIZE_COLUMN_ID)
			return Misc.compareAllObjects(object1, object2, "getFileSize",order);
		if (columnID == SWTConstants.UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID)
			return Misc.compareAllObjects(object1, object2, "getSpeed", order);
		if (columnID == SWTConstants.UPLOAD_LIST_PEERS_COLUMN_ID)
			return Misc.compareAllObjects(object1, object2, "getPeerCount",order);
		if (columnID == SWTConstants.UPLOAD_LIST_ETA_COLUMN_ID)
			return Misc.compareAllObjects(object1, object2, "getETA", order);
		if (columnID == SWTConstants.UPLOAD_LIST_UPLOADED_COLUMN_ID)
			return Misc.compareAllObjects(object1, object2,"getTransferredBytes", order);
		return 0;
	}

	public void updateRow(UploadSession object) {
		if (isDisposed()) return;
		Image image = SWTImageRepository.getIconByExtension(object.getSharingName());
				
		setRowImage(object, SWTConstants.UPLOAD_LIST_FILE_NAME_COLUMN_ID, image);
		
		if (getRowText(object, SWTConstants.UPLOAD_LIST_FILE_NAME_COLUMN_ID).isEmpty())
			setRowText(object,  SWTConstants.UPLOAD_LIST_FILE_NAME_COLUMN_ID, object.getSharingName());
		
		if (getRowText(object, SWTConstants.UPLOAD_LIST_FILE_SIZE_COLUMN_ID).isEmpty())
			setRowText(object,  SWTConstants.UPLOAD_LIST_FILE_SIZE_COLUMN_ID, FileFormatter.formatFileSize(object.getFileSize()));

		setRowText(object,  SWTConstants.UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID, SpeedFormatter.formatSpeed(object.getSpeed()));
		setRowText(object, SWTConstants.UPLOAD_LIST_PEERS_COLUMN_ID, object.getPeerCount()+ "");
		
		long eta = object.getETA();
		setRowText(object,  SWTConstants.UPLOAD_LIST_ETA_COLUMN_ID, TimeFormatter.format(eta));
		setRowText(object,  SWTConstants.UPLOAD_LIST_UPLOADED_COLUMN_ID, FileFormatter.formatFileSize(object.getTransferredBytes()));
	}

	public void refresh() {
		if (isDisposed()) return;
		for(UploadSession session : upload_manager.getUploads()) {
			if (hasObject(session)) {
				if (getRow(session).isVisible())
					updateRow(session);
			}
			else
				if (session.sharingCompleteFile()) {
					addRow(session);
					MainWindow.getLogger().fine(Localizer._("mainwindow.logtab.message_upload_added",session.getSharingName()));
				}
					
		}
	}

	public void uploadAdded(final FileHash fileHash) {
		if (SWTThread.getDisplay().isDisposed()) return;
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				UploadSession session;
				try {
					session = upload_manager.getUpload(fileHash);
					if (hasObject(session)) return;
					if (session.sharingCompleteFile()) {
						addRow(session);
						MainWindow.getLogger().fine(Localizer._("mainwindow.logtab.message_upload_added",session.getSharingName()));
					}
				} catch (UploadManagerException e) {
					e.printStackTrace();
				}

			}
		});
	}

	public void uploadRemoved(final FileHash fileHash) {
		if (fileHash == null) return ;
		if (SWTThread.getDisplay().isDisposed()) return;
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				UploadSession session = null;
				
				for(BufferedTableRow s : line_list) {
					UploadSession x = (UploadSession) s.getData(SWTConstants.ROW_OBJECT_KEY);
					if (x.getFileHash().equals(fileHash)) { 
						session = x;
						break;
					}
				}
				if (session == null) return ;
				removeRow(session);
				
				if (session.sharingCompleteFile()) {
					MainWindow.getLogger().fine(Localizer._("mainwindow.logtab.message_upload_removed",session.getSharingName()));
				}
		}});
	}

	

}
