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
package org.jmule.ui.swing.common;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.sharingmanager.Gap;
import org.jmule.core.sharingmanager.GapList;

/**
 *
 * Created on Oct 6, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/16 17:35:11 $
 */
public class PiecesPanel extends JPanel {

	private DownloadSession session;
	
	public PiecesPanel(DownloadSession session) {
		this.session = session;
        init();
	}
	
	public void setSession(DownloadSession session) {
		this.session = session;
		init();
	}
	
	private void init() {
		this.setBackground(new Color(0,128,255));
		this.setForeground(Color.BLACK);
	}
	
	public void paint(Graphics g) {
		  super.paint(g);
		 
         long file_size = session.getFileSize();
         long transferred_bytes_count = session.getTransferredBytes();
         GapList gap_list = session.getGapList();
         
		 float total_length = this.getWidth();
		 float pixels_for_byte = total_length / (float)file_size; 
		 
		 g.setColor(Color.WHITE);
		 
	     g.fillRect(0, 0, this.getWidth(), 4);
		 
	     g.setColor(new Color(0,100,199)); // black blue
	     
	     g.fillRect(0, 0, Math.round((transferred_bytes_count * this.getWidth())/file_size), 4);
	     
		 //g.setColor(Color.GRAY);
		 //System.out.println(pixels_for_byte);
		  //g.drawLine(0, 6, this.getWidth(), 6);
			// g.fillRect(2, 2, this.getWidth(), 7);
		  g.setColor(Color.WHITE);
		  //g.fillRect(0, 1, this.getWidth(), 1);
		  g.drawLine(0, 3, this.getWidth(), 3);
		  for(Gap gap : gap_list.getGaps()) {
			  g.fillRect( Math.round(gap.getStart()*pixels_for_byte), 
					     4, 
					     Math.round(gap.getEnd()*pixels_for_byte - gap.getStart()*pixels_for_byte), 
					     this.getHeight());
		  }
	}
	
}
