package com.iGap.module;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmMessageAttachment;

import java.io.File;

import io.realm.Realm;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/28/2016.
 */
public class StructMessageAttachment implements Parcelable {
    public String token;
    public String name;
    public long size;
    public int width;
    public int height;
    public double duration;
    @Nullable
    private String localThumbnailPath;
    @Nullable
    private String localFilePath;
    public StructMessageThumbnail largeThumbnail;
    public StructMessageThumbnail smallThumbnail;

    @Nullable
    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(final long messageId, @Nullable final String path) {
        this.localFilePath = path;
        Realm realm = Realm.getDefaultInstance();
        final RealmMessageAttachment realmMessageAttachment = realm.where(RealmMessageAttachment.class).equalTo("messageId", messageId).findFirst();
        if (realmMessageAttachment == null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmMessageAttachment messageAttachment = realm.createObject(RealmMessageAttachment.class);
                    messageAttachment.setLocalFilePath(path);
                    messageAttachment.setMessageId(messageId);
                }
            });
        } else {
            if (realmMessageAttachment.getLocalFilePath() == null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realmMessageAttachment.setLocalFilePath(path);
                    }
                });
            }
        }
        realm.close();
    }

    @Nullable
    public String getLocalThumbnailPath() {
        return localThumbnailPath;
    }

    public void setLocalThumbnailPath(final long messageId, @Nullable final String localPath) {
        this.localThumbnailPath = localPath;
        Realm realm = Realm.getDefaultInstance();
        final RealmMessageAttachment realmMessageAttachment = realm.where(RealmMessageAttachment.class).equalTo("messageId", messageId).findFirst();
        if (realmMessageAttachment == null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmMessageAttachment messageAttachment = realm.createObject(RealmMessageAttachment.class);
                    messageAttachment.setLocalThumbnailPath(localPath);
                    messageAttachment.setMessageId(messageId);
                }
            });
        } else {
            if (realmMessageAttachment.getLocalThumbnailPath() == null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realmMessageAttachment.setLocalThumbnailPath(localPath);
                    }
                });
            }
        }
        realm.close();
    }

    public StructMessageAttachment(String token, String name, long size, int width, int height, double duration, @Nullable String localThumbnailPath, @Nullable String localFilePath, StructMessageThumbnail largeThumbnail, StructMessageThumbnail smallThumbnail) {
        this.token = token;
        this.name = name;
        this.size = size;
        this.width = width;
        this.height = height;
        this.duration = duration;
        this.localThumbnailPath = localThumbnailPath;
        this.localFilePath = localFilePath;
        this.largeThumbnail = largeThumbnail;
        this.smallThumbnail = smallThumbnail;
    }

    public boolean isFileExistsOnLocal() {
        return localFilePath != null && new File(localFilePath).exists();
    }

    public boolean isThumbnailExistsOnLocal() {
        return localThumbnailPath != null && new File(localThumbnailPath).exists();
    }

    public static StructMessageAttachment convert(ProtoGlobal.File attachment) {
        if (attachment == null) {
            return new StructMessageAttachment();
        }
        return new StructMessageAttachment(attachment.getToken(), attachment.getName(), attachment.getSize(), attachment.getWidth(), attachment.getHeight(), attachment.getDuration(), null, null, StructMessageThumbnail.convert(attachment.getLargeThumbnail()), StructMessageThumbnail.convert(attachment.getSmallThumbnail()));
    }

    public static StructMessageAttachment convert(RealmMessageAttachment attachment) {
        if (attachment == null) {
            return new StructMessageAttachment();
        }
        return new StructMessageAttachment(attachment.getToken(), attachment.getName(), attachment.getSize(), attachment.getWidth(), attachment.getHeight(), attachment.getDuration(), attachment.getLocalThumbnailPath(), attachment.getLocalFilePath(), StructMessageThumbnail.convert(attachment.getLargeThumbnail()), StructMessageThumbnail.convert(attachment.getSmallThumbnail()));
    }

    public StructMessageAttachment() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeString(this.name);
        dest.writeLong(this.size);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeDouble(this.duration);
        dest.writeString(this.localThumbnailPath);
        dest.writeParcelable(this.largeThumbnail, flags);
        dest.writeParcelable(this.smallThumbnail, flags);
    }

    protected StructMessageAttachment(Parcel in) {
        this.token = in.readString();
        this.name = in.readString();
        this.size = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.duration = in.readDouble();
        this.localThumbnailPath = in.readString();
        this.largeThumbnail = in.readParcelable(StructMessageThumbnail.class.getClassLoader());
        this.smallThumbnail = in.readParcelable(StructMessageThumbnail.class.getClassLoader());
    }

    public static final Parcelable.Creator<StructMessageAttachment> CREATOR = new Parcelable.Creator<StructMessageAttachment>() {
        @Override
        public StructMessageAttachment createFromParcel(Parcel source) {
            return new StructMessageAttachment(source);
        }

        @Override
        public StructMessageAttachment[] newArray(int size) {
            return new StructMessageAttachment[size];
        }
    };
}
