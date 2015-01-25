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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2009/07/05 08:02:02 $$
 */
public class FileUtils {

	private Logger log = Logger.getLogger(FileUtils.class.toString());
	
	 /**
     * Checks, whether the child directory is a subdirectory of the base directory.
     *
     * @param base the base directory.
     * @param child the suspected child directory.
     * @return true, if the child is a subdirectory of the base directory.
     * @throws IOException if an IOError occured during the test.
     */
    public static boolean isSubDirectory(File base, File child)
        throws IOException {
        base = base.getCanonicalFile();
        child = child.getCanonicalFile();

        File parentFile = child;
        while (parentFile != null) {
            if (base.equals(parentFile)) {
                return true;
            }
            parentFile = parentFile.getParentFile();
        }
        return false;
    }
    
    public static List<File> extractNewFolders(File[] current_chosen_folders, List<File> chosen_folders,List<File> mustAddFolders) {
    	List<File> already_existed_folders = new LinkedList<File>();
    	for(File f1 : current_chosen_folders) {
			boolean must_be_added = true;
			for(File f2 : chosen_folders) {
			  try {	
			    if(FileUtils.isSubDirectory(f2, f1)) {
				    must_be_added = false;
				    break;
				       // we remove the folders from chosen_folders if the current
				       // chosen folder is the parent of some of the folders from chosen_folders
			    } else if( f1 != f2 && FileUtils.isSubDirectory(f1, f2) ) {
			    	chosen_folders.remove(f2);
			    	already_existed_folders.add( f2 );
			    }
			  } catch(Throwable t) { t.printStackTrace(); }
			}
			if( must_be_added ) mustAddFolders.add( f1 );
			else already_existed_folders.add( f1 );
		}
    	return already_existed_folders;
    }
    
    /**
     * 
     * @param dir
     * @return a list of files
     */
    public static List<File> traverseDirAndReturnListOfFiles(File dir) {
    	List<File> result_as_list = new ArrayList<File>();
    	Queue<File> list_of_dirs = new LinkedList<File>();
    	File[] list_of_files;
    	File current_dir;
    	
    	list_of_dirs.offer( dir );
    	while( list_of_dirs.size() != 0 ) {
    		current_dir = list_of_dirs.poll(); 
    		list_of_files = current_dir.listFiles();
    		for(File file : list_of_files) {
    			if( file.isDirectory() ) list_of_dirs.offer( file );
    			else result_as_list.add( file );
    		}
    	}
    	
    	return result_as_list;    	
    }
	
}
