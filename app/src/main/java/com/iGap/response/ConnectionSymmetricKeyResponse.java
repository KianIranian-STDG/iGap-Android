package com.iGap.response;

import com.iGap.Config;
import com.iGap.G;
import com.iGap.WebSocketClient;
import com.iGap.helper.HelperConnectionState;
import com.iGap.proto.ProtoConnectionSecuring;
import com.neovisionaries.ws.client.WebSocket;

public class ConnectionSymmetricKeyResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ConnectionSymmetricKeyResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoConnectionSecuring.ConnectionSymmetricKeyResponse.Builder builder = (ProtoConnectionSecuring.ConnectionSymmetricKeyResponse.Builder) message;
        ProtoConnectionSecuring.ConnectionSymmetricKeyResponse.Status status = builder.getStatus();
        int statusNumber = status.getNumber();

        if (statusNumber == Config.REJECT) {

            G.allowForConnect = false;
            WebSocketClient.getInstance().disconnect();
            //TODO [Saeed Mozaffari] [2016-09-06 12:30 PM] - go to upgrade page

        } else if (statusNumber == Config.ACCEPT) {
            HelperConnectionState.connectionState(Config.ConnectionState.IGAP);

            /**
             * when secure is false set useMask true otherwise set false
             */
            G.isSecure = true;
            WebSocket.useMask = false;

            G.ivSize = builder.getSymmetricIvSize();
            String sm = builder.getSymmetricMethod();
            G.symmetricMethod = sm.split("-")[2];
            if (G.onSecuring != null) {
                G.onSecuring.onSecure();
            }
        }
    }

    @Override
    public void timeOut() {
        // disconnect socket for do securing action again
        WebSocketClient.getInstance().disconnect();
        super.timeOut();
    }

    @Override
    public void error() {
        // disconnect socket for do securing action again
        WebSocketClient.getInstance().disconnect();
        super.error();
    }
}