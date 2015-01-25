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
package org.jmule.core.sharingmanager;

/**
 * 
 * @author pola
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/19 14:37:35 $$
 */
public class Gap implements Cloneable {

	long start;
	long end;
  
	public Gap(long start, long end) {
		this.start = start;
		this.end = end;
	}
     
	public long size() {
		return end - start;
	}
    
	public boolean intersects(Gap gap) {
		return ((gap.start >= this.start) && (gap.start < this.end) ||
			(this.start >= gap.start) && (this.start < gap.end));
	}
   
	public boolean covers(Gap gap) {
		return (this.start <= gap.start) && (this.end >= gap.end);
	}
       
	public long chop(Gap gap) {
		long previousSize = this.size();
		if(this.start < gap.start) this.end = Math.min(this.end, gap.start);
		else if(gap.start < this.start) this.start = Math.max(this.start, gap.end);
		return this.size() - previousSize;
	}
       
	public long merge(Gap gap) {
		long previousSize = this.size();
		this.start = Math.min(this.start, gap.start);
		this.end = Math.max(this.end, gap.end);
		return this.size() - previousSize;
	}
      
	public Gap intersect(Gap gap) {
		if(! this.intersects(gap))
			return null;
		try {
			Gap result = (Gap) this.clone();
			result.start = Math.max(result.start, gap.start);
			result.end = Math.min(result.end, gap.end);			
			return result;
		} catch (ClassCastException cce) {
			// You should never get here :-)
			return null;	
		}

	}
 
	public String toString() {
		return Long.toString(start) + ":" + Long.toString(end);
	}
     
	public Gap clone() {
		Gap gap = new Gap(getStart(), getEnd());
		return gap;
	}

	public boolean contain(long pos){
		if ((start<pos)&&(pos<=end))
			return true;
		return false;
	}
      
	public long getEnd() {
		return end;
	}
        
	public long getStart() {
		return start;
	}

}
