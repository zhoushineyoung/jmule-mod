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
package org.jmule.ui.swing.wizards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;

import org.jmule.core.utils.FileUtils;
import org.jmule.ui.swing.UISwingImageRepository;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2009/09/22 19:08:43 $$
 */
public class SharedFoldersChooser extends WizardPanel {

    private JButton add_button;
    private JButton remove_button;
    private JButton remove_all_button;
    private JLabel desc_label;
    private JList folder_list;
    private JPanel shared_folders_panel;
    private JScrollPane shared_folders_scrollpanel;
    
    private JFileChooser file_chooser;
    private FileSystemView file_system_view;
    // current chosen folders 
    private File[] current_chosen_folders;
    // all folders chosen during this session
    private ChosenFolders chosen_folders;
	private JDialog parent;
    
	public SharedFoldersChooser(JDialog parent) {
		this.parent = parent;
		doWork();
	}
	
    public SharedFoldersChooser() {
       	doWork();
    }
    
    private void doWork() {
    	
        initComponents();
        
        file_system_view = FileSystemView.getFileSystemView(); 
        file_chooser = new JFileChooser( file_system_view );
        file_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        file_chooser.setMultiSelectionEnabled(true);
        
        chosen_folders = new ChosenFolders();
        List<File> shared_folders = null;
        try {
           shared_folders = _config.getSharedFolders();
        }catch( Throwable cause ) {
        	cause.printStackTrace();
        }
        
        if(shared_folders != null) {      	
        	for(File folder : shared_folders) {
        	    chosen_folders.add(folder);	
        	}
        }
        
        folder_list.setModel( chosen_folders );
        
        final SharedFoldersChooser _this = this;
        
        // store chosen folders into the chosen_folders list 
        // also shows a dialog that tells to the user if he chose already existed folders
        file_chooser.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		if(event.getActionCommand() == JFileChooser.APPROVE_SELECTION) {
        			current_chosen_folders = file_chooser.getSelectedFiles();
        			LinkedList<File> newFolders = new LinkedList<File>();
        			final List<File> already_existed_folders = FileUtils.extractNewFolders(current_chosen_folders, chosen_folders.getFoldersList(), newFolders);
        			for(File file : newFolders)
        				chosen_folders.add(file);
        			if(already_existed_folders.size() != 0) {
         			   ExistedFoldersDialog existed_folders_dialog = new ExistedFoldersDialog(parent, true, new AbstractListModel() {
        				   public int getSize() { 
        					
        					   return already_existed_folders.size();
        				   }
        				   public Object getElementAt(int i) {
        					
        					   return already_existed_folders.get( i );
        				   }
        			   });
        			   existed_folders_dialog.setVisible(true);
        			}
        		}
        	}
        });
        
        // shows the folders selection dialog
        add_button.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		file_chooser.showDialog(_this,"Choose");	
        	}
        });
        
        // removes the selected folders from the shared folders list
        remove_button.addActionListener( new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
         		SwingUtilities.invokeLater(new Runnable() {
                   public void run() {
                	 Object[] selected_values;
               		 selected_values = folder_list.getSelectedValues();
               		 for(Object o : selected_values) {
               			File f = (File)o;
               			chosen_folders.remove( f );
               		 }  
                   }
               });
           }
        });
        
        // removes all folders from shared folders list
        remove_all_button.addActionListener( new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		//SwingUtilities.invokeLater(new Runnable() {
        			//public void run() {
                		chosen_folders.removeAll();	
        			//}
        		//});
        	}
        });
    	
    }

    private void initComponents() {

        shared_folders_panel = new JPanel();
        shared_folders_scrollpanel = new JScrollPane();
        folder_list = new JList();
        desc_label = new JLabel();
        add_button = new JButton();
        remove_button = new JButton();
        remove_all_button = new JButton();

        desc_label.setFont( skin.getLabelFont() );
        add_button.setFont( skin.getButtonFont() );
        remove_button.setFont( skin.getButtonFont() );
        remove_all_button.setFont( skin.getButtonFont() );
        
        setPreferredSize(new java.awt.Dimension(100, 100));

        shared_folders_panel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Shared folders"));
        shared_folders_scrollpanel.setViewportView(folder_list);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(shared_folders_panel);
        shared_folders_panel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(shared_folders_scrollpanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(shared_folders_scrollpanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addContainerGap())
        );

        desc_label.setText("Please specify the folders that you want to share on the network");

        add_button.setText("Add");
        add_button.setIcon(UISwingImageRepository.getIcon("add.png"));
        
        remove_button.setText("Remove");
        remove_button.setIcon(UISwingImageRepository.getIcon("remove.png"));

        remove_all_button.setText("Remove all");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(desc_label)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, add_button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, remove_all_button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, remove_button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(shared_folders_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(388, 388, 388)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(desc_label)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(add_button)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(remove_button)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(remove_all_button))
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(shared_folders_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(154, 154, 154))
        );
    }
    
    public ChosenFolders getChosenFolders() {
    	
    	return chosen_folders;
    }
 
}