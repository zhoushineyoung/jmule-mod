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
package org.jmule.core.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;


/**
 * 
 * @author gregork
 * @author javajox
 * @see http://phex.svn.sourceforge.net/viewvc/phex/phex/trunk/src/main/java/phex/common/address/AddressUtils.java?view=log
 * @see phex.common.address.AddressUtils
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2010/01/10 14:21:16 $$
 */
public class AddressUtils {
	
	private static final Set<Integer> not_available_ports = new HashSet<Integer>();
	
	static {
		
		not_available_ports.add(1); // tcpmux
		not_available_ports.add(5); // rje
		not_available_ports.add(7); // echo 
		not_available_ports.add(9); // discard
		not_available_ports.add(11); // systat
		not_available_ports.add(13); // daytime
		not_available_ports.add(15); // netstat
		not_available_ports.add(17); // qotd
		not_available_ports.add(18); // send/rwp 
		not_available_ports.add(19); // chargen
		not_available_ports.add(20); // ftp-data
		not_available_ports.add(21); // ftp
		not_available_ports.add(22); // ssh, pcAnywhere
		not_available_ports.add(23); // Telnet 
		not_available_ports.add(25); // SMTP
		not_available_ports.add(27); // ETRN 
		not_available_ports.add(29); // msg-icp 
		not_available_ports.add(31); // msg-auth
		not_available_ports.add(33); // dsp 
		not_available_ports.add(37); // time
		not_available_ports.add(38); // RAP 
		not_available_ports.add(39); // rlp 
		not_available_ports.add(42); // nameserv, WINS 
		not_available_ports.add(43); // whois, nickname
		not_available_ports.add(49); // TACACS, Login Host Protocol
		not_available_ports.add(50); // RMCP, re-mail-ck 
		not_available_ports.add(53); // DNS
		not_available_ports.add(57); // MTP
		not_available_ports.add(59); // NFILE
		not_available_ports.add(63); // whois++ 
		not_available_ports.add(66); // sql*net
		not_available_ports.add(67); // bootps
		not_available_ports.add(68); // bootpd/dhcp
		not_available_ports.add(69); // Trivial File Transfer Protocol (tftp) 
		not_available_ports.add(70); // Gopher 
		not_available_ports.add(79); // finger 
		not_available_ports.add(80); // www-http 
		not_available_ports.add(88); // Kerberos, WWW 
		not_available_ports.add(95); // supdup 
		not_available_ports.add(96); // DIXIE 
		not_available_ports.add(98); // linuxconf 
		not_available_ports.add(101); // HOSTNAME 
		not_available_ports.add(102); // ISO, X.400, ITOT 
		not_available_ports.add(105); // cso
		not_available_ports.add(106); // poppassd 
		not_available_ports.add(109); // POP2
		not_available_ports.add(110); // POP3 
		not_available_ports.add(111); // Sun RPC Portmapper 
		not_available_ports.add(113); // identd/auth 
		not_available_ports.add(115); // sftp
		not_available_ports.add(117); // uucp 
		not_available_ports.add(119); // NNTP
		not_available_ports.add(120); // CFDP 
		not_available_ports.add(123); // NTP
		not_available_ports.add(124); // SecureID
		not_available_ports.add(129); // PWDGEN 
		not_available_ports.add(133); // statsrv
		not_available_ports.add(135); // loc-srv/epmap
		not_available_ports.add(137); // netbios-ns
		not_available_ports.add(138); // netbios-dgm (UDP) 
		not_available_ports.add(139); // NetBIOS
		not_available_ports.add(143); // IMAP
		not_available_ports.add(144); // NewS
		not_available_ports.add(152); // BFTP
		not_available_ports.add(153); // SGMP
		not_available_ports.add(137); // 
		not_available_ports.add(137); // 
		not_available_ports.add(137); // 
		not_available_ports.add(137); // 
		not_available_ports.add(137); // 
		not_available_ports.add(137); // 
		not_available_ports.add(137); // 
		not_available_ports.add(137); // 
		not_available_ports.add(137); // 
		not_available_ports.add(137); // 
		
		
	};
	
