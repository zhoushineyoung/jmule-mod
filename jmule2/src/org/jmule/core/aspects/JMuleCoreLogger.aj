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
package org.jmule.core.aspects;

import java.util.logging.Logger;

import org.jmule.core.JMThread;
import org.jmule.core.impl.JMuleCoreImpl;
import org.jmule.core.utils.Misc;
/**
 * 
 * @author binary256
 * @version $$Revision: 1.6 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/04/29 10:58:54 $$
 */
public privileged aspect JMuleCoreLogger {
	private Logger log = Logger.getLogger("org.jmule.core");
	
	before() : call( JMuleCoreImpl.new()) {
		log.info(" JMule Core creation ");
	}
	
	after() : execution(void JMuleCoreImpl.start()) {
		log.info(" JMule Core started ");
	}
	
	after() throwing (Throwable t): execution (* JMuleCoreImpl.*(..)) {
		log.warning(Misc.getStackTrace(t));
	}

	after() throwing (Throwable t): execution (* JMThread.*(..)) {
		log.warning(Misc.getStackTrace(t));
	}
	
	after(String event) : args(event) && (call(void JMuleCoreImpl.logEvent(String))) {
		
/*		String downloadmanager = "Downloads : \n";
		for(DownloadSession download_session : DownloadManagerSingleton.getInstance().getDownloads()) {
			downloadmanager += download_session + "\n";
		}
		
		String network_manager = "Network manager : \n" + NetworkManagerSingleton.getInstance()+"\n";
		
		String peermanager = "Peer Manager : \n" + PeerManagerSingleton.getInstance() + "\n" ;
		
		//for (Peer peer : PeerManagerSingleton.getInstance().getPeers()) {
		//		peermanager += peer + " " + peer.getStatus()+ " \n";
		//}
		
//		peermanager += "Unknown peers : \n";
//
//		for(Peer peer : PeerManagerSingleton.getInstance().getUnknownPeers())
//			
//			peermanager += peer + "\n";
		
		peermanager += "Low ID Peers \n";
		//PeerManagerImpl impl = (PeerManagerImpl)PeerManagerFactory.getInstance();
		//for(Peer peer : impl.low_id_peer_list) {
		//	peermanager += peer + "\n";
		//}
		
		String uploadmanager = "Upload Manager :\n" + UploadManagerSingleton.getInstance();
		
		//for(UploadSession upload_session : UploadManagerSingleton.getInstance().getUploads()) 
		//	uploadmanager +=upload_session+"\n";
		
		String jkad = "JKad : \n" + JKadManagerSingleton.getInstance();
		
		log.fine("\nEvent : "+event+"\n"+
				network_manager+"\n"+
				peermanager+"\n"+
				downloadmanager+"\n"+
				uploadmanager+"\n" + jkad + "\n");*/
		
//		String downloadManager = "\nDownloads : \n";
//		for(DownloadSession download_session : DownloadManagerSingleton.getInstance().getDownloads()) {
//			downloadManager += download_session + "\n";
//		}
		
//		log.fine("Network manager : " + NetworkManagerSingleton.getInstance() 
//				+ "\nPeer manager : \n" + PeerManagerSingleton.getInstance());
	}
	

}
