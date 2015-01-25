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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author javajox
 * @author binary
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:54 $$
 */
public class UIDescriptor {

	public static final String ORDER = "ORDER";
	public static final String VISIBILITY = "VISIBILITY";
	public static final String TEXT_ONLY = "TEXT_ONLY";
	public static final String ICON_ONLY = "ICON_ONLY";
	public static final String ICON_AND_TEXT = "ICON_AND_TEXT";
	
	private Object UIElement;
	
	private Map map = new HashMap();
	
	public UIDescriptor(Object UIElement) {
		
		this.UIElement = UIElement;
		
	}
	
	public Object getUIElement() {
		return UIElement;
	}

	public void setUIElement(Object element) {
		UIElement = element;
	}

	public Object getProperty(int property) {
		
		return map.get(property);
		
	}
	
}
