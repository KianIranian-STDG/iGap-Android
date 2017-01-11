package com.iGap.response;

import android.util.Log;
import com.iGap.AESCrypt;
import com.iGap.G;
import com.iGap.helper.HelperNumerical;
import com.iGap.helper.HelperUnpackMessage;
import java.security.GeneralSecurityException;

public class HandleResponse extends Thread {

    byte[] binary;

    public HandleResponse(byte[] binary) {
        this.binary = binary;
    }

    @Override
    public void run() {
        super.run();
        if (G.isSecure) {
            Log.i("TTT", "G.ivSize : " + G.ivSize);
            Log.i("TTT", "G.symmetricMethod : " + G.symmetricMethod);
            Log.i("TTT", "G.symmetricKey : " + G.symmetricKey);
            Log.i("TTT", "G.symmetricKey.getAlgorithm() : " + G.symmetricKey.getAlgorithm());
            Log.i("TTT", "G.symmetricKey.getFormat() : " + G.symmetricKey.getFormat());
            Log.i("TTT", "G.symmetricKey.getEncoded() : " + G.symmetricKey.getEncoded().toString());
            byte[] iv = HelperNumerical.getIv(binary, G.ivSize);
            byte[] binaryDecode = HelperNumerical.getMessage(binary);

            try {
                binaryDecode = AESCrypt.decrypt(G.symmetricKey, iv, binaryDecode);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            HelperUnpackMessage.unpack(binaryDecode);
        } else {
            HelperUnpackMessage.unpack(binary);
        }
    }
}
