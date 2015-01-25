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
package org.jmule.core.jkad.indexer;

import static org.jmule.core.jkad.JKadConstants.INDEXER_CLEAN_DATA_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.INDEX_MAX_KEYWORDS;
import static org.jmule.core.jkad.JKadConstants.INDEX_MAX_NOTES;
import static org.jmule.core.jkad.JKadConstants.INDEX_MAX_SOURCES;
import static org.jmule.core.jkad.JKadConstants.KEY_INDEX_DAT;
import static org.jmule.core.jkad.JKadConstants.NOTE_INDEX_DAT;
import static org.jmule.core.jkad.JKadConstants.SRC_INDEX_DAT;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.packet.tag.IntTag;
import org.jmule.core.edonkey.packet.tag.ShortTag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.jkad.ClientID;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.InternalJKadManager;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.JKadManagerSingleton;
import org.jmule.core.jkad.utils.timer.Task;
import org.jmule.core.jkad.utils.timer.Timer;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.sharingmanager.SharingManagerSingleton;
import org.jmule.core.utils.Convert;

/**
 * Created on Jan 5, 2009
 * @author binary256
 * @version $Revision: 1.12 $
 * Last changed by $Author: binary255 $ on $Date: 2010/09/04 16:14:25 $
 */
public class Indexer {
	
	private static class IndexerSingletonHolder {
		private static final Indexer INSTANCE = new Indexer();
	}
	
	public static Indexer getSingleton() {
		return IndexerSingletonHolder.INSTANCE;
	}
	
	private Map<Int128, Index> notes = new ConcurrentHashMap<Int128,Index>();
	private Map<Int128, Index> keywords = new ConcurrentHashMap<Int128, Index>();
	private Map<Int128, Index> sources = new ConcurrentHashMap<Int128, Index>();
	
	private Task save_data_task;
	private Task cleaner_task;
	private boolean is_started = false;

	private Indexer() {
		
	}
	
