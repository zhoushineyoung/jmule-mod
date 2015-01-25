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
package org.jmule.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.jmule.core.JMConstants;
import org.jmule.core.configmanager.ConfigurationManager;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.12 $$ Last changed by $$Author: binary255 $$ on
 *          $$Date: 2010/01/05 14:44:56 $$
 */
public class UIPreferences extends UIConstants {

	// static Preferences preferences = Preferences.systemRoot();

	protected static Properties config_store;

	private static UIPreferences instance = null;

	public static final String UI_SETTINGS_FILE = ConfigurationManager.SETTINGS_DIR
			+ File.separator + "jmule_ui.properties";

	public static UIPreferences getSingleton() {
		if (instance == null)
			instance = new UIPreferences();
		return instance;
	}

	public void load() {
		try {
			  if( new File(UI_SETTINGS_FILE).exists() ) {
				 config_store = new Properties();
				 config_store.load(new FileInputStream(UI_SETTINGS_FILE));
			  } else {
				  config_store = new Properties();
			  }
		}catch(Throwable cause) {
				cause.printStackTrace();
		}
	}
	
	public static void storeDefaultPreferences(String particular_ui_root) {
		
		try {
			 config_store = new Properties();
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_NAME_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( SERVER_LIST_NAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_CC_COLUMN_ID ) ).
			 //putBoolean( VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_CC_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_CC_COLUMN_ID ) + VISIBILITY, 
					                  getDefaultColumnVisibility( SERVER_LIST_CC_COLUMN_ID ) + "");
			    
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_FLAG_COLUMN_ID ) ).
			 //putBoolean( VISIBILITY,  getDefaultColumnVisibility( SERVER_LIST_FLAG_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_FLAG_COLUMN_ID ) + VISIBILITY, 
					                  getDefaultColumnVisibility( SERVER_LIST_FLAG_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_IP_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_IP_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_IP_COLUMN_ID ) + VISIBILITY, 
					                  getDefaultColumnVisibility( SERVER_LIST_IP_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_DESCRIPTION_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_DESCRIPTION_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_DESCRIPTION_COLUMN_ID ) + VISIBILITY, 
					                  getDefaultColumnVisibility( SERVER_LIST_DESCRIPTION_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_PING_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_PING_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_PING_COLUMN_ID ) + VISIBILITY, 
					                  getDefaultColumnVisibility( SERVER_LIST_PING_COLUMN_ID ) + ""); 
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_USERS_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_USERS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_USERS_COLUMN_ID ) + VISIBILITY, 
					                  getDefaultColumnVisibility( SERVER_LIST_USERS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_MAX_USERS_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_MAX_USERS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_MAX_USERS_COLUMN_ID ) + VISIBILITY, 
					                  getDefaultColumnVisibility( SERVER_LIST_MAX_USERS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_FILES_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_FILES_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_FILES_COLUMN_ID ) + VISIBILITY, 
					                  getDefaultColumnVisibility( SERVER_LIST_FILES_COLUMN_ID ) + ""); 
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) + VISIBILITY, 
					                  getDefaultColumnVisibility( SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_HARD_LIMIT_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_HARD_LIMIT_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_HARD_LIMIT_COLUMN_ID ) + VISIBILITY,
					 				  getDefaultColumnVisibility( SERVER_LIST_HARD_LIMIT_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_VERSION_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_VERSION_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_VERSION_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( SERVER_LIST_VERSION_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_STATIC_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_STATIC_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_STATIC_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( SERVER_LIST_STATIC_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_ORDER_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_ORDER_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_ORDER_ID ) + VISIBILITY, 
					                  getDefaultColumnVisibility( DOWNLOAD_LIST_ORDER_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SIZE_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_SIZE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SIZE_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( DOWNLOAD_LIST_SIZE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) + VISIBILITY, 
					                  getDefaultColumnVisibility( DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) + VISIBILITY, 
					                  getDefaultColumnVisibility( DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SOURCES_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_SOURCES_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SOURCES_COLUMN_ID ) + VISIBILITY, 
					 				  getDefaultColumnVisibility( DOWNLOAD_LIST_SOURCES_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID ) + "");
			 
			 //preferences.
			 ///node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_REMAINING_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_REMAINING_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_REMAINING_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( DOWNLOAD_LIST_REMAINING_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_STATUS_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_STATUS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_STATUS_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( DOWNLOAD_LIST_STATUS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) + VISIBILITY, 
					                  getDefaultColumnVisibility( DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) + "");
			 
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_NAME_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_LIST_FILE_NAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_NAME_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( UPLOAD_LIST_FILE_NAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_PEERS_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_LIST_PEERS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_PEERS_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( UPLOAD_LIST_PEERS_COLUMN_ID ) + "");
			 
			 //preferences.
			// node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_ETA_COLUMN_ID ) ).
			// putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_LIST_ETA_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_ETA_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( UPLOAD_LIST_ETA_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOADED_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_LIST_UPLOADED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOADED_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( UPLOAD_LIST_UPLOADED_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_IP_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_PEER_LIST_IP_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_IP_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( UPLOAD_PEER_LIST_IP_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_FILEQUALITY_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SEARCH_FILEQUALITY_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_FILEQUALITY_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( SEARCH_FILEQUALITY_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_FILENAME_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SEARCH_FILENAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_FILENAME_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( SEARCH_FILENAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_FILESIZE_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SEARCH_FILESIZE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_FILESIZE_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( SEARCH_FILESIZE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_AVAILABILITY_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SEARCH_AVAILABILITY_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_AVAILABILITY_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( SEARCH_AVAILABILITY_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_COMPLETESRC_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SEARCH_COMPLETESRC_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_COMPLETESRC_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( SEARCH_COMPLETESRC_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_FILE_TYPE_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SEARCH_FILE_TYPE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_FILE_TYPE_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( SEARCH_FILE_TYPE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_FILE_ID_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SEARCH_FILE_ID_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_FILE_ID_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( SEARCH_FILE_ID_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_NAME_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SHARED_LIST_FILE_NAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_NAME_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( SHARED_LIST_FILE_NAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_SIZE_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SHARED_LIST_FILE_SIZE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_SIZE_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( SHARED_LIST_FILE_SIZE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_TYPE_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SHARED_LIST_FILE_TYPE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_TYPE_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( SHARED_LIST_FILE_TYPE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_ID_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SHARED_LIST_FILE_ID_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_ID_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( SHARED_LIST_FILE_ID_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SHARED_LIST_COMPLETED_COLUMN_ID ) ).
			 //putBoolean(VISIBILITY, getDefaultColumnVisibility( SHARED_LIST_COMPLETED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SHARED_LIST_COMPLETED_COLUMN_ID ) + VISIBILITY,
					                  getDefaultColumnVisibility( SHARED_LIST_COMPLETED_COLUMN_ID ) + "");

			 // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
			 
			// sets the default width of the columns
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_NAME_COLUMN_ID ) ).
			 //putInt(WIDTH,  getDefaultColumnWidth( SERVER_LIST_NAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_NAME_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SERVER_LIST_NAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 ///node( getColumnNodePath( particular_ui_root, SERVER_LIST_CC_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_CC_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_CC_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SERVER_LIST_CC_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_FLAG_COLUMN_ID ) ).
			 //putInt(WIDTH,  getDefaultColumnWidth( SERVER_LIST_FLAG_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_FLAG_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SERVER_LIST_FLAG_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_IP_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_IP_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_IP_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SERVER_LIST_IP_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_DESCRIPTION_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_DESCRIPTION_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_DESCRIPTION_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SERVER_LIST_DESCRIPTION_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_PING_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_PING_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_PING_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SERVER_LIST_PING_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_USERS_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_USERS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_USERS_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SERVER_LIST_USERS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_MAX_USERS_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_MAX_USERS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_MAX_USERS_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SERVER_LIST_MAX_USERS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_FILES_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_FILES_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_FILES_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SERVER_LIST_FILES_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_HARD_LIMIT_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_HARD_LIMIT_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_HARD_LIMIT_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SERVER_LIST_HARD_LIMIT_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_VERSION_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_VERSION_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_VERSION_COLUMN_ID ) + WIDTH, 
					                  getDefaultColumnWidth( SERVER_LIST_VERSION_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_STATIC_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_STATIC_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_STATIC_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SERVER_LIST_STATIC_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_ORDER_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_ORDER_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_ORDER_ID ) + WIDTH,
					                  getDefaultColumnWidth( DOWNLOAD_LIST_ORDER_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SIZE_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_SIZE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SIZE_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( DOWNLOAD_LIST_SIZE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SOURCES_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_SOURCES_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SOURCES_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( DOWNLOAD_LIST_SOURCES_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_REMAINING_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_REMAINING_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_REMAINING_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( DOWNLOAD_LIST_REMAINING_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_STATUS_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_STATUS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_STATUS_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( DOWNLOAD_LIST_STATUS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_NAME_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( UPLOAD_LIST_FILE_NAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_NAME_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( UPLOAD_LIST_FILE_NAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_PEERS_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( UPLOAD_LIST_PEERS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_PEERS_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( UPLOAD_LIST_PEERS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_ETA_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( UPLOAD_LIST_ETA_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_ETA_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( UPLOAD_LIST_ETA_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOADED_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( UPLOAD_LIST_UPLOADED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOADED_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( UPLOAD_LIST_UPLOADED_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_IP_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( UPLOAD_PEER_LIST_IP_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_IP_COLUMN_ID ) + WIDTH, 
					                  getDefaultColumnWidth( UPLOAD_PEER_LIST_IP_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) + WIDTH,
					 getDefaultColumnWidth( UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_FILEQUALITY_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SEARCH_FILEQUALITY_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_FILEQUALITY_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SEARCH_FILEQUALITY_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_FILENAME_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SEARCH_FILENAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_FILENAME_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SEARCH_FILENAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_FILESIZE_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SEARCH_FILESIZE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_FILESIZE_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SEARCH_FILESIZE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_AVAILABILITY_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SEARCH_AVAILABILITY_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_AVAILABILITY_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SEARCH_AVAILABILITY_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_COMPLETESRC_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SEARCH_COMPLETESRC_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_COMPLETESRC_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SEARCH_COMPLETESRC_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_FILE_TYPE_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SEARCH_FILE_TYPE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_FILE_TYPE_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SEARCH_FILE_TYPE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_FILE_ID_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SEARCH_FILE_ID_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_FILE_ID_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SEARCH_FILE_ID_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_NAME_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SHARED_LIST_FILE_NAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_NAME_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SHARED_LIST_FILE_NAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_SIZE_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SHARED_LIST_FILE_SIZE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_SIZE_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SHARED_LIST_FILE_SIZE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_TYPE_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SHARED_LIST_FILE_TYPE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_TYPE_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SHARED_LIST_FILE_TYPE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_ID_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SHARED_LIST_FILE_ID_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_ID_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SHARED_LIST_FILE_ID_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SHARED_LIST_COMPLETED_COLUMN_ID ) ).
			 //putInt(WIDTH, getDefaultColumnWidth( SHARED_LIST_COMPLETED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SHARED_LIST_COMPLETED_COLUMN_ID ) + WIDTH,
					                  getDefaultColumnWidth( SHARED_LIST_COMPLETED_COLUMN_ID ) + "");

			// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
			 
			// sets the default order of the columns
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_NAME_COLUMN_ID ) ).
			 //putInt(ORDER,  getDefaultColumnOrder( SERVER_LIST_NAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_NAME_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SERVER_LIST_NAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_CC_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_CC_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_CC_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SERVER_LIST_CC_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_FLAG_COLUMN_ID ) ).
			 //putInt(ORDER,  getDefaultColumnOrder( SERVER_LIST_FLAG_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_FLAG_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SERVER_LIST_FLAG_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_IP_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_IP_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_IP_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SERVER_LIST_IP_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_DESCRIPTION_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_DESCRIPTION_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_DESCRIPTION_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SERVER_LIST_DESCRIPTION_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_PING_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_PING_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_PING_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SERVER_LIST_PING_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_USERS_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_USERS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_USERS_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SERVER_LIST_USERS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_MAX_USERS_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_MAX_USERS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_MAX_USERS_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SERVER_LIST_MAX_USERS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_FILES_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_FILES_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_FILES_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SERVER_LIST_FILES_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_HARD_LIMIT_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_HARD_LIMIT_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_HARD_LIMIT_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SERVER_LIST_HARD_LIMIT_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_VERSION_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_VERSION_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_VERSION_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SERVER_LIST_VERSION_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SERVER_LIST_STATIC_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_STATIC_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SERVER_LIST_STATIC_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SERVER_LIST_STATIC_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_ORDER_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_ORDER_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_ORDER_ID ) + ORDER,
					                  getDefaultColumnOrder( DOWNLOAD_LIST_ORDER_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SIZE_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_SIZE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SIZE_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( DOWNLOAD_LIST_SIZE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SOURCES_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_SOURCES_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SOURCES_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( DOWNLOAD_LIST_SOURCES_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_REMAINING_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_REMAINING_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_REMAINING_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( DOWNLOAD_LIST_REMAINING_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_STATUS_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_STATUS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_STATUS_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( DOWNLOAD_LIST_STATUS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) + ORDER,
					 getDefaultColumnOrder( DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_NAME_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( UPLOAD_LIST_FILE_NAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_NAME_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( UPLOAD_LIST_FILE_NAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_PEERS_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( UPLOAD_LIST_PEERS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_PEERS_COLUMN_ID ) + ORDER, 
					                  getDefaultColumnOrder( UPLOAD_LIST_PEERS_COLUMN_ID ) + ""); 
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_ETA_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( UPLOAD_LIST_ETA_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_ETA_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( UPLOAD_LIST_ETA_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOADED_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( UPLOAD_LIST_UPLOADED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOADED_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( UPLOAD_LIST_UPLOADED_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_IP_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( UPLOAD_PEER_LIST_IP_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_IP_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( UPLOAD_PEER_LIST_IP_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_FILEQUALITY_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SEARCH_FILEQUALITY_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_FILEQUALITY_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SEARCH_FILEQUALITY_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_FILENAME_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SEARCH_FILENAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_FILENAME_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SEARCH_FILENAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_FILESIZE_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SEARCH_FILESIZE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_FILESIZE_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SEARCH_FILESIZE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_AVAILABILITY_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SEARCH_AVAILABILITY_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_AVAILABILITY_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SEARCH_AVAILABILITY_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_COMPLETESRC_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SEARCH_COMPLETESRC_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_COMPLETESRC_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SEARCH_COMPLETESRC_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_FILE_TYPE_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SEARCH_FILE_TYPE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_FILE_TYPE_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SEARCH_FILE_TYPE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SEARCH_FILE_ID_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SEARCH_FILE_ID_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SEARCH_FILE_ID_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SEARCH_FILE_ID_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_NAME_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SHARED_LIST_FILE_NAME_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_NAME_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SHARED_LIST_FILE_NAME_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_SIZE_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SHARED_LIST_FILE_SIZE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_SIZE_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SHARED_LIST_FILE_SIZE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_TYPE_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SHARED_LIST_FILE_TYPE_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_TYPE_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SHARED_LIST_FILE_TYPE_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_ID_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SHARED_LIST_FILE_ID_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_ID_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SHARED_LIST_FILE_ID_COLUMN_ID ) + "");
			 
			 //preferences.
			 //node( getColumnNodePath( particular_ui_root, SHARED_LIST_COMPLETED_COLUMN_ID ) ).
			 //putInt(ORDER, getDefaultColumnOrder( SHARED_LIST_COMPLETED_COLUMN_ID ) );
			 config_store.setProperty(getColumnNodePath( particular_ui_root, SHARED_LIST_COMPLETED_COLUMN_ID ) + ORDER,
					                  getDefaultColumnOrder( SHARED_LIST_COMPLETED_COLUMN_ID ) + "");
			 
			 // other elements
			 
			 //preferences.
			 //node( getPromptOnExitNodePath( particular_ui_root ) ).
			 //putBoolean(ENABLED, getDefaultPromptOnExit());
			 config_store.setProperty(getPromptOnExitNodePath( particular_ui_root ) + ENABLED,
					                  getDefaultPromptOnExit() + "");
			
			 //preferences.
			 //node( getStartupCheckUpdateNodePath( particular_ui_root ) ).
			 //putBoolean(ENABLED, getDefaultStartupCheckUpdate());
			 config_store.setProperty(getStartupCheckUpdateNodePath( particular_ui_root ) + ENABLED,
					                  getDefaultStartupCheckUpdate() + "");
			 
			 //preferences.
			 //node( getToolBarNodePath( particular_ui_root ) ).
			 //putBoolean(VISIBILITY, getToolBarDefaultVisibility());
			 config_store.setProperty(getToolBarNodePath( particular_ui_root ) + VISIBILITY, 
					                  getToolBarDefaultVisibility() + ""); 
			 
			 //preferences.
			 //node( getStatusBarNodePath( particular_ui_root ) ).
			 //putBoolean(VISIBILITY, getStatusBarDefaultVisibility());
			 config_store.setProperty(getStatusBarNodePath( particular_ui_root ) + VISIBILITY,
					                  getStatusBarDefaultVisibility() + "");
			 
			 //preferences.
			 //node( getConnectAtStartupNodePath( particular_ui_root ) ).
			 //putBoolean(ENABLED, getDefaultConnectAtStartup());
			 config_store.setProperty(getConnectAtStartupNodePath( particular_ui_root ) + ENABLED,
					                  getDefaultConnectAtStartup() + "");

			 config_store.store(new FileOutputStream(UI_SETTINGS_FILE), "");
		}catch(Throwable t) {
			t.printStackTrace();
		}
		
	}

	protected void save() {
		try {
			config_store.store(new FileOutputStream(UI_SETTINGS_FILE), "");
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	// PromptOnExit methods

	protected void setPromptOnExit(String uiRoot, boolean value) {
		String prompt_on_exit_node = getPromptOnExitNodePath(uiRoot);
		// preferences.node(prompt_on_exit_node).putBoolean(ENABLED, value);
		config_store.setProperty(prompt_on_exit_node + ENABLED, value + "");
		save();
	}

	protected boolean isPromptOnExitEnabled(String uiRoot) {
		String node = getPromptOnExitNodePath(uiRoot);

		return Boolean.parseBoolean(config_store.getProperty(node + ENABLED,
				getDefaultPromptOnExit() + ""));
		// return preferences.node(node).getBoolean(ENABLED,
		// getDefaultPromptOnExit());
	}

	// end PromptOnExitMethods

	// CheckForUpdatesAtStartup methods

	protected boolean isCheckForUpdatesAtStartup(String uiRoot) {
		String update_check_node = getStartupCheckUpdateNodePath(uiRoot);

		return Boolean.parseBoolean(config_store.getProperty(update_check_node
				+ ENABLED, getDefaultStartupCheckUpdate() + ""));
		// return preferences.node(update_check_node).getBoolean(ENABLED,
		// getDefaultStartupCheckUpdate());
	}

	protected void setCheckForUpdatesAtStartup(String uiRoot, boolean value) {
		String update_check_node = getStartupCheckUpdateNodePath(uiRoot);
		config_store.setProperty(update_check_node + ENABLED, value + "");
		save();
		// preferences.node(update_check_node).putBoolean(ENABLED, value);
	}

	// end CheckForUpdatesAtStartup methods

	// ToolBar methods

	protected boolean isToolBarVisible(String uiRoot) {
		String toolbar_node = getToolBarNodePath(uiRoot);
		// return preferences.node(toolbar_node).getBoolean(VISIBILITY,
		// getToolBarDefaultVisibility());
		return Boolean.parseBoolean(config_store.getProperty(toolbar_node
				+ VISIBILITY, getToolBarDefaultVisibility() + ""));
	}

	protected void setToolBarVisible(String uiRoot, boolean visibility) {
		String toolbar_node = getToolBarNodePath(uiRoot);
		// preferences.node(toolbar_node).putBoolean(VISIBILITY,visibility);
		config_store.setProperty(toolbar_node + VISIBILITY, visibility + "");
		save();
	}

	// end ToolBar methods

	// StatusBar methods

	protected boolean isStatusBarVisible(String uiRoot) {
		String toolbar_node = getStatusBarNodePath(uiRoot);
		// return preferences.node(toolbar_node).getBoolean(VISIBILITY,
		// getStatusBarDefaultVisibility());
		return Boolean.parseBoolean(config_store.getProperty(toolbar_node
				+ VISIBILITY, getStatusBarDefaultVisibility() + ""));
	}

	protected void setStatusBarVisible(String uiRoot, boolean visibility) {
		String statusbar_node = getStatusBarNodePath(uiRoot);
		// preferences.node(toolbar_node).putBoolean(VISIBILITY,visibility);
		config_store.setProperty(statusbar_node + VISIBILITY, visibility + "");
		save();
	}

	// end StatusBar methods

	// NightlyBuildWarning methods

	protected boolean isNightlyBuildWarning(String uiRoot) {
		String node = getNightlyBuildWarningNodePath(uiRoot);
		// boolean value = preferences.node(node).getBoolean(ENABLED,
		// getDefaultNightlyBuildWarningEnabled());
		boolean value = Boolean.parseBoolean(config_store.getProperty(node
				+ ENABLED, getDefaultNightlyBuildWarningEnabled() + ""));
		if (value) {
			setNightlyBuildWarningJMVer(uiRoot, JMConstants.DEV_VERSION);
			return true;
		}
		String stored_ver = getNightlyBuildWarningJMVer(uiRoot);
		if (JMConstants.compareDevVersions(stored_ver, JMConstants.DEV_VERSION) != 0) {
			setNightlyBuildWarning(uiRoot, true);
			setNightlyBuildWarningJMVer(uiRoot, JMConstants.DEV_VERSION);
			return true;
		}
		return false;
	}

	protected void setNightlyBuildWarning(String uiRoot, boolean value) {
		String node = getNightlyBuildWarningNodePath(uiRoot);
		// preferences.node(node).putBoolean(ENABLED, value);
		config_store.setProperty(node + ENABLED, value + "");
		save();
	}

	private void setNightlyBuildWarningJMVer(String uiRoot, String value) {
		String node_path = getNightlyBuildWarningJMVerNodePath(uiRoot);
		// preferences.node(node_path).put(VERSION, value);
		config_store.setProperty(node_path + VERSION, value);
		save();
	}

	private String getNightlyBuildWarningJMVer(String uiRoot) {
		String node_path = getNightlyBuildWarningJMVerNodePath(uiRoot);
		// return preferences.node(node_path).get(VERSION,
		// getDefaultNightlyBuildWarningJMVer());
		return config_store.getProperty(node_path + VERSION,
				getDefaultNightlyBuildWarningJMVer());
	}

	// end NightlyBuildWarning methods

	// Connect at start up methods

	protected boolean isConnectAtStartup(String uiRoot) {
		String node = getConnectAtStartupNodePath(uiRoot);
		// return preferences.node(node).getBoolean(ENABLED,
		// getDefaultConnectAtStartup());
		return Boolean.parseBoolean(config_store.getProperty(node + ENABLED,
				getDefaultConnectAtStartup() + ""));
	}

	protected void setConnectAtStartup(String uiRoot, boolean value) {
		String node = getConnectAtStartupNodePath(uiRoot);
		// preferences.node(node).putBoolean(ENABLED, value);
		config_store.setProperty(node + ENABLED, value + "");
		save();
	}

	// end connect at startup methods

	protected void setColumnWidth(String uiRoot, int columnID, int width) {
		String node = getColumnNodePath(uiRoot, columnID);
		// preferences.node(node).putInt(WIDTH, width);
		config_store.setProperty(node + WIDTH, width + "");
		save();
	}

	protected int getColumnWidth(String uiRoot, int columnID) {
		String node = getColumnNodePath(uiRoot, columnID);
		// return preferences.node(node).getInt(WIDTH,
		// getDefaultColumnWidth(columnID));
		return Integer.parseInt(config_store.getProperty(node + WIDTH,
				getDefaultColumnWidth(columnID) + ""));
	}

	protected void setColumnOrder(String uiRoot, int columnID, int order) {
		String node = getColumnNodePath(uiRoot, columnID);
		// preferences.node(node).putInt(ORDER, order);
		config_store.setProperty(node + ORDER, order + "");
		save();
	}

	protected int getColumnOrder(String uiRoot, int columnID) {
		String node = getColumnNodePath(uiRoot, columnID);
		// return preferences.node(node).getInt(ORDER,
		// getDefaultColumnOrder(columnID));
		return Integer.parseInt(config_store.getProperty(node + ORDER,
				getDefaultColumnOrder(columnID) + ""));
	}

	protected void setColumnVisibility(String uiRoot, int columnID,
			boolean visibility) {
		String node = getColumnNodePath(uiRoot, columnID);
		// preferences.node(node).putBoolean(VISIBILITY, visibility);
		config_store.setProperty(node + VISIBILITY, visibility + "");
		save();
	}

	protected boolean isColumnVisible(String uiRoot, int columnID) {
		String node = getColumnNodePath(uiRoot, columnID);
		// return preferences.node(node).getBoolean(VISIBILITY,
		// getDefaultColumnVisibility(columnID));
		return Boolean.parseBoolean(config_store.getProperty(node + VISIBILITY,
				getDefaultColumnVisibility(columnID) + ""));
	}

}
