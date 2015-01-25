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
package org.jmule.ui.localizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * 
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/10/16 18:20:55 $$
 */
public class Localizer {

	private static ResourceBundle resources = null;
	
	public static void initialize() {
		InputStream input_stream = (Localizer.class.getClassLoader().getResourceAsStream("org/jmule/ui/resources/internat/Language_en_US.properties"));
		try {
			resources = new PropertyResourceBundle(input_stream);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public static String _(String key) {
		
		return getString(key);
		
	}
	
	/**
	 * Expands a message text and replaces occurrences of %1 with first param, %2 with second...
	 * @param key
	 * @param params
	 * @return
	 */
	public static String _(String key,String... params) {
		return getString(key, params);
	}
	
	public static String getString(String key) {
		try {
			return resources.getString(key);
		}catch(MissingResourceException e) {
			return "";
		}
	}
	
	/**
	  * Expands a message text and replaces occurrences of %1 with first param, %2 with second...
	  * @param key
	  * @param params
	  * @return
	  */
	public static String getString(String key,String... params ) {
	  	String	res = getString(key);
	  	
	  	for(int i=0;i<params.length;i++){
	  		
	  		String	from_str 	= "%" + (i+1);
	  		String	to_str		= params[i];
	  		
	  		res = replaceStrings( res, from_str, to_str );
	  	}
	  	
	  	return( res );
	}

	private static String replaceStrings(String	str,String	f_s,String	t_s ) {
	  	int	pos = 0;
	  	
	  	String	res  = "";
	  	
	  	while( pos < str.length()){
	  	
	  		int	p1 = str.indexOf( f_s, pos );
	  		
	  		if ( p1 == -1 ){
	  			
	  			res += str.substring(pos);
	  			
	  			break;
	  		}
	  		
	  		res += str.substring(pos, p1) + t_s;
	  		
	  		pos = p1+f_s.length();
	  	}
	  	
	  	return( res );
	}
	  
}
