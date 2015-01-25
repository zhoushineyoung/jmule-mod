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
package org.jmule.core.uploadmanager;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.4 $$ Last changed by $$Author: binary255 $$ on $$Date:
 *          2009/07/06 14:31:58 $$
 */
public class FileChunkRequest implements Comparable<FileChunkRequest> {

	private long chunkBegin;
	private long chunkEnd;
	private long creationTime;
	
	public FileChunkRequest(long chunkBegin, long chunkEnd) {
		super();
		this.chunkBegin = chunkBegin;
		this.chunkEnd = chunkEnd;
		this.creationTime = System.currentTimeMillis();
	}

	public long getChunkBegin() {
		return chunkBegin;
	}

	public void setChunkBegin(long chunkBegin) {
		this.chunkBegin = chunkBegin;
	}

	public long getChunkEnd() {
		return chunkEnd;
	}

	public void setChunkEnd(long chunkEnd) {
		this.chunkEnd = chunkEnd;
	}

	public String toString() {
		return "Chunk begin : " + chunkBegin + " Chunk end : " + chunkEnd;
	}

	public int hashCode() {
		return (chunkBegin + ":" + chunkEnd).hashCode();
	}

	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (!(object instanceof FileChunkRequest))
			return false;
		return object.hashCode() == hashCode();
	}

	public int compareTo(FileChunkRequest object) {
		if (getChunkBegin() < object.getChunkBegin())
			return -1;
		if (getChunkBegin() > object.getChunkBegin())
			return 1;
		if (getChunkEnd() < object.getChunkEnd())
			return -1;
		if (getChunkEnd() > object.getChunkEnd())
			return 1;
		return this.toString().compareTo(object.toString());// chunks with same range
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}


}