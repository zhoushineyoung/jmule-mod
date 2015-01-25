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
package org.jmule.ui.swing.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * Created on Sep 1, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/16 17:35:11 $
 */
public class JMTableCellRenderer extends JLabel implements TableCellRenderer { //extends DefaultTableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		this.setOpaque(true);
		this.setFont(new Font("Dialog",0,12));
		//this.setBorder(new EmptyBorder(0, 0, 0, 0));
		//this.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		if(isSelected) {
			  this.setForeground(table.getSelectionForeground());
			  this.setBackground(table.getSelectionBackground());
		} else {
			  //this.setForeground(table.getForeground());
			  //this.setBackground(table.getBackground());
			 if ( (row % 2) == 0 ) {
			       this.setBackground(new Color(255,255,255));
                 //  this.setBorder(new LineBorder(new java.awt.Color(255,255,255), 1, false));
			 } else {
			       this.setBackground(new Color(240,240,240));
			     //  this.setBorder(new LineBorder(new java.awt.Color(240,240,240), 1, false));
			 }
		}
	    return this;
	}

}
