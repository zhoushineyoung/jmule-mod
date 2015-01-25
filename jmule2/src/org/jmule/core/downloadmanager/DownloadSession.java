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

import static org.jmule.core.edonkey.ED2KConstants.BLOCKSIZE;
import static org.jmule.core.edonkey.ED2KConstants.FT_FILENAME;
import static org.jmule.core.edonkey.ED2KConstants.FT_FILESIZE;
import static org.jmule.core.edonkey.ED2KConstants.OP_FILENAMEREQUEST;
import static org.jmule.core.edonkey.ED2KConstants.OP_FILESTATREQ;
import static org.jmule.core.edonkey.ED2KConstants.PARTSIZE;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.DataFormatException;

import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.downloadmanager.DownloadStatusList.PeerDownloadInfo;
import org.jmule.core.downloadmanager.strategy.DownloadStrategy;
import org.jmule.core.downloadmanager.strategy.DownloadStrategyFactory;
import org.jmule.core.downloadmanager.strategy.DownloadStrategyFactory.STRATEGY;
import org.jmule.core.edonkey.ED2KConstants.PeerFeatures;
import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.edonkey.packet.tag.IntTag;
import org.jmule.core.edonkey.packet.tag.StringTag;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.peermanager.InternalPeerManager;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.peermanager.PeerManagerException;
import org.jmule.core.peermanager.PeerManagerSingleton;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.core.session.JMTransferSession;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.sharingmanager.JMuleBitSet;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.sharingmanager.SharedFileException;
import org.jmule.core.sharingmanager.SharingManagerSingleton;
import org.jmule.core.uploadmanager.FileChunkRequest;
import org.jmule.core.uploadmanager.InternalUploadManager;
import org.jmule.core.uploadmanager.UploadManagerSingleton;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.JMuleZLib;
import org.jmule.core.utils.Misc;

/**
 * Created on 2008-Apr-20
 * @author binary256
 * @version $$Revision: 1.59 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/09/04 16:08:38 $$
 */
public class DownloadSession implements JMTransferSession {
	
	public enum DownloadStatus {
		STARTED, STOPPED
	}
	
	PartialFile sharedFile;
	FilePartStatus partStatus;
	DownloadStatus sessionStatus = DownloadStatus.STOPPED;
	DownloadStrategy download_strategy = null;
	FileRequestList file_request_list = new FileRequestList();
	Collection<Peer> session_peers = new ConcurrentLinkedQueue<Peer>();
	DownloadStatusList download_status_list = new DownloadStatusList(); // store main information about download process
	
	Map<Peer, Collection<CompressedChunkContainer>> compressedFileChunks = new ConcurrentHashMap<Peer, Collection<CompressedChunkContainer>>();

	InternalNetworkManager _network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
	InternalPeerManager _peer_manager = (InternalPeerManager) PeerManagerSingleton.getInstance();
	InternalDownloadManager _download_manager = (InternalDownloadManager) DownloadManagerSingleton.getInstance();
	InternalUploadManager _upload_manager = (InternalUploadManager) UploadManagerSingleton.getInstance();

	private Collection<String> file_names = new ConcurrentLinkedQueue<String>();
		
	public DownloadStatusList getDownloadStatusList() {
		return download_status_list;
	}
	
	
	private DownloadSession() {
		download_strategy = DownloadStrategyFactory.getStrategy(STRATEGY.DEFAULT, this);
	}

