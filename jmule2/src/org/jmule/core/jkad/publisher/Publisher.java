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
package org.jmule.core.jkad.publisher;

import static org.jmule.core.jkad.JKadConstants.MAX_CONCURRENT_PUBLISH_FILES;
import static org.jmule.core.jkad.JKadConstants.MAX_CONCURRENT_PUBLISH_KEYWORDS;
import static org.jmule.core.jkad.JKadConstants.MAX_CONCURRENT_PUBLISH_NOTES;
import static org.jmule.core.jkad.JKadConstants.MAX_PUBLISH_NOTES;
import static org.jmule.core.jkad.JKadConstants.PUBLISH_KEYWORD_CHECK_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.PUBLISH_KEYWORD_ON_ITERATION;
import static org.jmule.core.jkad.JKadConstants.PUBLISH_KEYWORD_SCAN_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.PUBLISH_NOTE_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.PUBLISH_SOURCE_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.TAG_FILENAME;
import static org.jmule.core.jkad.JKadConstants.TAG_FILERATING;
import static org.jmule.core.jkad.JKadConstants.TAG_FILESIZE;
import static org.jmule.core.jkad.JKadConstants.TAG_SOURCEIP;
import static org.jmule.core.jkad.JKadConstants.TAG_SOURCEPORT;
import static org.jmule.core.jkad.JKadConstants.TAG_SOURCETYPE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.packet.tag.IntTag;
import org.jmule.core.edonkey.packet.tag.StringTag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.JKadException;
import org.jmule.core.jkad.JKadManager;
import org.jmule.core.jkad.JKadManagerSingleton;
import org.jmule.core.jkad.utils.Convert;
import org.jmule.core.jkad.utils.MD4;
import org.jmule.core.jkad.utils.Utils;
import org.jmule.core.jkad.utils.timer.Task;
import org.jmule.core.jkad.utils.timer.Timer;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.sharingmanager.SharingManager;
import org.jmule.core.sharingmanager.SharingManagerSingleton;

/**
 * Created on Jan 14, 2009
 * @author binary256
 * @version $Revision: 1.15 $
 * Last changed by $Author: binary255 $ on $Date: 2010/08/31 10:28:30 $
 */

public class Publisher {
	private Map<Int128, PublishTask> publish_tasks = new ConcurrentHashMap<Int128, PublishTask>();

	private Collection<PublisherListener> listener_list = new ConcurrentLinkedQueue<PublisherListener>();
	private boolean isStarted = false;
	private PublishTaskListener keywordTaskListener;
	private PublishTaskListener noteTaskListener;
	private PublishTaskListener sourceTaskListener;
	
	private KeywordPublishQueue keyword_queue = new KeywordPublishQueue();
	
	public boolean isStarted() {
		return isStarted;
	}
	
	private static class PublisherSingletonHolder {
		private static final Publisher INSTANCE = new Publisher();
	}
	
	public static Publisher getInstance() {
		return PublisherSingletonHolder.INSTANCE;
	}
	
	private Task keyword_publisher;
	private Task keyword_scanner;
	private Task source_publisher;
	private Task note_publisher;
	private JKadManager _jkad_manager = null;
	
	private Publisher() {
		keywordTaskListener = new PublishTaskListener() {
			public void taskStarted(PublishTask task) {
				
			}

			public void taskStopped(PublishTask task) {
				removeTask(task.getPublishID());
			}

			public void taskTimeOut(PublishTask task) {
				removeTask(task.getPublishID());
			}
			
		};
		
		noteTaskListener = new PublishTaskListener() {
			public void taskStarted(PublishTask task) {
				
			}

			public void taskStopped(PublishTask task) {
				removeTask(task.getPublishID());
			}

			public void taskTimeOut(PublishTask task) {
				removeTask(task.getPublishID());
			}
			
		};
		
		sourceTaskListener = new PublishTaskListener() {
			public void taskStarted(PublishTask task) {
				
			}

			public void taskStopped(PublishTask task) {
				removeTask(task.getPublishID());
			}

			public void taskTimeOut(PublishTask task) {
				removeTask(task.getPublishID());
			}
			
		};
		
	}
	
