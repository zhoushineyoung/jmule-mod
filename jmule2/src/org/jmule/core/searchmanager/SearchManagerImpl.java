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
package org.jmule.core.searchmanager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.JMThread;
import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.edonkey.ED2KConstants.ServerFeatures;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.InternalJKadManager;
import org.jmule.core.jkad.JKadException;
import org.jmule.core.jkad.JKadManagerSingleton;
import org.jmule.core.jkad.indexer.Source;
import org.jmule.core.jkad.search.Search;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.servermanager.Server;
import org.jmule.core.servermanager.ServerManager;
import org.jmule.core.servermanager.ServerManagerSingleton;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.core.statistics.JMuleCoreStatsProvider;
import org.jmule.core.utils.timer.JMTimer;
import org.jmule.core.utils.timer.JMTimerTask;

/**
 * Created on 2008-Jul-06
 * @author binary
 * @author javajox
 * @version $$Revision: 1.20 $$ Last changed by $$Author: binary255 $$ on $$Date: 2010/09/04 16:16:39 $$
 */
public class SearchManagerImpl extends JMuleAbstractManager implements InternalSearchManager {

	private static final int SEARCH_SERVER_ANSWER_WAIT 		= 8000;
	private static final int GLOBAL_SEARCH_TIMEOUT 			= 1000 * 60;
	private static final int SEARCH_QUEUE_SCAN_INTERVAL 	= 1000;
	private static final int GLOBAL_SEARCH_QUERY_INTERVAL 	= 100;

	private Search jkad_search = Search.getSingleton();
	private InternalJKadManager _jkad = (InternalJKadManager) JKadManagerSingleton.getInstance();
	private InternalNetworkManager _network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();

	private Map<SearchQuery, SearchResult> search_result_list 	= new ConcurrentHashMap<SearchQuery, SearchResult>();
	private Queue<SearchQuery> server_search_request_queue 		= new ConcurrentLinkedQueue<SearchQuery>();
	private Queue<SearchQuery> kad_search_request_queue 		= new ConcurrentLinkedQueue<SearchQuery>();
	private Queue<SearchQuery> global_search_request_queue 		= new ConcurrentLinkedQueue<SearchQuery>();
	private ServerManager server_manager = ServerManagerSingleton.getInstance();

	private List<SearchResultListener> search_result_listeners = new LinkedList<SearchResultListener>();

	private Map<SearchQuery, Int128> kad_searches = new HashMap<SearchQuery, Int128>();
	private long searches_count = 0;

	private JMTimer timer = new JMTimer();
	private JMTimerTask server_search_task;
	private JMThread global_search_thread = null;

	private SearchQuery server_search_query 		= null;
	private SearchQuery kad_search_query 			= null;
	private SearchQuery global_search_query 		= null;

