package net.iGap.module.downloader;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.G;
import net.iGap.controllers.BaseController;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoFileDownload;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

class HttpDownloader extends BaseController implements IDownloader, Observer<Pair<HttpRequest, HttpDownloader.DownloadStatus>>, EventManager.EventDelegate {
    private static HttpDownloader[] instance = new HttpDownloader[AccountManager.MAX_ACCOUNT_COUNT];
    private Queue<HttpRequest> requestsQueue = new PriorityBlockingQueue<>();
    private List<HttpRequest> inProgressRequests = new ArrayList<>();
    private AtomicInteger inProgressCount = new AtomicInteger(0);
    private boolean canStartDownload;
    private static final int MAX_DOWNLOAD = 3;

    public static HttpDownloader getInstance(int account) {
        HttpDownloader localInstance = instance[account];
        if (localInstance == null) {
            synchronized (HttpDownloader.class) {
                localInstance = instance[account];
                if (localInstance == null) {
                    instance[account] = localInstance = new HttpDownloader(account);
                }
            }
        }
        return localInstance;
    }

    private HttpDownloader(int account) {
        super(account);
        canStartDownload = getRequestManager().isUserLogin();

        G.runOnUiThread(() -> getEventManager().postEvent(EventManager.USER_LOGIN_CHANGED, this));
    }

    public void download(@NonNull DownloadObject message, @NonNull ProtoFileDownload.FileDownload.Selector selector, int priority, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        if (requestsQueue == null)
            requestsQueue = new PriorityBlockingQueue<>();

        HttpRequest existedRequest = findExistedRequest(message.key);
        if (existedRequest == null) {
            existedRequest = new HttpRequest(currentAccount, message);
            existedRequest.setDownloadStatusListener(this);
            requestsQueue.add(existedRequest);
            scheduleNewDownload();
        }
        if (observer != null) {
            existedRequest.addObserver(observer);
        }
    }

    @Override
    public void download(@NonNull DownloadObject message, @NonNull ProtoFileDownload.FileDownload.Selector selector, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        download(message, selector, HttpRequest.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull DownloadObject message, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        download(message, ProtoFileDownload.FileDownload.Selector.FILE, HttpRequest.PRIORITY.PRIORITY_DEFAULT, observer);
    }

    @Override
    public void download(@NonNull DownloadObject message, int priority, @Nullable Observer<Resource<HttpRequest.Progress>> observer) {
        download(message, ProtoFileDownload.FileDownload.Selector.FILE, priority, observer);
    }

    private HttpRequest findExistedRequest(String requestId) {
        for (HttpRequest request : requestsQueue) {
            if (request.getRequestKey().equals(requestId))
                return request;
        }
        for (HttpRequest request : inProgressRequests) {
            if (request.getRequestKey().equals(requestId))
                return request;
        }
        return null;
    }

    public void cancelDownload(@NonNull String cacheId) {
        for (int i = 0; i < inProgressRequests.size(); i++) {
            if (inProgressRequests.get(i).getRequestKey().contains(cacheId)) {
                inProgressRequests.get(i).cancelDownload();
                break;
            }
        }

        for (HttpRequest request : requestsQueue) {
            if (cacheId.contains(request.getRequestKey())) {
                requestsQueue.remove(request);
                break;
            }
        }
    }

    @Override
    public boolean isDownloading(@NonNull String cacheId) {
        String key = DownloadObject.createKey(cacheId, ProtoFileDownload.FileDownload.Selector.FILE_VALUE);
        try {
            for (int i = 0; i < inProgressRequests.size(); i++) {
                if (inProgressRequests.get(i).getRequestKey().equals(key)) {
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
            if (request.getRequestKey().equals(inProgressRequests.get(i).getRequestKey())) {
                inProgressRequests.remove(i);
                inProgressCount.decrementAndGet();
                break;
            }
        }
    }

    private void scheduleNewDownload() {
        if (!canStartDownload) {
            return;
        }

        if (inProgressCount.get() >= MAX_DOWNLOAD) {
            return;
        }

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

    public void setCanStartDownload(boolean canStartDownload) {
        this.canStartDownload = canStartDownload;

        if (canStartDownload) {
            scheduleNewDownload();
        }
    }

    @Override
    public void receivedEvent(int id, int account, Object... args) {
        if (id == EventManager.USER_LOGIN_CHANGED) {
            if (AccountManager.selectedAccount == currentAccount) {
                setCanStartDownload(getRequestManager().isUserLogin());
            }
        }
    }

    enum DownloadStatus {
        DOWNLOADED, DOWNLOADING, NOT_DOWNLOADED
    }
}
