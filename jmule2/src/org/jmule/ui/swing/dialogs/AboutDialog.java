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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jmule.core.JMConstants;
import org.jmule.ui.UIConstants;
import org.jmule.ui.swing.BrowserLauncher;
import org.jmule.ui.swing.ImgRep;
import org.jmule.ui.swing.common.GradientPanel;

/**
 *
 * Created on Sep 4, 2008
 * @author javajox
 * @version $Revision: 1.4 $
 * Last changed by $Author: javajox $ on $Date: 2010/01/13 16:05:51 $
 */
public class AboutDialog  extends javax.swing.JDialog {

    private javax.swing.JPanel bottom_panel;
    private javax.swing.JLabel bug_reports_label;
    private javax.swing.JButton close_button;
    private javax.swing.JLabel downloads_label;
    private javax.swing.JPanel general_panel;
    private javax.swing.JLabel home_page_label;
    private javax.swing.JPanel internet_panel;
    private javax.swing.JLabel jmule_icon;
    private javax.swing.JLabel jmule_license_label;
    private javax.swing.JLabel jmule_name_label;
    private javax.swing.JLabel jmule_shot_desc_label;
    private javax.swing.JLabel jmule_version_label;
    private javax.swing.JPanel license_panel;
    private javax.swing.JScrollPane license_scroll_pane;
    private javax.swing.JTextArea license_text_area;
    private javax.swing.JLabel open_support_label;
    private javax.swing.JLabel project_forums_label;
    private javax.swing.JLabel sf_web_page_label;
    private javax.swing.JTabbedPane tabbed_pane;
    private GradientPanel top_panel;
	
    
    // new -----
    private javax.swing.JLabel copyright_team;
    private javax.swing.JLabel forum_jmule_org_label;
    private javax.swing.JLabel forums_label;
    private javax.swing.JLabel jmule_org_label;
    private javax.swing.JLabel jmule_version;
    private javax.swing.JScrollPane scroll_pane;
    private javax.swing.JTextPane text_pane;
    private javax.swing.JLabel web_site_label;
    // end new
    
    class PropertiesPanel extends JPanel {
    	
    	private JScrollPane scroll_pane;
    	private JTable table;
    	
    	public PropertiesPanel() {
    		
    		this.setLayout(new GridLayout(1,1));
    		
    		String[] column_names = { "Property", "Value" };
    		Set keys = System.getProperties().keySet();
    		Object[][] data = new Object[keys.size()][2];
    		int i = 0;
    		for(Object key : keys) {
                String value = (String) key;
                data[i][0] = key;
                data[i][1] = System.getProperty(value);
                ++i;
            }
    		
    		table = new JTable(data,column_names);
    		scroll_pane = new JScrollPane();
    		scroll_pane.setViewportView(table);
    		this.add(scroll_pane);
    	}
    }
  
    class NightlyBuildPanel extends JPanel {
    	
    	private JLabel label_icon;
    	
    	public NightlyBuildPanel() {
    		
    		label_icon = new JLabel();
    		label_icon.setForeground(Color.RED);
            label_icon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            label_icon.setIcon(ImgRep.getIcon("bomb.png"));
            label_icon.setText("<html><center>Attention !!! This is highly unstable nightly build of JMule,<br> your feedback on our forums FORUM.JMULE.ORG<br> will be greatly appreciated</center></html>");
            label_icon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            label_icon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            
            this.setLayout(new GridLayout(1,1));
            this.add(label_icon);
    		
    	}
    	
    }
    
    
    
