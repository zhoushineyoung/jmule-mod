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
package org.jmule.core.aspects;

import java.util.logging.Logger;

import org.jmule.core.networkmanager.NetworkManagerImpl;
import org.jmule.core.utils.Misc;
/**
 * Created on Sep 18, 2009
 * @author binary256
 * @version $Revision: 1.4 $
 * Last changed by $Author: binary255 $ on $Date: 2009/12/11 14:45:40 $
 */
public privileged aspect NetworkManagerLogger {
	private Logger log = Logger.getLogger("org.jmule.core.networkmanager.NetworkManager");
	
	after() throwing (Throwable t): execution (* NetworkManagerImpl.*(..)) {
		log.warning(Misc.getStackTrace(t));
	}
	
	/*before() : execution(* NetworkManagerImpl.*(..)) {
		String join_point = thisJoinPoint.toString();
		String args = " ";
		for(Object object : thisJoinPoint.getArgs()) {
			args += "(" + object + ") ";
		}
		log.info(join_point + "\n" + args);
	}*/
	
}
