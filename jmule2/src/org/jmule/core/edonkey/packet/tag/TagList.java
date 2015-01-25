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
package org.jmule.core.edonkey.packet.tag;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.utils.Misc;



/**
 * 
 * @author binary256
 * @version $$Revision: 1.9 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/08/15 12:07:36 $$
 */
public class TagList implements Iterable<Tag> {

	public final static TagList EMPTY_TAG_LIST;
	
	static {
		EMPTY_TAG_LIST = new TagList();
	}
	
	private Collection<Tag> tagList = new ConcurrentLinkedQueue<Tag>();

	public TagList() {
		
	}
	
	public TagList(Collection<Tag> list) {
		this.addTag(list,true);
	}
	
	public Iterator<Tag> iterator() {

		return tagList.iterator();
	}
	
	public boolean hasTag(byte[] tagName) {
		for(Tag tag : tagList) 
			if (Arrays.equals(tag.getTagName(), tagName))
				return true;
		return false;
	}
	
	public void addTag(Tag tag) {
		if (hasTag(tag.getTagName())) return;
		tagList.add(tag);
	}
	
	public void addTag(Tag tag, boolean removeIfExist) {
		if (hasTag(tag.getTagName())) 
			removeTag(tag.getTagName());
		tagList.add(tag);
	}
	
	public void addTag(Iterable<Tag> tagList, boolean removeIfExist) {
		for(Tag tag : tagList) {
			if (hasTag(tag.getTagName()))
				removeTag(tag.getTagName());
			addTag(tag);
		}
	}
	
	public Collection<Tag> getAsCollection() {
		return tagList;
	}
	
	public int size() {
		return tagList.size();
	}
	
	public void removeTag(List<byte[]> tags) {
		for(byte[] tag : tags)
			removeTag(tag);
	}
	
	public void removeTag(byte[] tagName) {
		Tag remove = null;
		
		for(Tag tag : tagList) 
			if (Arrays.equals(tag.getTagName(), tagName))
				remove = tag;
		if (remove == null) return;
		tagList.remove(remove);
	}
	
	public Tag getTag(byte[] tagName) {
		for(Tag tag : tagList) 
			if (Arrays.equals(tag.getTagName(), tagName))
				return tag;
		return null;
	}
	
	public String toString() {
		String result ="";
		for(Tag tag : tagList) {
			result += tag + "\n";
		}
		return result;
	}
	
	public int getByteSize() {
		int result = 0;
		for(Tag tag : tagList)
			result += tag.getSize();
		return result;
	}
	
	public ByteBuffer getAsByteBuffer() {
		ByteBuffer result = Misc.getByteBuffer(getByteSize());
		for(Tag tag : tagList) {
			ByteBuffer b = tag.getAsByteBuffer();
			b.position(0);
			result.put(b);
		}
		result.position(0);
		return result;
	}
	
	
}
