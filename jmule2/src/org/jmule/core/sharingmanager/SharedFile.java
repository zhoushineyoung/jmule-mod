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

import static org.jmule.core.edonkey.ED2KConstants.FT_FILERATING;
import static org.jmule.core.edonkey.ED2KConstants.TAG_FILE_TYPE_ARC;
import static org.jmule.core.edonkey.ED2KConstants.TAG_FILE_TYPE_AUDIO;
import static org.jmule.core.edonkey.ED2KConstants.TAG_FILE_TYPE_DOC;
import static org.jmule.core.edonkey.ED2KConstants.TAG_FILE_TYPE_IMAGE;
import static org.jmule.core.edonkey.ED2KConstants.TAG_FILE_TYPE_ISO;
import static org.jmule.core.edonkey.ED2KConstants.TAG_FILE_TYPE_PROGRAM;
import static org.jmule.core.edonkey.ED2KConstants.TAG_FILE_TYPE_UNKNOWN;
import static org.jmule.core.edonkey.ED2KConstants.TAG_FILE_TYPE_VIDEO;
import static org.jmule.core.edonkey.ED2KConstants.archive_extensions;
import static org.jmule.core.edonkey.ED2KConstants.audio_extensions;
import static org.jmule.core.edonkey.ED2KConstants.doc_extensions;
import static org.jmule.core.edonkey.ED2KConstants.image_extensions;
import static org.jmule.core.edonkey.ED2KConstants.iso_extensions;
import static org.jmule.core.edonkey.ED2KConstants.program_extensions;
import static org.jmule.core.edonkey.ED2KConstants.video_extensions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.edonkey.packet.tag.IntTag;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.uploadmanager.FileChunkRequest;
import org.jmule.core.utils.MD4FileHasher;
import org.jmule.core.utils.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.18 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/07/31 13:08:35 $$
 */
public abstract class SharedFile {
	
	protected FileChannel readChannel  = null;
	protected FileChannel writeChannel = null;
	protected PartHashSet hashSet = null;
	protected TagList tagList = new TagList();
	protected File file;

	protected Object lock = new Object();
	
	public FileChunk getData(FileChunkRequest chunkData) throws SharedFileException{
		ByteBuffer data = null;
		synchronized(lock) {
			if (readChannel == null) {
			
				try {
					readChannel = new RandomAccessFile(file,"rws").getChannel();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					throw new SharedFileException("Failed to open file : " + file.getName());
				}
			}
			data = Misc.getByteBuffer(chunkData.getChunkEnd()-chunkData.getChunkBegin());
			data.position(0);
			try {
				readChannel.position(chunkData.getChunkBegin());
				
				readChannel.read(data);
			} catch (IOException e) {
				throw new SharedFileException("I/O error on reading file "+this+"\n"+Misc.getStackTrace(e));
			}
		/*try {
			readChannel.close();
			readChannel = null;
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		}
		return new FileChunk(chunkData.getChunkBegin(),chunkData.getChunkEnd(),data);
	}
	
	public abstract boolean isCompleted();
	
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}
	
	public void delete() {
		file.delete();
	}
	
	public String getSharingName() {
		return file.getName();
	}
	
	public byte[] getMimeType() {
		String file_name = getSharingName();
		
		String extension = Misc.getFileExtension(file_name);
		extension = extension.toLowerCase();
		if (audio_extensions.contains(extension))
			return TAG_FILE_TYPE_AUDIO;
		
		if (video_extensions.contains(extension))
			return TAG_FILE_TYPE_VIDEO;
		
		if (image_extensions.contains(extension))
			return TAG_FILE_TYPE_IMAGE;
		
		if (doc_extensions.contains(extension))
			return TAG_FILE_TYPE_DOC;
		
		if (program_extensions.contains(extension))
			return TAG_FILE_TYPE_PROGRAM;
		
		if (archive_extensions.contains(extension))
			return TAG_FILE_TYPE_ARC;
		
		if (iso_extensions.contains(extension))
			return TAG_FILE_TYPE_ISO;
		
		return TAG_FILE_TYPE_UNKNOWN;
	}
	
	public int hashCode() {
		if (hashSet == null)
			return getFile().getAbsolutePath().hashCode();
		return this.getFileHash().hashCode();
	}
	
	public boolean equals(Object object){
		if (object == null) return false;
		if (!(object instanceof SharedFile)) return false;
		SharedFile shared_file = (SharedFile) object;
		return getFile().equals(shared_file.getFile());
		
	}
	
	public FileHash getFileHash() {
		return hashSet.getFileHash();
	}
	
	public File getFile() {
		return file;
	}
	
	public long length() {
		return file.length();
	}
	
	public boolean exists() {
		return file.exists();
	}
	
	public void updateHashes() throws SharedFileException {
		if (readChannel == null)
			try {
				readChannel = new RandomAccessFile(file,"rws").getChannel();
			} catch (FileNotFoundException e) {
				throw new SharedFileException("Shared file not found");
			}
		PartHashSet newSets = MD4FileHasher.calcHashSets(readChannel);
		hashSet = newSets;
	}

	public PartHashSet getHashSet() {
		return hashSet;
	}

	public void setHashSet(PartHashSet hashSet) throws SharedFileException {
		this.hashSet = hashSet;
	}

	public TagList getTagList() {
		return this.tagList;
	}

	public void setTagList(TagList newTagList) {
		this.tagList = newTagList;		
	}
	
	public void setFileQuality(FileQuality quality) {
		int tag_value = quality.getAsInt();
		if (tagList.hasTag(FT_FILERATING))
			tagList.removeTag(FT_FILERATING);
		Tag tag = new IntTag(FT_FILERATING, tag_value);		
		tagList.addTag(tag);
	}
	
	public FileQuality getFileQuality() {
		if (tagList.hasTag(FT_FILERATING)) {
			Tag tag = tagList.getTag(FT_FILERATING);
			try {
				return FileQuality.getAsFileQuality((Integer)tag.getValue());
			} catch (Throwable e) {
				return FileQuality.NOTRATED;
			}
		}
		return FileQuality.NOTRATED;
	}
	
	public ED2KFileLink getED2KLink() {
		return new ED2KFileLink(getSharingName(),length(),getFileHash());
	}
	
	public void closeFile() {
		
		if (readChannel!=null)
			try {
				readChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		if (writeChannel!=null)
			try {
				writeChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		readChannel = writeChannel = null;
	}
}
