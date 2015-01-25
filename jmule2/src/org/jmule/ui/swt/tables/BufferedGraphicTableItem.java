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
package org.jmule.ui.swt.tables;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:18 $$
 */
public abstract interface BufferedGraphicTableItem
{
	public abstract int getMarginWidth();

	public abstract int getMarginHeight();

	public abstract void setMargin(int width, int height);

	/** Orientation of cell.  SWT.LEFT, SWT.RIGHT, SWT.CENTER, or SWT.FILL.
	 * When SWT.FILL, update() will be called when the size of the cell has 
	 * changed.
	 */
	public abstract void setOrientation(int orientation);

	public abstract int getOrientation();

	public abstract Point getSize();

	public abstract boolean setGraphic(Image img);

	public abstract Image getGraphic();

	public abstract void invalidate();
}
