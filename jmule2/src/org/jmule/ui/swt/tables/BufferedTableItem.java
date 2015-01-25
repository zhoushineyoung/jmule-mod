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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

/**
 * @author TuxPaper
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:17 $$ 
 */
public interface BufferedTableItem
{

	public abstract String getText();

	public abstract boolean setText(String text);

	public abstract void setImage(Image img);

	public abstract void setRowForeground(Color color);

	public abstract boolean setForeground(Color color);

	public abstract boolean setForeground(int red, int green, int blue);

	public abstract Color getBackground();

	public abstract Rectangle getBounds();

	public abstract void refresh();

	public abstract void dispose();

	public abstract boolean isShown();

	public abstract boolean needsPainting();

	/** Paint the image only (no update needed)
	 */
	public abstract void doPaint(GC gc);

	/** Column location (not position) changed.  Usually due to a resize of
	 * a column in a position prior to this one.
	 */
	public abstract void locationChanged();

	public abstract int getPosition();

	/**
	 * 
	 */
	public abstract Image getBackgroundImage();

	/**
	 * @return
	 */
	public abstract Color getForeground();

}