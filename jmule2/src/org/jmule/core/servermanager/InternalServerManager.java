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
package org.jmule.core.servermanager;

import java.util.List;
import java.util.Set;

import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.ED2KConstants.ServerFeatures;
import org.jmule.core.edonkey.packet.tag.TagList;

/**
 * Created on Aug 20, 2009
 * @author binary256
 * @version $Revision: 1.4 $
 * Last changed by $Author: binary255 $ on $Date: 2010/07/31 12:49:52 $
 */
public interface InternalServerManager extends ServerManager {

	public void serverConnectingFailed(String ip, int port, Throwable cause);
	public void serverDisconnected(String ip, int port);
	
	public void receivedIDChange(ClientID clientID, Set<ServerFeatures> serverFeatures);
	public void receivedMessage(String message);
	public void receivedServerList(List<String> ipList, List<Integer> portList);
	public void receivedServerStatus(int userCount, int fileCount);
	public void receivedOldServerStatus(String ip, int port, int challenge, long userCount, long fileCount);
	public void receivedServerStatus(String ip, int port, int challenge,
			long userCount, long fileCount, long softLimit, long hardLimit,
			Set<ServerFeatures> serverFeatures);
	
	public void receivedServerDescription(String ip, int port, String name,
			String description);
	public void receivedNewServerDescription(String ip, int port,int challenge, TagList tagList);
}
