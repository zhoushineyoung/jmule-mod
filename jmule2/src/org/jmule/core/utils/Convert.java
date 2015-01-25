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
package org.jmule.core.utils;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.jmule.core.sharingmanager.JMuleBitSet;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.7 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/04/12 16:35:51 $$
 */
public class Convert {
	
	public static int byteToInt(byte bvalue) {
		ByteBuffer data = Misc.getByteBuffer(4);
		data.put(bvalue);
		return data.getInt(0);
	}
	
	public static int byteToShort(byte[] bvalue){
		ByteBuffer data = Misc.getByteBuffer(2);
		for(int i=0;i<bvalue.length;i++)
			data.put(bvalue[i]);
		return data.getShort(0);
	}
	
	public static int byteToInt(byte[] bvalue){
		ByteBuffer data = Misc.getByteBuffer(4);
		for(int i=0;i<bvalue.length;i++)
			data.put(bvalue[i]);
		return data.getInt(0);
	}
	
	public static int shortToInt(short value){
		ByteBuffer data = Misc.getByteBuffer(4);		
		data.putShort(value);
		return data.getInt(0);
	}
	
	public static long shortToLong(short value) {
		ByteBuffer data = Misc.getByteBuffer(8);
		data.putShort(value);
		return data.getLong(0);
	}
	
	public static byte intToByte(int value){
		ByteBuffer data = Misc.getByteBuffer(4);
		data.putInt(value);
		return data.get(0);
	}
	
	public static byte[] intToByteArray(int value) {
		ByteBuffer data = Misc.getByteBuffer(4);
		data.putInt(value);
		return data.array();
	}
	
	public static byte[] shortToByte(short value) {
		ByteBuffer data = Misc.getByteBuffer(2);
		data.putShort(value);
		return data.array();
	}
	
	public static short intToShort(int value){
		ByteBuffer data = Misc.getByteBuffer(4);
		data.putInt(value);
		return data.getShort(0);
	}
	
	public static String intToHex(int value){
		String hexValue=Integer.toHexString(value).toUpperCase();
		if (hexValue.length() == 1)
			hexValue = "0" + hexValue;
		return hexValue;
	}
	
	public static int arrayToInt(byte[] array){
		ByteBuffer data = Misc.getByteBuffer(4);
		for (int i = 0; i < 4; i++)
			data.put(array[i]);
		return data.getInt(0);
	}
	
	public static String byteToHex(byte value){
		return intToHex(byteToInt(value));
	}
	
	public static long intToLong(int value){
		ByteBuffer data = Misc.getByteBuffer(8);
		data.putInt(value);
		return data.getLong(0);
	}
	
	public static int longToInt(long value) {
		ByteBuffer data = Misc.getByteBuffer(8);
		data.putLong(value);
		return data.getInt(0);
	}
	
	public static byte longToByte(long value){
		ByteBuffer data = Misc.getByteBuffer(8);
		data.putLong(value);
		return data.get(0);
	}
	
	public static short longToShort(long value) {
		ByteBuffer data = Misc.getByteBuffer(8);
		data.putLong(value);
		return data.getShort(0);
	}
	
	public static long byteToLong(byte value){
		ByteBuffer data = Misc.getByteBuffer(8);
		data.put(value);
		return data.getLong(0);
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
		for (int i = 0; i < 3; i++) {
			String p = "";
			while (IPAddress.charAt(j) != '.')
				p = p + IPAddress.charAt(j++);
			j++;
			data[i] = (byte) Short.parseShort(p);

		}
		String p = "";
		for (int i = j; i < IPAddress.length(); i++)
			p = p + IPAddress.charAt(i);
		data[3] = ((byte) Short.parseShort(p));

		return (data);
	}
	
	
	public static String IPtoString(byte[] array){
		String IPAddress = "";
		for (int i = 0; i < array.length - 1; i++)
			IPAddress = IPAddress + "" + Convert.byteToInt(array[i]) + ".";
		IPAddress = IPAddress + "" + Convert.byteToInt(array[array.length - 1]);
		return IPAddress;
	}
	
