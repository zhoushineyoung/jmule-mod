/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.platform;

import java.io.File;

/**
 * Created on Aug 31, 2009
 * @author javajox
 * @version $Revision: 1.2 $
 * Last changed by $Author: javajox $ on $Date: 2009/10/25 08:36:11 $
 */
public abstract class UnixPlatformManager extends AbstractPlatformManager {

	private void copyOrMoveFile(String command, File source, File destination) throws PlatformManagerException {
		int exit_status;
		try {
			 ProcessBuilder mv_or_cp_cmd = new ProcessBuilder(new String[] { command, source.getAbsolutePath(), destination.getAbsolutePath() });
			 Process mv_or_cp_cmd_running = mv_or_cp_cmd.start();
			 exit_status = mv_or_cp_cmd_running.waitFor();
		}catch( Throwable cause ) {
			throw new PlatformManagerException( cause );
		}
		if( exit_status != 0 ) 
			  throw new PlatformManagerException(PROCESS_ERROR + exit_status);
	}
	
	public void copyFile(File source, File destination) throws PlatformManagerException {
		copyOrMoveFile( "cp", source, destination );
	}
	
	public void moveFile(File source, File destination) throws PlatformManagerException {
        copyOrMoveFile( "mv", source, destination );
	}	
	

}
