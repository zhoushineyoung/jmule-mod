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
package org.jmule.core;

import java.util.StringTokenizer;

/**
 * Created on 07-06-2008
 * @author javajox
 * @version $$Revision: 1.13 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2010/01/13 17:28:37 $$
 */
public class JMConstants {
     
	  public static final String JMULE_WEB_SITE            = "http://jmule.org/";
	  public static final String JMULE_FORUMS              = "http://forum.jmule.org/";
	  public static final String JMULE_DOWNLOAD_PAGE	   = "http://jmule.org/?page=download";
	  public static final String ONLINE_HELP_WEB_SITE      = "http://jmule.org/help";
	  public static final String OPEN_SUPPORT              = "http://jmule.org/opensupport";
	  public static final String SF_WEB_SITE               = "http://sourceforge.net/projects/jmule";
	  public static final String JMULE_BUG_TRACKER		   = "http://jmule.net/bugtracker";
	  
	  public static final String ABOUT_ED2K_LINKS		   = "http://jmule.net?page=ed2k_links";
	  
	  public static final String[] JMULE_DOMAINS 		   = { "jmule.org", "jmule.net" };
	  
	  public static final String JMULE_NAME                = "JMule";
	  public static final String JMULE_VERSION             = "0.5.8";
	  public static final boolean IS_BETA                  = true;
	  public static final boolean IS_NIGHTLY_BUILD         = false;
	  
	  // is used for nightly builds, format : "B" + 1,2,3...
	  public static final String BETA_VERSION              = "B1";
	  
	  public static final String DEV_VERSION               = JMULE_VERSION + "_" + BETA_VERSION;
	  
	  public static final String CURRENT_JMULE_VERSION     = IS_NIGHTLY_BUILD ? DEV_VERSION : ( JMULE_VERSION + ( IS_BETA ? " Beta" : "" ) );
	  
	  public static final String JMULE_FULL_NAME           = JMULE_NAME + " " + CURRENT_JMULE_VERSION; 
	  
	  public static final String KEY_SEPARATOR			   = ":";
	  
	  public static final String  OSName = System.getProperty("os.name");
	  public static final String  OSVersion = System.getProperty("os.version");
	  
	  public static final boolean isOSX				= OSName.toLowerCase().startsWith("mac os");
	  public static final boolean isLinux			= OSName.equalsIgnoreCase("Linux");
	  public static final boolean isSolaris			= OSName.equalsIgnoreCase("SunOS");
	  public static final boolean isFreeBSD			= OSName.equalsIgnoreCase("FreeBSD");
	  public static final boolean isWindowsXP		= OSName.equalsIgnoreCase("Windows XP");
	  public static final boolean isWindowsVista 	= OSName.equalsIgnoreCase("Windows Vista");
	  public static final boolean isWindows95		= OSName.equalsIgnoreCase("Windows 95");
	  public static final boolean isWindows98		= OSName.equalsIgnoreCase("Windows 98");
	  public static final boolean isWindowsME		= OSName.equalsIgnoreCase("Windows ME");
	  public static final boolean isWindows9598ME	= isWindows95 || isWindows98 || isWindowsME;

	  public static final boolean isWindows	= OSName.toLowerCase().startsWith("windows");
	  // If it isn't windows or osx, it's most likely an unix flavor
	  public static final boolean isUnix = !isWindows && !isOSX;
	  
	  public static final String	JAVA_VERSION = System.getProperty("java.version");
	  public static final boolean   isJava1_4    = JAVA_VERSION.startsWith("1.4");
	  public static final boolean   isJava1_5    = JAVA_VERSION.startsWith("1.5");   
	  public static final boolean   isJava1_6    = JAVA_VERSION.startsWith("1.6");   //our platform
	  public static final boolean   isJava1_7    = JAVA_VERSION.startsWith("1.7");
	  
	  // minimal java version required for JMule
	  public static final float JMULE_JAVA_VERSION = 1.6f;
	  /**
	    * <p>Gets the Java version as a <code>String</code> trimming leading letters.</p>
	    *
	    * <p>The field will return <code>null</code> if {@link #JAVA_VERSION} is <code>null</code>.</p>
	    * 
	    */
	  public static final String JAVA_VERSION_TRIMMED = getJavaVersionTrimmed();
	  
	  /**
	   * <p>Gets the Java version as a <code>float</code>.</p>
	   *
	   * <p>Example return values:</p>
	   * <ul>
	   *  <li><code>1.2f</code> for JDK 1.2
	   *  <li><code>1.31f</code> for JDK 1.3.1
	   * </ul>
	   *
	   * <p>The field will return zero if {@link #JAVA_VERSION} is <code>null</code>.</p>
	   * 
	   */
	  public static final float JAVA_VERSION_FLOAT = getJavaVersionAsFloat();
	  