	public static int IPtoInt(byte[] ip) {
		long num = ip[0];
		num += Math.pow(2, 8) * ip[1];
		num += Math.pow(2, 16) * ip[2];
		num += Math.pow(2, 24) * ip[3];
		return Convert.longToInt(num);
	}
	
	/**
	 * @author javajox
	 * @param inetSocketAddress
	 * @return the IP address as int
	 */
	public static int InetSocketAddressToInt(InetSocketAddress inetSocketAddress) {
		
		return IPtoInt( inetSocketAddress.getAddress().getAddress() );
	}
	
	public static String byteToHexString(byte[] bytes) {
		return byteToHexString(bytes, "");
	}

	public static String byteToHexString(byte[] bytes, String byteSeparator) {
		String value = "";
		for (int i = 0; i < bytes.length; i++)
			value = value + byteSeparator + Convert.byteToHex(bytes[i]);
		return value;
	}
	
	public static String byteToHexString(byte[] bytes, int begin,int end) {
		String value = "";
		for (int i = begin; i < end; i++)
			value = value + " " + Convert.byteToHex(bytes[i]);
		return value;
	}
	
	public static byte[] hexStringToByte(String string, String byteSeparator) {
		String bytes[] = string.split(byteSeparator);
		byte result[] = new byte[bytes.length - 1];

		for (int i = 1; i < bytes.length; i++)
			result[i - 1] = (byte) Integer.parseInt(bytes[i], 16);

		return result;
	}
	
	/**
	 * For each byte is allocated 2 chars from string.
	 * 01762E = byte[] {0x01, 0x76, 0x2E} 
	 */
	public static byte[] hexStringToByte(String string) {
		byte result[] = new byte[string.length() / 2];

		for (int i = 0, j = 0; i < string.length(); i += 2, j++) {
			result[j] = (byte) Integer.parseInt(string.charAt(i) + ""
					+ string.charAt(i + 1), 16);
		}

		return result;
	}
	
	/** Convert 0xAA Hex value to int value */
	public static int hexToInt(String value) {
		return Integer.parseInt(value.charAt(0) + "" + value.charAt(1), 16);
	}
	
	public static byte hexToByte(String value){
		return Convert.intToByte(Convert.hexToInt(value));
	}
	
	public static byte[] reverseArray(byte[] inputArray) {
		byte outArray[] = new byte[inputArray.length];

		for (int i = 0; i < inputArray.length; i++)
			outArray[inputArray.length - 1 - i] = inputArray[i];
		return outArray;
	}

	/**Work with BitSet class */
		
	public static int bitSetToBytes(JMuleBitSet bitSet, byte[] bytes, int pos) {
		for (int i = 0; i < bitSet.size(); i++)
			if (bitSet.get(i))
				bytes[(i / 8) + pos] |= 1 << (i % 8);
		return bitSet.length() / 8 + 1;
	}
	
	public static byte[] bitSetToBytes(JMuleBitSet bitSet) {
		byte[] bytes = new byte[((bitSet.size() - 1) / 8) + 1];
		bitSetToBytes(bitSet, bytes, 0);
		return (bytes);
	}

	public static JMuleBitSet byteToBitset(byte[] buffer, int pos, int length) {
		int nbits = length * 8;
		JMuleBitSet result = new JMuleBitSet(nbits);
		if (pos + length > buffer.length) {
			return result;
		}
		for (int i = 0; i < length; i++) {
			byte b = buffer[i + pos];
			for (int j = 0; j < 8; j++) {
				if ((b & 1 << j) != 0) {
					// "byte" * 8 + "bit in byte" = i * 8 + j
					result.set((i << 3) + j);
				}
			}
		}
		return (result);
	}
	
	public static JMuleBitSet byteToBitset(byte[] buffer) {
		return Convert.byteToBitset(buffer, 0, buffer.length);
	}
	
}
