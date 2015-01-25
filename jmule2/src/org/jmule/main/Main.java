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
package org.jmule.main;

import java.lang.reflect.Constructor;

import javax.swing.JOptionPane;

import org.jmule.core.JMConstants;


/**
 * Created on 07-Nov-2007
 * @author javajox
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2009/07/11 09:16:02 $$
 */
public class Main {

	public static void main(String args[]) {

		int choice = 0;
		
		if(!JMConstants.isJMuleJavaVersion()) {
			// Wrong java version dialog
			Object[] options = { "OK", "Cancel" };
			String message = "You are using Java " + JMConstants.JAVA_VERSION +
			                 " the minimal recommended version of java for " + JMConstants.JMULE_FULL_NAME + "\n" +
			                 "is " + JMConstants.JMULE_JAVA_VERSION + 
			                 " a greater Java version is a better choice.\n\n" +
			                 "You should manually download the latest version of Java from http://java.sun.com\n\n" +
			                 "Do you want to continue to use Java " + JMConstants.JAVA_VERSION + " ?\n\n";
			    choice = JOptionPane.showOptionDialog(null, message, "Warning", JOptionPane.DEFAULT_OPTION, 
			    		                              JOptionPane.WARNING_MESSAGE, 
			    		                                null, options, options[0]);
		}
		
		if( choice == JOptionPane.YES_OPTION ) {

			  try {
				   //TODO This is the first step on removing the swt libraries
				   //Class.forName("org.eclipse.swt.widgets.Display");
				  
				   Class.forName("org.jdesktop.swingx.JXFrame");
				   
		   	       final Class startupClass = Class.forName("org.jmule.main.Launcher");

		           final Constructor constructor = startupClass.getConstructor(null);
				
		            //constructor.newInstance(new Object[] {
		            //			     args
		            //});
		           constructor.newInstance(null);

		      } catch (Throwable cause) {
				  
		    	     if( cause instanceof ClassNotFoundException ) {
		    	    	 
		    	    	 cause.printStackTrace();
		    	    	 
		    	    	 System.out.println("REMARK: It seems you don't have one of the following .jar files in classpath : swt.jar, swingx.jar\n" +
		    	    			            "        you can found these jars on http://www.eclipse.org/swt and http://swingx.dev.java.net \n" +
		    	    			            "        If you need open support don't hesitate to contact us on http://forum.jmule.org");
		    	    	 
		    	    	 JOptionPane.showMessageDialog(null, cause, "Class not found", JOptionPane.ERROR_MESSAGE);

		    	    	 System.exit( 0 );
		    	     }
		    	  
				     cause.printStackTrace();
				     
				     System.out.println("REMARK: If you need open support don't hesitate to contact us on http://forum.jmule.org");
		      }
		} else if( choice == JOptionPane.NO_OPTION ) {
			
			System.exit( 0 );
			
		}
   }

}