	  /**
	    * Trims the text of the java version to start with numbers.
	    * 
	    * @return the trimmed java version
	    */
	  private static String getJavaVersionTrimmed() {
	        if (JAVA_VERSION != null) {
	            for (int i = 0; i < JAVA_VERSION.length(); i++) {
	                char ch = JAVA_VERSION.charAt(i);
	                if (ch >= '0' && ch <= '9') {
	                    return JAVA_VERSION.substring(i);
	                }
	            }
	        }
	        return null;
	  }
	  
	  /**
	   * <p>Gets the Java version number as a <code>float</code>.</p>
	   *
	   * <p>Example return values:</p>
	   * <ul>
	   *  <li><code>1.2f</code> for JDK 1.2
	   *  <li><code>1.31f</code> for JDK 1.3.1
	   * </ul>
	   * 
	   * <p>Patch releases are not reported.
	   * Zero is returned if {@link #JAVA_VERSION_TRIMMED} is <code>null</code>.</p>
	   * 
	   * @return the version, for example 1.31f for JDK 1.3.1
	   */
	  private static float getJavaVersionAsFloat() {
	        if (JAVA_VERSION_TRIMMED == null) {
	            return 0f;
	        }
	        String str = JAVA_VERSION_TRIMMED.substring(0, 3);
	        if (JAVA_VERSION_TRIMMED.length() >= 5) {
	            str = str + JAVA_VERSION_TRIMMED.substring(4, 5);
	        }
	        try {
	            return Float.parseFloat(str);
	        } catch (Exception ex) {
	            return 0;
	        }
	  }
	  
	  /**
	   * <p>Is the Java version at least the requested version.</p>
	   *
	   * <p>Example input:</p>
	   * <ul>
	   *  <li><code>1.2f</code> to test for JDK 1.2</li>
	   *  <li><code>1.31f</code> to test for JDK 1.3.1</li>
	   * </ul>
	   * 
	   * @param requiredVersion  the required version, for example 1.31f
	   * @return <code>true</code> if the actual version is equal or greater
	   *  than the required version
	   */
	  public static boolean isJavaVersionAtLeast(float requiredVersion) {
	      return JAVA_VERSION_FLOAT >= requiredVersion;
	  }
	  
	  public static boolean isJMuleJavaVersion() {
		  return isJavaVersionAtLeast( JMULE_JAVA_VERSION );
	  }
	  
	  /**
	   * compare two version strings of form n.n.n.n (e.g. 1.2.3.4)
	   * @see org.gudy.azureus2.core3.util.Constants
	   * @param version_1	
	   * @param version_2
	   * @return -ve -> version_1 lower, 0 = same, +ve -> version_1 higher
	   */
	  public static int compareVersions(String version_1, String version_2 ) {	
	  	try{
	  		if ( version_1.startsWith("." )){
	  			version_1 = "0" + version_1;
	  		}
	  		if ( version_2.startsWith("." )){
	  			version_2 = "0" + version_2;
	  		}

	  		version_1 = version_1.replaceAll("[^0-9.]", ".");
	  		version_2 = version_2.replaceAll("[^0-9.]", ".");
	  		
	  		StringTokenizer	tok1 = new StringTokenizer(version_1,".");
	  		StringTokenizer	tok2 = new StringTokenizer(version_2,".");
	  		
	  		while( true ){
	  			if ( tok1.hasMoreTokens() && tok2.hasMoreTokens()){
	  			
	  				int	i1 = Integer.parseInt(tok1.nextToken());
	  				int	i2 = Integer.parseInt(tok2.nextToken());
	  			
	  				if ( i1 != i2 ){
	  					
	  					return( i1 - i2 );
	  				}
	  			}else if ( tok1.hasMoreTokens()){
	  				
	  				int	i1 = Integer.parseInt(tok1.nextToken());

	  				if ( i1 != 0 ){
	  					
	  					return( 1 );
	  				}
	  			}else if ( tok2.hasMoreTokens()){
	  				
	  				int	i2 = Integer.parseInt(tok2.nextToken());

	  				if ( i2 != 0 ){
	  					
	  					return( -1 );
	  				}
	  			}else{
	  				return( 0 );
	  			}
	  		}
	  	}catch( Throwable e ){
	  		
	  		return( 0 );
	  	}
	  }
	  
	   public static int compareDevVersions(String dev_ver_1, String dev_ver_2) {
			  
		    String str_array[] = dev_ver_1.split("_");
		    String ver_1 = str_array[0];
		    String dev_1 = str_array[1];
		    
		    str_array = dev_ver_2.split("_");
		    String ver_2 = str_array[0];
		    String dev_2 = str_array[1];
		    
		    if( compareVersions(ver_1, ver_2) == 0 ) {
		    	String str_array1[] = dev_1.split("B");
		    	String str_array2[] = dev_2.split("B");
		    	int val_1 = Integer.parseInt(str_array1[1]);
		    	int val_2 = Integer.parseInt(str_array2[1]);
		    	if(val_1 > val_2) return 1;
		    	if(val_1 == val_2) return 0;
		    	if(val_1 < val_2) return -1;
		    }
		    
		    return compareVersions(ver_1, ver_2);
	   }

}
