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
package org.jmule.ui.swing.models;

import javax.swing.table.AbstractTableModel;

import org.jmule.core.searchmanager.SearchResult;
import org.jmule.core.searchmanager.SearchResultItemList;
import org.jmule.ui.utils.FileFormatter;

/**
 *
 * Created on Sep 10, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/16 17:35:11 $
 */
public class SearchResultTableModel extends AbstractTableModel { //extends JMTableModel

	private SearchResult search_result;
	private SearchResultItemList search_result_list; //= search_result.getSearchResultItemList();
	
	public final static int FILE_NAME        =    0;
	public final static int FILE_SIZE        =    1;
	public final static int AVAILABILITY     =    2;
	public final static int COMPLETE_SOURCES =    3;
	public final static int TYPE             =    4;
	public final static int FILE_ID          =    5;
	
	private final static String[] column_names = {
		                    "File name",
		                    "File size",
		                    "Availability",
		                    "Complete sources",
		                    "Type",
		                    "File ID"
	                    };

	
	public SearchResultTableModel(SearchResult searchResult) {
	

		//System.out.println("search_result_list = " + search_result.getSearchResultItemList());
		
		search_result = searchResult;
		
		search_result_list = search_result.getSearchResultItemList();
	
	}
	
	public Class getColumnClass(int col) {

        return String.class;
    }
	
	public int getRowCount() {
		
		//if(search_result_list == null) return 0;
		
		return search_result_list.size();
	}
	
	public Object getValueAt(int row, int col) {
		/*switch(col) {
		   case  FILE_NAME          :  return search_result_list.get(row).getFileName();
		   case  FILE_SIZE          :  return search_result_list.get(row).getFileSize();
		   case  AVAILABILITY       :  return search_result_list.get(row).getFileAviability();
		   case  COMPLETE_SOURCES   :  return search_result_list.get(row).getFileCompleteSrc();
		   case  TYPE               :  byte[] fileType = search_result_list.get(row).getMimeType();
			                           return FileFormatter.formatMimeType(fileType);
		   case FILE_ID             :  return search_result_list.get(row).getFileHash().getAsString();
		}*/
		return search_result_list.get(row);
	}
	
	public int getColumnCount() {
		  
	    return column_names.length;
	}
	
	public String getColumnName(int col) {
		  return column_names[col];
	}

}
