package net.iGap.module.downloader;

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

public class Downloader implements Observer<Pair<Request, Downloader.DownloadStatus>> {
    private static Downloader instance;
    private Queue<Request> requestsQueue;
    private List<Request> inProgressRequests;
    private AtomicInteger inProgressCount = new AtomicInteger(0);
    private static final int MAX_DOWNLOAD = 2;

    public static Downloader getInstance() {
        if (instance == null) {
            synchronized (Downloader.class) {
                if (instance == null) {
                    instance = new Downloader();
                }
            }
        }
        return instance;
    }

    private Downloader() {
        requestsQueue = new PriorityBlockingQueue<>();
        inProgressRequests = new ArrayList<>();
    }

    public String download(@NonNull RealmRoomMessage message,
                           @NonNull ProtoFileDownload.FileDownload.Selector selector,
                           int priority,
                           @Nullable Observer<Resource<Integer>> observer) {

        if (validateMessage(message)) {
            message = RealmRoomMessage.getFinalMessage(message);
        }

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
        return existedRequest.getRequestId();
    }

    private boolean validateMessage(RealmRoomMessage message) {
        return RealmRoomMessage.getFinalMessage(message).getAttachment() != null;
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

    public String download(@NonNull RealmRoomMessage message,
                           @NonNull ProtoFileDownload.FileDownload.Selector selector,
                           @Nullable Observer<Resource<Integer>> observer) {
        return download(message, selector, Request.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    public String download(@NonNull RealmRoomMessage message,
                           @Nullable Observer<Resource<Integer>> observer) {
        return download(message, ProtoFileDownload.FileDownload.Selector.FILE, Request.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    public String download(@NonNull RealmRoomMessage message,
                           int priority,
                           @Nullable Observer<Resource<Integer>> observer) {
        return download(message, ProtoFileDownload.FileDownload.Selector.FILE, priority, observer);
    }

    public void cancelDownload(@NonNull String cacheId) {
        for (int i = 0; i < inProgressRequests.size(); i++) {
            if (cacheId.contains(inProgressRequests.get(i).getRequestId())) {
                inProgressRequests.get(i).cancelDownload();
            }
        }

        for (Request request: requestsQueue) {
            if (cacheId.contains(request.getRequestId())) {
                requestsQueue.remove(request);
                break;
            }
        }
    }

    private void removeDownloadingRequestIfExist(Request request) {
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

    private synchronized void scheduleNewDownload() {
        if (inProgressCount.get() >= MAX_DOWNLOAD)
            return;

        Request request = requestsQueue.poll();
        if (request == null)
            return;

        inProgressRequests.add(request);
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
