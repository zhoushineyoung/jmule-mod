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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jmule.core.JMConstants;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMThread;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.ui.CommonUIPreferences;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.Splash;
import org.jmule.ui.swing.SwingConstants;
import org.jmule.ui.swing.SwingPreferences;
import org.jmule.ui.swing.SwingUtils;
import org.jmule.ui.swt.SWTPreferences;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2009/09/22 19:08:43 $$
 */
public class SetupWizard extends JDialog {
	
	private BorderLayout border_layout;
	private LeftLogo left_logo;
	private NavigationBar navigation_bar;
	private TopPanel top_panel;
	// for shutdown process we must have the core here
	private JMuleCore _core;
	
	// welcome message
	private JPanel stage1 = new WelcomeMessage();
	
	// shared folders set up
	private JPanel stage2 = new SharedFoldersChooser(this);
	
	// user name and ports
	private JPanel stage3 = new GeneralSettings();
	
	// network bandwidth selection
	private JPanel stage4 = new NetworkBandwidthSelection();
	
	// ui chooser
	private JPanel stage5 = new UIChooser();
	
	// finish !!! (congratulations you have been successfully configured JMule)
	private JPanel stage6 = new FinishPanel();
	
	private int current_stage = 1;
	
	Splash splash;
	
	public SetupWizard(Splash splash) {
		
		this.splash = splash;
		
		initComponents();
		
	}
	
	public SetupWizard() {
		
        initComponents();
        
	}
	
