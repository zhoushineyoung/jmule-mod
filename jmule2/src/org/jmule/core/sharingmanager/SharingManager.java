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

import java.io.File;
import java.util.List;

import org.jmule.core.JMIterable;
import org.jmule.core.JMuleManager;
import org.jmule.core.edonkey.FileHash;
/**
 * 
 * @author javajox
 * @version $$Revision: 1.9 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/10/26 16:33:20 $$
 */
public interface SharingManager extends JMuleManager {
	
	public static final String PART_MET_EXTENSION = "part.met"; 
	
	public void addCompletedFileListener(CompletedFileListener listener);
	
	/**
	 * Add partial file for sharing
	 * @param partialFile
	 */
	public void addPartialFile(PartialFile partialFile);
	
	/**
	 * Tells if the file identified by fileHash exists in the shared files hashtable and
	 * also checks if the file exists in the file file system
	 * @param fileHash the given file hash
	 * @return true if the file exists in shared files hashtable and in the file system
	 */
	public boolean existsFile(FileHash fileHash);
	
	/**
	 * Get the completed shared file identified by file hash
	 * @param fileHash
	 * @return Completed shared file
	 */
	public CompletedFile getCompletedFile(FileHash fileHash);
	
	/**
	 * Get all completed files
	 * @return 
	 */
	public List<CompletedFile> getCompletedFiles();
	
	public SharedFile getCurrentHashingFile();
	
	public double getCurrentHashingFilePercent();
	
	/**
	 * Get number of total sharing files
	 * @return
	 */
	public int getFileCount();
	
	/**
	 * Get all partial files
	 * @return
	 */
	public List<PartialFile> getPartialFiles();
	
	/**
	 * Get the partial shared file identified by file hash
	 * @param fileHash
	 * @return
	 */
	public PartialFile getPartialFle(FileHash fileHash);
	
	/**
	 * Get shared file by Java's file object.
	 * @param file
	 * @return
	 */
	public SharedFile getSharedFile(File file);
	
	/**
	 * Get shared file by fileHash.
	 * @param fileHash
	 * @return
	 */
	//TODO : Must throw exception if don't have filehash
	public SharedFile getSharedFile(FileHash fileHash);
	
	/**
	 * Used by PacketFactory in offer files packet creation.
	 * @return
	 */
	public JMIterable<SharedFile> getSharedFiles();
	
	/**
	 * Remove shared file identified by file hash
	 * @param fileHash file hash
	 */
	
	public List<CompletedFile> getUnhashedFiles();
	
	/**
	 * Check if have file identified by fileHash.
	 * @param fileHash
	 * @return
	 */
	public boolean hasFile(FileHash fileHash);
	
	public boolean isLoadingCompletedFileProcessRunning();
	
	public boolean isLoadingPartialFileProcessRunning();
	/**
	 * Loads the completed shared files from shared folders. This is a long time process 
	 * that eats a lot of CPU cycles. Can be interrupted with stopLoadingCompletedFiles()
	 * @see org.jmule.core.sharingmanager.SharingManager#stopLoadingCompletedFiles()
	 */
	public void loadCompletedFiles();

	/**
	 * Loads the partial files from the temp directory. This is a long time process
	 * that eats a lot of CPU cycles. Can be interrupted with stopLoadingPartialFiles()
	 * @see org.jmule.core.sharingmanager.SharingManager#stopLoadingPartialFiles()
	 */
	public void loadPartialFiles();
	
	/**
	 * Move the completed file (identified by fileHash) from temp dir to incoming dir 
	 * @param fileHash
	 * @throws SharingManagerException
	 */
	public void makeCompletedFile(FileHash fileHash) throws SharingManagerException;
	
	public void removeCompletedFileListener(CompletedFileListener listener);
	
	public void removeSharedFile(FileHash fileHash);
	
	/**
	 * Stops the loading process of completed files
	 */
	public void stopLoadingCompletedFiles();
	
	/**
	 * Stops the loading process of partial files
	 */
	public void stopLoadingPartialFiles();
	
	/**
	 * Write the all meta-info about files from completed files hash table in known.met
	 */
	public void writeMetadata();
}
