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
package org.jmule.updater;

import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jmule.core.JMConstants;
import org.jmule.ui.localizer._;

/**
 * Created on Aug 20, 2008
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/09/28 16:24:16 $
 */
public class JMUpdater {

	public static final String USER_AGENT = JMConstants.JMULE_FULL_NAME;
	public static final String ENCODING   = "UTF-8";

	private final String UPDATE_URL = "/update.php";
	
	private final String VERSION_KEY = "Version";
	
	private final String VERSION_REGEX = "<lastversion>(.*)</lastversion>";
	private final String CHANGELOG_REGEX = "<changelog>(.*)</changelog>";
	
	private String version = "";
	private String changelog = "";
	private int response_code;
	
	private static Map<Integer,String> error_codes = new Hashtable<Integer,String>();
	
	static {
		error_codes.put(301, _._("updaterwindow.error301"));
		error_codes.put(400, _._("updaterwindow.error400"));
		error_codes.put(401, _._("updaterwindow.error401"));
		error_codes.put(403, _._("updaterwindow.error403"));
		error_codes.put(404, _._("updaterwindow.error404"));
		error_codes.put(408, _._("updaterwindow.error408"));
		error_codes.put(500, _._("updaterwindow.error500"));
		error_codes.put(503, _._("updaterwindow.error503"));
	}
	
	private static JMUpdater updater = null;
	
	private long last_update_time = 0;
	
	public static JMUpdater getInstance() {
		if (updater==null)
			updater = new JMUpdater();
		return updater;
	}
	
	private JMUpdater() {
		
	}
	
	/**
	 * Check if new version of JMule is available 
	 */
	public void checkForUpdates() throws JMUpdaterException {
		for(String jm_doamin : JMConstants.JMULE_DOMAINS) {
			JMHTTPConnection connection = new JMHTTPConnection("http://" + jm_doamin + UPDATE_URL);
			connection.addPostValue(VERSION_KEY, JMConstants.CURRENT_JMULE_VERSION);
			try {
				String result = connection.sendQuery();
				last_update_time = System.currentTimeMillis();
				response_code = connection.getHttpResponseCode();
				Pattern pattern = Pattern.compile(VERSION_REGEX);
				Matcher matcher = pattern.matcher(result);
				if (!matcher.find())
					throw new JMUpdaterException("Version tag not found");
				version = matcher.group(1);
				version = version.trim();
				result = result.replace("\n", "<br>");
				pattern = Pattern.compile(CHANGELOG_REGEX);
				matcher = pattern.matcher(result);
				if (!matcher.find())
					throw new JMUpdaterException("Changelog tag not found");
				changelog = matcher.group(1);
				changelog = changelog.replace("<br>", "\n");
				return ;
			} catch (Throwable e) {
				response_code = connection.getHttpResponseCode();
			}
		}
		throw new JMUpdaterException("Unable to connect to update server");
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getChangeLog() {
		return changelog;
	}
	
	public long getLastUpdateTime() {
		return last_update_time;
	}
	
	public String getErrorCode() {
		String result = error_codes.get(response_code);
		return result == null ? "HTTP Error " + response_code : result;
	}
	
	public boolean isNewVersionAvailable() {
		int result = JMConstants.compareVersions(version,JMConstants.JMULE_VERSION);
		return result>=1;
	}
	
	public static void main(String...strings) throws JMUpdaterException {
		JMUpdater.getInstance().checkForUpdates();	
	}
	
}
