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
/**
 * @see http://www.dwgold.com/httpserver.aspx
 */
import java.net.*;
import java.lang.*;
import java.text.*;
import java.util.*;
import java.io.*;

public class working_server extends Thread

{
    public Properties _serverConfig;
    public Properties _mimeTypes;
    public Hashtable ht = new Hashtable();
    public Hashtable _mt;
    public Hashtable headerValues = new Hashtable();
    public Hashtable attrValues = new Hashtable();
    public String [] headerArray;
    public String [] env;
    public Socket _s;
    public double corruptionProbability;
    public int packetSize;
    public ServerSocket mainSocket;
    public Socket serviceSocket;
    public BufferedReader dataFromClient;
    public OutputStream dataToClient;
    public String fileName;
    public String serverData;
    public String clientData;
    public Random randomGenerator;
    String methodType = "";
    String http = "";
    String domain = "";
    String cgivars = "";
    String protocol = "";
    String httpVerSt = "";
    String httpVer = "0.0";
    double httpVersion = 0.0;
    String filetype = "";
    String filepath = "";
    String serverRoot = "";
    boolean send404 = false;
    String temp = "";
    String host = "";
    String ques = "";
    String filenm = "";
    String filename = "";
    String url = "";
    String headers = "";
    String ctype = "";
    int quesE = 0;
    int numEnvVars = 0;
    boolean cnt = true;
    boolean validMethod = false;
    boolean docDefined = false;
    boolean sent = false;
    boolean body = false;
    
    public working_server(Socket s, Properties serverConfig, Properties mimeTypes, Hashtable mt)
    {
	_s = s;
	_serverConfig = serverConfig;
	_mimeTypes = mimeTypes;
	_mt = mt;
	
    }
    
    public void run()
    {
	System.out.println("\n\n\n\nSession Started\n----------------------");
	
        
	try {    //Begin reading request
	
	    dataFromClient = new BufferedReader(new InputStreamReader(_s.getInputStream()));
	    dataToClient = _s.getOutputStream();      
	    File mtFile = new File(_serverConfig.getProperty("server.root") + "mime.types");
	    BufferedReader br = new BufferedReader(new FileReader(mtFile));
	    String line = "";
	    
	    while ((line = br.readLine()) != null)
		{
		 
		    StringTokenizer MTtype = new StringTokenizer(line,"=");
		    _mt.put(MTtype.nextToken(),MTtype.nextToken());
		}
	    
	    
	   
	    parseRequest();  // handle request
	    sendResponse();  // if all else fails send a 500 error
	    
	    
	    /*     byte [] temp;
            String st;
            st = dataFromClient.readLine();
            System.out.println(st);
            st = st+"\r\n";
            temp = st.getBytes();
            dataToClient.write(temp);
            st = "";
	    */
	    
	}
	catch (FileNotFoundException e) {}
	catch (IOException e) {}
	catch (InterruptedException e) {}
	catch (NullPointerException e) {}
    }
    
    
    public void parseRequest () throws FileNotFoundException, IOException, InterruptedException
    {
	//method which parses the incoming request line by line
	
	String thisline = dataFromClient.readLine();
	System.out.println(thisline);
	StringTokenizer line = new StringTokenizer(thisline, "\r");
	Vector tempV = new Vector();
	String rqline = "";
	System.out.println("Parsing Request");
	while(!thisline.equals("")){
	    tempV.addElement(thisline);
	    thisline = dataFromClient.readLine();
	}
	
	parseHeaders(tempV);    // Parse extra headers like refferer
	
	//begin parsing the request line
	rqline = (String) tempV.elementAt(0);
	StringTokenizer st = new StringTokenizer(rqline, " ");
	String [] request = new String[255];
	int count = 0;
	
	// will get the method type, file location, and http Ver
	while(st.hasMoreTokens())
	    {
		request[count] = st.nextToken();
		count++;
	    }
	
	if (count == 1){              // Bad request
            httpVer = "1.0";
            sendError(400, "Bad Request -- only 1 arg");
	}
	
        else if (count == 2){            // No protocol 0.9 is assumed
            httpVer = "0.9";
            methodType = request[0];
            checkMethod(methodType);
            url = request[1];
            checkUrl(url);
        }
	
        else if (count == 3){           //Valid request so far 
            httpVerSt = request[2];
            checkHttpVer(request);
            methodType = request[0];
            checkMethod(methodType);
            url = request[1];
            checkUrl(url);
        }
	
        else {              // Bad request
            httpVer = "1.0";
            sendError(400, "Bad Request -- more than 3 args");
        
	}
	
    }
    
