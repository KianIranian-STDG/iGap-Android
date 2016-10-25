package com.iGap.interface_package;

import com.iGap.proto.ProtoFileDownload;
import com.iGap.realm.enums.RoomType;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/28/2016.
 */
public interface OnFileDownloadResponse {
    void onFileDownload(String token, int offset, ProtoFileDownload.FileDownload.Selector selector, int progress);

    void onAvatarDownload(String token, int offset, ProtoFileDownload.FileDownload.Selector selector, int progress, long userId, RoomType roomType);
}
