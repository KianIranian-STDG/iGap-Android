package com.iGap.response;

import android.util.Log;

import com.iGap.Config;
import com.iGap.G;
import com.iGap.proto.ProtoConnectionSecuring;

public class ConnectionSymmetricKeyResponse extends MessageHandler {

    public int actionId;
    public Object message;

    public ConnectionSymmetricKeyResponse(int actionId, Object protoClass) {
        super(actionId, protoClass);

        this.message = protoClass;
        this.actionId = actionId;

    }

    @Override
    public void handler() {

        ProtoConnectionSecuring.ConnectionSymmetricKeyResponse.Builder builder = (ProtoConnectionSecuring.ConnectionSymmetricKeyResponse.Builder) message;
        ProtoConnectionSecuring.ConnectionSymmetricKeyResponse.Status status = builder.getStatus();
        int statusNumber = status.getNumber();

        if (statusNumber == Config.REJECT) {

        } else if (statusNumber == Config.ACCEPT) {
            G.isSecure = true;

            G.ivSize = builder.getSymmetricIvSize();
            Log.i("SOC", "ConnectionSymmetricKeyResponse handler ivSize : " + G.ivSize);
            String sm = builder.getSymmetricMethod();
            G.symmetricMethod = sm.split("-")[2];
            Log.i("SOC", "ConnectionSymmetricKeyResponse handler mode : " + G.symmetricMethod);
        }

    }

    @Override
    public void error() {

    }
}