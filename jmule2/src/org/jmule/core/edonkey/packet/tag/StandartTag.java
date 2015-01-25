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
import static org.jmule.core.utils.Misc.*;
import static org.jmule.core.utils.Convert.*;
/**
 * 
 * Standard tag structure :
 * 
 * <table width="100%" cellspacing="0" border="1" cellpadding="0">
 *   <tbody>
 *     <tr>
 *       <td>Name</td>
 *       <td>Size in Bytes</td>
 *       <td>Description</td>
 *     </tr>
 *     <tr>
 *       <td>Tag Type</td>
 *       <td>1</td>
 *       <td>Tag Type : String,Dword</td>
 *     </tr>
 *     <tr>
 *       <td>Meta Tag name length</td>
 *       <td>2</td>
 *       <td>Meta Tag name length</td>
 *     </tr>
 *     <tr>
 *       <td>Meta Tag Name</td>
 *       <td>&lt;Meta Tag Name length&gt;</td>
 *       <td>Meta tag name : File Name, File Size, Bit Rate etc</td>
 *     </tr>
 *     <tr>
 *       <td colspan="3"><center>Tag value</center></td>
 *     </tr>
 *   </tbody>
 * </table>
 * 
 * Created on Jul 15, 2009
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary255 $ on $Date: 2010/06/15 16:47:06 $
 */
abstract class StandartTag extends AbstractTag{

	private ByteBuffer tagHeader;
	
	public StandartTag(byte tagType, byte[] tagName) {
		super(tagType, tagName);
	
		tagHeader = getByteBuffer(1 + 2 + tagName.length);
		tagHeader.put(tagType);
		tagHeader.putShort(intToShort(tagName.length));
		tagHeader.put(tagName);
	}

	public ByteBuffer getTagHeader() {
		tagHeader.position(0);
		return tagHeader;
	}
	
	public int getHeaderSize() {
		return 1 + 2 + tagName.length ;
	}
	
	
	
}
