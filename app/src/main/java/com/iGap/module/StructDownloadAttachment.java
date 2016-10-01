package com.iGap.module;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/28/2016.
 */
public class StructDownloadAttachment {
    public String token;
    public int progress;
    public int offset;
    public int lastOffset = -1;
    public boolean thumbnailRequested;

    public StructDownloadAttachment(String token) {
        this.token = token;
    }
}
