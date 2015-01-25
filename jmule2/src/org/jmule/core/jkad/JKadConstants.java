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

import static org.jmule.core.configmanager.ConfigurationManager.SETTINGS_DIR;

import java.io.File;


/**
 * Created on Dec 28, 2008
 * @author binary256
 * @version $Revision: 1.24 $
 * Last changed by $Author: binary255 $ on $Date: 2010/10/23 05:38:50 $
 */
public class JKadConstants {
	public static final byte NODES_DAT_1					= 0x01;
	public static final byte NODES_DAT_2					= 0x02;
	public static final byte NODES_DAT_VERSION				= NODES_DAT_2;
	
	public static final String NODES_DAT					= SETTINGS_DIR + File.separator + "nodes.dat";
	
	public static final byte SRC_INDEX_VERSION				= 0x09;
	
	public static final String KEY_INDEX_DAT				= SETTINGS_DIR + File.separator + "key_index.dat";
	public static final String SRC_INDEX_DAT				= SETTINGS_DIR + File.separator + "src_index.dat";
	public static final String NOTE_INDEX_DAT				= SETTINGS_DIR + File.separator + "note_index.dat";
	
	public static final int MAX_UDP_PACKET					= 65534;
	public static final int MIN_UNPACKET_SIZE				= 100;
	public static final int FIREWALL_CHECK_INTERVAL			= 1000 * 60 * 60;
	public static final int FIREWALLED_STATUS_CHANGE_INTERVAL = 1000 * 5;
	public static final int FIREWALL_CHECK_CONTACTS			= 20;
	
	public static final byte KAD_VERSION 					= 0x05; // KADEMLIA_VERSION5_48a	0x05 // -0.48a

	public static final int MIN_CONTACTS_TO_SEND_BOOTSTRAP	= 200; 
	public static final int BOOTSTRAP_CONTACTS				= 20;
	public static final int BOOTSTRAP_STOP_CONTACTS			= 50;
	public static final int BOOTSTRAP_CHECK_INTERVAL		= 5000;
	public static final int BOOTSTRAP_REMOVE_TIME			= 10000;// 60000
	
	public static final int SEARCH_CONTACTS					= 2;
	public static final int INITIAL_SEARCH_CONTACT_COUNT	= 50;
	public static final int PUBLISH_KEYWORD_CONTACT_COUNT	= 20;
		
	public static final int  INITIAL_LOOKUP_CONTACTS 		= 50;
	public static final long LOOKUP_TASK_CHECK_INTERVAL		= 5000;
	public static final long LOOKUP_TASK_DEFAULT_TIMEOUT	= 15000;
	
	public static final long LOOKUP_CONTACT_CHECK_INTERVAL	= 5000;
	public static final long LOOKUP_CONTACT_TIMEOUT			= 1000 * 10;
	
	public static final long LOOKUP_NODE_TIMEOUT			= 1000 * 45;
	
	public static final long LOOKUP_SEARCH_SOURCE_TIMEOUT	= 1000 * 60;
	public static final long LOOKUP_SEARCH_KEYWORD_TIMEOUT	= 1000 * 60;
	public static final long LOOKUP_SEARCH_NOTE_TIMEOUT		= 1000 * 60;
	
	public static final long LOOKUP_STORE_FILE_TIMEOUT		= 1000 * 140;
	public static final long LOOKUP_STORE_KEYWORD_TIMEOUT	= 1000 * 140;
	public static final long LOOKUP_STORE_NOTE_TIMEOUT		= 1000 * 100;
	
	public static final long MAX_LOOKUP_RUNNING_TIME		= 1000 * 60 * 5;
	
	public static final int LOOKUP_NODE_CONTACTS			= 11;
	public static final int LOOKUP_VALUE_CONTACTS			= 2;
	public static final int LOOKUP_STORE_CONTACTS			= 4;
	
	public static final int MAX_PUBLISH_SOURCES				= 30; 
	public static final int MAX_PUBLISH_KEYWORDS			= 30;
	public static final int MAX_PUBLISH_NOTES				= 30; 
	
	public static final int MAX_KEYWORD_SEARCH_RESULTS 		= 1000;//300
	public static final int MAX_SOURCES_SEARCH_RESULTS 		= 300;
	public static final int MAX_NOTES_SEARCH_RESULTS 		= 50;
	
	public static final int  MAX_CONTACTS 							= 5000;
	public static final long ROUTING_TABLE_HELLO_SEND_INTERVAL		= 1000 * 60 * 2; //60
	public static final long ROUTING_TABLE_FAKE_LOOKUP_INTERVAL		= 1000 * 60 * 3; //60
	public static final int  CONTACTS_SEND_HELLO					= 5;
	public static final long ROUTING_TABLE_CHECK_INTERVAL			= 1000 * 30; //30
	public static final long ROUTING_TABLE_CONTACTS_CHECK_INTERVAL	= 1000 * 60; 
	public static final long ROUTING_TABLE_CONTACT_TIMEOUT  		= 1000 * 60 * 2;
	public static final long ROUTING_TABLE_CONTACT_ACCEP_TIME 		= 1000 * 60 * 2 - 1;
	public static final long ROUTING_TABLE_CONTACT_IGNORE_TIME 		= 1000 * 60 * 2; 
	public static final long ROUTING_TABLE_SAVE_INTERVAL			= 1000 * 60;
	public static final long ROUTING_TABLE_DIFICIT_CONTACTS 		= 500;//200
	public static final long ROUTING_TABLE_DIFICIT_CONTACTS_STOP = ROUTING_TABLE_DIFICIT_CONTACTS + 100;
	
