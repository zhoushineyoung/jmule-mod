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
package org.jmule.core.sharingmanager;

import java.util.BitSet;

import org.jmule.core.utils.Convert;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/07/09 17:29:43 $$
 */
public class JMuleBitSet extends BitSet {
	private int bitCount;
	
	public int getBitCount() {
		return bitCount;
	}

	public void setBitCount(int bitCount) {
		this.bitCount = bitCount;
	}
	
	public JMuleBitSet(int bitCount) {
		super(bitCount);
		this.bitCount = bitCount;
	}
	
	public int size() {
		return bitCount;
	}
	
	public byte[] getAsByteArray() {
		byte[] rawData = Convert.bitSetToBytes(this);
		return rawData;
	}
	
	public int getBitCount(boolean value) {
		int count = 0;
		for(int i = 0;i<size();i++)
			if (get(i) == value)
				count++;
		return count;
	}
	
	public boolean hasAtLeastOne(boolean value) {
		for (int i = 0; i < size(); i++)
			if (get(i) == value)
				return true;
		return false;
	}
	
	public String toString() {
		String value="";
		
		for(int i=0;i<this.bitCount;i++)
			if (this.get(i)) value+=" 1 ";
			else 
				value+=" 0 ";
		
		return value;
	}
	
}
