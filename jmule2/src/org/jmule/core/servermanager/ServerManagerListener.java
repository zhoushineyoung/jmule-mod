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


/**
 * Created on 2008-Jun-03
 * @author javajox
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/19 14:21:53 $$
 */
public interface ServerManagerListener {
     	
	 /**
	  * Tells when the system connects to the server
	  * @param server the given server
	  */
	 public void connected(Server server);
	 
	 /**
	  * Tells when the system has been disconnected from the server
	  * @param server the given server
	  */
	 public void disconnected(Server server);
	 
	 /**
	  * Tells when the system is waiting for a server connection
	  * @param server the given server
	  */
	 public void isConnecting(Server server);
	 
	 /**
	  * Tells when server send a message to client
	  * @param server the given server
	  * @param message message 
	  */
	 public void serverMessage(Server server,String message);
	 
	 public void serverConnectingFailed(Server server, Throwable cause);
	
	 /** 
	  * A new server is added to the list
	  * @param server the server that is added to the list
	  */
	 public void serverAdded(Server server);
	 
	 /**
	  * A server is removed from the list
	  * @param server the server that is removed from the list
	  */
	 public void serverRemoved(Server server);
	 
	 /**
	  * The server list is cleared
	  */
	 public void serverListCleared();
	 
	 /**
	  * Auto connect process started.
	  */
	 public void autoConnectStarted();
	 
	 public void autoConnectFailed();
	 
}
