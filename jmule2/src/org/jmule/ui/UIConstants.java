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


import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.jmule.core.JMConstants;


/**
 * 
 * @author javajox
 * @author binary
 * @version $$Revision: 1.19 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/01/18 18:03:55 $$
 */

public class UIConstants {
	
	protected static final String UI_ROOT = "org.jmule.ui";
	
	protected static final String VISIBILITY        =       ".VISIBILITY";
	protected static final String ORDER             =       ".ORDER";
	protected static final String WIDTH             =       ".WIDTH";
	protected static final String ENABLED           =       ".ENABLED";
	protected static final String VERSION           =       ".VERSION";
	protected static final String TABLE_COLUMN_PATH =       ".tables.columns";
	
	public static final int TOOL_BAR_ID                         = 1;
	public static final int STATUS_BAR_ID                       = 2;

	public static final int PROMPT_ON_EXIT_ID					= 3;
	public static final int CHECK_UPDATE_AT_STARTUP_ID			= 4;
	
	// if the current JM version is greater than NIGHTLY_BUILD_WARNING_JM_VER_ID
	// then the warning is shown at least once 
	public static final int NIGHTLY_BUILD_WARNING_ID            = 5;
	public static final int NIGHTLY_BUILD_WARNING_JM_VER_ID     = 6;
	
	public static final int CONNECT_AT_STARTUP_ID               = 7;
	
	// Columns ID

	// server list
	public static final int SERVER_LIST_NAME_COLUMN_ID          =   100;
	public static final int SERVER_LIST_CC_COLUMN_ID            =   110;
	public static final int SERVER_LIST_FLAG_COLUMN_ID          =   150;
	public static final int SERVER_LIST_IP_COLUMN_ID            =   200;
	public static final int SERVER_LIST_DESCRIPTION_COLUMN_ID   =   300;
	public static final int SERVER_LIST_PING_COLUMN_ID          =   400;
	public static final int SERVER_LIST_USERS_COLUMN_ID         =   500;
	public static final int SERVER_LIST_MAX_USERS_COLUMN_ID     =   600;
	public static final int SERVER_LIST_FILES_COLUMN_ID         =   700;
	public static final int SERVER_LIST_SOFT_LIMIT_COLUMN_ID    =   800;
	public static final int SERVER_LIST_HARD_LIMIT_COLUMN_ID    =   900;
	public static final int SERVER_LIST_VERSION_COLUMN_ID   	=   1000;
	public static final int SERVER_LIST_STATIC_COLUMN_ID        =   1010;
	public static final int SERVER_LIST_DOWN_COLUMN_ID          =   1020;
	
	// downloads
	public static final int DOWNLOAD_LIST_ORDER_ID                    = 1090;
	public static final int DOWNLOAD_LIST_FILE_NAME_COLUMN_ID         = 1100; 
	public static final int DOWNLOAD_LIST_SIZE_COLUMN_ID              = 1200;
	public static final int DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID       = 1300;
	public static final int DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID    = 1400;
	public static final int DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID      = 1500;
	public static final int DOWNLOAD_LIST_PROGRESS_COLUMN_ID          = 1600;
	public static final int DOWNLOAD_LIST_PROGRESS_BAR_COLUMN_ID      = 1610;
	public static final int DOWNLOAD_LIST_COMPLETED_COLUMN_ID         = 1650;
	public static final int DOWNLOAD_LIST_SOURCES_COLUMN_ID           = 1700;
	public static final int DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID  = 1710;
	public static final int DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID   = 1720;
	public static final int DOWNLOAD_LIST_REMAINING_COLUMN_ID         = 1800;
	public static final int DOWNLOAD_LIST_STATUS_COLUMN_ID            = 1900;
	
	public static final int DOWNLOAD_PEER_LIST_IP_COLUMN_ID           = 2000;
	public static final int DOWNLOAD_PEER_LIST_SOURCE_COLUMN_ID       = 2020;
	public static final int DOWNLOAD_PEER_LIST_CC_COLUMN_ID	          = 2025;
	public static final int DOWNLOAD_PEER_LIST_FLAG_COLUMN_ID         = 2050;
	public static final int DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID       = 2100;
	public static final int DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID     = 2200;
	public static final int DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID     = 2300;
	public static final int DOWNLOAD_PEER_LIST_DOWN_SPEED_COLUMN_ID   = 2325;
	public static final int DOWNLOAD_PEER_LIST_UP_SPEED_COLUMN_ID     = 2350;
	
	// uploads
	public static final int UPLOAD_LIST_FILE_NAME_COLUMN_ID           = 2400;
	public static final int UPLOAD_LIST_FILE_SIZE_COLUMN_ID           = 2500;
	public static final int UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID        = 2600;
	public static final int UPLOAD_LIST_PEERS_COLUMN_ID               = 2700;
	public static final int UPLOAD_LIST_ETA_COLUMN_ID                 = 2800;
	public static final int UPLOAD_LIST_UPLOADED_COLUMN_ID            = 2900;
	
	public static final int UPLOAD_PEER_LIST_IP_COLUMN_ID             = 3000;
	public static final int UPLOAD_PEER_LIST_CC_COLUMN_ID		      = 3025;
	public static final int UPLOAD_PEER_LIST_FLAG_COLUMN_ID		      = 3050;
	public static final int UPLOAD_PEER_LIST_STATUS_COLUMN_ID         = 3100;
	public static final int UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_ID   = 3150;
	public static final int UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID       = 3200;
	public static final int UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID       = 3300;
	
	// search
	public static final int SEARCH_FILEQUALITY_COLUMN_ID              = 3350;
	public static final int SEARCH_FILENAME_COLUMN_ID                 = 3400;
	public static final int SEARCH_FILESIZE_COLUMN_ID                 = 3500;
	public static final int SEARCH_AVAILABILITY_COLUMN_ID             = 3600;
	public static final int SEARCH_COMPLETESRC_COLUMN_ID              = 3700;
	public static final int SEARCH_FILE_TYPE_COLUMN_ID                = 3800;
	public static final int SEARCH_FILE_ID_COLUMN_ID                  = 3900;
	
	// shared
	public static final int SHARED_LIST_FILE_NAME_COLUMN_ID           = 4000;
	public static final int SHARED_LIST_FILE_SIZE_COLUMN_ID           = 4100;
	public static final int SHARED_LIST_FILE_TYPE_COLUMN_ID           = 4200;
	public static final int SHARED_LIST_FILE_ID_COLUMN_ID             = 4300;
	public static final int SHARED_LIST_COMPLETED_COLUMN_ID           = 4400;
	
	// add new downloads
	public static final int NEW_DOWNLOAD_LIST_FILE_NAME_COLUMN_ID	  = 4500;
	public static final int NEW_DOWNLOAD_LIST_FILE_SIZE_COLUMN_ID	  = 4600;
	public static final int NEW_DOWNLOAD_LIST_FILE_ID_COLUMN_ID		  = 4700;
	
	public static final int NEW_SERVER_LIST_NAME_COLUMN_ID		 	  = 4800;
	public static final int NEW_SERVER_LIST_PORT_COLUMN_ID		  	  = 4900;
	
	// kad
	public static final int KAD_CLIENT_ID_COLUMN_ID				 	  = 5000;
	public static final int KAD_CLIENT_DISTANCE_COLUMN_ID		  	  = 5100;
	
	public static final int KAD_TASK_TYPE_COLUMN_ID				  	  = 5200;
	public static final int KAD_TASK_LOOKUP_HASH_COLUMN_ID		  	  = 5300;
	public static final int KAD_TASK_LOOKUP_INFO_COLUMN_ID		  	  = 5400;
	
	// Column UI nodes
	protected static final String SERVER_LIST_NAME_NODE                      = ".server_list_name_column";
	protected static final String SERVER_LIST_CC_NODE                        = ".server_list_cc_column";
	protected static final String SERVER_LIST_FLAG_NODE                      = ".server_list_flag_column";
	protected static final String SERVER_LIST_IP_NODE                        = ".server_list_ip_column";
	protected static final String SERVER_LIST_DESCRIPTION_NODE               = ".server_list_description_column";
	protected static final String SERVER_LIST_PING_NODE                      = ".server_list_ping_column";
	protected static final String SERVER_LIST_USERS_NODE                     = ".server_list_user_column";
	protected static final String SERVER_LIST_MAX_USERS_NODE                 = ".server_list_max_user_column";
	protected static final String SERVER_LIST_FILES_NODE                     = ".server_list_files_column";
	protected static final String SERVER_LIST_SOFT_LIMIT_NODE                = ".server_list_soft_limit_column";
	protected static final String SERVER_LIST_HARD_LIMIT_NODE                = ".server_list_hard_limit_column";
	protected static final String SERVER_LIST_VERSION_NODE                   = ".server_list_version_column";
	protected static final String SERVER_LIST_STATIC_NODE                    = ".server_list_static_column";
	protected static final String SERVER_LIST_DOWN_NODE                      = ".server_list_down_column";
	
