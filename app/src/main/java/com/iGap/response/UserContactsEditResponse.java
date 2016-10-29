package com.iGap.response;

import android.util.Log;
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
        Log.i("XXX", "UserContactsEditResponse handler");
        ProtoUserContactsEdit.UserContactsEditResponse.Builder builder =
            (ProtoUserContactsEdit.UserContactsEditResponse.Builder) message;

        long phone = builder.getPhone();
        String first_name = builder.getFirstName();
        String last_name = builder.getLastName();
        Log.i("XXX", "first_name handler" + first_name);
        Log.i("XXX", "last_name handler" + last_name);
        G.onUserContactEdit.onContactEdit(first_name, last_name);

    }

    @Override
    public void timeOut() {
        Log.i("XXX", "UserContactsEditResponse timeOut");
        G.onUserContactEdit.onContactEditTimeOut();
    }

    @Override
    public void error() {

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        errorResponse.getMajorCode();
        errorResponse.getMinorCode();
        G.onUserContactEdit.onContactEditError();

        Log.i("XXX", "UserContactsEditResponse errorReponse.getMajorCode() : " + errorResponse.getMajorCode());
        Log.i("XXX", "UserContactsEditResponse errorReponse.getMinorCode() : " + errorResponse.getMinorCode());
    }
}


