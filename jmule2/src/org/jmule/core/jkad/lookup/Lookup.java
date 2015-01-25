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

import static org.jmule.core.jkad.JKadConstants.LOOKUP_CONTACT_CHECK_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.LOOKUP_NODE_CONTACTS;
import static org.jmule.core.jkad.JKadConstants.LOOKUP_TASK_CHECK_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.PUBLISH_KEYWORD_CONTACT_COUNT;
import static org.jmule.core.jkad.JKadConstants.SEARCH_CONTACTS;
import static org.jmule.core.jkad.utils.Utils.getNearestContact;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.jkad.ContactAddress;
import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.JKadException;
import org.jmule.core.jkad.lookup.LookupTask.RequestedContact;
import org.jmule.core.jkad.packet.KadPacket;
import org.jmule.core.jkad.packet.PacketFactory;
import org.jmule.core.jkad.routingtable.KadContact;
import org.jmule.core.jkad.routingtable.RoutingTable;
import org.jmule.core.jkad.utils.timer.Task;
import org.jmule.core.jkad.utils.timer.Timer;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;


/**
 * Created on Jan 9, 2009
 * @author binary256
 * @version $Revision: 1.14 $
 * Last changed by $Author: binary255 $ on $Date: 2010/08/31 10:28:30 $
 */
public class Lookup {
	
	private Map<Int128, LookupTask> lookupTasks = new ConcurrentHashMap<Int128, LookupTask>();
	
	private RoutingTable routing_table = null;
	private InternalNetworkManager _network_manager = null;
	
	private Task lookupCleaner = null;
	private Task lookupContactCleaner = null;
	
	private boolean isStarted = false;
	
	private Collection<LookupListener> listenerList = new ConcurrentLinkedQueue<LookupListener>();
	
	public boolean isStarted() {
		return isStarted;
	}
	
	private static class LookupSingletonHolder {
		private static final Lookup INSTANCE = new Lookup();
	}
	
	public static Lookup getSingleton() {
		return LookupSingletonHolder.INSTANCE;
	}
	
	private Lookup() {
		lookupCleaner  = new Task() {
			public void run() {
				for(Int128 key : lookupTasks.keySet()) {
					LookupTask task = lookupTasks.get(key);		
					if ((System.currentTimeMillis() - task.getResponseTime() > task.getTimeOut()) || (System.currentTimeMillis() - task.getStartTime() > JKadConstants.MAX_LOOKUP_RUNNING_TIME)){
						task.lookupTimeout();
						task.stopLookup();
						lookupTasks.remove(key);
						notifyListeners(task, LookupStatus.REMOVED);
					}
				}
			}
		};
		lookupContactCleaner = new Task() {
			public void run() {
				for(Int128 key : lookupTasks.keySet()) {
					LookupTask task = lookupTasks.get(key);
					for(ContactAddress address : task.requestedContacts.keySet()) {
						RequestedContact c = task.requestedContacts.get(address);
						if (c==null) { task.requestedContacts.remove(address); continue; }
						if (System.currentTimeMillis() - c.getRequestTime() > task.getContactLookupTimeout()) {
							task.requestedContacts.remove(address);
							// lookup next
							KadContact contact = getNearestContact(task.targetDistance, task.possibleContacts,task.usedContactsSet);
							if (contact == null)
								continue;
							task.lookupContact(contact);
						}
					}
				}
			}
		};
	}
	
	public Map<Int128,LookupTask> getLookupTasks() { return lookupTasks; }
	
	public String toString() {
		String result = " [\n ";
		result += "Lookup tasks : \n";
		for(Int128 key : lookupTasks.keySet())
			result += key.toHexString() + " " + lookupTasks.get(key) + "\n";
		result += " ] ";
		return result;
	}
	
