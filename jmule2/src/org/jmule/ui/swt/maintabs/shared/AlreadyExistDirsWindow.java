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
package org.jmule.ui.swt.maintabs.shared;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * Created on Aug 28, 2008
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/10/02 06:11:28 $
 */
public class AlreadyExistDirsWindow implements JMuleUIComponent {

	private java.util.List<File> list;
	private Shell shell;
	
	public AlreadyExistDirsWindow(java.util.List<File> fileList) {
		list = fileList;
	}
	
	public void getCoreComponents() {
		
	}

	public void initUIComponents() {
		SWTSkin skin = null;
		try {
			
		    skin = (SWTSkin) JMuleUIManager.getJMuleUI().getSkin();
		
		}catch(Throwable t) {}
		
		final Shell shell1=new Shell(SWTThread.getDisplay(),SWT.ON_TOP);
		shell=new Shell(shell1,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		
		shell.setText(_._("alreadyexistdirswindow.title"));
		
		shell.setLayout(new GridLayout(1,false));
		
		Label info = new Label(shell,SWT.NONE);
		info.setFont(skin.getLabelFont());
		info.setText(_._("alreadyexistdirswindow.label.info"));
		
		List file_list = new List(shell,SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		file_list.setLayoutData(new GridData(GridData.FILL_BOTH));
		for(File file : list) 
			file_list.add(file.getAbsolutePath());
		
		Button button_ok = new Button(shell,SWT.NONE);
		button_ok.setText(_._("alreadyexistdirswindow.button.ok"));
		button_ok.setImage(SWTImageRepository.getImage("ok.png"));
		GridData data = new GridData();
		data.horizontalAlignment = GridData.CENTER;
		data.widthHint = 60;
		button_ok.setLayoutData(data);
		button_ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
		}});
		
		shell.setSize(300,400);
		Utils.centreWindow(shell);
		shell.open();
	}

}
