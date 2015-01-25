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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentSkipListSet;

import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.JMuleManager;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.jkad.JKadManager;
import org.jmule.core.networkmanager.NetworkManager;
import org.jmule.core.peermanager.InternalPeerManager;
import org.jmule.core.peermanager.PeerManager;
import org.jmule.core.searchmanager.SearchManager;
import org.jmule.core.servermanager.ServerManager;
import org.jmule.core.sharingmanager.SharingManager;
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.core.utils.AddressUtils;


/**
 * Created on Nov 4, 2009
 * @author javajox
 * @version $Revision: 1.7 $
 * Last changed by $Author: javajox $ on $Date: 2010/01/12 13:32:14 $
 */
public class IPFilterImpl extends JMuleAbstractManager implements InternalIPFilter {

	private Set<BannedIP> banned_peers = new ConcurrentSkipListSet<BannedIP>();
	private Set<BannedIP> banned_servers = new ConcurrentSkipListSet<BannedIP>();
	private Set<BannedIPRange> banned_peer_ranges = new ConcurrentSkipListSet<BannedIPRange>();
	
	private Set<TemporaryBannedIP> temporary_banned_peers = new ConcurrentSkipListSet<TemporaryBannedIP>();
	private Set<TemporaryBannedIP> temporary_banned_servers = new ConcurrentSkipListSet<TemporaryBannedIP>();
	
	private List<IPFilterPeerListener> ip_filter_peer_listeners = new LinkedList<IPFilterPeerListener>();
	private List<IPFilterServerListener> ip_filter_server_listeners = new LinkedList<IPFilterServerListener>();
	
	private Timer remove_temp_banned_peers_timer;
	private Timer remove_temp_banned_servers_timer;
	
	private static final long REMOVE_TEMP_BANNED_PEERS_INTERVAL = 60 * 1000;
	private static final long REMOVE_TEMP_BANNED_SERVERS_INTERVAL = 60 * 1000;
	
	private JMuleCore _core = JMuleCoreFactory.getSingleton();
	private InternalPeerManager _internal_peer_manager = (InternalPeerManager)_core.getPeerManager();
	
	IPFilterImpl() {
		
	}

	private void addPeer(InetAddress address, BannedReason bannedReason, 
			int howLong, TimeUnit timeUnit, String who) {
		
		try {
			if( timeUnit == TimeUnit.INFINITY ) 
			
				banned_peers.add(new BannedIP( AddressUtils.addressToInt( address ), 
						bannedReason, who ));
			else
				
				temporary_banned_peers.add(new TemporaryBannedIP( AddressUtils.addressToInt( address ), 
						 bannedReason, toMiliseconds(howLong, timeUnit), who));
	
			    _internal_peer_manager.bannedNode( AddressUtils.addressToInt(address));
			    
			this.notify_peer_banned( AddressUtils.ip2string( address.getAddress() ) );
		}catch( Throwable cause ) {
			
			cause.printStackTrace();
		}
	}
	
	private void addServer(InetAddress address, BannedReason bannedReason, 
			int howLong, TimeUnit timeUnit, String who ) {
		
		try {
			if( timeUnit == TimeUnit.INFINITY )
				
				banned_servers.add(new BannedIP( AddressUtils.addressToInt( address ), 
						bannedReason, who ));
			else
				
				temporary_banned_servers.add(new TemporaryBannedIP( AddressUtils.addressToInt( address ), 
						 bannedReason, toMiliseconds(howLong, timeUnit), who));
			
			this.notify_server_banned( AddressUtils.ip2string( address.getAddress() ) );
		}catch( Throwable cause ) {
			
			cause.printStackTrace();
		}
	}
	
	public void addPeer(InetSocketAddress address, BannedReason bannedReason, 
			int howLong, TimeUnit timeUnit, String who) {

		addPeer(address.getAddress(), bannedReason, howLong, timeUnit, who);
	}
	
	public void addPeer(InetSocketAddress inetSocketAddress, 
			            BannedReason bannedReason) {
		
		addPeer(inetSocketAddress, bannedReason, 0, TimeUnit.INFINITY);
	}
	
	public void addServer(InetSocketAddress inetSocketAddress, 
			              BannedReason bannedReason) {
		
		addServer(inetSocketAddress, bannedReason, 0, TimeUnit.INFINITY);
	}
	
	public void addPeer(InetSocketAddress inetSocketAddress) {
		
		addPeer(inetSocketAddress, BannedReason.DEFAULT, 0, TimeUnit.INFINITY);
	}
	
