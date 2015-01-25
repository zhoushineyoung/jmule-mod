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

import static org.jmule.core.edonkey.ED2KConstants.PARTSIZE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.JMIterable;
import org.jmule.core.JMIterator;

/**
 * 
 * @author pola
 * @author binary256
 * @version $$Revision: 1.6 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/07/31 13:08:35 $$
 */
public class GapList {

	private List<Gap> gaps = new CopyOnWriteArrayList<Gap>();
	private long byteSize = 0;

	static final Comparator<Gap> gapComparator = new Comparator<Gap>() {
		public int compare(Gap gap1, Gap gap2) throws ClassCastException {
			return (int) (gap1.start - gap2.start);
		}
	};

	/**
	 * Reutrns the number of gaps in the GapList.
	 * 
	 * @return The number of gaps in the GapList.
	 */
	public int size() {
		return gaps.size();	
	}

	/**
	 * Reutrns the number of bytes in the GapList.
	 * 
	 * @return The number of bytes in the GapList.
	 */
	public long byteCount() {
		return byteSize;
	}

	/**
	 * Sets the number of bytes in the GapList.
	 * 
	 * @param byteSize The number of bytes in the GapList.
	 */
	private void byteSize(long byteSize) {
		this.byteSize = byteSize;
	}

	/**
	 * Returns the gaps stored in the GapList.
	 * 
	 * @return The gaps stored in the GapList.
	 */
	public JMIterable<Gap> getGaps() {
		return new JMIterable<Gap>(new JMIterator<Gap>(gaps.iterator()));
	}

	/**
	 * Sets the gaps in the GapList.
	 * 
	 * @param gaps The gaps to be added.
	 */
	private void setGaps(List<Gap> gaps) {
		this.gaps = gaps;
	}

	/**
	 * Returns the gaps stored in the GapList that are completely inside a
	 * a given range.
	 * 
	 * @param start The range start.
	 * @param end The range end
	 * .
	 * @return The gaps stored in the GapList that are completely inside a
	 * a given range.
	 */
	public Collection<Gap> getCoveredGaps(long start, long end) {
		Gap full_gap = new Gap(start, end);
		ArrayList<Gap> result = new ArrayList<Gap>();
		for (Gap currentGap : gaps) {
			if (full_gap.covers(currentGap))
				result.add(currentGap);
		}
		return result;
	}
	
	/**
	 * Returns the gaps stored in the GapList that intersects a given range.
	 * 
	 * @param start The range start.
	 * @param end The range end
	 * .
	 * @return The gaps stored in the GapList that intersects a given range.
	 */
	public Collection<Gap> getIntersectedGaps(long start, long end) {
		Gap full_gap = new Gap(start, end);
		ArrayList<Gap> result = new ArrayList<Gap>();
		for (Gap currentGap : gaps) {
			if (full_gap.intersects(currentGap))
				result.add(currentGap);

		}
		return result;
	}

	public boolean contain(long pos) {
		for(Gap gap : gaps) {
			if (gap.contain(pos))
				return true;
		}
		return false;
	}
	
	/**
	 * Sorts the GapList
	 */
	public void sort() {
		//Collections.sort(this.gaps, gapComparator);
	}

	/** Returns the first gap before a position.
     *
     * @param position The starting search position.
     * @return The first gap before a position.
     */
	public Gap getFirstGapBefore(long position) {
		Gap gap = null;
		for(Gap currentGap : gaps) {
			if((currentGap.start <= position) && ((gap == null) || (currentGap.start > gap.start)))
				gap = currentGap;
		}
		return gap;
	}

	/** Returns the first gap after a position.
     *
     * @param position The starting search position.
     * @return The first gap after a position.
     */
	public Gap getFirstGapAfter(long position) {
		Gap gap = null;
		for(Gap currentGap : gaps) {
			if((currentGap.start >= position) && ((gap == null) || 
					(currentGap.start < gap.start)))
				gap = currentGap;
		}
		return gap;
	}

	/**  Adds a new gap to the GapList, it merges the stored gaps if necesary.
     *
     * @param start The gap start.
     * @param end The gap end.
     */
	public void addGap(long start, long end) {
		Gap gap = new Gap(start, end);
		int gapsModifyed = 0;
		
		for(Gap currentGap : gaps) {
			if(currentGap.intersects(gap)) {
				gapsModifyed++;
				byteSize += currentGap.merge(gap);
			}
		}
		// If we expand more than 1 gap, we must merge thos gaps together.
		if(gapsModifyed > 1) {
			for(Gap currentGap : gaps) {
				if(currentGap.intersects(gap)) {
					gaps.remove(currentGap);
					byteSize -= currentGap.size();
					if(currentGap.start <= gap.start)
						gap.start = currentGap.start;
					if(currentGap.end >= gap.end)
						gap.end = currentGap.end;
				}
			}
		}
		if(gapsModifyed != 1) {
			this.gaps.add(gap);
			byteSize += gap.size();
		}
	}

