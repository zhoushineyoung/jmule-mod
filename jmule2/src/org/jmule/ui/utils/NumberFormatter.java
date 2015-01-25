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

import java.text.DecimalFormat;


/**
 * Created on Sep 5, 2008
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/09/28 16:22:48 $
 */
public class NumberFormatter {

	private static DecimalFormat formatter = new DecimalFormat("0.00");
	 
	 public static String formatProgress(double progress) {
		 return formatter.format(progress)+"%";
	 }
	
	public static String formatSizeHumanReadable(long number) {
		String result = "";
		
		DecimalFormat formatter = new DecimalFormat("0");
		double d = (double)number;
		if (d>=1000) {
			result = " K";
			d = d / 1000d;
			formatter = new DecimalFormat("0.00");
		}
		
		if (d >= 1000) {
			d = d / 1000d;
			result = " M";
		}
		result = formatter.format(d) + result;
		return result;
	}
	
}
