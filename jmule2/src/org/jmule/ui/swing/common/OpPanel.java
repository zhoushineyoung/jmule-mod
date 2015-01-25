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
package org.jmule.ui.swing.common;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * Created on Oct 5, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/16 17:35:11 $
 */
public class OpPanel extends JPanel {

	public enum button_type {
		OK,
		APPLY,
		CANCEL,
		CLOSE,
	}
	
	private button_type[] button_type_array;
	
	private JButton close_button = new JButton("Close");
	private JButton ok_button = new JButton("OK");
	private JButton cancel_button = new JButton("Cancel");
	private JButton apply_button = new JButton("Apply");
	
	public OpPanel(button_type[] button_type_array) {
		this.button_type_array = button_type_array;
		init();
	}
	
	private void init() {
		
		/*
		 * --------------------------------------------
		 * |                                          |
		 * |                                Close     |
		 * |                                          |
		 * --------------------------------------------
		 */
		if( ( button_type_array.length == 1 ) && 
			( button_type_array[0] == button_type.CLOSE ) ) {
			
			this.setPreferredSize(new java.awt.Dimension(386, 56));
			GridBagLayout thisLayout = new GridBagLayout();
			thisLayout.columnWidths = new int[] {7, 20, 7, 7, 7};
			thisLayout.rowHeights = new int[] {7};
			thisLayout.columnWeights = new double[] {0.1, 0.1, 0.1, 0.1, 0.1};
			thisLayout.rowWeights = new double[] {0.1};
			this.setLayout(thisLayout);
			{

				this.add(close_button, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(15, 0, 0, 5), 0, 0));
				close_button.setName("close_button");
			}
			thisLayout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1};
			thisLayout.rowHeights = new int[] {7, 7, 7, 7};
			thisLayout.columnWeights = new double[] {0.1, 0.1, 0.1, 0.1};
			thisLayout.columnWidths = new int[] {7, 7, 7, 7};
			
		} else 
		
		/*
		 * --------------------------------------------
		 * |                                          |
		 * |                     OK    Apply  Cancel  |
		 * |                                          |
		 * --------------------------------------------
		*/
		if( ( button_type_array.length == 3 ) && 
			( button_type_array[0] == button_type.OK ) &&
			( button_type_array[1] == button_type.APPLY ) &&
			( button_type_array[2] == button_type.CANCEL )) {
			
			GridBagLayout thisLayout = new GridBagLayout();
			thisLayout.rowWeights = new double[] {0.1};
			thisLayout.rowHeights = new int[] {7};
			thisLayout.columnWeights = new double[] {0.1, 0.1, 0.1, 0.1};
			thisLayout.columnWidths = new int[] {7, 7, 7, 7};
			this.setLayout(thisLayout);
			this.setPreferredSize(new java.awt.Dimension(394, 49));
			this.add(ok_button, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
            this.add(apply_button, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
            this.add(cancel_button, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
		}
		
	}
	
	public JButton getCloseButton() {
		
		return close_button;
	}
	
	public JButton getOKButton() {
		
		return ok_button;
	}
	
	public JButton getApplyButton() {
		
		return apply_button;
	}
	
	public JButton getCancelButton() {
		
		return cancel_button;
	}
	
}
