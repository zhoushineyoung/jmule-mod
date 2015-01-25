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
package org.jmule.core.jkad.indexer;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.jkad.ClientID;
import org.jmule.core.jkad.Int128;


/**
 * Created on Apr 22, 2009
 * @author binary256
 * @version $Revision: 1.3 $
 * Last changed by $Author: binary255 $ on $Date: 2010/06/28 18:01:50 $
 */
public class Index {

	protected Int128 id;
	protected Map<ClientID,Source> sources = new ConcurrentHashMap<ClientID, Source>();
	
	public Index(Int128 id) {
		this.id = id;
	}
	
	public Int128 getId() {
		return id;
	}
	public void setId(Int128 id) {
		this.id = id;
	}
	public Collection<Source> getSourceList() {
		return sources.values();
	}	
	
	public int getSourceCount() {
		return sources.size();
	}
	
	public void addSource(Source source) {
		if (sources.containsKey(source.getClientID())) {
			sources.get(source.getClientID()).setCreationTime(System.currentTimeMillis());
		} else
			sources.put(source.getClientID(),source);
	}
	
	public void removeContactsWithTimeOut(long timeOut) {
		for(ClientID clientID : sources.keySet()) {
			long ctime = System.currentTimeMillis();
			Source source = sources.get(clientID);
			if (ctime - source.getCreationTime() >= timeOut)
				sources.remove(clientID);
		}
	}
	
	public boolean isEmpty() {
		return sources.isEmpty();
	}
	
	public boolean containsClientID(ClientID clientID) {
		return sources.containsKey(clientID);
	}
	
	public void removeOldestSource() {
		long maxTimeout = 0;
		ClientID clientID = null;
		for(ClientID id : sources.keySet()) {
			if (clientID == null) {
				clientID = id;
				maxTimeout = System.currentTimeMillis() - sources.get(id).getCreationTime();
			} else
				if (System.currentTimeMillis() - sources.get(id).getCreationTime() > maxTimeout) {
					clientID = id;
					maxTimeout = System.currentTimeMillis() - sources.get(id).getCreationTime();
				}
		}
		if (clientID!=null)
			sources.remove(clientID);
	}
	
}
