/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.response;

import io.realm.Realm;
import java.util.ArrayList;
import java.util.List;
import net.iGap.module.SUID;
import net.iGap.module.enums.AttachmentFor;
import net.iGap.proto.ProtoChannelAvatarGetList;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;

public class ChannelAvatarGetListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAvatarGetListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        Realm realm = Realm.getDefaultInstance();
        final long roomId = Long.parseLong(identity);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findAll().deleteAllFromRealm();

                ProtoChannelAvatarGetList.ChannelAvatarGetListResponse.Builder builder = (ProtoChannelAvatarGetList.ChannelAvatarGetListResponse.Builder) message;

                List<ProtoGlobal.Avatar> avatarList = new ArrayList<>();
                ArrayList<Long> checkedList = new ArrayList<>();

                for (int j = 0; j < builder.getAvatarList().size(); j++) {
                    long bigAvatarId = 0;
                    ProtoGlobal.Avatar avatar = null;
                    for (int i = 0; i < builder.getAvatarList().size(); i++) {
                        if (builder.getAvatarList().get(i).getId() > bigAvatarId && !checkedList.contains(builder.getAvatarList().get(i).getId())) {
                            bigAvatarId = builder.getAvatarList().get(i).getId();
                            avatar = builder.getAvatarList().get(i);
                        }
                    }
                    checkedList.add(bigAvatarId);
                    avatarList.add(avatar);
                }

                // add all list to realm avatar
                for (int i = avatarList.size() - 1; i >= 0; i--) {
                    RealmAvatar realmAvatar = realm.createObject(RealmAvatar.class, builder.getAvatarList().get(i).getId());
                    realmAvatar.setOwnerId(roomId);
                    realmAvatar.setUid(SUID.id().get());
                    realmAvatar.setFile(RealmAttachment.build(builder.getAvatarList().get(i).getFile(), AttachmentFor.AVATAR, null));
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


