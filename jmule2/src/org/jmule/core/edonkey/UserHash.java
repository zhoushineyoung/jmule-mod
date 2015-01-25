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
import java.util.Random;

import org.jmule.core.utils.Convert;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/02/27 15:14:13 $$
 */
public class UserHash implements Comparable<UserHash> {
	
	private byte[] userHash = new byte[16];
	
	private int hashCode = 0;
	private String stringValue = "";
	
	public UserHash(byte[] hash) {
		this.userHash = hash;
		initUserHash();
	}
	
	private void initUserHash() {
		stringValue = "";
		for (int i = 0; i < userHash.length; i++)
			stringValue = stringValue + Convert.byteToHex(userHash[i]);
		
		hashCode = stringValue.hashCode();
	}
	
	public static UserHash genNewUserHash() {
		byte[] hash = new byte[16];
		new Random().nextBytes( hash );
		hash[5] = 14;
		hash[14] = 111;
		return new UserHash(hash);
	}
	
	public byte[] getUserHash() {
		return this.userHash;
	}
	
	public String getAsString() {
		return stringValue;
	}
	
	public String toString() {
		return  stringValue;
	}
	
	public int hashCode() {
		return hashCode;
	}
	
	public boolean equals(Object object) {
		if (object==null) return false;
		if (!(object instanceof UserHash )) return false;
		UserHash hash = (UserHash)object;
		return Arrays.equals(userHash, hash.getUserHash());
	}

	public int compareTo(UserHash o) {
		int hash_code1 = hashCode();
		int hash_code2 = o.hashCode();
		if (hash_code1 < hash_code2)
			return -1;
		if (hash_code1 == hash_code2)
			return 0;
		return 1;
	}
	
}
