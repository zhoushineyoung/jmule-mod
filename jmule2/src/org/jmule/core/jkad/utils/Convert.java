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
package org.jmule.core.jkad.utils;

import java.util.BitSet;


/**
 * Created on Dec 28, 2008
 * @author binary256
 * @version $Revision: 1.4 $
 * Last changed by $Author: binary255 $ on $Date: 2010/06/28 17:43:13 $
 */
public class Convert {
	

	public static Long bitSetToLong(BitSet bitSet) {
		long result = 0l;
		for (int i = 0; i < bitSet.length(); i++) {
			int poz = bitSet.size() - i - 1;
			if (bitSet.get(poz))
				result += Math.pow(2, i);
		}
		
		return result;
	}

	
	public static byte[] bitSetToByteArray(BitSet bitSet) {
		 byte[] bytes = new byte[bitSet.size()% 8 == 0 ? bitSet.size() / 8 : bitSet.size() / 8 + 1];
	        for (int i = 0; i < bytes.length; i++)
	            for (int j = 0; j < 8; j++)
	              if (bitSet.get(i * 8 + j))
	            	  bytes[i] |= 1 << j;
	        return bytes;
	}
	
	public static void byteArrayToBitSet(byte[] byteArray, BitSet bitSet) {
		
		for (int i = 0; i < byteArray.length; i++) {
			byte b = byteArray[i];
			for (int j = 0; j < 8; j++) {
				if ((b & 1 << j) != 0) {
                    //"byte" * 8 + "bit in byte" = i * 8 + j
					bitSet.set((i << 3) + j);

				}
			}
		}
	}
	
	/**
	 * Convert IPv4 address to byte array 
	 * A.B.C.D = [A,B,C,D]
	 * @param IPAddress
	 * @return
	 */
	public static byte[] stringIPToArray(String IPAddress){
		int j = 0;
		byte[] data = new byte[4];
		for(int i=0;i<3;i++){
			String p="";
			while (IPAddress.charAt(j)!='.') p=p+IPAddress.charAt(j++);
			j++;
			data[i]=(byte)Short.parseShort(p);
			
		}
		String p="";
		for(int i=j;i<IPAddress.length();i++) p=p+IPAddress.charAt(i);
		data[3]=((byte)Short.parseShort(p));
		
		return (data);
	}
	
	/**
	 * Convert MD4 ID into 'MD4 eMule ID'
	 */
	public static void updateSearchID(byte[] searchID) {
		for(int i = 0;i<searchID.length;i+=4) 
			reverse4(searchID, i);
	}	
	
	/**
	 * Reverse 4 bytes begin with position
	 * @param array
	 * @param position
	 */
	private static void reverse4(byte[] array, int position) {
		byte b1 = array[position];
		byte b2 = array[position+1];
		byte b3 = array[position+2];
		byte b4 = array[position+3];
		
		array[position] = b4;
		array[position+1] = b3;
		array[position+2] = b2;
		array[position+3] = b1;
	}
	
}
