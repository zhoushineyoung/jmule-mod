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
package org.jmule.core.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/20 09:03:11 $$
 */
public class Average<N extends Number> {

	private List<N> number_list;

	private int pos;

	private boolean useAll = false;

	public Average(int size) {
		number_list = new ArrayList<N>(size);
		for(int i = 0;i<size;i++)
			number_list.add(null);
		pos = 0;
	}
	
	public void reset() {
		number_list.clear();
		pos = 0;
	}

	public void add(N value) {
		if (pos >= number_list.size()) {
			useAll = true;
			pos = 0;
		}
		number_list.set(pos++, value);
	}

	public float getAverage() {
		float sum = 0;
		int max = pos;
		if (useAll)
			max = number_list.size();
		for (int i = 0; i < max; i++)
			sum += number_list.get(i).floatValue();
		if (max != 0)
			return (float) ((float) sum / (float) max);
		return 0;
	}

	public String toString() {
		String result = "";
		result = " [ ";
		for (N value : number_list)
			result += " " + value;
		result += " ] ";
		return result;
	}

}
