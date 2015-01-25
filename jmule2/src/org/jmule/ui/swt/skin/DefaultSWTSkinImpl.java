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
package org.jmule.ui.swt.skin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.jmule.ui.skin.SkinConstants;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTThread;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/10/16 18:20:02 $$
 */
public class DefaultSWTSkinImpl implements DefaultSWTSkin {

	public Font getButtonFont() {
		return SWTThread.getDisplay().getSystemFont();
	}

	public Color getDefaultColor() {
		return SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLACK);
	}

	public Font getDefaultFont() {
		return SWTThread.getDisplay().getSystemFont();
	}

	public Font getLabelFont() {
		return SWTThread.getDisplay().getSystemFont();
	}

	public Font getMenuBarFont() {
		return SWTThread.getDisplay().getSystemFont();
	}

	public Font getMenuFont() {
		return SWTThread.getDisplay().getSystemFont();
	}

	public Font getPopupMenuFont() {
		return SWTThread.getDisplay().getSystemFont();
	}

	public Image getButtonImage(int imageID) {
		
		switch (imageID) {
		
			case SkinConstants.OK_BUTTON_IMAGE  : {
				return SWTImageRepository.getImage("ok.png");
			}
		
			case SkinConstants.CANCEL_BUTTON_IMAGE  : {
				return SWTImageRepository.getImage("cancel.png");
			}
			
			case SkinConstants.FINISH_BUTTON_IMAGE : {
				return SWTImageRepository.getImage("accept.png");
			}
		}
		
		return null;
	}

}
