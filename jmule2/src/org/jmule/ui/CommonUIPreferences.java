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
import java.util.Properties;


/**
 * 
 * @author javajox
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2010/01/05 14:39:15 $$
 */
public class CommonUIPreferences extends UIPreferences {

	private static CommonUIPreferences singleton = null;
	public static String UI_TYPE = ".ui_type";
	
	public static CommonUIPreferences getSingleton() {
		if( singleton == null ) singleton = new CommonUIPreferences();
		return singleton;
	}
	
	private CommonUIPreferences() {
		try {
			  if( new File(UI_SETTINGS_FILE).exists() ) {
				 config_store = new Properties();
				 config_store.load(new FileInputStream(UI_SETTINGS_FILE));
			  } else {
				  load();
			  }
		}catch(Throwable cause) {
				cause.printStackTrace();
		}
	}
	
	/**
	 * Gets the user interface stored in repository
	 * @return the jmule user interface
	 */
	public String getUIType() {
		 
		 return config_store.getProperty(UI_ROOT + UI_TYPE, JMuleUIManager.DEFAULT_UI);
	}
	
	/**
	 * Sets the user interface
	 * @param type the jmule user interface
	 */
	public void setUIType(String type) {
		
		 config_store.setProperty(UI_ROOT + UI_TYPE, type);
		 save();
	}
	
	public void save() {
		
		super.save();
	}
}
