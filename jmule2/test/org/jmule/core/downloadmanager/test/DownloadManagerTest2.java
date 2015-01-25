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

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.utils.Misc;

/**
 * Created on Jan 10, 2010
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2010/01/10 14:23:10 $
 */
public class DownloadManagerTest2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			JMuleCoreFactory.create().start();
			JMuleCore _core = JMuleCoreFactory.getSingleton();
			
			DownloadManager _dm = _core.getDownloadManager();
			
			ED2KFileLink ed2k_file_link = new ED2KFileLink("ed2k://|file|firefox-3.5-source-code.tar.[contentdb.emule-project.net].bz2|46444013|af38d8b04b2585233c5b60516de3de23|/");
			_dm.addDownload( ed2k_file_link );
			
			Misc.w( _dm.hasDownload( ed2k_file_link.getFileHash() ) + "" );
			
			//_dm.cancelDownload( ed2k_file_link.getFileHash() );
			_dm.cancelSilentDownloads();
			
			Misc.w( _dm.hasDownload( ed2k_file_link.getFileHash() ) + "" );
			
			_core.stop();
			
		}catch( Throwable cause ){
			cause.printStackTrace();
		}

	}

}
