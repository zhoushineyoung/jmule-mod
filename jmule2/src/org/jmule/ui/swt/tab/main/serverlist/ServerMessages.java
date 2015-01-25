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
package org.jmule.ui.swt.tab.main.serverlist;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2011/03/27 16:51:29 $$
 */
public class ServerMessages extends Text {

	public ServerMessages(Composite composite) {
		super(composite,SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		SWTSkin skin = null;
		try {
		    skin = (SWTSkin)JMuleUIManager.getJMuleUI().getSkin();
		}catch(Throwable t) {}
		
		setFont(skin.getDefaultFont());
		setEditable(false);
		setBackground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		SWTServerListWrapper.getInstance().setServerMessages(this);
		
		Menu popup_menu = new Menu(this);
		
		final MenuItem copy_selected = new MenuItem(popup_menu,SWT.PUSH);
		copy_selected.setText(_._("mainwindow.serverlisttab.servermesasges.popupmenu.copy"));
		copy_selected.setImage(SWTImageRepository.getImage("copy.png"));
		copy_selected.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String text = getSelectionText();
				if (text.length()!=0)
					Utils.setClipBoardText(text);
			}
		});
		
		final MenuItem select_all = new MenuItem(popup_menu,SWT.PUSH);
		select_all.setText(_._("mainwindow.serverlisttab.servermesasges.popupmenu.select_all"));
		select_all.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				selectAll();
			}
		});
		
		new MenuItem(popup_menu,SWT.SEPARATOR);
		
		final MenuItem clear = new MenuItem(popup_menu,SWT.PUSH);
		clear.setText(_._("mainwindow.serverlisttab.servermesasges.popupmenu.clear"));
		clear.setImage(SWTImageRepository.getImage("remove_all.png"));
		clear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setText("");
			}
		});
		
		setMenu(popup_menu);
		
		addMenuDetectListener(new MenuDetectListener() {

			public void menuDetected(MenuDetectEvent arg0) {
				if (getText().length()==0) {
					clear.setEnabled(false);
					select_all.setEnabled(false);
				}
				else {
					clear.setEnabled(true);
					select_all.setEnabled(true);
				}
				
				if (getSelectionCount()==0)
					copy_selected.setEnabled(false);
				else
					copy_selected.setEnabled(true);
			}
			
		});
	}
	
	public void addText(String message) {
		append(message+"\n");
		setSelection(getText().length());
	}
	
	protected void checkSubclass() {

    }
	
}
