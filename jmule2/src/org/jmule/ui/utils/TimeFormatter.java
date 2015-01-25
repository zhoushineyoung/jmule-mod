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

import org.jmule.ui.UIConstants;

/**
 * Created on Aug, 07 2008
 * @author Olivier
 * @author binary256
 */
public class TimeFormatter {
  // XXX should be i18n'd
	static final String[] TIME_SUFFIXES = { "s", "m", "h", "d" };

	/**
	 * Format time into two time sections, the first chunk trimmed, the second
	 * with always with 2 digits.  Sections are *d, **h, **m, **s.  Section
	 * will be skipped if 0.   
	 * 
	 * @param time time in ms
	 * @return Formatted time string
	 */
	public static String format(long time) {
			if (time >= UIConstants.INFINITY_AS_INT)
				return UIConstants.INFINITY_STRING;

		if (time < 0)
			return "";

		// secs, mins, hours, days
		int[] vals = { (int) time % 60, (int) (time / 60) % 60,
				(int) (time / 3600) % 24, (int) (time / 86400) };

		int end = vals.length - 1;
		while (vals[end] == 0 && end > 0) {
			end--;
		}
		
		String result = vals[end] + TIME_SUFFIXES[end];

		// skip until we have a non-zero time section
		do {
			end--;
		} while (end >= 0 && vals[end] == 0);
		
		if (end >= 0)
			result += " " + twoDigits(vals[end]) + TIME_SUFFIXES[end];

		return result;
	}

    public static String formatColon(long time) {
      if (time >= UIConstants.INFINITY_AS_INT) 
    	  return UIConstants.INFINITY_STRING;
      if (time < 0) 
    	  return "";

      int secs = (int) time % 60;
      int mins = (int) (time / 60) % 60;
      int hours = (int) (time /3600) % 24;
      int days = (int) (time / 86400);
      
      String result = "";
      if (days > 0) result = days + "d ";
      result += twoDigits(hours) + ":" + twoDigits(mins) + ":" + twoDigits(secs);

      return result;
    }
    
    public static String twoDigits(int i) {
      return (i < 10) ? "0" + i : String.valueOf(i);
    }
    
    public static void main(String... args) {
    	long time =99000;
    	String str = formatColon(time);
    	System.out.println(str+"");
    }
}
