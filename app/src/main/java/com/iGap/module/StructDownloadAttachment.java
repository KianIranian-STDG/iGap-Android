package com.iGap.module;

import com.iGap.G;
import com.iGap.realm.RealmAttachment;
import java.io.File;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel public class StructDownloadAttachment {
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
                progress = (int) Math.ceil((offset * 100) / attachment.getSize());
            }
        }
    }
}
