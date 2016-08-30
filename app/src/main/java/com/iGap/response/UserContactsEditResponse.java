package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserContactsEdit;

import io.realm.Realm;

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
        ProtoUserContactsEdit.UserContactsEdit.Builder builder = (ProtoUserContactsEdit.UserContactsEdit.Builder) message;

        long phone = builder.getPhone();
        String first_name = builder.getFirstName();
        String last_name = builder.getLastName();

        G.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

            }
        });


    }

    @Override
    public void timeOut() {
        Log.i("XXX", "UserContactsEditResponse timeOut");

    }

    @Override
    public void error() {

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        errorResponse.getMajorCode();
        errorResponse.getMinorCode();

        Log.i("XXX", "UserContactsEditResponse errorReponse.getMajorCode() : " + errorResponse.getMajorCode());
        Log.i("XXX", "UserContactsEditResponse errorReponse.getMinorCode() : " + errorResponse.getMinorCode());
    }
}


