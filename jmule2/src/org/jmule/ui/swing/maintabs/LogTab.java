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
package org.jmule.ui.swing.maintabs;

import java.awt.GridLayout;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jmule.ui.utils.TimeFormatter;

/**
 *
 * Created on Oct 11, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/16 17:35:11 $
 */
public class LogTab extends AbstractTab implements JMLogger {

	private JScrollPane scroll_panel = new JScrollPane();
	private JTextArea text_area = new JTextArea();
	
	private static JMLogger log_instance = null;
	
	public LogTab(JFrame parent) {
		super(parent);
        log_instance = (JMLogger)this;
		init();
	}
	
	public static JMLogger getLogInstance() {
		
        return log_instance;
	}
	
	private void init() {
		this.setLayout(new GridLayout(1,1));
		text_area.setEditable(false);
		scroll_panel.setViewportView(text_area);
		this.add(scroll_panel);
	}
	
	public void addMessage(String str) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(System.currentTimeMillis());
		String date = TimeFormatter.twoDigits(calendar.get(Calendar.DAY_OF_MONTH)) + "." + TimeFormatter.twoDigits(calendar.get(Calendar.MONTH)) + "." + TimeFormatter.twoDigits(calendar.get(Calendar.YEAR));
		date += " "+TimeFormatter.twoDigits(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + TimeFormatter.twoDigits(calendar.get(Calendar.MINUTE))+":"+TimeFormatter.twoDigits(calendar.get(Calendar.SECOND));
		date = "[" + date + "]";
		text_area.append(" " + date + " " + str + "\n");
	}

}
