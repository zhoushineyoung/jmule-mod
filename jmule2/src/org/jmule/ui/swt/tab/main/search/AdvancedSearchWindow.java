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
package org.jmule.ui.swt.tab.main.search;


import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.jmule.core.sharingmanager.FileType;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer._;
import org.jmule.ui.skin.Skin;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.common.ExSlider;
import org.jmule.ui.swt.common.ExSliderModifyListener;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * Created on Nov 15, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2011/03/27 16:51:29 $
 */
public abstract class AdvancedSearchWindow implements JMuleUIComponent{
	private Shell shell;
	private ExSlider file_size_slider;
	private Text min_file_size, max_file_size, file_extension;
	private Spinner av_spinner, cmp_src_spinner;
	private Combo file_type;
	private Combo min_file_size_units, max_file_size_units;
	
	private String initial_extension = "";
	private int initial_file_type = 0;
	private int initial_av_src = 0;
	private int initial_cmp_src = 0;
	private int initial_min_size_units = 0;
	private int initial_max_size_units = 0;
	private double max = 1024;
	private double min = 0;
	
	private boolean updated_file_size = false;
	
	FileType file_types[] = new FileType[]{FileType.ANY,
			FileType.ARHIVE,
			FileType.AUDIO,
			FileType.CDIMAGE,
			FileType.DOCUMENT,
			FileType.PICTURE,
			FileType.PROGRAM,
			FileType.VIDEO};
	
	public AdvancedSearchWindow() {
		getCoreComponents();
		initUIComponents();
	}
	
	public AdvancedSearchWindow(long minFileSize,long maxFileSize,FileType fileType,String extension,long availableSources, long completeSources) {
		boolean is_updated = false;
		if (minFileSize !=0)
			is_updated = true;
		if (maxFileSize != 1024)
			is_updated = true;
		
		if (minFileSize == 0)
			if (maxFileSize == 0 ) {
				minFileSize = 0;
				maxFileSize = 1024;
			}
		
		initial_min_size_units = getUnit(minFileSize == 0 ? minFileSize : minFileSize - 1 );
		
		if (initial_min_size_units!= 0)
			min = minFileSize / Math.pow(1024, initial_min_size_units);
		else
			min = minFileSize;
		
		initial_max_size_units = getUnit(maxFileSize == 0 ? maxFileSize : maxFileSize - 1);
		
		if (initial_max_size_units!=0)
			max = maxFileSize / Math.pow(1024, initial_max_size_units);
		else
			max = maxFileSize;
		
		switch(fileType) {
			case ANY 	  : initial_file_type = 0; break;
			case ARHIVE	  : initial_file_type = 1; break;
			case AUDIO	  : initial_file_type = 2; break;
			case CDIMAGE  : initial_file_type = 3; break;
			case DOCUMENT : initial_file_type = 4; break;
			case PICTURE  : initial_file_type = 5; break;
			case PROGRAM  : initial_file_type = 6; break;
			case VIDEO    : initial_file_type = 7; break;
		}
		
		initial_extension = extension;
		initial_av_src = (int) availableSources;
		initial_cmp_src = (int) completeSources;
		
		if (is_updated) updated_file_size = true;
		getCoreComponents();
		initUIComponents();	
	}
	
	public void getCoreComponents() {
		
	}

	private double formatSize(double percent, long unit) {
		double result = 0;
		
		result = (percent * unit) /  100D;		
		return result;
	}
	
	private int getUnit(long fileSize) {
		int unit = 0;
		if (fileSize == 0) return 0;
		while(true) {
			if (fileSize >= Math.pow(1024, unit))
				if (fileSize < Math.pow(1024, unit+1))
					return unit;
			unit++;
		}
	}
	
