package net.iGap.module.downloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.helper.HelperDownloadFile;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;

import java.io.File;

import static net.iGap.module.AndroidUtils.suitableAppFilePath;

public class DownloaderAdapter implements IDownloader {
    private HelperDownloadFile oldDownloader;

    public DownloaderAdapter(HelperDownloadFile helperDownloadFile) {
        oldDownloader = helperDownloadFile;
    }

    @Override
    public void download(@NonNull RealmRoomMessage message,
                         @NonNull ProtoFileDownload.FileDownload.Selector selector,
                         int priority, @Nullable Observer<Resource<Request.Progress>> observer) {
        message = RealmRoomMessage.getFinalMessage(message);

        oldDownloader.startDownload(message.getMessageType(),
                String.valueOf(message.getMessageId()), message.getAttachment().getToken(),
                message.getAttachment().getUrl(), message.getAttachment().getCacheId(),
                message.getAttachment().getName(), message.getAttachment().getSize(), selector,
                generateDownloadFileForRequest(message.getAttachment().getCacheId(), message.getAttachment().getName(), message.getMessageType()).getAbsolutePath(),
                priority, new HelperDownloadFile.UpdateListener() {
                    @Override
                    public void OnProgress(String path, int progress) {
                        if (observer == null)
                            return;

                        if (progress < 100 && progress >= 0) {
                            observer.onUpdate(Resource.loading(new Request.Progress(progress, path)));
                        } else if (progress == 100) {
                            observer.onUpdate(Resource.success(new Request.Progress(progress, path)));
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
    public void download(@NonNull RealmRoomMessage message, @NonNull ProtoFileDownload.FileDownload.Selector selector,
                         @Nullable Observer<Resource<Request.Progress>> observer) {
        download(message, selector, Request.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull RealmRoomMessage message,@Nullable Observer<Resource<Request.Progress>> observer) {
        download(message, ProtoFileDownload.FileDownload.Selector.FILE, Request.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull RealmRoomMessage message, int priority, @Nullable Observer<Resource<Request.Progress>> observer) {
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
        String mime = ".data";
        if (name != null && name.contains("."))
            mime = name.substring(name.lastIndexOf("."));

        String path = suitableAppFilePath(messageType);
        return new File(path + "/" + cacheId + mime);
    }
}
