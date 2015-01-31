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

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.ui.swing.JMuleSwingUI;
import org.jmule.ui.swt.JMuleSWTUI;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.3 $$
 * changed by $$Author: javajox $$ on $$Date: 2009/07/12 14:44:54 $$
 * changed by ftarlao
 */
public class JMuleUIManager {

	public static String SWT_UI       =    "SWT";
	public static String SWING_UI     =    "SWING";
	public static String CONSOLE_UI   =    "CONSOLE";
	//TODO changed default to SWING_UI. We have to remove the SWT UI
	public static String DEFAULT_UI   =     SWING_UI;
	
	private static JMuleUIManager singleton = null;
	
	private static CommonUIPreferences common_ui_preferences = null;
	
	private static JMuleUI ui_instance = null;
	
	private JMuleCore _core;
	
	private JMuleUIManager() {
		
		common_ui_preferences = CommonUIPreferences.getSingleton();
		
		try {
		
		  _core = JMuleCoreFactory.getSingleton();
		  
		}catch(Throwable t) {
			
			t.printStackTrace();
			
		}
	}
	
	public static JMuleUIManager getSingleton() throws JMuleUIManagerException {
		
		if( singleton == null ) throw new JMuleUIManagerException("The JMule UI manager is not instantiated");
		
		return singleton;
	}
	
	public static JMuleUI getJMuleUI() throws JMuleUIManagerException {
		
		if( ui_instance == null ) throw new JMuleUIManagerException("The JMule UI is not instantiated");
		
		return ui_instance;
	}
	
	public static void create(String ui_type) throws JMuleUIManagerException {
		
		if( singleton == null ) singleton = new JMuleUIManager();
		
		       if( ui_type.equals( SWT_UI ) ) {
			
			            ui_instance = new JMuleSWTUI();
		    	   
		    	        // SWTUILauncher swt_launcher = new SWTUILauncher();
		    	        // swt_launcher.start();
			
		} else if( ui_type.equals( SWING_UI ) ) {
			
			            ui_instance = new JMuleSwingUI();
		} else {
			
			throw new JMuleUIManagerException("Unknown ui type : " + ui_type);
			
		}
		       
		ui_instance.initialize();
		
		ui_instance.start();
		
	}
	
	public static String getCurrentUIType() {
		
		if( ui_instance instanceof JMuleSWTUI ) return SWT_UI;
		
		if( ui_instance instanceof JMuleSwingUI ) return SWING_UI;
		
		return null;
	}
	
	public static void create() throws JMuleUIManagerException {
		
		if( singleton == null ) singleton = new JMuleUIManager();
		
		create( common_ui_preferences.getUIType() );
		
	}
	
	public JMuleCore getJMuleCore() {
		
		return _core;
	}
	
	public void shutdown() {
		
		
		try {
			
			_core.stop();
			
		}catch(Throwable t) {
			
			t.printStackTrace();
		}
		
	}
	
}
