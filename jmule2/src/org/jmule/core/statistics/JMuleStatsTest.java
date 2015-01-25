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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * Created on Aug 17, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/08/18 08:56:22 $
 */
public class JMuleStatsTest {


	
	public static void main(String... args) {
		
		class test_stats implements JMuleCoreStatsProvider {

			Set types = new HashSet();
			private int k = 0;
			
			public test_stats() {
				types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_BYTES);
				types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_BYTES);
				types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_BYTES);
				JMuleCoreStats.registerProvider(types, this);
			}
			
			public void updateStats(Set types, Map values) {
	   	          k+=10;
	             if( types.contains(JMuleCoreStats.ST_DISK_SHARED_FILES_BYTES) ) {
	                   	 values.put(JMuleCoreStats.ST_DISK_SHARED_FILES_BYTES, new Long(231 + k));
	             }
	             if( types.contains(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_BYTES) ) {
	                     values.put(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_BYTES, new Long(34 + k));	 
	             }
				 if( types.contains(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_BYTES) ) {
					     values.put(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_BYTES, new Long(100 + k));
				 }
			}
			
		}
		
		JMuleCoreStats core_stats = new JMuleCoreStats();
		
		test_stats t_s = new test_stats();
		
		Set types = new HashSet();
		types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_BYTES);
		types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_BYTES);
		types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_BYTES);
		for(int i = 0; i <= 10; i++) {
			
			 Map stats = core_stats.getStats(types);
			 
			 for(Object obj : stats.keySet()) {
				 System.out.println("---" + obj + stats.get(obj) + "---");
			 }
			 System.out.println("************************************************");
			 try {
				 Thread.currentThread().sleep(2000);
			 }catch(Throwable t) {
				 t.printStackTrace();
			 }
		}
		
	}
	
}
