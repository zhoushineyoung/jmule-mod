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
package org.jmule.ui.swing;

import java.io.File;

import org.jmule.ui.UIPreferences;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2010/01/05 14:39:15 $$
 */
public class SwingPreferences extends SwingConstants {

	private static SwingPreferences instance = null;
	
	public static SwingPreferences getSingleton() {
		if( instance == null ) instance = new SwingPreferences();
		return instance;
	}
	
	private SwingPreferences() {
		File ui_config_file = new File( UIPreferences.UI_SETTINGS_FILE );
		   if( !ui_config_file.exists() )
	          storeDefaultPreferences(SWING_ROOT);
		   else UIPreferences.getSingleton().load();
	}
	
	/**
	 * @param columnId the id of the column
	 * @return the column order
	 */
	public int getColumnOrder(int columnId) {
		
		String result = config_store.getProperty(getColumnNodePath(SWING_ROOT,columnId), 
				                                getDefaultColumnOrder(columnId) + "");
		return Integer.parseInt( result );
	}
	
	/**
	 * @param columnId the id of the column
	 * @return true if the column is visible
	 */
	public boolean isColumnVisible(int columnId) {
		
		String result = config_store.getProperty(getColumnNodePath(SWING_ROOT,columnId),
				                                 getDefaultColumnVisibility(columnId) + "");
		return Boolean.parseBoolean(result);
	}
	
	/**
	 * @param columnId the id of the column
	 * @param order the order of the column
	 */
	public void setColumnOrder(String columnId, int order) {
		
		config_store.setProperty(columnId, order + "");
		save();
	}
	
	/**
	 * @param columnId the id of the column
	 * @param value true if the column must be visible, false otherwise
	 */
	public void setColumnVisibility(String columnId, boolean value) {
		
		config_store.setProperty(columnId, value + "");
		save();
	}
	
    public boolean isPromptOnExitEnabled() {
    	return super.isPromptOnExitEnabled(SWING_ROOT);
    }
    
    public boolean isCheckForUpdatesAtStartup() {
    	return super.isCheckForUpdatesAtStartup(SWING_ROOT);
    }
    
    public void setPromptOnExit(boolean value) {
    	super.setPromptOnExit(SWING_ROOT, value);
    }
    
    public void setCheckForUpdatesAtStartup(boolean value) {
    	super.setCheckForUpdatesAtStartup(SWING_ROOT, value);
    }
    
    public boolean isNightlyBuildWarning() {
    	return super.isNightlyBuildWarning(SWING_ROOT);
    }
    
    public void setNightlyBuildWarning(boolean value) {
    	super.setNightlyBuildWarning(SWING_ROOT, value);
    }
    
    public boolean isConnectAtStartup() {
    	return super.isConnectAtStartup(SWING_ROOT);
    }
    
    public void setConnectAtStartup(boolean value) {
    	super.setConnectAtStartup(SWING_ROOT, value);
    }
    
}
