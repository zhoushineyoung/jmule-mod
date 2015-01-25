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
package org.jmule.middleware.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer extends Thread {

	private Socket s;
	private BufferedReader dataFromClient;
	
	public HTTPServer(Socket s) {
		this.s = s;
	}
	
	public void run() {
		System.out.println("------ Session started ------------");
		try {
		 dataFromClient = new BufferedReader(new InputStreamReader(s.getInputStream()));
		 String theLine;
		 while( (theLine = dataFromClient.readLine()) != null ) {
			 System.out.println( theLine );
		 }
		 //String a_line = dataFromClient.readLine();
		 //System.out.println(a_line);
		 //a_line = dataFromClient.readLine();
		 //System.out.println( a_line );
		 
		}catch( Exception e ) {
			e.printStackTrace();
		}
		
		//HTTP request message
		//<method> <request-URL> <version>
		//<header>
		//<entity-body>
		
		//HTTP response message
		//<version> <status> <reson-phrase>
		//<headers>
		//<entity-body>
	}
	
	
	public static void main(String args[]) {
		
	  try {	
		ServerSocket ss = new ServerSocket( 7042 );
	    while(true) { //loop forever
			Socket s = ss.accept(); //Blocks waiting for a new connection
			HTTPServer http_server = new HTTPServer( s ); 
			http_server.start(); //Start the child process
		}
	  }catch(Exception e) {
		  e.printStackTrace();
	  }
		
	}
	
}
