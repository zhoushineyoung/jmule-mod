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

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jmule.ui.swing.UISwingImageRepository;
import org.jmule.ui.swing.skin.WizardSkin;
import org.jmule.ui.swing.skin.WizardSkinFactory;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:09 $$
 */
public class TopPanel extends JPanel {


    private JLabel caption = new JLabel();
    private JLabel right_logo = new JLabel();
    private BorderLayout border_layout = new BorderLayout();
    private WizardSkin skin = WizardSkinFactory.getInstance();
	
    public TopPanel() {
    	
    	initComponents();
    }
    
    private void initComponents() {
       	this.setBackground(new java.awt.Color(255, 255, 255));
    	this.setLayout(border_layout);
    	caption.setText("nothing");
    	caption.setFont(skin.getLabelCaptionFont());
    	this.add(caption,BorderLayout.WEST);
    	
    	//right_logo.setIcon(new javax.swing.ImageIcon("/home/javajox/work/workspace/jMule_local/src/org/jmule/ui/resources/wizard_emule_logo.png"));
    	right_logo.setIcon(UISwingImageRepository.getIcon("wizard_emule_logo.png"));
    	this.add(right_logo,BorderLayout.EAST);
    }
    
    public void setCaption(String caption) {
    	
    	this.caption.setText("    " + caption);
    }
    
    public void setCaptionIcon(ImageIcon imageIcon) {
    	
    	this.caption.setIcon(imageIcon);
    }
	
}
