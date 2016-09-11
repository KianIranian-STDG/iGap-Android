package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatDelete;
import com.iGap.proto.ProtoError;

public class ChatDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }


    @Override
    public void handler() {
        Log.i("RRR", "ChatDeleteResponse delete 1");
        ProtoChatDelete.ChatDeleteResponse.Builder builder = (ProtoChatDelete.ChatDeleteResponse.Builder) message;
        Log.i("RRR", "ChatDeleteResponse delete 2");
        G.onChatDelete.onChatDelete(builder.getRoomId());
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder builder = (ProtoError.ErrorResponse.Builder) message;
        Log.i("RRR", "ChatDeleteResponse error builder.getMajorCode() : " + builder.getMajorCode());
        Log.i("RRR", "ChatDeleteResponse error builder.getMinorCode() : " + builder.getMinorCode());
    }
}


