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
package org.jmule.core.jkad.lookup;

import static org.jmule.core.jkad.JKadConstants.ALPHA;
import static org.jmule.core.jkad.JKadConstants.INITIAL_LOOKUP_CONTACTS;
import static org.jmule.core.jkad.JKadConstants.LOOKUP_CONTACT_TIMEOUT;
import static org.jmule.core.jkad.utils.Utils.getNearestContact;
import static org.jmule.core.jkad.utils.Utils.inToleranceZone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.jkad.ClientID;
import org.jmule.core.jkad.ContactAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.InternalJKadManager;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.JKadManagerSingleton;
import org.jmule.core.jkad.packet.KadPacket;
import org.jmule.core.jkad.packet.PacketFactory;
import org.jmule.core.jkad.routingtable.KadContact;
import org.jmule.core.jkad.routingtable.RoutingTable;
import org.jmule.core.jkad.utils.Utils;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;

/**
 * Created on Jan 9, 2009
 * @author binary256
 * @version $Revision: 1.12 $
 * Last changed by $Author: binary255 $ on $Date: 2010/07/06 08:49:07 $
 */
public abstract class LookupTask {
	protected int initialLookupContacts = INITIAL_LOOKUP_CONTACTS; 
	protected long last_response_time;
	protected Int128 targetID;
	protected Int128 targetDistance;
	protected long toleranceZone;
	protected long timeOut;
	protected long contactLookupTimeout = LOOKUP_CONTACT_TIMEOUT;
	
	protected Collection<KadContact> possibleContacts = new ConcurrentLinkedQueue<KadContact> ();
	protected Collection<KadContact> usedContacts = new ConcurrentLinkedQueue<KadContact>();
	protected Set<ClientID> usedContactsSet = new HashSet<ClientID>();
	protected Set<ClientID> usedAlphaContacts = new HashSet<ClientID>();
	protected Map<ContactAddress, RequestedContact> requestedContacts = new ConcurrentHashMap<ContactAddress, RequestedContact>();
	
	protected RoutingTable _routing_table;
	protected RequestType requestType;
		
	protected boolean lookupStarted = false;
	
	protected InternalNetworkManager _network_manager;
	protected InternalJKadManager 	 _jkad_manager;
	
	protected long startTime = 0;
	
	public long getStartTime() {
		return startTime;
	}
	
	public LookupTask(RequestType requestType, Int128 targetID, long lookupTimeOut) {
		_network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		_jkad_manager    = (InternalJKadManager) JKadManagerSingleton.getInstance();
		_routing_table = RoutingTable.getSingleton();
		
		this.startTime = System.currentTimeMillis();
		this.targetID = targetID;
		this.targetDistance = Utils.XOR(targetID, _jkad_manager.getClientID());
		this.toleranceZone = JKadConstants.TOLERANCE_ZONE;
		this.timeOut = lookupTimeOut;
	
		this.requestType = requestType;
		last_response_time = System.currentTimeMillis();
	}
		
	public void startLookup() {
		lookupStarted = true;
		last_response_time = System.currentTimeMillis();
		possibleContacts.addAll(_routing_table.getNearestContacts(targetID, initialLookupContacts));

		int count = ALPHA;
		if (count > possibleContacts.size()) count = possibleContacts.size();
		for (int i = 0; i < count; i++) {
			KadContact contact = getNearestContact(targetDistance, possibleContacts,usedContactsSet);
			
			lookupContact(contact);
		}
	}
	
	public void stopLookup() {
		lookupTerminated();
		lookupStarted = false;
		possibleContacts.clear();
		usedContacts.clear();
		usedContactsSet.clear();
		requestedContacts.clear();
	}
	
	public void  processResults(ContactAddress sender, List<KadContact> results) {
		last_response_time = System.currentTimeMillis();
		
		List<KadContact> alpha  = new ArrayList<KadContact>();
		
		for(KadContact contact : results) {
			if (usedContactsSet.contains(contact.getContactID())) continue;
			if (inToleranceZone(Utils.XOR(contact.getContactID(), _jkad_manager.getClientID()), targetDistance, toleranceZone)) {
				if (!usedAlphaContacts.contains(contact.getContactID())) {
					alpha.add(contact);
					usedAlphaContacts.add(contact.getContactID());
				}
				possibleContacts.add(contact);
			}
			else 
				possibleContacts.add(contact);
		}
		if (alpha.size()!=0) {
			processToleranceContacts(sender, alpha);
		}
		
		requestedContacts.remove(sender);
		KadContact contact = getNearestContact(Utils.XOR(targetID, _jkad_manager.getClientID()), possibleContacts,usedContactsSet);
		if (contact != null) lookupContact(contact);
	}
	
	void lookupContact(KadContact contact) {
		possibleContacts.remove(contact);
		usedContacts.add(contact);
		usedContactsSet.add(contact.getContactID());
		RequestedContact requested_contact = new RequestedContact(contact, System.currentTimeMillis());
		requestedContacts.put(contact.getContactAddress(), requested_contact);
		KadPacket packet = null;
		if (contact.supportKad2())
			packet = PacketFactory.getRequest2Packet(requestType, targetID, (Int128)contact.getContactID());
		else
			packet = PacketFactory.getRequest1Packet(requestType, targetID, (Int128)contact.getContactID());
		_network_manager.sendKadPacket(packet, contact.getIPAddress(), contact.getUDPPort());
	}
		
	public long getResponseTime() {
		return last_response_time;
	}
	
	public Int128 getTargetID() {
		return targetID;
	}
	public void setTargetID(Int128 targetID) {
		this.targetID = targetID;
	}

	public long getTimeOut() {
		return timeOut;
	}

	public long getToleranceZone() {
		return toleranceZone;
	}
	
	public abstract void lookupTerminated();
	
	/**
	 * Lookup process timeout
	 */
	public abstract void lookupTimeout();
	
	/**
	 * Process tolerance contacts
	 * @param sender
	 * @param results
	 */
	public abstract void processToleranceContacts(ContactAddress sender, List<KadContact> results);
	
	protected class RequestedContact {
		private KadContact contact;
		private long requestTime;
		
		public RequestedContact(KadContact contact, long requestTime) {
			super();
			this.contact = contact;
			this.requestTime = requestTime;
		}

		public KadContact getContact() {
			return contact;
		}

		public void setContact(KadContact contact) {
			this.contact = contact;
		}

		public long getRequestTime() {
			return requestTime;
		}
		
	}

	public boolean isLookupStarted() {
		return lookupStarted;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public long getContactLookupTimeout() {
		return contactLookupTimeout;
	}

	public void setContactLookupTimeout(long contactLookupTimeout) {
		this.contactLookupTimeout = contactLookupTimeout;
	}
}
