/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.jkad.publisher;

import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKadException;
import org.jmule.core.jkad.publisher.Publisher.PublishTaskListener;


/**
 * Created on Jan 14, 2009
 * @author binary256
 * @version $Revision: 1.8 $
 * Last changed by $Author: binary255 $ on $Date: 2010/07/28 13:13:24 $
 */
public abstract class PublishTask {

	protected Int128 publishID;
	protected long lastpublishTime;
	protected boolean isStarted = false;
	protected int publishedSources = 0;
	protected PublishTaskListener task_listener = null;
	protected PublishTask task_instance = null;
		
	public PublishTask(Int128 publishID,PublishTaskListener listener) {
		super();
		this.publishID = publishID;
		lastpublishTime = 0;
		task_listener = listener;
		this.task_instance = this;
	}

	public Int128 getPublishID() {
		return publishID;
	}

	public long getLastpublishTime() {
		return lastpublishTime;
	}
	
	protected void updatePublishTime() {
		lastpublishTime = System.currentTimeMillis();
	}
	
	public abstract void start() throws JKadException;
	public abstract void stop();

	public boolean isStarted() {
		return isStarted;
	}

	public void addPublishedSources(int sources) {
		publishedSources+= sources;
	}
	
	
	public int getPublishedSources() {
		return publishedSources;
	}
		
}
