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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:45:23 $$
 */
public class ScrolledContent extends ScrolledComposite {

	private Composite content;
	
	public ScrolledContent(Composite parent) {
		super(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		setExpandVertical(true);
		setExpandHorizontal(true);
		RowLayout layout = new RowLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);
	}
	
	/**
	 * Add a composite to ScrolledContent
	 * @param content composite
	 */
	public void setContent(Composite content) {
		setContent(content,true);
	}
	
	public void setContent(Composite content,boolean registerAllControls) {
		this.content = content;
		super.setContent(content);
		
		if (registerAllControls) {
			for(int i = 0;i<content.getChildren().length;i++) {
				Control c = content.getChildren()[i];
				addPaintListener(c);
			} 
		}
		
		content.addPaintListener(new PaintListener() {
		
			public void paintControl(PaintEvent arg0) {
				updateScroll();
			}
			
		});
	
	}

	/**
	 * Add paint listener to control, useful when you later(after call of setContent )
	 * add controls to composite.
	 * 
	 * @param control control.
	 */
	public void addPaintListener(Control control) {
		control.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent arg0) {
				updateScroll();
			}
			
		});
		
	}
	
	long last_call_time = System.currentTimeMillis();
	private void updateScroll() {
		
		if (System.currentTimeMillis() - last_call_time<100)
			return ;
		last_call_time = System.currentTimeMillis();
		int max_width = 0,max_height = 0;
		Control max_width_control = null,max_height_control = null;

		for(int i = 0;i<content.getChildren().length;i++) {
			Control c = content.getChildren()[i];
			
			int abs_width = c.getBounds().x+c.getBounds().width;
			int abs_height = c.getBounds().y+c.getBounds().height;
			
			if (abs_width>max_width) {
				max_width = abs_width;
				max_width_control = c;
			}
			
			if (abs_height>max_height) {
				max_height = abs_height;
				max_height_control = c;
				
			}
				
		}
		

		int parent_width = this.getParent().getBounds().width;
		int parent_height = this.getParent().getBounds().height;
		
		//System.out.println("Control size : "+parent_width+" "+parent_height);
		
		if (max_height>(parent_height-10)) max_height+=25;
		if (max_width>(parent_width-10)) max_width+=20;
		
		setMinHeight(max_height);
		setMinWidth(max_width);
	}
	
	protected void checkSubclass() {}

}
