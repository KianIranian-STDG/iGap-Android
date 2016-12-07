package com.iGap.realm;

import com.iGap.module.enums.AttachmentFor;
import com.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class RealmAvatar extends RealmObject {

    @PrimaryKey
    private long id;
    private long ownerId;
    private RealmAttachment file;

    public RealmAvatar() {
    }

    public RealmAvatar(long id) {
        this.id = id;
    }

    public static RealmAvatar put(long ownerId, ProtoGlobal.Avatar input) {
        if (!input.hasFile()) {
            return null;
        }

        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmAvatar> ownerAvatars = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, ownerId).findAll();

        boolean exists = false;
        for (RealmAvatar avatar : ownerAvatars) {
            if (avatar.getFile() != null && avatar.getFile().getToken().equalsIgnoreCase(input.getFile().getToken())) {
                exists = true;
                break;
            }
        }

        RealmAvatar avatar;
        if (!exists) {
            avatar = realm.createObject(RealmAvatar.class, input.getId());
            avatar.setOwnerId(ownerId);
            avatar.setFile(RealmAttachment.build(input.getFile(), AttachmentFor.AVATAR, null));
        } else {
            avatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, input.getId()).findFirst();
        }
        realm.close();
        return avatar;
    }

    /**
     * use this method just for group or channel , because chat don't have avatar in room(chat
     * avatar exist in channel info)
     */


    public static RealmAvatar convert(final ProtoGlobal.Room room) {
        ProtoGlobal.Avatar avatar = null;
        switch (room.getType()) {
            case GROUP:
                ProtoGlobal.GroupRoom groupRoom = room.getGroupRoomExtra();
                avatar = groupRoom.getAvatar();
                break;
            case CHANNEL:
                ProtoGlobal.ChannelRoom channelRoom = room.getChannelRoomExtra();
                avatar = channelRoom.getAvatar();
                break;
        }

        return RealmAvatar.put(room.getId(), avatar);
    }

    public static RealmAvatar convert(long userId, final RealmAttachment attachment) {
        Realm realm = Realm.getDefaultInstance();

        // don't put it into transaction
        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).findFirst();
        if (realmAvatar == null) {
            realmAvatar = realm.createObject(RealmAvatar.class, attachment.getId());
            realmAvatar.setOwnerId(userId);
        }
        realmAvatar.setFile(attachment);

        realm.close();

        return realmAvatar;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RealmAttachment getFile() {
        return file;
    }

    public void setFile(RealmAttachment file) {
        this.file = file;
    }
}
