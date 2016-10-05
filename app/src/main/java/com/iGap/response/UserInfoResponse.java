package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserInfo;
import com.iGap.realm.RealmRegisteredInfo;

import io.realm.Realm;

public class UserInfoResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserInfoResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        final ProtoUserInfo.UserInfoResponse.Builder builder = (ProtoUserInfo.UserInfoResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo("id", builder.getUser().getId()).findFirst();

                if (realmRegisteredInfo == null) {
                    realmRegisteredInfo = realm.createObject(RealmRegisteredInfo.class);
                    realmRegisteredInfo.setId(builder.getUser().getId());
                }

                realmRegisteredInfo.setAvatar(builder.getUser().getAvatar());
                realmRegisteredInfo.setAvatarCount(builder.getUser().getAvatarCount());
                realmRegisteredInfo.setColor(builder.getUser().getColor());
                realmRegisteredInfo.setDisplayName(builder.getUser().getDisplayName());
                realmRegisteredInfo.setFirstName(builder.getUser().getFirstName());
                realmRegisteredInfo.setInitials(builder.getUser().getInitials());
                realmRegisteredInfo.setLastSeen(builder.getUser().getLastSeen());
                realmRegisteredInfo.setPhone(builder.getUser().getPhone());
                realmRegisteredInfo.setStatus(builder.getUser().getStatus().toString());
                realmRegisteredInfo.setUsername(builder.getUser().getUsername());
            }
        });
        realm.close();

        G.onUserInfoResponse.onUserInfo(builder.getUser(), builder.getResponse());
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "UserInfoResponse timeout");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "UserInfoResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "UserInfoResponse response.minorCode() : " + minorCode);
    }
}


