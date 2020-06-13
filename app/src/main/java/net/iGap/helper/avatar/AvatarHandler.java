/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.helper.avatar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import net.iGap.module.accountManager.DbManager;
import net.iGap.G;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLog;
import net.iGap.helper.LooperThreadHelper;
import net.iGap.observers.interfaces.OnAvatarAdd;
import net.iGap.observers.interfaces.OnComplete;
import net.iGap.observers.interfaces.OnDownload;
import net.iGap.observers.interfaces.OnFileDownloaded;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestFileDownload;
import net.iGap.request.RequestUserInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import io.realm.Realm;

import static net.iGap.G.context;
import static net.iGap.realm.RealmAvatar.getLastAvatar;

/**
 * helper avatar for add or delete avatars for user or room
 */
public class AvatarHandler {

    private class CacheValue {
        public Bitmap bitmap;
        long fileId;
        long avatarId;

        CacheValue(Bitmap bitmap, Long fileId, Long avatarId) {
            this.bitmap = bitmap;
            this.fileId = fileId;
            this.avatarId = avatarId;
        }
    }

    private static CopyOnWriteArraySet<AvatarHandler> allAvatarHandler = new CopyOnWriteArraySet<>();

    private ConcurrentHashMap<ImageView, ImageHashValue> imageViewHashValue;
    private ConcurrentHashMap<Long, HashSet<ImageView>> avatarHashImages;

    private static ConcurrentHashMap<Long, CacheValue> avatarCache = new ConcurrentHashMap<>();
    private static ArrayList<Long> limitedList = new ArrayList<>();

    private static ConcurrentHashMap<Long, CacheValue> avatarCacheMain = new ConcurrentHashMap<>();
    private static ArrayList<Long> limitedListMain = new ArrayList<>();

    private final Object mutex;
    private final Object mutex2;
    private static final Object mutex3 = new Object();
    private HashMap<Long, Boolean> mRepeatList = new HashMap<>();

    public AvatarHandler() {
        this.imageViewHashValue = new ConcurrentHashMap<>();
        this.avatarHashImages = new ConcurrentHashMap<>();
        this.mutex = new Object();
        this.mutex2 = new Object();
    }

    public static void clearCacheForOwnerId(long avatarOwnerId) {
        synchronized (mutex3) {
            limitedListMain.remove(avatarOwnerId);
            limitedList.remove(avatarOwnerId);
            avatarCacheMain.remove(avatarOwnerId);
            avatarCache.remove(avatarOwnerId);
        }
    }

    public void registerChangeFromOtherAvatarHandler() {
        synchronized (mutex2) {
            allAvatarHandler.add(this);
        }
    }

    public void unregisterChangeFromOtherAvatarHandler() {
        synchronized (mutex2) {
            allAvatarHandler.remove(this);
        }
    }

