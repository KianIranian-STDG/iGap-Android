package com.iGap.module;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/28/2016.
 */
@Parcel
public class StructDownloadAttachment {
    public String token;
    public int progress;
    public long offset;
    public long lastOffset = -1;
    public boolean thumbnailRequested;

    @ParcelConstructor
    public StructDownloadAttachment(String token) {
        this.token = token;
    }
}
