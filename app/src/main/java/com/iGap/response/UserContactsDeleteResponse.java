package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserContactsDelete;
import com.iGap.realm.RealmUserContactsGetListResponse;

import io.realm.Realm;

public class UserContactsDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;

    public UserContactsDeleteResponse(int actionId, Object protoClass) {
        super(actionId, protoClass);

        this.message = protoClass;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        Log.i("XXX", "UserContactsDeleteResponse handler 3");
        ProtoUserContactsDelete.UserContactsDeleteResponse.Builder builder = (ProtoUserContactsDelete.UserContactsDeleteResponse.Builder) message;
        long phone = builder.getPhone();
        G.realm = Realm.getInstance(G.realmConfig);
        RealmUserContactsGetListResponse realmUserContactsGetListResponse = G.realm
                .where(RealmUserContactsGetListResponse.class)
                .equalTo("phone", phone)
                .findFirst();
        realmUserContactsGetListResponse.deleteFromRealm();

        Log.i("XXX", "UserContactsDeleteResponse handler 3.1");
        G.onUserContactdelete.onContactDelete();
    }

    @Override
    public void timeOut() {

        Log.i("XXX", "UserContactsDeleteResponse timeOut");
    }

    @Override
    public void error() {

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        errorResponse.getMajorCode();
        errorResponse.getMinorCode();

        Log.i("XXX", "UserContactsGetListResponse errorReponse.getMajorCode() : " + errorResponse.getMajorCode());
        Log.i("XXX", "UserContactsGetListResponse errorReponse.getMinorCode() : " + errorResponse.getMinorCode());

    }


}


