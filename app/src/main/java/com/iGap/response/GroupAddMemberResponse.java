package com.iGap.response;

import android.util.Log;
import com.iGap.G;
import com.iGap.proto.ProtoError;

public class GroupAddMemberResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupAddMemberResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {

        G.onGroupAddMember.onGroupAddMember();

        Log.i("XXX", "GroupAddMemberResponse handler : " + message);
    }

    @Override public void error() {

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        Log.i("XXX", "GroupAddMemberResponse majorCode : " + majorCode);
        Log.i("XXX", "GroupAddMemberResponse minorCode : " + minorCode);

        G.onGroupAddMember.onError(majorCode, minorCode);
    }
}