    public void checkMethod(String meth) throws IOException{
	
	//Method which validates the users method
	
	meth = meth.toUpperCase();
	methodType = meth;
	if (((meth.equals("GET")) || (meth.equals("POST")) || (meth.equals("HEAD")))){
            validMethod = true;
        }
        else if (((meth.equals("OPTIONS")) || (meth.equals("PUT")) || (meth.equals("DELETE")) || (meth.equals("TRACE")))){
            validMethod = false;
            sendError(501, "Method Not Implemented");
        }
        else{
            validMethod = false;
            sendError (400, "Bad Request -- Uknown Method");
            httpVer = "1.0";
        }
    }
    
    public void checkUrl(String newUrl) throws FileNotFoundException, IOException, InterruptedException{
	
	//method which parses and checks the URL portion ofthe request line

	System.out.println("Checking url");
	
	if (url.equals("/")){
	    //ge the default file 
	    System.out.println("There's nothing but a /");
	    filename = _serverConfig.getProperty("default.doc");
	    serverRoot = _serverConfig.getProperty("server.root");
	    filepath = serverRoot + _serverConfig.getProperty("doc.root");
	    filetype = _serverConfig.getProperty("default.type");
	    File defFile = new File(filepath,filename);
            getCT(filename);
	    sendFile(defFile);
	    
	}
	if ((methodType.equals("GET")) || (methodType.equals("POST")) || (methodType.equals("HEAD"))){
	    {
		
		if (url.startsWith("/"))
		    {
			StringTokenizer rqline = new StringTokenizer(url,"/");
			
			while (rqline.hasMoreTokens() && cnt)

			    //llop through the URL

			    {
				ques = rqline.nextToken();
				quesE = ques.indexOf("?");
				//if ((quesE != -1) || (url.regionMatches(true,0,"cgi",0,url.length())))
				if ((quesE != -1) || (ques.indexOf(".cgi")!=-1))
				    {    
					//We've got a cgi to proccess
					
					System.out.println("There's a ? mark or a cgi");
					if (quesE != -1){
					    StringTokenizer splitQ = new StringTokenizer(ques,"?");
					    filenm = splitQ.nextToken();
					    cgivars = splitQ.nextToken();
					    handleCGI(filenm, cgivars);
					}
					else{    
					    //We've got a cgi with out a ? mark
					    
					    System.out.println("gotcha");
					    filenm = ques;
					    handleCGI(filenm, "");
					}
				    }
				else if ((quesE == -1))
				    {
					
					//incoming URL request has no cgi in it or ? -- its a static file
					System.out.println("There's no ? mark -- not cgi");
					System.out.println("Anything but POST file: ");
					temp = temp + ques;
					
					filepath = _serverConfig.getProperty("server.root") + _serverConfig.getProperty("doc.root");
					File staticFile = new File(filepath,temp);
					if (staticFile.isDirectory())
					    {
						//loop through the directories of the request
						
						temp = temp + "/";
						if(!rqline.hasMoreTokens()){
						    File defStaticFile = new File(_serverConfig.getProperty("server.root")+_serverConfig.getProperty("doc.root")+"/"+temp, _serverConfig.getProperty("default.doc"));
						    getCT(_serverConfig.getProperty("default.doc"));
						    sendFile(defStaticFile);
						}
					    }
					else if ((staticFile.isFile()) && staticFile.canRead())
					    {
						
						//Its a valid file to read	
						// System.out.println("staticfile = " +staticFile+ "\n");
						filenm = ques;
						//	  temp = temp + filenm;
						cnt = false;
						//System.out.println("filenm = " + filenm + "\n" );
						getCT(filenm);
						sendFile(staticFile);
					    }
					else if (!staticFile.isFile()){
					    sendError(404, "File Not Found");
					}
					else if (!staticFile.canRead()){
					    sendError(403, "Forbidden -- File not readable");
					}
					
					else
					    {
						
						System.out.println("Not found: " + filepath);
						sendError(404, "File not found");
					    }
				    }
			    }
		    }
		
	    }
	}
    }
    
