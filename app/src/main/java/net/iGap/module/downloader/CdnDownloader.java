package net.iGap.module.downloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.Status;

import net.iGap.controllers.BaseController;
import net.iGap.controllers.MessageDataStorage;
import net.iGap.module.AndroidUtils;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.proto.ProtoFileDownload.FileDownload.Selector;

import java.io.IOException;
import java.util.HashMap;

import static net.iGap.proto.ProtoFileDownload.FileDownload.Selector.FILE_VALUE;
import static net.iGap.proto.ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL_VALUE;
import static net.iGap.proto.ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL_VALUE;

public class CdnDownloader extends BaseController implements IDownloader {
    private static CdnDownloader[] instance = new CdnDownloader[AccountManager.MAX_ACCOUNT_COUNT];
    private HashMap<String, DownloadObject> requestedDownload = new HashMap<>();

    private CdnDownloader(int account) {
        super(account);
    }

    public static CdnDownloader getInstance(int account) {
        CdnDownloader localInstance = instance[account];
        if (localInstance == null) {
            synchronized (CdnDownloader.class) {
                localInstance = instance[account];
                if (localInstance == null) {
                    instance[account] = localInstance = new CdnDownloader(account);
                }
            }
        }
        return localInstance;
    }

    @Override
    public void download(@NonNull DownloadObject file, @NonNull Selector selector, int priority, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        if (!file.isPublic()) {
            return;
        }
        DownloadObject publicMessage = requestedDownload.get(file.key);
        if (publicMessage != null) {
            Status status = PRDownloader.getStatus(publicMessage.downloadId);
            switch (status) {
                case COMPLETED:
                    requestedDownload.remove(publicMessage.key);
                    return;
                case PAUSED:
                    PRDownloader.resume(publicMessage.downloadId);
                    if (observer != null)
                        publicMessage.addObserver(observer);
                    return;
                case CANCELLED:
                case FAILED:
                case UNKNOWN:
                    requestedDownload.remove(publicMessage.key);
                    break;
            }
        }

        publicMessage = requestedDownload.get(file.key);
        if (publicMessage == null) {
            publicMessage = file;

            final DownloadObject finalPublicMessage = publicMessage;
            publicMessage.downloadId = PRDownloader.download(publicMessage.publicUrl, publicMessage.tempFile.getParent(), publicMessage.tempFile.getName())
                    .setTag(publicMessage.key)
                    .build()
                    .setOnProgressListener(progress -> handleProgress(finalPublicMessage, progress))
                    .setOnCancelListener(() -> {
                        requestedDownload.remove(finalPublicMessage.key);
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
            requestedDownload.put(publicMessage.key, publicMessage);
        }
        if (observer != null)
            publicMessage.addObserver(observer);
    }

    private void handleProgress(DownloadObject message, Progress progress) {
        int percent = (int) ((progress.currentBytes * 100) / progress.totalBytes);
        if (percent > message.progress) {
            message.notifyObservers(Resource.loading(new HttpRequest.Progress(percent, message.destFile.getAbsolutePath())));
        }
    }

    private void handleError(DownloadObject message, String error) {
        message.notifyObservers(Resource.error(error, null));
    }

    private void downloadCompleted(DownloadObject file) {
        MessageDataStorage storage = MessageDataStorage.getInstance(currentAccount);
        String path = null;
        try {
            if (file.selector == FILE_VALUE) {
                AndroidUtils.cutFromTemp(file.tempFile.getAbsolutePath(), file.destFile.getAbsolutePath());
                storage.setAttachmentFilePath(file.mainCacheId, path = file.destFile.getAbsolutePath(), false);
            } else if (file.selector == SMALL_THUMBNAIL_VALUE || file.selector == LARGE_THUMBNAIL_VALUE) {
                storage.setAttachmentFilePath(file.mainCacheId, path = file.tempFile.getAbsolutePath(), true);
            }

            file.notifyObservers(Resource.success(new HttpRequest.Progress(100, path)));
        } catch (IOException e) {
            handleError(file, e.getMessage());
        }
    }

    @Override
    public void download(@NonNull DownloadObject file, @NonNull Selector selector, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        download(file, selector, HttpRequest.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull DownloadObject file, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        download(file, Selector.FILE, HttpRequest.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull DownloadObject file, int priority, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        download(file, Selector.FILE, priority, observer);
    }

    @Override
    public void cancelDownload(@NonNull String cacheId) {
        String key = DownloadObject.createKey(cacheId, FILE_VALUE);
        DownloadObject message = requestedDownload.get(key);
        if (message == null)
            return;

        PRDownloader.pause(message.downloadId);
    }

    @Override
    public boolean isDownloading(@NonNull String cacheId) {
        String key = DownloadObject.createKey(cacheId, FILE_VALUE);
        DownloadObject message = requestedDownload.get(key);
        if (message == null)
            return false;

        Status status = PRDownloader.getStatus(message.downloadId);
        return status == Status.RUNNING;
    }
}