	protected static final String DOWNLOAD_LIST_ORDER_NODE                   = ".download_list_order";        
	protected static final String DOWNLOAD_LIST_FILE_NAME_COLUMN_NODE        = ".download_list_file_name_column";
	protected static final String DOWNLOAD_LIST_SIZE_COLUMN_NODE             = ".download_list_size_column";
	protected static final String DOWNLOAD_LIST_TRANSFERRED_COLUMN_NODE      = ".download_list_transferred_column";
	protected static final String DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_NODE   = ".download_list_download_speed_column";
	protected static final String DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_NODE     = ".download_list_upload_speed_column";
	protected static final String DOWNLOAD_LIST_PROGRESS_COLUMN_NODE         = ".download_list_progress_column";
	protected static final String DOWNLOAD_LIST_PROGRESS_BAR_COLUMN_NODE     = ".download_list_progress_bar_column";
	protected static final String DOWNLOAD_LIST_COMPLETED_COLUMN_NODE        = ".download_list_completed_column";
	protected static final String DOWNLOAD_LIST_SOURCES_COLUMN_NODE          = ".download_list_sources_column";
	protected static final String DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_NODE = ".download_list_complete_sources_column";
	protected static final String DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_NODE  = ".download_list_partial_sources_column";
	protected static final String DOWNLOAD_LIST_REMAINING_COLUMN_NODE        = ".download_remaining_column";
	protected static final String DOWNLOAD_LIST_STATUS_COLUMN_NODE           = ".download_list_status_column";
	
	protected static final String DOWNLOAD_PEER_LIST_IP_COLUMN_NODE          = ".download_peer_list_ip_column";
	protected static final String DOWNLOAD_PEER_LIST_SOURCE_COLUMN_NODE      = ".download_peer_list_source_column";
	protected static final String DOWNLOAD_PEER_LIST_STATUS_COLUMN_NODE      = ".download_peer_list_status_column";
	protected static final String DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_NODE    = ".download_peer_list_nickname_column";
	protected static final String DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_NODE    = ".download_peer_list_software_column";
	protected static final String DOWNLOAD_PEER_LIST_DOWN_SPEED_COLUMN_NODE  = ".download_peer_list_down_speed_column";
	protected static final String DOWNLOAD_PEER_LIST_UP_SPEED_COLUMN_NODE    = ".download_peer_list_up_speed_column";
	protected static final String DOWNLOAD_PEER_LIST_FLAG_COLUMN_NODE   	 = ".download_peer_list_flag_column";
	protected static final String DOWNLOAD_PEER_LIST_CC_COLUMN_NODE		   	 = ".download_peer_list_cc_column";

	protected static final String UPLOAD_LIST_FILENAME_COLUMN_NODE           = ".upload_list_filename_column";
	protected static final String UPLOAD_LIST_FILESIZE_COLUMN_NODE           = ".upload_list_filesize_column";
	protected static final String UPLOAD_LIST_UPLOADSPEED_COLUMN_NODE        = ".upload_list_uploadspeed_column";
	protected static final String UPLOAD_LIST_PEERS_COLUMN_NODE              = ".upload_list_peers_column";
	protected static final String UPLOAD_LIST_ETA_COLUMN_NODE                = ".upload_list_eta_column";
	protected static final String UPLOAD_LIST_UPLOADED_COLUMN_NODE           = ".upload_list_uploaded_column";
	
	protected static final String UPLOAD_PEER_LIST_IP_COLUMN_NODE           = ".upload_peer_list_ip_column";
	protected static final String UPLOAD_PEER_LIST_CC_COLUMN_NODE	        = ".upload_peer_list_cc_column";
	protected static final String UPLOAD_PEER_LIST_FLAG_COLUMN_NODE	        = ".upload_peer_list_flag_column";
	protected static final String UPLOAD_PEER_LIST_STATUS_COLUMN_NODE       = ".upload_peer_list_status_column";
	protected static final String UPLOAD_PEER_LIST_NICKNAME_COLUMN_NODE     = ".upload_peer_list_nickname_column";
	protected static final String UPLOAD_PEER_LIST_SOFTWARE_COLUMN_NODE     = ".upload_peer_list_software_column";
	protected static final String UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_NODE = ".upload_peer_list_upload_speed_column";
	
	protected static final String SEARCH_FILENAME_COLUMN_NODE               = ".search_filename_column";
	protected static final String SEARCH_FILESIZE_COLUMN_NODE               = ".search_filesize_column";
	protected static final String SEARCH_FILEQUALITY_COLUMN_NODE            = ".search_filequality_column";
	protected static final String SEARCH_AVAILABILITY_COLUMN_NODE           = ".search_availability_column";
	protected static final String SEARCH_COMPLETESRC_COLUMN_NODE            = ".search_completsrc_column";
	protected static final String SEARCH_FILE_TYPE_COLUMN_NODE              = ".search_file_type_column";
	protected static final String SEARCH_FILE_ID_COLUMN_NODE                = ".search_file_id_column";
	
	protected static final String SHARED_LIST_FILE_NAME_COLUMN_NODE         = ".shared_list_file_name_column";
	protected static final String SHARED_LIST_FILE_SIZE_COLUMN_NODE         = ".shared_list_file_size_column";
	protected static final String SHARED_LIST_FILE_TYPE_COLUMN_NODE         = ".shared_list_file_type_column";
	protected static final String SHARED_LIST_FILE_ID_COLUMN_NODE           = ".shared_list_file_id_column";
	protected static final String SHARED_LIST_COMPLETED_COLUMN_NODE         = ".shared_list_completed_column";
	
	protected static final String NEW_DOWNLOAD_LIST_FILE_NAME_COLUMN_NODE	= ".new_download_list_file_name_column";
	protected static final String NEW_DOWNLOAD_LIST_FILE_SIZE_COLUMN_NODE	= ".new_download_list_file_size_column";
	protected static final String NEW_DOWNLOAD_LIST_FILE_ID_COLUMN_NODE		= ".new_download_list_file_id_column";

	protected static final String NEW_SERVER_LIST_SERVER_NAME_COLUMN_NODE	= ".new_server_list_name_column";
	protected static final String NEW_SERVER_LIST_SERVER_PORT_COLUMN_NODE	= ".new_server_list_port_column";
	
	protected static final String KAD_CLIENT_ID_COLUMN_NAME_NODE			= ".kad_client_id";
	protected static final String KAD_CLIENT_DISTANCE_COLUMN_NAME_NODE		= ".kad_client_distance";
	
	protected static final String KAD_TASK_TYPE_COLUMN_NAME_NODE			= ".kad_task_type";
	protected static final String KAD_TASK_LOOKUP_HASH_COLUMN_NAME_NODE		= ".kad_lookup_hash";
	protected static final String KAD_TASK_LOOKUP_INFO_COLUMN_NAME_NODE		= ".kad_lookup_info";

	protected static final String TOOL_BAR_NODE                             = ".tool_bar";
	protected static final String STATUS_BAR_NODE                           = ".status_bar";
	
	protected static final String PROMPT_ON_EXIT_NODE						= ".prompt_on_exit";
	protected static final String CHECK_UPDATE_AT_STARTUP_NODE				= ".startup_update_check";
	
	protected static final String NIGHTLY_BUILD_WARNING_NODE                = ".nightly_build_warning";
	protected static final String NIGHTLY_BUILD_WARNING_JM_VER_NODE         = ".nightly_build_warning_jm_ver";
	
	protected static final String CONNECT_AT_STARTUP_NODE                   = ".connect_at_startup";
	
	protected static final HashMap<String,Object> default_values = new HashMap<String,Object>();
	
