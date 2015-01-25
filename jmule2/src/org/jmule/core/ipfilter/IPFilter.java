/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.ipfilter;

import java.net.InetSocketAddress;

import org.jmule.core.JMuleManager;

/**
 * Created on Nov 4, 2009
 * @author javajox
 * @version $Revision: 1.3 $
 * Last changed by $Author: javajox $ on $Date: 2010/01/12 11:00:14 $
 */
public interface IPFilter extends JMuleManager {

	public enum BannedReason {
		DEFAULT,
		BAD_PACKETS,
		SPAM
	}
	
	public enum TimeUnit {
		MINUTE,
		HOUR,
		DAY,
		INFINITY
	}
	
	// -------- ip filter ops for peers with address as string
	public void addPeer(String address, BannedReason bannedReason, 
			int howLong, TimeUnit timeUnit);
	
	public void addPeer(String address, BannedReason bannedReason);
	
	public void addPeer(String address, int howLong, TimeUnit timeUnit);
	
	public void addPeer(String address);
	
	public boolean isPeerBanned(String address);
	
	public void unbanPeer(String address);
	
	// ----------- ip filter ops for servers with address as string
	public void addServer(String address, BannedReason bannedReason, 
			int howLong, TimeUnit timeUnit);
	
	public void addServer(String address, BannedReason bannedReason);
	
	public void addServer(String address, int howLong, TimeUnit timeUnit);
	
	public void addServer(String address);
	
	public boolean isServerBanned(String address);
	
	public void unbanServer(String address);
	
	public void addPeer(InetSocketAddress inetSocketAddress, 
			 BannedReason bannedReason);
	
	public void addServer(InetSocketAddress inetSocketAddress, 
			BannedReason bannedReason);
	
	public void addPeer(InetSocketAddress inetSocketAddress);
	
	public void addServer(InetSocketAddress inetSocketAddress);
	
	public boolean isPeerBanned(InetSocketAddress inetSocketAddress);
	
	public boolean isServerBanned(InetSocketAddress inetSocketAddress);
	
	public void addPeer(InetSocketAddress inetSocketAddress, 
			BannedReason bannedReason, int howLong, TimeUnit timeUnit);
	
	public void addServer(InetSocketAddress inetSocketAddress, 
			BannedReason bannedReason, int howLong, TimeUnit timeUnit);
	
	public void addPeer(InetSocketAddress inetSocketAddress, 
			int howLong, TimeUnit timeUnit);
	
	public void addServer(InetSocketAddress inetSocketAddress, 
			int howLong, TimeUnit timeUnit);
	
	// ------------------- ban the peer and tell who are you
	public void addPeer(String address, BannedReason bannedReason, 
			int howLong, TimeUnit timeUnit, String who);
	
	public void addPeer(String address, 
			BannedReason bannedReason, String who);
	
	public void addPeer(String address, int howLong, 
			TimeUnit timeUnit, String who);
	
	public void addPeer(String address, String who);
	
	// ------------------ ban the server and tell who are you
	public void addServer(String address, BannedReason bannedReason, 
			int howLong, TimeUnit timeUnit, String who);
	
	public void addServer(String address, 
			BannedReason bannedReason, String who);
	
	public void addServer(String address, int howLong, 
			TimeUnit timeUnit, String who);
	
	public void addServer(String address, String who);
	
	public void clearBannedPeers();
	
	public void clearBannedServers();
	
	public void clear();
	
	public void addIPFilterPeerListener(IPFilterPeerListener peerListener);
	
	public void removeIPFilterPeerListener(IPFilterPeerListener peerListener);
	
	public void addIPFilterServerListener(IPFilterServerListener serverListener);
	
	public void removeIPFilterServerListener(IPFilterServerListener serverListener);
}
