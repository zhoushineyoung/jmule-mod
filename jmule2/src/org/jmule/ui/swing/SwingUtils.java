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
package org.jmule.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JFrame;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/10/16 17:35:11 $$
 */
public final class SwingUtils {

    /**
     * Centers a window on screen.
     * <p>
     * @param w     The window to center.
     */
    public static void centerOnScreen(Window w) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension splashSize = w.getPreferredSize();
        w.setLocation(screenSize.width / 2 - (splashSize.width / 2),
                      screenSize.height / 2 - (splashSize.height / 2));
    }
    
    // Center Window on screen
    public static void centerAndSizeWindow( Window win, int fraction, int base) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width  = screenSize.width * fraction / base;
        int height = screenSize.height * fraction / base;
    
        Rectangle rect = new Rectangle( (screenSize.width - width) / 2,
            (screenSize.height - height) / 2, width, height );
        win.setBounds(rect);
    }
    
    /**
     * Maximizes a JFrame, just like the 'maximize window' button does.
     * <p>
     * @param f     The frame to maximize.
     */
    public static void maximizeJFrame(JFrame f) {
    	
        f.setExtendedState( Frame.MAXIMIZED_BOTH );
    }
    
    public static void setWindowLocationRelativeTo( Window window, Window relativeWindow ) {
        Rectangle windowBounds = window.getBounds();
        Dimension rwSize = relativeWindow.getSize();
        Point rwLoc = relativeWindow.getLocation();

        int dx = rwLoc.x + (( rwSize.width - windowBounds.width ) >> 1 );
        int dy = rwLoc.y + (( rwSize.height - windowBounds.height ) >> 1 );
        Dimension ss = window.getToolkit().getScreenSize();

        if ( dy + windowBounds.height > ss.height)
        {
             dy = ss.height - windowBounds.height;
             dx = rwLoc.x < ( ss.width >> 1 ) ? rwLoc.x + rwSize.width :
                  rwLoc.x - windowBounds.width;
        }
        if ( dx + windowBounds.width > ss.width )
        {
             dx = ss.width - windowBounds.width;
        }
        if (dx < 0)
        {
            dx = 0;
        }
        if (dy < 0)
        {
            dy = 0;
        }
        window.setLocation( dx, dy );
    }
    
    /**
     * Creates a new <code>Color</code> that is a brighter version of this
     * <code>Color</code>. This method is the same implementation
     * java.awt.Color#brighter is usind except it has a configurable factor.
     * The java.awt.Color default facotr is 0.7
     * @return     a new <code>Color</code> object that is  
     *                 a brighter version of this <code>Color</code>.
     * @see        java.awt.Color#darker
     */
    public static Color brighterColor( Color color, double factor ) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int)(1.0/(1.0-factor));
        if ( r == 0 && g == 0 && b == 0) {
           return new Color(i, i, i);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return new Color(Math.min((int)(r/factor), 255),
                         Math.min((int)(g/factor), 255),
                         Math.min((int)(b/factor), 255));
    }
    
    /**
     * Creates a new <code>Color</code> that is a darker version of this
     * <code>Color</code>. This method is the same implementation
     * java.awt.Color#darker is usind except it has a configurable factor.
     * The java.awt.Color default facotr is 0.7
     * @return  a new <code>Color</code> object that is 
     *                    a darker version of this <code>Color</code>.
     * @see        java.awt.Color#brighter
     */
    public static Color darkerColor( Color color, double factor )
    {
        return new Color(Math.max((int)(color.getRed()  * factor), 0), 
             Math.max((int)(color.getGreen()* factor), 0),
             Math.max((int)(color.getBlue() * factor), 0));
    }
	
}
