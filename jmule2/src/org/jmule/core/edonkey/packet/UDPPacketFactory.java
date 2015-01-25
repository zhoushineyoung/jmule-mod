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

import static org.jmule.core.edonkey.ED2KConstants.*;
import static org.jmule.core.edonkey.ED2KConstants.OP_GLOBGETSOURCES2;
import static org.jmule.core.edonkey.ED2KConstants.OP_GLOBSEARCHREQ;
import static org.jmule.core.edonkey.ED2KConstants.OP_GLOBSEARCHREQ2;
import static org.jmule.core.edonkey.ED2KConstants.OP_GLOBSEARCHREQ3;
import static org.jmule.core.edonkey.ED2KConstants.OP_GLOBSERVRSTATREQ;
import static org.jmule.core.edonkey.ED2KConstants.OP_REASKFILEPING;
import static org.jmule.core.edonkey.ED2KConstants.OP_SERVER_DESC_REQ;
import static org.jmule.core.edonkey.ED2KConstants.PROTO_EDONKEY_SERVER_UDP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.searchmanager.SearchQuery;
import org.jmule.core.searchmanager.tree.NodeValue;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.7 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/07/31 16:00:42 $$
 */
public class UDPPacketFactory {

	/**
	 * Server status request packet : request more server information : hard/soft limits, users, files.
	 * 
	 * @param clientTime
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
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x96</td>
	 *       <td>The value of the OP_GLOBSERVSTATREQ opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Challenge</td>
	 *       <td>4</td>
	 *       <td>NA</td>
	 *       <td>A unsigned integer challenge sent to the server, used for reply verification (the corresponding variable is called 'time' on the client)</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 */
	public static UDPPacket getUDPStatusRequest(int clientTime){
		UDPPacket packet = new UDPPacket(4,PROTO_EDONKEY_SERVER_UDP);
		packet.setCommand(OP_GLOBSERVRSTATREQ);
		packet.insertData(clientTime);
		return packet;
	}

	/**
	 * Request sources from server.
	 * @param dest
	 * @param fileHashList
	 * @return
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
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x9A</td>
	 *       <td>The value of the OP_GLOBGETSOURCES opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File ID List</td>
	 *       <td>NA</td>
	 *       <td>NA</td>
	 *       <td>A list of file IDs (hashes) (each 16 byte length), The IDs are ordered one after the
	 * other without a preceding count</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static UDPPacket getSourcesRequest(Collection<FileHash> fileHashList){
		UDPPacket packet = new UDPPacket(fileHashList.size()*16,PROTO_EDONKEY_SERVER_UDP);
		packet.setCommand(OP_GLOBGETSOURCES);
		for(FileHash hash : fileHashList)
			packet.insertData(hash.getHash());
		
		return packet;
	}
	
	public static UDPPacket getSources2Request(List<FileHash> fileHashList, List<Long> sizeList){
		UDPPacket packet = new UDPPacket(fileHashList.size()*(16 + 4 + 8),PROTO_EDONKEY_SERVER_UDP);
		packet.setCommand(OP_GLOBGETSOURCES2);
		for(int i = 0;i<fileHashList.size();i++) {
			FileHash hash = fileHashList.get(i);
			long size = sizeList.get(i);
			packet.insertData(hash.getHash());
			packet.insertData((int)0);
			packet.insertData(size);
		}

		
		return packet;
	}
	
	
	/**
	 * Server description request.
	 * @param dest
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
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0xA2</td>
	 *       <td>The value of the OP_SERVER_DESC_REQ opcode</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static UDPPacket getUDPServerDescRequest(){
		UDPPacket packet = new UDPPacket(0,PROTO_EDONKEY_SERVER_UDP);
		packet.setCommand(OP_SERVER_DESC_REQ);
		return packet;
	}
	
	
	/**
	 * Server search request.
	 * @param searchString
	 * @param dest
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
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x98 or 0x92</td>
	 *       <td>The value of the OP_GLOBSEARCHREQ or the OP_GLOBSEARCHREQ2 opcodes respectively</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Search request parameters</td>
	 *       <td>Varies</td>
	 *       <td>NA</td>
	 *       <td>Same as the search request message parameters in the Client to Server
	 * TCP communication </td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static UDPPacket getSearchPacket(SearchQuery query){
		
		List<byte[]> data = new ArrayList<byte[]>();
		int total_size = 0;
		List<NodeValue> nodes = query.getSearchTree().traverse();
		for(NodeValue value : nodes) {
			byte[] tmp = value.getBytes();
			data.add(tmp);
			total_size += tmp.length;
		}
		UDPPacket packet = new UDPPacket(total_size,PROTO_EDONKEY_SERVER_UDP);
		packet.setCommand(OP_GLOBSEARCHREQ);
		for(byte[] b : data)
			packet.insertData(b);
		return packet;
	}
	
	public static UDPPacket getSearch2Packet(SearchQuery query){
		List<byte[]> data = new ArrayList<byte[]>();
		int total_size = 0;
		List<NodeValue> nodes = query.getSearchTree().traverse();
		for(NodeValue value : nodes) {
			byte[] tmp = value.getBytes();
			data.add(tmp);
			total_size += tmp.length;
		}
		UDPPacket packet = new UDPPacket(total_size,PROTO_EDONKEY_SERVER_UDP);
		packet.setCommand(OP_GLOBSEARCHREQ2);
		for(byte[] b : data)
			packet.insertData(b);
		return packet;
	}
	
	public static UDPPacket getSearch3Packet(SearchQuery query){
		List<byte[]> data = new ArrayList<byte[]>();
		int total_size = 0;
		List<NodeValue> nodes = query.getSearchTree().traverse();
		for(NodeValue value : nodes) {
			byte[] tmp = value.getBytes();
			data.add(tmp);
			total_size += tmp.length;
		}
		UDPPacket packet = new UDPPacket(4 + 1 + 1 + 1 + total_size,PROTO_EDONKEY_SERVER_UDP);
		packet.setCommand(OP_GLOBSEARCHREQ3);
		packet.insertData((int)1);
		packet.insertData((byte) 0x89);
		packet.insertData((byte) 0x0E);
		packet.insertData((byte) 0x01);
		for(byte[] b : data)
			packet.insertData(b);
		return packet;
	}
	
	public static UDPPacket getCallBackRequest(ClientID clientID) {
		UDPPacket packet = new UDPPacket(4,PROTO_EDONKEY_SERVER_UDP);
		packet.setCommand(OP_CALLBACKREQUEST);
		packet.insertData(clientID.getClientID());
		return packet;
	}
	
	/**
	 * Re-ask file packet.
	 * @param fileHash
	 * @param dest
	 * @return
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
	 *       <td>0x90</td>
	 *       <td>The value of the OP_REASKFILEPING opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File ID</td>
	 *       <td>16</td>
	 *       <td>NA</td>
	 *       <td>The ID of the file which is reasked</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Source count</td>
	 *       <td>2</td>
	 *       <td>NA</td>
	 *       <td>Optional. Unsigned integer, The current
	 * number of sources of the requested file</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static UDPPacket getUDPReaskFilePacket(FileHash fileHash){
		UDPPacket packet = new UDPPacket(16,PROTO_EDONKEY_SERVER_UDP);
		packet.setCommand(OP_REASKFILEPING);
		packet.insertData(fileHash.getHash());
		return packet;
	}
	

}
