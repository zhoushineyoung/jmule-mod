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
package org.jmule.core.networkmanager;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.ED2KConstants.ServerFeatures;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.packet.KadPacket;
import org.jmule.core.networkmanager.JMConnection.ConnectionStatus;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.searchmanager.SearchQuery;
import org.jmule.core.searchmanager.SearchResultItemList;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.sharingmanager.JMuleBitSet;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.uploadmanager.FileChunkRequest;

/**
 * Created on Aug 19, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.22 $
 * Last changed by $Author: binary255 $ on $Date: 2010/07/31 13:02:49 $
 */
public interface InternalNetworkManager extends NetworkManager {
	
	public void tcpPortChanged();
	
	public void udpPortChanged();
	
	public void udpPortStatusChanged();
	
	public void addPeer(String ip, int port) throws NetworkManagerException;
	
	public void callBackRequest(ClientID clientID);
	
	public void connectToServer(String ip, int port) throws NetworkManagerException;
	
	public void disconnectFromServer() throws NetworkManagerException ;
	
	public void disconnectPeer(String ip, int port);
	
	public void doSearchOnServer(SearchQuery searchQuery);
	
	public float getPeerDownloadServiceSpeed(String peerIP, int peerPort);
	
	public float getPeerDownloadSpeed(String peerIP, int peerPort);
	
	public float getPeerUploadServiceSpeed(String peerIP, int peerPort);
	
	public float getPeerUploadSpeed(String peerIP, int peerPort);
	
	public long getFileDownloadedBytes(String peerIP, int peerPort);
	
	public long getFileUploadedBytes(String peerIP, int peerPort);
	
	public long getServiceDownloadedBytes(String peerIP, int peerPort);
	
	public long getServiceUploadedBytes(String peerIP, int peerPort);
	
	public ConnectionStatus getServerConnectionStatus();
	
	public void offerFilesToServer(ClientID userID, List<SharedFile> filesToShare);
	
	public void peerConnected(String ip, int port);
	
	public void peerConnectingFailed(String ip, int port, Throwable cause);
	
	public void peerDisconnected(String ip, int port);
	
	public void receivedCallBackFailed();
	
	public void receivedCallBackRequest(String ip, int port);
	
	public void receivedCompressedFileChunkFromPeer(String peerIP, int peerPort, FileHash fileHash, FileChunk compressedFileChunk);
	
	public void receivedEMuleHelloFromPeer(String ip, int port,byte clientVersion, byte protocolVersion, TagList tagList);
	
	public void receivedEMuleHelloAnswerFromPeer(String ip, int port, byte clientVersion, byte protocolVersion, TagList tagList);
	
	public void receivedEndOfDownloadFromPeer(String peerIP, int peerPort);
	
	public void receivedFileChunkRequestFromPeer(String peerIP, int peerPort, FileHash fileHash, List<FileChunkRequest> requestedChunks);
	
	public void receivedFileNotFoundFromPeer(String peerIP, int peerPort, FileHash fileHash);
	
	public void receivedFileRequestAnswerFromPeer(String peerIP, int peerPort, FileHash fileHash, String fileName);
	
	public void receivedFileRequestFromPeer(String peerIP, int peerPort, FileHash fileHash);
	
	public void receivedFileStatusRequestFromPeer(String peerIP, int peerPort, FileHash fileHash);
	
	public void receivedFileStatusResponseFromPeer(String peerIP, int peerPort, FileHash fileHash, JMuleBitSet partStatus);
	
	public void receivedHashSetRequestFromPeer(String peerIP, int peerPort, FileHash fileHash);
	
	public void receivedHashSetResponseFromPeer(String peerIP, int peerPort, PartHashSet partHashSet);
	
	public void receivedHelloAnswerFromPeer(String peerIP, 
			  int peerPort, 
			  UserHash userHash, 
			  ClientID clientID,  
			  int peerPacketPort, 
			  TagList tagList, 
			  String serverIP, 
			  int serverPort);
	
	public void receivedHelloFromPeerAndRespondTo(String peerIP, 
			  int peerPort, 
			  UserHash userHash, 
			  ClientID clientID,  
			  int peerPacketPort, 
			  TagList tagList, 
			  String serverIP, 
			  int serverPort);
	
	public void receivedIDChangeFromServer(ClientID clientID, Set<ServerFeatures> serverFeatures);
	
	public void receivedMessageFromServer(String message);
	
	public void receivedNewServerDescription(String ip, int port, int challenge, TagList tagList);
	
	public void receivedQueueRankFromPeer(String peerIP, int peerPort, int queueRank);
	
	public void receivedRequestedFileChunkFromPeer(String peerIP, int peerPort, FileHash fileHash, FileChunk chunk);
	
	public void receivedSearchResult(SearchResultItemList resultList);
	
	public void receivedServerDescription(String ip, int port, String name, String description);
	
	public void receivedServerList(List<String> ipList, List<Integer> portList);
	
	public void receivedServerStatus(int userCount, int fileCount);
	
	public void receivedOldServerStatus(String ip, int port, int challenge, long userCount, long fileCount);
	
	public void receivedServerStatus(String ip , int port, int challenge, long userCount, long fileCount, long softLimit, long hardLimit, Set<ServerFeatures> serverFeatures);

