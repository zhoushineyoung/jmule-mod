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

import static org.jmule.core.jkad.JKadConstants.K;
import static org.jmule.core.jkad.utils.Utils.getNearestContact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.jkad.ClientID;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.utils.Utils;



/**
 * Created on Dec 28, 2008
 * @author binary256
 * @version $Revision: 1.5 $
 * Last changed by $Author: binary255 $ on $Date: 2010/06/28 17:46:03 $
 */
public class KBucket {
	
	private Collection<KadContact> contact_list = new ConcurrentLinkedQueue<KadContact>();
	
	public KBucket() {
		
	}
	
	public void add(KadContact contact) { 
		contact_list.add(contact);
	}
	
	public void remove(KadContact contact) {
		contact_list.remove(contact);
	}
	
	public boolean hasContact(KadContact k) {
		for(KadContact c : contact_list)
			if (c.equals(k)) return true;
		return false;
	}
	
	public KadContact getContact(Int128 contactID) {
		for(KadContact contact : contact_list)
			if (contact.getContactID().equals(contactID)) return contact;
		
		return null;
	}
	
	
	public List<KadContact> getRandomContacts(int contactCount){
		List<KadContact> list = new ArrayList<KadContact>();
		
		if (contact_list.size()<=contactCount) {
			list.addAll(contact_list);
			return list;
		}
		
		Collection<Integer> add_id = Utils.getRandomValues(contact_list.size(), contactCount, true);
		KadContact[] array = contact_list.toArray(new KadContact[0]);
		for(Integer id : add_id) 
			list.add(array[id]);
		
		return list;
	}
	
	/**
	 * Get nearest contacts to target
	 * @param targetID XOR distance
	 * @param contactCount
	 * @return
	 */
	public List<KadContact> getNearestContacts(Int128 targetID, int contactCount) {
		List<KadContact> list = new ArrayList<KadContact>();
		Set<ClientID> id_set = new HashSet<ClientID>();

		do {		
			KadContact contact = getNearestContact(targetID, contact_list, id_set);
			if (contact == null) break;
			id_set.add(contact.getContactID());
			list.add(contact);
		} while (list.size() < contactCount);

		return list;
	}
	
	public void clear() {
		contact_list.clear();
	}
	
	public String toString() {
		String result = " [ " + contact_list.size()+" :    ";
		
		for(KadContact contact : contact_list)
			result += contact.getContactAddress() + "  ";
		
		return result + " ]";
	}
	
	public boolean isFull() {
		return contact_list.size() == K;
	}
	
	public int size() {
		return contact_list.size();
	}
	
	public Collection<KadContact> getContacts() {
		return contact_list;
	}

}
