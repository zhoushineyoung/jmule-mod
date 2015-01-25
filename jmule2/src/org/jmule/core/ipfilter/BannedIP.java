/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.ipfilter;

import org.jmule.core.utils.AddressUtils;

/**
 * Created on Nov 4, 2009
 * @author javajox
 * @version $Revision: 1.3 $
 * Last changed by $Author: javajox $ on $Date: 2010/01/12 12:00:01 $
 */
public class BannedIP implements BannedObject, Comparable<BannedIP> {

	private int banned_ip_as_int;
	private IPFilter.BannedReason banned_reason = IPFilter.BannedReason.DEFAULT;
	private String who_banned;
	
	BannedIP(int bannedIPAsInt, 
			 IPFilter.BannedReason bannedReason,
			 String whoBanned) {
		
		banned_ip_as_int = bannedIPAsInt;
		banned_reason = bannedReason;
		who_banned = whoBanned;
	}
	
	BannedIP(int bannedIPAsInt,
			 String whoBanned) {
		banned_ip_as_int = bannedIPAsInt;
		banned_reason = IPFilter.BannedReason.DEFAULT;
		who_banned = whoBanned;
	}
	
	BannedIP(int bannedIPAsInt) {
		banned_ip_as_int = bannedIPAsInt;
	}
	
	int getIPAsInt() {
		
		return banned_ip_as_int;
	}
	
	public IPFilter.BannedReason getReason() {
	
		return banned_reason;
	}
	
	public String getWhoBanned() {
		
		return who_banned;
	}
	
	public String getIPAsString() {
		
		return AddressUtils.ip2string( banned_ip_as_int );
	}
	
	public boolean equals(Object obj) {
				
		if( obj instanceof BannedIP )
			return ((BannedIP)obj).getIPAsInt() == banned_ip_as_int;
			
		return false;
	}
	
	public int hashCode() {
		
		return banned_ip_as_int;
	}

	@Override
	public int compareTo(BannedIP obj) {	
			int int_value = ((BannedIP)obj).getIPAsInt();
			if( int_value < banned_ip_as_int ) return -1;
			if( int_value > banned_ip_as_int ) return 1;
		return 0;
	}
}