	static {
		// default table column's visibility
		default_values.put(SERVER_LIST_NAME_COLUMN_ID + VISIBILITY,              true);
		default_values.put(SERVER_LIST_CC_COLUMN_ID + VISIBILITY,                true);
		default_values.put(SERVER_LIST_FLAG_COLUMN_ID + VISIBILITY,              true);
		default_values.put(SERVER_LIST_IP_COLUMN_ID + VISIBILITY,                true);
		default_values.put(SERVER_LIST_DESCRIPTION_COLUMN_ID + VISIBILITY,       true);
		default_values.put(SERVER_LIST_PING_COLUMN_ID + VISIBILITY,              true);
		default_values.put(SERVER_LIST_USERS_COLUMN_ID + VISIBILITY,             true);
		default_values.put(SERVER_LIST_MAX_USERS_COLUMN_ID + VISIBILITY,         true);
		default_values.put(SERVER_LIST_FILES_COLUMN_ID + VISIBILITY,             true);
		default_values.put(SERVER_LIST_SOFT_LIMIT_COLUMN_ID + VISIBILITY,        true);
		default_values.put(SERVER_LIST_HARD_LIMIT_COLUMN_ID + VISIBILITY,        true);
		default_values.put(SERVER_LIST_VERSION_COLUMN_ID + VISIBILITY,           true);
		default_values.put(SERVER_LIST_STATIC_COLUMN_ID + VISIBILITY,            true);
		default_values.put(SERVER_LIST_DOWN_COLUMN_ID + VISIBILITY,              true);
		
		default_values.put(DOWNLOAD_LIST_ORDER_ID + VISIBILITY,                  true);
		default_values.put(DOWNLOAD_LIST_FILE_NAME_COLUMN_ID + VISIBILITY,       true);
		default_values.put(DOWNLOAD_LIST_SIZE_COLUMN_ID + VISIBILITY,            true);
		default_values.put(DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID + VISIBILITY,     true);
		default_values.put(DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID + VISIBILITY,  true);
		default_values.put(DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID + VISIBILITY,    true);
		default_values.put(DOWNLOAD_LIST_PROGRESS_COLUMN_ID + VISIBILITY,        true);
		default_values.put(DOWNLOAD_LIST_PROGRESS_BAR_COLUMN_ID + VISIBILITY,    true);
		default_values.put(DOWNLOAD_LIST_COMPLETED_COLUMN_ID + VISIBILITY,       true);
		default_values.put(DOWNLOAD_LIST_SOURCES_COLUMN_ID + VISIBILITY,         true);
		default_values.put(DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID + VISIBILITY,true);
		default_values.put(DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID + VISIBILITY, true);
		default_values.put(DOWNLOAD_LIST_REMAINING_COLUMN_ID + VISIBILITY,       true);
		default_values.put(DOWNLOAD_LIST_STATUS_COLUMN_ID + VISIBILITY,          true);
		
		default_values.put(DOWNLOAD_PEER_LIST_IP_COLUMN_ID + VISIBILITY,         true);
		default_values.put(DOWNLOAD_PEER_LIST_SOURCE_COLUMN_ID + VISIBILITY,     true);
		default_values.put(DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID + VISIBILITY,     true);
		default_values.put(DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID + VISIBILITY,   true);
		default_values.put(DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID + VISIBILITY,   true);
		default_values.put(DOWNLOAD_PEER_LIST_DOWN_SPEED_COLUMN_ID + VISIBILITY, true);
		default_values.put(DOWNLOAD_PEER_LIST_UP_SPEED_COLUMN_ID + VISIBILITY,   true);
		default_values.put(DOWNLOAD_PEER_LIST_FLAG_COLUMN_ID + VISIBILITY,		 true);
		default_values.put(DOWNLOAD_PEER_LIST_CC_COLUMN_ID + VISIBILITY,		 true);
		
		default_values.put(UPLOAD_LIST_FILE_NAME_COLUMN_ID + VISIBILITY,         true);
		default_values.put(UPLOAD_LIST_FILE_SIZE_COLUMN_ID + VISIBILITY,         true);
		default_values.put(UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID + VISIBILITY,      true);
		default_values.put(UPLOAD_LIST_PEERS_COLUMN_ID + VISIBILITY,             true);
		default_values.put(UPLOAD_LIST_ETA_COLUMN_ID + VISIBILITY,               true);
		default_values.put(UPLOAD_LIST_UPLOADED_COLUMN_ID + VISIBILITY,          true);
		
		default_values.put(UPLOAD_PEER_LIST_IP_COLUMN_ID + VISIBILITY,           true);
		default_values.put(UPLOAD_PEER_LIST_CC_COLUMN_ID + VISIBILITY,  	     true);
		default_values.put(UPLOAD_PEER_LIST_FLAG_COLUMN_ID + VISIBILITY,         true);
		default_values.put(UPLOAD_PEER_LIST_STATUS_COLUMN_ID + VISIBILITY,       true);
		default_values.put(UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID + VISIBILITY,     true);
		default_values.put(UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID + VISIBILITY,     true);
		default_values.put(UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_ID + VISIBILITY, true);
		
		default_values.put(SEARCH_FILENAME_COLUMN_ID + VISIBILITY,               true);
		default_values.put(SEARCH_FILESIZE_COLUMN_ID + VISIBILITY,               true);
		default_values.put(SEARCH_FILEQUALITY_COLUMN_ID + VISIBILITY,            true);
		default_values.put(SEARCH_AVAILABILITY_COLUMN_ID + VISIBILITY,           true);
		default_values.put(SEARCH_COMPLETESRC_COLUMN_ID + VISIBILITY,            true);
		default_values.put(SEARCH_FILE_TYPE_COLUMN_ID + VISIBILITY,              true);
		default_values.put(SEARCH_FILE_ID_COLUMN_ID + VISIBILITY,                true);
		
		default_values.put(SHARED_LIST_FILE_NAME_COLUMN_ID + VISIBILITY,         true);
		default_values.put(SHARED_LIST_FILE_SIZE_COLUMN_ID + VISIBILITY,         true);
		default_values.put(SHARED_LIST_FILE_TYPE_COLUMN_ID + VISIBILITY,         true);
		default_values.put(SHARED_LIST_FILE_ID_COLUMN_ID + VISIBILITY,           true);
		default_values.put(SHARED_LIST_COMPLETED_COLUMN_ID + VISIBILITY,         true);

		default_values.put(NEW_DOWNLOAD_LIST_FILE_NAME_COLUMN_ID + VISIBILITY, 	 true);
		default_values.put(NEW_DOWNLOAD_LIST_FILE_SIZE_COLUMN_ID + VISIBILITY, 	 true);
		default_values.put(NEW_DOWNLOAD_LIST_FILE_ID_COLUMN_ID + VISIBILITY, 	 true);
		
		default_values.put(NEW_SERVER_LIST_NAME_COLUMN_ID + VISIBILITY, 	 	 true);
		default_values.put(NEW_SERVER_LIST_PORT_COLUMN_ID + VISIBILITY, 	 	 true);
		
		default_values.put(KAD_CLIENT_ID_COLUMN_ID + VISIBILITY, 			 	 true);
		default_values.put(KAD_CLIENT_DISTANCE_COLUMN_ID + VISIBILITY,	 	 	 true);
		
		default_values.put(KAD_TASK_TYPE_COLUMN_ID + VISIBILITY, 			 	 true);
		default_values.put(KAD_TASK_LOOKUP_HASH_COLUMN_ID + VISIBILITY, 	 	 true);
		default_values.put(KAD_TASK_LOOKUP_INFO_COLUMN_ID + VISIBILITY, 	 	 true);

		
		// default table column's order
		default_values.put(SERVER_LIST_NAME_COLUMN_ID + ORDER,               1);
		default_values.put(SERVER_LIST_CC_COLUMN_ID + ORDER,                 2);
        default_values.put(SERVER_LIST_FLAG_COLUMN_ID + ORDER,               3);
		default_values.put(SERVER_LIST_IP_COLUMN_ID + ORDER,                 4);
		default_values.put(SERVER_LIST_DESCRIPTION_COLUMN_ID + ORDER,        5);
		default_values.put(SERVER_LIST_PING_COLUMN_ID + ORDER,               6);
		default_values.put(SERVER_LIST_USERS_COLUMN_ID + ORDER,              7);
		default_values.put(SERVER_LIST_MAX_USERS_COLUMN_ID + ORDER,          8);
		default_values.put(SERVER_LIST_FILES_COLUMN_ID + ORDER,              9);
		default_values.put(SERVER_LIST_SOFT_LIMIT_COLUMN_ID + ORDER,         10);
		default_values.put(SERVER_LIST_HARD_LIMIT_COLUMN_ID + ORDER,         11);
		default_values.put(SERVER_LIST_VERSION_COLUMN_ID + ORDER,            12);
		default_values.put(SERVER_LIST_STATIC_COLUMN_ID + ORDER,             13);
		default_values.put(SERVER_LIST_DOWN_COLUMN_ID + ORDER,               14);
		
		default_values.put(DOWNLOAD_LIST_ORDER_ID + ORDER,                   100);
		default_values.put(DOWNLOAD_LIST_FILE_NAME_COLUMN_ID + ORDER,        1);
		default_values.put(DOWNLOAD_LIST_SIZE_COLUMN_ID + ORDER,             2);
		default_values.put(DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID + ORDER,      3);
		default_values.put(DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID + ORDER,   4);
		default_values.put(DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID + ORDER,     5);
		default_values.put(DOWNLOAD_LIST_PROGRESS_COLUMN_ID + ORDER,         6);
		default_values.put(DOWNLOAD_LIST_COMPLETED_COLUMN_ID + ORDER,        7);
		default_values.put(DOWNLOAD_LIST_SOURCES_COLUMN_ID + ORDER,          8);
		default_values.put(DOWNLOAD_LIST_REMAINING_COLUMN_ID + ORDER,        9);
		default_values.put(DOWNLOAD_LIST_STATUS_COLUMN_ID + ORDER,           10);
		default_values.put(DOWNLOAD_LIST_PROGRESS_BAR_COLUMN_ID + ORDER,     11);
		default_values.put(DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID + ORDER, 12);
		default_values.put(DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID + ORDER,  13);

		
		default_values.put(DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID + ORDER,    1);
		default_values.put(DOWNLOAD_PEER_LIST_CC_COLUMN_ID + ORDER,	         2);
		default_values.put(DOWNLOAD_PEER_LIST_FLAG_COLUMN_ID + ORDER,        3);
		default_values.put(DOWNLOAD_PEER_LIST_IP_COLUMN_ID + ORDER,          4);
		default_values.put(DOWNLOAD_PEER_LIST_SOURCE_COLUMN_ID + ORDER,      5);
		default_values.put(DOWNLOAD_PEER_LIST_DOWN_SPEED_COLUMN_ID + ORDER,  6);
		default_values.put(DOWNLOAD_PEER_LIST_UP_SPEED_COLUMN_ID + ORDER,    7);
		default_values.put(DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID + ORDER,    8);
		default_values.put(DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID + ORDER,      9);
		
		default_values.put(UPLOAD_LIST_FILE_NAME_COLUMN_ID + ORDER,          1);
		default_values.put(UPLOAD_LIST_FILE_SIZE_COLUMN_ID + ORDER,          2);
		default_values.put(UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID + ORDER,       3);
		default_values.put(UPLOAD_LIST_PEERS_COLUMN_ID + ORDER,              4);
		default_values.put(UPLOAD_LIST_ETA_COLUMN_ID + ORDER,                5);
		default_values.put(UPLOAD_LIST_UPLOADED_COLUMN_ID + ORDER,           6);
		
		default_values.put(UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID + ORDER,      1);
		default_values.put(UPLOAD_PEER_LIST_CC_COLUMN_ID + ORDER,            2);
		default_values.put(UPLOAD_PEER_LIST_FLAG_COLUMN_ID + ORDER,          3);
		default_values.put(UPLOAD_PEER_LIST_IP_COLUMN_ID + ORDER,            4);
		default_values.put(UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_ID + ORDER,  5);
		default_values.put(UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID + ORDER,      6);
		default_values.put(UPLOAD_PEER_LIST_STATUS_COLUMN_ID + ORDER,        7);
		
		default_values.put(SEARCH_FILEQUALITY_COLUMN_ID + ORDER,             1);
		default_values.put(SEARCH_FILENAME_COLUMN_ID + ORDER,                2);
		default_values.put(SEARCH_FILESIZE_COLUMN_ID + ORDER,                3);
		default_values.put(SEARCH_AVAILABILITY_COLUMN_ID + ORDER,            4);
		default_values.put(SEARCH_COMPLETESRC_COLUMN_ID + ORDER,             5);
		default_values.put(SEARCH_FILE_TYPE_COLUMN_ID + ORDER,               6);
		default_values.put(SEARCH_FILE_ID_COLUMN_ID + ORDER,                 7);
		
		default_values.put(SHARED_LIST_FILE_NAME_COLUMN_ID + ORDER,          1);
		default_values.put(SHARED_LIST_FILE_SIZE_COLUMN_ID + ORDER,          2);
		default_values.put(SHARED_LIST_FILE_TYPE_COLUMN_ID + ORDER,          3);
		default_values.put(SHARED_LIST_FILE_ID_COLUMN_ID + ORDER,            4);
		default_values.put(SHARED_LIST_COMPLETED_COLUMN_ID + ORDER,          5);
		
		default_values.put(NEW_DOWNLOAD_LIST_FILE_NAME_COLUMN_ID + ORDER,    1);
		default_values.put(NEW_DOWNLOAD_LIST_FILE_SIZE_COLUMN_ID + ORDER,    2);
		default_values.put(NEW_DOWNLOAD_LIST_FILE_ID_COLUMN_ID + ORDER,    	 3);
		
		default_values.put(NEW_SERVER_LIST_NAME_COLUMN_ID + ORDER, 	 		 1);
		default_values.put(NEW_SERVER_LIST_PORT_COLUMN_ID + ORDER, 	 		 2);
		
		default_values.put(KAD_CLIENT_ID_COLUMN_ID + ORDER, 			 	 1);
		default_values.put(KAD_CLIENT_DISTANCE_COLUMN_ID + ORDER,	 	 	 2);
		
		default_values.put(KAD_TASK_TYPE_COLUMN_ID + ORDER, 			 	 1);
		default_values.put(KAD_TASK_LOOKUP_HASH_COLUMN_ID + ORDER, 	 	 	 2);
		default_values.put(KAD_TASK_LOOKUP_INFO_COLUMN_ID + ORDER, 	 	 	 3);
		
		default_values.put(SERVER_LIST_NAME_COLUMN_ID + WIDTH,              150);
		default_values.put(SERVER_LIST_CC_COLUMN_ID + WIDTH,                 40);
		default_values.put(SERVER_LIST_FLAG_COLUMN_ID + WIDTH,               40);
		default_values.put(SERVER_LIST_IP_COLUMN_ID + WIDTH,                150);
		default_values.put(SERVER_LIST_DESCRIPTION_COLUMN_ID + WIDTH,       150);
		default_values.put(SERVER_LIST_PING_COLUMN_ID + WIDTH,              100);
		default_values.put(SERVER_LIST_USERS_COLUMN_ID + WIDTH,             150);
		default_values.put(SERVER_LIST_MAX_USERS_COLUMN_ID + WIDTH,         150);
		default_values.put(SERVER_LIST_FILES_COLUMN_ID + WIDTH,             150);
		default_values.put(SERVER_LIST_SOFT_LIMIT_COLUMN_ID + WIDTH,        150);
		default_values.put(SERVER_LIST_HARD_LIMIT_COLUMN_ID + WIDTH,        150);
		default_values.put(SERVER_LIST_VERSION_COLUMN_ID + WIDTH,           150);
		default_values.put(SERVER_LIST_STATIC_COLUMN_ID + WIDTH,            40);
		
		default_values.put(DOWNLOAD_LIST_ORDER_ID + WIDTH,                   10);
		default_values.put(DOWNLOAD_LIST_FILE_NAME_COLUMN_ID + WIDTH,        100);
		default_values.put(DOWNLOAD_LIST_SIZE_COLUMN_ID + WIDTH,             50);
		default_values.put(DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID + WIDTH,      100);
		default_values.put(DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID + WIDTH,   100);
		default_values.put(DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID + WIDTH,     100);
		default_values.put(DOWNLOAD_LIST_PROGRESS_COLUMN_ID + WIDTH,         100);
		default_values.put(DOWNLOAD_LIST_COMPLETED_COLUMN_ID + WIDTH,        100);
		default_values.put(DOWNLOAD_LIST_SOURCES_COLUMN_ID + WIDTH,          100);
		default_values.put(DOWNLOAD_LIST_REMAINING_COLUMN_ID + WIDTH,        100);
		default_values.put(DOWNLOAD_LIST_STATUS_COLUMN_ID + WIDTH,           100);
		default_values.put(DOWNLOAD_LIST_PROGRESS_BAR_COLUMN_ID + WIDTH,     200);
		default_values.put(DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID + WIDTH, 50);
		default_values.put(DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID + WIDTH,  50);
		
		default_values.put(DOWNLOAD_PEER_LIST_IP_COLUMN_ID + WIDTH,          150);
		default_values.put(DOWNLOAD_PEER_LIST_SOURCE_COLUMN_ID + WIDTH,      100);
		default_values.put(DOWNLOAD_PEER_LIST_CC_COLUMN_ID + WIDTH,	          50);
		default_values.put(DOWNLOAD_PEER_LIST_FLAG_COLUMN_ID + WIDTH,         50);
		default_values.put(DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID + WIDTH,      150);
		default_values.put(DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID + WIDTH,    100);
		default_values.put(DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID + WIDTH,    100);
		default_values.put(DOWNLOAD_PEER_LIST_DOWN_SPEED_COLUMN_ID + WIDTH,  100);
		default_values.put(DOWNLOAD_PEER_LIST_UP_SPEED_COLUMN_ID + WIDTH,    100);
		
		default_values.put(UPLOAD_LIST_FILE_NAME_COLUMN_ID + WIDTH,          100);
		default_values.put(UPLOAD_LIST_FILE_SIZE_COLUMN_ID + WIDTH,          100);
		default_values.put(UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID + WIDTH,       100);
		default_values.put(UPLOAD_LIST_PEERS_COLUMN_ID + WIDTH,              100);
		default_values.put(UPLOAD_LIST_ETA_COLUMN_ID + WIDTH,                100);
		default_values.put(UPLOAD_LIST_UPLOADED_COLUMN_ID + WIDTH,           100);
		
		default_values.put(UPLOAD_PEER_LIST_IP_COLUMN_ID + WIDTH,            150);
		default_values.put(UPLOAD_PEER_LIST_CC_COLUMN_ID + WIDTH,            50);
		default_values.put(UPLOAD_PEER_LIST_FLAG_COLUMN_ID + WIDTH,          50);
		default_values.put(UPLOAD_PEER_LIST_STATUS_COLUMN_ID + WIDTH,        150);
		default_values.put(UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID + WIDTH,      100);
		default_values.put(UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID + WIDTH,      100);
		default_values.put(UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_ID + WIDTH,  100);
		
		default_values.put(SEARCH_FILENAME_COLUMN_ID + WIDTH,                150);
		default_values.put(SEARCH_FILESIZE_COLUMN_ID + WIDTH,                150);
		default_values.put(SEARCH_FILEQUALITY_COLUMN_ID + WIDTH,              20);
		default_values.put(SEARCH_AVAILABILITY_COLUMN_ID + WIDTH,            150);
		default_values.put(SEARCH_COMPLETESRC_COLUMN_ID + WIDTH,             150);
		default_values.put(SEARCH_FILE_TYPE_COLUMN_ID + WIDTH,               159);
		default_values.put(SEARCH_FILE_ID_COLUMN_ID + WIDTH,                 150);
		
		default_values.put(SHARED_LIST_FILE_NAME_COLUMN_ID + WIDTH,          150);
		default_values.put(SHARED_LIST_FILE_SIZE_COLUMN_ID + WIDTH,          150);
		default_values.put(SHARED_LIST_FILE_TYPE_COLUMN_ID + WIDTH,          150);
		default_values.put(SHARED_LIST_FILE_ID_COLUMN_ID + WIDTH,            250);
		default_values.put(SHARED_LIST_COMPLETED_COLUMN_ID + WIDTH,          60);
		
		default_values.put(NEW_DOWNLOAD_LIST_FILE_NAME_COLUMN_ID + WIDTH, 	 150);
		default_values.put(NEW_DOWNLOAD_LIST_FILE_SIZE_COLUMN_ID + WIDTH, 	 100);
		default_values.put(NEW_DOWNLOAD_LIST_FILE_ID_COLUMN_ID + WIDTH, 	 150);

		default_values.put(NEW_SERVER_LIST_NAME_COLUMN_ID + WIDTH, 	 		 150);
		default_values.put(NEW_SERVER_LIST_PORT_COLUMN_ID + WIDTH, 	 		 150);
		
		default_values.put(KAD_CLIENT_ID_COLUMN_ID + WIDTH, 			 	 300);
		default_values.put(KAD_CLIENT_DISTANCE_COLUMN_ID + WIDTH,	 	 	 500);
		
		default_values.put(KAD_TASK_TYPE_COLUMN_ID + WIDTH, 			 	 300);
		default_values.put(KAD_TASK_LOOKUP_HASH_COLUMN_ID + WIDTH, 	 	 	 500);
		default_values.put(KAD_TASK_LOOKUP_INFO_COLUMN_ID + WIDTH, 	 	 	 400);
		
		default_values.put(TOOL_BAR_ID + VISIBILITY,                         true);
		default_values.put(STATUS_BAR_ID + VISIBILITY,                       true);
		
		default_values.put(PROMPT_ON_EXIT_ID + ENABLED,						 true);
		default_values.put(CHECK_UPDATE_AT_STARTUP_ID + ENABLED,			 true);
		
		default_values.put(NIGHTLY_BUILD_WARNING_ID + ENABLED,               true);
		default_values.put(NIGHTLY_BUILD_WARNING_JM_VER_ID + VERSION,        JMConstants.DEV_VERSION);
		
		default_values.put(CONNECT_AT_STARTUP_ID + ENABLED,                  false);
		
	}
	
