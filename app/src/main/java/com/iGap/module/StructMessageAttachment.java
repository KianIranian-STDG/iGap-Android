package com.iGap.module;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmMessageAttachment;

import java.io.File;

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
    public String localPath;

    public StructMessageAttachment(String token, String name, long size, int width, int height, double duration, String localPath) {
        this.token = token;
        this.name = name;
        this.size = size;
        this.width = width;
        this.height = height;
        this.duration = duration;
        this.localPath = localPath;
    }

    public boolean existsOnLocal() {
        return localPath != null && new File(localPath).exists();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static StructMessageAttachment convert(ProtoGlobal.File attachment) {
        if (attachment == null) {
            return new StructMessageAttachment();
        }
        return new StructMessageAttachment(attachment.getToken(), attachment.getName(), attachment.getSize(), attachment.getWidth(), attachment.getHeight(), attachment.getDuration(), null);
    }

    public static StructMessageAttachment convert(RealmMessageAttachment attachment) {
        if (attachment == null) {
            return new StructMessageAttachment();
        }
        return new StructMessageAttachment(attachment.getToken(), attachment.getName(), attachment.getSize(), attachment.getWidth(), attachment.getHeight(), attachment.getDuration(), attachment.getLocalPath());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeString(this.name);
        dest.writeLong(this.size);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeDouble(this.duration);
        dest.writeString(this.localPath);
    }

    public StructMessageAttachment() {
    }

    protected StructMessageAttachment(Parcel in) {
        this.token = in.readString();
        this.name = in.readString();
        this.size = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.duration = in.readDouble();
        this.localPath = in.readString();
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
