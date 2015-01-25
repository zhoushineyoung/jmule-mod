package org.jmule.core.bccrypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Vector;

import static org.jmule.core.bccrypto.ASN1.*;
import static org.jmule.core.bccrypto.BER.*;

public class DER {

	public static class DERApplicationSpecific 
    extends ASN1Object
{
    private int       tag;
    private byte[]    octets;
    
    public DERApplicationSpecific(
        int        tag,
        byte[]    octets)
    {
        this.tag = tag;
        this.octets = octets;
    }
    
    public DERApplicationSpecific(
        int                  tag, 
        DEREncodable         object) 
        throws IOException 
    {
        this(true, tag, object);
    }

    public DERApplicationSpecific(
        boolean      explicit,
        int          tag,
        DEREncodable object)
        throws IOException
    {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DEROutputStream dos = new DEROutputStream(bOut);

        dos.writeObject(object);

        byte[] data = bOut.toByteArray();

        if (tag >= 0x1f)
        {
            throw new IOException("unsupported tag number");
        }

        if (explicit)
        {
            this.tag = tag | DERTags.CONSTRUCTED;
            this.octets = data;
        }
        else
        {
            this.tag = tag;
            int lenBytes = getLengthOfLength(data);
            byte[] tmp = new byte[data.length - lenBytes];
            System.arraycopy(data, lenBytes, tmp, 0, tmp.length);
            this.octets = tmp;
        }
    }

    private int getLengthOfLength(byte[] data)
    {
        int count = 2;               // TODO: assumes only a 1 byte tag number

        while((data[count - 1] & 0x80) != 0)
        {
            count++;
        }

        return count;
    }

    public boolean isConstructed()
    {
        return (tag & DERTags.CONSTRUCTED) != 0;
    }
    
    public byte[] getContents()
    {
        return octets;
    }
    
    public int getApplicationTag() 
    {
        return tag;
    }
     
    public DERObject getObject() 
        throws IOException 
    {
        return new ASN1InputStream(getContents()).readObject();
    }

    /**
     * Return the enclosed object assuming implicit tagging.
     *
     * @param derTagNo the type tag that should be applied to the object's contents.
     * @return  the resulting object
     * @throws IOException if reconstruction fails.
     */
    public DERObject getObject(int derTagNo)
        throws IOException
    {
        if (tag >= 0x1f)
        {
            throw new IOException("unsupported tag number");
        }
                
        byte[] tmp = this.getEncoded();

        tmp[0] = (byte)derTagNo;
        
        return new ASN1InputStream(tmp).readObject();
    }
    
    /* (non-Javadoc)
     * @see org.bouncycastle.asn1.DERObject#encode(org.bouncycastle.asn1.DEROutputStream)
     */
    void encode(DEROutputStream out) throws IOException
    {
        out.writeEncoded(DERTags.APPLICATION | tag, octets);
    }
    
    boolean asn1Equals(
        DERObject o)
    {
        if (!(o instanceof DERApplicationSpecific))
        {
            return false;
        }
        
        DERApplicationSpecific other = (DERApplicationSpecific)o;
        
        if (tag != other.tag)
        {
            return false;
        }
        
        if (octets.length != other.octets.length)
        {
            return false;
        }
        
        for (int i = 0; i < octets.length; i++) 
        {
            if (octets[i] != other.octets[i])
            {
                return false;
            }
        }
        
        return true;
    }
    
    public int hashCode()
    {
        byte[]  b = this.getContents();
        int     value = 0;

        for (int i = 0; i != b.length; i++)
        {
            value ^= (b[i] & 0xff) << (i % 4);
        }

        return value ^ this.getApplicationTag();
    }
}
	
	
	
