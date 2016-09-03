package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserContactsDelete;
import com.iGap.realm.RealmContacts;

import io.realm.Realm;

public class UserContactsDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserContactsDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        Log.i("XXX", "UserContactsDeleteResponse handler 3");
        ProtoUserContactsDelete.UserContactsDeleteResponse.Builder builder = (ProtoUserContactsDelete.UserContactsDeleteResponse.Builder) message;
        long phone = builder.getPhone();
        G.realm = Realm.getInstance(G.realmConfig);
        RealmContacts realmUserContactsGetListResponse = G.realm
                .where(RealmContacts.class)
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


