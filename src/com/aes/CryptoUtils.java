package com.aes;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.List;

public class CryptoUtils {

    public static byte[] getRandomBytes( int numBytes ) {
        byte[] nonce = new byte[ numBytes ];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    //  AES Secret Key
    public static SecretKey getAESKey ( int keySize ) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(keySize, SecureRandom.getInstanceStrong());
        return keyGenerator.generateKey();
    }

    //  Password derived AES 256 bits secret key
    public static SecretKey getAESKeyFromPassword( char[] password, byte[] salt ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        //  iterationCount = 65536
        //  keyLength = 256
        KeySpec keySpec = new PBEKeySpec(password, salt, 65536, 256);
        SecretKey secretKey = new SecretKeySpec(secretKeyFactory.generateSecret(keySpec).getEncoded(), "AES");
        return secretKey;
    }

    //  Hex representation
    public static String hex( byte[] bytes ) {
        StringBuilder result = new StringBuilder();
        for ( byte b : bytes ) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    //  print hex with block size split
    public static String hexWithBlockSize( byte[] bytes, int blockSize ) {
        String hex = hex( bytes );
        blockSize = blockSize * 2;  //  one hex = 2 chars
        List<String> result = new ArrayList<>();
        for ( int i = 0; i < hex.length(); i+=blockSize ) {
            result.add( hex.substring(i, Math.min(i + blockSize, hex.length())) );
        }
        return result.toString();
    }

}






