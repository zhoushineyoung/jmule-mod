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
package org.jmule.core.edonkey.packet;

import static org.jmule.core.edonkey.ED2KConstants.DefaultJMuleFeatures;
import static org.jmule.core.edonkey.ED2KConstants.ET_COMMENTS;
import static org.jmule.core.edonkey.ED2KConstants.ET_COMPRESSION;
import static org.jmule.core.edonkey.ED2KConstants.ET_EXTENDEDREQUEST;
import static org.jmule.core.edonkey.ED2KConstants.ET_FEATURES;
import static org.jmule.core.edonkey.ED2KConstants.ET_SOURCEEXCHANGE;
import static org.jmule.core.edonkey.ED2KConstants.ET_UDPPORT;
import static org.jmule.core.edonkey.ED2KConstants.ET_UDPVER;
import static org.jmule.core.edonkey.ED2KConstants.OP_AICHFILEHASHREQ;
import static org.jmule.core.edonkey.ED2KConstants.OP_ANSWERSOURCES;
import static org.jmule.core.edonkey.ED2KConstants.OP_EMULEHELLOANSWER;
import static org.jmule.core.edonkey.ED2KConstants.OP_EMULE_HELLO;
import static org.jmule.core.edonkey.ED2KConstants.OP_EMULE_QUEUERANKING;
import static org.jmule.core.edonkey.ED2KConstants.OP_END_OF_DOWNLOAD;
import static org.jmule.core.edonkey.ED2KConstants.OP_FILENAMEREQUEST;
import static org.jmule.core.edonkey.ED2KConstants.OP_FILEREQANSNOFILE;
import static org.jmule.core.edonkey.ED2KConstants.OP_FILEREQANSWER;
import static org.jmule.core.edonkey.ED2KConstants.OP_FILESTATREQ;
import static org.jmule.core.edonkey.ED2KConstants.OP_FILESTATUS;
import static org.jmule.core.edonkey.ED2KConstants.OP_GETSERVERLIST;
import static org.jmule.core.edonkey.ED2KConstants.OP_GETSOURCES;
import static org.jmule.core.edonkey.ED2KConstants.OP_HASHSETANSWER;
import static org.jmule.core.edonkey.ED2KConstants.OP_HASHSETREQUEST;
import static org.jmule.core.edonkey.ED2KConstants.OP_KAD_CALLBACK;
import static org.jmule.core.edonkey.ED2KConstants.OP_LOGINREQUEST;
import static org.jmule.core.edonkey.ED2KConstants.OP_MESSAGE;
import static org.jmule.core.edonkey.ED2KConstants.OP_MULTIPACKET;
import static org.jmule.core.edonkey.ED2KConstants.OP_MULTIPACKETANSWER;
import static org.jmule.core.edonkey.ED2KConstants.OP_MULTIPACKET_EXT;
import static org.jmule.core.edonkey.ED2KConstants.OP_OFFERFILES;
import static org.jmule.core.edonkey.ED2KConstants.OP_PEERHELLO;
import static org.jmule.core.edonkey.ED2KConstants.OP_PEERHELLOANSWER;
import static org.jmule.core.edonkey.ED2KConstants.OP_PUBLICKEY;
import static org.jmule.core.edonkey.ED2KConstants.OP_REQUESTPARTS;
import static org.jmule.core.edonkey.ED2KConstants.OP_REQUESTSOURCES;
import static org.jmule.core.edonkey.ED2KConstants.OP_SEARCHREQUEST;
import static org.jmule.core.edonkey.ED2KConstants.OP_SECIDENTSTATE;
import static org.jmule.core.edonkey.ED2KConstants.OP_SENDINGPART;
import static org.jmule.core.edonkey.ED2KConstants.OP_SIGNATURE;
import static org.jmule.core.edonkey.ED2KConstants.OP_SLOTGIVEN;
import static org.jmule.core.edonkey.ED2KConstants.OP_SLOTRELEASE;
import static org.jmule.core.edonkey.ED2KConstants.OP_SLOTREQUEST;
import static org.jmule.core.edonkey.ED2KConstants.PACKET_CALLBACKREQUEST;
import static org.jmule.core.edonkey.ED2KConstants.PEER_FEATURES_REPORTED_TO_SERVER;
import static org.jmule.core.edonkey.ED2KConstants.PROTOCOL_VERSION;
import static org.jmule.core.edonkey.ED2KConstants.PROTO_EDONKEY_TCP;
import static org.jmule.core.edonkey.ED2KConstants.PROTO_EMULE_EXTENDED_TCP;
import static org.jmule.core.edonkey.ED2KConstants.ServerSoftwareVersion;
import static org.jmule.core.edonkey.ED2KConstants.TAG_NAME_CLIENTVER;
import static org.jmule.core.edonkey.ED2KConstants.TAG_NAME_MISC_OPTIONS1;
import static org.jmule.core.edonkey.ED2KConstants.TAG_NAME_MISC_OPTIONS2;
import static org.jmule.core.edonkey.ED2KConstants.TAG_NAME_NAME;
import static org.jmule.core.edonkey.ED2KConstants.TAG_NAME_PROTOCOLVERSION;
import static org.jmule.core.edonkey.ED2KConstants.TAG_NAME_SERVER_FLAGS;
import static org.jmule.core.edonkey.ED2KConstants.TAG_NAME_UDP_PORT;
import static org.jmule.core.edonkey.ED2KConstants.TAG_NAME_UDP_PORT_PEER;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jmule.core.JMException;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.ED2KConstants;
import org.jmule.core.edonkey.ED2KConstants.PeerFeatures;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.edonkey.packet.tag.IntTag;
import org.jmule.core.edonkey.packet.tag.StringTag;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.utils.Utils;
import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.searchmanager.SearchQuery;
import org.jmule.core.searchmanager.tree.NodeValue;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.sharingmanager.JMuleBitSet;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.uploadmanager.FileChunkRequest;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.26 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/07/31 16:00:42 $$
 */
