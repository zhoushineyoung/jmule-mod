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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Created on Nov 13, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/05/09 11:37:43 $
 */
public abstract class ExSlider extends Canvas {

	private double upPercent = 0;
	private double downPercent = 0;
	
	private Color barBorderColor = null;
	private Color barBackgroundColor = null ;
	private Color barUsedSegmentColor = null;
	private Color pointerColor = null;
	
	private int bar_height = 5;
	private int pointer_height = 10;
	private int pointer_width = 5;
	private int bar_margin = 5;
	private int draw_offset = 10;
	
	private enum SelectedPointer { UP, DOWN };
	
	private boolean mouse_down = false;
	
	private List<ExSliderModifyListener> listener_list = new LinkedList<ExSliderModifyListener>();
	
	public ExSlider(Composite parent) {
		super(parent, SWT.NONE);
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent arg0) {
				draw();
			}
		});
		addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent arg0) {
			}

			public void mouseDown(MouseEvent arg0) {
				if (getSelectedPointer(arg0.x,arg0.y)!=null) 
					mouse_down = true;
				else
					mouse_down = false;
			}

			public void mouseUp(MouseEvent arg0) {
				mouse_down = false;
			}
		});
		
		addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent arg0) {
				if (mouse_down) {
					SelectedPointer type = getSelectedPointer(arg0.x,arg0.y);
					if (type == null) return ;
					double value = (100D * (arg0.x-draw_offset)) / getBarWidth();
					if (type == SelectedPointer.DOWN) 
						if (validate(getUpValue(), value)) {
							setDownValue(value);
							notifyListeners(SelectedPointer.DOWN);
						}
					if (type == SelectedPointer.UP)
						if (validate(value, getDownValue())) {
							setUpValue(value);
							notifyListeners(SelectedPointer.UP);
						}
					
				}
			}
		});
	}
	
	private int getBarHeight() {
		return bar_height;
	}
	private int getBarWidth() {
		return getClientArea().width - draw_offset*2;
	}
	
	private int getUpCoord() {
		return ((int) ((getBarWidth() * upPercent) / 100))+draw_offset;
	}
	
	private int getDownCoord() {
		return ((int) ((getBarWidth() * downPercent) / 100)) + draw_offset;
	}
	
	private int getX() { 
		return draw_offset;
	}
	
	private int getY() {
		return pointer_height;
	}
	
	private void draw() {
		GC gc = new GC(this);
		
		int x = getX();
		int y = getY();
		int height = getBarHeight();
		int width = getBarWidth(); 
		
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		gc.fillRectangle(0, y-pointer_height, getClientArea().width, pointer_height*2 + bar_height);
		
		// draw bar
		gc.setBackground(barBackgroundColor);
		gc.fillRectangle(x - bar_margin, y,width + bar_margin*2,height);
		
		// draw used segment
		gc.setBackground(barUsedSegmentColor);
		int up_coord = getUpCoord();
		int down_coord = getDownCoord();
		gc.fillPolygon(new int[] { up_coord,y,down_coord,y,down_coord,y + bar_height, up_coord, y + bar_height});
		
		// draw pointers
		gc.setBackground(pointerColor);
		gc.fillPolygon(new int[] { up_coord, y, up_coord + pointer_width, y - pointer_height, up_coord - pointer_width, y - pointer_height});
		int y2 = y + bar_height;
		gc.fillPolygon(new int[] { down_coord, y2, down_coord + pointer_width, y2 + pointer_height, down_coord - pointer_width, y2 + pointer_height});
		gc.dispose();
	}

	private SelectedPointer getSelectedPointer(int x,int y) {
		int up_coord = getUpCoord();
		int down_coord = getDownCoord();
		Rectangle area; 
		area = new Rectangle(up_coord - pointer_width,getY() - pointer_height, pointer_width*2,pointer_height);
		if (area.contains(x, y)) return SelectedPointer.UP;
		area = new Rectangle(down_coord - pointer_width,getY() + bar_height, pointer_width * 2 , pointer_height);
		if (area.contains(x, y)) return SelectedPointer.DOWN;
		return null;
	}
	
	public void setUpValue(double upValue) {
		if (upValue > 100) upValue = 100D;
		if (upValue < 0) upValue = 0;
		this.upPercent = upValue;
		draw();
		//notifyListeners();
	}
	
	public void setDownValue(double downValue) {
		if (downValue > 100) downValue = 100D;
		if (downValue < 0) downValue = 0;
		this.downPercent = downValue;
		draw();
		//notifyListeners();
	}
	
	public double getDownValue() { return downPercent; }
	public double getUpValue() { return upPercent; }
	
	public abstract boolean validate(double upValue,double downValue);
	
	public void addModifyListener(ExSliderModifyListener listener) {
		listener_list.add(listener);
	}
	
	public void removeModifyListener(ExSliderModifyListener listener) {
		listener_list.remove(listener);
	}
	
	private void notifyListeners(SelectedPointer pointer) {
		for(ExSliderModifyListener listener : listener_list)
			if (pointer == SelectedPointer.UP)
				listener.upValueChanged(getUpValue());
			else
				listener.downValueChanged(getDownValue());
	}
	
	public Color getBarBorderColor() {
		return barBorderColor;
	}

	public void setBarBorderColor(Color barBorderColor) {
		this.barBorderColor = barBorderColor;
	}

	public Color getBarBackgroundColor() {
		return barBackgroundColor;
	}

	public void setBarBackgroundColor(Color barBackgroundColor) {
		this.barBackgroundColor = barBackgroundColor;
	}

	public Color getBarUsedSegment() {
		return barUsedSegmentColor;
	}

	public void setBarUsedSegmentColor(Color barUsedSegment) {
		this.barUsedSegmentColor = barUsedSegment;
	}

	public Color getPointerColor() {
		return pointerColor;
	}

	public void setPointerColor(Color pointerColor) {
		this.pointerColor = pointerColor;
	}
	
}
