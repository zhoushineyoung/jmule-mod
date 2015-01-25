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

import static org.jmule.core.downloadmanager.PeerDownloadStatus.CONNECTED;
import static org.jmule.core.downloadmanager.PeerDownloadStatus.DISCONNECTED;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.peermanager.Peer;
import org.jmule.core.uploadmanager.FileChunkRequest;

/**
 * Store peer status in download process
 * Created on 07-19-2008
 * @author binary256
 * @version $$Revision: 1.13 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/08/25 12:32:56 $$
 */
public class DownloadStatusList {

	private Collection<PeerDownloadInfo> peer_status_list = new ConcurrentLinkedQueue<PeerDownloadInfo>();

	DownloadStatusList() {
	}
	
	public Collection<PeerDownloadInfo> getStatusList() {
		return peer_status_list;
	}
	
	List<Peer> getPeersWithInactiveTime(long inactiveTime) {
		List<Peer> peer_list = new LinkedList<Peer>();
		for (PeerDownloadInfo peer_download_status : peer_status_list) {
			if ((System.currentTimeMillis() - peer_download_status.lastUpdateTime) >= inactiveTime) {
				peer_list.add(peer_download_status.peer);
			}
		}
		return peer_list;
	}
	
	List<Peer> getPeersWithInactiveTime(long inactiveTime, PeerDownloadStatus... downloadStatus) {
		List<Peer> peer_list = new LinkedList<Peer>();
		for (PeerDownloadInfo peer_download_status : peer_status_list) {
			if ((System.currentTimeMillis() - peer_download_status.lastUpdateTime) >= inactiveTime) {
				for (PeerDownloadStatus status : downloadStatus) {
					if (status == peer_download_status.peerStatus)
						peer_list.add(peer_download_status.peer);
				}
			}
		}
		return peer_list;
	}
	
	List<Peer> getPeersWithLastSeenTime(long lastSeenTime, PeerDownloadStatus... downloadStatus) {
		List<Peer> peer_list = new LinkedList<Peer>();
		for (PeerDownloadInfo peer_download_status : peer_status_list) {
			if ((System.currentTimeMillis() - peer_download_status.getPeer().getLastSeen()) >= lastSeenTime) {
				for (PeerDownloadStatus status : downloadStatus) {
					if (status == peer_download_status.peerStatus)
						peer_list.add(peer_download_status.peer);
				}
			}
		}
		return peer_list;
	}
	
	public List<Peer> getPeersByStatus(PeerDownloadStatus... status) {
		List<Peer> peer_list = new LinkedList<Peer>();
		
		for(PeerDownloadInfo peer_download_status : peer_status_list) {
			for(PeerDownloadStatus peer_status : status) {
				if (peer_download_status.peerStatus==peer_status) {
					peer_list.add(peer_download_status.peer);
				}
			}
		}
		return peer_list;
	}
	
	void addPeer(Peer peer) {
		if (hasPeer(peer)) return ;
		PeerDownloadInfo peerStatus = new PeerDownloadInfo(
				peer);
		peer_status_list.add(peerStatus);

	}
	
	/**
	 * Used for debug
	 * @param peer
	 * @param record
	 */
	void addPeerHistoryRecord(Peer peer, String record) {
		PeerDownloadInfo download_status = getPeerDownloadInfo(peer);
		if (download_status != null) {
			download_status.updateHistoryRecord(record);
		}
	}
	
	public void setPeerStatus(Peer peer, PeerDownloadStatus downloadStatus) {
		PeerDownloadInfo download_status = getPeerDownloadInfo(peer);
		if (download_status != null) { // peer may be removed, don't register
										// history for removed peers
			download_status.updateHistoryRecord();
			download_status.lastUpdateTime = System.currentTimeMillis();
			download_status.peerStatus = downloadStatus;
			download_status.queue_rank = -1;
		}
	}
	
