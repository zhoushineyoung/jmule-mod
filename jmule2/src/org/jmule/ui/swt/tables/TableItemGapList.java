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

import org.eclipse.swt.graphics.GC;
import org.jmule.ui.swt.common.GapListPainter;

/**
 * Created on Aug 5, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/09/07 15:24:57 $
 */
public class TableItemGapList extends BufferedTableRowCustomControl{

	private GapListPainter painter;
		
	public TableItemGapList(int columnID, GapListPainter painter) {
		super(columnID);
		this.painter = painter;
	}

	public void draw(GC gc, int x,int y, int width, int height) {
		painter.draw(gc, x, y, width, height);
	}


	public void setData(BufferedTableRowCustomControl customControl) {
		GapListPainter gap_list_painter = (GapListPainter) customControl.getControl();
		painter.setData(gap_list_painter.getGapList(), gap_list_painter.getFileSize());
	}

	public Object getControl() {
		return painter;
	}

	
}
