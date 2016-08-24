package com.iGap;

import android.util.Log;

import com.iGap.helper.HelperNumerical;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encrypt and decrypt messages using AES 256 bit encryption that are compatible with AESCrypt-ObjC and AESCrypt Ruby.
 */
public final class AESCrypt {


    private static final String AES_MODE = "AES/" + G.symmetricMethod + "/PKCS5Padding"; //AES/CBC/PKCS7Padding , AES/CBC/NoPadding , AES/CBC/PKCS5Padding , ISO10126Padding ,RSA/NONE/PKCS1Padding

    /**
     * More flexible AES encrypt that doesn't encode
     *
     * @param key     AES key typically 128, 192 or 256 bit
     * @param message in bytes (assumed it's already been decoded)
     * @return Encrypted cipher text (not encoded)
     * @throws GeneralSecurityException if something goes wrong during encryption
     */
    public static byte[] encrypt(final SecretKeySpec key, final byte[] message) throws GeneralSecurityException {

        try {
            final Cipher cipher = Cipher.getInstance("AES/" + G.symmetricMethod + "/PKCS5Padding");
            SecureRandom r = new SecureRandom();
            byte[] ivBytes = new byte[G.ivSize];
            r.nextBytes(ivBytes);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            byte[] encryptMessage = cipher.doFinal(message);
            byte[] finalEncryptMessage = HelperNumerical.appendByteArrays(ivBytes, encryptMessage);

            return finalEncryptMessage;
        } catch (Exception e) {
            Log.i("WEB_REQ", "RequestQueue 16 Error : " + e);
        }

        return null;
    }

    /**
     * More flexible AES decrypt that doesn't encode
     *
     * @param key               AES key typically 128, 192 or 256 bit
     * @param iv                Initiation Vector
     * @param decodedCipherText in bytes (assumed it's already been decoded)
     * @return Decrypted message cipher text (not encoded)
     * @throws GeneralSecurityException if something goes wrong during encryption
     */
    public static byte[] decrypt(final SecretKeySpec key, final byte[] iv, final byte[] decodedCipherText) throws GeneralSecurityException {

//        final Cipher cipher = Cipher.getInstance(AES_MODE);
        final Cipher cipher = Cipher.getInstance("AES/" + G.symmetricMethod + "/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(decodedCipherText);

        return decryptedBytes;
    }

    /**
     * encrypt symmetricKey with PublicKey
     *
     * @param key          publicKey that get from server
     * @param symmetricKey random String that generate in client
     * @return
     */

    public static byte[] encryptSymmetricKey(PublicKey key, byte[] symmetricKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypt = cipher.doFinal(symmetricKey);
            return encrypt;
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException e) {
            Log.i("SOC_REQ", "encrypt error : " + e);
        }
        return null;
    }
}
