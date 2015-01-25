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
package org.jmule.ui.swt.tab.main.shared;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.jmule.core.sharingmanager.FileQuality;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.GUIUpdater;
import org.jmule.ui.swt.Refreshable;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.skin.SWTSkin;
import org.jmule.ui.utils.FileFormatter;

/**
 * Created on Aug 18, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2011/03/27 16:51:29 $
 */
public class SharedFilePropertiesWindow implements JMuleUIComponent {

	private Shell shell;
	private SharedFile shared_file;
	private Label completed;
	private Refreshable refresher = null;
	private Combo file_quality_combo;
	
	public SharedFilePropertiesWindow(SharedFile sharedFile) {
		shared_file = sharedFile;	
	}
	
	public void getCoreComponents() {
		SWTSkin skin = null;
		try {
		    skin = (SWTSkin) JMuleUIManager.getJMuleUI().getSkin();
		}catch(Throwable t) {
		}
		
		final Shell shell1=new Shell(SWTThread.getDisplay(),SWT.ON_TOP);
		shell=new Shell(shell1,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		
		shell.setText(_._("sharedfilepropertieswindow.title"));
		shell.setImage(SWTImageRepository.getImage("info.png"));
		
		shell.setSize(500, 310);
		
		Utils.centreWindow(shell);
		
		shell.setLayout(new GridLayout(1,false));
		Composite shared_file_fields = new Composite(shell,SWT.BORDER);
		shared_file_fields.setLayout(new GridLayout(2,false));
		shared_file_fields.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.FILL_BOTH));

		Label label;
		
		label = new Label(shared_file_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("sharedfilepropertieswindow.label.filename")+" : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		label = new Label(shared_file_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(shared_file.getSharingName());
		
		label = new Label(shared_file_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("sharedfilepropertieswindow.label.filesize")+" : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		label = new Label(shared_file_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(FileFormatter.formatFileSize(shared_file.length()));
		
		label = new Label(shared_file_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("sharedfilepropertieswindow.label.filepath")+" : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		label = new Label(shared_file_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(shared_file.getAbsolutePath());
		label.setToolTipText(shared_file.getAbsolutePath());
		
		label = new Label(shared_file_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("sharedfilepropertieswindow.label.filestype")+" : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		label = new Label(shared_file_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(FileFormatter.formatMimeType(shared_file.getMimeType()));
		
		label = new Label(shared_file_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("sharedfilepropertieswindow.label.file_hash")+" : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		label = new Label(shared_file_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(shared_file.getFileHash().getAsString());
		label.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		
		label = new Label(shared_file_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("sharedfilepropertieswindow.label.completed")+" : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
				
		completed = new Label(shared_file_fields,SWT.NONE);
		completed.setFont(skin.getLabelFont());
		completed.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (shared_file instanceof PartialFile) {
			refresher = new Refreshable(){

				public void refresh() {
					PartialFile p_file = (PartialFile) shared_file;
					completed.setText(FileFormatter.formatProgress(p_file.getPercentCompleted()));
				}
				
			};
			GUIUpdater.getInstance().addRefreshable(refresher);
		}else
			completed.setText("100%");
		
		label = new Label(shared_file_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("sharedfilepropertieswindow.label.ed2k_link") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		label = new Label(shared_file_fields,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(shared_file.getED2KLink().getAsString());
		label.setToolTipText(_._("sharedfilepropertieswindow.label.ed2k_link.tooltip"));
		label.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		label.setCursor(new Cursor(SWTThread.getDisplay(),SWT.CURSOR_HAND));
		label.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event arg0) {
				Utils.setClipBoardText(shared_file.getED2KLink().getAsString());
			}
		});
		
		label = new Label(shared_file_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("sharedfilepropertieswindow.label.file_quality") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		
		file_quality_combo = new Combo(shared_file_fields,SWT.READ_ONLY);
		
		if (!shared_file.isCompleted())
			file_quality_combo.setEnabled(false);
		file_quality_combo.setFont(skin.getDefaultFont());
		file_quality_combo.add(_._("sharedfilepropertieswindow.label.fq_not_rated"));
		file_quality_combo.setData(_._("sharedfilepropertieswindow.label.fq_not_rated"), FileQuality.NOTRATED);
		
		file_quality_combo.add(_._("sharedfilepropertieswindow.label.fq_fake"));
		file_quality_combo.setData(_._("sharedfilepropertieswindow.label.fq_fake"), FileQuality.FAKE);
		
		file_quality_combo.add(_._("sharedfilepropertieswindow.label.fq_poor"));
		file_quality_combo.setData(_._("sharedfilepropertieswindow.label.fq_poor"), FileQuality.POOR);
		
		file_quality_combo.add(_._("sharedfilepropertieswindow.label.fq_fair"));
		file_quality_combo.setData(_._("sharedfilepropertieswindow.label.fq_fair"), FileQuality.FAIR);
		
		file_quality_combo.add(_._("sharedfilepropertieswindow.label.fq_good"));
		file_quality_combo.setData(_._("sharedfilepropertieswindow.label.fq_good"), FileQuality.GOOD);
		
		file_quality_combo.add(_._("sharedfilepropertieswindow.label.fq_excellent"));
		file_quality_combo.setData(_._("sharedfilepropertieswindow.label.fq_excellent"), FileQuality.EXCELLENT);
		
		switch(shared_file.getFileQuality()) {
			case FAKE : file_quality_combo.select(1); break;
			case POOR : file_quality_combo.select(2); break;
			case FAIR : file_quality_combo.select(3); break;
			case GOOD : file_quality_combo.select(4); break;
			case EXCELLENT : file_quality_combo.select(5); break;
			default : 	file_quality_combo.select(0); break; 
		}
		
		Composite button_bar = new Composite(shell,SWT.NONE);
		button_bar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		GridLayout layout = new GridLayout(2,false);
		button_bar.setLayout(layout);

		Button button_ok = new Button(button_bar,SWT.NONE);
		button_ok.setFont(skin.getButtonFont());
		button_ok.setText(_._("sharedfilepropertieswindow.button.ok"));
		button_ok.setImage(skin.getButtonImage(SWTSkin.OK_BUTTON_IMAGE));
		
		Button button_cancel = new Button(button_bar,SWT.NONE);
		button_cancel.setFont(skin.getButtonFont());
		button_cancel.setText(_._("sharedfilepropertieswindow.button.cancel"));
		button_cancel.setImage(skin.getButtonImage(SWTSkin.CANCEL_BUTTON_IMAGE));
		
		GridData grid_data = new GridData();
		grid_data.horizontalAlignment = GridData.END;
		grid_data.widthHint = 60;
		grid_data.grabExcessHorizontalSpace = true;
		button_ok.setLayoutData(grid_data);
		button_ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				String selection = file_quality_combo.getItem(file_quality_combo.getSelectionIndex());
				FileQuality quality = (FileQuality) file_quality_combo.getData(selection);
				shared_file.setFileQuality(quality);
				remove_refreshable();
				shell.close();
			}
		});
		
		button_cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				remove_refreshable();
				shell.close();
			}
		});
	}

	public void initUIComponents() {
		shell.open();
	}

	private void remove_refreshable() {
		if (refresher != null) {
			GUIUpdater.getInstance().removeRefreshable(refresher);
		}
	}
}
