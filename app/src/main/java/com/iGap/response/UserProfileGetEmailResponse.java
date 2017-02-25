package com.iGap.response;

import com.iGap.proto.ProtoUserProfileGetEmail;
import com.iGap.realm.RealmUserInfo;
import io.realm.Realm;

public class UserProfileGetEmailResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileGetEmailResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoUserProfileGetEmail.UserProfileGetEmailResponse.Builder builder = (ProtoUserProfileGetEmail.UserProfileGetEmailResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                realmUserInfo.setEmail(builder.getEmail());
            }
        });
        realm.close();


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


