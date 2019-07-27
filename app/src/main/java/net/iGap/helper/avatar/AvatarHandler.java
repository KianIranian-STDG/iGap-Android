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

import net.iGap.G;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLog;
import net.iGap.helper.LooperThreadHelper;
import net.iGap.interfaces.OnAvatarAdd;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

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

        CacheValue(Bitmap bitmap, Long fileId) {
            this.bitmap = bitmap;
            this.fileId = fileId;
        }
    }

    private HashSet<AvatarHandler> allAvatarHandler = new HashSet<>();

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

    private void notifyAll(String avatarPath, long avatarId, boolean isMain, long fileId) {
        try {
            notifyMe(avatarPath, avatarId, isMain, fileId);
            notifyOther(avatarPath, avatarId, isMain, fileId);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    private void notifyOther(String avatarPath, long avatarId, boolean isMain, long fileId) {
        synchronized (mutex2) {
            for (AvatarHandler avatarHandler : allAvatarHandler) {
                if (!avatarHandler.equals(this)) {
                    avatarHandler.notifyMe(avatarPath, avatarId, isMain, fileId);
                }
            }
        }
    }

    private void notifyMe(String avatarPath, long avatarId, boolean isMain, long fileId) {
        final Bitmap bmImg = BitmapFactory.decodeFile(avatarPath);
        if (bmImg != null) {
            synchronized (mutex3) {
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
                    limit = 15;
                }

                myAvatarCache.put(avatarId, new CacheValue(bmImg, fileId));
                int index = myLimitedList.indexOf(avatarId);
                if (index < 0) {
                    if (myLimitedList.size() > limit) {
                        Long ss = myLimitedList.remove(0);
                        myAvatarCache.remove(ss);
                    }
                    myLimitedList.add(avatarId);
                } else if (myLimitedList.size() - 1 != index){
                    Collections.swap(myLimitedList, myLimitedList.size() - 1, index);
                }
            }
        } else {
            if (avatarPath != null && new File(avatarPath).exists()) {
                HelperLog.setErrorLog(new Exception("avatar " + avatarId + " is null with path: " + avatarPath + " and isMain:" + isMain + " File Exist:" + "true" +
                        " FileSize=" + new File(avatarPath).length()));
            } else {
                HelperLog.setErrorLog(new Exception("avatar " + avatarId + " is null with path: " + avatarPath + " and isMain:" + isMain + " file not exist"));
            }
        }

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mutex) {
                    HashSet<ImageView> imageViews = avatarHashImages.get(avatarId);
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
                Realm realm = Realm.getDefaultInstance();

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (src == null) {
                            return;
                        }

                        String avatarPath = copyAvatar(src, avatar);
                        RealmAvatar a = RealmAvatar.putOrUpdate(realm, ownerId, avatar);
                        a.getFile().setLocalFilePath(avatarPath);

                        if (onAvatarAdd != null && avatarPath != null) {
                            onAvatarAdd.onAvatarAdd(avatarPath);
                        }
                        AvatarHandler.this.notifyAll(avatarPath, ownerId, true, a.getFile().getId());
                    }
                });
                realm.close();
            }
        });
    }

    public void avatarDelete(BaseParam baseParam, long avatarId) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();
                baseParam.useCache = false;
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmAvatar.deleteAvatar(realm, avatarId);
                    }
                }, () -> {
                    G.refreshRealmUi();
                    getAvatar(baseParam);
                }, error -> {
                });
                realm.close();
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

        if (Looper.myLooper() != Looper.getMainLooper()) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    getAvatar(baseParam);
                }
            });
            return;
        }

        if (baseParam.imageView == null || baseParam.avatarId == null) {
            return;
        }

        baseParam.imageView.setTag(baseParam.avatarId);

        synchronized (mutex) {
            Bitmap cacheValue = null;
            if (baseParam.useCache) {
                CacheValue mainCache = avatarCacheMain.get(baseParam.avatarId);
                CacheValue thumbnailCache = avatarCache.get(baseParam.avatarId);

                if (baseParam.showMain) {
                    if (mainCache == null) {
                        if (thumbnailCache != null) {
                            cacheValue = thumbnailCache.bitmap;
                        }
                    } else {
                        if (thumbnailCache == null) {
                            cacheValue = mainCache.bitmap;
                        } else {
                            if (mainCache.fileId == thumbnailCache.fileId) {
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
                    String[] initialsStart = showInitials(baseParam.avatarId, ((ParamWithAvatarType) baseParam).avatarType);
                    if (initialsStart != null) {
                        cacheValue = HelperImageBackColor.drawAlphabetOnPicture((int) context.getResources().getDimension(((ParamWithAvatarType) baseParam).avatarSize), initialsStart[0], initialsStart[1]);
                    }
                } else if (baseParam instanceof ParamWithInitBitmap){
                    cacheValue = ((ParamWithInitBitmap) baseParam).initAvatar;
                }
            }

            baseParam.imageView.setImageBitmap(cacheValue);
            if (baseParam.onInitSet != null)
                baseParam.onInitSet.OnInitSet();

            ImageHashValue imageHashValue = imageViewHashValue.get(baseParam.imageView);
            if (imageHashValue != null) {
                HashSet<ImageView> imageViews = avatarHashImages.get(imageHashValue.avatarId);
                if (imageViews != null) {
                    imageViews.remove(baseParam.imageView);
                    avatarHashImages.put(imageHashValue.avatarId, imageViews);
                }
            }

            imageViewHashValue.put(baseParam.imageView, new ImageHashValue(baseParam.avatarId, baseParam.onAvatarChange));
            HashSet<ImageView> imageViews = avatarHashImages.get(baseParam.avatarId);
            if (imageViews == null) {
                imageViews = new HashSet<>();
            }
            imageViews.add(baseParam.imageView);
            avatarHashImages.put(baseParam.avatarId, imageViews);
        }

        LooperThreadHelper.getInstance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                getAvatarImage(baseParam, realm);
                realm.close();
            }
        });
    }

    /**
     * check avatar in Realm and download if needed
     */
    private void getAvatarImage(BaseParam baseParam, Realm _realm) {
        RealmAvatar realmAvatar = getLastAvatar(baseParam.avatarId, _realm);

        if (realmAvatar == null && baseParam instanceof ParamWithAvatarType && ((ParamWithAvatarType)baseParam).registeredUser != null) {
            insertRegisteredInfoToDB(((ParamWithAvatarType)baseParam).registeredUser, _realm);
            realmAvatar = getLastAvatar(baseParam.avatarId, _realm);
        }

        if (realmAvatar != null) {

            if (baseParam.showMain && realmAvatar.getFile().isFileExistsOnLocal()) {
                final String path = realmAvatar.getFile().getLocalFilePath();
                notifyAll(path, baseParam.avatarId, true, realmAvatar.getFile().getId());
            } else if (realmAvatar.getFile().isThumbnailExistsOnLocal()) {
                final String path = realmAvatar.getFile().getLocalThumbnailPath();
                notifyAll(path, baseParam.avatarId, false, realmAvatar.getFile().getId());
            } else {

                new AvatarDownload().avatarDownload(realmAvatar.getFile(), ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL, new OnDownload() {
                    @Override
                    public void onDownload(final String filepath, final String token) {
                        if (!(new File(filepath).exists())) {
                            HelperLog.setErrorLog(new Exception("File Dont Exist After Download !!" + filepath));
                        }

                        final ArrayList<Long> ownerIdList = new ArrayList<>();
                        final ArrayList<Long> fileIdList = new ArrayList<>();
                        Realm realm = Realm.getDefaultInstance();

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                for (RealmAvatar realmAvatar1 : realm.where(RealmAvatar.class).equalTo("file.token", token).findAll()) {
                                    realmAvatar1.getFile().setLocalThumbnailPath(filepath);
                                    ownerIdList.add(realmAvatar1.getOwnerId());
                                    fileIdList.add(realmAvatar1.getFile().getId());
                                }
                            }
                        });

                        realm.close();
                        for (int i = 0; i < ownerIdList.size(); i++) {
                            AvatarHandler.this.notifyAll(filepath, ownerIdList.get(i), false, fileIdList.get(i));
                        }
                        ownerIdList.clear();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        } else if (baseParam instanceof ParamWithAvatarType) {
            String[] initials = showInitials(baseParam.avatarId, ((ParamWithAvatarType) baseParam).avatarType);
            if (initials == null) {
                getAvatarAfterTime(baseParam);
            }
        }
    }

    private void getAvatarAfterTime(BaseParam baseParam) {

        try {
            if (mRepeatList.containsKey(baseParam.avatarId)) {
                return;
            }
            LooperThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRepeatList.put(baseParam.avatarId, true);
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
                long offset = 0;
                if (new File(filePath).exists()) {
                    offset = new File(filePath).length();
                }
                new RequestFileDownload().download(realmAttachment.getToken(), offset, (int) fileSize, selector,
                        new RequestFileDownload.IdentityFileDownload(ProtoGlobal.RoomMessageType.IMAGE, realmAttachment.getToken(), filePath, selector, fileSize, offset, RequestFileDownload.TypeDownload.AVATAR));
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFileDownload(String filePath, String token, long fileSize, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress) {
            if (progress >= 100) {
                if (!(new File(filePath).exists())) {
                    HelperLog.setErrorLog(new Exception("After Download File Not Exist Bug. Please check" + filePath));
                }

                String _newPath = filePath.replace(G.DIR_TEMP, G.DIR_IMAGE_USER);
                try {
                    AndroidUtils.cutFromTemp(filePath, _newPath);
                } catch (IOException e) {
                    HelperLog.setErrorLog(e);
                    e.printStackTrace();
                }

                onDownload.onDownload(_newPath, token);
            } else {
                /**
                 * don't use offset in getting thumbnail
                 */
                try {
                    new RequestFileDownload().download(token, offset, (int) fileSize, selector, new RequestFileDownload.IdentityFileDownload(ProtoGlobal.RoomMessageType.IMAGE, token, filePath, selector, fileSize, offset, RequestFileDownload.TypeDownload.AVATAR));
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
