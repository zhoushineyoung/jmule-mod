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
package org.jmule.ui.swing.maintabs.statistics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * Created on Oct 12, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/16 17:35:11 $
 */
public class JavaNonHeapMemoryPanel extends JPanel {

	private JLabel non_heap_memory_label = new JLabel("Non heap memory");
	public JLabel maximum_non_heap_memory_value = new JLabel();
	public JLabel used_non_heap_momory_value = new JLabel();
	public JLabel initial_non_heap_memory_value = new JLabel();
	private JLabel maximum_non_heap_memody_label = new JLabel("Maximum non heap memory");
	private JLabel used_non_heap_memory_label = new JLabel("Use non heap memory");
	
	public JavaNonHeapMemoryPanel() {
		
		init();
	}
	
	private void init() {
		GridBagLayout thisLayout = new GridBagLayout();
		this.setBorder(BorderFactory.createTitledBorder("Non heap memory"));
		this.setPreferredSize(new java.awt.Dimension(359, 95));
		thisLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		thisLayout.rowHeights = new int[] {18, 19, 19, 7};
		thisLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		thisLayout.columnWidths = new int[] {7, 175, 18, 7};
		this.setLayout(thisLayout);
	    this.add(non_heap_memory_label, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(used_non_heap_memory_label, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(maximum_non_heap_memody_label, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(initial_non_heap_memory_value, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(used_non_heap_momory_value, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(maximum_non_heap_memory_value, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));		
	}
	
}
