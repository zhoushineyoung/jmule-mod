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
package org.jmule.core.jkad;

import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_BOOTSTRAP_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_BOOTSTRAP_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_HELLO_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_HELLO_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_PUBLISH_KEY_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_PUBLISH_NOTES_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_PUBLISH_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_PUBLISH_SOURCE_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_SEARCH_KEY_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_SEARCH_NOTES_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_SEARCH_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_SEARCH_SOURCE_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_BOOTSTRAP_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_BOOTSTRAP_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_CALLBACK_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_FINDBUDDY_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_FIREWALLED_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_FIREWALLED_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_HELLO_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_HELLO_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_PUBLISH_NOTES_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_PUBLISH_NOTES_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_PUBLISH_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_PUBLISH_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_SEARCH_NOTES_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_SEARCH_NOTES_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_SEARCH_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_SEARCH_RES;
import static org.jmule.core.jkad.JKadConstants.PROTO_KAD_UDP;
import static org.jmule.core.jkad.JKadConstants.TAG_SOURCETYPE;
import static org.jmule.core.jkad.JKadManagerImpl.JKadStatus.CONNECTED;
import static org.jmule.core.jkad.JKadManagerImpl.JKadStatus.CONNECTING;
import static org.jmule.core.jkad.JKadManagerImpl.JKadStatus.DISCONNECTED;
import static org.jmule.core.utils.Convert.byteToInt;
import static org.jmule.core.utils.Convert.shortToInt;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jmule.core.JMRunnable;
import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.configmanager.InternalConfigurationManager;
import org.jmule.core.downloadmanager.DownloadManagerSingleton;
import org.jmule.core.downloadmanager.InternalDownloadManager;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.packet.tag.TagScanner;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.indexer.Indexer;
import org.jmule.core.jkad.indexer.Source;
import org.jmule.core.jkad.lookup.Lookup;
import org.jmule.core.jkad.packet.CorruptedPacketException;
import org.jmule.core.jkad.packet.KadPacket;
import org.jmule.core.jkad.packet.PacketFactory;
import org.jmule.core.jkad.packet.UnknownPacketOPCodeException;
import org.jmule.core.jkad.packet.UnknownPacketType;
import org.jmule.core.jkad.publisher.Publisher;
import org.jmule.core.jkad.routingtable.KadContact;
import org.jmule.core.jkad.routingtable.RoutingTable;
import org.jmule.core.jkad.search.Search;
import org.jmule.core.jkad.utils.Convert;
import org.jmule.core.jkad.utils.timer.Timer;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerException;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.peermanager.Peer.PeerSource;
import org.jmule.core.peermanager.PeerManagerSingleton;

/**
 * 
 * Utilization : 
 *  Kad.getSingelton()
 *  Kad.initialize()
 *  
 *  Kad.connect() / Kad.connect(IP to bootstrap);
 *  Kad.disconnect()
 *  
 *  Kad.stop();
 *  
 *  
 * Created on Dec 29, 2008
 * @author binary256
 * @version $Revision: 1.27 $
 * Last changed by $Author: binary255 $ on $Date: 2011/04/04 12:27:42 $
 */
class JKadManagerImpl extends JMuleAbstractManager implements InternalJKadManager {
	public enum JKadStatus { CONNECTED, CONNECTING, DISCONNECTED }

	private ClientID clientID;
	private RoutingTable routing_table = null;
	private Indexer indexer = null;
	private FirewallChecker firewallChecker = null;
	private BootStrap bootStrap = null;
	private Lookup lookup = null;
	private Search search = null;
	private Publisher publisher = null;
	private JKadStatus status = DISCONNECTED;

	private InternalConfigurationManager _config_manager = null;
	private InternalNetworkManager _network_manager = null;
	private InternalDownloadManager _download_manager = null;
	
	private List<JKadListener> listener_list = new ArrayList<JKadListener>();

	private boolean is_started = false;
	
	JKadManagerImpl() {
	}

