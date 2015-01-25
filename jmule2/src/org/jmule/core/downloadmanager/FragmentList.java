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
package org.jmule.core.downloadmanager;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created on 07-19-2008
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/08/02 14:21:09 $$
 */
public class FragmentList extends LinkedList<FileFragment> {

	public FragmentList() {
	}

	public Collection<FileFragment> getFragmentsFromSegment(long start,long end) {
		LinkedList <FileFragment> list = new LinkedList<FileFragment>();
		
		for(int i = 0;i<this.size();i++) {
			FileFragment ff = this.get(i);

			if ((ff.getStart()>=start)&&(ff.getStart()<=end)) {
				
				if ((ff.getEnd()>=start)&&(ff.getEnd()<=end)) {
					list.add(new FileFragment(ff.getStart(),ff.getEnd()));
					continue;
				}
				
				if (ff.getEnd()>end) {
					list.addLast(new FileFragment(ff.getStart(),end));
					continue;
				}
			}

			if (ff.getStart()<start) {
				if ((ff.getEnd()>=start)&&(ff.getEnd()<=end)) {
					list.add(new FileFragment(start,ff.getEnd()));
					continue;
				}
				
				if (ff.getEnd()>end) {
					list.add(new FileFragment(start,end));
					continue;
				}
			}
			
		}
		
		return list;
	}
	
	public boolean hasFragment(long begin,long end) {
		for(int i = 0;i<this.size();i++) {
			FileFragment fg = this.get(i);
			if (fg.containPos(begin)&&fg.containPos(end))
				return true;
		}
		return false;
	}
	
	public void splitFragment(long begin,long end) {
		
		for(int i = 0;i<this.size();i++) {
			FileFragment ff = this.get(i);

			if (ff.containPos(begin)&&ff.containPos(end)) {
				Collection<FileFragment> ffc = ff.splitFragment(begin, end);
				this.remove(i);
				this.addAll(ffc);
				return ;
			}
		}
		
	}
	
	public String toString() {
		int i;
		String result = " [ ";
		for(i=0;i<this.size();i++) 
			result += this.get(i)+"";
		result +=" ] ";
		return result;
	}
	
}
