/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2008 JMule team ( jmule@jmule.org / http://jmule.org )
 *
 *  Any parts of this program derived from the Apache Commons, 
 *  Phex, Zeus-jscl, AspectJ or JDIC project, or contributed 
 *  by third-party developers are copyrighted by their
 *  respective authors.
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
package org.jmule.ui.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import org.jmule.core.JMRunnable;
import org.jmule.ui.Splash;

/**
 * This is dummy splash(text)screen for console execution; Perhaps this is useless and 
 * may be removed later or improved.
 * <p>
 * @author ftarlao
 * 
 */
public final class ConsoleSplash extends JWindow implements Splash {

	@Override
	public void splashOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void splashOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void increaseProgress(int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void increaseProgress(int value, String message) {
		// TODO Auto-generated method stub
		System.out.println(message);
		
	}

	@Override
	public void resetProgress() {
		// TODO Auto-generated method stub
		
	} 
    
}
