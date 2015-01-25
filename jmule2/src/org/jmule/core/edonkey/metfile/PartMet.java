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
import static org.jmule.core.edonkey.ED2KConstants.FT_GAPEND;
import static org.jmule.core.edonkey.ED2KConstants.FT_GAPSTART;
import static org.jmule.core.edonkey.ED2KConstants.FT_TEMPFILE;
import static org.jmule.core.edonkey.ED2KConstants.PARTFILE_VERSION;
import static org.jmule.core.edonkey.ED2KConstants.PARTSIZE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.jmule.core.JMIterable;
import org.jmule.core.edonkey.ED2KConstants;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.edonkey.packet.tag.IntTag;
import org.jmule.core.edonkey.packet.tag.StringTag;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.packet.tag.TagScanner;
import org.jmule.core.sharingmanager.Gap;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * <table cellpadding="0" border="1" cellspacing="0" width="70%">
 *  <tbody>
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
 *     <td>Last modification date</td>
 *     <td>4</td>
 *     <td>&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>File hash</td>
 *     <td>16</td>
 *     <td>-</td>
 *   </tr>
 *   <tr>
 *     <td>Part count</td>
 *     <td>2</td>
 *     <td>-</td>
 *   </tr>
 *   <tr>
 *     <td>Parts hash</td>
 *     <td>&lt;Part count&gt;*16</td>
 *     <td>-</td>
 *   </tr>
 *   <tr>
 *     <td>Tag count</td>
 *     <td>4</td>
 *     <td>-</td>
 *   </tr>
 *   <tr>
 *     <td>Tag list</td>
 *     <td>Variable</td>
 *     <td>-</td>
 *   </tr>
 * </tbody>
 * </table>
 *
 * Created on Nov 7, 2007
 * @author binary256
 * @version $$Revision: 1.18 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/08/19 16:12:24 $$
 */
public class PartMet extends MetFile {
	
	public static final String PART_MET_FILE_EXTENTSION 		=  ".part.met";
	
	private byte partFileFormat;
	private int modDate;
	private TagList tagList = new TagList();
	private FileHash fileHash;
	private PartHashSet fileHashSet;
	private GapList gapList;
	
	public PartMet(File file) throws PartMetException {
		super(file);
	}
	
	public PartMet(String fileName) throws PartMetException {
		super(fileName);
	}
	
	public String getAbsolutePath() {
		return file.getAbsolutePath();
		
	}
	
	private Object lock = new Object();
	
	public void load() throws PartMetException{
		synchronized (lock) {
			try {
				FileChannel channel = new FileInputStream(file).getChannel();
				
				ByteBuffer file_content = Misc.getByteBuffer(channel.size());
				channel.read(file_content);
				channel.close();
				
				file_content.position(0);
				
				this.partFileFormat = file_content.get();
				if (this.partFileFormat != PARTFILE_VERSION)
					throw new PartMetException("Unsupported part file");
				
				this.modDate = file_content.getInt();
				
				//Load file hash
				byte[] file_hash = new byte[16];
				file_content.get(file_hash);
				
				fileHash = new FileHash(file_hash);
				fileHashSet = new PartHashSet(fileHash);
				
				//Read part count
				short partCount = file_content.getShort();
				for(int i = 0 ; i < partCount; i++){
					byte[] part_hash = new byte[16];
					file_content.get(part_hash);
					fileHashSet.add(part_hash);
				}
				
				//Read tag count
				int tagCount = file_content.getInt();
				//Load Tags
				this.tagList = new TagList();
				
				for(int i = 0 ; i < tagCount; i++) {
					Tag tag = TagScanner.scanTag(file_content);
					if (tag != null)
						tagList.addTag(tag);
					else {
						throw new PartMetException("Corrupted tag list in file : " + file.getName() );
					}
				}		
				gapList = new GapList();
				byte tag_id = ED2KConstants.GAP_OFFSET;
				Tag start_tag, end_tag;
				while (true) {
					start_tag = tagList.getTag(new byte[]{FT_GAPSTART[0],tag_id});
					if (start_tag == null) break;
					end_tag = tagList.getTag(new byte[]{FT_GAPEND[0],tag_id});
					if (end_tag == null)
						throw new PartMetException("Can't find end of gap in file partial file ");
					tagList.removeTag(start_tag.getTagName());
					tagList.removeTag(end_tag.getTagName());
					try {
						long begin = Convert.intToLong((Integer)start_tag.getValue());
						long end = Convert.intToLong((Integer)end_tag.getValue());
						gapList.addGap(begin,end );
					} catch (Throwable e) {
						throw new PartMetException("Failed to extract gap positions form file : " + file.getName());
					}
					tag_id++;
				}
				file_content.clear();
				file_content = null;
				//System.gc();
			} catch (FileNotFoundException e) {
				throw new PartMetException("Failed to load PartFile ");
			} catch (IOException e) {
				throw new PartMetException("Failed to read data from PartFile ");
			} catch(Throwable t) {
				throw new PartMetException(Misc.getStackTrace(t));
			}
		}
	}

