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
package org.jmule.ui.swt.tab.main.logs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jmule.core.JMRunnable;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.Logger;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.tab.MainTab;
import org.jmule.ui.utils.TimeFormatter;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2011/03/27 16:51:29 $$
 */
public class LogsTab extends MainTab implements Logger {

	private StyledText log_content;
	
	private static final Color fine_event_color    = SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLACK);
	private static final Color warning_event_color = SWTThread.getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA);
	private static final Color error_event_color   = SWTThread.getDisplay().getSystemColor(SWT.COLOR_RED);
	
	private static final Color date_foreground 	   = new Color(SWTThread.getDisplay(),0,0,168);
	
	private Menu popup_menu;
	public LogsTab(Composite ctab) {
		super(ctab,"Logs");
		
		tab_content.setLayout(new FillLayout());
		
		log_content = new StyledText(tab_content, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.READ_ONLY);
		log_content.setEditable(false);
		log_content.setBackground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		popup_menu = new Menu(log_content);
		
		
		final MenuItem copy_item = new MenuItem(popup_menu,SWT.PUSH);
		copy_item.setText(_._("mainwindow.logtab.popupmenu.copy"));
		copy_item.setImage(SWTImageRepository.getImage("copy.png"));
		
		copy_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Utils.setClipBoardText(log_content.getSelectionText());
			}
		});
		
		final MenuItem save_item = new MenuItem(popup_menu,SWT.PUSH);
		save_item.setText(_._("mainwindow.logtab.popupmenu.save"));
		save_item.setImage(SWTImageRepository.getImage("save.png"));
		save_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog (tab_content.getShell(), SWT.SAVE);
				dialog.setFilterNames (new String [] {"All Files (*.*)"});
				dialog.setFilterExtensions (new String [] {"*.*"});
				dialog.open();
				String filename = dialog.getFileName();
				if (filename == null) return ;
				final String full_path = (dialog.getFilterPath() + File.separator + filename);
				final String content = log_content.getText();
				new Thread( new JMRunnable() {
					public void JMRun() {
						try {
							FileWriter file_writer = new FileWriter(full_path);
							file_writer.write(content);
							file_writer.close();
						} catch (IOException e) {
						}
					}}).start();
			}
		});
		
		final MenuItem select_all_item = new MenuItem(popup_menu,SWT.PUSH);
		select_all_item.setText(_._("mainwindow.logtab.popupmenu.select_all"));
		select_all_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				log_content.selectAll();
			}
		});
		
		new MenuItem(popup_menu,SWT.SEPARATOR);
		
		final MenuItem clear_item = new MenuItem(popup_menu,SWT.PUSH);
		clear_item.setText(_._("mainwindow.logtab.popupmenu.clear"));
		clear_item.setImage(SWTImageRepository.getImage("remove_all.png"));
		clear_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				log_content.setText("");
			}
		});
		
		log_content.setMenu(popup_menu);
		
		log_content.addMenuDetectListener(new MenuDetectListener() {

			public void menuDetected(MenuDetectEvent arg0) {
				copy_item.setEnabled(true);
				if (log_content.getSelectionCount()==0)
					copy_item.setEnabled(false);
				
				clear_item.setEnabled(true);
				select_all_item.setEnabled(true);
				save_item.setEnabled(true);
				
				if (log_content.getText().length()==0) {
					clear_item.setEnabled(false);
					select_all_item.setEnabled(false);
					save_item.setEnabled(false);
				}
				
			}
			
		});
	
	}
	
	public void fine(String message) {
		log(System.currentTimeMillis(),fine_event_color,message);
	}
	
	public void warning(String message) {
		log(System.currentTimeMillis(),warning_event_color,message);
	}
	
	public void error(String message) {
		log(System.currentTimeMillis(),error_event_color,message);
	}
	
	private void log(final long time,final Color foreground, final String text) {
		// syncExec - we need sequentially logging
		if (log_content.isDisposed()) return;
		SWTThread.getDisplay().syncExec(new Runnable() {
			public void run() {
				Calendar calendar = new GregorianCalendar();
				calendar.setTimeInMillis(time);
				int date_begin = log_content.getText().length();
				String date = TimeFormatter.twoDigits(calendar
						.get(Calendar.DAY_OF_MONTH))
						+ "."
						+ TimeFormatter
								.twoDigits(calendar.get(Calendar.MONTH) + 1)
						+ "."
						+ TimeFormatter.twoDigits(calendar.get(Calendar.YEAR));
				date += " "
						+ TimeFormatter.twoDigits(calendar
								.get(Calendar.HOUR_OF_DAY))
						+ ":"
						+ TimeFormatter
								.twoDigits(calendar.get(Calendar.MINUTE))
						+ ":"
						+ TimeFormatter
								.twoDigits(calendar.get(Calendar.SECOND));
				date = "[" + date + "]";
				log_content.append(date);
								
				StyleRange date_style = new StyleRange();
				date_style.start = date_begin;
				date_style.length = date.length();
				date_style.foreground = date_foreground;
				log_content.setStyleRange(date_style);
				
				log_content.append("  ");
				int style_begin = log_content.getText().length();
				log_content.append(text);
				log_content.append(System.getProperty("line.separator"));
		
				StyleRange style = new StyleRange();
				style.start = style_begin;
				style.length = text.length();
				style.foreground = foreground;
				log_content.setStyleRange(style);
				
				log_content.setSelection(log_content.getText().length());
			}
		});
	}

	public JMULE_TABS getTabType() {
		
		return JMULE_TABS.LOGS;
	}
	
	public void lostFocus() {
	}

	public void obtainFocus() {
	}

}
