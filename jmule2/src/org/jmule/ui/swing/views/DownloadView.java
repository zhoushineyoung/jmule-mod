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
package org.jmule.ui.swing.views;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:45:16 $$
 */
public class DownloadView extends JPanel {

	  public DownloadView() {
		   this.setLayout( new GridLayout(1,1) );
		   JScrollPane jsp = new JScrollPane();
		   jsp.setViewportView( new DownloadTable() );
		   this.add( jsp );
	  }
	  
	  class DownloadTableModel extends AbstractTableModel {
		  public static final int FILE_NAME = 0;
		  public static final int SIZE = 1;
		  public static final int COMPLETED = 2;
		  public static final int SPEED = 3;
		  public static final int PROGRESS_BAR = 4;
		  public static final int SOURCES = 5;
		  public static final int STATUS = 6;
		  
		  private final String[] column_names = {
				                  "File name",
				                  "Size",
				                  "Completed",
				                  "Speed",
				                  "Progress",
				                  "Sources",
				                  "Status"
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
	  
	  class DownloadTable extends JTable {
			public DownloadTable() {
				//super(new DownloadTableModel());
				this.setModel( new DownloadTableModel() );
				init();
			}
			
			private void init() {
			    this.getColumnModel().
			        getColumn(DownloadTableModel.PROGRESS_BAR).
			        setCellRenderer( new TableCellRenderer() {
			        	public Component getTableCellRendererComponent(JTable table,
			        			                                       Object stringValue,
			        			                                       boolean isSelected,
			        			                                       boolean hasFocus,
			        			                                       int row,
			        			                                       int column) {
			        		//for test only put jlabel here
			        		 JProgressBar progress_bar = new JProgressBar(); 
					         return progress_bar;
			        	}
			        });
			    
			}
	    }
	  
		public static void main(String args[]) {
			
			JFrame jf = new JFrame();
			jf.setSize( 500, 400 );
			
			DownloadView slv = new DownloadView();

			jf.getContentPane().add( slv );
			
			jf.setVisible( true );
		}
	
}