	public void addServer(InetSocketAddress inetSocketAddress) {
		
		addServer(inetSocketAddress, BannedReason.DEFAULT, 0, TimeUnit.INFINITY);
	}
	
	public boolean isPeerBanned(InetSocketAddress inetSocketAddress) {
	    
		return this.isPeerBanned( AddressUtils.ip2string( inetSocketAddress.getAddress().getAddress() ) );
	}
	
	public boolean isServerBanned(InetSocketAddress inetSocketAddress) {
		
		return this.isServerBanned( AddressUtils.ip2string( inetSocketAddress.getAddress().getAddress() ) );
	}
	
	
	public void addPeer(InetSocketAddress inetSocketAddress,
			BannedReason bannedReason, int howLong, TimeUnit timeUnit) {
		
		this.addPeer(inetSocketAddress, bannedReason, howLong, timeUnit, "");
	}

	public void addPeer(InetSocketAddress inetSocketAddress, int howLong,
			TimeUnit timeUnit) {
		
		addPeer(inetSocketAddress, BannedReason.DEFAULT, howLong, timeUnit);
	}

	
	public void addServer(InetSocketAddress inetSocketAddress,
			BannedReason bannedReason, int howLong, TimeUnit timeUnit) {
		
		this.addServer(inetSocketAddress, bannedReason, howLong, timeUnit, "");
	}

	public void addServer(InetSocketAddress address, BannedReason bannedReason, 
			int howLong, TimeUnit timeUnit, String who) {

		this.addServer(address.getAddress(), 
				bannedReason, howLong, timeUnit, who);
	}
	
	public void addServer(InetSocketAddress inetSocketAddress, int howLong,
			TimeUnit timeUnit) {
		
		addServer(inetSocketAddress, BannedReason.DEFAULT, howLong, timeUnit);
	}	
	
	public void addPeer(String address, BannedReason bannedReason, int howLong,
			TimeUnit timeUnit) {
		
		this.addPeer(address, bannedReason, howLong, timeUnit);
	}

	public void addPeer(String address, BannedReason bannedReason) {
		
		addPeer(address, bannedReason, 0, TimeUnit.INFINITY, "");
	}

	public void addPeer(String address, int howLong, TimeUnit timeUnit) {
		
		addPeer(address, BannedReason.DEFAULT, howLong, timeUnit, "");
	}

	public void addPeer(String address) {
		
		addPeer(address, BannedReason.DEFAULT, 0, TimeUnit.INFINITY, "");
	}

	public void addServer(String address, BannedReason bannedReason,
			int howLong, TimeUnit timeUnit) {
		
        this.addServer(address, bannedReason, howLong, timeUnit, "");
	}

	public void addServer(String address, BannedReason bannedReason) {
	     
		addServer(address, bannedReason, 0, TimeUnit.INFINITY);
	}

	public void addServer(String address, int howLong, TimeUnit timeUnit) {
		
		addServer(address, BannedReason.DEFAULT, howLong, timeUnit);
	}

	public void addServer(String address) {
		
		this.addServer(address, BannedReason.DEFAULT, 0, TimeUnit.INFINITY, "");
	}

	public boolean isPeerBanned(String address) {
        try {
        	int address_as_int = AddressUtils.addressToInt( address );
        	BannedIP bp = new BannedIP( address_as_int );
        	return banned_peers.contains( bp ) ||
        	       temporary_banned_peers.contains( bp );
        }catch( Throwable cause ) {
           cause.printStackTrace();        	
        }
		return false;
	}

	public boolean isServerBanned(String address) {
       try {
    	  int address_as_int =  AddressUtils.addressToInt(address);
    	  BannedIP bs = new BannedIP( address_as_int );
		  return banned_servers.contains( bs ) ||
		         temporary_banned_servers.contains( bs );
       }catch( Throwable cause ) {
    	   cause.printStackTrace();
       }
       return false;
	}

	public void unbanPeer(String address) {
        try {
        	int address_as_int =  AddressUtils.addressToInt(address);
        	BannedIP bp = new BannedIP( address_as_int );
        	if( banned_peers.remove( bp ) || temporary_banned_peers.remove( bp ) )
        		this.notify_peer_unbanned( address );
        }catch( Throwable cause ) {
        	cause.printStackTrace();
        }
	}

