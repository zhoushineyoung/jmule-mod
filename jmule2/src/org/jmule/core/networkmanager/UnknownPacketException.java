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

import javax.management.JMException;

import org.jmule.core.utils.Convert;

/**
 * Created on Aug 21, 2009
 * @author binary256
 * @version $Revision: 1.3 $
 * Last changed by $Author: binary255 $ on $Date: 2010/08/31 10:15:45 $
 */
public class UnknownPacketException extends JMException {
	
	public UnknownPacketException(String ip, int port, byte header,byte opCode, byte[] packetContent) {
		super("Sender : " + ip + " : " + port + "\nUnknown packet exception " + "\nHeader : " + Convert.byteToHex(header) + "\nOpCode : " + Convert.byteToHex(opCode) +"\n"  );
	}
	
	public UnknownPacketException(String cause) {
		super(cause);
	}

}
