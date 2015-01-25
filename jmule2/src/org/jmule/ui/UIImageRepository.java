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
package org.jmule.ui;

import java.io.InputStream;

import org.jmule.core.sharingmanager.FileQuality;


/**
 * 
 * @author parg
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/10/24 15:24:36 $$
 */
public class 
UIImageRepository
{
	public static InputStream 
	getImageAsStream(
			String	name ) 
	{
		InputStream input_stream = (UIImageRepository.class.getClassLoader().getResourceAsStream("org/jmule/ui/resources/" + name));
		if ( input_stream == null )
			input_stream = (UIImageRepository.class.getClassLoader().getResourceAsStream("org/jmule/ui/resources/image_not_found.png"));
		return input_stream;
	}
	
	public static String getImagePath(FileQuality fileQuality) {
		String path="org/jmule/ui/resources/file_quality/";
		String image="fq_not_rated.png";
		
		switch(fileQuality) {
			case FAKE : image = "fq_fake.png"; break;
			case POOR : image = "fq_poor.png"; break;
			case FAIR : image = "fq_fair.png"; break;
			case GOOD : image = "fq_good.png"; break;
			case EXCELLENT : image = "fq_excellent.png"; break;
		}
		
		return path+image;
	}
}
