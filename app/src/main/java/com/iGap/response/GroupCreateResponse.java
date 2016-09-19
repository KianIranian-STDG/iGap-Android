package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoGroupCreate;

public class GroupCreateResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupCreateResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        Log.i("XXX", "GroupCreateResponse handler : " + message);
        ProtoGroupCreate.GroupCreateResponse.Builder builder = (ProtoGroupCreate.GroupCreateResponse.Builder) message;
        G.onGroupCreate.onGroupCreate(builder.getRoomId());
    }

    @Override
    public void error() {
        Log.i("XXX", "GroupCreateResponse error : " + message);
    }
}
