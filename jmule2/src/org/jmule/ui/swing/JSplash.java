/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2008 JMule team ( jmule@jmule.org / http://jmule.org )
 *
 *  Any parts of this program derived from the Apache Commons, 
 *  Phex, Zeus-jscl, AspectJ or JDIC project, or contributed 
 *  by third-party developers are copyrighted by their
 *  respective authors.
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
package org.jmule.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import org.jmule.core.JMRunnable;
import org.jmule.ui.Splash;

/**
 * A simple, yet nice splash screen implementation for java applications.
 * Follows Sun recommendations for splash screen and logos: see 
 * <a href="http://java.sun.com/products/jlf/ed2/book/HIG.Graphics7.html">
 * <i>"Designing Graphics for Corporate and Product Identity"</i></a>.
 * Draws a black border of one pixel wide around the splash image.
 * Also uses a simple progress bar that the user must "progress" manually in his
 * code in order for it to work. Also, it has options for percent display,
 * custom loading messages display and application version string display at the
 * bottom-right corner of the image.
 * <p>
 * @author Gregory Kotsaftis
 * @author javajox
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/05/15 18:22:31 $$
 */
public final class JSplash extends JWindow implements Splash { 

    /**
     * Progress bar to use in the splash screen.
     */
    private JProgressBar m_progress = null;

    /**
     * Check for whether to use the progress bar or not.
     */
    private boolean m_progressBar = false;

    /**
     * Check for whether to use progress bar messages or not.
     */
    private boolean m_progressBarMessages = false;

    /**
     * Check for whether to use precentage values or not.
     */
    private boolean m_progressBarPercent = false;

    private int total_progress = 0;

    //public JSplash(URL url, 
    //    boolean progress, boolean messages, boolean percent,
    //    String versionString, Font versionStringFont, Color versionStringColor)
    public JSplash()
    {
        super();
        URL url = JSplash.class.getClassLoader().getResource("org/jmule/ui/resources/splash/splash_logo.png");
        m_progressBar = true;
        m_progressBarMessages = true;
        m_progressBarPercent = false;

        this.setAlwaysOnTop(false);
        
        // build a panel with a black line for border, 
        // and set it as the content pane
        //
        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );
        panel.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        setContentPane( panel );
        
        // build a label and set it's icon
        //
        JSplashLabel label = new JSplashLabel(url, 
                null, null, null);
        
        // build a progress bar
        //
        if( m_progressBar )
        {
            m_progress = new JProgressBar();

            if( m_progressBarMessages || m_progressBarPercent )
            {
                m_progress.setStringPainted( true );
            }
            else
            {
                m_progress.setStringPainted( false );
            }
            
            if( m_progressBarMessages && !m_progressBarPercent )
            {
                m_progress.setString( "" );
            }
            
            m_progress.setMaximum( 100 );
            m_progress.setMinimum( 0 );
            m_progress.setValue( 0 );
        }

        // add the components to the panel
        //
        getContentPane().add(label, BorderLayout.CENTER);
        
        if( m_progressBar )
        {
            getContentPane().add(m_progress, BorderLayout.SOUTH);
        }
        
        // validate, and display the components
        pack();
        
        // center on screen
        SwingUtils.centerOnScreen( this );
        
        // hide the panel for now...
        setVisible( false );
    }
    
    
    /**
     * Displays the splash screen
     */
    public void splashOn()
    {
    	try {
			SwingUtilities.invokeAndWait(new JMRunnable() {
				public void JMRun() {
					setVisible( true );
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
    }
    
    
    /**
     * Hides and disposes the splash screen
     */
    public void splashOff()
    {
    	
    	try {
			SwingUtilities.invokeAndWait(new JMRunnable() {
				public void JMRun() {
					setVisible( false );
			        dispose();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		} 
		
        
    }

    
    /**
     * Sets the progress indicator (values: 0 - 100).
     * <p>
     * @param value     The progress indicator value.
     */
    private void setProgress(int value)
    {
        if( m_progressBar && value>=0 && value<=100 )
        {
            m_progress.setValue( value );
        }
    }

    
    /**
     * Sets the progress indicator (values: 0 - 100) and a label to print
     * inside the progress bar.
     * <p>
     * @param value     The progress indicator value.
     * @param msg       The message to print.
     */
    private void setProgress(final int value, final String msg)
    {
    	try {
			SwingUtilities.invokeAndWait(new JMRunnable() {
				public void JMRun() {
					 setProgress( value );

				        if( m_progressBarMessages && !m_progressBarPercent && msg!=null )
				        {
				            m_progress.setString( msg );
				        }
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
       
    }


	public void increaseProgress(int value) {
		
		total_progress += value;
		setProgress( total_progress );
	}


	public void increaseProgress(int value, String message) {
		
		total_progress += value;
		setProgress( total_progress, message );
	}


	public void resetProgress() {
		
		total_progress = 0;
		setProgress( total_progress );
	}
    
}
