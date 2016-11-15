package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoGroupSetAction;

public class GroupSetActionResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupSetActionResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        ProtoGroupSetAction.GroupSetActionResponse.Builder builder = (ProtoGroupSetAction.GroupSetActionResponse.Builder) message;
        Log.i("YYY", "GroupSetActionResponse : " + message);
        if (G.onSetAction != null) {
            G.onSetAction.onSetAction(builder.getRoomId(), builder.getUserId(), builder.getAction());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


