/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2010 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.networkmanager;

import java.nio.ByteBuffer;

import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * Created on Mar 4, 2010
 * @author binary256
 * @version $Revision: 1.4 $
 * Last changed by $Author: binary255 $ on $Date: 2010/06/16 18:21:19 $
 */
public class PacketFragment {

	private JMConnection connection;
	private ByteBuffer content;
	private int packetLimit;
	private long lastUpdate;
	private int length = 0;
	
	public String toString() {
		return "packetLimit : " + packetLimit + " " + " lastUpdate : " + lastUpdate + " content:: capacity :: " + content.capacity();
	}
	
	public PacketFragment(JMConnection connection, long size) {
		this.connection = connection;
		content = Misc.getByteBuffer(size);
		
		upadateLength();
		
		lastUpdate = System.currentTimeMillis();
	}
	
	public void upadateLength() {
		content.position(1);
		ByteBuffer len = Misc.getByteBuffer(4);
		for (int i = 0; i < 4; i++)
			len.put(content.get());
		content.position(0);
		length = Convert.byteToInt(len.array());
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	
	public boolean isFullUsed() {
		boolean value = content.position() >= getPacketLimit();
		if (content.capacity()==0)
			value = false;
		return value;
	}
	
	public JMConnection getConnection() {
		return connection;
	}
	
	public ByteBuffer getContent() {
		return content;
	}
	
	public void concat(PacketFragment fragment) {
		long size = getPacketLimit() + fragment.getPacketLimit();
		ByteBuffer result = Misc.getByteBuffer(size);
		
		int l;
		l = getPacketLimit();
		result.put(content.array(), 0, l);
		
		l = fragment.getPacketLimit();
		result.put(fragment.getContent().array(),0, l);
		
		setPacketLimit(result.position());
		content = result;
		content.position(0);
		
		upadateLength();
		
		lastUpdate = System.currentTimeMillis();
		
		fragment.clear();
	}
	
	public byte getHead() {
		content.position(0);
		byte result = content.get();
		content.position(0);
		return result;
	}
	
	public int getLength() {
		return length;
	}
	
	public void moveUnusedBytes(int beginPos) {
		if (beginPos > getPacketLimit()) {
			return;
		}
		int moveFragmentSize = (getPacketLimit()- beginPos);
		if (moveFragmentSize <= 0) {
			return;
		}
		if (moveFragmentSize == 0) {
			return;
		}
		content.position(beginPos);
		ByteBuffer temp = Misc.getByteBuffer(moveFragmentSize );
		content.get(temp.array());
		
		content.clear();
		content.position(0);
		
		temp.position(0);
		
		content.put(temp);
		setPacketLimit(content.position());
		content.position(0);
		
		upadateLength();
		
		lastUpdate = System.currentTimeMillis();
	}
	
	public void clear() {
		content.clear();
		content.compact();
		content.rewind();
		content.limit(0);
		length = 0;
		content = null;
	}

	public int getPacketLimit() {
		return packetLimit;
	}

	public void setPacketLimit(int packetLimit) {
		this.packetLimit = packetLimit;
	}
}