	protected static String getColumnNodeById(int ColumnID) {
		
		switch(ColumnID) {
		
		  	case SERVER_LIST_NAME_COLUMN_ID             :  return SERVER_LIST_NAME_NODE;
		  	case SERVER_LIST_CC_COLUMN_ID               :  return SERVER_LIST_CC_NODE;
		  	case SERVER_LIST_FLAG_COLUMN_ID				:  return SERVER_LIST_FLAG_NODE;
		  	case SERVER_LIST_IP_COLUMN_ID               :  return SERVER_LIST_IP_NODE;
		  	case SERVER_LIST_DESCRIPTION_COLUMN_ID      :  return SERVER_LIST_DESCRIPTION_NODE;
		  	case SERVER_LIST_PING_COLUMN_ID             :  return SERVER_LIST_PING_NODE;
		  	case SERVER_LIST_USERS_COLUMN_ID            :  return SERVER_LIST_USERS_NODE;
		  	case SERVER_LIST_MAX_USERS_COLUMN_ID        :  return SERVER_LIST_MAX_USERS_NODE;
		  	case SERVER_LIST_FILES_COLUMN_ID            :  return SERVER_LIST_FILES_NODE;
		  	case SERVER_LIST_SOFT_LIMIT_COLUMN_ID       :  return SERVER_LIST_SOFT_LIMIT_NODE;
		  	case SERVER_LIST_HARD_LIMIT_COLUMN_ID       :  return SERVER_LIST_HARD_LIMIT_NODE;
		  	case SERVER_LIST_VERSION_COLUMN_ID          :  return SERVER_LIST_VERSION_NODE;
		  	case SERVER_LIST_STATIC_COLUMN_ID           :  return SERVER_LIST_STATIC_NODE;
		  	case SERVER_LIST_DOWN_COLUMN_ID             :  return SERVER_LIST_DOWN_NODE;
		  	
		  	case DOWNLOAD_LIST_ORDER_ID                 :  return DOWNLOAD_LIST_ORDER_NODE;
		  	case DOWNLOAD_LIST_FILE_NAME_COLUMN_ID      :  return DOWNLOAD_LIST_FILE_NAME_COLUMN_NODE;
		  	case DOWNLOAD_LIST_SIZE_COLUMN_ID           :  return DOWNLOAD_LIST_SIZE_COLUMN_NODE;
		  	case DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID    :  return DOWNLOAD_LIST_TRANSFERRED_COLUMN_NODE;
		  	case DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID :  return DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_NODE;
		  	case DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID   :  return DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_NODE;
		  	case DOWNLOAD_LIST_PROGRESS_COLUMN_ID       :  return DOWNLOAD_LIST_PROGRESS_COLUMN_NODE;
		  	case DOWNLOAD_LIST_COMPLETED_COLUMN_ID      :  return DOWNLOAD_LIST_COMPLETED_COLUMN_NODE;
		  	case DOWNLOAD_LIST_SOURCES_COLUMN_ID        :  return DOWNLOAD_LIST_SOURCES_COLUMN_NODE;
		  	case DOWNLOAD_LIST_REMAINING_COLUMN_ID      :  return DOWNLOAD_LIST_REMAINING_COLUMN_NODE;	
		  	case DOWNLOAD_LIST_STATUS_COLUMN_ID         :  return DOWNLOAD_LIST_STATUS_COLUMN_NODE;
		  	case DOWNLOAD_LIST_PROGRESS_BAR_COLUMN_ID   :  return DOWNLOAD_LIST_PROGRESS_BAR_COLUMN_NODE;
		  	case DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID: return DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_NODE;
		  	case DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID : return DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_NODE; 
		  		
		  	case DOWNLOAD_PEER_LIST_IP_COLUMN_ID        :  return DOWNLOAD_PEER_LIST_IP_COLUMN_NODE;
		  	case DOWNLOAD_PEER_LIST_SOURCE_COLUMN_ID    :  return DOWNLOAD_PEER_LIST_SOURCE_COLUMN_NODE;
		  	case DOWNLOAD_PEER_LIST_CC_COLUMN_ID        :  return DOWNLOAD_PEER_LIST_CC_COLUMN_NODE;
		  	case DOWNLOAD_PEER_LIST_FLAG_COLUMN_ID      :  return DOWNLOAD_PEER_LIST_FLAG_COLUMN_NODE;
		  	case DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID    :  return DOWNLOAD_PEER_LIST_STATUS_COLUMN_NODE;
		  	case DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID  :  return DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_NODE;
		  	case DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID  :  return DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_NODE;
		  	case DOWNLOAD_PEER_LIST_DOWN_SPEED_COLUMN_ID:  return DOWNLOAD_PEER_LIST_DOWN_SPEED_COLUMN_NODE;
		  	case DOWNLOAD_PEER_LIST_UP_SPEED_COLUMN_ID  :  return DOWNLOAD_PEER_LIST_UP_SPEED_COLUMN_NODE;
		  		
		  	case UPLOAD_LIST_FILE_NAME_COLUMN_ID        :  return UPLOAD_LIST_FILENAME_COLUMN_NODE;
		  	case UPLOAD_LIST_FILE_SIZE_COLUMN_ID        :  return UPLOAD_LIST_FILESIZE_COLUMN_NODE;
		  	case UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID     :  return UPLOAD_LIST_UPLOADSPEED_COLUMN_NODE;
		  	case UPLOAD_LIST_PEERS_COLUMN_ID            :  return UPLOAD_LIST_PEERS_COLUMN_NODE;
		  	case UPLOAD_LIST_ETA_COLUMN_ID              :  return UPLOAD_LIST_ETA_COLUMN_NODE;
		  	case UPLOAD_LIST_UPLOADED_COLUMN_ID         :  return UPLOAD_LIST_UPLOADED_COLUMN_NODE;
		  		
		  	case UPLOAD_PEER_LIST_IP_COLUMN_ID          :  return UPLOAD_PEER_LIST_IP_COLUMN_NODE;
		  	case UPLOAD_PEER_LIST_CC_COLUMN_ID          :  return UPLOAD_PEER_LIST_CC_COLUMN_NODE;
		  	case UPLOAD_PEER_LIST_FLAG_COLUMN_ID        :  return UPLOAD_PEER_LIST_FLAG_COLUMN_NODE;
		  	case UPLOAD_PEER_LIST_STATUS_COLUMN_ID      :  return UPLOAD_PEER_LIST_STATUS_COLUMN_NODE;
		  	case UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID    :  return UPLOAD_PEER_LIST_NICKNAME_COLUMN_NODE;
		  	case UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID    :  return UPLOAD_PEER_LIST_SOFTWARE_COLUMN_NODE;
		  	case UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_ID:  return UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_NODE;
		  	
		  	case SEARCH_FILENAME_COLUMN_ID              :  return SEARCH_FILENAME_COLUMN_NODE;
		  	case SEARCH_FILESIZE_COLUMN_ID              :  return SEARCH_FILESIZE_COLUMN_NODE;
		  	case SEARCH_FILEQUALITY_COLUMN_ID           :  return SEARCH_FILEQUALITY_COLUMN_NODE;
		  	case SEARCH_AVAILABILITY_COLUMN_ID          :  return SEARCH_AVAILABILITY_COLUMN_NODE;
		  	case SEARCH_COMPLETESRC_COLUMN_ID           :  return SEARCH_COMPLETESRC_COLUMN_NODE;
		  	case SEARCH_FILE_TYPE_COLUMN_ID             :  return SEARCH_FILE_TYPE_COLUMN_NODE;
		  	case SEARCH_FILE_ID_COLUMN_ID               :  return SEARCH_FILE_ID_COLUMN_NODE;
		  		
		  	case SHARED_LIST_FILE_NAME_COLUMN_ID        :  return SHARED_LIST_FILE_NAME_COLUMN_NODE;
		  	case SHARED_LIST_FILE_SIZE_COLUMN_ID        :  return SHARED_LIST_FILE_SIZE_COLUMN_NODE;
		  	case SHARED_LIST_FILE_TYPE_COLUMN_ID        :  return SHARED_LIST_FILE_TYPE_COLUMN_NODE;
		  	case SHARED_LIST_FILE_ID_COLUMN_ID          :  return SHARED_LIST_FILE_ID_COLUMN_NODE;
		  	case SHARED_LIST_COMPLETED_COLUMN_ID        :  return SHARED_LIST_COMPLETED_COLUMN_NODE;
		  	 
		  	case NEW_DOWNLOAD_LIST_FILE_ID_COLUMN_ID  	:  return NEW_DOWNLOAD_LIST_FILE_ID_COLUMN_NODE;
		  	case NEW_DOWNLOAD_LIST_FILE_NAME_COLUMN_ID  :  return NEW_DOWNLOAD_LIST_FILE_NAME_COLUMN_NODE;
		  	case NEW_DOWNLOAD_LIST_FILE_SIZE_COLUMN_ID  :  return NEW_DOWNLOAD_LIST_FILE_SIZE_COLUMN_NODE;

		  	case NEW_SERVER_LIST_NAME_COLUMN_ID 		:  return NEW_SERVER_LIST_SERVER_NAME_COLUMN_NODE;
		  	case NEW_SERVER_LIST_PORT_COLUMN_ID 		:  return NEW_SERVER_LIST_SERVER_PORT_COLUMN_NODE;
		  	
		  	case KAD_CLIENT_ID_COLUMN_ID 				:  return KAD_CLIENT_ID_COLUMN_NAME_NODE; 
		  	case KAD_CLIENT_DISTANCE_COLUMN_ID			:  return KAD_CLIENT_ID_COLUMN_NAME_NODE;
		  	
		  	case KAD_TASK_TYPE_COLUMN_ID				:  return KAD_TASK_TYPE_COLUMN_NAME_NODE;
		  	case KAD_TASK_LOOKUP_HASH_COLUMN_ID			:  return KAD_TASK_LOOKUP_HASH_COLUMN_NAME_NODE;
		  	case KAD_TASK_LOOKUP_INFO_COLUMN_ID			:  return KAD_TASK_LOOKUP_INFO_COLUMN_NAME_NODE;
		  	
		  	
		  	//case NIGHTLY_BUILD_WARNING_ID               :  return NIGHTLY_BUILD_WARNING_NODE;
		  	//case NIGHTLY_BUILD_WARNING_JM_VER_ID        :  return NIGHTLY_BUILD_WARNING_JM_VER_NODE;
		  	
		}
		
		return "unknown column ID";
	}
	
