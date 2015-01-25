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
package org.jmule.core.session;

import java.util.List;

import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.sharingmanager.SharedFile;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/17 18:19:34 $$
 */
public interface JMTransferSession {

	/**
	 * Get shared file which session is assigned
	 * @return shared file
	 */
	public SharedFile getSharedFile();
	
	/**
	 * Get transfer speed
	 * @return transfer speed
	 */
	public float getSpeed();
	   
	/**
	 * Get total bytes transfered in this session
	 * @return transfered bytes
	 */
	public long getTransferredBytes();
	   
	/** 
	 * Get File hash of file used by this session
	 * @return file hash
	 */
	public FileHash getFileHash();
	   
	/**
	 * Get sharing name of file used by this session
	 * @return sharing name
	 */
	public String getSharingName();
	   
	/**
	 * Get file size of file used by this session
	 * @return
	 */
	public long getFileSize();
	   
	/**
	 * Get all peers used by this session
	 */
	public List<Peer> getPeers();
	
	public int getPeerCount();
	   
	/**
	 * Get ED2K link of processed file 
	 * @return ED2K link of file
	 */
	public ED2KFileLink getED2KLink();
	   
	/**
	 * Get ETA : Estimated Time of Arrival
	 * @return return ETA in seconds or infinity(31536000)
	 */
	public long getETA();
}
