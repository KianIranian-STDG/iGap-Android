package net.iGap.module.downloader;

import net.iGap.G;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmThumbnail;

import java.io.File;
import java.util.Locale;

import static net.iGap.module.AndroidUtils.suitableAppFilePath;
import static net.iGap.proto.ProtoFileDownload.FileDownload.Selector.FILE_VALUE;
import static net.iGap.proto.ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL_VALUE;
import static net.iGap.proto.ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL_VALUE;

public class DownloadObject extends Observable<Resource<HttpRequest.Progress>> {
    public String key;
    public String fileToken;
    public String mainCacheId;
    public String thumbCacheId;
    public String mimeType;
    public String publicUrl;
    public String fileName;
    public File tempFile;
    public File destFile;
    public long messageId;
    public long fileSize;
    public long offset;
    public int selector;
    public int progress;
    public int priority;
    public ProtoGlobal.RoomMessageType messageType;
    public int downloadId;

    private DownloadObject() {
    }

    public static DownloadObject createForThumb(RealmRoomMessage message, boolean big) {
        final RealmRoomMessage finalMessage = RealmRoomMessage.getFinalMessage(message);

        if (finalMessage == null || finalMessage.attachment == null) {
            return null;
        }

        final RealmThumbnail thumbnail = big ? finalMessage.attachment.largeThumbnail : finalMessage.attachment.smallThumbnail;

        if (thumbnail == null || (thumbnail.cacheId == null || thumbnail.cacheId.isEmpty())) {
            return null;
        }

        DownloadObject struct = new DownloadObject();
        struct.selector = big ? LARGE_THUMBNAIL_VALUE : SMALL_THUMBNAIL_VALUE;
        struct.key = createKey(thumbnail.cacheId, struct.selector);
        struct.thumbCacheId = thumbnail.cacheId;
        struct.mainCacheId = finalMessage.attachment.cacheId;
        struct.fileToken = finalMessage.attachment.token;
        struct.fileName = finalMessage.attachment.name;
        struct.fileSize = big ? finalMessage.attachment.largeThumbnail.size : finalMessage.attachment.smallThumbnail.size;
        struct.mimeType = struct.extractMime(struct.fileName);
        struct.publicUrl = struct.getPublicUrl(finalMessage.attachment.url);
        struct.priority = HttpRequest.PRIORITY.PRIORITY_HIGH;

        String path = suitableAppFilePath(finalMessage.getMessageType());
        struct.destFile = new File(path + "/" + struct.thumbCacheId + "_" + struct.mimeType);
        struct.tempFile = new File(G.DIR_TEMP + "/" + struct.key);
        struct.messageType = ProtoGlobal.RoomMessageType.valueOf(finalMessage.messageType);

        if (struct.tempFile.exists()) {
            struct.offset = struct.tempFile.length();

            if (struct.offset > 0 && struct.fileSize > 0) {
                struct.progress = (int) ((struct.offset * 100) / struct.fileSize);
            }
        }

        return struct;
    }

    public static DownloadObject createForRoomMessage(RealmRoomMessage message) {
        final RealmRoomMessage finalMessage = RealmRoomMessage.getFinalMessage(message);

        if (finalMessage == null || finalMessage.attachment == null) {
            return null;
        }

        DownloadObject struct = new DownloadObject();
        struct.selector = FILE_VALUE;
        struct.key = createKey(finalMessage.attachment.cacheId, struct.selector);
        struct.mainCacheId = finalMessage.attachment.cacheId;
        struct.fileToken = finalMessage.attachment.token;
        struct.fileName = finalMessage.attachment.name;
        struct.fileSize = finalMessage.attachment.size;
        struct.mimeType = struct.extractMime(struct.fileName);
        struct.publicUrl = struct.getPublicUrl(finalMessage.attachment.url);
        struct.priority = HttpRequest.PRIORITY.PRIORITY_MEDIUM;

        String path = suitableAppFilePath(finalMessage.getMessageType());
        struct.destFile = new File(path + "/" + struct.mainCacheId + "_" + struct.mimeType);
        struct.tempFile = new File(G.DIR_TEMP + "/" + struct.key);
        struct.messageType = ProtoGlobal.RoomMessageType.valueOf(finalMessage.messageType);

        if (struct.tempFile.exists()) {
            struct.offset = struct.tempFile.length();

            if (struct.offset > 0 && struct.fileSize > 0) {
                struct.progress = (int) ((struct.offset * 100) / struct.fileSize);
            }
        }

        return struct;
    }

    public static DownloadObject createForAvatar(RealmAttachment attachment) {

        if (attachment == null) {
            return null;
        }

        DownloadObject struct = new DownloadObject();
        struct.selector = LARGE_THUMBNAIL_VALUE;
        struct.key = createKey(attachment.cacheId, struct.selector);
        struct.mainCacheId = attachment.cacheId;
        struct.fileToken = attachment.token;
        struct.fileName = attachment.name;
        struct.fileSize = attachment.largeThumbnail.size;
        struct.mimeType = struct.extractMime(struct.fileName);
        struct.publicUrl = struct.getPublicUrl(attachment.url);
        struct.priority = HttpRequest.PRIORITY.PRIORITY_MEDIUM;

        String filePath = AndroidUtils.getFilePathWithCashId(attachment.cacheId, attachment.name, G.DIR_TEMP, true);
        struct.destFile = new File(filePath + "/" + struct.mainCacheId + "_" + struct.mimeType);
        struct.tempFile = new File(G.DIR_TEMP + "/" + struct.key);

        if (struct.tempFile.exists()) {
            struct.offset = struct.tempFile.length();

            if (struct.offset > 0 && struct.fileSize > 0) {
                struct.progress = (int) ((struct.offset * 100) / struct.fileSize);
            }
        }

        return struct;
    }

    public static String createKey(String cacheId, int selector) {
        return String.format(Locale.US, "%s_%d", cacheId, selector);
    }

    public boolean isPublic() {
        return publicUrl != null;
    }

    private String extractMime(String name) {
        String mime = ".png";
        int index = name.lastIndexOf(".");
        if (index >= 0) {
            mime = name.substring(index);
        }
        return mime;
    }

    public long getMessageId() {
        return messageId;
    }

    public long getFileSize() {
        return fileSize;
    }

    private String getPublicUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        if (selector == SMALL_THUMBNAIL_VALUE) {
            url = url + "?selector=" + 1;

        } else if (selector == LARGE_THUMBNAIL_VALUE) {
            url = url + "?selector=" + 2;
        }

        return url;
    }

}