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
package org.jmule.core.jkad.routingtable;

import static org.jmule.core.jkad.JKadConstants.NODES_DAT_VERSION;
import static org.jmule.core.utils.Convert.intToShort;
import static org.jmule.core.utils.Misc.getByteBuffer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.jmule.core.jkad.ClientID;
import org.jmule.core.jkad.ContactAddress;
import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.JKadUDPKey;
import org.jmule.core.jkad.utils.Utils;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * Created on Dec 29, 2008
 * @author binary256
 * @version $Revision: 1.7 $
 * Last changed by $Author: binary255 $ on $Date: 2010/08/22 14:29:28 $
 */
public class NodesDat {

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static List<KadContact> load(String fileName) {
		List<KadContact> result = new ArrayList<KadContact>();
		
		try {
						
			FileChannel channel = new FileInputStream(fileName).getChannel();
			ByteBuffer file_content = Misc.getByteBuffer(channel.size());
			channel.read(file_content);
			file_content.position(0);
			channel.close();
			
			file_content.position(4 + 4); // skip 'old' contacts count field, skip nodes.dat version
			
			int contact_count = file_content.getInt();
			
			for(int i = 1 ; i <= contact_count ; i++) {
				byte[] byte_contact_id = new byte[16];
				file_content.get(byte_contact_id);
				ClientID contact_id = new ClientID(byte_contact_id);
				byte[] byte_ip = new byte[4];
				file_content.get(byte_ip);
				IPAddress address = new IPAddress(byte_ip);
				
				short udp_port = file_content.getShort();
				short tcp_port = file_content.getShort();
				
				byte contact_version = file_content.get(); 
				
				byte[] data = new byte[4];
				file_content.get(data);
				
				byte[] data2 = new byte[4];
				file_content.get(data2);
				
				JKadUDPKey udp_key = new JKadUDPKey(data, data2);
				
				byte verified = file_content.get();
				if (Utils.isGoodAddress(address)) {
					KadContact contact = new KadContact(contact_id, new ContactAddress(address, Convert.shortToInt(udp_port)), Convert.shortToInt(tcp_port), contact_version, udp_key, verified==1 ? true : false);
					
					result.add(contact);
				}
			}
			file_content.clear();
			file_content = null;
		}catch(Throwable t) {
			t.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param fileName
	 * @param contactList
	 */
	public static void store(String fileName, List<KadContact> contactList) {
		try {
			
			ByteBuffer file_content = Misc.getByteBuffer(4 + 4 + 4 + contactList.size() * ( 16 + 4 + 2 + 2 + 1 + 4 + 4 + 1) );
			
			file_content.position(4);
			file_content.putInt(NODES_DAT_VERSION);
			file_content.putInt(contactList.size());
			for(KadContact contact : contactList) {
				file_content.put(contact.getContactID().toByteArray());
				file_content.put(contact.getIPAddress().getAddress());
				file_content.putShort(intToShort(contact.getUDPPort()));
				file_content.putShort(intToShort(contact.getTCPPort()));
				file_content.put(contact.getVersion());
				
				// write key
				JKadUDPKey key = contact.getKadUDPKey();
				if (key == null) {
					ByteBuffer data  = getByteBuffer(4 + 4);
					data.position(0);
					file_content.put(data);
				} else {
					ByteBuffer data  = getByteBuffer(4 + 4);
					data.put(key.getKey());
					data.put(key.getAddress().getAddress());
					data.position(0);
					file_content.put(data);
				}
				
				file_content.put((byte)(contact.isIPVerified() ? 1 : 0));
			}
			
			FileChannel channel = new FileOutputStream(fileName).getChannel();
			file_content.position(0);
			channel.write(file_content);
			channel.close();
			
			file_content.clear();
			file_content = null;
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
}
