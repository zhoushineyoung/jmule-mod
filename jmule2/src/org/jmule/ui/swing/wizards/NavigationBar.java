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

import javax.swing.JPanel;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.swing.UISwingImageRepository;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/10/16 16:10:38 $$
 */
public class NavigationBar extends JPanel implements JMuleUIComponent  {

	private JMuleCore _core;
	private SetupWizard parent;
	
	public NavigationBar(SetupWizard parent) {
		
		this.parent = parent;
		getCoreComponents();
		initUIComponents();
	}
	
	public NavigationBar() {
		getCoreComponents();
		initUIComponents();
	}
	
	public void getCoreComponents() {
		
		  _core = JMuleCoreFactory.getSingleton();
		
	}
	
	public void initUIComponents() {

		this.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		
        backButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        finishButton = new javax.swing.JButton();

        backButton.setIcon(UISwingImageRepository.getIcon("arrow_left.png"));
        backButton.setText("Back");

        nextButton.setText("Next");
        nextButton.setIcon(UISwingImageRepository.getIcon("arrow_right.png"));
        nextButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        cancelButton.setText("Cancel");
        cancelButton.setIcon(UISwingImageRepository.getIcon("cancel.png"));

        finishButton.setText("Finish");
        finishButton.setIcon(UISwingImageRepository.getIcon("accept.png"));      
        
        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(110, Short.MAX_VALUE)
                .add(backButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(nextButton)
                .add(30, 30, 30)
                .add(cancelButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(finishButton)
                .add(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(finishButton)
                    .add(cancelButton)
                    .add(nextButton)
                    .add(backButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    private javax.swing.JButton backButton;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton finishButton;

	public javax.swing.JButton getBackButton() {
		return backButton;
	}

	public void setBackButton(javax.swing.JButton backButton) {
		this.backButton = backButton;
	}

	public javax.swing.JButton getNextButton() {
		return nextButton;
	}

	public void setNextButton(javax.swing.JButton nextButton) {
		this.nextButton = nextButton;
	}

	public javax.swing.JButton getCancelButton() {
		return cancelButton;
	}

	public void setCancelButton(javax.swing.JButton cancelButton) {
		this.cancelButton = cancelButton;
	}

	public javax.swing.JButton getFinishButton() {
		return finishButton;
	}

	public void setFinishButton(javax.swing.JButton finishButton) {
		this.finishButton = finishButton;
	}

}
