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
package org.jmule.core.sharingmanager;

import static org.jmule.core.edonkey.ED2KConstants.MAX_OFFER_FILES;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.JMIterable;
import org.jmule.core.JMThread;
import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.downloadmanager.DownloadManagerException;
import org.jmule.core.downloadmanager.DownloadManagerSingleton;
import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.downloadmanager.InternalDownloadManager;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.metfile.KnownMet;
import org.jmule.core.edonkey.metfile.KnownMetEntity;
import org.jmule.core.edonkey.metfile.PartMet;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.servermanager.InternalServerManager;
import org.jmule.core.servermanager.Server;
import org.jmule.core.servermanager.ServerManagerSingleton;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.core.statistics.JMuleCoreStatsProvider;
import org.jmule.core.uploadmanager.InternalUploadManager;
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.core.uploadmanager.UploadManagerException;
import org.jmule.core.uploadmanager.UploadManagerSingleton;
import org.jmule.core.uploadmanager.UploadSession;
import org.jmule.core.utils.timer.JMTimer;
import org.jmule.core.utils.timer.JMTimerTask;

public class SharingManagerImpl extends JMuleAbstractManager implements InternalSharingManager {
	
	private InternalNetworkManager _network_manager;
	private InternalServerManager _server_manager;
	private InternalDownloadManager _download_manager;
	private InternalUploadManager _upload_manager;
	
	private Map<FileHash, SharedFile> sharedFiles;
	private LoadCompletedFiles load_completed_files;
	private LoadPartialFiles load_partial_files;
	private SharedFile current_hashing_file;

	private List<CompletedFileListener> completed_file_listeners = new LinkedList<CompletedFileListener>();

	private Map<FileHash, SharedFile> server_shared_files = new HashMap<FileHash, SharedFile>();
	private JMTimer sharing_manager_timer;
	private JMTimerTask rescan_dirs_task;
	private JMTimerTask server_sharing_task;
	private JMTimerTask store_metadata_task;
	
	SharingManagerImpl() {
		
	}

	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		_network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		_server_manager = (InternalServerManager) ServerManagerSingleton.getInstance();
		_download_manager = (InternalDownloadManager) DownloadManagerSingleton.getInstance();
		_upload_manager = (InternalUploadManager) UploadManagerSingleton.getInstance();
		
		sharing_manager_timer = new JMTimer();
		sharedFiles = new ConcurrentHashMap<FileHash, SharedFile>();

