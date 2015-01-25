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


import java.awt.*;

import javax.swing.*;
import javax.swing.text.html.*;

/**
 * 
 * @author gregork
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:43 $$
 */
public class HTMLMultiLinePanel extends JEditorPane
{
	private String text;
	private Font label_font;
	private Color label_color;
	private Color label_bg_color;
	
    public HTMLMultiLinePanel(String text) {
        super();
        this.text = text;
        init();

        // adjust the used style sheet to match the label style..
        setFontAndColor( UIManager.getFont("Label.font"),
            UIManager.getColor("Label.foreground") );
        setBackground(UIManager.getColor("Label.background"));
    }
    
    public HTMLMultiLinePanel(String text, 
    		                  Font  labelFont,
    		                  Color labelColor,
    		                  Color labelBgColor) {
    	this.text = text;
    	this.label_font = labelFont;
    	this.label_color = labelColor;
    	this.label_bg_color = labelBgColor;
    	init();
    }
    
    private void init() {
    	
        setEditable(false);
        setContentType("text/html");
        setText(text);
        setBorder( null );
        // adjust the used style sheet to match the label style..
        setFontAndColor( label_font, label_color );
        setBackground( label_bg_color );
    }

    /**
     * Sets the default font and default color. These are set by
     * adding a rule for the body that specifies the font and color.
     * This allows the html to override these should it wish to have
     * a custom font or color.
     */
    private void setFontAndColor(Font font, Color fg)
    {
        StringBuffer rule = null;

        if (font != null)
        {
            rule = new StringBuffer("body { font-family: ");
            rule.append(font.getFamily());
            rule.append(";");
            rule.append(" font-size: ");
            rule.append(font.getSize());
            rule.append("pt");
            if (font.isBold())
            {
                rule.append("; font-weight: 700");
            }
            if (font.isItalic())
            {
                rule.append("; font-style: italic");
            }
        }
        if (fg != null)
        {
            if (rule == null)
            {
                rule = new StringBuffer("body { color: #");
            }
            else
            {
                rule.append("; color: #");
            }
            if (fg.getRed() < 16)
            {
                rule.append('0');
            }
            rule.append(Integer.toHexString(fg.getRed()));
            if (fg.getGreen() < 16)
            {
                rule.append('0');
            }
            rule.append(Integer.toHexString(fg.getGreen()));
            if (fg.getBlue() < 16)
            {
                rule.append('0');
            }
            rule.append(Integer.toHexString(fg.getBlue()));
        }
        if (rule != null)
        {
            rule.append(" }");
            try
            {
                StyleSheet style = ((HTMLDocument)getDocument()).getStyleSheet();                
                style.addRule(rule.toString());
            }
            catch (RuntimeException re)
            {
            }
        }
    }
}