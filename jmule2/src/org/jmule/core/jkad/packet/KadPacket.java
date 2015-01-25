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
package org.jmule.core.jkad.packet;

import static org.jmule.core.jkad.JKadConstants.PROTO_KAD_COMPRESSED_UDP;
import static org.jmule.core.jkad.JKadConstants.PROTO_KAD_UDP;
import static org.jmule.core.utils.Misc.getByteBuffer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

import org.jmule.core.edonkey.packet.UDPPacket;
import org.jmule.core.utils.JMuleZLib;

/**
 * Created on Dec 29, 2008
 * @author binary256
 * @version $Revision: 1.4 $
 * Last changed by $Author: binary255 $ on $Date: 2010/09/04 16:14:24 $
 */
public class KadPacket extends UDPPacket {
		
	public KadPacket(byte packetOPCode, int length) {
		super(length + 1 + 1, PROTO_KAD_UDP);
		setCommand(packetOPCode);
	}
	
	public KadPacket(byte packetOPCode) {
		super(1+1, PROTO_KAD_UDP);
		setCommand(packetOPCode);
	}
	
	public KadPacket(ByteBuffer packetContent, InetSocketAddress sender) {
		packet_data = packetContent;
		this.sender = sender;
	}
	
	
	public void compress() {
		if (isCompressed()) return;
	}
	
	public void decompress() {
		if (!isCompressed()) return;
		ByteBuffer compressedData = getByteBuffer(packet_data.limit() - 2);
		packet_data.position(2);
		packet_data.get(compressedData.array());
		packet_data.position(0);
		try {
			ByteBuffer decompressedData = JMuleZLib.decompressData(compressedData);
			decompressedData.position(0);
			
			byte packetOPCode = packet_data.get(1);
			packet_data = getByteBuffer(decompressedData.capacity()+2);

			packet_data.put(PROTO_KAD_UDP);
			packet_data.put(packetOPCode);
			packet_data.put(decompressedData);
			
		} catch (DataFormatException e) {
			e.printStackTrace();
		} 
	}
	
	public boolean isCompressed() {
		try {
			return packet_data.get(0) == PROTO_KAD_COMPRESSED_UDP;
		}catch(Throwable t) { t.printStackTrace(); return false; }
	}
	
	protected void finalize() {
		if (packet_data != null) {
			packet_data.clear();
			packet_data.position(0);
		}
	}

	
	
	
	
}
