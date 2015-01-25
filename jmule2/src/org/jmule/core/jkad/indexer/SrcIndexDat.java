/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.jkad.indexer;

import static org.jmule.core.jkad.JKadConstants.SRC_INDEX_VERSION;
import static org.jmule.core.utils.Misc.getByteBuffer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.packet.tag.TagScanner;
import org.jmule.core.jkad.ClientID;
import org.jmule.core.jkad.Int128;
import org.jmule.core.utils.Convert;

/**
 * Created on Apr 21, 2009
 * @author binary256
 * @version $Revision: 1.9 $
 * Last changed by $Author: binary255 $ on $Date: 2010/08/22 14:29:51 $
 */
public class SrcIndexDat {

	public static  Map<Int128, Index> loadFile(String fileName) throws Throwable {
		Map<Int128, Index> result = new Hashtable<Int128, Index>();
		
		FileChannel file_channel = new FileInputStream(fileName).getChannel();
		ByteBuffer file_content = getByteBuffer(file_channel.size());
		file_channel.read(file_content);
		file_channel.close();
		
		file_content.position(4 + 4);
		
		int count = file_content.getInt();
		
		ByteBuffer data;
		
		for(int i=0;i<count;i++) {
			data = getByteBuffer(16);
			file_content.get(data.array());
			
			Int128 key_id = new Int128(data.array());
			Index index = new Index(key_id);
			
			data = getByteBuffer(4);
			
			int source_count = file_content.getInt();
			
			for(int j = 0;j<source_count;j++) {
				data = getByteBuffer(16);
				file_content.get(data.array());
				
				ClientID client_id = new ClientID(data.array());
				long creation_time = file_content.getLong();
				
				int tagCount = file_content.get();
				TagList tagList = new TagList();
				
				for(int k = 0;k<tagCount;k++) {
					Tag tag = TagScanner.scanTag(file_content);
					
					if (tag!=null)
						tagList.addTag(tag);
				}
				Source source = new Source(client_id, tagList,creation_time);
				source.setTagList(tagList);
				index.addSource(source);
			}
			result.put(key_id, index);
		}
		
		file_content.clear();
		file_content = null;
		
		return result;
	}
	
	public static void writeFile(String fileName, Map<Int128, Index> sourceData) throws Throwable {
		
		long buffer_size = 4 + 4 + 4 ;
		List<ByteBuffer> index_byte_buffer = new ArrayList<ByteBuffer>();
		
		for(Int128 key : sourceData.keySet()) {
			ByteBuffer content = getByteBuffer(16 + 4);
			content.put(key.toByteArray());
			
			Index index = sourceData.get(key);
			content.putInt(index.getSourceList().size());
			buffer_size += content.capacity();
			
			content.position(0);
			index_byte_buffer.add(content);
			
			for(Source source : index.getSourceList()) {
				ByteBuffer tag_list = source.getTagList().getAsByteBuffer();
				content = getByteBuffer(16 + 8 + 1 + tag_list.capacity());
				buffer_size += content.capacity();
				
				content.put(source.getClientID().toByteArray());
				content.putLong(source.getCreationTime());
				content.put(Convert.intToByte(source.getTagList().size()));
				tag_list.position(0);
				content.put(tag_list);
				content.position(0);
				index_byte_buffer.add(content);
				tag_list = null;
			}
		}
		
		ByteBuffer file_content = getByteBuffer(buffer_size);
		ByteBuffer data = getByteBuffer(4);
		data.put(SRC_INDEX_VERSION);
		data.position(0);
		
		file_content.put(data);
		
		data.position(0);
		data.putInt(Convert.longToInt(System.currentTimeMillis()));
		data.position(0);
		
		file_content.put(data);
		
		data.position(0);
		data.putInt(sourceData.size());
		data.position(0);
		file_content.put(data);
		
		for(ByteBuffer buffer : index_byte_buffer) {
			buffer.position(0);
			file_content.put(buffer);
		}
		
		FileChannel channel = new FileOutputStream(fileName).getChannel();
		file_content.position(0);
		channel.write(file_content);
		channel.close();
		
		file_content.clear();
		file_content = null;
		index_byte_buffer.clear();
		index_byte_buffer = null;
		
		
		/*FileChannel channel = new FileOutputStream(fileName).getChannel();
		ByteBuffer data = getByteBuffer(4);
		
		data.put(SRC_INDEX_VERSION);
		data.position(0);
		channel.write(data);
		
		data.position(0);
		data.putInt(Convert.longToInt(System.currentTimeMillis()));
		data.position(0);
		channel.write(data);
		
		data.position(0);
		data.putInt(sourceData.size());
		data.position(0);
		channel.write(data);
		
		for(Int128 key : sourceData.keySet()) {
			data = getByteBuffer(16);
			data.put(key.toByteArray());
			data.position(0);
			channel.write(data);
			
			Index index = sourceData.get(key);
			data = getByteBuffer(4);
			data.putInt(index.getSourceList().size());
			data.position(0);
			channel.write(data);
			
			for(Source source : index.getSourceList()) {
				data = getByteBuffer(16);
				data.put(source.getClientID().toByteArray());
				data.position(0);
				channel.write(data);
				
				data = getByteBuffer(8);
				data.putLong(0, source.getCreationTime());
				data.position(0);
				channel.write(data);
				
				data = getByteBuffer(1);
				data.put(Convert.intToByte(source.getTagList().size()));
				data.position(0);
				channel.write(data);
				for(Tag tag : source.getTagList()) {
					ByteBuffer buffer = tag.getAsByteBuffer();
					buffer.position(0);
					channel.write(buffer);
				}
			}
		}
		channel.close();*/
	}
	
}
