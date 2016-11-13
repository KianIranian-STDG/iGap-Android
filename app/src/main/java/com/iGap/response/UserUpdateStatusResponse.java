package com.iGap.response;

import android.util.Log;

import com.iGap.G;
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
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, builder.getUserId()).findFirst();
                if (builder.getStatus() == ProtoUserUpdateStatus.UserUpdateStatus.Status.OFFLINE) {
                    realmRegisteredInfo.setStatus("last seen recently");
                } else {
                    realmRegisteredInfo.setStatus("online");
                }


                Log.i("DDD", "UserUpdateStatusResponse : " + builder);
                Log.i("DDD", "realmRegisteredInfo.getDisplayName() : " + realmRegisteredInfo.getDisplayName());

                if (builder.getUserId() == realm.where(RealmUserInfo.class).findFirst().getUserId()) {
                    if (builder.getStatus() == ProtoUserUpdateStatus.UserUpdateStatus.Status.ONLINE) {
                        G.isUserStatusOnline = true;
                    } else {
                        G.isUserStatusOnline = false;
                    }
                }

            }
        });
        realm.close();
        Log.i("DDD", "onUserUpdateStatus : " + G.onUserUpdateStatus);
        if (G.onUserUpdateStatus != null) {
            G.onUserUpdateStatus.onUserUpdateStatus(builder.getUserId(), builder.getStatus());
        }


    }

    @Override
    public void timeOut() {
        super.timeOut();
        Log.i("DDD", "timeOut : " + message);
    }

    @Override
    public void error() {
        super.error();
        Log.i("DDD", "error : " + message);
    }
}


