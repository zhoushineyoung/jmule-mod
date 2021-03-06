/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.jkad.publisher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jmule.core.jkad.ContactAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.JKadException;
import org.jmule.core.jkad.lookup.Lookup;
import org.jmule.core.jkad.lookup.LookupTask;
import org.jmule.core.jkad.packet.KadPacket;
import org.jmule.core.jkad.packet.PacketFactory;
import org.jmule.core.jkad.publisher.Publisher.PublishTaskListener;
import org.jmule.core.jkad.routingtable.KadContact;


/**
 * Created on Jan 14, 2009
 * @author binary256
 * @version $Revision: 1.12 $
 * Last changed by $Author: binary255 $ on $Date: 2010/07/28 13:13:43 $
 */
public class PublishKeywordTask extends PublishTask {

	private LookupTask lookup_task;
	private Collection<Collection<PublishItem>> publish_items;
	private String keyword;
	
	public PublishKeywordTask(PublishTaskListener listener,Int128 keywordID, Collection<PublishItem> publishItems, String keyword) {
		super(keywordID,listener);
		publish_items = new ArrayList<Collection<PublishItem>>();
		this.keyword = keyword;
		Collection<PublishItem> list = new ArrayList<PublishItem>();
		int i = 0;
		for(PublishItem p : publishItems) {
			if (i!=0)
				if (i % JKadConstants.PUBLISH_KEYWORD_IN_PACKET == 0) {
					publish_items.add(list);
					list = new ArrayList<PublishItem>();
				}
			i++;
			list.add(p);
		}
		if (list.size() != 0)
			publish_items.add(list);
	}
	
	public String getKeyword() {
		return keyword;
	}
		
	public void start() throws JKadException {
		if (lookup_task!=null)
			if (lookup_task.isLookupStarted()) return;
		isStarted = true;
		lookup_task = new LookupTask(RequestType.STORE, publishID, JKadConstants.LOOKUP_STORE_KEYWORD_TIMEOUT) {
			
			public void lookupTimeout() {
				isStarted = false;
				updatePublishTime();
				task_listener.taskTimeOut(task_instance);
			}

			public void processToleranceContacts(ContactAddress sender, List<KadContact> results) {
				for(KadContact contact : results) {
					KadPacket packet = null;
					if (!contact.supportKad2())
						for(Collection<PublishItem> items : publish_items) {
							packet = PacketFactory.getPublish1ReqPacket(targetID, items);
							_network_manager.sendKadPacket(packet, contact.getIPAddress(), contact.getUDPPort());
						}
					else
						for(Collection<PublishItem> items : publish_items) {
							packet = PacketFactory.getPublishKeyReq2Packet(targetID, items);
							_network_manager.sendKadPacket(packet, contact.getIPAddress(), contact.getUDPPort());
						}					
				}
			}
			
			public void lookupTerminated() {
				isStarted = false;
				updatePublishTime();
				task_listener.taskStopped(task_instance);
			}
			
		};

		Lookup.getSingleton().addLookupTask(lookup_task);
		task_listener.taskStarted(task_instance);
	}

	public void stop() {
		if (!isStarted) return;
		isStarted = false; 
		Lookup.getSingleton().removeLookupTask(publishID);
	}

}
