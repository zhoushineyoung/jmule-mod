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
package org.jmule.ui.swt.tab;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.jmule.ui.swt.SWTPreferences;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2011/04/25 10:55:30 $$
 */
public abstract class AbstractTab {
	
	public static enum JMULE_TABS { SERVERLIST,KAD, TRANSFERS, SEARCH, SHARED, STATISTICS, LOGS, SETTINGS };
	
	public static final String TAB_KEY = "TabKey";
	
	protected SWTPreferences swt_preferences;
	protected Composite tab_content;
	
	protected String tabTitle;
	
	public AbstractTab(Composite parent, String tabTitle) {
		swt_preferences = SWTPreferences.getInstance();
		tab_content = new Composite(parent, SWT.NONE);
		this.tabTitle = tabTitle;
		tab_content.setData(TAB_KEY, this);
	}
	
	public void disposeTab() {
		tab_content.dispose();
	}
	
	public Composite getContent() {
		return tab_content;
	}
	
	protected CTabItem tab_item = null;
	
	public CTabItem createIfNeedCTabItem(CTabFolder parent) {
		if ((tab_item == null) || (tab_item.isDisposed())) {
			tab_item = new CTabItem(parent, SWT.CLOSE);
			tab_item.setText(tabTitle);
		}
		return tab_item;
	}
	
	public abstract JMULE_TABS getTabType();
	
	public abstract void obtainFocus();
	public abstract void lostFocus();
	
}
