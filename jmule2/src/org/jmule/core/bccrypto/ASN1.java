package org.jmule.core.bccrypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

import static org.jmule.core.bccrypto.BER.*;
import static org.jmule.core.bccrypto.DER.*;

public class ASN1 {

	public static abstract class ASN1Encodable
    implements DER.DEREncodable
{
    public static final String DER1 = "DER";
    public static final String BER1 = "BER";
    
    public byte[] getEncoded() 
        throws IOException
    {
        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
        ASN1OutputStream        aOut = new ASN1OutputStream(bOut);
        
        aOut.writeObject(this);
        
        return bOut.toByteArray();
    }
    
    public byte[] getEncoded(
        String encoding) 
        throws IOException
    {
        if (encoding.equals(DER1))
        {
            ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
            DER.DEROutputStream         dOut = new DER.DEROutputStream(bOut);
            
            dOut.writeObject(this);
            
            return bOut.toByteArray();
        }
        
        return this.getEncoded();
    }
    
    /**
     * Return the DER encoding of the object, null if the DER encoding can not be made.
     * 
     * @return a DER byte array, null otherwise.
     */
    public byte[] getDEREncoded()
    {
        try
        {
            return this.getEncoded(DER1);
        }
        catch (IOException e)
        {
            return null;
        }
    }
    
    public int hashCode()
    {
        return this.toASN1Object().hashCode();
    }

    public boolean equals(
        Object  o)
    {
        if (this == o)
        {
            return true;
        }
        
        if (!(o instanceof DER.DEREncodable))
        {
            return false;
        }

        DER.DEREncodable other = (DER.DEREncodable)o;

        return this.toASN1Object().equals(other.getDERObject());
    }

    public DER.DERObject getDERObject()
    {        
        return this.toASN1Object();
    }

    public abstract DER.DERObject toASN1Object();
}
	
	
	public  static class ASN1EncodableVector
    extends DER.DEREncodableVector
{
    // migrating from DEREncodeableVector
    public ASN1EncodableVector()
    {
        
    }
}

	
	public static abstract class ASN1Generator
	{
	    protected OutputStream _out;
	    
	    public ASN1Generator(OutputStream out)
	    {
	        _out = out;
	    }
	    