	public void unbanServer(String address) {
        try {
        	int address_as_int =  AddressUtils.addressToInt(address);
        	BannedIP bs = new BannedIP( address_as_int );
        	if( banned_servers.remove( bs ) || temporary_banned_servers.remove( bs ) )
        		this.notify_server_unbanned( address );
        }catch( Throwable cause ) {
        	cause.printStackTrace();
        }
	}
	
	private static long toMiliseconds(int howLong, TimeUnit timeUnit) {
		switch( timeUnit ) {
		    case MINUTE  : return ( howLong * 1000 * 60 );
		    case HOUR    : return ( howLong * 1000 * 60 * 60);
		    case DAY     : return ( howLong * 1000 * 60 * 60 * 24);
		}
		return 0;
	}
	
	// ------------------- ban the peer and tell who are you
	public void addPeer(String address, BannedReason bannedReason, 
			int howLong, TimeUnit timeUnit, String who) {
	   try {
		   
          this.addPeer( InetAddress.getByAddress(AddressUtils.textToNumericFormat( address )), 
        		  bannedReason, howLong, timeUnit, who );
	   }catch(Throwable cause) {
		   cause.printStackTrace();
	   }
	}
	
	public void addPeer(String address, 
			BannedReason bannedReason, String who) {
		
		this.addPeer(address, bannedReason, 0, TimeUnit.INFINITY, who);
	}
	
	public void addPeer(String address, int howLong, 
			TimeUnit timeUnit, String who) {
		
		this.addPeer(address, BannedReason.DEFAULT, howLong, timeUnit, who);
	}
	
	public void addPeer(String address, String who) {
		
		this.addPeer(address, BannedReason.DEFAULT, 0, TimeUnit.INFINITY, who);
	}
	
	// ------------------ ban the server and tell who are you
	public void addServer(String address, BannedReason bannedReason, 
			int howLong, TimeUnit timeUnit, String who) {
		try {
			   
		     this.addServer( InetAddress.getByAddress(AddressUtils.textToNumericFormat( address )), 
		        		  bannedReason, howLong, timeUnit, who );
	    }catch(Throwable cause) {
	    	
		    cause.printStackTrace();
		}
	}
	
	public void addServer(String address, 
			BannedReason bannedReason, String who) {
		
		this.addServer(address, bannedReason, 0, TimeUnit.INFINITY, who);
	}
	
	public void addServer(String address, int howLong, 
			TimeUnit timeUnit, String who) {
		
		this.addServer(address, BannedReason.DEFAULT, howLong, timeUnit, who);
	}
	
	public void addServer(String address, String who) {
		
		this.addServer(address, BannedReason.DEFAULT, 0, TimeUnit.INFINITY, who);
	}
	
	// -------- ban the peer and tell who are you (which JMule manager?)
	public void addPeer(String address, BannedReason bannedReason, 
			int howLong, TimeUnit timeUnit, JMuleManager who) {
		
		this.addPeer(address, bannedReason, 
				howLong, timeUnit, getJMuleManagerAsString( who ));
	}
	
	public void addPeer(String address, 
			BannedReason bannedReason, JMuleManager who) {
		
		this.addPeer(address, bannedReason,
				getJMuleManagerAsString( who ));
	}
	
	public void addPeer(String address, int howLong, 
			TimeUnit timeUnit, JMuleManager who) {
		
		this.addPeer(address, howLong, 
				timeUnit, getJMuleManagerAsString( who ));
	}
	
	public void addPeer(String address, JMuleManager who) {
		
		this.addPeer(address, getJMuleManagerAsString( who ));
	}
	
	// -------- ban the server and tell who are you (which JMule manager?)
	
	public void addServer(String address, BannedReason bannedReason, 
			int howLong, TimeUnit timeUnit, JMuleManager who) {
		
		this.addServer(address, bannedReason, 
				howLong, timeUnit, getJMuleManagerAsString( who ));
	}
	
	public void addServer(String address, 
			BannedReason bannedReason, JMuleManager who) {
		
		this.addServer(address, 
				bannedReason, getJMuleManagerAsString( who ));
	}
	
	public void addServer(String address, int howLong, 
			TimeUnit timeUnit, JMuleManager who) {
	
		this.addServer(address, howLong, 
				timeUnit, getJMuleManagerAsString( who ));
	}
	
	public void addServer(String address, JMuleManager who) {
		
		this.addServer(address, getJMuleManagerAsString( who ));
	}
	
	public void clearBannedPeers() {
	    
		banned_peers.clear();
		temporary_banned_peers.clear();
	}
	
	public void clearBannedServers() {
		
		banned_servers.clear();
		temporary_banned_servers.clear();
	}
	
