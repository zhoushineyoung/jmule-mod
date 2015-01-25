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

/**
 * Created on Aug 30, 2009
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary255 $ on $Date: 2009/12/25 20:13:28 $
 */
public interface PeerManagerListener {

	public void newPeer(Peer peer);
	
	public void peerRemoved(Peer peer);
	
	public void peerConnecting(Peer peer);
	
	public void peerConnected(Peer peer);
	
	public void peerDisconnected(Peer peer);
	
	public void peerConnectingFailed(Peer peer, Throwable cause);
	
	public void peerMessage(Peer peer, String message);
	
	public void peerCaptchaImage(Peer peer, ByteBuffer image);
	
	public void peerCaptchaStatusAnswer(Peer peer, byte answer);// 0 - response accepted
}
