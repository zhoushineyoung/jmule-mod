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
package org.jmule.ui.swing.dialogs;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.jmule.ui.IDialog;
import org.jmule.ui.swing.Refreshable;
import org.jmule.ui.swing.SwingGUIUpdater;

/**
 *
 * Created on Oct 6, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/16 17:35:11 $
 */
public abstract class RefreshableDialog extends JDialog implements IDialog, Refreshable, WindowListener {

	private SwingGUIUpdater _updater = SwingGUIUpdater.getInstance();
	
	public RefreshableDialog() {
		_updater.addRefreshable(this);
	}
	
	public RefreshableDialog(JFrame parent, String name, boolean modal) {
		super(parent, name, modal);
		_updater.addRefreshable(this);
	}
	
	public DialogAction getDialogAction() {

		return null;
	}

	public void windowActivated(WindowEvent e) {}

	public void windowClosed(WindowEvent e) {
        _updater.removeRefreshable(this);
	}

	public void windowClosing(WindowEvent e) {}

	public void windowDeactivated(WindowEvent e) {}

	public void windowDeiconified(WindowEvent e) {
        _updater.addRefreshable(this);
    }

	public void windowIconified(WindowEvent e) {
        _updater.removeRefreshable(this);
    }

	public void windowOpened(WindowEvent e) {
        _updater.addRefreshable(this);
	}
	
}
