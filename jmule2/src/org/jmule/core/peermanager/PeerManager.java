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
package org.jmule.core.peermanager;


import java.util.List;

import org.jmule.core.JMuleManager;
import org.jmule.core.peermanager.Peer.PeerSource;

/**
 * 
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/01/28 13:14:23 $$
 */
public interface PeerManager extends JMuleManager {

	/**
	 * Adds a new peer to peer manager
	 * @param peer the given peer
	 */
	public Peer newPeer(String ip, int port, PeerSource source) throws PeerManagerException;
	
	public void removePeer(Peer peer) throws PeerManagerException;
	
	public Peer getPeer(String ip, int port) throws PeerManagerException;
	
	public boolean hasPeer(String ip, int port);
	
	public void connect(Peer peer) throws PeerManagerException;
	
	public void disconnect(Peer peer) throws PeerManagerException;

	public List<Peer> getPeers();
	
	public void sendMessage(Peer peer, String message) throws PeerManagerException;
	
	public float getCredit(Peer peer);
	
	public void addPeerManagerListener(PeerManagerListener listener);

	public void removePeerManagerListener(PeerManagerListener listener);

}