public class PacketFactory {
	
	/**
	 * Server login packet.
	 * <table cellpadding="0" border="1" cellspacing="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>- </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including the header and size fileds</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x01</td>
	 *       <td>The value of the OP_LOGINREQUEST opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>User Hash</td>
	 *       <td>16</td>
	 *       <td>-</td>
	 *       <td>- </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Client ID</td>
	 *       <td>4</td>
	 *       <td>0</td>
	 *       <td>The client ID that is sent on first connection is usually zero</td>
	 *     </tr>
	 *     <tr>
	 *       <td>TCP Port</td>
	 *       <td>2</td>
	 *       <td>4662</td>
	 *       <td>The TCP port used by the client, configurable</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Tag Count</td>
	 *       <td>4</td>
	 *       <td>4</td>
	 *       <td>The number of tags following in the message</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Name Tag</td>
	 *       <td>varies</td>
	 *       <td>NA</td>
	 *       <td>The user's nickname. The tag is a string tag and the tag name is an integer of value 0x1</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Version Tag</td>
	 *       <td>8</td>
	 *       <td>0x3C</td>
	 *       <td>The eDonkey version supported by the client. The tag is an integer tag and the tag name is an integer of value 0x11</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Port Tag</td>
	 *       <td>8</td>
	 *       <td>4662</td>
	 *       <td>The TCP port used by the client. The tag is an integer tag and the tag name is an integer of value 0x0F</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Flags Tag</td>
	 *       <td>8</td>
	 *       <td>0x01</td>
	 *       <td>The tag is an integer tag and the tag name is an integer of value 0x20</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static Packet getServerLoginPacket(UserHash userHash, int clientPort, String userNickName) throws JMException {
		
		TagList tagList = new TagList();
		tagList.addTag(new StringTag(TAG_NAME_NAME, userNickName));
		tagList.addTag(new IntTag(TAG_NAME_PROTOCOLVERSION,PROTOCOL_VERSION ));
		tagList.addTag(new IntTag(TAG_NAME_CLIENTVER, ServerSoftwareVersion));
		tagList.addTag(new IntTag(TAG_NAME_SERVER_FLAGS, PEER_FEATURES_REPORTED_TO_SERVER));
		tagList.addTag(new IntTag(TAG_NAME_UDP_PORT, ConfigurationManagerSingleton.getInstance().getUDP()));
	
		int tag_list_size = tagList.getByteSize();
		
		Packet packet = new Packet(16 + 4 + 2 + 4 + tag_list_size, PROTO_EDONKEY_TCP);
		packet.setCommand(OP_LOGINREQUEST);//Insert LOGIN_COMMAND
		packet.insertData(userHash.getUserHash());//Insert user Hash
		packet.insertData(new byte[]{0,0,0,0});//Insert default user ID
		packet.insertData((short)clientPort);//Insert user port

		packet.insertData(tagList.size());//Insert tag count
		for(Tag tag : tagList) {
			packet.insertData(tag.getAsByteBuffer());
		}
		
		return packet;

	}
	
	/**
	 * Create Server list request packet.
	 * <table cellpadding="0" border="1" cellspacing="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default Value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>&nbsp;</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>&nbsp;</td>
	 *       <td>The size of the message in bytes not including the header and size fields</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x14</td>
	 *       <td>The value of the OP_SERVERSTATUS opcode</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static Packet getGetServerListPacket(){
		Packet packet=new Packet(0, PROTO_EDONKEY_TCP);
		packet.setCommand(OP_GETSERVERLIST);
		return packet;
	}
	
	/**
	 * Create server search packet.
	 * <table cellpadding="0" border="1" cellspacing="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default Value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>&nbsp;</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including the header and size fields</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>16</td>
	 *       <td>The value of the OP_SEARCHREQUEST opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Parsed search string</td>
	 *       <td>varies</td>
	 *       <td>NA</td>
	 *       <td>The parsed search string format is described below</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File Type Constraint</td>
	 *       <td>varies</td>
	 *       <td>NA</td>
	 *       <td>Optional. A string constraint. The string values are one of ("Audio", "Video", "Pro" or "Image". The type field is 3 bytes: 0x1 0x0 0x3</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Min Size Constraint</td>
	 *       <td>varies</td>
	 *       <td>NA</td>
	 *       <td>Optional. An integer constraint. The file size is provided in mega bytes. The type field is 4 bytes : 0x1 0x1 0x0 0x2</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Max Size Constraint</td>
	 *       <td>varies</td>
	 *       <td>NA</td>
	 *       <td>Optional. An integer constraint. The file size is provided in mega bytes. The type field is 4 bytes : 0x2 0x1 0x0 0x2</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Availability Constraint</td>
	 *       <td>varies</td>
	 *       <td>NA</td>
	 *       <td>Optional. An integer constraint. Sets a lower limit on the number of clien that poses the searched file. The type field is 4 bytes: 0x1 0x1 0x0 0x15</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Filename Extension constrain</td>
	 *       <td>varies</td>
	 *       <td>NA</td>
	 *       <td>Optional. A string constraint. The type field is 3 bytes: 0x1 0x0 0x3</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static Packet getSearchPacket(SearchQuery query){
		List<byte[]> data = new ArrayList<byte[]>();
		int total_size = 0;
		List<NodeValue> nodes = query.getSearchTree().traverse();
		for(NodeValue value : nodes) {
			byte[] tmp = value.getBytes();
			data.add(tmp);
			total_size += tmp.length;
		}
		Packet packet=new Packet(total_size, PROTO_EDONKEY_TCP);
		
		packet.setCommand(OP_SEARCHREQUEST);
		
		for(byte[] b : data)
			packet.insertData(b);
		return packet;
	}

	
	/**
     * Create get sources packet.
     * @param fileHash hash of file to download
     * 
	 * <table cellpadding="0" border="1" cellspacing="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default Value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>&nbsp;</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including the header and size fields</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x58</td>
	 *       <td>The value of the OP_GETSOURCES opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File hash</td>
	 *       <td>16</td>
	 *       <td>NA</td>
	 *       <td>The requested file hash</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File size</td>
	 *       <td>4</td>
	 *       <td>NA</td>
	 *       <td>The requested file size (lungdum 17.3)</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
     */
    public static Packet getSourcesRequestPacket(FileHash fileHash,long fileSize){
        Packet packet=new Packet(16+4, PROTO_EDONKEY_TCP);

        packet.setCommand(OP_GETSOURCES);
        packet.insertData(fileHash.getHash());
        packet.insertData(Convert.longToInt(fileSize));
        return packet;
    }
	
