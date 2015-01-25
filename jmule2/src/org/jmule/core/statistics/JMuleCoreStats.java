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
package org.jmule.core.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * Created on Aug 17, 2008
 * @author javajox
 * @author binary256_
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/08/20 15:38:02 $
 */
public class JMuleCoreStats {

	public static final String ST_NET_SESSION_DOWNLOAD_BYTES             =    "net.session.download.bytes";
	public static final String ST_NET_SESSION_UPLOAD_BYTES               =    "net.session.upload.bytes";
	public static final String ST_NET_SESSION_DOWNLOAD_COUNT             =    "net.session.download.count";
	public static final String ST_NET_SESSION_UPLOAD_COUNT               =    "net.session.upload.count";
	
	public static final String ST_NET_PEERS_COUNT                        =    "net.peers.count";
	public static final String ST_NET_PEERS_DOWNLOAD_COUNT               =    "net.peers.download.count";
	public static final String ST_NET_PEERS_UPLOAD_COUNT                 =    "net.peers.upload.count";
	
	public static final String ST_NET_SERVERS_COUNT                      =    "net.servers.count";
	public static final String ST_NET_SERVERS_DEAD_COUNT                 =    "net.servers.dead.count";
	public static final String ST_NET_SERVERS_ALIVE_COUNT                =    "net.servers.alive.count";
	
	public static final String ST_DISK_SHARED_FILES_COUNT                =    "disk.shared.files.count";
	public static final String ST_DISK_SHARED_FILES_PARTIAL_COUNT        =    "disk.shared.files.partial.count";
	public static final String ST_DISK_SHARED_FILES_COMPLETE_COUNT       =    "disk.shared.files.complete.count";
	public static final String ST_DISK_SHARED_FILES_BYTES                =    "disk.shared.files.bytes";
	public static final String ST_DISK_SHARED_FILES_PARTIAL_BYTES        =    "disk.shared.files.partial.bytes";
	public static final String ST_DISK_SHARED_FILES_COMPLETE_BYTES       =    "disk.shared.files.complete.bytes";
	
	public static final String SEARCHES_COUNT                            =    "searches.count";
	
	private static final String[] _ST_ALL = {
		ST_NET_SESSION_DOWNLOAD_BYTES,
		ST_NET_SESSION_UPLOAD_BYTES,
		ST_NET_SESSION_DOWNLOAD_COUNT,
		ST_NET_SESSION_UPLOAD_COUNT,
		ST_NET_PEERS_COUNT,
		ST_NET_PEERS_DOWNLOAD_COUNT,
		ST_NET_PEERS_UPLOAD_COUNT,
		ST_NET_SERVERS_COUNT,
		ST_NET_SERVERS_DEAD_COUNT,
		ST_NET_SERVERS_ALIVE_COUNT,
		ST_DISK_SHARED_FILES_COUNT,
		ST_DISK_SHARED_FILES_PARTIAL_COUNT,
		ST_DISK_SHARED_FILES_COMPLETE_COUNT,
		ST_DISK_SHARED_FILES_BYTES,
		ST_DISK_SHARED_FILES_PARTIAL_BYTES,
		ST_DISK_SHARED_FILES_COMPLETE_BYTES,
		SEARCHES_COUNT
	};
	
	private static class Pair {
		private Set<String> types;
		private JMuleCoreStatsProvider provider;
		public Pair(Set<String> types, JMuleCoreStatsProvider provider) {
			this.types = types;
			this.provider = provider;
		}
		public Set<String> getTypes() {
			return types;
		}
		public JMuleCoreStatsProvider getProvider() {
			return provider;
		}
	}
	
    private static List<Pair>	providers = new ArrayList<Pair>();
	
	public static Map<String,Object> getStats(Set<String> types) {
		
		Set<String>	expanded = new HashSet<String>();

		for(String type : types) {	
			
			Pattern pattern = Pattern.compile( type );
						
			for(String s : _ST_ALL) {	
				
				if ( pattern.matcher( s ).matches()){

					expanded.add( s );
				}
			}
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		for(Pair pair : providers) {	
							
			pair.getProvider().updateStats( expanded, result );
		}
		
		return  result ;
	}
	
	public static void registerProvider(Set<String> types, JMuleCoreStatsProvider	provider ) {
		
		synchronized( providers ) {
			
			providers.add(  new Pair(types, provider));
		}
	}
	
}
