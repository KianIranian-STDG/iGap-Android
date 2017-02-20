package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserInfo;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmUserInfo;
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
        super.handler();
        final ProtoUserInfo.UserInfoResponse.Builder builder = (ProtoUserInfo.UserInfoResponse.Builder) message;

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, builder.getUser().getId()).findFirst();
                        if (realmRegisteredInfo == null) {
                            realmRegisteredInfo = realm.createObject(RealmRegisteredInfo.class, builder.getUser().getId());
                        }

                        realmRegisteredInfo.setAvatarCount(builder.getUser().getAvatarCount());
                        realmRegisteredInfo.setColor(builder.getUser().getColor());
                        realmRegisteredInfo.setDisplayName(builder.getUser().getDisplayName());
                        realmRegisteredInfo.setFirstName(builder.getUser().getFirstName());
                        realmRegisteredInfo.setInitials(builder.getUser().getInitials());
                        realmRegisteredInfo.setLastSeen(builder.getUser().getLastSeen());
                        realmRegisteredInfo.setPhoneNumber(Long.toString(builder.getUser().getPhone()));
                        realmRegisteredInfo.setStatus(builder.getUser().getStatus().toString());
                        realmRegisteredInfo.setUsername(builder.getUser().getUsername());
                        realmRegisteredInfo.setMutual(builder.getUser().getMutual());
                        realmRegisteredInfo.setCacheId(builder.getUser().getCacheId());

                        RealmAvatar.put(builder.getUser().getId(), builder.getUser().getAvatar(), true);

                        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                        if (realmUserInfo != null && (builder.getUser().getId() == realmUserInfo.getUserId())) {
                            if (G.onUserInfoMyClient != null) {
                                G.onUserInfoMyClient.onUserInfoMyClient(builder.getUser(), identity);
                            }
                        }

                        if (G.onUserUpdateStatus != null) {
                            G.onUserUpdateStatus.onUserUpdateStatus(builder.getUser().getId(), builder.getUser().getLastSeen(), builder.getUser().getStatus().toString());
                        }

                        if (G.onUserInfoResponse != null) {
                            G.onUserInfoResponse.onUserInfo(builder.getUser(), identity);
                        }

                        if (G.onUserInfoForAvatar != null) {
                            G.onUserInfoForAvatar.onUserInfoForAvatar(builder.getUser());
                        }
                    }
                });
            }
        });
    }

    @Override
    public void timeOut() {
        super.timeOut();
        G.onUserInfoResponse.onUserInfoTimeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        G.onUserInfoResponse.onUserInfoError(majorCode, minorCode);
    }
}


