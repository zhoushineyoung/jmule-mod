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
package org.jmule.ui.swt.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jmule.core.sharingmanager.Gap;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.ui.swt.SWTThread;

/**
 * Created on Aug 02 2008
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/01/04 11:06:45 $$
 */
public class GapListPainter {
	
	private GapList gap_list;
	private long file_size;
	
	private static Color gaplist_border_color  = new Color(SWTThread.getDisplay(), new RGB(194, 194, 194));
	private static Color gaplist_bg_color  = new Color(SWTThread.getDisplay(), new RGB(0, 128, 255)); // light blue
    private static Color gaplist_fg_color = new Color(SWTThread.getDisplay(), new RGB(255, 255, 255)); // white
    private static Color progress_bar_fg_color = new Color(SWTThread.getDisplay(), new RGB(0, 100, 199)); // dark blue
    private static Color progress_bar_bg_color = new Color(SWTThread.getDisplay(), new RGB(255, 255, 255)); // white
    private static Color separator_color = new Color(SWTThread.getDisplay(), new RGB(209,209,209));
    
	private static final int PROGRESS_BAR_HEIGHT = 3;
	private int margin_width = 4;
		
	public GapListPainter(GapList gapList,long fileSize) {
		gap_list = gapList;
		file_size = fileSize;
	}
	
	public void setData(GapList gapList, long fileSize) {
		gap_list = gapList;
		file_size = fileSize ;
	}
	
	public GapList getGapList() {
		return gap_list;
	}
	
	public long getFileSize() {
		return file_size;
	}
	
	public void setMarginWidth(int marginWidth) {
		margin_width = marginWidth;
	}
	
	public void draw(GC gc, int x,int y, int width, int height) {
		
		width  = width  - margin_width - 10;
		height = height - margin_width - 2;
		x += margin_width;
		y += margin_width;
		
		float k = (float)(width)/(float)(file_size);
		//Gaps
		gc.setBackground(gaplist_bg_color);
		gc.fillRectangle(new Rectangle(x,y,width,height));
		gc.setBackground(gaplist_fg_color);
		for(Gap gap : gap_list.getGaps()) {
			int startPos = Math.round((float)(gap.getStart() * k));
			int length = Math.round((float)((gap.getEnd() - gap.getStart()) * k));
			Rectangle rect = new Rectangle(x + startPos,y + 0,length,height);
			gc.fillRectangle(rect);
		}
		//Draw progress bar
		long downloaded = file_size - gap_list.byteCount();
		long progress = Math.round(((downloaded* 100f)/ (float) file_size));
		k = (float)(width)/100f;
		Rectangle progress_bar = new Rectangle(x, y,(int)( width), PROGRESS_BAR_HEIGHT);
		gc.setBackground(progress_bar_bg_color);
		gc.fillRectangle(progress_bar);
		progress_bar = new Rectangle(x,y,(int)(progress* k),PROGRESS_BAR_HEIGHT);
		gc.setBackground(progress_bar_fg_color);
		gc.fillRectangle(progress_bar);
		
		gc.setForeground(separator_color);
		gc.drawLine(x,y + PROGRESS_BAR_HEIGHT,x + width, y + PROGRESS_BAR_HEIGHT);
		
		gc.setForeground(gaplist_border_color);
		gc.drawRectangle(x, y, width, height);
	}
	static Shell shell;
	static GapListPainter gap_list_painter;
	public static void main(String... args) {
		Display display = Display.getCurrent();
		shell = new Shell(display);
		GapList gap_list = new GapList();
		gap_list.addGap(70, 90);
		gap_list.addGap(1, 20);
		gap_list.addGap(25, 40);
		gap_list_painter = new GapListPainter(gap_list,100); 
		FillLayout layout = new FillLayout();
		layout.marginHeight = 10;
		layout.marginWidth  = 10;
		shell.setLayout(layout);
		final Canvas canvas = new Canvas(shell,SWT.NONE);
		
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent arg0) {
				System.out.println(arg0.width+" : " + arg0.height);
				gap_list_painter.draw(arg0.gc, 0, 0, arg0.width, arg0.height);
			}
		});
		
		shell.setSize(500,100);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();

		
		
	}
	
}
