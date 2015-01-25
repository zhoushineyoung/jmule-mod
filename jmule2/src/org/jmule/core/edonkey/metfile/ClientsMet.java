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

import static org.jmule.core.edonkey.ED2KConstants.CREDITFILE_VERSION;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.jmule.core.edonkey.ED2KConstants;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.peermanager.PeerCredit;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;
/**
 * Clients.met format
 * <table cellpadding="0" border="1" cellspacing="0" width="70%">
 *  <tbody>
 *   <tr>
 *     <td>Name</td>
 *     <td>Size in bytes</td>
 *     <td>Default value</td>
 *   </tr>
 *   <tr>
 *     <td>File header</td>
 *     <td>1</td>
 *     <td>0x12</td>
 *   </tr>
 *   <tr>
 *     <td>Number of entries</td>
 *     <td>4</td>
 *     <td>&nbsp; </td>
 *  </tr>
 *  </tbody>
 * </table>
 * <br>
 * One entry format : 
 * 
 * <table cellpadding="0" border="1" cellspacing="0" width="70%">
 * 	<tbody>
 *   <tr>
 *     <td>Name</td>
 *     <td>Size in bytes</td>
 *   </tr>
 *   <tr>
 *     <td>User Hash</td>
 *     <td>16</td>
 *   </tr>
 *   <tr>
 *     <td>nUploadedLo</td>
 *     <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>nDownloadedLo</td>
 *     <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>nLastSeen</td>
 *    <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>nUploadedHi</td>
 *     <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>nDownloadedHi</td>
 *     <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>nReserved3</td>
 *     <td>2</td>
 *   </tr>
 *   <tr>
 *     <td>nKeySize</td>
 *     <td>1</td>
 *   </tr>
 *   <tr>
 *     <td>abySecureIdent</td>
 *     <td><nKeySize></td>
 *   </tr>
 * </tbody>
 * </table>
 * 
 * @author binary256
 * @version $$Revision: 1.9 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/08/22 12:46:17 $$
 */
public class ClientsMet extends MetFile {

	public ClientsMet(String fileName) throws ClientsMetException {
		super(fileName);
		/*if (file_channel == null)
			throw new ClientsMetException("Failed to open " + fileName);*/
	}

	public Map<UserHash,PeerCredit> load() throws IOException,ClientsMetException {
		
		FileChannel file_channel = new FileInputStream(file).getChannel();
		
		ByteBuffer file_content = Misc.getByteBuffer(file_channel.size());
		file_channel.read(file_content);
		file_channel.close();
		
		Hashtable<UserHash,PeerCredit> result = new Hashtable<UserHash,PeerCredit>();
		ByteBuffer data;
		
		file_content.position(0);
		short file_version = file_content.get();
		
		if (file_version != CREDITFILE_VERSION) {
			file_content.clear();
			file_content = null;
			throw new ClientsMetException("Unknow file format");
		}
		long count = Convert.intToLong(file_content.getInt());
		for (long i = 0; i < count; i++) {
			byte abyKey[] = new byte[16];
			int nUploadedLo, nDownloadedLo, nUploadedHi, nDownloadedHi;
			long nLastSeen;
			byte abySecureIdent[];
			int nKeySize;
			int nReserved3;

			data = Misc.getByteBuffer(16);
			file_content.get(data.array());
			abyKey = data.array();
			
			nUploadedLo = file_content.getInt();
			nDownloadedLo = file_content.getInt();
			
			nLastSeen = file_content.getLong();
			nUploadedHi = file_content.getInt();
			nDownloadedHi = file_content.getInt();
			nReserved3 = Convert.shortToInt(file_content.getShort());
			nKeySize = Convert.byteToInt(file_content.get());
			
			abySecureIdent = new byte[nKeySize];
			file_content.get(abySecureIdent);
			if (System.currentTimeMillis() - nLastSeen < ED2KConstants.PEER_CLIENTS_MET_EXPIRE_TIME) {
				PeerCredit cc = new PeerCredit(abyKey,nUploadedLo,nDownloadedLo,nLastSeen,nUploadedHi,nDownloadedHi,nReserved3,abySecureIdent);
				result.put(cc.getUserHash(), cc);
			}
		}
		
		file_content.clear();
		file_content = null;
		
		return result;
	}
	
	public void store(Collection<PeerCredit> clientsCredit) throws IOException {
		
		long file_size = 0;
		
		List<ByteBuffer> file_content_blocks = new ArrayList<ByteBuffer>();
		
		ByteBuffer file_header = Misc.getByteBuffer(1 + 4);
		file_header.put(CREDITFILE_VERSION);
		file_header.putInt(clientsCredit.size());
		file_header.position(0);
		file_content_blocks.add(file_header);
		file_size += file_header.capacity();
		
		for(PeerCredit credit : clientsCredit) {
			ByteBuffer credit_data = Misc.getByteBuffer( 16 + 4 + 4 + 8 + 4 + 4 + 2 + 1 + credit.getAbySecureIdent().length );
			file_size += credit_data.capacity();
			
			credit_data.put(credit.getUserHash().getUserHash());
			credit_data.putInt(credit.getNUploadedLo());
			credit_data.putInt(credit.getNDownloadedLo());
			credit_data.putLong(credit.getLastSeen());
			credit_data.putInt(credit.getNUploadedHi());
			credit_data.putInt(credit.getNDownloadedHi());
			credit_data.putShort(Convert.intToShort(credit.getNReserved3()));
			credit_data.put(Convert.intToByte(credit.getAbySecureIdent().length));
			credit_data.put(credit.getAbySecureIdent());
			credit_data.position(0);
			file_content_blocks.add(credit_data);
		}
		
		ByteBuffer file_content = Misc.getByteBuffer(file_size);
		for(ByteBuffer block : file_content_blocks) {
			block.position(0);
			file_content.put(block);
		}
		file_content.position(0);
		
		FileChannel file_channel = new FileOutputStream(file).getChannel();
		file_channel.write(file_content);
		file_channel.close();
		
		file_content_blocks.clear();
		file_content.clear();
		file_content = null;
		
	}
}
