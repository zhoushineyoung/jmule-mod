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
package org.jmule.core.ipfiltermanager.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.ipfilter.IPFilterSingleton;
import org.jmule.core.ipfilter.InternalIPFilter;
import org.jmule.core.ipfilter.IPFilter.BannedReason;
import org.jmule.core.ipfilter.IPFilter.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created on Jan 9, 2010
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2010/01/10 14:23:10 $
 */
public class IPFilterTest {

	private InternalIPFilter ip_filter = (InternalIPFilter) IPFilterSingleton.getInstance();
	
	static {
		try {
			JMuleCoreFactory.create().start();
		}catch( Throwable cause ) {
			cause.printStackTrace();
		}
	}
	
	public IPFilterTest() {
		
	}
	
	@Before
	public void setUp() {
		
	}
	
	@After
	public void setDown() {
		
		ip_filter.clear();
	}
	
	@Test
	public void testBanPeers() {
		try {
			String ip = "209.85.229.105";
			ip_filter.addPeer( ip );
			assertTrue( ip_filter.isPeerBanned(ip) );
		}catch( Throwable c ) {
			fail(c+"");
		}
		
		try {
			String ip = "216.34.181.45";
			ip_filter.addPeer( ip );
			assertTrue( ip_filter.isPeerBanned( ip ) );
		}catch( Throwable c ) {
			fail(c+"");
		}
		
		try {
			String ip1 = "64.191.203.30";
			String ip2 = "77.67.19.145";
			String ip3 = "74.54.128.98";
			ip_filter.addPeer(ip1);
			ip_filter.addPeer(ip2);
			ip_filter.addPeer(ip3);
			assertFalse( ip_filter.isPeerBanned( "62.149.24.66" ) );
		}catch( Throwable c ) {
			fail(c+"");
		}	
		
		try {
			String first_ip_part = "74.54.128.";
			for(int i=5; i<=200; i++)
				ip_filter.addPeer(first_ip_part + i);
			assertTrue( ip_filter.isPeerBanned("74.54.128.101") );
		}catch( Throwable c ) {
			fail(c+"");
		}
	}
	
	@Test
	public void testTemporaryBanIPs() {
		try {
			String ip = "69.63.187.19";
			ip_filter.addPeer(ip, 3, TimeUnit.DAY);
			assertTrue( ip_filter.isPeerBanned( ip ) );
		}catch( Throwable c ) {
			fail(c+"");
		}
	}
	
	@Test
	public void testBanServers() {
		try {
		   String ip = "69.147.114.224";
		   ip_filter.addServer( ip );
		   assertTrue( ip_filter.isServerBanned(ip) );
		}catch( Throwable c ) {
			fail(c+"");
		}
		
		try {
			String ip = "69.63.187.19";
			ip_filter.addServer(ip, BannedReason.BAD_PACKETS);
			assertTrue( ip_filter.isServerBanned(ip) );
		}catch( Throwable c ) {
			fail(c+"");
		}
		
		try {
			String ip = "69.63.187.19";
			ip_filter.addServer(ip, BannedReason.BAD_PACKETS);
			assertFalse( ip_filter.isPeerBanned(ip) );
		}catch( Throwable c ) {
			fail(c+"");
		}
		
	}
	
}
