package com.iGap.module;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/4/2016.
 */
public class StructContactInfo {
    public long peerId;
    public int sectionManager;
    public int sectionFirstPosition;
    public boolean isHeader;
    public String text;
    public String Status;

    public StructContactInfo(long peerId, String text, String status, boolean isHeader, int sectionManager, int sectionFirstPosition) {
        this.peerId = peerId;
        this.isHeader = isHeader;
        this.text = text;
        this.Status = status;
        this.sectionManager = sectionManager;
        this.sectionFirstPosition = sectionFirstPosition;
    }
}
