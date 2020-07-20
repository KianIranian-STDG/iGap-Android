package net.iGap.module.downloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.proto.ProtoFileDownload;
import net.iGap.realm.RealmRoomMessage;

public interface IDownloader {
    void download(@NonNull RealmRoomMessage message,
                       @NonNull ProtoFileDownload.FileDownload.Selector selector,
                       int priority,
                       @Nullable Observer<Resource<Integer>> observer);

    void download(@NonNull RealmRoomMessage message,
                  @NonNull ProtoFileDownload.FileDownload.Selector selector,
                  @Nullable Observer<Resource<Integer>> observer);

    void download(@NonNull RealmRoomMessage message,
                  @Nullable Observer<Resource<Integer>> observer);

    void download(@NonNull RealmRoomMessage message,
             int priority,
             @Nullable Observer<Resource<Integer>> observer);

    void cancelDownload(@NonNull String cacheId);

    boolean isDownloading(@NonNull String cacheId);
}
