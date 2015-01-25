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
package org.jmule.core.utils;

import static org.jmule.core.edonkey.ED2KConstants.PARTSIZE;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/07/31 13:08:35 $$
 */
public class MD4FileHasher {
	
	public static PartHashSet calcHashSets(FileChannel fileChannel) {
	    try {
	    	fileChannel.position(0);
            int i;
            int c;
            long position = 0L;
            ByteBuffer hashset;
            MD4 msgDigest = new MD4();
            ByteBuffer bb = ByteBuffer.allocateDirect(8192).order(ByteOrder.LITTLE_ENDIAN);
            ByteBuffer di = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
            
            c = (int) ((fileChannel.size() + PARTSIZE - 1) / PARTSIZE);
            
            // we will need space for c Parts
            hashset = ByteBuffer.allocate(16 * (c>0?c:1)).order(ByteOrder.LITTLE_ENDIAN);
            for (i = 1; i < c; i++) {
                while (position <= (i * PARTSIZE - bb.capacity())) {
                    position+=fileChannel.read(bb, position);
                    bb.flip();
                    msgDigest.update(bb);
                    bb.rewind();
                }
            
                if (position < (i * PARTSIZE)) {
                    bb.limit((int) ((i * PARTSIZE) - position));
                    position+=fileChannel.read(bb, position);
                    bb.flip();
                    msgDigest.update(bb);
                    bb.rewind();
                }
                 hashset.limit(16 * i);
                 //update hashset - add a hash
                 msgDigest.finalDigest(hashset);
            }
            if (c > 0) {
            	
                while (position < (fileChannel.size())) {
                    position+=fileChannel.read(bb, position);
                    bb.flip();
                    msgDigest.update(bb);
                    bb.rewind();
                }
                hashset.limit(16 * i);
            }
            msgDigest.finalDigest(hashset);
           
            hashset.flip();
            if (c > 1) {
                msgDigest.update(hashset);
                msgDigest.finalDigest(di);
            } else {
                di.put(hashset);
            }
            
            di.rewind();
            hashset.rewind();
            //hashes 
            byte[][] hashes = new byte[(c!=1)?(c + 1):1][16];
            di.get(hashes[0]);
            for (int j = 1; j < hashes.length; j++) {
                hashset.get(hashes[j]);
            }
            hashset.rewind();
                       
            PartHashSet fileHashSet = new PartHashSet(new FileHash(hashes[0]));
            for(int j=1; j < hashes.length;j++){
            	fileHashSet.add(hashes[j]);
            }
            return fileHashSet;
            
        } catch (Throwable e) {e.printStackTrace();}
		return null;
		
	}
}
