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
package org.jmule.core.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.jmule.core.EventDescriptor;
import org.jmule.core.InternalJMuleCore;
import org.jmule.core.JMRawData;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreComponent;
import org.jmule.core.JMuleCoreEvent;
import org.jmule.core.JMuleCoreEventListener;
import org.jmule.core.JMuleCoreException;
import org.jmule.core.JMuleCoreLifecycleListener;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.downloadmanager.DownloadManagerSingleton;
import org.jmule.core.ipfilter.IPFilter;
import org.jmule.core.ipfilter.IPFilterSingleton;
import org.jmule.core.jkad.JKadManager;
import org.jmule.core.jkad.JKadManagerSingleton;
import org.jmule.core.networkmanager.NetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.peermanager.PeerManager;
import org.jmule.core.peermanager.PeerManagerSingleton;
import org.jmule.core.searchmanager.SearchManager;
import org.jmule.core.searchmanager.SearchManagerSingleton;
import org.jmule.core.servermanager.ServerManager;
import org.jmule.core.servermanager.ServerManagerSingleton;
import org.jmule.core.sharingmanager.SharingManager;
import org.jmule.core.sharingmanager.SharingManagerSingleton;
import org.jmule.core.speedmanager.SpeedManager;
import org.jmule.core.speedmanager.SpeedManagerSingleton;
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.core.uploadmanager.UploadManagerSingleton;

/**
 * Created on 2008-Apr-16
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.29 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2011/04/04 12:29:12 $$
 */
public class JMuleCoreImpl implements InternalJMuleCore {
	
	private static JMuleCoreImpl instance = null;
	
	private DebugThread debugThread ;
		
	private List<JMuleCoreLifecycleListener> lifecycle_listeners = new ArrayList<JMuleCoreLifecycleListener>();
	private List<JMuleCoreEventListener> event_listeners = new ArrayList<JMuleCoreEventListener>();
	
	// the first run flag is true when certain conditions have been met
	private boolean first_run = false; 
	
	private JMRawData core_params;
	
	private boolean is_starting = false;
	
	private boolean is_stopping = false; 
	
	private JMuleCoreImpl() {
		// when the JMule core is created we must exactly know if this is the first start up
	}
	
	private JMuleCoreImpl(JMRawData coreParams) {
		
		this.core_params = coreParams;
	}
	
	public static JMuleCore create() throws JMuleCoreException {
		
		if (instance != null)
			
			throw new JMuleCoreException("JMule core already instantiated");
		
			instance = new JMuleCoreImpl();
			
		return instance;
	}
	
	public static JMuleCore create(JMRawData coreParams) throws JMuleCoreException {
		
		if(instance != null)
			
			throw new JMuleCoreException("JMule core already instantiated");
		
		    instance = new JMuleCoreImpl(coreParams);
		    
		return instance;
	}
	
