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
package org.jmule.core.peermanager;

import java.nio.ByteBuffer;
import java.util.List;

import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.ipfilter.DisconnectNode;
import org.jmule.core.peermanager.Peer.PeerSource;

/**
 * Created on Aug 16, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.7 $
 * Last changed by $Author: binary255 $ on $Date: 2010/07/17 14:39:07 $
 */
public interface InternalPeerManager extends PeerManager, DisconnectNode {

	public Peer newIncomingPeer(String ip, int port) throws PeerManagerException;
	
	public void peerDisconnected(String ip, int port);
	
	public void peerConnectingFailed(String ip, int port, Throwable cause);
	
	public void helloFromPeer(String peerIP, 
							  int peerPort, 
							  UserHash userHash, 
							  ClientID clientID,  
							  int peerPacketPort, 
							  TagList tagList, 
							  String serverIP, 
							  int serverPort);
	
	public void helloAnswerFromPeer(String peerIP, 
			  int peerPort, 
			  UserHash userHash, 
			  ClientID clientID,  
			  int peerPacketPort, 
			  TagList tagList, 
			  String serverIP, 
			  int serverPort);
		
	public void callBackRequestFailed();
	
	public void receivedCallBackRequest(String ip, int port, PeerSource source);
	
	public void receivedEMuleHelloFromPeer(String ip, int port,byte clientVersion, byte protocolVersion,
			TagList tagList);
	
	public void receivedEMuleHelloAnswerFromPeer(String ip, int port,byte clientVersion, byte protocolVersion,
			TagList tagList);
		
	public List<Peer> createPeerList(List<String> peerIPList, List<Integer> peerPort, boolean addKnownPeersInList, String serverIP, int serverPort, PeerSource peerSource);
	
	public void receivedMessage(String ip, int port, String message);
	
	public void receivedCaptchaImage(String ip, int port, ByteBuffer image);
	
	public void receivedCaptchaStatusAnswer(String ip, int port, byte answer);
	
	public byte[] getPublicKey(Peer peer);
	
	public void receivedPublicKey(String peerIP, int peerPort, byte[] key);
	
	public void receivedSignature(String peerIP, int peerPort, byte[] signature);
	
	public void receivedSecIdentState(String peerIP, int peerPort, byte state, byte[] challenge);
	
}