	public void initialize() {

		try {
			super.initialize();
		} catch (JMuleManagerException e1) {
			e1.printStackTrace();
			return;
		}
		_config_manager = (InternalConfigurationManager) ConfigurationManagerSingleton.getInstance();
		_network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		_download_manager = (InternalDownloadManager) DownloadManagerSingleton.getInstance();

		routing_table = RoutingTable.getSingleton();
		indexer = Indexer.getSingleton();
		

		firewallChecker = FirewallChecker.getSingleton();
		bootStrap = BootStrap.getInstance();
		lookup = Lookup.getSingleton();
		search = Search.getSingleton();
		publisher = Publisher.getInstance();

		try {
			clientID = _config_manager.getJKadClientID();
		} catch (ConfigurationManagerException e1) {
			e1.printStackTrace();
		}

		ConfigurationManagerSingleton.getInstance().addConfigurationListener(
				new ConfigurationAdapter() {
					public void jkadStatusChanged(boolean newStatus) {
						if (newStatus == false)
							if (!isDisconnected())
								disconnect();
						if (newStatus == true)
							if (getStatus() == DISCONNECTED)
								connect();
					}
				});

	}

	public void shutdown() {
		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		is_started = false;
		if (!isDisconnected())
			disconnect();
		Timer.getSingleton().stop();
	}

	
	public void start() {
		try {
			super.start();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		is_started = true;
		this.connect();
	}

	protected boolean iAmStoppable() {
		return true;
	}

	public void connect() {
		setStatus(CONNECTING);
		
		routing_table.start();
		/*if (routing_table.getTotalContacts() == 0) {
			routing_table.stop();
			setStatus(DISCONNECTED);
			return;
		}*/
		
		lookup.start();
		publisher.start();
		search.start();
		indexer.start();
		
		bootStrap.start();
	}

	public void connect(ContactAddress address) {
		setStatus(CONNECTING);
		routing_table.start();

		lookup.start();
		publisher.start();
		search.start();
		indexer.start();

		bootStrap.start(address.getAddress(), address.getUDPPort());
	}

	public void disconnect() {
		if (isDisconnected())
			return;

		firewallChecker.stop();
		indexer.stop();
		search.stop();

		publisher.stop();
		lookup.stop();
		bootStrap.stop();
		routing_table.stop();

		Timer.getSingleton().stop();
		
		setStatus(DISCONNECTED);
	}

	public boolean isStarted() {
		return is_started;
	}

	public void setStatus(JKadStatus newStatus) {
		
		boolean status_changed = false;
		if (status != newStatus)
			status_changed = true;
		status = newStatus;
		if (status_changed) {
			if (status == CONNECTED) {
				if (!firewallChecker.isStarted())
					firewallChecker.start();
			}
			notifyListeners(newStatus);
			if (status == CONNECTED) 
				_download_manager.jKadConnected();
			if (status == DISCONNECTED)
				_download_manager.jKadDisconnected();
		}
	}

	public boolean isFirewalled() {
		return firewallChecker.isFirewalled();
	}

	public void receivePacket(KadPacket packet) {
		try {
			processPacket(packet);
		} catch (Throwable t) {
			t.printStackTrace();
			//Logger.getSingleton().logException(t);
			/*ByteBuffer unkPacket = getByteBuffer(packet.getAsByteBuffer().limit());
			packet.getAsByteBuffer().position(0);
			packet.getAsByteBuffer().get(unkPacket.array(), 0,packet.getAsByteBuffer().capacity());
			Logger.getSingleton().logMessage("Exception in processing : \n"+ byteToHexString(unkPacket.array(), " "));*/
		}
	}

	private void processPacket(KadPacket packet) throws UnknownPacketOPCodeException, CorruptedPacketException, UnknownPacketType {
		IPAddress sender_address = new IPAddress(packet.getAddress());
		int sender_port = packet.getAddress().getPort();
		if (isDisconnected())
			return;
		if (packet.isCompressed())
			packet.decompress();
		if (packet.getProtocol() != PROTO_KAD_UDP)
			throw new UnknownPacketType();

		// update last response
		KadContact rcontact = routing_table.getContact(packet.getAddress());
		if (rcontact != null) {
			rcontact.setLastResponse(System.currentTimeMillis());
			rcontact.setConnected(true);
			rcontact.setIPVerified(true);
		}

		byte packetOPCode = packet.getCommand();
		ByteBuffer rawData = packet.getAsByteBuffer();
		rawData.position(2);
		boolean unknown_packet = false;
		try {
		switch (packetOPCode) {
			case KADEMLIA_BOOTSTRAP_REQ: {
				byte[] client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				byte[] address = new byte[4];
				rawData.get(address);
				int udp_port = shortToInt(rawData.getShort());
				int tcp_port = shortToInt(rawData.getShort());
	
				bootStrap.processBootStrap1Req(new ClientID(client_id_raw),new IPAddress(address), udp_port,tcp_port);
				break;
			}
			
			case KADEMLIA_BOOTSTRAP_RES : {
				int contactCount = shortToInt(rawData.getShort());
				List<KadContact> contact_list = new ArrayList<KadContact>();
				for (int i = 0; i < contactCount; i++) {
					contact_list.add(getContact(rawData));
				}
	
				bootStrap.processBootStrap1Res(contact_list);
				break;
			}
			
			case KADEMLIA_HELLO_REQ : {
				byte[] client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				byte[] address = new byte[4];
				rawData.get(address);
				int udp_port = shortToInt(rawData.getShort());
				int tcp_port = shortToInt(rawData.getShort());
	
				KadContact contact = routing_table.getContact(new ClientID(client_id_raw));
				if (contact != null) {
					contact.setTCPPort(tcp_port);
					contact.setUDPPort(udp_port);
	
				}
	
				KadPacket response;
				response = PacketFactory.getHello1ResPacket();
				_network_manager.sendKadPacket(response, sender_address,sender_port);
				break;
			}
				
			case KADEMLIA_HELLO_RES : {
				byte[] client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				byte address[] = new byte[4];
				rawData.get(address);
				int udp_port = shortToInt(rawData.getShort());
				int tcp_port = shortToInt(rawData.getShort());
	
				KadContact contact = routing_table.getContact(new ClientID(
						client_id_raw));
	
				if (contact != null) {
					contact.setTCPPort(tcp_port);
					contact.setUDPPort(udp_port);
				}
				
				if (bootStrap.isStarted())
					bootStrap.processHello1Res(sender_address, sender_port);
				routing_table.processHello1Res(sender_address, sender_port);
				break;
			}
			
			case KADEMLIA_REQ : {
				byte type = rawData.get();
	
				byte[] client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				ClientID targetClientID = new ClientID(client_id_raw);
	
				client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				ClientID receiverClientID = new ClientID(client_id_raw);
				RequestType requestType = RequestType.FIND_VALUE;
				switch (type) {
				case JKadConstants.FIND_VALUE:
					requestType = RequestType.FIND_VALUE;
					break;
	
				case JKadConstants.STORE:
					requestType = RequestType.STORE;
					break;
	
				case JKadConstants.FIND_NODE:
					requestType = RequestType.FIND_NODE;
					break;
				}
				lookup.processRequest(packet.getAddress(), requestType,targetClientID, receiverClientID, false);
				break;
			}
			
			case KADEMLIA_RES : {
				byte[] client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				int contactCount = byteToInt(rawData.get());
				List<KadContact> contact_list = new ArrayList<KadContact>();
				for (int i = 0; i < contactCount; i++) {
					contact_list.add(getContact(rawData));
				}
	
				lookup.processResponse(packet.getAddress(), new ClientID(client_id_raw), contact_list);
				break;
			}
			
			case KADEMLIA_PUBLISH_REQ : {
				byte target_id[] = new byte[16];
				rawData.get(target_id);
				Int128 targetID = new Int128(target_id);
				int clientCount = shortToInt(rawData.getShort());
	
				List<Source> list = new ArrayList<Source>();
	
				for (int i = 0; i < clientCount; i++) {
					byte clientID[] = new byte[16];
					rawData.get(clientID);
					int tagCount = shortToInt(rawData.get());
					TagList tagList = new TagList();
					for (int k = 0; k < tagCount; k++) {
						Tag tag = TagScanner.scanTag(rawData);
						tagList.addTag(tag);
					}
					ClientID client_id = new ClientID(clientID);
					Source source = new Source(client_id, tagList);
	
					source.setAddress(new IPAddress(packet.getAddress()));
					source.setUDPPort(packet.getAddress().getPort());
	
					KadContact contact = routing_table.getContact(client_id);
					if (contact != null)
						source.setTCPPort(contact.getTCPPort());
	
					list.add(source);
				}
	
				boolean source_load = false;
				int load = 0;
				for (Source source : list) {
					boolean isSourcePublish = false;
					isSourcePublish = source.getTagList().hasTag(TAG_SOURCETYPE);
					
					if (isSourcePublish) {
						load = indexer.addFileSource(targetID, source);
						source_load = true;
					} else
						load = indexer.addKeywordSource(targetID, source);
				}
				KadPacket response = null;
				if (source_load)
					response = PacketFactory.getPublish1ResPacket(targetID,load);
				else
					response = PacketFactory.getPublish1ResPacket(targetID,load);
				_network_manager.sendKadPacket(response, sender_address,sender_port);
				break;
			}
			
			case KADEMLIA_PUBLISH_RES : {
				byte targetID[] = new byte[16];
				rawData.get(targetID);
				int load = byteToInt(rawData.get());
				publisher.processPublishResponse(new ClientID(targetID), load);
				break;
			}
			
			case KADEMLIA_SEARCH_NOTES_REQ : {
				byte[] targetID = new byte[16];
				rawData.get(targetID);
				Collection<Source> source_list = indexer.getNoteSources(new Int128(targetID));
				KadPacket response = PacketFactory.getNotes1Res(new Int128(targetID), source_list);
				_network_manager.sendKadPacket(response, sender_address,sender_port);
				break;
			}
			
			case KADEMLIA_SEARCH_NOTES_RES : {
				byte[] noteID = new byte[16];
				rawData.get(noteID);
				int resultCount = shortToInt(rawData.getShort());
				List<Source> sourceList = new ArrayList<Source>();
	
				for (int i = 0; i < resultCount; i++) {
					byte[] clientID = new byte[16];
					rawData.get(clientID);
					Convert.updateSearchID(clientID);
					int tagCount = shortToInt(rawData.get());
					TagList tagList = new TagList();
					for (int k = 0; k < tagCount; k++) {
						Tag tag = TagScanner.scanTag(rawData);
						tagList.addTag(tag);
					}
					Source source = new Source(new ClientID(clientID), tagList);
					source.setAddress(new IPAddress(packet.getAddress()));
					source.setUDPPort(packet.getAddress().getPort());
					KadContact contact = RoutingTable.getSingleton().getContact(new ClientID(clientID));
					if (contact != null)
						source.setTCPPort(contact.getTCPPort());
					sourceList.add(source);
				}
				search.processSearchResults(sender_address, new Int128(noteID),sourceList);
				break;
			}
			case KADEMLIA_PUBLISH_NOTES_REQ : {
				byte[] noteID = new byte[16];
				rawData.get(noteID);
				byte[] publisherID = new byte[16];
				rawData.get(publisherID);
				int tagCount = byteToInt(rawData.get());
				TagList tagList = new TagList();
				for (int i = 0; i < tagCount; i++) {
					Tag tag = TagScanner.scanTag(rawData);
					tagList.addTag(tag);
				}
				ClientID publisher_id = new ClientID(publisherID);
				Source source = new Source(publisher_id, tagList);
				source.setAddress(new IPAddress(packet.getAddress()));
				source.setUDPPort(packet.getAddress().getPort());
	
				KadContact contact = routing_table.getContact(publisher_id);
				if (contact != null)
					source.setTCPPort(contact.getTCPPort());
	
				int load = indexer.addNoteSource(new Int128(noteID), source);
				KadPacket response = PacketFactory.getPublishNotes1Res(new Int128(noteID), load);
				_network_manager.sendKadPacket(response, sender_address,sender_port);
				break;
			}
			
			case KADEMLIA_PUBLISH_NOTES_RES : {
				byte[] noteID = new byte[16];
				rawData.get(noteID);
				int load = byteToInt(rawData.get());
				publisher.processNoteResponse(new Int128(noteID), load);
				break;
			}
			
			case KADEMLIA_SEARCH_REQ : {
				byte targetID[] = new byte[16];
				rawData.get(targetID);
				boolean sourceSearch = false;
				if (rawData.limit() == 17)
					if (rawData.get() == 1)
						sourceSearch = true;
				Collection<Source> source_list;
	
				if (sourceSearch)
					source_list = indexer.getFileSources(new Int128(targetID));
				else
					source_list = indexer.getKeywordSources(new Int128(targetID));
				KadPacket response = PacketFactory.getSearchResPacket(new Int128(targetID), source_list);
				_network_manager.sendKadPacket(response, sender_address,sender_port);
				break;
			}
			
			case KADEMLIA_SEARCH_RES : {
				byte targetID[] = new byte[16];
				rawData.get(targetID);
				int resultCount = shortToInt(rawData.getShort());
	
				List<Source> sourceList = new ArrayList<Source>();
	
				for (int i = 0; i < resultCount; i++) {
					byte[] contactID = new byte[16];
					rawData.get(contactID);
					int tagCount = byteToInt(rawData.get());
					TagList tagList = new TagList();
					for (int k = 0; k < tagCount; k++) {
						try {
							Tag tag = TagScanner.scanTag(rawData);
							if (tag == null)
								continue;
	
							tagList.addTag(tag);
						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
					ClientID client_id = new ClientID(contactID);
					Source source = new Source(client_id, tagList);
					KadContact contact = routing_table.getContact(client_id);
					if (contact != null) {
						source.setUDPPort(contact.getUDPPort());
						source.setTCPPort(contact.getTCPPort());
					}
					sourceList.add(source);
				}
				search.processSearchResults(sender_address,new Int128(targetID), sourceList);
				break;
			}
			
			case KADEMLIA_FIREWALLED_REQ : {
				int tcpPort = shortToInt(rawData.getShort());
				firewallChecker.processFirewallRequest(sender_address,tcpPort,sender_port);
				break;
			}
			
			case KADEMLIA_FIREWALLED_RES : {
				byte[] address = new byte[4];
				rawData.get(address);
				firewallChecker.porcessFirewallResponse(packet.getAddress(),new IPAddress(address));
				break;
			}
			//Kad 2.0
			case KADEMLIA2_BOOTSTRAP_REQ : {
				bootStrap.processBootStrap2Req(sender_address,sender_port);
				break;
			}
			case KADEMLIA2_BOOTSTRAP_RES : {
				byte[] client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				ClientID clientID = new ClientID(client_id_raw);
				int tcpPort = shortToInt(rawData.getShort());
				byte version = rawData.get();
				int contactCount = shortToInt(rawData.getShort());
				List<KadContact> contact_list = new ArrayList<KadContact>();
				for (int i = 0; i < contactCount; i++) 
					contact_list.add(getContact(rawData));
				bootStrap.processBotStrap2Res(clientID, tcpPort, version,contact_list);
				break;
			}
			case KADEMLIA2_HELLO_REQ : {
				byte[] client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				ClientID clientID = new ClientID(client_id_raw);
				int tcpPort = shortToInt(rawData.getShort());
				byte version = rawData.get();
				byte tag_count = rawData.get();
				List<Tag> tag_list = new ArrayList<Tag>();
				for (byte i = 0; i < tag_count; i++) {
					Tag tag = TagScanner.scanTag(rawData);
					if (tag == null)
						throw new CorruptedPacketException();
					tag_list.add(tag);
				}
	
				KadContact contact = routing_table.getContact(clientID);
				if (contact != null) {
					contact.setTCPPort(tcpPort);
					contact.setUDPPort(packet.getAddress().getPort());
					contact.setVersion(version);
				}
	
				KadPacket response = PacketFactory.getHelloRes2Packet(TagList.EMPTY_TAG_LIST);
				_network_manager.sendKadPacket(response, sender_address,sender_port);
				break;
			}
			case KADEMLIA2_HELLO_RES : {
				byte[] client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				ClientID clientID = new ClientID(client_id_raw);
				int tcpPort = shortToInt(rawData.getShort());
				byte version = rawData.get();
				byte tag_count = rawData.get();
				List<Tag> tag_list = new ArrayList<Tag>();
	
				for (byte i = 0; i < tag_count; i++) {
					Tag tag = TagScanner.scanTag(rawData);
					if (tag == null)
						throw new CorruptedPacketException();
					tag_list.add(tag);
				}
	
				KadContact contact = routing_table.getContact(clientID);
				if (contact != null) {
					contact.setTCPPort(tcpPort);
					contact.setVersion(version);
				}
				// ignore message if contact is not in routing table
				/*
				 * ContactAddress address = new ContactAddress(new
				 * IPAddress(packet.getSender()),packet.getSender().getPort());
				 * KadContact add_contact = new KadContact(clientID, address,
				 * tcpPort,version, null);
				 * routing_table.addContact(add_contact);
				 * 
				 * add_contact.setConnected(true);
				 */
				
				if (bootStrap.isStarted())
					bootStrap.processHello2Res(sender_address, sender_port);
				routing_table.processHello2Res(sender_address, sender_port);
				break;
			}
			case KADEMLIA2_REQ : {
				byte type = rawData.get();
	
				byte[] client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				ClientID targetClientID = new ClientID(client_id_raw);
	
				client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				ClientID receiverClientID = new ClientID(client_id_raw);
				RequestType requestType = RequestType.FIND_VALUE;
				switch (type) {
				case JKadConstants.FIND_VALUE:
					requestType = RequestType.FIND_VALUE;
					break;
	
				case JKadConstants.STORE:
					requestType = RequestType.STORE;
					break;
	
				case JKadConstants.FIND_NODE:
					requestType = RequestType.FIND_NODE;
					break;
				}
				lookup.processRequest(packet.getAddress(), requestType,targetClientID, receiverClientID, true);
				break;
			}
			case KADEMLIA2_RES : {
				byte[] client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				int contactCount = byteToInt(rawData.get());
				List<KadContact> contact_list = new ArrayList<KadContact>();
				for (int i = 0; i < contactCount; i++) {
					contact_list.add(getContact(rawData));
				}
				lookup.processResponse(packet.getAddress(), new ClientID(client_id_raw), contact_list);
				break;
			}
			case KADEMLIA2_PUBLISH_KEY_REQ : {
				byte[] client_id = new byte[16];
				rawData.get(client_id);
				ClientID clientID = new ClientID(client_id);
				int count = rawData.getShort();
				int load = 0;
				for (int i = 0; i < count; i++) {
					byte[] hash = new byte[16];
					rawData.get(hash);
					byte tagCount = rawData.get();
					TagList tag_list = new TagList();
					for (int j = 0; j < tagCount; j++) {
						Tag tag = TagScanner.scanTag(rawData);
						if (tag == null)
							throw new CorruptedPacketException();
						tag_list.addTag(tag);
					}
					Source source = new Source(clientID, tag_list);
					source.setAddress(new IPAddress(packet.getAddress()));
					source.setUDPPort(packet.getAddress().getPort());
	
					KadContact contact = routing_table.getContact(clientID);
					if (contact != null)
						source.setTCPPort(contact.getTCPPort());
					load = indexer.addKeywordSource(new Int128(hash), source);
				}
				KadPacket response = PacketFactory.getPublishRes2Packet(clientID, load);
				_network_manager.sendKadPacket(response, sender_address,sender_port);
				break;
			}
			case KADEMLIA2_PUBLISH_RES : {
				byte targetID[] = new byte[16];
				rawData.get(targetID);
				int load = byteToInt(rawData.get());
				publisher.processPublishResponse(new ClientID(targetID), load);
				break;
			}
			
			case KADEMLIA2_PUBLISH_SOURCE_REQ : {
				byte[] client_id = new byte[16];
				byte[] source_id = new byte[16];
	
				rawData.get(client_id);
				rawData.get(source_id);
	
				int tagCount = rawData.get();
				TagList tag_list = new TagList();
				for (int i = 0; i < tagCount; i++) {
					Tag tag = TagScanner.scanTag(rawData);
					if (tag == null)
						throw new CorruptedPacketException();
					tag_list.addTag(tag);
				}
				Source source = new Source(new ClientID(client_id), tag_list);
				source.setAddress(new IPAddress(packet.getAddress()));
				source.setUDPPort(packet.getAddress().getPort());
	
				KadContact contact = routing_table.getContact(new ClientID(client_id));
				if (contact != null)
					source.setTCPPort(contact.getTCPPort());
	
				int load = indexer.addFileSource(new Int128(source_id), source);
	
				KadPacket response = PacketFactory.getPublishRes2Packet(new ClientID(client_id), load);
				_network_manager.sendKadPacket(response, sender_address,sender_port);
				break;
			}
			
			case KADEMLIA2_PUBLISH_NOTES_REQ : {
				byte[] noteID = new byte[16];
				rawData.get(noteID);
				byte[] publisherID = new byte[16];
				rawData.get(publisherID);
				int tagCount = byteToInt(rawData.get());
				TagList tagList = new TagList();
				for (int i = 0; i < tagCount; i++) {
					Tag tag = TagScanner.scanTag(rawData);
					tagList.addTag(tag);
				}
				ClientID publisher_id = new ClientID(publisherID);
				Source source = new Source(publisher_id, tagList);
				source.setAddress(new IPAddress(packet.getAddress()));
				source.setUDPPort(packet.getAddress().getPort());
	
				KadContact contact = routing_table.getContact(publisher_id);
				if (contact != null)
					source.setTCPPort(contact.getTCPPort());
	
				int load = indexer.addNoteSource(new Int128(noteID), source);
				KadPacket response = PacketFactory.getPublishRes2Packet(clientID, load);
				_network_manager.sendKadPacket(response, sender_address,sender_port);
				break;
			}
			
			case KADEMLIA2_SEARCH_KEY_REQ: {
				byte targetID[] = new byte[16];
				rawData.get(targetID);
				Collection<Source> source_list;
	
				source_list = indexer.getKeywordSources(new Int128(targetID));
				KadPacket response = PacketFactory.getSearchRes2Packet(new Int128(targetID), source_list);
				_network_manager.sendKadPacket(response, sender_address,sender_port);
				break;
			}
			
			case KADEMLIA2_SEARCH_SOURCE_REQ : {
				byte targetID[] = new byte[16];
				rawData.get(targetID);
				short start_pos;
				start_pos = rawData.getShort();
				long fileSize = rawData.getLong();
				Collection<Source> source_list;
	
				source_list = indexer.getFileSources(new Int128(targetID),start_pos, fileSize);
				KadPacket response = PacketFactory.getSearchRes2Packet(new Int128(targetID), source_list);
				_network_manager.sendKadPacket(response, sender_address,sender_port);
				break;
			}
			
			case KADEMLIA2_SEARCH_NOTES_REQ : {
				byte targetID[] = new byte[16];
				rawData.get(targetID);
				long fileSize = rawData.getLong();
				Collection<Source> source_list;
	
				source_list = indexer.getNoteSources(new Int128(targetID),fileSize);
				KadPacket response = PacketFactory.getSearchRes2Packet(new Int128(targetID), source_list);
				_network_manager.sendKadPacket(response, sender_address,sender_port);
				break;
			}
			
			case KADEMLIA2_SEARCH_RES : {
				byte senderID[] = new byte[16];
				rawData.get(senderID);
				byte targetID[] = new byte[16];
				rawData.get(targetID);
				int resultCount = shortToInt(rawData.getShort());
	
				List<Source> sourceList = new ArrayList<Source>();
	
				for (int i = 0; i < resultCount; i++) {
					byte[] contactID = new byte[16];
					rawData.get(contactID);
					int tagCount = byteToInt(rawData.get());
					TagList tagList = new TagList();
					for (int k = 0; k < tagCount; k++) {
						try {
							Tag tag = TagScanner.scanTag(rawData);
							if (tag == null)
								continue;
	
							tagList.addTag(tag);
						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
					ClientID client_id = new ClientID(contactID);
					Source source = new Source(client_id, tagList);
					KadContact contact = routing_table.getContact(client_id);
					if (contact != null) {
						source.setUDPPort(contact.getUDPPort());
						source.setTCPPort(contact.getTCPPort());
					}
					sourceList.add(source);
				}
				search.processSearchResults(sender_address,new Int128(targetID), sourceList);
				break;
			}
			case KADEMLIA_FINDBUDDY_REQ : {
				byte[] receiver_id = new byte[16];
				byte[] sender_id = new byte[16];
				short tcp_port;
	
				rawData.get(receiver_id);
				rawData.get(sender_id);
				tcp_port = rawData.getShort();
	
				Buddy buddy = Buddy.getInstance();
				//Logger.getSingleton().logMessage("Buddy request : " + packet.getAddress());
				
				if (!buddy.isJKadUsedAsBuddy()) {
					ClientID receiverID = new ClientID(receiver_id);
					Int128 senderID = new Int128(sender_id);
	
					if (getClientID().equals(receiverID)) {
						buddy.setClientID(senderID);
						buddy.setAddress(new IPAddress(packet.getAddress()));
						buddy.setTCPPort(tcp_port);
						buddy.setUDPPort(org.jmule.core.utils.Convert.intToShort(packet.getAddress().getPort()));
	
						buddy.setKadIsUsedAsBuddy(true);
	
						//Logger.getSingleton().logMessage("New buddy : " + buddy.getAddress() + " TCP : "+ buddy.getTCPPort() + " UDP : "+ buddy.getUDPPort());
	
						ConfigurationManager configManager = ConfigurationManagerSingleton.getInstance();
						KadPacket response;
						try {
							response = PacketFactory.getBuddyResPacket(new ClientID(sender_id), getClientID(),(short) configManager.getTCP());
							_network_manager.sendKadPacket(response,sender_address, sender_port);
						} catch (ConfigurationManagerException e) {
							e.printStackTrace();
						}
	
					}
	
				}
				break;
			}
			
			case KADEMLIA_CALLBACK_REQ : {
				Buddy buddy = Buddy.getInstance();
				if (buddy.isJKadUsedAsBuddy()) {
					//Logger.getSingleton().logMessage("KAD callback request");
					byte[] cid = new byte[16];
					rawData.get(cid);
					final Int128 clientID = new Int128(cid);
					byte[] fid = new byte[16];
					rawData.get(fid);
					final FileHash fileHash = new FileHash(fid);
					final short port = rawData.getShort();
					final IPAddress ipAddress = new IPAddress(packet.getAddress());
					// final Peer peer = new Peer(buddy.getAddress().toString(),
					// buddy.getTCPPort(), PeerSource.KAD);
					final Peer peer = PeerManagerSingleton.getInstance().newPeer(buddy.getAddress().toString(),buddy.getTCPPort(), PeerSource.KAD);
					//Logger.getSingleton().logMessage("KAD callback request, Peer : " + peer);
					JMRunnable task = new JMRunnable() {
						public void JMRun() {
							try {
								_network_manager.addPeer(peer.getIP(), peer.getPort());
							} catch (NetworkManagerException e1) {
								e1.printStackTrace();
							}
							long counter = 0;
							while (!peer.isConnected()) {
								counter++;
								if (counter == 5) {
									//Logger.getSingleton().logMessage("KAD callback request, Peer : failed to connect");
									return;
								}
								try {
									Thread.sleep(5000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							//Logger.getSingleton().logMessage("KAD callback request, Peer : connected & send packet to :  " + peer);
							_network_manager.sendCallBackRequest(peer.getIP(),peer.getPort(), clientID, fileHash,ipAddress, port);
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							//Logger.getSingleton().logMessage("KAD callback request : Disconnecting from  "+ peer);
							_network_manager.disconnectPeer(peer.getIP(), peer.getPort());

						}
					};
					new Thread(task).start();
				}
				break;
			}
			default: {
				unknown_packet = true;
			}
			}

		} catch (Throwable cause) {
			cause.printStackTrace();
		}

		if (unknown_packet)
			throw new UnknownPacketOPCodeException(packetOPCode);

	}

	public IPAddress getIPAddress() {
		return firewallChecker.getMyIPAddress();
	}

	public ClientID getClientID() {
		return clientID;
	}

	public JKadStatus getStatus() {
		return status;
	}

	public boolean isConnected() {
		return status == CONNECTED;
	}

	public boolean isDisconnected() {
		return status == DISCONNECTED;
	}

	public boolean isConnecting() {
		return status == CONNECTING;
	}

	public void addJKadListener(JKadListener listener) {
		listener_list.add(listener);
	}

	public void removeJKadListener(JKadListener listener) {
		listener_list.remove(listener);
	}

	private void notifyListeners(JKadStatus newStatus) {
		for (JKadListener listener : listener_list)
			if (newStatus == JKadStatus.CONNECTED)
				listener.JKadIsConnected();
			else if (newStatus == JKadStatus.CONNECTING)
				listener.JKadIsConnecting();
			else if (newStatus == JKadStatus.DISCONNECTED)
				listener.JKadIsDisconnected();
	}

	public RoutingTable getRoutingTable() {
		return routing_table;
	}

	public BootStrap getBootStrap() {
		return bootStrap;
	}

	public FirewallChecker getFirewallChecker() {
		return firewallChecker;
	}

	public Indexer getIndexer() {
		return indexer;
	}

	public Lookup getLookup() {
		return lookup;
	}
	
	public Search getSearch() {
		return search;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	private KadContact getContact(ByteBuffer packetData) {
		byte[] client_id_raw = new byte[16];
		packetData.get(client_id_raw);
		byte[] address = new byte[4];
		packetData.get(address);
		int udp_port = shortToInt(packetData.getShort());
		int tcp_port = shortToInt(packetData.getShort());
		byte client_version = packetData.get();
		return new KadContact(new ClientID(client_id_raw), new ContactAddress(
				address, udp_port), tcp_port, client_version, null, false);
	}
	
	public String toString() {
		
		/*private RoutingTable routing_table = null;
		private Indexer indexer = null;
		private FirewallChecker firewallChecker = null;
		private BootStrap bootStrap = null;
		private Lookup lookup = null;
		private Search search = null;
		private Publisher publisher = null;*/
		
		String result = "";
		/*result += "\nJKad status : " + status;
		//result += "\nRouting table : " + routing_table;
		result += "\nLookup : " + lookup;
		result += "\nSearch : " + search;*/
		
		result += publisher;
		return result;
	}
	
	
	
}