	//protected static String getToolBarNode() {
		
	//	return TOOL_BAR_NODE;
	//}
	
	//protected static String getStatusBarNode() {
		
	//	return STATUS_BAR_NODE;
	///}
	
	// ToolBar methods
	
	protected static String getToolBarNodePath(String variablePath) {
		
		return UI_ROOT + variablePath + TOOL_BAR_NODE;
	}
	
	protected static boolean getToolBarDefaultVisibility() {
		
		return Boolean.parseBoolean(default_values.get(TOOL_BAR_ID + VISIBILITY).toString());
	}
	
	// end ToolBar methods
	
	// StatusBar methods
	
	protected static String getStatusBarNodePath(String variablePath) {
		
		return UI_ROOT + variablePath + STATUS_BAR_NODE;
	}
	
	protected static boolean getStatusBarDefaultVisibility() {
		
		return Boolean.parseBoolean(default_values.get(STATUS_BAR_ID + VISIBILITY).toString());
	}
	
	// end StatusBar methods
	
	// PromptOnExit methods
	
	protected static boolean getDefaultPromptOnExit() {
		
		return Boolean.parseBoolean(default_values.get(PROMPT_ON_EXIT_ID + ENABLED).toString());
	}
	
	protected static String getPromptOnExitNodePath(String variablePath) {
		
		return UI_ROOT + variablePath + PROMPT_ON_EXIT_NODE;
	}
	
