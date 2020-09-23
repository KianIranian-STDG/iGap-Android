package net.iGap.module.downloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.proto.ProtoFileDownload;

public interface IDownloader {
    void download(@NonNull DownloadStruct message, @NonNull ProtoFileDownload.FileDownload.Selector selector, int priority, @Nullable Observer<Resource<HttpRequest.Progress>> observer);

    void download(@NonNull DownloadStruct message, @NonNull ProtoFileDownload.FileDownload.Selector selector, @Nullable Observer<Resource<HttpRequest.Progress>> observer);

    void download(@NonNull DownloadStruct message, @Nullable Observer<Resource<HttpRequest.Progress>> observer);

    void download(@NonNull DownloadStruct message, int priority, @Nullable Observer<Resource<HttpRequest.Progress>> observer);

    void cancelDownload(@NonNull String cacheId);

    boolean isDownloading(@NonNull String cacheId);
}