    public void checkHttpVer(String [] rq) throws IOException{
	
	//Method which tests the HTTP Protocol portion of the request line 
	
	rq[2] = rq[2].toUpperCase();
	if (rq[2] == null)
	    {
		httpVer = "0.9";
	    }
	else if (rq[2].compareTo("HTTP/1.0") == 0)
	    {
		httpVer = "1.0";
	    }
	else if ((rq[2].startsWith("HTTP/") && (!(rq[2].compareTo("HTTP/1.0") == 0)) && (!(rq[2].compareTo("HTTP/0.9") == 0))))
	    {
		httpVer = "1.0";
		
	    }
	else
	    {
		httpVer = "1.0";
		sendError(400,"Bad HTTP Specification");
	    }
	
	
    }
    
    public void parseHeaders(Vector v) {

	//Method which handles and parses the additional headers in the request and stores them in Env Variables
	
	System.out.println("Parsing Headers");
	try{
	    if(v.isEmpty()){
		sendError(400, "Bad Request -- nothing");
	    }
	}	    
	catch(IOException e){
	    System.err.println("IOException");
	    System.exit(-1);
	}
	
	int vsize = v.size();
	int c;
	int b;
	String name = "";
	String value = "";
	String  temp = "";
	StringTokenizer tp;
	
	if(vsize<=1){
	    numEnvVars = 2;
	    return;
	}
	else{

	    //Test headers for validity
	    
	    numEnvVars = 2;
	    for (int j=1; j<vsize; j++){
		temp = (String) v.elementAt(j);
		c = temp.indexOf(":");
		tp = new StringTokenizer(temp, ":");
		b=tp.countTokens();
		if(temp.indexOf("=")!=-1){
		    body = true;
		     System.out.println("Body = true");
		}
		if((c == -1)  && (body==false)){
		    System.out.println("Bad Header: "+temp);
		    System.out.println("Removing Bad EnvVar: " + (String) v.elementAt(j));
		    v.removeElementAt(j);
		    --j;
		    --vsize;
		}
		else if ((b < 2) &&(body==false)) {
		    System.out.println("Bad Header: "+temp);
		    System.out.println("Removing Bad EnvVar: " + (String) v.elementAt(j));
		    v.removeElementAt(j);
		    --j;
		    --vsize;
		}
		
		
		else{
		    numEnvVars ++;
		}
	    }
	    
	     //begin filling the env variable array with valid headers
	    
	    headerArray = new String [numEnvVars-2];
	    System.out.println("numVectorelements: "+v.size());
	    int d = 1;
	    if(body==false){
		for (int j=0; j<numEnvVars-2; j++){
		    value = "";
		    temp = (String) v.elementAt(d);
		    StringTokenizer pos  = new StringTokenizer(temp, ":");
		    if((pos.hasMoreTokens())){
			name = pos.nextToken().trim();
			name = "HTTP_"+name.toUpperCase();
			System.out.println(" name = HTTP_"+name.toUpperCase());
			
			if(pos.hasMoreTokens()){
			    value = pos.nextToken().trim();
			    while(pos.hasMoreTokens()){
				value = value + ":" + pos.nextToken().trim();
			    }
			    System.out.println(" value = "+value);
			    headerArray[j] = name+"="+value;
			    d++;
			}
		    }
		}
	    }
	    else{

		//handles headers when there is a body from a post request

		
		System.out.println("into else");
		
		for (int j=0; j<numEnvVars-3; j++){
		    value = "";
		    temp = (String) v.elementAt(d);
		    StringTokenizer pos  = new StringTokenizer(temp, ":");
		    if((pos.hasMoreTokens())){
			name = pos.nextToken().trim();
			name = "HTTP_"+name.toUpperCase();
			System.out.println(" name = HTTP_"+name.toUpperCase());
			
			if(pos.hasMoreTokens()){
			    value = pos.nextToken().trim();
			    while(pos.hasMoreTokens()){
				value = value + ":" + pos.nextToken().trim();
			    }
			    System.out.println(" value = "+value);
			    headerArray[j] = name+"="+value;
			    d++;
			}
		    }
		}
		headerArray[numEnvVars-3]=(String)v.lastElement();
	    }
	}
    }
    


