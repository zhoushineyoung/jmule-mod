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
package org.jmule.core.jkad.indexer;

import org.jmule.core.edonkey.packet.tag.IntTag;
import org.jmule.core.edonkey.packet.tag.ShortTag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.jkad.ClientID;
import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.utils.Convert;


/**
 * Created on Jan 5, 2009
 * @author binary256
 * @version $Revision: 1.6 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/18 08:08:29 $
 */
public class Source {
	private ClientID clientID;
	
	//private IPAddress address;
	//private int udpPort;
	//private int tcpPort;
	private byte kadVersion;
	private TagList tagList;
	private long creationTime;
	
	public Source(ClientID clientID, TagList tagList) {
		this(clientID, tagList, System.currentTimeMillis());
	}
	
	public Source(ClientID clientID, TagList tagList, long creationTime) {
		this.clientID = clientID;
		this.tagList = tagList;
		this.creationTime = creationTime;
	}
	
	public Source(ClientID clientID, IPAddress address, int udpPort, int tcpPort) {
		creationTime = System.currentTimeMillis();
		this.clientID = clientID;
		tagList = new TagList();
		tagList.addTag(new IntTag(JKadConstants.TAG_SOURCEIP,Convert.byteToInt(address.getAddress())));
		tagList.addTag(new ShortTag(JKadConstants.TAG_SOURCEPORT,Convert.intToShort(tcpPort)));
		tagList.addTag(new ShortTag(JKadConstants.TAG_SOURCEUPORT,Convert.intToShort(udpPort)));
	}
	
	//public Source(ClientID clientID) {
//		this.clientID = clientID;
//	}
	

	public ClientID getClientID() {
		return clientID;
	}

	public void setClientID(ClientID clientID) {
		this.clientID = clientID;
	}

	public IPAddress getAddress() {
		Integer value = (Integer)tagList.getTag(JKadConstants.TAG_SOURCEIP).getValue();
		return new IPAddress(Convert.intToByteArray(value));
	}

	public void setAddress(IPAddress address) {
		tagList.removeTag(JKadConstants.TAG_SOURCEIP);
		tagList.addTag(new IntTag(JKadConstants.TAG_SOURCEIP,Convert.byteToInt(address.getAddress())));
	}

	public int getUDPPort() {
		Object object = tagList.getTag(JKadConstants.TAG_SOURCEUPORT).getValue();
		return (Integer)object;
	}

	public void setUDPPort(int udpPort) {
		tagList.removeTag(JKadConstants.TAG_SOURCEUPORT);
		tagList.addTag(new ShortTag(JKadConstants.TAG_SOURCEUPORT,Convert.intToShort(udpPort)));
	}

	public int getTCPPort() {
		Object object = tagList.getTag(JKadConstants.TAG_SOURCEPORT).getValue();
		return (Integer)object;
	}

	public void setTCPPort(int tcpPort) {
		tagList.removeTag(JKadConstants.TAG_SOURCEPORT);
		tagList.addTag(new ShortTag(JKadConstants.TAG_SOURCEPORT,Convert.intToShort(tcpPort)));
	}

	public byte getKadVersion() {
		return kadVersion;
	}

	public void setKadVersion(byte kadVersion) {
		this.kadVersion = kadVersion;
	}

	public TagList getTagList() {
		return tagList;
	}

	public void setTagList(TagList tagList) {
		this.tagList = tagList;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
	
	public boolean equals(Object object) {
		if (object == null) return false;
		if (!(object instanceof Source)) return false;
		Source source = (Source) object;
		if (!source.getClientID().equals(getClientID())) return false;
		return true;
	}
	
	public int hashCode() {
		return getClientID().hashCode();
	}
	
	public String toString() {
		String result = "";
		result += "Address  : " + getAddress() + "\n";
		result += "UDP Port : " + getUDPPort() + "\nTCP Port : " + getTCPPort() + "\n";
		result += tagList;
		return result;
	}
	
}
