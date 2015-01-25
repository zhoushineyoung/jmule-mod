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

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jmule.core.JMConstants;
import org.jmule.ui.swing.BrowserLauncher;


/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:11 $$
 */
public class WelcomeMessage extends javax.swing.JPanel {

    private javax.swing.JLabel cvs_build_label;
    private javax.swing.JLabel welcome_label;
    private javax.swing.JLabel what_wizard_is_good_for_label;
	
    public WelcomeMessage() {
        initComponents();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        welcome_label = new javax.swing.JLabel();
        what_wizard_is_good_for_label = new javax.swing.JLabel();
        cvs_build_label = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridLayout(4, 1, 10, 0));

        welcome_label.setFont(new java.awt.Font("Serif", 1, 16)); 
        String welcome_text = "<html><center>&nbsp;&nbsp;&nbsp;Welcome to the "+ JMConstants.JMULE_NAME +"  Installation Wizard</center></html>";
        welcome_label.setText( welcome_text );
        add(welcome_label);

        what_wizard_is_good_for_label.setFont(new java.awt.Font("Dialog", 0, 14));
        String what_wizard = "<html><center>&nbsp;&nbsp;&nbsp;This wizard will guide you through the installation process of "+ JMConstants.JMULE_NAME +" ver. "+ JMConstants.JMULE_VERSION +"</center></html>";
        what_wizard_is_good_for_label.setText(what_wizard);
        add(what_wizard_is_good_for_label);

        if(JMConstants.IS_NIGHTLY_BUILD) {
        	  String str = "<html><center>&nbsp;&nbsp;&nbsp;Attention ! this is unstable <b>"+
        	               JMConstants.DEV_VERSION +
        	               " CVS build of " +
        	               JMConstants.JMULE_NAME + 
        	               "</b></center><br><center>Any comments or suggestions are welcome</center><br><center>Our forums <font color=blue>"+
        	               JMConstants.JMULE_FORUMS +
        	               "</font></center></html>";
        	  cvs_build_label.setFont(new java.awt.Font("Dialog", 0, 12));
              cvs_build_label.setForeground(java.awt.Color.red);
              cvs_build_label.setText(str);
              cvs_build_label.setToolTipText(JMConstants.JMULE_FORUMS);
              cvs_build_label.setCursor(new Cursor(Cursor.HAND_CURSOR));
              cvs_build_label.addMouseListener(new MouseAdapter() {
            	  public void mouseClicked(MouseEvent event) {
            		  BrowserLauncher.openURL(JMConstants.JMULE_FORUMS);
            	  }
              });
              add(cvs_build_label);
        }
    }

}
