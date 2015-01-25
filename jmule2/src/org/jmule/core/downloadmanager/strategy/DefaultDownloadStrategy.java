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
package org.jmule.core.downloadmanager.strategy;

import static org.jmule.core.edonkey.ED2KConstants.PARTSIZE;

import java.util.Collection;
import java.util.List;

import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.downloadmanager.FileFragment;
import org.jmule.core.downloadmanager.FilePartStatus;
import org.jmule.core.downloadmanager.FileRequestList;
import org.jmule.core.downloadmanager.PeerDownloadStatus;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.sharingmanager.Gap;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.sharingmanager.JMuleBitSet;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.uploadmanager.FileChunkRequest;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.6 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/08/24 17:32:26 $$
 */
public class DefaultDownloadStrategy implements DownloadStrategy {

	private DownloadSession session;
	
	public DefaultDownloadStrategy(DownloadSession session) {
		this.session = session;
	}
	
	@Override
	public FileChunkRequest fileChunkRequest(Peer sender, long blockSize,long fileSize, GapList gapList, FilePartStatus filePartStatus, FileRequestList fileRequestList) {
		int availibility[] = filePartStatus.getPartAvailibility();
		int countSet = 0;// Total count of downloaded parts
		JMuleBitSet bit_set_availability = filePartStatus.get(sender);
		for (int i = 0; i < filePartStatus.getPartAvailibility().length; i++) {
			if (availibility[i] != 0)
				if (bit_set_availability.get(i)) {
					if (gapList.getIntersectedGaps(PARTSIZE * i,
							PARTSIZE * (i + 1) - 1).size() == 0) {
						availibility[i] = 0;// This part is downloaded
						countSet++;
					}
				} else
					availibility[i] = 0;// Sender don't have i part
		}
		if (countSet == availibility.length) {
			// Have all parts, end download
			return null;
		}
		do {
			// Obtain minimal sources part
			int minPos = -1;
			for (int i = 0; i < availibility.length; i++)
				if (availibility[i] != 0) {
					if (minPos == -1)
						minPos = i;
					else
					if (availibility[i] < availibility[minPos])
						minPos = i;
				}
			if (minPos == -1)
				return null;
			long begin = minPos * PARTSIZE;
			long end = (minPos + 1) * PARTSIZE;
			if (end > fileSize)
				end = fileSize;
			long startPos = 0;
			long endPos = 0;
			Collection<Gap> fg = intersectGapListFileRequstList(gapList,fileRequestList, begin, end);
			if (fg.size() == 0) {
				// Don't have fragments, try another part
				availibility[minPos] = 0;
				continue;
			}
			Gap f1 = (Gap) fg.toArray()[0];
			startPos = f1.getStart();
			endPos = startPos + blockSize;
			while (!((endPos >= f1.getStart()) && (endPos <= f1.getEnd())))
				endPos--;
			return new FileChunkRequest(startPos, endPos);
		} while (true);
	}

	@Override
	public FileChunkRequest[] fileChunk3Request(Peer sender, long blockSize, long fileSize, GapList gapList, FilePartStatus filePartStatus, FileRequestList fileRequestList) {
		FileChunkRequest[] fileChunks = new FileChunkRequest[3];
		for (int i = 0; i < fileChunks.length; i++) {
			FileChunkRequest fileChunk = this.fileChunkRequest(sender,blockSize, fileSize, gapList, filePartStatus,fileRequestList);
			if (fileChunk == null)
				fileChunk = new FileChunkRequest(0, 0);
			else
				fileRequestList.addFragment(sender, fileChunk.getChunkBegin(),fileChunk.getChunkEnd());
			fileChunks[i] = fileChunk;
		}
		return fileChunks;
	}

