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
package org.jmule.ui.swing.maintabs.statistics;

import java.awt.Font;
import java.awt.GridLayout;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.ui.swing.Refreshable;
import org.jmule.ui.swing.maintabs.AbstractTab;
import org.jmule.ui.utils.FileFormatter;
import org.jmule.ui.utils.TimeFormatter;

import java.lang.management.ManagementFactory;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/02/03 19:37:16 $$
 */
public class StatisticsTabs extends AbstractTab {

	private SessionsPanel sessions_panel = new SessionsPanel();
	private PeersPanel peers_panel = new PeersPanel();
	private ServersPanel servers_panel = new ServersPanel();
	private SharedFilesPanel shared_files_panel = new SharedFilesPanel(); 
	
	private JavaPanel java_panel = new JavaPanel();
	private JavaHeapMemoryPanel java_heap_memory_panel = new JavaHeapMemoryPanel();
	private JavaNonHeapMemoryPanel java_non_heap_momory_panel = new JavaNonHeapMemoryPanel();
	private JavaThreadsPanel java_threads_panel = new JavaThreadsPanel();
	
	private JTabbedPane tabbed_panel = new JTabbedPane();
	
	private JPanel general_panel = new JPanel();
	private JPanel java_vm_panel = new JPanel();
	
	// --------------- Stats data ---------------------------------------------------
	private Map<String,JLabel> stats_fields = new Hashtable<String,JLabel>();
	private Set<String> types = new HashSet<String>();
	private Set<String> filesize_formatter = new HashSet<String>();
	
	private RuntimeMXBean runtime_bean = ManagementFactory.getRuntimeMXBean();
	private MemoryMXBean memory_bean = ManagementFactory.getMemoryMXBean();
	private MemoryUsage heap_memory = memory_bean.getHeapMemoryUsage();
	private MemoryUsage nonheap_memory = memory_bean.getNonHeapMemoryUsage();
	private ThreadMXBean thread_bean = ManagementFactory.getThreadMXBean();
	
	private final static Font label_value_font = new java.awt.Font("Dialog", 0, 12);
	
	public StatisticsTabs(JFrame parent) {
		super(parent);
		init();
        
        super.registerRefreshable(new Refreshable() {
		   public void refresh() {
			  updateData();
		   }
	    });
        
        updateData();
	}
	
