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
package org.jmule.ui.swt.serverlistimportwindow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.servermanager.ServerManager;
import org.jmule.core.servermanager.ServerManagerException;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * Created on Aug 23, 2008
 * @author binary256
 * @version $Revision: 1.3 $
 * Last changed by $Author: binary255 $ on $Date: 2009/09/20 09:05:15 $
 */
public class ServerListImportWindow implements JMuleUIComponent{

	private ServerManager server_manager;
	private Shell shell;
	
	private Button browse_button,ok_button, cancel_button;
	private Text file_path;
	private boolean allow_close = true;
	
	public void getCoreComponents() {
		server_manager = JMuleCoreFactory.getSingleton().getServerManager();
	}

	public void initUIComponents() {
		SWTSkin skin = null;
		try {
		    skin = (SWTSkin)JMuleUIManager.getJMuleUI().getSkin();
		}catch(Throwable t) {}
		
		final Shell shell1=new Shell(SWTThread.getDisplay(),SWT.ON_TOP);
		shell=new Shell(shell1,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		shell.setSize(400, 130);
		Utils.centreWindow(shell);
		shell.setText(_._("serverlistimportwindow.title"));
		shell.setImage(SWTImageRepository.getImage("import.png"));
		
		
		shell.setLayout(new GridLayout(1,false));
		
		Group group = new Group(shell,SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setLayout(new GridLayout(3,false));
		
		Label label = new Label(group,SWT.NONE);
		label.setText(_._("serverlistimportwindow.label.file_path")+" : ");
		
		file_path = new Text(group,SWT.BORDER );
		file_path.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		browse_button = new Button(group,SWT.NONE);
		browse_button.setFont(skin.getButtonFont());
		browse_button.setText(_._("serverlistimportwindow.button.browse"));
		
		browse_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				FileDialog dialog = new FileDialog (shell, SWT.OPEN);
				dialog.setFilterNames (new String [] {"Server.met(*.met)", "All Files (*.*)"});
				dialog.setFilterExtensions (new String [] {"*.met", "*.*"});
				String result = dialog.open();
				if (result != null)
					file_path.setText(result);
		} });
		
		
		Composite button_bar = new Composite(shell,SWT.NONE);
		button_bar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		GridLayout layout = new GridLayout(2,false);
		button_bar.setLayout(layout);
		
		ok_button = new Button(button_bar,SWT.NONE);
		ok_button.setFont(skin.getButtonFont());
		ok_button.setText(_._("serverlistimportwindow.button.ok"));
		ok_button.setImage(SWTImageRepository.getImage("accept.png"));
		GridData grid_data = new GridData();
		grid_data.horizontalAlignment = GridData.END;
		grid_data.widthHint = 60;
		grid_data.grabExcessHorizontalSpace = true;
		ok_button.setLayoutData(grid_data);
		ok_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				importServerList();
		} });
		
		
		cancel_button = new Button(button_bar,SWT.NONE);
		cancel_button.setFont(skin.getButtonFont());
		cancel_button.setText(_._("serverlistimportwindow.button.cancel"));
		cancel_button.setImage(SWTImageRepository.getImage("cancel2.png"));
		grid_data = new GridData();
		grid_data.widthHint = 80;
		cancel_button.setLayoutData(grid_data);
		
		cancel_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				shell.close();
		} });
		
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event arg0) {
				arg0.doit = allow_close;
			}
		});
		
		shell.open();
	}
	
	private void importServerList() {
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				setWindowStatus(false);
				try {
					server_manager.importList(file_path.getText());
				} catch (ServerManagerException e) {
					Utils.showWarningMessage(shell, _._("serverlistimportwindow.import_failed_title"), 
							_._("serverlistimportwindow.import_failed")+"\n"+e.getLocalizedMessage());
				}
				setWindowStatus(true);
				shell.close();
			}
		});
	}

	private void setWindowStatus(boolean status) {
		allow_close = status;
		browse_button.setEnabled(status);
		ok_button.setEnabled(status);
		cancel_button.setEnabled(status);
		file_path.setEnabled(status);
	}
	
}