	public void clear() {
		
		this.clearBannedPeers();
		this.clearBannedServers();
	}
	
	public void addIPFilterPeerListener(IPFilterPeerListener peerListener) {
		ip_filter_peer_listeners.add( peerListener );
	}
	
	public void removeIPFilterPeerListener(IPFilterPeerListener peerListener) {
		ip_filter_peer_listeners.remove( peerListener );
	}
	
	public void addIPFilterServerListener(IPFilterServerListener serverListener) {
		ip_filter_server_listeners.add( serverListener );
	}
	
	public void removeIPFilterServerListener(IPFilterServerListener serverListener) {
		ip_filter_server_listeners.remove( serverListener );
	}
	
	private void notify_peer_banned(String address) {
		for(IPFilterPeerListener peer_listener : ip_filter_peer_listeners)
			try {
				peer_listener.peerBanned( address );
			}catch( Throwable cause ) {
				cause.printStackTrace();
			}
	}
	
	private void notify_peer_unbanned(String address) {
		for(IPFilterPeerListener peer_listener : ip_filter_peer_listeners)
			try {
				peer_listener.peerUnbanned( address );
			}catch( Throwable cause ) {
				cause.printStackTrace();
			}
	}
	
	private void notify_server_banned(String address) {
		for(IPFilterServerListener server_listener : ip_filter_server_listeners)
			try {
				server_listener.serverBanned( address );
			}catch( Throwable cause ) {
				cause.printStackTrace();
			}
	}
	
	private void notify_server_unbanned(String address) {
		 for(IPFilterServerListener server_listener : ip_filter_server_listeners)
			 try {
				 server_listener.serverUnbanned( address );
			 }catch( Throwable cause ) {
				 cause.printStackTrace();
			 }
	}
	
	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void shutdown() {
		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return ;
		}
		remove_temp_banned_peers_timer.cancel();
		remove_temp_banned_servers_timer.cancel();
	}
	
	public void start() {
		try {
			super.start();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return ;
		}
		remove_temp_banned_peers_timer = new Timer( "Remove temp banned peers timer", true );
		remove_temp_banned_peers_timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				for(TemporaryBannedIP banned_ip : temporary_banned_peers) 
					if( ( System.currentTimeMillis() - banned_ip.getWhenBanned() ) >= banned_ip.getHowLong() )
						temporary_banned_peers.remove( banned_ip );
			}
		}, (long)1, REMOVE_TEMP_BANNED_PEERS_INTERVAL);
		
		remove_temp_banned_servers_timer = new Timer( "Remove temp banned servers timer", true);
		remove_temp_banned_servers_timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				for(TemporaryBannedIP banned_ip : temporary_banned_servers) 
					if( ( System.currentTimeMillis() - banned_ip.getWhenBanned() ) >= banned_ip.getHowLong() )
						temporary_banned_servers.remove( banned_ip );
			}			
		}, (long)1, REMOVE_TEMP_BANNED_SERVERS_INTERVAL);
	}
	
	protected boolean iAmStoppable() {

		return false;
	}
	
	private static final String DOWNLOAD_MANAGER = "Download manager";
	private static final String JKAD_MANAGER = "JKad manager";
	private static final String NETWORK_MANAGER = "Network manager";
	private static final String PEER_MANAGER = "Peer manager";
	private static final String SEARCH_MANAGER = "Search manager";
	private static final String SERVER_MANAGER = "Server manager";
	private static final String SHARING_MANAGER = "Sharing manager";
	private static final String UPLOAD_MANAGER = "Upload manager";
	private static final String CONFIG_MANAGER = "Config manager";
	
	private static final String getJMuleManagerAsString(JMuleManager manager) {
		if( manager instanceof DownloadManager )
			return DOWNLOAD_MANAGER;
		if( manager instanceof JKadManager )
			return JKAD_MANAGER;
		if( manager instanceof NetworkManager )
			return NETWORK_MANAGER;
		if( manager instanceof PeerManager )
			return PEER_MANAGER;
		if( manager instanceof SearchManager )
			return SEARCH_MANAGER;
		if( manager instanceof ServerManager )
			return SERVER_MANAGER;
		if( manager instanceof SharingManager )
			return SHARING_MANAGER;
		if( manager instanceof UploadManager )
			return UPLOAD_MANAGER;
		if( manager instanceof ConfigurationManager )
			return CONFIG_MANAGER;
		return "";
	}
	
}
