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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.JMRunnable;
import org.jmule.core.JMThread;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2011/03/27 16:51:29 $$
 */
public class GUIUpdater extends JMThread {

	private static GUIUpdater instance = null;
	
	public static GUIUpdater getInstance() {
		
		if (instance==null) 
			instance = new GUIUpdater();
		
		return instance;
		
	}
	
	private boolean stop = false;
	
	private List<Refreshable> refreshable_list = new CopyOnWriteArrayList<Refreshable>();
	
	private GUIUpdater() {
		super("GUI updater thread");
	}

	public void JMStop() {
		stop = true;
		interrupt();
		
	}
	
	public void addRefreshable(Refreshable refreshable) {
		if (refreshable_list.contains(refreshable)) return;
		refreshable_list.add(refreshable);
	}

	public void removeRefreshable(Refreshable refreshable) {
		if (refreshable_list.contains(refreshable))
			refreshable_list.remove(refreshable);
	}

	protected boolean process_refreshables = false; 
	
	public void run() {
		process_refreshables = false; 
		while(!stop) {
			
			try {
				Thread.sleep(SWTConstants.GUI_UPDATE_INTERVAL);
			} catch (InterruptedException e) {
				if (stop) return ;
			}
			if (!process_refreshables) {
				SWTThread.getDisplay().syncExec(new JMRunnable() {
				public void JMRun() {
					process_refreshables = true;
					for(Refreshable refreshable : refreshable_list) {
						try {
							long enter = System.currentTimeMillis();
							//System.out.println("Enter : " + refreshable);
							refreshable.refresh();
							//System.out.println("Exit  : " + refreshable);
							long exit = System.currentTimeMillis();
							//System.out.println("Time : " + (exit - enter) + "\n" + refreshable);
						}catch(Throwable t) {
							t.printStackTrace();
						}
					}
					process_refreshables = false;
				}
			}); }
			
		}
		
	}
	
}
