package net.iGap.module.downloader;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import net.iGap.G;
import net.iGap.api.apiService.ApiStatic;
import net.iGap.api.apiService.TokenContainer;
import net.iGap.helper.OkHttpClientInstance;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoFileDownload.FileDownload.Selector;
import net.iGap.realm.RealmAttachment;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.CipherInputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static net.iGap.module.AndroidUtils.suitableAppFilePath;

public class Request extends Observable<Resource<Request.Progress>> implements Comparable<Request> {
    public static final String BASE_URL = ApiStatic.UPLOAD_URL + "download/";

    private String requestId;
    private Downloader.MessageStruct message;
    private Selector selector;
    private volatile boolean isDownloading;
    private volatile boolean isDownloaded;
    private File tempFile;
    private File downloadedFile;
    private int progress;
    private long size;
    private long offset;
    private int priority = PRIORITY.PRIORITY_DEFAULT;
    private AtomicBoolean cancelDownload = new AtomicBoolean(false);
    private AppExecutors appExecutors;
    private Observer<Pair<Request, DownloadThroughApi.DownloadStatus>> downloadStatusObserver;
    private Call call;

    protected Request(Downloader.MessageStruct message, Selector selector, int priority) {
        this(message, selector);
        this.priority = priority;
    }

    private Request(Downloader.MessageStruct msg, Selector selector) {
        this.selector = selector;
        message = msg;
        requestId = generateRequestId();
        appExecutors = AppExecutors.getInstance();
        initFileRelatedVariables();
        onProgress(1);
    }

    private void initFileRelatedVariables() {
        tempFile = generateTempFileForRequest();
        downloadedFile = generateDownloadFileForRequest();

        size = message.getFileSize();

        if (tempFile.exists()) {
            offset = tempFile.length();

            if (offset > 0 && size > 0) {
                progress = (int) ((offset * 100) / size);
            }
        }

        isDownloaded = downloadedFile.exists();
        isDownloading = false;
    }

    @NonNull
    private String generateRequestId() {
        return generateRequestId(message, selector);
    }

    private File generateDownloadFileForRequest() {
        String name = message.getCacheId();
        String mime = message.getMime();
        String path = suitableAppFilePath(message.getMessageType());
        return new File(path + "/" + name + mime);
    }

