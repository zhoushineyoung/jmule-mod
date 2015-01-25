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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.swt.GUIUpdater;
import org.jmule.ui.swt.Refreshable;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * Created on Aug 4, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2011/03/27 16:51:29 $
 */
public class DownloadInfoWindow implements JMuleUIComponent {

	private Shell shell;
	private DownloadSession download_session;
	
	private List<Refreshable> tabs = new LinkedList<Refreshable>();
	
	public DownloadInfoWindow(DownloadSession session) {
		download_session = session;
	}
	
	public void getCoreComponents() {
		
	}

	public void initUIComponents() {
		SWTSkin skin = null;
		try {
		    skin = (SWTSkin) JMuleUIManager.getJMuleUI().getSkin();
		}catch(Throwable t) {}
		
		
		Display display = SWTThread.getDisplay();
		final Shell shell1=new Shell(display,SWT.ON_TOP);
		shell=new Shell(shell1,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		
		shell.setText(Localizer._("downloadinfowindow.title"));
		shell.setImage(SWTImageRepository.getImage("info.png"));
				
		shell.setLayout(new FillLayout());
		
		Composite content = new Composite(shell,SWT.BORDER);
		content.setLayout(new GridLayout(1,true));
		CTabFolder tab_folder = new CTabFolder(content,SWT.BORDER);
		tab_folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tab_folder.setSimple(false);

		DownloadGeneralTab general_tab = new DownloadGeneralTab(tab_folder,download_session);
		tab_folder.setSelection(general_tab);
		tabs.add(general_tab);
		
		DownloadPeerListTab peer_list_tab = new DownloadPeerListTab(tab_folder,download_session);
		tabs.add(peer_list_tab);
		
		for(Refreshable refreshable : tabs) 
			GUIUpdater.getInstance().addRefreshable(refreshable);
		
		Composite buttons_composite = new Composite(content,SWT.NONE);
		buttons_composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttons_composite.setLayout(new GridLayout(1,false));
		Button button_close = new Button(buttons_composite,SWT.NONE);
		GridData grid_data = new GridData();
		grid_data.widthHint = 70;
		grid_data.horizontalAlignment = GridData.END;
		grid_data.grabExcessHorizontalSpace = true;
		button_close.setLayoutData(grid_data);
		
		button_close.setFont(skin.getButtonFont());
		button_close.setText(Localizer._("downloadinfowindow.button.close"));
		
		button_close.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e ) {
				for(Refreshable refreshable : tabs) {
					GUIUpdater.getInstance().removeRefreshable(refreshable);
				}
				shell.close();
		}});
		
		shell.setSize(600, 450);
		Utils.centreWindow(shell);
		
		shell.open();
	}


}
