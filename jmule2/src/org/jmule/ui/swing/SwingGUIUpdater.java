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
package org.jmule.ui.swing;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

import org.jmule.core.JMRunnable;
import org.jmule.core.JMThread;


/**
 *
 * Created on Aug 28, 2008
 * @author javajox
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary255 $ on $Date: 2010/05/15 18:22:31 $
 */
public class SwingGUIUpdater extends JMThread {

	private static SwingGUIUpdater instance = null;
	
	public static SwingGUIUpdater getInstance() {
		
		if (instance==null) 
			instance = new SwingGUIUpdater();
		
		return instance;
		
	}
	
	private boolean stop = false;
	
	private List<Refreshable> refreshable_list = new CopyOnWriteArrayList<Refreshable>();
	
	private SwingGUIUpdater() {
		super("SWING GUI updater thread");
	}

	public void JMStop() {
		stop = true;
		refreshable_list.clear();
		interrupt();
		
	}
	
	public void addRefreshable(Refreshable refreshable) {
		refreshable_list.add(refreshable);
	}

	public void removeRefreshable(Refreshable refreshable) {
		refreshable_list.remove(refreshable);
	}

	protected boolean process_refreshables = false; 
	
	public void run() {
		process_refreshables = false; 
		while(!stop) {
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				if (stop) return ;
			}
			if (!process_refreshables) {
			SwingUtilities.invokeLater(new JMRunnable() {
				public void JMRun() {
					process_refreshables = true;
					for(Refreshable refreshable : refreshable_list) {
						try {
							refreshable.refresh();
						}catch(Throwable t) { t.printStackTrace(); }
					}
					process_refreshables = false;
				}
			}); }
			
		}
		
	}
	
}

