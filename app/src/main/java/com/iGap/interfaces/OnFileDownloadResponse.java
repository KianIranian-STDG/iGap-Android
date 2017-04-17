package com.iGap.interfaces;

import com.iGap.proto.ProtoFileDownload;

public interface OnFileDownloadResponse {
    void onFileDownload(String token, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress);

    void onError(int majorCode, int minorCode, String token, ProtoFileDownload.FileDownload.Selector selector);

}
