package com.iGap.response;

import com.iGap.G;
import com.iGap.module.AppUtils;
import com.iGap.proto.ProtoUserUpdateStatus;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmUserInfo;
import io.realm.Realm;

public class UserUpdateStatusResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserUpdateStatusResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoUserUpdateStatus.UserUpdateStatusResponse.Builder builder = (ProtoUserUpdateStatus.UserUpdateStatusResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        final RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, builder.getUserId()).findFirst();
        if (realmRegisteredInfo != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    realmRegisteredInfo.setStatus(builder.getStatus().toString());

                    if (builder.getUserId() == realm.where(RealmUserInfo.class).findFirst().getUserId()) {
                        if (builder.getStatus() == ProtoUserUpdateStatus.UserUpdateStatus.Status.ONLINE) {
                            G.isUserStatusOnline = true;
                        } else {
                            G.isUserStatusOnline = false;
                        }
                    }
                }
            });
            if (G.onUserUpdateStatus != null) {
                G.onUserUpdateStatus.onUserUpdateStatus(builder.getUserId(), realmRegisteredInfo.getLastSeen(), AppUtils.setStatsForUser(builder.getStatus().toString()));
            }
        }
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


