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
package org.jmule.core.jkad;

import static org.jmule.core.jkad.JKadConstants.BOOTSTRAP_CHECK_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.BOOTSTRAP_CONTACTS;
import static org.jmule.core.jkad.JKadConstants.BOOTSTRAP_REMOVE_TIME;
import static org.jmule.core.jkad.JKadConstants.BOOTSTRAP_STOP_CONTACTS;
import static org.jmule.core.jkad.JKadConstants.MIN_CONTACTS_TO_SEND_BOOTSTRAP;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.JMException;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.jkad.JKadManagerImpl.JKadStatus;
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
 * @version $Revision: 1.13 $
 * Last changed by $Author: binary255 $ on $Date: 2010/10/23 05:49:29 $
 */
public class BootStrap {
	private InternalJKadManager _jkad_manager;
	private InternalNetworkManager _network_manager;
	private RoutingTable _routing_table = null;
	
	private Collection<KadContact> bootStrapContacts = new ConcurrentLinkedQueue<KadContact>();
	private Collection<KadContact> usedContacts 	 = new ConcurrentLinkedQueue<KadContact>();
	private int bootStrapResponses = 0;
	private Task bootStrapTask;
	
	private boolean isStarted = false;

	private static class BootStrapSingletonHolder {
		private static final BootStrap INSTANCE = new BootStrap();
	}

	public static BootStrap getInstance() {
		return BootStrapSingletonHolder.INSTANCE;
	}
	
	private BootStrap(){
		
	}
	
	public void start() {
		_routing_table = RoutingTable.getSingleton();
		_network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		_jkad_manager = (InternalJKadManager) JKadManagerSingleton.getInstance();
		isStarted = true;
		List<KadContact> contactList = _routing_table.getRandomContacts(BOOTSTRAP_CONTACTS);
		
		bootStrapResponses = 0;
		bootStrapContacts.clear();
		bootStrapContacts.addAll(contactList);
		
		usedContacts.clear();
		usedContacts.addAll(contactList);
		
		for(KadContact contact : contactList) {
			bootStrapContacts.add(contact);
			usedContacts.add(contact);
			try {
				if (_routing_table.getTotalContacts()<MIN_CONTACTS_TO_SEND_BOOTSTRAP) {
					KadPacket packet = null;
					if (contact.supportKad2())
						packet = PacketFactory.getBootStrapReq2Packet();
					else
						packet = PacketFactory.getBootStrap1ReqPacket();
					_network_manager.sendKadPacket(packet, contact.getIPAddress(), contact.getUDPPort());
				} else {
					KadPacket packet = null;
					if (contact.supportKad2())
						packet = PacketFactory.getHelloReq2Packet(TagList.EMPTY_TAG_LIST);
					else
						packet = PacketFactory.getHello1ReqPacket();
					_network_manager.sendKadPacket(packet, contact.getIPAddress(), contact.getUDPPort());
				}
			}catch(JMException e) {
				e.printStackTrace();
			}
		}
		
		bootStrapTask = new Task() {
			public void run() {
				if (bootStrapResponses >= BOOTSTRAP_STOP_CONTACTS) {
					completeBootStrap();
					return ;
				}
				for(KadContact contact : bootStrapContacts) {
					long time = System.currentTimeMillis() - contact.getLastResponse();
					if (time >= BOOTSTRAP_REMOVE_TIME) {
						bootStrapContacts.remove(contact);
						continue;
					}
					
				}
				
				int contactCount = BOOTSTRAP_CONTACTS - bootStrapContacts.size();
				if (contactCount <= 0)
					return;
				
				List<KadContact> contactList = _routing_table.getRandomContacts(contactCount,usedContacts);
				for(KadContact contact : contactList) {
					bootStrapContacts.add(contact);
					usedContacts.add(contact);
					try {
						if (_routing_table.getTotalContacts() < MIN_CONTACTS_TO_SEND_BOOTSTRAP) {
							KadPacket packet = null;
							if (contact.supportKad2())
								packet = PacketFactory.getBootStrapReq2Packet();
							else
								packet = PacketFactory.getBootStrap1ReqPacket();
							_network_manager.sendKadPacket(packet, contact.getIPAddress(), contact.getUDPPort());
						} else {
							KadPacket packet = null;
							if (contact.supportKad2())
								packet = PacketFactory.getHelloReq2Packet(TagList.EMPTY_TAG_LIST);
							else
								packet = PacketFactory.getHello1ReqPacket();
							_network_manager.sendKadPacket(packet, contact.getIPAddress(), contact.getUDPPort());
						}
					}catch(JMException e) {
						e.printStackTrace();
					}
				}
			}
		};
		Timer.getSingleton().addTask(BOOTSTRAP_CHECK_INTERVAL, bootStrapTask, true);
	}
	
