package com.iGap.module;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/28/2016.
 */
public class StructDownloadAttachment implements Parcelable {
    public String token;
    public int progress;
    public int offset;
    public int lastOffset = -1;
    public boolean thumbnailRequested;

    public StructDownloadAttachment(String token) {
        this.token = token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeInt(this.progress);
        dest.writeInt(this.offset);
        dest.writeInt(this.lastOffset);
        dest.writeByte(this.thumbnailRequested ? (byte) 1 : (byte) 0);
    }

    protected StructDownloadAttachment(Parcel in) {
        this.token = in.readString();
        this.progress = in.readInt();
        this.offset = in.readInt();
        this.lastOffset = in.readInt();
        this.thumbnailRequested = in.readByte() != 0;
    }

    public static final Parcelable.Creator<StructDownloadAttachment> CREATOR = new Parcelable.Creator<StructDownloadAttachment>() {
        @Override
        public StructDownloadAttachment createFromParcel(Parcel source) {
            return new StructDownloadAttachment(source);
        }

        @Override
        public StructDownloadAttachment[] newArray(int size) {
            return new StructDownloadAttachment[size];
        }
    };
}
