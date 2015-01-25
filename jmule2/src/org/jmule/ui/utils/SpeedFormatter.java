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

import java.text.NumberFormat;

import org.jmule.ui.UIConstants;

/**
 * Created on Aug 11, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/08/26 19:39:47 $
 */
public class SpeedFormatter {
	
	private static boolean use_units_rate_bits = false;
	
	final public static int UNIT_B  = 0;
	final public static int UNIT_KB = 1;
	final public static int UNIT_MB = 2;
	final public static int UNIT_GB = 3;
	final public static int UNIT_TB = 4;
	
	private static int unitsStopAt = UNIT_TB;
	
	final private static int UNITS_PRECISION[] =	 {	 0, // B
        1, //KB
        2, //MB
        2, //GB
        3 //TB
     };
	
	private static String[] units_rate = new String[5];
	private static String[] units = new String[5];
	
	final private static NumberFormat[]	cached_number_formats = new NumberFormat[20]; 
	
	static {
		units_rate[UNIT_B] = "B";
		units_rate[UNIT_KB] = "KB";
		units_rate[UNIT_MB] = "MB";
		units_rate[UNIT_GB] = "GB";
		units_rate[UNIT_TB] = "TB";
		
		units[UNIT_B] = "B";
		units[UNIT_KB] = "KB";
		units[UNIT_MB] = "MB";
		units[UNIT_GB] = "GB";
		units[UNIT_TB] = "TB";
	}
	

	
	 public static String formatSpeed(float speed) {
		 long n = (long)speed;
		    if (n < 1024)
		      return n + " B/s";
		    if (n < 1024 * 1024)
		      return (n / 1024) + "." + ((n % 1024) / 103) + " kB/s";
		    if (n < 1024 * 1024 * 1024)
		      return (n / (1024 * 1024))
		        + "."
		        + ((n % (1024 * 1024)) / (103 * 1024))
		        + " MB/s";
		    if (n < 1024l * 1024l * 1024l * 1024l)
		      return (n / (1024l * 1024l * 1024l))
		        + "."
		        + ((n % (1024l * 1024l * 1024l)) / (103l * 1024l * 1024l))
		        + " GB/s";
		    return "A lot !!!";
	}
	 
	public static String formatByteCountToKiBEtcPerSec(long	n, boolean bTruncateZeros) {
			return( formatByteCountToKiBEtc(n,true, bTruncateZeros));
	}
	 
	public static String formatByteCountToKiBEtc(long n, boolean rate, boolean bTruncateZeros) {
		return formatByteCountToKiBEtc(n, rate, bTruncateZeros, -1);
	}
	
	public static String formatByteCountToKiBEtc(long n, boolean rate,boolean bTruncateZeros, int precision) {
		double dbl = (rate && use_units_rate_bits) ? n * 8 : n;

	  	int unitIndex = UNIT_B;
	  	
	  	while (dbl >= 1024 && unitIndex < unitsStopAt){ 
	  	
		  dbl /= 1024L;
		  unitIndex++;
		}
	  	
	  if (precision < 0) {
	  	precision = UNITS_PRECISION[unitIndex];
	  }
			 
	  // round for rating, because when the user enters something like 7.3kbps
		// they don't want it truncated and displayed as 7.2  
		// (7.3*1024 = 7475.2; 7475/1024.0 = 7.2998;  trunc(7.2998, 1 prec.) == 7.2
	  //
		// Truncate for rest, otherwise we get complaints like:
		// "I have a 1.0GB torrent and it says I've downloaded 1.0GB.. why isn't 
		//  it complete? waaah"

		return formatDecimal(dbl, precision, bTruncateZeros, rate)
				+ (rate ? units_rate[unitIndex] : units[unitIndex]);
	}
	
	  /**
	   * Format a real number
	   * 
	   * @param value real number to format
	   * @param precision max # of digits after the decimal place
	   * @param bTruncateZeros remove any trailing zeros after decimal place
	   * @param bRound Whether the number will be rounded to the precision, or
	   *                truncated off.
	   * @return formatted string
	   */
		public static String
		formatDecimal(
				double value,
				int precision,
				boolean bTruncateZeros,
				boolean bRound)
		{
			if (Double.isNaN(value) || Double.isInfinite(value)) {
				return UIConstants.INFINITY_STRING;
			}

			double tValue;
			if (bRound) {
				tValue = value;
			} else {
				// NumberFormat rounds, so truncate at precision
				if (precision == 0) {
					tValue = (long) value;
				} else {
					double shift = Math.pow(10, precision);
					tValue = ((long) (value * shift)) / shift;
				}
			}

			int cache_index = (precision << 2) + ((bTruncateZeros ? 1 : 0) << 1)
					+ (bRound ? 1 : 0);

			NumberFormat nf = null;

			if (cache_index < cached_number_formats.length) {
				nf = cached_number_formats[cache_index];
			}

			if (nf == null) {
				nf = NumberFormat.getNumberInstance();
				nf.setGroupingUsed(false); // no commas
				if (!bTruncateZeros) {
					nf.setMinimumFractionDigits(precision);
				}
				if (bRound) {
					nf.setMaximumFractionDigits(precision);
				}

				if (cache_index < cached_number_formats.length) {
					cached_number_formats[cache_index] = nf;
				}
			}

			return nf.format(tValue);
		}
	
}