	// end PromptOnExit methods
	
	// StartupCheckUpdate methods
	
	protected static boolean getDefaultStartupCheckUpdate() {
		
		return Boolean.parseBoolean(default_values.get(CHECK_UPDATE_AT_STARTUP_ID + ENABLED).toString());
	}
	
	protected static String getStartupCheckUpdateNodePath(String variablePath) {
		
		return UI_ROOT + variablePath + CHECK_UPDATE_AT_STARTUP_NODE;
	}
	
	// end StartupCheckUpdate methods
	
	// NightlyBuildWarning methods
	
	protected static String getNightlyBuildWarningNodePath(String variablePath) {
		
		return UI_ROOT + variablePath + NIGHTLY_BUILD_WARNING_NODE;
	}
	
	protected static String getNightlyBuildWarningJMVerNodePath(String variablePath) {
		
		return UI_ROOT + variablePath + NIGHTLY_BUILD_WARNING_JM_VER_NODE;
	}
	
	protected static boolean getDefaultNightlyBuildWarningEnabled() {
		
		return Boolean.parseBoolean(default_values.get(NIGHTLY_BUILD_WARNING_ID + ENABLED).toString());
	}
	
	protected static String getDefaultNightlyBuildWarningJMVer() {
		
		return default_values.get(NIGHTLY_BUILD_WARNING_JM_VER_ID + VERSION).toString();
	}
	
	// end NightlyBuildWarning methods
	
	// Connect at startup methods
	
	protected static boolean getDefaultConnectAtStartup() {
		
		return Boolean.parseBoolean(default_values.get(CONNECT_AT_STARTUP_ID + ENABLED).toString());
	}
	
	protected static String getConnectAtStartupNodePath(String variablePath) {
		
		return UI_ROOT + variablePath + CONNECT_AT_STARTUP_NODE;
	}
	
	// end connect at startup methods
	
	// Column methods
	
	protected static String getColumnNodePath(String variablePath, int columnID) {
		
		return UI_ROOT + variablePath + getColumnNodeById(columnID);
	}
	
	public static int getDefaultColumnOrder(int columnID) {
		
		return Integer.parseInt(default_values.get(columnID + ORDER).toString());
	}
	
	protected static boolean getDefaultColumnVisibility(int columnID) {
		
		return Boolean.parseBoolean(default_values.get(columnID + VISIBILITY).toString());
	}
	
	protected static int getDefaultColumnWidth(int columnID) {
		
		return Integer.parseInt(default_values.get(columnID + WIDTH).toString());
	}
	
	// end column methods

	private static Map<String,String> icon_name_by_extension = new Hashtable<String,String>();

