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

import static org.jmule.core.edonkey.ED2KConstants.TAG_NAME_CLIENTVER;
import static org.jmule.core.edonkey.ED2KConstants.*;
import static org.jmule.core.edonkey.ED2KConstants.TAG_NAME_NICKNAME;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import org.jmule.core.JMException;
import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.ED2KConstants;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.edonkey.ED2KConstants.PeerFeatures;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.utils.Utils;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.utils.AddressUtils;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.27 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/08/31 10:33:10 $$
 */
public class Peer {
	public enum PeerSource {INCOMING, SERVER, KAD, PEX, ED2KLINK, EXTERNAL /* Added directly */, GLOBAL}
	public enum PeerStatus {DISCONNECTED, CONNECTED, CONNECTING}
		
	private String ip;
	private int ip_as_int = 0;
	private int port;
	private int listenPort = 0;
	
	private String server_ip = null;
	private int server_port = 0;
	
	private ClientID clientID = null;
	private UserHash userHash = null;
	TagList tag_list = new TagList();
	
	Map<PeerFeatures,Integer> peer_features = new Hashtable<PeerFeatures, Integer>();
	private PeerSource peer_source;
	
	private PeerStatus peer_status = PeerStatus.DISCONNECTED;
	private long lastSeen = 0;
	
	Peer(String ip, int port,PeerSource peerSource) {
		this.ip = ip;
		this.clientID = new ClientID(ip);
		this.port = port;
		this.peer_source = peerSource;
	}
	
	// method is used to replace content of lowid peers with
	// add code to copy each peer field
	void copyFields(Peer peer) {
		this.ip = peer.ip;
		this.port = peer.port;
		
		this.listenPort = peer.listenPort;
		
		this.server_ip = peer.server_ip;
		this.server_port = peer.server_port;
		
		this.clientID = peer.clientID;
		this.userHash = peer.userHash;
		this.tag_list = peer.tag_list;
		
		this.peer_features = peer.peer_features;
		this.peer_status = peer.peer_status;
		
		this.lastSeen = peer.lastSeen;
	}
	
	void setStatus(PeerStatus newStatus) {
		this.peer_status = newStatus;
		lastSeen = System.currentTimeMillis();
	}
	
	void setServer(String serverIP, int serverPort) {
		this.server_ip = serverIP;
		this.server_port = serverPort;
	}
	
	void setClientID(ClientID clientID) {
		this.clientID = clientID;
	}
	
	void setUserHash(UserHash userHash) {
		this.userHash = userHash;
	}
	
	void setTagList(TagList tagList) {
		this.tag_list = tagList;
		if (tag_list.hasTag(TAG_NAME_MISC_OPTIONS1)) {
			Tag tag = tag_list.getTag(TAG_NAME_MISC_OPTIONS1);
				long value;
				try {
					value = Misc.extractNumberTag(tag);
					peer_features = Utils.scanTCPPeerFeatures1(Convert.longToInt(value));
				} catch (JMException e) {
					e.printStackTrace();
				}
		}
		
		if (tag_list.hasTag(TAG_NAME_MISC_OPTIONS2)) {
			Tag tag = tag_list.getTag(TAG_NAME_MISC_OPTIONS2);
				long value;
				try {
					value = Misc.extractNumberTag(tag);
					peer_features.putAll(Utils.scanTCPPeerFeatures2(Convert.longToInt(value)));
				} catch (JMException e) {
					e.printStackTrace();
				}
		}
		
	}
	