	public void initUIComponents() {
		GridLayout layout;
		GridData layout_data;
		Label label;
		SWTSkin skin = null;
		try {
			
		    skin = (SWTSkin)JMuleUIManager.getJMuleUI().getSkin();
		
		}catch(Throwable t) {}
		
		Display display = SWTThread.getDisplay();
		
		final Shell shell1=new Shell(display,SWT.ON_TOP);
		shell=new Shell(shell1,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		shell.setText(_._("advancedsearchwindow.title"));
		
		shell.setSize(500,300);

		layout = new GridLayout(1,false);
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		shell.setLayout(layout);
		
		Composite file_size_options = new Composite(shell,SWT.NONE);
		layout = new GridLayout(7,false);
		layout.marginHeight=0;
		layout.marginWidth=0;
		file_size_options.setLayout(layout);
		
		layout_data = new GridData();
		layout_data.grabExcessHorizontalSpace = true;
		layout_data.horizontalAlignment = GridData.FILL;
		file_size_options.setLayoutData(layout_data);
		
		label = new Label(file_size_options,SWT.NONE);
		label.setText(_._("advancedsearchwindow.label.min_size") + " : ");
		
		min_file_size = new Text(file_size_options,SWT.BORDER);
		min_file_size.addVerifyListener(new VerifyListener() {			
			public void verifyText(VerifyEvent e) {
				try {
					String str = min_file_size.getText().substring(0, e.start)+e.text+min_file_size.getText().substring(e.end);
					double value = Double.parseDouble(str);
					double x = (100 * value) / 1024D;
					if (x>100) { 
						e.doit = false;
						return ;
					}
					long min_unit = (Long)min_file_size_units.getData(min_file_size_units.getText());
					long max_unit = (Long)max_file_size_units.getData(max_file_size_units.getText());
					double min = formatSize(x, min_unit);
					double max = formatSize(file_size_slider.getUpValue(), max_unit);
					if (min>max) { e.doit = false; return ;}
					e.doit = true;
					file_size_slider.setDownValue(x);				
				}catch(Exception t) {
					e.doit = false;
				}
			}
		});
		
		min_file_size_units = new Combo(file_size_options,SWT.READ_ONLY);
		
		min_file_size_units.add("Bytes");
		min_file_size_units.add("KB");
		min_file_size_units.add("MB");
		min_file_size_units.add("GB");
		
		min_file_size_units.setData("Bytes", 1024L);
		min_file_size_units.setData("KB", 1024 * 1024L);
		min_file_size_units.setData("MB", 1024 * 1024 * 1024L);
		min_file_size_units.setData("GB", 1024 * 1024 * 1024 * 1024L);
		min_file_size_units.select(0);
		
		min_file_size_units.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent arg0) {
				updated_file_size = true;
			}
			
		});
		
		layout_data = new GridData();
		layout_data.grabExcessHorizontalSpace = true;
		layout_data.horizontalAlignment = GridData.FILL;
		new Label(file_size_options,SWT.NONE).setLayoutData(layout_data);
		
		label = new Label(file_size_options,SWT.NONE);
		label.setText(_._("advancedsearchwindow.label.max_size") + " : ");
		
		max_file_size = new Text(file_size_options,SWT.BORDER);
		
		max_file_size.addVerifyListener(new VerifyListener() {			
			public void verifyText(VerifyEvent e) {
				try {
					String str = max_file_size.getText().substring(0, e.start)+e.text+max_file_size.getText().substring(e.end);
					double value = Double.parseDouble(str);
					double x = (100 * value) / 1024D;
					
					if (x>100) { 
						e.doit = false;
						return ;
					}
					
					long min_unit = (Long)min_file_size_units.getData(min_file_size_units.getText());
					long max_unit = (Long)max_file_size_units.getData(max_file_size_units.getText());
					double min = formatSize(file_size_slider.getDownValue(), min_unit);
					double max = formatSize(x, max_unit);
					if (min>max) { e.doit = false; return ;}
					e.doit = true;
					file_size_slider.setUpValue(x);				
				}catch(Exception t) {
					e.doit = false;
				}
			}
		});
		
		max_file_size_units = new Combo(file_size_options,SWT.READ_ONLY);
		max_file_size_units.add("Bytes");
		max_file_size_units.add("KB");
		max_file_size_units.add("MB");
		max_file_size_units.add("GB");
		
		max_file_size_units.setData("Bytes", 1024L);
		max_file_size_units.setData("KB", 1024 * 1024L);
		max_file_size_units.setData("MB", 1024 * 1024 * 1024L);
		max_file_size_units.setData("GB", 1024 * 1024 * 1024 * 1024L);
		max_file_size_units.select(0);
		
		max_file_size_units.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				updated_file_size = true;
			}
		});
		
		Composite slider_composite = new Composite(shell,SWT.NONE);
		layout_data = new GridData();
		layout_data.grabExcessHorizontalSpace = true;
		layout_data.horizontalAlignment = GridData.FILL;
		slider_composite.setLayoutData(layout_data);
		slider_composite.setLayout(new GridLayout(1,true));
		file_size_slider = new ExSlider(slider_composite){
			public boolean validate(double upValue, double downValue) {
				long min_unit = (Long)min_file_size_units.getData(min_file_size_units.getText());
				long max_unit = (Long)max_file_size_units.getData(max_file_size_units.getText());
				double min = formatSize(downValue, min_unit);
				double max = formatSize(upValue, max_unit);
				
				return max>min;
			}
		};
		
		file_size_slider.setBarBackgroundColor(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		file_size_slider.setBarUsedSegmentColor(SWTThread.getDisplay().getSystemColor(SWT.COLOR_RED));
		file_size_slider.setPointerColor(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		
		file_size_slider.setUpValue(0);
		file_size_slider.setUpValue(100);
		
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.heightHint = 30;
		file_size_slider.setLayoutData(layout_data);
		
		file_size_slider.addModifyListener(new ExSliderModifyListener() {

			public void downValueChanged(double downValue) {
				updated_file_size = true;
				DecimalFormat formatter = new DecimalFormat("0.00");
				
				downValue = Math.round(downValue);
				double u = 1024 / 100D;
				
				min_file_size.setText(formatter.format(u * downValue));
				
			}

			public void upValueChanged(double upValue) {
				updated_file_size = true;
				DecimalFormat formatter = new DecimalFormat("0.00");
				
				upValue = Math.round(upValue);
				double u = 1024 / 100D;
				
				max_file_size.setText(formatter.format(u * upValue));
			}
		});
				
		Composite options_composite = new Composite(shell,SWT.NONE);
		layout_data = new GridData();
		layout_data.grabExcessHorizontalSpace = true;
		layout_data.horizontalAlignment = GridData.FILL;
		options_composite.setLayoutData(layout_data);
		options_composite.setLayout(new GridLayout(4,false));
		
		label = new Label(options_composite,SWT.NONE);
		label.setText("File type : ");
		layout_data = new GridData();
		layout_data.horizontalAlignment = SWT.RIGHT;
		label.setLayoutData(layout_data);
		
		file_type = new Combo(options_composite,SWT.READ_ONLY);
		// must be same order as filetypes
		file_type.add(_._("advancedsearchwindow.file_type.any"));
		file_type.add(_._("advancedsearchwindow.file_type.archive"));
		file_type.add(_._("advancedsearchwindow.file_type.audio"));
		file_type.add(_._("advancedsearchwindow.file_type.cd_image"));
		file_type.add(_._("advancedsearchwindow.file_type.document"));
		file_type.add(_._("advancedsearchwindow.file_type.pictures"));
		file_type.add(_._("advancedsearchwindow.file_type.program"));
		file_type.add(_._("advancedsearchwindow.file_type.video"));
		
		file_type.select(0);
		file_type.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout_data = new GridData();
		file_type.setLayoutData(layout_data);
				
		label = new Label(options_composite,SWT.NONE);
		label.setText(_._("advancedsearchwindow.label.extension") + " : ");
		layout_data = new GridData();
		layout_data.horizontalAlignment = SWT.RIGHT;
		label.setLayoutData(layout_data);
		
		file_extension = new Text(options_composite,SWT.BORDER);
		
		label = new Label(options_composite,SWT.NONE);
		label.setText(_._("advancedsearchwindow.label.availability") + " : ");
		layout_data = new GridData();
		layout_data.horizontalAlignment = SWT.RIGHT;
		label.setLayoutData(layout_data);
		
		av_spinner = new Spinner(options_composite,SWT.BORDER);
		av_spinner.setMaximum(1000);
		
		label = new Label(options_composite,SWT.NONE);
		label.setText(_._("advancedsearchwindow.label.complete_sources") + " : ");
		layout_data = new GridData();
		layout_data.horizontalAlignment = SWT.RIGHT;
		label.setLayoutData(layout_data);
		cmp_src_spinner = new Spinner(options_composite,SWT.BORDER);
		cmp_src_spinner.setMaximum(1000);
		
		
		Composite button_composite = new Composite(shell,SWT.BORDER);
		layout_data = new GridData();
		layout_data.verticalAlignment = GridData.END;
		layout_data.horizontalAlignment = GridData.FILL;
		layout_data.grabExcessHorizontalSpace = true;
		layout_data.grabExcessVerticalSpace=true;
		button_composite.setLayoutData(layout_data);
		
		layout = new GridLayout(3,false);
		button_composite.setLayout(layout);
		
		Button button_clear = new Button(button_composite,SWT.NONE);
		button_clear.setText(_._("advancedsearchwindow.button.clear"));
		button_clear.setImage(SWTImageRepository.getImage("remove_all.png"));
		button_clear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				min_file_size.setText("0.0");
				min_file_size_units.select(0);
				
				max_file_size.setText("1024.00");
				max_file_size_units.select(0);
				
				file_extension.setText("");
				file_type.select(0);
				
				av_spinner.setSelection(0);
				cmp_src_spinner.setSelection(0);
				
				updated_file_size = false;
			}
		});
		
		Button button_ok = new Button(button_composite,SWT.NONE);
		button_ok.setText(_._("advancedsearchwindow.button.ok"));
		button_ok.setImage(skin.getButtonImage(Skin.OK_BUTTON_IMAGE));
		
		layout_data = new GridData();
		layout_data.grabExcessHorizontalSpace=true;
		layout_data.horizontalAlignment = SWT.RIGHT;
		button_ok.setLayoutData(layout_data);
		
		button_ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				long min_unit = (Long)min_file_size_units.getData(min_file_size_units.getText());
				long max_unit = (Long)max_file_size_units.getData(max_file_size_units.getText());
				double min = formatSize(file_size_slider.getDownValue(), min_unit);
				double max = formatSize(file_size_slider.getUpValue(), max_unit);
				if (updated_file_size)
					save((long)min, (long)max, file_types[file_type.getSelectionIndex()],file_extension.getText(), av_spinner.getSelection(), cmp_src_spinner.getSelection());
				else
					save((long)0, (long)0, file_types[file_type.getSelectionIndex()],file_extension.getText(), av_spinner.getSelection(), cmp_src_spinner.getSelection());
				shell.close();
			}
		});
		
		Button button_cancel = new Button(button_composite,SWT.NONE);
		button_cancel.setText(_._("advancedsearchwindow.button.cancel"));
		button_cancel.setImage(skin.getButtonImage(Skin.CANCEL_BUTTON_IMAGE));
		button_cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				shell.close();
			}
		});
		
		Utils.centreWindow(shell);
		shell.open();
		
		// initialization
		
		DecimalFormat formatter = new DecimalFormat("0.00");
		
		min_file_size.setText(formatter.format(min));
		max_file_size.setText(formatter.format(max));
		
		min_file_size_units.select(initial_min_size_units);
		max_file_size_units.select(initial_max_size_units);
		
		file_extension.setText(initial_extension);
		file_type.select(initial_file_type);
		
		av_spinner.setSelection(initial_av_src);
		cmp_src_spinner.setSelection(initial_cmp_src);
	}

	public abstract void save(long minSize, long maxSize,FileType fileType,String extension,long availableSources, long completedSources);

}