	public static JMuleCore getSingleton() throws JMuleCoreException {
		
		if (instance == null)
			
			throw new JMuleCoreException("JMule core is not instantiated");
		
		return instance;
	}
	
	
	public void start() throws JMuleCoreException {
		
		System.out.println("Core starting process initiated");
		
		long start_time = System.currentTimeMillis();
		
		is_starting = true;
		
		File[] main_dirs = new File[4];
		
		File incoming_dir = new File( ConfigurationManager.INCOMING_DIR );
		
		main_dirs[0] = incoming_dir; 
		
		File temp_dir = new File( ConfigurationManager.TEMP_DIR );
		
		main_dirs[1] = temp_dir;
		
		File logs_dir = new File( ConfigurationManager.LOGS_DIR );
		
		main_dirs[2] = logs_dir;
		
		File settings_dir = new File( ConfigurationManager.SETTINGS_DIR );
		
		main_dirs[3] = settings_dir;
		
		for(File file : main_dirs) {

		    if( !file.exists()  ) {
				
		      try {
		    	
		    	  file.mkdir();
			   
		      }catch(Throwable cause) {
		    	  
		    	  throw new JMuleCoreException( cause );
		    	  
		      }
			      
		    }
			
		    if( !file.isDirectory() ) throw new JMuleCoreException("The file " + incoming_dir + " is not a directory");
		
		}
		
		File config_file = new File(ConfigurationManager.CONFIG_FILE);
		
		if( !config_file.exists() ) {
			
			first_run = true;
			
			try {
			
				config_file.createNewFile();
			
			} catch( Throwable cause ) {
				
				throw new JMuleCoreException( cause );
				
			}
			
		}
				
		ConfigurationManager configuration_manager = ConfigurationManagerSingleton.getInstance();
		
		configuration_manager.initialize();
		
		configuration_manager.start();
		
		Logger log = Logger.getLogger("org.jmule");
		
		/**Setup logger*/
		
		log.setLevel(Level.ALL);//Log all events
		
		try {
			FileHandler fileHandler = new FileHandler(ConfigurationManager.LOGS_DIR+File.separator+"JMule%u.log",(int)ConfigurationManager.LOG_FILE_SIZE,ConfigurationManager.LOG_FILES_NUMBER);
			
			fileHandler.setFormatter(new SimpleFormatter());
			
			log.addHandler(fileHandler);
			
		} catch (Throwable e) {
			
			e.printStackTrace();
		}
		
		SpeedManagerSingleton.getInstance().initialize();
		SpeedManagerSingleton.getInstance().start();
		// notifies that the speed manager has been started
		notifyComponentStarted(SpeedManagerSingleton.getInstance());
		
		NetworkManagerSingleton.getInstance().initialize();
		NetworkManagerSingleton.getInstance().start();
		notifyComponentStarted(NetworkManagerSingleton.getInstance());
		
		// notifies that the config manager has been started
		notifyComponentStarted(configuration_manager);
		
		IPFilter ip_filter = IPFilterSingleton.getInstance();
		ip_filter.initialize();
		ip_filter.start();
		
		notifyComponentStarted( ip_filter );
		
		JKadManagerSingleton.getInstance().initialize();
		try {
			if (configuration_manager.isJKadAutoconnectEnabled()) {
				JKadManagerSingleton.getInstance().start();
				notifyComponentStarted(JKadManagerSingleton.getInstance());
			}
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
		}
				
		PeerManagerSingleton.getInstance().initialize();
		PeerManagerSingleton.getInstance().start();
		// notifies that the peer manager has been started
		notifyComponentStarted(PeerManagerSingleton.getInstance());

		UploadManagerSingleton.getInstance().initialize();
		UploadManagerSingleton.getInstance().start();
		// notifies that the upload manager has been started
		notifyComponentStarted(UploadManagerSingleton.getInstance());
		
		DownloadManagerSingleton.getInstance().initialize();
		DownloadManagerSingleton.getInstance().start();
		// notifies that the download manager has been started
		notifyComponentStarted(DownloadManagerSingleton.getInstance());
		
		SharingManager sharingManager = SharingManagerSingleton.getInstance();
		sharingManager.initialize();
		sharingManager.start();
		
		sharingManager.loadCompletedFiles();
		sharingManager.loadPartialFiles();
		// notifies that the sharing manager has been started
		notifyComponentStarted(sharingManager);
		
		
		ServerManager servers_manager = ServerManagerSingleton.getInstance();
		servers_manager.initialize();
		try {
			servers_manager.loadServerList();
		} catch (Throwable t) {
			t.printStackTrace();
		} 
		
		servers_manager.start();
		// notifies that the download manager has been started
		notifyComponentStarted(servers_manager);
		
		
		SearchManager search_manager = SearchManagerSingleton.getInstance();
		search_manager.initialize();
		search_manager.start();
		notifyComponentStarted(search_manager);
		
		/** Enable Debug thread!**/	
		 debugThread = new DebugThread();
		
/*		Runtime.getRuntime().addShutdownHook( new JMThread("Shutdown Hook") {

			public void xrun() {
				try {
				   JMuleCoreImpl.this.stop();
		    	} catch(Throwable t) {
		    		t.printStackTrace();
		    	}
			}
		 });*/
		
		is_starting = false;
		
		System.out.println("Total start up time = " + ( System.currentTimeMillis() - start_time ) );
	}

	public void stop() throws JMuleCoreException {
		
		System.out.println("Core stopping process initiated");
		
		long stop_time = System.currentTimeMillis();
		
		is_stopping = true;
		
		logEvent("Stop jMule");
		
		SearchManagerSingleton.getInstance().shutdown();
		// notifies that the search manager has been stopped
		notifyComponentStopped(SearchManagerSingleton.getInstance());
		
		ServerManagerSingleton.getInstance().shutdown();
		// notifies that the server manager has been stopped
		notifyComponentStopped(ServerManagerSingleton.getInstance());
		
		SharingManagerSingleton.getInstance().shutdown();
		// notifies that the sharing manager has been stopped
		notifyComponentStopped(SharingManagerSingleton.getInstance());
		
		DownloadManagerSingleton.getInstance().shutdown();
		// notifies that the download manager has been stopped
		notifyComponentStopped(DownloadManagerSingleton.getInstance());
		
		UploadManagerSingleton.getInstance().shutdown();
		// notifies that the upload manager has been stopped
		notifyComponentStopped(UploadManagerSingleton.getInstance());
		
		PeerManagerSingleton.getInstance().shutdown();
		// notifies that the peer manager has been stopped
		notifyComponentStopped(PeerManagerSingleton.getInstance());
		
		SpeedManagerSingleton.getInstance().shutdown();
		// notifies that the speed manager has been stopped
		notifyComponentStopped(SpeedManagerSingleton.getInstance());
		
		JKadManager _jkad = getJKadManager();
		if (!_jkad.isDisconnected()) {
			_jkad.disconnect();
			_jkad.shutdown();
			// notifies that the kad manager has been stopped
			notifyComponentStopped(_jkad);
		}
		
		IPFilterSingleton.getInstance().shutdown();
		// notifies that the ip filter manager has been stopped
		notifyComponentStopped(IPFilterSingleton.getInstance());
		
		ConfigurationManagerSingleton.getInstance().shutdown();
		notifyComponentStopped(ConfigurationManagerSingleton.getInstance());
		
		NetworkManagerSingleton.getInstance().shutdown();
		// notifies that the network manager has been stopped
		notifyComponentStopped(NetworkManagerSingleton.getInstance());
		
		if (debugThread != null)
			debugThread.JMStop();
		
		is_stopping = false;
		
		System.out.println("Total shutdown time = " + ( System.currentTimeMillis() - stop_time ) );
		
		//System.exit( 0 );
	}

