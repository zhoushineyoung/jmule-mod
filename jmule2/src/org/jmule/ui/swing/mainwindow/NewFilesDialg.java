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
package org.jmule.ui.swing.mainwindow;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.jmule.core.JMRunnable;
import org.jmule.core.JMThread;
import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.ui.swing.tables.JMTableCellRenderer;
import org.jmule.ui.utils.FileFormatter;


/**
 *
 * Created on Sep 18, 2008
 * @author javajox
 * @version $Revision: 1.2 $
 * Last changed by $Author: javajox $ on $Date: 2009/09/22 19:08:43 $
 */
public class NewFilesDialg extends NewED2KDialog {

	/*class Pair {
		public Pair(ED2KFileLink fileLink, boolean isSelected) {
			this.fileLink = fileLink;
			this.isSelected = isSelected;
		}
		public Pair(ED2KFileLink fileLink) {
			this.fileLink = fileLink;
			this.isSelected = true;
		}
		public ED2KFileLink fileLink;
		public boolean isSelected;
	}*/
	private GridBagLayout grid_bag_layout1 = new GridBagLayout();
	private GridBagLayout grid_bag_layout2 = new GridBagLayout();
	private final List<ED2KFileLink> ed2k_file_links_list = new LinkedList<ED2KFileLink>();
	//private final List<Pair> selected_list = new LinkedList<Pair>();
	//private final Map<ED2KFileLink,Boolean> selected_list = new HashMap<ED2KFileLink,Boolean>();
	private NewFilesTableModel new_files_table_model = new NewFilesTableModel();
	private final static String[] column_names = { "File name", "Size", "Hash" };
	class NewFilesTableModel extends AbstractTableModel {
		  public final static int FILE_NAME = 0;
		  public final static int FILE_SIZE = 1;
		  public final static int FILE_HASH = 2;
		  public Class getColumnClass(int col) {
              return Object.class;
		  }
		  public boolean isCellEditable(int row, int col){
			  return true; 
		  }
		  public int getColumnCount() {			  
			  return column_names.length;
		  }
		  public int getRowCount() {
			  return ed2k_file_links_list.size();
		  }
		  public Object getValueAt(int row, int col) {
			  ED2KFileLink file_link = ed2k_file_links_list.get(row);
			  switch( col ) {
			     case FILE_NAME : return file_link.getFileName();
			     case FILE_SIZE : return file_link.getFileSize();
			     case FILE_HASH : return file_link.getFileHash();
			  }
			  return null;
		  }
		  public String getColumnName(int col) {
			  return column_names[col];
		  }
	}
	
	public NewFilesDialg(JFrame parent) {
		super(parent);
		init();
	}
	
