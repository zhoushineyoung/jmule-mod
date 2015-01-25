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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMThread;
import org.jmule.ui.swt.SWTConstants;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.SWTThread;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.11 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/04/12 16:40:27 $$
 */
public abstract class JMTable<T> extends Table {

	public static final Color ROW_ALTERNATE_COLOR_1 = new Color(SWTThread.getDisplay(),238,238,238);
	public static final Color ROW_ALTERNATE_COLOR_2 = SWTThread.getDisplay().getSystemColor(SWT.COLOR_WHITE);
	
	protected List<BufferedTableRow> line_list = new LinkedList<BufferedTableRow>(); // Store buffered controls for each line
	protected List<BufferedTableRow> default_line_list = new LinkedList<BufferedTableRow>(); // Store default buffered controls for each line
	
	private List<List<Object>> default_custom_control_list = new LinkedList<List<Object>>();// Default custom controls on each line

	private Listener column_data_save_listener;
	protected SWTPreferences swt_preferences = SWTPreferences.getInstance();
	protected boolean is_sorted = false;
	protected int last_sort_column ;
	protected boolean last_sort_dir = true;
	protected boolean enabled_sorting = true;
	protected boolean enable_alternate_background_color = true;
	
	public JMTable(Composite composite, int style) {
		super(composite,SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | style);		
		
		setHeaderVisible(true);
		setLinesVisible (true);
		
		
		
		column_data_save_listener = new Listener() {
			public void handleEvent(Event arg0) {
				saveColumnSettings();
			}
		};
		
		addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent arg0) {
				setMenu(getPopUpMenu());				
			}
		});
		
		addListener(SWT.SetData, new Listener(){
			public void handleEvent(Event arg0) {
				TableItem item = (TableItem) arg0.item;
				int index = indexOf(item);
				
				if (enable_alternate_background_color)
					if (index%2==0)
						item.setBackground(ROW_ALTERNATE_COLOR_2);
					else
						item.setBackground(ROW_ALTERNATE_COLOR_1);
				
				T object = (T)line_list.get(index).getData(SWTConstants.ROW_OBJECT_KEY);
				updateRow(object);
			}
		});
		
		addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
			    switch(event.type) {
			     case SWT.MeasureItem: {
			      event.height = SWTConstants.TABLE_ROW_HEIGHT;
			      break;
			     }}}
		});
		
		addListener(SWT.PaintItem, new Listener() {
			public void handleEvent(Event event) {
				int row_id = indexOf((TableItem)event.item);
				int column_id = event.index;
				int width = getColumn(column_id).getWidth(); // event's width is not used !
				BufferedTableRow row = line_list.get(row_id);
				for(BufferedTableRowCustomControl control : row.getControlList()) {
					if (control.getColumnID() == column_id) 
						control.draw(event.gc, event.x,event.y, width, event.height);
				}
			}
		});
		
	}
	
	// method must update row assigned to object
	public abstract void updateRow(T object);
	
	// comparator used in sorting
	protected abstract int compareObjects(T object1, T object2, int columnID,boolean order);
	
	// pop-up menu getter
	protected abstract Menu getPopUpMenu();
	
	public void setAlternateBackgroundColor(boolean value) {
		enable_alternate_background_color = value;
	}
	
	public void addColumn(int style, int ColumnID,String columnTitle, String columnDesc,int width) {
		TableColumn table_column = new TableColumn(this, style);
		table_column.setData(SWTConstants.COLUMN_NAME_KEY, ColumnID);
		table_column.setWidth(width);
		
		table_column.setText(columnTitle);
		
		table_column.setToolTipText(columnDesc);
		table_column.setData(SWTConstants.COLUMN_DESC_KEY, columnDesc);
		
		table_column.setMoveable(true);
		table_column.setResizable(true);
		table_column.addListener(SWT.Resize, column_data_save_listener);
		table_column.addListener(SWT.Move, column_data_save_listener);
		
		Listener sort_listener = new Listener() {
			private int sort_order = 0;
			public void handleEvent(final Event e) {
				
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
					if (!enabled_sorting) return ;
					TableColumn column = (TableColumn)e.widget;
					int column_id = (Integer)column.getData(SWTConstants.COLUMN_NAME_KEY);
					if (is_sorted)
						if (!(last_sort_column == column_id))
							sort_order = 0;
					clearColumnImages();
					if (sort_order == 0) {
						sort_order = SWT.UP;
					}
					else
						if (sort_order == SWT.UP)
							sort_order = SWT.DOWN;
						else
							if (sort_order == SWT.DOWN) {
								sort_order = 0;
								is_sorted = false;
							}
					
					if (sort_order == SWT.UP)
						sortColumn(column_id, true);
					
					if (sort_order == SWT.DOWN) {
						sortColumn(column_id, false);
					}
						
					if (sort_order == SWT.UP) {
						column.setImage(SWTImageRepository.getImage("sort-down.png"));
					}
					
					if (sort_order == SWT.DOWN) {
						column.setImage(SWTImageRepository.getImage("sort-up.png"));
					}
					
					if (sort_order == 0) {
						column.setImage(null);
						resetOrdering();
					}
				}
				
				}
			);}};
		table_column.addListener(SWT.Selection, sort_listener);
	}

	public void setSorting(boolean value) {
		enabled_sorting = value;
	}
	
	public void disableColumn(int columnID) {
		for(TableColumn column : getColumns()) {
			int column_id = (Integer) column.getData(SWTConstants.COLUMN_NAME_KEY);
			if (column_id == columnID) {
				column.setData(SWTConstants.COLUMN_DISABLED_KEY, true);
				return ;
			}
		}
	}
	
	public void addRow(T... object) {
		int ifactor = super.getItemCount();
		int x = ifactor+object.length;
		super.setItemCount(x);
		for(int i = 0;i<object.length;i++) {
	        BufferedTableRow table_row = new BufferedTableRow(this);
	        line_list.add(table_row);
	        default_line_list.add(table_row);
	        default_custom_control_list.add(new LinkedList<Object>());
	       
	        TableItem item = getItem(ifactor+i);
	        table_row.setSWTRow(item);
        
	       table_row.setData(SWTConstants.ROW_OBJECT_KEY, object[i]);
    	   // updateLine(object[i]);

		}
        
	}
	
	public void addCustumControl(final int line,BufferedTableRowCustomControl control) {
		List<Object> control_list = default_custom_control_list.get(line);
		control_list.add(control.getControl());
		line_list.get(line).getControlList().add(control);
	}
	
	public void setRowText(T object,int columnID,String text) {
		int line_id = getObjectID(object);
		if (line_id==-1) {
			return ;
		}
		int column = getColumnOrder(columnID);
		BufferedTableRow row = line_list.get(line_id);
		row.setText(column, text);
	}
	
	public String getRowText(T object, int columnID) {
		int line_id = getObjectID(object);
		if (line_id==-1) {
			return "";
		}
		int column = getColumnOrder(columnID);
		BufferedTableRow row = line_list.get(line_id);
		return row.getText(column);
	}
	
	public void setRowImage(T object,int columnID,Image image) {
		int line_id = getObjectID(object);
		if (line_id==-1) return;
		int column = getColumnOrder(columnID);
		BufferedTableRow row = line_list.get(line_id);
		Image old_image = row.getImage(column);
		if (old_image != null) {
			old_image.dispose();
		}
		row.setImage(column, image);
	}
	
	public Image getRowImage(T object,int columnID) {
		int line_id = getObjectID(object);
		if (line_id==-1) return null;
		int column = getColumnOrder(columnID);
		BufferedTableRow row = line_list.get(line_id);
		return row.getImage(column);
	}
	
	private int getColumnOrder(int columnID) {
		int column_id = 0;
		for(TableColumn column : getColumns()) {
			Integer id = (Integer)column.getData(SWTConstants.COLUMN_NAME_KEY);
			if (id == columnID)
				return column_id;
			column_id++;
		}
		return 0;
	}
	
	private void clearColumnImages() {
		for(int i = 0;i<getColumnCount();i++) {
			getColumn(i).setImage(null);
		}
	}
	
	public void clear() {
		default_line_list.clear();
        line_list.clear();
        removeAll();
    }
	
	public T getSelectedObject() {
		int i = getSelectionIndex();
		if ( i == -1 ) return null;
		return (T)line_list.get(i).getData(SWTConstants.ROW_OBJECT_KEY);
	}
	
	public List<T> getSelectedObjects() {
		int indexes[] = getSelectionIndices();
		List<T> selected_objects = new LinkedList<T>();
		for(int i = 0;i<indexes.length;i++) {
			BufferedTableRow row = line_list.get(indexes[i]);
			selected_objects.add((T)row.getData(SWTConstants.ROW_OBJECT_KEY));
		}
		return selected_objects;
	}
	
	public BufferedTableRow getRow(T object) {
		int id = getObjectID(object);
		if (id!=-1) {
			return line_list.get(id);
		}
		return null;
	}
	
	public void setForegroundColor(T object,final Color color) {
		final BufferedTableRow item = getRow(object);
		if (item == null) return ;
	   	item.setForeground(color);
	}
	
	public void setBackgroundColor(T object,final Color color) {
		final BufferedTableRow item = getRow(object);
		if (item == null) return ;
	   	item.setBackgrounColor(color);
	}
	
	public void removeRow(T object) {
		int id = getObjectID(object);
		if (id==-1) return ;
		if (id!=getItemCount()) {
			
		}
		BufferedTableRow row = line_list.get(id);
		
		remove(id);
		line_list.remove(id);
		default_custom_control_list.remove(id);
		default_line_list.remove(id);
		if (getItemCount() == 0) return ;
		row.dispose();
		clearAll();		
	}
	
	protected List<BufferedTableRowCustomControl> getCustumElements(T object) {
		int lineID = getObjectID(object);
		if (lineID==-1) return null;
		
		return line_list.get(lineID).getControlList();
	}
	
	protected void updateColumnVisibility() {
		for(TableColumn column : getColumns()) {
			
			int column_id = (Integer)column.getData(SWTConstants.COLUMN_NAME_KEY);
			
			boolean column_visibility = swt_preferences.isColumnVisible(column_id);
			
			Object isDisabled = column.getData(SWTConstants.COLUMN_DISABLED_KEY);
			if (isDisabled != null)
				column_visibility = false;
			
			if (!column_visibility) {
				if (column.getWidth()!=0) {
				column.setWidth(0);
				column.setResizable(false);
				}				
			} else {
				column.setResizable(true);
				column.setWidth(swt_preferences.getColumnWidth(column_id));
			}
		}
	}
	
	protected void updateColumnOrder() {
		int column_order[] = new int[getColumnCount()];
		for(int i = 0; i < this.getColumnCount(); i++ ) {
			TableColumn column = getColumn(i);
			int column_id = (Integer)column.getData(SWTConstants.COLUMN_NAME_KEY);
			int pos = swt_preferences.getColumnOrder(column_id);
			column_order[pos] = i;
		}
		
		setColumnOrder(column_order);
	}
	
	public void updateColumnSettings() {
		updateColumnVisibility();
		updateColumnOrder();
	}
	
	public T getData(int index) {
		return (T) line_list.get(index).getData(SWTConstants.ROW_OBJECT_KEY);
	}
	
	protected void saveColumnSettings() {
		if (!isVisible()) return ;
		int column_order[];
		column_order = getColumnOrder();
		for(int i = 0; i < getColumnCount(); i++ ) {
			int column_id = column_order[i];
			TableColumn table_column = getColumn(column_id);
			if (table_column.getWidth()==0) continue;
			
			int column_id2 = (Integer)table_column.getData(SWTConstants.COLUMN_NAME_KEY);
			swt_preferences.setColumnOrder(column_id2, i);
			if (swt_preferences.isColumnVisible(column_id2)) 
					swt_preferences.setColumnWidth(column_id2, table_column.getWidth());
		}
	}
	
	public void showColumnEditorWindow() {
		new TableColumnEditorWindow(this,this.getShell(),getColumns(), new TableStructureModificationListener() {
			public void tableStructureChanged() {
				updateColumnVisibility();
				updateColumnOrder();
				
				update();
				redraw();
			}
		});
	}
	
	public boolean hasObject(T object) {
		for(BufferedTableRow row : line_list) {
			T o = (T)row.getData(SWTConstants.ROW_OBJECT_KEY);
			if (o!=null)
			if (object.equals(o)) 
				return true;
		}
		
		return false;
	}
	
	public List<T> getObjects() {
		List<T> list = new LinkedList<T>();
		for(BufferedTableRow row : line_list) {
			T o = (T)row.getData(SWTConstants.ROW_OBJECT_KEY);
			list.add(o);
		}
		return list;
	}
	
	protected int getObjectID(T object) {
		int id = 0;
		boolean found = false;
		for(BufferedTableRow row : line_list) {
			T o = (T)row.getData(SWTConstants.ROW_OBJECT_KEY);
			if (o!=null)
			if (object.equals(o)) {
				found = true;
				break;
			}
			id++;
		}
		
		if (found) return id;
		return -1;
		
	}
	
	private JMThread sort_thread;
	protected void sortColumn(final int columnID,final boolean sortOrder) {
		if (sort_thread!=null)
			if (sort_thread.isAlive()) return ;
		sort_thread = new JMThread(new JMRunnable() {
			public void JMRun() {
				last_sort_column = columnID;
				last_sort_dir = sortOrder;
				Collections.sort(line_list, new Comparator() {
						public int compare(Object arg0, Object arg1) {
							BufferedTableRow row1 = (BufferedTableRow)arg0;
							BufferedTableRow row2 = (BufferedTableRow)arg1;
								
							T object1 = (T) row1.getData(SWTConstants.ROW_OBJECT_KEY);
							T object2 = (T) row2.getData(SWTConstants.ROW_OBJECT_KEY);
							return compareObjects(object1,object2,columnID,sortOrder);
					} });
				SWTThread.getDisplay().syncExec(new JMRunnable() {
					public void JMRun() {
						clearAll();
						updateIndexes();
						setItemCount(line_list.size());
					}
				});
			} 
		});
		sort_thread.start();
	}
	
	/**
	 * Set to default order
	 */
	private JMThread reset_order_thread;
	
	private void resetOrdering() {
		if (reset_order_thread != null)
			if (reset_order_thread.isAlive()) return ;
		reset_order_thread =  new JMThread(new JMRunnable() {
			public void JMRun() {
				for(int i = 0;i<default_line_list.size(); i++) {
					line_list.set(i, default_line_list.get(i));
				}
				SWTThread.getDisplay().syncExec(new JMRunnable() {
					public void JMRun() {
						clearAll();
						updateIndexes();
						setItemCount(line_list.size());
					}
				});
			}
		});
		reset_order_thread.start();		
	}
	
	private void updateIndexes() {

		for(int i = 0;i<line_list.size();i++) {
			line_list.get(i).setSWTRow(getItem(i));
		}
		
	}
		
	protected void checkSubclass() {}

}