    public void handleCGI(String fname, String cgiVars)  throws IOException  {
	
	//method which handles incoming cgi requests

	//     System.out.println("Filename: "+ fname + "\r\nCGIVars: " + cgiVars);
	filepath = _serverConfig.getProperty("server.root") + _serverConfig.getProperty("cgi.root");
	
	System.out.println("Filepath: " + filepath);
	File f = new File(filepath, fname);
	filepath = filepath+fname;
	if ((f.isFile()) && (f.canRead())){
	    //begin filling the enviroment variables
	    env = new String [numEnvVars];
	    env[0] = "REQUEST_METHOD="+methodType;
	    env[1] = "QUERY_STRING="+cgiVars;
	    System.out.println("env[0]: "+env[0]);
	    System.out.println("env[1]: "+env[1]);
	    int b = 0;
	    for(int a=2;a<numEnvVars; a++){
		env[a]=headerArray[b];
		System.out.println("env["+a+"]: "+env[a] );
		b++;
	    }
	    if(methodType.equals("GET")){
		startCGI(filepath, env, "");
	    }
	    else if(methodType.equals("POST") && body == true){
		//if post request must get the body of the request and store it after validating the request

		int cl = 0;
		String x ="";
		String cleng = "HTTP_CONTENT-LENGTH";
		String ct = "HTTP_CONTENT-TYPE=APPLICATION/X-WWW-FORM-URLENCODED"; 
		String rqcleng = env[numEnvVars-2];
		String rqct = env[numEnvVars-3];
		
		if(!rqcleng.startsWith(cleng)) {
		    System.out.println("incoming content length: "+rqcleng);
		    System.out.println("should b content length: "+cleng);
		    System.out.println("content length failed");
		    sendError(400, "Bad POST Request -- Invalid Syntax");

		}
		if(!rqct.regionMatches(true,0,ct,0,rqct.length())){
		    System.out.println("content type failed");
		    sendError(400, "Bad POST Request -- Invalid Syntax");
		}
		
		else{
		    //Make sure content lengthh is correct
		    System.out.println("Its a Valid Post Request!!!!!!");
		    StringTokenizer st = new StringTokenizer (rqcleng, "=");
		    if((st.hasMoreTokens()) && (st.countTokens()==2)){
			x=st.nextToken().trim();
			x=st.nextToken().trim();
			cl= new Integer(x).intValue();
			
		    }
		    System.out.println("content length supposed to be: "+cl);
		    System.out.println("content length is------------: "+(env[env.length-1].length()));
		  
		    if (cl!=((env[env.length-1].length()))){
		    	sendError(400, "Content length not correct");
		    }
		    
		    System.out.println("filepath: "+filepath);
		
		}
		
	       startCGI(filepath, env, env[env.length-1]);
	    }
	}
	else if(!f.isFile()){ 
	    sendError(404, "File not found");
	}
	else if(!f.canRead()){ 
	    sendError(403, "Forbidden -- File not readable");
	}
	else{
	    sendError(500, "Internal Server Error");
	}
	
    }

    
    public void startCGI(String fileName, String [] envs, String postBody) {
       	
	//method which uns the cgi programs

	System.out.println("into startCGI"); 
	Process p = null;
	InputStream out;
	InputStreamReader isr;
	BufferedReader br;
	String s = "";
	Vector cgiOut = new Vector();
	int a;
	String [] envVars;
	byte[] ba = postBody.getBytes();
	int m = 1;
	if (body==true){
	    String [] ta = new String [envs.length-1];
	    for (int w = 0; w<envs.length-1; w++){
		ta[w] = envs[w];
	    }
	    envVars = new String [ta.length];
	    envVars = ta;
	}
	else{
	    envVars = new String [envs.length];
	    envVars = envs;
	}
	    
	try{
	    System.out.println("running cgi: "+fileName); 
	    p = Runtime.getRuntime().exec(fileName,envVars); 
	    try{
		OutputStream os = p.getOutputStream();
		os.write(ba);
		os.close();
		p.waitFor();
		m = p.exitValue();
	    }
	     catch(InterruptedException e){}
	}
	catch(IOException i){}
	//	System.out.println("made it out of proccess"); 
	
	

	if(m==0){
	    
	    //cgi exited properly
	    
	    out = p.getInputStream();
	    isr = new InputStreamReader(out);
	    br = new BufferedReader(isr);
	    
	    
	    try{
		
		while((s=br.readLine())!=null){
		    
		    System.out.println(s);
		    s=s+"\n";
		    cgiOut.add(s);
		}
	    br.close();
	    isr.close();
	    out.close();
	    sent=true;
	    
	    }catch(IOException i){
		System.out.println("IOexception");
	    }
	    //	 System.out.println("calling sendcgi");
	    sendCGI(cgiOut);
	}
	else{

	    //cgi did not exit properly
	    try{
	    sendError(500, "internal Server Error While Running cgi");
	     }catch(IOException i){
		System.out.println("IOexception");
	     }
	}
    }