    /**
     * Converts the given bytes of an IP to a string representation.
     */
    public static String ip2string(byte[] ip) {
        if ( ip == null )
        {
            throw new NullPointerException("Ip is null!");
        }
       //assert ip.length == 4;
        
        return (ip[0] & 0xff) + "." 
            + (ip[1] & 0xff) + "." 
            + (ip[2] & 0xff) + "." 
            + (ip[3] & 0xff);
    }
    
    /**
     * Converts the given bytes of an IP to a string representation.
     */
    public static String ip2string( int ip ) {
        return ((ip >> 24) & 0xFF) + "." +
               ((ip >> 16) & 0xFF) + "." + 
               ((ip >>  8) & 0xFF) + "." + 
               ( ip        & 0xFF);
    }
    /**
     * Validates a port value if it is in range ( 1 - 65535 )
     * 
     * @param port the port to verify in int value. Unsigned short ports must be
     * converted to singned int to let this function work correctly.
     * @return true if the port is in range, false otherwise.
     */
    public static boolean isPortInRange( int port ) {
        return ( port & 0xFFFF0000 ) == 0 && port != 0;
    }
    
    /**
     * Tells if the port is available or not
     * @param port the given port
     * @return true if the port is available, false otherwise
     */
    public static boolean isPortAvailable(int port) {
    	
       return !not_available_ports.contains(port);
    }
    
    public static boolean isValidPort(String port) {
    	try {
    		int int_port = Integer.parseInt(port);
    		
    		return isPortAvailable(int_port);
    	}catch(Exception e) {
    		return false;
    	}
    }
    
    public static boolean isValidPort(Integer port) {
    	
    	return isPortAvailable(port.intValue());
    }
    
    public static boolean isPortInRange(Integer port) {
    	
    	return isPortInRange(port.intValue());
    }
    
    public static boolean isPortInRange(String port) {
    	
    	return isPortInRange(Integer.parseInt(port));
    }
    
    public static boolean isValidIP(String address) {
    	try {
    		Convert.stringIPToArray(address);
    		return true;
    	}catch(Exception e) {
    		return false;
    	}
    }
    
    public static int addressToInt(String address ) throws UnknownHostException {
    	
		InetAddress i_address = InetAddress.getByAddress( textToNumericFormat( address ) );
		byte[]	bytes = i_address.getAddress();
		return byteArrayToInt( bytes );
	}
    
    public static int addressToInt(InetAddress address) {
    	
		byte[]	bytes = address.getAddress();
		return byteArrayToInt( bytes );
    }
    
    private static int byteArrayToInt(byte[] bytes) {
    	
    	return (bytes[0]<<24)&0xff000000 | (bytes[1] << 16)&0x00ff0000 | (bytes[2] << 8)&0x0000ff00 | bytes[3]&0x000000ff;
    }
    
    final static int INADDRSZ	= 4;
    
    public static byte[] textToNumericFormat(String src ) {
           if (src.length() == 0) {
            	return null;
            }
            	
            if ( src.indexOf(':') != -1 ){
            		
             try{
            	 return( InetAddress.getByName(src).getAddress());
            				
             }catch( Throwable e ){
            				
            	 return( null );
             }
            }
            		
            int octets;
            char ch;
            byte[] dst = new byte[INADDRSZ];
            	
            char[] srcb = src.toCharArray();
            boolean saw_digit = false;

            octets = 0;
            int i = 0;
            int cur = 0;
            while (i < srcb.length) {
               ch = srcb[i++];
               if (Character.isDigit(ch)) {
               // note that Java byte is signed, so need to convert to int
               int sum = (dst[cur] & 0xff)*10
            			+ (Character.digit(ch, 10) & 0xff);
            			
                if (sum > 255)
            	    return null;
                dst[cur] = (byte)(sum & 0xff);
            		if (! saw_digit) {
            			 if (++octets > INADDRSZ)
            			    return null;
            			 saw_digit = true;
            		 }
                 } else if (ch == '.' && saw_digit) {
            		if (octets == INADDRSZ)
            			    return null;
            	       cur++;
            		   dst[cur] = 0;
            		   saw_digit = false;
            	  } else
            			return null;
            	  }
            	if (octets < INADDRSZ)
            		    return null;
            return dst;
    }
    
}
