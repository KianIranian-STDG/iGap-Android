package net.iGap.module.downloader;

public interface IProgress {
    void onProgress(int progress);

    void onDownloadCompleted();

    void onError(Throwable throwable);
}