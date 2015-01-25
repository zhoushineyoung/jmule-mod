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
package org.jmule.core.speedmanager;

import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationManagerException;

/**
 *
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/17 18:29:43 $$
 */
public class SpeedManagerImpl extends JMuleAbstractManager implements InternalSpeedManager {

	private BandwidthController uploadController;
	private BandwidthController downloadController;
	private boolean is_started = false;
	SpeedManagerImpl() {

	}

	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		JMuleCoreFactory.getSingleton().getConfigurationManager()
				.addConfigurationListener(new ConfigurationAdapter() {

					public void downloadLimitChanged(long downloadLimit) {
						downloadController.setThrottlingRate(downloadLimit);
					}

					public void uploadLimitChanged(long uploadLimit) {
						uploadController.setThrottlingRate(uploadLimit);
					}
				});
	}

	public void shutdown() {
		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		is_started = false;
	}

	public void start() {
		try {
			super.start();
		} catch (JMuleManagerException e1) {
			e1.printStackTrace();
			return;
		}
		is_started = true;
		long uploadLimit = 0;
		try {
			uploadLimit = JMuleCoreFactory.getSingleton()
					.getConfigurationManager().getUploadLimit();
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
		}

		uploadController = BandwidthController.acquireBandwidthController(
				"Upload bandwidth controller", uploadLimit);

		long downloadLimit = 0;
		try {
			downloadLimit = JMuleCoreFactory.getSingleton()
					.getConfigurationManager().getDownloadLimit();
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
		}

		downloadController = BandwidthController.acquireBandwidthController(
				"Download bandwidth controller", downloadLimit);

	}

	public BandwidthController getUploadController() {

		return uploadController;

	}

	public BandwidthController getDownloadController() {

		return downloadController;

	}

	protected boolean iAmStoppable() {
		return true;
	}

	public boolean isStarted() {
		return is_started;
	}

}