	public void start() {
		isStarted = true;
		
		_jkad_manager = JKadManagerSingleton.getInstance();
		
		keyword_publisher = new Task() {
			List<Int128> publishing_keywords = new ArrayList<Int128>();
						
			@Override
			public void run() {
				if (!_jkad_manager.isConnected())
					return;
								
				int to_publish = MAX_CONCURRENT_PUBLISH_KEYWORDS;
				List<Int128> to_remove = new ArrayList<Int128>();
				
				for(Int128 keywordID : publishing_keywords) { 
					if (isPublishing(keywordID))
						to_publish--;
					else
						to_remove.add(keywordID);
					if (to_publish == 0)
						break;
				}
				
				publishing_keywords.removeAll(to_remove);
				
				if (to_publish == 0)
					return;
				
				SharingManager _sharing_manager = SharingManagerSingleton.getInstance();
				List<String> used_keywords = new ArrayList<String>();
				
				while(to_publish != 0) {
					String keyword = keyword_queue.keyword_queue.poll();
					if (keyword == null)
						break;
					
					used_keywords.add(keyword);
					
					byte[] tmp2 = MD4.MD4Digest(keyword.getBytes()).toByteArray();
					Convert.updateSearchID(tmp2);
					Int128 keywordID = new Int128(tmp2);
					
					if (publishing_keywords.contains(keywordID))
						continue;
										
					Deque<PublishKeywordContainer> keyword_files = keyword_queue.keyword_files.get(keyword);
										
					Collection<SharedFile> files_to_publish = new ArrayList<SharedFile>();
					Collection<PublishKeywordContainer> used_containers = new ArrayList<Publisher.PublishKeywordContainer>();
					int i = 0;
					while (i < PUBLISH_KEYWORD_ON_ITERATION) {
						PublishKeywordContainer container = keyword_files.poll();
						if (container == null)
							break;
						
						if (_sharing_manager.hasFile(container.fileHash))
							used_containers.add(container);
						
						if (System.currentTimeMillis() - container.publishTime < JKadConstants.TIME_24_HOURS)
							break;
						
						container.publishTime = System.currentTimeMillis();
						
						if (_sharing_manager.hasFile(container.fileHash)) {
							files_to_publish.add(_sharing_manager.getSharedFile(container.fileHash));
							i++;
						}
					}
					
					keyword_files.addAll(used_containers);
										
					if (i == 0)
						continue;
					
					to_publish--;
					
					Collection<PublishItem> publish_items = new ArrayList<PublishItem>();
					for(SharedFile file : files_to_publish) {
						
						TagList tagList = new TagList();
						tagList.addTag(new StringTag(TAG_FILENAME, file.getSharingName()));
						tagList.addTag(new IntTag(TAG_FILESIZE, (int) file.length()));
						publish_items.add(new PublishItem(file.getFileHash(), tagList));
					}
					
					try {
						publishKeyword(keywordID, publish_items, keyword);
						publishing_keywords.add(keywordID);
					} catch (JKadException e) {
						e.printStackTrace();
					}
				}
				keyword_queue.keyword_queue.addAll(used_keywords);
			}
		};
		
		keyword_scanner = new Task() {
			@Override
			public void run() {
				if (!_jkad_manager.isConnected())
					return;
				SharingManager _sharing_manager = SharingManagerSingleton.getInstance();
				
				for(SharedFile shared_file : _sharing_manager.getSharedFiles()) {
					if (!keyword_queue.hasFile(shared_file.getFileHash())) {
						boolean allow = true;
						if (shared_file instanceof PartialFile)
							if (((PartialFile) shared_file).getAvailablePartCount() == 0)
								allow = false;
						if (allow)
							keyword_queue.addFile(shared_file);
					}

				}
			}
			
		};
		
		source_publisher = new Task() {
			List<Int128> publishing_source = new ArrayList<Int128>();
			Map<Int128, Long> published_source = new ConcurrentHashMap<Int128, Long>();
			@Override
			public void run() {
				if (!_jkad_manager.isConnected())
					return;
				for(Int128 id : published_source.keySet())
					if (System.currentTimeMillis() - published_source.get(id) > JKadConstants.TIME_5_HOURS)
						published_source.remove(id);
				
				int to_publish = MAX_CONCURRENT_PUBLISH_FILES;
				List<Int128> to_remove = new ArrayList<Int128>();
				
				for(Int128 keywordID : publishing_source) { 
					if (isPublishing(keywordID))
						to_publish--;
					else
						to_remove.add(keywordID);
					if (to_publish == 0)
						break;
				}
				publishing_source.removeAll(to_remove);
				if (to_publish == 0)
					return;
				SharingManager _sharing_manager = SharingManagerSingleton.getInstance();
				ConfigurationManager _config_manager = ConfigurationManagerSingleton.getInstance();
				
				for(SharedFile file : _sharing_manager.getSharedFiles()) {
					if (to_publish==0)
						break;
					
					if (file instanceof PartialFile) 
						if (((PartialFile)file).getAvailablePartCount()==0)
							continue;
					
					byte[] hash = file.getFileHash().getHash().clone();
					Convert.updateSearchID(hash);
					Int128 fileID = new Int128(hash);
					
					if (publishing_source.contains(fileID))
						continue;
					
					if (published_source.containsKey(fileID))
						continue;
					
					to_publish--;
					
					TagList tagList = new TagList();
					tagList.addTag(new StringTag(TAG_FILENAME, file.getSharingName()));
					tagList.addTag(new IntTag(TAG_SOURCETYPE, 1));
					tagList.addTag(new IntTag(TAG_FILESIZE, (int) file.length()));
					tagList.addTag(new IntTag(TAG_SOURCEIP,org.jmule.core.utils.Convert.byteToInt(_jkad_manager.getIPAddress().getAddress())));
					try {
						tagList.addTag(new IntTag(TAG_SOURCEPORT, _config_manager.getTCP()));
					} catch (ConfigurationManagerException e) {
						e.printStackTrace();
					}
										
					PublishItem publishItem = new PublishItem(file.getFileHash(), tagList);
					try {
						publishSource(fileID, publishItem);
						published_source.put(fileID, System.currentTimeMillis());
						publishing_source.add(fileID);
					} catch (JKadException e) {
						e.printStackTrace();
					}
					
				}
			}
		};
		
		note_publisher = new Task() {
			List<Int128> publishing_note = new ArrayList<Int128>();
			Map<Int128, Long> published_note = new ConcurrentHashMap<Int128, Long>();
			@Override
			public void run() {
				if (!_jkad_manager.isConnected())
					return;
				for(Int128 id : published_note.keySet())
					if (System.currentTimeMillis() - published_note.get(id) > JKadConstants.TIME_5_HOURS)
						published_note.remove(id);
				
				int to_publish = MAX_CONCURRENT_PUBLISH_NOTES;
				List<Int128> to_remove = new ArrayList<Int128>();
				
				for(Int128 keywordID : publishing_note) { 
					if (isPublishing(keywordID))
						to_publish--;
					else
						to_remove.add(keywordID);
					if (to_publish == 0)
						break;
				}
				publishing_note.removeAll(to_remove);
				if (to_publish == 0)
					return;
				SharingManager _sharing_manager = SharingManagerSingleton.getInstance();
				
				for(SharedFile file : _sharing_manager.getSharedFiles()) {
					if (to_publish==0)
						break;
					
					byte[] hash = file.getFileHash().getHash().clone();
					Convert.updateSearchID(hash);
					Int128 fileID = new Int128(hash);
					
					if (publishing_note.contains(fileID))
						continue;
					
					if (published_note.containsKey(fileID))
						continue;
					
					if (isPublishing(fileID))
						continue;
					
					if (!file.getTagList().hasTag(TAG_FILERATING))
						continue;
										
					TagList tagList = new TagList();
					tagList.addTag(new StringTag(TAG_FILENAME, file.getSharingName()));
					tagList.addTag(new IntTag(TAG_FILESIZE, (int) file.length()));
					
					tagList.addTag(new IntTag(TAG_FILERATING, file.getFileQuality().getAsInt()));
					if (file.getTagList().hasTag(JKadConstants.TAG_DESCRIPTION))
						tagList.addTag(file.getTagList().getTag(JKadConstants.TAG_DESCRIPTION));
					
					to_publish--;
					PublishItem publishItem = new PublishItem(file.getFileHash(), tagList);
					try {
						publishNote(fileID, publishItem);
						published_note.put(fileID, System.currentTimeMillis());
						publishing_note.add(fileID);
					} catch (JKadException e) {
						e.printStackTrace();
					}
				}
			}
		};
				
		Timer.getSingleton().addTask(PUBLISH_KEYWORD_CHECK_INTERVAL, keyword_publisher, true);
		Timer.getSingleton().addTask(PUBLISH_KEYWORD_SCAN_INTERVAL, keyword_scanner, true);
		
		
		Timer.getSingleton().addTask(PUBLISH_SOURCE_INTERVAL, source_publisher, true);
		Timer.getSingleton().addTask(PUBLISH_NOTE_INTERVAL, note_publisher, true);
	}
	
