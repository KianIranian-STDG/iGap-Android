package com.iGap.module;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/4/2016.
 */
public class StructContactInfo {
    public long peerId;
    public boolean isHeader;
    public String displayName;
    public String status;

    public StructContactInfo(long peerId, String displayName, String status, boolean isHeader) {
        this.peerId = peerId;
        this.isHeader = isHeader;
        this.displayName = displayName;
        this.status = status;
    }
}
