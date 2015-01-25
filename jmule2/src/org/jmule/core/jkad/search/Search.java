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
package org.jmule.core.jkad.search;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.JKadException;
import org.jmule.core.jkad.indexer.Source;
import org.jmule.core.jkad.utils.Convert;
import org.jmule.core.jkad.utils.MD4;


/**
 * Created on Jan 8, 2009
 * @author binary256
 * @version $Revision: 1.11 $
 * Last changed by $Author: binary255 $ on $Date: 2010/10/23 05:51:17 $
 */
public class Search {
	private Map<Int128, SearchTask> searchTasks = new ConcurrentHashMap<Int128, SearchTask>();
	private Collection<SearchListener> listener_list = new ConcurrentLinkedQueue<SearchListener>();
	
	private boolean isStarted = false;
		
	private static class SearchSingletonHolder {
		private static final Search INSTANCE = new Search();
	}
	
	public static Search getSingleton() {
		return SearchSingletonHolder.INSTANCE;
	}
	
	private Search() {
	}
	
	public SearchTask getSearchTask(Int128 id) {
		return searchTasks.get(id);
	}
	
	public void start() {
		isStarted = true;
	}
	
	public void stop() {
		for(Int128 key : searchTasks.keySet()) {
			SearchTask task = searchTasks.get(key);
			if (task.isStarted()) {
				task.stop();
			}
		}
		isStarted = false;
	}
	
	public boolean isStarted() {
		return isStarted;
	}
	
	public Int128 searchKeyword(String keyword) throws JKadException {
		return searchKeyword(keyword, null);
	}
	
	public Int128 searchKeyword(String keyword, SearchResultListener listener) throws JKadException {
		byte[] tmp = MD4.MD4Digest(keyword.getBytes()).toByteArray();
		Convert.updateSearchID(tmp);
		Int128 keywordID = new Int128(tmp);
		
		if (searchTasks.containsKey(keywordID)) return null;
		KeywordSearchTask search_task = new KeywordSearchTask(keywordID);
		search_task.setSearchKeyword(keyword);
		search_task.setSearchResultListener(listener);
		searchTasks.put(keywordID, search_task);
		notifyListeners(search_task, SearchStatus.ADDED);
		search_task.start();
		notifyListeners(search_task, SearchStatus.STARTED);
		return keywordID;
	}

	public void searchSources(Int128 fileID,long fileSize) throws JKadException {
		searchSources(fileID, null,fileSize);
	}
	
	public void searchSources(Int128 fileID,SearchResultListener listener,long fileSize) throws JKadException {
		if (searchTasks.containsKey(fileID)) return;
		SourceSearchTask search_task = new SourceSearchTask(fileID,fileSize);
		search_task.setSearchResultListener(listener);
		searchTasks.put(fileID, search_task);
		notifyListeners(search_task, SearchStatus.ADDED);
		search_task.start();
		notifyListeners(search_task, SearchStatus.STARTED);
	}

	public void searchNotes(Int128 fileID,long fileSize) throws JKadException {
		searchNotes(fileID,null,fileSize);
	}
	
	public void searchNotes(Int128 fileID,SearchResultListener listener, long fileSize) throws JKadException {
		byte[] t = fileID.toByteArray();
		Convert.updateSearchID(t);
		Int128 updatedID = new Int128(t);
		if (searchTasks.containsKey(updatedID)) return;
		NoteSearchTask search_task = new NoteSearchTask( updatedID,fileSize);
		search_task.setSearchResultListener(listener);
		searchTasks.put(updatedID, search_task);
		notifyListeners(search_task, SearchStatus.ADDED);
		search_task.start();
		notifyListeners(search_task, SearchStatus.STARTED);
	}
	
	public void processSearchResults(IPAddress sender, final Int128 targetID, final List<Source> sources) {
		SearchTask task = searchTasks.get(targetID);
		if (task == null) return ;
		task.addSearchResult(sources);
		if (task instanceof KeywordSearchTask)
			if (task.getResultCount()>=JKadConstants.MAX_KEYWORD_SEARCH_RESULTS)
				cancelSearch(targetID);
		if (task instanceof SourceSearchTask)
			if (task.getResultCount()>=JKadConstants.MAX_SOURCES_SEARCH_RESULTS)
				cancelSearch(targetID);
		if (task instanceof NoteSearchTask)
			if (task.getResultCount()>=JKadConstants.MAX_NOTES_SEARCH_RESULTS)
				cancelSearch(targetID);
	}
	
	public boolean hasSearchTask(Int128 searchID) {
		return searchTasks.containsKey(searchID);
	}
	
	public Collection<Source> getSearchResults(Int128 searchID) {
		return searchTasks.get(searchID).getSearchResults();
	}

	public void cancelSearch(Int128 searchID) {
		if (!hasSearchTask(searchID)) return ;
		SearchTask task = searchTasks.get(searchID);
		task.stopSearchRequest();
		notifyListeners(task, SearchStatus.STOPPED);
		removeSearchID(searchID);
	}
	
	public Collection<SearchTask> getSearchTasks() {
		return searchTasks.values();
	}
	
	/**
	 * Only remove search by ID
	 * @param searchID
	 */
	void removeSearchID(Int128 searchID) {
		SearchTask task = searchTasks.get(searchID);
		searchTasks.remove(searchID);
		notifyListeners(task, SearchStatus.REMOVED);
	}
	
	public String toString() {
		String result = " [ ";
		for(Int128 key : searchTasks.keySet()) {
			SearchTask task = searchTasks.get(key);
			result += "Task ID : " + key.toHexString() + "\n";
			result += "Value   : \n" + task + "\n";
		}
		result += " ] ";
		return result;
	}
	
	private enum SearchStatus {ADDED, STARTED, STOPPED, REMOVED}
	
	private void notifyListeners(SearchTask searchTask, SearchStatus status) {
		for(SearchListener listener : listener_list) {
			try {
				if (status == SearchStatus.ADDED) {
					listener.searchAdded(searchTask);
					continue;
				}
				if (status == SearchStatus.STARTED) {
					listener.searchStarted(searchTask);
					continue;
				}
				if (status == SearchStatus.STOPPED) {
					listener.searchStopped(searchTask);
					continue;
				}
				if (status == SearchStatus.REMOVED) {
					listener.searchRemoved(searchTask);
					continue;
				}
			}catch(Throwable t) {
				
			}
		}
	}
	
	public void addListener(SearchListener listener) {
		listener_list.add(listener);
	}
	
	public void removeListener(SearchListener listener) {
		listener_list.remove(listener);
	}
	
}