    /**
     * Call back request packet.
     * @param clientID
	 *
	 * <table cellpadding="0" border="1" cellspacing="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default Value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>&nbsp;</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including the header and size fields</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x1C</td>
	 *       <td>The value of the OP_CALLBACKREQUEST opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Client ID</td>
	 *       <td>4</td>
	 *       <td>NA</td>
	 *       <td>The ID of the client which is asked to call back</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
     */
    public static Packet getCallBackRequestPacket(ClientID clientID){
    	Packet packet = new Packet(4, PROTO_EDONKEY_TCP);
    	packet.setCommand(PACKET_CALLBACKREQUEST);
    	packet.insertData(clientID.getClientID());
    	return packet;
    }
	
    /**
     * Offer files packet.
	 * <table cellpadding="0" border="1" cellspacing="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default Value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>&nbsp;</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>&nbsp;</td>
	 *       <td>The size of the message in bytes not including the header and size fields</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x14</td>
	 *       <td>The value of the OP_SERVERSTATUS opcode</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
     */
    public static Packet getOfferFilesPacket(ClientID userID, List<SharedFile> sharedFiles) throws JMException  {
    	int data_length=0;
    	List<ByteBuffer> shared_files_data = new ArrayList<ByteBuffer>();
    	for(SharedFile sFile : sharedFiles) {
    		TagList tag_list = sFile.getTagList();
    		int tag_list_size = tag_list.getByteSize();
    		ByteBuffer buffer = Misc.getByteBuffer(16 + 4 + 2 + 4 + tag_list_size);
    		data_length += buffer.limit();
    		buffer.position(0);
    		buffer.put(sFile.getFileHash().getHash());
    		if (userID.isHighID()) {
    			buffer.put(userID.getClientID());
    			buffer.putShort((short)ConfigurationManagerSingleton.getInstance().getTCP());
    		}
    		else { 
    			buffer.putInt(0); 
    			buffer.putShort((short)0); 
    		}
    		buffer.putInt(tag_list.size());//Insert tag count
    		
    		for(Tag tag : tag_list) 
    			buffer.put(tag.getAsByteBuffer());
    		
    		shared_files_data.add(buffer);
    	}
    	
    	Packet packet = new Packet(4+data_length, PROTO_EDONKEY_TCP);
    	packet.setCommand(OP_OFFERFILES);
    	packet.insertData(sharedFiles.size());
    	
    	for(ByteBuffer buffer : shared_files_data)
    		packet.insertData(buffer.array());
    	
    	return packet;
    }
    
	 // =======================
	 // Peer<==>Peer
	 // =======================
	 

