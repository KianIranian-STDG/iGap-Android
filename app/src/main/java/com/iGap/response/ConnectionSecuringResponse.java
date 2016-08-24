package com.iGap.response;

import android.util.Log;

import com.google.protobuf.ByteString;
import com.iGap.AESCrypt;
import com.iGap.G;
import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoConnectionSecuring;
import com.iGap.request.RequestQueue;
import com.iGap.request.RequestWrapper;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

public class ConnectionSecuringResponse extends MessageHandler {

    public int actionId;
    public Object message;

    public ConnectionSecuringResponse(int actionId, Object protoClass) {
        super(actionId, protoClass);

        this.message = protoClass;
        this.actionId = actionId;

    }


    @Override
    public void handler() {

        ProtoConnectionSecuring.ConnectionSecuringResponse.Builder builder = (ProtoConnectionSecuring.ConnectionSecuringResponse.Builder) message;

        String publicKey = builder.getPublicKey();
        int symmetricKeyLength = builder.getSymmetricKeyLength();

        String key = HelperString.generateKey(symmetricKeyLength);
        G.symmetricKey = HelperString.generateSymmetricKey(key);

        Log.i("SOC", "ConnectionSecuringResponse 1");

        byte[] encryption = null;
        try {
            RSAPublicKey rsaPublicKey = (RSAPublicKey) HelperString.getPublicKeyFromPemFormat(publicKey);
            Log.i("SOC", "ConnectionSecuringResponse 2");
            PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent()));
            Log.i("SOC", "ConnectionSecuringResponse 3");
            encryption = AESCrypt.encryptSymmetricKey(pubKey, G.symmetricKey.getEncoded());

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            Log.i("SOC", "ConnectionSecuringResponse Error : " + e);
            e.printStackTrace();
        }

        Log.i("SOC", "ConnectionSecuringResponse 4");
        ProtoConnectionSecuring.ConnectionSymmetricKey.Builder connectionSymmetricKey = ProtoConnectionSecuring.ConnectionSymmetricKey.newBuilder();
        Log.i("SOC", "ConnectionSecuringResponse 5");
        connectionSymmetricKey.setSymmetricKey(ByteString.copyFrom(encryption));
        Log.i("SOC", "ConnectionSecuringResponse 6");
        RequestWrapper requestWrapper = new RequestWrapper(2, connectionSymmetricKey);
        Log.i("SOC", "ConnectionSecuringResponse 6");
        try {
            RequestQueue.sendRequest(requestWrapper);
            Log.i("SOC", "ConnectionSecuringResponse 7");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void error() {

    }


}

