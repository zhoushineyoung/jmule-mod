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
package org.jmule.core;

/**
 * Created on Aug 13, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/08/13 18:24:07 $
 */
public abstract class JMuleAbstractManager {

	private boolean is_started = false;
	private boolean is_initialized = false;
	private boolean is_stopped = true;
	
	private JMuleCore _core = JMuleCoreFactory.getSingleton();
	
	public JMuleAbstractManager() {
	}
	
	protected abstract boolean iAmStoppable();
	
	protected void initialize() throws JMuleManagerException {
		if (!iAmStoppable()) 
			if (!_core.isStarting())
				throw new JMuleManagerException("The manager can't be initialized because the core is not in starting phase");
		if (is_initialized)
			throw new JMuleManagerException("The manager can't be initialized twice");
		is_initialized = true;
	}
	
	protected void start() throws JMuleManagerException {
		if (!iAmStoppable())
			if (!_core.isStarting())
				throw new JMuleManagerException("The manager can't be started because the core is not in starting phase");
		if (!is_initialized)
			throw new JMuleManagerException("The manager can't be started without initialization");
		if (is_started)
			throw new JMuleManagerException("The manager can't be started twice");
		is_started = true;
		is_stopped = false;
	}
	
	protected void shutdown() throws JMuleManagerException {
		if (!iAmStoppable())
			if (!_core.isSopping())
				throw new JMuleManagerException("The manager can't be stopped because the core is not in stopping phase");
		if (!is_initialized)
			throw new JMuleManagerException("The manager can't be stopped without initialization");
		if (!is_started)
			throw new JMuleManagerException("The manager can't be stopped without start");
		if (is_stopped)
			throw new JMuleManagerException("The manager can't be stopped twice");
		is_stopped = true;
		is_started = false;
		is_initialized = false;
	}
	
}