	public void stop() {
		isStarted = false;
		
		Timer.getSingleton().removeTask(keyword_publisher);
		Timer.getSingleton().removeTask(keyword_scanner);
		
		Timer.getSingleton().removeTask(source_publisher);
		Timer.getSingleton().removeTask(note_publisher);
		
		for(Int128 key : publish_tasks.keySet())
			publish_tasks.get(key).stop();
	}
	
	public void publishKeyword(Int128 keywordID, Collection<PublishItem> publishItems, String keyword) throws JKadException {
		PublishKeywordTask task = new PublishKeywordTask(keywordTaskListener, keywordID, publishItems, keyword);
		task.start();
		publish_tasks.put(keywordID, task);
		notifyListeners(task, TaskStatus.ADDED);
		notifyListeners(task, TaskStatus.STARTED);
	}
	

		
	public void publishSource(Int128 fileID, PublishItem publishItem) throws JKadException  {
		PublishSourceTask task = new PublishSourceTask(sourceTaskListener,fileID, publishItem);
		task.start();
		publish_tasks.put(fileID, task);
		notifyListeners(task, TaskStatus.ADDED);
		notifyListeners(task, TaskStatus.STARTED);
	}
	
	public void publishNote(Int128 fileID, PublishItem publishItem) throws JKadException {
		PublishNoteTask task = new PublishNoteTask(noteTaskListener,fileID, publishItem);
		task.start();
		publish_tasks.put(fileID, task);
		notifyListeners(task, TaskStatus.ADDED);
		notifyListeners(task, TaskStatus.STARTED);
	}
	
