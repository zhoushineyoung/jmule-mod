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
package org.jmule.core.edonkey.packet.tag;

import static org.jmule.core.edonkey.ED2KConstants.*;
import static org.jmule.core.edonkey.ED2KConstants.TAGTYPE_HASH;
import static org.jmule.core.edonkey.ED2KConstants.TAGTYPE_STRING;
import static org.jmule.core.edonkey.ED2KConstants.TAGTYPE_UINT16;
import static org.jmule.core.edonkey.ED2KConstants.TAGTYPE_UINT32;
import static org.jmule.core.edonkey.ED2KConstants.TAGTYPE_UINT64;
import static org.jmule.core.edonkey.ED2KConstants.TAGTYPE_UINT8;
import static org.jmule.core.utils.Misc.getByteBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.jmule.core.edonkey.ED2KConstants;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * Created on Aug 27, 2008
 * @author binary256
 * @version $Revision: 1.9 $
 * Last changed by $Author: binary255 $ on $Date: 2010/07/31 16:00:42 $
 */
public class TagScanner {
	
	private static int INT_TAGTYPE_EXSTRING_LONG = Convert.byteToInt(ED2KConstants.TAGTYPE_EXSTRING_LONG);
	private static int INT_TAGTYPE_EXSTRING_SHORT_BEGIN = Convert.byteToInt(ED2KConstants.TAGTYPE_EXSTRING_SHORT_BEGIN);
	private static int INT_TAGTYPE_EXBYTE = Convert.byteToInt(ED2KConstants.TAGTYPE_EXBYTE);
	private static int INT_TAGTYPE_EXWORD = Convert.byteToInt(ED2KConstants.TAGTYPE_EXWORD);
	private static int INT_TAGTYPE_EXDWORD = Convert.byteToInt(ED2KConstants.TAGTYPE_EXDWORD);
	
	public static Tag scanTag(ByteBuffer data) {
		byte tagType;
		byte[] tagNameLength = new byte[]{0x01,0x00};
		byte[] tagName;
		
		tagType = data.get();
		if (!(Convert.byteToInt(tagType) >= INT_TAGTYPE_EXSTRING_LONG))
				data.get(tagNameLength);
		
		tagName = new byte[Convert.byteToInt(tagNameLength)];
		data.get(tagName);
		
		// Analyze extended tags
		boolean isExtendedCompressedStringTag = false;
		int oldTagType = Convert.byteToInt(tagType);
		int ivalue = Convert.byteToInt(tagType);
		if (ivalue >= INT_TAGTYPE_EXSTRING_SHORT_BEGIN) {
			tagType = TAGTYPE_STRING;
			isExtendedCompressedStringTag = true;
		}
		if (ivalue== INT_TAGTYPE_EXSTRING_LONG) {
			tagType = TAGTYPE_STRING;
		}
		
		if (ivalue == INT_TAGTYPE_EXBYTE) {
			int value = Convert.byteToInt(data.get());
			return new IntTag(tagName, value);
		}
		
		if (ivalue == INT_TAGTYPE_EXWORD) {
			int value = Convert.shortToInt(data.getShort());
			return new IntTag(tagName, value);
		}
		if (ivalue == INT_TAGTYPE_EXDWORD) {
			int value = data.getInt();
			return new IntTag(tagName, value);
		}
		
		switch(tagType) {
			case TAGTYPE_HASH : 
				byte[] hash = new byte[16];
				data.get(hash);
				FileHash tag_hash_data = new FileHash(hash);
				return new HashTag(tagName, tag_hash_data);
	
			case TAGTYPE_STRING :
				if (isExtendedCompressedStringTag) {
					int str_length = (oldTagType) - INT_TAGTYPE_EXSTRING_SHORT_BEGIN;
					byte str_data[] = new byte[str_length];
					data.get(str_data);
					String str = new String(str_data);
					return new StringTag(tagName, str);
				}
				byte[] length = new byte[2];
				data.get(length);
				int str_length = Convert.byteToInt(length);
				byte str_data[] = new byte[str_length];
				data.get(str_data);
				String str = new String(str_data);
				return new StringTag(tagName, str);
				
			case TAGTYPE_UINT8 : 
				byte tag_data = data.get();
				return new ByteTag(tagName, tag_data);
				
			case TAGTYPE_UINT16 : 
				short tag_short_data = data.getShort();
				return new ShortTag(tagName, tag_short_data);
				
			case TAGTYPE_UINT32 :
				int tag_int_data = data.getInt();
				return new IntTag(tagName, tag_int_data);
				
			case TAGTYPE_UINT64 :
				long long1 = data.getLong();
				
				return new LongTag(tagName, long1);
				
			case TAGTYPE_BSOB : 
				byte size = data.get();
				ByteBuffer raw_data = getByteBuffer(size+1);
				raw_data.put(size);
				data.get(raw_data.array(), 1, size);
				return new BSOBTag(tagName, raw_data);
				
			case TAGTYPE_FLOAT32 : 
				float value = data.getFloat();
				return new FloatTag(tagName, value);
			case TAGTYPE_BOOL :
				byte boolValue = data.get();
				return new BoolTag(tagName, boolValue == 1 ? true : false );
			case TAGTYPE_BOOLARRAY : 
				short boolArraySize = data.getShort();
				byte[] boolArray = new byte[(boolArraySize+7)/8];
				data.get(boolArray);
				return new BoolArrayTag(tagName, boolArray);
			case TAGTYPE_BLOB : 
				int blobSize = data.getInt();
				byte[] blobData = new byte[blobSize];
				data.get(blobData);
				return new BlobTag(tagName, blobData);
				
		}
		return null;
	}
	
