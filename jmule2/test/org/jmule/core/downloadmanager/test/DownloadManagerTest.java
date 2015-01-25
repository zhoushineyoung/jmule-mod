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
package org.jmule.core.downloadmanager.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.downloadmanager.DownloadManagerSingleton;
import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.downloadmanager.InternalDownloadManager;
import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.utils.Misc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created on Jan 10, 2010
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2010/01/10 14:23:10 $
 */
public class DownloadManagerTest {

	private InternalDownloadManager _dm = (InternalDownloadManager)DownloadManagerSingleton.getInstance();
	
	static {
		try {
			JMuleCoreFactory.create().start();
		}catch( Throwable cause ) {
			cause.printStackTrace();
		}
	}
	
	public DownloadManagerTest() {
		
	}
	
	@Before
	public void setUp() {
		Misc.w("Set Up (JUnit)");
		_dm.cancelSilentDownloads();
		List<DownloadSession> dss = _dm.getDownloads();
		for(DownloadSession ds : dss) {
			System.out.println( ds );
		}
	}
	
	@After
	public void setDown() {
		Misc.w("Set Down (JUnit)");
		_dm.cancelSilentDownloads();
		List<DownloadSession> dss = _dm.getDownloads();
		for(DownloadSession ds : dss) {
			System.out.println( ds );
		}
	}
	
	@Test
	public void testAddDownloadsAsED2KLinks() {
		try {
			ED2KFileLink ed2k_file_link = new ED2KFileLink("ed2k://|file|firefox-3.5-source-code.tar.[contentdb.emule-project.net].bz2|46444013|af38d8b04b2585233c5b60516de3de23|/");
			_dm.addDownload( ed2k_file_link );
			_dm.startDownload( ed2k_file_link.getFileHash() );
			assertTrue( _dm.hasDownload(ed2k_file_link.getFileHash()) );
		}catch( Throwable c ) {
			fail(c+"");
		}
		//try {
			
		//}catch( Throwable c ) {
		//	fail(c+"");
		//}
		
	}
	
	@Test
	public void testAddSilentDownloads() {
		try {
			_dm.addAndStartSilentDownloads("ed2k://|file|firefox-3.5-source-code.tar.[contentdb.emule-project.net].bz2|46444013|af38d8b04b2585233c5b60516de3de23|/").
			    addAndStartSilentDownloads("ed2k://|file|jmule-0.5.6.jar|2826940|B6D49948A7188BB06653EE2B6796AFB8|/").
			    addAndStartSilentDownloads("ed2k://|file|jmule-0.5.6.tar.gz|7005896|BA95E3367416D09D7ACA5FB260C7D91F|/").
			    addAndStartSilentDownloads("ed2k://|file|jmule-0.5.6-src.zip|1923245|AFCC6F451C6EA3CE72FFA048EE0A07EF|/").
			    finishAddAndStartSilentDownloads();
			assertTrue( 4 == _dm.getDownloadCount() );
			_dm.cancelSilentDownloads();
			assertTrue( 0 == _dm.getDownloadCount() );
			String ed2k_link_as_str = "ed2k://|file|jmule-0.5.6.tar.gz|7005896|BA95E3367416D09D7ACA5FB260C7D91F|/";
			ED2KFileLink ed2k_link = new ED2KFileLink( ed2k_link_as_str );
			_dm.addAndStartSilentDownloads(ed2k_link_as_str).finishAddAndStartSilentDownloads();
			assertTrue( _dm.hasDownload( ed2k_link.getFileHash() ) );
		}catch( Throwable c ) {
			fail(c+"");
		}
	}
	
	@Test
	public void testAddAVeryLargeDownloadList() {
		try {
			String p1 = "ed2k://|file|test1.tar.gz|1923245|B6D49948A1";
			String p2 = "BB06653EE2B6796AFB8|/";
			for(int i=1000; i<2000; i++)
				_dm.addAndStartSilentDownload(p1 + i + p2);
			System.out.println( "dc = " + _dm.getDownloadCount() + "");
			assertTrue( 1000 == _dm.getDownloadCount() );
		}catch( Throwable c ) {
			c.printStackTrace();
			fail(c+"");
		}
	}
}
