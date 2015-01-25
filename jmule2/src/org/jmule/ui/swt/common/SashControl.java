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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:45:24 $$
 */
public class SashControl {

	public static void createVerticalSash(final int limit,int init_percent, final Composite parent,final Composite composite1,final Composite composite2) {
		
		final Sash sash = new Sash (parent, SWT.VERTICAL | SWT.BORDER);
		
		FormData composite1_data = new FormData ();
		composite1_data.left = new FormAttachment (0,0);
		composite1_data.right = new FormAttachment (sash,0);
		composite1_data.top = new FormAttachment (0,0);
		composite1_data.bottom = new FormAttachment (100,0);
		
		composite1.setLayoutData(composite1_data);
		
		FormData composite2_data = new FormData ();
		composite2_data.left = new FormAttachment (sash, 0);
		composite2_data.right = new FormAttachment (100, 0);
		composite2_data.top = new FormAttachment (0, 0);
		composite2_data.bottom = new FormAttachment (100, 0);
		
		composite2.setLayoutData (composite2_data);
		
		final FormData sash_data = new FormData ();
		sash_data.left = new FormAttachment (init_percent,0);
		sash_data.top = new FormAttachment (0,0);
		sash_data.bottom = new FormAttachment (100,0);
		
		sash.setLayoutData(sash_data);
		
		sash.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				Rectangle sashRect = sash.getBounds ();
				Rectangle shellRect = parent.getClientArea ();
				int right = shellRect.width - sashRect.width - limit;
				e.x = Math.max (Math.min (e.x, right), limit);
				if (e.x != sashRect.x)  {
					sash_data.left = new FormAttachment (0, e.x);
					//sashData_messages.right = new FormAttachment (0, e.x+4);
//					parent.layout();
//					composite1.layout();
//					composite2.layout();
				}
			}
		});
		
		sash.addListener(SWT.MouseUp, new Listener () {
			public void handleEvent (Event e) {
				
				parent.layout();
				composite1.layout();
				composite2.layout();
				
			} });
		
	}
	
	public static void createHorizontalSash(final int limit,int init_percent, final Composite parent,final Composite composite1,final Composite composite2) {
	
		final Sash sash = new Sash (parent, SWT.HORIZONTAL | SWT.BORDER);
		
		FormData composite1_data = new FormData ();
		composite1_data.left = new FormAttachment (0);
		composite1_data.right = new FormAttachment (100);
		composite1_data.top = new FormAttachment (0);
		composite1_data.bottom = new FormAttachment (sash);
		composite1.setLayoutData (composite1_data);
		
		FormData composite2_data = new FormData ();
		composite2_data.left = new FormAttachment (0, 0);
		composite2_data.right = new FormAttachment (100, 0);
		composite2_data.top = new FormAttachment (sash, 0);
		composite2_data.bottom = new FormAttachment (100, 0);
		composite2.setLayoutData (composite2_data);
		
		final FormData sash_data = new FormData ();
		sash_data.left = new FormAttachment (0);
		sash_data.right = new FormAttachment (100);
		sash_data.top = new FormAttachment (init_percent, 0);
		//sash_data.bottom = new FormAttachment (init_percent+2, 0);
		sash.setLayoutData (sash_data);
		
		sash.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				Rectangle sashRect = sash.getBounds ();
				Rectangle shellRect = parent.getClientArea ();
				int right = shellRect.height - sashRect.height - limit;
				e.y = Math.max (Math.min (e.y, right), limit);
				if (e.y != sashRect.y)  {
					sash_data.top = new FormAttachment (0,e.y);
					//sash_data.bottom = new FormAttachment (0,e.y+4);
					//parent.layout();
					//composite1.layout();
					//composite2.layout();
				}
			}
		});
		
		sash.addListener(SWT.MouseUp, new Listener () {
			public void handleEvent (Event e) {
				
				parent.layout();
				composite1.layout();
				composite2.layout();
				
			} });
		
		
	}
	
	public static void main(String... args) {
		Display display = new Display ();
		Shell shell = new Shell (display);

		Composite c1 = new Composite(shell,SWT.BORDER);
		Composite c2 = new Composite(shell,SWT.BORDER);
		
		//createVerticalSash(10,50,shell,c1,c2);
		createHorizontalSash(10,50,shell,c1,c2);
		
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();

	}
	
}
