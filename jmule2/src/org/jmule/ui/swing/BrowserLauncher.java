/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2008 JMule team ( jmule@jmule.org / http://jmule.org )
 *
 *  Any parts of this program derived from the Apache Commons, 
 *  Phex, Zeus-jscl, AspectJ or JDIC project, or contributed 
 *  by third-party developers are copyrighted by their
 *  respective authors.
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
package org.jmule.ui.swing;

import java.lang.reflect.Method;

import org.jmule.core.JMConstants;
import org.jmule.core.JMException;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:02 $$
 */
public class BrowserLauncher {

  public static void openURL(String url) {
    try {
     if (JMConstants.isOSX) {
        Class fileMgr = Class.forName("com.apple.eio.FileManager");
        Method openURL = fileMgr.getDeclaredMethod("openURL",
           new Class[] {String.class});
        openURL.invoke(null, new Object[] {url});
        } else if (JMConstants.isWindows)
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
     else { //assume Unix or Linux
        String[] browsers = {
           "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
        String browser = null;
        for (int count = 0; count < browsers.length && browser == null; count++)
           if (Runtime.getRuntime().exec(
                 new String[] {"which", browsers[count]}).waitFor() == 0)
              browser = browsers[count];
        if (browser == null)
           throw new JMException("Could not find web browser");
        else
           Runtime.getRuntime().exec(new String[] {browser, url});
      }
     } catch (Exception e) {
        e.printStackTrace();
     }
  }
}