	public void receivedSlotGivenFromPeer(String peerIP, int peerPort);

	public void receivedSlotReleaseFromPeer(String peerIP, int peerPort);

	public void receivedSlotRequestFromPeer(String peerIP, int peerPort, FileHash fileHash);
	
	public void receivedSlotTakenFromPeer(String peerIP, int peerPort);
	
	public void receivedSourcesFromServer(FileHash fileHash, List<String> clientIPList, List<Integer> portList);
	
	public void receiveKadPacket(KadPacket packet);
	
	public void receivedSourcesRequestFromPeer(String peerIP, int peerPort, FileHash fileHash);
	
	public void receivedSourcesAnswerFromPeer(String peerIP, int peerPort, FileHash fileHash, List<String> ipList, List<Integer> portList);
	
	public void requestSourcesFromServer(FileHash fileHash, long fileSize);
	
	public void sendMessage(String peerIP, int peerPort, String message);
	
	public void sendCallBackRequest(String peerIP, int peerPort, Int128 clientID, FileHash fileHash, IPAddress buddyIP, short buddyPort);
	
	public void sendEndOfDownload(String peerIP, int peerPort, FileHash fileHash);
	
	public void sendFileChunk(String peerIP, int peerPort, FileHash fileHash, FileChunk fileChunk);
	
	public void sendFileHashSetAnswer(String peerIP, int peerPort, PartHashSet partHashSet);
	
	public void sendFileNotFound(String peerIP, int peerPort, FileHash fileHash);
	
	public void sendFilePartsRequest(String peerIP, int peerPort, FileHash fileHash, FileChunkRequest... requestData);
	
	public void sendFileNameRequest(String peerIP, int peerPort, FileHash fileHash);
		
	public void sendFileNameRequest(String peerIP, int peerPort, FileHash fileHash, GapList fileGapList, long fileSize, int sourceCount);
	
	public void sendFileRequestAnswer(String peerIP, int peerPort, FileHash fileHash, String fileName);
	
	public void sendFileStatusAnswer(String peerIP, int peerPort, PartHashSet partHashSet, long fileSize, GapList gapList);
	
	public void sendFileStatusRequest(String peerIP, int peerPort, FileHash fileHash);
	
	public void sendKadPacket(KadPacket packet, IPAddress address, int port);
	
	public void sendPartHashSetRequest(String peerIP, int peerPort, FileHash fileHash);
	
	public void sendQueueRanking(String peerIP, int peerPort, int queueRank);
	
	public void sendSlotGiven(String peerIP, int peerPort, FileHash fileHash);

	public void sendSlotRelease(String peerIP, int peerPort);
	
	public void sendUploadRequest(String peerIP, int peerPort, FileHash fileHash);
	
	public void sendSourcesRequest(String peerIP, int peerPort, FileHash fileHash);
	
	public void sendSourcesResponse(String peerIP, int peerPort, FileHash fileHash, Collection<Peer> peer_list);
	
	public void sendMultiPacketRequest(String peerIP, int peerPort, FileHash fileHash, Collection<Byte> entries);
	
	public void sendMultiPacketExtRequest(String peerIP, int peerPort, FileHash fileHash, long fileSize, Collection<Byte> entries);
	
	public void serverConnected();
	
	public void serverConnectingFailed(Throwable cause);
	
	public void serverDisconnected();
	
	public void serverListRequest();
	// count uploaded bytes for file
	public long getUploadedFileBytes(String peerIP, int peerPort);
	
	public void resetUploadedFileBytes(String peerIP, int peerPort);
	
	public void receivedPublicKey(String peerIP, int peerPort, byte[] key);
	
	public void receivedSignature(String peerIP, int peerPort, byte[] signature);
	
	public void receivedSecIdentState(String peerIP, int peerPort, byte state, byte[] challenge);
	
	public void sendPublicKeyPacket(String peerIP, int peerPort);
	
	public void sendSignaturePacket(String peerIP, int peerPort, byte[] challenge);
	
	public void sendSecIdentStatePacket(String peerIP, int peerPort, boolean isPublicKeyNeeded, byte[] challenge);
	
	public void sendEMuleHelloPacket(String peerIP, int peerPort);
	
	public void sendEMuleHelloAnswerPacket(String peerIP, int peerPort);
	
	// UDP 
	
	public void sendServerUDPStatusRequest(String serverIP, int serverPort, int clientTime);
	
	public void sendServerUDPDescRequest(String serverIP, int serverPort);
	
	public void sendServerUDPSourcesRequest(String serverIP, int serverPort, Collection<FileHash> fileHashList);
	
	public void sendServerUDPSources2Request(String serverIP, int serverPort, List<FileHash> fileHashList, List<Long> fileSizeList);
	
	public void sendServerUDPSearchRequest(String serverIP, int serverPort, SearchQuery searchQuery);
	
	public void sendServerUDPSearch2Request(String serverIP, int serverPort, SearchQuery searchQuery);
	
	public void sendServerUDPSearch3Request(String serverIP, int serverPort, SearchQuery searchQuery);
	
	public void sendServerUDPReaskFileRequest(String serverIP, int serverPort, FileHash fileHash);
	
	public void sendServerUDPCallBackRequest(String serverIP, int serverPort, ClientID clientID);
	
	public boolean hasPeer(String peerIP, int peerPort);
}
