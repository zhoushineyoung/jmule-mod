package org.jmule.core.bccrypto;


import java.io.IOException;

import static org.jmule.core.bccrypto.ASN1.*;
import static org.jmule.core.bccrypto.DER.*;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

public abstract class JDKKeyFactory
{
protected boolean elGamalFactory = false;

public JDKKeyFactory()
{
}

 public PrivateKey engineGeneratePrivate(
    KeySpec keySpec)
    throws InvalidKeySpecException
{
    if (keySpec instanceof PKCS8EncodedKeySpec)
    {
        try
        {
            return JDKKeyFactory.createPrivateKeyFromDERStream(
                ((PKCS8EncodedKeySpec)keySpec).getEncoded());
        }
        catch (Exception e)
        {
            throw new InvalidKeySpecException(e.toString());
        }
    }

    throw new InvalidKeySpecException("Unknown KeySpec type: " + keySpec.getClass().getName());
}

public PublicKey engineGeneratePublic(
    KeySpec    keySpec)
    throws InvalidKeySpecException
{
    if (keySpec instanceof X509EncodedKeySpec)
    {
        try
        {
            return JDKKeyFactory.createPublicKeyFromDERStream(
                ((X509EncodedKeySpec)keySpec).getEncoded());
        }
        catch (Exception e)
        {
            throw new InvalidKeySpecException(e.toString());
        }
    }

    throw new InvalidKeySpecException("Unknown KeySpec type: " + keySpec.getClass().getName());
}

protected KeySpec engineGetKeySpec(
    Key    key,
    Class    spec)
throws InvalidKeySpecException
{
   if (spec.isAssignableFrom(PKCS8EncodedKeySpec.class) && key.getFormat().equals("PKCS#8"))
   {
           return new PKCS8EncodedKeySpec(key.getEncoded());
   }
   else if (spec.isAssignableFrom(X509EncodedKeySpec.class) && key.getFormat().equals("X.509"))
   {
           return new X509EncodedKeySpec(key.getEncoded());
   }
   else if (spec.isAssignableFrom(RSAPublicKeySpec.class) && key instanceof RSAPublicKey)
   {
        RSAPublicKey    k = (RSAPublicKey)key;

        return new RSAPublicKeySpec(k.getModulus(), k.getPublicExponent());
   }
   else if (spec.isAssignableFrom(RSAPrivateKeySpec.class) && key instanceof RSAPrivateKey)
   {
        RSAPrivateKey    k = (RSAPrivateKey)key;

        return new RSAPrivateKeySpec(k.getModulus(), k.getPrivateExponent());
   }
   else if (spec.isAssignableFrom(RSAPrivateCrtKeySpec.class) && key instanceof RSAPrivateCrtKey)
   {
        RSAPrivateCrtKey    k = (RSAPrivateCrtKey)key;

        return new RSAPrivateCrtKeySpec(
                        k.getModulus(), k.getPublicExponent(),
                        k.getPrivateExponent(),
                        k.getPrimeP(), k.getPrimeQ(),
                        k.getPrimeExponentP(), k.getPrimeExponentQ(),
                        k.getCrtCoefficient());
   }
   throw new RuntimeException("not implemented yet " + key + " " + spec);
}


/**
 * create a public key from the given DER encoded input stream. 
 */ 
public static PublicKey createPublicKeyFromDERStream(
    byte[]         in)
    throws IOException
{
    return createPublicKeyFromPublicKeyInfo(
        new SubjectPublicKeyInfo((ASN1Sequence) ASN1Object.fromByteArray(in)));
}

/**
 * create a public key from the given public key info object.
 */ 
static PublicKey createPublicKeyFromPublicKeyInfo(
    SubjectPublicKeyInfo         info)
{
    AlgorithmIdentifier     algId = info.getAlgorithmId();
    
    if (algId.getObjectId().equals(Identifiers.rsaEncryption)
    	|| algId.getObjectId().equals(Identifiers.id_ea_rsa))
    {
          return new JCERSAPublicKey(info);
    }
    else
    {
        throw new RuntimeException("algorithm identifier in key not recognised");
    }
}

/**
 * create a private key from the given DER encoded input stream. 
 */ 
protected static PrivateKey createPrivateKeyFromDERStream(
    byte[]         in)
    throws IOException
{
    return createPrivateKeyFromPrivateKeyInfo(
        new PrivateKeyInfo((ASN1Sequence) ASN1Object.fromByteArray(in)));
}

/**
 * create a private key from the given public key info object.
 */ 
static PrivateKey createPrivateKeyFromPrivateKeyInfo(PrivateKeyInfo info) {
    DERObjectIdentifier algOid = info.getAlgorithmId().getObjectId();
    if (RSAUtil.isRsaOid(algOid))
    	return new JCERSAPrivateCrtKey(info);
    throw new RuntimeException("algorithm identifier " + algOid + " in key not recognised");
}

public static class RSA
    extends JDKKeyFactory
{
    public RSA()
    {
    }

    public PrivateKey engineGeneratePrivate(
        KeySpec    keySpec)
        throws InvalidKeySpecException
    {
        if (keySpec instanceof PKCS8EncodedKeySpec)
        {
            try
            {
                return JDKKeyFactory.createPrivateKeyFromDERStream(
                            ((PKCS8EncodedKeySpec)keySpec).getEncoded());
            }
            catch (Exception e)
            {
                //
                // in case it's just a RSAPrivateKey object...
                //
                try
                {
                    return new JCERSAPrivateCrtKey(
                        new RSAPrivateKeyStructure(
                            (ASN1Sequence) ASN1Object.fromByteArray(((PKCS8EncodedKeySpec)keySpec).getEncoded())));
                }
                catch (Exception ex)
                {
                    throw new InvalidKeySpecException(ex.toString());
                }
            }
        }
        else if (keySpec instanceof RSAPrivateCrtKeySpec)
        {
            return new JCERSAPrivateCrtKey((RSAPrivateCrtKeySpec)keySpec);
        }
        else if (keySpec instanceof RSAPrivateKeySpec)
        {
            return new JCERSAPrivateKey((RSAPrivateKeySpec)keySpec);
        }

        throw new InvalidKeySpecException("Unknown KeySpec type: " + keySpec.getClass().getName());
    }

    public PublicKey engineGeneratePublic(
        KeySpec    keySpec)
        throws InvalidKeySpecException
    {
        if (keySpec instanceof RSAPublicKeySpec)
        {
            return new JCERSAPublicKey((RSAPublicKeySpec)keySpec);
        }

        return super.engineGeneratePublic(keySpec);
    }
}

}
