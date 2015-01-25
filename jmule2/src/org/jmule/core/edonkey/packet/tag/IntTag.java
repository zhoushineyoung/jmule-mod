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

import static org.jmule.core.edonkey.ED2KConstants.TAGTYPE_UINT32;

import java.nio.ByteBuffer;

import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;
/**
 * Created on Jul 15, 2009
 * @author binary256
 * @version $Revision: 1.3 $
 * Last changed by $Author: binary255 $ on $Date: 2010/07/31 16:00:42 $
 */
public class IntTag extends StandartTag implements NumberTag {

	private int tagValue;
	
	public IntTag(byte[] tagName, int tagValue) {
		super(TAGTYPE_UINT32, tagName);
		this.tagValue = tagValue;
	}

	ByteBuffer getValueAsByteBuffer() {
		ByteBuffer result = Misc.getByteBuffer(4);
		result.putInt(tagValue);
		result.position(0);
		return result;
	}


	int getValueLength() {
		return 4;
	}


	public Integer getValue() {
		return tagValue;
	}


	public void setValue(Object object) {
		tagValue = (Integer) object;
	}

	public long getNumber() {
		return Convert.intToLong(tagValue);
	}

	public void setNumber(long value) {
		tagValue = Convert.longToInt(value);
	}
	
	

}
