package com.iGap.interfaces;

import com.iGap.proto.ProtoFileDownload;
import com.iGap.realm.enums.RoomType;

public interface OnFileDownloadResponse {
    void onFileDownload(String token, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress);

    void onAvatarDownload(String token, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress, long userId, RoomType roomType);

    void onError(int majorCode, int minorCode, String token, ProtoFileDownload.FileDownload.Selector selector);

}
