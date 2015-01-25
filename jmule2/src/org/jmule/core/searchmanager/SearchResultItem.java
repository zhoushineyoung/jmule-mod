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
package org.jmule.core.searchmanager;

import static org.jmule.core.edonkey.ED2KConstants.FT_FILERATING;
import static org.jmule.core.edonkey.ED2KConstants.TAG_FILE_TYPE_ARC;
import static org.jmule.core.edonkey.ED2KConstants.TAG_FILE_TYPE_AUDIO;
import static org.jmule.core.edonkey.ED2KConstants.TAG_FILE_TYPE_DOC;
import static org.jmule.core.edonkey.ED2KConstants.TAG_FILE_TYPE_IMAGE;
import static org.jmule.core.edonkey.ED2KConstants.TAG_FILE_TYPE_ISO;
import static org.jmule.core.edonkey.ED2KConstants.TAG_FILE_TYPE_PROGRAM;
import static org.jmule.core.edonkey.ED2KConstants.TAG_FILE_TYPE_UNKNOWN;
import static org.jmule.core.edonkey.ED2KConstants.TAG_FILE_TYPE_VIDEO;
import static org.jmule.core.edonkey.ED2KConstants.TAG_NAME_AVIABILITY;
import static org.jmule.core.edonkey.ED2KConstants.TAG_NAME_COMPLETESRC;
import static org.jmule.core.edonkey.ED2KConstants.TAG_NAME_NAME;
import static org.jmule.core.edonkey.ED2KConstants.TAG_NAME_SIZE;
import static org.jmule.core.edonkey.ED2KConstants.archive_extensions;
import static org.jmule.core.edonkey.ED2KConstants.audio_extensions;
import static org.jmule.core.edonkey.ED2KConstants.doc_extensions;
import static org.jmule.core.edonkey.ED2KConstants.image_extensions;
import static org.jmule.core.edonkey.ED2KConstants.iso_extensions;
import static org.jmule.core.edonkey.ED2KConstants.program_extensions;
import static org.jmule.core.edonkey.ED2KConstants.video_extensions;

import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.sharingmanager.FileQuality;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * Created on 2008-Aug-09
 * @author javajox
 * @version $$Revision: 1.9 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/08/15 12:19:34 $$
 */
public class SearchResultItem extends TagList {

	private SearchQueryType resultType = SearchQueryType.SERVER;
	
	private FileHash fileHash;
	private ClientID clientID;
	private short clientPort;
	
	public SearchResultItem(FileHash fileHash, ClientID clientID, short clientPort) {
		super();
		this.fileHash = fileHash;
		this.clientID = clientID;
		this.clientPort = clientPort;
	}
	
	public SearchResultItem(FileHash fileHash, ClientID clientID, short clientPort,SearchQueryType resultType) {
		this(fileHash, clientID,clientPort);
		this.resultType = resultType;
	}
	
	public ED2KFileLink getAsED2KLink() {
		return new ED2KFileLink(getFileName(),getFileSize(),getFileHash());
	}
	
	public String toString() {
		String result="";
		result+="Name : " + this.getFileName()+" Size : "+this.getFileSize();
		return result;
	}
	
	public SearchQueryType getResultType() {
		return resultType;
	}
	
	public String getFileName(){
		try {
			return (String)super.getTag(TAG_NAME_NAME).getValue();
		} catch (Throwable e) {
			return null;
		}
	}
	
	public long getFileSize() {
		try {
			return Convert.intToLong((Integer)super.getTag(TAG_NAME_SIZE).getValue());
		} catch (Throwable e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int getFileAviability() {
		try {
			return (Integer)super.getTag(TAG_NAME_AVIABILITY).getValue();
		} catch (Throwable e) {
			return 0;
		}
	}
	
	public int getFileCompleteSrc() {
		try {
			return (Integer)super.getTag(TAG_NAME_COMPLETESRC).getValue();
		} catch (Throwable e) {
			return 0;
		}
	}
	
	public FileQuality getFileQuality() {
		if (!hasTag(FT_FILERATING)) return FileQuality.NOTRATED;
		try {
			int quality = (Integer)super.getTag(FT_FILERATING).getValue();
			return FileQuality.getAsFileQuality(quality);
		} catch (Throwable e) {
			return FileQuality.NOTRATED;
		}
	}
	
	public byte[] getMimeType() {
		String file_name = getFileName();
		
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

	public FileHash getFileHash() {
		return fileHash;
	}

	public void setFileHash(FileHash fileHash) {
		this.fileHash = fileHash;
	}

	public ClientID getClientID() {
		return clientID;
	}

	public void setClientID(ClientID clientID) {
		this.clientID = clientID;
	}

	public short getClientPort() {
		return clientPort;
	}

	public void setClientPort(short clientPort) {
		this.clientPort = clientPort;
	}
	
}
