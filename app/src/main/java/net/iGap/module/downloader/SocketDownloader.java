package net.iGap.module.downloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.helper.HelperDownloadFile;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;

import java.io.File;

class SocketDownloader implements IDownloader {
    private static SocketDownloader instance;
    private HelperDownloadFile oldDownloader;

    public static SocketDownloader getInstance() {
        SocketDownloader localInstance = instance;
        if (localInstance == null) {
            synchronized (SocketDownloader.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SocketDownloader();
                }
            }
        }
        return localInstance;
    }

    private SocketDownloader() {
        oldDownloader = HelperDownloadFile.getInstance();
    }

    @Override
    public void download(@NonNull DownloadStruct message, @NonNull ProtoFileDownload.FileDownload.Selector selector, int priority, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        long size;
        String filePath = "";
        switch (selector) {
            case SMALL_THUMBNAIL:
                size = message.getSmallThumbnail().getSize();
                break;

            case LARGE_THUMBNAIL:
                size = message.getLargeThumbnail().getSize();
                break;

            default:
                size = message.getFileSize();
                filePath = generateDownloadFileForRequest(message.getCacheId(), message.getName(), message.getMessageType()).getAbsolutePath();
        }
        oldDownloader.startDownload(message.getMessageType(),
                String.valueOf(message.getMessageId()), message.getToken(),
                message.getUrl(), message.getCacheId(),
                message.getName(), size, selector, filePath,
                priority, new HelperDownloadFile.UpdateListener() {
                    @Override
                    public void OnProgress(String path, int progress) {
                        if (observer == null)
                            return;

                        if (progress < 100 && progress >= 0) {
                            observer.onUpdate(Resource.loading(new HttpRequest.Progress(progress, path)));
                        } else if (progress == 100) {
                            observer.onUpdate(Resource.success(new HttpRequest.Progress(progress, path)));
                        }
                    }

                    @Override
                    public void OnError(String token) {
                        if (observer != null)
                            observer.onUpdate(Resource.error(token, null));
                    }
                });
    }

    @Override
    public void download(@NonNull DownloadStruct message, @NonNull ProtoFileDownload.FileDownload.Selector selector,
                         @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        download(message, selector, HttpRequest.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull DownloadStruct message, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        download(message, ProtoFileDownload.FileDownload.Selector.FILE, HttpRequest.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull DownloadStruct message, int priority, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        download(message, ProtoFileDownload.FileDownload.Selector.FILE, priority, observer);
    }

    @Override
    public void cancelDownload(@NonNull String cacheId) {
        oldDownloader.stopDownLoad(cacheId);
    }

    @Override
    public boolean isDownloading(@NonNull String cacheId) {
        return oldDownloader.isDownLoading(cacheId);
    }

    private File generateDownloadFileForRequest(String cacheId, String name, ProtoGlobal.RoomMessageType messageType) {
        return new File(AndroidUtils.getFilePathWithCashId(cacheId, name, messageType));
    }
}
