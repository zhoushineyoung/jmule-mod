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
package org.jmule.core.jkad;

import static org.jmule.core.utils.Convert.shortToInt;

import java.net.InetSocketAddress;


/**
 * Created on Jan 10, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public class ContactAddress {
	
	private IPAddress address;
	private int udpPort;
	
	public ContactAddress(IPAddress address, int udpPort) {
		super();
		this.address = address;
		this.udpPort = udpPort;
	}

	public ContactAddress(byte[] address, int udpPort) {
		super();
		this.address = new IPAddress(address);
		this.udpPort = udpPort;
	}
	
	public ContactAddress(InetSocketAddress address, int udpPort) {
		this.address = new IPAddress(address);
		this.udpPort = udpPort;
	}
	
	public ContactAddress(InetSocketAddress address) {
		this.address = new IPAddress(address);
		this.udpPort = address.getPort();
	}
	
	public IPAddress getAddress() {
		return address;
	}

	public void setAddress(IPAddress address) {
		this.address = address;
	}

	public int getUDPPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}
	
	public InetSocketAddress getAsInetSocketAddress() {
		return new InetSocketAddress(address.toString(), shortToInt((short)getUDPPort()));
	}
	
	public int hashCode() {
		return (address.toString() + udpPort).hashCode();
	}

	public boolean equals(Object object) {
		if (object == null) return false;
		return hashCode()== object.hashCode();
	}
	
	public String toString() {
		return address+" : " +shortToInt((short)getUDPPort());
	}
	
}