	public void stopPublish(Int128 fileID) {
		PublishTask task = publish_tasks.get(fileID);
		if (task == null) return ;
		task.stop();
		publish_tasks.remove(fileID);
		removeTask(fileID);
	}
	
	public void processPublishResponse(Int128 id, int load) {
		PublishTask task = publish_tasks.get(id);
		if (task == null)
			return ;
		task.addPublishedSources(1);
		
		if (task instanceof PublishKeywordTask)
			if (task.getPublishedSources() >= JKadConstants.MAX_PUBLISH_KEYWORDS) {
				removeTask(id);
			}
		
		if (task instanceof PublishSourceTask)
			if (task.getPublishedSources() >= JKadConstants.MAX_PUBLISH_SOURCES) {
				removeTask(id);
			}
		
		if (task instanceof PublishNoteTask)
			if (task.getPublishedSources() >= MAX_PUBLISH_NOTES) {
				removeTask(id);
			}
				
	}
	
	public void processNoteResponse(Int128 id, int load) {
		PublishTask task = publish_tasks.get(id);
		if (task == null) return;
		if (!(task instanceof PublishTask))
			return;
		task.addPublishedSources(1);
		if (task.getPublishedSources()>=MAX_PUBLISH_NOTES) {
			task.stop();
			removeTask(id);
		}
		
	}
	
