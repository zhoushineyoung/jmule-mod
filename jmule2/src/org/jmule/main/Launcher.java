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
package org.jmule.main;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreComponent;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.JMuleCoreLifecycleAdapter;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.peermanager.PeerManager;
import org.jmule.core.servermanager.ServerManager;
import org.jmule.core.sharingmanager.SharingManager;
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.ui.CommonUIPreferences;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.Splash;
import org.jmule.ui.swing.JSplash;
import org.jmule.ui.swing.wizards.SetupWizard;
import org.jmule.ui.swt.SWTSplash;
import org.jmule.ui.swt.SWTThread;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/17 18:34:31 $$
 */
public class Launcher {

	Splash splash = null;

	public Launcher() {

		try {

			CommonUIPreferences _pref = CommonUIPreferences.getSingleton();

			if (_pref.getUIType().equals(JMuleUIManager.SWING_UI)) {
				splash = new JSplash();
			}

			if (_pref.getUIType().equals(JMuleUIManager.SWT_UI)) {
				// first of all we must start the swt thread
				SWTThread.getInstance().initialize();
				SWTThread.getInstance().start();
				splash = new SWTSplash();
			}

			splash.splashOn();

			splash.increaseProgress(5, "Initialize the system");

			SetupWizard setup_wizard;

			JMuleCoreFactory.create();

			JMuleCore _core = JMuleCoreFactory.getSingleton();

			JMuleCoreLifecycleAdapter core_lifecycle_listener = new JMuleCoreLifecycleAdapter() {

				public void componentStarted(JMuleCoreComponent component) {

					if (component instanceof ConfigurationManager)
						splash.increaseProgress(10, "Config manager started");
					else if (component instanceof SharingManager)
						splash.increaseProgress(10, "Sharing manager started");
					else if (component instanceof UploadManager)
						splash.increaseProgress(10, "Upload manager started");
					else if (component instanceof PeerManager)
						splash.increaseProgress(10, "Peer manager started");
					else if (component instanceof DownloadManager)
						splash.increaseProgress(10, "Download manager started");
					else if (component instanceof ServerManager)
						splash.increaseProgress(10, "Server manager started");

				}

			};

			_core.addLifecycleListener(core_lifecycle_listener);

			_core.start();

			splash.increaseProgress(10, "JMule core started");

			_core.removeLifecycleListener(core_lifecycle_listener);

			boolean is_core_first_run = _core.isFirstRun();

			if (!is_core_first_run) {

				splash.increaseProgress(5, "Starting JMule UI manager");

				splash.splashOff();

				JMuleUIManager.create();

			}

			if (is_core_first_run) {

				splash.increaseProgress(5, "Running setup wizard");

				// setup_wizard = new SetupWizard(splash);

				setup_wizard = new SetupWizard();

				setup_wizard.setAlwaysOnTop(true);

				setup_wizard.setVisible(true);

				splash.splashOff();
			}

		} catch (Throwable t) {

			t.printStackTrace();

		}

	}

}
