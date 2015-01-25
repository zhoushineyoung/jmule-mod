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
package org.jmule.core.sharingmanager;

import static org.jmule.core.edonkey.ED2KConstants.*;

import java.util.Arrays;

/**
 * Created on Oct 28, 2008
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary255 $ on $Date: 2010/07/31 13:08:35 $
 */
public enum FileType {
	ANY     { public byte[] getBytes() { return null; } },
	ARHIVE  { public byte[] getBytes() { return TAG_FILE_TYPE_PROGRAM; } },
	AUDIO   { public byte[] getBytes() { return TAG_FILE_TYPE_AUDIO; } },
	CDIMAGE { public byte[] getBytes() { return TAG_FILE_TYPE_PROGRAM; } },
	DOCUMENT{ public byte[] getBytes() { return TAG_FILE_TYPE_DOC; } },
	PICTURE { public byte[] getBytes() { return TAG_FILE_TYPE_IMAGE; } },
	PROGRAM { public byte[] getBytes() { return TAG_FILE_TYPE_PROGRAM; } },
	VIDEO   { public byte[] getBytes() { return TAG_FILE_TYPE_VIDEO; } },
	COLLECTION { public byte[] getBytes() { return TAG_FILE_TYPE_COLLECTION; } };
	
	public static FileType getAsFileType(byte[] data) {
		if (Arrays.equals(TAG_FILE_TYPE_AUDIO, data)) return AUDIO;
		if (Arrays.equals(TAG_FILE_TYPE_DOC, data)) return DOCUMENT;
		if (Arrays.equals(TAG_FILE_TYPE_IMAGE, data)) return PICTURE;
		if (Arrays.equals(TAG_FILE_TYPE_PROGRAM, data)) return PROGRAM;
		if (Arrays.equals(TAG_FILE_TYPE_VIDEO, data)) return VIDEO;
		if (Arrays.equals(TAG_FILE_TYPE_COLLECTION, data)) return COLLECTION;
		return ANY;
	}
	
	public abstract byte[] getBytes();
}
