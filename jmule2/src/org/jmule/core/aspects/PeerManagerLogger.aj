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
package org.jmule.core.aspects;

import java.util.logging.Logger;

import org.jmule.core.peermanager.PeerManager;
import org.jmule.core.peermanager.InternalPeerManager;
import org.jmule.core.utils.Misc;
import org.jmule.core.peermanager.Peer;
/**
 * 
 * @author binary256
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/12/11 14:45:40 $$
 */
public privileged aspect PeerManagerLogger {
	private Logger log = Logger.getLogger("org.jmule.core.peermanager.PeerManager");
	
	after() throwing (Throwable t): execution (* PeerManager.*(..)) {
		log.warning(Misc.getStackTrace(t));
	}
	
	before(Peer peer) : args(peer) && execution (void PeerManager.connect(Peer)) {
		log.info("Connect to peer " + peer.getIP() + ":" + peer.getPort());
	}
	
	before(String ip, int port) : args(ip, port) && execution (Peer InternalPeerManager.newIncomingPeer(String,int)) {
		log.info("Incoming peer " + ip + ":" + port);
	}
	
	before(String ip, int port) : args(ip, port) && execution (void InternalPeerManager.peerDisconnected(String,int)) {
		log.info("Disconnected peer " + ip + ":" + port);
	}
	
	/*before() : execution(* PeerManager.*(..)) {
		String join_point = thisJoinPoint.toString();
		String args = " ";
		for(Object object : thisJoinPoint.getArgs()) {
			args += "(" + object + ") ";
		}
		log.info(join_point + "\n" + args);
	}*/
	
	
}
