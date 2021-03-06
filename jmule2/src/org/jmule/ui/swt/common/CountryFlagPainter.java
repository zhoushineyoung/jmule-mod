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
package org.jmule.ui.swt.common;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

/**
 * Created on Aug 18, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/09/07 16:40:14 $
 */
public class CountryFlagPainter {

	private Image country_flag;
	
	public CountryFlagPainter(Image countryFlag) {
		country_flag = countryFlag;

	}
	
	public void setData(Image newFalg) {
		country_flag = newFalg;
	}

	public Image getData() {
		return country_flag;
	}
	
	public void draw(GC gc,int x, int y, int width,int height) {
		gc.drawImage(country_flag,x + (width / 2) - 15, y + 5); 
	}
}
