package net.iGap.module.downloader;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesCipherDownloadOptimized {
    private static Cipher cipher;

    static {
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public static String decrypt(final SecretKeySpec key, String messageToDecrypt) throws Exception {
        if (cipher == null)
            throw new Exception("Cipher is null!");

        final byte[] decodedCipherText = android.util.Base64.decode(messageToDecrypt, android.util.Base64.DEFAULT);
        byte[] iv = Arrays.copyOfRange(decodedCipherText, 0, 16);
        byte[] data = Arrays.copyOfRange(decodedCipherText, 16, decodedCipherText.length);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return new String(cipher.doFinal(data));
    }

    public static Cipher getCipher(byte[] iv, final SecretKeySpec key) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return cipher;
    }

    public static Cipher getCipherEnc(byte[] iv, final SecretKeySpec key) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        return cipher;
    }
}
