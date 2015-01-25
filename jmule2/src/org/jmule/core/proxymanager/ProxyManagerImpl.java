/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.proxymanager;

import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;

/**
 * Created on Aug 13, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/09/17 18:38:42 $
 */
public class ProxyManagerImpl extends JMuleAbstractManager implements ProxyManager {
	
	private boolean is_started = false;
	
	public boolean iAmStoppable() {
		return true;
	}

	
	public boolean isStarted() {
		
		return is_started;
	}


	 public void initialize() {
		 try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
	 }
	  
	  
	  public void start() {
		  try {
			super.start();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return ;
		}
		is_started = true;
	  }
	
	  public void shutdown() {
			try {
				super.shutdown();
			} catch (JMuleManagerException e) {
				e.printStackTrace();
				return ;
			}
			is_started = false;
	  }

}
