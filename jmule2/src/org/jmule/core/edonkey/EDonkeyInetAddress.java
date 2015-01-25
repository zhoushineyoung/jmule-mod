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
package org.jmule.core.edonkey;

import org.jmule.core.utils.Convert;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/17 17:45:20 $$
 */
public class EDonkeyInetAddress {

	private byte[] peerAddress = new byte[4];
	
	public EDonkeyInetAddress(String address) {
		byte[] peer= Convert.stringIPToArray(address);
		for(int i = 0; i<4;i++)
			peerAddress[i] = peer[i];
	}

	public byte[] getPeerAddress() {
		return peerAddress.clone();
	}

	public void setPeerAddress(byte[] peerAddress) {
		this.peerAddress = peerAddress;
	}
	
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (!(obj instanceof EDonkeyInetAddress)) return false;
		byte[] address = ((EDonkeyInetAddress)(obj)).getPeerAddress();
		for(int i = 0 ; i<address.length ; i++) {
			if (address[i] != peerAddress[i]) return false;
		}
		return true;
	}
	
	public String toString() {
		return Convert.IPtoString(peerAddress);
	}
	
	public long getAsLong() {
		String str="";
		for(int i = 0 ;i<peerAddress.length;i++)
			str+=""+Convert.byteToInt(peerAddress[i]);
		return Long.parseLong(str);
	}
	
	 
}
