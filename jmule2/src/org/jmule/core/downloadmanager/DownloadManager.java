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
package org.jmule.core.downloadmanager;

import java.util.List;

import org.jmule.core.JMuleManager;
import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.core.sharingmanager.PartialFile;

/**
 * Created on 2008-Apr-20
 * @author javajox
 * @author binary
 * @version $$Revision: 1.9 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2010/01/10 14:18:48 $$
 */
public interface DownloadManager extends JMuleManager {

	public void addDownload(SearchResultItem searchResult)
			throws DownloadManagerException;

	public void addDownload(ED2KFileLink fileLink)
			throws DownloadManagerException;

	public void addDownload(String ed2kLinkAsString)
	        throws DownloadManagerException;
	
	/**
	 * Adds a new download and start it, no exception is thrown if something is wrong
	 * this means if something goes wrong we'll never know what really happened
	 * @param ed2kLink as {@link ED2KFileLink}
	 */
	public void addAndStartSilentDownload(ED2KFileLink ed2kLink);
	
	/**
	 * Adds a new download and start it, no exception is thrown if something is wrong
	 * this means if something goes wrong we'll never know what really happened
	 * @param edk2LinkAsString the ed2k link as String
	 */
	public void addAndStartSilentDownload(String ed2kLinkAsString);
	
	/**
	 * Adds a new download and start it, no exception is thrown if something is wrong
	 * this means if something goes wrong we'll never know what really happened, the method returns the download manager,
	 * so it's easy to add many downloads and start them, we can build things like :
	 * <code>
	 *   JMuleCore _core = JMuleCoreFactory.getSingleton();
	 *   _dm = _core.getDownloadManager();
	 *   _dm.addAndStartSilentDownloads("ed2k://|file|jmule-0.5.0_B2.jar|2693693|E8D635CD815FB4AADB4FA59116809542|/").
	 *       addAndStartSilentDownloads("ed2k://|file|jmule-0.5.6.zip|7024938|381FF1A9E3D9E8B758A512E1C9EC2132|/").
	 *       addAndStartSilentDownloads("ed2k://|file|jmule-0.5.6-src.tar.gz|1123446|3F7DD6037CFCA91F6ADF1217D345FD33|/").
	 *       finishAddAndStartSilentDownloads();
	 * </code>
	 * <remark>
	 *   This method works in conjunction with finishAddAndStartSilentDownloads() 
	 * </remark>
	 * @see #finishAddAndStartSilentDownloads()
	 * @param edk2LinkAsString the ed2k link as String
	 * @return DownloadManager
	 */
	public DownloadManager addAndStartSilentDownloads(String edk2LinkAsString);
	
	/**
	 * The stop point for addAndStartSilentDownloads(String ed2kLinkAsString)
	 * @see #addAndStartSilentDownloads(String)
	 */
	public void finishAddAndStartSilentDownloads();
	
	public void addDownload(PartialFile partialFile)
			throws DownloadManagerException;

	public DownloadSession getDownload(FileHash fileHash)
			throws DownloadManagerException;

	public int getDownloadCount();

	public void startDownload(FileHash fileHash)
			throws DownloadManagerException;

	public void startDownload();
	
	public void stopDownload();
	
	public void stopDownload(FileHash fileHash) throws DownloadManagerException;

	public void cancelDownload(FileHash fileHash) throws DownloadManagerException;
	
	/**
	 * Silently cancel the downloads. <b>Attention !!! This is a very dangerous method, no restore point is provided</b>
	 */
	public void cancelSilentDownloads();

	public boolean hasDownload(FileHash fileHash);

	public List<DownloadSession> getDownloads();

	public void addDownloadManagerListener(DownloadManagerListener listener);

	public void removeDownloadMangerListener(DownloadManagerListener listener);
	
	public void addNeedMorePeersListener( NeedMorePeersListener listener );
	
	public void removeNeedMorePeersListener( NeedMorePeersListener listener );
	
}
