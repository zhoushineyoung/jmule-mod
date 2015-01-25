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
package org.jmule.core.configmanager;

import java.io.File;
import java.util.List;

import org.jmule.core.JMuleManager;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.jkad.ClientID;

/**
 * Created on 07-17-2008
 * @author javajox
 * @version $$Revision: 1.45 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/09/04 16:01:25 $$
 */
public interface ConfigurationManager extends JMuleManager {

	// directories
	public static final String       INCOMING_DIR    				=     "incoming";
	public static final String       TEMP_DIR        				=     "temp";
	public static final String       LOGS_DIR        				=     "logs";
	public static final String		 SYSTEM_PROPERTIES_DUMP			=     LOGS_DIR + File.separator + "dump.properties";
	public static final String       SETTINGS_DIR    				=     "settings";
	public static final String       CONFIG_FILE     				=     SETTINGS_DIR + File.separator + "jmule.properties";
	public static final String       KNOWN_MET         				=     SETTINGS_DIR + File.separator + "known.met";
	public static final String       SERVER_MET         			=     SETTINGS_DIR + File.separator + "server.met";
	
	public static final String       USER_HASH_FILE                 =     SETTINGS_DIR + File.separator + "preferences.dat";
	public static final String       KAD_ID_FILE	                =     SETTINGS_DIR + File.separator + "preferenceskad.dat";
	public static final String       CRYPTKEY_FILE	                =     SETTINGS_DIR + File.separator + "cryptkey.dat";
	public static final String       CLIENTS_FILE	                =     SETTINGS_DIR + File.separator + "clients.met";
	
	public static final String       NICK_NAME       				=     "http://jmule.net";
	
	public static final int          TCP_PORT        				=     4662;
	public static final int          UDP_PORT        				=     4662;
	public static final boolean      UDP_ENABLED                    =     true;
	public static final int          LOG_FILES_NUMBER   			=     10;
	public static final int          LOG_FILE_SIZE      			=     20 * 1024 * 1024;
	
	public static final int          SERVER_SOURCES_QUERY_INTERVAL 	=     1000 * 60 * 40; //1000 * 60 * 32;
	public static final int          PEX_SOURCES_QUERY_INTERVAL 	=     1000 * 60 * 1;
	public static final int          GLOBAL_SOURCES_QUERY_INTERVAL 	=     1000 * 60 * 30; // 30
	
	public static final int          PEER_ACTIVITY_CHECH_INTERVAL   =     1000 * 30;
	public static final int          PEER_INACTIVITY_REMOVE_TIME    =     20 * 1000;
	public static final int 		 PEERS_ACTIVITY_CHECK_INTERVAL	=     1000 * 10;
	public static final int          SPEED_CHECK_INTERVAL           =     1000;
	
	// upload queue
	public static final int          UPLOAD_QUEUE_SIZE              =     200;
	public static final int          UPLOAD_QUEUE_SLOTS             =     10;
	public static final long 		 UPLOADQUEUE_REMOVE_TIMEOUT		= 	  1000 * 60 * 60;
	public static final long 		 UPLOAD_SLOT_LOSE_TIMEOUT		= 	  1000 * 60 + 1000 * 30;
	public static final long 		 UPLOAD_QUEUE_CHECK_INTERVAL	= 	  1000 * 3;
	public static final long 		 UPLOAD_QUEUE_TRANSFER_CHECK_INTERVAL	= 	  1000;
	public static final long		 UPLOAD_QUEUE_PAYLOAD_TIME		=	  1000 * 60 * 5;
	public static final long 		 UPLOAD_QUEUE_PAYLOAD_CHECK_INTERVAL	= 	  1000;
	public static final long 		 UPLOAD_QUEUE_PAYLOAD_LOOSED_CHECK_INTERVAL	= 	  1000 * 60 * 15;
	
	public static final int          SERVER_UDP_QUERY_INTERVAL      =     1000 * 60;
	public static final int          SERVER_LIST_STORE_INTERVAL     =     1000 * 60;
	public static final int          SERVER_DOWN_TIMEOUT	        =     1000 * 60 * 5;
	public static final int 		 SERVER_CONNECTING_TIMEOUT		=	  1000 * 40;
	public static final int			 SERVER_RECONNECT_COUNT			=	  4;
	public static final int			 SERVER_RECONNECT_TIMEOUT		= 	  5000;
	public static final int			 SERVER_MAX_FAIL_COUNT			= 	  10;
	
	public static final int 		 MAX_PACKET_SIZE				= 	  1024*500;
	
