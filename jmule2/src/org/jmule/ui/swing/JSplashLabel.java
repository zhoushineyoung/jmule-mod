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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.MediaTracker;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Extends JLabel to provide support for custom text drawing inside image used
 * for JSplash component.
 * <p>
 * @author Gregory Kotsaftis
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:02 $$
 */
public final class JSplashLabel extends JLabel {
    
    /**
     * Used to draw the text string.
     */
    private String m_text = null;

    /**
     * Font to use when drawing the text.
     */
    private Font m_font = null;

    /**
     * Colour to use when drawing the text.
     */
    private Color m_color = null;
    
    
    /**
     * Constructor.
     * <p>
     * @param url   The location of the image (<b>it cannot be null</b>).
     * @param s     The string to draw (can be null).
     * @param f     The font to use (can be null).
     * @param c     The color to use (can be null).
     */
    public JSplashLabel(URL url, String s, Font f, Color c)
    {
        super();
        
        ImageIcon icon = new ImageIcon( url );
        if( icon.getImageLoadStatus()!=MediaTracker.COMPLETE )
        {
            System.err.println("Cannot load splash screen: " + url);
            setText("Cannot load splash screen: " + url);
        }
        else
        {
            setIcon( icon );
            m_text = s;
            m_font = f;
            m_color = c;
            
            if( m_font!=null )
            {
                setFont( m_font );
            }
        }
    }
    

    /**
     * Overrides paint in order to draw the version number on the splash screen.
     * <p>
     * @param g     The graphics context to use.
     */
    public void paint(Graphics g)
    {
        super.paint(g);
        
        if( m_text!=null )
        {
            if( m_color!=null )
            {
                g.setColor( m_color );
            }            
            
            FontMetrics fm = g.getFontMetrics();
            int width = fm.stringWidth(m_text) + 20;
            int height = fm.getHeight();
            
            g.drawString(m_text, getWidth() - width, getHeight() - height);
        }
    }
    
}