	/**
	 * Peer hello packet.
	 * @param userHash user hash
	 * @param myPort client port
	 * @param serverIP server IP address
	 * @param serverPort server port
	 * @param userName user name
	 * @return peer hello packet
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including the header and size fields</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x01</td>
	 *       <td>The value of the OP HELLO opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>User Hash size</td>
	 *       <td>1</td>
	 *       <td>16</td>
	 *       <td>The size of the user hash ﬁeld</td>
	 *     </tr>
	 *     <tr>
	 *       <td>User Hash</td>
	 *       <td>16</td>
	 *       <td>&nbsp;</td>
	 *       <td>TBD</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Client ID</td>
	 *       <td>4</td>
	 *       <td>0</td>
	 *       <td>TBD</td>
	 *     </tr>
	 *     <tr>
	 *       <td>TCP Port</td>
	 *       <td>2</td>
	 *       <td>4662</td>
	 *       <td>The TCP port used by the client, conﬁgurable</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Tag Count</td>
	 *       <td>4</td>
	 *       <td>4</td>
	 *       <td>The number of tags following in the message</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Tag list</td>
	 *       <td>varies</td>
	 *       <td>NA</td>
	 *       <td>A list of tags specifying remote client’s properties
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Server IP</td>
	 *       <td>4</td>
	 *       <td>NA</td>
	 *       <td>The IP of the server to which the client is connected
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Server TCP Port
	 * </td>
	 *       <td>2</td>
	 *       <td>NA</td>
	 *       <td>The TCP port on which the server listens</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static Packet getPeerHelloPacket(UserHash userHash,ClientID clientID,int myPort,
			byte[] serverIP,int serverPort,String userName, Map<PeerFeatures, Integer> clientFeatures) throws JMException  {
				
		int misc_optins1 = Utils.peerFeatures1ToInt(clientFeatures);
		int misc_optins2 = Utils.peerFeatures2ToInt(clientFeatures);
		
		TagList tagList = new TagList();
		tagList.addTag(new StringTag(TAG_NAME_NAME, userName));
		tagList.addTag(new IntTag(TAG_NAME_MISC_OPTIONS1,misc_optins1 ));
		tagList.addTag(new IntTag(TAG_NAME_MISC_OPTIONS2,misc_optins2));
		
		tagList.addTag(new IntTag(TAG_NAME_PROTOCOLVERSION,PROTOCOL_VERSION ));
		tagList.addTag(new IntTag(TAG_NAME_CLIENTVER, ED2KConstants.getSoftwareVersion()));
		tagList.addTag(new IntTag(TAG_NAME_UDP_PORT_PEER, ConfigurationManagerSingleton.getInstance().getUDP()));
	
		int tag_list_size = tagList.getByteSize();
		
        
        Packet packet = new Packet(1 + 16 + 4 + 2 + 4 + tag_list_size + 4 + 2, PROTO_EDONKEY_TCP);
       
        packet.setCommand(OP_PEERHELLO);//hello 
        packet.insertData((byte)16);//Insert length of user Hash
        packet.insertData(userHash.getUserHash());//insert user hash
        if (clientID == null)
        	packet.insertData(new byte[]{0,0,0,0});//insert user ID
        else 
        	packet.insertData(clientID.getClientID());//insert user ID
        packet.insertData((short)myPort);//insert my active port
        packet.insertData(tagList.size());//insert tag count
        packet.insertData(tagList.getAsByteBuffer());
        
        if (serverIP==null) 
        		packet.insertData(new byte[] { 0, 0, 0, 0 });
        	else 
        		packet.insertData((serverIP));
        
        packet.insertData((short)serverPort);
        return packet;
	}

	
	
    /**
     * Peer hello answer packet.
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including the header and size fields</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x4C</td>
	 *       <td>The value of the OP HELLOANSWER opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Hello fields</td>
	 *       <td> &nbsp;</td>
	 *       <td> &nbsp;</td>
	 *       <td>The same fields as in the hello message starting with the user hash</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
     * */
	public static Packet getPeerHelloAnswerPacket(UserHash userHash,
			ClientID clientID, int myPort, String userName, byte[] serverIP,
			int serverPort, Map<PeerFeatures, Integer> clientFeatures)
			throws JMException {

		int misc_optins1 = Utils.peerFeatures1ToInt(clientFeatures);
		int misc_optins2 = Utils.peerFeatures2ToInt(clientFeatures);
		TagList tagList = new TagList();
		tagList.addTag(new StringTag(TAG_NAME_NAME, userName));
		tagList.addTag(new IntTag(TAG_NAME_MISC_OPTIONS1, misc_optins1));
		tagList.addTag(new IntTag(TAG_NAME_MISC_OPTIONS2, misc_optins2));

		tagList.addTag(new IntTag(TAG_NAME_PROTOCOLVERSION, PROTOCOL_VERSION));
		tagList.addTag(new IntTag(TAG_NAME_CLIENTVER, ED2KConstants.getSoftwareVersion()));
		tagList.addTag(new IntTag(TAG_NAME_UDP_PORT_PEER, ConfigurationManagerSingleton.getInstance().getUDP()));

		int tag_list_size = tagList.getByteSize();

		Packet packet = new Packet(1 + 1 + 16 + 4 + 2 + 4 + 4 + 2 + 2 + tag_list_size + 4 + 2, PROTO_EDONKEY_TCP);
		packet.setCommand(OP_PEERHELLOANSWER);// hello answer tag

		packet.insertData(userHash.getUserHash());// insert user hash
		if (clientID == null)
			packet.insertData(new byte[] { 0, 0, 0, 0 });
		else
			packet.insertData(clientID.getClientID());// insert user ID

		packet.insertData((short) myPort);// insert my active port

		packet.insertData(tagList.size());

		packet.insertData(tagList.getAsByteBuffer());

		if (serverIP != null)
			packet.insertData(serverIP);
		else
			packet.insertData(new byte[] { 0, 0, 0, 0 });// Server IP
		if (serverPort != 0)
			packet.insertData((short) serverPort);
		else
			packet.insertData((short) 0);// Server port
		return packet;
	}
	
	/**
	 * Peer chat message.
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size ﬁelds
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x4E</td>
	 *       <td>The value of the OP_MESSAGE opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Length</td>
	 *       <td>2</td>
	 *       <td> NA</td>
	 *       <td>The length of the message</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Message</td>
	 *       <td>Varies</td>
	 *       <td>NA</td>
	 *       <td>The actual message
	 * </td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 **/
	public static Packet getMessagePacket(String message){
		byte[] message_bytes = message.getBytes();
		Packet packet = new Packet(2 + message.length(), PROTO_EDONKEY_TCP);
		packet.setCommand(OP_MESSAGE);
		packet.insertData(Convert.intToShort(message_bytes.length));
		packet.insertData(message_bytes);
		return packet;
	}

	
	/**
	 * Get File Request packet.
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size ﬁelds
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x58</td>
	 *       <td>The value of the OP FILEREQUEST opcode
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>File ID</td>
	 *       <td>16</td>
	 *       <td>NA</td>
	 *       <td>Unique file ID</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Part Status</td>
	 *       <td>3</td>
	 *       <td>NA</td>
	 *       <td>Optional, sent if the extended request version
	 * indicated in the eMule info message is greater
	 * than zero. The file significance is explained in
	 * this section
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Source count</td>
	 *       <td>2</td>
	 *       <td>NA</td>
	 *       <td>Optional, sent if the extended request version
	 * indicated in the eMule info message is greater
	 * than one. Indicated the current number of
	 * sources for this file
	 * </td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * */
	public static Packet getFileNameRequestPacket(FileHash fileHash){
        Packet packet=new Packet(16, PROTO_EDONKEY_TCP);
        packet.setCommand(OP_FILENAMEREQUEST);
        packet.insertData(fileHash.getHash());
        return packet;
	}
	
