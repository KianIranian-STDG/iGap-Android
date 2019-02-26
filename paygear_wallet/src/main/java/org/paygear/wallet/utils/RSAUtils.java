package org.paygear.wallet.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.paygear.wallet.R;
import org.paygear.wallet.fragment.AccountPaymentDialog;
import org.paygear.wallet.model.Card;
import org.paygear.wallet.model.Payment;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.model.Coupon;
import ir.radsense.raadcore.model.QR;
import ir.radsense.raadcore.utils.RaadCommonUtils;

import static ir.radsense.raadcore.model.QR.QR_TYPE_ACCOUNT;
import static ir.radsense.raadcore.model.QR.QR_TYPE_COUPON;
import static ir.radsense.raadcore.model.QR.QR_TYPE_DIRECT_PAY;

/**
 * Created by Ghaisar on 4/18/2016 AD.
 */
public class RSAUtils {

    public static String encode(String pem, String text) throws NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, UnsupportedEncodingException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException
    {
        pem = pem.replace("-----BEGIN PUBLIC KEY-----\n", "");
        pem = pem.replace("-----END PUBLIC KEY-----", "");
        byte[] decodedPEM = Base64.decode(pem, Base64.DEFAULT);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedPEM);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKey key = (RSAPublicKey) kf.generatePublic(spec);

        //Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        //Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] cipherData = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.encodeToString(cipherData, Base64.NO_WRAP);
        //return String.format("%040x", new BigInteger(1, cipherData)); //Hex String
    }


    public static String getRSA(String pem, String value) {
        try {
            return encode(pem, value);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCardDataRSA(Payment mPayment, Card mCard, String pin2, String cvv2) {
        Map<String, Object> map = new HashMap<>();
        map.put("t", System.currentTimeMillis());

        //String card = mCard != null ? mCard.token : carNumberText.getText().toString();
        map.put("c", mCard.token);
        map.put("bc", mCard.bankCode);
        map.put("type", mCard.type);

        /*if (!TextUtils.isEmpty(mMonth))
            map.put("em", Integer.parseInt(mMonth));
        if (!TextUtils.isEmpty(mYear))
            map.put("ey", Integer.parseInt(mYear));*/

        if (!TextUtils.isEmpty(cvv2))
            map.put("cv", cvv2);

        if (pin2 != null)
            map.put("p2", pin2);

        Gson gson = new Gson();
        String cardInfoJson = gson.toJson(map);

        String publicKey;
        if (mPayment != null)
            publicKey = mPayment.paymentAuth.publicKey;
        else
            publicKey = Auth.getCurrentAuth().getPublicKey();
        return RSAUtils.getRSA(publicKey, cardInfoJson);
    }

}
