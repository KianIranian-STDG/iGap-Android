package net.iGap.module.downloader;

import android.util.Pair;

import androidx.annotation.WorkerThread;

import net.iGap.G;
import net.iGap.api.apiService.ApiStatic;
import net.iGap.api.apiService.TokenContainer;
import net.iGap.controllers.MessageDataStorage;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperDataUsage;
import net.iGap.helper.OkHttpClientInstance;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoFileDownload.FileDownload.Selector;

import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static net.iGap.proto.ProtoGlobal.RoomMessageType.UNRECOGNIZED;

public class HttpRequest extends Observable<Resource<HttpRequest.Progress>> implements Comparable<HttpRequest> {
    public static final String BASE_URL = ApiStatic.FILE + "download/";
    private final int currentAccount;
    private DownloadObject fileObject;
    private int selector;
    private volatile boolean isDownloading;
    private volatile boolean isDownloaded;
    private final int priority = PRIORITY.PRIORITY_DEFAULT;
    private final AtomicBoolean cancelDownload = new AtomicBoolean(false);
    private final FileIOExecutor fileExecutors;
    private Observer<Pair<HttpRequest, HttpDownloader.DownloadStatus>> downloadStatusObserver;
    private Call call;
    private int retryCount = 0;

    HttpRequest(int account, DownloadObject fileObject) {
        currentAccount = account;
        this.fileObject = fileObject;
        selector = fileObject.selector;
        fileExecutors = FileIOExecutor.getInstance();
        isDownloaded = fileObject.destFile.exists() && fileObject.destFile.length() == fileObject.fileSize || fileObject.offset == fileObject.fileSize;
        isDownloading = false;

        onProgress(1);
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void download() {
        if (isDownloaded) {
            notifyDownloadStatus(HttpDownloader.DownloadStatus.DOWNLOADED);
            return;
        }

        fileExecutors.execute(() -> download(TokenContainer.getInstance().getToken(), fileObject));
    }

    public void cancelDownload() {
        cancelDownload.set(true);
        if (call != null) {
            call.cancel();
        }
        onError(new Exception("Download canceled"));
    }

    @WorkerThread
    private void download(String jwtToken, DownloadObject fileStruct) {
        isDownloading = true;
        notifyDownloadStatus(HttpDownloader.DownloadStatus.DOWNLOADING);

        OkHttpClient client = OkHttpClientInstance.getInstance();

        String fileToken = fileStruct.fileToken;
        String qParam = "?selector=" + fileStruct.selector;
        String url = BASE_URL + fileToken + qParam;

        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", jwtToken);

        if (fileStruct.offset > 0) {
            builder.addHeader("Range", "bytes=" + fileStruct.offset + "-" + fileStruct.fileSize);
        }

        FileLog.i("HttpRequest", "download Start with " + url + " range " + "bytes=" + fileStruct.offset + "-" + fileStruct.fileSize + " cashId: " + fileStruct.mainCacheId);

        Request request = builder.build();
        Response response = null;

        try (OutputStream os = new FileOutputStream(fileStruct.tempFile, true)) {
            call = client.newCall(request);
            response = call.execute();
            if (response.body() == null) {
                throw new Exception("Download body is null!");
            }

            if (response.isSuccessful()) {
                InputStream inputStream = response.body().byteStream();
                byte[] iv = new byte[16];
                int i = inputStream.read(iv, 0, 16);// do not remove i :)
                InputStream cipherInputStream = new CipherInputStream(inputStream, getCipher(iv, G.symmetricKey));
                byte[] data = new byte[4096];
                int count;
                long downloaded = fileStruct.offset;
                while ((count = cipherInputStream.read(data)) != -1) {
                    downloaded += count;
                    int t = (int) ((downloaded * 100) / fileStruct.fileSize);
                    if (fileStruct.progress < t) {
                        fileStruct.progress = t;
                        onProgress(fileStruct.progress);
                    }
                    os.write(data, 0, count);
                    if (cancelDownload.get()) {
                        safelyCancelDownload();
                        return;
                    }
                }
                onDownloadCompleted();
                cipherInputStream.close();
            } else if (response.code() == 401) { //token is expired
                refreshAccessTokenAndRetry();
            } else {
                onError(new Exception("Download is not successful!"));
            }
        } catch (Exception e) {
            onError(e);
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
    }

    private void refreshAccessTokenAndRetry() {
        if (retryCount++ < 3) {
            TokenContainer.getInstance().getRefreshToken(this::download);
        } else {
            onError(new Exception("Refreshing access token failed."));
        }
    }

    private void safelyCancelDownload() {
        isDownloading = false;

        if (fileObject.messageType == null)
            fileObject.messageType = UNRECOGNIZED;

        long downloadedBytes = (fileObject.fileSize / 100) * fileObject.progress;
        HelperDataUsage.progressDownload(downloadedBytes, fileObject.messageType);

        if (fileObject.destFile.exists())
            fileObject.destFile.delete();

        if (fileObject.tempFile != null && fileObject.tempFile.exists() && fileObject.messageType == UNRECOGNIZED) {
            fileObject.tempFile.delete();
        }

        notifyDownloadStatus(HttpDownloader.DownloadStatus.NOT_DOWNLOADED);
    }


    public String getRequestKey() {
        return fileObject.key;
    }

    public void onDownloadCompleted() {
        try {
            HelperDataUsage.progressDownload(fileObject.fileSize, fileObject.messageType);
            HelperDataUsage.increaseDownloadFiles(fileObject.messageType);
            moveTempToDownloadedDir();
            fileObject.progress = 100;
            notifyObservers(Resource.success(new Progress(fileObject.progress, selector == Selector.FILE_VALUE ? fileObject.destFile.getAbsolutePath() : fileObject.tempFile.getAbsolutePath(), fileObject.fileToken)));
            notifyDownloadStatus(HttpDownloader.DownloadStatus.DOWNLOADED);

        } catch (Exception e) {
            onError(e);
        }
    }

    public void onProgress(int progress) {
        if (selector == Selector.FILE_VALUE)
            notifyObservers(Resource.loading(new Progress(progress, fileObject.destFile.getAbsolutePath(), fileObject.fileToken)));
        else
            notifyObservers(Resource.loading(new Progress(progress, fileObject.tempFile.getAbsolutePath(), fileObject.fileToken)));
    }

    public void onError(@NotNull Throwable throwable) {
        safelyCancelDownload();
        notifyObservers(Resource.error(throwable.getMessage(), null));
        FileLog.e("HttpRequest", throwable);
    }

    @WorkerThread
    private void moveTempToDownloadedDir() throws IOException {
        MessageDataStorage storage = MessageDataStorage.getInstance(currentAccount);
        switch (selector) {
            case Selector.FILE_VALUE:
                AndroidUtils.cutFromTemp(fileObject.tempFile.getAbsolutePath(), fileObject.destFile.getAbsolutePath());
                storage.setAttachmentFilePath(fileObject.mainCacheId, fileObject.destFile.getAbsolutePath(), false);
                break;
            case Selector.SMALL_THUMBNAIL_VALUE:
            case Selector.LARGE_THUMBNAIL_VALUE:
                storage.setAttachmentFilePath(fileObject.mainCacheId, fileObject.tempFile.getAbsolutePath(), true);
                break;
        }
    }

    @Override
    public int compareTo(HttpRequest o) {
        return priority - o.priority;
    }

    public void setDownloadStatusListener(Observer<Pair<HttpRequest, HttpDownloader.DownloadStatus>> observer) {
        this.downloadStatusObserver = observer;
    }

    public void notifyDownloadStatus(HttpDownloader.DownloadStatus downloadStatus) {
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
        String token;

        public Progress(int progress, String filePath, String fileToken) {
            this.progress = progress;
            this.filePath = filePath;
            this.token = fileToken;
        }

        public int getProgress() {
            return progress;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getToken() {
            return token;
        }
    }

    public Cipher getCipher(byte[] iv, final SecretKeySpec key) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return cipher;
    }
}