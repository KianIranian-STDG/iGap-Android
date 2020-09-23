package net.iGap.module.downloader;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.proto.ProtoFileDownload;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

class HttpDownloader implements IDownloader, Observer<Pair<HttpRequest, HttpDownloader.DownloadStatus>> {
    private static HttpDownloader instance;
    private Queue<HttpRequest> requestsQueue;
    private List<HttpRequest> inProgressRequests;
    private AtomicInteger inProgressCount = new AtomicInteger(0);
    private static final int MAX_DOWNLOAD = 3;

    public static HttpDownloader getInstance() {
        HttpDownloader localInstance = instance;
        if (localInstance == null) {
            synchronized (HttpDownloader.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new HttpDownloader();
                }
            }
        }
        return localInstance;
    }

    private HttpDownloader() {
        requestsQueue = new PriorityBlockingQueue<>();
        inProgressRequests = new ArrayList<>();
    }

    public void download(@NonNull DownloadStruct message, @NonNull ProtoFileDownload.FileDownload.Selector selector, int priority, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        if (requestsQueue == null)
            requestsQueue = new PriorityBlockingQueue<>();

        HttpRequest existedRequest = findExistedRequest(HttpRequest.generateRequestId(message, selector));
        if (existedRequest == null) {
            existedRequest = new HttpRequest(message, selector, priority);
            existedRequest.setDownloadStatusListener(this);
            requestsQueue.add(existedRequest);
            scheduleNewDownload();
        }
        if (observer != null) {
            existedRequest.addObserver(observer);
        }
    }

    @Override
    public void download(@NonNull DownloadStruct message, @NonNull ProtoFileDownload.FileDownload.Selector selector, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
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

    private HttpRequest findExistedRequest(String requestId) {
        for (HttpRequest request : requestsQueue) {
            if (request.getRequestId().equals(requestId))
                return request;
        }
        for (HttpRequest request : inProgressRequests) {
            if (request.getRequestId().equals(requestId))
                return request;
        }
        return null;
    }

    public void cancelDownload(@NonNull String cacheId) {
        for (int i = 0; i < inProgressRequests.size(); i++) {
            if (inProgressRequests.get(i).getRequestId().contains(cacheId)) {
                inProgressRequests.get(i).cancelDownload();
                break;
            }
        }

        for (HttpRequest request : requestsQueue) {
            if (cacheId.contains(request.getRequestId())) {
                requestsQueue.remove(request);
                break;
            }
        }
    }

    @Override
    public boolean isDownloading(@NonNull String cacheId) {
        try {
            for (int i = 0; i < inProgressRequests.size(); i++) {
                if (inProgressRequests.get(i).getRequestId().contains(cacheId)) {
                    return inProgressRequests.get(i).isDownloading();
                }
            }
        } catch (Exception e) {
            // array out of bound exception is possible. The use case is when we are checking
            // if the request is downloading in the loop but the request has just popped out of
            // inProgressRequests and has threw exception.
            return false;
        }
        return false;
    }

    private void removeDownloadingRequestIfExist(HttpRequest request) {
        if (request == null)
            return;

        for (int i = 0; i < inProgressRequests.size(); i++) {
            if (request.getRequestId().equals(inProgressRequests.get(i).getRequestId())) {
                inProgressRequests.remove(i);
                inProgressCount.decrementAndGet();
                break;
            }
        }
    }

    private void scheduleNewDownload() {
        if (inProgressCount.get() >= MAX_DOWNLOAD)
            return;

        HttpRequest request = requestsQueue.poll();
        if (request == null)
            return;

        inProgressRequests.add(request);
        inProgressCount.incrementAndGet();

        request.download();
    }

    @Override
    public void onUpdate(Pair<HttpRequest, DownloadStatus> arg) {
        if (arg == null)
            return;

        switch (arg.second) {
            case DOWNLOADED:
            case NOT_DOWNLOADED:
                removeDownloadingRequestIfExist(arg.first);
                scheduleNewDownload();
                break;
        }
    }

    enum DownloadStatus {
        DOWNLOADED, DOWNLOADING, NOT_DOWNLOADED
    }
}
