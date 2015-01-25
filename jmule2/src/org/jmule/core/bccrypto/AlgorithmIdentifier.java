package org.jmule.core.bccrypto;

import org.jmule.core.bccrypto.ASN1.*;

import static org.jmule.core.bccrypto.DER.*;

public class AlgorithmIdentifier
    extends ASN1Encodable
{
    private DER.DERObjectIdentifier objectId;
    private DER.DEREncodable        parameters;
    private boolean             parametersDefined = false;

    public static AlgorithmIdentifier getInstance(
        ASN1TaggedObject obj,
        boolean          explicit)
    {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }
    
    public static AlgorithmIdentifier getInstance(
        Object  obj)
    {
        if (obj== null || obj instanceof AlgorithmIdentifier)
        {
            return (AlgorithmIdentifier)obj;
        }
        
        if (obj instanceof DER.DERObjectIdentifier)
        {
            return new AlgorithmIdentifier((DER.DERObjectIdentifier)obj);
        }

        if (obj instanceof String)
        {
            return new AlgorithmIdentifier((String)obj);
        }

        if (obj instanceof ASN1Sequence)
        {
            return new AlgorithmIdentifier((ASN1Sequence)obj);
        }

        throw new IllegalArgumentException("unknown object in factory");
    }

    public AlgorithmIdentifier(
        DER.DERObjectIdentifier     objectId)
    {
        this.objectId = objectId;
    }

    public AlgorithmIdentifier(
        String     objectId)
    {
        this.objectId = new DER.DERObjectIdentifier(objectId);
    }

    public AlgorithmIdentifier(
        DER.DERObjectIdentifier     objectId,
        DER.DEREncodable            parameters)
    {
        parametersDefined = true;
        this.objectId = objectId;
        this.parameters = parameters;
    }

    public AlgorithmIdentifier(
        ASN1Sequence   seq)
    {
        if (seq.size() < 1 || seq.size() > 2)
        {
            throw new IllegalArgumentException("Bad sequence size: "
                    + seq.size());
        }
        
        objectId = org.jmule.core.bccrypto.DER.DERObjectIdentifier.getInstance(seq.getObjectAt(0));

        if (seq.size() == 2)
        {
            parametersDefined = true;
            parameters = seq.getObjectAt(1);
        }
        else
        {
            parameters = null;
        }
    }

    public DER.DERObjectIdentifier getObjectId()
    {
        return objectId;
    }

    public DER.DEREncodable getParameters()
    {
        return parameters;
    }

    /**
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     *      AlgorithmIdentifier ::= SEQUENCE {
     *                            algorithm OBJECT IDENTIFIER,
     *                            parameters ANY DEFINED BY algorithm OPTIONAL }
     * </pre>
     */
    public DER.DERObject toASN1Object()
    {
        ASN1EncodableVector  v = new ASN1EncodableVector();

        v.add(objectId);

        if (parametersDefined)
        {
            v.add(parameters);
        }

        return new DER.DERSequence(v);
    }
}
