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

import static org.jmule.core.edonkey.ED2KConstants.PARTSIZE;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import org.jmule.core.JMException;
import org.jmule.core.edonkey.packet.tag.NumberTag;
import org.jmule.core.edonkey.packet.tag.Tag;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.9 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/07/31 13:08:35 $$
 */
public class Misc {
	
	public static final int    INFINITY_AS_INT = 31536000;
	
	public static String getStackTrace(Throwable e) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos));

			e.printStackTrace(pw);

			pw.close();

			return baos.toString();

		} catch (Throwable ignore) {
			return "";
		}
	}
	
	public static int compareAllObjects(Object object1, Object object2,String methodName,boolean order) {

		Class cl = object1.getClass();
		Object result1 = null, result2 = null;
		try {
			Method m = cl.getMethod(methodName, null);
			result1 = m.invoke(object1, null);
			result2 = m.invoke(object2, null);
			
			if (result1 instanceof String) {
				
				String name1 = (String)result1;
				String name2 = (String)result2;
				int result = name1.compareTo(name2);
				if (order)
					return result;
				else
					return reverse(result);
			}
			
			if (result1 instanceof Integer) {
				int int1 = (Integer)result1;
				int int2 = (Integer)result2;
				int result = 0;
				if (int1>int2) result = 1;
				if (int1<int2) result = -1;
				if (order)
					return result;
				else
					return reverse(result);
			}
			
			if (result1 instanceof Long) {
				long int1 = (Long)result1;
				long int2 = (Long)result2;
				int result = 0;
				if (int1>int2) result = 1;
				if (int1<int2) result = -1;
				if (order)
					return result;
				else
					return reverse(result);
			}
			
			if (result1 instanceof Float) {
				float int1 = (Float)result1;
				float int2 = (Float)result2;
				int result = 0;
				if (int1>int2) result = 1;
				if (int1<int2) result = -1;
				if (order)
					return result;
				else
					return reverse(result);
			}
			
			if (result1 instanceof Double) {
				double int1 = (Double)result1;
				double int2 = (Double)result2;
				int result = 0;
				if (int1>int2) result = 1;
				if (int1<int2) result = -1;
				if (order)
					return result;
				else
					return reverse(result);
			}

			if (result1 instanceof Boolean) {
				boolean b1 = (Boolean)result1;
				boolean b2 = (Boolean)result2;
				int result = 0;
				if ((b1==true)&&(b2==false)) result = 1;
				if ((b1==false)&&(b2==true)) result = -1;
				
				if (order)
					return result;
				else
					return reverse(result);
			}
				
			String name1 = result1 + "";
			String name2 = result2 + "";
			int result = name1.compareTo(name2);
			if (order)
				return result;
			else
				return reverse(result);
			
		} catch (Throwable e) {
			e.printStackTrace();
		} 
		
		return 0;
	}
	
	public static final String NO_EXTENSION = "no extension";
	
	public static String getFileExtension(String fileName) {
		int id = fileName.length()-1;
		while(id>0) { 
			if (fileName.charAt(id)=='.') break;
			id--;
		}
		String extension = NO_EXTENSION;
		if (id!=0)
			extension = fileName.substring(id+1, fileName.length());
	
		return extension;
	}
	
	public static int reverse(int value) {
		if (value<0) return 1;
		if (value>0) return -1;
		return 0;
	}
	
	public static ByteBuffer getByteBuffer(long bufferSize) {
		ByteBuffer data = ByteBuffer.allocate(8);
		data.order(ByteOrder.LITTLE_ENDIAN);
		data.putLong(bufferSize);
		return ByteBuffer.allocate(data.getInt(0)).order(
				ByteOrder.LITTLE_ENDIAN);
	}

	public static long extractNumberTag(Tag tag) throws JMException {
		if (tag instanceof NumberTag) {
			NumberTag ntag = (NumberTag) tag;
			return ntag.getNumber();
		}
		throw new JMException("Tag is not number tag\n"+tag);
	}
	
	/**
	 * 
	 * @param source
	 * @param destination
	 * @throws Exception
	 * @deprecated
	 */
	public static void copyFile(String source, String destination) throws Exception {
        FileChannel srcChannel = new FileInputStream(source).getChannel();
    
        FileChannel dstChannel = new FileOutputStream(destination).getChannel();
    
        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
    
        srcChannel.close();
        dstChannel.close();
	}
	
	public static int getPartCount(long fileSize) {
		
		if (fileSize<PARTSIZE) return 0;
		
		int partCount = (int)(fileSize/PARTSIZE);
		if (fileSize%PARTSIZE !=0) partCount++;
		return partCount;
	}
	
	public static byte getByte(int num,int pos) {
		ByteBuffer data = Misc.getByteBuffer(4);
		data.putInt(0, num);
		return data.get(pos);
	}
	
	public static boolean isHexadecimalNumber(String number) {
		if(number.length() == 0) return false;
		if(number.length() % 2 != 0) return false;
		for(Character current_char : number.toCharArray()) {
			char char_to_test = Character.toUpperCase(current_char);
			if ( '0' <= char_to_test && char_to_test <= '9' )
				continue;
			if ( 'A' <= char_to_test && char_to_test <= 'F' )
				continue;
			return false;
		}
		return true;
	}
	
	public static boolean isEmpty(byte[] array) {
		for (byte b : array)
			if (b != 0)
				return false;
		return true;
	}
	
	public static void w(String string) {
		System.out.println( string );
	}
	
	public static void wl(String string) {
		System.out.print( string );
	}
}
