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
package org.jmule.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 07-19-2008
 * @author javajox
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/08/02 14:09:27 $$
 */
public class JMRawData {

	public static final int       UDP_PORT                =        100;
	public static final int       TCP_PORT                =        200;
	public static final int       NICK_NAME               =        300;
	public static final int       DOWNLOAD_LIMIT          =        400;
	public static final int       UPLOAD_LIMIT            =        500;
	public static final int       SHARED_FOLDERS          =        600;
	
	
	Map<Integer,Object> raw_data = new HashMap<Integer,Object>();
	
	public Object getValue(int key) {
		
		return raw_data.get(key);
	}
	
	public void setValue(int key, Object value) {
		
		raw_data.put(key, value);
	}

}