	SearchManagerImpl() {
		server_search_task = new JMTimerTask() {
			
			long last_server_search_request = 0;
			long last_global_search_request = 0;
			
			public void run() {
				
				if (server_search_query!=null)
					if (System.currentTimeMillis() - last_server_search_request > SEARCH_SERVER_ANSWER_WAIT) {
						notifySearchFailed(server_search_query);
						server_search_request_queue.remove(server_search_query);
						server_search_query = null;
					}
							
				if (server_search_query == null)
					if (!server_search_request_queue.isEmpty())
						if (server_manager.isConnected()) {
								server_search_query = server_search_request_queue.poll();
								_network_manager.doSearchOnServer(server_search_query);
								notifySearchStarted(server_search_query);
								last_server_search_request = System.currentTimeMillis();
						}
				
				
					if (global_search_query!=null)
						if (System.currentTimeMillis() - last_global_search_request > GLOBAL_SEARCH_TIMEOUT) {
							if ((global_search_thread!=null) && (global_search_thread.isAlive()))
								global_search_thread.JMStop();
							notifySearchFailed(global_search_query);
							global_search_request_queue.remove(global_search_query);
							global_search_query = null;
						}
												
					if (global_search_query == null)
						if (!global_search_request_queue.isEmpty())
						if ((global_search_thread==null) || (!global_search_thread.isAlive())) {
							global_search_query = global_search_request_queue.poll();
							global_search_thread = new JMThread() {
								boolean loop = true;
								public void run() {
									for(Server server : server_manager.getServers()) {
										if (!loop) break;
										
										if (server.getFeatures().contains(ServerFeatures.LargeFiles) && server.getFeatures().contains(ServerFeatures.GetFiles))
											_network_manager.sendServerUDPSearch3Request(server.getAddress(), server.getUDPPort(), global_search_query);
										else
										if (server.getFeatures().contains(ServerFeatures.LargeFiles))
											_network_manager.sendServerUDPSearch2Request(server.getAddress(), server.getUDPPort(), global_search_query);
										else
											_network_manager.sendServerUDPSearchRequest(server.getAddress(), server.getUDPPort(), global_search_query);
										
										synchronized (this) {
											try {
												this.wait(GLOBAL_SEARCH_QUERY_INTERVAL);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
										}
									}
									if (loop) {
										synchronized (this) {
											try {
												this.wait(GLOBAL_SEARCH_QUERY_INTERVAL);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
										}
										notifySearchCompleted(global_search_query);
										global_search_request_queue.remove(global_search_query);
										global_search_query = null;
									}
								}
								
								public void JMStop() {
									loop = false;
									synchronized (this) {
										this.notify();
									}
								}
							};
							global_search_thread.start();
							notifySearchStarted(global_search_query);
							last_global_search_request = System.currentTimeMillis();
						}
					
				
				/*while (!server_search_request_queue.isEmpty()) {
					server_search_request = server_search_request_queue.poll();
					if (server_manager.isConnected()) {
						network_manager.doSearchOnServer(server_search_request);
						notifySearchStarted(server_search_request);
						try {
							Thread.sleep(SEARCH_ANSWER_WAIT);
						} catch (InterruptedException e) {
							e.printStackTrace();
							return;
						}
					}
					if (server_search_request_queue.contains(server_search_request)) {
						notifySearchFailed(server_search_request);
						server_search_request_queue.remove(server_search_request);
					}
				}
				this.stopTask();*/
			}
		};
	}

	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}

		Set<String> types = new HashSet<String>();
		types.add(JMuleCoreStats.SEARCHES_COUNT);
		JMuleCoreStats.registerProvider(types, new JMuleCoreStatsProvider() {
			public void updateStats(Set<String> types,
					Map<String, Object> values) {
				if (types.contains(JMuleCoreStats.SEARCHES_COUNT)) {
					values.put(JMuleCoreStats.SEARCHES_COUNT, searches_count);
				}
			}
		});
	}

	public void shutdown() {
		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		
		if ((global_search_thread!=null) && (global_search_thread.isAlive()))
			global_search_thread.JMStop();
		
		timer.removeTask(server_search_task);
		timer.cancelAllTasks();
	}

