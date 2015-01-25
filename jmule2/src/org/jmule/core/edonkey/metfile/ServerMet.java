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
package org.jmule.core.edonkey.metfile;

import static org.jmule.core.edonkey.ED2KConstants.SERVERLIST_VERSION;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.packet.tag.TagScanner;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;


/**
 * <table cellpadding="0" border="1" cellspacing="0" width="70%">
 * <tbody>
 *   <tr>
 *     <td>Name</td>
 *     <td>Size in bytes</td>
 *     <td>Default value</td>
 *   </tr>
 *   <tr>
 *     <td>File header</td>
 *     <td>1</td>
 *     <td>0xE0</td>
 *   </tr>
 *   <tr>
 *     <td>Server count</td>
 *     <td>4</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>Servers</td>
 *     <td>Varies</td>
 *     <td></td>
 *   </tr>
 * </tbody>
 * </table>
 * <br>
 * Server Data Block :
 *  
 * <table cellpadding="0" border="1" cellspacing="0" width="70%">
 * <tbody>
 *   <tr>
 *     <td>Name</td>
 *     <td>Size in bytes</td>
 *   </tr>
 *   <tr>
 *     <td>Server IP</td>
 *     <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>Server Port</td>
 *     <td>2</td>
 *   </tr>
 *   <tr>
 *     <td>Tag count</td>
 *     <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>Tag list</td>
 *     <td>variable</td>
 *   </tr>
 * </tbody>
 * </table>
 *
 * @author binary256
 * @version $$Revision: 1.13 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/08/22 12:46:17 $$
 */
public class ServerMet extends MetFile {
	
	private List<String> ip_list;
	private List<Integer> port_list;
	private List<TagList> tag_list;

	
	public ServerMet(String fileName) throws ServerMetException  {
		super(fileName);
	}
	
	public void load() throws ServerMetException {
		try {
			FileChannel file_channel = new FileInputStream(file).getChannel();
			ByteBuffer file_content = Misc.getByteBuffer(file_channel.size());
			file_channel.read(file_content);
			file_channel.close();
			
			ip_list = new ArrayList<String>();
			port_list = new ArrayList<Integer>();
			tag_list = new ArrayList<TagList>();
			
			byte serverListFormat;
			file_content.position(0);
			
			serverListFormat = file_content.get();
			//if (serverListFormat != SERVERLIST_VERSION)
			//	throw new ServerMetException("Unsupported server met file");
			long serverCount = Convert.intToLong(file_content.getInt());
			for(long i = 0; i < serverCount; i++) {
				//Read server IP
				byte[] byte_ip = new byte[4];
				file_content.get(byte_ip);
				
				String remonteAddress = Convert.IPtoString(byte_ip);
				
				//Read server port 
				int remontePort = (Convert.shortToInt(file_content.getShort()));
				
				//Read TagList count
				TagList loaded_tag_list = new TagList();
				int tag_count = file_content.getInt();
				//Load tags....
				for(int j = 0; j<tag_count; j++) {
					Tag tag = TagScanner.scanTag(file_content);
					if (tag != null)
						loaded_tag_list.addTag(tag);
				}
				ip_list.add(remonteAddress);
				port_list.add(remontePort);
				tag_list.add(loaded_tag_list);
			}
			
			file_content.clear();
			file_content = null;
			
		 } catch(Throwable exception) {
			 
			 throw new ServerMetException(exception);
			 
		 }
	}
	
	public void store() throws ServerMetException {
	
			Collection<ByteBuffer> file_blocks = new ArrayList<ByteBuffer>();
			long file_size = 0;
			ByteBuffer header = Misc.getByteBuffer(1 + 4);
			
			file_size += header.capacity();
			
			header.put(SERVERLIST_VERSION);
			header.putInt(ip_list.size());
			header.position(0);
			file_blocks.add(header);
			
			for (int i = 0; i < ip_list.size(); i++) {
				
				ByteBuffer tag_list_block = tag_list.get(i).getAsByteBuffer();
				
				ByteBuffer server_block = Misc.getByteBuffer(4 + 2 + 4 + tag_list_block.capacity());
				file_size += server_block.capacity();
				
				server_block.put(Convert.stringIPToArray( ip_list.get(i) ));
				server_block.putShort(Convert.intToShort( port_list.get(i) ));
				server_block.putInt( tag_list.get(i).size() );
				tag_list_block.position(0);
				server_block.put(tag_list_block);
				
				server_block.position(0);
				file_blocks.add(server_block);
			}
			
		ByteBuffer file_block = Misc.getByteBuffer(file_size);
		for(ByteBuffer block : file_blocks) {
			block.position(0);
			file_block.put(block);
		}
		file_block.position(0);
		
		try {
			FileChannel file_channel = new FileOutputStream(file).getChannel();
			file_channel.write(file_block);
			file_channel.close();
		} catch(Throwable ioe) {
		  throw new ServerMetException("IOException : " + ioe);
		}
		
		file_block.clear();
		file_block = null;
		file_blocks.clear();
		file_blocks = null;
	}
	
	public List<String> getIPList() {
		return ip_list;
	}
	
	public List<Integer> getPortList() {
		return port_list;
	}
	
	public List<TagList> getTagList() {
		return tag_list;
	}
	
	public void setIPList(List<String> ipList) {
		this.ip_list = ipList;
	}

	public void setPortList(List<Integer> portList) {
		this.port_list = portList;
	}
	
	public void setTagList(List<TagList> tagList) {
		this.tag_list = tagList;
	}
}