	public static class DERBitString
    extends ASN1Object
    implements DERString
{
    private static final char[]  table = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    
    protected byte[]      data;
    protected int         padBits;

    /**
     * return the correct number of pad bits for a bit string defined in
     * a 32 bit constant
     */
    static protected int getPadBits(
        int bitString)
    {
        int val = 0;
        for (int i = 3; i >= 0; i--) 
        {
            //
            // this may look a little odd, but if it isn't done like this pre jdk1.2
            // JVM's break!
            //
            if (i != 0)
            {
                if ((bitString >> (i * 8)) != 0) 
                {
                    val = (bitString >> (i * 8)) & 0xFF;
                    break;
                }
            }
            else
            {
                if (bitString != 0)
                {
                    val = bitString & 0xFF;
                    break;
                }
            }
        }
 
        if (val == 0)
        {
            return 7;
        }


        int bits = 1;

        while (((val <<= 1) & 0xFF) != 0)
        {
            bits++;
        }

        return 8 - bits;
    }

    /**
     * return the correct number of bytes for a bit string defined in
     * a 32 bit constant
     */
    static protected byte[] getBytes(int bitString)
    {
        int bytes = 4;
        for (int i = 3; i >= 1; i--)
        {
            if ((bitString & (0xFF << (i * 8))) != 0)
            {
                break;
            }
            bytes--;
        }
        
        byte[] result = new byte[bytes];
        for (int i = 0; i < bytes; i++)
        {
            result[i] = (byte) ((bitString >> (i * 8)) & 0xFF);
        }

        return result;
    }

    /**
     * return a Bit String from the passed in object
     *
     * @exception IllegalArgumentException if the object cannot be converted.
     */
    public static DERBitString getInstance(
        Object  obj)
    {
        if (obj == null || obj instanceof DERBitString)
        {
            return (DERBitString)obj;
        }

        if (obj instanceof ASN1OctetString)
        {
            byte[]  bytes = ((ASN1OctetString)obj).getOctets();
            int     padBits = bytes[0];
            byte[]  data = new byte[bytes.length - 1];

            System.arraycopy(bytes, 1, data, 0, bytes.length - 1);

            return new DERBitString(data, padBits);
        }

        if (obj instanceof ASN1TaggedObject)
        {
            return getInstance(((ASN1TaggedObject)obj).getObject());
        }

        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    /**
     * return a Bit String from a tagged object.
     *
     * @param obj the tagged object holding the object we want
     * @param explicit true if the object is meant to be explicitly
     *              tagged false otherwise.
     * @exception IllegalArgumentException if the tagged object cannot
     *               be converted.
     */
    public static DERBitString getInstance(
        ASN1TaggedObject obj,
        boolean          explicit)
    {
        return getInstance(obj.getObject());
    }
    
    protected DERBitString(
        byte    data,
        int     padBits)
    {
        this.data = new byte[1];
        this.data[0] = data;
        this.padBits = padBits;
    }

    /**
     * @param data the octets making up the bit string.
     * @param padBits the number of extra bits at the end of the string.
     */
    public DERBitString(
        byte[]  data,
        int     padBits)
    {
        this.data = data;
        this.padBits = padBits;
    }

    public DERBitString(
        byte[]  data)
    {
        this(data, 0);
    }

    public DERBitString(
        DEREncodable  obj)
    {
        try
        {
            ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
            DEROutputStream         dOut = new DEROutputStream(bOut);

            dOut.writeObject(obj);
            dOut.close();

            this.data = bOut.toByteArray();
            this.padBits = 0;
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException("Error processing object : " + e.toString());
        }
    }

    public byte[] getBytes()
    {
        return data;
    }

    public int getPadBits()
    {
        return padBits;
    }


    /**
     * @return the value of the bit string as an int (truncating if necessary)
     */
    public int intValue()
    {
        int value = 0;
        
        for (int i = 0; i != data.length && i != 4; i++)
        {
            value |= (data[i] & 0xff) << (8 * i);
        }
        
        return value;
    }
    
    void encode(
        DEROutputStream  out)
        throws IOException
    {
        byte[]  bytes = new byte[getBytes().length + 1];

        bytes[0] = (byte)getPadBits();
        System.arraycopy(getBytes(), 0, bytes, 1, bytes.length - 1);

        out.writeEncoded(BIT_STRING, bytes);
    }

    public int hashCode()
    {
        int     value = 0;

        for (int i = 0; i != data.length; i++)
        {
            value ^= (data[i] & 0xff) << (i % 4);
        }

        return value;
    }
    
    protected boolean asn1Equals(
        DERObject  o)
    {
        if (!(o instanceof DERBitString))
        {
            return false;
        }

        DERBitString  other = (DERBitString)o;

        if (data.length != other.data.length)
        {
            return false;
        }

        for (int i = 0; i != data.length; i++)
        {
            if (data[i] != other.data[i])
            {
                return false;
            }
        }

        return (padBits == other.padBits);
    }

    public String getString()
    {
        StringBuffer          buf = new StringBuffer("#");
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ASN1OutputStream      aOut = new ASN1OutputStream(bOut);
        
        try
        {
            aOut.writeObject(this);
        }
        catch (IOException e)
        {
           throw new RuntimeException("internal error encoding BitString");
        }
        
        byte[]    string = bOut.toByteArray();
        
        for (int i = 0; i != string.length; i++)
        {
            buf.append(table[(string[i] >>> 4) & 0xf]);
            buf.append(table[string[i] & 0xf]);
        }
        
        return buf.toString();
    }

    public String toString()
    {
        return getString();
    }
}
	
	/**
	 * @deprecated use DERSequence.
	 */
	public static class DERConstructedSequence
	    extends ASN1Sequence
	{
	    public void addObject(
	        DEREncodable obj)
	    {
	        super.addObject(obj);
	    }

	    public int getSize()
	    {
	        return size();
	    }

	    /*
	     * A note on the implementation:
	     * <p>
	     * As DER requires the constructed, definite-length model to
	     * be used for structured types, this varies slightly from the
	     * ASN.1 descriptions given. Rather than just outputing SEQUENCE,
	     * we also have to specify CONSTRUCTED, and the objects length.
	     */
	    void encode(
	        DEROutputStream out)
	        throws IOException
	    {
	        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
	        DEROutputStream         dOut = new DEROutputStream(bOut);
	        Enumeration             e = this.getObjects();

	        while (e.hasMoreElements())
	        {
	            Object    obj = e.nextElement();

	            dOut.writeObject(obj);
	        }

	        dOut.close();

	        byte[]  bytes = bOut.toByteArray();

	        out.writeEncoded(SEQUENCE | CONSTRUCTED, bytes);
	    }
	}

	
	public static class DERConstructedSet
    extends ASN1Set
{
    public DERConstructedSet()
    {
    }

    /**
     * @param obj - a single object that makes up the set.
     */
    public DERConstructedSet(
        DEREncodable   obj)
    {
        this.addObject(obj);
    }

    /**
     * @param v - a vector of objects making up the set.
     */
    public DERConstructedSet(
        DEREncodableVector   v)
    {
        for (int i = 0; i != v.size(); i++)
        {
            this.addObject(v.get(i));
        }
    }

    public void addObject(
        DEREncodable    obj)
    {
        super.addObject(obj);
    }

    public int getSize()
    {
        return size();
    }

    /*
     * A note on the implementation:
     * <p>
     * As DER requires the constructed, definite-length model to
     * be used for structured types, this varies slightly from the
     * ASN.1 descriptions given. Rather than just outputing SET,
     * we also have to specify CONSTRUCTED, and the objects length.
     */
    void encode(
        DEROutputStream out)
        throws IOException
    {
        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
        DEROutputStream         dOut = new DEROutputStream(bOut);
        Enumeration             e = this.getObjects();

        while (e.hasMoreElements())
        {
            Object    obj = e.nextElement();

            dOut.writeObject(obj);
        }

        dOut.close();

        byte[]  bytes = bOut.toByteArray();

        out.writeEncoded(SET | CONSTRUCTED, bytes);
    }
}
	
	public static interface DEREncodable
	{
	    public DERObject getDERObject();
	}


	public static class DEREncodableVector
	{
	    private Vector  v = new Vector();

	    /**
	     * @deprecated use ASN1EncodableVector instead.
	     */
	    public DEREncodableVector()
	    {

	    }
	    
	    public void add(
	        DEREncodable   obj)
	    {
	        v.addElement(obj);
	    }

	    public DEREncodable get(
	        int i)
	    {
	        return (DEREncodable)v.elementAt(i);
	    }

	    public int size()
	    {
	        return v.size();
	    }
	}
	
	public static class DEREnumerated
    extends ASN1Object
{
    byte[]      bytes;

    /**
     * return an integer from the passed in object
     *
     * @exception IllegalArgumentException if the object cannot be converted.
     */
    public static DEREnumerated getInstance(
        Object  obj)
    {
        if (obj == null || obj instanceof DEREnumerated)
        {
            return (DEREnumerated)obj;
        }

        if (obj instanceof ASN1OctetString)
        {
            return new DEREnumerated(((ASN1OctetString)obj).getOctets());
        }

        if (obj instanceof ASN1TaggedObject)
        {
            return getInstance(((ASN1TaggedObject)obj).getObject());
        }

        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    /**
     * return an Enumerated from a tagged object.
     *
     * @param obj the tagged object holding the object we want
     * @param explicit true if the object is meant to be explicitly
     *              tagged false otherwise.
     * @exception IllegalArgumentException if the tagged object cannot
     *               be converted.
     */
    public static DEREnumerated getInstance(
        ASN1TaggedObject obj,
        boolean          explicit)
    {
        return getInstance(obj.getObject());
    }

    public DEREnumerated(
        int         value)
    {
        bytes = BigInteger.valueOf(value).toByteArray();
    }

    public DEREnumerated(
        BigInteger   value)
    {
        bytes = value.toByteArray();
    }

    public DEREnumerated(
        byte[]   bytes)
    {
        this.bytes = bytes;
    }

    public BigInteger getValue()
    {
        return new BigInteger(bytes);
    }

    void encode(
        DEROutputStream out)
        throws IOException
    {
        out.writeEncoded(ENUMERATED, bytes);
    }
    
    boolean asn1Equals(
        DERObject  o)
    {
        if (!(o instanceof DEREnumerated))
        {
            return false;
        }

        DEREnumerated other = (DEREnumerated)o;

        if (bytes.length != other.bytes.length)
        {
            return false;
        }

        for (int i = 0; i != bytes.length; i++)
        {
            if (bytes[i] != other.bytes[i])
            {
                return false;
            }
        }

        return true;
    }
    
    public int hashCode()
    {
        return this.getValue().hashCode();
    }
}

	
	
	
	public static class DERInputStream
    extends FilterInputStream implements DERTags
{
    /**
     * @deprecated use ASN1InputStream
     */
    public DERInputStream(
        InputStream is)
    {
        super(is);
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
        }

        return length;
    }

    protected void readFully(
        byte[]  bytes)
        throws IOException
    {
        int     left = bytes.length;

        if (left == 0)
        {
            return;
        }

        while (left > 0)
        {
            int    l = read(bytes, bytes.length - left, left);
            
            if (l < 0)
            {
                throw new EOFException("unexpected end of stream");
            }
            
            left -= l;
        }
    }

    /**
     * build an object given its tag and a byte stream to construct it
     * from.
     */
    protected DERObject buildObject(
        int       tag,
        byte[]    bytes)
        throws IOException
    {
        switch (tag)
        {
        case NULL:
            return null;   
        case SEQUENCE | CONSTRUCTED:
            ByteArrayInputStream    bIn = new ByteArrayInputStream(bytes);
            BERInputStream          dIn = new BERInputStream(bIn);
            DERConstructedSequence  seq = new DERConstructedSequence();

            try
            {
                for (;;)
                {
                    DERObject   obj = dIn.readObject();

                    seq.addObject(obj);
                }
            }
            catch (EOFException ex)
            {
                return seq;
            }
        case SET | CONSTRUCTED:
            bIn = new ByteArrayInputStream(bytes);
            dIn = new BERInputStream(bIn);

            ASN1EncodableVector    v = new ASN1EncodableVector();

            try
            {
                for (;;)
                {
                    DERObject   obj = dIn.readObject();

                    v.add(obj);
                }
            }
            catch (EOFException ex)
            {
                return new DERConstructedSet(v);
            }

        case INTEGER:
            return new DERInteger(bytes);
        case ENUMERATED:
            return new DEREnumerated(bytes);
        case OBJECT_IDENTIFIER:
            return new DERObjectIdentifier(bytes);
        case BIT_STRING:
            int     padBits = bytes[0];
            byte[]  data = new byte[bytes.length - 1];

            System.arraycopy(bytes, 1, data, 0, bytes.length - 1);

            return new DERBitString(data, padBits);

        case OCTET_STRING:
            return new DEROctetString(bytes);

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

                if (bytes.length == 0)        // empty tag!
                {
                    if ((tag & CONSTRUCTED) == 0)
                    {
                        return new DERTaggedObject(false, tag & 0x1f, new DERNull());
                    }
                    else
                    {
                        return new DERTaggedObject(false, tag & 0x1f, new DERConstructedSequence());
                    }
                }

                //
                // simple type - implicit... return an octet string
                //
                if ((tag & CONSTRUCTED) == 0)
                {
                    return new DERTaggedObject(false, tag & 0x1f, new DEROctetString(bytes));
                }

                bIn = new ByteArrayInputStream(bytes);
                dIn = new BERInputStream(bIn);

                DEREncodable dObj = dIn.readObject();

                //
                // explicitly tagged (probably!) - if it isn't we'd have to
                // tell from the context
                //
                if (dIn.available() == 0)
                {
                    return new DERTaggedObject(tag & 0x1f, dObj);
                }

                //
                // another implicit object, we'll create a sequence...
                //
                seq = new DERConstructedSequence();

                seq.addObject(dObj);

                try
                {
                    for (;;)
                    {
                        dObj = dIn.readObject();

                        seq.addObject(dObj);
                    }
                }
                catch (EOFException ex)
                {
                    // ignore --
                }

               /* return new DERTaggedObject(false, tag & 0x1f, seq);*/
            }

            return new DERUnknownTag(tag, bytes);
        }
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
        byte[]  bytes = new byte[length];

        readFully(bytes);

        return buildObject(tag, bytes);
    }
}

	
	public static class DERInteger
    extends ASN1Object
{
    byte[]      bytes;

    /**
     * return an integer from the passed in object
     *
     * @exception IllegalArgumentException if the object cannot be converted.
     */
    public static DERInteger getInstance(
        Object  obj)
    {
        if (obj == null || obj instanceof DERInteger)
        {
            return (DERInteger)obj;
        }

        if (obj instanceof ASN1OctetString)
        {
            return new DERInteger(((ASN1OctetString)obj).getOctets());
        }

        if (obj instanceof ASN1TaggedObject)
        {
            return getInstance(((ASN1TaggedObject)obj).getObject());
        }

        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    /**
     * return an Integer from a tagged object.
     *
     * @param obj the tagged object holding the object we want
     * @param explicit true if the object is meant to be explicitly
     *              tagged false otherwise.
     * @exception IllegalArgumentException if the tagged object cannot
     *               be converted.
     */
    public static DERInteger getInstance(
        ASN1TaggedObject obj,
        boolean          explicit)
    {
        return getInstance(obj.getObject());
    }

    public DERInteger(
        int         value)
    {
        bytes = BigInteger.valueOf(value).toByteArray();
    }

    public DERInteger(
        BigInteger   value)
    {
        bytes = value.toByteArray();
    }

    public DERInteger(
        byte[]   bytes)
    {
        this.bytes = bytes;
    }

    public BigInteger getValue()
    {
        return new BigInteger(bytes);
    }

    /**
     * in some cases positive values get crammed into a space,
     * that's not quite big enough...
     */
    public BigInteger getPositiveValue()
    {
        return new BigInteger(1, bytes);
    }

    void encode(
        DEROutputStream out)
        throws IOException
    {
        out.writeEncoded(INTEGER, bytes);
    }
    
    public int hashCode()
    {
         int     value = 0;
 
         for (int i = 0; i != bytes.length; i++)
         {
             value ^= (bytes[i] & 0xff) << (i % 4);
         }
 
         return value;
    }

    boolean asn1Equals(
        DERObject  o)
    {
        if (!(o instanceof DERInteger))
        {
            return false;
        }

        DERInteger other = (DERInteger)o;

        if (bytes.length != other.bytes.length)
        {
            return false;
        }

        for (int i = 0; i != bytes.length; i++)
        {
            if (bytes[i] != other.bytes[i])
            {
                return false;
            }
        }

        return true;
    }

    public String toString()
    {
      return getValue().toString();
    }
}

	
	
	public static class DERNull
    extends ASN1Null
{
    public static final DERNull INSTANCE = new DERNull();

    byte[]  zeroBytes = new byte[0];

    public DERNull()
    {
    }

    void encode(
        DEROutputStream  out)
        throws IOException
    {
        out.writeEncoded(NULL, zeroBytes);
    }
}

	
	
	public abstract static class DERObject
    extends ASN1Encodable
    implements DERTags
{
    public DERObject toASN1Object()
    {
        return this;
    }
    
    public abstract int hashCode();
    
    public abstract boolean equals(Object o);
    
    abstract void encode(DEROutputStream out)
        throws IOException;
}
	
	public static class DERObjectIdentifier
    extends ASN1Object
{
    String      identifier;

    /**
     * return an OID from the passed in object
     *
     * @exception IllegalArgumentException if the object cannot be converted.
     */
    public static DERObjectIdentifier getInstance(
        Object  obj)
    {
        if (obj == null || obj instanceof DERObjectIdentifier)
        {
            return (DERObjectIdentifier)obj;
        }

        if (obj instanceof ASN1OctetString)
        {
            return new DERObjectIdentifier(((ASN1OctetString)obj).getOctets());
        }

        if (obj instanceof ASN1TaggedObject)
        {
            return getInstance(((ASN1TaggedObject)obj).getObject());
        }

        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    /**
     * return an Object Identifier from a tagged object.
     *
     * @param obj the tagged object holding the object we want
     * @param explicit true if the object is meant to be explicitly
     *              tagged false otherwise.
     * @exception IllegalArgumentException if the tagged object cannot
     *               be converted.
     */
    public static DERObjectIdentifier getInstance(
        ASN1TaggedObject obj,
        boolean          explicit)
    {
        return getInstance(obj.getObject());
    }
    

    DERObjectIdentifier(
        byte[]  bytes)
    {
        StringBuffer    objId = new StringBuffer();
        long            value = 0;
        BigInteger      bigValue = null;
        boolean         first = true;

        for (int i = 0; i != bytes.length; i++)
        {
            int b = bytes[i] & 0xff;

            if (value < 0x80000000000000L) 
            {
                value = value * 128 + (b & 0x7f);
                if ((b & 0x80) == 0)             // end of number reached
                {
                    if (first)
                    {
                        switch ((int)value / 40)
                        {
                        case 0:
                            objId.append('0');
                            break;
                        case 1:
                            objId.append('1');
                            value -= 40;
                            break;
                        default:
                            objId.append('2');
                            value -= 80;
                        }
                        first = false;
                    }

                    objId.append('.');
                    objId.append(value);
                    value = 0;
                }
            } 
            else 
            {
                if (bigValue == null)
                {
                    bigValue = BigInteger.valueOf(value);
                }
                bigValue = bigValue.shiftLeft(7);
                bigValue = bigValue.or(BigInteger.valueOf(b & 0x7f));
                if ((b & 0x80) == 0) 
                {
                    objId.append('.');
                    objId.append(bigValue);
                    bigValue = null;
                    value = 0;
                }
            }
        }

        this.identifier = objId.toString();
    }

    public DERObjectIdentifier(
        String  identifier)
    {
        if (!isValidIdentifier(identifier))
        {
            throw new IllegalArgumentException("string " + identifier + " not an OID");
        }

        this.identifier = identifier;
    }

    public String getId()
    {
        return identifier;
    }

    private void writeField(
        OutputStream    out,
        long            fieldValue)
        throws IOException
    {
        if (fieldValue >= (1L << 7))
        {
            if (fieldValue >= (1L << 14))
            {
                if (fieldValue >= (1L << 21))
                {
                    if (fieldValue >= (1L << 28))
                    {
                        if (fieldValue >= (1L << 35))
                        {
                            if (fieldValue >= (1L << 42))
                            {
                                if (fieldValue >= (1L << 49))
                                {
                                    if (fieldValue >= (1L << 56))
                                    {
                                        out.write((int)(fieldValue >> 56) | 0x80);
                                    }
                                    out.write((int)(fieldValue >> 49) | 0x80);
                                }
                                out.write((int)(fieldValue >> 42) | 0x80);
                            }
                            out.write((int)(fieldValue >> 35) | 0x80);
                        }
                        out.write((int)(fieldValue >> 28) | 0x80);
                    }
                    out.write((int)(fieldValue >> 21) | 0x80);
                }
                out.write((int)(fieldValue >> 14) | 0x80);
            }
            out.write((int)(fieldValue >> 7) | 0x80);
        }
        out.write((int)fieldValue & 0x7f);
    }

    private void writeField(
        OutputStream    out,
        BigInteger      fieldValue)
        throws IOException
    {
        int byteCount = (fieldValue.bitLength()+6)/7;
        if (byteCount == 0) 
        {
            out.write(0);
        }  
        else 
        {
            BigInteger tmpValue = fieldValue;
            byte[] tmp = new byte[byteCount];
            for (int i = byteCount-1; i >= 0; i--) 
            {
                tmp[i] = (byte) ((tmpValue.intValue() & 0x7f) | 0x80);
                tmpValue = tmpValue.shiftRight(7); 
            }
            tmp[byteCount-1] &= 0x7f;
            out.write(tmp);
        }

    }

    void encode(
        DEROutputStream out)
        throws IOException
    {
        OIDTokenizer            tok = new OIDTokenizer(identifier);
        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
        DEROutputStream         dOut = new DEROutputStream(bOut);

        writeField(bOut, 
                    Integer.parseInt(tok.nextToken()) * 40
                    + Integer.parseInt(tok.nextToken()));

        while (tok.hasMoreTokens())
        {
            String token = tok.nextToken();
            if (token.length() < 18) 
            {
                writeField(bOut, Long.parseLong(token));
            }
            else
            {
                writeField(bOut, new BigInteger(token));
            }
        }

        dOut.close();

        byte[]  bytes = bOut.toByteArray();

        out.writeEncoded(OBJECT_IDENTIFIER, bytes);
    }

    public int hashCode()
    {
        return identifier.hashCode();
    }

    boolean asn1Equals(
        DERObject  o)
    {
        if (!(o instanceof DERObjectIdentifier))
        {
            return false;
        }

        return identifier.equals(((DERObjectIdentifier)o).identifier);
    }

    public String toString()
    {
        return getId();
    }

    private static boolean isValidIdentifier(
        String identifier)
    {
        if (identifier.length() < 3
            || identifier.charAt(1) != '.')
        {
            return false;
        }

        char first = identifier.charAt(0);
        if (first < '0' || first > '2')
        {
            return false;
        }

        boolean periodAllowed = false;
        for (int i = identifier.length() - 1; i >= 2; i--)
        {
            char ch = identifier.charAt(i);

            if ('0' <= ch && ch <= '9')
            {
                periodAllowed = true;
                continue;
            }

            if (ch == '.')
            {
                if (!periodAllowed)
                {
                    return false;
                }

                periodAllowed = false;
                continue;
            }

            return false;
        }

        return periodAllowed;
    }
}

	
	
	public static class DEROctetString
    extends ASN1OctetString
{
    /**
     * @param string the octets making up the octet string.
     */
    public DEROctetString(
        byte[]  string)
    {
        super(string);
    }

    public DEROctetString(
        DEREncodable  obj)
    {
        super(obj);
    }

    void encode(
        DEROutputStream out)
        throws IOException
    {
        out.writeEncoded(OCTET_STRING, string);
    }
}
	
	public static class DEROutputStream
    extends FilterOutputStream implements DERTags
{
    public DEROutputStream(
        OutputStream    os)
    {
        super(os);
    }

    private void writeLength(
        int length)
        throws IOException
    {
        if (length > 127)
        {
            int size = 1;
            int val = length;

            while ((val >>>= 8) != 0)
            {
                size++;
            }

            write((byte)(size | 0x80));

            for (int i = (size - 1) * 8; i >= 0; i -= 8)
            {
                write((byte)(length >> i));
            }
        }
        else
        {
            write((byte)length);
        }
    }

    void writeEncoded(
        int     tag,
        byte[]  bytes)
        throws IOException
    {
        write(tag);
        writeLength(bytes.length);
        write(bytes);
    }

    protected void writeNull()
        throws IOException
    {
        write(NULL);
        write(0x00);
    }

    public void write(byte[] buf)
        throws IOException
    {
        out.write(buf, 0, buf.length);
    }

    public void write(byte[] buf, int offSet, int len)
        throws IOException
    {
        out.write(buf, offSet, len);
    }

    public void writeObject(
        Object    obj)
        throws IOException
    {
        if (obj == null)
        {
            writeNull();
        }
        else if (obj instanceof DERObject)
        {
            ((DERObject)obj).encode(this);
        }
        else if (obj instanceof DEREncodable)
        {
        	DERObject x = ((DEREncodable)obj).getDERObject();
            x.encode(this);
        }
        else 
        {
            throw new IOException("object not DEREncodable");
        }
    }
}

	
	
	public static class DERSequence
    extends ASN1Sequence
{
    /**
     * create an empty sequence
     */
    public DERSequence()
    {
    }

    /**
     * create a sequence containing one object
     */
    public DERSequence(
        DER.DEREncodable    obj)
    {
        this.addObject(obj);
    }

    /**
     * create a sequence containing a vector of objects.
     */
    public DERSequence(
        DEREncodableVector   v)
    {
        for (int i = 0; i != v.size(); i++)
        {
            this.addObject(v.get(i));
        }
    }

    /**
     * create a sequence containing an array of objects.
     */
    public DERSequence(
        ASN1Encodable[]   a)
    {
        for (int i = 0; i != a.length; i++)
        {
            this.addObject(a[i]);
        }
    }
    
    /*
     * A note on the implementation:
     * <p>
     * As DER requires the constructed, definite-length model to
     * be used for structured types, this varies slightly from the
     * ASN.1 descriptions given. Rather than just outputing SEQUENCE,
     * we also have to specify CONSTRUCTED, and the objects length.
     */
    void encode(
        DEROutputStream out)
        throws IOException
    {
        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
        DEROutputStream         dOut = new DEROutputStream(bOut);
        Enumeration             e = this.getObjects();

        while (e.hasMoreElements())
        {
            Object    obj = e.nextElement();

            dOut.writeObject(obj);
        }

        dOut.close();

        byte[]  bytes = bOut.toByteArray();

        out.writeEncoded(SEQUENCE | CONSTRUCTED, bytes);
    }
}

	
	public static class DERSet
    extends ASN1Set
{
    /**
     * create an empty set
     */
    public DERSet()
    {
    }

    /**
     * @param obj - a single object that makes up the set.
     */
    public DERSet(
        DEREncodable   obj)
    {
        this.addObject(obj);
    }

    /**
     * @param v - a vector of objects making up the set.
     */
    public DERSet(
        DEREncodableVector   v)
    {
        this(v, true);
    }
    
    /**
     * create a set from an array of objects.
     */
    public DERSet(
        ASN1Encodable[]   a)
    {
        for (int i = 0; i != a.length; i++)
        {
            this.addObject(a[i]);
        }
        
        this.sort();
    }
    
    /**
     * @param v - a vector of objects making up the set.
     */
    DERSet(
        DEREncodableVector   v,
        boolean              needsSorting)
    {
        for (int i = 0; i != v.size(); i++)
        {
            this.addObject(v.get(i));
        }

        if (needsSorting)
        {
            this.sort();
        }
    }

    /*
     * A note on the implementation:
     * <p>
     * As DER requires the constructed, definite-length model to
     * be used for structured types, this varies slightly from the
     * ASN.1 descriptions given. Rather than just outputing SET,
     * we also have to specify CONSTRUCTED, and the objects length.
     */
    void encode(
        DEROutputStream out)
        throws IOException
    {
        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
        DEROutputStream         dOut = new DEROutputStream(bOut);
        Enumeration             e = this.getObjects();

        while (e.hasMoreElements())
        {
            Object    obj = e.nextElement();

            dOut.writeObject(obj);
        }

        dOut.close();

        byte[]  bytes = bOut.toByteArray();

        out.writeEncoded(SET | CONSTRUCTED, bytes);
    }
}

	
	public interface DERString
	{
	    public String getString();
	}


	
	public static class DERTaggedObject
    extends ASN1TaggedObject
{
    /**
     * @param tagNo the tag number for this object.
     * @param obj the tagged object.
     */
    public DERTaggedObject(
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
    public DERTaggedObject(
        boolean         explicit,
        int             tagNo,
        DEREncodable    obj)
    {
        super(explicit, tagNo, obj);
    }

    /**
     * create an implicitly tagged object that contains a zero
     * length sequence.
     */
    public DERTaggedObject(
        int             tagNo)
    {
        super(false, tagNo, new DERSequence());
    }

    void encode(
        DEROutputStream  out)
        throws IOException
    {
        if (!empty)
        {
            ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
            DEROutputStream         dOut = new DEROutputStream(bOut);

            dOut.writeObject(obj);
            dOut.close();

            byte[]  bytes = bOut.toByteArray();

            if (explicit)
            {
                out.writeEncoded(CONSTRUCTED | TAGGED | tagNo, bytes);
            }
            else
            {
                //
                // need to mark constructed types...
                //
                if ((bytes[0] & CONSTRUCTED) != 0)
                {
                    bytes[0] = (byte)(CONSTRUCTED | TAGGED | tagNo);
                }
                else
                {
                    bytes[0] = (byte)(TAGGED | tagNo);
                }

                out.write(bytes);
            }
        }
        else
        {
            out.writeEncoded(CONSTRUCTED | TAGGED | tagNo, new byte[0]);
        }
    }
}

	
	public interface DERTags
	{
	    public static final int BOOLEAN             = 0x01;
	    public static final int INTEGER             = 0x02;
	    public static final int BIT_STRING          = 0x03;
	    public static final int OCTET_STRING        = 0x04;
	    public static final int NULL                = 0x05;
	    public static final int OBJECT_IDENTIFIER   = 0x06;
	    public static final int EXTERNAL            = 0x08;
	    public static final int ENUMERATED          = 0x0a;
	    public static final int SEQUENCE            = 0x10;
	    public static final int SEQUENCE_OF         = 0x10; // for completeness
	    public static final int SET                 = 0x11;
	    public static final int SET_OF              = 0x11; // for completeness


	    public static final int NUMERIC_STRING      = 0x12;
	    public static final int PRINTABLE_STRING    = 0x13;
	    public static final int T61_STRING          = 0x14;
	    public static final int VIDEOTEX_STRING     = 0x15;
	    public static final int IA5_STRING          = 0x16;
	    public static final int UTC_TIME            = 0x17;
	    public static final int GENERALIZED_TIME    = 0x18;
	    public static final int GRAPHIC_STRING      = 0x19;
	    public static final int VISIBLE_STRING      = 0x1a;
	    public static final int GENERAL_STRING      = 0x1b;
	    public static final int UNIVERSAL_STRING    = 0x1c;
	    public static final int BMP_STRING          = 0x1e;
	    public static final int UTF8_STRING         = 0x0c;
	    
	    public static final int CONSTRUCTED         = 0x20;
	    public static final int APPLICATION         = 0x40;
	    public static final int TAGGED              = 0x80;
	}

	
	public static class DERUnknownTag
    extends DERObject
{
    int         tag;
    byte[]      data;

    /**
     * @param tag the tag value.
     * @param data the octets making up the time.
     */
    public DERUnknownTag(
        int     tag,
        byte[]  data)
    {
        this.tag = tag;
        this.data = data;
    }

    public int getTag()
    {
        return tag;
    }

    public byte[] getData()
    {
        return data;
    }

    void encode(
        DEROutputStream  out)
        throws IOException
    {
        out.writeEncoded(tag, data);
    }
    
    public boolean equals(
        Object o)
    {
        if (!(o instanceof DERUnknownTag))
        {
            return false;
        }
        
        DERUnknownTag other = (DERUnknownTag)o;
        
        if (tag != other.tag)
        {
            return false;
        }
        
        if (data.length != other.data.length)
        {
            return false;
        }
        
        for (int i = 0; i < data.length; i++) 
        {
            if(data[i] != other.data[i])
            {
                return false;
            }
        }
        
        return true;
    }
    
    public int hashCode()
    {
        byte[]  b = this.getData();
        int     value = 0;

        for (int i = 0; i != b.length; i++)
        {
            value ^= (b[i] & 0xff) << (i % 4);
        }

        return value ^ this.getTag();
    }
}

	
	
	
}
