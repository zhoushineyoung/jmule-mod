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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.servermanager.ServerManagerException;
import org.jmule.core.utils.AddressUtils;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.skin.SkinConstants;
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
public class ServerAddWindow implements JMuleUIComponent {

	private Shell shell;
	private JMuleCore _core = JMuleCoreFactory.getSingleton();
	public ServerAddWindow() {
		
	}

	public void getCoreComponents() {
		
	}

	public void initUIComponents() {
		String title = Localizer._("serveraddwindow.title");
		SWTSkin skin = null;
		try {
			
		    skin = (SWTSkin)JMuleUIManager.getJMuleUI().getSkin();
		
		}catch(Throwable t) {}
		
		Display display = SWTThread.getDisplay();
		
		final Shell shell1=new Shell(display,SWT.ON_TOP);
		shell=new Shell(shell1,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		shell.setImage(SWTImageRepository.getImage("server_add.png"));
		shell.setText(title);
		
		Utils.centreWindow(shell);
		
		shell.setLayout(new GridLayout(1,true));
		
		Composite content = new Composite(shell,SWT.BORDER);
		content.setLayoutData(new GridData(GridData.FILL_BOTH));
		content.setLayout(new GridLayout(2,false));
		
		Label label = new Label(content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("serveraddwindow.server_ip_address")+" : ");
		
		final Text text_server_ip = new Text(content,SWT.BORDER);
		text_server_ip.setFont(skin.getDefaultFont());
		text_server_ip.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		text_server_ip.addListener(SWT.Verify,new Listener() {
			public void handleEvent(Event e) {
		        String text = e.text;
		        char[] chars = new char[text.length()];
		        text.getChars(0, chars.length, chars, 0);
		        for (int i = 0; i < chars.length; i++) {
		          if (!('0' <= chars[i] && chars[i] <= '9'))
		        	  if (chars[i]!='.') {
		        	  
			            e.doit = false;
			            return;
		          }
				}
			}
		});
		
		label = new Label(content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("serveraddwindow.server_port")+" : ");
		
		final Text text_port = new Text(content,SWT.BORDER);
		text_port.setFont(skin.getDefaultFont());
		text_port.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		text_port.addListener(SWT.Verify,new Listener() {
			public void handleEvent(Event e) {
		        String text = e.text;
		        char[] chars = new char[text.length()];
		        text.getChars(0, chars.length, chars, 0);
		        for (int i = 0; i < chars.length; i++) {
		          if (!('0' <= chars[i] && chars[i] <= '9')) {
			            e.doit = false;
			            return;
		          }
				}
			}
		});
		
		Composite buttons_composite = new Composite(shell,SWT.NONE);
		buttons_composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		buttons_composite.setLayout(new GridLayout(2,false));
		
		Button button_ok = new Button(buttons_composite,SWT.NONE);
		GridData grid_data = new GridData();
		//grid_data.widthHint = 40;
		grid_data.horizontalAlignment = GridData.END;
		grid_data.grabExcessHorizontalSpace = true;
		button_ok.setLayoutData(grid_data);
		
		button_ok.setFont(skin.getButtonFont());
		button_ok.setImage(skin.getButtonImage(SkinConstants.OK_BUTTON_IMAGE));
		button_ok.setText(Localizer._("mainwindow.button.ok"));
		button_ok.addSelectionListener(new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent arg0) {
				String server_ip = text_server_ip.getText();
				if (!AddressUtils.isValidIP(server_ip)) {
					MessageBox dialog = new MessageBox(shell,
			                SWT.OK | SWT.ICON_WARNING);
					dialog.setMessage(Localizer._("serveraddwindow.wrong_server_address"));
					dialog.open();
					return ;
				}
				
				if (!AddressUtils.isValidPort(text_port.getText())) {
					MessageBox dialog = new MessageBox(shell,
					SWT.OK | SWT.ICON_WARNING);
					dialog.setMessage(Localizer._("serveraddwindow.wrong_server_port"));
					dialog.open();
					return ;
				}
				int server_port = Integer.parseInt(text_port.getText());
				try {
					_core.getServerManager().newServer(server_ip, server_port);
				} catch (ServerManagerException e) {
					Utils.showWarningMessage(shell, "", e.getMessage());
				}
				
				shell.close();
		} });
		
		Button button_cancel = new Button(buttons_composite,SWT.NONE);
		button_cancel.setImage(skin.getButtonImage(SkinConstants.CANCEL_BUTTON_IMAGE));
		button_cancel.setText(Localizer._("mainwindow.button.cancel"));
		
		button_cancel.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent arg0) {
				shell.close();
				
			}
			
		});
		
		shell.setSize(300, 175);
		shell.open();
	}
	
}
