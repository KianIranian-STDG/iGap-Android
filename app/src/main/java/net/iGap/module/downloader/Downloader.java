package net.iGap.module.downloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.G;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.proto.ProtoFileDownload.FileDownload.Selector;
import net.iGap.realm.RealmRoomMessage;

import java.util.HashSet;

public class Downloader implements IDownloader {
    private static Downloader instance;

    private IDownloader downloadThroughProto;
    private IDownloader downloadThroughApi;
    private IDownloader downloadThroughCdn;
    private HashSet<String> publicCacheId = new HashSet<>();

    private Downloader() {
        downloadThroughApi = DownloadThroughApi.getInstance();
        downloadThroughProto = new DownloaderAdapter(HelperDownloadFile.getInstance());
        downloadThroughCdn = DownloadThroughCdn.getInstance();
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
    public void download(@NonNull DownloadStruct message, @NonNull Selector selector, int priority, @Nullable Observer<Resource<Request.Progress>> observer) {
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
    public void download(@NonNull DownloadStruct message, @NonNull Selector selector, @Nullable Observer<Resource<Request.Progress>> observer) {
        download(message, selector, Request.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull DownloadStruct message, @Nullable Observer<Resource<Request.Progress>> observer) {
        download(message, Selector.FILE, Request.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull DownloadStruct message, int priority, @Nullable Observer<Resource<Request.Progress>> observer) {
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
        else if (G.uploadDownloadConfig == 0) {
            return downloadThroughProto;
        } else if (G.uploadDownloadConfig == 1) {
            return downloadThroughApi;
        }
        return downloadThroughProto;
    }
}
