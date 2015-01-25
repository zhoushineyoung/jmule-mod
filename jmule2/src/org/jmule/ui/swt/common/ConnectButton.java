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
package org.jmule.ui.swt.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.tab.main.serverlist.SWTServerListWrapper;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2011/03/27 16:51:29 $$
 */
public class ConnectButton extends ToolItem {

	private final int STATUS_CONNECTING 		= 0x01;
	private final int STATUS_CONNECTED 			= 0x02;
	private final int STATUS_DISCONNECTED 		= 0x03;
	
	private int status = STATUS_DISCONNECTED;
	
	public ConnectButton(ToolBar toolBar) {
		super(toolBar,  SWT.NONE);

		change_icon();
		
		SWTServerListWrapper.getInstance().setConnectButton(this);
		addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {}

			public void widgetSelected(SelectionEvent arg0) {
				
				if ( status == STATUS_DISCONNECTED ) {
					SWTServerListWrapper.getInstance().startAutoConnect();
				}
				
				if ( status == STATUS_CONNECTING ) {
					SWTServerListWrapper.getInstance().stopConnecting();
				}
				
				if (status == STATUS_CONNECTED) {
					SWTServerListWrapper.getInstance().disconnect();
				}
			}
			
		});
	}
	
	public void setDisconnected() {
		status = STATUS_DISCONNECTED;
		change_icon();
	}
	
	public void setConnecting() {
		status = STATUS_CONNECTING;
		change_icon();
	}
	
	public void setConnected() {
		status = STATUS_CONNECTED;
		change_icon();
	}
	
	private void change_icon() {
		switch(status) {
			case STATUS_CONNECTING : {
				setText(Localizer._("mainwindow.toolbar.connect_item.cancel"));
				setImage(SWTImageRepository.getImage("connect_stop.png"));
				break;
			}
			
			case STATUS_CONNECTED : {
				setText(Localizer._("mainwindow.toolbar.connect_item.disconnect"));
				setImage(SWTImageRepository.getImage("connect_drop.png"));
				break;
			}
			
			case STATUS_DISCONNECTED : {
				setText(Localizer._("mainwindow.toolbar.connect_item.connect"));
				setImage(SWTImageRepository.getImage("connect_do.png"));
				break;
			}
		}
	}

	protected void checkSubclass() {
    }
	
}
