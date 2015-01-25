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
package org.jmule.core.edonkey.packet;

import static org.jmule.core.edonkey.ED2KConstants.PROTO_EDONKEY_PEER_UDP;
import static org.jmule.core.edonkey.ED2KConstants.PROTO_EDONKEY_SERVER_UDP;
import static org.jmule.core.jkad.JKadConstants.PROTO_KAD_UDP;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * 
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.10 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/08/22 12:44:58 $$
 */
public class UDPPacket {
	protected ByteBuffer packet_data = null;
	protected InetSocketAddress sender;

	public UDPPacket(ByteBuffer packetContent, InetSocketAddress sender) {
		this.packet_data = packetContent;
		this.sender = sender;
	}
	
	public UDPPacket(int packetLength, byte packetProtocol) {
		if (packetProtocol == PROTO_EDONKEY_SERVER_UDP) {
			packet_data = Misc.getByteBuffer(packetLength + 2);
			this.setProtocol(packetProtocol);
		}
		if (packetProtocol == PROTO_KAD_UDP) {
			packet_data = Misc.getByteBuffer(packetLength);
			this.setProtocol(packetProtocol);
		}
		if (packetProtocol == PROTO_EDONKEY_PEER_UDP) {
			packet_data = Misc.getByteBuffer(packetLength + 4);
			this.setProtocol(packetProtocol);
			packet_data.putInt(1, packetLength);
		}
	}

	public UDPPacket() {
	}

	public InetSocketAddress getAddress() {
		return sender;
	}

	public void setAddress(InetSocketAddress sender) {
		this.sender = sender;
	}

	public byte getProtocol() {
		return this.packet_data.get(0);
	}

	public void setProtocol(byte protocol) {
		this.packet_data.put(0, protocol);
	}

	public void setCommand(byte packetCommand) {
		if ((getProtocol() == PROTO_EDONKEY_SERVER_UDP)
				|| (getProtocol() == PROTO_KAD_UDP)) {
			packet_data.position(1);
			packet_data.put(packetCommand);
		} else {
			packet_data.position(5);
			packet_data.put(packetCommand);
		}
	}

	public byte getCommand() {
		if (getProtocol() == PROTO_EDONKEY_SERVER_UDP) {
			return packet_data.get(1);
		}
		if (getProtocol() == PROTO_KAD_UDP) {
			return packet_data.get(1);
		} else {
			return packet_data.get(5);
		}
	}

	public void insertData(ByteBuffer insertData) {
		packet_data.put(insertData);
	}

	public void insertData(long insertData) {
		packet_data.putLong(insertData);
	}

	public void insertData(int insertData) {
		packet_data.putInt(insertData);
	}

	public void insertData(byte[] insertData) {
		packet_data.put(insertData);
	}

	public void insertData(short insertData) {
		packet_data.putShort(insertData);
	}

	public void insertData(byte insertData) {
		packet_data.put(insertData);
	}

	public void insertData(int startPos, byte[] insertData) {
		packet_data.position(startPos);
		packet_data.put(insertData);
	}

	public byte[] getPacket() {
		return packet_data.array();
	}

	public ByteBuffer getAsByteBuffer() {
		packet_data.position(0);
		return packet_data;
	}

	public int getLength() {
		return packet_data.limit();
	}

	public void clear() {
		if (packet_data == null)
			return;
		packet_data.clear();
		packet_data.compact();
		packet_data.rewind();
		packet_data.limit(0);
		packet_data = null;
	}

	public String toString() {
		String result = "";
		if (sender!=null)
			result += "From : " + sender.getAddress().getHostAddress() + " : "
					+ sender.getPort();
					else 
					result += "From : null : null ";
		result += "\n" + Convert.byteToHexString(packet_data.array(), " 0x"); 
		return  result;
				
	}

}
