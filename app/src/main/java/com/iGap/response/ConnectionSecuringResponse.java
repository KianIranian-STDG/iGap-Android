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
    public String identity;

    public ConnectionSecuringResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        Log.i("SOC_RECONNECT", "ConnectionSecuringResponse handler : " + message);
        ProtoConnectionSecuring.ConnectionSecuringResponse.Builder builder =
                (ProtoConnectionSecuring.ConnectionSecuringResponse.Builder) message;

        String publicKey = builder.getPublicKey();
        int symmetricKeyLength = builder.getSymmetricKeyLength();

        Log.i("SOC_RECONNECT", "ConnectionSecuringResponse 1");
        String key = HelperString.generateKey(symmetricKeyLength);
        Log.i("SOC_RECONNECT", "ConnectionSecuringResponse 1 G.symmetricKey : " + G.symmetricKey);
        if (G.symmetricKey != null) {
            return;
        }
        Log.i("SOC_RECONNECT", "ConnectionSecuringResponse 3");

        G.symmetricKey = HelperString.generateSymmetricKey(key);

        byte[] encryption = null;
        try {
            RSAPublicKey rsaPublicKey =
                    (RSAPublicKey) HelperString.getPublicKeyFromPemFormat(publicKey);
            PublicKey pubKey = KeyFactory.getInstance("RSA")
                    .generatePublic(new RSAPublicKeySpec(rsaPublicKey.getModulus(),
                            rsaPublicKey.getPublicExponent()));
            encryption = AESCrypt.encryptSymmetricKey(pubKey, G.symmetricKey.getEncoded());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        Log.i("SOC_RECONNECT", "ConnectionSecuringResponse 2 ");
        ProtoConnectionSecuring.ConnectionSymmetricKey.Builder connectionSymmetricKey =
                ProtoConnectionSecuring.ConnectionSymmetricKey.newBuilder();
        connectionSymmetricKey.setSymmetricKey(ByteString.copyFrom(encryption));
        Log.i("SOC_RECONNECT", "ConnectionSecuringResponse 5");
        RequestWrapper requestWrapper = new RequestWrapper(2, connectionSymmetricKey);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error() {

    }
}

