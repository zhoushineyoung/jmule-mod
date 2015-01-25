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
package org.jmule.core.edonkey;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * ED2K server link
 * ed2k://|server|IP|PORT|/
 * @author binary
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/17 17:45:20 $$
 */
public class ED2KServerLink extends ED2KLink {

	private String server_address = "";
	
	private int server_port = 0;
	
	public ED2KServerLink(String server_address,int server_port) {
		
		this.server_address = server_address;
		
		this.server_port = server_port;
		
	}
	
	public ED2KServerLink(String link){
		Pattern s;
		
		Matcher m;
		
		s = Pattern.compile("ed2k:\\/\\/\\|server\\|([0-9]{1,3}+.[0-9]{1,3}+.[0-9]{1,3}+.[0-9]{1,3}+)\\|([0-65535]*)\\|\\/");
		
		m = s.matcher(link);
		
		if (m.matches()) {
			server_address = m.group(1);
			server_port = Integer.valueOf(m.group(2)).intValue();
		}
	}
	
	public static boolean isValidLink(String link) {
		Pattern s;
		
		Matcher m;
		
		s = Pattern.compile("ed2k:\\/\\/\\|server\\|([0-9]{1,3}+.[0-9]{1,3}+.[0-9]{1,3}+.[0-9]{1,3}+)\\|([0-65535]*)\\|\\/");
		
		m = s.matcher(link);
		
		return m.matches();
	}
	
	public static List<ED2KServerLink> extractLinks(String rawData) {
		
		Pattern s = Pattern.compile("ed2k:\\/\\/\\|server\\|([0-9]{1,3}+.[0-9]{1,3}+.[0-9]{1,3}+.[0-9]{1,3}+)\\|([0-65535]*)\\|\\/");
		
		Matcher m = s.matcher(rawData);
		
		List<ED2KServerLink> links = new LinkedList<ED2KServerLink>();
		
		while(m.find()) {
			
			String server_address = m.group(1);
			
			int server_port = Integer.valueOf(m.group(2)).intValue();
			
			links.add(new ED2KServerLink(server_address,server_port));
			
		}
		
		return links;
	}
	
	public String getAsString() {
		return "ed2k://|server|" + server_address + "|" + server_port + "|/";
	}
	
	public String toString() {
		return "ed2k://|server|" + server_address + "|" + server_port + "|/";
	}
	
	public String getServerAddress() {
		return server_address;
	}

	public int getServerPort() {
		return server_port;
	}
	
	public int hashCode() {
		return getAsString().hashCode();
	}
	
	public boolean equals(Object object) {
		if (object == null) return false;
		if (!(object instanceof ED2KServerLink)) return false;
		if (hashCode() != object.hashCode()) return false;
		return true;
	}
	
	public static void main(String... args) {
		
		ED2KServerLink link;
		link = new ED2KServerLink("ed2k://|server|213.25.156.20|7000|/");
		System.out.println("Link : "+link.getServerAddress()+" : "+link.getServerPort());
		
		/*String text = "dwhkgshked2k://|server|207.44.222.51|4242|/sdasded2k://|server|207.44.222.51|4242|/ed2k://|server|207.44.222.51|4242|/";
		List<ED2KServerLink> list = ED2KServerLink.extractLinks(text);
		
		for(ED2KServerLink l : list) {
			System.out.println(l+"");
		}*/
		
	}
}
