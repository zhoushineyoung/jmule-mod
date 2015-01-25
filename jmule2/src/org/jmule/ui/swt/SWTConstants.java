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

import org.jmule.ui.UIPreferences;

/**
 * 
 * @author binary256
 * @author javajox
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2010/01/05 14:39:15 $$
 */
public class SWTConstants extends UIPreferences {

	public static final long GUI_UPDATE_INTERVAL		 		= 	1000;
	
	public static final int TABLE_ROW_HEIGHT					=   20;
	
	protected static final String SWT_NODE 				 		=   ".swt";
	
	public static final String COLUMN_NAME_KEY			 		=   "ColumnID";
	public static final String COLUMN_DISABLED_KEY	 			=   "ColumnDisabled";
	public static final String COLUMN_DESC_KEY	 				=   "ColumnDesc";
	
	public static final String ROW_OBJECT_KEY				    =   "Row Object";
}