	/**  Removes a gap from the GapList.
     *
     * @param start The gap start.
     * @param end The gap end.
     */
	public void removeGap(long start, long end) {
		Gap newGap = null;
		Gap gap = new Gap(start, end);
		
		for(Gap currentGap : gaps) {
			if(currentGap.intersects(gap))
				if(gap.covers(currentGap)) {
					//i.remove();
					gaps.remove(currentGap);
					byteSize -= currentGap.size();
				}
				else if(currentGap.covers(gap)) {
					byteSize -= currentGap.size();
					if(gap.end < currentGap.end)
						newGap = new Gap(gap.end, currentGap.end);
					if(currentGap.start < gap.start) {
						currentGap.end = gap.start;
						byteSize += currentGap.size();					
					} else {
						//i.remove();
						gaps.remove(currentGap);
					}
					break;
				} else {
					byteSize += currentGap.chop(gap);
				}
		}
		if(newGap != null) {
			gaps.add(newGap);
			byteSize += newGap.size();
		}
	}
	/**
	 * Return the String representation of GapList.
	 * 
	 * @return A String representation of GapList.
	 */
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		this.sort();

		stringBuffer.append(" [");
		for(Gap currentGap : gaps) {
			stringBuffer.append(currentGap.toString() + " ");
		}
		stringBuffer.append("]");
		return stringBuffer.toString();
	}

	/** Creates a copy of this GapList.
     * @return A copy of this GapList.
     * @throws CloneNotSupportedException if the object's class does 
     * not support the Cloneable interface. (This should not happend).
     */
	public Object clone() throws CloneNotSupportedException {
		GapList result = (GapList) super.clone();
		ArrayList<Gap> newGapList = new ArrayList<Gap>();
		for(Gap currentGap : gaps) 
			newGapList.add(currentGap.clone());
		result.setGaps(newGapList);
		return result;
	}
	/**
	 * Returns a new GapList only with gaps covering the selected range. It 
	 * chops the begining and ending gaps if necesary.
	 * 
	 * @param start The gap start.
	 * @param end The gap end.
	 * 
	 * @return A new GapList only with gaps covering the selected range. It 
	 * chops the begining and ending gaps if necesary.
	 */
	public GapList part(long start, long end) {
		long size = 0;
		Gap range = new Gap(start, end);
		GapList result = new GapList();
		ArrayList<Gap> nGaps = new ArrayList<Gap>();
		for(Gap gap : gaps) {
			if(gap.intersects(range)) {
				Gap nGap =  gap.intersect(range);
				nGaps.add(nGap);
				size += nGap.size();
			}
		}
		result.setGaps(nGaps);
		result.byteSize(size);
		return result;
	}

	
	public JMuleBitSet getBitSet(long fileSize) {
		int partCount = (int) ((fileSize/PARTSIZE));
		
		if (fileSize % PARTSIZE!=0) partCount++;
		
		JMuleBitSet bitSet = new JMuleBitSet(partCount);
		
		for(int i = 0 ;i<partCount; i++)
			if (this.getIntersectedGaps(i*PARTSIZE, (i+1)*PARTSIZE).size()==0)
				bitSet.set(i, true);
			else
				bitSet.set(i, false);
		bitSet.setBitCount(partCount);
		return bitSet;
	}
	
	/**
	 * Get all gaps from [start, end] segment
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Gap> getGapsFromSegment(long start,long end) {
		List<Gap> gapList = new LinkedList<Gap>();
		
		for(Gap g : gaps) {
				
			/** Start of gap is inside of Start:End **/
			if ((g.getStart()>=start)&&(g.getStart()<=end)) {
				
				if ((g.getEnd()>=start)&&(g.getEnd()<=end)) {
					Gap g1 = (Gap)g.clone();
					if (g1.getStart()!=g1.getEnd())
						gapList.add(g1);
				}
				
				if ((g.getEnd()>end)) {
					Gap g1 = new Gap(g.getStart(),end);
					if (g1.getStart()!=g1.getEnd())
					gapList.add(g1);
					continue;
				} 
			}
			
			/** Start is in left of Start:End **/
			if (g.getStart()<start) {
				if ((g.getEnd()>=start)&&(g.getEnd()<=end)) {
					Gap g1 = new Gap(start,g.getEnd());
					if (g1.getStart()!=g1.getEnd())
					gapList.add(g1);
					continue;
				}
				if (g.getEnd()>end) {
					gapList.add(new Gap(start,end));
					continue;
				}
			}
				
			}
		
		return gapList;
	}
	
}
