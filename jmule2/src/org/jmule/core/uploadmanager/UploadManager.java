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

import java.util.List;

import org.jmule.core.JMuleManager;
import org.jmule.core.edonkey.FileHash;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/20 09:02:37 $$
 */
public interface UploadManager extends JMuleManager {
	
	public UploadQueue getUploadQueue();
	
	public boolean hasUpload(FileHash fileHash);

	public UploadSession getUpload(FileHash fileHash) throws UploadManagerException;
		
	public List<UploadSession> getUploads();
	
	public int getUploadCount();
	
	public void addUploadManagerListener(UploadManagerListener listener);
	
	public void removeUploadMaanagerListener(UploadManagerListener listener);
	
}
