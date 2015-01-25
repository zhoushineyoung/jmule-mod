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

import java.util.List;

import org.jmule.core.JMConstants;
import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManager;
import org.jmule.core.JMuleManagerException;

/**
 * Created on Aug 30, 2009
 * @author javajox
 * @version $Revision: 1.2 $
 * Last changed by $Author: javajox $ on $Date: 2009/10/25 08:36:11 $
 */
public abstract class AbstractPlatformManager extends JMuleAbstractManager implements PlatformManager {
	
	protected final static String PROCESS_ERROR = "The OS process terminated abnormally, exit status : "; 
	protected List<CPUCapabilities> cpus_capabilities;
	
	public void initialize() {
	   try {
		  super.initialize();
	   } catch (JMuleManagerException cause) {
		   cause.printStackTrace();
		   return;
	   }  
	}
			  
	public void start() {
		try {
		   super.start();
		} catch (JMuleManagerException cause) {
		   cause.printStackTrace();
			return ;
		}  
	}
			  
	public void shutdown() {
		try {
			super.shutdown();
		} catch (JMuleManagerException cause) {
			cause.printStackTrace();
		    return;
		}  
	}
			
	protected boolean iAmStoppable() {

	  return false;
	}

	public String getOSName() throws PlatformManagerException {
		String os_name; 
        try {
            os_name = JMConstants.OSName;
        }catch(Throwable cause) {
            throw new PlatformManagerException( cause );
        }
		return os_name;
	}

	public String getOSVersion() throws PlatformManagerException {
		String os_version;
		try {
			os_version = JMConstants.OSVersion;
		}catch( Throwable cause ) {
			throw new PlatformManagerException( cause );
		}
		return os_version;
	}

	public String getArchitecture() throws PlatformManagerException {
		String arch;
		try {
			arch = System.getProperty("os.arch");
		}catch( Throwable cause ) {
			throw new PlatformManagerException( cause );
		}
		return arch;
	}
	
	public boolean isNativeAvailable(JMuleManager manager, String methodName) throws PlatformManagerException {

		return false;
	}

}
