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

import org.eclipse.swt.widgets.Display;
import org.jmule.core.JMRunnable;
import org.jmule.ui.JMuleUI;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.swt.mainwindow.MainWindow;
import org.jmule.ui.swt.skin.DefaultSWTSkinImpl;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2011/03/27 20:06:03 $$
 */
public class JMuleSWTUI implements JMuleUI<SWTSkin> {
	
	private SWTSkin default_skin;
	private MainWindow main_window;
	
	public void initialize() {
		Localizer.initialize();
		SWTThread.getInstance().initialize();
		default_skin = new DefaultSWTSkinImpl();
		main_window = new MainWindow();
		main_window.getCoreComponents();
	}
	
	public void shutdown() {
		GUIUpdater.getInstance().JMStop();
		SWTThread.getInstance().stop();
	}

	public void start() {
		GUIUpdater.getInstance();
		main_window.initUIComponents();
		SWTThread.getInstance().start();
		GUIUpdater.getInstance().start();
		/*Display.getDefault().asyncExec(new JMRunnable() {
			public void JMRun() {
				Sleak sleak = new Sleak ();
				sleak.open ();
			}
		});*/
	}

	public SWTSkin getSkin() {

		return default_skin;
	}

}
