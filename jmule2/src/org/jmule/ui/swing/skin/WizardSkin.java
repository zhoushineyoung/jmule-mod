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
package org.jmule.ui.swing.skin;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:21 $$
 */
public class WizardSkin implements SwingSkin {
	
	// general
	private Font button_font = new Font("Dialog", 1, 12);
	private Font default_font = new Font("Dialog", 1, 12);
	private Font label_font = new Font("Dialog", 1, 12);
	// is used in TopPanel
	private Font label_font_caption = new Font("Bitstream Vera Serif", 1, 14);
	// for WelcomeMessage
	private Font label_welcome_message_font = new java.awt.Font("Serif", 1, 16);
	private Font lable_welcome_message_desc_font = new Font("Dialog", 0, 14); 
	
	private Color highlighted_ui_chooser_item = new Color(0xdadada);
	
	
	
	public Font getButtonFont() {
		
		return button_font;
	}

	public Image getButtonImage(int imageID) {
		
		return null;
	}

	public Color getDefaultColor() {
		
		return null;
	}

	public Font getDefaultFont() {
		
		return default_font;
	}

	public Font getLabelFont() {
		
		return label_font;
	}

	public Font getMenuBarFont()  {
		
		return null;
	}

	public Font getMenuFont() {
		
		return null;
	}

	public Font getPopupMenuFont() {
		
		return null;
	}
	
	public Font getLabelCaptionFont() {
		
		return label_font_caption;
	}
	
	public Font getWelcomeMessageFont() {
		
		return label_welcome_message_font;
	}

	public Font getWelcomeMessageDescFont() {
		
		return lable_welcome_message_desc_font;
	}
	
	public Color getHighlightedUIChooserItem() {
		
		return highlighted_ui_chooser_item;
	}
	
}
