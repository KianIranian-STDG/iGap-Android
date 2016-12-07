package com.iGap.module;

import com.iGap.G;
import com.iGap.realm.RealmAttachment;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.io.File;

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

    public StructDownloadAttachment(RealmAttachment attachment) {
        if (attachment != null) {
            String tempFilePath = G.DIR_TEMP + "/" + attachment.getToken() + "_" + attachment.getName();
            File tempFile = new File(tempFilePath);

            token = attachment.getToken();
            if (tempFile.exists()) {
                offset = tempFile.length();
                progress = (int) ((offset * 100) / attachment.getSize());
            }
        }
    }
}