	/**
	 * Intersect GapList with FileFragment list.
	 * 
	 * @return
	 */
	private Collection<Gap> intersectGapListFileRequstList(GapList gapList, FileRequestList fileRequestList, long begin, long end) {
		// Obtain Gaps from [begin:end] segment
		Collection<Gap> gaps;
		gaps = gapList.getGapsFromSegment(begin, end);
		// Obtain File Fragments from [begin:end] segment
		Collection<FileFragment> fragment;
		fragment = fileRequestList.getFragmentsFromSegment(begin, end);
		boolean stop = true;
		do {
			stop = true;
			for (int i = 0; i < gaps.size(); i++) {
				Gap g = (Gap) gaps.toArray()[i];
				for (int j = 0; j < fragment.size(); j++) {
					FileFragment ff = (FileFragment) fragment.toArray()[j];
					if ((ff.getStart() > g.getStart()) && (ff.getEnd() < g.getEnd()))
						if (ff.getEnd() > g.getStart() && (ff.getEnd() < g.getEnd())) {
							gaps.remove(g);
							Gap g1 = new Gap(g.getStart(), ff.getStart() - 1);
							gaps.add(g1);
							Gap g2 = new Gap(ff.getEnd(), g.getEnd());
							gaps.add(g2);
							stop = false;
							break;
						}
					if ((ff.getStart() > g.getStart()) && (ff.getEnd() < g.getEnd()))
						if (ff.getEnd() >= g.getEnd()) {
							gaps.remove(g);
							Gap g1 = new Gap(g.getStart(), ff.getStart() - 1);
							gaps.add(g1);
							stop = false;
							break;
						}
					if (ff.getStart() <= g.getStart())
						if (ff.getEnd() > g.getStart() && (ff.getEnd() < g.getEnd())) {
							gaps.remove(g);
							Gap g1 = new Gap(ff.getEnd(), g.getEnd());
							gaps.add(g1);
							stop = false;
							break;
						}
					if ((ff.getStart() <= g.getStart()) && (ff.getEnd() >= g.getEnd())) {
						gaps.remove(g);
						stop = false;
						break;
					}
				}
				if (!stop)
					break;
			}
		} while (stop == false);
		return gaps;
	}

	@Override
	public void processPartCheckResult(List<Integer> partsID) {
		PartialFile file = (PartialFile) session.getSharedFile();
		for(Integer id : partsID) {
			long begin = PARTSIZE * id;
			long end = PARTSIZE * (id + 1) - 1;
			file.getGapList().addGap(begin, end);
		}
	}

	@Override
	public void processFileCheckResult(List<Integer> processResult) {
		PartialFile file = (PartialFile) session.getSharedFile();
		boolean file_is_broken = false;
		if (processResult.contains(-1)) {
			System.out.println(" *** Broken file *** " + session.getFileHash());
			file_is_broken = true;
			file.getGapList().addGap(0, file.length());
		}
		
		for(Integer id : processResult) {
			if (id < 0) continue;
			file_is_broken = true;
			long begin = PARTSIZE * id;
			long end = PARTSIZE * (id + 1) - 1;
			file.getGapList().addGap(begin, end);
		}
		
		if (file_is_broken) {
			List<Peer> peer_list = session.getDownloadStatusList().getPeersByStatus(PeerDownloadStatus.HASHSET_REQUEST, PeerDownloadStatus.ACTIVE_UNUSED);
			InternalNetworkManager _network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
			for (Peer peer : peer_list) {
				_network_manager.sendUploadRequest(peer.getIP(), peer.getPort(), file.getFileHash());
				session.getDownloadStatusList().setPeerStatus(peer,PeerDownloadStatus.UPLOAD_REQUEST);
			}
		}
			
	}

	@Override
	public void downloadStarted() {
		
	}

	@Override
	public void downloadStopped() {
		
	}

	@Override
	public void downloadCancelled() {
		
	}

	@Override
	public void peerAdded(Peer peer) {
		
	}

	@Override
	public void peerConnected(Peer peer) {
		
	}

	@Override
	public void peerDisconnected(Peer peer) {
		
	}

	@Override
	public void peerConnectingFailed(Peer peer, Throwable cause) {
		
	}

	@Override
	public void receivedFileNotFoundFromPeer(Peer peer) {
		
	}

	@Override
	public void receivedFileRequestAnswerFromPeer(Peer sender, String fileName) {
		
	}

	@Override
	public void receivedFileStatusResponseFromPeer(Peer sender,FileHash fileHash, JMuleBitSet bitSetpartStatus) {
		
	}

	@Override
	public void receivedHashSetResponseFromPeer(Peer sender,PartHashSet partHashSet) {
		
	}

	@Override
	public void receivedQueueRankFromPeer(Peer sender, int queueRank) {
		
	}

	@Override
	public void receivedSlotGivenFromPeer(Peer sender) {
		
	}

	@Override
	public void receivedSlotTakenFromPeer(Peer sender) {
		
	}

	@Override
	public void receivedRequestedFileChunkFromPeer(Peer sender,FileHash fileHash, FileChunk chunk) {
		
	}

	@Override
	public void receivedCompressedFileChunk(Peer sender,FileChunk compressedFileChunk) {
		
	}

	@Override
	public void receivedSourcesAnswerFromPeer(Peer peer, List<Peer> peerList) {
		
	}

	@Override
	public void peerRemoved(Peer peer) {
		
	}

}