	void setPeerQueuePosition(Peer peer, int queuePosition) {
		PeerDownloadInfo download_status = getPeerDownloadInfo(peer);
		if (download_status != null) {
			download_status.updateHistoryRecord();
			download_status.lastUpdateTime = System.currentTimeMillis();
			download_status.peerStatus = PeerDownloadStatus.IN_QUEUE;
			download_status.queue_rank = queuePosition;
		}
	}
	
	void updatePeerTime(Peer peer) {
		PeerDownloadInfo download_info = getPeerDownloadInfo(peer);
		if (download_info != null) {
			download_info.lastUpdateTime = System.currentTimeMillis();
		}
	}
	
	void setPeerChunkRequests(Peer peer, FileChunkRequest[] chunkRequests) {
		PeerDownloadInfo download_status = getPeerDownloadInfo(peer);
		if (download_status != null) {
			download_status.chunkRequests = chunkRequests;
		}
	}
	
	void setPeerResendCount(Peer peer, int resendCount) {
		PeerDownloadInfo download_status = getPeerDownloadInfo(peer);
		if (download_status != null) {
			download_status.resendCount = resendCount;
		}
	}
	
	PeerDownloadStatus getPeerDownloadStatus(Peer peer) {
		PeerDownloadInfo download_status = getPeerDownloadInfo(peer);
		if (download_status != null) 
			return download_status.peerStatus;
		return null;
	}
	
	int getPeerQueuePosition(Peer peer) {
		PeerDownloadInfo download_status = getPeerDownloadInfo(peer);
		if (download_status != null) 
			return download_status.queue_rank;
		return -1;
	}
	
	FileChunkRequest[] getPeerFileChunksRequest(Peer peer) {
		PeerDownloadInfo download_status = getPeerDownloadInfo(peer);
		if (download_status != null) 
			return download_status.chunkRequests;
		return null;
	}
	
	int getPeerResendCount(Peer peer) {
		PeerDownloadInfo download_status = getPeerDownloadInfo(peer);
		if (download_status != null) 
			return download_status.resendCount;
		return -1;
	}
	
	boolean hasPeer(Peer peer) {
		for(PeerDownloadInfo container : peer_status_list)
			if (peer.equals(container.peer))
				return true;
		return false;
	}
	
	void removePeer(Peer peer) {
		if (!hasPeer(peer)) return ;
		peer_status_list.remove(getPeerDownloadInfo(peer));
	}
	
	PeerDownloadInfo getPeerDownloadInfo(Peer peer) {
		for (PeerDownloadInfo status : peer_status_list) {
			if (status.peer.equals(peer))
				return status;
		}
		return null;
	}
	
	void clear() {
		peer_status_list.clear();
	}
	
	public String toString() {
		String result = "";
		for (PeerDownloadInfo download_status : peer_status_list) {
			result += download_status + "\n";
		}
		return result;
	}
	
	public class PeerDownloadInfo {
		Peer peer;
		List<String> statusList = new ArrayList<String>();
		long lastUpdateTime = 0;
		PeerDownloadStatus peerStatus;
		
		FileChunkRequest[] chunkRequests;
		
		int queue_rank = -1;
		int resendCount = 0;
		
		public Peer getPeer() {
			return peer;
		}
		
		public int getResendCount() {
			return resendCount;
		}
		
		public long getLastUpdateTime() {
			return lastUpdateTime;
		}

		public PeerDownloadInfo(Peer statusPeer) {
			this.peer = statusPeer;
			lastUpdateTime = System.currentTimeMillis();
			if (peer.isConnected())
				peerStatus = CONNECTED;
			else
				peerStatus = DISCONNECTED;
			
		}
		
		public int getQueueRank() {
			return queue_rank;
		}
		
		public PeerDownloadStatus getStatus() {
			return peerStatus;
		}
		
		public String toString()  {
			String result = "" + peer + " : ";
			String status = "";
			status = peerStatus + ""; 
			result+= " Current status : "+status+" ";
			
			return result;

		}

		void updateHistoryRecord() {
			String record = peerStatus + "";
			if (queue_rank != -1)
				record += " " + queue_rank;
			
			this.statusList.add(record);
		}
		
		void updateHistoryRecord(String message) {
			this.statusList.add(message);
		}

	}
	
}
