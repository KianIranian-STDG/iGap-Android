package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserContactsEdit;

public class UserContactsEditResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserContactsEditResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserContactsEdit.UserContactsEditResponse.Builder builder =
                (ProtoUserContactsEdit.UserContactsEditResponse.Builder) message;

        long phone = builder.getPhone();
        String first_name = builder.getFirstName();
        String last_name = builder.getLastName();
        G.onUserContactEdit.onContactEdit(first_name, last_name);
    }

    @Override
    public void timeOut() {
        super.timeOut();
        G.onUserContactEdit.onContactEditTimeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int MajorCode = errorResponse.getMajorCode();
        int MinorCode = errorResponse.getMinorCode();
        G.onUserContactEdit.onContactEditError(MajorCode, MinorCode);
    }
}


