package com.iGap.response;

import com.iGap.proto.ProtoUserContactsGetBlockedList;

public class UserContactsGetBlockedListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserContactsGetBlockedListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserContactsGetBlockedList.UserContactsGetBlockedListResponse.Builder builder = (ProtoUserContactsGetBlockedList.UserContactsGetBlockedListResponse.Builder) message;
        builder.getUserList();
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