    private void notifyAll(String avatarPath, long avatarOwnerId, boolean isMain, long fileId, long avatarId) {
        try {
            notifyMe(avatarPath, avatarOwnerId, isMain, fileId, avatarId);
            notifyOther(avatarPath, avatarOwnerId, isMain, fileId, avatarId);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    private void notifyOther(String avatarPath, long avatarOwnerId, boolean isMain, long fileId, long avatarId) {
        synchronized (mutex2) {
            for (AvatarHandler avatarHandler : allAvatarHandler) {
                if (!avatarHandler.equals(this)) {
                    avatarHandler.notifyMe(avatarPath, avatarOwnerId, isMain, fileId, avatarId, false);
                }
            }
        }
    }

    public void notifyAllAvatar(String avatarPath, long avatarOwnerId, boolean isMain, long fileId, long avatarId) {
        for (AvatarHandler avatarHandler : allAvatarHandler) {
            avatarHandler.notifyMe(avatarPath, avatarOwnerId, isMain, fileId, avatarId, false);
        }
    }

    private void notifyMe(String avatarPath, long avatarOwnerId, boolean isMain, long fileId, long avatarId) {
        notifyMe(avatarPath, avatarOwnerId, isMain, fileId, avatarId, true);
    }

    private void notifyMe(String avatarPath, long avatarOwnerId, boolean isMain, long fileId, long avatarId, boolean returnCache) {
        ArrayList<Long> myLimitedList;
        ConcurrentHashMap<Long, CacheValue> myAvatarCache;
        int limit;
        if (isMain) {
            myAvatarCache = avatarCacheMain;
            myLimitedList = limitedListMain;
            limit = 1;
        } else {
            myAvatarCache = avatarCache;
            myLimitedList = limitedList;
            limit = 20;
        }

        CacheValue cache = myAvatarCache.get(avatarOwnerId);
        if (returnCache && cache != null && cache.fileId == fileId) {
            return;
        }

        final Bitmap bmImg = BitmapFactory.decodeFile(avatarPath);
        if (bmImg != null) {
            synchronized (mutex3) {
                myAvatarCache.put(avatarOwnerId, new CacheValue(bmImg, fileId, avatarId));
                int index = myLimitedList.indexOf(avatarOwnerId);
                if (index < 0) {
                    if (myLimitedList.size() > limit) {
                        Long ss = myLimitedList.remove(0);
//                        CacheValue cacheValue = myAvatarCache.get(ss);
//                        if (cacheValue != null && cacheValue.bitmap != null) {
//                            cacheValue.bitmap.recycle();
//                        }
                        myAvatarCache.remove(ss);
                    }
                    myLimitedList.add(avatarOwnerId);
                } else if (myLimitedList.size() - 1 != index) {
                    Collections.swap(myLimitedList, myLimitedList.size() - 1, index);
                }
            }
        } else {
            if (avatarPath != null && new File(avatarPath).exists()) {
                HelperLog.getInstance().setErrorLog(new Exception("avatar " + avatarOwnerId + " is null with path: " + avatarPath + " and isMain:" + isMain + " File Exist:" + "true" +
                        " FileSize=" + new File(avatarPath).length()));
            } else {
                HelperLog.getInstance().setErrorLog(new Exception("avatar " + avatarOwnerId + " is null with path: " + avatarPath + " and isMain:" + isMain + " file not exist"));
            }
        }

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mutex) {
                    HashSet<ImageView> imageViews = avatarHashImages.get(avatarOwnerId);
                    if (imageViews != null) {
                        for (ImageView imageView : imageViews) {
                            ImageHashValue imageHashValue = imageViewHashValue.get(imageView);
                            if (imageHashValue != null && bmImg != null) {
                                imageView.setImageBitmap(bmImg);
                                if (imageHashValue.onChangeAvatar != null)
                                    imageHashValue.onChangeAvatar.onChange(false);
                            }
                        }
                    }
                }
            }
        });
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
                DbManager.getInstance().doRealmTransaction(realm -> {
                    if (src == null) {
                        return;
                    }

                    String avatarPath = copyAvatar(src, avatar);
                    RealmAvatar a = RealmAvatar.putOrUpdate(realm, ownerId, avatar);
                    a.getFile().setLocalFilePath(avatarPath);

                    if (onAvatarAdd != null && avatarPath != null) {
                        onAvatarAdd.onAvatarAdd(avatarPath);
                    }
                    AvatarHandler.this.notifyAll(avatarPath, ownerId, true, a.getFile().getId(), a.getId());
                });
            }
        });
    }

    public void avatarDelete(BaseParam baseParam, long avatarOwnerId) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                baseParam.useCache = false;
                DbManager.getInstance().doRealmTask(realm -> {
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmAvatar.deleteAvatar(realm, avatarOwnerId);
                        }
                    }, () -> {
                        getAvatar(baseParam);
                    });
                });
            }
        });
    }


    // ******************************************* End *********************************************

    public void removeImageViewFromHandler(ImageView avatarIv) {
        if (avatarIv.getTag() == null)
            return;

        HashSet<ImageView> imageViewHashSet = avatarHashImages.get(avatarIv.getTag());

        if (imageViewHashSet != null) {
            imageViewHashSet.remove(avatarIv);
        }

        imageViewHashValue.remove(avatarIv);
        avatarIv.setTag(-1L);
    }

    public void getAvatar(BaseParam baseParam) {
        getAvatar(baseParam, false);
    }

    public void getAvatar(BaseParam baseParam, boolean requestUserInfo) {

        if (Looper.myLooper() != Looper.getMainLooper()) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    getAvatar(baseParam);
                }
            });
            return;
        }

        if (baseParam.imageView == null || baseParam.avatarOwnerId == null) {
            return;
        }

        baseParam.imageView.setTag(baseParam.avatarOwnerId);

        synchronized (mutex) {
            Bitmap cacheValue = null;
            if (baseParam.useCache) {
                CacheValue mainCache = avatarCacheMain.get(baseParam.avatarOwnerId);
                CacheValue thumbnailCache = avatarCache.get(baseParam.avatarOwnerId);

                if (baseParam.showMain) {
                    if (mainCache == null) {
                        if (thumbnailCache != null) {
                            cacheValue = thumbnailCache.bitmap;
                        }
                    } else {
                        if (thumbnailCache == null) {
                            cacheValue = mainCache.bitmap;
                        } else {
                            if (mainCache.avatarId >= thumbnailCache.avatarId) {
                                cacheValue = mainCache.bitmap;
                            } else {
                                cacheValue = thumbnailCache.bitmap;
                            }
                        }
                    }
                } else {
                    if (thumbnailCache != null)
                        cacheValue = thumbnailCache.bitmap;
                }
            }
            if (cacheValue == null) {
                if (baseParam instanceof ParamWithAvatarType) {
                    String[] initialsStart = showInitials(baseParam.avatarOwnerId, ((ParamWithAvatarType) baseParam).avatarType);
                    if (initialsStart != null) {
                        cacheValue = HelperImageBackColor.drawAlphabetOnPicture((int) context.getResources().getDimension(((ParamWithAvatarType) baseParam).avatarSize), initialsStart[0], initialsStart[1]);
                    }
                } else if (baseParam instanceof ParamWithInitBitmap) {
                    cacheValue = ((ParamWithInitBitmap) baseParam).initAvatar;
                }
            }

            baseParam.imageView.setImageBitmap(cacheValue);
            if (baseParam.onInitSet != null)
                baseParam.onInitSet.OnInitSet();

            ImageHashValue imageHashValue = imageViewHashValue.get(baseParam.imageView);
            if (imageHashValue != null) {
                HashSet<ImageView> imageViews = avatarHashImages.get(imageHashValue.avatarOwnerId);
                if (imageViews != null) {
                    imageViews.remove(baseParam.imageView);
                    avatarHashImages.put(imageHashValue.avatarOwnerId, imageViews);
                }
            }

            imageViewHashValue.put(baseParam.imageView, new ImageHashValue(baseParam.avatarOwnerId, baseParam.onAvatarChange));
            HashSet<ImageView> imageViews = avatarHashImages.get(baseParam.avatarOwnerId);
            if (imageViews == null) {
                imageViews = new HashSet<>();
            }
            imageViews.add(baseParam.imageView);
            avatarHashImages.put(baseParam.avatarOwnerId, imageViews);
        }

        LooperThreadHelper.getInstance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                DbManager.getInstance().doRealmTask(realm -> {
                    getAvatarImage(baseParam, realm, requestUserInfo);
                });
            }
        });
    }

    /**
     * check avatar in Realm and download if needed
     */
    private void getAvatarImage(BaseParam baseParam, Realm _realm, boolean requestUserInfo) {
        RealmAvatar realmAvatar = getLastAvatar(baseParam.avatarOwnerId, _realm);

        if (realmAvatar == null && requestUserInfo && G.userLogin) {

            new RequestUserInfo().userInfoWithCallBack(new OnComplete() {
                @Override
                public void complete(boolean result, String messageOne, String MessageTow) {
                    getAvatar(baseParam, false);
                }
            }, baseParam.avatarOwnerId, "" + baseParam.avatarOwnerId);

        }

        if (realmAvatar == null && baseParam instanceof ParamWithAvatarType && ((ParamWithAvatarType) baseParam).registeredUser != null) {
            insertRegisteredInfoToDB(((ParamWithAvatarType) baseParam).registeredUser, _realm);
            realmAvatar = getLastAvatar(baseParam.avatarOwnerId, _realm);
        }

        if (realmAvatar != null) {

            if (baseParam.showMain && realmAvatar.getFile().isFileExistsOnLocal()) {
                final String path = realmAvatar.getFile().getLocalFilePath();
                notifyAll(path, baseParam.avatarOwnerId, true, realmAvatar.getFile().getId(), realmAvatar.getId());
            } else if (realmAvatar.getFile().isThumbnailExistsOnLocal()) {
                final String path = realmAvatar.getFile().getLocalThumbnailPath();
                notifyAll(path, baseParam.avatarOwnerId, false, realmAvatar.getFile().getId(), realmAvatar.getId());
            } else {

                new AvatarDownload().avatarDownload(realmAvatar.getFile(), ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL, new OnDownload() {
                    @Override
                    public void onDownload(final String filepath, final String token) {
                        if (!(new File(filepath).exists())) {
                            HelperLog.getInstance().setErrorLog(new Exception("File Dont Exist After Download !!" + filepath));
                        }

                        final ArrayList<Long> ownerIdList = new ArrayList<>();
                        final ArrayList<Long> fileIdList = new ArrayList<>();
                        final ArrayList<Long> avatarIdList = new ArrayList<>();
                        DbManager.getInstance().doRealmTransaction(realm -> {
                            for (RealmAvatar realmAvatar1 : realm.where(RealmAvatar.class).equalTo("file.token", token).findAll()) {
                                realmAvatar1.getFile().setLocalThumbnailPath(filepath);
                                ownerIdList.add(realmAvatar1.getOwnerId());
                                fileIdList.add(realmAvatar1.getFile().getId());
                                avatarIdList.add(realmAvatar1.getId());
                            }
                        });
                        for (int i = 0; i < ownerIdList.size(); i++) {
                            AvatarHandler.this.notifyAll(filepath, ownerIdList.get(i), false, fileIdList.get(i), avatarIdList.get(i));
                        }
                        ownerIdList.clear();
                        fileIdList.clear();
                        avatarIdList.clear();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        } else if (baseParam instanceof ParamWithAvatarType) {
            String[] initials = showInitials(baseParam.avatarOwnerId, ((ParamWithAvatarType) baseParam).avatarType);
            if (initials == null) {
                getAvatarAfterTime(baseParam);
            }
        }
    }

    private void getAvatarAfterTime(BaseParam baseParam) {

        try {
            if (mRepeatList.containsKey(baseParam.avatarOwnerId)) {
                return;
            }
            LooperThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRepeatList.put(baseParam.avatarOwnerId, true);
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            getAvatar(baseParam);
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
        return DbManager.getInstance().doRealmTask(realm -> {
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

            if (initials != null && color != null) {
                return new String[]{initials, color};
            }
            return null;
        });
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
                long offset = 0;
                if (new File(filePath).exists()) {
                    offset = new File(filePath).length();
                }
                new RequestFileDownload().download(realmAttachment.getToken(), offset, (int) fileSize, selector,
                        new RequestFileDownload.IdentityFileDownload(ProtoGlobal.RoomMessageType.IMAGE, realmAttachment.getToken(), filePath, selector, fileSize, offset, RequestFileDownload.TypeDownload.AVATAR), true);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFileDownload(String filePath, String token, long fileSize, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress) {
            if (progress >= 100) {
                if (!(new File(filePath).exists())) {
                    HelperLog.getInstance().setErrorLog(new Exception("After Download File Not Exist Bug. Please check" + filePath));
                }

                String _newPath = filePath.replace(G.DIR_TEMP, G.DIR_IMAGE_USER);
                try {
                    AndroidUtils.cutFromTemp(filePath, _newPath);
                } catch (IOException e) {
                    HelperLog.getInstance().setErrorLog(e);
                    e.printStackTrace();
                }

                onDownload.onDownload(_newPath, token);
            } else {
                /**
                 * don't use offset in getting thumbnail
                 */
                try {
                    new RequestFileDownload().download(token, offset, (int) fileSize, selector, new RequestFileDownload.IdentityFileDownload(ProtoGlobal.RoomMessageType.IMAGE, token, filePath, selector, fileSize, offset, RequestFileDownload.TypeDownload.AVATAR), true);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onError(int major, Object identity) {
        }
    }
}
