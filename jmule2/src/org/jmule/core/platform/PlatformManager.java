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

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;

import org.jmule.core.JMuleManager;

/**
 * Created on Aug 23, 2009
 * @author javajox
 * @version $Revision: 1.2 $
 * Last changed by $Author: javajox $ on $Date: 2009/10/25 08:36:11 $
 */
public interface PlatformManager extends JMuleManager {

	public enum PLATFORM_TYPE { WINDOWS, MACOS, UBUNTU, SOLARIS, DEBIAN, FREEBSD };
	
	public String getOSName() throws PlatformManagerException;
	
	public String getOSVersion() throws PlatformManagerException;
	
	public String getArchitecture() throws PlatformManagerException;
	
	public List<CPUCapabilities> getCPUCapabilities() throws PlatformManagerException;
	
	public void copyFile(File source, File destination) throws PlatformManagerException;
	
	public void moveFile(File source, File destination) throws PlatformManagerException;
	
	public PingResult ping(InetAddress source, 
			               NetworkInterface networkInterface,
			               int count) throws PlatformManagerException;
	
	/**
	 * Tests if native code is available for all or some functionality of the given JMule manager
	 * @param manager
	 * @param methodName TODO
	 * @return true if native code is available, false otherwise
	 */
	public boolean isNativeAvailable(JMuleManager manager, String methodName) throws PlatformManagerException;
	
	public void addToIPFilter(Object ip) throws PlatformManagerException;
	
	public void removeFromIPFilter(Object ip) throws PlatformManagerException;
}
