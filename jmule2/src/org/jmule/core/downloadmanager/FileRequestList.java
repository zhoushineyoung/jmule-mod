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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.jmule.core.peermanager.Peer;

/**
 * Map Peer and requested fragments
 * Created on 07-19-2008
 * @author binary256
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/08/15 12:30:40 $$
 */
public class FileRequestList extends Hashtable<Peer,FragmentList>{
	
	public boolean hasPeer(Peer p) {
		if (this.get(p)==null) return false;
		return true;
	}

	public Collection<FileFragment> getFragmentsFromSegment(long start,long end) {
		Collection<FileFragment> result = new LinkedList<FileFragment>();
		for (int i = 0; i < this.size(); i++) {
			FragmentList fl = (FragmentList) this.values().toArray()[i];
			Collection<FileFragment> j = fl.getFragmentsFromSegment(start, end);
			for (Iterator<FileFragment> x = j.iterator(); x.hasNext();) {
				result.add(x.next());
			}
		}
		return result;
	}
	
	public void addFragment(Peer peer,long begin,long end) {
		FragmentList fList;
		if (!hasPeer(peer)) {
			fList = new FragmentList();
			super.put(peer, fList);
		}else 
			fList = super.get(peer);
		if (!fList.hasFragment(begin, end))
			fList.add(new FileFragment(begin,end));
			
	}
	
	public boolean hasFragment(long begin,long end) {
		for(int i = 0;i<this.values().size();i++) {
			FragmentList fl = (FragmentList) this.values().toArray()[i];
			if (fl.hasFragment(begin, end)) 
					return true;
		}
		return false;
	}
	
	private FragmentList getFragmentList(long begin,long end) {
		if (!hasFragment(begin,end)) return null;
		for(int i = 0;i<this.values().size();i++) {
			FragmentList fl = (FragmentList) this.values().toArray()[i];
			if (fl.hasFragment(begin, end))
				return fl;
		}
		return null;
	}
	
	public FragmentList getFragmentList(Peer peer) {
		if (!hasPeer(peer)) return null;
		return super.get(peer);
	}
	
	public void splitFragment(Peer peer,long begin,long end) {
		if (!hasPeer(peer)) return ;
		
		FragmentList fl = this.get(peer);
		fl.splitFragment(begin,end);
	}
	
	public long getUnrequestedPos(long pos) {
		for(int i = 0;i<this.values().size();i++) {
			FragmentList fl = (FragmentList) this.values().toArray()[i];
			for(int j = 0;j<fl.size();j++){
				FileFragment f = fl.get(j);
				if (f.containPos(pos))
					pos = f.getEnd();
			}
		}
		return pos;
	}
	
	public String toString() {
		String result = "{ ";
		for (int i = 0; i < this.keySet().toArray().length; i++) {
			Peer p = (Peer) this.keySet().toArray()[i];
			FragmentList f = this.get(p);
			result += " " + p.getIP() + " : " + p.getPort() + " : " + f + "";
		}
		result += " }";
		return result;
	}

}