	public void start() {
		is_started = true;
		try {
			notes.putAll(SrcIndexDat.loadFile(NOTE_INDEX_DAT));
			keywords.putAll(SrcIndexDat.loadFile(KEY_INDEX_DAT));
			sources.putAll(SrcIndexDat.loadFile(SRC_INDEX_DAT));
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		
		save_data_task = new Task() {
			public void run() {
					try {
						SrcIndexDat.writeFile(NOTE_INDEX_DAT, notes);
					} catch (Throwable e) {
						e.printStackTrace();
					}

					try {
						SrcIndexDat.writeFile(KEY_INDEX_DAT, keywords);
					} catch (Throwable e) {
						e.printStackTrace();
					}

					try {
							SrcIndexDat.writeFile(SRC_INDEX_DAT, sources);
					} catch (Throwable e) {
						e.printStackTrace();
					}
			}
		};
		
		Timer.getSingleton().addTask(JKadConstants.INDEXER_SAVE_DATA_INTERVAL, save_data_task, true);
		
		cleaner_task = new Task() {
			public void run() {
					for(Int128 id : notes.keySet()) {
						Index index = notes.get(id);
						index.removeContactsWithTimeOut(JKadConstants.TIME_24_HOURS);
						if (index.isEmpty()) notes.remove(id);
				}
				
					for(Int128 id : keywords.keySet()) {
						Index index = keywords.get(id);
						index.removeContactsWithTimeOut(JKadConstants.TIME_24_HOURS);
						if (index.isEmpty()) keywords.remove(id);
					}
				
					for(Int128 id : sources.keySet()) {
						Index index = sources.get(id);
						index.removeContactsWithTimeOut(JKadConstants.TIME_24_HOURS);
						if (index.isEmpty()) sources.remove(id);
					}
			}
		};
		Timer.getSingleton().addTask(INDEXER_CLEAN_DATA_INTERVAL, cleaner_task, true);
		
	}

	public void stop() {
		is_started = false;
		Timer.getSingleton().removeTask(save_data_task);
		Timer.getSingleton().removeTask(cleaner_task);
		
			notes.clear();
			keywords.clear();
			sources.clear();		
	}
	
	public boolean isStarted() {
		return is_started;
	}
	
	public int getKeywordLoad() {
		return (keywords.size() * 100) / INDEX_MAX_KEYWORDS;
	}
	
	public int getNoteLoad() {
		return (notes.size() * 100) / INDEX_MAX_NOTES;
	}
	
	public int getFileSourcesLoad() {
		return (sources.size() * 100) / INDEX_MAX_SOURCES;
	}
	
	public boolean hasKeywordSource(ClientID clientID) {
		for(Index i : keywords.values())
			if (i.containsClientID(clientID))
				return true;
		return false;
	}
	
	public boolean hasNoteSource(ClientID clientID) {
		for(Index i : notes.values())
			if (i.containsClientID(clientID))
				return true;
		return false;
	}
	
	public boolean hasFileSource(ClientID clientID) {
		for(Index i : sources.values())
			if (i.containsClientID(clientID))
				return true;
		return false;
	}
	
	public int addFileSource(Int128 fileID, Source source) {
		if (sources.size() >= INDEX_MAX_SOURCES)
			return 100;
		Index index = sources.get(fileID);
		if (index == null) {
			index = new SourceIndex(fileID);
			index.addSource(source);
			sources.put(fileID,index);
			return 1;
		}
		if (index.getSourceCount() == JKadConstants.SOURCES_MAX_PER_FILE) {
			((SourceIndex)index).removeOldestSource();
			index.addSource(source);
			return 100;
		}
		index.addSource(source);
		return (index.getSourceCount()*100)/JKadConstants.SOURCES_MAX_PER_FILE;
	}
	
	public int addKeywordSource(Int128 keywordID, Source source) {
		if (keywords.size() >= INDEX_MAX_KEYWORDS)
			return 100;
		Index index = keywords.get(keywordID);
		if (index == null) {
			index = new KeywordIndex(keywordID);
			keywords.put(keywordID, index);
			index.addSource(source);
			return 1;
		}
		if (index.getSourceCount() < JKadConstants.KEYWORD_MAX_SOURCES)
			return 100;
		if (( !index.containsClientID(source.getClientID()) ) || (index.getSourceCount() <= JKadConstants.KEYWORD_MAX_SOURCES_FOR_NEW_SOURCES)) {
			index.addSource(source);
			return (index.getSourceCount()*100)/JKadConstants.KEYWORD_MAX_SOURCES;
		}
		return 100;
	}
	
	public int addNoteSource(Int128 noteID, Source source) {
		if (notes.size() >= INDEX_MAX_NOTES)
			return 100;
		
		Index index = notes.get(noteID);		
		if (index == null) {
			index = new NoteIndex(noteID);
			index.addSource(source);
			notes.put(noteID, index);
			return 1;
		}
		
		if (index.getSourceCount() >= JKadConstants.NOTES_MAX_PER_FILE) {
			index.removeOldestSource();
			index.addSource(source);
			return 100;
		}
		index.addSource(source);
		return (index.getSourceCount()*100) / JKadConstants.NOTES_MAX_PER_FILE;
	}

	public Collection<Source> getFileSources(Int128 targetID) {
		Index indexer =  sources.get(targetID);
		
		FileHash fileHash = new FileHash(targetID.toByteArray());
		if (SharingManagerSingleton.getInstance().hasFile(fileHash)) {
			if (indexer == null) indexer = new Index(targetID);
			SharedFile file = SharingManagerSingleton.getInstance().getSharedFile(fileHash);
			InternalJKadManager _jkad_manager = (InternalJKadManager) JKadManagerSingleton.getInstance();
			ConfigurationManager config_manager = ConfigurationManagerSingleton.getInstance();
			TagList tagList = new TagList();
			tagList.addTag(new IntTag(JKadConstants.TAG_SOURCEIP, Convert.byteToInt(_jkad_manager.getIPAddress().getAddress())));
			try {
				tagList.addTag(new ShortTag(JKadConstants.TAG_SOURCEPORT, Convert.intToShort(config_manager.getTCP())));
			} catch (ConfigurationManagerException e) {
				e.printStackTrace();
				_jkad_manager.disconnect();
			}
			try {
				tagList.addTag(new ShortTag(JKadConstants.TAG_SOURCEUPORT, Convert.intToShort(config_manager.getUDP())));
			} catch (ConfigurationManagerException e) {
				e.printStackTrace();
				_jkad_manager.disconnect();
			}
			tagList.addTag(new IntTag(JKadConstants.TAG_FILESIZE, Convert.longToInt(file.length())));
			Source my_source = new Source(_jkad_manager.getClientID(), tagList);
			
			indexer.addSource(my_source);
		}
		
		if (indexer == null) return null;
		return indexer.getSourceList();
	}
	
	public Collection<Source> getFileSources(Int128 targetID,short start_position, long fileSize) {
		return this.getFileSources(targetID);
	}

	public Collection<Source> getKeywordSources(Int128 targetID) {
		Index indexer = keywords.get(targetID);
		if (indexer == null) return null;
		return indexer.getSourceList();
	}

	public Collection<Source> getNoteSources(Int128 noteID) {
		Index indexer = notes.get(noteID);
		if (indexer == null) return null;
		return indexer.getSourceList();
	}
	public Collection<Source> getNoteSources(Int128 noteID, long fileSize) {
		return getNoteSources(noteID);
	}

	
}
