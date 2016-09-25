package com.iGap.module;

import com.iGap.proto.ProtoGlobal;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/4/2016.
 */
public class StructContactInfo {
    public long peerId;
    public boolean isHeader;
    public String displayName;
    public String status;
    public boolean isSelected;
    public String phone;
    public String role = ProtoGlobal.GroupRoom.Role.MEMBER.toString();

    public StructContactInfo(long peerId, String displayName, String status, boolean isHeader, boolean isSelected, String phone) {
        this.peerId = peerId;
        this.isHeader = isHeader;
        this.displayName = displayName;
        this.status = status;
        this.isSelected = isSelected;
        this.phone = phone;
    }
}
