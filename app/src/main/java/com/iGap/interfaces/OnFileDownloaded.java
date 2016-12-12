package com.iGap.interfaces;

import com.iGap.proto.ProtoFileDownload;

/**
 * just implement this interface in HelperAvatar
 */

public interface OnFileDownloaded {
    void onFileDownload(String token, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress);

    void onError();
}
