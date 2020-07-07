package net.iGap.module.downloader;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import net.iGap.G;
import net.iGap.api.apiService.TokenContainer;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoFileDownload.FileDownload.Selector;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRoomMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.CipherInputStream;

import okhttp3.OkHttpClient;
import okhttp3.Response;

import static net.iGap.module.AndroidUtils.suitableAppFilePath;

public class Request extends Observable<Resource<Integer>> implements Comparable<Request> {
    public static final String BASE_URL = "http://192.168.10.31:3007/v1.0/download/";

    private String requestId;
    private RealmRoomMessage message;
    private Selector selector;
    private volatile boolean isDownloading;
    private volatile boolean isDownloaded;
    private File tempFile;
    private File downloadedFile;
    private int progress;
    private long size;
    private long offset;
    private int priority = PRIORITY.PRIORITY_DEFAULT;
    private AppExecutors appExecutors;
    private Observer<Pair<Request, Downloader.DownloadStatus>> downloadStatusObserver;

    protected Request(RealmRoomMessage message, Selector selector, int priority) {
        this(message, selector);
        this.priority = priority;
    }

    private Request(RealmRoomMessage message, Selector selector) {
        this.selector = selector;
        this.message = message;
        requestId = generateRequestId();
        appExecutors = AppExecutors.getInstance();
        initFileRelatedVariables();
    }

    private void initFileRelatedVariables() {
        tempFile = generateTempFileForRequest();
        downloadedFile = generateDownloadFileForRequest();

        size = 19105002;
//        size = message.getAttachment().getSize();

        if (tempFile.exists()) {
            offset = tempFile.length();

            if (offset > 0 && size > 0) {
                progress = (int) ((offset * 100) / size);
            }
        }

        isDownloaded = checkIsDownloaded();
        isDownloading = false;
    }

    @NonNull
    private String generateRequestId() {
        return generateRequestId(message, selector);
    }

    private File generateDownloadFileForRequest() {
        String name = message.getAttachment().getCacheId();
        String type = "";

        switch (message.getMessageType()) {
            case VOICE:
                type = ".ogg";
                break;
            case AUDIO_TEXT:
            case AUDIO:
                type = ".mp3";
                break;
            case IMAGE_TEXT:
            case IMAGE:
                type = ".jpg";
                break;
            default:
                type = ".data";
        }

        String path = suitableAppFilePath(message.getMessageType());
        return new File(path + "/" + name + type);
    }

    private File generateTempFileForRequest() {
        String fileName = message.getAttachment().getCacheId() + selector.toString();
        return new File(G.DIR_TEMP + fileName);
    }

    private boolean checkIsDownloaded() {
        return downloadedFile.exists();
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void download() {
        if (isDownloaded) {
//            notifyDownloadStatus(Downloader.DownloadStatus.DOWNLOADED);
            return;
        }
        isDownloading = true;
//        notifyDownloadStatus(Downloader.DownloadStatus.DOWNLOADING);


        appExecutors.networkIO().execute(() -> {
            download(TokenContainer.getInstance().getToken());
        });
    }

    @WorkerThread
    private void download(String jwtToken) {
        notifyDownloadStatus(Downloader.DownloadStatus.DOWNLOADING);
        OkHttpClient client = new OkHttpClient();
//        String fileToken =  RealmRoomMessage.getFinalMessage(message).getAttachment().getToken();
        String fileToken = "5a8b3703-0e60-4461-81b2-6d831d500959";

        String url = BASE_URL + fileToken;
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).addHeader("Authorization", jwtToken)
                .addHeader("Range", String.format("bytes=" + offset + "-" + size)).build();
        Response response = null;

        try (OutputStream os = new FileOutputStream(tempFile, true)) {
            response = client.newCall(request).execute();
            if (response.body() == null) {
                throw new Exception("body is null!");
            }
            InputStream inputStream = response.body().byteStream();
            byte[] iv = new byte[16];
            int c = inputStream.read(iv, 0, 16);
            InputStream in = new CipherInputStream(inputStream, AesCipherDownloadOptimized.getCipher(iv, G.symmetricKey));
            byte[] data = new byte[4096];
            int count;
            long downloaded = offset;
            while ((count = in.read(data)) != -1) {
                downloaded += count;
                int t = (int) ((downloaded * 100) / size);
                if (progress < t) {
                    progress = t;
                    onProgress(progress);
                }
                os.write(data, 0, count);
            }
            onDownloadCompleted();
        } catch (Exception e) {
            onError(e);
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
    }

    public String getRequestId() {
        return requestId;
    }

    public RealmRoomMessage getMessage() {
        return message;
    }

    public void onDownloadCompleted() {
        try {
            moveTempToDownloadedDir();
            this.progress = 100;
            notifyObservers(Resource.success(this.progress));
            notifyDownloadStatus(Downloader.DownloadStatus.DOWNLOADED);
        } catch (IOException e) {
            onError(e);
        }
    }

    public void onProgress(int progress) {
        notifyObservers(Resource.success(progress));
    }

    public void onError(Throwable throwable) {
        throwable.printStackTrace();

        if (downloadedFile.exists())
            downloadedFile.delete();
        isDownloading = false;
        notifyDownloadStatus(Downloader.DownloadStatus.NOT_DOWNLOADED);
    }

    @WorkerThread
    private void moveTempToDownloadedDir() throws IOException {
        if (!downloadedFile.exists()) {
            AndroidUtils.cutFromTemp(tempFile.getAbsolutePath(), downloadedFile.getAbsolutePath());
        }

        switch (selector) {
            case FILE:
                RealmAttachment.setFilePAthToDataBaseAttachment(message.getAttachment().getCacheId(), downloadedFile.getAbsolutePath());
                break;
            case SMALL_THUMBNAIL:
            case LARGE_THUMBNAIL:
                RealmAttachment.setThumbnailPathDataBaseAttachment(message.getAttachment().getCacheId(), tempFile.getAbsolutePath());
                break;
        }
    }

    @Override
    public int compareTo(Request o) {
        return priority - o.priority;
    }

    public static String generateRequestId(RealmRoomMessage message, Selector selector) {
        return message.getAttachment().getCacheId() + selector.toString();
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

    public void setDownloadStatusListener(Observer<Pair<Request, Downloader.DownloadStatus>> observer) {
        this.downloadStatusObserver = observer;
    }

    public void notifyDownloadStatus(Downloader.DownloadStatus downloadStatus) {
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
}