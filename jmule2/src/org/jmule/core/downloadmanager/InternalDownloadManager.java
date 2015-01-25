/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule team ( jmule@jmule.org / http://jmule.org )
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

import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.servermanager.Server;
import org.jmule.core.sharingmanager.JMuleBitSet;

/**
 * Created on Aug 29, 2009
 * 
 * @author binary256
 * @version $Revision: 1.10 $ Last changed by $Author: binary255 $ on $Date: 2010/08/15 12:29:52 $
 */
public interface InternalDownloadManager extends DownloadManager {

	public void scheduleForPartCheck(DownloadSession session);
	
	public void scheduleToComplete(DownloadSession session);
	
	public void receivedCompressedFileChunk(Peer sender,FileHash fileHash, FileChunk compressedFileChunk);

	public void receivedFileNotFoundFromPeer(Peer sender,FileHash fileHash);

	public void receivedFileRequestAnswerFromPeer(Peer sender,FileHash fileHash, String fileName);

	public void receivedFileStatusResponseFromPeer(Peer sender,FileHash fileHash, JMuleBitSet partStatus);

	public void receivedHashSetResponseFromPeer(Peer sender,FileHash fileHash, PartHashSet partHashSet);

	public void receivedQueueRankFromPeer(Peer sender,int queueRank);

	public void receivedRequestedFileChunkFromPeer(Peer sender,FileHash fileHash, FileChunk chunk);

	public void receivedSlotGivenFromPeer(Peer sender);

	public void receivedSlotTakenFromPeer(Peer sender);
	
	public void receivedSourcesAnswerFromPeer(Peer peer, FileHash fileHash, List<Peer> peerList);
	
	/**
	 * Add peers which have fileHash to the download session identified by
	 * fileHash
	 * 
	 * @param fileHash
	 * @param peerList
	 */
	public void addDownloadPeers(FileHash fileHash, List<Peer> peerList);

	public boolean hasPeer(Peer peer);

	public void peerRemoved(Peer peer);
	
	public void peerDisconnected(Peer peer);
	
	public void peerConnected(Peer peer);
	
	public void peerConnectingFailed(Peer peer, Throwable cause);
	
	public void connectedToServer(Server server);
	
	public void disconnectedFromServer(Server server);

	
	public void jKadConnected();
	
	public void jKadDisconnected();
	
}