	private void initComponents() {
		
		try {
			
			 _core = JMuleCoreFactory.getSingleton();
			 
		}catch(Throwable t) {
				
		}
		
		top_panel = new TopPanel();
		border_layout = new BorderLayout();
		left_logo = new LeftLogo();
		navigation_bar = new NavigationBar( this );
		final SetupWizard _this = this;
		navigation_bar.getCancelButton().addActionListener( new ActionListener() {
        	
        	public void actionPerformed(ActionEvent event) {
        		 
        		 // we must stop the core if this is the first run (so we must shutdown the entire system)
        		 if( _core.isFirstRun() ) {
        			 
        			  try {
        				  
        				  _this.setVisible(false);
        				  
        				  _core.stop();
        				  
        				  
        			  }catch(Throwable t) {
        				  t.printStackTrace();
        			  }
        		 // close the wizard only if we have a fully functional appl.	 
        		 } else _this.setVisible(false);
        		
        	}
        });
		
		navigation_bar.getFinishButton().addActionListener(new ActionListener() {
			
			 public void actionPerformed(ActionEvent event) {
				
				_this.setVisible(false);
					
			   (new JMThread( new JMRunnable() {	 
				
				public void JMRun() {   
				 
					ConfigurationManager _config = _core.getConfigurationManager();
					GeneralSettings gs = null;
				    try {
						_config.setSharedFolders(((SharedFoldersChooser)stage2).getChosenFolders().getFoldersList());	 
					
						gs = (GeneralSettings)stage3;
					 
						_config.setNickName( gs.getNickName() );
					 	
						_config.setTCP( gs.getTCP() );
					 
						_config.setUDP( gs.getUDP() );
					 
						_config.setUDPEnabled( gs.isUDPEnabled() );
					 
						NetworkBandwidthSelection nbs = (NetworkBandwidthSelection)stage4;
					 
						_config.setDownloadBandwidth( nbs.getDownloadBandwidth() );
					 
						_config.setUploadBandwidth( nbs.getUploadBandwidth() );
						
						_config.save();
				    }catch( Throwable cause ) {
				    	cause.printStackTrace();
				    }
					CommonUIPreferences.getSingleton().setUIType( ((UIChooser)stage5).getChosenUI() );
				 
					//TODO modify this
					String our_ui = ((UIChooser)stage5).getChosenUI();
					
					if(our_ui.equals("SWT"))
						SWTPreferences.getInstance().setConnectAtStartup(gs.isConnectAtStartup());
					else if(our_ui.equals("SWING"))
						SwingPreferences.getSingleton().setConnectAtStartup(gs.isConnectAtStartup());
					
					CommonUIPreferences.getSingleton().save();
					
					 //splash.increaseProgress(5, "Starting JMule UI manager");

					if (_core.isFirstRun())
					
					try {
					 
						JMuleUIManager.create();
				    
					}catch(Throwable t) {
					 
						t.printStackTrace();
					}
				
					 //splash.splashOff();
				 
				}	
				 
			   })).start();	 
		    }
				 
		});
		
		this.setTitle( JMConstants.JMULE_FULL_NAME + " setup wizard" );
		this.setPreferredSize( SwingConstants.SETUP_WIZARD_DIMENSION );
		this.setSize( SwingConstants.SETUP_WIZARD_DIMENSION );
		this.setResizable( false );
		this.setLayout( border_layout );
		this.add( left_logo, BorderLayout.WEST );
		this.add( navigation_bar, BorderLayout.SOUTH );
		this.add( top_panel, BorderLayout.NORTH );
		SwingUtils.centerOnScreen( this );
        // so after the initialization we go to stage 1
		this.stage1();
		
		navigation_bar.getBackButton().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				--current_stage;
				execute_stage(current_stage);
			}
		});
		
		navigation_bar.getNextButton().addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				++current_stage;
				execute_stage(current_stage);
			}
		});
	}
	
	private void execute_stage(int stage) {
		switch(stage) {
		  case 1 : stage1(); break;
		  case 2 : stage2(); break;
		  case 3 : stage3(); break;
		  case 4 : stage4(); break;
		  case 5 : stage5(); break;
		  case 6 : stage6(); break;
		}
	}
	
	private void stage1() {
		navigation_bar.getBackButton().setEnabled(false);
		navigation_bar.getNextButton().setEnabled(true);
		// must be enabled all the time
		//navigation_bar.getCancelButton().setEnabled(true);
		navigation_bar.getFinishButton().setEnabled(false);
		left_logo.setVisible(true);
		top_panel.setVisible(false);
		//top_panel.setCaptionIcon(null);
		this.remove(stage2);
		this.add(stage1, BorderLayout.CENTER);
		stage1.updateUI();
		this.repaint();
	}
	
	private void stage2() {
		left_logo.setVisible(false);
		this.remove(stage1);
		this.remove(stage3);
		this.add(stage2, BorderLayout.CENTER);
		stage2.updateUI();
		top_panel.setVisible(true);
		top_panel.setCaption("Shared folders");
		//top_panel.setCaptionIcon(new javax.swing.ImageIcon("/home/javajox/work/workspace/JMule_local2/src/org/jmule/ui/resources/shared_files2.png"));
		this.navigation_bar.getBackButton().setEnabled(true);
		this.repaint();
	}
	
     private void stage3() {
		this.remove(stage2);
		this.remove(stage4);
		this.add(stage3, BorderLayout.CENTER);
		stage3.updateUI();
		top_panel.setVisible(true);
		top_panel.setCaption("User name and ports");
		//top_panel.setCaptionIcon(null);
		this.navigation_bar.getBackButton().setEnabled(true);
		this.navigation_bar.getNextButton().setEnabled(true);
		this.repaint();
	}
	
	private void stage4() {
		this.remove(stage3);
		this.remove(stage5);
		this.add(stage4, BorderLayout.CENTER);
		stage4.updateUI();
		top_panel.setCaption("Network bandwidth selection");
		this.navigation_bar.getBackButton().setEnabled(true);
		this.navigation_bar.getNextButton().setEnabled(true);
		this.navigation_bar.getFinishButton().setEnabled(false);
		this.repaint();
	}
	
	private void stage5() {
		this.top_panel.setVisible(true);
		this.remove(stage4);
		this.remove(stage6);
		this.add(stage5, BorderLayout.CENTER);
		stage5.updateUI();
		top_panel.setCaption("UI Chooser");
		this.navigation_bar.getFinishButton().setEnabled(false);
		this.navigation_bar.getNextButton().setEnabled(true);
		this.repaint();
	}
	
	public void stage6() {
		this.top_panel.setVisible(false);
		this.remove(stage5);
		this.add(stage6, BorderLayout.CENTER);
		stage6.updateUI();
		//top_panel.setCaption("Finish");
		this.navigation_bar.getNextButton().setEnabled(false);
		this.navigation_bar.getFinishButton().setEnabled(true);
		this.repaint();
	}

}
