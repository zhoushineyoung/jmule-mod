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
package org.jmule.core.jkad.utils;

import static org.jmule.core.utils.Convert.byteToInt;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.jkad.ClientID;
import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.routingtable.KadContact;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.utils.Misc;

/**
 * Created on Jan 8, 2009
 * @author binary256
 * @version $Revision: 1.12 $
 * Last changed by $Author: binary255 $ on $Date: 2010/08/02 09:52:44 $
 */
public class Utils {
	
	private static Random random = new Random();

	private static String KAD_INVALID_CHARS = " ()[]{}<>,._-!?:;\\/\"";
	
	public static List<String> getFileKeyword(String fileName) {
		List<String> result = new ArrayList<String>();
	
		StringTokenizer keywords =  new StringTokenizer(fileName, KAD_INVALID_CHARS);
		
		while (keywords.hasMoreElements()) {
			String keyword = keywords.nextToken();
			if (keyword.getBytes().length >= 3)
				result.add(keyword);
		}

		return result;
	}
	
	public static byte[] getRandomInt128() {
		ByteBuffer result = Misc.getByteBuffer(16);
		result.putInt(random.nextInt());
		result.putInt(random.nextInt());
		result.putInt(random.nextInt());
		result.putInt(random.nextInt());
		return result.array();
	}
	
	public static int getRandom(int range) {
		return random.nextInt(range);
	}
	
	public static Collection<Integer> getRandomValues(int range, int count, boolean unique) {
		Collection<Integer> result = new ArrayList<Integer>();
		
		do {
			if (result.size() == count) break;
			Integer candidate = getRandom(range);
			if (unique)
				if (!result.contains(candidate))
					result.add(candidate);
		}while(true);
		
		return result;
	}

	/**
	 * Always filter following IP's : 
	 * 0.0.0.0							invalid.
	 * 127.0.0.0 - 127.255.255.255		Loopback.
     * 224.0.0.0 - 239.255.255.255		Multicast.
     * 240.0.0.0 - 255.255.255.255		Reserved for Future Use.
	 * 255.255.255.255					invalid.
	 * 
	 * @param address
	 * @return
	 */
	public static boolean isGoodAddress(IPAddress address) {
		long value = byteToInt(address.getAddress());
		int s = byteToInt(address.getAddress()[3]);
		if ((s==0) || (s==127) || (s>=224)) return false;
		if (value<=60) return false;
		return true;
	}
	


	
	public static boolean inToleranceZone(Int128 target, Int128 source, long toleranceZone) {
		Int128 xorValue = XOR(target,source);
		if (xorValue.get32Bit(0) > toleranceZone) 
			return false;
		return true;
	}
	
	public static Int128 XOR(Int128 a, Int128 b) {
		BitSet result = (BitSet) a.getBitSet().clone();
		result.xor(b.getBitSet());
		return new Int128(result);
/*		BitSet result = new BitSet(128);
		for (int i = 0; i < result.size(); i++)
			result.set(i, a.getBit(i) ^ b.getBit(i));
		return new Int128(result);*/
	}
	
	/**
	 * Retrun bigger rang from bit set.
	 * Example : 0101011000
	 * Result is : 7   ^
	 * @param bitSet
	 * @return bigger rang
	 */
	public static int getBiggerRang(BitSet bitSet) {
		 for(int i = bitSet.size()-1;i>=0;i--) {
			 if (bitSet.get(i)) return i;
		 }
		 return 0;
	}
	
	/**
	 * Return nearest contact from contactList except contacts from exeptList
	 * @param targetID  XOR distance from JKad to target
	 * @param contactList
	 * @param exceptList
	 * @return nearest contact or null
	 */
	public static KadContact getNearestContact(Int128 targetID, Collection<KadContact> contactList, Set<ClientID> exceptList) {
		KadContact result = null;
		int biggerRang = -1;
		for(KadContact contact : contactList) {
			if (exceptList.contains(contact.getContactID())) {
				continue;
			}
			Int128 distance = XOR(targetID, contact.getContactDistance());
			if (result==null) {
				result = contact;
				biggerRang = getBiggerRang(distance.getBitSet());
				continue;
			}
			int rang = getBiggerRang(distance.getBitSet());
			if (rang<biggerRang) {
				result = contact;
				biggerRang = rang;
			}
		}
		return result;
	}
	
	public static String KadFileIDToFileName(Int128 fileID) {
		byte[] file_id = fileID.toByteArray();
		Convert.updateSearchID(file_id);
		FileHash fileHash = new FileHash(file_id);
		SharedFile file = JMuleCoreFactory.getSingleton().getSharingManager().getSharedFile(fileHash);
		if (file == null)
			return "";
		return file.getSharingName();
	}
	
}
