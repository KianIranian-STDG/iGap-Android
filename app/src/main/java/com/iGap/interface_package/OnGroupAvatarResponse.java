package com.iGap.interface_package;

import com.iGap.proto.ProtoGlobal;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/8/2016.
 */
public interface OnGroupAvatarResponse {
    void onAvatarAdd(long roomId, ProtoGlobal.Avatar avatar);
}
