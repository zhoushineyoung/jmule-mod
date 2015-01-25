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
package org.jmule.core.aspects;

import java.util.logging.Logger;

import org.jmule.core.edonkey.metfile.PartMet;
import org.jmule.core.edonkey.metfile.PartMetException;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.utils.Misc;
/**
 * 
 * @author binary256
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/01/28 13:17:40 $$
 */
public privileged aspect PartialFileLogger {
	private Logger log = Logger.getLogger("org.jmule.core.sharingmanager.PartialFile");
	
	after() throwing (Throwable t): execution (* PartialFile.*(..)) {
		log.warning(Misc.getStackTrace(t));
	}
	
	after(PartMet part_met) throwing(PartMetException e) : target(part_met) && call (void PartMet.writeFile()) {
		log.warning("Failed to write data in file "+part_met.getName() );
	}
	
	after(PartialFile pFile) returning(boolean result) : target(pFile) && execution(boolean PartialFile.checkFullFileIntegrity()) {
		
		if (!pFile.hasHashSet()) {
			log.warning("Can't check file integrity don't have part hash set\n" + pFile);
			return ;
		}
		
		if (!result)
			log.warning("Broken file : \n"+pFile+"\n redownload");
	}
	
	after(PartialFile pFile) returning(boolean result) : target(pFile) && execution(boolean PartialFile.checkFilePartsIntegrity()) {
		if (!pFile.hasHashSet()) {
			log.warning("Can't check file integrity don't have part hash set\n"+pFile);
			return ;
		}
		if (!result)
			log.warning("Broken file : \n"+pFile+"\n mark for redownload bad pars\nGapList : "+pFile.getGapList());
	}
}
