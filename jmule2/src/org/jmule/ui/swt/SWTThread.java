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
package org.jmule.ui.swt;

import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.widgets.Display;
import org.jmule.core.JMThread;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2011/03/27 16:51:29 $$
 */
public class SWTThread {

	private static class SWTThreadInstanceHolder {
		private static final SWTThread INSTANCE = new SWTThread();
	}
	
	public static SWTThread getInstance() {
		return SWTThreadInstanceHolder.INSTANCE;
	}
	
	private static Display display;
	private JMSWTThread swt_thread;
	private boolean display_created = false;
	
	private SWTThread() {
		swt_thread = new JMSWTThread();
	}
	
	public void initialize() {
		if (!swt_thread.isAlive()) {
			swt_thread.start();
		
			while(!display_created)
				try {
					Thread.sleep(100) ;
				} catch (InterruptedException e) {
					break;
				}
		}
		
	}
	
	public void start() {
		synchronized(swt_thread) {
			swt_thread.notify();
		}
	}
	
	public static Display getDisplay() {
		return display;
	}
	
	public void stop() {
		swt_thread.JMStop();
	}
	
	private class JMSWTThread extends JMThread {
	
		private boolean stop = false;
		
		public JMSWTThread() {
			
			super("SWT Thread");
			
		}
	
		public void run() {
		
			display_created = false;
			
			display = Display.getCurrent();
			if ( display == null ) {
				DeviceData data = new DeviceData();
			    data.tracking = true;
				display = new Display(data);
			}
		
			display_created = true;
			synchronized(this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					
				}
			}
		    
			while(!stop) {
				if (display.isDisposed()) return ; 
				try {
					while (!display.isDisposed ()) {
						
						if (!display.readAndDispatch ()) 
							display.sleep ();
					}
					}catch(Throwable t) {
						t.printStackTrace();
				} 
			}
		}
		
		public void JMStop() {
			display.dispose();
			stop = true;
		}
	
	}
	
}
