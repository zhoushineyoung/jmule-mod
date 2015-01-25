/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Created on Aug 11, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/08/11 12:06:48 $
 */
public class MiscTest {

	@Test
	public void testIsHexadecimalNumber() {
		assertTrue(Misc.isHexadecimalNumber("11"));
		assertTrue(Misc.isHexadecimalNumber("BA1C"));
		assertTrue(Misc.isHexadecimalNumber("BA1CAAFF1264"));
		assertFalse(Misc.isHexadecimalNumber("BA1CAAFF12641"));
		assertFalse(Misc.isHexadecimalNumber("11ll335pp"));
		assertFalse(Misc.isHexadecimalNumber(""));
		assertTrue(Misc.isHexadecimalNumber("00aFb12f"));
		assertTrue(Misc.isHexadecimalNumber("aabbccddeeff"));
	}

}

