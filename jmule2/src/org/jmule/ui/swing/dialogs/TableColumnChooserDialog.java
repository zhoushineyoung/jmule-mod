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
package org.jmule.ui.swing.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:45:19 $$
 */
public class TableColumnChooserDialog extends JDialog {
    
	  public TableColumnChooserDialog(JFrame frame) {
		  super(frame, true);
		  init();
	  }
	  
	  private void init() {
		  
		  JScrollPane scrollPane = new JScrollPane();
		  BorderLayout borderLayout = new BorderLayout();
		  TableColumnChooser table = new TableColumnChooser();
		  
		  this.setLayout(borderLayout);
		  scrollPane.setViewportView(table);
		  this.add(scrollPane, BorderLayout.CENTER);
	  }
	  
	  class TableColumnChooser extends JTable {
		  
		  public TableColumnChooser() {
			  this.setModel(new TableColumnChooserModel());
			  init();
		  }
		  
		  private void init() {
			  this.getColumnModel().
			         getColumn(TableColumnChooserModel.COLUMN_NAME).
			           setCellRenderer(new TableCellRenderer() {
			        	  public Component getTableCellRendererComponent(JTable table,
			        			                                         Object stringValue,
			        			                                         boolean isSelected,
			        			                                         boolean hasFocus,
			        			                                         int row,
			        			                                         int col) {
			        		  JCheckBox column_name = new JCheckBox("test test");
			        		  return column_name;
			        	  }
			           });
			  this.getColumnModel().
		            getColumn(TableColumnChooserModel.COLUMN_NAME).
		              setCellEditor(new DefaultCellEditor(new JCheckBox("qqq")) {
		            	  
                      });
		  }
		  
	  }
	  
	  class TableColumnChooserModel extends AbstractTableModel {
		  
		    public final static int COLUMN_NAME = 0;
		    public final static int COLUMN_DESCRIPTION = 1;
		    
		    //TODO get the strings from Localizer
		    private final String[] column_names = {
		    		               "Column name",
		    		               "Description"
		                           };
		    
		    public int getColumnCount() {
		    	return column_names.length;
		    }
		    
		    public int getRowCount() {
		    	return 10;
		    }
		    
		    public Object getValueAt(int row, int col) {
		    	return 2;
		    }
		    
		    public String getColumnName(int col) {
		    	return column_names[col];
		    }
		    
		    public boolean isCellEditable(int row, int col) {
		    	return false;
		    }
	  }
	  
	   //For test only
	   public static void main(String[] args) {
		   
		   JFrame jf = new JFrame();
		   jf.setSize(400,300);
		   
		   jf.setVisible(true);
		   TableColumnChooserDialog jmd = new TableColumnChooserDialog( jf );
		   jmd.setSize(200,300);
		   jmd.setVisible(true);
		   
	   }
}


