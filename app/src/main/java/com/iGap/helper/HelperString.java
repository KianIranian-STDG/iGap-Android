package com.iGap.helper;

import android.util.Base64;

import com.iGap.G;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.spec.SecretKeySpec;

/**
 * HelperString
 */
public class HelperString {

    /**
     * generate random id contain 0-9 , a-z , A-Z
     * <p>
     * return string with 10 character
     */

    public static String generateKey() {
        return generate(10);
    }

    public static String generateKey(int length) {
        return generate(length);
    }

    private static String generate(int length) {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) { // random string length is 10 now
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        return sb.toString();
    }

    /**
     * create random string and convert currentTimeMillis to date
     *
     * @param length length for random string
     * @return concatenated random string and converted date
     */

    public static String getRandomFileName(int length) {
        char[] chars = "123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) { // random string length is 10 now
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        return HelperCalander.milladyDate(System.currentTimeMillis()) + "_" + sb.toString();
    }

    public static SecretKeySpec generateSymmetricKey(String key) {
        return new SecretKeySpec(key.getBytes(), "AES");
    }

    /**
     * convert string publicKey to PublicKey format
     *
     * @return RSAPublicKey
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */

    public static PublicKey getPublicKeyFromPemFormat(String PEMString)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        BufferedReader pemReader = null;
        pemReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(PEMString.getBytes("UTF-8"))));
        StringBuffer content = new StringBuffer();
        String line = null;
        while ((line = pemReader.readLine()) != null) {
            if (line.indexOf("-----BEGIN PUBLIC KEY-----") != -1) {
                while ((line = pemReader.readLine()) != null) {
                    if (line.indexOf("-----END PUBLIC KEY") != -1) {
                        break;
                    }
                    content.append(line.trim());
                }
                break;
            }
        }
        if (line == null) {
            throw new IOException("PUBLIC KEY" + " not found");
        }
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(
                new X509EncodedKeySpec(Base64.decode(content.toString(), Base64.DEFAULT)));
        return key;
    }

    /**
     * search in lookupMap and get key from value after replace "." with ""
     *
     * @param className current class name
     * @return id
     */

    public static int getActionId(String className) {

        Iterator keys = G.lookupMap.keySet().iterator();
        while (keys.hasNext()) {

            int id = (int) keys.next();
            String lookupMapValue = G.lookupMap.get(id);
            lookupMapValue = "Request" + lookupMapValue.replace(".", "");

            if (lookupMapValue.equals(className)) {
                return id;
            }
        }
        return -1;
    }

    /**
     * regex for detect number from text
     *
     * @param text  input text that contain number
     * @param regex regex pattern for detection
     */

    public static String regexExtractValue(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(text);
        String code = "";
        while (m.find()) {
            code = m.group();
        }
        return code;
    }
}
