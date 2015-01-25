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

import org.jmule.core.JMConstants;
import org.jmule.ui.swing.UISwingImageRepository;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:07 $$
 */
public class FinishPanel extends javax.swing.JPanel {

    public FinishPanel() {
        initComponents();
    }

    private void initComponents() {

        wizard_finish_logo = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        wizard_finish_logo.setFont(new java.awt.Font("Dialog", 0, 12));
        wizard_finish_logo.setIcon( UISwingImageRepository.getIcon("wizard/wizard_finish_logo.png") );
        wizard_finish_logo.setText("<html><br><br><br>Congratulations ! You have successfully configured " 
        		                  + JMConstants.JMULE_NAME 
        		                  + " ver. " 
        		                  + JMConstants.CURRENT_JMULE_VERSION 
        		                  + "<br><br><center>Click <b>Finish</b> to perform the ultimately steps</center></html>");
        wizard_finish_logo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        wizard_finish_logo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        add(wizard_finish_logo);
    }

    private javax.swing.JLabel wizard_finish_logo;
}
