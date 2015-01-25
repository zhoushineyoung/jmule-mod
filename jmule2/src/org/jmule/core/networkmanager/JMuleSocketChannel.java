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
package org.jmule.core.networkmanager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.jmule.core.speedmanager.BandwidthController;
import org.jmule.core.speedmanager.SpeedManagerSingleton;
import org.jmule.core.utils.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.13 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/06/30 18:04:26 $$
 */
class JMuleSocketChannel {
	private SocketChannel channel;
	private BandwidthController uploadController, downloadController;

	JMConnectionTransferStats file_transfer_trafic = new JMConnectionTransferStats();
	JMConnectionTransferStats service_trafic	   = new JMConnectionTransferStats();

	long transferred_bytes = 0;
	
	JMuleSocketChannel(SocketChannel channel) throws IOException {
		this.channel = channel;
		this.channel.socket().setKeepAlive(true);
		init();
	}
	
	long getServiceUploadBytes() {
		return service_trafic.getTotalSent();
	}
	
	long getServiceDownloadBytes() {
		return service_trafic.getTotalReceived();
	}
	
	long getUploadBytes() {
		return file_transfer_trafic.getTotalSent();
	}
	
	long getDownloadedBytes() {
		return file_transfer_trafic.getTotalReceived();
	}
	
	float getDownloadSpeed() {
		return file_transfer_trafic.getDownloadSpeed();
	}
	
	float getUploadSpeed() {
		return file_transfer_trafic.getUploadSpeed();
	}
	
	float getServiceDownloadSpeed() {
		return service_trafic.getDownloadSpeed();
	}
	
	float getServiceUploadSpeed() {
		return service_trafic.getUploadSpeed();
	}

	void connect(InetSocketAddress address) throws IOException {
		channel.connect(address);
	}

	
	SocketChannel getChannel() {
		return channel;
	}

	private void init() {
		uploadController = SpeedManagerSingleton.getInstance().getUploadController();
		downloadController = SpeedManagerSingleton.getInstance().getDownloadController();
	}

	int read(ByteBuffer packetBuffer) throws IOException,JMEndOfStreamException {
		int readedBytes = 0;
		if (downloadController.getThrottlingRate() == 0) {
			readedBytes = channel.read(packetBuffer);
			if (readedBytes == -1)
				throw new JMEndOfStreamException();
			transferred_bytes += readedBytes;
			return readedBytes;
		}
		long cacheLimit = Math.min(downloadController.getThrottlingRate(),packetBuffer.capacity());
		ByteBuffer readCache = Misc.getByteBuffer(cacheLimit);
		
		int mustRead = downloadController.getAvailableByteCount(readCache.remaining(), true);
		readCache.limit(mustRead);
		
		readedBytes = channel.read(readCache);
		if (readedBytes == -1)
			throw new JMEndOfStreamException();
		downloadController.markBytesUsed(readedBytes);
		packetBuffer.position(0);
		packetBuffer.put(readCache.array());
		
		transferred_bytes += readedBytes;
		
		return readedBytes;	
	}

	int write(ByteBuffer packet) throws IOException, JMEndOfStreamException {
		int transferedBytes = 0;
		if (uploadController.getThrottlingRate() == 0) {
			transferedBytes = channel.write(packet);
			if (transferedBytes == -1)
				throw new JMEndOfStreamException();
			
			return transferedBytes;
		}
		
		long cacheLimit = Math.min(uploadController.getThrottlingRate(),packet.remaining());
		int mustWrite = uploadController.getAvailableByteCount((int)cacheLimit, true);
			
		packet.limit(packet.position() + mustWrite);
		transferedBytes = channel.write(packet);
		
		packet.limit(packet.capacity());
		
		if (transferedBytes == -1)
			throw new JMEndOfStreamException();
		
		uploadController.markBytesUsed(transferedBytes);
		
		return transferedBytes;
	}

	boolean isOpen() {
		if (channel == null)
			return false;
		return channel.isOpen();
	}

	Socket getSocket() {
		if (channel == null)
			return null;
		return channel.socket();
	}

	boolean isConnected() {
		if (channel == null)
			return false;
		return channel.isConnected();
	}

	void disconnect() throws IOException {
		channel.socket().close();
		channel.close();
	}

}
