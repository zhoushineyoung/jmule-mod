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
package org.jmule.countrylocator;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.jmule.core.configmanager.ConfigurationManager;

import com.maxmind.geoip.Country;
import com.maxmind.geoip.LookupService;

/**
 *
 * Created on Aug 12, 2008
 * @author javajox
 * @author binary256_
 * @version $Revision: 1.5 $
 * Last changed by $Author: binary255 $ on $Date: 2010/08/26 10:34:48 $
 */
public class CountryLocator {
	public static final String GEOIP_DAT       = ConfigurationManager.SETTINGS_DIR + File.separator + "geoip.dat";
	public static int FLAG_WIDTH 			   = 25;
	public static int FLAG_HEIGHT			   = 15;
	
	private LookupService lookup_service;
	
	private boolean service_down = false;
	
	public static final String COUNTRY_NAME_NOT_AVAILABLE = "N/A";
	
	public static final String COUNTRY_CODE_NOT_AVAILABLE = "--";
	
	private CountryLocator() {
	
		try {
			
			lookup_service = new LookupService( GEOIP_DAT, LookupService.GEOIP_MEMORY_CACHE );		
			
		} catch (IOException e) {
			
	        service_down = true;
		}
		
	}
	
	private static class CountryLocatorSingletonHolder {
		private static final CountryLocator INSTANCE = new CountryLocator();
	}
	
	public static CountryLocator getInstance() {
		return CountryLocatorSingletonHolder.INSTANCE;
	}
	
	public Country getCountry(InetAddress inetAddress) {
		
		return service_down ? null : lookup_service.getCountry( inetAddress );
	}
	
	public Country getCountry(long ipAddress) {
		
		return service_down ? null : lookup_service.getCountry( ipAddress );
	}
	
	public Country getCountry(String ipAddress) {
		
		return service_down ? null : lookup_service.getCountry( ipAddress );
	}
	
	public String getCountryName( InetAddress inetAddress ) {
		
		return service_down ? null : getCountry( inetAddress ).getName();
	}
	
	public String getCountryName( long inetAddress ) {
		
		return service_down ? null : getCountry( inetAddress ).getName();
	}
	
	public String getCountryName( String inetAddress ) {
		
		return service_down ? null : getCountry( inetAddress ).getName();
	}
	
	//public String getCountryCode( InetAddress inetAddress ) {
		
	//	return service_down ? null : getCountry( inetAddress ).getCode();
	//}
	
	//public String getCountryCode( long inetAddress ) {
		
	//	return service_down ? null : getCountry( inetAddress ).getCode();
	//}
	
	//public String getCountryCode( String inetAddress ) {
		
	//	return service_down ? null : getCountry( inetAddress ).getCode();
	//}
	
	public String getCountryCode(Object inetAddress) {
		
		if( service_down ) return null;
		
		if(inetAddress instanceof String) 
			return lookup_service.getCountry((String)inetAddress).getCode();
		
		if(inetAddress instanceof InetAddress)
		    return lookup_service.getCountry((InetAddress)inetAddress).getCode();
		
		return null;
	}
	
	public boolean isServiceDown() {
		return service_down;
	}

	
}
