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
package org.jmule.core.speedmanager;

/**
 * A class that units a clamping bandwidth throttle with memory and a simple
 * current/avg. bandwith tracker.
 * Bandwidth generally does not exceed set value within a one second period.
 * Excess bandwidth is partially available for the
 * next period, exceeded bandwidth is fully unavailble for the next period.
 * @author gregork
 * @author binary
 * @see phex.common.bandwidth
 * @see http://phex.svn.sourceforge.net/viewvc/phex/phex/trunk/src/main/java/phex/common/bandwidth/BandwidthController.java?view=log
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/02 18:08:57 $$
 */
public class BandwidthController {
    private static final int ITERATION_PER_SECONDS = 5;//5
    private static final int MILLIS_PER_ITERATION = 1000/ITERATION_PER_SECONDS;
    
    /**
     * The number of bytes each iteration has.
     */
    private int bytesPerIteration;

    /**
     * The number of bytes left in the current iteration
     */
    private int bytesRemaining = 0;

    /**
     * The timestamp of the start of the current iteration.
     */
    private long lastIterationTime;

    /**
     * The maximal rate in bytes per second.
     */
    private long throttlingRate;

    /**
     * The name of this BandwidthController.
     */
    private final String controllerName;

    /**
     * Create a new bandwidth controller through acquireController()
     * @param controllerName the name of this BandwidthController.
     * @param throttlingRate the used throttling rate in bytes per second.
     */
    private BandwidthController(String controllerName, long throttlingRate)
    {
        this.controllerName = controllerName + " "
            + Integer.toHexString(hashCode());
        setThrottlingRate(throttlingRate);
        // init the bytes remaining on start to ensure correct stats on start.
        bytesRemaining = bytesPerIteration;
    }
    
    /**
     * Call to set the desired throttling rate.
     */
    public  void setThrottlingRate(long bytesPerSecond)
    {
        throttlingRate = bytesPerSecond;
        bytesPerIteration = (int) ((double) throttlingRate / (double) ITERATION_PER_SECONDS);
        if (bytesPerIteration == 0) bytesPerIteration = 9999;
        bytesRemaining = Math.min( bytesRemaining, bytesPerIteration );
    }
    
    /**
     * Returns the throttling rate in bytes per seconds
     * @return
     */
    public long getThrottlingRate()
    {
        return throttlingRate;
    }
    
    /**
     * Returns the name of this BandwidthController.
     * @return the name.
     */
    public String getName()
    {
        return controllerName;
    }

    public String toString() {
    	return "";
    }
    
    /**
     * Returns a BandwidthController object which can be used as a bandwidth
     * tracker and throttle.
     * @param controllerName the name of BandwidthController to create.
     * @param throttlingRate the used throttling rate in bytes per second.
     */
    public static BandwidthController acquireBandwidthController(
        String controllerName, long throttlingRate){
    	BandwidthController b = new BandwidthController(controllerName, throttlingRate);
    	return b;
        
    }
    
    /**
     * Returns the max number of bytes available through this bandwidth controller
     * and its parents.
     * @return
     */
    public int getAvailableByteCount( boolean blockTillAvailable ){
    	updateIteration( blockTillAvailable );
        int bytesAllowed = bytesRemaining;
        if ( bytesRemaining < 0 ){
        	while(bytesRemaining<0)
        		updateIteration(blockTillAvailable);
        	bytesAllowed = bytesRemaining;
        }
        return bytesAllowed;
    }
    
    /**
     * Returns the max number of bytes available through this bandwidth controller
     * and its parents.
     * @return
     */
    public int getAvailableByteCount( int maxToSend, 
        boolean blockTillAvailable ){
    	int i = getAvailableByteCount(blockTillAvailable);
        return Math.min( maxToSend, i );
    }
    
    /**
     * Marks bytes as used.
     * @param byteCount
     */
    public  void markBytesUsed( int byteCount ){
        bytesRemaining -= byteCount;
      // updateIteration( false );
        if  ( bytesRemaining < 0 )
        {
            updateIteration( true );
        }
    }
    
    private void updateIteration( boolean blockTillAvailable ){
        long elapsedIterationMillis;
        long now;
        while ( true ){
            now = System.currentTimeMillis();
            elapsedIterationMillis = now - lastIterationTime;
            if (elapsedIterationMillis >= MILLIS_PER_ITERATION ){
                // if last iteration used up too many bytes  
                if ( bytesRemaining < 0 ){
                    bytesRemaining += bytesPerIteration;
                }
                else {
                    bytesRemaining = bytesPerIteration; 
                }                
                lastIterationTime = now;
            }
            if ( !blockTillAvailable || bytesRemaining > 0 ) {
                return ;
            }
            try {
                Thread.sleep( Math.max( 
                    MILLIS_PER_ITERATION - elapsedIterationMillis, 0 ) );
            }
            catch (InterruptedException e) {
              return ;
            }
        }
    }

    
}