	public static Packet getFileNameRequestPacket(FileHash fileHash, GapList fileGapList, long fileSize, int sourceCount){
		JMuleBitSet bitSet = fileGapList.getBitSet(fileSize);
		byte [] bitSetArray = bitSet.getAsByteArray();
        Packet packet=new Packet(16 + bitSetArray.length + 2, PROTO_EDONKEY_TCP);
        packet.setCommand(OP_FILENAMEREQUEST);
        packet.insertData(fileHash.getHash());
        packet.insertData(bitSetArray);
        packet.insertData(Convert.intToShort(sourceCount));
        return packet;
	}
	
	
	/**
	 * Get requested file ID, this packet must be used with FileRequest packet.
	 * @param fileHash file ID
	 * @return requested file ID packet
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size fields
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x4F</td>
	 *       <td>The value of the OP SETREQFILEID opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File ID</td>
	 *       <td>16</td>
	 *       <td>NA</td>
	 *       <td>The ID of the requested file</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 */
	public static Packet getFileStatusRequestPacket(FileHash fileHash) {
		Packet packet = new Packet(16, PROTO_EDONKEY_TCP);
		packet.setCommand(OP_FILESTATREQ);
		packet.insertData(fileHash.getHash());
		return packet;
	}
	
    /**
     * Start upload request for file.
     * @param fileHash file hash
     * @return eDonkey packet
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size ﬁelds
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x54</td>
	 *       <td>The value of the OP_ STARTUPLOADREQ
	 * opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File ID</td>
	 *       <td>16</td>
	 *       <td> NA</td>
	 *       <td>The ID of the requested file</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
     */
	public static Packet getUploadReuqestPacket(FileHash fileHash){
		Packet packet=new Packet(16, PROTO_EDONKEY_TCP);
		packet.setCommand(OP_SLOTREQUEST);
		packet.insertData(fileHash.getHash());
		return packet;
	}
 
	/**
	 * Request file parts packet.
	 * @param fileHash  hash of download file
	 * @param partsData position of parts max 3, format <begin> <begin> <begin> <end> <end> <end>
	 * @return
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size fields
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x46</td>
	 *       <td>The value of the OP REQUESTPARTS opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File ID</td>
	 *       <td> 16</td>
	 *       <td> NA</td>
	 *       <td>A unique file ID calculated by hashing the
	 * file's data
	 *  hash</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Part 1 Start
	 * offset</td>
	 *       <td>4</td>
	 *       <td>NA</td>
	 *       <td>Start offset of the part 1 in the file</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Part 2 Start offset</td>
	 *       <td>4</td>
	 *       <td>NA</td>
	 *       <td>&nbsp;</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Part 3 Start offset</td>
	 *       <td>4</td>
	 *       <td>NA</td>
	 *       <td>&nbsp;</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Part 1 End offset
	 * </td>
	 *       <td>4</td>
	 *       <td>NA</td>
	 *       <td>End offset of the part 1 in the file</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Part 2 End offset</td>
	 *       <td>4</td>
	 *       <td>NA</td>
	 *       <td>&nbsp;</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Part 3 End offset</td>
	 *       <td>4</td>
	 *       <td>NA</td>
	 *       <td>&nbsp;</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 */
	public static Packet getPeerRequestFileParts(FileHash fileHash,
			FileChunkRequest... partsData) {

			Packet packet = new Packet(16 + (8 * 3), PROTO_EDONKEY_TCP);
			packet.setCommand(OP_REQUESTPARTS);
			packet.insertData(fileHash.getHash());
			
			ByteBuffer num = Misc.getByteBuffer(4);
			
			for(FileChunkRequest request : partsData) {
				num.clear();
				num.rewind();
				num.putInt(Convert.longToInt(request.getChunkBegin()));
				packet.insertData(num.array());
			}
			
			for(FileChunkRequest request : partsData) {
				num.clear();
				num.rewind();
				num.putInt(Convert.longToInt(request.getChunkEnd()));
				packet.insertData(num.array());
			}
			
			return packet;
	}
	
	/**
	 * Request hashes of parts.
	 * @param fileHash file hash
	 * @return request file hash packet
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size fields
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x51</td>
	 *       <td>The value of the OP_HASHSETREQUEST
	 * opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File ID</td>
	 *       <td>16</td>
	 *       <td> NA</td>
	 *       <td>The file ID of the requested file</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 */
	public static Packet getRequestPartHashSetPacket(FileHash fileHash) {
		Packet packet = new Packet(16, PROTO_EDONKEY_TCP);
		packet.setCommand(OP_HASHSETREQUEST);
		packet.insertData(fileHash.getHash());
		return packet;
	}

	/**
	 * Get Slot release packet, sended when download is finished/canceled.
	 * @return
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size fields
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x56</td>
	 *       <td>The value of the OP_ CANCELTRANSFER
	 * opcode
	 * 
	 * </td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 */
	public static Packet getSlotReleasePacket() {
		Packet packet = new Packet(0, PROTO_EDONKEY_TCP);
		packet.setCommand(OP_SLOTRELEASE);
		return packet;
	}

	/**
	 * End of Download Packet.
	 * @param fileHash
	 * @return
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size fields
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x49</td>
	 *       <td>The value of the OP END OF DOWNLOAD
	 * opcode
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>File ID</td>
	 *       <td> 16</td>
	 *       <td> NA</td>
	 *       <td>The file ID</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 */
	public static Packet getEndOfDownloadPacket(FileHash fileHash) {
		Packet packet = new Packet(16, PROTO_EDONKEY_TCP);
		packet.setCommand(OP_END_OF_DOWNLOAD);
		packet.insertData(fileHash.getHash());
		return packet;
	}
	
