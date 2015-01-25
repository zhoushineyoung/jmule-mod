package org.jmule.core.bccrypto;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import org.jmule.core.bccrypto.ASN1.*;

import static org.jmule.core.bccrypto.DER.*;
import static org.jmule.core.bccrypto.ASN1.*;

public class BER {

	
	public static class BERConstructedOctetString
    extends DER.DEROctetString
{
    private static final int MAX_LENGTH = 1000;

    /**
     * convert a vector of octet strings into a single byte string
     */
    static private byte[] toBytes(
        Vector  octs)
    {
        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();

        for (int i = 0; i != octs.size(); i++)
        {
            try
            {
                DER.DEROctetString  o = (DER.DEROctetString)octs.elementAt(i);

                bOut.write(o.getOctets());
            }
            catch (ClassCastException e)
            {
                throw new IllegalArgumentException(octs.elementAt(i).getClass().getName() + " found in input should only contain DEROctetString");
            }
            catch (IOException e)
            {
                throw new IllegalArgumentException("exception converting octets " + e.toString());
            }
        }

        return bOut.toByteArray();
    }

    private Vector  octs;

    /**
     * @param string the octets making up the octet string.
     */
    public BERConstructedOctetString(
        byte[]  string)
    {
        super(string);
    }

    public BERConstructedOctetString(
        Vector  octs)
    {
        super(toBytes(octs));

        this.octs = octs;
    }

    public BERConstructedOctetString(
        DER.DERObject  obj)
    {
        super(obj);
    }

    public BERConstructedOctetString(
        DER.DEREncodable  obj)
    {
        super(obj.getDERObject());
    }

    public byte[] getOctets()
    {
        return string;
    }

    /**
     * return the DER octets that make up this string.
     */
    public Enumeration getObjects()
    {
        if (octs == null)
        {
            return generateOcts().elements();
        }

        return octs.elements();
    }

    private Vector generateOcts()
    {
        int     start = 0;
        int     end = 0;
        Vector  vec = new Vector();

        while ((end + 1) < string.length)
        {
            if (string[end] == 0 && string[end + 1] == 0)
            {
                byte[]  nStr = new byte[end - start + 1];

                System.arraycopy(string, start, nStr, 0, nStr.length);

                vec.addElement(new DER.DEROctetString(nStr));
                start = end + 1;
            }
            end++;
        }

        byte[]  nStr = new byte[string.length - start];

        System.arraycopy(string, start, nStr, 0, nStr.length);

        vec.addElement(new DER.DEROctetString(nStr));

        return vec;
    }

    public void encode(
        DER.DEROutputStream out)
        throws IOException
    {
        if (out instanceof ASN1OutputStream)
        {
            out.write(CONSTRUCTED | OCTET_STRING);

            out.write(0x80);

            //
            // write out the octet array
            //
            if (octs != null)
            {
                for (int i = 0; i != octs.size(); i++)
                {
                    out.writeObject(octs.elementAt(i));
                }
            }
            else
            {
                for (int i = 0; i < string.length; i += MAX_LENGTH)
                {
                    int end;

                    if (i + MAX_LENGTH > string.length)
                    {
                        end = string.length;
                    }
                    else
                    {
                        end = i + MAX_LENGTH;
                    }

                    byte[]  nStr = new byte[end - i];

                    System.arraycopy(string, i, nStr, 0, nStr.length);

                    out.writeObject(new DER.DEROctetString(nStr));
                }
            }

            out.write(0x00);
            out.write(0x00);
        }
        else
        {
            super.encode(out);
        }
    }
}
	
	
	public static class BERConstructedSequence
    extends DER.DERConstructedSequence
{
    /*
     */
    void encode(
        DER.DEROutputStream out)
        throws IOException
    {
        if (out instanceof ASN1OutputStream)
        {
            out.write(SEQUENCE | CONSTRUCTED);
            out.write(0x80);
            
            Enumeration e = getObjects();
            while (e.hasMoreElements())
            {
                out.writeObject(e.nextElement());
            }
        
            out.write(0x00);
            out.write(0x00);
        }
        else
        {
            super.encode(out);
        }
    }
}

	
	
