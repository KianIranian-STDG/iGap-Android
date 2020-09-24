package net.iGap.module.downloader;

import androidx.annotation.NonNull;

import net.iGap.G;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmThumbnail;

import java.io.File;

import static net.iGap.module.AndroidUtils.suitableAppFilePath;

public class DownloadStruct extends Observable<Resource<HttpRequest.Progress>> {
    private String token;
    private long messageId;
    private long fileSize;
    private String cacheId;
    private String mime;
    private String url;
    private ProtoGlobal.RoomMessageType messageType;
    private String name;
    private File destinationFile;
    private File tempFile;
    private ProtoFileDownload.FileDownload.Selector selector;
    private int downloadId;
    private int progress = 0;
    private RealmThumbnail smallThumbnail;
    private RealmThumbnail largeThumbnail;

    public DownloadStruct(@NonNull RealmRoomMessage message) {
        message = RealmRoomMessage.getFinalMessage(message);
        if (message.getAttachment() == null)
            return;

        messageId = message.getMessageId();
        fileSize = message.getAttachment().getSize();
        cacheId = message.getAttachment().getCacheId();
        messageType = message.getMessageType();
        name = message.getAttachment().getName();
        mime = extractMime();
        token = message.getAttachment().getToken();
        url = message.getAttachment().getUrl();
        selector = ProtoFileDownload.FileDownload.Selector.FILE;
        destinationFile = generateDestinationPath();
        tempFile = generateTempPath(selector);
        largeThumbnail = message.getAttachment().getLargeThumbnail();
        smallThumbnail = message.getAttachment().getSmallThumbnail();
    }

    public static DownloadStruct getForRoom(RealmRoomMessage roomMessage) {
        return new DownloadStruct(roomMessage);
    }

    public DownloadStruct(@NonNull RealmRoomMessage message, ProtoFileDownload.FileDownload.Selector selector) {
        this(message);
        this.selector = selector;
        tempFile = generateTempPath(selector);
    }

    private String extractMime() {
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

    public String getCacheId() {
        return cacheId;
    }

    public String getMime() {
        return mime;
    }

    public ProtoGlobal.RoomMessageType getMessageType() {
        return messageType;
    }

    public String getToken() {
        return token;
    }

    public String getUrl() {
        return url;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    private File generateTempPath(ProtoFileDownload.FileDownload.Selector selector) {
        String fileName = cacheId + selector.toString();
        return new File(G.DIR_TEMP + fileName);
    }

    private File generateDestinationPath() {
        String path = suitableAppFilePath(messageType);
        return new File(path, cacheId + mime);
    }

    public String getName() {
        return name;
    }

    public File getDestinationFile() {
        return destinationFile;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public File getTempFile() {
        return tempFile;
    }

    public String getRequestId() {
        return generateRequestId(cacheId, selector);
    }

    public static String generateRequestId(String cacheId, ProtoFileDownload.FileDownload.Selector selector) {
        return cacheId + selector.toString();
    }

    public ProtoFileDownload.FileDownload.Selector getSelector() {
        return selector;
    }

    public RealmThumbnail getSmallThumbnail() {
        return smallThumbnail;
    }

    public RealmThumbnail getLargeThumbnail() {
        return largeThumbnail;
    }
}