	void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}
	
	public long getLastSeen() {
		return lastSeen;
	}
	
	public int getListenPort() {
		return listenPort;
	}
	
	public boolean isConnected() {
		return getStatus() == PeerStatus.CONNECTED;
	}
	
	public boolean isDisconnected() {
		return getStatus() == PeerStatus.DISCONNECTED;
	}
	
	public boolean isConnecting() {
		return getStatus() == PeerStatus.CONNECTING;
	}
	
	public PeerStatus getStatus() {
		return peer_status;
	}
	
	public Map<PeerFeatures,Integer> getFeatures() {
		return peer_features;
	}
	
	public Integer getFeature(PeerFeatures feature) {
		Integer result = peer_features.get(feature);
		if (result == null)
			return 0;
		return result;
	}
		
	public PeerSource getSource() {
		return peer_source;
	}
	
	public String getIP() {
		return ip;
	}
	
	public int getIPAsInt() {		
		if( ip_as_int == 0 ) {
		   try {	
			 ip_as_int = AddressUtils.addressToInt( ip );
		   }catch( Throwable cause ) {
			   cause.printStackTrace();
		   }
		}
		return ip_as_int;
	}
	
	public int getPort() {
		if (listenPort != 0) 
			return listenPort;
		return port;
	}
	
	public String getServerIP() {
		return server_ip;
	}
	
	public int getServerPort() {
		return server_port;
	}
	
	public String getNickName() {
		Tag tag = tag_list.getTag(TAG_NAME_NICKNAME);
		if (tag != null)
			return (String) tag.getValue();
		return getIP();

	}
		
	public ClientID getClientID() {
		return clientID;
	}
	
	public boolean isHighID() {
		return this.clientID.isHighID();
	}
	
	public String toString() {
		String result = getIP() + ":" + getPort();
		if (clientID != null)
			result += " " + clientID.getAsString();
		result += " Open port : " + getListenPort();
		result += " Download Speed : " + getDownloadSpeed();
		result += " Upload Speed : " + getUploadSpeed();
		result += " Status : " + getStatus();
		result += " isConnected : " + isConnected();
		result += " userHash : " + userHash;
		
		result += " lastSeen : " + lastSeen;
		result += " source : " + getSource();
		
		result +=" Features : ";
		for(PeerFeatures feature : peer_features.keySet())
			result += " " + feature + " (" + peer_features.get(feature) + ")";
		return result;
	}
	
	public int hashCode() {
		if (userHash == null)
			return getClientID().hashCode();
		return userHash.hashCode();
	}
	
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (!(object instanceof Peer))
			return false;
		Peer p = (Peer) object;
		if ((p.getUserHash() != null) && (userHash != null))
			return p.getUserHash().equals(getUserHash());
		ClientID id = p.getClientID();
		ClientID id2 = getClientID();
		if ((id == null) || (id2 == null))
			return (getIP() + ":" + getPort()).equals(p.getIP() + ":"+ p.getPort());
		byte[] b1 = id.getClientID();
		byte[] b2 = id2.getClientID();

		return Arrays.equals(b1, b2);
	}
	
	public int getClientSoftware() {
		int clientInfo;
		try {
			if (!tag_list.hasTag(TAG_NAME_CLIENTVER))
				return ED2KConstants.SO_COMPAT_UNK;
			Tag tag = tag_list.getTag(TAG_NAME_CLIENTVER);
			
			long value = Misc.extractNumberTag(tag);
			clientInfo = Convert.longToInt(value);
		} catch (Throwable e) {
			e.printStackTrace();
			return ED2KConstants.SO_COMPAT_UNK;
		}
		return (clientInfo >> 24) & 0x000000ff;
	}

	/**
	 * Return client version
	 * @return array with software version 1.2.3.4 => [1,2,3,4]
	 */
	public int[] getVersion() {
		
		int clientInfo;
		try {
			long value = Misc.extractNumberTag(tag_list.getTag(TAG_NAME_CLIENTVER));
			clientInfo = Convert.longToInt(value);
		}catch(Throwable e) {
			return new int[] {0,0,0,0};
		}
		int[] result = new int[4];
		
		result[0] = (clientInfo >> 17) & 0x7F;
		result[1] = (clientInfo >> 10) & 0x7F;
		result[2] = (clientInfo >> 7) & 0x07;
		result[3] = clientInfo & 0x7F;
		return result;
	}
	
	public UserHash getUserHash() {
		return this.userHash;
	}

	public TagList getTagList() {
		return this.tag_list;
	}
	
	public float getDownloadSpeed() {
		if (!isConnected()) return 0;
		InternalNetworkManager network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		if (!network_manager.hasPeer(getIP(), getPort())) {
			return 0;
		}
		return network_manager.getPeerDownloadSpeed(getIP(), getPort());
	}

	public float getUploadSpeed() {
		if (!isConnected()) return 0;
		InternalNetworkManager network_manager = (InternalNetworkManager) NetworkManagerSingleton
				.getInstance();
		return network_manager.getPeerUploadSpeed(getIP(), getPort());
	}

	public float getDownloadServiceSpeed() {
		if (!isConnected()) return 0;
		InternalNetworkManager network_manager = (InternalNetworkManager) NetworkManagerSingleton
				.getInstance();
		return network_manager.getPeerDownloadServiceSpeed(getIP(), getPort());
	}

	public float getUploadServiceSpeed() {
		InternalNetworkManager network_manager = (InternalNetworkManager) NetworkManagerSingleton
				.getInstance();
		return network_manager.getPeerUploadServiceSpeed(getIP(), getPort());
	}
}