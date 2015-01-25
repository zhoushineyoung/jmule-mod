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
package org.jmule.core.uploadmanager;

import java.util.List;

import org.jmule.core.edonkey.FileHash;
import org.jmule.core.peermanager.Peer;

/**
 * Created on Aug 29, 2009
 * 
 * @author binary256
 * @version $Revision: 1.5 $ Last changed by $Author: binary255 $ on $Date: 2010/05/29 11:40:35 $
 */
public interface InternalUploadManager extends UploadManager {
	
	public void receivedFileRequestFromPeer(Peer sender,FileHash fileHash);

	public void receivedFileStatusRequestFromPeer(Peer sender,FileHash fileHash);

	public void receivedHashSetRequestFromPeer(Peer sender,FileHash fileHash);

	public void receivedSlotRequestFromPeer(Peer sender,FileHash fileHash);

	public void receivedSlotReleaseFromPeer(Peer sender);
	
	public void receivedFileChunkRequestFromPeer(Peer sender,FileHash fileHash, List<FileChunkRequest> requestedChunks);
	
	public void endOfDownload(Peer sender);
	
	public void removeUpload(FileHash fileHash);

	public boolean hasPeer(Peer peer);
	
	public void peerConnected(Peer peer);

	public void peerConnectingFailed(Peer peer, Throwable cause);

	public void peerDisconnected(Peer peer);
	
	public void peerRemoved(Peer peer);
	
}
