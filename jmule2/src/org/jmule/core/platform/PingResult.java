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
package org.jmule.core.platform;

/**
 * Created on Sep 1, 2009
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2009/10/25 08:36:11 $
 */
public class PingResult {

	private int transmittedPackets;
	private int receivedPackets;
	private int packetLoss;
	private int time;
	
	PingResult() {
		
	}
	
	public int getTransmittedPackets() {
		return transmittedPackets;
	}
	void setTransmittedPackets(int transmittedPackets) {
		this.transmittedPackets = transmittedPackets;
	}
	public int getReceivedPackets() {
		return receivedPackets;
	}
	void setReceivedPackets(int receivedPackets) {
		this.receivedPackets = receivedPackets;
	}
	public int getPacketLoss() {
		return packetLoss;
	}
	void setPacketLoss(int packetLoss) {
		this.packetLoss = packetLoss;
	}
	public int getTime() {
		return time;
	}
	void setTime(int time) {
		this.time = time;
	}
		
}
