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

import static org.jmule.core.edonkey.ED2KConstants.FILE_QUALITY_EXCELLENT;
import static org.jmule.core.edonkey.ED2KConstants.FILE_QUALITY_FAIR;
import static org.jmule.core.edonkey.ED2KConstants.FILE_QUALITY_FAKE;
import static org.jmule.core.edonkey.ED2KConstants.FILE_QUALITY_GOOD;
import static org.jmule.core.edonkey.ED2KConstants.FILE_QUALITY_NOTRATED;
import static org.jmule.core.edonkey.ED2KConstants.FILE_QUALITY_POOR;

/**
 * Created on Oct 23, 2008
 * @author binary256
 * @version $Revision: 1.3 $
 * Last changed by $Author: binary255 $ on $Date: 2010/07/31 13:08:35 $
 */
public enum FileQuality { 
	NOTRATED { public int getAsInt() { return FILE_QUALITY_NOTRATED; } },
	FAKE 	 { public int getAsInt() { return FILE_QUALITY_FAKE; } }, 
	POOR 	 { public int getAsInt() { return FILE_QUALITY_POOR; } }, 
	FAIR 	 { public int getAsInt() { return FILE_QUALITY_FAIR; } }, 
	GOOD 	 { public int getAsInt() { return FILE_QUALITY_GOOD; } }, 
	EXCELLENT { public int getAsInt() { return FILE_QUALITY_EXCELLENT; } };
	
	/**
	 * Convert int value to FileQuality
	 * @param value
	 * @return
	 */
	public static FileQuality getAsFileQuality(int value) {
		switch(value) {
			case FILE_QUALITY_FAKE : 	return FAKE; 
			case FILE_QUALITY_POOR : 	return POOR;
			case FILE_QUALITY_FAIR : 	return FAIR;
			case FILE_QUALITY_GOOD : 	return GOOD;
			case FILE_QUALITY_EXCELLENT : return EXCELLENT;
		}
		return NOTRATED;
	}
	
	/**
	 * Convert enum value into int
	 * @return int value of enum
	 */
	public abstract int getAsInt();
};
