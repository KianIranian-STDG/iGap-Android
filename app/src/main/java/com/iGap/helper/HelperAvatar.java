package com.iGap.helper;

import android.support.annotation.Nullable;
import com.iGap.G;
import com.iGap.interfaces.OnAvatarAdd;
import com.iGap.interfaces.OnAvatarDelete;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.interfaces.OnDownload;
import com.iGap.interfaces.OnFileDownloaded;
import com.iGap.interfaces.OnUserInfoForAvatar;
import com.iGap.module.AndroidUtils;
import com.iGap.module.SUID;
import com.iGap.module.enums.AttachmentFor;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.request.RequestFileDownload;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import java.io.File;
import java.io.IOException;

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
                RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, avatar.getId()).findFirst();
                if (realmAvatar == null) {
                    realmAvatar = realm.createObject(RealmAvatar.class, avatar.getId());
                }
                realmAvatar.setUid(SUID.id().get());
                realmAvatar.setOwnerId(ownerId);
                realmAvatar.setFile(RealmAttachment.build(avatar.getFile(), AttachmentFor.AVATAR, null));

                if (onAvatarAdd != null && avatarPath != null) {
                    onAvatarAdd.onAvatarAdd(avatarPath);
                }
            }
        });
        realm.close();
    }

    /**
     * get temp address for source and get token and name
     * from avatar for file destination
     *
     * @param src temp address
     * @param avatar avatar that want copy
     * @return return destination path if copy was successfully
     */

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
     * read avatarPath from realm avatar and return latest avatarPath
     *
     * @param ownerId if is user set userId and if is room set roomId
     */

    public static void getAvatar(final long ownerId, AvatarType avatarType, final OnAvatarGet onAvatarGet) {

        final RealmAvatar realmAvatar = getLastAvatar(ownerId);
        if (realmAvatar != null) {
            if (realmAvatar.getFile().isFileExistsOnLocal()) {
                onAvatarGet.onAvatarGet(realmAvatar.getFile().getLocalFilePath(), ownerId);
            } else if (realmAvatar.getFile().isThumbnailExistsOnLocal()) {
                onAvatarGet.onAvatarGet(realmAvatar.getFile().getLocalThumbnailPath(), ownerId);
            } else {

                new AvatarDownload().avatarDownload(realmAvatar.getFile(), ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, new OnDownload() {
                    @Override
                    public void onDownload(final String filepath, final String token) {

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        for (RealmAvatar realmAvatar1 : realm.where(RealmAvatar.class).findAll()) {
                                            if (realmAvatar1.getFile().getToken().equals(token)) {
                                                realmAvatar1.getFile().setLocalThumbnailPath(filepath);
                                                break;
                                            }
                                        }
                                    }
                                }, new Realm.Transaction.OnSuccess() {
                                    @Override
                                    public void onSuccess() {
                                        Realm realm1 = Realm.getDefaultInstance();
                                        for (RealmAvatar realmAvatar1 : realm1.where(RealmAvatar.class).findAll()) {
                                            if (realmAvatar1.getFile().getToken().equals(token)) {
                                                onAvatarGet.onAvatarGet(filepath, realmAvatar1.getOwnerId());
                                                break;
                                            }
                                        }
                                        realm1.close();
                                    }
                                });
                                realm.close();
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });

                String[] initials = showInitials(ownerId, avatarType);
                if (initials != null) {
                    onAvatarGet.onShowInitials(initials[0], initials[1]);
                }
            }
        } else {
            String[] initials = showInitials(ownerId, avatarType);
            if (initials != null) {
                onAvatarGet.onShowInitials(initials[0], initials[1]);
            }
        }
    }

    /**
     * return latest avatar with this ownerId
     *
     * @param ownerId if is user set userId and if is room set roomId
     * @return return latest RealmAvatar for this ownerId
     */

    public static RealmAvatar getLastAvatar(long ownerId) {
        Realm realm = Realm.getDefaultInstance();
        for (RealmAvatar avatar : realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, ownerId).findAllSorted(RealmAvatarFields.UID, Sort.DESCENDING)) {
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
            } else {
                for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {
                    if (realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getPeerId() == ownerId) {
                        initials = realmRoom.getInitials();
                        color = realmRoom.getColor();
                    }
                }
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
     * check in RealmAvatar that exist any avatar for this
     * ownerId (User or Room)
     *
     * @param ownerId userId if avatar is for user , roomId if avatar is for room
     * @return true if avatar exist otherwise return false
     */

    public static boolean checkExistAvatar(final long ownerId) {

        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmAvatar> results = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, ownerId).findAll();
        if (results.size() > 0) {
            realm.close();
            return true;
        }
        realm.close();
        return false;
    }

    /**
     * delete avatar and if another avatar is exist for this user
     * call latestAvatarPath latest avatar and if isn't exist call showInitials
     *
     * @param ownerId if is user set userId and if is room set roomId
     * @param avatarType set USER for user and ROOM for chat or group or channel
     * @param avatarId id avatar for delete from RealmAvatar
     */

    public static void avatarDelete(final long ownerId, final long avatarId, final AvatarType avatarType, @Nullable final OnAvatarDelete onAvatarDelete) {
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

                if (onAvatarDelete != null) {
                    getAvatar(ownerId, avatarType, new OnAvatarGet() {
                        @Override
                        public void onAvatarGet(String avatarPath, long ownerId) {
                            onAvatarDelete.latestAvatarPath(avatarPath);
                        }

                        @Override
                        public void onShowInitials(String initials, String color) {
                            onAvatarDelete.showInitials(initials, color);
                        }
                    });
                }

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                if (onAvatarDelete != null) {
                    String[] initials = showInitials(ownerId, avatarType);
                    if (initials != null) {
                        onAvatarDelete.showInitials(initials[0], initials[1]);
                    }
                }
            }
        });

        realm.close();
    }

    private static class AvatarDownload implements OnFileDownloaded {

        private static OnDownload onDownload;

        private void avatarDownload(RealmAttachment realmAttachment, ProtoFileDownload.FileDownload.Selector selector, OnDownload onDownload) {

            this.onDownload = onDownload;
            long fileSize = 0;
            String fileName = "";

            G.onFileDownloaded = this;
            if (selector == ProtoFileDownload.FileDownload.Selector.FILE) {
                fileName = realmAttachment.getToken() + "_" + realmAttachment.getName();
                fileSize = realmAttachment.getSize();
            } else if (selector == ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL) {
                fileName = "thumb_" + realmAttachment.getToken() + "_" + realmAttachment.getName();
                fileSize = realmAttachment.getLargeThumbnail().getSize();
            } else if (selector == ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL) {
                fileName = "thumb_" + realmAttachment.getToken() + "_" + realmAttachment.getName();
                fileSize = realmAttachment.getSmallThumbnail().getSize();
            }

            String identity = realmAttachment.getToken() + '*' + selector.toString() + '*' + fileSize + '*' + fileName + '*' + 0;

            new RequestFileDownload().download(realmAttachment.getToken(), 0, (int) fileSize, selector, identity);

        }

        @Override
        public void onFileDownload(String fileName, String token, long fileSize, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress) {
            if (progress == 100) {

                try {
                    AndroidUtils.cutFromTemp(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                onDownload.onDownload(G.DIR_IMAGE_USER + "/" + fileName, token);
            } else {
                // I don't use offset in getting thumbnail
                try {
                    String identity = token + '*' + selector.toString() + '*' + fileSize + '*' + fileName + '*' + offset;

                    new RequestFileDownload().download(token, offset, (int) fileSize, selector, identity);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        public void onError() {
            //TODO [Saeed Mozaffari] [2016-12-13 9:28 AM] - do something on download error (( hint : if was timeout reDownload file))
        }
    }


    //======================do this for room list

    public static void getAvatar1(final long ownerId, AvatarType avatarType, final OnAvatarGet onAvatarGet) {

        final RealmAvatar realmAvatar = getLastAvatar(ownerId);
        if (realmAvatar != null) {
            if (realmAvatar.getFile().isFileExistsOnLocal()) {
                onAvatarGet.onAvatarGet(realmAvatar.getFile().getLocalFilePath(), ownerId);
            } else if (realmAvatar.getFile().isThumbnailExistsOnLocal()) {
                onAvatarGet.onAvatarGet(realmAvatar.getFile().getLocalThumbnailPath(), ownerId);
            } else {

                new AvatarDownload().avatarDownload(realmAvatar.getFile(), ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, new OnDownload() {
                    @Override
                    public void onDownload(final String filepath, final String token) {

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        for (RealmAvatar realmAvatar1 : realm.where(RealmAvatar.class).findAll()) {
                                            if (realmAvatar1.getFile().getToken().equals(token)) {
                                                realmAvatar1.getFile().setLocalThumbnailPath(filepath);
                                                break;
                                            }
                                        }
                                    }
                                }, new Realm.Transaction.OnSuccess() {
                                    @Override
                                    public void onSuccess() {
                                        Realm realm1 = Realm.getDefaultInstance();
                                        for (RealmAvatar realmAvatar1 : realm1.where(RealmAvatar.class).findAll()) {
                                            if (realmAvatar1.getFile().getToken().equals(token)) {
                                                G.onUpdateAvatar.onUpdateAvatar(getRoomId(realmAvatar1.getOwnerId()));
                                                break;
                                            }
                                        }
                                        realm1.close();
                                    }
                                });
                                realm.close();
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });

                String[] initials = showInitials(ownerId, avatarType);
                if (initials != null) {
                    onAvatarGet.onShowInitials(initials[0], initials[1]);
                }
            }
        } else {
            String[] initials = showInitials(ownerId, avatarType);
            if (initials != null) {
                onAvatarGet.onShowInitials(initials[0], initials[1]);
            }
        }
    }

    private static long getRoomId(long ownerId) {
        Realm realm = Realm.getDefaultInstance();
        for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {
            if (realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getPeerId() == ownerId) {
                return realmRoom.getId();
            }
        }
        realm.close();
        return ownerId;
    }

    public static class UserInfo implements OnUserInfoForAvatar {

        public void getUserInfo(long userId) {
            G.onUserInfoForAvatar = this;
            HelperUserInfo.needUpdateUser(userId, null);
        }

        @Override
        public void onUserInfoForAvatar(final ProtoGlobal.RegisteredUser user) {

            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {
                        if (realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getPeerId() == user.getId()) {
                            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, user.getId()).findFirst();
                            if (realmRegisteredInfo != null) {
                                realmRoom.setAvatar(getLastAvatar(realmRegisteredInfo.getId()));
                            }
                        }
                    }
                }
            });
            realm.close();

            G.onUpdateAvatar.onUpdateAvatar(getRoomId(user.getId()));
        }
    }
}