	public static class BERInputStream
    extends DER.DERInputStream
{
    private static final DER.DERObject END_OF_STREAM = new DER.DERObject()
    {
                                        void encode(
                                            DER.DEROutputStream out)
                                        throws IOException
                                        {
                                            throw new IOException("Eeek!");
                                        }
                                        public int hashCode()
                                        {
                                            return 0;
                                        }
                                        public boolean equals(
                                            Object o) 
                                        {
                                            return o == this;
                                        }
                                    };
    public BERInputStream(
        InputStream is)
    {
        super(is);
    }

    /**
     * read a string of bytes representing an indefinite length object.
     */
    private byte[] readIndefiniteLengthFully()
        throws IOException
    {
        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
        int                     b, b1;

        b1 = read();

        while ((b = read()) >= 0)
        {
            if (b1 == 0 && b == 0)
            {
                break;
            }

            bOut.write(b1);
            b1 = b;
        }

        return bOut.toByteArray();
    }

    private BERConstructedOctetString buildConstructedOctetString()
        throws IOException
    {
        Vector               octs = new Vector();

        for (;;)
        {
            DERObject        o = readObject();

            if (o == END_OF_STREAM)
            {
                break;
            }

            octs.addElement(o);
        }

        return new BERConstructedOctetString(octs);
    }

    public DERObject readObject()
        throws IOException
    {
        int tag = read();
        if (tag == -1)
        {
            throw new EOFException();
        }
    
        int     length = readLength();

        if (length < 0)    // indefinite length method
        {
            switch (tag)
            {
            case NULL:
                return null;
            case SEQUENCE | CONSTRUCTED:
                BERConstructedSequence  seq = new BERConstructedSequence();
    
                for (;;)
                {
                    DERObject   obj = readObject();

                    if (obj == END_OF_STREAM)
                    {
                        break;
                    }

                    seq.addObject(obj);
                }
                return seq;
            case OCTET_STRING | CONSTRUCTED:
                return buildConstructedOctetString();
            case SET | CONSTRUCTED:
                ASN1EncodableVector  v = new ASN1EncodableVector();
    
                for (;;)
                {
                    DERObject   obj = readObject();

                    if (obj == END_OF_STREAM)
                    {
                        break;
                    }

                    v.add(obj);
                }
                return new BERSet(v);
            default:
                //
                // with tagged object tag number is bottom 5 bits
                //
                if ((tag & TAGGED) != 0)  
                {
                    if ((tag & 0x1f) == 0x1f)
                    {
                        throw new IOException("unsupported high tag encountered");
                    }

                    //
                    // simple type - implicit... return an octet string
                    //
                    if ((tag & CONSTRUCTED) == 0)
                    {
                        byte[]  bytes = readIndefiniteLengthFully();

                        return new BERTaggedObject(false, tag & 0x1f, new DEROctetString(bytes));
                    }

                    //
                    // either constructed or explicitly tagged
                    //
                    DERObject        dObj = readObject();

                    if (dObj == END_OF_STREAM)     // empty tag!
                    {
                        return new DERTaggedObject(tag & 0x1f);
                    }

                    DERObject       next = readObject();

                    //
                    // explicitly tagged (probably!) - if it isn't we'd have to
                    // tell from the context
                    //
                    if (next == END_OF_STREAM)
                    {
                        return new BERTaggedObject(tag & 0x1f, dObj);
                    }

                    //
                    // another implicit object, we'll create a sequence...
                    //
                    seq = new BERConstructedSequence();

                    seq.addObject(dObj);

                    do
                    {
                        seq.addObject(next);
                        next = readObject();
                    }
                    while (next != END_OF_STREAM);

                    return new BERTaggedObject(false, tag & 0x1f, seq);
                }

                throw new IOException("unknown BER object encountered");
            }
        }
        else
        {
            if (tag == 0 && length == 0)    // end of contents marker.
            {
                return END_OF_STREAM;
            }

            byte[]  bytes = new byte[length];
    
            readFully(bytes);
    
            return buildObject(tag, bytes);
        }
    }
}

	
	
	public static class BERNull
    extends DER.DERNull
{
    public static final BERNull INSTANCE = new BERNull();

    public BERNull()
    {
    }

    void encode(
        DER.DEROutputStream  out)
        throws IOException
    {
        if (out instanceof ASN1OutputStream)
        {
            out.write(NULL);
        }
        else
        {
            super.encode(out);
        }
    }
}

	
	public static class BERSequence extends DERSequence
	{

	    public BERSequence()
	    {
	    }

	    /**
	     * create a sequence containing one object
	     */
	    public BERSequence(
	        DER.DEREncodable    obj)
	    {
	        super(obj);
	    }

	    /**
	     * create a sequence containing a vector of objects.
	     */
	    public BERSequence(
	        DEREncodableVector   v)
	    {
	        super(v);
	    }