	public void start() {
		try {
			super.start();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		
		timer.addTask(server_search_task, SEARCH_QUEUE_SCAN_INTERVAL, true);
	}

	protected boolean iAmStoppable() {
		return false;
	}

	public void removeSearch(SearchQuery searchRequest) {
		server_search_request_queue.remove(searchRequest);
		kad_search_request_queue.remove(searchRequest);
		if (kad_searches.containsKey(searchRequest)) {
			Int128 searchID = kad_searches.get(searchRequest);
			jkad_search.cancelSearch(searchID);
		}
		if (searchRequest.getQueryType() == SearchQueryType.KAD) {
			processKadSearchRequest();
		}
	}

	public void search(String searchString) {
		SearchQuery search_request = new SearchQuery(searchString);
		search(search_request);
	}

	public void search(SearchQuery searchRequest) {
		if (searchRequest.getQueryType() == SearchQueryType.SERVER) {
			server_search_request_queue.offer(searchRequest);
		}
		if (searchRequest.getQueryType() == SearchQueryType.KAD) {
			if (!_jkad.isConnected())
				return;
			kad_search_request_queue.offer(searchRequest);
			if (kad_search_request_queue.size() == 1)
				processKadSearchRequest();
		}
		if (searchRequest.getQueryType() == SearchQueryType.GLOBAL) {
			global_search_request_queue.offer(searchRequest);
		}

		if (searchRequest.getQueryType() == SearchQueryType.SERVER_KAD) {
			if (_jkad.isConnected()) {
				kad_search_request_queue.offer(searchRequest);
				if (kad_search_request_queue.size() == 1)
					processKadSearchRequest();
			}

			server_search_request_queue.add(searchRequest);
		}

		searches_count++;
	}

	public void addSeachResultListener(SearchResultListener searchResultListener) {
		search_result_listeners.add(searchResultListener);
	}

	private void processKadSearchRequest() {
		if (kad_search_request_queue.isEmpty())
			return;
		kad_search_query = (SearchQuery) kad_search_request_queue.poll(); // remove
																			// query
																			// from
																			// queue
																			// after
																			// complete
																			// search
		Int128 keyword_id;
		try {
			try {
				keyword_id = jkad_search.searchKeyword(kad_search_query
						.getQuery(),
						new org.jmule.core.jkad.search.SearchResultListener() {
							SearchQuery searchquery = (SearchQuery) kad_search_query.clone();
							SearchResultItemList result_list = new SearchResultItemList();
							SearchResult search_result = new SearchResult(result_list, searchquery);

							public void processNewResults(List<Source> result) {
								result_list.clear();
								for (Source source : result) {
									SearchResultItem item = new SearchResultItem(
											source.getClientID().toFileHash(),
											null, (short) 0, SearchQueryType.KAD);
									for (Tag tag : source.getTagList()) {
										item.addTag(tag);
									}
									result_list.add(item);
								}
								notifySearchArrived(search_result);
							}

							public void searchFinished() {
								notifySearchCompleted(searchquery);
								//kad_search_request_queue.poll();
								processKadSearchRequest();

							}

							public void searchStarted() {
								notifySearchStarted(searchquery);
							}

						});
				kad_searches.put(kad_search_query, keyword_id);
			} catch (JKadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				notifySearchCompleted(kad_search_query);
			}
			

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

	}

	public void receivedServerSearchResult(SearchResultItemList resultList) {
		SearchResult searchResult = new SearchResult(resultList,server_search_query, server_manager.getConnectedServer());
		search_result_list.put(server_search_query, searchResult);
		notifySearchArrived(searchResult);
		if (searchResult.getSearchQuery().getQueryType() != SearchQueryType.SERVER_KAD)
			notifySearchCompleted(searchResult.searchQuery);
	}
	
	public void receivedServerUDPSearchResult(SearchResultItemList resultList) {
		SearchResult search_result = new SearchResult(resultList, global_search_query);
		notifySearchArrived(search_result);
	}

	public void removeSearchResultListener(SearchResultListener searchResultListener) {
		search_result_listeners.remove(searchResultListener);
	}

	private void notifySearchArrived(SearchResult search_result) {
		for (SearchResultListener listener : search_result_listeners) {
			try {
				listener.resultArrived(search_result);
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private void notifySearchStarted(SearchQuery query) {
		for (SearchResultListener listener : search_result_listeners) {
			try {
				listener.searchStarted(query);
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private void notifySearchCompleted(SearchQuery query) {
		for (SearchResultListener listener : search_result_listeners) {
			try {
				listener.searchCompleted(query);
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private void notifySearchFailed(SearchQuery query) {
		for (SearchResultListener listener : search_result_listeners) {
			try {
				listener.searchFailed(query);
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
}
