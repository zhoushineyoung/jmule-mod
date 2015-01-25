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
package org.jmule.ui.swt.updaterwindow;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jmule.core.JMConstants;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMThread;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.skin.SWTSkin;
import org.jmule.updater.JMUpdater;
import org.jmule.updater.JMUpdaterException;

/**
 * Created on Aug 23, 2008
 * @author binary256
 * @version $Revision: 1.8 $
 * Last changed by $Author: binary255 $ on $Date: 2010/01/11 17:07:06 $
 */
public class UpdaterWindow implements JMuleUIComponent {

	public static final int UPDATE_INTERVAL		= 10000;
	
	private enum WindowStatus { CONNECTING, CONNECTED, FAILED};
	
	private JMUpdater updater = JMUpdater.getInstance();

	private Shell shell;
	private Label available_version, last_update, user_version;
	private StyledText changelog_text;
	private CLabel download_link;
	
	private Color green_color = new Color(SWTThread.getDisplay(),68,174,71);
	private Color red_color = SWTThread.getDisplay().getSystemColor(SWT.COLOR_RED);
	private Color connecting_color = SWTThread.getDisplay().getSystemColor(SWT.COLOR_MAGENTA);
	
	public void getCoreComponents() {
	}

	public void initUIComponents() {
		SWTSkin skin = null;
		try {
		    skin = (SWTSkin)JMuleUIManager.getJMuleUI().getSkin();
		}catch(Throwable t) { }
		
		final Shell shell1=new Shell(SWTThread.getDisplay(),SWT.ON_TOP);
		shell=new Shell(shell1,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		shell.setSize(400, 330);
		Utils.centreWindow(shell);
		
		shell.setImage(SWTImageRepository.getImage("updater.png"));
		shell.setText(_._("updaterwindow.title"));

		shell.setLayout(new GridLayout(1,false));
		
		Label label;
		GridData grid_data;
		GridLayout layout;
		
		Composite window_content = new Composite(shell,SWT.NONE);
		layout = new GridLayout(2,false);
		layout.marginLeft = 50;
		layout.marginBottom = 0;
		window_content.setLayout(layout);
		window_content.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		label = new Label(window_content,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setForeground(skin.getDefaultColor());
		label.setText(JMConstants.JMULE_NAME + " " + _._("updaterwindow.label.jmule_version") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		user_version = new Label(window_content,SWT.NONE);
		FontData data = skin.getDefaultFont().getFontData()[0];
		Font font = new Font(SWTThread.getDisplay(),data.getName(),data.getHeight(),SWT.BOLD );
		user_version.setFont(font);
		user_version.setText(JMConstants.CURRENT_JMULE_VERSION);
		user_version.setForeground(green_color);
		
		label = new Label(window_content,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setText(_._("updaterwindow.label.available_version") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		available_version = new Label(window_content,SWT.NONE);
		available_version.setFont(font);
		available_version.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		available_version.setForeground(green_color);
		
		label = new Label(window_content,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setForeground(skin.getDefaultColor());
		label.setText(_._("updaterwindow.label.last_update") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		last_update = new Label(window_content,SWT.NONE);
		last_update.setFont(skin.getDefaultFont());
		last_update.setForeground(skin.getDefaultColor());
		last_update.setForeground(skin.getDefaultColor());
		last_update.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		
		download_link = new CLabel(shell,SWT.NONE);
		grid_data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		grid_data.horizontalSpan = 2;
		download_link.setLayoutData(grid_data);
		download_link.setText(_._("updaterwindow.label.download_link"));
		download_link.setData(JMConstants.JMULE_DOWNLOAD_PAGE);
		download_link.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		download_link.setCursor(new Cursor(SWTThread.getDisplay(),SWT.CURSOR_HAND));
		download_link.addMouseListener(new MouseAdapter() {	
			public void mouseDoubleClick(MouseEvent arg0) {
				String path = (String) ((CLabel) arg0.widget).getData();
				if (!Utils.launchProgram(path)) 
					Utils.showWarningMessage(shell, _._("updaterwindow.error_open_url.title")
							, Localizer._("updaterwindow.error_open_url",path));
			}
			public void mouseDown(MouseEvent arg0) {
				String path = (String) ((CLabel) arg0.widget).getData();
				if (!Utils.launchProgram(path)) 
					Utils.showWarningMessage(shell, _._("updaterwindow.error_open_url.title")
							, Localizer._("updaterwindow.error_open_url",path));
			}
		});
		
		download_link.setVisible(false);
		
		Group changelog_group = new Group(shell,SWT.NONE);
		changelog_group.setLayout(new FillLayout());
		changelog_group.setLayoutData(new GridData(GridData.FILL_BOTH));
		changelog_group.setText(_._("updaterwindow.group.changelog"));
		
		changelog_text = new StyledText(changelog_group,SWT.H_SCROLL | SWT.V_SCROLL);
		changelog_text.setEditable(false);
		Font font_changelog = new Font(SWTThread.getDisplay(),"Courier",10,SWT.NONE );
		changelog_text.setFont(font_changelog);
		
		Composite button_bar = new Composite(shell,SWT.NONE);
		button_bar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		layout = new GridLayout(3,false);
		button_bar.setLayout(layout);
		
		final Button button_startup_check = new Button(button_bar,SWT.CHECK);
		grid_data = new GridData();
		grid_data.horizontalSpan = 2;
		grid_data.horizontalAlignment = GridData.BEGINNING;
		button_startup_check.setLayoutData(grid_data);
		button_startup_check.setText(_._("updaterwindow.check.startup_check"));
		button_startup_check.setSelection(SWTPreferences.getInstance().updateCheckAtStartup());
		
		Button button_ok = new Button(button_bar,SWT.NONE);
		button_ok.setFont(skin.getButtonFont());
		button_ok.setText(_._("updaterwindow.button.ok"));
		button_ok.setImage(SWTImageRepository.getImage("ok.png"));
		grid_data = new GridData();
		grid_data.horizontalAlignment = GridData.END;
		grid_data.widthHint = 60;
		grid_data.grabExcessHorizontalSpace = true;
		button_ok.setLayoutData(grid_data);
		button_ok.forceFocus();
		button_ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				SWTPreferences.getInstance().setUpdateCheckAtStartup(button_startup_check.getSelection());
				shell.close();
			}
		});
		
		shell.open();
		
		
		long update_time = updater.getLastUpdateTime();
		
		if ((System.currentTimeMillis() - update_time) <= UPDATE_INTERVAL) {
			setWindowStatus(WindowStatus.CONNECTED);
			return ;
		}
		
		setWindowStatus(WindowStatus.CONNECTING);
		
		new JMThread(new JMRunnable() {
			public void JMRun() {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						setWindowStatus(WindowStatus.CONNECTING);
					}});
				try {
					updater.checkForUpdates();
				}catch(JMUpdaterException e) {
					SWTThread.getDisplay().asyncExec(new JMRunnable() {
						public void JMRun() {
							Utils.showWarningMessage(shell, _._("updaterwindow.connect_failed_title"), _._("updaterwindow.connect_failed")+"\n"+updater.getErrorCode());
							setWindowStatus(WindowStatus.FAILED);
						}});
					return ;
				}
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						setWindowStatus(WindowStatus.CONNECTED);
					}});
			}
		}).start();
		
	}

	private void setWindowStatus(WindowStatus status) {
		if (status == WindowStatus.CONNECTING) {
			changelog_text.setText("");
			available_version.setText(_._("updaterwindow.label.connecting"));
			available_version.setForeground(connecting_color);
			last_update.setText("");
			download_link.setVisible(false);
			user_version.setForeground(green_color);
			return ;
		}
		if (status == WindowStatus.FAILED) {
			changelog_text.setText("");
			available_version.setText(_._("updaterwindow.label.failed"));
			available_version.setForeground(red_color);
			last_update.setText("");
			download_link.setVisible(false);
			user_version.setForeground(green_color);
			return ;
		}
		
		changelog_text.setText(updater.getChangeLog());
		available_version.setText(updater.getVersion());
		available_version.setForeground(green_color);
		
		long check_time = updater.getLastUpdateTime();
		if (check_time != 0) {
			Calendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(check_time);
			String upate_date = format(calendar.get(Calendar.DAY_OF_MONTH))
					+ "." + format(calendar.get(Calendar.MONTH) + 1) + "."
					+ format(calendar.get(Calendar.YEAR));
			upate_date += "  " + format(calendar.get(Calendar.HOUR_OF_DAY))
					+ ":" + format(calendar.get(Calendar.MINUTE)) + ":"
					+ format(calendar.get(Calendar.SECOND));
			last_update.setText(upate_date);
		}
		
		if (updater.isNewVersionAvailable()) {
			user_version.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_RED));
			download_link.setVisible(true);
		} else {
			user_version.setForeground(green_color);
			download_link.setVisible(false);
		}
		
	}
	
	private String format(int value) {
		return value<10 ? "0"+value : value+"";
	}
	
}
