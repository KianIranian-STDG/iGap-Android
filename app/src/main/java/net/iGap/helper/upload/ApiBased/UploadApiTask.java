package net.iGap.helper.upload.ApiBased;

import android.util.Log;
import android.webkit.MimeTypeMap;

import net.iGap.api.UploadsApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.helper.upload.OnUploadListener;
import net.iGap.model.UploadData;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import shadow.org.apache.commons.io.FilenameUtils;

public class UploadApiTask extends Thread implements HandShakeCallback {

    private UploadsApi apiService;
    private OnUploadListener listener;
    private String identity;
    private String token = null;
    private String roomID;
    private ProtoGlobal.RoomMessageType uploadType;
    private File file;

    private Disposable uploadDisposable;
    private RequestBody requestBody;

    private FileChannel fileChannel;
    private RandomAccessFile randomAccessFile;

    public UploadApiTask(String identity, String roomID, File file, ProtoGlobal.RoomMessageType uploadType, OnUploadListener listener) {
        this.listener = listener;
        this.identity = identity;
        this.uploadType = uploadType;
        this.file = file;
        this.roomID = roomID;
        this.apiService = new RetrofitFactory().getUploadRetrofit();
    }

    public UploadApiTask(RealmRoomMessage message, OnUploadListener onUploadListener) {
        this(message.getMessageId() + "", String.valueOf(message.getRoomId()), new File(message.getAttachment().getLocalFilePath()), message.getMessageType(), onUploadListener);
    }

    public UploadApiTask(RealmRoomMessage message, String compressedPass, OnUploadListener onUploadListener) {
        this(message.getMessageId() + "", String.valueOf(message.getRoomId()), new File(compressedPass), message.getMessageType(), onUploadListener);
    }

    @Override
    public void run() {
        super.run();
        try {
            openFile(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.d("bagi", file.getAbsolutePath());
        Log.d("bagi", file.length() + "fileLen");
        getUploadInfoServer(!(token == null));
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void openFile(String filePath) throws FileNotFoundException {
        if (randomAccessFile == null) {
            randomAccessFile = new RandomAccessFile(new File(filePath), "r");
        }
        if (fileChannel == null || !fileChannel.isOpen()) {
            fileChannel = randomAccessFile.getChannel();
        }
    }

    private void closeFile() throws IOException {
        try {
            if (fileChannel != null) {
                fileChannel.close();
            }
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        } finally {
            fileChannel = null;
            randomAccessFile = null;
        }
    }

    private void getUploadInfoServer(boolean isResume) {
        long size = ((file.length() / 16 + 1) * 16) + 16;
        new ApiInitializer<UploadData>().initAPI(apiService.initUpload("", String.valueOf(size),
                FilenameUtils.getBaseName(file.getName()), FilenameUtils.getExtension(file.getName()), roomID),
                this, new ResponseCallback<UploadData>() {
                    @Override
                    public void onSuccess(UploadData data) {
                        if (!isResume) {
                            token = data.getToken();
                        } else {

                        }
                    }

                    @Override
                    public void onError(String error) {
                        listener.onError(identity);
                    }

                    @Override
                    public void onFailed() {
                        listener.onError(identity);
                    }
                });
    }

    private void uploadFile() {
        Flowable<Double> uploadFlow = Flowable.create(emitter -> {
            try {
                apiService.uploadData(token, createMultipartBody(file.getAbsolutePath(), emitter)).blockingGet();
                emitter.onComplete();
            } catch (Exception e) {
                emitter.tryOnError(e);
            }
        }, BackpressureStrategy.LATEST);
        uploadDisposable = uploadFlow.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<Double>() {
                    @Override
                    public void onNext(Double progress) {
                        listener.onProgress(identity, progress.intValue());
                    }

                    @Override
                    public void onError(Throwable t) {
                        listener.onError(identity);
                    }

                    @Override
                    public void onComplete() {
                        try {
                            closeFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        synchronized (this) {
                            notify();
                        }
                    }
                });
    }

    public void cancelUpload() {
        if (uploadDisposable != null && !uploadDisposable.isDisposed()) {
            uploadDisposable.dispose();
        }
    }

    private RequestBody createMultipartBody(String filePath, FlowableEmitter<Double> emitter) {
        File file = new File(filePath);
        return createCountingRequestBody(file, emitter);
    }

    private RequestBody createRequestBody(File file) {
        return RequestBody.create(MediaType.parse(getMimeType(file.getAbsolutePath())), file);
    }

    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private RequestBody createCountingRequestBody(File file, FlowableEmitter<Double> emitter) {
        RequestBody requestBody = createRequestBody(file);
        return new CountingRequestBody(requestBody, (bytesWritten, contentLength) -> {
            double progress = (1.0 * bytesWritten) / contentLength;
            emitter.onNext(progress);
        });
    }

    @Override
    public void onHandShake() {

    }
}
