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

import androidx.annotation.Nullable;

import net.iGap.G;
import net.iGap.helper.HelperMimeType;
import net.iGap.helper.HelperString;
import net.iGap.module.AndroidUtils;
import net.iGap.module.SUID;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.AttachmentFor;
import net.iGap.proto.ProtoGlobal;


import java.io.File;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class RealmAttachment extends RealmObject {
    // should be message id for message attachment and user id for avatar
    @PrimaryKey
    public long id;

    @Index
    public String token;
    public String url;
    public String name;
    public long size;
    public int width;
    public int height;
    public double duration;

    @Index
    public String cacheId;
    public RealmThumbnail largeThumbnail;
    public RealmThumbnail smallThumbnail;
    @Nullable
    public String localThumbnailPath;
    @Nullable
    public String localFilePath;
    public String mimeType;

    public static void updateToken(long fakeId, String token) {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmAttachment attachment = realm.where(RealmAttachment.class).equalTo("id", fakeId).findFirst();
            if (attachment != null) {
                attachment.setToken(token);
            }
        });
    }

    public static void updateFileSize(final long messageId, final long fileSize) {
        new Thread(() -> {
            DbManager.getInstance().doRealmTransaction(realm1 -> {
                RealmAttachment attachment = realm1.where(RealmAttachment.class).equalTo("id", messageId).findFirst();
                if (attachment != null) {
                    attachment.setSize(fileSize);
                }
            });
        }).start();

    }

    public static RealmAttachment putOrUpdate(Realm realm, long messageId, RealmAttachment realmAttachment, ProtoGlobal.File attachment) {
        if (realmAttachment == null) {
            realmAttachment = realm.createObject(RealmAttachment.class, messageId);
        }
        realmAttachment.setCacheId(attachment.getCacheId());
        realmAttachment.setDuration(attachment.getDuration());
        realmAttachment.setHeight(attachment.getHeight());
        realmAttachment.setName(attachment.getName());
        realmAttachment.setSize(attachment.getSize());
        realmAttachment.setToken(attachment.getToken());
        realmAttachment.setUrl(attachment.getPublicUrl());
        realmAttachment.setWidth(attachment.getWidth());
        realmAttachment.setMimeType(attachment.getMime());

        long smallMessageThumbnail = SUID.id().get();
        RealmThumbnail.put(realm, smallMessageThumbnail, messageId, attachment.getSmallThumbnail());

        long largeMessageThumbnail = SUID.id().get();
        RealmThumbnail.put(realm, largeMessageThumbnail, messageId, attachment.getSmallThumbnail());

        realmAttachment.setSmallThumbnail(realm.where(RealmThumbnail.class).equalTo("id", smallMessageThumbnail).findFirst());
        realmAttachment.setLargeThumbnail(realm.where(RealmThumbnail.class).equalTo("id", largeMessageThumbnail).findFirst());

        return realmAttachment;
    }

    public static RealmAttachment build(Realm realm, ProtoGlobal.File file, AttachmentFor attachmentFor, @Nullable ProtoGlobal.RoomMessageType messageType) {

        RealmAttachment realmAttachment = realm.where(RealmAttachment.class).equalTo("token", file.getToken()).findFirst();
        if (realmAttachment == null) {
            long id = SUID.id().get();
            realmAttachment = realm.createObject(RealmAttachment.class, id);

            realmAttachment.setCacheId(file.getCacheId());
            realmAttachment.setDuration(file.getDuration());
            realmAttachment.setHeight(file.getHeight());
            realmAttachment.setMimeType(file.getMime());

            long largeId = SUID.id().get();
            RealmThumbnail.put(realm, largeId, id, file.getLargeThumbnail());
            long smallId = SUID.id().get();
            RealmThumbnail.put(realm, smallId, id, file.getSmallThumbnail());

            RealmThumbnail largeThumbnail = realm.where(RealmThumbnail.class).equalTo("id", largeId).findFirst();
            realmAttachment.setLargeThumbnail(largeThumbnail);
            RealmThumbnail smallThumbnail = realm.where(RealmThumbnail.class).equalTo("id", smallId).findFirst();
            realmAttachment.setSmallThumbnail(smallThumbnail);

            String tempFilePath = "";
            String filePath = "";
            switch (attachmentFor) {
                case MESSAGE_ATTACHMENT:
                    filePath = AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), messageType);
                    tempFilePath = AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), G.DIR_TEMP, true);
                    break;
                case AVATAR:
                    filePath = AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), G.DIR_IMAGE_USER, false);
                    tempFilePath = AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), G.DIR_IMAGE_USER, true);
                    break;
            }

            realmAttachment.setLocalFilePath(new File(filePath).exists() ? filePath : null);
            realmAttachment.setLocalThumbnailPath(new File(tempFilePath).exists() ? tempFilePath : null);
            realmAttachment.setName(file.getName());
            realmAttachment.setSize(file.getSize());
            realmAttachment.setToken(file.getToken());
            realmAttachment.setUrl(file.getPublicUrl());
            realmAttachment.setWidth(file.getWidth());
        } else {

            if (realmAttachment.height != file.getHeight()) {
                realmAttachment.setHeight(file.getHeight());
            }

            if (realmAttachment.width != file.getWidth()) {
                realmAttachment.setWidth(file.getWidth());
            }

            if (realmAttachment.smallThumbnail != null) {
                realmAttachment.smallThumbnail.setSize(file.getSmallThumbnail().getSize());
            }
            if (realmAttachment.largeThumbnail != null) {
                realmAttachment.largeThumbnail.setSize(file.getLargeThumbnail().getSize());
            }

            if (realmAttachment.size != file.getSize()) {
                realmAttachment.setSize(file.getSize());
            }

            if (realmAttachment.mimeType == null || !realmAttachment.mimeType.equals(file.getMime())) {
                realmAttachment.setMimeType(file.getMime());
            }

            String _filePath = realmAttachment.getLocalFilePath();

            String _Dir = "";

            if (_filePath != null && _filePath.length() > 0) {
                String _defaultFilePAth = "";
                switch (attachmentFor) {
                    case MESSAGE_ATTACHMENT:
                        _defaultFilePAth = AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), messageType);
                        _Dir = AndroidUtils.suitableAppFilePath(messageType);
                        break;
                    case AVATAR:
                        _defaultFilePAth = AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), G.DIR_IMAGE_USER, false);

                        if (realmAttachment.getLocalThumbnailPath() == null) {
                            realmAttachment.setLocalThumbnailPath(AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), G.DIR_IMAGE_USER, true));
                        }
                        _Dir = G.DIR_IMAGE_USER;

                        break;
                }

                if (!_filePath.equals(_defaultFilePAth)) {
                    try {
                        File _File1 = new File(_filePath);
                        if (_File1.exists()) {

                            if (_filePath.contains(_Dir)) {
                                _File1.renameTo(new File(_defaultFilePAth));
                                realmAttachment.setLocalFilePath(_defaultFilePAth);
                            } else {
                                AndroidUtils.copyFile(_File1, new File(_defaultFilePAth),0,null);
                                realmAttachment.setLocalFilePath(_defaultFilePAth);

                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return realmAttachment;
    }

    public static void setThumbnailPathDataBaseAttachment(final String cashID, final String path) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmResults<RealmAttachment> attachments = realm.where(RealmAttachment.class).equalTo("cacheId", cashID).findAll();
            for (RealmAttachment attachment : attachments) {
                attachment.setLocalThumbnailPath(path);
            }
        });
    }

    public static void setFilePAthToDataBaseAttachment(final String cashID, final String path) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmResults<RealmAttachment> attachments = realm.where(RealmAttachment.class).equalTo("cacheId", cashID).findAll();

            for (RealmAttachment attachment : attachments) {
                attachment.setLocalFilePath(path);
            }
        });
    }

    public RealmThumbnail getLargeThumbnail() {
        return largeThumbnail;
    }

    public void setLargeThumbnail(RealmThumbnail largeThumbnail) {
        if (largeThumbnail != null && this.largeThumbnail != null && this.largeThumbnail.getCacheId() != null && this.largeThumbnail.getCacheId().equals(largeThumbnail.getCacheId()))
            return;

        this.largeThumbnail = largeThumbnail;
    }

    public RealmThumbnail getSmallThumbnail() {
        return smallThumbnail;
    }

    public void setSmallThumbnail(RealmThumbnail smallThumbnail) {
        if (smallThumbnail != null && this.smallThumbnail != null && this.smallThumbnail.getCacheId() != null && this.smallThumbnail.getCacheId().equals(smallThumbnail.getCacheId()))
            return;
        this.smallThumbnail = smallThumbnail;
    }

    @Nullable
    public String getLocalThumbnailPath() {
        return localThumbnailPath;
    }

    public void setLocalThumbnailPath(@Nullable String localThumbnailPath) {
        if (this.localThumbnailPath != null && this.localThumbnailPath.equals(localThumbnailPath))
            return;
        this.localThumbnailPath = localThumbnailPath;
    }

    public boolean thumbnailExistsOnLocal() {
        return localThumbnailPath != null && new File(localThumbnailPath).exists();
    }

    public boolean fileExistsOnLocal() {
        return localFilePath != null && new File(localFilePath).exists();
    }

    @Nullable
    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(@Nullable String path) {
        if (localFilePath == null)
            localFilePath = path;
        else if (localFilePath.equals(path))
            return;
        localFilePath = path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        if (this.token != null && this.token.equals(token))
            return;

        this.token = token;
    }

    public void setMimeType(String mimeType) {
        if (this.mimeType != null && this.mimeType.equals(mimeType))
            return;
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if (this.url != null && this.url.equals(url))
            return;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.name != null && this.name.equals(name))
            return;

        try {
            this.name = name;
        } catch (Exception e) {
            this.name = HelperString.getUtf8String(name);
        }
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        if (this.size != size) {
            this.size = size;
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        if (this.width != width) {
            this.width = width;
        }
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        if (this.height != height) {
            this.height = height;
        }
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        if (this.duration != duration) {
            this.duration = duration;
        }
    }

    public String getCacheId() {
        return cacheId;
    }

    public void setCacheId(String cacheId) {
        if (this.cacheId != null && this.cacheId.equals(cacheId))
            return;

        this.cacheId = cacheId;
    }

    /**
     * check file exist and also user can read this file (permission granted)
     */
    public boolean isFileExistsOnLocal() {
        return localFilePath != null && new File(localFilePath).exists() && new File(localFilePath).canRead();
    }

    public boolean isFileExistsOnLocalAndIsImage() {
        assert localFilePath != null;
        return isFileExistsOnLocal() && HelperMimeType.isFileImage(localFilePath.toLowerCase());
    }

    public boolean isAnimatedSticker() {
        return name != null && HelperMimeType.isFileJson(name);
    }

    /**
     * check file thumbnail exist and also user can read this file (permission granted)
     */
    public boolean isThumbnailExistsOnLocal() {
        return localThumbnailPath != null && new File(localThumbnailPath).exists() && new File(localThumbnailPath).canRead();
    }
}