	public static final int ROUTING_TABLE_MAINTENANCE_CONTACTS			= 10; //3
	public static final int ROUTING_TABLE_MAX_MAINTENANCE_CONTACTS		= 50;
	
	public static final int INDEX_MAX_KEYWORDS				= 6000;//60000
	public static final int KEYWORD_MAX_SOURCES_FOR_NEW_SOURCES	= 4500;//45000
	public static final int KEYWORD_MAX_SOURCES				= 5000;//50000
	
	public static final int INDEX_MAX_SOURCES				= 6000;//60000
	public static final int SOURCES_MAX_PER_FILE			= 300;
	
	public static final int INDEX_MAX_NOTES					= 6000;//60000
	public static final int NOTES_MAX_PER_FILE				= 300; // 50
	
	public static final int INDEXER_SAVE_DATA_INTERVAL		= 1000 * 60 * 2;
	public static final int INDEXER_CLEAN_DATA_INTERVAL		= 1000 * 60 ;
	
	public static final long TIME_24_HOURS 					=  1000 * 60 * 60 * 24;
	public static final long TIME_5_HOURS 					=  1000 * 60 * 60 * 5;
		
	public static final long PUBLISH_KEYWORD_CHECK_INTERVAL	 = 1000 * 6;
	public static final long PUBLISH_KEYWORD_SCAN_INTERVAL	 = 1000 * 10;
	public static final long PUBLISHER_PUBLISH_CHECK_INTERVAL= 5000;
	public static final int PUBLISH_KEYWORD_ON_ITERATION	 = 150;
	public static final int PUBLISH_KEYWORD_IN_PACKET		 = 50;
	
	public static final long PUBLISH_SOURCE_INTERVAL		 = 1000 * 10;
	public static final long PUBLISH_NOTE_INTERVAL			 = 1000 * 10;
	
	public static final long INDEXTER_MAX_LOAD_TO_NOT_PUBLISH = 60;
	
	public static final int KAD_SOURCES_SEARCH_INTERVAL     =  1000 * 60 * 1;
	
	public static final long ITERATION_MAX_PUBLISH_FILES 	= 2;
	
	public static final int MAX_CONCURRENT_PUBLISH_KEYWORDS = 2;
	public static final int MAX_CONCURRENT_PUBLISH_FILES 	= 3;
	public static final int MAX_CONCURRENT_PUBLISH_NOTES 	= 1;
	
	public static final int K 								= 10;//10
	public static final int ALPHA 							= 3;//3;

	public static final long TOLERANCE_ZONE  				= 16777216;
	
	
	public static final byte PROTO_KAD_UDP 					= (byte) 0xE4;
	public static final byte PROTO_KAD_COMPRESSED_UDP		= (byte) 0xE5;
	
	public static final byte KADEMLIA_BOOTSTRAP_REQ			= (byte) 0x00;
	public static final byte KADEMLIA_BOOTSTRAP_RES			= (byte) 0x08;
	
	public static final byte KADEMLIA_HELLO_REQ				= (byte) 0x10;
	public static final byte KADEMLIA_HELLO_RES				= (byte) 0x18;
	
	public static final byte KADEMLIA_FIREWALLED_REQ		= (byte) 0x50;
	public static final byte KADEMLIA_FIREWALLED_RES		= (byte) 0x58;
	
	public static final byte KADEMLIA_CALLBACK_REQ			= (byte) 0x52;
	
	public static final byte KADEMLIA_REQ					= (byte) 0x20;
	public static final byte KADEMLIA_RES					= (byte) 0x28;

	public static final byte KADEMLIA_PUBLISH_REQ			= (byte) 0x40;
	public static final byte KADEMLIA_PUBLISH_RES			= (byte) 0x48;
	
	public static final byte KADEMLIA_SEARCH_REQ			= (byte) 0x30;
	public static final byte KADEMLIA_SEARCH_RES			= (byte) 0x38;
	
	public static final byte KADEMLIA_SEARCH_NOTES_REQ		= (byte) 0x32;
	public static final byte KADEMLIA_SEARCH_NOTES_RES		= (byte) 0x3A;
	
	public static final byte KADEMLIA_FINDBUDDY_REQ			= (byte) 0x51;
	public static final byte KADEMLIA_FINDBUDDY_RES			= (byte) 0x5A;
	
	public static final byte KADEMLIA_PUBLISH_NOTES_REQ		= (byte) 0x42;
	public static final byte KADEMLIA_PUBLISH_NOTES_RES		= (byte) 0x4A; 
	
	public static final byte KADEMLIA2_BOOTSTRAP_REQ		= (byte) 0x01;
	public static final byte KADEMLIA2_BOOTSTRAP_RES		= (byte) 0x09;

