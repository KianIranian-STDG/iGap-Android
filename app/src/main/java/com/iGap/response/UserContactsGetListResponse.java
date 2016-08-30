package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserContactsGetList;

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

        final ProtoUserContactsGetList.UserContactsGetListResponse.Builder builder = (ProtoUserContactsGetList.UserContactsGetListResponse.Builder) message;
//        G.realm = Realm.getInstance(G.realmConfig);
//        G.realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                for (ProtoGlobal.RegisteredUser registerUser : builder.getRegisteredUserList()) {
//
//                    RealmUserContactsGetListResponse listResponse = G.realm.createObject(RealmUserContactsGetListResponse.class);
//                    listResponse.setId(registerUser.getId());
//                    listResponse.setPhone(registerUser.getPhone());
//                    listResponse.setFirst_name(registerUser.getFirstName());
//                    listResponse.setLast_name(registerUser.getLastName());
//                    listResponse.setDisplay_name(registerUser.getDisplayName());
//                    listResponse.setInitials(registerUser.getInitials());
//                    listResponse.setColor(registerUser.getColor());
////                    listResponse.setStatus(registerUser.getStatus());
//                    listResponse.setLast_seen(registerUser.getLastSeen());
//                }
//            }
//        });

        Log.i("XXX", "UserContactsGetListResponse handler 2");
        G.onUserContactGetLis.onContactGetList();

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


