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

import java.util.Arrays;

import org.jmule.core.utils.Convert;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/06/09 15:14:56 $$
 */
public class ClientID {
	
	private byte[] data = new byte[4];
	private int hashCode = 0;
	
	public ClientID(byte[] clientID){
		for(int i = 0;i<clientID.length;i++)
			this.data[i] = clientID[i];
		hashCode = toString().hashCode();
	}
	
	public ClientID(String clientID){
		data = (Convert.stringIPToArray(clientID));
		hashCode = toString().hashCode();
	}
	
	public byte[] getClientID() {
		return data.clone();
	}
	
	public boolean isHighID() {
		return !(data[3]==0);
		//if (data[3] != 0) return true;
		//return getAsLong()>=16777216;
	}

	public String getAsString() {
		return Convert.byteToInt(data[0])+"."+Convert.byteToInt(data[1])+"."+Convert.byteToInt(data[2])+"."+Convert.byteToInt(data[3]);
	}
	
	public String toString() {
		return getAsString();
	}
	
	public long getAsLong() {
		long num = data[0];
		num += Math.pow(2, 8) * data[1];
		num += Math.pow(2, 16) * data[2];
		num += Math.pow(2, 24) * data[3];
		return num;
	}
	
	public int hashCode() {
		return hashCode;
	}
	
	public boolean equals(Object object){
		if (object==null) return false;
		if (!(object instanceof ClientID)) return false;
		ClientID oClientID = (ClientID) object;
		return Arrays.equals(data, oClientID.data);
		
	}		
}