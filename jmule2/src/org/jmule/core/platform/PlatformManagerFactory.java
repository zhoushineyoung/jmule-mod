/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.platform;

import org.jmule.core.JMConstants;

/**
 * Created on Aug 30, 2009
 * @author javajox
 * @version $Revision: 1.2 $
 * Last changed by $Author: javajox $ on $Date: 2009/10/25 08:57:58 $
 */
public class PlatformManagerFactory {

	private static PlatformManager instance;
	
	public static PlatformManager getInstance() {
		
		if ( instance == null ) {
			
			    if( JMConstants.isLinux )
				
				    instance = new LinuxPlatformManager();
			    
		   else if( JMConstants.isFreeBSD )
			   
			        instance = new FreeBSDPlatformManager();
			    
		   else if ( JMConstants.isSolaris )
			   
			        instance = new SolarisPlatformManager();	    
			    
		   else if ( JMConstants.isOSX )
			   
			        instance = new MacOSPlatformManager();
			    
		   else if ( JMConstants.isWindowsXP )
			   
			        instance = new WinXPPlatformManager();
			   
		   else
			  
			        instance = new DummyPlatformManager();
		}
		
		return instance;
		
	}
	
}