	private void init() {
		
		GridLayout general_panel_grid_layout = new GridLayout(2,2);
		
		general_panel.setLayout(general_panel_grid_layout);
		
		general_panel.add(sessions_panel);
		
		general_panel.add(peers_panel);
		
		general_panel.add(servers_panel);
		
		general_panel.add(shared_files_panel);
		
		GridLayout java_vm_panel_grid_layout = new GridLayout(2,2);
		
		java_vm_panel.setLayout(java_vm_panel_grid_layout);
		
		java_vm_panel.add(java_panel);
		
		java_vm_panel.add(java_heap_memory_panel);
		
		java_vm_panel.add(java_non_heap_momory_panel);
		
		java_vm_panel.add(java_threads_panel);
		
		tabbed_panel.add("General", general_panel);
		
		tabbed_panel.add("Java VM", java_vm_panel);
		
		this.setLayout(new GridLayout(1,1));
		
		this.add(tabbed_panel);
		
		types.add(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_BYTES);
		types.add(JMuleCoreStats.ST_NET_SESSION_UPLOAD_BYTES);
		types.add(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_COUNT);
		types.add(JMuleCoreStats.ST_NET_SESSION_UPLOAD_COUNT);
		types.add(JMuleCoreStats.ST_NET_PEERS_COUNT);
		types.add(JMuleCoreStats.ST_NET_PEERS_DOWNLOAD_COUNT);
		types.add(JMuleCoreStats.ST_NET_PEERS_UPLOAD_COUNT);
		types.add(JMuleCoreStats.ST_NET_SERVERS_COUNT);
		types.add(JMuleCoreStats.ST_NET_SERVERS_DEAD_COUNT);
		types.add(JMuleCoreStats.ST_NET_SERVERS_ALIVE_COUNT);
		
		types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_COUNT);
		types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_COUNT);
		types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_COUNT);
		types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_BYTES);
		types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_BYTES);
		types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_BYTES);
		
		types.add(JMuleCoreStats.SEARCHES_COUNT);
		
		stats_fields.put(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_BYTES, sessions_panel.total_downloaded_value);
		stats_fields.put(JMuleCoreStats.ST_NET_SESSION_UPLOAD_BYTES, sessions_panel.total_uploaded_value);
		stats_fields.put(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_COUNT, sessions_panel.total_download_sessions_value);
		stats_fields.put(JMuleCoreStats.ST_NET_SESSION_UPLOAD_COUNT, sessions_panel.total_upload_sessions_value);
		
		stats_fields.put(JMuleCoreStats.ST_NET_PEERS_COUNT, peers_panel.total_peers_value);
		stats_fields.put(JMuleCoreStats.ST_NET_PEERS_DOWNLOAD_COUNT, peers_panel.downloading_peers_value);
		stats_fields.put(JMuleCoreStats.ST_NET_PEERS_UPLOAD_COUNT, peers_panel.uploading_peers_value);
		
		stats_fields.put(JMuleCoreStats.ST_NET_SERVERS_COUNT, servers_panel.total_servers_value);
		stats_fields.put(JMuleCoreStats.ST_NET_SERVERS_ALIVE_COUNT, servers_panel.alive_servers_value);
		stats_fields.put(JMuleCoreStats.ST_NET_SERVERS_DEAD_COUNT, servers_panel.dead_servers_value);
		stats_fields.put(JMuleCoreStats.SEARCHES_COUNT, servers_panel.searches_count_value);
		
		stats_fields.put(JMuleCoreStats.ST_DISK_SHARED_FILES_COUNT, shared_files_panel.shared_files_value);
		stats_fields.put(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_COUNT, shared_files_panel.partial_files_value);
		stats_fields.put(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_COUNT, shared_files_panel.completed_files_value);
		stats_fields.put(JMuleCoreStats.ST_DISK_SHARED_FILES_BYTES, shared_files_panel.total_shared_size_value);
		stats_fields.put(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_BYTES, shared_files_panel.completed_files_size_value);
		stats_fields.put(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_BYTES, shared_files_panel.partial_file_size_value);
		
		filesize_formatter.add(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_BYTES);
		filesize_formatter.add(JMuleCoreStats.ST_NET_SESSION_UPLOAD_BYTES);
		filesize_formatter.add(JMuleCoreStats.ST_DISK_SHARED_FILES_BYTES);
		filesize_formatter.add(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_BYTES);
		filesize_formatter.add(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_BYTES);
		
		java_panel.name_value.setText(runtime_bean.getVmName());
		java_panel.vendor_value.setText(runtime_bean.getVmVendor());
		java_panel.version_value.setText(runtime_bean.getVmVersion());
		
		sessions_panel.total_downloaded_value.setFont(label_value_font);
		sessions_panel.total_uploaded_value.setFont(label_value_font);
		sessions_panel.total_download_sessions_value.setFont(label_value_font);
		sessions_panel.total_upload_sessions_value.setFont(label_value_font);
		peers_panel.total_peers_value.setFont(label_value_font);
		peers_panel.downloading_peers_value.setFont(label_value_font);
		peers_panel.uploading_peers_value.setFont(label_value_font);
		servers_panel.total_servers_value.setFont(label_value_font);
		servers_panel.alive_servers_value.setFont(label_value_font);
		servers_panel.dead_servers_value.setFont(label_value_font);
		servers_panel.searches_count_value.setFont(label_value_font);
		shared_files_panel.shared_files_value.setFont(label_value_font);
		shared_files_panel.partial_files_value.setFont(label_value_font);
		shared_files_panel.completed_files_value.setFont(label_value_font);
		shared_files_panel.total_shared_size_value.setFont(label_value_font);
		shared_files_panel.completed_files_size_value.setFont(label_value_font);
		shared_files_panel.partial_file_size_value.setFont(label_value_font);
		
		java_panel.uptime_value.setFont(label_value_font);
		java_heap_memory_panel.initial_heap_memory_value.setFont(label_value_font);
		java_heap_memory_panel.used_heap_memory_value.setFont(label_value_font);
		java_heap_memory_panel.maximum_heap_memory_value.setFont(label_value_font);
		java_non_heap_momory_panel.initial_non_heap_memory_value.setFont(label_value_font);
		java_non_heap_momory_panel.used_non_heap_momory_value.setFont(label_value_font);
		java_non_heap_momory_panel.maximum_non_heap_memory_value.setFont(label_value_font);
		java_threads_panel.jmule_threads_value.setFont(label_value_font);
		java_threads_panel.daemon_threads_value.setFont(label_value_font);
		java_threads_panel.peak_threads_value.setFont(label_value_font);
		java_threads_panel.total_threads_value.setFont(label_value_font);
	}
	
	private void updateData() {
		Map<String,Object> stats = JMuleCoreStats.getStats(types);
		for(String key : stats.keySet()) {
			Number value = (Number)stats.get(key);
			JLabel label = stats_fields.get(key);
			String str = value+"";
			
			if (filesize_formatter.contains(key))
				str = FileFormatter.formatFileSize((Long) value);
			
			
			label.setText(str);
		}
		
		java_panel.uptime_value.setText(TimeFormatter.formatColon(runtime_bean.getUptime()/1000)+"");
		
		java_heap_memory_panel.initial_heap_memory_value.setText(FileFormatter.formatFileSize(heap_memory.getInit()));
		java_heap_memory_panel.used_heap_memory_value.setText(FileFormatter.formatFileSize(heap_memory.getUsed()));
		java_heap_memory_panel.maximum_heap_memory_value.setText(FileFormatter.formatFileSize(heap_memory.getMax()));
		
		java_non_heap_momory_panel.initial_non_heap_memory_value.setText(FileFormatter.formatFileSize(nonheap_memory.getInit()));
		java_non_heap_momory_panel.used_non_heap_momory_value.setText(FileFormatter.formatFileSize(nonheap_memory.getUsed()));
		java_non_heap_momory_panel.maximum_non_heap_memory_value.setText(FileFormatter.formatFileSize(nonheap_memory.getMax()));
		
		java_threads_panel.jmule_threads_value.setText(thread_bean.getThreadCount()+"");
		java_threads_panel.daemon_threads_value.setText(thread_bean.getDaemonThreadCount()+"");
		java_threads_panel.peak_threads_value.setText(thread_bean.getPeakThreadCount()+"");
		java_threads_panel.total_threads_value.setText(thread_bean.getTotalStartedThreadCount()+"");
	}
	
}
