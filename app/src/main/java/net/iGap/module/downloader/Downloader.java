package net.iGap.module.downloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.G;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.proto.ProtoFileDownload.FileDownload.Selector;
import net.iGap.proto.ProtoGlobal.RoomMessageType;
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
    public void download(@NonNull RealmRoomMessage message, @NonNull Selector selector, int priority, @Nullable Observer<Resource<Request.Progress>> observer) {
        message = RealmRoomMessage.getFinalMessage(message);
        if (isPublic(message)) {
            publicCacheId.add(message.getAttachment().getCacheId());
        }
        getCurrentDownloader(message.getAttachment().getCacheId()).download(message, selector, priority, observer);
    }

    private boolean isPublic(RealmRoomMessage message) {
        message = RealmRoomMessage.getFinalMessage(message);
        return validateMessage(message) && message.getAttachment().getUrl() != null && !message.getAttachment().getUrl().isEmpty();
    }

    private boolean validateMessage(RealmRoomMessage message) {
        message = RealmRoomMessage.getFinalMessage(message);
        return message != null && message.getAttachment() != null;
    }


    @Override
    public void download(@NonNull RealmRoomMessage message, @NonNull Selector selector, @Nullable Observer<Resource<Request.Progress>> observer) {
        download(message, selector, Request.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull RealmRoomMessage message, @Nullable Observer<Resource<Request.Progress>> observer) {
        download(message, Selector.FILE, Request.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull RealmRoomMessage message, int priority, @Nullable Observer<Resource<Request.Progress>> observer) {
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

    static class MessageStruct {
        String token;
        long messageId;
        long fileSize;
        String cacheId;
        String mime;
        RoomMessageType messageType;

        public MessageStruct(@NonNull RealmRoomMessage message) {
            message = RealmRoomMessage.getFinalMessage(message);
            if (message.getAttachment() == null)
                return;

            messageId = message.getMessageId();
            fileSize = message.getAttachment().getSize();
            cacheId = message.getAttachment().getCacheId();
            messageType = message.getMessageType();
            mime = extractMime(message);
            token = message.getAttachment().getToken();
        }

        private String extractMime(@NonNull RealmRoomMessage message) {
            if (message.getAttachment().getMimeType() == null)
                return ".data";
            String[] contentType = message.getAttachment().getMimeType().replace("/", "!!!").split("!!!");
            if (contentType.length == 2)
                return "." + contentType[1];
            return ".data";
        }

        public long getMessageId() {
            return messageId;
        }

        public long getFileSize() {
            return fileSize;
        }

        public String getCacheId() {
            return cacheId;
        }

        public String getMime() {
            return mime;
        }

        public RoomMessageType getMessageType() {
            return messageType;
        }

        public String getToken() {
            return token;
        }
    }
}