	public void store() throws PartMetException {
		synchronized (lock) {
			try {
				int tag_count = tagList.size() + gapList.size() * 2;
				
				ByteBuffer buffer_tag_list = tagList.getAsByteBuffer();
				List<ByteBuffer> byte_buffer_gaps = new ArrayList<ByteBuffer>();
				int gaps_size = 0;
				/**Write Gap List*/
				byte counter = ED2KConstants.GAP_OFFSET;
				byte metaTagBegin[] = FT_GAPSTART.clone();
				byte metaTagEnd[] = FT_GAPEND.clone();
				JMIterable<Gap> gap_list = gapList.getGaps();
				
				for(Gap gap : gap_list){
					metaTagBegin[1] = counter;
					Tag tagBegin = new IntTag(metaTagBegin,Convert.longToInt(gap.getStart()));
					
					metaTagEnd[1]=counter;
					Tag tagEnd = new IntTag(metaTagEnd,Convert.longToInt(gap.getEnd()));
					
					ByteBuffer tag_pair = Misc.getByteBuffer(tagBegin.getSize() + tagEnd.getSize());
					tag_pair.put(tagBegin.getAsByteBuffer());
					tag_pair.put(tagEnd.getAsByteBuffer());
					
					gaps_size += tag_pair.capacity();
					tag_pair.position(0);
					byte_buffer_gaps.add(tag_pair);
					
					counter++;
				}
				
				long file_size = Convert.intToLong((Integer) tagList.getTag(FT_FILESIZE).getValue());
				int part_count = (int)(file_size / PARTSIZE);
				if ((file_size % PARTSIZE) != 0)
					part_count++;
				
				ByteBuffer file_content = Misc.getByteBuffer( 1 + 4 + 16 + 2 + (part_count * 16) + 4 + buffer_tag_list.capacity() + gaps_size);
				
				file_content.put(PARTFILE_VERSION);
				file_content.putInt( Convert.longToInt(System.currentTimeMillis()) );
				file_content.put(getFileHash().getHash());
				file_content.putShort(Convert.intToShort(part_count));
				if (fileHashSet!=null)
					for(byte[] part_hash : fileHashSet)
						file_content.put(part_hash);
				else {
					byte[] blank_hash_set = new byte[16];
					for(int i = 0;i<part_count;i++)
						file_content.put(blank_hash_set);
				}
				file_content.putInt(tag_count);
				buffer_tag_list.position(0);
				file_content.put(buffer_tag_list);
				
				for(ByteBuffer block : byte_buffer_gaps)
					file_content.put(block);
				
				file_content.position(0);
				FileChannel channel = new FileOutputStream(file).getChannel();
				channel.write(file_content);
				channel.close();
				
				buffer_tag_list.clear();
				byte_buffer_gaps.clear();
				buffer_tag_list = null; 
				byte_buffer_gaps = null;
				//System.gc();
				
			} catch (FileNotFoundException e) {
				throw new PartMetException("Failed to open for writing part file : " +file.getName());
			} catch (IOException e) {
				throw new PartMetException("Failed to write dta in part file : "+file.getName());
			}
		}
		
	}
	
	public boolean delete() {
		return file.delete();
	}

	public String getTempFileName() {
		String tmpFileName;
		try {
			tmpFileName = (String)this.tagList.getTag(FT_TEMPFILE).getValue();
		} catch (Throwable e) {
			return null;
		}
		return tmpFileName;
	}
	
	public void setTempFileName(String tempFileName) {
		this.tagList.removeTag(FT_TEMPFILE);
		Tag tag = new StringTag(FT_TEMPFILE, tempFileName);
		tagList.addTag(tag);
	}
	
	public String getRealFileName() {
		String realFileName;
		try {
			realFileName = (String)this.tagList.getTag(FT_FILENAME).getValue();
		} catch (Throwable e) {
			return null;
		}
		return realFileName;
	}
	
	public void setRealFileName(String realFileName) {
		this.tagList.removeTag(FT_FILENAME);
		Tag tag = new StringTag(FT_FILENAME, realFileName);
		tagList.addTag(tag);
	}
	
	public long getFileSize() {
		long fileSize;
		try {
			fileSize = (Integer)this.tagList.getTag(FT_FILESIZE).getValue();
		} catch (Throwable e) {
			return 0;
		}
		return fileSize;
	}
	
	public void setFileSize(long fileSize){
		this.tagList.removeTag(FT_FILESIZE);
		Tag tag = new IntTag(FT_FILESIZE, Convert.longToInt(fileSize));
		this.tagList.addTag(tag);
	}
	
	

	public PartHashSet getFileHashSet() {
		return fileHashSet;
	}
	
	public void setFileHashSet(PartHashSet fileHashSet) {
		this.fileHashSet = fileHashSet;
	}


	public byte getPartFileFormat() {
		return partFileFormat;
	}


	public void setPartFileFormat(byte partFileFormat) {
		this.partFileFormat = partFileFormat;
	}


	public int getModDate() {
		return modDate;
	}

	public void setModDate(int modDate) {
		this.modDate = modDate;
	}

	public TagList getTagList() {
		return tagList;
	}

	public void setTagList(TagList tagList) {
		this.tagList = tagList;
	}
	
	public GapList getGapList() {
		return gapList;
	}

	public void setGapList(GapList gapList) {
		this.gapList = gapList;
	}
	
	public String getName() {
		return file.getName();
	}
	
	public FileHash getFileHash() {
		return fileHash;
	}

	public void setFileHash(FileHash fileHash) {
		this.fileHash = fileHash;
	}
	
}
