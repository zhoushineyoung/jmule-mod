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

import org.jmule.core.searchmanager.tree.Node;
import org.jmule.core.searchmanager.tree.NodeValue;
import org.jmule.core.searchmanager.tree.Tree;
import org.jmule.core.searchmanager.tree.NodeValue.NodeType;
import org.jmule.core.sharingmanager.FileType;


/**
 * @author binary256
 * @author javajox
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/07/11 17:58:39 $$
 */
public class SearchQuery implements Cloneable {

	private Tree search_tree;
	
	private SearchQueryType query_type = SearchQueryType.SERVER;
	
	/**
	 * Constructs an search query based on the given search string
	 * @param searchStr the given search string
	 */
	public SearchQuery(String searchStr) {
		search_tree = new Tree(searchStr);
	}
	
	public SearchQuery(String searchStr,SearchQueryType type) {
		this(searchStr);
		setQueryType(type);
	}
	
	public SearchQuery(SearchQuery query) {
		search_tree = query.search_tree;
		query_type = query.query_type;
	}
	
	public SearchQueryType getQueryType() {
		return query_type;
	}

	public void setQueryType(SearchQueryType queryType) {
		query_type = queryType;
	}
	
	/**
	 * Gets the search string
	 * @return the search string
	 */
	public String getQuery() {
		Node n = search_tree.getNode(NodeType.FILE_NAME);
		return (String)n.getKey().getValue(Tree.DATA_KEY);
		
	}

	/**
	 * Sets a new search string
	 * @param searchStr the new search string
	 */
	public void setQuery(String searchStr) {
		Node n = search_tree.getNode(NodeType.FILE_NAME);
		n.getKey().setValue(searchStr);
	}
	
	
	public void setMinimalSize(long minimalSize) {
		NodeValue value = new NodeValue(NodeType.MINSIZE);
		value.setValue(minimalSize);
		search_tree.addNodeIfNeed(value);
	}
		
	public void setMaximalSize(long maximalSize) {
		NodeValue value = new NodeValue(NodeType.MAXSIZE);
		value.setValue(maximalSize);
		search_tree.addNodeIfNeed(value);
	}
	
	public void setMinCompleteSources(long completeSources) {
		NodeValue value = new NodeValue(NodeType.MINCOMPLETESRC);
		value.setValue(completeSources);
		search_tree.addNodeIfNeed(value);
	}
	
	public void setMaxCompleteSources(long completeSources) {
		NodeValue value = new NodeValue(NodeType.MAXCOMPLETESRC);
		value.setValue(completeSources);
		search_tree.addNodeIfNeed(value);
	}
	
	public void setMinAvailability(long availability) {
		NodeValue value = new NodeValue(NodeType.MINAVAILABILITY);
		value.setValue(availability);
		search_tree.addNodeIfNeed(value);
	}
	
	public void setMaxAvailability(long availability) {
		NodeValue value = new NodeValue(NodeType.MAXAVAILABILITY);
		value.setValue(availability);
		search_tree.addNodeIfNeed(value);
	}
	
	public void setExtension(String extension) {
		NodeValue value = new NodeValue(NodeType.EXTENSION);
		value.setValue(extension);
		search_tree.addNodeIfNeed(value);
	}
	
	public void setFileType(FileType type) {
		NodeValue value = new NodeValue(NodeType.FILETYPE);
		value.setValue(type);
		search_tree.addNodeIfNeed(value);
	}
	
	public Tree getSearchTree() {
		return search_tree;
	}
	
	public int hashCode() {
		return getQuery().hashCode();
	}
	
	public boolean equals(Object object) {
		if (object == null) return false;
		if (!(object instanceof SearchQuery)) return false;
		SearchQuery query = (SearchQuery)object;
		return query.getQuery().equals(getQuery());
	}
	
	public Object clone() throws CloneNotSupportedException {
		//return super.clone();
		SearchQuery query = new SearchQuery(getQuery(), getQueryType());
		query.search_tree = search_tree;
		return query;
	}
	
}
