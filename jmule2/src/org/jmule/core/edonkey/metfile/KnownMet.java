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
package org.jmule.core.edonkey.metfile;

import static org.jmule.core.edonkey.ED2KConstants.FT_FILENAME;
import static org.jmule.core.edonkey.ED2KConstants.FT_FILESIZE;
import static org.jmule.core.edonkey.ED2KConstants.KNOWN_VERSION;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;

import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.packet.tag.TagScanner;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * Known.met file format.
 * <table cellpadding="0" border="1" cellspacing="0" width="70%">
 * <tbody>
 *   <tr>
 *     <td>Name</td>
 *     <td>Size in bytes</td>
 *     <td>Default value</td>
 *   </tr>
 *   <tr>
 *     <td>File header</td>
 *     <td>1</td>
 *     <td>0xE0</td>
 *   </tr>
 *   <tr>
 *     <td>Number of entries</td>
 *     <td>4</td>
 *     <td>&nbsp;</td>
 *   </tr>
 * </tbody>
 * </table>
 * <br>
 * File data block : <br>
 *  
 * <table cellpadding="0" border="1" cellspacing="0" width="70%">
 * <tbody>
 *   <tr>
 *     <td>Name</td>
 *     <td>Size in bytes</td>
 *   </tr>
 *   <tr>
 *     <td>Date</td>
 *     <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>File Hash</td>
 *     <td>16</td>
 *   </tr>
 *   <tr>
 *     <td>Part count</td>
 *     <td>2</td>
 *   </tr>
 *   <tr>
 *     <td>&lt;Part hash&gt;*&lt;Part count&gt;</td>
 *     <td>16*&lt;Part count&gt;</td>
 *   </tr>
 *   <tr>
 *     <td>Tag Count</td>
 *     <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>Tag list</td>
 *     <td>variable</td>
 *   </tr>
 * </tbody>
 * </table>
 * 
 * @author binary256
 * @version $$Revision: 1.10 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/08/22 12:46:17 $$
 */
public class KnownMet extends MetFile {

	public KnownMet(String fileName) throws KnownMetException {
		super(fileName);
		/*if (file_channel == null)
			throw new KnownMetException("Failed to load "+fileName);*/
	}

	public Hashtable<String,KnownMetEntity> load() throws KnownMetException,IOException {
		
		FileChannel filechannel = new FileInputStream(file).getChannel();
		ByteBuffer file_content = Misc.getByteBuffer(filechannel.size());
		filechannel.read(file_content);
		filechannel.close();
		
		file_content.position(0);
		// avoid files from known.met that have the same size and name
		HashSet<String> repeated_keys = new HashSet<String>();
		Hashtable<String,KnownMetEntity> knownFiles = new Hashtable<String, KnownMetEntity>();
		byte file_version = file_content.get();
		
		//if (data.get(0)!=KNOWN_VERSION) 
		//		throw new KnownMetException("Unsupported file version");
		long record_count = Convert.longToInt(file_content.getInt());
		for(int j = 0;j<record_count;j++) {
			KnownMetEntity known_met_entity = new KnownMetEntity();
		
			int date = file_content.getInt();
			known_met_entity.setDate(date);
		
			byte[] file_hash = new byte[16];
			file_content.get(file_hash);
			FileHash fileHash = new FileHash(file_hash);
			
			int part_count = file_content.getShort();
			PartHashSet partHash = new PartHashSet(fileHash);
		
			for(int i = 0;i<part_count;i++) {
				byte[] part_hash = new byte[16];
				file_content.get(part_hash);
				partHash.add(part_hash);
			}
		
			known_met_entity.setPartHashSet(partHash);
			
			int tagCount = file_content.getInt();
			TagList tagList = new TagList();
			for(int i = 0;i<tagCount;i++) {
				Tag tag = TagScanner.scanTag(file_content);
				if (tag != null)
					tagList.addTag(tag);
			}
			known_met_entity.setTagList(tagList);
		
			try {
				String file_name = (String)known_met_entity.getTagList().getTag(FT_FILENAME).getValue();
				long file_size = Convert.intToLong((Integer)known_met_entity.getTagList().getTag(FT_FILESIZE).getValue());
				String key = file_name + file_size;
				if(repeated_keys.contains(key)) continue;
				KnownMetEntity known_file = knownFiles.get(key);
				if (known_file == null)		
					knownFiles.put(key, known_met_entity);
				else {
					knownFiles.remove(key);
					repeated_keys.add(key);
				}
			} catch (Throwable e) { e.printStackTrace();}
		
		}
		
		file_content.clear();
		file_content = null;
		
		return knownFiles;
	}
	
	public void store(Collection<SharedFile> fileList) throws KnownMetException, IOException {
		
		Collection<ByteBuffer> file_blocks = new ArrayList<ByteBuffer>();
		long file_size = 0;
		
		ByteBuffer header = Misc.getByteBuffer(1 + 4);
		file_size += header.capacity();
		header.put(KNOWN_VERSION);
		header.putInt(fileList.size());
		header.position(0);
		file_blocks.add(header);
		
				
		for(SharedFile file : fileList) {
			
			ByteBuffer tag_list = file.getTagList().getAsByteBuffer();
			tag_list.position(0);
			ByteBuffer file_block = Misc.getByteBuffer(4 + 16 + 2 + ( file.getHashSet().size() * 16 ) + 4 +  tag_list.capacity() );
			file_size += file_block.capacity();
			
			int data = file_block.getInt();
			file_block.put(file.getFileHash().getHash());
			file_block.putShort(Convert.intToShort(file.getHashSet().size()));
			
			for(byte[] part_hash : file.getHashSet())
				file_block.put(part_hash);
			
			file_block.putInt(file.getTagList().size());
			file_block.put(tag_list);
		
			file_block.position(0);
			file_blocks.add(file_block);
		}
		
		ByteBuffer file_content = Misc.getByteBuffer(file_size);
		for(ByteBuffer block : file_blocks) {
			block.position(0);
			file_content.put(block);
		}
		
		file_content.position(0);
		
		FileChannel filechannel = new FileOutputStream(file).getChannel();
		filechannel.write(file_content);
		filechannel.close();
		
		file_content.clear();
		file_content = null;
		file_blocks.clear();
		file_blocks = null;
		
	}

}
