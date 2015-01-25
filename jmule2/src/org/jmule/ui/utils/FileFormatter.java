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
package org.jmule.ui.utils;

import static org.jmule.core.edonkey.ED2KConstants.*;

import java.text.DecimalFormat;
import java.util.Arrays;

import org.jmule.ui.localizer._;
/**
 * Created on Aug 16, 2008
 * @author binary256
 * @version $Revision: 1.5 $
 * Last changed by $Author: binary255 $ on $Date: 2010/07/31 13:08:35 $
 */
public class FileFormatter {

	 public static String formatFileSize(long fileSzie) {
		 long n = fileSzie;
		    if (n < 1024)
		      return n + " B";
		    if (n < 1024 * 1024)
		      return (n / 1024) + "." + ((n % 1024) / 103) + " KB";
		    if (n < 1024 * 1024 * 1024)
		      return (n / (1024 * 1024))
		        + "."
		        + ((n % (1024 * 1024)) / (103 * 1024))
		        + " MB";
		    if (n <= 1024l * 1024l * 1024l * 1024l)
		      return (n / (1024l * 1024l * 1024l))
		        + "."
		        + ((n % (1024l * 1024l * 1024l)) / (103l * 1024l * 1024l))
		        + " GB";
		    return "A lot !!!";
	}
	
	 private static DecimalFormat formatter = new DecimalFormat("0.00");
	 
	 public static String formatProgress(double progress) {
		 return formatter.format(progress)+"%";
	 }
	 
	public static final String NO_EXTENSION = "no extension";
	
	public static String getFileExtension(String fileName) {
		int id = fileName.length()-1;
		while(id>0) { 
			if (fileName.charAt(id)=='.') break;
			id--;
		}
		String extension = NO_EXTENSION;
		if (id!=0)
			extension = fileName.substring(id+1, fileName.length());
	
		return extension;
	}
	
	public static String formatMimeType(byte[] fileType) {
		
		if (Arrays.equals(TAG_FILE_TYPE_AUDIO,fileType)) 
			return _._("mainwindow.searchtab.column.filetype.audio");
		
		if (Arrays.equals(TAG_FILE_TYPE_VIDEO,fileType)) 
			return _._("mainwindow.searchtab.column.filetype.video");
		
		if (Arrays.equals(TAG_FILE_TYPE_IMAGE,fileType)) 
			return _._("mainwindow.searchtab.column.filetype.image");
		
		if (Arrays.equals(TAG_FILE_TYPE_DOC,fileType)) 
			return _._("mainwindow.searchtab.column.filetype.doc");

		if (Arrays.equals(TAG_FILE_TYPE_PROGRAM,fileType)) 
			return _._("mainwindow.searchtab.column.filetype.program");
		
		if (Arrays.equals(TAG_FILE_TYPE_ARC,fileType)) 
			return _._("mainwindow.searchtab.column.filetype.archive");
		
		if (Arrays.equals(TAG_FILE_TYPE_ISO,fileType)) 
			return _._("mainwindow.searchtab.column.filetype.iso");
		
		return _._("mainwindow.searchtab.column.filetype.unknown");
	}
	
}
