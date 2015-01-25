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

import java.awt.Color;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import org.jmule.core.JMConstants;
import org.jmule.core.JMRunnable;
import org.jmule.ui.JMuleUI;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.swing.dialogs.NightlyBuildDialog;
import org.jmule.ui.swing.mainwindow.MainWindow;
import org.jmule.ui.swing.skin.DefaultSwingSkinImpl;
import org.jmule.ui.swing.skin.SwingSkin;
import org.jmule.ui.swing.versionchecker.VersionChecker;
import org.jmule.updater.JMUpdater;
import org.jmule.updater.JMUpdaterException;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/05/15 18:22:31 $$
 */
public class JMuleSwingUI implements JMuleUI<SwingSkin> {

	private SwingPreferences swing_preferences;
	private SwingSkin swing_skin_instance;
	private MainWindow main_window;
	
	public void initialize() {
		try {
			SwingUtilities.invokeAndWait(new JMRunnable() {
				public void JMRun() {
					Localizer.initialize();
					
					SwingGUIUpdater.getInstance().start();
					
					UIManager.put("ToolTip.foreground", new ColorUIResource(Color.BLACK));
					UIManager.put("ToolTip.background", new ColorUIResource(0Xfdf7c2));
					
					swing_preferences = SwingPreferences.getSingleton();
					
					swing_skin_instance = new DefaultSwingSkinImpl();
					
					main_window = new MainWindow();

				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		} 
		
				
	}

	public void shutdown() {
		SwingGUIUpdater.getInstance().JMStop();
	}

	public void start() {
		try {
			SwingUtilities.invokeAndWait(new JMRunnable() {
				public void JMRun() {
					SwingPreferences _pref = SwingPreferences.getSingleton();
					
					main_window.setVisible( true );
					
					
					if(JMConstants.IS_NIGHTLY_BUILD) 
						
					  if(_pref.isNightlyBuildWarning()) {	
						
					     NightlyBuildDialog nightly_build_dialog = new NightlyBuildDialog(main_window);
					  
					     SwingUtils.setWindowLocationRelativeTo(nightly_build_dialog, main_window);
					     
					     nightly_build_dialog.setVisible(true);
					  }
					
					// check for newer version if the option is enabled
					if(_pref.isCheckForUpdatesAtStartup()) {
					  JMUpdater update = JMUpdater.getInstance();
					  try {
						 update.checkForUpdates();
					  } catch (JMUpdaterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					  }
					  if(update.isNewVersionAvailable()) {
					    VersionChecker version_checker = new VersionChecker(main_window);
					    SwingUtils.setWindowLocationRelativeTo(version_checker, main_window);
					    version_checker.setVisible(true);
					  }
					}

				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		} 
		
		
	}

	public SwingSkin getSkin() {
		
		return swing_skin_instance;
	}

}
