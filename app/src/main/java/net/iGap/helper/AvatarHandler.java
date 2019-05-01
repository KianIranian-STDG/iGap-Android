/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ImageView;

import net.iGap.G;
import net.iGap.fragments.BaseFragment;
import net.iGap.interfaces.OnAvatarAdd;
import net.iGap.interfaces.OnAvatarDelete;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnDownload;
import net.iGap.interfaces.OnFileDownloaded;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestFileDownload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.realm.Realm;

import static net.iGap.realm.RealmAvatar.getLastAvatar;

/**
 * helper avatar for add or delete avatars for user or room
 */
public class AvatarHandler {

    public class ImageHashValue {
        public long avatarId;
        public OnAvatarGet onAvatarGet;

        ImageHashValue(long avatarId, OnAvatarGet onAvatarGet) {
            this.avatarId = avatarId;
            this.onAvatarGet = onAvatarGet;
        }
    }

    private ConcurrentHashMap<ImageView, ImageHashValue> imageViewHashValue;
    private ConcurrentHashMap<Long, HashSet<ImageView>> avatarHashImages;
    private ConcurrentHashMap<Long, Bitmap> avatarCache;
    private FragmentManager fragmentManager;
    private final Object mutex;
    private HashMap<Long, Boolean> mRepeatList = new HashMap<>();
    private ArrayList<String> reDownloadFiles = new ArrayList<>();

    public AvatarHandler(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.imageViewHashValue = new ConcurrentHashMap<>();
        this.avatarHashImages = new ConcurrentHashMap<>();
        this.avatarCache = new ConcurrentHashMap<>();
        this.mutex = new Object();
    }

    private ArrayList<BaseFragment> getVisibleBaseFragments() {
        List<Fragment> fragments = fragmentManager.getFragments();
        ArrayList<BaseFragment> baseFragments = new ArrayList<>();
        for (Fragment fragment : fragments) {
            if (fragment instanceof BaseFragment) {
                baseFragments.add((BaseFragment) fragment);
            }
        }
        return baseFragments;
    }

    private void notifyImageViewAvatarFragments(String avatarPath, long avatarId) {
        for (BaseFragment baseFragment : getVisibleBaseFragments()) {
            baseFragment.avatarHandler.notifyImageViewAvatar(avatarPath, avatarId);
        }
    }

    private void notifyImageViewAvatarFragments(String initials, String color, long avatarId) {
        for (BaseFragment baseFragment : getVisibleBaseFragments()) {
            baseFragment.avatarHandler.notifyImageViewAvatar(initials, color, avatarId);
        }
    }

    private ArrayList<ImageHashValue> getAllCallback(long avatarId) {
        ArrayList<ImageHashValue> result = new ArrayList<>();
        HashSet<ImageView> imageViews = avatarHashImages.get(avatarId);
        if (imageViews != null) {
            for (ImageView imageView : imageViews) {
                ImageHashValue imageHashValue = imageViewHashValue.get(imageView);
                if (imageHashValue != null) {
                    result.add(imageHashValue);
                }
            }
        }
        return result;
    }

