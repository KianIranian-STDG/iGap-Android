/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.realm;

import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.AttachmentFor;
import net.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class RealmAvatar extends RealmObject {

    @PrimaryKey
    private long id;
    private long uid; // id for sorting avatars
    @Index
    private long ownerId; // userId for users and roomId for rooms
    private RealmAttachment file;

    public RealmAvatar() {
    }

    public RealmAvatar(long id) {
        this.id = id;
    }

    /**
     * HINT : use this method in transaction. and never use this method in loop for one userId.
     * <p>
     * put avatar to realm and manage need delete any avatar for this ownerId or no
     */
    public static RealmAvatar putOrUpdateAndManageDelete(Realm realm, final long ownerId, final ProtoGlobal.Avatar input) {
        if (!input.hasFile()) {
            deleteAllAvatars(ownerId, realm);
            return null;
        }

        /**
         * bigger than input.getId() exist avatar means that user deleted an avatar which has more priority.
         */
        realm.where(RealmAvatar.class).equalTo("ownerId", ownerId).greaterThan("id", input.getId()).findAll().deleteAllFromRealm();

        RealmAvatar avatar = realm.where(RealmAvatar.class).equalTo("id", input.getId()).findFirst();
        if (avatar == null) {
            avatar = realm.createObject(RealmAvatar.class, input.getId());
            avatar.setOwnerId(ownerId);
            avatar.setFile(RealmAttachment.build(realm, input.getFile(), AttachmentFor.AVATAR, null));
        }
        return avatar;
    }

    public static RealmAvatar putOrUpdate(Realm realm, final long ownerId, final ProtoGlobal.Avatar input) {
        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo("id", input.getId()).findFirst();
        if (realmAvatar == null) {
            realmAvatar = realm.createObject(RealmAvatar.class, input.getId());
        }
        realmAvatar.setOwnerId(ownerId);
        realmAvatar.setFile(RealmAttachment.build(realm, input.getFile(), AttachmentFor.AVATAR, null));

        return realmAvatar;
    }

    /**
     * Hint:use in transaction
     * delete all avatars from RealmAvatar
     *
     * @param ownerId use this id for delete from RealmAvatar
     */
    public static void deleteAllAvatars(final long ownerId, Realm realm) {
        AvatarHandler.clearCacheForOwnerId(ownerId);
        RealmResults<RealmAvatar> ownerAvatars = realm.where(RealmAvatar.class).equalTo("ownerId", ownerId).findAll();
        if (ownerAvatars.size() > 0) {
            ownerAvatars.deleteAllFromRealm();
        }
    }

    public static void deleteAvatar(Realm realm, final long avatarId) {
        AvatarHandler.clearCacheForOwnerId(avatarId);
        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo("id", avatarId).findFirst();
        if (realmAvatar != null) {
            realmAvatar.deleteFromRealm();
        }
    }

    public static void deleteAvatarWithOwnerId(final long ownerId) {
        AvatarHandler.clearCacheForOwnerId(ownerId);
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo("ownerId", ownerId).findFirst();
            if (realmAvatar != null) {
                realmAvatar.deleteFromRealm();
            }
        });
    }


    /**
     * return latest avatar with this ownerId
     *
     * @param ownerId if is user set userId and if is room set roomId
     * @return return latest RealmAvatar for this ownerId
     */
    public static RealmAvatar getLastAvatar(long ownerId, Realm realm) {
        for (RealmAvatar avatar : realm.where(RealmAvatar.class).equalTo("ownerId", ownerId).findAll().sort("id", Sort.DESCENDING)) {
            if (avatar.getFile() != null) {
                return avatar;
            }
        }
        return null;
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
