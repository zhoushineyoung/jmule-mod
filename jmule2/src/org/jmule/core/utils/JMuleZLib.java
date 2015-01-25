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

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Vector;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/17 18:32:25 $$
 */
public class JMuleZLib{

	private static int BLOCK_SIZE = 1000;
	
	public static ByteBuffer compressData(ByteBuffer inputData) {
		
		Deflater compressor = new Deflater();
		
		compressor.setInput(inputData.array());
		compressor.finish();
		long capacity = 0;
		
		int byte_count = 0;
		
		List<ByteBuffer> vector = new Vector<ByteBuffer>();
		
		do {
			ByteBuffer tmpData = Misc.getByteBuffer(BLOCK_SIZE);
			
			byte_count = compressor.deflate(tmpData.array());
		
			if (byte_count == 0) break;
			
			tmpData.limit(byte_count);
			
			vector.add(tmpData);
			
			capacity += byte_count;
			
		} while (byte_count != 0);
		
		compressor.end();
		
		ByteBuffer outputData = Misc.getByteBuffer(capacity);
		
		for(ByteBuffer buffer : vector) {
			buffer.position(0);
			outputData.put(buffer.array(), 0, buffer.limit());
		}
		
		return outputData;
		
	}
	
	public static ByteBuffer decompressData(ByteBuffer inputData) throws DataFormatException {
		
		Inflater decompressor = new Inflater();
		
		decompressor.setInput(inputData.array());
		
		long capacity = 0;
		
		int byte_count = 0;
		
		Vector<ByteBuffer> vector = new Vector<ByteBuffer>();
		
		do {
			
			ByteBuffer tmpData = Misc.getByteBuffer(BLOCK_SIZE);
			
			byte_count = decompressor.inflate(tmpData.array());
		
			tmpData.limit(byte_count);
			
			vector.add(tmpData);
			
			capacity += byte_count;
			
		} while(byte_count !=0 );
		
		decompressor.end();
		
		ByteBuffer outputBuffer = Misc.getByteBuffer(capacity);
		
		for(ByteBuffer buffer : vector) {
			
			outputBuffer.put(buffer.array(), 0, buffer.limit());
			
		}
		
		return outputBuffer;
		
		
	}
	
}
