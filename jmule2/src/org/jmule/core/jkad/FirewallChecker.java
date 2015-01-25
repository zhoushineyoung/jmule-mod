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

import static org.jmule.core.jkad.JKadConstants.FIREWALLED_STATUS_CHANGE_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.FIREWALL_CHECK_CONTACTS;
import static org.jmule.core.jkad.JKadConstants.FIREWALL_CHECK_INTERVAL;
import static org.jmule.core.utils.Convert.reverseArray;

import java.net.InetSocketAddress;
import java.util.List;

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.jkad.packet.KadPacket;
import org.jmule.core.jkad.packet.PacketFactory;
import org.jmule.core.jkad.routingtable.KadContact;
import org.jmule.core.jkad.routingtable.RoutingTable;
import org.jmule.core.jkad.utils.timer.Task;
import org.jmule.core.jkad.utils.timer.Timer;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;


/**
 * Created on Jan 8, 2009
 * @author binary256
 * @version $Revision: 1.10 $
 * Last changed by $Author: binary255 $ on $Date: 2010/06/25 10:14:30 $
 */
public class FirewallChecker {

	private static FirewallChecker singleton = null;

	private boolean firewalled = true;
	private long lastStateChange = System.currentTimeMillis();

	private InternalNetworkManager _network_manager ;
	private InternalJKadManager _jkad_manager ;
	
	private RoutingTable routing_table = null;
	private Task firewall_check_task = null;

	private IPAddress my_ip_address = null;

	private boolean is_started = false;

	public static FirewallChecker getSingleton() {
		if (singleton == null)
			singleton = new FirewallChecker();
		return singleton;
	}

	private FirewallChecker() {
		_network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		routing_table = RoutingTable.getSingleton();
		firewall_check_task = new Task() {
			public void run() {
				if (!_jkad_manager.isConnected()) {
					return;
				}
				List<KadContact> list = routing_table.getRandomContacts(FIREWALL_CHECK_CONTACTS);
				ConfigurationManager configManager = ConfigurationManagerSingleton.getInstance();
				for (KadContact contact : list) {
					if (!_jkad_manager.isConnected()) {
						return;
					}
					KadPacket packet;
					try {
						packet = PacketFactory.getFirewalled1Req(configManager.getTCP());
						_network_manager.sendKadPacket(packet, contact.getIPAddress(), contact.getUDPPort());
					} catch (ConfigurationManagerException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	public void start() {
		if (is_started) return ;
		is_started = true;
		
		_jkad_manager = (InternalJKadManager) JKadManagerSingleton.getInstance();
		Timer.getSingleton().addTask(FIREWALL_CHECK_INTERVAL,firewall_check_task, true);
		firewall_check_task.run();
	}

	public void stop() {
		is_started = false;
		Timer.getSingleton().removeTask(firewall_check_task);
	}

	public void startNowFirewallCheck() {
		firewall_check_task.run();
	}

	public boolean isStarted() {
		return is_started;
	}

	public boolean isFirewalled() {
		return firewalled;
	}

	private void setFirewalled(boolean value) {
		if (System.currentTimeMillis() - lastStateChange < FIREWALLED_STATUS_CHANGE_INTERVAL)
			return;
		this.firewalled = value;
		lastStateChange = System.currentTimeMillis();
	}

	public void processFirewallRequest(IPAddress sender, int TCPPort, int udpPort) {
		byte data[] = sender.getAddress();
		data = reverseArray(data);

		KadPacket packet = PacketFactory.getFirewalled1Res(data);
		_network_manager.sendKadPacket(packet, sender, udpPort);
	}

	public void sendFirewallRequest(InetSocketAddress sender, Int128 contactID) {

		KadContact contact = routing_table.getContact(contactID);
		if (contact == null)
			return;
		contact.setLastUDPFirewallResponse(System.currentTimeMillis());

		if (System.currentTimeMillis() - contact.getLastUDPFirewallResponse() < FIREWALL_CHECK_INTERVAL)
			return;
		contact.setUDPFirewallQueries(contact.getUDPFirewallQueries() + 1);

		KadPacket packet = PacketFactory.getFirewalled1Req(contact.getTCPPort());
		_network_manager.sendKadPacket(packet, new IPAddress(sender), sender.getPort());
	}

	public void porcessFirewallResponse(InetSocketAddress sender,IPAddress address) {
		KadContact contact = routing_table.getContact(new IPAddress(sender));
		if (contact != null) {
			contact.setLastUDPFirewallResponse(System.currentTimeMillis());
			contact.setUDPFirewallResponses(contact.getUDPFirewallResponses() + 1);
			if (my_ip_address == null) {
				my_ip_address = address;
			} else if (my_ip_address.equals(address))
				setFirewalled(false);
			else
				setFirewalled(true);
		}
	}

	public IPAddress getMyIPAddress() {
		if (my_ip_address == null)
			return new IPAddress(new byte[] { 0, 0, 0, 0 });
		return my_ip_address;
	}
}