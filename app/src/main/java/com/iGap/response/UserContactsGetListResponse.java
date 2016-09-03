package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoUserContactsGetList;
import com.iGap.realm.RealmContacts;

import io.realm.Realm;

public class UserContactsGetListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserContactsGetListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        Log.i("XXX", "UserContactsGetListResponse handler message : " + message);
        final ProtoUserContactsGetList.UserContactsGetListResponse.Builder builder = (ProtoUserContactsGetList.UserContactsGetListResponse.Builder) message;
        builder.toString().length();
        G.realm = Realm.getInstance(G.realmConfig);
        G.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                G.realm.delete(RealmContacts.class);

                for (ProtoGlobal.RegisteredUser registerUser : builder.getRegisteredUserList()) {
                    Log.i("XXX", "UserContactsGetListResponse handler registerUser : " + registerUser);
                    RealmContacts listResponse = G.realm.createObject(RealmContacts.class);
                    listResponse.setId(registerUser.getId());
                    listResponse.setPhone(registerUser.getPhone());
                    listResponse.setFirst_name(registerUser.getFirstName());
                    listResponse.setLast_name(registerUser.getLastName());
                    listResponse.setDisplay_name(registerUser.getDisplayName());
                    listResponse.setInitials(registerUser.getInitials());
                    listResponse.setColor(registerUser.getColor());
                    listResponse.setStatus(registerUser.getStatus().toString());
                    listResponse.setLast_seen(registerUser.getLastSeen());
                }
            }
        });

        G.onUserContactGetList.onContactGetList();

        G.realm.close();
    }

    @Override
    public void timeOut() {
        Log.i("XXX", "UserContactsGetListResponse timeOut");
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


