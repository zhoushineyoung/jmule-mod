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
package org.jmule.core.edonkey;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jmule.core.utils.Convert;


/**
 * Examples :
 * ed2k://|file|NAME|SIZE|MD4-HASH|p=HASH1:HASH2:...|/
 * ed2k://|file|NAME|SIZE|MD4-HASH|h=HASH|/
 * ed2k://|file|NAME|SIZE|MD4-HASH|/|sources,<IP:Port>,<IP:Port>,...|/
 * @author binary
 * @author javajox
 * @author shunyi1108
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/02 19:04:09 $$
 */
public class ED2KFileLink extends ED2KLink {
	
	private static final String ED2K_LINK_PATTERN = 
						  "ed2k:\\/\\/\\|file\\|([^|]*)\\|([0-9]*)\\|([a-h0-9A-H]*)\\|" // base
						+ "(?:p=([a-z0-9A-Z]+(?::[a-z0-9A-Z]+)*)\\|)?" // part hashs
						+ "(?:h=([a-z0-9A-Z]+)\\|)?" // root hash
						+ "(?:s=http://(?:[a-zA-Z0-9\\-\\.]+|\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(?::[0-9]*)?/?(?:[a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~])*\\|)*" // urls
						+ "(?:/\\|sources,((?:(?:[a-zA-Z0-9\\-\\.]+|\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):[0-9]*)"
						+		         "(?:(?:,[a-zA-Z0-9\\-\\.]+|\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):[0-9]*)*)\\|)?" // sources
						+ "\\/";

	private static final String URL_PATTERN = 
		"\\|s=(http://(?:[a-zA-Z0-9\\-\\.]+|\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(?::[0-9]*)?/?(?:[a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~])*)";
	
	private static final String SOURCES_PATTERN = 
		"([a-z0-9\\-\\.]+|\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):([0-9]*)";
	
	private String fileName;
	
	private long fileSize;
	
	private FileHash fileHash;
	
	private PartHashSet partHashSet;

	private String rootHash;

	private List<URL> urls;

	private List<InetSocketAddress> sources;

	public ED2KFileLink(String fileName, long fileSize, FileHash fileHash) {
		this(fileName, fileSize, fileHash, null, null, null, null);
	}

	public ED2KFileLink(String fileName, long fileSize, FileHash fileHash,
			PartHashSet partHashes, String rootHash, List<URL> urls,
			List<InetSocketAddress> sources) {
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.fileHash = fileHash;
		this.partHashSet = partHashes;
		this.rootHash = rootHash;
		this.urls = urls;
		this.sources = sources;
	}
	
	public ED2KFileLink(String fileLink) throws ED2KLinkMalformedException {
		
		if (!isValidLink(fileLink))
			throw new ED2KLinkMalformedException("This ED2K link is not valid " + fileLink);
		
		Pattern s;
		Matcher m;
		s = Pattern.compile(ED2K_LINK_PATTERN, Pattern.CASE_INSENSITIVE);
		m = s.matcher(fileLink);
		if (m.matches()) {
			this.fileName=m.group(1);
			this.fileSize=Long.valueOf(m.group(2)).longValue();
			this.fileHash = new FileHash(m.group(3));
			this.partHashSet = extractPartHashes(fileHash,m.group(4));
			this.rootHash = m.group(5);
			this.urls = extractUrls(m.group(0));
			this.sources = extractSources(m.group(6));
	    }
	}
	
	public static List<ED2KFileLink> extractLinks(String rawData) throws ED2KLinkMalformedException {
		Pattern s;
		Matcher m;
		s = Pattern.compile(ED2K_LINK_PATTERN, Pattern.CASE_INSENSITIVE);
		m = s.matcher(rawData);
		List<ED2KFileLink> links = new LinkedList<ED2KFileLink>();
		while(m.find()) {
			String fileName = m.group(1);
			long fileSize = Long.valueOf(m.group(2)).longValue();
			FileHash fileHash = new FileHash(m.group(3));
			String linkRawData = m.group(0);
			String partHashsRawData = m.group(4);
			String rootHash = m.group(5);
			String sourceRawData = m.group(6);

			PartHashSet partHashs = extractPartHashes(fileHash, partHashsRawData);
			List<URL> urls = extractUrls(linkRawData);
			List<InetSocketAddress> sources = extractSources(sourceRawData);
			links.add(new ED2KFileLink(fileName,fileSize,fileHash, partHashs, rootHash, urls, sources));
		}
		return links;
	}
	
