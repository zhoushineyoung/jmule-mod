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

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import org.jmule.ui.swing.SwingUtils;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/10/16 16:10:38 $$
 */
public class ExistedFoldersDialog extends javax.swing.JDialog {

    private JLabel desc_label;
    private JList existed_folders_list;
    private JScrollPane existed_folders_scroll_panel;
    private JButton ok_button;
    private ListModel list_model;
	
    public ExistedFoldersDialog(JFrame parent, boolean modal, AbstractListModel listModel) {
        super(parent, modal);
        this.list_model = listModel;
        initComponents();
    }
    
    public ExistedFoldersDialog(JDialog parent, boolean modal, AbstractListModel listModel) {
        super(parent, modal);
        this.list_model = listModel;
        initComponents();
    }

    private void initComponents() {

        desc_label = new JLabel();
        existed_folders_scroll_panel = new JScrollPane();
        existed_folders_list = new JList();
        ok_button = new JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Existed folders");
        setModal(true);
        setName("Existed folders");

        desc_label.setText("<html>The following folders already existed in<br>the sharing folders list :</html>");
        
        //existed_folders_list.setModel(new javax.swing.AbstractListModel() {
        //    String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
        //    public int getSize() { return strings.length; }
        //    public Object getElementAt(int i) { return strings[i]; }
        //});
        existed_folders_list.setModel(list_model);
        existed_folders_scroll_panel.setViewportView(existed_folders_list);

        ok_button.setText("Ok");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, existed_folders_scroll_panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, desc_label)))
                    .add(layout.createSequentialGroup()
                        .add(97, 97, 97)
                        .add(ok_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 79, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(desc_label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(existed_folders_scroll_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 251, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(ok_button)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        
        final ExistedFoldersDialog _this = this;
        ok_button.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		_this.setVisible(false);
        	}
        });
        SwingUtils.centerOnScreen( this );
        setResizable(false);
    }

}
