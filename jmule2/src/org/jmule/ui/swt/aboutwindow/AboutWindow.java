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
package org.jmule.ui.swt.aboutwindow;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jmule.core.JMConstants;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMThread;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.UIConstants;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.skin.SWTSkin;
import org.jmule.ui.swt.tables.JMTable;

/**
 * Created on Aug 22, 2008
 * @author binary256
 * @version $Revision: 1.10 $
 * Last changed by $Author: binary255 $ on $Date: 2010/01/11 17:16:30 $
 */
public class AboutWindow implements JMuleUIComponent {
	
	private int TOP_GRADIENT_HEIGHT = 60;
	private Color GRADIENT_COLOR_1 = SWTThread.getDisplay().getSystemColor (SWT.COLOR_WHITE);
	private Color GRADIENT_COLOR_2 = new Color(SWTThread.getDisplay(),255,141,5);
	
	private final String logo_image = "jmule_logo.png";
	
	private Shell shell;
	private Display display;
	
	public void getCoreComponents() {
		
	}

	public void initUIComponents() {
		SWTSkin skin = null;
		try {
		    skin = (SWTSkin)JMuleUIManager.getJMuleUI().getSkin();
		}catch(Throwable t) { }
		
		display = SWTThread.getDisplay();
		
		final Shell shell1=new Shell(display,SWT.ON_TOP);
		shell=new Shell(shell1,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		MouseAdapter link_listener = new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent arg0) {
				String path = (String) ((CLabel) arg0.widget).getData();
				if (!Utils.launchProgram(path)) 
					Utils.showWarningMessage(shell, _._("aboutwindow.error_open_url.title")
							, Localizer._("aboutwindow.error_open_url",path));
			}
			public void mouseDown(MouseEvent arg0) {
				String path = (String) ((CLabel) arg0.widget).getData();
				if (!Utils.launchProgram(path)) 
					Utils.showWarningMessage(shell, _._("aboutwindow.error_open_url.title")
							, Localizer._("aboutwindow.error_open_url",path));
			}
		};
		
		GridData layout_data;
		GridLayout layout;
		Composite content;
		