	    public abstract OutputStream getRawOutputStream();
	}
	
	
	/**
	 * a general purpose ASN.1 decoder - note: this class differs from the
	 * others in that it returns null after it has read the last object in
	 * the stream. If an ASN.1 NULL is encountered a DER/BER Null object is
	 * returned.
	 */
	public static class ASN1InputStream
	    extends FilterInputStream
	    implements DERTags
	{
	    private static final DERObject END_OF_STREAM = new DERObject()
	    {
	        void encode(
	            DEROutputStream out)
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
	    
	    boolean eofFound = false;
	    int     limit = Integer.MAX_VALUE;

	    public ASN1InputStream(
	        InputStream is)
	    {
	        super(is);
	    }

	    /**
	     * Create an ASN1InputStream based on the input byte array. The length of DER objects in
	     * the stream is automatically limited to the length of the input array.
	     * 
	     * @param input array containing ASN.1 encoded data.
	     */
	    public ASN1InputStream(
	        byte[] input)
	    {
	        this(new ByteArrayInputStream(input), input.length);
	    }
	    
	    /**
	     * Create an ASN1InputStream where no DER object will be longer than limit.
	     * 
	     * @param input stream containing ASN.1 encoded data.
	     * @param limit maximum size of a DER encoded object.
	     */
	    public ASN1InputStream(
	        InputStream input,
	        int         limit)
	    {
	        super(input);
	        this.limit = limit;
	    }
	    
	    protected int readLength()
	        throws IOException
	    {
	        int length = read();
	        if (length < 0)
	        {
	            throw new IOException("EOF found when length expected");
	        }

	        if (length == 0x80)
	        {
	            return -1;      // indefinite-length encoding
	        }

	        if (length > 127)
	        {
	            int size = length & 0x7f;

	            if (size > 4)
	            {
	                throw new IOException("DER length more than 4 bytes");
	            }
	            
	            length = 0;
	            for (int i = 0; i < size; i++)
	            {
	                int next = read();

	                if (next < 0)
	                {
	                    throw new IOException("EOF found reading length");
	                }

	                length = (length << 8) + next;
	            }
	            
	            if (length < 0)
	            {
	                throw new IOException("corrupted stream - negative length found");
	            }
	            
	            if (length >= limit)   // after all we must have read at least 1 byte
	            {
	                throw new IOException("corrupted stream - out of bounds length found");
	            }
	        }

	        return length;
	    }

	    protected void readFully(
	        byte[]  bytes)
	        throws IOException
	    {
	        int     left = bytes.length;
	        int     len;

	        if (left == 0)
	        {
	            return;
	        }

	        while ((len = read(bytes, bytes.length - left, left)) > 0)
	        {
	            if ((left -= len) == 0)
	            {
	                return;
	            }
	        }

	        if (left != 0)
	        {
	            throw new EOFException("EOF encountered in middle of object");
	        }
	    }

	    /**
	     * build an object given its tag and the number of bytes to construct it from.
	     */
	    protected DER.DERObject buildObject(
	        int       tag,
	        int       tagNo,
	        int       length)
	        throws IOException
	    {
	        if ((tag & APPLICATION) != 0)
	        {
	            return new DER.DERApplicationSpecific(tagNo, readDefiniteLengthFully(length));
	        }

	        boolean isConstructed = (tag & CONSTRUCTED) != 0;

	        if (isConstructed)
	        {
	            switch (tag)
	            {
	            case SEQUENCE | CONSTRUCTED:
	                return new DERSequence(buildDerEncodableVector(length));
	            case SET | CONSTRUCTED:
	                return new DER.DERSet(buildDerEncodableVector(length), false);
	            case OCTET_STRING | CONSTRUCTED:
	                return buildDerConstructedOctetString(length);
	            default:
	            {
	                //
	                // with tagged object tag number is bottom 5 bits
	                //
	                if ((tag & TAGGED) != 0)  
	                {
	                    if (length == 0)     // empty tag!
	                    {
	                        return new DER.DERTaggedObject(false, tagNo, new DERSequence());
	                    }

	                    ASN1EncodableVector v = buildDerEncodableVector(length);

	                    if (v.size() == 1)
	                    {
	                        //
	                        // explicitly tagged (probably!) - if it isn't we'd have to
	                        // tell from the context
	                        //
	                        return new DER.DERTaggedObject(tagNo, v.get(0));
	                    }

	                    return new DER.DERTaggedObject(false, tagNo, new DERSequence(v));
	                }

	                return new DER.DERUnknownTag(tag, readDefiniteLengthFully(length));
	            }
	            }
	        }

	        byte[] bytes = readDefiniteLengthFully(length);

	        switch (tag)
	        {
	        case NULL:
	            return DERNull.INSTANCE;   
	        case INTEGER:
	            return new DER.DERInteger(bytes);
	        case ENUMERATED:
	            return new DER.DEREnumerated(bytes);
	        case OBJECT_IDENTIFIER:
	            return new DERObjectIdentifier(bytes);
	        case BIT_STRING:
	        {
	            int     padBits = bytes[0];
	            byte[]  data = new byte[bytes.length - 1];

	            System.arraycopy(bytes, 1, data, 0, bytes.length - 1);

	            return new DERBitString(data, padBits);
	        }

	        case OCTET_STRING:
	            return new DEROctetString(bytes);
	        default:
	        {
	            //
	            // with tagged object tag number is bottom 5 bits
	            //
	            if ((tag & TAGGED) != 0)  
	            {
	                if (bytes.length == 0)     // empty tag!
	                {
	                    return new DER.DERTaggedObject(false, tagNo, DERNull.INSTANCE);
	                }

	                //
	                // simple type - implicit... return an octet string
	                //
	                return new DER.DERTaggedObject(false, tagNo, new DEROctetString(bytes));
	            }

	            return new DER.DERUnknownTag(tag, bytes);
	        }
	        }
	    }

	    private byte[] readDefiniteLengthFully(int length)
	        throws IOException
	    {
	        byte[] bytes = new byte[length];
	        readFully(bytes);
	        return bytes;
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

	    private BERConstructedOctetString buildConstructedOctetString(DERObject sentinel)
	        throws IOException
	    {
	        Vector octs = new Vector();
	        DERObject o;

	        while ((o = readObject()) != sentinel)
	        {
	            octs.addElement(o);
	        }

	        return new BERConstructedOctetString(octs);
	    }

	    //
	    // yes, people actually do this...
	    //
	    private BERConstructedOctetString buildDerConstructedOctetString(int length)
	        throws IOException
	    {
	        DefiniteLengthInputStream dIn = new DefiniteLengthInputStream(this, length);
	        ASN1InputStream aIn = new ASN1InputStream(dIn, length);

	        return aIn.buildConstructedOctetString(null);
	    }

	    private ASN1EncodableVector buildEncodableVector(DERObject sentinel)
	        throws IOException
	    {
	        ASN1EncodableVector v = new ASN1EncodableVector();
	        DERObject o;

	        while ((o = readObject()) != sentinel)
	        {
	            v.add(o);
	        }

	        return v;
	    }

	    private ASN1EncodableVector buildDerEncodableVector(int length)
	        throws IOException
	    {
	        DefiniteLengthInputStream dIn = new DefiniteLengthInputStream(this, length);
	        ASN1InputStream aIn = new ASN1InputStream(dIn, length);

	        return aIn.buildEncodableVector(null);
	    }

	    public DERObject readObject()
	        throws IOException
	    {
	        int tag = read();
	        if (tag == -1)
	        {
	            if (eofFound)
	            {
	                throw new EOFException("attempt to read past end of file.");
	            }

	            eofFound = true;

	            return null;
	        }
	    
	        int tagNo = 0;
	        
	        if ((tag & TAGGED) != 0 || (tag & APPLICATION) != 0)
	        {
	            tagNo = readTagNumber(tag);
	        }
	        
	        int     length = readLength();

	        if (length < 0)    // indefinite length method
	        {
	            switch (tag)
	            {
	            case NULL:
	                return BERNull.INSTANCE;
	            case SEQUENCE | CONSTRUCTED:
	                return new BERSequence(buildEncodableVector(END_OF_STREAM));
	            case SET | CONSTRUCTED:
	                return new BERSet(buildEncodableVector(END_OF_STREAM), false);
	            case OCTET_STRING | CONSTRUCTED:
	                return buildConstructedOctetString(END_OF_STREAM);
	            default:
	            {
	                //
	                // with tagged object tag number is bottom 5 bits
	                //
	                if ((tag & TAGGED) != 0)  
	                {
	                    //
	                    // simple type - implicit... return an octet string
	                    //
	                    if ((tag & CONSTRUCTED) == 0)
	                    {
	                        byte[] bytes = readIndefiniteLengthFully();

	                        return new BERTaggedObject(false, tagNo, new DEROctetString(bytes));
	                    }

	                    //
	                    // either constructed or explicitly tagged
	                    //
	                    ASN1EncodableVector v = buildEncodableVector(END_OF_STREAM);

	                    if (v.size() == 0)     // empty tag!
	                    {
	                        return new DERTaggedObject(tagNo);
	                    }

	                    if (v.size() == 1)
	                    {
	                        //
	                        // explicitly tagged (probably!) - if it isn't we'd have to
	                        // tell from the context
	                        //
	                        return new BERTaggedObject(tagNo, v.get(0));
	                    }

	                    return new BERTaggedObject(false, tagNo, new BERSequence(v));
	                }

	                throw new IOException("unknown BER object encountered");
	            }
	            }
	        }
	        else
	        {
	            if (tag == 0 && length == 0)    // end of contents marker.
	            {
	                return END_OF_STREAM;
	            }

	            return buildObject(tag, tagNo, length);
	        }
	    }

	    private int readTagNumber(int tag) 
	        throws IOException
	    {
	        int tagNo = tag & 0x1f;

	        if (tagNo == 0x1f)
	        {
	            int b = read();

	            tagNo = 0;

	            while ((b >= 0) && ((b & 0x80) != 0))
	            {
	                tagNo |= (b & 0x7f);
	                tagNo <<= 7;
	                b = read();
	            }

	            if (b < 0)
	            {
	                eofFound = true;
	                throw new EOFException("EOF found inside tag value.");
	            }
	            
	            tagNo |= (b & 0x7f);
	        }
	        
	        return tagNo;
	    }
	}
	
	
	public static abstract class ASN1Null
    extends ASN1Object
{
    public ASN1Null()
    {
    }

    public int hashCode()
    {
        return 0;
    }

    boolean asn1Equals(
        DER.DERObject o)
    {
        if (!(o instanceof ASN1Null))
        {
            return false;
        }
        
        return true;
    }

    abstract void encode(DER.DEROutputStream out)
        throws IOException;

    public String toString()
    {
         return "NULL";
    }
}

	
	
	public static abstract  class ASN1Object
    extends DER.DERObject
{
    /**
     * Create a base ASN.1 object from a byte stream.
     *
     * @param data the byte stream to parse.
     * @return the base ASN.1 object represented by the byte stream.
     * @exception IOException if there is a problem parsing the data.
     */
    public static ASN1Object fromByteArray(byte[] data)
        throws IOException
    {
        ASN1InputStream aIn = new ASN1InputStream(data);

        return (ASN1Object)aIn.readObject();
    }

    public final boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        
        return (o instanceof DER.DEREncodable) && asn1Equals(((DER.DEREncodable)o).getDERObject());
    }

    public abstract int hashCode();

    abstract void encode(DER.DEROutputStream out) throws IOException;

    abstract boolean asn1Equals(DER.DERObject o);
}

	
	
	public static class ASN1ObjectParser
	{
	    private int              _baseTag;
	    private int              _tagNumber;

	    private ASN1StreamParser _aIn;

	    protected ASN1ObjectParser(
	        int         baseTag,
	        int         tagNumber,
	        InputStream contentStream)
	    {
	        _baseTag = baseTag;
	        _tagNumber = tagNumber;
	        _aIn = new ASN1StreamParser(contentStream);
	    }

	    /**
	     * Return the tag number for this object.
	     *
	     * @return the tag number.
	     */
	    int getTagNumber()
	    {
	        return _tagNumber;
	    }

	    int getBaseTag()
	    {
	        return _baseTag;
	    }

	    DER.DEREncodable readObject()
	        throws IOException
	    {
	        return _aIn.readObject();
	    }

	    ASN1EncodableVector readVector()
	        throws IllegalStateException
	    {
	        ASN1EncodableVector v = new ASN1EncodableVector();
	        DER.DEREncodable obj;

	        try
	        {
	            while ((obj = readObject()) != null)
	            {
	                v.add(obj.getDERObject());
	            }
	        }
	        catch (IOException e)
	        {
	            throw new IllegalStateException(e.getMessage());
	        }

	        return v;
	    }
	}

	
	public static abstract class ASN1OctetString
    extends ASN1Object
    implements ASN1OctetStringParser
{
    byte[]  string;

    /**
     * return an Octet String from a tagged object.
     *
     * @param obj the tagged object holding the object we want.
     * @param explicit true if the object is meant to be explicitly
     *              tagged false otherwise.
     * @exception IllegalArgumentException if the tagged object cannot
     *              be converted.
     */
    public static ASN1OctetString getInstance(
        ASN1TaggedObject    obj,
        boolean             explicit)
    {
        return getInstance(obj.getObject());
    }
    
    /**
     * return an Octet String from the given object.
     *
     * @param obj the object we want converted.
     * @exception IllegalArgumentException if the object cannot be converted.
     */
    public static ASN1OctetString getInstance(
        Object  obj)
    {
        if (obj == null || obj instanceof ASN1OctetString)
        {
            return (ASN1OctetString)obj;
        }

        if (obj instanceof ASN1TaggedObject)
        {
            return getInstance(((ASN1TaggedObject)obj).getObject());
        }

        if (obj instanceof ASN1Sequence)
        {
            Vector      v = new Vector();
            Enumeration e = ((ASN1Sequence)obj).getObjects();

            while (e.hasMoreElements())
            {
                v.addElement(e.nextElement());
            }

            return new BERConstructedOctetString(v);
        }

        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    /**
     * @param string the octets making up the octet string.
     */
    public ASN1OctetString(
        byte[]  string)
    {
        this.string = string;
    }

    public ASN1OctetString(
        DER.DEREncodable obj)
    {
        try
        {
            ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
            DER.DEROutputStream         dOut = new DER.DEROutputStream(bOut);

            dOut.writeObject(obj);
            dOut.close();

            this.string = bOut.toByteArray();
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException("Error processing object : " + e.toString());
        }
    }

    public InputStream getOctetStream()
    {
        return new ByteArrayInputStream(string);
    }

    public ASN1OctetStringParser parser()
    {
        return this;
    }

    public byte[] getOctets()
    {
        return string;
    }

    public int hashCode()
    {
        byte[]  b = this.getOctets();
        int     value = 0;

        for (int i = 0; i != b.length; i++)
        {
            value ^= (b[i] & 0xff) << (i % 4);
        }

        return value;
    }

    boolean asn1Equals(
        DER.DERObject  o)
    {
        if (!(o instanceof ASN1OctetString))
        {
            return false;
        }

        ASN1OctetString  other = (ASN1OctetString)o;

        byte[] b1 = other.string;
        byte[] b2 = this.string;

        if (b1.length != b2.length)
        {
            return false;
        }

        for (int i = 0; i != b1.length; i++)
        {
            if (b1[i] != b2[i])
            {
                return false;
            }
        }

        return true;
    }

    abstract void encode(DER.DEROutputStream out)
        throws IOException;


}

	public static interface ASN1OctetStringParser
    extends DER.DEREncodable
{
    public InputStream getOctetStream();
}

	
	public static class ASN1OutputStream
    extends DER.DEROutputStream
{
    public ASN1OutputStream(
        OutputStream    os)
    {
        super(os);
    }

    public void writeObject(
        Object    obj)
        throws IOException
    {
        if (obj == null)
        {
            writeNull();
        }
        else if (obj instanceof DER.DERObject)
        {
            ((DER.DERObject)obj).encode(this);
        }
        else if (obj instanceof DER.DEREncodable)
        {
            ((DER.DEREncodable)obj).getDERObject().encode(this);
        }
        else
        {
            throw new IOException("object not ASN1Encodable");
        }
    }
}

	
	
	public abstract static class ASN1Sequence
    extends ASN1Object
{
    private Vector seq = new Vector();

    /**
     * return an ASN1Sequence from the given object.
     *
     * @param obj the object we want converted.
     * @exception IllegalArgumentException if the object cannot be converted.
     */
    public static ASN1Sequence getInstance(
        Object  obj)
    {
        if (obj == null || obj instanceof ASN1Sequence)
        {
            return (ASN1Sequence)obj;
        }

        throw new IllegalArgumentException("unknown object in getInstance");
    }

    /**
     * Return an ASN1 sequence from a tagged object. There is a special
     * case here, if an object appears to have been explicitly tagged on 
     * reading but we were expecting it to be implictly tagged in the 
     * normal course of events it indicates that we lost the surrounding
     * sequence - so we need to add it back (this will happen if the tagged
     * object is a sequence that contains other sequences). If you are
     * dealing with implicitly tagged sequences you really <b>should</b>
     * be using this method.
     *
     * @param obj the tagged object.
     * @param explicit true if the object is meant to be explicitly tagged,
     *          false otherwise.
     * @exception IllegalArgumentException if the tagged object cannot
     *          be converted.
     */
    public static ASN1Sequence getInstance(
        ASN1TaggedObject    obj,
        boolean             explicit)
    {
        if (explicit)
        {
            if (!obj.isExplicit())
            {
                throw new IllegalArgumentException("object implicit - explicit expected.");
            }

            return (ASN1Sequence)obj.getObject();
        }
        else
        {
            //
            // constructed object which appears to be explicitly tagged
            // when it should be implicit means we have to add the
            // surrounding sequence.
            //
            if (obj.isExplicit())
            {
                if (obj instanceof BERTaggedObject)
                {
                    return new BERSequence(obj.getObject());
                }
                else
                {
                    return new DERSequence(obj.getObject());
                }
            }
            else
            {
                if (obj.getObject() instanceof ASN1Sequence)
                {
                    return (ASN1Sequence)obj.getObject();
                }
            }
        }

        throw new IllegalArgumentException(
                "unknown object in getInstanceFromTagged");
    }

    public Enumeration getObjects()
    {
        return seq.elements();
    }

    public ASN1SequenceParser parser()
    {
        final ASN1Sequence outer = this;

        return new ASN1SequenceParser()
        {
            private final int max = size();

            private int index;

            public DEREncodable readObject() throws IOException
            {
                if (index == max)
                {
                    return null;
                }
                
                DEREncodable obj = getObjectAt(index++);
                if (obj instanceof ASN1Sequence)
                {
                    return ((ASN1Sequence)obj).parser();
                }
                if (obj instanceof ASN1Set)
                {
                    return ((ASN1Set)obj).parser();
                }

                return obj;
            }

            public DERObject getDERObject()
            {
                return outer;
            }
        };
    }

    /**
     * return the object at the sequence postion indicated by index.
     *
     * @param index the sequence number (starting at zero) of the object
     * @return the object at the sequence postion indicated by index.
     */
    public DER.DEREncodable getObjectAt(
        int index)
    {
        return (DER.DEREncodable)seq.elementAt(index);
    }

    /**
     * return the number of objects in this sequence.
     *
     * @return the number of objects in this sequence.
     */
    public int size()
    {
        return seq.size();
    }

    public int hashCode()
    {
        Enumeration             e = this.getObjects();
        int                     hashCode = 0;

        while (e.hasMoreElements())
        {
            Object    o = e.nextElement();
            
            if (o != null)
            {
                hashCode ^= o.hashCode();
            }
        }

        return hashCode;
    }

    boolean asn1Equals(
        DERObject  o)
    {
        if (!(o instanceof ASN1Sequence))
        {
            return false;
        }
        
        ASN1Sequence   other = (ASN1Sequence)o;

        if (this.size() != other.size())
        {
            return false;
        }

        Enumeration s1 = this.getObjects();
        Enumeration s2 = other.getObjects();

        while (s1.hasMoreElements())
        {
            DERObject  o1 = ((DEREncodable)s1.nextElement()).getDERObject();
            DERObject  o2 = ((DEREncodable)s2.nextElement()).getDERObject();

            if (o1 == o2 || (o1 != null && o1.equals(o2)))
            {
                continue;
            }

            return false;
        }

        return true;
    }

    protected void addObject(
        DER.DEREncodable obj)
    {
        seq.addElement(obj);
    }

    abstract void encode(DEROutputStream out)
        throws IOException;

    public String toString() 
    {
      return seq.toString();
    }
}
	
	public interface ASN1SequenceParser
    extends DEREncodable
{
    DEREncodable readObject()
        throws IOException;
}

	
	abstract public static class ASN1Set
    extends ASN1Object
{
    protected Vector set = new Vector();

    /**
     * return an ASN1Set from the given object.
     *
     * @param obj the object we want converted.
     * @exception IllegalArgumentException if the object cannot be converted.
     */
    public static ASN1Set getInstance(
        Object  obj)
    {
        if (obj == null || obj instanceof ASN1Set)
        {
            return (ASN1Set)obj;
        }

        throw new IllegalArgumentException("unknown object in getInstance");
    }

    /**
     * Return an ASN1 set from a tagged object. There is a special
     * case here, if an object appears to have been explicitly tagged on 
     * reading but we were expecting it to be implictly tagged in the 
     * normal course of events it indicates that we lost the surrounding
     * set - so we need to add it back (this will happen if the tagged
     * object is a sequence that contains other sequences). If you are
     * dealing with implicitly tagged sets you really <b>should</b>
     * be using this method.
     *
     * @param obj the tagged object.
     * @param explicit true if the object is meant to be explicitly tagged
     *          false otherwise.
     * @exception IllegalArgumentException if the tagged object cannot
     *          be converted.
     */
    public static ASN1Set getInstance(
        ASN1TaggedObject    obj,
        boolean             explicit)
    {
        if (explicit)
        {
            if (!obj.isExplicit())
            {
                throw new IllegalArgumentException("object implicit - explicit expected.");
            }

            return (ASN1Set)obj.getObject();
        }
        else
        {
            //
            // constructed object which appears to be explicitly tagged
            // and it's really implicit means we have to add the
            // surrounding sequence.
            //
            if (obj.isExplicit())
            {
                ASN1Set    set = new DER.DERSet(obj.getObject());

                return set;
            }
            else
            {
                if (obj.getObject() instanceof ASN1Set)
                {
                    return (ASN1Set)obj.getObject();
                }

                //
                // in this case the parser returns a sequence, convert it
                // into a set.
                //
                ASN1EncodableVector  v = new ASN1EncodableVector();

                if (obj.getObject() instanceof ASN1Sequence)
                {
                    ASN1Sequence s = (ASN1Sequence)obj.getObject();
                    Enumeration e = s.getObjects();

                    while (e.hasMoreElements())
                    {
                        v.add((DER.DEREncodable)e.nextElement());
                    }

                    return new DER.DERSet(v, false);
                }
            }
        }

        throw new IllegalArgumentException(
                    "unknown object in getInstanceFromTagged");
    }

    public ASN1Set()
    {
    }

    public Enumeration getObjects()
    {
        return set.elements();
    }

    /**
     * return the object at the set postion indicated by index.
     *
     * @param index the set number (starting at zero) of the object
     * @return the object at the set postion indicated by index.
     */
    public DER.DEREncodable getObjectAt(
        int index)
    {
        return (DER.DEREncodable)set.elementAt(index);
    }

    /**
     * return the number of objects in this set.
     *
     * @return the number of objects in this set.
     */
    public int size()
    {
        return set.size();
    }

    public ASN1SetParser parser()
    {
        final ASN1Set outer = this;

        return new ASN1SetParser()
        {
            private final int max = size();

            private int index;

            public DER.DEREncodable readObject() throws IOException
            {
                if (index == max)
                {
                    return null;
                }

                DER.DEREncodable obj = getObjectAt(index++);
                if (obj instanceof ASN1Sequence)
                {
                    return ((ASN1Sequence)obj).parser();
                }
                if (obj instanceof ASN1Set)
                {
                    return ((ASN1Set)obj).parser();
                }

                return obj;
            }

            public DERObject getDERObject()
            {
                return outer;
            }
        };
    }

    public int hashCode()
    {
        Enumeration             e = this.getObjects();
        int                     hashCode = 0;

        while (e.hasMoreElements())
        {
            hashCode ^= e.nextElement().hashCode();
        }

        return hashCode;
    }

    boolean asn1Equals(
        DERObject  o)
    {
        if (!(o instanceof ASN1Set))
        {
            return false;
        }

        ASN1Set   other = (ASN1Set)o;

        if (this.size() != other.size())
        {
            return false;
        }

        Enumeration s1 = this.getObjects();
        Enumeration s2 = other.getObjects();

        while (s1.hasMoreElements())
        {
            DERObject  o1 = ((DEREncodable)s1.nextElement()).getDERObject();
            DERObject  o2 = ((DEREncodable)s2.nextElement()).getDERObject();

            if (o1 == o2 || (o1 != null && o1.equals(o2)))
            {
                continue;
            }

            return false;
        }

        return true;
    }

    /**
     * return true if a <= b (arrays are assumed padded with zeros).
     */
    private boolean lessThanOrEqual(
         byte[] a,
         byte[] b)
    {
         if (a.length <= b.length)
         {
             for (int i = 0; i != a.length; i++)
             {
                 int    l = a[i] & 0xff;
                 int    r = b[i] & 0xff;
                 
                 if (r > l)
                 {
                     return true;
                 }
                 else if (l > r)
                 {
                     return false;
                 }
             }

             return true;
         }
         else
         {
             for (int i = 0; i != b.length; i++)
             {
                 int    l = a[i] & 0xff;
                 int    r = b[i] & 0xff;
                 
                 if (r > l)
                 {
                     return true;
                 }
                 else if (l > r)
                 {
                     return false;
                 }
             }

             return false;
         }
    }

    private byte[] getEncoded(
        DEREncodable obj)
    {
        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
        ASN1OutputStream        aOut = new ASN1OutputStream(bOut);

        try
        {
            aOut.writeObject(obj);
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException("cannot encode object added to SET");
        }

        return bOut.toByteArray();
    }

    protected void sort()
    {
        if (set.size() > 1)
        {
            boolean    swapped = true;
            int        lastSwap = set.size() - 1;

            while (swapped)
            {
                int    index = 0;
                int    swapIndex = 0;
                byte[] a = getEncoded((DEREncodable)set.elementAt(0));
                
                swapped = false;

                while (index != lastSwap)
                {
                    byte[] b = getEncoded((DEREncodable)set.elementAt(index + 1));

                    if (lessThanOrEqual(a, b))
                    {
                        a = b;
                    }
                    else
                    {
                        Object  o = set.elementAt(index);

                        set.setElementAt(set.elementAt(index + 1), index);
                        set.setElementAt(o, index + 1);

                        swapped = true;
                        swapIndex = index;
                    }

                    index++;
                }

                lastSwap = swapIndex;
            }
        }
    }

    protected void addObject(
        DEREncodable obj)
    {
        set.addElement(obj);
    }

    abstract void encode(DEROutputStream out)
            throws IOException;

    public String toString() 
    {
      return set.toString();
    }
}

	
	public interface ASN1SetParser
    extends DER.DEREncodable
{
    public DER.DEREncodable readObject()
        throws IOException;
}

	
	public static class ASN1StreamParser
	{
	    InputStream _in;

	    private int     _limit;
	    private boolean _eofFound;

	    public ASN1StreamParser(
	        InputStream in)
	    {
	        this(in, Integer.MAX_VALUE);
	    }

	    public ASN1StreamParser(
	        InputStream in,
	        int         limit)
	    {
	        this._in = in;
	        this._limit = limit;
	    }

	    public ASN1StreamParser(
	        byte[] encoding)
	    {
	        this(new ByteArrayInputStream(encoding), encoding.length);
	    }

	    InputStream getParentStream()
	    {
	        return _in;
	    }

	    private int readLength()
	        throws IOException
	    {
	        int length = _in.read();
	        if (length < 0)
	        {
	            throw new EOFException("EOF found when length expected");
	        }

	        if (length == 0x80)
	        {
	            return -1;      // indefinite-length encoding
	        }

	        if (length > 127)
	        {
	            int size = length & 0x7f;

	            if (size > 4)
	            {
	                throw new IOException("DER length more than 4 bytes");
	            }

	            length = 0;
	            for (int i = 0; i < size; i++)
	            {
	                int next = _in.read();

	                if (next < 0)
	                {
	                    throw new EOFException("EOF found reading length");
	                }

	                length = (length << 8) + next;
	            }

	            if (length < 0)
	            {
	                throw new IOException("corrupted stream - negative length found");
	            }

	            if (length >= _limit)   // after all we must have read at least 1 byte
	            {
	                throw new IOException("corrupted stream - out of bounds length found");
	            }
	        }

	        return length;
	    }

	    public DER.DEREncodable readObject()
	        throws IOException
	    {
	        int tag = _in.read();
	        if (tag == -1)
	        {
	            if (_eofFound)
	            {
	                throw new EOFException("attempt to read past end of file.");
	            }

	            _eofFound = true;

	            return null;
	        }

	        //
	        // calculate tag number
	        //
	        int baseTagNo = tag & ~DER.DERTags.CONSTRUCTED;
	        int tagNo = baseTagNo;

	        if ((tag & DER.DERTags.TAGGED) != 0)
	        {
	            tagNo = tag & 0x1f;

	            //
	            // with tagged object tag number is bottom 5 bits, or stored at the start of the content
	            //
	            if (tagNo == 0x1f)
	            {
	                tagNo = 0;

	                int b = _in.read();

	                while ((b >= 0) && ((b & 0x80) != 0))
	                {
	                    tagNo |= (b & 0x7f);
	                    tagNo <<= 7;
	                    b = _in.read();
	                }

	                if (b < 0)
	                {
	                    _eofFound = true;

	                    throw new EOFException("EOF encountered inside tag value.");
	                }

	                tagNo |= (b & 0x7f);
	            }
	        }

	        //
	        // calculate length
	        //
	        int length = readLength();

	       
	        {
	            DefiniteLengthInputStream defIn = new DefiniteLengthInputStream(_in, length);

	            switch (baseTagNo)
	            {
	            case DER.DERTags.INTEGER:
	                return new DER.DERInteger(defIn.toByteArray());
	            case DER.DERTags.NULL:
	                defIn.toByteArray(); // make sure we read to end of object bytes.
	                return DER.DERNull.INSTANCE;
	            case DER.DERTags.OBJECT_IDENTIFIER:
	                return new DER.DERObjectIdentifier(defIn.toByteArray());
	            case DER.DERTags.OCTET_STRING:
	                return new DER.DEROctetString(defIn.toByteArray());
	            case DER.DERTags.SEQUENCE:
	                return new DER.DERSequence(loadVector(defIn, length)).parser();
	            case DER.DERTags.SET:
	                return new DER.DERSet(loadVector(defIn, length)).parser();
	            default:
	                return null;
	            }
	        }
	    }

	    private ASN1EncodableVector loadVector(InputStream in, int length)
	        throws IOException
	    {
	        ASN1InputStream         aIn = new ASN1InputStream(in, length);
	        ASN1EncodableVector     v = new ASN1EncodableVector();

	        DERObject obj;
	        while ((obj = aIn.readObject()) != null)
	        {
	            v.add(obj);
	        }

	        return v;
	    }
	}

	
	public abstract static class ASN1TaggedObject
    extends ASN1Object
    implements ASN1TaggedObjectParser
{
    int             tagNo;
    boolean         empty = false;
    boolean         explicit = true;
    DER.DEREncodable    obj = null;

    static public ASN1TaggedObject getInstance(
        ASN1TaggedObject    obj,
        boolean             explicit)
    {
        if (explicit)
        {
            return (ASN1TaggedObject)obj.getObject();
        }

        throw new IllegalArgumentException("implicitly tagged tagged object");
    }

    static public ASN1TaggedObject getInstance(
        Object obj) 
    {
        if (obj == null || obj instanceof ASN1TaggedObject) 
        {
                return (ASN1TaggedObject)obj;
        }

        throw new IllegalArgumentException("unknown object in getInstance");
    }

    /**
     * Create a tagged object in the explicit style.
     * 
     * @param tagNo the tag number for this object.
     * @param obj the tagged object.
     */
    public ASN1TaggedObject(
        int             tagNo,
        DER.DEREncodable    obj)
    {
        this.explicit = true;
        this.tagNo = tagNo;
        this.obj = obj;
    }

    /**
     * Create a tagged object with the style given by the value of explicit.
     * <p>
     * If the object implements ASN1Choice the tag style will always be changed
     * to explicit in accordance with the ASN.1 encoding rules.
     * </p>
     * @param explicit true if the object is explicitly tagged.
     * @param tagNo the tag number for this object.
     * @param obj the tagged object.
     */
    public ASN1TaggedObject(
        boolean         explicit,
        int             tagNo,
        DER.DEREncodable    obj)
    {
        {
            this.explicit = explicit;
        }
        
        this.tagNo = tagNo;
        this.obj = obj;
    }
    
    boolean asn1Equals(
    		DER.DERObject o)
    {
        if (!(o instanceof ASN1TaggedObject))
        {
            return false;
        }
        
        ASN1TaggedObject other = (ASN1TaggedObject)o;
        
        if (tagNo != other.tagNo || empty != other.empty || explicit != other.explicit)
        {
            return false;
        }
        
        if(obj == null)
        {
            if (other.obj != null)
            {
                return false;
            }
        }
        else
        {
            if (!(obj.getDERObject().equals(other.obj.getDERObject())))
            {
                return false;
            }
        }
        
        return true;
    }
    
    public int hashCode()
    {
        int code = tagNo;

        if (obj != null)
        {
            code ^= obj.hashCode();
        }

        return code;
    }

    public int getTagNo()
    {
        return tagNo;
    }

    /**
     * return whether or not the object may be explicitly tagged. 
     * <p>
     * Note: if the object has been read from an input stream, the only
     * time you can be sure if isExplicit is returning the true state of
     * affairs is if it returns false. An implicitly tagged object may appear
     * to be explicitly tagged, so you need to understand the context under
     * which the reading was done as well, see getObject below.
     */
    public boolean isExplicit()
    {
        return explicit;
    }

    public boolean isEmpty()
    {
        return empty;
    }

    /**
     * return whatever was following the tag.
     * <p>
     * Note: tagged objects are generally context dependent if you're
     * trying to extract a tagged object you should be going via the
     * appropriate getInstance method.
     */
    public DER.DERObject getObject()
    {
        if (obj != null)
        {
            return obj.getDERObject();
        }

        return null;
    }

    /**
     * Return the object held in this tagged object as a parser assuming it has
     * the type of the passed in tag. If the object doesn't have a parser
     * associated with it, the base object is returned.
     */
    public org.jmule.core.bccrypto.DER.DEREncodable getObjectParser(
        int     tag,
        boolean isExplicit)
    {
        switch (tag)
        {
        case org.jmule.core.bccrypto.DER.DERTags.SET:
            return ASN1Set.getInstance(this, isExplicit).parser();
        case org.jmule.core.bccrypto.DER.DERTags.SEQUENCE:
            return ASN1Sequence.getInstance(this, isExplicit).parser();
        case org.jmule.core.bccrypto.DER.DERTags.OCTET_STRING:
            return ASN1OctetString.getInstance(this, isExplicit).parser();
        }

        if (isExplicit)
        {
            return getObject();
        }

        throw new RuntimeException("implicit tagging not implemented for tag: " + tag);
    }

    abstract void encode(DEROutputStream  out)
        throws IOException;

    public String toString()
    {
        return "[" + tagNo + "]" + obj;
    }
}

	
	
	public interface ASN1TaggedObjectParser
    extends DEREncodable
{
    public int getTagNo();
    
    public DEREncodable getObjectParser(int tag, boolean isExplicit)
        throws IOException;
}

	
	
	
	
	
}
