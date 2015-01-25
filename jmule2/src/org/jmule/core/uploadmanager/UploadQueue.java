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
package org.jmule.core.uploadmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.JMIterable;
import org.jmule.core.JMIterator;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.edonkey.ED2KConstants;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.peermanager.InternalPeerManager;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.peermanager.PeerManagerSingleton;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.19 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/07/31 13:11:45 $$
 */
public class UploadQueue {
	private static UploadQueue instance = null;
	
	static UploadQueue getInstance() {
		if (instance == null)
				instance = new UploadQueue();
		return instance;
	}
	
	Map<UserHash, UploadQueueContainer> upload_queue = new ConcurrentHashMap<UserHash, UploadQueueContainer>();
	Collection<UploadQueueContainer>    slot_clients = new ConcurrentLinkedQueue<UploadQueueContainer>();
	enum PeerQueueStatus { SLOTGIVEN, SLOTTAKEN }
	
	private InternalPeerManager _peer_manager;
	
	private UploadQueue() {
		_peer_manager = (InternalPeerManager) PeerManagerSingleton.getInstance();
	}
	
	boolean hasPeer(Peer peer) {
		if (peer.getUserHash() == null) return false;
		return upload_queue.containsKey(peer.getUserHash());
	}
	
	boolean hasPeer(UserHash userHash) {
		if (userHash == null)
			return false;
		return upload_queue.containsKey(userHash);
	}
	
	void addPeer(Peer peer, FileHash fileHash) throws UploadQueueException {
		if (!(size() < ConfigurationManager.UPLOAD_QUEUE_SIZE))
			throw new UploadQueueException("Queue full");
		upload_queue.put(peer.getUserHash(), 
				new UploadQueueContainer(peer,fileHash, ED2KConstants.INITIAL_RATING));
	}
	
	public int getPeerPosition(Peer peer) throws UploadQueueException {
		UploadQueueContainer peer_container = upload_queue.get(peer.getUserHash());
		if (peer_container == null)
			throw new UploadQueueException("Peer " + peer + " not found in UploadQueue");
		float peer_score = getPeerScore(peer_container);
		int position = 1;
		// Hot code
		for (UploadQueueContainer container : upload_queue.values()) {
			if (getPeerScore(container) > peer_score)
				position++;
		}
		return position;
	}
	
	float getPeerScore(UploadQueueContainer container) {
		float rating = container.rating;
		rating *= _peer_manager.getCredit(container.peer);
		float time_in_queue = (System.currentTimeMillis() - container.addTime) / 1000f;
		float score = rating * time_in_queue / 100f;
		return score;
	}
	
	UploadQueueContainer getContainerByUserHash(UserHash hash) {
		return upload_queue.get(hash);
	}
	
	void removePeer(Peer peer) {
		if (!hasPeer(peer))
			return;

		UploadQueueContainer container = upload_queue.get(peer.getUserHash());
		upload_queue.remove(peer.getUserHash());
		if (slot_clients.contains(container))
			slot_clients.remove(container);
	}
	
	public int size() {
		return upload_queue.size();
	}
	
	void clear() {
		upload_queue.clear();
		slot_clients.clear();
	}
	
	boolean isFull() {
		return (!(size() < ConfigurationManager.UPLOAD_QUEUE_SIZE));
	}
	
	public boolean hasSlotPeer(Peer peer) {
		for(UploadQueueContainer container : slot_clients)
			if (container.peer.equals(peer))
				return true;
		return false;
	}
	
	public JMIterable<Peer> getPeers() {
		List<Peer> list = new LinkedList<Peer>();
		for (UploadQueueContainer element : upload_queue.values())
			list.add(element.peer);
		return new JMIterable<Peer>(new JMIterator<Peer>(list.iterator()));
	}
	
	public Collection<Peer> getPeers(FileHash fileHash) {
		List<Peer> result = new LinkedList<Peer>();
		for (UploadQueueContainer element : upload_queue.values())
			if (element.fileHash.equals(fileHash))
				result.add(element.peer);
		return result;
	}
	
	public String toString() {
		String result = "Upload Queue : size : " + upload_queue.size() + "\n";
		/*for (UserHash hash : upload_queue.keySet()) {
			try {
				UploadQueueContainer container = upload_queue.get(hash);
				result += "[ \n { "+hash.toString()+" } \n" + container
						+ "\n Peer position :" + getPeerPosition(container.peer) + "\n]\n";
			} catch (UploadQueueException e) {
				e.printStackTrace();
			}
		}*/
		
		result += "   \nSlot peers : " + slot_clients.size() + "\n";
		/*for (UploadQueueContainer container : slot_clients) {
			try {
				result += "[\n" + container
						+ "\n Peer position :" + getPeerPosition(container.peer) + "\n]\n";
			} catch (UploadQueueException e) {
				e.printStackTrace();
			}
		}*/
		
		return result;
	}
	
	public void recalcSlotPeers(List<UploadQueueContainer> lostSlotPeers, List<UploadQueueContainer> obtainedSlotPeers) {
		List<UploadQueueContainer> max_score_peers = new ArrayList<UploadQueueContainer>();
		for(UploadQueueContainer container : upload_queue.values()) {
			if (container.getLastResponseTime() >= ConfigurationManager.UPLOADQUEUE_REMOVE_TIMEOUT) {
				removePeer(container.peer);
				continue;
			}
			if (max_score_peers.size() < ConfigurationManager.UPLOAD_QUEUE_SLOTS)
				max_score_peers.add(container);
			else {
				float current_peer_score = getPeerScore(container);
				UploadQueueContainer slot_container = null;
				boolean found = false;
				
				for (int i = 0; i < max_score_peers.size(); i++) {
					slot_container = max_score_peers.get(i);
					float c_score = getPeerScore(slot_container);
					if (current_peer_score > c_score) {
						found = true;
						break;
					}
				}
				
				if (found) {
					max_score_peers.remove(slot_container);
					max_score_peers.add(container);
				}
			}
		}
		
		//extract lost slot peers
		for(UploadQueueContainer container : slot_clients) {
			if (!max_score_peers.contains(container)) 
				lostSlotPeers.add(container);
			
		}
		slot_clients.removeAll(lostSlotPeers);
		
		// create list with peers which obtain slot
		for(UploadQueueContainer container : max_score_peers) {
			if (!slot_clients.contains(container)) {
				slot_clients.add(container);
				obtainedSlotPeers.add(container);
			}
		}
		
		
	}

	public void updateLastRequestTime(Peer peer, long time) {
		UploadQueueContainer container = upload_queue.get(peer.getUserHash());
		if (container == null) { return ; }
		container.lastRequestTime = time;
	}

	public class UploadQueueContainer {
		float rating = 0;
		Peer peer;
		long addTime;
		long lastRequestTime;
		FileHash fileHash;
		Set<PeerQueueStatus> peer_status = new HashSet<PeerQueueStatus>();
		
		public UploadQueueContainer(Peer uploadPeer, FileHash fileHash, float initialRating) {
			peer = uploadPeer;
			this.fileHash = fileHash;
			lastRequestTime = addTime = System.currentTimeMillis();
			rating = initialRating;
		}
		
		public long getLastResponseTime() {
			return System.currentTimeMillis() - lastRequestTime;
		}
		
		public String toString() {
			return " "+peer+
			"\n Rating   : "+ rating+
			"\n Score    : "+ getPeerScore(this)+
			"\n Addtime  : " + addTime +
			"\n Last request  : " + lastRequestTime +
			"\n FileHash : " + fileHash
			;
		}

	}
	
}

