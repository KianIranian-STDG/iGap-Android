package net.iGap.module.downloader;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.proto.ProtoFileDownload;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadThroughApi implements IDownloader, Observer<Pair<Request, DownloadThroughApi.DownloadStatus>> {
    private static final String TAG = "DownloadThroughApi";
    private static DownloadThroughApi instance;
    private Queue<Request> requestsQueue;
    private List<Request> inProgressRequests;
    private AtomicInteger inProgressCount = new AtomicInteger(0);
    private static final int MAX_DOWNLOAD = 2;

    public static DownloadThroughApi getInstance() {
        if (instance == null) {
            synchronized (DownloadThroughApi.class) {
                if (instance == null) {
                    instance = new DownloadThroughApi();
                }
            }
        }
        return instance;
    }

    private DownloadThroughApi() {
        requestsQueue = new PriorityBlockingQueue<>();
        inProgressRequests = new ArrayList<>();
    }

    public void download(@NonNull DownloadStruct message,
                         @NonNull ProtoFileDownload.FileDownload.Selector selector,
                         int priority,
                         @Nullable Observer<Resource<Request.Progress>> observer) {

        if (requestsQueue == null)
            requestsQueue = new PriorityBlockingQueue<>();

        Request existedRequest = findExistedRequest(Request.generateRequestId(message, selector));
        if (existedRequest == null) {
            existedRequest = new Request(message, selector, priority);
            existedRequest.setDownloadStatusListener(this);
            requestsQueue.add(existedRequest);
            scheduleNewDownload();
        }
        if (observer != null) {
            existedRequest.addObserver(observer);
        }
    }

    @Override
    public void download(@NonNull DownloadStruct message,
                         @NonNull ProtoFileDownload.FileDownload.Selector selector,
                         @Nullable Observer<Resource<Request.Progress>> observer) {
        download(message, selector, Request.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull DownloadStruct message,
                         @Nullable Observer<Resource<Request.Progress>> observer) {
        download(message, ProtoFileDownload.FileDownload.Selector.FILE, Request.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull DownloadStruct message,
                         int priority,
                         @Nullable Observer<Resource<Request.Progress>> observer) {
        download(message, ProtoFileDownload.FileDownload.Selector.FILE, priority, observer);
    }

    private Request findExistedRequest(String requestId) {
        for (Request request : requestsQueue) {
            if (request.getRequestId().equals(requestId))
                return request;
        }
        for (Request request : inProgressRequests) {
            if (request.getRequestId().equals(requestId))
                return request;
        }
        return null;
    }

    public void cancelDownload(@NonNull String cacheId) {
        for (int i = 0; i < inProgressRequests.size(); i++) {
            if (inProgressRequests.get(i).getRequestId().contains(cacheId)) {
                Log.i(TAG, "cancelDownload: " + inProgressRequests.get(i).getRequestId());
                inProgressRequests.get(i).cancelDownload();
                break;
            }
        }

        for (Request request : requestsQueue) {
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

    private void removeDownloadingRequestIfExist(Request request) {
        if (request == null)
            return;

        Log.i(TAG, "removeDownloadingRequestIfExist: ");

        for (int i = 0; i < inProgressRequests.size(); i++) {
            if (request.getRequestId().equals(inProgressRequests.get(i).getRequestId())) {
                Log.i(TAG, "removeDownloadingRequestIfExist: " + inProgressRequests.get(i).getRequestId());
                inProgressRequests.remove(i);
                inProgressCount.decrementAndGet();
                break;
            }
        }
    }

    private void scheduleNewDownload() {
        if (inProgressCount.get() >= MAX_DOWNLOAD)
            return;

        Log.i(TAG, "scheduleNewDownload: ");

        Request request = requestsQueue.poll();
        if (request == null)
            return;

        inProgressRequests.add(request);
        Log.i(TAG, "scheduleNewDownload: " + request.getRequestId());
        inProgressCount.incrementAndGet();

        request.download();
    }

    @Override
    public void onUpdate(Pair<Request, DownloadStatus> arg) {
        if (arg == null)
            return;

        Log.i(TAG, "onUpdate: ");

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
