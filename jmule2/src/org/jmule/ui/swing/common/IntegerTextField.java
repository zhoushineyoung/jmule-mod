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

import java.awt.Toolkit;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * @see http://forums.sun.com/thread.jspa?threadID=620999&messageID=3500774
 * Created on Sep 21, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/09/25 17:59:46 $
 */ 
public class IntegerTextField extends JTextField {
 
    private long maxValue = Long.MAX_VALUE;
    private long minValue = 0;
    private int maxLength = String.valueOf(maxValue).length();
    private boolean isIPField = false;
 
    public IntegerTextField() {
	    super();
    }
 
    protected Document createDefaultModel() {
	    return new IntegerDocument();
    }
 
    public void setMinValue(long value) {
	    minValue = value;
    }
 
    public long getMinValue() {
	    return minValue;
    }
 
    public void setIPField(boolean value) {
	    isIPField = value;
    }
 
    public boolean getIPField() {
	    return isIPField;
    }
 
    public void setMaxValue(long value) {
	    maxValue = value;
    }
 
    public long getMaxValue() {
	    return maxValue;
    }
 
    public void setMaxLength(int value) {
	    maxLength = value;
    }
 
    public int getMaxLength() {
	    return maxLength;
    }
 
    private class IntegerDocument extends PlainDocument {
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		
	    long typedValue = -1;
 
  	    StringBuffer textBuffer = new StringBuffer(IntegerTextField.this.getText().trim());
  	    //The offset argument must be greater than or equal to 0, and less than or equal to the length of this string buffer
  	    if((offs >= 0) && (offs <= textBuffer.length()))
  	    {
  	    	    textBuffer.insert(offs,str);
	    		String textValue = textBuffer.toString();
	    		if(textBuffer.length() > maxLength)
	    		{
					JOptionPane.showMessageDialog(IntegerTextField.this, "The number of characters 	must be less than or equal to " + getMaxLength(), "Error Message",JOptionPane.ERROR_MESSAGE);
					return;
	    		}
 
				if((textValue == null) || (textValue.equals("")))
				{
					remove(0,getLength());
					super.insertString(0, "", null);
					return;
				}
 
				if(textValue.equals("-") && minValue < 0)
				{
					super.insertString(offs,new String(str), a);
					return;
				}
 
				if(str.equals(".") && isIPField)
				{
					super.insertString(offs,new String(str),a);
					return;
				}
				else
				{
					try
					{
						typedValue = Long.parseLong(textValue);
						if((typedValue > maxValue) || (typedValue < minValue))
						{
							JOptionPane.showMessageDialog(IntegerTextField.this, "The value can only be from "+getMinValue()+" to " + getMaxValue(), "Error Message", JOptionPane.ERROR_MESSAGE);
						}
						else
						{
							super.insertString(offs,new String(str),a);
						}
					}
					catch(NumberFormatException ex)
					{
						Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(IntegerTextField.this, "Only numeric values allowed.", "Error Message", JOptionPane.ERROR_MESSAGE);
					}
				}
		}
    }
}
}