	public static final int 		 PEER_CONNECTING_TIMEOUT		= 	  1000 * 40;
	public static final int			 ERRORS_TO_DISCONNECT_PEER		= 	  3;
	public static final int 		 CONNECTION_TRAFIC_AVERAGE_CHECKS= 	  10;
	
	// the network
	public static final long          DOWNLOAD_BANDWIDTH            =    1024 * 10 * 256;
	public static final long          UPLOAD_BANDWIDTH              =    1024 * 10 * 256;
	public static final int           MAX_CONNECTIONS               =    500;
	public static final long          DOWNLOAD_LIMIT     			=    1024 * 1024;
	public static final long          UPLOAD_LIMIT       			=    1024 * 512;
	
	public static final long		  MAX_UDP_PACKET_SIZE			=    65535;
	
	public static final long          WRONG_PACKET_CHECK_INTERVAL	=     1000 * 5;
	public static final long		  MAX_WRONG_PACKET_COUNT		=     50;
	
	public static final int 		  DIR_RESCAN_INTERVAL  			=	  1000 * 60 * 10;
	public static final int 		  WRITE_METADATA_INTERVAL		=	  1000 * 60;
	public static final int 		  SEARCH_QUERY_CHECK_INTERVAL   =	  10000;
	
	public static final int 		  SHARED_FILES_PUBLISH_INTERVAL = 	  1000 * 60;
	
	public static final int 		  MAX_PEERS						= 	  300;
	
	public static final int			  MAX_PEX_RESPONSE				= 	  500;
	
	public static final int			  NETWORK_READ_BUFFER			= 	  1024 * 2;
	
	public static final int			DROP_DISCONNECTED_PEER_TIMEOUT =	  1000 * 60 * 1;
	public static final int 		DROP_CONNECTING_HIGH_ID_PEER_TIMEOUT = 	  1000 * 60 * 1;
	public static final int 		DROP_CONNECTING_LOW_ID_PEER_TIMEOUT  = 	  1000 * 60 * 2;
	public static final int			DROP_CONNECTED_PEER_TIMEOUT    =	  1000 * 60 * 1;
	
	// data base keys
	public static final String       NICK_NAME_KEY                  =     "NickName";
	public static final String       TCP_PORT_KEY                   =     "TCPPort";
	public static final String       UDP_PORT_KEY                	=     "UDPPort";
	public static final String       UDP_ENABLED_KEY                =     "UDPEnabled";
	public static final String       USER_HASH_KEY                  =     "UserHash";
	public static final String       DOWNLOAD_LIMIT_KEY             =     "DownloadLimit";
	public static final String       UPLOAD_LIMIT_KEY               =     "UploadLimit";
	public static final String       DOWNLOAD_BANDWIDTH_KEY         =     "DownloadBandwidth";
	public static final String       UPLOAD_BANDWIDTH_KEY           =     "UploadBandwidth";
	public static final String       SHARED_DIRECTORIES_KEY         =     "SharedDirectories";
	public static final String       WORKING_DIR_KEY                =     "WorkingDir";
	
	public static final String		 SERVER_LIST_UPDATE_ON_CONNECT_KEY	   =     "ServerListUpdateOnConnect";
	
	public static final String 		 CUSTOM_PARAMETER_KEY				   =     "CustomParameter";
	
	public static final	String		 SECURITY_IDENTIFICATION			   = 	 "SecurityIdentificationEnabled";
	
	public static final String 		 JKAD_ENABLED_KEY					   = 	"JKadEnabled";
	public static final String 		 JKAD_ID_KEY						   = 	"JKadID";
	
	public static final String		 NIC_NAME_KEY						   =    "NicName";
	public static final String		 NIC_IP_KEY							   =    "NicIP";
	
	// 
	//public static final String       PEER_ACTIVITY_CHECK_TIME_KEY   	   =     "PeerActivityCheckTime";
	//public static final String       SOURCES_QUERY_INTERVAL_KEY     	   =     "SourcesQueryInterval";
	//public static final String       PEER_INACTIVITY_REMOVE_TIME_KEY     =     "PeerInactivityRemoveTime";
	
	/**
	 * Loads the configuration from the repository
	 */
	public void load() throws ConfigurationManagerException ;
	
	/**
	 * Save the configuration to the repository
	 */
	public void save() throws ConfigurationManagerException ;
	
	/**
	 * Sets the nick name that is used in the client
	 * @param nickName the given nick name
	 */
	public void setNickName(String nickName) throws ConfigurationManagerException ;
	
	/**
	 * Sets the tcp port to the given value
	 * @param tcp the given value of the tcp port
	 */
	public void setTCP(int tcp) throws ConfigurationManagerException;
	
