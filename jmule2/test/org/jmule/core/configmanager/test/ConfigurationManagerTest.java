/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.configmanager.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.NetworkInterface;
import java.util.List;

import org.jmule.core.JMException;
import org.jmule.core.JMuleCoreException;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.configmanager.InternalConfigurationManager;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.NetworkUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created on Aug 10, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.4 $
 * Last changed by $Author: binary255 $ on $Date: 2010/01/01 15:01:39 $
 */
public class ConfigurationManagerTest {

	private InternalConfigurationManager manager = (InternalConfigurationManager) ConfigurationManagerSingleton.getInstance();
	
	static {
		try {
			JMuleCoreFactory.create().start();
		} catch (JMuleCoreException e) {
			e.printStackTrace();
		}
	}
	
	public ConfigurationManagerTest() {
		
	}
	
	@Before
	public void setUp() {
		
	}
	
	@After
	public void setDown() {
		
	}

	
	
	@Test
	public void testGetDownloadBandwidth() {
		try {
			manager.setDownloadBandwidth(100);
			assertEquals(100, manager.getDownloadBandwidth());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}	
		
	}

	@Test
	public void testGetDownloadLimit() {
		try {
			manager.setDownloadLimit(100);
			assertEquals(100, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit(0);
			assertEquals(0, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit("1234");
			assertEquals(1234, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit(-1);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setDownloadLimit("-10");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetNickName() {
		try {
			manager.setNickName("Bee");
			assertEquals("Bee", manager.getNickName());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		// the nickname's length can't be 0
		try {
			manager.setNickName("");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		 
		try {
			byte[] array = new byte[65540];
			manager.setNickName(new String(array));
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		// the nickname's length can't be more than 65535
		try {
			byte[] array = new byte[65535];
			manager.setNickName(new String(array));
			assertEquals(65535, manager.getNickName().length());
		} catch (ConfigurationManagerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void testGetTCP() {
		try {
			manager.setTCP(0);
			assertEquals(0, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setTCP(65535);
			assertEquals(65535, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setTCP(-1);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setTCP(1000);
			assertEquals(1000, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testGetUDP() {
		try {
			manager.setUDP(0);
			assertEquals(0, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setUDP(65535);
			assertEquals(65535, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setUDP(-1);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setUDP(2000);
			assertEquals(2000, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testGetUploadBandwidth() {
		try {
			manager.setDownloadBandwidth(1000);
			assertEquals(1000, manager.getDownloadBandwidth());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadBandwidth(-1000);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setDownloadBandwidth(0);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetUploadLimit() {
		try {
			manager.setDownloadLimit(256);		
			assertEquals(256, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit(0);		
			assertEquals(0, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit(-128);		
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testLoad() {
		try {
			manager.load();
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testSave() {
		try {
			manager.save();
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testSetDownloadBandwidthLong() {
		try {
			manager.setDownloadBandwidth(-1000);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setDownloadBandwidth(0);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
				
		try {
			manager.setDownloadBandwidth(500);
			assertEquals(500, manager.getDownloadBandwidth());
		} catch (ConfigurationManagerException e) {
			assertTrue(false);
		}
		
				
		
	}

	@Test
	public void testSetDownloadBandwidthString() {
		try {
			manager.setDownloadBandwidth("0_0");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setDownloadBandwidth("-1");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setDownloadBandwidth("2465");
			assertEquals(2465,manager.getDownloadBandwidth());
		} catch (ConfigurationManagerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void testSetDownloadLimitLong() {
		try {
			manager.setDownloadLimit(0);
			assertEquals(0, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit(500);
			assertEquals(500, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit(-10);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetDownloadLimitString() {
		try {
			manager.setDownloadLimit("0");
			assertEquals(0, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit("4444");
			assertEquals(4444, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit("-5550");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetNickName() {
		try {
			manager.setNickName("test");
			assertEquals("test",manager.getNickName());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testSetTCPString() {
		try {
			manager.setTCP("111");
			assertEquals(111, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setTCP("0");
			assertEquals(0, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setTCP("65535");
			assertEquals(65535, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setTCP("-1");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		try {
			manager.setTCP("bad port ");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
	}

	@Test
	public void testSetTCPInt() {
		try {
			manager.setTCP(666);
			assertEquals(666, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setTCP(0);
			assertEquals(0, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setTCP(65535);
			assertEquals(65535, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setTCP(-1);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetUDPString() {
		try {
			manager.setUDP("666");
			assertEquals(666, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setUDP("0");
			assertEquals(0, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setUDP("65535");
			assertEquals(65535, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setUDP("-1");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		try {
			manager.setUDP("4dd3zxcc ");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetUDPInt() {
		try {
			manager.setUDP(364);
			assertEquals(364, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setUDP(0);
			assertEquals(0, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setUDP(65535);
			assertEquals(65535, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setUDP(-1);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetUploadBandwidthLong() {
		try {
			manager.setUploadBandwidth(-2000);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setUploadBandwidth(0);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
				
		try {
			manager.setUploadBandwidth(600);
			assertEquals(600, manager.getUploadBandwidth());
		} catch (ConfigurationManagerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void testSetUploadBandwidthString() {
		try {
			manager.setUploadBandwidth("-20");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setUploadBandwidth("0");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setUploadBandwidth("11.223");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
				
		try {
			manager.setUploadBandwidth("12");
			assertEquals(12, manager.getUploadBandwidth());
		} catch (ConfigurationManagerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void testSetUploadLimitLong() {
		try {
			manager.setUploadLimit(0);
			assertEquals(0, manager.getUploadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		

		try {
			manager.setUploadLimit(-60);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setUploadLimit(1500);
			assertEquals(1500, manager.getUploadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
	}

	@Test
	public void testSetUploadLimitString() {
		try {
			manager.setUploadLimit("0");
			assertEquals(0, manager.getUploadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		

		try {
			manager.setUploadLimit("-11");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setUploadLimit("a11");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setUploadLimit("2500");
			assertEquals(2500, manager.getUploadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testSetUDPEnabled() {
		try {
			manager.setUDPEnabled(false);
			assertFalse(manager.isUDPEnabled());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
	}

	@Test
	public void testIsUDPEnabled() {
		try {
			manager.setUDPEnabled(true);
			assertTrue(manager.isUDPEnabled());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testIsJKadAutoconnectEnabled() {
		try {
			manager.setAutoconnectJKad(true);
			assertTrue(manager.isJKadAutoconnectEnabled());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testSetAutoconnectJKad() {
		try {
			manager.setAutoconnectJKad(false);
			assertFalse(manager.isJKadAutoconnectEnabled());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testSetUpdateServerListAtConnect() {
		try {
			manager.setUpdateServerListAtConnect(true);
			assertTrue(manager.updateServerListAtConnect());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testUpdateServerListAtConnect() {
		try {
			manager.setUpdateServerListAtConnect(false);
			assertFalse(manager.updateServerListAtConnect());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}
	
	@Test
	public void testGetNicName() { 
		try {
			List<NetworkInterface> network_interfaces = NetworkUtils.getAllNetworkInterfaces();
			String nic_name = network_interfaces.get(0).getName();
			
			manager.setNicName(nic_name);
			assertEquals(nic_name, manager.getNicName());
		} catch (JMException e) {
			fail(e+"");
		}
		
	}
	
	@Test
	public void testSetNicName() { 
		try {
			List<NetworkInterface> network_interfaces = NetworkUtils.getAllNetworkInterfaces();
			String nic_name = network_interfaces.get(0).getName();
			
			manager.setNicName(nic_name);
			assertEquals(nic_name, manager.getNicName());
		} catch (JMException e) {
			fail(e+"");
		}
		
		try {
			
			manager.setNicName("test111 dd225 666 ");
			assertTrue(false);
		} catch (JMException e) {
			assertTrue(true);
		}
		
	}
	
	@Test
	public void testGetNicIP() { 
		try {
			List<NetworkInterface> network_interfaces = NetworkUtils.getAllNetworkInterfaces();
			String ip = Convert.IPtoString(network_interfaces.get(0).getInterfaceAddresses().get(1).getAddress().getAddress());
			manager.setNicIP(ip);
			assertEquals(ip, manager.getNicIP());
		} catch (JMException e) {
			fail(e+"");
		}
	}
	
	@Test
	public void testSetNicIP() { 
		try {
			List<NetworkInterface> network_interfaces = NetworkUtils.getAllNetworkInterfaces();
			String ip = Convert.IPtoString(network_interfaces.get(0).getInterfaceAddresses().get(1).getAddress().getAddress());
			manager.setNicIP(ip);
			assertEquals(ip, manager.getNicIP());
		} catch (JMException e) {
			fail(e+"");
		}
		
		
		try {
			manager.setNicIP("111.4664");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testInitialize() {
		manager.initialize();
		manager.start();
		manager.shutdown();
	}
	

}
