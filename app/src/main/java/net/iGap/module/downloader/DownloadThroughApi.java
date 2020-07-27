package net.iGap.module.downloader;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.proto.ProtoFileDownload;
import net.iGap.realm.RealmRoomMessage;

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

    public void download(@NonNull RealmRoomMessage message,
                         @NonNull ProtoFileDownload.FileDownload.Selector selector,
                         int priority,
                         @Nullable Observer<Resource<Request.Progress>> observer) {

        Downloader.MessageStruct messageStruct;
        if (validateMessage(RealmRoomMessage.getFinalMessage(message))) {
            message = RealmRoomMessage.getFinalMessage(message);
            messageStruct = new Downloader.MessageStruct(message);
        } else {
            Log.i(TAG, "download: invalid message to download");
            return;
        }

        if (requestsQueue == null)
            requestsQueue = new PriorityBlockingQueue<>();

        Request existedRequest = findExistedRequest(Request.generateRequestId(messageStruct, selector));
        if (existedRequest == null) {
            existedRequest = new Request(messageStruct, selector, priority);
            existedRequest.setDownloadStatusListener(this);
            requestsQueue.add(existedRequest);
            scheduleNewDownload();
        }
        if (observer != null) {
            existedRequest.addObserver(observer);
        }
    }

    @Override
    public void download(@NonNull RealmRoomMessage message,
                         @NonNull ProtoFileDownload.FileDownload.Selector selector,
                         @Nullable Observer<Resource<Request.Progress>> observer) {
        download(message, selector, Request.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull RealmRoomMessage message,
                         @Nullable Observer<Resource<Request.Progress>> observer) {
        download(message, ProtoFileDownload.FileDownload.Selector.FILE, Request.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull RealmRoomMessage message,
                         int priority,
                         @Nullable Observer<Resource<Request.Progress>> observer) {
        download(message, ProtoFileDownload.FileDownload.Selector.FILE, priority, observer);
    }

    private boolean validateMessage(RealmRoomMessage message) {
        return message.getAttachment() != null;
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
        for (int i = 0; i < inProgressRequests.size(); i++) {
            if (inProgressRequests.get(i).getRequestId().contains(cacheId)) {
                return inProgressRequests.get(i).isDownloading();
            }
        }
        return false;
    }

    private void removeDownloadingRequestIfExist(Request request) {
        if (request == null)
            return;

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
