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
 * This class calculates average transfer speed. It trackes the average over
 * a given period with a given refresh time.
 * It is based on a cyclic array of longs to store its values.
 * @author gregork
 * @author binary
 * @see phex.common.bandwidth
 * @see http://phex.svn.sourceforge.net/viewvc/phex/phex/trunk/src/main/java/phex/common/bandwidth/TransferAverage.java?view=log
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/07/06 09:02:29 $$
 */
public class TransferAverage
{
    /**
     * The refresh time for each slice in the cycle (in ms).
     */
    private int refreshRate;

    /**
     * The period of time to track (in s).
     */
    private int period;

    /**
     * The last update time factor used.
     */
    private long updateTimeFactor;

    /**
     * The array of values to calc the avg for.
     */
    private long values[];

    public TransferAverage( int refreshRate, int period )
    {
        if ( period * 1000 < refreshRate )
        {
            throw new IllegalArgumentException( "Invalid period" );
        }
        this.refreshRate = refreshRate;
        this.period = period;
        updateTimeFactor = System.currentTimeMillis() / refreshRate;
        
        // we have 2 extra elements one for the next value to fill and the other
        // is the currently filled value.
        int elementCount = (period * 1000) / refreshRate + 2;
        values = new long[elementCount];
    }

    /**
     * Updates and cleans the buffer of values.
     */
    private void update( long currentTimeFactor )
    {
        if ( updateTimeFactor < currentTimeFactor - values.length )
        {
            // last update is old.. skip to only erase buffer once.
            updateTimeFactor = currentTimeFactor - values.length - 1;
        }

        // clear all values between last updateTimeFactor and current.
        for ( long i = updateTimeFactor + 1; i <= currentTimeFactor; i++ )
        {
            values[(int) (i % values.length)] = 0;
        }
        // clear next
        values[(int) ((currentTimeFactor + 1) % values.length)] = 0;
                
        updateTimeFactor = currentTimeFactor;
    }

    /**
     * Adds a value to the average.
     */
    public void addValue( long value )
    {
        long currentTimeFactor = System.currentTimeMillis() / refreshRate;
        update( currentTimeFactor );
        values[(int) (currentTimeFactor % values.length)] += value;
    }

    /**
     * Returns the current average.
     */
    public long getAverage()
    {
        long currentTimeFactor = System.currentTimeMillis() / refreshRate;
        update( currentTimeFactor );

        long sum = 0;
        for ( long i = currentTimeFactor + 2; i < currentTimeFactor + values.length; i++ )
        {
            sum += values[(int) (i % values.length)];
        }
        return (sum / period);
    }
    
    public String toString()
    {
        return super.toString() + "Rate: " + refreshRate + " Period: " + period + " Avg: " + getAverage();
    }
}
