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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import org.jmule.ui.CommonUIPreferences;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.swing.ImgRep;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/10/16 16:10:38 $$
 */
public class UIChooser extends WizardPanel {

	private JPanel swt_panel = new JPanel();
	private JPanel swing_panel = new JPanel();
	private JTextArea swing_text_area = new JTextArea();
	private JTextArea swt_text_area = new JTextArea();
	private JRadioButton swing_radio_button = new JRadioButton();
	private JRadioButton swt_radio_button = new JRadioButton();
	private JLabel swing_logo_label = new JLabel();
	private JLabel eclipse_logo_label = new JLabel();
	private ButtonGroup radio_group = new ButtonGroup();
	
	public UIChooser() {
		
		init();
	}
	
	private void init() {
		GridBagLayout main_grid_bag_layout = new GridBagLayout();
		main_grid_bag_layout.rowWeights = new double[] {0.1, 0.1};
		main_grid_bag_layout.rowHeights = new int[] {7, 7};
		main_grid_bag_layout.columnWeights = new double[] {0.1};
		main_grid_bag_layout.columnWidths = new int[] {7};
		this.setPreferredSize(new java.awt.Dimension(497, 282));
		this.setLayout(main_grid_bag_layout);
		
		swt_panel = new JPanel();
		GridBagLayout swt_panel_layout = new GridBagLayout();
		swt_panel_layout.columnWidths = new int[] {17, 119, 95, 7};
		swt_panel_layout.rowHeights = new int[] {7};
		swt_panel_layout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		swt_panel_layout.rowWeights = new double[] {0.1};
		this.add(swt_panel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(20, 0, 20, 10), 0, 0));
		swt_panel.setLayout(swt_panel_layout);
		eclipse_logo_label = new JLabel();
		eclipse_logo_label.setIcon(ImgRep.getIcon("wizard/eclipse_logo.jpg"));
		eclipse_logo_label.setOpaque(true);
		eclipse_logo_label.setBackground(Color.WHITE);
		swt_panel.add(eclipse_logo_label, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		eclipse_logo_label.setName("eclipse_logo_label");
		swt_radio_button = new JRadioButton("SWT");
		swt_radio_button.setSelected(true);
		swt_radio_button.addActionListener(new ActionListener() {
		        	 public void actionPerformed(ActionEvent event) {
		        		 swt_panel.setBackground(Color.GRAY);
		        		 swing_panel.setBackground(new Color(238, 238, 238));		        		 
		        		 swt_radio_button.setBackground(Color.GRAY);
		        		 swing_radio_button.setBackground(new Color(238, 238, 238));
		        		 swt_text_area.setBackground(Color.GRAY);
		        		 swt_text_area.setForeground(Color.WHITE);
		        		 
		        		 swing_text_area.setBackground(new Color(238, 238, 238));
		        		 swing_text_area.setForeground(Color.BLACK);
		        	 }
		        });
		swt_panel.add(swt_radio_button, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		swt_radio_button.setName("swt_radio_button");
		swt_text_area = new JTextArea();
		swt_text_area.setBackground(swt_panel.getBackground());
		swt_text_area.setEditable(false);
		swt_text_area.setText("Standard Widget Toolkit - is a native\nplatform independent user interface\nframework\nMore info on http://eclipse.org/swt");
		swt_panel.add(swt_text_area, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		swing_panel = new JPanel();
	    GridBagLayout swing_panel_layout = new GridBagLayout();
		swing_panel_layout.columnWidths = new int[] {16, 121, 96, 7};
		swing_panel_layout.rowHeights = new int[] {7};
		swing_panel_layout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		swing_panel_layout.rowWeights = new double[] {0.1};
		this.add(swing_panel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(20, 0, 20, 10), 0, 0));
		swing_panel.setLayout(swing_panel_layout);
		swing_logo_label = new JLabel();
		swing_logo_label.setIcon(ImgRep.getIcon("wizard/java_logo.jpg"));
		swing_logo_label.setOpaque(true);
		swing_logo_label.setBackground(Color.WHITE);
		swing_panel.add(swing_logo_label, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		swing_logo_label.setName("swing_logo_label");
		swing_radio_button = new JRadioButton("SWING");
		swing_radio_button.addActionListener(new ActionListener() {
		        	 public void actionPerformed(ActionEvent event) {
		        		 swt_panel.setBackground(new Color(238, 238, 238));
		        		 swing_panel.setBackground(Color.GRAY);
		        		 
		        		 swt_radio_button.setBackground(new Color(238, 238, 238));
		        		 swing_radio_button.setBackground(Color.GRAY);
		        		 
		        		 swt_text_area.setBackground(new Color(238, 238, 238));
		        		 swt_text_area.setForeground(Color.BLACK);
		        		 
		        		 swing_text_area.setBackground(Color.GRAY);
		        		 swing_text_area.setForeground(Color.WHITE);
		        	 }
		          });
		swing_panel.add(swing_radio_button, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		swing_text_area = new JTextArea();
		swing_text_area.setBackground(swing_panel.getBackground());
		swing_text_area.setEditable(false);
		swing_text_area.setText("Standard Java GUI provided by Sun\nMicrosystems. More info on\nhttp://en.wikipedia.org/wiki/Swing_(Java)");
		swing_panel.add(swing_text_area, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		swing_text_area.setName("swing_text_area");
		
		radio_group.add(swt_radio_button);
		radio_group.add(swing_radio_button);
		
		if(CommonUIPreferences.getSingleton().getUIType().equals("SWT"))
		  swt_radio_button.doClick();
		else 
		  swing_radio_button.doClick();
	}

    public String getChosenUI() {
    	
    	     if( swt_radio_button.isSelected() ) return JMuleUIManager.SWT_UI;
    	
    	else if( swing_radio_button.isSelected() ) return JMuleUIManager.SWING_UI;
    	     
    	//else if( com_line_button.isSelected() ) return JMuleUIManager.CONSOLE_UI;
    	
    	return null;
    }
    
}
