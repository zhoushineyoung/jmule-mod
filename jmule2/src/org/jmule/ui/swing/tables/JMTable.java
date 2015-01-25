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
package org.jmule.ui.swing.tables;

import java.awt.Dimension;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jmule.ui.swing.Refreshable;
import org.jmule.ui.swing.SwingPreferences;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/10/18 17:42:46 $$
 */
public class JMTable extends JXTable implements Refreshable {

	protected List<TableColumnExt> table_columns = new LinkedList<TableColumnExt>();
	protected SwingPreferences _pref = SwingPreferences.getSingleton();
    protected JFrame parent;
	
	public JMTable() {
		init();
	}
	
	public JMTable(JFrame parent) {
		this.parent = parent;
		init();
	}
	
	private void init() {
		this.setRowHeight(20);
		this.setShowGrid(false); 
		//this.setIntercellSpacing(new Dimension(0,0));
		//this.setColumnMargin(0);
		//this.setDoubleBuffered(true);
		//this.setShowVerticalLines(false);
		//this.setHorizontalScrollEnabled(false);
		//this.setRowMargin(0);
		this.setHorizontalScrollEnabled(true);
		//this.setFillsViewportHeight(true);
		//this.setRowMargin(0);
		//this.setShowVerticalLines(false);
		//this.set
        //this.setIntercellSpacing(new java.awt.Dimension(0, 0));
       // this.setRowMargin(0);
       // this.setShowHorizontalLines(false);
       // this.setShowVerticalLines(false);
        //setRowMargin(0);
       // getColumnModel().setColumnMargin(0);

       // resizeAndRepaint();
	}
	
	protected void buildColumns(AbstractTableModel tableModel) {
		// set the table columns like user prefer
		Collections.sort(table_columns, new Comparator<TableColumnExt>() {
          	public int compare(TableColumnExt o1, TableColumnExt o2) {
          		  int o1_order = _pref.getColumnOrder(Integer.parseInt(o1.getIdentifier().toString()));
          		  int o2_order = _pref.getColumnOrder(Integer.parseInt(o2.getIdentifier().toString()));
                  if( o1_order < o2_order ) return -1; 
                  if( o1_order > o2_order ) return 1;
              return 0;
			}
		});		
		TableColumnModel column_model = new DefaultTableColumnModel();		
		for(TableColumnExt column : table_columns) {
			column_model.addColumn(column);
		}		
		column_model.setColumnMargin(0);
		this.setModel(tableModel);
		this.setColumnModel(column_model);
	}
	
	public void updateUI() {
		super.updateUI();
		
		setIntercellSpacing( new Dimension( 0, 0 ) );
	}
	 //private DefaultTableCellRenderer whiteRenderer;
	 //private DefaultTableCellRenderer grayRenderer;

	public void refresh() {
		
		repaint();
	}
	 
     /*public TableCellRenderer getCellRenderer(int row, int column) {
	      if (whiteRenderer == null)
	      {
	         whiteRenderer = new DefaultTableCellRenderer();
	      }
	      if (grayRenderer == null)
	      {
	         grayRenderer = new DefaultTableCellRenderer();
	         grayRenderer.setBackground(new Color(240,240,240));
	      }
	 
	      if ( (row % 2) == 0 )
	            return whiteRenderer;
	      else
	            return grayRenderer;
	 }*/
}