	public void stop() {
		bootStrapContacts.clear();
		usedContacts.clear();
		bootStrapResponses = 0;
		isStarted = false;
		if (bootStrapTask== null) return ;
		Timer.getSingleton().removeTask(bootStrapTask);
	}
		
	private void completeBootStrap() {
		_jkad_manager.setStatus(JKadStatus.CONNECTED);					
	
		// stop task if already have enough contacts
		Timer.getSingleton().removeTask(bootStrapTask);
		FirewallChecker.getSingleton().start();
	}
	
	/**
	 * Bootstrap from client
	 * @param address
	 * @param port
	 */
	public void start(IPAddress address, int port) {
		KadPacket packet = PacketFactory.getBootStrapReq2Packet();
		_network_manager.sendKadPacket(packet, address, port);
	}
	
	public boolean isStarted() {
		return isStarted;
	}
	
	public void processBootStrap1Req(ClientID clientID, IPAddress ipAddress, int udpPort,int tcpPort) {
		List<KadContact> contactList = _routing_table.getNearestContacts(clientID, BOOTSTRAP_CONTACTS);
		KadPacket packet = PacketFactory.getBootStrap1ResPacket(contactList);
		_network_manager.sendKadPacket(packet, ipAddress, udpPort);
	}
	
	public void processBootStrap2Req(IPAddress ipaddress, int udpPort) {
		List<KadContact> contactList = _routing_table.getRandomContacts(BOOTSTRAP_CONTACTS);
		KadPacket packet;
		try {
			packet = PacketFactory.getBootStrapRes2Packet(contactList);
			_network_manager.sendKadPacket(packet, ipaddress, udpPort);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}
	
	public void processBootStrap1Res(List<KadContact> contactList) {
		for(KadContact contact : contactList) {
			if (!_routing_table.hasContact(contact)) {
				bootStrapResponses++;
				bootStrapContacts.remove(contact);
				
				_routing_table.addContact(contact);
								
				//Packet packet = PacketFactory.getRequestPacket(FIND_NODE, contact.getContactID(), Kad.getSingelton().getClientID());
				//udpConnection.sendPacket(packet, contact.getIPAddress(), contact.getUDPPort());
			}
		}		
	}
	
	public void processBotStrap2Res(ClientID clientID, int tcpPort, byte version, List<KadContact> contactList) {
		for(KadContact contact : contactList) {
			if (!_routing_table.hasContact(contact)) {
				bootStrapResponses++;
				bootStrapContacts.remove(contact);
				
				_routing_table.addContact(contact);
			}
		}
	}
	
	public void processHello1Res(IPAddress senderAddress, int senderPort) {
		for (KadContact c : usedContacts) {
			if (c.getContactAddress().getAddress().equals(senderAddress)) {
				bootStrapResponses++;
				break;
			}
		}
		if (bootStrapResponses >= 10) {
			completeBootStrap();
			return;
		}
	}
	
	public void processHello2Res(IPAddress senderAddress, int senderPort) {
		for (KadContact c : usedContacts) {
			if (c.getContactAddress().getAddress().equals(senderAddress)) {
				bootStrapResponses++;
				break;
			}
		}
		if (bootStrapResponses >= 10) {
			completeBootStrap();
			return;
		}
	}
}
