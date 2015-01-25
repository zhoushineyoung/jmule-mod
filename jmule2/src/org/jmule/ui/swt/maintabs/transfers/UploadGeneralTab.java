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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.jmule.core.uploadmanager.UploadSession;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.Refreshable;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.skin.SWTSkin;
import org.jmule.ui.utils.FileFormatter;
import org.jmule.ui.utils.SpeedFormatter;
import org.jmule.ui.utils.TimeFormatter;

/**
 * Created on Aug 11, 2008
 * @author binary256
 * @version $Revision: 1.3 $
 * Last changed by $Author: binary255 $ on $Date: 2009/11/17 14:53:42 $
 */
public class UploadGeneralTab extends CTabItem implements Refreshable {

	private UploadSession upload_session;

	private Label upload_speed,total_uploaded,eta,peers;
	
	public UploadGeneralTab(CTabFolder tabFolder, UploadSession session) {
		super(tabFolder, SWT.NONE);
		upload_session = session;
		setText(Localizer._("uploadinfowindow.tab.general.title"));
		
		SWTSkin skin = null;
		try {
		    skin = (SWTSkin)JMuleUIManager.getJMuleUI().getSkin();
		}catch(Throwable t) {
		}
		Composite content = new Composite(tabFolder,SWT.NONE);
		setControl(content);
		content.setLayout(new GridLayout(1,false));
		Label label;
		
		Group general = new Group(content,SWT.NONE);
		GridLayout layout = new GridLayout(2,false);
		layout.marginWidth = 10;
		general.setLayout(layout);
		general.setText(Localizer._("uploadinfowindow.tab.general.title"));
		general.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		label = new Label(general,SWT.NONE);
		label.setText(Localizer._("uploadinfowindow.tab.general.label.filename")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		label = new Label(general,SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(upload_session.getSharingName());
		label.setToolTipText(upload_session.getSharingName());
		
		label = new Label(general,SWT.NONE);
		label.setText(Localizer._("uploadinfowindow.tab.general.label.filehash")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		label = new Label(general,SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(upload_session.getFileHash().getAsString());
		label.setToolTipText(upload_session.getFileHash().getAsString());
		label.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		
		label = new Label(general,SWT.NONE);
		label.setText(Localizer._("uploadinfowindow.tab.general.label.filesize")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		label = new Label(general,SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(FileFormatter.formatFileSize(upload_session.getFileSize()));
		label.setToolTipText(FileFormatter.formatFileSize(upload_session.getFileSize()));
		
		label = new Label(general,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("uploadinfowindow.tab.general.label.ed2k_link") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		label = new Label(general,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(upload_session.getED2KLink().getAsString());
		label.setToolTipText(_._("uploadinfowindow.tab.general.label.ed2k_link.tooltip"));
		label.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		label.setCursor(new Cursor(SWTThread.getDisplay(),SWT.CURSOR_HAND));
		label.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event arg0) {
				Utils.setClipBoardText(upload_session.getED2KLink().getAsString());
			}
		});
		
		Group transfer = new Group(content,SWT.NONE);
		GridLayout transfer_layout = new GridLayout(2,false);
		transfer_layout.marginWidth = 10;
		transfer.setLayout(transfer_layout);
		transfer.setText(Localizer._("uploadinfowindow.tab.general.group.transfer"));
		transfer.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		label = new Label(transfer,SWT.NONE);
		label.setText(Localizer._("uploadinfowindow.tab.transfer.label.uploadspeed")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		upload_speed = new Label(transfer,SWT.NONE);
		upload_speed.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		upload_speed.setText(SpeedFormatter.formatSpeed(upload_session.getSpeed()));

		label = new Label(transfer,SWT.NONE);
		label.setText(Localizer._("uploadinfowindow.tab.transfer.label.transferred")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		total_uploaded = new Label(transfer,SWT.NONE);
		total_uploaded.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		total_uploaded.setText(FileFormatter.formatFileSize(upload_session.getTransferredBytes()));
		
		label = new Label(transfer,SWT.NONE);
		label.setText(Localizer._("uploadinfowindow.tab.transfer.label.eta")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		eta = new Label(transfer,SWT.NONE);
		eta.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		eta.setText(TimeFormatter.format(upload_session.getETA()));

		label = new Label(transfer,SWT.NONE);
		label.setText(Localizer._("uploadinfowindow.tab.transfer.label.peers")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		peers = new Label(transfer,SWT.NONE);
		peers.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		peers.setText(upload_session.getPeerCount() + "");		
	}

	public void refresh() {
		if (isDisposed()) return ;
		peers.setText(upload_session.getPeerCount() + "");
		eta.setText(TimeFormatter.format(upload_session.getETA()));
		total_uploaded.setText(FileFormatter.formatFileSize(upload_session.getTransferredBytes()));
		upload_speed.setText(SpeedFormatter.formatSpeed(upload_session.getSpeed()));
	}

}