	public static final byte KADEMLIA2_REQ					= (byte) 0x21;
	public static final byte KADEMLIA2_RES					= (byte) 0x29;
	
	public static final byte KADEMLIA2_HELLO_REQ			= (byte) 0x11;
	public static final byte KADEMLIA2_HELLO_RES 			= (byte) 0x19;

	public static final byte KADEMLIA2_HELLO_RES_ACK		= (byte) 0x22;
	
	public static final byte KADEMLIA_FIREWALLED2_REQ       = (byte) 0x53;
	
	public static final byte KADEMLIA2_FIREWALLUDP			= (byte) 0x62;
	
	public static final byte KADEMLIA2_SEARCH_KEY_REQ		= (byte) 0x33;
	public static final byte KADEMLIA2_SEARCH_SOURCE_REQ	= (byte) 0x34;
	public static final byte KADEMLIA2_SEARCH_NOTES_REQ		= (byte) 0x35;
	
	public static final byte KADEMLIA2_SEARCH_RES			= (byte) 0x3B;
	
	public static final byte KADEMLIA2_PUBLISH_KEY_REQ		= (byte) 0x43;
	public static final byte KADEMLIA2_PUBLISH_SOURCE_REQ	= (byte) 0x44;
	public static final byte KADEMLIA2_PUBLISH_NOTES_REQ	= (byte) 0x45;
	
	public static final byte KADEMLIA2_PUBLISH_RES			= (byte) 0x4B;

	public static final byte KADEMLIA2_PUBLISH_RES_ACK		= (byte) 0x4C;
	
	public static final byte KADEMLIA2_PING					= (byte) 0x60;
	public static final byte KADEMLIA2_PONG					= (byte) 0x61;
	
	public static final byte FIND_VALUE 					= (byte) 0x02;
	public static final byte STORE      					= (byte) 0x04;
	public static final byte FIND_NODE						= (byte) 0x0B;
	
	public static final byte ContactType0					= (byte) 0x00;
	public static final byte ContactType1					= (byte) 0x01;
	public static final byte ContactType2					= (byte) 0x02;
	public static final byte ContactType3					= (byte) 0x03;
	public static final byte ContactType4					= (byte) 0x04;
	
	public static final byte[] TAG_SOURCETYPE				=  new byte[] { (byte)0xFF };
	
	public static final byte[] TAG_FILENAME					=  new byte[] { (byte)0x01 };
	public static final byte[] TAG_FILESIZE					=  new byte[] { (byte)0x02 };
	public static final byte[] TAG_SOURCECOUNT				=  new byte[] { (byte)0x15 };
	public static final byte[] TAG_FILERATING				=  new byte[] { (byte)0xF7 };
	public static final byte[] TAG_DESCRIPTION				=  new byte[] { (byte)0x0B };
	
	public static final byte[] TAG_SOURCEPORT				=  new byte[] { (byte)0xFD };
	public static final byte[] TAG_SOURCEUPORT				=  new byte[] { (byte)0xFC };
	public static final byte[] TAG_SOURCEIP					=  new byte[] { (byte)0xFE };
	
	public static final byte[] TAG_SERVERIP					=  new byte[] { (byte)0xFB };
	public static final byte[] TAG_SERVERPORT				=  new byte[] { (byte)0xFA };
	
	public enum IntTagSize {
		Int64,
		Int32,
		Int16,
		Int8
	}
	
	public enum ContactType {
		Active2MoreHours { // longer than 2 hours active
			public byte toByte() {
				return ContactType0;
			}
			
			public String toString() {
				return "Active2MoreHours";
			}

			public long timeRequired() {
				return 1000 * 60 * 60 * 3;
			}
		}, 
		Active1Hour { // active since 1 -2 hours
			public byte toByte() {
				return ContactType1;
			}
			public String toString() {
				return "Active1Hour";
			}
			public long timeRequired() {
				return 1000 * 60 * 60 * 2;
			}
		},
		Active { // less then 1 hour active
			public byte toByte() {
				return ContactType2;
			}
			public String toString() {
				return "Active";
			}
			public long timeRequired() {
				return 1000 * 60 * 60 * 1;
			}
		},
		JustAdded { // Just created
			public byte toByte() {
				return ContactType3;
			}
			public String toString() {
				return "JustAdded";
			}
			
			public long timeRequired() {
				return 1;
			}
			
		},
		ScheduledForRemoval { // Preview for its deletion
			public byte toByte() {
				return ContactType2;
			}
			public String toString() {
				return "ScheduledForRemoval";
			}
			public long timeRequired() {
				return 0;
			}
		};
		
		public abstract byte toByte();
		public abstract long timeRequired();
	}
	
	public enum RequestType {
		FIND_VALUE{
			public byte toByte() {
				return JKadConstants.FIND_VALUE;
			}
		},
		FIND_NODE{
			public byte toByte() {
				return JKadConstants.FIND_NODE;
			}
		},
		STORE {
			public byte toByte() {
				return JKadConstants.STORE;
			}
		};
		
		public abstract byte toByte();
	}
	
}
