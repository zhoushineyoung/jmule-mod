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
package org.jmule.core.jkad.indexer;

import org.jmule.core.jkad.Int128;

/**
 * Created on Jan 27, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public class KeywordIndex extends Index{

	public KeywordIndex(Int128 id) {
		super(id);
	}
	
	/*private Int128 keywordID;
	private Map<Int128, SourceRecord> keywordSources = new Hashtable<Int128, SourceRecord>();
			
	public KeywordIndex(Int128 keywordID) {
		this.keywordID = keywordID;
	}
	
	public Int128 getKeywordID() {
		return keywordID;
	}
	
	public void addName(Int128 sourceID, String name) {
		SourceRecord record = null;
		if (!keywordSources.containsKey(sourceID)) {
			keywordSources.put(sourceID, new SourceRecord(sourceID));
		}
		record = keywordSources.get(sourceID);
		record.addName(name);
	}
	
	public void addPublisher(Int128 sourceID, IPAddress address) {
		SourceRecord record = null;
		if (!keywordSources.containsKey(sourceID)) {
			keywordSources.put(sourceID, new SourceRecord(sourceID));
		}
		record = keywordSources.get(sourceID);
		record.addPublisherIP(address);
	}
	
	void addTagList(Int128 sourceID, TagList tagList) {
		SourceRecord record = null;
		if (!keywordSources.containsKey(sourceID)) {
			keywordSources.put(sourceID, new SourceRecord(sourceID));
		}
		record = keywordSources.get(sourceID);
		record.setTagList(tagList);
	}
	
	public List<Source> getKeywordSources() {
		List<Source> result = new LinkedList<Source>();
		
		return result;
	}
	
	private class SourceRecord {
		private List<String> nameList = new LinkedList<String>();
		private List<IPAddress> publisherIPs = new LinkedList<IPAddress>();
		private long lifeTime;
		private Int128 sourceID;
		private TagList tagList;
		
		public SourceRecord(Int128 sourceID) {
			this.sourceID = sourceID;
		}
		
		public List<String> getNameList() {
			return nameList;
		}
		public void addName(String name) {
			nameList.add(name);
		}
		public long getLifeTime() {
			return lifeTime;
		}
		public void setLifeTime(long lifeTime) {
			this.lifeTime = lifeTime;
		}
		public Int128 getSourceID() {
			return sourceID;
		}
		public void setSourceID(Int128 sourceID) {
			this.sourceID = sourceID;
		}

		public TagList getTagList() {
			return tagList;
		}

		public void setTagList(TagList tagList) {
			this.tagList = tagList;
		}

		public List<IPAddress> getPublishers() {
			return publisherIPs;
		}

		public void addPublisherIP(IPAddress address) {
			if (publisherIPs.contains(address)) return ;
			publisherIPs.add(address);
		}
		
	}*/
}