package net.iGap.module.downloader;

interface IProgress {
    void onProgress(int progress);

    void onDownloadCompleted();

    void onError(Throwable throwable);
}