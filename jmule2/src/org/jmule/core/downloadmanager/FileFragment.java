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
public class FileFragment {
	private long start;
	private long end;
	
	public FileFragment(long start, long end) {
		super();
		this.start = start;
		this.end = end;
	}
	
	public String toString() {
		return " < "+start+" : "+end+" > ";
	}
	
	public boolean containPos(long pos) {
		if ((pos<=end)&&(pos>=start)) return true;
		return false;
	}
	
	public Collection<FileFragment> splitFragment(long sbegin,long send) {
		LinkedList<FileFragment> newFragments = new LinkedList<FileFragment>();
		
		if (!containPos(sbegin)) return newFragments;
		if (!containPos(end)) return newFragments;
		
		if ((sbegin==start)&&(this.end==send))
			return newFragments;
		
		if (sbegin==start) {
			newFragments.add(new FileFragment(send,end));
			return newFragments;
		}
			
		if (this.end==send) {
			newFragments.add(new FileFragment(start,sbegin-1));
			return newFragments;
		}
			
		newFragments.add(new FileFragment(start,sbegin-1));
		newFragments.add(new FileFragment(end,end));
		
		return newFragments;
	}
	
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	
	
}
