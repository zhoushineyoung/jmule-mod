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
package org.jmule.ui.swt;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;
import org.jmule.core.sharingmanager.FileQuality;
import org.jmule.ui.FlagPack;
import org.jmule.ui.UIConstants;
import org.jmule.ui.UIImageRepository;
import org.jmule.ui.FlagPack.FlagSize;

/**
 * Created on Aug 12, 2008
 * @author binary256
 * @version $Revision: 1.7 $
 * Last changed by $Author: binary255 $ on $Date: 2010/01/01 14:20:48 $
 */
public class SWTImageRepository {

	public static Image getImage(String	name ) {
		InputStream input_stream = (SWTImageRepository.class.getClassLoader().getResourceAsStream("org/jmule/ui/resources/" + name));
		if ( input_stream == null )
			input_stream = (SWTImageRepository.class.getClassLoader().getResourceAsStream("org/jmule/ui/resources/image_not_found.png"));
		Image image = new Image(SWTThread.getDisplay(),input_stream);
		try {
			input_stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public static Image getIconByExtension(String fileName) {
		int id = fileName.length()-1;
		while(id>0) { 
			if (fileName.charAt(id)=='.') break;
			id--;
		}
		String extension = "no extension";
		if (id!=0)
			extension = fileName.substring(id+1, fileName.length());
		
		Program program = Program.findProgram(extension);
		if (program == null) {
			
			InputStream stream = UIConstants.getIconByExtension(extension);
			Image img = new Image(SWTThread.getDisplay(),stream);
			
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			stream = null;
			return img;
		}
		
		ImageData image_data = program.getImageData();
		if (image_data == null) { 
			return getImage("image_not_found.png");
		}
		image_data = image_data.scaledTo(16, 16);
		Image image = new Image(SWTThread.getDisplay(),image_data);
		return image;
	}
	
	public static Image getMenuImage(String name) {
		InputStream input_stream = (SWTImageRepository.class.getClassLoader().getResourceAsStream("org/jmule/ui/resources/menuicons/" + name));
		if ( input_stream == null )
			input_stream = (SWTImageRepository.class.getClassLoader().getResourceAsStream("org/jmule/ui/resources/image_not_found.png"));
		Image image = new Image(SWTThread.getDisplay(),input_stream);
		try {
			input_stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public static Image getFlagByAddress(String address,FlagSize size) {
		InputStream stream = FlagPack.getFlagAsInputStreamByIP(address, size);
		Image image = new Image(SWTThread.getDisplay(),stream);
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public static Image getImage(FileQuality fileQuality) {
		String path = UIImageRepository.getImagePath(fileQuality);
		InputStream input_stream = (SWTImageRepository.class.getClassLoader().getResourceAsStream(path));
		if ( input_stream == null )
			input_stream = (SWTImageRepository.class.getClassLoader().getResourceAsStream("org/jmule/ui/resources/image_not_found.png"));
		Image image = new Image(SWTThread.getDisplay(),input_stream);
		try {
			input_stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
}
