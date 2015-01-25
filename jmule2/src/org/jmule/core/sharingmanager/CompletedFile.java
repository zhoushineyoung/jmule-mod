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

import static org.jmule.core.edonkey.ED2KConstants.FT_FILENAME;
import static org.jmule.core.edonkey.ED2KConstants.FT_FILESIZE;

import java.io.File;

import org.jmule.core.edonkey.packet.tag.IntTag;
import org.jmule.core.edonkey.packet.tag.StringTag;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.utils.Convert;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/07/31 13:08:35 $$
 */
public class CompletedFile extends SharedFile {

	public CompletedFile(String filePath){
		file = new File(filePath);
		init();
	}
	
	public CompletedFile(File file) {
		this.file = file;
		init();
	}
	
	private void init() {
		String fileName = this.getSharingName();
		if (fileName==null) fileName = file.getName();
		Tag tag = new StringTag(FT_FILENAME, fileName);
		tagList.addTag(tag);

		tag = new IntTag(FT_FILESIZE, Convert.longToInt(file.length()));
		tagList.addTag(tag);
	}

	public String toString() {
		return "["+getSharingName()+" ; "+file.length()+" ; "+" ]";
	}

	public boolean isCompleted() {
		return true;
	}
}
