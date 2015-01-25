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
package org.jmule.ui.swt.tab.main.statistics;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.GUIUpdater;
import org.jmule.ui.swt.Refreshable;
import org.jmule.ui.swt.skin.SWTSkin;
import org.jmule.ui.swt.tab.MainTab;
import org.jmule.ui.utils.FileFormatter;
import org.jmule.ui.utils.TimeFormatter;
/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2011/03/27 16:51:29 $$
 */
public class StatisticsTab extends MainTab {

	private Refreshable refreshable;
	
	private Map<String,Label> stats_fields = new Hashtable<String,Label>();
	private Set<String> types = new HashSet<String>();
	private Set<String> filesize_formatter = new HashSet<String>();
	
	private RuntimeMXBean runtime_bean = ManagementFactory.getRuntimeMXBean();
	private MemoryMXBean memory_bean = ManagementFactory.getMemoryMXBean();
	private MemoryUsage heap_memory = memory_bean.getHeapMemoryUsage();
	private MemoryUsage nonheap_memory = memory_bean.getNonHeapMemoryUsage();
	private ThreadMXBean thread_bean = ManagementFactory.getThreadMXBean();
	
	private Label heapmemory_init, heapmemory_used, heapmemory_max, nonheapmemory_init,jvm_uptime, 
				  nonheapmemory_used, nonheapmemory_max, thread_count,daemon_thread_count,peak_thread_count, total_thread_count;
	
	CTabItem network_stats;
	CTabItem jvm_stats;
	Composite tab_content2;
	
