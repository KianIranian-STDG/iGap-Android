package net.iGap.module.SmsRetriver;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class AppSignatureHashHelper extends ContextWrapper {

    public static final String TAG = AppSignatureHashHelper.class.getSimpleName();
    private static final String HASH_TYPE = "SHA-256";
    public static final int NUM_HASHED_BYTES = 9;
    public static final int NUM_BASE64_CHAR = 11;

    // This code requires one time to get Hash keys do comment and share key    Log.e("TAG", "Apps Hash Key: " + new AppSignatureHashHelper(this).getAppSignatures().get(0));

    public AppSignatureHashHelper(Context context) {
        super(context);
    }

    public ArrayList<String> getAppSignatures() {
        ArrayList<String> appSignaturesHashs = new ArrayList<>();

        try {
            String packageName = getPackageName();
            PackageManager packageManager = getPackageManager();
            Signature[] signatures = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures;

            for (Signature signature : signatures) {
                String hash = hash(packageName, signature.toCharsString());
                if (hash != null) {
                    appSignaturesHashs.add(String.format("%s", hash));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Package not found", e);
        }
        return appSignaturesHashs;
    }

    private static String hash(String packageName, String signature) {
        String appInfo = packageName + " " + signature;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASH_TYPE);
            messageDigest.update(appInfo.getBytes(Charset.forName("UTF-8")));
            byte[] hashSignature = messageDigest.digest();
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES);
            String base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING | Base64.NO_WRAP);
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR);
            return base64Hash;
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "No Such Algorithm Exception", e);
        }
        return null;
    }
}