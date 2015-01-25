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
package org.jmule.ui.swt.maintabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jmule.ui.swt.SWTPreferences;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/07/28 15:15:09 $$
 */
public abstract class AbstractTab extends Composite{

	public static enum JMULE_TABS { SERVERLIST,KAD, TRANSFERS, SEARCH, SHARED, STATISTICS, LOGS };
	
	protected static SWTPreferences swtPreferences;
	
	public AbstractTab(Composite shell) {
		super(shell, SWT.NONE);
		swtPreferences = SWTPreferences.getInstance();
	}
	
	public abstract JMULE_TABS getTabType();

	public abstract void obtainFocus();
	
	public abstract void lostFocus();
	
	public abstract void disposeTab();
	
}