	    /*
	     */
	    void encode(
	        DEROutputStream out)
	        throws IOException
	    {
	        if (out instanceof ASN1OutputStream)
	        {
	            out.write(SEQUENCE | CONSTRUCTED);
	            out.write(0x80);
	            
	            Enumeration e = getObjects();
	            while (e.hasMoreElements())
	            {
	                out.writeObject(e.nextElement());
	            }
	        
	            out.write(0x00);
	            out.write(0x00);
	        }
	        else
	        {
	            super.encode(out);
	        }
	    }
	}

	
	
	public static class BERSet
    extends DERSet
{
    /**
     * create an empty sequence
     */
    public BERSet()
    {
    }

    /**
     * create a set containing one object
     */
    public BERSet(
        DEREncodable    obj)
    {
        super(obj);
    }

    /**
     * @param v - a vector of objects making up the set.
     */
    public BERSet(
        DEREncodableVector   v)
    {
        super(v, false);
    }

    /**
     * @param v - a vector of objects making up the set.
     */
    BERSet(
        DEREncodableVector   v,
        boolean              needsSorting)
    {
        super(v, needsSorting);
    }

    /*
     */
    void encode(
        DEROutputStream out)
        throws IOException
    {
        if (out instanceof ASN1OutputStream)
        {
            out.write(SET | CONSTRUCTED);
            out.write(0x80);
            
            Enumeration e = getObjects();
            while (e.hasMoreElements())
            {
                out.writeObject(e.nextElement());
            }
        
            out.write(0x00);
            out.write(0x00);
        }
        else
        {
            super.encode(out);
        }
    }
}

	
	/**
	 * BER TaggedObject - in ASN.1 nottation this is any object proceeded by
	 * a [n] where n is some number - these are assume to follow the construction
	 * rules (as with sequences).
	 */
	public static class BERTaggedObject
	    extends DER.DERTaggedObject
	{
	    /**
	     * @param tagNo the tag number for this object.
	     * @param obj the tagged object.
	     */
	    public BERTaggedObject(
	        int             tagNo,
	        DER.DEREncodable    obj)
	    {
	        super(tagNo, obj);
	    }

	    /**
	     * @param explicit true if an explicitly tagged object.
	     * @param tagNo the tag number for this object.
	     * @param obj the tagged object.
	     */
	    public BERTaggedObject(
	        boolean         explicit,
	        int             tagNo,
	        DER.DEREncodable    obj)
	    {
	        super(explicit, tagNo, obj);
	    }

	    /**
	     * create an implicitly tagged object that contains a zero
	     * length sequence.
	     */
	    public BERTaggedObject(
	        int             tagNo)
	    {
	        super(false, tagNo, new BERSequence());
	    }

	    void encode(
	        DER.DEROutputStream  out)
	        throws IOException
	    {
	        if (out instanceof ASN1OutputStream)
	        {
	            out.write(CONSTRUCTED | TAGGED | tagNo);
	            out.write(0x80);

	            if (!empty)
	            {
	                if (!explicit)
	                {
	                    if (obj instanceof ASN1OctetString)
	                    {
	                        Enumeration  e;

	                        if (obj instanceof BERConstructedOctetString)
	                        {
	                            e = ((BERConstructedOctetString)obj).getObjects();
	                        }
	                        else
	                        {
	                            ASN1OctetString             octs = (ASN1OctetString)obj;
	                            BERConstructedOctetString   berO = new BERConstructedOctetString(octs.getOctets());

	                            e = berO.getObjects();
	                        }

	                        while (e.hasMoreElements())
	                        {
	                            out.writeObject(e.nextElement());
	                        }
	                    }
	                    else if (obj instanceof ASN1Sequence)
	                    {
	                        Enumeration  e = ((ASN1Sequence)obj).getObjects();

	                        while (e.hasMoreElements())
	                        {
	                            out.writeObject(e.nextElement());
	                        }
	                    }
	                    else if (obj instanceof ASN1Set)
	                    {
	                        Enumeration  e = ((ASN1Set)obj).getObjects();

	                        while (e.hasMoreElements())
	                        {
	                            out.writeObject(e.nextElement());
	                        }
	                    }
	                    else
	                    {
	                        throw new RuntimeException("not implemented: " + obj.getClass().getName());
	                    }
	                }
	                else
	                {
	                    out.writeObject(obj);
	                }
	            }

	            out.write(0x00);
	            out.write(0x00);
	        }
	        else
	        {
	            super.encode(out);
	        }
	    }
	}


	
}
