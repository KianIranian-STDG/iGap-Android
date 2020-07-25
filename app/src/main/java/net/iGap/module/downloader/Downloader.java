package net.iGap.module.downloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.G;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.realm.RealmRoomMessage;

public class Downloader implements IDownloader {
    private static Downloader instance;

    private IDownloader downloadThroughProto;
    private IDownloader downloadThroughApi;

    private Downloader() {
        downloadThroughApi = DownloadThroughApi.getInstance();
        downloadThroughProto = new DownloaderAdapter(HelperDownloadFile.getInstance());
    }

    public static Downloader getInstance() {
        if (instance == null) {
            synchronized (Downloader.class) {
                if (instance == null) {
                    instance = new Downloader();
                }
            }
        }
        return instance;
    }

    @Override
    public void download(@NonNull RealmRoomMessage message, @NonNull ProtoFileDownload.FileDownload.Selector selector, int priority, @Nullable Observer<Resource<Request.Progress>> observer) {
        getCurrentDownloader().download(message, selector, priority, observer);
    }

    @Override
    public void download(@NonNull RealmRoomMessage message, @NonNull ProtoFileDownload.FileDownload.Selector selector, @Nullable Observer<Resource<Request.Progress>> observer) {
        download(message, selector, Request.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull RealmRoomMessage message, @Nullable Observer<Resource<Request.Progress>> observer) {
        download(message, ProtoFileDownload.FileDownload.Selector.FILE, Request.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull RealmRoomMessage message, int priority, @Nullable Observer<Resource<Request.Progress>> observer) {
        download(message, ProtoFileDownload.FileDownload.Selector.FILE, priority, observer);
    }

    @Override
    public void cancelDownload(@NonNull String cacheId) {
        getCurrentDownloader().cancelDownload(cacheId);
    }

    @Override
    public boolean isDownloading(@NonNull String cacheId) {
        return getCurrentDownloader().isDownloading(cacheId);
    }

    // 0 => through proto
    // 1 => through api
    private IDownloader getCurrentDownloader() {
        if (G.uploadDownloadConfig == 0) {
            return downloadThroughProto;
        } else if (G.uploadDownloadConfig == 1) {
            return downloadThroughApi;
        }
        return downloadThroughProto;
    }
}
