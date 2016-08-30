package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserContactsImport;

public class UserContactsImportResponse extends MessageHandler {

    public int actionId;
    public Object message;

    public UserContactsImportResponse(int actionId, Object protoClass) {
        super(actionId, protoClass);

        this.message = protoClass;
        this.actionId = actionId;
    }


    @Override
    public void handler() {


        ProtoUserContactsImport.UserContactsImportResponse.Builder userContactResponse = (ProtoUserContactsImport.UserContactsImportResponse.Builder) message;

        G.onContactImport.onContactImport();


        Log.i("XXX", "handler");


    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorReponse = (ProtoError.ErrorResponse.Builder) message;
        errorReponse.getMajorCode();
        errorReponse.getMinorCode();


        Log.i("XXX", "UserContactsImportResponse errorReponse.getMajorCode() : " + errorReponse.getMajorCode());
        Log.i("XXX", "UserContactsImportResponse errorReponse.getMinorCode() : " + errorReponse.getMinorCode());
    }
}


