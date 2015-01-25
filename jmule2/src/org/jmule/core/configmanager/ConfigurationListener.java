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
package org.jmule.core.configmanager;

import java.io.File;
import java.util.List;

import org.jmule.core.jkad.ClientID;

/**
 * Created on 07-22-2008
 * @author javajox
 * @version $$Revision: 1.9 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/01/28 12:47:10 $$
 */
public interface ConfigurationListener {

	public void UDPPortChanged(int udp);
	
	public void TCPPortChanged(int tcp);
	
	public void isUDPEnabledChanged(boolean enabled);
	
	public void nickNameChanged(String nickName);
	
	public void downloadBandwidthChanged(long downloadBandwidth);
	
	public void uploadBandwidthChanged(long uploadBandwidth);
	
	public void downloadLimitChanged(long downloadLimit);
	
	public void uploadLimitChanged(long uploadLimit);
	
	public void sharedDirectoriesChanged(List<File> sharedDirs);
	
	public void workingDirChanged(File newWorkingDir);
	
	public void jkadStatusChanged(boolean newStatus);
	
	public void updateServerListAtConnectChanged(boolean newStatus);
	
	public void jkadIDChanged(ClientID newID);
	
	public void nicNameChanged(String newNicName);
	
	public void nicIPChanged(String newNicIP);

	public void securityIdentificationStatusChanged(boolean newStatus);
	
}
