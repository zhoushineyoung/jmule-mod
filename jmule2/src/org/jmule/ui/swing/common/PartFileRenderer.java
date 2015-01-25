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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jmule.core.sharingmanager.GapList;

/**
 *
 * Created on Sep 29, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/16 17:35:11 $
 */
public class PartFileRenderer extends Canvas {

	class Pair {
		private int x;
		private int y;
		
		public Pair(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public void setX(int x) {
			this.x = x;
		}
		
		public void setY(int y) {
			this.y = y;
		}
	}
	
	private ArrayList<Pair> pairs = new ArrayList();
	private GapList gap_list;
	private Long file_size;
	
	public PartFileRenderer() {
		pairs.add(new Pair(10,13));
		pairs.add(new Pair(22,50));
		pairs.add(new Pair(56,60));
		pairs.add(new Pair(80,90));
		pairs.add(new Pair(100,101));
		//pairs.add(new Pair(0,1300));
	}
	
	public PartFileRenderer(GapList gapList, Long fileSize) {
		setGapList(gapList);
		setFileSize(fileSize);
	}
	
	public void setGapList(GapList gapList) {
		this.gap_list = gapList;
	}
	
	public void setFileSize(Long fileSize) {
		//this.file_size = fileSize;
	}
	
	public void fillRect(int x, int length) {
		
	}
	
	public void paint(Graphics g) {
		  super.paint(g);
		  this.setBackground(Color.BLUE);
		 // g = this.getGraphics();
		 
		 //g.drawLine(50, 50, 200, 200);
		 //g.fillRect(60, 0, 10, this.getHeight());
		 //g.fillRect(60+this.getWidth()-300+50, 0, this.getWidth()-300-120, this.getHeight());
		 //g.fillRect(x, y, width, height)
		 
		 float file_size = 1300; // in bytes
		 float total_length = this.getWidth();
		 float pixels_for_byte = total_length / file_size; 
		 
		 g.setColor(Color.WHITE);
		 
		 g.fillRect(0, 0, this.getWidth(), 5);
		 
		 g.setColor(Color.GRAY);
		 //System.out.println(pixels_for_byte);
		  g.drawLine(0, 6, this.getWidth(), 6);
		  g.setColor(Color.WHITE);
		  /*for(Gap gap : gap_list.getGaps()) {
			  g.fillRect( Math.round(gap.getStart()*pixels_for_byte), 
					     7, 
					     Math.round(gap.getEnd()*pixels_for_byte - gap.getStart()*pixels_for_byte), 
					     this.getHeight());
		  }*/
		  for(Pair p : pairs) {
			  g.fillRect( Math.round(p.getX()*pixels_for_byte), 
					     7, 
					     Math.round(p.getY()*pixels_for_byte - p.getX()*pixels_for_byte), 
					     this.getHeight());
			  // ---------------------------------------------------------
			  System.out.println(Math.round(p.getX()*pixels_for_byte));
			  System.out.println(Math.round(p.getY()*pixels_for_byte - p.getX()*pixels_for_byte));
		  }
	}
	
}
