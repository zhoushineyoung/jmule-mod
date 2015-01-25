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
package org.jmule.ui.swing.wizards;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


//internal shared folders store is used for both JList(GUI) & SharingManager
// ListModel - is used for JList
// Iterable<File> - is used for SharingManager
/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:11 $$
 */
public class ChosenFolders  implements ListModel, Iterable<File>  {

	private List<File> chosen_folders = new CopyOnWriteArrayList<File>();
	private List<ListDataListener> listeners = new LinkedList<ListDataListener>();

	public Object getElementAt(int index) {
		
		return chosen_folders.get( index );
	}
	
	public List<File> getFoldersList() {
		
		return chosen_folders;
	}

	public int getSize() {
		
		return chosen_folders.size();
	}
	
	public void add(File file) {
		
		chosen_folders.add( file );
		int i = chosen_folders.indexOf( file );
		fireIntervalAdded( i, i );
	}
	
	public void remove(File file) {
		
		chosen_folders.remove( file );
		int i = chosen_folders.indexOf( file ) + 1;
		fireIntervalRemoved( i, i );
	}
	
	public void removeAll() {
		
		chosen_folders.clear();
		fireIntervalRemoved(1,getSize());
	}
	
	public boolean contains(File f) {
		
		return chosen_folders.contains( f );
	}

	public Iterator<File> iterator() {
		
        return chosen_folders.iterator();
	}
	
	// hmm.. need to think..
	private void fireContentsChanged() {
		
	}
	
	private void fireIntervalAdded(int i, int j) {
		 ChosenFolders _this = this;
		 for(ListDataListener listener : listeners) {
			 listener.intervalAdded( new ListDataEvent(_this, ListDataEvent.INTERVAL_ADDED, i, j) {
				 
			 });
		 }
	}
	
	private void fireIntervalRemoved(int i, int j) {
		ChosenFolders _this = this;
		for(ListDataListener listener : listeners) {
			listener.intervalRemoved( new ListDataEvent(_this, ListDataEvent.INTERVAL_REMOVED, i, j) {
				
			});
		}
	}
	
	public void addListDataListener(ListDataListener l) {
		
		listeners.add( l );
	}
	
	public void removeListDataListener(ListDataListener l) {
		
		listeners.remove( l );
	}
}
