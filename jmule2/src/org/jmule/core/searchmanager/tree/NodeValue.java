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
package org.jmule.core.searchmanager.tree;

import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Map;

import org.jmule.core.sharingmanager.FileType;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

import static org.jmule.core.edonkey.ED2KConstants.*;
import static org.jmule.core.searchmanager.tree.NodeValue.NodeType.*;
/**
 * Tree node value.
 * Created on Oct 26, 2008
 * @author binary256
 * @version $Revision: 1.5 $
 * Last changed by $Author: binary255 $ on $Date: 2010/07/31 13:08:35 $
 */

public class NodeValue {
	public enum NodeType {
		NOT, OR, AND, FILE_NAME, FILETYPE, MINSIZE, MAXSIZE, MINAVAILABILITY, MAXAVAILABILITY, MINCOMPLETESRC, MAXCOMPLETESRC, EXTENSION
	}
	
	private Map<String,Object> data = new Hashtable<String,Object>();
	
	private NodeType type;
	
	public NodeValue(NodeType type) {
		this.type = type;
	}
	
	public NodeType getType() {
		return type;
	}
	
	public Object getValue(String key) {
		return data.get(key);
	}
	
	public void setValue(String key,Object value) {
		data.put(key, value);
	}
	
	public void setValue(Object value) {
		setValue(Tree.DATA_KEY, value);
	}
	
	public byte[] getBytes() {
		if (type == NOT) return SEARCH_NOT; 
		if (type == OR)  return SEARCH_OR;
		if (type == AND) return SEARCH_AND;
		if (type == FILE_NAME) {
			String str = (String)getValue(Tree.DATA_KEY);
			byte[] str_content = str.getBytes();
			ByteBuffer data = Misc.getByteBuffer(1 + 2 + str_content.length);
			data.position(0);
			data.put(SEARCH_BY_NAME);
			data.putShort(Convert.intToShort(str_content.length));
			data.put(str_content);
			return data.array();
		}
		if (type == FILETYPE) {
			FileType type = (FileType)getValue(Tree.DATA_KEY);
			ByteBuffer data = Misc.getByteBuffer(1 + 2 + type.getBytes().length + 2 + TAG_NAME_FILE_TYPE.length);
			data.position(0);
			data.put(SEARCH_BY_META);
			data.putShort(Convert.intToShort(type.getBytes().length));
			data.put(type.getBytes());
			data.putShort(Convert.intToShort(TAG_NAME_FILE_TYPE.length));
			data.put(TAG_NAME_FILE_TYPE);
			return data.array();
		}
		
		if (type == MINSIZE) {
			ByteBuffer data = Misc.getByteBuffer(1 + 4 + 1 + 2 + TAG_NAME_SIZE.length);
			data.position(0);
			data.put(SEARCH_BY_LIMIT);
			long v = (Long)getValue(Tree.DATA_KEY);
			data.putInt(Convert.longToInt(v));
			data.put(LIMIT_MIN);
			data.putShort(Convert.intToShort(TAG_NAME_SIZE.length));
			data.put(TAG_NAME_SIZE);
			return data.array();
		}
		
		if (type == MAXSIZE) {
			ByteBuffer data = Misc.getByteBuffer(1 + 4 + 1 + 2 + TAG_NAME_SIZE.length);
			data.position(0);
			data.put(SEARCH_BY_LIMIT);
			long v = (Long)getValue(Tree.DATA_KEY);
			data.putInt(Convert.longToInt(v));
			data.put(LIMIT_MAX);
			data.putShort(Convert.intToShort(TAG_NAME_SIZE.length));
			data.put(TAG_NAME_SIZE);
			return data.array();
		}
		
		if (type == MINAVAILABILITY) {
			ByteBuffer data = Misc.getByteBuffer(1 + 4 + 1 + 2 + TAG_NAME_AVIABILITY.length);
			data.position(0);
			data.put(SEARCH_BY_LIMIT);
			long v = (Long)getValue(Tree.DATA_KEY);
			data.putInt(Convert.longToInt(v));
			data.put(LIMIT_MIN);
			data.putShort(Convert.intToShort(TAG_NAME_AVIABILITY.length));
			data.put(TAG_NAME_AVIABILITY);
			return data.array();
		}
		if (type == MAXAVAILABILITY) {
			ByteBuffer data = Misc.getByteBuffer(1 + 4 + 1 + 2 + TAG_NAME_AVIABILITY.length);
			data.position(0);
			data.put(SEARCH_BY_LIMIT);
			long v = (Long)getValue(Tree.DATA_KEY);
			data.putInt(Convert.longToInt(v));
			data.put(LIMIT_MAX);
			data.putShort(Convert.intToShort(TAG_NAME_AVIABILITY.length));
			data.put(TAG_NAME_AVIABILITY);
			return data.array();
		}
		if (type == MINCOMPLETESRC) {
			ByteBuffer data = Misc.getByteBuffer(1 + 4 + 1 + 2 + TAG_NAME_COMPLETESRC.length);
			data.position(0);
			data.put(SEARCH_BY_LIMIT);
			long v = (Long)getValue(Tree.DATA_KEY);
			data.putInt(Convert.longToInt(v));
			data.put(LIMIT_MIN);
			data.putShort(Convert.intToShort(TAG_NAME_COMPLETESRC.length));
			data.put(TAG_NAME_COMPLETESRC);
			return data.array();
		}
		if (type == MAXCOMPLETESRC) {
			ByteBuffer data = Misc.getByteBuffer(1 + 4 + 1 + 2 + TAG_NAME_COMPLETESRC.length);
			data.position(0);
			data.put(SEARCH_BY_LIMIT);
			long v = (Long)getValue(Tree.DATA_KEY);
			data.putInt(Convert.longToInt(v));
			data.put(LIMIT_MAX);
			data.putShort(Convert.intToShort(TAG_NAME_COMPLETESRC.length));
			data.put(TAG_NAME_COMPLETESRC);
			return data.array();
		}
		if (type == EXTENSION) {
			String str = (String)getValue(Tree.DATA_KEY);
			ByteBuffer data = Misc.getByteBuffer(1 + 2 + str.length() + 2 + TAG_NAME_FORMAT.length);
			data.position(0);
			data.put(SEARCH_BY_LIMIT);
			data.putShort(Convert.intToShort(str.length()));
			data.put(str.getBytes());
			data.putShort(Convert.intToShort(TAG_NAME_FORMAT.length));
			data.put(TAG_NAME_FORMAT);
			return data.array();
		}
		return null;
	}

}
