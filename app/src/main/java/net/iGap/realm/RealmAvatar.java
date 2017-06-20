/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.realm;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import net.iGap.module.SUID;
import net.iGap.module.enums.AttachmentFor;
import net.iGap.proto.ProtoGlobal;

public class RealmAvatar extends RealmObject {

    @PrimaryKey private long id;
    private long uid; // id for sorting avatars
    @Index private long ownerId; // userId for users and roomId for rooms
    private RealmAttachment file;

    public RealmAvatar() {
    }

    public RealmAvatar(long id) {
        this.id = id;
    }

    /**
     * if file is repetitious send it to bottom for detect it later
     * for main avatar
     *
     * @param sendAvatarToBottom if need send avatar to bottom of avatars for that user
     */

    public static RealmAvatar put(long ownerId, ProtoGlobal.Avatar input, boolean sendAvatarToBottom) {
        Realm realm = Realm.getDefaultInstance();
        if (!input.hasFile()) {
            deleteAllAvatars(ownerId, realm);

            realm.close();

            return null;
        }

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
            RealmResults<RealmAvatar> avatars = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, input.getId()).findAll();
            if (avatars.size() == 0) {
                avatar = realm.createObject(RealmAvatar.class, input.getId());
                avatar.setOwnerId(ownerId);
                avatar.setFile(RealmAttachment.build(input.getFile(), AttachmentFor.AVATAR, null));
                avatar.setUid(SUID.id().get());
            } else {
                avatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, input.getId()).findFirst();
                if (sendAvatarToBottom) {
                    updateAvatarUid(input.getId());
                }
            }
        } else {
            avatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, input.getId()).findFirst();
            if (sendAvatarToBottom) {
                updateAvatarUid(input.getId());
            }
        }
        realm.close();
        return avatar;
    }

    /**
     * update uid for avatar for send it to bottom
     * hint : i need do this action because client read avatars from RealmAvatar and sort descending
     * avatars for get latest avatar
     */
    //TODO [Saeed Mozaffari] [2017-05-03 1:25 PM] - now avatar sorted with avatarId so client don't need to work with uid and update it
    private static void updateAvatarUid(final long avatarId) {
        Realm realm = Realm.getDefaultInstance();
        RealmAvatar avatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, avatarId).findFirst();
        if (avatar != null) {
            avatar.setUid(SUID.id().get());
        }

        realm.close();
    }


    /**
     * delete all avatars from RealmAvatar
     *
     * @param ownerId use this id for delete from RealmAvatar
     */
    private static void deleteAllAvatars(final long ownerId, Realm realm) {
        RealmResults<RealmAvatar> ownerAvatars = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, ownerId).findAll();
        if (ownerAvatars.size() > 0) {
            ownerAvatars.deleteAllFromRealm();
        }
    }

    public static RealmAvatar convert(long userId, final RealmAttachment attachment) {
        Realm realm = Realm.getDefaultInstance();

        // don't put it into transaction
        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).findFirst();
        if (realmAvatar == null) {
            realmAvatar = realm.createObject(RealmAvatar.class, attachment.getId());
            realmAvatar.setOwnerId(userId);
            realmAvatar.setUid(SUID.id().get());
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

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public RealmAttachment getFile() {
        return file;
    }

    public void setFile(RealmAttachment file) {
        this.file = file;
    }
}
