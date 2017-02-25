package com.iGap.response;

import com.iGap.proto.ProtoUserProfileGetGender;
import com.iGap.realm.RealmUserInfo;
import io.realm.Realm;

public class UserProfileGetGenderResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileGetGenderResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoUserProfileGetGender.UserProfileGetGenderResponse.Builder builder =
                (ProtoUserProfileGetGender.UserProfileGetGenderResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                realmUserInfo.setGender(builder.getGender());
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