    public void sendCGI(Vector c){

	//method which sends the results fromthe cgi program

	String [] tmp = new String [c.size()];
	String s = "";
	byte[] tempb = new byte[1];
	System.out.println("into SendingCGI");
	/*	for(int i=0; i<c.size(); i++){
	    tmp[i] = (String) c.elementAt(i);
	    s = s+ tmp[i];
	    System.out.println("adding");
	
	try{
	    System.out.println("Sending");
	    tempb = s.getBytes();
	    dataToClient.write(tempb);
	}   
	
	
	catch(IOException e){}
	}
	 _s.close();
	*/
	try{
	    for(int i=0; i<c.size(); i++){
		s = (String) c.elementAt(i);
		
		tempb = s.getBytes();
		dataToClient.write(tempb);
	    }
	    _s.close();
	    
	}catch(IOException e){}
	
	
    }
    
    public void sendFile(File rqFile)throws FileNotFoundException, IOException, InterruptedException{
	
	//method which sends static (non cgi) files
	
	System.out.println("Sending File: " + rqFile + "\n");
	BufferedReader br = new BufferedReader(new FileReader(rqFile));
	//FileReader fr = new FileReader(rqFile);
	String tempLine;
	byte[] tempb;
	byte b;
	
	Date currentTime  = new Date();
	//   ctype = (String) _mt.get(filetype);
	headers = "HTTP/"+httpVer + " 200 OK\r\nDate: " + getDate().format(currentTime);
	headers = headers +  "\r\nConnection: Close";
	headers = headers + "\r\nContent-Length: " + rqFile.length();
	headers = headers +  "\r\nContent-Type: " + ctype + "\r\n";
	tempb = headers.getBytes();
	dataToClient.write(tempb);
	if(!methodType.equals("HEAD")){
	    while ((tempLine = br.readLine()) != null)
	    // while ((b=(byte)fr.read()) != -1)
	    {
		tempb = tempLine.getBytes();
		dataToClient.write(tempb,0,tempb.length);
		//dataToClient.write(b);
		//_s.flush();
	    }
	}
	_s.close();
	sent = true;
    }
    
