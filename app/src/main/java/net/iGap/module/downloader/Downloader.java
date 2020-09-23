package net.iGap.module.downloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.proto.ProtoFileDownload.FileDownload.Selector;
import net.iGap.realm.RealmRoomMessage;

import java.util.HashSet;

public class Downloader implements IDownloader {
    private static volatile Downloader instance;

    private IDownloader downloadThroughProto;
    private IDownloader downloadThroughApi;
    private IDownloader downloadThroughCdn;
    private HashSet<String> publicCacheId = new HashSet<>();

    private Downloader() {
        downloadThroughApi = HttpDownloader.getInstance();
        downloadThroughProto = SocketDownloader.getInstance();
        downloadThroughCdn = CdnDownloader.getInstance();
    }

    public static Downloader getInstance() {
        Downloader localInstance = instance;
        if (localInstance == null) {
            synchronized (Downloader.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Downloader();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void download(@NonNull DownloadStruct message, @NonNull Selector selector, int priority, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        if (isPublic(message)) {
            publicCacheId.add(message.getCacheId());
        }
        getCurrentDownloader(message.getCacheId()).download(message, selector, priority, observer);
    }

    private boolean isPublic(DownloadStruct message) {
        return message.getUrl() != null && !message.getUrl().isEmpty();
    }

    private boolean validateMessage(RealmRoomMessage message) {
        message = RealmRoomMessage.getFinalMessage(message);
        return message != null && message.getAttachment() != null;
    }


    @Override
    public void download(@NonNull DownloadStruct message, @NonNull Selector selector, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        download(message, selector, HttpRequest.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull DownloadStruct message, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        download(message, Selector.FILE, HttpRequest.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull DownloadStruct message, int priority, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        download(message, Selector.FILE, priority, observer);
    }

    @Override
    public void cancelDownload(@NonNull String cacheId) {
        getCurrentDownloader(cacheId).cancelDownload(cacheId);
    }

    @Override
    public boolean isDownloading(@NonNull String cacheId) {
        return getCurrentDownloader(cacheId).isDownloading(cacheId);
    }

    // 0 => through proto
    // 1 => through api
    private IDownloader getCurrentDownloader(String cacheId) {
        if (publicCacheId.contains(cacheId))
            return downloadThroughCdn;
        else if (/*AppConfig.fileGateway == 0*/false) {
            return downloadThroughProto;
        } else if (/*AppConfig.fileGateway == 1*/ true) {
            return downloadThroughApi;
        }
        return downloadThroughProto;
    }
}
