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
package org.jmule.ui.swing.maintabs;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jmule.ui.swing.Refreshable;
import org.jmule.ui.swing.SwingGUIUpdater;

/**
 *
 * Created on Oct 6, 2008
 * @author javajox
 * @version $Revision: 1.3 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/19 09:03:32 $
 */
public abstract class AbstractTab extends JPanel {

	protected JFrame parent;
	
	protected List<Refreshable> refreshable_components = new LinkedList<Refreshable>();
	private SwingGUIUpdater _updater = SwingGUIUpdater.getInstance();
	
	public AbstractTab(JFrame parent) {
		
		this.parent = parent;		
		
		/*this.addComponentListener(new ComponentAdapter() {
			
			 public void componentHidden(ComponentEvent evt) {
				 System.out.println("Component become hidden " + refreshable_components);
				 for(Refreshable refreshable : refreshable_components) 
					 _updater.removeRefreshable(refreshable);
			 }
			 
			 public void componentShown(ComponentEvent evt) {
				 System.out.println("Component become visible " + refreshable_components);
				 for(Refreshable refreshable : refreshable_components)
					 _updater.addRefreshable(refreshable);
			 }
		});*/
	}
	
	protected synchronized void registerRefreshable(Refreshable refreshable) {
		
		refreshable_components.add(refreshable);
	}
	
	public synchronized void registerAllRefreshables() {
		 for(Refreshable refreshable : refreshable_components) {
			 _updater.addRefreshable(refreshable);
		 }
	}
	
	public synchronized void deregisterAllRefreshables() {
		 for(Refreshable refreshable : refreshable_components) {
			 _updater.removeRefreshable(refreshable);
		 }
	}
	
}