		layout = new GridLayout(1,false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		
		shell.setLayout(layout);
		
		// Top gradient
		final Canvas window_top = new Canvas(shell,SWT.NONE);
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.heightHint = TOP_GRADIENT_HEIGHT;
		window_top.setLayoutData(layout_data);
		window_top.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent arg0) {
				Rectangle rect = shell.getClientArea ();
				GC gc = arg0.gc;
				gc.setForeground (GRADIENT_COLOR_2);
				gc.setBackground (GRADIENT_COLOR_1);
				gc.fillGradientRectangle (rect.x, rect.y, rect.width, rect.height, false);
				Image image = SWTImageRepository.getImage(logo_image);
				gc.drawImage(image, rect.width - image.getImageData().width - 10, 5);
				
				gc.setForeground(new Color(SWTThread.getDisplay(),0,0,0));
				Font font = new Font(display,"Arial",14,SWT.NONE ); 
				gc.setFont(font);
				gc.drawText(JMConstants.JMULE_NAME, 20, TOP_GRADIENT_HEIGHT / 2 - 15,true);
				
				gc.setForeground(new Color(SWTThread.getDisplay(),0,0,0));
				font = new Font(display,"Arial",10,SWT.NONE ); 
				gc.setFont(font);
				gc.drawText("Version : " + JMConstants.JMULE_VERSION, 17, TOP_GRADIENT_HEIGHT / 2 + 5,true);
			}
		});
		
		Composite window_content = new Composite(shell,SWT.NONE);
		window_content.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		FillLayout fill_layout = new FillLayout();
		fill_layout.marginHeight = 0;
		fill_layout.marginWidth = 0;
		window_content.setLayout(fill_layout);
		
		CTabFolder tab_list = new CTabFolder(window_content, SWT.BORDER);
		tab_list.setLayout(new FillLayout());
		tab_list.setSimple(false);
		tab_list.setUnselectedImageVisible(false);
		tab_list.setUnselectedCloseVisible(false);
		
		CTabItem general_tab = new CTabItem(tab_list,SWT.NONE);
		content = new Composite(tab_list,SWT.NONE);
		general_tab.setControl(content);
		general_tab.setText(_._("aboutwindow.tab.general"));
		layout = new GridLayout(1,false);
		layout.marginTop +=10;
		content.setLayout(layout);

		Composite container1 = new Composite(content,SWT.NONE);
		container1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout(1,false);
		layout.marginWidth = 15;
		container1.setLayout(layout);
		
		Label jmule_version = new Label(container1,SWT.LEFT);
		FontData data = skin.getLabelFont().getFontData()[0];
		
		Font bold_font = new Font(display,data.getName(),data.getHeight(),SWT.BOLD);
		jmule_version.setText(JMConstants.JMULE_FULL_NAME);
		jmule_version.setFont(bold_font);
		
		Label copyright = new Label(container1,SWT.LEFT);
		copyright.setText("Copyright (C) 2007-2010 JMule Team");
		copyright.setFont(bold_font);
		
		new Label(container1,SWT.NONE);
		
		StyledText about_text = new StyledText(container1,SWT.LEFT | SWT.READ_ONLY);
		about_text.setText(_._("aboutwindow.tab.general.label.about"));
		about_text.setWordWrap(true);
		about_text.setBackground(shell.getBackground());
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		about_text.setLayoutData(layout_data);
		
		Composite links_container = new Composite(content,SWT.NONE);
		links_container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout(2,false);
		layout.marginWidth = 15;
		links_container.setLayout(layout);
		
		FontData link_font_data = skin.getLabelFont().getFontData()[0];
		Font link_font = new Font(SWTThread.getDisplay(),link_font_data.getName(), link_font_data.getHeight(), SWT.UNDERLINE_SINGLE);
		
		CLabel link;
		
		link = new CLabel(links_container,SWT.NONE);
		link.setText(_._("aboutwindow.tab.general.label.home_page") + " : ");
		link = new CLabel(links_container,SWT.NONE);
		link.setFont(link_font);
		link.setText(JMConstants.JMULE_WEB_SITE);
		link.setData(JMConstants.JMULE_WEB_SITE);
		link.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		link.setCursor(new Cursor(SWTThread.getDisplay(),SWT.CURSOR_HAND));
		link.addMouseListener(link_listener);

		link = new CLabel(links_container,SWT.NONE);
		link.setText(_._("aboutwindow.tab.general.label.forum") + " : ");
		
		link = new CLabel(links_container,SWT.NONE);
		link.setFont(link_font);
		link.setText(JMConstants.JMULE_FORUMS);
		link.setData(JMConstants.JMULE_FORUMS);
		link.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		link.setCursor(new Cursor(SWTThread.getDisplay(),SWT.CURSOR_HAND));
		link.addMouseListener(link_listener);
		
		if (JMConstants.IS_NIGHTLY_BUILD) {
			CTabItem nightly_build = new CTabItem(tab_list,SWT.NONE);
			content = new Composite(tab_list,SWT.NONE);
			nightly_build.setControl(content);
			nightly_build.setText(_._("aboutwindow.tab.nightly_build"));
			layout = new GridLayout(1,false);
			layout.marginHeight = 10;
			layout.marginWidth  = 10;
			content.setLayout(layout);
			
			final Label image = new Label(content,SWT.NONE);
			image.setImage(SWTImageRepository.getImage("bomb.png"));
			layout_data = new GridData();
			layout_data.grabExcessHorizontalSpace = true;
			layout_data.horizontalAlignment = GridData.CENTER;
			image.setLayoutData(layout_data);
			
			Label nightly_build_label = new Label(content,SWT.NONE);
			nightly_build_label.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_RED));
			layout_data = new GridData(GridData.FILL_HORIZONTAL);
			layout_data.horizontalAlignment = GridData.CENTER;
			nightly_build_label.setLayoutData(layout_data);
			nightly_build_label.setText(_._("aboutwindow.tab.nightly_build.label.nightly_buld_str1"));
			
			Label nightly_build_label2 = new Label(content,SWT.NONE);
			nightly_build_label2.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_RED));
			layout_data = new GridData(GridData.FILL_HORIZONTAL );
			layout_data.horizontalAlignment = GridData.CENTER;
			nightly_build_label2.setLayoutData(layout_data);
			nightly_build_label2.setText(_._("aboutwindow.tab.nightly_build.label.nightly_buld_str2")+" "+_._("aboutwindow.tab.general.label.forum"));
			
			CLabel forum_link = new CLabel(content,SWT.NONE);
			layout_data = new GridData(GridData.FILL_HORIZONTAL );
			layout_data.horizontalAlignment = GridData.CENTER;
			forum_link.setLayoutData(layout_data);
			forum_link.setFont(link_font);
			forum_link.setText(JMConstants.JMULE_FORUMS);
			forum_link.setData(JMConstants.JMULE_FORUMS);
			forum_link.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			forum_link.setCursor(new Cursor(SWTThread.getDisplay(),SWT.CURSOR_HAND));
			forum_link.addMouseListener(link_listener);
		}
		
		CTabItem environment_tab = new CTabItem(tab_list,SWT.NONE);
		content = new Composite(tab_list,SWT.NONE);
		environment_tab.setControl(content);
		environment_tab.setText(_._("aboutwindow.tab.environment"));
		content.setLayout(new FillLayout());
		
		JMTable<String> table = new JMTable<String>(content,SWT.NONE) {
			protected int compareObjects(String object1, String object2,
					int columnID, boolean order) {
				return 0;
			}

			protected Menu getPopUpMenu() {
				return null;
			}

			public void updateRow(String object) {
				setRowText(object, 1, object);
				if (object.equals("line.separator"))
					setRowText(object,2,"\\n");
				else {
					String data = System.getProperty(object);
					setRowText(object,2,data);
				}
			}
			protected void saveColumnSettings() {}
		};
		table.setLayout(new FillLayout());
		table.setLinesVisible(true);
		table.setAlternateBackgroundColor(false);
		table.setSorting(false);
		table.addColumn(SWT.LEFT, 1, _._("aboutwindow.tab.environment.table.column.key"), "", 200);
		table.addColumn(SWT.LEFT, 2, _._("aboutwindow.tab.environment.table.column.value"), "", 500);
		
		Set<Object> keys = System.getProperties().keySet();
		
		for(Object key : keys) {
			String value = (String) key;
			table.addRow(value);
		}
		CTabItem license_tab = new CTabItem(tab_list,SWT.NONE);
		content = new Composite(tab_list,SWT.NONE);
		license_tab.setControl(content);
		license_tab.setText(_._("aboutwindow.tab.license"));
		
		content.setLayout(new FillLayout());
		
		Text license_text = new Text(content,SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.READ_ONLY);
		Font font_license = new Font(SWTThread.getDisplay(),"Courier",10,SWT.NONE );
		license_text.setFont(font_license);
	    license_text.setText(UIConstants.GNU_LICENSE);
	    license_text.setBackground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	    
		tab_list.setSelection(general_tab);
		
		Composite button_bar = new Composite(shell,SWT.NONE);
		button_bar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		layout = new GridLayout(1,false);
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		button_bar.setLayout(layout);
		
		Button button_ok = new Button(button_bar,SWT.NONE);
		button_ok.setFont(skin.getButtonFont());
		button_ok.setText(_._("aboutwindow.button.ok"));
		GridData grid_data = new GridData();
		grid_data.horizontalAlignment = GridData.END;
		grid_data.widthHint = 60;
		grid_data.grabExcessHorizontalSpace = true;
		button_ok.setFocus();
		button_ok.setLayoutData(grid_data);
		
		button_ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});
		
		shell.setText(Localizer._("aboutwindow.title"));
		shell.setImage(SWTImageRepository.getImage("jmule.png"));
		shell.setSize(500,400);
		Utils.centreWindow(shell);
		
		shell.setAlpha(0);			
		shell.open();

		new JMThread(new JMRunnable() {
				int i;
				private static final int INCREMENT = 10;
				public void JMRun() {
					for(i = 0; i<255;i+=INCREMENT ) {
					display.syncExec(new JMRunnable() {
						public void JMRun() {
							shell.setAlpha(i);
						}
					});
					}
				}
			}).start();
	}

}