	public static Tag scanTag(FileChannel file) throws IOException {
		byte tagType;
		int tagNameLength;
		byte[] tagName;
		
		ByteBuffer data = getByteBuffer(1);
		file.read(data);
		
		tagType = data.get(0);
		
		data = getByteBuffer(2);
		file.read(data);
		
		tagNameLength = Convert.shortToInt(data.getShort(0));

		data = getByteBuffer(tagNameLength);
		file.read(data);
		tagName = data.array();
		
		switch(tagType) {
			case TAGTYPE_HASH : 
				data = getByteBuffer(16);
				file.read(data);
				byte[] hash = data.array();
				FileHash tag_hash_data = new FileHash(hash);
				return new HashTag(tagName, tag_hash_data);

			case TAGTYPE_STRING :
				data =  getByteBuffer(2);
				file.read(data);
				int str_length = Convert.shortToInt(data.getShort(0));
				data =  getByteBuffer(str_length);
				file.read(data);
				String str = new String(data.array());
				return new StringTag(tagName, str);
			
			case TAGTYPE_UINT8 : 
				data =  getByteBuffer(1);
				file.read(data);
				byte tag_data = data.get(0);
				return new ByteTag(tagName, tag_data);
			
			case TAGTYPE_UINT16 : 
				data =  getByteBuffer(2);
				file.read(data);
				short tag_short_data = data.getShort(0);
				return new ShortTag(tagName, tag_short_data);
			
			case TAGTYPE_UINT32 :
				data =  getByteBuffer(4);
				file.read(data);
				int tag_int_data = data.getInt(0);
				return new IntTag(tagName, tag_int_data);
				
			case TAGTYPE_UINT64 :
				data =  getByteBuffer(8);
				file.read(data);
				return new LongTag(tagName, data.getLong(0));
			case TAGTYPE_BSOB : 
				ByteBuffer t = getByteBuffer(1);
				file.read(t);
				byte size = t.get(0);
				ByteBuffer content = getByteBuffer(size);
				file.read(content);
				ByteBuffer raw_data = getByteBuffer(size+1);
				raw_data.put(size);
				content.position(0);
				raw_data.put(content);
				return new BSOBTag(tagName, raw_data);
				
			case TAGTYPE_FLOAT32 :
				ByteBuffer tfloat = Misc.getByteBuffer(3);
				file.read(tfloat);
				float value = tfloat.getFloat(0);
				return new FloatTag(tagName, value);
			case TAGTYPE_BOOL :
				ByteBuffer tbool = Misc.getByteBuffer(1);
				file.read(tbool);
				byte boolValue = tbool.get(0);
				return new BoolTag(tagName, boolValue == 1 ? true : false );
			case TAGTYPE_BOOLARRAY : 
				ByteBuffer tboolArraySize = Misc.getByteBuffer(2);
				file.read(tboolArraySize);
				short boolArraySize = tboolArraySize.getShort(0);
				ByteBuffer boolArray = Misc.getByteBuffer((boolArraySize+7)/8);
				file.read(boolArray);
				return new BoolArrayTag(tagName, boolArray.array());
			case TAGTYPE_BLOB : 
				ByteBuffer tblobSize = Misc.getByteBuffer(4);
				file.read(tblobSize);
				int blobSize = tblobSize.getInt(0);
				ByteBuffer blobData = Misc.getByteBuffer(blobSize);
				file.read(blobData);
				return new BlobTag(tagName, blobData.array());
		}
		System.out.println("Unknown tag opcode : " + Convert.byteToHex(tagType));
		return null;
	}
}
