package net.iGap.module.mobileBank;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSACipher {

    KeyPairGenerator kpg;
    KeyPair kp;
    PublicKey publicKey;
    PrivateKey privateKey;
    byte[] encryptedBytes, decryptedBytes;
    Cipher cipher, cipher1;
    String encrypted, decrypted;

    private final static String CRYPTO_METHOD = "RSA";
    private final static int CRYPTO_BITS = 2048;

    public RSACipher()
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        generateKeyPair();
    }

    private void generateKeyPair()
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        kpg = KeyPairGenerator.getInstance(CRYPTO_METHOD);
        kpg.initialize(CRYPTO_BITS);
        kp = kpg.genKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();
    }

    /**
     * Encrypt plain text to RSA encrypted and Base64 encoded string
     *
     * @param args args[0] should be plain text that will be encrypted
     *             If args[1] is be, it should be RSA public key to be used as encrypt public key
     * @return a encrypted string that Base64 encoded
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String encrypt(Object... args)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        String plain = (String) args[0];
        PublicKey rsaPublicKey;

        if (args.length == 1) {
            rsaPublicKey = this.publicKey;
        } else {
            rsaPublicKey = (PublicKey) args[1];
        }

        cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        encryptedBytes = cipher.doFinal(plain.getBytes());

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public String decrypt(String result)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        cipher1 = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher1.init(Cipher.DECRYPT_MODE, privateKey);
        decryptedBytes = cipher1.doFinal(Base64.decode(result, Base64.DEFAULT));
        decrypted = new String(decryptedBytes);

        return decrypted;
    }

    public String getPublicKey(String option)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        switch (option) {

            case "pkcs1-pem":
                String pkcs1pem = "-----BEGIN RSA PUBLIC KEY-----\n";
                pkcs1pem += Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
                pkcs1pem += "-----END RSA PUBLIC KEY-----";

                return pkcs1pem;

            case "pkcs8-pem":
                String pkcs8pem = "-----BEGIN PUBLIC KEY-----\n";
                pkcs8pem += Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
                pkcs8pem += "-----END PUBLIC KEY-----";

                return pkcs8pem;

            case "base64":
                return Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);

            default:
                return null;

        }

    }

    public static PublicKey stringToPublicKey(String publicKeyString)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        try {
            if (publicKeyString.contains("-----BEGIN PUBLIC KEY-----") || publicKeyString.contains("-----END PUBLIC KEY-----"))
                publicKeyString = publicKeyString.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
            byte[] keyBytes = Base64.decode(publicKeyString, Base64.DEFAULT);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePublic(spec);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();

            return null;
        }
    }
}