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
package org.jmule.core.configmanager;

import static org.jmule.core.jkad.utils.Utils.getRandomInt128;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.jmule.core.JMException;
import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.bccrypto.JDKKeyFactory;
import org.jmule.core.edonkey.ED2KConstants;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.jkad.ClientID;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.utils.AddressUtils;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;
import org.jmule.core.utils.NetworkUtils;

/**
 * Created on 07-22-2008
 * @author javajox
 * @version $$Revision: 1.24 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/07/31 12:56:46 $$
 */
public class ConfigurationManagerImp extends JMuleAbstractManager implements InternalConfigurationManager  {

	private static final String 	DEFAULT_FALSE = "false";
	private static final String 	DEFAULT_TRUE  = "true";
	
	private Properties config_store;
	private UserHash user_hash = null;
	private ClientID client_id = null;
	private List<ConfigurationListener> config_listeners = new LinkedList<ConfigurationListener>();
		
	ConfigurationManagerImp() {
		
	}
	
	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		config_store = new Properties();
	}

	public void start() {
		try {
			super.start();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return ;
		}
		
         try {
			this.load();
		} catch (ConfigurationManagerException cause) {
			cause.printStackTrace();
		}
	}
	
	public void shutdown() {

		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		
		 try {
			this.save();
		} catch (ConfigurationManagerException cause) {
			cause.printStackTrace();
		}
	}
	
	protected boolean iAmStoppable() {
		return false;
	}
	
	public long getDownloadBandwidth() throws ConfigurationManagerException {
		
		String download_bandwidth = config_store.getProperty(DOWNLOAD_BANDWIDTH_KEY,DOWNLOAD_BANDWIDTH+"");
		long value;
		try {
			value = Long.parseLong( download_bandwidth );
		}catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
		
		if (value <= 0)
			throw new ConfigurationManagerException("Download bandwidth can't be negative or 0, " + value + " given");
		return value;
	}

	
	public long getDownloadLimit() throws ConfigurationManagerException {
		
		String download_limit = config_store.getProperty(DOWNLOAD_LIMIT_KEY,DOWNLOAD_LIMIT+"");
		
		long value;
		try {
			value = Long.parseLong( download_limit );
		}catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
		
		if (value < 0)
			throw new ConfigurationManagerException("Download limit can't be negative, " + value + " given");
		return value;
		
	}

	
	public String getNickName() throws ConfigurationManagerException {
        String nick_name = config_store.getProperty(NICK_NAME_KEY, NICK_NAME);
        
        if ( nick_name.length()==0 )
        	throw new ConfigurationManagerException("The nickname can't be 0 length");
        
		return nick_name;
	}

	
	public int getTCP() throws ConfigurationManagerException {
		
		String tcp = config_store.getProperty(TCP_PORT_KEY,TCP_PORT+"");
		
		int value;
		try {
			value = Integer.parseInt( tcp );
		}catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
		
		if (! ( ( value >= 0 ) && ( value <= 65535 ) ) )
			throw new ConfigurationManagerException("The port can be between 0 and 65535, not " + value + " ");
		return value;
	}

	
	public int getUDP() throws ConfigurationManagerException {
		
		String udp = config_store.getProperty(UDP_PORT_KEY,UDP_PORT+"");
		
		int value;
		try {
			value = Integer.parseInt( udp );
		}catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
		
		if (! ( ( value >= 0 ) && ( value <= 65535 ) ) )
			throw new ConfigurationManagerException("The port can be between 0 and 65535, not " + value + " ");
		return value;

	}

	
	public long getUploadBandwidth() throws ConfigurationManagerException  {
		
		String upload_bandwidth = config_store.getProperty(UPLOAD_BANDWIDTH_KEY, UPLOAD_BANDWIDTH + "");
		
		long value;
		try {
			value = Long.parseLong( upload_bandwidth );
		}catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
		
		if (value <= 0)
			throw new ConfigurationManagerException("Upload bandwidth can't be negative or 0, " + value + " given");
		return value;
	}

	
	public long getUploadLimit() throws ConfigurationManagerException {
		
		String upload_limit = config_store.getProperty(UPLOAD_LIMIT_KEY, UPLOAD_LIMIT+"");
		
		long value;
		try {
			value = Long.parseLong( upload_limit );
		}catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
		
		if (value < 0)
			throw new ConfigurationManagerException("Upload limit can't be negative, " + value + " given");
		return value;
	}
	
	
	public UserHash getUserHash() throws ConfigurationManagerException {
		return user_hash;
	}
	
	public ClientID getJKadClientID()throws ConfigurationManagerException {
		return client_id;
	}
	
	public void load() throws ConfigurationManagerException {
		try {
		   config_store.load( new FileInputStream( CONFIG_FILE ) );
		   user_hash = null;
		   try {
			   loadUserHash();
		   }catch(Throwable t) { t.printStackTrace(); }
		   if (user_hash==null) {
			   user_hash = UserHash.genNewUserHash();
			   storeUserHash();
			   notifyPropertyChanged(USER_HASH_KEY, user_hash);
		   }
		   
		   client_id = null;
		   try {
			   loadClientID();
		   }catch(Throwable t) { t.printStackTrace(); }
		   if (client_id==null) {
			   client_id = new ClientID(getRandomInt128());
			   storeClientID();
			   notifyPropertyChanged(JKAD_ID_KEY, client_id);
		   }
		   
		   if (isSecurityIdenficiationEnabled()) {
			   publicKey = null;
			   privateKey = null;
			   try {
				   loadSecurityKeys();
			   }catch(Throwable cause) { cause.printStackTrace(); }
			   
				if ((publicKey == null) || (privateKey == null)) {
					try {
						genSecurityKeys();
						storeSecurityKeys();
					}catch(Throwable cause) { cause.printStackTrace(); }
			   }
		   }
		} catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
	}
	
	private void storeUserHash() throws Throwable {
		FileChannel output_channel = new FileOutputStream(USER_HASH_FILE).getChannel();
		ByteBuffer hash = Misc.getByteBuffer(16);
		hash.put(user_hash.getUserHash());
		hash.position(0);
		output_channel.write(hash);
		output_channel.close();
	}
	
	private void loadUserHash() throws Throwable {
		File file = new File(USER_HASH_FILE);
		if (!file.exists()) return ;
		FileChannel input_channel = new FileInputStream(file).getChannel();
		ByteBuffer hash = Misc.getByteBuffer(16);
		input_channel.read(hash);
		user_hash = new UserHash(hash.array());
		input_channel.close();
	}
	
	private void storeClientID() throws Throwable {
		FileChannel output_channel = new FileOutputStream(KAD_ID_FILE).getChannel();
		ByteBuffer data = Misc.getByteBuffer(16);
		data.put(client_id.toByteArray());
		data.position(0);
		output_channel.write(data);
		output_channel.close();
	}
	
	private void loadClientID() throws Throwable {
		File file = new File(KAD_ID_FILE);
		if (!file.exists()) return ;
		FileChannel input_channel = new FileInputStream(file).getChannel();
		ByteBuffer data = Misc.getByteBuffer(16);
		input_channel.read(data);
		client_id = new ClientID(data.array());
	}

	public void save() throws ConfigurationManagerException {
       try {
    	   
    	   config_store.store( new FileOutputStream( CONFIG_FILE ),"" );
    	   
       } catch(Throwable cause) {
    	   throw new ConfigurationManagerException( cause );
       }

	}
	
	public void setDownloadBandwidth(long downloadBandwidth) throws ConfigurationManagerException {
		 if (downloadBandwidth <= 0)
			 throw new ConfigurationManagerException("Download bandwidth can't be negative or 0, " + downloadBandwidth + " given");
		 config_store.setProperty( DOWNLOAD_BANDWIDTH_KEY, downloadBandwidth + "" );
		 save();
		 notifyPropertyChanged( DOWNLOAD_BANDWIDTH_KEY, downloadBandwidth );
	}
	
	
	public void setDownloadBandwidth(String downloadBandwidth) throws ConfigurationManagerException  {
		long bandwidth;
		try {
			bandwidth = Long.parseLong( downloadBandwidth );
		}catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
		setDownloadBandwidth(bandwidth);
	}

	
	public void setDownloadLimit(long downloadLimit) throws ConfigurationManagerException {
		if ( downloadLimit < 0 )
			throw new ConfigurationManagerException("Download limit can't be negative, " + downloadLimit + " given");
		config_store.setProperty( DOWNLOAD_LIMIT_KEY, downloadLimit + "" );
		save();	
		notifyPropertyChanged( DOWNLOAD_LIMIT_KEY, downloadLimit );
	}
	
	
	public void setDownloadLimit(String downloadLimit) throws ConfigurationManagerException {
		long download_limit;
		try {
			download_limit = Long.parseLong(downloadLimit);
		}catch ( Throwable cause ) {
			throw new ConfigurationManagerException( cause );
		}
		setDownloadLimit(download_limit);
	}

	
	public void setNickName(String nickName) throws ConfigurationManagerException  {
		if( nickName.length() == 0 )
			throw new ConfigurationManagerException("The nickname can't be 0 length");
		
		if( nickName.length() > 65535 )
			throw new ConfigurationManagerException("The nickname length can't be more than 65535 chars");
		
        config_store.setProperty(NICK_NAME_KEY, nickName);
		save();
        notifyPropertyChanged( NICK_NAME_KEY, nickName ); 
	}

	
	public void setSharedFolders(List<File> sharedFolders) throws ConfigurationManagerException {
		// first remove old values
		List<File> file_list = getSharedFolders();
		
		int key_count = file_list == null ? 0 : file_list.size();
		
		for (int i = 0;i< key_count ;i++)
			
			config_store.remove(SHARED_DIRECTORIES_KEY + i);
		
		for(int i = 0; i < sharedFolders.size(); i++) {
			
			config_store.setProperty(SHARED_DIRECTORIES_KEY + i, sharedFolders.get(i).toString());
			
		}
		save();	
		notifyPropertyChanged( SHARED_DIRECTORIES_KEY, sharedFolders );
	}
	
	
	public List<File> getSharedFolders() throws ConfigurationManagerException {
		
		int i = 0;
		
		String file_name;
		
		List<File> shared_directories = new LinkedList<File>();
		
		while( true ) {
			
		  try {	
			  
			file_name = config_store.getProperty(SHARED_DIRECTORIES_KEY + i);
			
			if( file_name == null ) {
				
				if( shared_directories.isEmpty() ) shared_directories = null;
				
				break;
			}
			
			shared_directories.add( new File( file_name ) );
			
			++i;
			
		  } catch( Throwable cause ) {
			  
			  throw new ConfigurationManagerException( cause );
		  }
		  
		}
		
		return shared_directories;
	}

	public void setWorkingDir(File workDir) throws ConfigurationManagerException {
		try {
		    config_store.setProperty(WORKING_DIR_KEY, workDir.toString());
		    save();
		}catch( Throwable cause ) {
			throw new ConfigurationManagerException( cause );
		}
		this.notifyPropertyChanged(WORKING_DIR_KEY, workDir);
	}
	
	public File getWorkingDir() throws ConfigurationManagerException {
		File working_dir;
		try {
		   String dir_name = config_store.getProperty(WORKING_DIR_KEY);
		   working_dir = new File( dir_name );
		}catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
		return working_dir;
	}
	
	public void setTCP(String tcp) throws ConfigurationManagerException {
		int tcp_port;
		try {
			tcp_port = Integer.parseInt( tcp );
		}catch(Throwable cause) {
			
			throw new ConfigurationManagerException(cause);
		}
		
		setTCP( tcp_port );
	}

	
	public void setTCP(int tcp) throws ConfigurationManagerException {
		
		if ( ! ( ( tcp >= 0 ) && ( tcp <= 65535 ) ) )
			throw new ConfigurationManagerException("The port between 0 and 65535, " + tcp + " given");
		
		config_store.setProperty( TCP_PORT_KEY, tcp + "" );
		save(); 
		notifyPropertyChanged( TCP_PORT_KEY, tcp );
		((InternalNetworkManager)NetworkManagerSingleton.getInstance()).tcpPortChanged();
	}

	
	public void setUDP(String udp) throws ConfigurationManagerException  {	
		
		int udp_port;
		
		try {
			
			udp_port = Integer.parseInt(udp);
			
		}catch( Throwable cause ) {
			throw new ConfigurationManagerException( cause );
		}
		
		setUDP( udp_port );
	}

	
	public void setUDP(int udp) throws ConfigurationManagerException  {
		
		if ( ! ( ( udp >= 0 ) && ( udp <= 65535 ) ) )
			throw new ConfigurationManagerException("The port between 0 and 65535, " + udp + " given");
		
		config_store.setProperty( UDP_PORT_KEY, udp + "" );
		save(); 
		notifyPropertyChanged( UDP_PORT_KEY, udp );
		((InternalNetworkManager)NetworkManagerSingleton.getInstance()).udpPortChanged();
	}

	
	public void setUploadBandwidth(long uploadBandwidth) throws ConfigurationManagerException {
	   if (uploadBandwidth <= 0) {
		   throw new ConfigurationManagerException("Upload bandwidth can't be negative or 0, " + uploadBandwidth + " given");
	   }
       config_store.setProperty( UPLOAD_BANDWIDTH_KEY, uploadBandwidth + "" );
       save(); 
       notifyPropertyChanged( UPLOAD_BANDWIDTH_KEY, uploadBandwidth );
	}

	
	public void setUploadBandwidth(String uploadBandwidth) throws ConfigurationManagerException {
		long upload_bandwidth;
		try {
			upload_bandwidth = Long.parseLong(uploadBandwidth);
		}catch( Throwable cause ) {
			throw new ConfigurationManagerException( cause );
		}
		
		setUploadBandwidth(upload_bandwidth);
		
	}

	
	public void setUploadLimit(long uploadLimit) throws ConfigurationManagerException {
	   if ( uploadLimit < 0 ) 
		   throw new ConfigurationManagerException("Upload limit can't be negative, " + uploadLimit + " given");
	   
       config_store.setProperty( UPLOAD_LIMIT_KEY, uploadLimit + "" );
       save(); 
       notifyPropertyChanged( UPLOAD_LIMIT_KEY, uploadLimit );
	}
	
	
	public void setUploadLimit(String uploadLimit) throws ConfigurationManagerException {
		
		long upload_limit;
		try {
			upload_limit = Long.parseLong(uploadLimit);
		}catch( Throwable cause ) {
			throw new ConfigurationManagerException ( cause );
		}
		
		setUploadLimit(upload_limit);
		
	}
	
	public void setUDPEnabled(boolean enabled) throws ConfigurationManagerException  {	
		config_store.setProperty( UDP_ENABLED_KEY, enabled + "" );
		save(); 
		notifyPropertyChanged( UDP_ENABLED_KEY, enabled );
		((InternalNetworkManager)NetworkManagerSingleton.getInstance()).udpPortStatusChanged();
	}
	
	
	public boolean isUDPEnabled() throws ConfigurationManagerException  {
		String udp_enabled = config_store.getProperty(UDP_ENABLED_KEY, UDP_ENABLED+"");
		boolean is_udp_enabled;
		try {
			is_udp_enabled = Boolean.parseBoolean(udp_enabled);
		}catch(Throwable cause ) {
			throw new ConfigurationManagerException( cause );
		}
		
		return is_udp_enabled;
	}
	
	
	public boolean isJKadAutoconnectEnabled() throws ConfigurationManagerException {
		String status = config_store.getProperty(JKAD_ENABLED_KEY, DEFAULT_TRUE);
		boolean jkad_enabled;
		try {
			jkad_enabled = Boolean.parseBoolean(status);
		}catch(Throwable cause ) {
			throw new ConfigurationManagerException( cause );
		}
		return jkad_enabled;
	}

	
	public void setAutoconnectJKad(boolean newStatus) throws ConfigurationManagerException {
		config_store.setProperty(JKAD_ENABLED_KEY, newStatus+"");
		save();
		notifyPropertyChanged(JKAD_ENABLED_KEY, newStatus);
	}	
	
	public void setUpdateServerListAtConnect(boolean newStatus) throws ConfigurationManagerException {
		config_store.setProperty(SERVER_LIST_UPDATE_ON_CONNECT_KEY, newStatus+"");
		save();
		notifyPropertyChanged(SERVER_LIST_UPDATE_ON_CONNECT_KEY, newStatus);
	}
	
	public boolean updateServerListAtConnect() throws ConfigurationManagerException {
		String status = config_store.getProperty(SERVER_LIST_UPDATE_ON_CONNECT_KEY,DEFAULT_FALSE);
		boolean update_server_list;
		try {
			update_server_list = Boolean.parseBoolean(status);
		}catch( Throwable cause ) {
			throw new ConfigurationManagerException( cause );
		}
		return update_server_list;
	}
	
    public String getNicName() throws ConfigurationManagerException {
    	if (!config_store.containsKey(NIC_NAME_KEY))
    		return null;
    	String nicName = config_store.getProperty(NIC_NAME_KEY);
    	try {
			if (!NetworkUtils.hasNicName(nicName))
				throw new ConfigurationManagerException("The Nic "+nicName+" doesn't exists");
		} catch (JMException cause) {
			throw new ConfigurationManagerException(cause);
		}
    	return nicName;
    }
    
    public void setNicName(String nicName) throws ConfigurationManagerException {
    	try {
			if (!NetworkUtils.hasNicName(nicName))
				throw new ConfigurationManagerException("The Nic "+nicName+" doesn't exists");
		} catch (JMException cause) {
			throw new ConfigurationManagerException(cause);
		}
    	config_store.setProperty(NIC_NAME_KEY, nicName);
    	save();
		notifyPropertyChanged(NIC_NAME_KEY, nicName);
    		
    }
    
    public String getNicIP() throws ConfigurationManagerException {
    	if (!config_store.containsKey(NIC_IP_KEY))
    		return null;
    	String nicIP = config_store.getProperty(NIC_IP_KEY);
    	if (nicIP.length()==0)
    		throw new ConfigurationManagerException("Nic IP length is 0");
    	if (!AddressUtils.isValidIP(nicIP))
    		throw new ConfigurationManagerException("Nic IP "+nicIP+" is not valid");
    	return nicIP;
    }
    
    public void setNicIP(String nicIP) throws ConfigurationManagerException {
    	if (!AddressUtils.isValidIP(nicIP))
    		throw new ConfigurationManagerException("Nic IP "+nicIP+" is not valid");
    	if (nicIP.length()==0)
    		throw new ConfigurationManagerException("Nic IP length is 0");
    	config_store.setProperty(NIC_IP_KEY, nicIP);
    	save();
		notifyPropertyChanged(NIC_IP_KEY, nicIP);
    }

    public boolean isSecurityIdenficiationEnabled() throws ConfigurationManagerException {
    	String security_identification_string = config_store.getProperty(SECURITY_IDENTIFICATION, DEFAULT_TRUE);
		boolean status;
		try {
			status = Boolean.parseBoolean(security_identification_string);
		}catch(Throwable cause ) {
			throw new ConfigurationManagerException( cause );
		}
		return status;
    }
    
    public void setSecurityIdentification(boolean enableSecutity) throws ConfigurationManagerException {
    	config_store.setProperty(SECURITY_IDENTIFICATION, enableSecutity+"");
    	save();
		notifyPropertyChanged(SECURITY_IDENTIFICATION, enableSecutity);
    }	

    private RSAPrivateKey privateKey = null;
    private RSAPublicKey  publicKey = null;
    
    public RSAPublicKey getPublicKey() {
    	return publicKey;
    }
    
    public RSAPrivateKey getPrivateKey() {
    	return privateKey;
    }
    
    private void genSecurityKeys() throws Throwable {
    	BigInteger PUBLIC_EXPONENT = BigInteger.valueOf(0x10001L);
    	SecureRandom random = new SecureRandom();
    	BigInteger p = null;
        BigInteger q = null;
        BigInteger modulus = null;
        BigInteger private_exponent = null;
        while(true){
            try{
                do{
                    p = new BigInteger(ED2KConstants.SECURITY_IDENTIFICATION_BIT_KEY_LENGTH >>> 1, 85, random);
                    q = new BigInteger(ED2KConstants.SECURITY_IDENTIFICATION_BIT_KEY_LENGTH >>> 1, 85, random);
                    modulus = p.multiply(q);
                } while( (p.compareTo(q) == 0) || (modulus.bitLength() != 384) );
                private_exponent = PUBLIC_EXPONENT.modInverse(p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)));
                break;  
            } catch(ArithmeticException ae) {
            }
        }
        RSAPublicKeySpec  pubKeySpec = new RSAPublicKeySpec(modulus, PUBLIC_EXPONENT);
        RSAPrivateKeySpec pKeySpec = new RSAPrivateKeySpec(modulus, private_exponent);
    	
        JDKKeyFactory.RSA rsa_key_factory = new JDKKeyFactory.RSA();
        privateKey  = (RSAPrivateKey)rsa_key_factory.engineGeneratePrivate(pKeySpec);
        publicKey = (RSAPublicKey)rsa_key_factory.engineGeneratePublic(pubKeySpec);
        
    	/*KeyPair keyPair = null;
    	KeyPairGenerator keyGenerator =  KeyPairGenerator.getInstance("RSA","BC");
    	keyGenerator.initialize(E2DKConstants.SECURITY_IDENTIFICATION_BIT_KEY_LENGTH);
    	while(true) {
	    	keyPair = keyGenerator.generateKeyPair();
	    	RSAPublicKey key = (RSAPublicKey) keyPair.getPublic();
	    	if (!(key.getEncoded().length > E2DKConstants.MAX_SECURITY_KEY_LENGTH))
	    		break;
    	}
    	publicKey = (RSAPublicKey) keyPair.getPublic();
    	privateKey = (RSAPrivateKey) keyPair.getPrivate();*/
    }
    
    private void loadSecurityKeys() throws Throwable {
    	File key_file = new File(CRYPTKEY_FILE);
    	if (!key_file.exists())
    		return;
    	FileChannel key_channel = new FileInputStream(key_file).getChannel();
    	ByteBuffer data_length = Misc.getByteBuffer(1);
    	key_channel.read(data_length);
    	
    	ByteBuffer modulus = Misc.getByteBuffer(data_length.get(0));
    	key_channel.read(modulus);
    	
    	data_length.position(0);
    	key_channel.read(data_length);
    	
    	ByteBuffer public_exponent = Misc.getByteBuffer(data_length.get(0));
    	key_channel.read(public_exponent);
    	
    	data_length.position(0);
    	key_channel.read(data_length);
    	
    	ByteBuffer private_exponent = Misc.getByteBuffer(data_length.get(0));
    	key_channel.read(private_exponent);
    	
    	key_channel.close();
    	
    	JDKKeyFactory.RSA rsa_key_factory = new JDKKeyFactory.RSA();
		privateKey = (RSAPrivateKey) rsa_key_factory
				.engineGeneratePrivate(new RSAPrivateKeySpec(new BigInteger(
						modulus.array()), new BigInteger(private_exponent
						.array())));
		publicKey = (RSAPublicKey) rsa_key_factory
				.engineGeneratePublic(new RSAPublicKeySpec(new BigInteger(
						modulus.array()), new BigInteger(public_exponent
						.array())));
    	
    	/*KeyFactory keyFactory = KeyFactory.getInstance("RSA","BC");
    	
		publicKey = (RSAPublicKey) keyFactory
				.generatePublic(new RSAPublicKeySpec(new BigInteger(modulus.array())
								, new BigInteger(public_exponent.array())));
		
		privateKey = (RSAPrivateKey)keyFactory.generatePrivate(new RSAPrivateKeySpec(new BigInteger(modulus.array())
								, new BigInteger(private_exponent.array())));  */
    }
    
    private void storeSecurityKeys() throws Throwable {
    	FileChannel key_channel = new FileOutputStream(CRYPTKEY_FILE).getChannel();
    	ByteBuffer length = Misc.getByteBuffer(1);
    	byte[] bmodulus = publicKey.getModulus().toByteArray();
    	byte[] bpublic = publicKey.getPublicExponent().toByteArray();
    	byte[] bprivate = privateKey.getPrivateExponent().toByteArray();
    	
    	length.put(Convert.intToByte(bmodulus.length));
    	length.position(0);
    	key_channel.write(length);
    	ByteBuffer modulus = Misc.getByteBuffer(bmodulus.length);
    	modulus.put(bmodulus);
    	modulus.position(0);
    	key_channel.write(modulus);
    	
    	length.position(0);
    	length.put(Convert.intToByte(bpublic.length));
    	length.position(0);
    	key_channel.write(length);
    	ByteBuffer public_key = Misc.getByteBuffer(bpublic.length);
    	public_key.put(bpublic);
    	public_key.position(0);
    	key_channel.write(public_key);
    	
    	length.position(0);
    	length.put(Convert.intToByte(bprivate.length));
    	length.position(0);
    	key_channel.write(length);
    	
    	ByteBuffer private_key = Misc.getByteBuffer(bprivate.length);
    	private_key.put(bprivate);
    	private_key.position(0);
    	key_channel.write(private_key);
    	
    	key_channel.close();
    }
    
	public void addConfigurationListener(ConfigurationListener listener) {
		  config_listeners.add( listener );
	}
	
	public void removeConfigurationListener(ConfigurationListener listener) {
      config_listeners.remove( listener );
	}
	
	
	private void notifyPropertyChanged(String property, Object new_value) {
		
		for(ConfigurationListener listener : config_listeners) {
			
			try {
				    if( property == NICK_NAME_KEY ) listener.nickNameChanged((String)new_value); 
				    
			   else if( property == TCP_PORT_KEY ) listener.TCPPortChanged((Integer)new_value);
				    
			   else if( property == UDP_PORT_KEY ) listener.UDPPortChanged((Integer)new_value);
				    
			   else if( property == DOWNLOAD_BANDWIDTH_KEY ) listener.downloadBandwidthChanged((Long)new_value);
				    
			   else if( property == UPLOAD_BANDWIDTH_KEY ) listener.uploadBandwidthChanged((Long)new_value);
				    
			   else if( property == UDP_ENABLED_KEY ) listener.isUDPEnabledChanged((Boolean)new_value);
				    
			   else if( property == SHARED_DIRECTORIES_KEY) listener.sharedDirectoriesChanged((List<File>)new_value);
				    
			   else if( property == WORKING_DIR_KEY ) listener.workingDirChanged((File)new_value);
				    
			   else if( property == DOWNLOAD_LIMIT_KEY) listener.downloadLimitChanged((Long)new_value);
				    
			   else if( property == UPLOAD_LIMIT_KEY) listener.uploadLimitChanged((Long)new_value);
				    
			   else if( property == JKAD_ENABLED_KEY) listener.jkadStatusChanged((Boolean)new_value);
				    
			   else if( property == SERVER_LIST_UPDATE_ON_CONNECT_KEY) listener.updateServerListAtConnectChanged((Boolean)new_value);
			   
			   else if( property == JKAD_ID_KEY) listener.jkadIDChanged((ClientID) new_value);
				    
			   else if( property == NIC_NAME_KEY) listener.nicNameChanged((String) new_value);
				    
			   else if( property == NIC_IP_KEY) listener.nicIPChanged((String) new_value);
			   
			   else if (property == SECURITY_IDENTIFICATION ) listener.securityIdentificationStatusChanged((Boolean)new_value);
				    
			}catch(Throwable cause) {
				cause.printStackTrace();
			}
			
		}
		
	}
	

}
