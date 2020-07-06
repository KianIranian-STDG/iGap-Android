package net.iGap.module.downloader;

import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.Observer;

import net.iGap.G;
import net.iGap.api.DownloadApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.api.apiService.TokenContainer;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoFileDownload.FileDownload.Selector;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRoomMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.CipherInputStream;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static net.iGap.module.AndroidUtils.suitableAppFilePath;

public class Request implements IProgress, Comparable<Request> {
    private String requestId;
    private RealmRoomMessage message;
    private List<Observer<Resource<Integer>>> observers;
    private Observer<Pair<Request, Downloader.DownloadStatus>> downloadStatus;
    private Selector selector;
    private volatile boolean isDownloading;
    private volatile boolean isDownloaded;
    private File tempFile;
    private File downloadedFile;
    private DownloadApi downloadApi;
    private int progress;
    private long size;
    private long offset;
    private Handler handler;
    private int priority = PRIORITY.PRIORITY_DEFAULT;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    protected Request(RealmRoomMessage message, Selector selector, int priority) {
        this(message, selector);
        this.priority = priority;
    }

    private Request(RealmRoomMessage message, Selector selector) {
        handler = new Handler();
        this.selector = selector;
        this.message = message;
        this.downloadApi = new RetrofitFactory().getDownloadRetrofit(this);
        observers = new ArrayList<>();
        requestId = generateRequestId();

        initFileRelatedVariables();
    }

    private void initFileRelatedVariables() {
        tempFile = generateTempFileForRequest();
        downloadedFile = generateDownloadFileForRequest();

        size = 8344500;
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
            notifyDownloadStatus(Downloader.DownloadStatus.DOWNLOADED);
            return;
        }
        isDownloading = true;
        notifyDownloadStatus(Downloader.DownloadStatus.DOWNLOADING);
        Disposable disposable = downloadApi.downloadData("886968ba-a15e-4a6e-89c4-178d9434956c", String.valueOf(message.getUserId()), TokenContainer.getInstance().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::saveDelegate, this::onError);
        addToCompositeDisposable(disposable);
    }

    private void saveDelegate(ResponseBody responseBody) {
        Disposable disposable = Flowable.just(responseBody)
                .observeOn(Schedulers.io())
                .subscribe(this::saveInTemp, this::onError);

        addToCompositeDisposable(disposable);
    }

    private void addToCompositeDisposable(Disposable disposable) {
        if (compositeDisposable == null)
            compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(disposable);
    }

    @WorkerThread
    private void saveInTemp(ResponseBody value) {
        byte[] iv = new byte[0];
        try {
            iv = value.source().readByteArray(16);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        File file = new File(G.DIR_TEMP + "/result.jpg");
        try (CipherInputStream is = new CipherInputStream(value.byteStream(), AesCipherDownloadOptimized.getCipher(iv, G.symmetricKey)); OutputStream os = new FileOutputStream(tempFile)) {

            byte[] data = new byte[4096];
            int count;
            while ((count = is.read(data)) != -1) {
                os.write(data, 0, count);
                offset += count;
            }
            onDownloadCompleted();
            os.flush();
        } catch (Exception e) {
            onError(e);
        }

//        boolean isEqual = true;
//        try {
//            FileInputStream fis1 = new FileInputStream(file);
//            FileInputStream fis2 = new FileInputStream(downloadedFile);
//            byte[] f1 = new byte[2048];
//            byte[] f2 = new byte[2048];
//            while (fis1.read(f1) != -1 && fis2.read(f2) != -1) {
//                if (!Arrays.equals(f1, f2)) {
//                    isEqual = false;
//                    break;
//                }
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Log.i(TAG, "saveInTemp: " + isEqual);
    }

//    @WorkerThread
//    private void saveInTemp(ResponseBody value) {
//
//        try (CipherInputStream is = new CipherInputStream(value.byteStream(),  AesCipherDownloadOptimized.getCipher("5183666c72eec9e4".getBytes(), HelperString.generateSymmetricKey("bf3c199c2470cb477d907b1e0917c17b")));
//             OutputStream os = new FileOutputStream(tempFile)) {
//            byte[] data = new byte[4096];
//            int count;
//            while ((count = is.read(data)) != -1) {
//                os.write(data, 0, count);
//                offset += count;
//            }
//            onDownloadCompleted();
//            os.flush();
//        } catch (IOException e) {
//            onError(e);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        }
//    }

    public String getRequestId() {
        return requestId;
    }

    private void notifyObservers() {
        for (Observer<Resource<Integer>> observer : observers) {
            observer.onChanged(Resource.success(progress));
        }
    }

    public void setDownloadStatusObserver(Observer<Pair<Request, Downloader.DownloadStatus>> downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public void notifyDownloadStatus(Downloader.DownloadStatus downloadStatus) {
        if (this.downloadStatus != null)
            this.downloadStatus.onChanged(new Pair<>(this, downloadStatus));
    }

    public void addObserver(Observer<Resource<Integer>> observer) {
        for (Observer obs : observers) {
            if (obs == observer) {
                return;
            }
        }

        observers.add(observer);
    }

    public void removeObserver(Observer<Resource<Integer>> observer) {
        for (int i = 0; i < observers.size(); i++) {
            if (observers.get(i) == observer) {
                observers.remove(i);
                return;
            }
        }
    }

    public void dispose() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
    }

    @Override
    public void onProgress(int progress) {
        if (this.progress == progress)
            return;

        this.progress = progress;
        Log.i("downloadProgress", "onProgress: " + progress);
        handler.post(this::notifyObservers);
    }

    @Override
    public void onDownloadCompleted() {
        try {
            moveTempToDownloadedDir();
            this.progress = 100;
            handler.post(this::notifyObservers);
        } catch (IOException e) {
            onError(e);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();

        if (tempFile.exists())
            tempFile.delete();
        if (downloadedFile.exists())
            downloadedFile.delete();
        isDownloading = false;
        notifyDownloadStatus(isDownloaded ? Downloader.DownloadStatus.DOWNLOADED : Downloader.DownloadStatus.NOT_DOWNLOADED);
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

    public static class PRIORITY {
        public static final int PRIORITY_LOW = 5;
        public static final int PRIORITY_MEDIUM = 3;
        public static final int PRIORITY_HIGH = 1;
        public static final int PRIORITY_DEFAULT = PRIORITY_MEDIUM;
    }
}