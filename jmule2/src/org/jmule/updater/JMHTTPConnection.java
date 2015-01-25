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

package org.jmule.updater;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created on Aug 20, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/08/27 17:13:51 $
 */
public class JMHTTPConnection {

	private String address;
	
	private Map<String,String> post_values = new Hashtable<String,String>();
	private Map<String,String> get_values = new Hashtable<String,String>();
	private int http_response_code;
	
	public JMHTTPConnection(String address) {
		this.address = address;
	}
	
	public void addGetValue(String key,String value) {
		get_values.put(key, value);
	}
	
	public void addPostValue(String key,String value) {
		post_values.put(key,value);
	}
	
	public int getHttpResponseCode() {
		return http_response_code;
	}
	
	public String sendQuery() throws JMHTTPConnectionException {
		try {
			String connect_address = address;
			if (!get_values.isEmpty()) {
				boolean first = true;
				for(String key : get_values.keySet()) {
					String request_value = URLEncoder.encode(key, "UTF-8") + "="+URLEncoder.encode(get_values.get(key), "UTF-8") ;
					if (first) {
						connect_address+="?";
						connect_address+=request_value;
						first = false;
					}else
						connect_address+="&"+request_value;
				}
			}
			String post_data="";
			boolean first = true;
			for(String key : post_values.keySet()) {
				String request_value = URLEncoder.encode(key, "UTF-8") + "="+URLEncoder.encode(post_values.get(key), "UTF-8") ;
				if (first) {
					post_data+=request_value;
					first = false;
				} else {
					post_data+="&"+request_value;
				}
					
			}
			
			URL url = new URL(connect_address);
			HttpURLConnection httpConnection =(HttpURLConnection) url.openConnection();
			
			httpConnection.setRequestProperty("User-Agent",     JMUpdater.USER_AGENT);
			httpConnection.setRequestProperty("Accept-Charset", JMUpdater.ENCODING);
			
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			if (post_data.length()!=0)
				httpConnection.getOutputStream().write(post_data.getBytes());
			
			httpConnection.connect();
			http_response_code = httpConnection.getResponseCode();
			String result = "";
			InputStreamReader bufIn = new InputStreamReader(httpConnection.getInputStream(),JMUpdater.ENCODING);
            int c;
            while(true) {
            	c= bufIn.read();
            	if (c==-1) break;
            	result+=((char)c);
            }
            httpConnection.disconnect();
			return result;
		}catch(Throwable t) {
			throw new JMHTTPConnectionException(t);
		}
		
		
	}
	
	
}
