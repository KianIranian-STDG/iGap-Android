package com.iGap.helper;

import com.iGap.G;
import com.iGap.interfaces.OnAvatarAdd;
import com.iGap.interfaces.OnAvatarDelete;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.module.AndroidUtils;
import com.iGap.module.enums.AttachmentFor;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;

import java.io.File;
import java.io.IOException;

import io.realm.Realm;
import io.realm.Sort;

/**
 * helper avatar for add or delete avatars for user or room
 */
public class HelperAvatar {

    public enum AvatarType {
        USER, ROOM
    }

    /**
     * add avatar in RealmAvatar and after copy avatar
     * to final path call callback for return final path
     *
     * @param ownerId user id for users and roomId for rooms
     */

    public static void avatarAdd(final long ownerId, final String src, final ProtoGlobal.Avatar avatar, final OnAvatarAdd onAvatarAdd) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                String avatarPath = copyAvatar(src, avatar);

                RealmAvatar realmAvatar = realm.createObject(RealmAvatar.class, avatar.getId());
                realmAvatar.setOwnerId(ownerId);
                realmAvatar.setFile(RealmAttachment.build(avatar.getFile(), AttachmentFor.AVATAR));

                if (onAvatarAdd != null && avatarPath != null) {
                    onAvatarAdd.onAvatarAdd(avatarPath);
                }
            }
        });
        realm.close();
    }

    private static String copyAvatar(String src, ProtoGlobal.Avatar avatar) {
        try {
            /*
             * G.DIR_IMAGE_USER use for all avatars , user or room
             */
            String avatarPath = G.DIR_IMAGE_USER + "/" + avatar.getFile().getToken() + "_" + avatar.getFile().getName();
            AndroidUtils.copyFile(new File(src), new File(avatarPath));

            return avatarPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * read avatarPath from realm avatar and return avatarPath
     *
     * @param ownerId if is user set userId and if is room set roomId
     */

    public static void getAvatar(long ownerId, AvatarType avatarType, OnAvatarGet onAvatarGet) {

        RealmAvatar realmAvatar = getLastAvatar(ownerId);
        if (realmAvatar != null) {
            if (realmAvatar.getFile().isFileExistsOnLocal()) {
                onAvatarGet.onAvatarGet(realmAvatar.getFile().getLocalFilePath());
            } else if (realmAvatar.getFile().isThumbnailExistsOnLocal()) {
                onAvatarGet.onAvatarGet(realmAvatar.getFile().getLocalThumbnailPath());
            } else {

                String[] initials = showInitials(ownerId, avatarType);
                if (initials != null) {
                    onAvatarGet.onShowInitials(initials[0], initials[1]);
                }
            }
        }
    }

    private static RealmAvatar getLastAvatar(long ownerId) {
        Realm realm = Realm.getDefaultInstance();
        for (RealmAvatar avatar : realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, ownerId).findAllSorted(RealmAvatarFields.ID, Sort.ASCENDING)) {
            if (avatar.getFile() != null) {
                return avatar;
            }
        }
        realm.close();
        return null;
    }

    /**
     * read from user and room db in local for find initials and color
     *
     * @param ownerId if is user set userId and if is room set roomId
     * @return initials[0] , color[1]
     */

    public static String[] showInitials(long ownerId, AvatarType avatarType) {
        Realm realm = Realm.getDefaultInstance();
        String initials = null;
        String color = null;
        if (avatarType == AvatarType.USER) {

            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, ownerId).findFirst();
            if (realmRegisteredInfo != null) {
                initials = realmRegisteredInfo.getInitials();
                color = realmRegisteredInfo.getColor();
            }

        } else if (avatarType == AvatarType.ROOM) {

            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, ownerId).findFirst();
            if (realmRoom != null) {
                initials = realmRoom.getInitials();
                color = realmRoom.getColor();
            }
        }
        realm.close();

        if (initials != null && color != null) {
            return new String[]{initials, color};
        }

        return null;
    }

    /**
     * delete avatar and if another avatar is exist for this user
     * call latestAvatarPath latest avatar and if isn't exist call showInitials
     *
     * @param ownerId    if is user set userId and if is room set roomId
     * @param avatarType set USER for user and ROOM for chat or group or channel
     * @param avatarId   id avatar for delete from RealmAvatar
     */

    public static void avatarDelete(final long ownerId, final long avatarId, final AvatarType avatarType, final OnAvatarDelete onAvatarDelete) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, avatarId).findFirst();
                if (realmAvatar != null) {
                    realmAvatar.deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

                getAvatar(ownerId, avatarType, new OnAvatarGet() {
                    @Override
                    public void onAvatarGet(String avatarPath) {
                        onAvatarDelete.latestAvatarPath(avatarPath);
                    }

                    @Override
                    public void onShowInitials(String initials, String color) {
                        onAvatarDelete.showInitials(initials, color);
                    }
                });

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                String[] initials = showInitials(ownerId, avatarType);
                if (initials != null) {
                    onAvatarDelete.showInitials(initials[0], initials[1]);
                }
            }
        });

        realm.close();
    }
}