	public StatisticsTab(Composite content) {
		super(content,"Statistics");

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
		
		filesize_formatter.add(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_BYTES);
		filesize_formatter.add(JMuleCoreStats.ST_NET_SESSION_UPLOAD_BYTES);
		filesize_formatter.add(JMuleCoreStats.ST_DISK_SHARED_FILES_BYTES);
		filesize_formatter.add(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_BYTES);
		filesize_formatter.add(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_BYTES);
		
		refreshable = new Refreshable() {
			Map<String,Object> stats = null;
			public void refresh() {
				stats = JMuleCoreStats.getStats(types);
				
				for(String key : stats.keySet()) {
					Number value = (Number)stats.get(key);
					Label label = stats_fields.get(key);
					String str = value+"";
					if (filesize_formatter.contains(key))
						str = FileFormatter.formatFileSize((Long) value);
					
					label.setText(str);
				}
				
				jvm_uptime.setText(TimeFormatter.formatColon(runtime_bean.getUptime()/1000)+"");
				
				heapmemory_init.setText(FileFormatter.formatFileSize(heap_memory.getInit()));
				heapmemory_used.setText(FileFormatter.formatFileSize(heap_memory.getUsed()));
				heapmemory_max.setText(FileFormatter.formatFileSize(heap_memory.getMax()));
				
				nonheapmemory_init.setText(FileFormatter.formatFileSize(nonheap_memory.getInit()));
				nonheapmemory_used.setText(FileFormatter.formatFileSize(nonheap_memory.getUsed()));
				nonheapmemory_max.setText(FileFormatter.formatFileSize(nonheap_memory.getMax()));
				
				thread_count.setText(thread_bean.getThreadCount()+"");
				daemon_thread_count.setText(thread_bean.getDaemonThreadCount()+"");
				peak_thread_count.setText(thread_bean.getPeakThreadCount()+"");
				total_thread_count.setText(thread_bean.getTotalStartedThreadCount()+"");
			}
		};
		
		SWTSkin skin = null;
		try {
			
		    skin = (SWTSkin) JMuleUIManager.getJMuleUI().getSkin();
		
		}catch(Throwable t) {}
		GridData layout_data;
		GridLayout layout;
		Label label;
		tab_content.setLayout(new FillLayout());

		CTabFolder stats_tabs = new CTabFolder(tab_content,SWT.BORDER);
		stats_tabs.setLayout(new FillLayout());
		stats_tabs.setSimple(true);
		
		CTabItem network_stats = new CTabItem(stats_tabs,SWT.NONE);
		network_stats.setText(_._("mainwindow.statisticstab.tab.general"));
		content = new Composite(stats_tabs,SWT.NONE);
		network_stats.setControl(content);
		content.setLayout(new GridLayout(2,true));
		
		Group sessions_stats = new Group(content,SWT.NONE);
		sessions_stats.setText(_._("mainwindow.statisticstab.tab.general.group.sessions"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		sessions_stats.setLayoutData(layout_data);
		layout = new GridLayout(2,false);
		sessions_stats.setLayout(layout);
		
		label = new Label(sessions_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.session_downloaded") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label download_session_bytes = new Label(sessions_stats,SWT.NONE);
		download_session_bytes.setFont(skin.getLabelFont());
		download_session_bytes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stats_fields.put(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_BYTES, download_session_bytes);
		
		label = new Label(sessions_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.session_uploaded") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label upload_session_bytes = new Label(sessions_stats,SWT.NONE);
		upload_session_bytes.setFont(skin.getLabelFont());
		upload_session_bytes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stats_fields.put(JMuleCoreStats.ST_NET_SESSION_UPLOAD_BYTES, upload_session_bytes);
		
		label = new Label(sessions_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.session_download_count") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label download_session_count = new Label(sessions_stats,SWT.NONE);
		download_session_count.setFont(skin.getLabelFont());
		download_session_count.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		stats_fields.put(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_COUNT, download_session_count);
		
		label = new Label(sessions_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.session_upload_count") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label upload_session_count = new Label(sessions_stats,SWT.NONE);
		upload_session_count.setFont(skin.getLabelFont());
		upload_session_count.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		stats_fields.put(JMuleCoreStats.ST_NET_SESSION_UPLOAD_COUNT, upload_session_count);
		
		Group peers_stats = new Group(content,SWT.NONE);
		peers_stats.setText(_._("mainwindow.statisticstab.tab.general.group.peers"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING );
		peers_stats.setLayoutData(layout_data);
		layout = new GridLayout(2,false);
		peers_stats.setLayout(layout);
		
		label = new Label(peers_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.peer_count") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label peer_count = new Label(peers_stats,SWT.NONE);
		peer_count.setFont(skin.getLabelFont());
		peer_count.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stats_fields.put(JMuleCoreStats.ST_NET_PEERS_COUNT, peer_count);
		
		label = new Label(peers_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.download_peers") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label download_peers = new Label(peers_stats,SWT.NONE);
		download_peers.setFont(skin.getLabelFont());
		download_peers.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stats_fields.put(JMuleCoreStats.ST_NET_PEERS_DOWNLOAD_COUNT, download_peers);
		
		label = new Label(peers_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.upload_peers") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label upload_peers = new Label(peers_stats,SWT.NONE);
		upload_peers.setFont(skin.getLabelFont());
		upload_peers.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stats_fields.put(JMuleCoreStats.ST_NET_PEERS_UPLOAD_COUNT, upload_peers);
		
		new Label(peers_stats,SWT.NONE);
		
		Group sesvers_stats = new Group(content,SWT.NONE);
		sesvers_stats.setText(_._("mainwindow.statisticstab.tab.general.group.servers"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING );
		sesvers_stats.setLayoutData(layout_data);
		layout = new GridLayout(2,false);
		sesvers_stats.setLayout(layout);
		
		label = new Label(sesvers_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.server_count") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label server_count = new Label(sesvers_stats,SWT.NONE);
		server_count.setFont(skin.getLabelFont());
		server_count.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stats_fields.put(JMuleCoreStats.ST_NET_SERVERS_COUNT, server_count);
		
		label = new Label(sesvers_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.server_alive_count") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label server_alive_count = new Label(sesvers_stats,SWT.NONE);
		server_alive_count.setFont(skin.getLabelFont());
		server_alive_count.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stats_fields.put(JMuleCoreStats.ST_NET_SERVERS_ALIVE_COUNT, server_alive_count);
		
		label = new Label(sesvers_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.server_dead_count") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label server_dead_count = new Label(sesvers_stats,SWT.NONE);
		server_dead_count.setFont(skin.getLabelFont());
		server_dead_count.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stats_fields.put(JMuleCoreStats.ST_NET_SERVERS_DEAD_COUNT, server_dead_count);

		label = new Label(sesvers_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.search_query_count") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label search_query_count = new Label(sesvers_stats,SWT.NONE);
		search_query_count.setFont(skin.getLabelFont());
		search_query_count.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stats_fields.put(JMuleCoreStats.SEARCHES_COUNT, search_query_count);
		
		layout_data = new GridData();
		layout_data.horizontalSpan = 2;
		new Label(sesvers_stats,SWT.NONE).setLayoutData(layout_data);
		
		layout_data = new GridData();
		layout_data.horizontalSpan = 2;
		new Label(sesvers_stats,SWT.NONE).setLayoutData(layout_data);
		
		Group sharing_stats = new Group(content,SWT.NONE);
		sharing_stats.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sharing_stats.setLayout(new GridLayout(2,false));
		sharing_stats.setText(_._("mainwindow.statisticstab.tab.general.group.shared"));
		label = new Label(sharing_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.shared_files_count") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label shared_files_count = new Label(sharing_stats,SWT.NONE);
		shared_files_count.setFont(skin.getLabelFont());
		shared_files_count.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stats_fields.put(JMuleCoreStats.ST_DISK_SHARED_FILES_COUNT, shared_files_count);
		
		label = new Label(sharing_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.shared_partial_files") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label shared_partial_files = new Label(sharing_stats,SWT.NONE);
		shared_partial_files.setFont(skin.getLabelFont());
		shared_partial_files.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		stats_fields.put(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_COUNT, shared_partial_files);

		label = new Label(sharing_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.shared_completed_files") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label shared_completed_files = new Label(sharing_stats,SWT.NONE);
		shared_completed_files.setFont(skin.getLabelFont());
		shared_completed_files.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		stats_fields.put(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_COUNT, shared_completed_files);

		label = new Label(sharing_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.shared_files_bytes") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label shared_files_bytes = new Label(sharing_stats,SWT.NONE);
		shared_files_bytes.setFont(skin.getLabelFont());
		shared_files_bytes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		stats_fields.put(JMuleCoreStats.ST_DISK_SHARED_FILES_BYTES, shared_files_bytes);

		label = new Label(sharing_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.completed_files_bytes") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label completed_files_bytes = new Label(sharing_stats,SWT.NONE);
		completed_files_bytes.setFont(skin.getLabelFont());
		completed_files_bytes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		stats_fields.put(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_BYTES, completed_files_bytes);

		label = new Label(sharing_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.general.label.partial_files_bytes") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Label partial_files_bytes = new Label(sharing_stats,SWT.NONE);
		partial_files_bytes.setFont(skin.getLabelFont());
		partial_files_bytes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		stats_fields.put(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_BYTES, partial_files_bytes);

		CTabItem jvm_stats = new CTabItem(stats_tabs,SWT.NONE);
		jvm_stats.setText(_._("mainwindow.statisticstab.tab.jvm"));
		content = new Composite(stats_tabs,SWT.NONE);
		jvm_stats.setControl(content);
		content.setLayout(new GridLayout(2,true));
		
		Group jvm_general_stats = new Group(content,SWT.NONE);
		jvm_general_stats.setText(_._("mainwindow.statisticstab.tab.jvm.group.general"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		jvm_general_stats.setLayoutData(layout_data);
		layout = new GridLayout(2,false);
		jvm_general_stats.setLayout(layout);
		
		label = new Label(jvm_general_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.jvm.label.jvm_name") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		label = new Label(jvm_general_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(runtime_bean.getVmName());
		
		label = new Label(jvm_general_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.jvm.label.jvm_vendor") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		label = new Label(jvm_general_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(runtime_bean.getVmVendor());
		
		label = new Label(jvm_general_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.jvm.label.jvm_version") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		label = new Label(jvm_general_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(runtime_bean.getVmVersion());
		
		
		label = new Label(jvm_general_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.jvm.label.jvm_uptime") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		jvm_uptime = new Label(jvm_general_stats,SWT.NONE);
		jvm_uptime.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		jvm_uptime.setText(TimeFormatter.formatColon(runtime_bean.getUptime()/1000)+"");		

		Group heapmemory_stats = new Group(content,SWT.NONE);
		heapmemory_stats.setText(_._("mainwindow.statisticstab.tab.jvm.group.heap_memory"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		heapmemory_stats.setLayoutData(layout_data);
		layout = new GridLayout(2,false);
		heapmemory_stats.setLayout(layout);
		
		label = new Label(heapmemory_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.jvm.label.heapmemory_init") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		heapmemory_init = new Label(heapmemory_stats,SWT.NONE);
		heapmemory_init.setFont(skin.getLabelFont());
		heapmemory_init.setText(FileFormatter.formatFileSize(heap_memory.getInit()));
		
		label = new Label(heapmemory_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.jvm.label.heapmemory_used") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		heapmemory_used = new Label(heapmemory_stats,SWT.NONE);
		heapmemory_used.setFont(skin.getLabelFont());
		heapmemory_used.setText(FileFormatter.formatFileSize(heap_memory.getUsed()));
		
		label = new Label(heapmemory_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.jvm.label.heapmemory_max") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		heapmemory_max = new Label(heapmemory_stats,SWT.NONE);
		heapmemory_max.setFont(skin.getLabelFont());
		heapmemory_max.setText(FileFormatter.formatFileSize(heap_memory.getMax()));
		
		new Label(heapmemory_stats,SWT.NONE);
		
		Group nonheapmemory_stats = new Group(content,SWT.NONE);
		nonheapmemory_stats.setText(_._("mainwindow.statisticstab.tab.jvm.group.nonheap_memory"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		nonheapmemory_stats.setLayoutData(layout_data);
		layout = new GridLayout(2,false);
		nonheapmemory_stats.setLayout(layout);

		label = new Label(nonheapmemory_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.jvm.label.nonheapmemory_init") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		nonheapmemory_init = new Label(nonheapmemory_stats,SWT.NONE);
		nonheapmemory_init.setFont(skin.getLabelFont());
		nonheapmemory_init.setText(FileFormatter.formatFileSize(nonheap_memory.getInit()));
		
		label = new Label(nonheapmemory_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.jvm.label.nonheapmemory_used") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		nonheapmemory_used = new Label(nonheapmemory_stats,SWT.NONE);
		nonheapmemory_used.setFont(skin.getLabelFont());
		nonheapmemory_used.setText(FileFormatter.formatFileSize(nonheap_memory.getUsed()));
		
		label = new Label(nonheapmemory_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.jvm.label.nonheapmemory_max") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		nonheapmemory_max = new Label(nonheapmemory_stats,SWT.NONE);
		nonheapmemory_max.setFont(skin.getLabelFont());
		nonheapmemory_max.setText(FileFormatter.formatFileSize(nonheap_memory.getMax()));
		
		label = new Label(nonheapmemory_stats,SWT.NONE);
		
		Group thread_stats = new Group(content,SWT.NONE);
		thread_stats.setText(_._("mainwindow.statisticstab.tab.jvm.group.threads"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		thread_stats.setLayoutData(layout_data);
		layout = new GridLayout(2,false);
		thread_stats.setLayout(layout);
		
		label = new Label(thread_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.jvm.label.thread_count") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		thread_count = new Label(thread_stats,SWT.NONE);
		thread_count.setFont(skin.getLabelFont());
		thread_count.setText(thread_bean.getThreadCount()+"");
		
		label = new Label(thread_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.jvm.label.daemon_thread_count") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		daemon_thread_count = new Label(thread_stats,SWT.NONE);
		daemon_thread_count.setFont(skin.getLabelFont());
		daemon_thread_count.setText(thread_bean.getDaemonThreadCount()+"");
		
		label = new Label(thread_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.jvm.label.peak_thread_count") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		peak_thread_count = new Label(thread_stats,SWT.NONE);
		peak_thread_count.setFont(skin.getLabelFont());
		peak_thread_count.setText(thread_bean.getPeakThreadCount()+"");

		label = new Label(thread_stats,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("mainwindow.statisticstab.tab.jvm.label.total_thread_count") + " : ");
		label.setToolTipText(_._("mainwindow.statisticstab.tab.jvm.label.total_thread_count.tooltip"));
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		total_thread_count = new Label(thread_stats,SWT.NONE);
		total_thread_count.setFont(skin.getLabelFont());
		total_thread_count.setText(thread_bean.getTotalStartedThreadCount()+"");
		total_thread_count.setToolTipText(_._("mainwindow.statisticstab.tab.jvm.label.total_thread_count.tooltip"));
		
		stats_tabs.setSelection(network_stats);
	}

	public JMULE_TABS getTabType() {
		return JMULE_TABS.STATISTICS;
	}

	public void lostFocus() {
		GUIUpdater.getInstance().removeRefreshable(refreshable);
	}

	public void obtainFocus() {
		refreshable.refresh();
		GUIUpdater.getInstance().addRefreshable(refreshable);
	}

	public void disposeTab() {
		super.disposeTab();
		tab_content2.dispose();
	}
	
/*	public void create() {
		network_stats = new CTabItem(ctab,SWT.NONE);
		network_stats.setText(_._("mainwindow.statisticstab.tab.general"));
		network_stats.setControl(tab_content);
		
		jvm_stats = new CTabItem(ctab,SWT.NONE);
		jvm_stats.setText(_._("mainwindow.statisticstab.tab.jvm"));
		jvm_stats.setControl(tab_content2);
		
		ctab.setSelection(network_stats);
	}
	
	public void close() {
		network_stats.dispose();
		jvm_stats.dispose();
	}*/

}
