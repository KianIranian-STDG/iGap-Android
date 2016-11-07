package com.iGap.module;

import android.os.Parcel;
import android.os.Parcelable;

import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmThumbnail;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/29/2016.
 */
public class StructMessageThumbnail implements Parcelable {
    public static final Parcelable.Creator<StructMessageThumbnail> CREATOR =
            new Parcelable.Creator<StructMessageThumbnail>() {
                @Override
                public StructMessageThumbnail createFromParcel(Parcel source) {
                    return new StructMessageThumbnail(source);
                }

                @Override
                public StructMessageThumbnail[] newArray(int size) {
                    return new StructMessageThumbnail[size];
                }
            };
    public long size;
    public int width;
    public int height;
    public String cacheId;

    public StructMessageThumbnail() {
    }

    public StructMessageThumbnail(long size, int width, int height, String cacheId) {
        this.size = size;
        this.width = width;
        this.height = height;
        this.cacheId = cacheId;
    }

    protected StructMessageThumbnail(Parcel in) {
        this.size = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.cacheId = in.readString();
    }

    public static StructMessageThumbnail convert(ProtoGlobal.Thumbnail thumbnail) {
        return new StructMessageThumbnail(thumbnail.getSize(), thumbnail.getWidth(),
                thumbnail.getHeight(), thumbnail.getCacheId());
    }

    public static StructMessageThumbnail convert(RealmThumbnail thumbnail) {
        if (thumbnail == null) {
            return new StructMessageThumbnail();
        }
        return new StructMessageThumbnail(thumbnail.getSize(), thumbnail.getWidth(),
                thumbnail.getHeight(), thumbnail.getCacheId());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.size);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.cacheId);
    }
}