    private void notifyImageViewAvatar(String avatarPath, long avatarId) {
        final Bitmap bmImg = BitmapFactory.decodeFile(avatarPath);
        avatarCache.put(avatarId, bmImg);
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mutex) {
                    HashSet<ImageView> imageViews = avatarHashImages.get(avatarId);
                    if (imageViews != null) {
                        for (ImageView imageView : imageViews) {
                            ImageHashValue imageHashValue = imageViewHashValue.get(imageView);
                            if (imageHashValue != null) {
                                imageView.setImageBitmap(bmImg);
                                imageHashValue.onAvatarGet.onAvatarGet(avatarPath, avatarId);
                            }
                        }
                    }
                }
            }
        });

    }

    private void notifyImageViewAvatar(String initials, String color, long avatarId) {
        HashSet<ImageView> imageViews = avatarHashImages.get(avatarId);
        if (imageViews != null) {
            for (ImageView imageView : imageViews) {
                ImageHashValue imageHashValue = imageViewHashValue.get(imageView);
                if (imageHashValue != null) {
                    imageHashValue.onAvatarGet.onShowInitials(initials, color, avatarId);
                }
            }
        }
    }

    /**
     * add avatar in RealmAvatar and after copy avatar
     * to final path call callback for return final path
     *
     * @param ownerId user id for users and roomId for rooms
     */
    public void avatarAdd(final long ownerId, final String src, final ProtoGlobal.Avatar avatar, final OnAvatarAdd onAvatarAdd) {
        LooperThreadHelper.getInstance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (src == null) {
                            return;
                        }

                        String avatarPath = copyAvatar(src, avatar);
                        RealmAvatar.putOrUpdate(realm, ownerId, avatar).getFile().setLocalFilePath(avatarPath);

                        if (onAvatarAdd != null && avatarPath != null) {
                            onAvatarAdd.onAvatarAdd(avatarPath);
                        }
                        notifyImageViewAvatarFragments(avatarPath, ownerId);
                    }
                });
                realm.close();
            }
        });
    }

    /**
     * delete avatar and if another avatar is exist for this user
     * call latestAvatarPath latest avatar and if isn't exist call showInitials
     *
     * @param ownerId    if is user set userId and if is room set roomId
     * @param avatarType set USER for user and ROOM for chat or group or channel
     * @param avatarId   id avatar for delete from RealmAvatar
     */
    public void avatarDelete(ImageView imageView, final long ownerId, final long avatarId, final AvatarType avatarType, @Nullable final OnAvatarDelete onAvatarDelete) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmAvatar.deleteAvatar(realm, avatarId);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        if (onAvatarDelete != null) {
                            getAvatar(imageView, null, ownerId, avatarType, false, null, false, new OnAvatarGet() {
                                @Override
                                public void onAvatarGet(String avatarPath, long ownerId) {
                                    onAvatarDelete.latestAvatarPath(avatarPath);
                                }

                                @Override
                                public void onShowInitials(String initials, String color, long ownerId) {
                                    onAvatarDelete.showInitials(initials, color);
                                }
                            });
                        }
                        realm.close();
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
                        realm.close();
                    }
                });
            }
        });
    }

    /**
     * use this method if have realm instance
     * read avatarPath from realm avatar and return latest avatarPath
     *
     * @param registeredUser if avatar not detect will be used from this params for add to realm and after than find avatar
     * @param ownerId        if is user set userId and if is room set roomId
     * @param avatarType     USER for contacts and chat , ROOM for group and channel
     * @param showMain       true for set main avatar and false for show thumbnail
     * @param onAvatarGet    callback for return info
     */
    public void getAvatar(ImageView imageView, @Nullable ProtoGlobal.RegisteredUser registeredUser, final Long ownerId, AvatarType avatarType, boolean showMain, Bitmap initAvatar, boolean useCache, final OnAvatarGet onAvatarGet) {
        /**
         * first show user initials and after that show avatar if exist
         */
        /**
         * important note: this function must always be called from ui thread
         */

        if (useCache && !showMain) {
            Bitmap cacheValue = avatarCache.get(ownerId);
            if (cacheValue != null) {
                imageView.setImageBitmap(cacheValue);
                onAvatarGet.onAvatarGet(null, ownerId);
                return;
            }
        }

        synchronized (mutex) {
            if (initAvatar == null) {
                String[] initialsStart = showInitials(ownerId, avatarType);
                if (initialsStart != null) {
                    onAvatarGet.onShowInitials(initialsStart[0], initialsStart[1], ownerId);
                }
            } else {
                imageView.setImageBitmap(initAvatar);
            }

            ImageHashValue imageHashValue = imageViewHashValue.get(imageView);
            if (imageHashValue != null) {
                HashSet<ImageView> imageViews = avatarHashImages.get(imageHashValue.avatarId);
                if (imageViews != null) {
                    imageViews.remove(imageView);
                    avatarHashImages.put(imageHashValue.avatarId, imageViews);
                }
            }

            imageViewHashValue.put(imageView, new ImageHashValue(ownerId, onAvatarGet));
            HashSet<ImageView> imageViews = avatarHashImages.get(ownerId);
            if (imageViews == null) {
                imageViews = new HashSet<>();
            }
            imageViews.add(imageView);
            avatarHashImages.put(ownerId, imageViews);
        }

        // do first init
        LooperThreadHelper.getInstance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                getAvatarImage(imageView, initAvatar, registeredUser, ownerId, avatarType, showMain, realm, onAvatarGet);
                realm.close();
            }
        });

    }

    /**
     * check avatar in Realm and download if needed
     */
    private void getAvatarImage(ImageView imageView, Bitmap bitmap, ProtoGlobal.RegisteredUser registeredUser, final long ownerId, AvatarType avatarType, boolean showMain, Realm _realm, final OnAvatarGet onAvatarGet) {
        RealmAvatar realmAvatar = getLastAvatar(ownerId, _realm);

        if (realmAvatar == null && registeredUser != null) {
            insertRegisteredInfoToDB(registeredUser, _realm);
            realmAvatar = getLastAvatar(ownerId, _realm);
        }

        if (realmAvatar != null) {

            if (showMain && realmAvatar.getFile().isFileExistsOnLocal()) {
                final String path = realmAvatar.getFile().getLocalFilePath();
                notifyImageViewAvatar(path, ownerId);
            } else if (realmAvatar.getFile().isThumbnailExistsOnLocal()) {
                final String path = realmAvatar.getFile().getLocalThumbnailPath();
                notifyImageViewAvatar(path, ownerId);
            } else {

                new AvatarDownload().avatarDownload(realmAvatar.getFile(), ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, new OnDownload() {
                    @Override
                    public void onDownload(final String filepath, final String token) {

                        final ArrayList<Long> ownerIdList = new ArrayList<>();
                        Realm realm = Realm.getDefaultInstance();

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                for (RealmAvatar realmAvatar1 : realm.where(RealmAvatar.class).equalTo("file.token", token).findAll()) {
                                    realmAvatar1.getFile().setLocalThumbnailPath(filepath);
                                    ownerIdList.add(realmAvatar1.getOwnerId());
                                }
                            }
                        });

                        realm.close();
                        for (long ownerId : ownerIdList) {
                            notifyImageViewAvatar(filepath, ownerId);
                        }
                        ownerIdList.clear();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        } else {
            String[] initials = showInitials(ownerId, avatarType);
            if (initials == null) {
                getAvatarAfterTime(imageView, ownerId, avatarType, onAvatarGet, bitmap);
            }
        }
    }

    private void getAvatarAfterTime(final ImageView imageView, final long ownerId, final AvatarType avatarType, final OnAvatarGet onAvatarGet, final Bitmap bitmap) {

        try {
            if (mRepeatList.containsKey(ownerId)) {
                return;
            }
            LooperThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRepeatList.put(ownerId, true);
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            getAvatar(imageView, null, ownerId, avatarType, false, bitmap, true, onAvatarGet);
                        }
                    });
                }
            }, 1500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get temp address for source and get token and name
     * from avatar for file destination
     *
     * @param src    temp address
     * @param avatar avatar that want copy
     * @return return destination path if copy was successfully
     */
    private String copyAvatar(String src, ProtoGlobal.Avatar avatar) {
        try {
            /**
             * G.DIR_IMAGE_USER use for all avatars , user or room
             */
            String avatarPath = AndroidUtils.getFilePathWithCashId(avatar.getFile().getCacheId(), avatar.getFile().getName(), G.DIR_IMAGE_USER, false);

            AndroidUtils.copyFile(new File(src), new File(avatarPath));

            return avatarPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void insertRegisteredInfoToDB(final ProtoGlobal.RegisteredUser registeredUser, Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRegisteredInfo.putOrUpdate(realm, registeredUser);
                RealmAvatar.putOrUpdateAndManageDelete(realm, registeredUser.getId(), registeredUser.getAvatar());
            }
        });
    }

    /**
     * read from user and room db in local for find initials and color
     *
     * @param ownerId if is user set userId and if is room set roomId
     * @return initials[0] , color[1]
     */
    public String[] showInitials(long ownerId, AvatarType avatarType) {
        Realm realm = Realm.getDefaultInstance();
        String initials = null;
        String color = null;
        if (avatarType == AvatarType.USER) {

            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, ownerId);
            if (realmRegisteredInfo != null) {
                initials = realmRegisteredInfo.getInitials();
                color = realmRegisteredInfo.getColor();
            } else {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, ownerId).findFirst();
                if (realmRoom != null) {
                    initials = realmRoom.getInitials();
                    color = realmRoom.getColor();
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

    public enum AvatarType {
        USER, ROOM
    }

    private class AvatarDownload implements OnFileDownloaded {

        private OnDownload onDownload;

        private void avatarDownload(RealmAttachment realmAttachment, ProtoFileDownload.FileDownload.Selector selector, OnDownload onDownload) {

            try {
                this.onDownload = onDownload;
                long fileSize = 0;
                String filePath = "";

                G.onFileDownloaded = this;
                if (selector == ProtoFileDownload.FileDownload.Selector.FILE) {
                    filePath = AndroidUtils.getFilePathWithCashId(realmAttachment.getCacheId(), realmAttachment.getName(), G.DIR_TEMP, false);
                    fileSize = realmAttachment.getSize();
                } else if (selector == ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL) {
                    filePath = AndroidUtils.getFilePathWithCashId(realmAttachment.getCacheId(), realmAttachment.getName(), G.DIR_TEMP, true);
                    fileSize = realmAttachment.getLargeThumbnail().getSize();
                } else if (selector == ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL) {
                    filePath = AndroidUtils.getFilePathWithCashId(realmAttachment.getCacheId(), realmAttachment.getName(), G.DIR_TEMP, true);
                    fileSize = realmAttachment.getSmallThumbnail().getSize();
                }
                new RequestFileDownload().download(realmAttachment.getToken(), 0, (int) fileSize, selector,
                        new RequestFileDownload.IdentityFileDownload(ProtoGlobal.RoomMessageType.IMAGE, realmAttachment.getToken(), filePath, selector, fileSize, 0, RequestFileDownload.TypeDownload.AVATAR));
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFileDownload(String filePath, String token, long fileSize, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress) {
            if (progress == 100) {
                String _newPath = filePath.replace(G.DIR_TEMP, G.DIR_IMAGE_USER);
                try {
                    AndroidUtils.cutFromTemp(filePath, _newPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                onDownload.onDownload(_newPath, token);
            } else {
                /**
                 * don't use offset in getting thumbnail
                 */
                try {
                    new RequestFileDownload().download(token, offset, (int) fileSize, selector, new RequestFileDownload.IdentityFileDownload(ProtoGlobal.RoomMessageType.IMAGE, token, filePath, selector, fileSize, 0, RequestFileDownload.TypeDownload.AVATAR));
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onError(int major, Object identity) {
            if (major == 5 && identity != null) { //if is time out reDownload once
                RequestFileDownload.IdentityFileDownload identityFileDownload = ((RequestFileDownload.IdentityFileDownload) identity);
                String token = identityFileDownload.cacheId;
                if (!reDownloadFiles.contains(token)) {
                    reDownloadFiles.add(token);
                    new RequestFileDownload().download(token, 0, (int) identityFileDownload.size, identityFileDownload.selector, identity);
                }
            }
        }
    }
}
