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

import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:00 $$
 */
public class VerticalAligner {
	private static boolean bFixGTKBug;

	static {
//		COConfigurationManager.addAndFireParameterListener("SWT_bGTKTableBug",
//				new ParameterListener() {
//					public void parameterChanged(String parameterName) {
//						// some people switch from motif to gtk & back again, so make this
//						// only apply to GTK, even if it was enabled prior
//						bFixGTKBug = COConfigurationManager
//								.getBooleanParameter("SWT_bGTKTableBug")
//								&& Utils.isGTK && SWT.getVersion() < 3226;
//					}
//				});
	}

	public static int getTableAdjustVerticalBy(Table t) {
		if (!bFixGTKBug || t == null || t.isDisposed())
			return 0;
		return -t.getHeaderHeight();
	}

	public static int getTableAdjustHorizontallyBy(Table t) {
		if (!bFixGTKBug || t == null || t.isDisposed())
			return 0;
		ScrollBar sb = t.getHorizontalBar();
		if (sb == null)
			return 0;
		return sb.getSelection();
	}

}
