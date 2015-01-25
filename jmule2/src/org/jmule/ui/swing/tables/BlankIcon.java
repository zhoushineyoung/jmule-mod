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
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * 
 * @author gregork
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:02 $$
 */
public class BlankIcon implements Icon {
	  private Color fillColor;
	  private int size;
	 
	  public BlankIcon() {
	    this(null, 11);
	  }
	 
	  public BlankIcon(Color color, int size) {
	    //UIManager.getColor("control")
	    //UIManager.getColor("controlShadow")
	    fillColor = color;
	 
	    this.size = size;    
	  }
	 
	  public void paintIcon(Component c, Graphics g, int x, int y) {
	    if (fillColor != null) {
	      g.setColor(fillColor);
	      g.drawRect(x, y, size-1, size-1);
	    }
	  }
	 
	  public int getIconWidth() {
	    return size;
	  }
	 
	  public int getIconHeight() {
	    return size;
	  }
}
