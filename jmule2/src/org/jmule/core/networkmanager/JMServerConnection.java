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

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created on Aug 20, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.10 $
 * Last changed by $Author: binary255 $ on $Date: 2010/06/30 18:04:26 $
 */
public class JMServerConnection extends JMConnection {

	private JMuleSocketChannel jm_socket_channel;
	private InetSocketAddress remote_inet_socket_address;
	private ConnectionStatus connection_status = ConnectionStatus.DISCONNECTED;	
	
	JMServerConnection(String ipAddress, int port) {
		remote_inet_socket_address = new InetSocketAddress(ipAddress, port);
	}
	
	JMServerConnection(InetSocketAddress remoteInetSocketAddress) {
		remote_inet_socket_address = remoteInetSocketAddress;
	}
	
	public InetSocketAddress getRemoteInetSocketAddress() {
		return remote_inet_socket_address;
	}
	
	String getIPAddress() {
		return remote_inet_socket_address.getAddress().getHostAddress();
	}
	
	int getPort() {
		return remote_inet_socket_address.getPort();
	}
	
	ConnectionStatus getStatus() {
		return connection_status;
	}
	
	void setStatus(ConnectionStatus newStatus) {
		this.connection_status = newStatus;
	}
	
	JMuleSocketChannel getJMChannel() {
		return jm_socket_channel;
	}
	
	void setJMConnection(JMuleSocketChannel newChannel) {
		this.jm_socket_channel = newChannel;
	}
	
	void disconnect() throws NetworkManagerException {
		if ((connection_status == ConnectionStatus.CONNECTED) || (connection_status == ConnectionStatus.CONNECTING)) {
			try {
				jm_socket_channel.disconnect();
				connection_status = ConnectionStatus.DISCONNECTED;
			} catch (IOException cause) {
				throw new NetworkManagerException(cause);
			}
		}
	}
	
}
