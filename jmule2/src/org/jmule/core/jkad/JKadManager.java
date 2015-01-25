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
package org.jmule.core.jkad;

import org.jmule.core.JMuleStoppableManager;
import org.jmule.core.jkad.JKadManagerImpl.JKadStatus;
import org.jmule.core.jkad.indexer.Indexer;
import org.jmule.core.jkad.lookup.Lookup;
import org.jmule.core.jkad.publisher.Publisher;
import org.jmule.core.jkad.routingtable.RoutingTable;
import org.jmule.core.jkad.search.Search;

/**
 * Created on Aug 29, 2009
 * @author binary256
 * @version $Revision: 1.4 $
 * Last changed by $Author: binary255 $ on $Date: 2010/10/23 05:38:10 $
 */
public interface JKadManager extends JMuleStoppableManager  {

	public void connect();
	public void connect(ContactAddress address);
	public void disconnect();
	
	public boolean isFirewalled();
	public JKadStatus getStatus();
	
	public boolean isConnected();
	public boolean isDisconnected();
	public boolean isConnecting();
	
	public IPAddress getIPAddress();
	public ClientID getClientID();
	
	public RoutingTable getRoutingTable();
	public BootStrap getBootStrap();
	public FirewallChecker getFirewallChecker();
	public Indexer getIndexer();
	public Lookup getLookup();
	public Publisher getPublisher();
	public Search getSearch();

	public void addJKadListener(JKadListener listener);
	public void removeJKadListener(JKadListener listener);
	
}