	public static boolean isValidLink(String link) {
		Pattern s;
		Matcher m;
		s = Pattern.compile(ED2K_LINK_PATTERN, Pattern.CASE_INSENSITIVE);
		m = s.matcher(link);
		return m.matches();
	}
	
	public String getAsString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ed2k://|file|"+fileName+"|"+fileSize+"|"+fileHash+"|"); // base

		// part hashs 
		if (partHashSet != null) {
			sb.append("p=");
			
			for(byte[] partHash : partHashSet) {
				sb.append(Convert.byteToHexString(partHash));
				sb.append(":");
			}
			sb.replace(sb.length()-1, sb.length(), "|");
		}

		// root hash
		if (rootHash != null) {
			sb.append("h=").append(rootHash).append("|");
		}

		// urls
		if (urls != null) {
			for (URL url : urls) {
				sb.append("s=").append(url).append("|");
			}
		}

		// sources
		if (sources != null) {
			sb.append("/|sources");
			for (InetSocketAddress source : sources) {
				sb.append(",").append(source.getHostName()).append(":").append(source.getPort());
			}
			sb.append("|");
		}

		sb.append("/");
		return sb.toString();
	}
	
	public long getFileSize() {
		return this.fileSize;
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public String toString(){
		return getAsString();
	}
	
	public FileHash getFileHash() {
		return this.fileHash;
	}

	public PartHashSet getPartHashSet() {
		return this.partHashSet;
	}

	public String getRootHash() {
		return this.rootHash;
	}

	public List<URL> getUrls() {
		return this.urls;
	}

	public List<InetSocketAddress> getSources() {
		return this.sources;
	}

	public int hashCode() {
		return getAsString().hashCode();
	}
	
	public boolean equals(Object object) {
		if (object == null) return false;
		if (!(object instanceof ED2KFileLink)) return false;
		if (hashCode() != object.hashCode()) return false;
		return true;
	}
	
	private static PartHashSet extractPartHashes(FileHash fileHash, String partHashsRawData) {
		PartHashSet partHashSet = null;
		if (partHashsRawData != null) {
			partHashSet = new PartHashSet(fileHash);
			Pattern p = Pattern.compile(":");
			String[] partHashArray = p.split(partHashsRawData);
			for(String partHash : partHashArray)
				partHashSet.add(Convert.hexStringToByte(partHash));
		}
		return partHashSet;
	}

	private static List<URL> extractUrls(String linkRawData) throws ED2KLinkMalformedException {
		List<URL> urls = new ArrayList<URL>();
		if (linkRawData != null) {
			Pattern s = Pattern.compile(URL_PATTERN, Pattern.CASE_INSENSITIVE);
			Matcher m = s.matcher(linkRawData);
			while (m.find()) {
				try {
					urls.add(new URL(m.group(1)));
				} catch (MalformedURLException e) {
					throw new ED2KLinkMalformedException(e);
				}
			}
		}
		return urls.isEmpty() ? null : urls;
	}

	private static List<InetSocketAddress> extractSources(String sourcesRawData) {
		List<InetSocketAddress> sources = new ArrayList<InetSocketAddress>();
		if (sourcesRawData != null) {
			Pattern s = Pattern.compile(SOURCES_PATTERN, Pattern.CASE_INSENSITIVE);
			Matcher m = s.matcher(sourcesRawData);
			while (m.find()) {
				String hostName = m.group(1);
				int port = Integer.parseInt(m.group(2));
				sources.add(InetSocketAddress.createUnresolved(hostName, port));
			}
		}
		return sources.isEmpty() ? null : sources;
	}
}
