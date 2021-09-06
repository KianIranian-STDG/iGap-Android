package net.iGap.module.downloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.controllers.BaseController;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.AppConfig;
import net.iGap.proto.ProtoFileDownload.FileDownload.Selector;

import java.util.HashSet;

public class Downloader extends BaseController implements IDownloader {
    private static volatile Downloader[] instance = new Downloader[AccountManager.MAX_ACCOUNT_COUNT];

    private IDownloader downloadThroughProto;
    private IDownloader downloadThroughApi;
    private IDownloader downloadThroughCdn;
    private HashSet<String> publicCacheId = new HashSet<>();

    private Downloader(int account) {
        super(account);
        downloadThroughApi = HttpDownloader.getInstance(account);
        downloadThroughProto = SocketDownloader.getInstance();
        downloadThroughCdn = CdnDownloader.getInstance(account);
    }

    public static Downloader getInstance(int account) {
        Downloader localInstance = instance[account];
        if (localInstance == null) {
            synchronized (Downloader.class) {
                localInstance = instance[account];
                if (localInstance == null) {
                    instance[account] = localInstance = new Downloader(account);
                }
            }
        }
        return localInstance;
    }

    @Override
    public void download(@NonNull DownloadObject file, @NonNull Selector selector, int priority, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
//        if (file.isPublic()) {
//            publicCacheId.add(file.mainCacheId);
//        }
        getCurrentDownloader(file.mainCacheId).download(file, selector, priority, observer);
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
        getCurrentDownloader(cacheId).cancelDownload(cacheId);
    }

    public void onCdnDownloadComplete(String cacheId) {
        publicCacheId.remove(cacheId);
    }

    @Override
    public boolean isDownloading(@NonNull String cacheId) {
        return getCurrentDownloader(cacheId).isDownloading(cacheId);
    }

    private IDownloader getCurrentDownloader(String cacheId) {
        if (publicCacheId.contains(cacheId))
            return downloadThroughCdn;
        else if (AppConfig.fileGateway == 1) {
            return downloadThroughProto;
        } else if (AppConfig.fileGateway == 0) {
            return downloadThroughApi;
        }
        return downloadThroughProto;
    }
}
