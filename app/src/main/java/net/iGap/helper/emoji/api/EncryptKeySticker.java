package net.iGap.helper.emoji.api;

import android.util.Base64;
import android.util.Log;

import net.iGap.G;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class EncryptKeySticker {
    public static String PUBLIC_KEY = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKfzsTIcKJH7P2iHTUIsEIcpTMEzivy1wazIaLif5Limym+0J/6hwb/JNR1K+9kEqWIEX41j25MSyM4nVH2+NucCAwEAAQ==";

   public static String enccriptData() {
        String encoded = "";
        byte[] encrypted = null;
        try {
            byte[] publicBytes = Base64.decode(PUBLIC_KEY, Base64.DEFAULT);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING"); //or try with "RSA"
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            encrypted = cipher.doFinal(String.valueOf(G.userId).getBytes());
            encoded = Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
       return encoded;
    }
}
