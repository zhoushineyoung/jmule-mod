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
package org.jmule.ui.swing.wizards;

import javax.swing.JPanel;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.swing.skin.WizardSkin;
import org.jmule.ui.swing.skin.WizardSkinFactory;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:11 $$
 */
public class WizardPanel extends JPanel implements JMuleUIComponent {

	protected WizardSkin skin = WizardSkinFactory.getInstance();
	protected JMuleCore _core;
	protected ConfigurationManager _config;
	
	public WizardPanel() {
		
		getCoreComponents();
		
		initUIComponents();
		
	}

	public void getCoreComponents() {
		
	   try {
		   
		   _core = JMuleCoreFactory.getSingleton();
		 
	   } catch(Throwable t) {
		   
	   }
	   
	   _config = _core.getConfigurationManager();
	}

	public void initUIComponents() {
		
	}

	
}