	/**
	 * File request answer packet.
	 * @param fileHash
	 * @param fileName
	 * @return
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size fields
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x59</td>
	 *       <td>The value of the OP FILEREQANSWER opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File ID</td>
	 *       <td>16</td>
	 *       <td>NA</td>
	 *       <td>Unique file ID</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Name length</td>
	 *       <td>2</td>
	 *       <td>NA</td>
	 *       <td>Optional, sent if the extended request version
	 * indicated in the eMule info message is greater
	 * than zero. The file significance is explained in
	 * this section
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Filename</td>
	 *       <td>Varies</td>
	 *       <td>NA</td>
	 *       <td>Optional, sent if the extended request version
	 * indicated in the eMule info message is greater
	 * than one. Indicated the current number of
	 * sources for this file
	 * </td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 */
	public static Packet getFileRequestAnswerPacket(FileHash fileHash,
			String fileName) {
		byte[] fileNameBytes = fileName.getBytes();
		Packet packet = new Packet(16 + 2 + fileNameBytes.length, PROTO_EDONKEY_TCP);
		packet.setCommand(OP_FILEREQANSWER);
		packet.insertData(fileHash.getHash());
		packet.insertData(Convert.intToShort(fileNameBytes.length));
		packet.insertData(fileNameBytes);
		return packet;
	}
	
	/**
	 * File Status reply.
	 * @param partHashSet
	 * @param fileSize
	 * @param fileGapList
	 * @return
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size fields
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x50</td>
	 *       <td>The value of the OP FILESTATUS opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File ID</td>
	 *       <td>16</td>
	 *       <td>NA</td>
	 *       <td>The ID of the file</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Part count</td>
	 *       <td>2</td>
	 *       <td>NA</td>
	 *       <td>File parts count</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Part status</td>
	 *       <td>&nbsp;</td>
	 *       <td>NA</td>
	 *       <td>bit array with parts status : 1 - completed part ; 0 - uncompleted part</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static Packet getFileStatusReplyPacket(PartHashSet partHashSet,long fileSize, GapList fileGapList){
		JMuleBitSet bitSet = fileGapList.getBitSet(fileSize);
		byte [] bitSetArray = bitSet.getAsByteArray();
		Packet packet = new Packet(16 + 2 + bitSetArray.length, PROTO_EDONKEY_TCP);
		packet.setCommand(OP_FILESTATUS);
		packet.insertData(partHashSet.getFileHash().getHash());
		packet.insertData(Convert.intToShort(partHashSet.size()));
		packet.insertData(bitSetArray);
		return packet;
	}

	/**
	 * File hash set answer packet.
	 * @param fileHashSet
	 * @return
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size fields
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x51</td>
	 *       <td>The value of the OP_HASHSETREQUEST opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File Hash</td>
	 *       <td>16</td>
	 *       <td> NA</td>
	 *       <td>The hash extracted from all the file</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Part Count</td>
	 *       <td>2</td>
	 *       <td>NA</td>
	 *       <td>The number of parts in the file</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Part Hashes</td>
	 *       <td>Varies</td>
	 *       <td>NA</td>
	 *       <td>A hash for each file part - the size of each hash
	 * is 16 bytes
	 * </td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static Packet getFileHashReplyPacket(PartHashSet fileHashSet){
		Packet packet = new Packet(16 + 2 + 16 * fileHashSet.size(),
				PROTO_EDONKEY_TCP);
		packet.setCommand(OP_HASHSETANSWER);
		packet.insertData(fileHashSet.getFileHash().getHash());
		packet.insertData(Convert.intToShort(fileHashSet.size()));
		for (int i = 0; i < fileHashSet.size(); i++)
			packet.insertData(fileHashSet.get(i));
		return packet;
	}
	
	/**
	 * Accept upload packet.
	 * @param fileHash
	 * @return
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size fields</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x55</td>
	 *       <td>The value of the OP_ACCEPTUPLOADREQ opcode
	 * </td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static Packet getAcceptUploadPacket(FileHash fileHash){
		Packet packet = new Packet(16, PROTO_EDONKEY_TCP);
		packet.setCommand(OP_SLOTGIVEN);
		packet.insertData(fileHash.getHash());
		return packet;		
	}

	/**
	 * File part sending packet.
	 * @param fileHash
	 * @param fileChunk
	 * @return
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size fields
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x46</td>
	 *       <td>The value of the OP SENDINGPART opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File ID</td>
	 *       <td> 16</td>
	 *       <td> NA</td>
	 *       <td>A unique file ID calculated by hashing the
	 * file’s data
	 *  </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Start Pos</td>
	 *       <td>4</td>
	 *       <td>NA</td>
	 *       <td>The start position of the downloaded data</td>
	 *     </tr>
	 *     <tr>
	 *       <td>End Pos</td>
	 *       <td>4</td>
	 *       <td>NA</td>
	 *       <td>The end position of the downloaded data</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Data</td>
	 *       <td>&nbsp;</td>
	 *       <td>&nbsp;</td>
	 *       <td>The actual downloaded data. This data may
	 * be compressed.
	 * </td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static Packet getFilePartSendingPacket(FileHash fileHash,FileChunk fileChunk){
		Packet packet = new Packet(16 + 4 + 4 + fileChunk.getChunkData()
				.capacity(), PROTO_EDONKEY_TCP);
		packet.setCommand(OP_SENDINGPART);
		packet.insertData(fileHash.getHash());
		packet.insertData(Convert.longToInt(fileChunk.getChunkStart()));
		packet.insertData(Convert.longToInt(fileChunk.getChunkEnd()));
		packet.insertData(fileChunk.getChunkData().array());
		return packet;
	}
	
	/**
	 * File not found packet.
	 * @param fileHash
	 * @return
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size fields
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x48</td>
	 *       <td>The value of the OP FILEREQANSNOFIL
	 * opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File ID</td>
	 *       <td>16</td>
	 *       <td>NA</td>
	 *       <td>The non existent file ID</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static Packet getFileNotFoundPacket(FileHash fileHash){
		Packet packet = new Packet(16, PROTO_EDONKEY_TCP);
		packet.setCommand(OP_FILEREQANSNOFILE);
		packet.insertData(fileHash.getHash());
		return packet;
	}
	
	
	
	public static Packet getEMulePeerHelloPacket() throws JMException {
		TagList tag_list = new TagList();
		tag_list.addTag(new IntTag(ET_COMPRESSION, DefaultJMuleFeatures.get(PeerFeatures.DataCompressionVersion)));
		tag_list.addTag(new IntTag(ET_UDPPORT, (ConfigurationManagerSingleton.getInstance().getUDP())));
		tag_list.addTag(new IntTag(ET_UDPVER, DefaultJMuleFeatures.get(PeerFeatures.UDPVersion)));
		tag_list.addTag(new IntTag(ET_SOURCEEXCHANGE, DefaultJMuleFeatures.get(PeerFeatures.SourceExchange1Version) ));
		tag_list.addTag(new IntTag(ET_COMMENTS, DefaultJMuleFeatures.get(PeerFeatures.AcceptCommentVersion)));
		tag_list.addTag(new IntTag(ET_EXTENDEDREQUEST, DefaultJMuleFeatures.get(PeerFeatures.ExtendedRequestsVersion) ));
		tag_list.addTag(new IntTag(ET_FEATURES, DefaultJMuleFeatures.get(PeerFeatures.NoViewSharedFiles)));
		
		Packet packet = new Packet(1 + 4 + 1 + 1 + 1 + 4 + tag_list.getByteSize(),
				PROTO_EMULE_EXTENDED_TCP);
		packet.setCommand(OP_EMULE_HELLO);
		packet.insertData((byte)0x47);
		packet.insertData((byte)0xFF);
		packet.insertData(tag_list.size());
		packet.insertData(tag_list.getAsByteBuffer());
		return packet;
	}
	
	/**
	 * Create eMule hello answer packet.
	 * 
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xC5</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size fields
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x02</td>
	 *       <td>The value of the OP EMULEINFOANSWER
	 * opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>eMule Info
	 * fields</td>
	 *       <td>&nbsp;</td>
	 *       <td>&nbsp;</td>
	 *       <td>This message has the same fields as an eMule
	 * info message.</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 */
	public static Packet getEMulePeerHelloAnswerPacket() throws JMException {
		TagList tag_list = new TagList();
		tag_list.addTag(new IntTag(ET_COMPRESSION, DefaultJMuleFeatures.get(PeerFeatures.DataCompressionVersion)));
		tag_list.addTag(new IntTag(ET_UDPPORT, (ConfigurationManagerSingleton.getInstance().getUDP())));
		tag_list.addTag(new IntTag(ET_UDPVER, DefaultJMuleFeatures.get(PeerFeatures.UDPVersion)));
		tag_list.addTag(new IntTag(ET_SOURCEEXCHANGE, DefaultJMuleFeatures.get(PeerFeatures.SourceExchange1Version) ));
		tag_list.addTag(new IntTag(ET_COMMENTS, DefaultJMuleFeatures.get(PeerFeatures.AcceptCommentVersion)));
		tag_list.addTag(new IntTag(ET_EXTENDEDREQUEST, DefaultJMuleFeatures.get(PeerFeatures.ExtendedRequestsVersion) ));
		tag_list.addTag(new IntTag(ET_FEATURES, DefaultJMuleFeatures.get(PeerFeatures.NoViewSharedFiles)));
		
		Packet packet = new Packet(1 + 4 + 1 + 1 + 1 + 4 + tag_list.getByteSize(),
				PROTO_EMULE_EXTENDED_TCP);
		packet.setCommand(OP_EMULEHELLOANSWER);
		packet.insertData((byte)0x47);
		packet.insertData((byte)0xFF);
		packet.insertData(tag_list.size());
		packet.insertData(tag_list.getAsByteBuffer());
		return packet;
	}
	