    public void sendResponse() throws IOException{
	//method which sends if nothing else worked
	
	if(!sent){
            byte [] temp;
            String st;
            String err = "<HTML><HEAD><TITLE>Error 500</TITLE></HEAD> ";
	    err = err +"<BODY> <H1>Error: 500 Internal Server Error</H1></BODY></HTML>";
	    
	    st = "HTTP/1.0 500 Internal Server Error\r\n";
	    st = st + "Connection: Close \r\n";
	    st = st + "Content-length: "+ err.length() + "\r\n";
	    st = st + "Content-type: text/html \r\n";
	    st = st + err;
	    temp = st.getBytes();
            dataToClient.write(temp);
            st = "";
            _s.close();
	    this.stop();
	}
	sent = true;
    }
    
    
    public void sendError(int errNum, String errDes) throws IOException{

	//method which sends detailed error message
	System.out.println("Into SendError");

	if(!sent){
            byte [] temp;
            String st;
            String err = "<HTML><HEAD><TITLE>Error " + errNum+ ": "+ errDes+"</TITLE></HEAD> ";
	    err = err +"<BODY> <H1>Error: " +errNum +" " + errDes + "</H1></BODY></HTML>";
	    
	    st = "HTTP/"+httpVer+" "+errNum+" "+errDes+"\r\n";
	    st = st + "Connection: Close \r\n";
	    st = st + "Content-length: "+ err.length() + "\r\n";
	    st = st + "Content-type: text/html \r\n";
	    st = st + err;
	    temp = st.getBytes();
            dataToClient.write(temp);
            st = "";
            _s.close();
	   this.stop();
	}
	sent = true;
    }
    
    public void getCT(String rqFile){
	
	//method which gets the content type of files

	System.out.print("Into Content Type \n");
	StringTokenizer st = new StringTokenizer(rqFile,".");
	
	String fname = st.nextToken();
	System.out.print("Fname: " + fname + " \n");
	
	String ext = st.nextToken();
	System.out.print("Ext: " + ext + " \n");
	ctype = (String) _mt.get(ext);
	
    }
    
    public SimpleDateFormat getDate(){

	//method which gets the time
	
	String dateFormatString = "EEE, dd MMM yyyy, kk:mm:ss z";
	SimpleDateFormat sdf = new SimpleDateFormat(dateFormatString);
	sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	return sdf;
    }
    
    public static void main (String [] args) throws FileNotFoundException, IOException, InterruptedException, NoSuchMethodError
    {
	Properties serverConfig = new Properties();
	
	Properties mimeTypes = new Properties();
	
	Hashtable mt = new Hashtable();
	
	//	new File(".").getAbsolutePath();
	//serverConfig.load(new FileInputStream(new File(".").getAbsolutePath()+"/srm.conf"));
	//mimeTypes.load(new FileInputStream(new File(".").getAbsolutePath()+"/mime.types"));
	serverConfig.load(new FileInputStream(new File("src/org/jmule2/middleware/http").getAbsolutePath()+"/srm.conf"));
	mimeTypes.load(new FileInputStream(new File("src/org/jmule2/middleware/http").getAbsolutePath()+"/mime.types"));
        int localport = -1;
 
        boolean error = false;
	int defport = new Integer(serverConfig.getProperty("port")).intValue();
	String usage = "usage: java working_server [-p]\noptions:\n[-p port number] the port to run on (default port: " + defport +")";
      
	    try{
		for (int i=0; i<args.length; i++){
		    if (args[i].equals("-p")){
			if (++i>=args.length){
			    System.err.println(usage);
			    System.exit(1);
			}
			localport =  new Integer(args[i]).intValue();
			//		localport = parselocalport.parseInt(args[i], 10);
		    }
		    else{
			System.err.println(usage);
			System.exit(1);
		    }
		}
	    }
	    catch(NumberFormatException e){
		System.err.println("Error: " + e.getMessage() + "\n");
		System.exit(1);
	    }
	    
	    /*	    if(localport <= 0){
		System.err.println("Error: Invalid Local Port Specification " + "\n");
		error = true;
	    }
	}
	
	if (args.length > 1){
	    System.err.println("Usage: server <port>  \n  or server (default port used)");
	}
	if(error)
	    System.exit(-1);
	
	
	if (localport == -1){
	    localport = new Integer(serverConfig.getProperty("port")).intValue();
	}
	    */

	    if(localport==-1)
		localport=defport;
	    System.out.println("Starting server on port " + localport);
	    ServerSocket ss = new ServerSocket(localport);
	
	    while(true) { //loop forever
		Socket s = ss.accept(); //Blocks waiting for a new connection
		working_server serv = new working_server(s, serverConfig, mimeTypes, mt); //Create a new child process
		serv.start(); //Start the child process
	    }
    }
    
}
