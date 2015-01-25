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
package org.jmule.core.downloadmanager;

import java.util.Hashtable;

import org.jmule.core.peermanager.Peer;
import org.jmule.core.sharingmanager.JMuleBitSet;

/**
 * Class manage file part(9.28 MB) availability for each peer 
 * and count total availability of the file in the network.
 * Created on 04-27-2008
 * @author binary256
 * @version $$Revision: 1.6 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/07/09 17:22:12 $$
 */
public class FilePartStatus extends Hashtable<Peer,JMuleBitSet> {
	private int partCount;
	private int partAvailability[];
	
	private int complete_sources = 0;
	private int partial_sources  = 0;
	
	public FilePartStatus(int partCount) {
		if (partCount==0) partCount++;
		this.partCount = partCount;
		this.partAvailability = new int[partCount];
	}
	
	public boolean hasStatus(Peer peer) {
		return get(peer)!=null;
	}
	
	public String toString() {
		String result ="";
		
		for(int i = 0;i <keySet().size();i++) {
			Peer p = (Peer)keySet().toArray()[i];
			result += "{ ";
			result+= p.getIP()+" : "+p.getPort()+" ";
			result+=" BitSet :  " +get(p);
			result +=" } ";
		}
		
		return result;
	}
	
	public void addPartStatus(Peer peer,JMuleBitSet partStatus) {
		try {
			if (hasStatus(peer))
				removePartStatus(peer);
			if (partStatus.getBitCount(true) == partCount)
				complete_sources++;
			else
				partial_sources++;
			super.put(peer, partStatus);
			UpdateTotalAvailability(partStatus, true);
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	public void removePartStatus(Peer peer){
		if (hasStatus(peer)) {
			JMuleBitSet bit_set = get(peer);
			if (bit_set.getBitCount(true) == partCount)
				complete_sources--;
			else
				partial_sources--;
			super.remove(peer);
			UpdateTotalAvailability(bit_set, false);
		}
	}
	
	public void clear() {
		super.clear();
		complete_sources = 0;
		partial_sources = 0;
	}
	
	public int[] getPartAvailibility(){
		return partAvailability;
	}
	
	private void UpdateTotalAvailability(JMuleBitSet bitSet,boolean add) {
		if (bitSet.getBitCount() != partCount) {
			return ;
		}
		
		for(int i = 0;i<bitSet.getBitCount();i++) {
			if (bitSet.get(i)) 
				if (add) 
					partAvailability[i]++;
				else 
					partAvailability[i]--;
		}
	}

	public int getPartCount() {
		return partCount;
	}

	public int getCompletedSources() {
		return complete_sources;
	}

	public int getPartialSources() {
		return partial_sources;
	}
}
