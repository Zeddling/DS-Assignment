package com.aes;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 *  Encryption and decryption a string using 256-bit AES in Galois Counter Mode (GCM)
 *  AES-GCM inputs - 12 bytes IV, need the same IV and secret keys for encryption and decryption.
 *  The output consist of iv, encrypted content, and auth tag in the following format:
 *          output = byte[] {i i i c c c c c c ...}
*   i = IV bytes
 *  c = content bytes (encrypted content, auth tag)
 */
public class EncryptorAESGCM {
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
    private static final Charset UTF_8 = StandardCharsets.UTF_8;


    //  Provide AES-GCM with GCMParameterSpec
    public static byte[] encrypt ( byte[] plainText, String password ) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        byte[] salt = CryptoUtils.getRandomBytes(SALT_LENGTH_BYTE);     //  16 bytes salt
        byte[] iv = CryptoUtils.getRandomBytes(IV_LENGTH_BYTE);         //  12 bytes GCM
        SecretKey aesKeyFromPassword = CryptoUtils.getAESKeyFromPassword(password.toCharArray(), salt); //  Provide GCMParameterSpec to AES-GCM
        Cipher cipher = Cipher.getInstance( ENCRYPT_ALGO );
        cipher.init(Cipher.ENCRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv ));
        byte[] cipherText = cipher.doFinal(plainText);

        //  Prefix IV and salt to cipher text
        byte[] cipherTextWithIvSalt = ByteBuffer.allocate(iv.length + salt.length + cipherText.length)
                .put(iv)
                .put(salt)
                .put(cipherText)
                .array();
        return cipherTextWithIvSalt;
    }


    public static String decrypt( byte[] fullCipheredText, String password ) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        //  Get IV and salt from cipher text
        ByteBuffer byteBuffer = ByteBuffer.wrap(fullCipheredText);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        byteBuffer.get(iv);

        byte[] salt = new byte[SALT_LENGTH_BYTE];
        byteBuffer.get(salt);

        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);

        //  Get back AES key from the password and salt
        SecretKey aesKeyFromPassword = CryptoUtils.getAESKeyFromPassword(password.toCharArray(), salt);
        Cipher cipher = Cipher.getInstance( ENCRYPT_ALGO );
        cipher.init( Cipher.DECRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv) );
        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText, UTF_8);
    }


}





