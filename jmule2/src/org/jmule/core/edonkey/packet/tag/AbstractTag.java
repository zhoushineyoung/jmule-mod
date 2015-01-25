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

import static org.jmule.core.utils.Misc.getByteBuffer;

import java.nio.ByteBuffer;

import org.jmule.core.utils.Convert;

/**
 * Created on Jul 15, 2009
 * @author binary256
 * @version $Revision: 1.3 $
 * Last changed by $Author: binary255 $ on $Date: 2010/06/15 16:47:06 $
 */
abstract class AbstractTag implements Tag {
	protected byte tagType;
	protected byte[] tagName;
	
	public AbstractTag(byte tagType, byte[] tagName) {
		super();
		this.tagType = tagType;
		this.tagName = tagName;
	}

	public byte getType() {
		return tagType;
	}
	
	public byte[] getTagName() {
		return tagName;
	}
	
	abstract ByteBuffer getValueAsByteBuffer();
	abstract int getValueLength();
	
	public int getSize() {
		return getHeaderSize() + getValueLength();
	}
	
	public ByteBuffer getAsByteBuffer() {
		ByteBuffer result = getByteBuffer(getSize());
		result.put(getTagHeader());
		result.put(getValueAsByteBuffer());

		result.position(0);
		return result;
	}
	
	public String toString() {
		return "[ "+Convert.byteToHexString(getAsByteBuffer().array(), " 0x") + " ] = [ " + getValue() +" ]";
	}
	
}
