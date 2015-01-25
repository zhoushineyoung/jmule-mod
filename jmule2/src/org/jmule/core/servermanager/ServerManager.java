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
package org.jmule.core.servermanager;

import java.util.List;

import org.jmule.core.JMuleManager;
import org.jmule.core.edonkey.ED2KServerLink;

/**
 * Created on 2008-Jul-06
 * @author javajox
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/06/17 14:45:31 $$
 */
public interface ServerManager extends JMuleManager {

	public enum Status { DISCONNECTED,CONNECTING,CONNECTED };
	
	/**
	 * Import servers from specified file
	 * @param fileName file with servers
	 * @throws ServerManagerException
	 */
	public void importList(String fileName) throws ServerManagerException;
	
	/**
	 * Adds a new server to the server list
	 * @param server the server that is added to the server list
	 */
	public Server newServer(String serverIP, int serverPort) throws ServerManagerException;

	/**
	 * Add a new server specified by ED2K link to the server list
	 * @param serverLink
	 */
	public Server newServer(ED2KServerLink serverLink) throws ServerManagerException;
	
	/**
	 * Removes the given server form the server list
	 * @param server the given server
	 */
	public void removeServer(Server... server) throws ServerManagerException;
	
	public void removeServer(List<Server> server) throws ServerManagerException;;
	
	/**
	 * Returns the current connected server or null if there is no connected server
	 * @return the connected server
	 */
	public Server getConnectedServer();
	
	/**
	 * Tried to connect to the given server
	 * @param server the given server
	 * @throws ServerManagerException if the server is not present in the list or the server can't be reached
	 */
	public void connect(Server server) throws ServerManagerException;
	
	/**
	 * @throws ServerManagerException
	 */
	public void connect() throws ServerManagerException;
	
	public void disconnect() throws ServerManagerException;
	
	public boolean isConnected();
	
	/**
	 * @return the number of servers from the server list
	 */
	public int getServersCount();
	
	/**
	 * @return the current server list
	 */
	public List<Server> getServers();
	
	public Server getServer(String address, int port);
	
	/**
	 * Clears the current server list
	 */
	public void clearServerList();
	
	/**
	 * Loads the server list from the given server met file (usually server.met)
	 * @param serverMet the given server met file
	 * @throws ServerManagerException
	 */
	public void loadServerList() throws ServerManagerException;
	
	/**
	 * Stores the server list to the given met file (usually server.met)
	 * @param serverMet the given met file
	 * @throws ServerManagerException
	 */
	public void storeServerList() throws ServerManagerException;
		
	/**
	 * Check if ServerManager has server specified by address and port
	 * @return true - server exists in list, false - server is not present
	 */
	public boolean hasServer(String address, int port);
		
	public Status getStatus();
	
	/**
	 * Adds a new server list listener
	 * @param serverListListener the given server list listener
	 */
	public void addServerListListener(ServerManagerListener serverListListener);
	
	/**
	 * Removes the given server list listener
	 * @param serverListListener the given server list listener
	 */
	public void removeServerListListener(ServerManagerListener serverListListener);	
}