	public boolean isPublishing(Int128 id) {
		return publish_tasks.containsKey(id);
	}

	
	void removeTask(Int128 id) {
		PublishTask task = publish_tasks.get(id);
		if (task !=null) {
			task.stop();
			notifyListeners(task, TaskStatus.STOPPED);
			publish_tasks.remove(id); 
			notifyListeners(task, TaskStatus.REMOVED);
		}
	}
	
	public Collection<PublishTask> getPublishTasks() {
		return publish_tasks.values();
	}
	
	public PublishTask getPublishTask(Int128 id) {
		return publish_tasks.get(id);
	}
	
	public void addListener(PublisherListener listener) {
		listener_list.add(listener);
	}
	
	public void removeListener(PublisherListener listener) {
		listener_list.remove(listener);
	}
	
	private enum TaskStatus {ADDED, STARTED, STOPPED, REMOVED}
	
	private void notifyListeners(PublishTask task, TaskStatus status) {
		for(PublisherListener listener : listener_list) {
			try {
				if (status == TaskStatus.ADDED) {
					listener.publishTaskAdded(task);
					continue;
				}
				if (status == TaskStatus.STARTED) {
					listener.publishTaskStarted(task);
					continue;
				}
				if (status == TaskStatus.STOPPED) {
					listener.publishTaskStopped(task);
					continue;
				}
				if (status == TaskStatus.REMOVED) {
					listener.publishTaskRemoved(task);
					continue;
				}
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
	interface PublishTaskListener{
		public void taskStarted(PublishTask task);
		public void taskTimeOut(PublishTask task);
		public void taskStopped(PublishTask task);
	}
	
	
	class PublishKeywordContainer {
		FileHash fileHash;
		long publishTime;
		
		public PublishKeywordContainer(FileHash fileHash) {
			this.fileHash = fileHash;
			this.publishTime = 0;
		}
	}
	
	class KeywordPublishQueue {
		Map<String,Deque<PublishKeywordContainer>> keyword_files = new ConcurrentHashMap<String, Deque<PublishKeywordContainer>>();
		Map<FileHash, String> hash_mapping = new ConcurrentHashMap<FileHash, String>();
		
		Queue<String> keyword_queue = new ConcurrentLinkedQueue<String>();
		
		public boolean hasFile(FileHash hash) {
			return hash_mapping.containsKey(hash);
		}
		
		public void addFile(SharedFile sharedFile) {
			List<String> keywords = Utils.getFileKeyword(sharedFile.getSharingName());
			for(String keyword : keywords) {
				if (!keyword_files.containsKey(keyword)) {
					keyword_files.put(keyword, new LinkedBlockingDeque<PublishKeywordContainer>());
					keyword_queue.offer(keyword);
				}
				keyword_files.get(keyword).addFirst( new PublishKeywordContainer(sharedFile.getFileHash()));
			}
			hash_mapping.put(sharedFile.getFileHash(), sharedFile.getSharingName());
		}
		
		public void removeFile(FileHash hash) {
			List<String> keywords = Utils.getFileKeyword(hash_mapping.get(hash));
			for(String keyword : keywords) {
				if (keyword_files.containsKey(keyword)) {
					Queue<PublishKeywordContainer> queue = keyword_files.get(keyword);
					PublishKeywordContainer to_remove = null;
					for(PublishKeywordContainer container : queue) 
						if (container.fileHash.equals(hash)) {
							to_remove = container;
							break;
						}
					if (to_remove != null)
						queue.remove(to_remove);
					if (queue.isEmpty())
						keyword_files.remove(keyword);
				}
			}
			hash_mapping.remove(hash);
		}
		
	}
	
}