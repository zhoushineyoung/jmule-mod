package org.jmule.core.bccrypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;

import static org.jmule.core.bccrypto.ASN1.*;
import static org.jmule.core.bccrypto.DER.*;

public class JDKDigestSignature
    extends Signature 
{
    private Digest                  digest;
    private AsymmetricBlockCipher   cipher;
    private AlgorithmIdentifier     algId;

    protected JDKDigestSignature(
        String                  name,
        Digest                  digest,
        AsymmetricBlockCipher   cipher)
    {
        super(name);

        this.digest = digest;
        this.cipher = cipher;
        this.algId = new AlgorithmIdentifier(new DERObjectIdentifier("1.3.14.3.2.26"), null);
    }

    protected void engineInitVerify(
        PublicKey   publicKey)
        throws InvalidKeyException
    {
		if ( !(publicKey instanceof RSAPublicKey) )
		{
			throw new InvalidKeyException("Supplied key is not a RSAPublicKey instance");
		}

        CipherParameters    param = RSAUtil.generatePublicKeyParameter((RSAPublicKey)publicKey);

        digest.reset();
        cipher.init(false, param);
    }

    protected void engineInitSign(
        PrivateKey  privateKey)
        throws InvalidKeyException
    {
		if ( !(privateKey instanceof RSAPrivateKey) )
		{
			throw new InvalidKeyException("Supplied key is not a RSAPrivateKey instance");
		}

        CipherParameters    param = RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)privateKey);

        digest.reset();

        cipher.init(true, param);
    }

    protected void engineUpdate(
        byte    b)
        throws SignatureException
    {
        digest.update(b);
    }

    protected void engineUpdate(
        byte[]  b,
        int     off,
        int     len) 
        throws SignatureException
    {
        digest.update(b, off, len);
    }

    protected byte[] engineSign()
        throws SignatureException
    {
        byte[]  hash = new byte[digest.getDigestSize()];

        digest.doFinal(hash, 0);

        try
        {
            byte[]  bytes = derEncode(hash);

            return cipher.processBlock(bytes, 0, bytes.length);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new SignatureException("key too small for signature type");
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            throw new SignatureException(e.toString());
        }
    }

    protected boolean engineVerify(
        byte[]  sigBytes) 
        throws SignatureException
    {
        byte[]  hash = new byte[digest.getDigestSize()];

        digest.doFinal(hash, 0);

        DigestInfo  digInfo;
        byte[]      sig;

        try
        {
            sig = cipher.processBlock(sigBytes, 0, sigBytes.length);
            digInfo = derDecode(sig);
        }
        catch (Exception e)
        {
            return false;
        }

//        if (!digInfo.getAlgorithmId().equals(algId))
//        {
//            return false;
//        }

        byte[]  sigHash = digInfo.getDigest();

        if (hash.length != sigHash.length)
        {
            return false;
        }

        for (int i = 0; i < hash.length; i++)
        {
            if (sigHash[i] != hash[i])
            {
                return false;
            }
        }

        return true;
    }

    protected void engineSetParameter(
        AlgorithmParameterSpec params)
    {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    /**
     * @deprecated replaced with <a href = "#engineSetParameter(java.security.spec.AlgorithmParameterSpec)">
     */
    protected void engineSetParameter(
        String  param,
        Object  value)
    {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    /**
     * @deprecated
     */
    protected Object engineGetParameter(
        String      param)
    {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    private byte[] derEncode(
        byte[]  hash)
        throws IOException
    {
        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
        DEROutputStream         dOut = new DEROutputStream(bOut);
        DigestInfo              dInfo = new DigestInfo(algId, hash);

        dOut.writeObject(dInfo);

        return bOut.toByteArray();
    }

    private DigestInfo derDecode(
        byte[]  encoding)
        throws IOException
    {
        ByteArrayInputStream    bIn = new ByteArrayInputStream(encoding);
        DER.DERInputStream          dIn = new DER.DERInputStream(bIn);

        return new DigestInfo((ASN1Sequence)dIn.readObject());
    }

    
}
