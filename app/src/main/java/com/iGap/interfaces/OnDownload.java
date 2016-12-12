package com.iGap.interfaces;

public interface OnDownload {
    void onDownload(String filePath, String token);

    void onError();
}
