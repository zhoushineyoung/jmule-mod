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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKadException;
import org.jmule.core.jkad.indexer.Source;

/**
 * Created on Jan 8, 2009
 * @author binary256
 * @version $Revision: 1.7 $
 * Last changed by $Author: binary255 $ on $Date: 2010/07/06 08:55:52 $
 */
public abstract class SearchTask {

	protected Int128 searchID;

	protected Collection<Source> searchResults = new ConcurrentLinkedQueue<Source>();
	protected boolean isStarted = false;
	
	protected SearchResultListener listener = null;
	
	public SearchTask(Int128 searchID) {
		super();
		this.searchID = searchID;
	}

	public abstract void start() throws JKadException;
	public abstract void stop();
	
	public abstract void stopSearchRequest();
	
	public void setSearchResultListener(SearchResultListener listener) {
		this.listener = listener;
	}
	
	public Int128 getSearchID() {
		return searchID;
	}

	public Collection<Source> getSearchResults() {
		return searchResults;
	}
	
	public int getResultCount() {
		return searchResults.size();
	}

	public boolean isStarted() {
		return isStarted;
	}
	
	void addSearchResult(Source result) {
		searchResults.add(result);
	}
	
	void addSearchResult(List<Source> result) {
		List<Source> unicalList = new ArrayList<Source>();
		for(Source s : result)
			if (!searchResults.contains(s))
				unicalList.add(s);
		searchResults.addAll(unicalList);
		if (listener != null)
			listener.processNewResults(unicalList);
	}
}