	public void start() {
		isStarted = true;
		routing_table = RoutingTable.getSingleton();
		_network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		Timer.getSingleton().addTask(LOOKUP_TASK_CHECK_INTERVAL, lookupCleaner, true);
		Timer.getSingleton().addTask(LOOKUP_CONTACT_CHECK_INTERVAL, lookupContactCleaner, true);
	}
	
	public void stop() {
		isStarted = false;
		for (Int128 key : lookupTasks.keySet()) {
			LookupTask task = lookupTasks.get(key);
			task.stopLookup();
			lookupTasks.remove(key);
			notifyListeners(task, LookupStatus.REMOVED);
		}
		Timer.getSingleton().removeTask(lookupCleaner);
		Timer.getSingleton().removeTask(lookupContactCleaner);
	}

	public void addLookupTask(LookupTask task) throws JKadException {
		if (lookupTasks.containsKey(task.getTargetID()))
				throw new JKadException("Already has lookup task with : " + task.getTargetID().toHexString());
		lookupTasks.put(task.getTargetID(), task);
		notifyListeners(task, LookupStatus.ADDED);
		task.startLookup();
		notifyListeners(task, LookupStatus.STARTED);
	}
	
	public void removeLookupTask(Int128 targetID) {
		if (lookupTasks.containsKey(targetID)) {
			LookupTask task = lookupTasks.get(targetID);
			task.stopLookup();
			lookupTasks.remove(targetID);
			notifyListeners(task, LookupStatus.REMOVED);
		}
	}
	
	public boolean hasTask(Int128 targetID) {
		return lookupTasks.containsKey(targetID);
	}
		
	public void processRequest(InetSocketAddress sender, RequestType requestType, Int128 targetID, Int128 sourceID, boolean useKad2 ) {
		switch(requestType) {
		case FIND_NODE : {
			List<KadContact> list = routing_table.getNearestContacts(targetID,LOOKUP_NODE_CONTACTS);
			KadPacket response;
			if (!useKad2)
				response = PacketFactory.getResponse1Packet(targetID, list);
			else
				response = PacketFactory.getResponse2Packet(targetID, list);
			_network_manager.sendKadPacket(response, new IPAddress(sender), sender.getPort());
			break;
		}
		
		case FIND_VALUE : {
			List<KadContact> list = routing_table.getNearestContacts(targetID, SEARCH_CONTACTS);
			KadPacket response;
			if (!useKad2)
				response = PacketFactory.getResponse1Packet(targetID, list);
			else
				response = PacketFactory.getResponse2Packet(targetID, list);
			_network_manager.sendKadPacket(response, new IPAddress(sender), sender.getPort());
			break;
		}
		
		case STORE : {
			List<KadContact> list = routing_table.getNearestContacts(targetID, PUBLISH_KEYWORD_CONTACT_COUNT);
			
			KadPacket response;
			if (!useKad2)
				response = PacketFactory.getResponse1Packet(targetID, list);
			else
				response = PacketFactory.getResponse2Packet(targetID, list);
			_network_manager.sendKadPacket(response, new IPAddress(sender), sender.getPort());
		}
		
		}
	}
	
	public void processResponse(final InetSocketAddress sender, final Int128 targetID, final List<KadContact> contactList) {
		LookupTask listener = lookupTasks.get(targetID);
		if (listener == null) return ;
		listener.processResults(new ContactAddress(sender), contactList);
	}
	
	public void addListener(LookupListener listener) {
		listenerList.add(listener);
	}
	
	public void removeListener(LookupListener listener) {
		listenerList.remove(listener);
	}
	
	private enum LookupStatus { ADDED, STARTED, REMOVED}
	
	private void notifyListeners(LookupTask lookup, LookupStatus status) {
		for(LookupListener listener : listenerList) {
			if (status == LookupStatus.ADDED)
				listener.taskAdded(lookup);
			if (status == LookupStatus.STARTED)
				listener.taskStarted(lookup);
			if (status == LookupStatus.REMOVED)
				listener.taskRemoved(lookup);
		}
	}
}