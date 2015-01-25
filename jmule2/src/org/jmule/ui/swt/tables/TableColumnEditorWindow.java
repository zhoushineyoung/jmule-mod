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
package org.jmule.ui.swt.tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.skin.SkinConstants;
import org.jmule.ui.swt.SWTConstants;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.VerticalAligner;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * @author tuxpaper
 * @author binary256
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2011/03/27 16:53:32 $$
 */
public class TableColumnEditorWindow {
  
  public static final Color ROW_ALTERNATE_COLOR_1 = new Color(SWTThread.getDisplay(),238,238,238);
  public static final Color ROW_ALTERNATE_COLOR_2 = SWTThread.getDisplay().getSystemColor(SWT.COLOR_WHITE);
	
  private Display display;
  private Shell shell;
  private Color blue;
  private Table table;
  
  private ArrayList<TableColumn> tableColumns;
  private Map<TableColumn,Boolean> newEnabledState;
  private TableStructureModificationListener listener;
  
  private boolean mousePressed;
  private TableItem selectedItem;
  private Point oldPoint;

  /**
   * Default Constructor
   * 
   * @param parent Parent Shell
   * @param _tableColumns List of columns available
   * @param _listener Callback listener to trigger when columns changed
   */
  public TableColumnEditorWindow(final Table editTable,Shell parent,
		  						 TableColumn[] _tableColumns,
                                 TableStructureModificationListener _listener) {
	SWTSkin skin = null;
	try {
		
		skin = (SWTSkin)JMuleUIManager.getJMuleUI().getSkin();
		
	}catch(Throwable t) {
	}
		
    RowData rd;
    display = parent.getDisplay();
    listener = _listener;
    
    tableColumns = new ArrayList<TableColumn>();
    for(int i = 0;i<editTable.getColumnCount();i++){
    	int id = editTable.getColumnOrder()[i];
    	tableColumns.add(editTable.getColumn(id));
    }
    
    newEnabledState = new HashMap<TableColumn,Boolean>();

    for(TableColumn column : tableColumns) {
    	int column_id = (Integer)column.getData(SWTConstants.COLUMN_NAME_KEY);
    	Boolean status = SWTPreferences.getInstance().isColumnVisible(column_id);
    	newEnabledState.put(column, status);
    }
    
    
    blue = new Color(display,0,0,128);
   
    Shell shell1=new Shell(display,SWT.ON_TOP);
	shell=new Shell(shell1,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	shell.setText(Localizer._("columneditorwindow.title"));
	shell.setImage(SWTImageRepository.getImage("columns_setup.png"));
	
    GridLayout layout = new GridLayout();
    shell.setLayout (layout);
    
    GridData gridData;
    
    Label label = new Label(shell,SWT.NULL);
    label.setText(Localizer._("columneditorwindow.draghint"));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    label.setLayoutData(gridData);
    
    table = new Table (shell, SWT.VIRTUAL | SWT.CHECK | SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
    gridData = new GridData(GridData.FILL_BOTH);
    table.setLayoutData(gridData);
    table.setHeaderVisible(true);
    
    Composite cButtonArea = new Composite(shell, SWT.NULL);
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
    cButtonArea.setLayoutData(gridData);
    RowLayout rLayout = new RowLayout(SWT.HORIZONTAL);
    rLayout.marginLeft = 0;
 	rLayout.marginTop = 0;
 	rLayout.marginRight = 0;
 	rLayout.marginBottom = 0;
 	rLayout.spacing = 5;
 	cButtonArea.setLayout (rLayout);
    
    Button bOk = new Button(cButtonArea,SWT.PUSH);
    bOk.setFont(skin.getButtonFont());
    bOk.setImage(skin.getButtonImage(SkinConstants.FINISH_BUTTON_IMAGE));
    bOk.setText(Localizer._("mainwindow.button.ok"));
    rd = new RowData();
    rd.width = 70;
    bOk.setLayoutData(rd);
    bOk.addListener(SWT.Selection,new Listener() {
      public void handleEvent(Event e) {
        saveAndApply();
        close();
      }
    });
    
    Button bCancel = new Button(cButtonArea,SWT.PUSH);
    bCancel.setFont(skin.getButtonFont());
    bCancel.setImage(skin.getButtonImage(SkinConstants.CANCEL_BUTTON_IMAGE));
    bCancel.setText(Localizer._("mainwindow.button.cancel"));
    rd = new RowData();
    rd.width = 70;
    bCancel.setLayoutData(rd);
    bCancel.addListener(SWT.Selection,new Listener() {
      public void handleEvent(Event e) {
        close();
      }
    });
    
    Button bApply = new Button(cButtonArea,SWT.PUSH);
    bApply.setFont(skin.getButtonFont());
    bApply.setImage(skin.getButtonImage(SkinConstants.OK_BUTTON_IMAGE));
    bApply.setText(Localizer._("mainwindow.button.apply"));
    rd = new RowData();
    rd.width = 70;
    bApply.setLayoutData(rd);
    bApply.addListener(SWT.Selection,new Listener() {
      public void handleEvent(Event e) {
        saveAndApply();
      }
    });
    
    
    TableColumn column;
    column = new TableColumn(table, SWT.NONE);
    column.setText(Localizer._("columneditorwindow.column.column_name"));
    column = new TableColumn(table, SWT.NONE);
    column.setText(Localizer._("columneditorwindow.column.description"));
    table.getColumn(0).setWidth(160);
    table.getColumn(1).setWidth(1000);

    table.addListener(SWT.Selection,new Listener() {
      public void handleEvent(Event e) {
      	if (e.detail != SWT.CHECK)
      		return;
        mousePressed = false;
		TableItem item = (TableItem) e.item;
		int index = item.getParent().indexOf(item);
		TableColumn tableColumn = tableColumns.get(index);
		newEnabledState.put(tableColumn, new Boolean(item.getChecked()));
      }
    });
    
    table.addListener(SWT.SetData, new Listener() {
			public void handleEvent(Event event) {
				final TableItem item = (TableItem) event.item;
				if (item == null)
					return;
				Table table = item.getParent();
				int index = table.indexOf(item);
				if (index < 0) {
					// Trigger a Table._getItem, which assigns the item to the array
					// in Table, so indexOf(..) can find it.  This is a workaround for
					// a WinXP bug.
					Rectangle r = item.getBounds(0);
					table.getItem(new Point(r.x, r.y));
					index = table.indexOf(item);
					if (index < 0)
						return;
				}
				
			if (index % 2 == 0)
				item.setBackground(ROW_ALTERNATE_COLOR_2);
			else
				item.setBackground(ROW_ALTERNATE_COLOR_1);
				
			TableColumn tableColumn = tableColumns.get(index);
		    //String sTitleLanguageKey = tableColumn.getTitleLanguageKey();
		    item.setText(0, tableColumn.getText());
		    item.setText(1, (String)tableColumn.getData(SWTConstants.COLUMN_DESC_KEY));
		    
		    //Causes SetData listener to be triggered again, which messes up SWT 
	        //table.getColumn(1).pack();

		    final boolean bChecked = ((Boolean) newEnabledState.get(tableColumn));
		    item.setChecked(bChecked);

			}
    }); 
    table.setItemCount(tableColumns.size());
    
    table.addMouseListener(new MouseAdapter() {
      
      public void mouseDown(MouseEvent arg0) {
        mousePressed = true;
        selectedItem = table.getItem(new Point(arg0.x,arg0.y));
      }
      
      public void mouseUp(MouseEvent e) {
        mousePressed = false;
        //1. Restore old image
        if(oldPoint != null) {
        	table.redraw(oldPoint.x, oldPoint.y, shell.getSize().x,
							oldPoint.y + 2, false);
          oldPoint = null;
        }
        Point p = new Point(e.x,e.y);
        TableItem item = table.getItem(p);
        if(item != null && selectedItem != null) {
          int index = table.indexOf(item);
          int oldIndex = table.indexOf(selectedItem);
          if(index == oldIndex)
            return;

          TableColumn tableColumn = 
                           (TableColumn)tableColumns.get(oldIndex);
          
          tableColumns.remove(tableColumn);
          tableColumns.add(index, tableColumn);
          table.clearAll();
        }
      }
    });
    
    table.addMouseMoveListener(new MouseMoveListener(){
      public void mouseMove(MouseEvent e) {
        if (!mousePressed || selectedItem == null)
          return;

        Point p = new Point(e.x,e.y);
        TableItem item = table.getItem(p);
        if (item == null)
          return;

        Rectangle bounds = item.getBounds(0);
        int selectedPosition = table.indexOf(selectedItem);
        int newPosition = table.indexOf(item);

        //1. Restore old area
        if(oldPoint != null) {
        	table.redraw(oldPoint.x, oldPoint.y, bounds.width, oldPoint.y + 2, false);
          oldPoint = null;
        }            
        bounds.y += VerticalAligner.getTableAdjustVerticalBy(table);
        if(newPosition <= selectedPosition)
          oldPoint = new Point(bounds.x,bounds.y);
        else
          oldPoint = new Point(bounds.x,bounds.y+bounds.height);

        //3. Draw a thick line
      	table.redraw(oldPoint.x, oldPoint.y, bounds.width, oldPoint.y + 2, false);
      }
    });
    
    table.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if (!mousePressed || selectedItem == null || oldPoint == null) {
					return;
				}
				
        Point p = new Point(e.x,e.y);
        TableItem item = table.getItem(p);
        if (item == null)
          return;
		
        Rectangle bounds = item.getBounds(0);
        GC gc = new GC(table);
        gc.setBackground(blue);
        gc.fillRectangle(oldPoint.x,oldPoint.y,bounds.width,2);
        gc.dispose();
			}
		});

    shell.pack();
    Point p = shell.getSize();
    p.x = 550;
    // For Windows, to get rid of the scrollbar
    p.y += 2;
    
    if (p.y + 64 > display.getClientArea().height)
    	p.y = display.getBounds().height - 64;
    
    shell.setSize(p);
    
    Utils.centreWindow(shell);
    shell.open (); 
  }
  
  private void close() {
    if(blue != null && ! blue.isDisposed())
      blue.dispose();
    if (!shell.isDisposed())
    	shell.dispose();
  }
  
  private void saveAndApply() {
    TableItem[] items = table.getItems();
    for(int i = 0 ; i < items.length ; i++) {
      TableColumn tableColumn = tableColumns.get(i);
	  boolean bChecked = ((Boolean) newEnabledState.get(tableColumn));
	  int column_id = (Integer)tableColumn.getData(SWTConstants.COLUMN_NAME_KEY);
	  SWTPreferences.getInstance().setColumnOrder(column_id, i);
	  SWTPreferences.getInstance().setColumnVisibility(column_id, bChecked);
	  
    }
    listener.tableStructureChanged();
  }
}