	private void init() {
       // ------------------------------------------------------------------
	   grid_bag_layout1.rowWeights = new double[] {0.1, 0.1, 0.1};
	   grid_bag_layout1.rowHeights = new int[] {7, 7, 7};
	   grid_bag_layout1.columnWeights = new double[] {0.1};
	   grid_bag_layout1.columnWidths = new int[] {7};
	   // ------------------------------------------------------------------
	   central_panel.setLayout(grid_bag_layout1);
	   // ------------------------------------------------------------------
	   grid_bag_layout2.rowWeights = new double[] {0.0, 0.0, 0.1};
	   grid_bag_layout2.rowHeights = new int[] {163, 62, 7};
	   grid_bag_layout2.columnWeights = new double[] {0.1, 0.1, 0.1, 0.1};
	   grid_bag_layout2.columnWidths = new int[] {7, 7, 7, 7};
	   // ------------------------------------------------------------------
	   central_panel.add(paste_ed2k_links, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 20), 0, 0));	
	   central_panel.add(learn_about_ed2k_links_label, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
	   table.setModel(new_files_table_model);
	   table.getColumnModel().getColumn(0).setPreferredWidth(300);
	   table.setRowSelectionAllowed(false);
	   // TODO the name cell must be a checkbox
	   //table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
	   /*table.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {
         public Component getTableCellRendererComponent(JTable table,
				      Object value, boolean isSelected, boolean hasFocus, int row,
				      int column) {
			  //super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        	  final JCheckBox check_box = new JCheckBox();
        	  //check_box.setSelected(true);
        	  final ED2KFileLink file_link = (ED2KFileLink)value;
        	  boolean is_checked = selected_list.get(file_link);
        	  check_box.setSelected(true);
        	  //if(pair.isSelected) check_box.setSelected(true);
        	  //check_box.setSelected(true);
        	 // check_box.addMouseListener(new MouseAdapter() {
				//  public void mouseClicked(MouseEvent evt) {
	        		  if(is_checked) { 
	        			  selected_list.remove(file_link);
	        		      selected_list.put(file_link,false);
	        		      check_box.setSelected(false);
	        		  } else {
	        			  selected_list.remove(file_link);
	        		      selected_list.put(file_link,true);
	        		      check_box.setSelected(true);
	        		  }
				//  }
			  //});
        	  check_box.setText(file_link.getFileName());
        	  check_box.setToolTipText(file_link.getFileName());
      		  if(isSelected) {
      			  check_box.setForeground(table.getSelectionForeground());
      			  check_box.setBackground(table.getSelectionBackground());
  		      } else {
  		    	  check_box.setForeground(table.getForeground());
  		    	  check_box.setBackground(table.getBackground());
  		      }
			  return check_box;
		 }
	   });*/
	   //
	   ////table.getColumnModel().getColumn(0).setCellEditor(new TableCellEditor() {
		//   
	   //});
	   //table.getColumnModel().getColumn(0).setPreferredWidth(50);
	   table.getColumnModel().getColumn(0).setCellRenderer(new JMTableCellRenderer() {
		   public Component getTableCellRendererComponent(JTable table,
				      Object value, boolean isSelected, boolean hasFocus, int row,
				      int column) {
			  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			  this.setText(value.toString());
			  this.setToolTipText(value.toString());
			  return this;
		   }
	   });
	   table.getColumnModel().getColumn(1).setCellRenderer(new JMTableCellRenderer() {
		   public Component getTableCellRendererComponent(JTable table,
				      Object value, boolean isSelected, boolean hasFocus, int row,
				      int column) {
			  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			  this.setHorizontalAlignment(SwingConstants.RIGHT);
			  this.setText(FileFormatter.formatFileSize(Integer.parseInt(value.toString()))+" ");
			  return this;
		   }
	   });
	   table.getColumnModel().getColumn(2).setCellRenderer(new JMTableCellRenderer() {
		   public Component getTableCellRendererComponent(JTable table,
				      Object value, boolean isSelected, boolean hasFocus, int row,
				      int column) {
			  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			  this.setText(value.toString());
			  this.setToolTipText(value.toString());
			  this.setForeground(Color.GRAY);
			  return this;
		   }
	   });
	   paste_ed2k_links.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent event) {
			      (new JMThread(new JMRunnable() {
			    	   public void JMRun() {
			    		 try {  
			    		   Object clipboard_contents = clipboard.getData(DataFlavor.stringFlavor);
			    		   List<ED2KFileLink> clipboard_file_links = ED2KFileLink.extractLinks(clipboard_contents.toString());
						   if( clipboard_file_links.size() != 0 )  
							 for(ED2KFileLink file_link : clipboard_file_links)  
								if( !ed2k_file_links_list.contains(file_link) ) {  
								   ed2k_file_links_list.add(file_link);
								   //selected_list.put(file_link, true);
								}
						   SwingUtilities.invokeAndWait(new Runnable() {
							    public void run() {
							    	if( ed2k_file_links_list.size() != 0 ) filesPanel(); 
							    }
						   });
			    		 }catch(Throwable t) {
			    			 t.printStackTrace();
			    		 }
					   }
			      })).start();
		   }
	   });
	   clearButton.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent event) {
			    ed2k_file_links_list.clear();
			    emptyPanel();
		   }
	   });
	}
	
	private void filesPanel() {
		// clean up the central panel
		central_panel.remove(paste_ed2k_links);
		central_panel.remove(learn_about_ed2k_links_label);
		// end clean up	
		central_panel.setLayout(grid_bag_layout2);
		central_panel.add(paste_ed2k_links, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		central_panel.add(scroll_panel, new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		// table.setModel(new_files_table_model);
		central_panel.updateUI();
		this.repaint();
	}
	
	private void emptyPanel() {
		// clean up the central panel
		central_panel.remove(paste_ed2k_links);
		central_panel.remove(scroll_panel);
		// end clean up
		central_panel.setLayout(grid_bag_layout1);
		central_panel.add(paste_ed2k_links, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 20), 0, 0));	
		central_panel.add(learn_about_ed2k_links_label, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
		central_panel.updateUI();
		this.repaint();
	}
	
	public static void main(String args[]) {
		JFrame frame = new JFrame("T e s t i n g   ! ! !");
		NewFilesDialg new_files_dialog = new NewFilesDialg(frame);
		frame.setSize(300,400);
		new_files_dialog.setSize(500, 300);
		frame.setVisible(true);
		new_files_dialog.setVisible(true);
	}
	
}
