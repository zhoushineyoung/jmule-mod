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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.downloadmanager.DownloadManagerListener;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.core.uploadmanager.UploadManagerListener;
import org.jmule.ui.swing.maintabs.AbstractTab;
import org.jmule.ui.swing.maintabs.LogTab;
import org.jmule.ui.swing.tables.DownloadsTable;
import org.jmule.ui.swing.tables.UploadsTable;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2009/09/22 19:08:43 $$
 */
public class TransfersTab extends AbstractTab {

	private JSplitPane splitPane;
	private GridLayout gridLayout;
	private JScrollPane downloadsScrollPane;
	private JScrollPane uploadsScrollPane;
	private DownloadsTable downloadsTable;
	private UploadsTable uploadsTable;
	private TitledBorder downloads_titled_border;
	private TitledBorder uploads_titled_border;
	
	private JMuleCore _core = JMuleCoreFactory.getSingleton();
	private DownloadManager _download_manager = _core.getDownloadManager();
	private UploadManager _upload_manager = _core.getUploadManager();
	
	public TransfersTab(JFrame parent) {
		super(parent);
		initComponents();
		super.registerRefreshable(downloadsTable);
		super.registerRefreshable(uploadsTable);
	}
	
	private void initComponents() {
		
		splitPane = new JSplitPane();
		gridLayout = new GridLayout(1,1);
		downloadsScrollPane = new JScrollPane();
		uploadsScrollPane = new JScrollPane();
		downloadsTable = new DownloadsTable(parent);
		uploadsTable = new UploadsTable(parent);
		
	    downloadsScrollPane.setPreferredSize(new Dimension(100,200));
		uploadsScrollPane.setPreferredSize(new Dimension(10,10));
		
		downloads_titled_border = javax.swing.BorderFactory.createTitledBorder("Downloads");
		Border border = BorderFactory.createLineBorder(Color.GRAY);
		downloads_titled_border.setBorder(border);
		downloads_titled_border.setTitleFont(new java.awt.Font("Dialog", 0, 12));
		//downloadsScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Downloads", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 11), java.awt.Color.gray));
		
		downloadsScrollPane.setBorder(downloads_titled_border);
		
		uploads_titled_border = javax.swing.BorderFactory.createTitledBorder("Uploads");
		uploads_titled_border.setBorder(border);
		uploads_titled_border.setTitleFont(new java.awt.Font("Dialog", 0, 12));
		
		uploadsScrollPane.setBorder(uploads_titled_border);
		
		this.setLayout( gridLayout );
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		this.add(splitPane);
		
		splitPane.setTopComponent( downloadsScrollPane );
		splitPane.setBottomComponent( uploadsScrollPane );
		
		downloadsScrollPane.setViewportView(downloadsTable);
		uploadsScrollPane.setViewportView(uploadsTable);
		
		_download_manager.addDownloadManagerListener(new DownloadManagerListener() {

			public void downloadAdded(FileHash fileHash) {
				countDownloads();
				LogTab.getLogInstance().addMessage("Download added : " + fileHash.getAsString());
			}

			public void downloadRemoved(FileHash fileHash) {
				countDownloads();
				LogTab.getLogInstance().addMessage("Download removed : " + fileHash.getAsString());
			}

			public void downloadStarted(FileHash fileHash) {
				LogTab.getLogInstance().addMessage("Download started : " + fileHash.getAsString());
			}

			public void downloadStopped(FileHash fileHash) {
				LogTab.getLogInstance().addMessage("Download stopped : " + fileHash.getAsString());
			}
			
		});
		
		_upload_manager.addUploadManagerListener(new UploadManagerListener() {

			public void uploadAdded(FileHash fileHash) {
				countUploads();
			}

			public void uploadRemoved(FileHash fileHash) {
				countUploads();
			}
			
		});
		
		//splitPane.resetToPreferredSizes();
		countDownloads();
		countUploads();
	}
	
	private void countDownloads() {
		String download_count = "Downloads(" + _download_manager.getDownloadCount() + ")";
		downloads_titled_border.setTitle(download_count);
		downloadsScrollPane.updateUI();
	}
	
	private void countUploads() {
		String upload_count = "Uploads(" + _upload_manager.getUploadCount() + ")";
		uploads_titled_border.setTitle(upload_count);
		uploadsScrollPane.updateUI();
	}
	
}