	public void setTCP(String tcp) throws ConfigurationManagerException;
	
	/**
	 * Sets the udp port to the given value
	 * @param udp the given value of the udp port
	 */
	public void setUDP(int udp) throws ConfigurationManagerException;
	
	public void setUDP(String udp) throws ConfigurationManagerException;
	
	/**
	 * Sets the list of shared folders 
	 * @param sharedFolders the given list of folders
	 */
	public void setSharedFolders(List<File> sharedFolders) throws ConfigurationManagerException;
	
	/**
	 * 
	 * @return the list of shared folders
	 */
	public List<File> getSharedFolders() throws ConfigurationManagerException;
	
	public void setWorkingDir(File workDir) throws ConfigurationManagerException;
	
	public File getWorkingDir() throws ConfigurationManagerException;
	
	/**
	 * Sets the download limit
	 * @param downloadLimit the given download limit
	 */
	public void setDownloadLimit(long downloadLimit) throws ConfigurationManagerException;
	
	public void setDownloadLimit(String downloadLimit) throws ConfigurationManagerException;
	
	/**
	 * Sets the upload limit
	 * @param uploadLimit the given upload limit
	 */
	public void setUploadLimit(long uploadLimit) throws ConfigurationManagerException;
	
	public void setUploadLimit(String uploadLimit) throws ConfigurationManagerException;
	
	/**
	 * Sets the download bandwidth for the connection that the client will use
	 * @param downloadBandwidth
	 */
	public void setDownloadBandwidth(long downloadBandwidth) throws ConfigurationManagerException;
	
	public void setDownloadBandwidth(String downloadBandwidth) throws ConfigurationManagerException;
	
	/**
	 * Sets the upload bandwidth for the connection that the client will use
	 * @param uploadBandwidth
	 */
	public void setUploadBandwidth(long uploadBandwidth) throws ConfigurationManagerException;
	
	public void setUploadBandwidth(String uploadBandwidth) throws ConfigurationManagerException;
	
	/**
	 * @return the nick name
	 */
	public String getNickName() throws ConfigurationManagerException;
	
	/**
	 * 
	 * @return the tcp port
	 */
	public int getTCP() throws ConfigurationManagerException;
	
	/**
	 * 
	 * @return the udp port
	 */
	public int getUDP() throws ConfigurationManagerException;
	
	/**
	 * Sets the status of the UDP port 
	 * @param enabled
	 */
	public void setUDPEnabled(boolean enabled) throws ConfigurationManagerException;
	
	/**
	 * Tells if the UDP port is enabled
	 * @return true if the UDP port is enabled, false otherwise
	 */
	public boolean isUDPEnabled() throws ConfigurationManagerException;
    
    /**
     * 
     * @return download limit
     */
    public long getDownloadLimit() throws ConfigurationManagerException;
    
    /**
     * 
     * @return upload limit
     */
    public long getUploadLimit() throws ConfigurationManagerException;
    
    /**
     * 
     * @return user hash
     */
    public UserHash getUserHash() throws ConfigurationManagerException;
    /**
     * 
     * @return download bandwidth
     */
    public long getDownloadBandwidth() throws ConfigurationManagerException;
    
    /**
     * 
     * @return upload bandwidth
     */
    public long getUploadBandwidth() throws ConfigurationManagerException;
    
    public boolean isJKadAutoconnectEnabled() throws ConfigurationManagerException;
    
    public void setAutoconnectJKad(boolean newStatus) throws ConfigurationManagerException;
    
    public boolean updateServerListAtConnect() throws ConfigurationManagerException;
    
    public void setUpdateServerListAtConnect(boolean newStatus) throws ConfigurationManagerException;
    
    public ClientID getJKadClientID() throws ConfigurationManagerException;
        
    public String getNicName() throws ConfigurationManagerException;
    
    public void setNicName(String nicName) throws ConfigurationManagerException;
    
    public String getNicIP() throws ConfigurationManagerException;
    
    public void setNicIP(String nicIP) throws ConfigurationManagerException;
    
    public boolean isSecurityIdenficiationEnabled() throws ConfigurationManagerException;
    
    public void setSecurityIdentification(boolean enableSecutity) throws ConfigurationManagerException;
    
    /**
	 * Adds a configuration listener
	 * @param listener the given configuration listener
	 */
	public void addConfigurationListener(ConfigurationListener listener);
		
	/**
	 * Removes a configuration listener
	 * @param listener the given configuration listener
	 */
	public void removeConfigurationListener(ConfigurationListener listener);
	
}
