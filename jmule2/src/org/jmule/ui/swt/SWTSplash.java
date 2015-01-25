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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.jmule.core.JMConstants;
import org.jmule.core.JMRunnable;
import org.jmule.ui.Splash;

/**
 * Created on Aug 25, 2008
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary255 $ on $Date: 2009/12/04 20:23:03 $
 */
public class SWTSplash implements Splash{

	private Shell shell;
	private Label currentTask;
	private ProgressBar progress_bar;
	private Label image;
	public SWTSplash() {
		SWTThread.getDisplay().syncExec(new JMRunnable() {
			public void JMRun() {
				shell = new Shell(SWTThread.getDisplay(),SWT.NULL);
				GridLayout layout = new GridLayout();
			    layout.numColumns = 1;
			    layout.horizontalSpacing = 0;
			    layout.verticalSpacing = 0;
			    layout.marginHeight = 0;
			    layout.marginWidth = 0;
			    shell.setLayout(layout);
			    
			    image = new Label(shell,SWT.NONE);
			    image.setImage(SWTImageRepository.getImage("splash/splash_logo.png"));
			    
			    currentTask = new Label(shell,SWT.BORDER);
			    GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
			    currentTask.setLayoutData(gridData);
			    currentTask.setBackground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			    
			    progress_bar = new ProgressBar(shell,SWT.HORIZONTAL);
			    progress_bar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			    progress_bar.setMinimum(0);
			    progress_bar.setMaximum(100);
			    
			    shell.setText(JMConstants.JMULE_FULL_NAME);
			    shell.setImage(SWTImageRepository.getImage("jmule.png"));
			    shell.pack();
			    shell.layout();
			    Utils.centreWindow(shell);
			    
			    
			}
		});
	}
	
	public void increaseProgress(final int value) {
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				progress_bar.setSelection(progress_bar.getSelection() + value);
			}
		});
	}

	public void increaseProgress(final int value, final String message) {
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				progress_bar.setSelection(progress_bar.getSelection() + value);
				currentTask.setText(message);
			}
		});
	}

	public void resetProgress() {
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				progress_bar.setSelection(0);
				currentTask.setText("");
			}
		});	
	}

	public void splashOff() {
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				shell.setVisible(false);
				image.getImage().dispose();
				shell.close();
				shell.dispose();
			}
		});
	}

	public void splashOn() {
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				shell.open();	
			}
		});
	}

}