	/**
	 * Create Queue ranking packet.
	 * @param position position in queue
	 * 
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xC5</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size fields
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x60</td>
	 *       <td>The value of the OP_QUEUERANKING
	 * opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Queue position</td>
	 *       <td>2</td>
	 *       <td>NA</td>
	 *       <td>The position of the client in the queue</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Buffer</td>
	 *       <td>10</td>
	 *       <td>0</td>
	 *       <td>10 zero bytes, purpose unknown</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 */
	public static Packet getQueueRankingPacket(int position) {
		Packet packet = new Packet(2 + 10, PROTO_EMULE_EXTENDED_TCP);
		packet.setCommand(OP_EMULE_QUEUERANKING);
		packet.insertData(Convert.intToShort(position));

		ByteBuffer zero_array = Misc.getByteBuffer(10);
		zero_array.position(0);
		packet.insertData(zero_array);
		return packet;
	}
	
	/**
	 * PEX request
	 * Create sources request packet
	 * @param fileHash hash of file which sources are requested.
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xC5</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size fields
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x81</td>
	 *       <td>The value of the OP_ REQUESTSOURCES
	 * opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File ID</td>
	 *       <td>16</td>
	 *       <td>NA</td>
	 *       <td>The file ID of the required file</td>
	 *     </tr>
	 *   </tbody>
	 */
	public static Packet getSourcesRequestPacket(FileHash fileHash) {
		Packet packet = new Packet(16, PROTO_EMULE_EXTENDED_TCP);
		packet.setCommand(OP_REQUESTSOURCES);
		packet.insertData(fileHash.getHash());
		
		return packet;
	}
	
	/**
	 * Basic PEX answer pachet
	 * 
	 */
	public static Packet getSourcesAnswerPacket(FileHash fileHash, Collection<Peer> peer_list) {
		Packet packet = new Packet(16 + 2 + peer_list.size() * (4 + 2),PROTO_EMULE_EXTENDED_TCP);
		packet.setCommand(OP_ANSWERSOURCES);
		packet.insertData(fileHash.getHash());
		packet.insertData(Convert.intToShort(peer_list.size()));
		for(Peer peer : peer_list) {
			byte[] ip_data = Convert.stringIPToArray(peer.getIP());
			short port = Convert.intToShort(peer.getListenPort());
			packet.insertData(ip_data);
			packet.insertData(port);
		}
		return packet;
	}
	