    public AboutDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setTitle("About");
        initComponents();
        this.setSize(500, 400);
    }

    private void initComponents() {

        top_panel = new GradientPanel(new java.awt.Color(255, 140, 5), new java.awt.Color(255, 255, 255));
        jmule_name_label = new javax.swing.JLabel();
        jmule_version_label = new javax.swing.JLabel();
        jmule_icon = new javax.swing.JLabel();
        tabbed_pane = new javax.swing.JTabbedPane();
        general_panel = new javax.swing.JPanel();
        internet_panel = new javax.swing.JPanel();
        home_page_label = new javax.swing.JLabel();
        sf_web_page_label = new javax.swing.JLabel();
        project_forums_label = new javax.swing.JLabel();
        bug_reports_label = new javax.swing.JLabel();
        open_support_label = new javax.swing.JLabel();
        downloads_label = new javax.swing.JLabel();
        jmule_shot_desc_label = new javax.swing.JLabel();
        jmule_license_label = new javax.swing.JLabel();
        license_panel = new javax.swing.JPanel();
        license_scroll_pane = new javax.swing.JScrollPane();
        license_text_area = new javax.swing.JTextArea();
        bottom_panel = new javax.swing.JPanel();
        close_button = new javax.swing.JButton();
        JPanel description_panel = new JPanel();


        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        description_panel.setLayout(new GridLayout(1,1));
        
        top_panel.setBackground(new java.awt.Color(255, 153, 0));
        top_panel.setPreferredSize(new java.awt.Dimension(100, 70));

        jmule_name_label.setFont(new java.awt.Font("Dialog", 1, 14));
        jmule_name_label.setText(JMConstants.JMULE_NAME);

        jmule_version_label.setFont(new java.awt.Font("Dialog", 0, 12));
        jmule_version_label.setText("Version: " + JMConstants.CURRENT_JMULE_VERSION);

        jmule_icon.setIcon(ImgRep.getIcon("jmule_logo.png"));

        org.jdesktop.layout.GroupLayout top_panelLayout = new org.jdesktop.layout.GroupLayout(top_panel);
        top_panel.setLayout(top_panelLayout);
        top_panelLayout.setHorizontalGroup(
            top_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(top_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(top_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jmule_name_label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jmule_version_label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 201, Short.MAX_VALUE)
                .add(jmule_icon)
                .addContainerGap())
        );
        top_panelLayout.setVerticalGroup(
            top_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(top_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(top_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(top_panelLayout.createSequentialGroup()
                        .add(jmule_name_label)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jmule_version_label))
                    .add(jmule_icon))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        getContentPane().add(top_panel, java.awt.BorderLayout.PAGE_START);

        internet_panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Internet", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 14), new java.awt.Color(0, 51, 255))); // NOI18N
        internet_panel.setLayout(new java.awt.GridLayout(2, 3, 20, 15));

        home_page_label.setText("      Home page");
        internet_panel.add(home_page_label);

        sf_web_page_label.setText("SF Web page");
        internet_panel.add(sf_web_page_label);

        project_forums_label.setText("Project Forums");
        internet_panel.add(project_forums_label);

        bug_reports_label.setText("      Bug reports");
        internet_panel.add(bug_reports_label);

        open_support_label.setText("Open support");
        internet_panel.add(open_support_label);

        downloads_label.setText("Downloads");
        internet_panel.add(downloads_label);

        jmule_shot_desc_label.setText("JMule - Java file sharing client for eDonkey2000 networks");

        jmule_license_label.setText("Licensed unde the GNU General Public License");

        /*org.jdesktop.layout.GroupLayout general_panelLayout = new org.jdesktop.layout.GroupLayout(general_panel);
        general_panel.setLayout(general_panelLayout);
        general_panelLayout.setHorizontalGroup(
            general_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(internet_panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, general_panelLayout.createSequentialGroup()
                .addContainerGap(61, Short.MAX_VALUE)
                .add(general_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jmule_license_label)
                    .add(jmule_shot_desc_label))
                .add(60, 60, 60))
        );
        general_panelLayout.setVerticalGroup(
            general_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, general_panelLayout.createSequentialGroup()
                .addContainerGap(89, Short.MAX_VALUE)
                .add(jmule_shot_desc_label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jmule_license_label)
                .add(79, 79, 79)
                .add(internet_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );*/
        
        jmule_version = new javax.swing.JLabel();
        copyright_team = new javax.swing.JLabel();
        scroll_pane = new javax.swing.JScrollPane();
        text_pane = new javax.swing.JTextPane();
        web_site_label = new javax.swing.JLabel();
        jmule_org_label = new javax.swing.JLabel();
        forums_label = new javax.swing.JLabel();
        forum_jmule_org_label = new javax.swing.JLabel();

        jmule_version.setText("JMule " + JMConstants.CURRENT_JMULE_VERSION);

        copyright_team.setText("Copyright (c) 2007-2010 JMule Team");

        text_pane.setBackground(new java.awt.Color(238, 238, 238));
        text_pane.setText("JMule - Java multi-platform file sharing client for eDonkey 2000 networks, licensed unde the GNU General Public License. The client is completely free and allows you to share files with anybody in the world.\n");
        text_pane.setEditable(false);
        scroll_pane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scroll_pane.setViewportView(text_pane);

        web_site_label.setText("Web site :");

        jmule_org_label.setText("http://jmule.org");
        
        jmule_org_label.addMouseListener(new MouseAdapter() {
			 public void mouseClicked(MouseEvent event) {
				 BrowserLauncher.openURL(JMConstants.JMULE_WEB_SITE);
			 }
			 public void mouseEntered(MouseEvent event) {
				 jmule_org_label.setForeground(Color.BLUE);
				 setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
			 }
			 public void mouseExited(MouseEvent event) {
				 jmule_org_label.setForeground(Color.BLACK);
				 setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
			 }
		});

        forums_label.setText("Forums  :");

        forum_jmule_org_label.setText("http://forum.jmule.org");
        
        forum_jmule_org_label.addMouseListener(new MouseAdapter() {
			 public void mouseClicked(MouseEvent event) {
				 BrowserLauncher.openURL(JMConstants.JMULE_FORUMS);
			 }
			 public void mouseEntered(MouseEvent event) {
				 forum_jmule_org_label.setForeground(Color.BLUE);
				 setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
			 }
			 public void mouseExited(MouseEvent event) {
				 forum_jmule_org_label.setForeground(Color.BLACK);
				 setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
			 }
		});
        
        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(general_panel);
        general_panel.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(scroll_pane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jmule_version))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(forums_label)
                                .add(18, 18, 18)
                                .add(forum_jmule_org_label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(web_site_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jmule_org_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 181, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(copyright_team, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 253, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(30, 30, 30)
                .add(jmule_version)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(copyright_team)
                .add(26, 26, 26)
                .add(scroll_pane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(web_site_label)
                    .add(jmule_org_label))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(forums_label)
                    .add(forum_jmule_org_label))
                .addContainerGap(68, Short.MAX_VALUE))
        );

        tabbed_pane.addTab("General", general_panel);

        if(JMConstants.IS_NIGHTLY_BUILD) tabbed_pane.addTab("Nightly build", new NightlyBuildPanel());
        
        tabbed_pane.addTab("Properties", new PropertiesPanel());
        
        license_panel.setLayout(new java.awt.CardLayout());

        license_text_area.setEditable(false);
        license_text_area.setColumns(20);
        license_text_area.setRows(5);
        license_text_area.setFont(new Font("Courir", Font.PLAIN, 12));
        license_text_area.setText(UIConstants.GNU_LICENSE);
        license_scroll_pane.setViewportView(license_text_area);
        
        license_text_area.setCaretPosition(0);
        
        license_panel.add(license_scroll_pane, "card2");

        tabbed_pane.addTab("License", license_panel);

        getContentPane().add(tabbed_pane, java.awt.BorderLayout.CENTER);

        bottom_panel.setPreferredSize(new java.awt.Dimension(100, 45));

        close_button.setText("Close");
        close_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_buttonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout bottom_panelLayout = new org.jdesktop.layout.GroupLayout(bottom_panel);
        bottom_panel.setLayout(bottom_panelLayout);
        bottom_panelLayout.setHorizontalGroup(
            bottom_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, bottom_panelLayout.createSequentialGroup()
                .addContainerGap(369, Short.MAX_VALUE)
                .add(close_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        bottom_panelLayout.setVerticalGroup(
            bottom_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(bottom_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(close_button)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(bottom_panel, java.awt.BorderLayout.PAGE_END);
        
        pack();
    }

    private void close_buttonActionPerformed(java.awt.event.ActionEvent evt) {                                             
          this.setVisible(false);
    }                                            
}