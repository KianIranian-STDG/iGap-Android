package com.iGap.response;

import com.iGap.proto.ProtoUserProfileGetSelfRemove;
import com.iGap.realm.RealmUserInfo;
import io.realm.Realm;

public class UserProfileGetSelfRemoveResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileGetSelfRemoveResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoUserProfileGetSelfRemove.UserProfileGetSelfRemoveResponse.Builder builder = (ProtoUserProfileGetSelfRemove.UserProfileGetSelfRemoveResponse.Builder) message;

        builder.getSelfRemove();

        Realm realm1 = Realm.getDefaultInstance();
        realm1.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                realm.where(RealmUserInfo.class).findFirst().setSelfRemove(builder.getSelfRemove());
            }
        });

        realm1.close();



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


