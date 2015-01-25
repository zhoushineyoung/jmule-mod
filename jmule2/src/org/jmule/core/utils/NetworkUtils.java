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
package org.jmule.core.utils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.jmule.core.JMException;

/**
 * Created on Aug 12, 2009
 * @author javajox
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/08/13 06:37:01 $$
 */
public class NetworkUtils {

	
	public static List<NetworkInterface> getAllNetworkInterfaces() throws JMException {
		
		List<NetworkInterface> all_network_interfaces = new ArrayList<NetworkInterface>();
		try {
			Enumeration<NetworkInterface> network_interfaces_enum = NetworkInterface.getNetworkInterfaces();
			while(network_interfaces_enum.hasMoreElements()) {
				all_network_interfaces.add( network_interfaces_enum.nextElement() );
			}
		} catch (SocketException cause) {
			cause.printStackTrace();
			throw new JMException( cause );
		}
		return all_network_interfaces;
	}
	
	public static boolean hasNicName(String nicName) throws JMException {
		List<NetworkInterface> network_interfaces = getAllNetworkInterfaces();
		for(NetworkInterface network_interface : network_interfaces) {
			if (network_interface.getName().equals(nicName)) return true;
		}
		return false;
	}
	
	
	// for testing purpose only
	public static void main(String[] args) throws Throwable {
		
		List<NetworkInterface> ni = getAllNetworkInterfaces();
		
		for( NetworkInterface n : ni ) {
			System.out.println( "Name : " + n.getName() );
			System.out.println( "Display name : " + n.getDisplayName() );
			System.out.println( "Hardware address : " + ( n.getHardwareAddress() != null ? Convert.byteToHexString(n.getHardwareAddress(), ":" ) : ""));
			System.out.println( "MTU : " + n.getMTU() );
			
		}
		
	}
	
}
