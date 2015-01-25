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
package org.jmule.ui.swt;

import java.io.File;
import org.jmule.ui.UIConstants;
import org.jmule.ui.UIPreferences;

/**
 * 
 * @author binary256
 * @author javajox
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2011/03/27 16:51:29 $$
 */
public class SWTPreferences extends SWTConstants {
	
	private static class SWTPreferencesInstanceHolder {
		private static final SWTPreferences INSTANCE = new SWTPreferences();
	}
	
	public static SWTPreferences getInstance() {
		return SWTPreferencesInstanceHolder.INSTANCE;
	}
	
	private SWTPreferences() {
		try {
			   File ui_config_file = new File( UIPreferences.UI_SETTINGS_FILE );
			   if( !ui_config_file.exists() )
		          storeDefaultPreferences(SWTConstants.SWT_NODE);
			   else UIPreferences.getSingleton().load();
				 
			} catch(Throwable e) {
				e.printStackTrace();
			}	
	}
	
	public static int getDefaultColumnOrder(int columnID) {
		if (columnID == DOWNLOAD_LIST_COMPLETE_SOURCES_COLUMN_ID)
			return UIConstants.getDefaultColumnOrder(columnID)-2;
		if (columnID == DOWNLOAD_LIST_PARTIAL_SOURCES_COLUMN_ID)
			return UIConstants.getDefaultColumnOrder(columnID)-2;
		return UIConstants.getDefaultColumnOrder(columnID)-1;
	}
	
	public boolean promptOnExit() {
		return isPromptOnExitEnabled(SWT_NODE);
	}
	
	public void setPromprtOnExit(boolean value) {
		setPromptOnExit(SWT_NODE, value);
	}
	
	public boolean updateCheckAtStartup() {
		return isCheckForUpdatesAtStartup(SWT_NODE);
	}
	
	public void setUpdateCheckAtStartup(boolean value) {
		setCheckForUpdatesAtStartup(SWT_NODE,value);
	}
	
	public boolean isToolBarVisible() {
		return isToolBarVisible(SWT_NODE);
	}
	
	public boolean isStatusBarVisible() {
		return isStatusBarVisible(SWT_NODE);
	}
		
	public void setToolBarVisible(boolean visibility) {
		setToolBarVisible(SWT_NODE, visibility);
	}
	
	public void setStatusBarVisible(boolean visibility) {
		setStatusBarVisible(SWT_NODE, visibility);
	}
	
	/**
	 * @param columnId the id of the column
	 * @return the column order
	 */
	public int getColumnOrder(int columnID) {
		String node = getColumnNodePath(SWT_NODE, columnID);
		//String result = config_store.getProperty(node + ORDER, 
		//		                    getDefaultColumnOrder(columnID) + "");
		String result = getDefaultColumnOrder(columnID) + "";
		return Integer.parseInt(result);
	}
	
	/**
	 * @param columnId the id of the column
	 * @param order the order of the column
	 */
	public void setColumnOrder(int columnID, int order) {
		super.setColumnOrder(SWT_NODE, columnID, order);
	}
	
	/**
	 * @param columnId the id of the column
	 * @return the column width
	 */
	public int getColumnWidth(int columnID) {
		if (isColumnVisible(columnID)) 
			return super.getColumnWidth(SWT_NODE, columnID);
		else 
			return 0;
	}
	
	/**
	 * @param columnId the id of the column
	 * @return true if the column is visible
	 */
	public boolean isColumnVisible(int columnID) {
		return super.isColumnVisible(SWT_NODE, columnID);
	}
	
	/**
	 * @param columnId the id of the column
	 * @param value true if the column must be visible, false otherwise
	 */
	public void setColumnVisibility(int columnID, boolean value) {
		super.setColumnVisibility(SWT_NODE, columnID, value);
	}
	
	/**
	 * @param columnId the id of the column
	 * @param value the column width
	 */
	public void setColumnWidth(int columnID, int value) {
		super.setColumnWidth(SWT_NODE, columnID, value);
	}
	
	
	public boolean isNightlyBuildWarning() {
		return super.isNightlyBuildWarning(SWT_NODE);
	}
	
	public void setNightlyBuildWarning(boolean value) {
		super.setNightlyBuildWarning(SWT_NODE, value);
	}
	
	public boolean isConnectAtStartup() {
		return super.isConnectAtStartup(SWT_NODE);
	}
	
	public void setConnectAtStartup(boolean value) {
		super.setConnectAtStartup(SWT_NODE, value);
	}
}
