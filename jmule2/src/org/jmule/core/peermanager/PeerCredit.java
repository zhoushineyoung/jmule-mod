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
package org.jmule.core.peermanager;

import org.jmule.core.edonkey.UserHash;
import org.jmule.core.utils.Convert;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/01/28 13:14:23 $$
 */
public class PeerCredit {
	private UserHash userHash;

	private int nUploadedLo = 0;
	private int nDownloadedLo = 0;
	private long nLastSeen = 0;
	private int nUploadedHi = 0;
	private int nDownloadedHi = 0;
	private int nReserved3 = 0; 
	private byte abySecureIdent[] = new byte[0];
	
	private boolean verified = false;
	
	public PeerCredit(UserHash userHash) {
		this.userHash = userHash;
	}
	
	public PeerCredit(byte[] abyKey, int uploadedLo, int downloadedLo,
			long lastSeen, int uploadedHi, int downloadedHi, int reserved3,
			byte[] abySecureIdent) {
		super();
		userHash = new UserHash(abyKey);
		nUploadedLo = uploadedLo;
		nDownloadedLo = downloadedLo;
		nLastSeen = lastSeen;
		nUploadedHi = uploadedHi;
		nDownloadedHi = downloadedHi;
		nReserved3 = reserved3;
		this.abySecureIdent = abySecureIdent;
	}

	public String toString() {
		String result = "";
		result += "User Hash : " + userHash + "\n";
		result += "nUploadedLo : " + nUploadedLo + "\n";
		result += "nDownloadedLo : " + nDownloadedLo + "\n";
		result += "nLastSeen : " + nLastSeen + "\n";
		result += "nUploadedHi : " + nUploadedHi + "\n";
		result += "nDownloadedHi : " + nDownloadedHi + "\n";
		result += "nReserved3 : " + nReserved3 + "\n";
		result += "abySecureIdent : "
				+ Convert.byteToHexString(abySecureIdent, " ") + "\n";
		
		result += "Download : " + getDownload() + "\n";
		result += "Upload   : " + getUpload() + "\n";
		return result;
	}
	
	public boolean isVerified() {
		return verified;
	}
	
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	
	public void addUpload(long bytes) {
		long total = getUpload() + bytes;
		setNUploadedLo(Convert.longToInt(total));
		setNUploadedHi(Convert.longToInt(total >> 32));
	}
	
	public void addDownload(long bytes) {
		long total = getDownload() + bytes;
		setNDownloadedLo(Convert.longToInt(total));
		setNDownloadedHi(Convert.longToInt(total >> 32));
	}
	
	public long getUpload() {
		return getNUploadedHi() << 32 | getNUploadedLo();
	}
	
	public long getDownload() {
		return Convert.intToLong(getNDownloadedHi() << 32 | getNDownloadedLo());
	}

	public int getNUploadedLo() {
		return nUploadedLo;
	}

	public void setNUploadedLo(int uploadedLo) {
		nUploadedLo = uploadedLo;
	}

	public int getNDownloadedLo() {
		return nDownloadedLo;
	}

	public void setNDownloadedLo(int downloadedLo) {
		nDownloadedLo = downloadedLo;
	}

	public long getLastSeen() {
		return nLastSeen;
	}


	public void setLastSeen(long lastSeen) {
		nLastSeen = lastSeen;
	}

	public int getNUploadedHi() {
		return nUploadedHi;
	}

	public void setNUploadedHi(int uploadedHi) {
		nUploadedHi = uploadedHi;
	}

	public int getNDownloadedHi() {
		return nDownloadedHi;
	}

	public void setNDownloadedHi(int downloadedHi) {
		nDownloadedHi = downloadedHi;
	}

	public int getNReserved3() {
		return nReserved3;
	}

	public void setNReserved3(int reserved3) {
		nReserved3 = reserved3;
	}

	public byte[] getAbySecureIdent() {
		return abySecureIdent;
	}

	public void setAbySecureIdent(byte[] abySecureIdent) {
		this.abySecureIdent = abySecureIdent;
	}

	public UserHash getUserHash() {
		return userHash;
	}

	public void setUserHash(UserHash userHash) {
		this.userHash = userHash;
	}

}
