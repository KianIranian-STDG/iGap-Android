package net.iGap.module.downloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.Status;

import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoFileDownload.FileDownload.Selector;
import net.iGap.realm.RealmAttachment;

import java.io.IOException;
import java.util.HashMap;

public class DownloadThroughCdn implements IDownloader {
    private static IDownloader instance;

    private HashMap<String, DownloadStruct> requestedDownload = new HashMap<>();

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
    public void download(@NonNull DownloadStruct message, @NonNull Selector selector, int priority,
                         @Nullable Observer<Resource<Request.Progress>> observer) {
        if (!isPublic(message)) {
            return;
        }
        DownloadStruct publicMessage = requestedDownload.get(DownloadStruct.generateRequestId(message.getCacheId(), selector));
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

        publicMessage = requestedDownload.get(DownloadStruct.generateRequestId(message.getCacheId(), selector));
        if (publicMessage == null) {
            publicMessage = message;

            final DownloadStruct finalPublicMessage = publicMessage;
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

    private void handleProgress(DownloadStruct message, Progress progress) {
        int percent = (int) ((progress.currentBytes * 100) / progress.totalBytes);
        if (percent > message.getProgress()) {
            message.notifyObservers(Resource.loading(new Request.Progress(percent, message.getDestinationFile().getAbsolutePath())));
        }
    }

    private void handleError(DownloadStruct message, String error) {
        message.notifyObservers(Resource.error(error, null));
    }

    private void downloadCompleted(DownloadStruct message) {
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
    public void download(@NonNull DownloadStruct message, @NonNull Selector selector,
                         @Nullable Observer<Resource<Request.Progress>> observer) {
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
        Selector selector = Selector.FILE;
        DownloadStruct message = requestedDownload.get(DownloadStruct.generateRequestId(cacheId, selector));
        if (message == null)
            return;

        PRDownloader.pause(message.getDownloadId());
    }

    @Override
    public boolean isDownloading(@NonNull String cacheId) {
        Selector selector = Selector.FILE;
        DownloadStruct message = requestedDownload.get(DownloadStruct.generateRequestId(cacheId, selector));
        if (message == null)
            return false;

        Status status = PRDownloader.getStatus(message.getDownloadId());
        return status == Status.RUNNING;
    }

    private boolean isPublic(@NonNull DownloadStruct message) {
        return message.getUrl() == null || !message.getUrl().isEmpty();
    }
}
