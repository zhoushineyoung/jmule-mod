package org.jmule.core.bccrypto;

import java.io.InputStream;

abstract class LimitedInputStream
        extends InputStream
{
    protected final InputStream _in;

    LimitedInputStream(
        InputStream in)
    {
        this._in = in;
    }

    InputStream getUnderlyingStream()
    {
        return _in;
    }

    protected void setParentEofDetect(boolean on)
    {
       
    }
}
