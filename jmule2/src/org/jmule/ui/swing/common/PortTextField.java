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

import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

/**
 *
 * Created on Sep 14, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/09/14 17:31:10 $
 */
public class PortTextField extends JFormattedTextField {

	private static DecimalFormat decimal_formater = new DecimalFormat("#####");
    private static NumberFormatter number_formatter = new NumberFormatter(decimal_formater) {
        public String valueToString(Object iv) throws ParseException {
            if ((iv == null) || (((Integer)iv).intValue() == -1)) {
                return "";
            }
            else {
                return super.valueToString(iv);
            }
        }
        public Object stringToValue(String text) throws ParseException {
            if ("".equals(text)) {
                return null;
            }
            return super.stringToValue(text);
        }
    };
	
    static {
	    number_formatter.setMinimum(1);
	    number_formatter.setMaximum(65535);
	    number_formatter.setValueClass(Integer.class);
    }
	
	public PortTextField() {
		super(number_formatter);
	    this.setHorizontalAlignment(javax.swing.JTextField.CENTER);
	    this.setColumns(5);
	}
}