	static {
		icon_name_by_extension.put("rar","mimetypes/archive.png");
		icon_name_by_extension.put("zip","mimetypes/archive.png");
		icon_name_by_extension.put("tar","mimetypes/archive.png");
		icon_name_by_extension.put("gz","mimetypes/archive.png");
		icon_name_by_extension.put("7z","mimetypes/archive.png");
		
		icon_name_by_extension.put("mp3","mimetypes/audio.png");
		icon_name_by_extension.put("wav","mimetypes/audio.png");
		icon_name_by_extension.put("midi","mimetypes/audio.png");
		
		icon_name_by_extension.put("iso","mimetypes/cd_image.png");
		icon_name_by_extension.put("nrg","mimetypes/cd_image.png");
		icon_name_by_extension.put("cue","mimetypes/cd_image.png");
		
		icon_name_by_extension.put("png","mimetypes/image.png");
		icon_name_by_extension.put("jpg","mimetypes/image.png");
		icon_name_by_extension.put("jpeg","mimetypes/image.png");
		icon_name_by_extension.put("gif","mimetypes/image.png");
		icon_name_by_extension.put("bmp","mimetypes/image.png");
		icon_name_by_extension.put("ico","mimetypes/image.png");
		
		icon_name_by_extension.put("pdf","mimetypes/pdf.png");
		
		icon_name_by_extension.put("doc","mimetypes/doc.png");
		icon_name_by_extension.put("wri","mimetypes/doc.png");
		icon_name_by_extension.put("odt","mimetypes/doc.png");
		icon_name_by_extension.put("sxw","mimetypes/doc.png");
		icon_name_by_extension.put("vor","mimetypes/doc.png");
		
		icon_name_by_extension.put("xls","mimetypes/calc.png");
		
		icon_name_by_extension.put("exe", "mimetypes/windows_exe.png");
		icon_name_by_extension.put("com", "mimetypes/windows_exe.png");
		icon_name_by_extension.put("bat", "mimetypes/windows_exe.png");
		icon_name_by_extension.put("cmd", "mimetypes/windows_exe.png");
		
		icon_name_by_extension.put("so", "mimetypes/executable.png");
		icon_name_by_extension.put("bin", "mimetypes/executable.png");
		icon_name_by_extension.put("sh", "mimetypes/executable.png");
		
		icon_name_by_extension.put("mpg", "mimetypes/video.png");
		icon_name_by_extension.put("mpeg", "mimetypes/video.png");
		icon_name_by_extension.put("avi", "mimetypes/video.png");
		icon_name_by_extension.put("wmv", "mimetypes/video.png");
		icon_name_by_extension.put("bik", "mimetypes/video.png");
		icon_name_by_extension.put("mov", "mimetypes/video.png");
	}
	
	public static final String INFINITY_STRING	= "\u221E"; // "oo";
	public static final int    INFINITY_AS_INT = 31536000; // seconds (365days)
	
	public static InputStream getIconByExtension(String extension) {
		extension = extension.toLowerCase();
		String image_path = icon_name_by_extension.get(extension);
		if (image_path == null)
			image_path = "mimetypes/default.png";
		return UIImageRepository.getImageAsStream(image_path);
	}
	
	public static URL getMimeURLByExtension(String extension) {
		extension = extension.toLowerCase();
		URL url = null;
		
		String file_name = icon_name_by_extension.get(extension);
		if(file_name != null)
		   url = UIConstants.class.
		           getClassLoader().
		             getResource("org/jmule/ui/resources/" + icon_name_by_extension.get(extension));
		else
		   url = UIConstants.class.
	           getClassLoader().
	             getResource("org/jmule/ui/resources/mimetypes/default.png");
		
		return url;
	}
	
