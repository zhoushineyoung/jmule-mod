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
package org.jmule.core.networkmanager;


import org.jmule.core.JMException;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * Created on Oct 16, 2009
 * @author binary256
 * @version $Revision: 1.7 $
 * Last changed by $Author: binary255 $ on $Date: 2010/08/31 10:15:45 $
 */
public class MalformattedPacketException extends JMException {

	public MalformattedPacketException(String ip, int port, byte proto, byte opcode, byte[] packetContent, Throwable cause) {
		super("Sender : " + ip + " : " + port + "\nMalformatted packet : " + Convert.byteToHex(proto) + "\n" + "Opcode : "
				+ Convert.byteToHex(opcode) + "\n" + Misc.getStackTrace(cause) );
	}
	
	/*public MalformattedPacketException(byte[] packetContent, Throwable cause) {
		//Convert.byteToHexString(packetContent) + "\n" +
		super("Malformatted packet : \n" +  Misc.getStackTrace(cause));
	}
	
	public MalformattedPacketException(ByteBuffer packet, Throwable cause) {
		//Convert.byteToHexString(packet.array(), 0, packet.limit()) + "\n" +
		super("Malformatted packet : \n" +  Misc.getStackTrace(cause));
	}*/
	
	public MalformattedPacketException(String cause) {
		super(cause);
	}
	
}