    private File generateTempFileForRequest() {
        String fileName = message.getCacheId() + selector.toString();
        return new File(G.DIR_TEMP + fileName);
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void download() {
        if (isDownloaded) {
            notifyDownloadStatus(DownloadThroughApi.DownloadStatus.DOWNLOADED);
            return;
        }
        appExecutors.networkIO().execute(() -> download(TokenContainer.getInstance().getToken(), message));
    }

    public void cancelDownload() {
        cancelDownload.set(true);
        if (call != null) {
            call.cancel();
        }
        onError(new Exception("download canceled"));
    }

    @WorkerThread
    private void download(String jwtToken, Downloader.MessageStruct message) {
        isDownloading = true;
        notifyDownloadStatus(DownloadThroughApi.DownloadStatus.DOWNLOADING);
        OkHttpClient client = OkHttpClientInstance.getInstance();
        String fileToken = message.getToken();

        String url = BASE_URL + fileToken;
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).addHeader("Authorization", jwtToken)
                .addHeader("Range", String.format("bytes=" + offset + "-" + size)).build();
        Response response = null;

        try (OutputStream os = new FileOutputStream(tempFile, true)) {
            call = client.newCall(request);
            response = call.execute();
            if (response.body() == null) {
                throw new Exception("body is null!");
            }
            InputStream inputStream = response.body().byteStream();
            byte[] iv = new byte[16];
            int readCount = inputStream.read(iv, 0, 16);
            InputStream cipherInputStream = new CipherInputStream(inputStream, AesCipherDownloadOptimized.getCipher(iv, G.symmetricKey));
            byte[] data = new byte[4096];
            int count;
            long downloaded = offset;
            while ((count = cipherInputStream.read(data)) != -1) {
                downloaded += count;
                int t = (int) ((downloaded * 100) / size);
                if (progress < t) {
                    progress = t;
                    onProgress(progress);
                }
                os.write(data, 0, count);
                if (cancelDownload.get()) {
                    safelyCancelDownload();
                    return;
                }
            }
            onDownloadCompleted();
            cipherInputStream.close();
        } catch (Exception e) {
            onError(e);
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
    }

    private void safelyCancelDownload() {
        if (downloadedFile.exists())
            downloadedFile.delete();
        isDownloading = false;
        notifyDownloadStatus(DownloadThroughApi.DownloadStatus.NOT_DOWNLOADED);
    }

    public String getRequestId() {
        return requestId;
    }

    public void onDownloadCompleted() {
        try {
            moveTempToDownloadedDir();
            this.progress = 100;
            notifyObservers(Resource.success(new Progress(this.progress, selector == Selector.FILE ? downloadedFile.getAbsolutePath() : tempFile.getAbsolutePath())));

            notifyDownloadStatus(DownloadThroughApi.DownloadStatus.DOWNLOADED);
        } catch (Exception e) {
            onError(e);
        }
    }

    public void onProgress(int progress) {
        Log.i("amiiiiir", "onProgress: " + getRequestId() + " " + progress);
        if (selector == Selector.FILE)
            notifyObservers(Resource.loading(new Progress(progress, downloadedFile.getAbsolutePath())));
        else
            notifyObservers(Resource.loading(new Progress(progress, tempFile.getAbsolutePath())));
    }

    public void onError(@NotNull Throwable throwable) {
        throwable.printStackTrace();
        safelyCancelDownload();
        notifyObservers(Resource.error(throwable.getMessage(), null));
    }

    @WorkerThread
    private void moveTempToDownloadedDir() throws IOException {
        Log.i("amiiiiir", "moveTempToDownloadedDir: " + downloadedFile.length() + " " + getRequestId());
        switch (selector) {
            case FILE:
                AndroidUtils.cutFromTemp(tempFile.getAbsolutePath(), downloadedFile.getAbsolutePath());
                RealmAttachment.setFilePAthToDataBaseAttachment(message.getCacheId(), downloadedFile.getAbsolutePath());
                break;
            case SMALL_THUMBNAIL:
            case LARGE_THUMBNAIL:
                RealmAttachment.setThumbnailPathDataBaseAttachment(message.getCacheId(), tempFile.getAbsolutePath());
                break;
        }
    }

    @Override
    public int compareTo(Request o) {
        return priority - o.priority;
    }

    public static String generateRequestId(Downloader.MessageStruct message, Selector selector) {
        return message.getCacheId() + selector.toString();
    }

    public File getDownloadedFile() {
        return downloadedFile;
    }

    public int getProgress() {
        return progress;
    }

    public long getSize() {
        return size;
    }

    public int getPriority() {
        return priority;
    }

    public long getOffset() {
        return offset;
    }

    public void setDownloadStatusListener(Observer<Pair<Request, DownloadThroughApi.DownloadStatus>> observer) {
        this.downloadStatusObserver = observer;
    }

    public void notifyDownloadStatus(DownloadThroughApi.DownloadStatus downloadStatus) {
        if (downloadStatusObserver != null) {
            downloadStatusObserver.onUpdate(new Pair<>(this, downloadStatus));
        }
    }

    public static class PRIORITY {
        public static final int PRIORITY_LOW = 5;
        public static final int PRIORITY_MEDIUM = 3;
        public static final int PRIORITY_HIGH = 1;
        public static final int PRIORITY_DEFAULT = PRIORITY_MEDIUM;
    }

    public static class Progress {
        int progress;
        String filePath;

        public Progress(int progress, String filePath) {
            this.progress = progress;
            this.filePath = filePath;
        }

        public int getProgress() {
            return progress;
        }

        public String getFilePath() {
            return filePath;
        }
    }
}