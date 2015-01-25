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

import java.util.LinkedList;

import org.jmule.core.utils.Convert;


/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/17 17:45:20 $$
 */
public class PartHashSet extends LinkedList<byte[]> {
	
	private FileHash fileHash;
	

	public PartHashSet(FileHash fileHash){
		
		this.fileHash = fileHash;
		
	}

	public boolean add(byte[] partHash){
		
		byte[] pHash = partHash.clone();
		
		super.addLast(pHash);
		
		return true;
		
	}
	
	public void setFileHash(FileHash fileHash){
		
		this.fileHash=fileHash;
		
	}
	
	public FileHash getFileHash() {
		
		return this.fileHash;
		
	}
	
	public byte[] get(int partID){
		
		return super.get(partID);
		
	}
	
	public boolean isEmpty() {
		for(byte[] hashSet : this) {
			for(int i = 0; i<hashSet.length;i++)
				if (hashSet[i] != 0) return false;
		}
		return true;
	}
	
	public String toString() {
		
		String result="["+fileHash+"]\n";
		
		for(int i = 0;i<this.toArray().length;i++) {
			
			byte[] a = this.get(i);
			
			result+=Convert.byteToHexString(a)+" ";
			
		}
		
		return result;
		
	}
}