	public boolean isStarted() {
		
		return instance != null;
		
	}
	
	public boolean isSopping() {
		return is_stopping;
	}

	
	public boolean isStarting() {
		return is_starting;
	}
	
	public boolean isFirstRun() {
		
		return first_run;
	}
	
	public void logEvent(String event) {
		//Check aspect
	}
	
	public JKadManager getJKadManager() {
		return JKadManagerSingleton.getInstance();
	}
	
	public NetworkManager getNetworkManager() {
		return NetworkManagerSingleton.getInstance();
	}
	
	public DownloadManager getDownloadManager() {
		
		return DownloadManagerSingleton.getInstance();
		
	}
	
	public UploadManager getUploadManager() {
		
		return UploadManagerSingleton.getInstance();
		
	}
	
	public ServerManager getServerManager() {
		
		return ServerManagerSingleton.getInstance();
		
	}
	
	public PeerManager getPeerManager() {
		
		return PeerManagerSingleton.getInstance();
	}
	
	public SharingManager getSharingManager() {
		
		return SharingManagerSingleton.getInstance();
		
	}
	
	public SpeedManager getSpeedManager() {
		
		return SpeedManagerSingleton.getInstance();
		
	}
	
	public ConfigurationManager getConfigurationManager() {
		
		return ConfigurationManagerSingleton.getInstance();
		
	}
	
	public SearchManager getSearchManager() {
		
		return SearchManagerSingleton.getInstance();
		
	}

	public IPFilter getIPFilterManager() {
		
		return IPFilterSingleton.getInstance();
	}
	
	private class DebugThread extends Thread {
		
		private boolean stop = false;
		
		public DebugThread() {
			
			super("JMule Debug thread");
			
			start();
			
		}
		
		public void run() {
		
			while(!stop){
			
				try {
					logEvent("Debug thread");
				}catch(Throwable cause) {
					cause.printStackTrace();
				}
			
				try {
					Thread.sleep(1000);			
				} catch (InterruptedException e) {}
			}
		}
		
		public void JMStop() {
			stop = true;
			interrupt();
		}
		
	}
	
	private void notifyComponentStarted(JMuleCoreComponent manager) {
		for(JMuleCoreLifecycleListener listener : lifecycle_listeners) {			
		   try {	
			 listener.componentStarted( manager );
		   }catch(Throwable cause) {
			   cause.printStackTrace();
		   }
		}
	}
	
	private void notifyComponentStopped(JMuleCoreComponent manager) {
		for(JMuleCoreLifecycleListener listener : lifecycle_listeners) {
		  try {	
			listener.componentStopped( manager );
		  }catch(Throwable cause) {
			  cause.printStackTrace();
		  }
		}
	}

	public void notifyListenersEventOccured(JMuleCoreEvent event, EventDescriptor eventDescriptor) {
		for(JMuleCoreEventListener listener : event_listeners) {
			try {
				listener.eventOccured(event, eventDescriptor);
			}catch(Throwable cause) {
				cause.printStackTrace();
			}
		}
	}
	
	public void addLifecycleListener(
			JMuleCoreLifecycleListener lifeCycleListener) {
		
		lifecycle_listeners.add( lifeCycleListener );
	}

	public void removeLifecycleListener(
			JMuleCoreLifecycleListener lifeCycleListener) {
		
		lifecycle_listeners.remove( lifeCycleListener );
	}

	public void addEventListener(
			JMuleCoreEventListener eventListener) {
		
		event_listeners.add( eventListener );
	}
	
	public void removeEventListener(
			JMuleCoreEventListener eventListener) {
		
		event_listeners.remove( eventListener );
	}
	
}