	public static Packet getSecureIdentificationPacket(byte[] challenge, boolean isPublicKeyNeeded) {
		Packet packet = new Packet(5, PROTO_EMULE_EXTENDED_TCP);
		packet.setCommand(OP_SECIDENTSTATE);
		if (isPublicKeyNeeded)
			packet.insertData((byte)2);
		else
			packet.insertData((byte)1);
		packet.insertData(challenge);
		return packet;
	}
	
	public static Packet getPublicKeyPacket(byte[] publicKey) {
		Packet packet = new Packet(1+publicKey.length, PROTO_EMULE_EXTENDED_TCP);
		
		packet.setCommand(OP_PUBLICKEY);
		packet.insertData((byte)publicKey.length);
		packet.insertData(publicKey);
		
		return packet;
	}
	
	public static Packet getSignaturePacket(byte[] signature) {
		Packet packet = new Packet(1 + signature.length, PROTO_EMULE_EXTENDED_TCP);
		packet.setCommand(OP_SIGNATURE);
		packet.insertData((byte)signature.length);
		packet.insertData(signature);
		return packet;
	}
	
	public static Packet getKadCallBackRequest(Int128 clientID, FileHash fileHash,IPAddress ipAddress, short tcpPort) {
		Packet packet = new Packet(16 + 16 + 4 + 2, PROTO_EMULE_EXTENDED_TCP);
		packet.setCommand(OP_KAD_CALLBACK);
		packet.insertData(clientID.toByteArray());
		packet.insertData(fileHash.getHash());
		packet.insertData(ipAddress.getAddress());
		packet.insertData(tcpPort);
		return packet;
	}
	
	
	public static Packet getMultipacketRequest(FileHash fileHash, Collection<ByteBuffer> entries) {
		int entries_capacity = 0;
		for (ByteBuffer entry : entries)
			entries_capacity += entry.capacity();
		Packet packet = new Packet(16 + entries_capacity,PROTO_EMULE_EXTENDED_TCP);
		packet.setCommand(OP_MULTIPACKET);
		packet.insertData(fileHash.getHash());
		for (ByteBuffer entry : entries)
			packet.insertData(entry.array());
		return packet;
	}
	
	public static Packet getMultipacketExtRequest(FileHash fileHash, long fileSize, Collection<ByteBuffer> entries) {
		int entries_capacity = 0;
		for (ByteBuffer entry : entries)
			entries_capacity += entry.capacity();
		Packet packet = new Packet(16 + 8 + entries_capacity,PROTO_EMULE_EXTENDED_TCP);
		packet.setCommand(OP_MULTIPACKET_EXT);
		packet.insertData(fileHash.getHash());
		packet.insertData(fileSize);
		for (ByteBuffer entry : entries)
			packet.insertData(entry.array());
		return packet;
	}
	
	public static Packet getMultipacketResponse(FileHash fileHash, Collection<ByteBuffer> entries) {
		int entries_capacity = 0;
		for (ByteBuffer entry : entries)
			entries_capacity += entry.capacity();
		Packet packet = new Packet(16 + entries_capacity,PROTO_EMULE_EXTENDED_TCP);
		packet.setCommand(OP_MULTIPACKETANSWER);
		packet.insertData(fileHash.getHash());
		for (ByteBuffer entry : entries)
			packet.insertData(entry.array());
		return packet;
	}
	
	/*
	 * Multipacket entries
	 */
	
	public static ByteBuffer getFileNameRequestEntry() {
		ByteBuffer entry = Misc.getByteBuffer(1);
		entry.put(OP_FILENAMEREQUEST);
		entry.position(0);
		return entry;
	}

	public static ByteBuffer getFileNotFoundResponseEntry() {
		ByteBuffer entry = Misc.getByteBuffer(1);
		entry.put(OP_FILEREQANSNOFILE);
		entry.position(0);
		return entry;
	}
	
	public static ByteBuffer getFileNameResponseEntry(String fileName) {
		byte[] str_content = fileName.getBytes();
		ByteBuffer entry = Misc.getByteBuffer(1 + 2 + str_content.length );
		entry.put(OP_FILEREQANSWER);
		
		entry.putShort(Convert.intToShort(str_content.length));
		entry.put(str_content);
		
		entry.position(0);
		return entry;
	}
	
	public static ByteBuffer getFileStatusRequestEntry() {
		ByteBuffer entry = Misc.getByteBuffer(1);
		entry.put(OP_FILESTATREQ);
		entry.position(0);
		return entry;
	}
	
	public static ByteBuffer getCompletedFileStatusResponseEntry() {
		ByteBuffer entry = Misc.getByteBuffer(1 + 2);
		entry.put(OP_FILESTATUS);
		entry.putShort((short)0);
		entry.position(0);
		return entry;
	}
	
	public static ByteBuffer getFileStatusResponseEntry(GapList fileGapList,int partCount, long fileSize) {
		JMuleBitSet bitSet = fileGapList.getBitSet(fileSize);
		byte [] bitSetArray = bitSet.getAsByteArray();
		ByteBuffer entry = Misc.getByteBuffer(1 + 2 + bitSetArray.length);
		entry.put(OP_FILESTATUS);
		entry.putShort(Convert.intToShort(partCount));
		entry.put(bitSetArray);
		entry.position(0);
		return entry;
	}
	
	public static ByteBuffer getFileSourcesRequestEntry() {
		ByteBuffer entry = Misc.getByteBuffer(1);
		entry.put(OP_REQUESTSOURCES);
		entry.position(0);
		return entry;
	}
		
	public static ByteBuffer getFileAICHRequestEntry() {
		ByteBuffer entry = Misc.getByteBuffer(1);
		entry.put(OP_AICHFILEHASHREQ);
		entry.position(0);
		return entry;
	}
	
	
	
}