	public static final String GNU_LICENSE = "		    GNU GENERAL PUBLIC LICENSE"+
    "\n		       Version 2, June 1991"+
    "\n"+
    "\n Copyright (C) 1989, 1991 Free Software Foundation, " +
    "\nInc. 59 Temple Place,Suite 330, Boston, MA  02111-1307  USA"+
    "\n Everyone is permitted to copy and distribute verbatim copies"+
    "\n of this license document, but changing it is not allowed."+
    "\n"+
    "\n			    Preamble"+
    "\n"+
    "\n  The licenses for most software are designed to take away your"+
    "\nfreedom to share and change it.  By contrast, the GNU General Public"+
    "\nLicense is intended to guarantee your freedom to share and change free"+
    "\nsoftware--to make sure the software is free for all its users.  This"+
    "\nGeneral Public License applies to most of the Free Software"+
    "\nFoundation's software and to any other program whose authors commit to"+
    "\nusing it.  (Some other Free Software Foundation software is covered by"+
    "\nthe GNU Library General Public License instead.)  You can apply it to"+
    "\nyour programs, too."+
    "\n"+
    "\n  When we speak of free software, we are referring to freedom, not"+
    "\nprice.  Our General Public Licenses are designed to make sure that you"+
    "\nhave the freedom to distribute copies of free software (and charge for"+
    "\nthis service if you wish), that you receive source code or can get it"+
    "\nif you want it, that you can change the software or use pieces of it"+
    "\nin new free programs; and that you know you can do these things."+
    "\n"+
    "\n  To protect your rights, we need to make restrictions that forbid"+
    "\nanyone to deny you these rights or to ask you to surrender the rights."+
    "\nThese restrictions translate to certain responsibilities for you if you"+
    "\ndistribute copies of the software, or if you modify it."+
    "\n"+
    "\n  For example, if you distribute copies of such a program, whether"+
    "\ngratis or for a fee, you must give the recipients all the rights that"+
    "\nyou have.  You must make sure that they, too, receive or can get the"+
    "\nsource code.  And you must show them these terms so they know their"+
    "\nrights."+
    "\n"+
    "\n  We protect your rights with two steps: (1) copyright the software, and"+
    "\n(2) offer you this license which gives you legal permission to copy,"+
    "\ndistribute and/or modify the software."+
    "\n"+
    "\n  Also, for each author's protection and ours, we want to make certain"+
    "\nthat everyone understands that there is no warranty for this free"+
    "\nsoftware.  If the software is modified by someone else and passed on, we"+
    "\nwant its recipients to know that what they have is not the original, so"+
    "\nthat any problems introduced by others will not reflect on the original"+
    "\nauthors' reputations."+
    "\n"+
    "\n  Finally, any free program is threatened constantly by software"+
    "\npatents.  We wish to avoid the danger that redistributors of a free"+
    "\nprogram will individually obtain patent licenses, in effect making the"+
    "\nprogram proprietary.  To prevent this, we have made it clear that any"+
    "\npatent must be licensed for everyone's free use or not licensed at all."+
    "\n"+
    "\n  The precise terms and conditions for copying, distribution and"+
    "\nmodification follow."+
    "\n"+
    "\n		    GNU GENERAL PUBLIC LICENSE"+
    "\n   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION"+
    "\n"+
    "\n  0. This License applies to any program or other work which contains"+
    "\na notice placed by the copyright holder saying it may be distributed"+
    "\nunder the terms of this General Public License.  The \"Program\", below,"+
    "\nrefers to any such program or work, and a \"work based on the Program\""+
    "\nmeans either the Program or any derivative work under copyright law:"+
    "\nthat is to say, a work containing the Program or a portion of it,"+
    "\neither verbatim or with modifications and/or translated into another"+
    "\nlanguage.  (Hereinafter, translation is included without limitation in"+
    "\nthe term \"modification\".)  Each licensee is addressed as \"you\"."+
    "\n"+
    "\nActivities other than copying, distribution and modification are not"+
    "\ncovered by this License; they are outside its scope.  The act of"+
    "\nrunning the Program is not restricted, and the output from the Program"+
    "\nis covered only if its contents constitute a work based on the"+
    "\nProgram (independent of having been made by running the Program)."+
    "\nWhether that is true depends on what the Program does."+
    "\n"+
    "\n  1. You may copy and distribute verbatim copies of the Program's"+
    "\nsource code as you receive it, in any medium, provided that you"+
    "\nconspicuously and appropriately publish on each copy an appropriate"+
    "\ncopyright notice and disclaimer of warranty; keep intact all the"+
    "\nnotices that refer to this License and to the absence of any warranty;"+
    "\nand give any other recipients of the Program a copy of this License"+
    "\nalong with the Program."+
    "\n"+
    "\nYou may charge a fee for the physical act of transferring a copy, and"+
    "\nyou may at your option offer warranty protection in exchange for a fee."+
    "\n"+
    "\n  2. You may modify your copy or copies of the Program or any portion"+
    "\nof it, thus forming a work based on the Program, and copy and"+
    "\ndistribute such modifications or work under the terms of Section 1"+
    "\nabove, provided that you also meet all of these conditions:"+
    "\n"+
    "\n    a) You must cause the modified files to carry prominent notices"+
    "\n    stating that you changed the files and the date of any change."+
    "\n"+
    "\n    b) You must cause any work that you distribute or publish, that in"+
    "\n    whole or in part contains or is derived from the Program or any"+
    "\n    part thereof, to be licensed as a whole at no charge to all third"+
    "\n    parties under the terms of this License."+
    "\n"+
    "\n    c) If the modified program normally reads commands interactively"+
    "\n    when run, you must cause it, when started running for such"+
    "\n    interactive use in the most ordinary way, to print or display an"+
    "\n    announcement including an appropriate copyright notice and a"+
    "\n    notice that there is no warranty (or else, saying that you provide"+
    "\n    a warranty) and that users may redistribute the program under"+
    "\n    these conditions, and telling the user how to view a copy of this"+
    "\n    License.  (Exception: if the Program itself is interactive but"+
    "\n    does not normally print such an announcement, your work based on"+
    "\n    the Program is not required to print an announcement.)"+
    "\n"+
    "\nThese requirements apply to the modified work as a whole.  If"+
    "\nidentifiable sections of that work are not derived from the Program,"+
    "\nand can be reasonably considered independent and separate works in"+
    "\nthemselves, then this License, and its terms, do not apply to those"+
    "\nsections when you distribute them as separate works.  But when you"+
    "\ndistribute the same sections as part of a whole which is a work based"+
    "\non the Program, the distribution of the whole must be on the terms of"+
    "\nthis License, whose permissions for other licensees extend to the"+
    "\nentire whole, and thus to each and every part regardless of who wrote it."+
    "\n"+
    "\nThus, it is not the intent of this section to claim rights or contest"+
    "\nyour rights to work written entirely by you; rather, the intent is to"+
    "\nexercise the right to control the distribution of derivative or"+
    "\ncollective works based on the Program."+
    "\n"+
    "\nIn addition, mere aggregation of another work not based on the Program"+
    "\nwith the Program (or with a work based on the Program) on a volume of"+
    "\na storage or distribution medium does not bring the other work under"+
    "\nthe scope of this License."+
    "\n"+
    "\n  3. You may copy and distribute the Program (or a work based on it,"+
    "\nunder Section 2) in object code or executable form under the terms of"+
    "\nSections 1 and 2 above provided that you also do one of the following:"+
    "\n"+
    "\n    a) Accompany it with the complete corresponding machine-readable"+
    "\n    source code, which must be distributed under the terms of Sections"+
    "\n    1 and 2 above on a medium customarily used for software interchange; or,"+
    "\n"+
    "\n    b) Accompany it with a written offer, valid for at least three"+
    "\n    years, to give any third party, for a charge no more than your"+
    "\n    cost of physically performing source distribution, a complete"+
    "\n    machine-readable copy of the corresponding source code, to be"+
    "\n    distributed under the terms of Sections 1 and 2 above on a medium"+
    "\n    customarily used for software interchange; or,"+
    "\n"+
    "\n    c) Accompany it with the information you received as to the offer"+
    "\n    to distribute corresponding source code.  (This alternative is"+
    "\n    allowed only for noncommercial distribution and only if you"+
    "\n    received the program in object code or executable form with such"+
    "\n    an offer, in accord with Subsection b above.)"+
    "\n"+
    "\nThe source code for a work means the preferred form of the work for"+
    "\nmaking modifications to it.  For an executable work, complete source"+
    "\ncode means all the source code for all modules it contains, plus any"+
    "\nassociated interface definition files, plus the scripts used to"+
    "\ncontrol compilation and installation of the executable.  However, as a"+
    "\nspecial exception, the source code distributed need not include"+
    "\nanything that is normally distributed (in either source or binary"+
    "\nform) with the major components (compiler, kernel, and so on) of the"+
    "\noperating system on which the executable runs, unless that component"+
    "\nitself accompanies the executable."+
    "\n"+
    "\nIf distribution of executable or object code is made by offering"+
    "\naccess to copy from a designated place, then offering equivalent"+
    "\naccess to copy the source code from the same place counts as"+
    "\ndistribution of the source code, even though third parties are not"+
    "\ncompelled to copy the source along with the object code."+
    "\n"+
    "\n  4. You may not copy, modify, sublicense, or distribute the Program"+
    "\nexcept as expressly provided under this License.  Any attempt"+
    "\notherwise to copy, modify, sublicense or distribute the Program is"+
    "\nvoid, and will automatically terminate your rights under this License."+
    "\nHowever, parties who have received copies, or rights, from you under"+
    "\nthis License will not have their licenses terminated so long as such"+
    "\nparties remain in full compliance."+
    "\n"+
    "\n  5. You are not required to accept this License, since you have not"+
    "\nsigned it.  However, nothing else grants you permission to modify or"+
    "\ndistribute the Program or its derivative works.  These actions are"+
    "\nprohibited by law if you do not accept this License.  Therefore, by"+
    "\nmodifying or distributing the Program (or any work based on the"+
    "\nProgram), you indicate your acceptance of this License to do so, and"+
    "\nall its terms and conditions for copying, distributing or modifying"+
    "\nthe Program or works based on it."+
    "\n"+
    "\n  6. Each time you redistribute the Program (or any work based on the"+
    "\nProgram), the recipient automatically receives a license from the"+
    "\noriginal licensor to copy, distribute or modify the Program subject to"+
    "\nthese terms and conditions.  You may not impose any further"+
    "\nrestrictions on the recipients' exercise of the rights granted herein."+
    "\nYou are not responsible for enforcing compliance by third parties to"+
    "\nthis License."+
    "\n"+
    "\n  7. If, as a consequence of a court judgment or allegation of patent"+
    "\ninfringement or for any other reason (not limited to patent issues),"+
    "\nconditions are imposed on you (whether by court order, agreement or"+
    "\notherwise) that contradict the conditions of this License, they do not"+
    "\nexcuse you from the conditions of this License.  If you cannot"+
    "\ndistribute so as to satisfy simultaneously your obligations under this"+
    "\nLicense and any other pertinent obligations, then as a consequence you"+
    "\nmay not distribute the Program at all.  For example, if a patent"+
    "\nlicense would not permit royalty-free redistribution of the Program by"+
    "\nall those who receive copies directly or indirectly through you, then"+
    "\nthe only way you could satisfy both it and this License would be to"+
    "\nrefrain entirely from distribution of the Program."+
    "\n"+
    "\nIf any portion of this section is held invalid or unenforceable under"+
    "\nany particular circumstance, the balance of the section is intended to"+
    "\napply and the section as a whole is intended to apply in other"+
    "\ncircumstances."+
    "\n"+
    "\nIt is not the purpose of this section to induce you to infringe any"+
    "\npatents or other property right claims or to contest validity of any"+
    "\nsuch claims; this section has the sole purpose of protecting the"+
    "\nintegrity of the free software distribution system, which is"+
    "\nimplemented by public license practices.  Many people have made"+
    "\ngenerous contributions to the wide range of software distributed"+
    "\nthrough that system in reliance on consistent application of that"+
    "\nsystem; it is up to the author/donor to decide if he or she is willing"+
    "\nto distribute software through any other system and a licensee cannot"+
    "\nimpose that choice."+
    "\n"+
    "\nThis section is intended to make thoroughly clear what is believed to"+
    "\nbe a consequence of the rest of this License."+
    "\n"+
    "\n  8. If the distribution and/or use of the Program is restricted in"+
    "\ncertain countries either by patents or by copyrighted interfaces, the"+
    "\noriginal copyright holder who places the Program under this License"+
    "\nmay add an explicit geographical distribution limitation excluding"+
    "\nthose countries, so that distribution is permitted only in or among"+
    "\ncountries not thus excluded.  In such case, this License incorporates"+
    "\nthe limitation as if written in the body of this License."+
    "\n"+
    "\n  9. The Free Software Foundation may publish revised and/or new versions"+
    "\nof the General Public License from time to time.  Such new versions will"+
    "\nbe similar in spirit to the present version, but may differ in detail to"+
    "\naddress new problems or concerns."+
    "\n"+
    "\nEach version is given a distinguishing version number.  If the Program"+
    "\nspecifies a version number of this License which applies to it and \"any"+
    "\nlater version\", you have the option of following the terms and conditions"+
    "\neither of that version or of any later version published by the Free"+
    "\nSoftware Foundation.  If the Program does not specify a version number of"+
    "\nthis License, you may choose any version ever published by the Free Software"+
    "\nFoundation."+
    "\n"+
    "\n  10. If you wish to incorporate parts of the Program into other free"+
    "\nprograms whose distribution conditions are different, write to the author"+
    "\nto ask for permission.  For software which is copyrighted by the Free"+
    "\nSoftware Foundation, write to the Free Software Foundation; we sometimes"+
    "\nmake exceptions for this.  Our decision will be guided by the two goals"+
    "\nof preserving the free status of all derivatives of our free software and"+
    "\nof promoting the sharing and reuse of software generally."+
    "\n"+
    "\n			    NO WARRANTY"+
    "\n"+
    "\n  11. BECAUSE THE PROGRAM IS LICENSED FREE OF CHARGE, THERE IS NO WARRANTY"+
    "\nFOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW.  EXCEPT WHEN"+
    "\nOTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES"+
    "\nPROVIDE THE PROGRAM \"AS IS\" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED"+
    "\nOR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF"+
    "\nMERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.  THE ENTIRE RISK AS"+
    "\nTO THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU.  SHOULD THE"+
    "\nPROGRAM PROVE DEFECTIVE, YOU ASSUME THE COST OF ALL NECESSARY SERVICING,"+
    "\nREPAIR OR CORRECTION."+
    "\n"+
    "\n  12. IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING"+
    "\nWILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MAY MODIFY AND/OR"+
    "\nREDISTRIBUTE THE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU FOR DAMAGES,"+
    "\nINCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING"+
    "\nOUT OF THE USE OR INABILITY TO USE THE PROGRAM (INCLUDING BUT NOT LIMITED"+
    "\nTO LOSS OF DATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY"+
    "\nYOU OR THIRD PARTIES OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER"+
    "\nPROGRAMS), EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE"+
    "\nPOSSIBILITY OF SUCH DAMAGES."+
    "\n"+
    "\n		     END OF TERMS AND CONDITIONS";
	

}

