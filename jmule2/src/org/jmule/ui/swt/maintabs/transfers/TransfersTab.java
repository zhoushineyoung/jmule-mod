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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.jmule.core.JMuleCore;
import org.jmule.core.uploadmanager.UploadSession;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.swt.GUIUpdater;
import org.jmule.ui.swt.Refreshable;
import org.jmule.ui.swt.common.SashControl;
import org.jmule.ui.swt.maintabs.AbstractTab;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/10/29 16:10:08 $$
 */
public class TransfersTab extends AbstractTab{

	private Composite download_panel;
	
	private Composite upload_panel;
	
	private JMuleCore _core;
	
	private DownloadList download_list;
	private UploadList 	 upload_list;
	
	private Refreshable refreshable;
	private Group downloads, uploads;	
	public TransfersTab(Composite shell,JMuleCore core) {
		super(shell);

		_core = core;
		
		setLayout(new FormLayout());
		
		download_panel = new Composite(this,SWT.NONE);
		upload_panel = new Composite(this,SWT.NONE);
		
		SashControl.createHorizontalSash(50, 50, this, download_panel, upload_panel);
		
		download_panel.setLayout(new FillLayout());
		upload_panel.setLayout(new FillLayout());
		
		SWTSkin skin = null;
		try {
			
		    skin = (SWTSkin)JMuleUIManager.getJMuleUI().getSkin();
		
		}catch(Throwable t) {}
		
		downloads = new Group(download_panel,SWT.NONE);
		downloads.setFont(skin.getDefaultFont());
		downloads.setLayout(new FillLayout());
		download_list = new DownloadList(downloads,_core);
		
		uploads = new Group(upload_panel,SWT.NONE);
		uploads.setFont(skin.getDefaultFont());
		uploads.setLayout(new FillLayout());
		upload_list = new UploadList(uploads,_core);
		
		refreshable = new Refreshable() {
			public void refresh() {
				if (isDisposed()) return;
				int download_count = _core.getDownloadManager().getDownloadCount();
				int upload_count = _core.getUploadManager().getUploadCount();
				
				for(UploadSession session : _core.getUploadManager().getUploads()) {
					if (_core.getDownloadManager().hasDownload(session.getFileHash()))
						upload_count--;
				}
				
				downloads.setText(Localizer._("mainwindow.transferstab.downloads")+"("+download_count+")");
				uploads.setText(Localizer._("mainwindow.transferstab.uploads")+"("+upload_count+")");
			}
		};
		refreshable.refresh();
	}

	public JMULE_TABS getTabType() {
		return JMULE_TABS.TRANSFERS;
	}

	public void lostFocus() {
		GUIUpdater.getInstance().removeRefreshable(download_list);
		GUIUpdater.getInstance().removeRefreshable(upload_list);
		GUIUpdater.getInstance().removeRefreshable(refreshable);
	}

	public void obtainFocus() {
		GUIUpdater.getInstance().addRefreshable(download_list);
		GUIUpdater.getInstance().addRefreshable(upload_list);
		GUIUpdater.getInstance().addRefreshable(refreshable);
	}

	public void disposeTab() {

	}

}