		Set<String> types = new HashSet<String>();
		types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_COUNT);
		types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_COUNT);
		types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_COUNT);
		types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_BYTES);
		types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_BYTES);
		types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_BYTES);

		JMuleCoreStats.registerProvider(types, new JMuleCoreStatsProvider() {
			public void updateStats(Set<String> types,
					Map<String, Object> values) {
				if (types.contains(JMuleCoreStats.ST_DISK_SHARED_FILES_COUNT))
					values.put(JMuleCoreStats.ST_DISK_SHARED_FILES_COUNT,
							sharedFiles.size());
				if (types
						.contains(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_COUNT))
					values.put(
							JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_COUNT,
							getPartialFiles().size());
				if (types
						.contains(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_COUNT))
					values.put(
							JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_COUNT,
							getCompletedFiles().size());
				if (types.contains(JMuleCoreStats.ST_DISK_SHARED_FILES_BYTES)) {
					long total_bytes = 0;
					for (PartialFile shared_file : getPartialFiles())
						total_bytes += shared_file.getDownloadedBytes();

					for (CompletedFile shared_file : getCompletedFiles())
						total_bytes += shared_file.length();

					values.put(JMuleCoreStats.ST_DISK_SHARED_FILES_BYTES,
							total_bytes);
				}
				if (types
						.contains(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_BYTES)) {
					long total_bytes = 0;
					for (PartialFile shared_file : getPartialFiles())
						total_bytes += shared_file.getDownloadedBytes();
					values.put(
							JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_BYTES,
							total_bytes);
				}

				if (types
						.contains(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_BYTES)) {
					long total_bytes = 0;
					for (CompletedFile shared_file : getCompletedFiles())
						total_bytes += shared_file.length();
					values.put(
							JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_BYTES,
							total_bytes);
				}
			}
		});

		JMuleCoreFactory.getSingleton().getConfigurationManager()
				.addConfigurationListener(new ConfigurationAdapter() {
					public void sharedDirectoriesChanged(List<File> sharedDirs) {
						loadCompletedFiles();
					}
				});
	}

	/**
	 * A set of actions that are applied on the given file This interface is
	 * meant to be used in conjunction with traverseDir method
	 * 
	 * @see {@link #traverseDir(File, boolean, WorkOnFiles) traverseDir}
	 * @author javajox
	 */
	private interface WorkOnFiles {
		/**
		 * A set of actions that are applied on the given file
		 * 
		 * @param file
		 */
		public void doWork(File file);

		/**
		 * Tells if we still need to traverse the directory This method is very
		 * important, if we need to interrupt the "traverse process" without
		 * side effects we MUST call this method
		 */
		public boolean stopTraverseDir();
	}

	/**
	 * Traverse a directory in a non-recursive mode
	 * 
	 * @param dir
	 *            the directory that we have to traverse
	 * @param with_part_met_extension
	 *            has a role of filter, if given, only files with part.met at
	 *            the end of file name are permitted
	 * @param work_on_files
	 *            the actions that are applied on the found file
	 */
	private void traverseDir(File dir, boolean with_part_met_extension,
			WorkOnFiles work_on_files) {
		Queue<File> list_of_dirs = new LinkedList<File>();
		File[] list_of_files;
		File current_dir;

		list_of_dirs.offer(dir);
		while (list_of_dirs.size() != 0) {
			if (work_on_files.stopTraverseDir())
				return;
			current_dir = list_of_dirs.poll();
			if (with_part_met_extension)
				list_of_files = current_dir.listFiles(new FileFilter() {
					public boolean accept(File file) {
						if (file.isDirectory())
							return true;
						if (file.getName().endsWith(PART_MET_EXTENSION))
							return true;
						return false;
					}

				});
			else
				list_of_files = current_dir.listFiles();
			for (File file : list_of_files) {
				if (work_on_files.stopTraverseDir())
					return;
				if (file.isDirectory())
					list_of_dirs.offer(file);
				else {
					work_on_files.doWork(file);
				}
			}
		}
	}

	public void start() {
		try {
			super.start();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}

		rescan_dirs_task = new JMTimerTask() {
			public void run() {
				loadCompletedFiles();
			}
		};
		store_metadata_task = new JMTimerTask() {
			public void run() {
				writeMetadata();
			}
		};
		sharing_manager_timer.addTask(rescan_dirs_task,
				ConfigurationManager.DIR_RESCAN_INTERVAL, true);
		
		sharing_manager_timer.addTask(store_metadata_task,
				ConfigurationManager.WRITE_METADATA_INTERVAL, true);
	}
	
	protected boolean iAmStoppable() {
		return false;
	}
	
	public void startSharingFilesToServer() {
		server_sharing_task = new JMTimerTask() {
			public void run() {
				List<SharedFile> files_to_share = new LinkedList<SharedFile>();
				for (FileHash fileHash : sharedFiles.keySet()) {
					if (files_to_share.size() == MAX_OFFER_FILES)
						break;
					if (server_shared_files.containsKey(fileHash))
						break;
					SharedFile shared_file = sharedFiles.get(fileHash);
					if (shared_file instanceof PartialFile) {
						PartialFile partial_file = (PartialFile) shared_file;
						if (partial_file.getAvailablePartCount()==0)
							continue;
						try {
							DownloadSession session = _download_manager.getDownload(partial_file.getFileHash());
							if (!session.isStarted()) 
								continue;
						} catch (DownloadManagerException e) {
							e.printStackTrace();
							continue;
						}
					}
					files_to_share.add(shared_file);
					server_shared_files.put(fileHash, shared_file);
				}
				if (files_to_share.size() == 0) return ;
								
				Server server = _server_manager.getConnectedServer();
				_network_manager.offerFilesToServer(server.getClientID(),files_to_share);

			}
		};
		sharing_manager_timer.addTask(server_sharing_task, ConfigurationManager.SHARED_FILES_PUBLISH_INTERVAL, true);
		new JMThread() {
			public void run() {
				server_sharing_task.run();
			}			
		}.start();
	}

	public void stopSharingFilesToServer() {
		if (server_sharing_task != null)
			server_sharing_task.stopTask();
		server_shared_files.clear();
	}

	public boolean isLoadingCompletedFileProcessRunning() {

		if (load_completed_files == null)
			return false;

		return load_completed_files.isDone();

	}

	public boolean isLoadingPartialFileProcessRunning() {

		if (load_partial_files == null)
			return false;

		return load_partial_files.isDone();
	}

	private class LoadPartialFiles extends JMFileTask {
		public void run() {
			isDone = false;
			File shared_dir = new File(ConfigurationManager.TEMP_DIR);
			traverseDir(shared_dir, true, new WorkOnFiles() {
				public void doWork(File file) {
					try {
						PartMet part_met = new PartMet(file);
						part_met.load();
						PartialFile partial_shared_file = new PartialFile(part_met);
						//sharedFiles.put(partial_shared_file.getFileHash(),partial_shared_file);
						//System.out.println(partial_shared_file + " : " + partial_shared_file.getAvailablePartCount());
						addPartialFile(partial_shared_file);
						JMuleCoreFactory.getSingleton().getDownloadManager().addDownload(partial_shared_file);
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}

				public boolean stopTraverseDir() {

					return stop;
				}

			});
			isDone = true;
		}

		public double getPercent() {

			return 0;
		}
	}

	private class LoadCompletedFiles extends JMFileTask {

		private FileHashing file_hashing;
		private List<CompletedFile> files_needed_to_hash;

		Map<String, KnownMetEntity> known_file_list;
		Set<FileHash> files_hash_set = new HashSet<FileHash>();

		public void JMStop() {

			super.JMStop();

			if ((file_hashing != null) && (file_hashing.isAlive()))
				file_hashing.JMStop();

		}

		public void run() {
			isDone = false;
			String knownFilePath = ConfigurationManager.KNOWN_MET;

			// new files from user's shared dirs that need to be hashed
			files_needed_to_hash = new CopyOnWriteArrayList<CompletedFile>();

			files_hash_set.clear();
			for (FileHash fileHash : sharedFiles.keySet())
				if (!DownloadManagerSingleton.getInstance().hasDownload(
						fileHash))
					files_hash_set.add(fileHash);

			// load shared completed files
			try {
				KnownMet known_met = new KnownMet(knownFilePath);
				known_file_list = known_met.load();
			} catch (Throwable e) {
				e.printStackTrace();
				known_file_list = new Hashtable<String, KnownMetEntity>();
			}

			File incoming_dir = new File(ConfigurationManager.INCOMING_DIR);
			List<File> shared_dirs = null;
			try {
				shared_dirs = ConfigurationManagerSingleton.getInstance()
						.getSharedFolders();
			} catch (ConfigurationManagerException e1) {

				e1.printStackTrace();
			}
			if (shared_dirs == null)
				shared_dirs = new LinkedList<File>();
			shared_dirs.add(incoming_dir);

			for (File dir : shared_dirs) {
				if (stop)
					return;
				traverseDir(dir, false, new WorkOnFiles() {
					String file_name;
					long file_size;
					KnownMetEntity known_met_entity = null;

					public void doWork(File file) {
						file_name = file.getName();
						file_size = file.length();
						known_met_entity = known_file_list.get(file_name
								+ file_size);
						if (known_met_entity == null) {
							files_needed_to_hash.add(new CompletedFile(file));
						} else {
							FileHash hash = known_met_entity.getFileHash();
							if (files_hash_set.contains(hash))
								files_hash_set.remove(hash);
							if (sharedFiles.get(hash) != null)
								return; // file already in list
							CompletedFile shared_completed_file = new CompletedFile(
									file);
							try {
								shared_completed_file
										.setHashSet(known_met_entity
												.getPartHashSet());
							} catch (SharedFileException e) {
								e.printStackTrace();
							}
							shared_completed_file.setTagList(known_met_entity
									.getTagList());
							sharedFiles.put(
									shared_completed_file.getFileHash(),
									shared_completed_file);
						}
						known_met_entity = null;
					}

					public boolean stopTraverseDir() {

						return stop;
					}
				});

			}
			for (FileHash file_hash : files_hash_set) {
				sharedFiles.remove(file_hash);
			}

			// hash new files from the file system
			boolean need_to_write_metadata = files_needed_to_hash.size() != 0;
			for (CompletedFile shared_completed_file : files_needed_to_hash) {
				current_hashing_file = null;
				if (stop)
					return;
				try {
					current_hashing_file = shared_completed_file;
					FileChannel file_channel = new FileInputStream(
							shared_completed_file.getFile()).getChannel();
					file_hashing = new FileHashing(file_channel);
					file_hashing.start();
					file_hashing.join();
					file_channel.close();
					files_needed_to_hash.remove(shared_completed_file);
					if (!file_hashing.isDone())
						continue;
					if (sharedFiles.containsKey(file_hashing.getFileHashSet()
							.getFileHash()))
						continue;
					shared_completed_file.setHashSet(file_hashing
							.getFileHashSet());
					sharedFiles.put(shared_completed_file.getFileHash(),
							shared_completed_file);

					notifyCompletedFileAdded(shared_completed_file);
				} catch (Throwable t) {
					current_hashing_file = null;
					t.printStackTrace();
				}
			}
			current_hashing_file = null;
			// write our files in known.met
			if (need_to_write_metadata) {
				writeMetadata();
			}
			isDone = true;
		}

		public double getPercent() {
			if (file_hashing == null)
				return 100;
			if (!file_hashing.isAlive())
				return 100;
			return file_hashing.getPercent();
		}

		public SharedFile getCurrentHashingFile() {
			return current_hashing_file;
		}

		public List<CompletedFile> getUnhashedFiles() {
			return files_needed_to_hash;
		}

	}

	public List<CompletedFile> getUnhashedFiles() {
		if ((load_completed_files == null) || (!load_completed_files.isAlive()))
			return null;
		return load_completed_files.getUnhashedFiles();
	}

	public double getCurrentHashingFilePercent() {

		if ((load_completed_files == null) || (!load_completed_files.isAlive()))
			return 0;

		return load_completed_files.getPercent();
	}

	public SharedFile getCurrentHashingFile() {

		if ((load_completed_files == null) || (!load_completed_files.isAlive()))
			return null;

		return load_completed_files.getCurrentHashingFile();
	}

	public void stopLoadingCompletedFiles() {
		if ((load_completed_files == null) || (!load_completed_files.isAlive()))
			return;
		load_completed_files.JMStop();
	}

	public void stopLoadingPartialFiles() {

		if ((load_partial_files == null) || (!load_partial_files.isAlive()))
			return;

		load_partial_files.JMStop();

	}

	public void loadCompletedFiles() {
		if (load_completed_files != null)
			if (load_completed_files.isAlive())
				return;
		load_completed_files = new LoadCompletedFiles();
		load_completed_files.start();
	}

	public void loadPartialFiles() {

		load_partial_files = new LoadPartialFiles();

		load_partial_files.start();
	}

	public CompletedFile getCompletedFile(FileHash fileHash) {
		SharedFile sharedFile = sharedFiles.get(fileHash);
		if (sharedFile == null)
			return null;
		if (!sharedFile.exists()) {
			sharedFiles.remove(fileHash);
			return null;
		}
		if (sharedFile instanceof CompletedFile)
			return (CompletedFile) sharedFile;
		else
			return null;
	}

	public PartialFile getPartialFle(FileHash fileHash) {
		SharedFile sharedFile = sharedFiles.get(fileHash);
		if (sharedFile == null)
			return null;
		if (!sharedFile.exists()) {
			sharedFiles.remove(fileHash);
			return null;
		}
		if (sharedFile instanceof PartialFile)
			return (PartialFile) sharedFile;
		else
			return null;
	}

	public void makeCompletedFile(FileHash fileHash)
			throws SharingManagerException {
		File incoming_dir = new File(ConfigurationManager.INCOMING_DIR);

		PartialFile shared_partial_file = getPartialFle(fileHash);
		if (shared_partial_file == null)
			throw new SharingManagerException("The file " + fileHash
					+ "doesn't exists");
		shared_partial_file.closeFile();
		shared_partial_file.deletePartialFile();
		File completed_file = new File(incoming_dir.getAbsoluteFile()
				+ File.separator + shared_partial_file.getSharingName());
		UploadManager upload_manager = JMuleCoreFactory.getSingleton()
				.getUploadManager();
		try {

			if (upload_manager.hasUpload(fileHash)) { // JMule is now uploading
														// file, need to
														// synchronize moving
				UploadSession upload_sessison = upload_manager.getUpload(fileHash);
				synchronized (upload_sessison.getSharedFile()) {
					sharedFiles.remove(fileHash);
					// FileUtils.moveFile(shared_partial_file.getFile(),
					// completed_file);
					shared_partial_file.getFile().renameTo(completed_file);
					CompletedFile shared_completed_file = new CompletedFile(
							completed_file);
					shared_completed_file.setHashSet(shared_partial_file
							.getHashSet());
					sharedFiles.put(fileHash, shared_completed_file);
				}
			} else {
				sharedFiles.remove(fileHash);
				// FileUtils.moveFile(shared_partial_file.getFile(),
				// completed_file);
				shared_partial_file.getFile().renameTo(completed_file);
				CompletedFile shared_completed_file = new CompletedFile(
						completed_file);
				shared_completed_file.setHashSet(shared_partial_file
						.getHashSet());
				sharedFiles.put(fileHash, shared_completed_file);
			}
			writeMetadata();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new SharingManagerException(e);
		}
	}

	public void addPartialFile(PartialFile partialFile) {
		sharedFiles.put(partialFile.getFileHash(), partialFile);
	}

	public int getFileCount() {
		return sharedFiles.size();
	}

	public boolean hasFile(FileHash fileHash) {
		return sharedFiles.containsKey(fileHash);
	}

	public boolean existsFile(FileHash fileHash) {
		if (!hasFile(fileHash))
			return false;
		SharedFile shared_file = sharedFiles.get(fileHash);
		if (!shared_file.exists())
			return false;
		return true;
	}

	public SharedFile getSharedFile(FileHash fileHash) {
		if (!hasFile(fileHash))
			return null;
		SharedFile sharedFile = sharedFiles.get(fileHash);

		return sharedFile;
	}

	public JMIterable<SharedFile> getSharedFiles() {
		return new JMIterable<SharedFile>(sharedFiles.values().iterator());
	}

	public List<PartialFile> getPartialFiles() {
		List<PartialFile> file_list = new LinkedList<PartialFile>();
		for (SharedFile file : sharedFiles.values()) {
			if (file instanceof PartialFile)
				file_list.add((PartialFile) file);
		}

		return file_list;
	}

	public List<CompletedFile> getCompletedFiles() {
		List<CompletedFile> file_list = new LinkedList<CompletedFile>();

		for (SharedFile file : sharedFiles.values())
			if (file instanceof CompletedFile)
				file_list.add((CompletedFile) file);

		return file_list;
	}

	public void writeMetadata() {
		try {
			KnownMet known_met = new KnownMet(ConfigurationManager.KNOWN_MET);
			known_met.store(sharedFiles.values());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void shutdown() {

		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}

		sharing_manager_timer.cancelAllTasks();

		stopLoadingCompletedFiles();

		stopLoadingPartialFiles();
		writeMetadata();

	}
	
	public Collection<Peer> getFileSources(Peer sender, FileHash fileHash) {
		Collection<Peer> result = new ArrayList<Peer>();
		if (!hasFile(fileHash)) return result;
		List<Peer> peer_list = new LinkedList<Peer>();
		if (_download_manager.hasDownload(fileHash)) {
			DownloadSession session;
			try {
				session = _download_manager.getDownload(fileHash);
				if (!session.isStarted()) return result; //also don't get uploading peers from upload session (1)
				PartialFile partialFile = getPartialFle(fileHash);
				if (partialFile.getAvailablePartCount()==0) return result;  // (1)
				List<Peer> download_peer_list = session.getPeers();
				for(Peer source_peer : download_peer_list) {
					if (peer_list.size() > ConfigurationManager.MAX_PEX_RESPONSE ) break;
					if (source_peer.equals(sender)) continue;
					if (!source_peer.isHighID()) continue;
					JMuleBitSet availability = session.getPartAvailability(source_peer);
					if (availability == null) continue;
					if (availability.hasAtLeastOne(true))
						peer_list.add(source_peer);
				}
			} catch (DownloadManagerException e) {
				e.printStackTrace();
			}
		}
		if (!(peer_list.size() > ConfigurationManager.MAX_PEX_RESPONSE ))
			if (_upload_manager.hasUpload(fileHash)) {
				try {
					UploadSession session = _upload_manager.getUpload(fileHash);
					List<Peer> upload_peer_list = session.getPeers();
					for(Peer upeer : upload_peer_list) {
						if (peer_list.size() > ConfigurationManager.MAX_PEX_RESPONSE ) break;
						if (!upeer.isHighID()) continue;
						peer_list.add(upeer);
					}
				} catch (UploadManagerException e) {
					e.printStackTrace();			
				}
			}
		return result;
	}
	
	public void receivedSourcesRequestFromPeer(Peer peer, FileHash fileHash) {
		Collection<Peer> peer_list = getFileSources(peer, fileHash);
		_network_manager.sendSourcesResponse(peer.getIP(), peer.getPort(), fileHash, peer_list);
	}

	public void addCompletedFileListener(CompletedFileListener listener) {

		completed_file_listeners.add(listener);

	}

	public void removeCompletedFileListener(CompletedFileListener listener) {

		completed_file_listeners.remove(listener);
	}

	private void notifyCompletedFileAdded(CompletedFile file) {

		for (CompletedFileListener listener : completed_file_listeners) {

			listener.fileAdded(file);

		}

	}

	public SharedFile getSharedFile(File file) {
		for (SharedFile shared_file : sharedFiles.values()) {
			if (shared_file.getAbsolutePath().equals(file.getAbsolutePath()))
				return shared_file;
		}

		return null;
	}

	public void removeSharedFile(FileHash fileHash) {
		SharedFile shared_file = sharedFiles.get(fileHash);
		if (shared_file == null)
			return;
		sharedFiles.remove(fileHash);
		shared_file.closeFile();
		if (shared_file instanceof CompletedFile) 
			return;
		shared_file.delete();
	}

}
