package com.iGap.response;

import com.iGap.module.enums.AttachmentFor;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoUserAvatarGetList;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;

import io.realm.Realm;
import io.realm.RealmList;

public class UserAvatarGetListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserAvatarGetListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();

        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
                .equalTo(RealmRegisteredInfoFields.ID, Long.parseLong(identity))
                .findFirst();
        final RealmList<RealmAvatar> realmAvatars = realmRegisteredInfo.getAvatars();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ProtoUserAvatarGetList.UserAvatarGetListResponse.Builder userAvatarGetListResponse =
                        (ProtoUserAvatarGetList.UserAvatarGetListResponse.Builder) message;
                for (ProtoGlobal.Avatar avatar : userAvatarGetListResponse.getAvatarList()) {

                    RealmAvatar realmAvatar = RealmAvatar.convert(Long.parseLong(identity),
                            RealmAttachment.build(avatar.getFile(), AttachmentFor.AVATAR));

                    if (!realmAvatars.contains(realmAvatar)) {
                        realmAvatars.add(realmAvatar);

                    }

                }
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