	DownloadSession(ED2KFileLink fileLink) {
		this();
		try {
			TagList tagList = new TagList();
			Tag tag;
			tag = new StringTag(FT_FILENAME, fileLink.getFileName());
			tagList.addTag(tag);
			tag = new IntTag(FT_FILESIZE, Convert.longToInt((fileLink.getFileSize())));
			tagList.addTag(tag);
			createDownloadFiles(fileLink.getFileName(), fileLink.getFileSize(), fileLink.getFileHash(), null, tagList);
			int partCount = (int) ((sharedFile.length() / PARTSIZE));
			if (sharedFile.length() % PARTSIZE != 0)
				partCount++;
			partStatus = new FilePartStatus(partCount);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	DownloadSession(PartialFile partialFile) {
		this();
		this.sharedFile = partialFile;
		this.sharedFile.getGapList().sort();
		int partCount = (int) ((sharedFile.length() / PARTSIZE));
		if (sharedFile.length() % PARTSIZE != 0)
			partCount++;
		partStatus = new FilePartStatus(partCount);
	}

	DownloadSession(SearchResultItem searchResult) {
		this();
		try {
			createDownloadFiles(searchResult.getFileName(), searchResult
					.getFileSize(), searchResult.getFileHash(), null,
					(TagList) searchResult);
			int partCount = (int) ((sharedFile.length() / PARTSIZE));
			if (sharedFile.length() % PARTSIZE != 0)
				partCount++;
			partStatus = new FilePartStatus(partCount);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private void createDownloadFiles(String fileName, long fileSize, FileHash fileHash, PartHashSet hashSet, TagList tagList) throws SharedFileException {
		sharedFile = new PartialFile(fileName, fileSize, fileHash, hashSet, tagList);
		SharingManagerSingleton.getInstance().addPartialFile(sharedFile);
	}

	void startDownload() {
		this.setStatus(DownloadStatus.STARTED);
		sharedFile.markDownloadStarted();
		download_strategy.downloadStarted();
	}
	
	void stopDownload() {
		stopDownload(true);
	}

	void stopDownload(boolean saveDownloadStatus) {
		compressedFileChunks.clear();
		
		for (Peer peer : download_status_list.getPeersByStatus(PeerDownloadStatus.ACTIVE, PeerDownloadStatus.ACTIVE_UNUSED))
			_network_manager.sendSlotRelease(peer.getIP(), peer.getPort());
		
		session_peers.clear();
		download_status_list.clear();
		partStatus.clear();
		file_request_list.clear();

		setStatus(DownloadStatus.STOPPED);
		if (saveDownloadStatus)
			sharedFile.markDownloadStopped();
		
		download_strategy.downloadStopped();
	}
	
	void cancelDownload() {
		if (isStarted())
			stopDownload(false);
		SharingManagerSingleton.getInstance().removeSharedFile(sharedFile.getFileHash());
		
		download_strategy.downloadCancelled();
	}
	
	void addDownloadPeers(List<Peer> peerList) {
		for (Peer peer : peerList) {
			if (_download_manager.hasPeer(peer))
				continue;
			if (_upload_manager.hasPeer(peer))
				continue;
			if (session_peers.contains(peer))
				continue;
			if (hasPeer(peer))
				continue;
			session_peers.add(peer);
			download_strategy.peerAdded(peer);
			if (peer.isConnected())
				peerConnected(peer);
			else
				try {
					_peer_manager.connect(peer);
				} catch (PeerManagerException e) {
					e.printStackTrace();
				}
		}
	}

	void peerConnected(Peer peer) {
		if (this.getStatus() == DownloadStatus.STOPPED)
			return;
		if (download_status_list.getPeerDownloadStatus(peer)==PeerDownloadStatus.IN_QUEUE) {
			requestStatusFileName(peer);
		} else {
			download_status_list.addPeer(peer);
			download_strategy.peerConnected(peer);
			requestStatusFileName(peer);
		}
	}

	void peerConnectingFailed(Peer peer, Throwable cause) {
		download_strategy.peerConnectingFailed(peer, cause);
		peerDisconnected(peer);
	}

	void peerDisconnected(Peer peer) {
		//TODO : mark in status list as disconnected, update download_status_list
		compressedFileChunks.remove(peer);
		PeerDownloadStatus status = download_status_list.getPeerDownloadStatus(peer);
		if ((status == null) || (status != PeerDownloadStatus.IN_QUEUE)) {
			partStatus.removePartStatus(peer);
			file_request_list.remove(peer);
			session_peers.remove(peer);
			download_status_list.removePeer(peer);
		}
		download_strategy.peerDisconnected(peer);

	}
	
	void requestStatusFileName(Peer peer) {
		Integer supportMultipacket = peer.getFeature(PeerFeatures.MultiPacket);
		if (supportMultipacket == 1) {
			Collection<Byte> entries = new ArrayList<Byte>();
			entries.add(OP_FILENAMEREQUEST);
			entries.add(OP_FILESTATREQ);
			Integer supportExtMultipacket = peer.getFeature(PeerFeatures.ExtMultiPacket);
			if (supportExtMultipacket==1) {
				_network_manager.sendMultiPacketExtRequest(peer.getIP(), peer.getPort(), getFileHash(), getFileSize(), entries);
			}
			else {
				_network_manager.sendMultiPacketRequest(peer.getIP(), peer.getPort(), getFileHash(),entries);
			}
			download_status_list.setPeerStatus(peer, PeerDownloadStatus.FILENAME_REQUEST);
			download_status_list.setPeerStatus(peer, PeerDownloadStatus.FILE_STATUS_REQUEST);
		} else {
			if (peer.getFeature(PeerFeatures.ExtendedRequestsVersion) > 1)
				_network_manager.sendFileNameRequest(peer.getIP(), peer.getPort(),getFileHash(), sharedFile.getGapList(), sharedFile.length(),  getCompletedSources());
			else
				_network_manager.sendFileNameRequest(peer.getIP(), peer.getPort(),getFileHash());
			download_status_list.setPeerStatus(peer, PeerDownloadStatus.FILENAME_REQUEST);
			_network_manager.sendFileStatusRequest(peer.getIP(), peer.getPort(), getFileHash());
			download_status_list.setPeerStatus(peer, PeerDownloadStatus.FILE_STATUS_REQUEST);
		}
	}
	
	void receivedFileNotFoundFromPeer(Peer sender) {
		download_status_list.addPeerHistoryRecord(sender, "File not found");
		download_status_list.setPeerStatus(sender,PeerDownloadStatus.DISCONNECTED);
		download_strategy.receivedFileNotFoundFromPeer(sender);
		try {
			_peer_manager.disconnect(sender);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	void receivedFileRequestAnswerFromPeer(Peer sender,String fileName) {
		if (!file_names.contains(fileName))
			file_names.add(fileName);
		download_status_list.updatePeerTime(sender);
		download_status_list.addPeerHistoryRecord(sender, "File request answer");
		download_status_list.setPeerStatus(sender,PeerDownloadStatus.UPLOAD_REQUEST);
		download_strategy.receivedFileRequestAnswerFromPeer(sender, fileName);
		_network_manager.sendUploadRequest(sender.getIP(), sender.getPort(), sharedFile.getFileHash());
	}
	
	void receivedFileStatusResponseFromPeer(Peer sender,FileHash fileHash, JMuleBitSet bitSetpartStatus) {
		JMuleBitSet bitSet = bitSetpartStatus;
		if (bitSet.getBitCount() == 0) {
			int partCount = (int) ((this.sharedFile.length() / PARTSIZE));
			if (this.sharedFile.length() % PARTSIZE != 0)
				partCount++;
			bitSet = new JMuleBitSet(partCount);
			bitSet.setBitCount(partCount);
			for (int i = 0; i < partCount; i++)
				bitSet.set(i);
		}
		partStatus.addPartStatus(sender, bitSet);
		download_strategy.receivedFileStatusResponseFromPeer(sender, fileHash, bitSetpartStatus);
		if (!this.sharedFile.hasHashSet()) {
			download_status_list.setPeerStatus(sender, PeerDownloadStatus.HASHSET_REQUEST);
			_network_manager.sendPartHashSetRequest(sender.getIP() , sender.getPort(),getFileHash());
		}
	}
	
	void receivedHashSetResponseFromPeer(Peer sender,PartHashSet partHashSet) {
		download_status_list.addPeerHistoryRecord(sender, "Hash set answer");
		if (!sharedFile.hasHashSet()) {
			try {
				sharedFile.setHashSet(partHashSet);
			} catch (SharedFileException e) {
				// sender.ban();
				return;
			}
		}
		
		download_strategy.receivedHashSetResponseFromPeer(sender, partHashSet);
		
		if (this.sharedFile.getGapList().size() == 0) {
			/*if (sharedFile.checkFullFileIntegrity()) {
				completeDownload();
				return;
			} else {
				// file is broken
			}*/
			
			_download_manager.scheduleToComplete(this);
		}
		return;
	}
	
	void receivedQueueRankFromPeer(Peer sender, int queueRank) {
		download_status_list.setPeerQueuePosition(sender, queueRank);
		download_strategy.receivedQueueRankFromPeer(sender, queueRank);
	}

	void receivedSlotGivenFromPeer(Peer sender) {
		download_status_list.setPeerStatus(sender, PeerDownloadStatus.ACTIVE);
		download_status_list.setPeerResendCount(sender, 0);
		download_status_list.addPeerHistoryRecord(sender, "Slot given");
		download_strategy.receivedSlotGivenFromPeer(sender);
		if (this.sharedFile.getGapList().size() == 0) {
			_download_manager.scheduleToComplete(this);
			return;
		}
			/*if (sharedFile.checkFullFileIntegrity()) {
				completeDownload();
				return ;
			} else {
				// file is broken
			}*/
		fileChunkRequest(sender);
		return;

	}
	
	void receivedSlotTakenFromPeer(Peer sender) {
		download_status_list.addPeerHistoryRecord(sender, "Slot taken");
		download_status_list.setPeerStatus(sender,PeerDownloadStatus.SLOTREQUEST);
		download_strategy.receivedSlotTakenFromPeer(sender);
	}
	
	/**
	 * Request file chunk from peer
	 * @param peer
	 */
	void fileChunkRequest(Peer peer) {
		if (peer == null) {
			return ;
		}
		if (!peer.isConnected())
			return;
		long blockSize;
		try {
			blockSize = ConfigurationManagerSingleton.getInstance().getDownloadBandwidth();
		} catch (ConfigurationManagerException e) {
			blockSize = BLOCKSIZE;
		}

		if (blockSize > BLOCKSIZE)
			blockSize = BLOCKSIZE;
		
		FileChunkRequest fragments[] = download_strategy.fileChunk3Request(peer,blockSize, this.sharedFile.length(), sharedFile.getGapList(),partStatus, file_request_list);

		int unused = 0;
		for (int i = 0; i < fragments.length; i++)
			if (((fragments[i].getChunkBegin() == fragments[i].getChunkEnd()) && (fragments[i].getChunkBegin() == 0)))
				unused++;
		
		if (unused == 3) {
			download_status_list.setPeerStatus(peer,
					PeerDownloadStatus.ACTIVE_UNUSED);
			download_status_list.setPeerChunkRequests(peer, null);
			return;
		}
		download_status_list.setPeerChunkRequests(peer, fragments);
		download_status_list.setPeerStatus(peer, PeerDownloadStatus.ACTIVE);
		
		_network_manager.sendFilePartsRequest(peer.getIP(), peer.getPort(),
				getFileHash(), fragments);
	}

	void receivedRequestedFileChunkFromPeer(Peer sender,FileHash fileHash, FileChunk chunk) {
		processFileChunk(sender, chunk);
		download_strategy.receivedRequestedFileChunkFromPeer(sender, fileHash, chunk);
	}
	
	void receivedCompressedFileChunk(Peer sender, FileChunk compressedFileChunk) {
		//if (sender == null) return ; // or maybe write chunk!
		download_status_list.updatePeerTime(sender);
		if (!compressedFileChunks.containsKey(sender)) {
			Collection<CompressedChunkContainer> list = new ConcurrentLinkedQueue<CompressedChunkContainer>();
			compressedFileChunks.put(sender, list);
		}
		Collection<CompressedChunkContainer> chunk_list = compressedFileChunks.get(sender);
		
		boolean found = false;
		for (CompressedChunkContainer container : chunk_list) {
			if (container.offset == compressedFileChunk.getChunkStart()) {
				compressedFileChunk.getChunkData().position(0);
				found = true;
				container.compressedData.put(compressedFileChunk.getChunkData().array());
				if (container.compressedData.position() == container.compressedData.capacity()) {// TODO : Fixme
					container.compressedData.position(0);
					ByteBuffer buffer;
					try {
						buffer = JMuleZLib.decompressData(container.compressedData);
						buffer.position(0);
						FileChunk chunk = new FileChunk(container.offset,container.offset + buffer.capacity(), buffer);
						processFileChunk(sender, chunk);
						chunk_list.remove(container);
					} catch (DataFormatException e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}
		if (!found) {
			CompressedChunkContainer container = new CompressedChunkContainer();
			container.offset = compressedFileChunk.getChunkStart();
			container.compressedData = Misc.getByteBuffer(compressedFileChunk.getChunkEnd());
			container.compressedData.put(compressedFileChunk.getChunkData().array());
			chunk_list.add(container);
			if (container.compressedData.position() == container.compressedData.capacity()) {// TODO : Fixme
				container.compressedData.position(0);
				ByteBuffer buffer;
				try {
					buffer = JMuleZLib.decompressData(container.compressedData);
					buffer.position(0);
					FileChunk chunk = new FileChunk(container.offset, container.offset + buffer.capacity(), buffer);
					processFileChunk(sender, chunk);
					chunk_list.remove(container);
				} catch (DataFormatException e) {

					e.printStackTrace();
				}

			}
		}
		download_strategy.receivedCompressedFileChunk(sender, compressedFileChunk);
	}
	
	void receivedSourcesAnswerFromPeer(Peer peer, List<Peer> peerList) {
		addDownloadPeers(peerList);
		download_strategy.receivedSourcesAnswerFromPeer(peer, peerList);
	}
	
	
	private void processFileChunk(Peer sender, FileChunk fileChunk) {
		download_status_list.updatePeerTime(sender);
		
		if (fileChunk.getChunkStart() < 0) {
			return;
		}
		
		if (fileChunk.getChunkEnd() > sharedFile.length()) {
			return;
		}
		
		download_status_list.setPeerStatus(sender, PeerDownloadStatus.ACTIVE);
		download_status_list.setPeerResendCount(sender, 0);
		try {
			sharedFile.writeData(fileChunk);
		} catch (SharedFileException e2) {
			e2.printStackTrace();
		}
		
		file_request_list.splitFragment(sender, fileChunk.getChunkStart(),fileChunk.getChunkEnd());

		_download_manager.scheduleForPartCheck(this);
		
		if (this.sharedFile.getGapList().size() == 0)

			if (!sharedFile.hasHashSet()) {
				// Request hash set
				List<Peer> peer_list = download_status_list.getPeersByStatus(PeerDownloadStatus.ACTIVE, PeerDownloadStatus.ACTIVE_UNUSED);
				for (Peer peer : peer_list) {
					_network_manager.sendPartHashSetRequest(peer.getIP(), peer.getPort(), getFileHash());
				}
				return;
			} else {

				_download_manager.scheduleToComplete(this);
				
				/*if (sharedFile.checkFullFileIntegrity()) {
					completeDownload();
					return;
				}
				// file is broken
				_network_manager.sendUploadRequest(sender.getIP(), sender.getPort(), getFileHash());
				download_status_list.setPeerStatus(sender,PeerDownloadStatus.UPLOAD_REQUEST);
				List<Peer> peer_list = download_status_list.getPeersByStatus(PeerDownloadStatus.HASHSET_REQUEST, PeerDownloadStatus.ACTIVE_UNUSED);
				for (Peer peer : peer_list) {
					_network_manager.sendUploadRequest(peer.getIP(), peer.getPort(), getFileHash());
					download_status_list.setPeerStatus(peer,PeerDownloadStatus.UPLOAD_REQUEST);
				}*/
			}
		FragmentList list = file_request_list.get(sender);
		if (list != null)
			if (this.file_request_list.get(sender).size() != 0) {
				// Have to receive more packets
				return;
			}

		fileChunkRequest(sender);
	}

	void removePeer(Peer peer) {
		if (!hasPeer(peer))
			return;
		
		if (peer.isConnected())
			_network_manager.sendSlotRelease(peer.getIP(), peer.getPort());
		
		session_peers.remove(peer);
		compressedFileChunks.remove(peer);
		download_status_list.removePeer(peer);
		
		download_strategy.peerRemoved(peer);
	}
	
	void completeDownload() {
		stopDownload();
		try {
			SharingManagerSingleton.getInstance().makeCompletedFile(sharedFile.getFileHash());
		} catch (Throwable t) {
			t.printStackTrace();
		}

		try {
			DownloadManagerSingleton.getInstance().cancelDownload(getFileHash());
		} catch (DownloadManagerException e) {
			e.printStackTrace();
		}
	}
	
	private void setStatus(DownloadStatus status) {
		this.sessionStatus = status;
	}

	public FileHash getFileHash() {
		return this.sharedFile.getFileHash();
	}

	public long getFileSize() {
		return this.sharedFile.length();
	}

	public GapList getGapList() {
		return this.sharedFile.getGapList();
	}

	public String getMetFilePath() {
		return sharedFile.getPartMetFile().getAbsolutePath();
	}

	public long getPartCount() {
		return Misc.getPartCount(sharedFile.length());
	}

	public int getPartialSources() {
		return partStatus.getPartialSources();
	}

	public int getPeerCount() {
		return session_peers.size();
	}

	public PeerDownloadInfo getPeerDownloadStatus(Peer peer) {
		return download_status_list.getPeerDownloadInfo(peer);
	}

	public List<Peer> getPeers() {
		return Arrays.asList(session_peers.toArray(new Peer[0]));
	}

	public double getPercentCompleted() {
		return this.sharedFile.getPercentCompleted();
	}

	public SharedFile getSharedFile() {
		return sharedFile;
	}

	public String getSharingName() {
		return sharedFile.getSharingName();
	}

	public float getSpeed() {
		float downloadSpeed = 0;
		for (Peer peer : session_peers) {
			downloadSpeed += peer.getDownloadSpeed();
		}
		return downloadSpeed; 
	}

	public DownloadStatus getStatus() {
		return sessionStatus;
	}

	public String getTempFileName() {
		return sharedFile.getFile().getAbsolutePath();
	}

	public long getTransferredBytes() {
		return this.sharedFile.getDownloadedBytes();
	}

	public int getUnknownSources() {
		return session_peers.size() - (getCompletedSources() + getPartialSources());
	}

	public int hashCode() {
		return this.sharedFile.getFileHash().hashCode();
	}

	public boolean hasPeer(Peer peer) {
		return session_peers.contains(peer) || download_status_list.hasPeer(peer);
	}
	
	boolean hasPeer(String ip, int port) {
		for(Peer peer : session_peers) 
			if (peer.getIP().equals(ip))
				if (peer.getPort() == port) return true;
		return false;
	}

	public boolean isStarted() {
		return sessionStatus == DownloadStatus.STARTED;
	}
	
	public long getAvailablePartCount() {
		return sharedFile.getAvailablePartCount();
	}

	public int getCompletedSources() {
		return partStatus.getCompletedSources();
	}

	public ED2KFileLink getED2KLink() {
		return new ED2KFileLink(getSharingName(), getFileSize(), getFileHash());
	}

	public long getETA() {
		float speed = getSpeed();
		long time = 0;
		if (speed != 0)
			time = (long) ((sharedFile.length() - getTransferredBytes()) / speed);
		else
			time = Misc.INFINITY_AS_INT;

		return time;
	}
	
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (!(object instanceof DownloadSession))
			return false;
		return this.hashCode() == object.hashCode();
	}
	
	public JMuleBitSet getPartAvailability(Peer peer) {
		return partStatus.get(peer);
	}
	
	public String toString() {
		String str = "[ ";
		str += sharedFile.getSharingName() + " " + sharedFile.getGapList()
				+ " " + sharedFile.getFileHash() + "\n";
		str += "Peer used by session : \n";
		for (Peer peer : session_peers)
			str += peer + "\n";
//		str += "\nDownload status list :\n" + download_status_list + "\n";
//		String c_chunks = " [ ";
//		for(Peer peer : compressedFileChunks.keySet()) {
//			c_chunks += peer + " : " + compressedFileChunks.get(peer)+"\n";
//		}
//		c_chunks += " ]";
//		str += "\nCompressed chunks : " + c_chunks + "\n"; 
//		str += fileRequestList + "\n";
		str += "]";
		return str;
	}
	
	public FilePartStatus getPartStatus() {
		return partStatus;
	}

	public Collection<String> getFileNames() {
		return file_names;
	}
	
	public FileRequestList getFileRequestList() {
		return file_request_list;
	}
	
	
	class CompressedChunkContainer {
		public ByteBuffer compressedData;
		public long offset;

		public String toString() {
			return "Offset : " + offset + " position : " + compressedData.position() + " capacity : "
					+ compressedData.capacity();
		}

	}
	
}
