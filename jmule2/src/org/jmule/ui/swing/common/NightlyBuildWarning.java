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
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jmule.ui.swing.ImgRep;

/**
 *
 * Created on Sep 25, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/16 17:35:11 $
 */
public class NightlyBuildWarning extends JLabel {
	
	public NightlyBuildWarning() {
		
		this.setForeground(Color.RED);
        this.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        this.setIcon(ImgRep.getIcon("bomb.png"));
        this.setText("<html><center>Attention !!! This is highly unstable nightly build of JMule,<br> your feedback on our forums FORUM.JMULE.ORG<br> will be greatly appreciated</center></html>");
        this.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        this.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		
	}
	
}
