/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.networkmanager;

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.utils.Average;

/**
 * Created on Nov 2, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/11/03 07:20:47 $
 */
class JMConnectionTransferStats {

	  private long totalReceived = 0;
	  private long totalSent = 0;
	  private Average<Float> downloadSpeed = new Average<Float>(ConfigurationManager.CONNECTION_TRAFIC_AVERAGE_CHECKS);
	  private Average<Float> uploadSpeed   = new Average<Float>(ConfigurationManager.CONNECTION_TRAFIC_AVERAGE_CHECKS);
	  
	  JMConnectionTransferStats() {
	  }
	  
	  void addReceivedBytes(long bytes) {
		  this.totalReceived += bytes;
	  }
	  
	  void addSendBytes(long bytes) {
		  this.totalSent+=bytes;
	  }

	  long getTotalReceived() {
		return totalReceived;
	  }

	  long getTotalSent() {
		return totalSent;
	  }
	
	  float getDownloadSpeed() {
		  return Math.round(this.downloadSpeed.getAverage());
	  }
	
	  float getUploadSpeed() {
		  return this.uploadSpeed.getAverage();
	  }
	
	  void resetByteCount() {
		  this.totalReceived = 0;
		  this.totalSent = 0;
	  }
	
	  long lastDownloadBytes = 0;
	  long lastDownloadTime  = System.currentTimeMillis();

	  long lastUploadBytes = 0;
	  long lastUploadTime = System.currentTimeMillis();
	
	  void syncSpeed() {
		  /** Update download speed**/
		  long nowBytes = totalReceived;
		  long transfered = nowBytes - lastDownloadBytes;
		  lastDownloadBytes = nowBytes;
				
		  long nowTime =  System.currentTimeMillis();
		  long dTime = nowTime - lastDownloadTime;
		  lastDownloadTime = nowTime;
		  downloadSpeed.add(((float)transfered/dTime)*1000);
				
		  /** Update upload speed **/
		  nowBytes = totalSent;
		  transfered = nowBytes - lastUploadBytes;
		  lastUploadBytes = nowBytes;
		  nowTime =  System.currentTimeMillis();
		  dTime = nowTime - lastUploadTime;
		  lastUploadTime = nowTime;
		  uploadSpeed.add(((float)transfered/dTime)*1000);
	}
}
