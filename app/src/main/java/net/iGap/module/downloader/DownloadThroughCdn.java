package net.iGap.module.downloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.Status;

import net.iGap.G;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoFileDownload.FileDownload.Selector;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRoomMessage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static net.iGap.module.AndroidUtils.suitableAppFilePath;

public class DownloadThroughCdn implements IDownloader {
    private static IDownloader instance;

    private HashMap<String, PublicMessageStruct> requestedDownload = new HashMap<>();

    private DownloadThroughCdn() {
    }

    public static IDownloader getInstance() {
        if (instance == null) {
            synchronized (DownloadThroughCdn.class) {
                if (instance == null) {
                    instance = new DownloadThroughCdn();
                }
            }
        }
        return instance;
    }

    @Override
    public void download(@NonNull RealmRoomMessage message, @NonNull Selector selector, int priority,
                         @Nullable Observer<Resource<Request.Progress>> observer) {
        if (!isPublic(message)) {
            return;
        }
        PublicMessageStruct publicMessage = requestedDownload.get(PublicMessageStruct.generateRequestId(message.getAttachment().getCacheId(), selector));
        if (publicMessage != null) {
            Status status = PRDownloader.getStatus(publicMessage.getDownloadId());
            switch (status) {
                case COMPLETED:
                    requestedDownload.remove(publicMessage.getRequestId());
                    return;

                case PAUSED:
                    PRDownloader.resume(publicMessage.getDownloadId());
                    if (observer != null)
                        publicMessage.addObserver(observer);
                    return;

                case CANCELLED:
                case FAILED:
                case UNKNOWN:
                    requestedDownload.remove(publicMessage.getRequestId());
            }
        }

        publicMessage = requestedDownload.get(PublicMessageStruct.generateRequestId(message.getAttachment().getCacheId(), selector));
        if (publicMessage == null) {
            publicMessage = new PublicMessageStruct(message, selector);

            PublicMessageStruct finalPublicMessage = publicMessage;
            int downloadId = PRDownloader.download(publicMessage.getUrl(), publicMessage.getTempFile().getParent(), publicMessage.getTempFile().getName())
                    .setTag(publicMessage.getRequestId())
                    .build()
                    .setOnProgressListener(progress -> handleProgress(finalPublicMessage, progress))
                    .setOnCancelListener(() -> {
                        requestedDownload.remove(finalPublicMessage.getRequestId());
                        finalPublicMessage.removeAll();
                        handleError(finalPublicMessage, "canceled");
                    })
                    .setOnPauseListener(() -> handleError(finalPublicMessage, "paused"))
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            downloadCompleted(finalPublicMessage);
                        }

                        @Override
                        public void onError(Error error) {
                            handleError(finalPublicMessage, error.getServerErrorMessage());
                        }
                    });
            publicMessage.setDownloadId(downloadId);
            requestedDownload.put(publicMessage.getRequestId(), publicMessage);
        }
        if (observer != null)
            publicMessage.addObserver(observer);
    }

    private void handleProgress(PublicMessageStruct message, Progress progress) {
        int percent = (int) ((progress.currentBytes * 100) / progress.totalBytes);
        if (percent > message.getProgress()) {
            message.notifyObservers(Resource.loading(new Request.Progress(percent, message.getDestinationFile().getAbsolutePath())));
        }
    }

    private void handleError(PublicMessageStruct message, String error) {
        message.notifyObservers(Resource.error(error, null));
    }

    private void downloadCompleted(PublicMessageStruct message) {
        try {
            switch (message.getSelector()) {
                case FILE:
                    AndroidUtils.cutFromTemp(message.getTempFile().getAbsolutePath(), message.getDestinationFile().getAbsolutePath());
                    RealmAttachment.setFilePAthToDataBaseAttachment(message.getCacheId(), message.getDestinationFile().getAbsolutePath());
                    break;
                case SMALL_THUMBNAIL:
                case LARGE_THUMBNAIL:
                    RealmAttachment.setThumbnailPathDataBaseAttachment(message.getCacheId(), message.getTempFile().getAbsolutePath());
                    break;
            }
            message.notifyObservers(Resource.success(new Request.Progress(100, message.getSelector() == Selector.FILE ? message.getDestinationFile().getAbsolutePath() : message.getTempFile().getAbsolutePath())));
        } catch (IOException e) {
            handleError(message, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void download(@NonNull RealmRoomMessage message, @NonNull Selector selector,
                         @Nullable Observer<Resource<Request.Progress>> observer) {
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
        Selector selector = Selector.FILE;
        PublicMessageStruct message = requestedDownload.get(PublicMessageStruct.generateRequestId(cacheId, selector));
        if (message == null)
            return;

        PRDownloader.pause(message.getDownloadId());
    }

    @Override
    public boolean isDownloading(@NonNull String cacheId) {
        Selector selector = Selector.FILE;
        PublicMessageStruct message = requestedDownload.get(PublicMessageStruct.generateRequestId(cacheId, selector));
        if (message == null)
            return false;

        Status status = PRDownloader.getStatus(message.getDownloadId());
        return status == Status.RUNNING;
    }

    private boolean isPublic(@NonNull RealmRoomMessage message) {
        message = RealmRoomMessage.getFinalMessage(message);
        return message.getAttachment() != null && message.getAttachment().getUrl() != null
                && !message.getAttachment().getUrl().isEmpty();
    }

    static class PublicMessageStruct extends Observable<Resource<Request.Progress>> {
        String url;
        String name;
        String cacheId;
        String mime;
        File destinationFile;
        File tempFile;
        Selector selector;
        ProtoGlobal.RoomMessageType messageType;
        int downloadId;
        int progress = 0;

        public PublicMessageStruct(RealmRoomMessage message, Selector selector) {
            url = message.getAttachment().getUrl();
            name = message.getAttachment().getName();
            cacheId = message.getAttachment().getCacheId();
            mime = extractExtension(message);
            this.selector = selector;
            messageType = message.getMessageType();
            destinationFile = generateDestinationPath();
            tempFile = generateTempPath(selector);
        }

        private String extractExtension(RealmRoomMessage message) {
            if (message.getAttachment().getMimeType() == null)
                return ".data";
            String[] contentType = message.getAttachment().getMimeType().replace("/", "!!!").split("!!!");
            if (contentType.length == 2)
                return "." + contentType[1];
            return ".data";
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public int getProgress() {
            return progress;
        }

        private File generateTempPath(Selector selector) {
            String fileName = cacheId + selector.toString();
            return new File(G.DIR_TEMP + fileName);
        }

        private File generateDestinationPath() {
            String path = suitableAppFilePath(messageType);
            return new File(path, cacheId + mime);
        }

        public String getUrl() {
            return url;
        }

        public String getName() {
            return name;
        }

        public String getCacheId() {
            return cacheId;
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

        public static String generateRequestId(String cacheId, Selector selector) {
            return cacheId + selector.toString();
        }

        public Selector getSelector() {
            return selector;
        }
    }
}
