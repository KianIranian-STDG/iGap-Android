package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoUserSessionGetActiveList;

public class UserSessionGetActiveListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserSessionGetActiveListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        ProtoUserSessionGetActiveList.UserSessionGetActiveListResponse.Builder builder = (ProtoUserSessionGetActiveList.UserSessionGetActiveListResponse.Builder) message;
        G.onUserSessionGetActiveList.onUserSessionGetActiveList(builder.getSessionList());
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
