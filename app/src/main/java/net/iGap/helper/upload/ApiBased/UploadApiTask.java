package net.iGap.helper.upload.ApiBased;

import android.util.Log;
import android.webkit.MimeTypeMap;

import net.iGap.api.UploadsApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.helper.HelperDataUsage;
import net.iGap.helper.HelperError;
import net.iGap.helper.upload.OnUploadListener;
import net.iGap.model.UploadData;
import net.iGap.module.AndroidUtils;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmUserInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import shadow.org.apache.commons.io.FilenameUtils;

public class UploadApiTask extends Thread implements HandShakeCallback {

    private UploadsApi apiService;
    private OnUploadListener listener;
    public String identity;
    private String token = "";
    private String roomID;
    private ProtoGlobal.RoomMessageType uploadType;
    private File file;

    private CompositeDisposable uploadDisposable;

    private FileChannel fileChannel;
    private RandomAccessFile randomAccessFile;

    private int progressValue = 0;

    private static final String TAG = "UploadApiTask http";

    private boolean isEncryptionActive = true;

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

    public UploadApiTask(RealmRoomMessage message, String compressedPath, OnUploadListener onUploadListener) {
        this(message.getMessageId() + "", String.valueOf(message.getRoomId()), new File(compressedPath), message.getMessageType(), onUploadListener);
    }

    @Override
    public void run() {
        super.run();
        try {
            openFile(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "path: " + file.getAbsolutePath());
        Log.d(TAG, "File size: " + file.length());
        getUploadInfoServer(!token.equals(""));
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

        long size = 0;
        if (isEncryptionActive)
            size = ((file.length() / 16 + 1) * 16) + 16;
        else
            size = file.length();

        Log.d(TAG, "getUploadInfoServer: " + file.length() + " " + size);
        if (!isEncryptionActive) {
            Log.d(TAG, "getUploadInfoServer: in V2 init");
            long finalSize = size;
            DbManager.getInstance().doRealmTask(realm -> {
                new ApiInitializer<UploadData>().initAPI(apiService.initUpload(String.valueOf(finalSize),
                        FilenameUtils.getBaseName(file.getName()), FilenameUtils.getExtension(file.getName()),
                        roomID, String.valueOf(realm.where(RealmUserInfo.class).findFirst().getUserId())),
                        this, new ResponseCallback<UploadData>() {
                            @Override
                            public void onSuccess(UploadData data) {
                                if (!isResume)
                                    token = data.getToken();
                                int uploadedSize = Integer.parseInt(data.getUploadedSize() == null ? "0" : data.getUploadedSize());
                                if (file.length() - uploadedSize > 0) {
//                                    uploadFile(isResume, uploadedSize);
                                    uploadFileWithReqBody(isResume, uploadedSize);
                                    listener.onProgress(identity, uploadedSize / ((int) file.length()) * 100);
                                }
                            }

                            @Override
                            public void onError(String error) {
                                listener.onError(identity);
                                synchronized (this) {
                                    notify();
                                }
                            }

                            @Override
                            public void onFailed() {
                                listener.onError(identity);
                                synchronized (this) {
                                    notify();
                                }
                            }
                        });
            });
        } else
            new ApiInitializer<UploadData>().initAPI(apiService.initUpload(String.valueOf(size),
                    FilenameUtils.getBaseName(file.getName()), FilenameUtils.getExtension(file.getName()), roomID, null),
                    this, new ResponseCallback<UploadData>() {
                        @Override
                        public void onSuccess(UploadData data) {
                            if (!isResume)
                                token = data.getToken();
                            int uploadedSize = Integer.parseInt(data.getUploadedSize() == null ? "0" : data.getUploadedSize());
                            if (file.length() - uploadedSize > 0) {
//                                uploadFile(isResume, uploadedSize);
                                uploadFileWithReqBody(isResume, uploadedSize);
                                listener.onProgress(identity, uploadedSize / ((int) file.length()) * 100);
                            }
                        }

                        @Override
                        public void onError(String error) {
                            listener.onError(identity);
                            synchronized (this) {
                                notify();
                            }
                        }

                        @Override
                        public void onFailed() {
                            listener.onError(identity);
                            synchronized (this) {
                                notify();
                            }
                        }
                    });
    }

    private void uploadFile(boolean isResume, int offset) {
        uploadDisposable = new CompositeDisposable();

        Flowable<Double> uploadFlow = Flowable.create(emitter -> {
            try {
                if (!isEncryptionActive) {
                    Log.d(TAG, "getUploadInfoServer: in V2 upload");
                    DbManager.getInstance().doRealmTask(realm -> {
                        apiService.uploadData(token, createWithMultipartBody(isResume, file.getAbsolutePath(), offset, emitter),
                                /*MediaType.parse(getMimeType(file.getAbsolutePath())).toString(),*/
                                String.valueOf(realm.where(RealmUserInfo.class).findFirst().getUserId())).blockingGet();
                        emitter.onComplete();
                    });
                } else {
                    apiService.uploadData(token, createWithMultipartBody(isResume, file.getAbsolutePath(), offset, emitter),
                            /*MediaType.parse(getMimeType(file.getAbsolutePath())).toString(),*/ null).blockingGet();
                    emitter.onComplete();
                }
            } catch (Exception e) {
                emitter.tryOnError(e);
            }
        }, BackpressureStrategy.LATEST);

        uploadDisposable.add(uploadFlow.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<Double>() {
                    @Override
                    public void onNext(Double progress) {
                        progressValue = progress.intValue();
                        listener.onProgress(identity, progressValue);
                    }

                    @Override
                    public void onError(Throwable t) {
                        listener.onError(identity);
                        HelperError.showSnackMessage("fail", true);
                        synchronized (this) {
                            notify();
                        }
                    }

                    @Override
                    public void onComplete() {
                        try {
                            closeFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        HelperDataUsage.increaseUploadFiles(uploadType);
                        listener.onProgress(identity, 100);
                        listener.onFinish(identity, token);
                        HelperError.showSnackMessage("finish", true);
                        synchronized (this) {
                            notify();
                        }
                    }
                }));
    }

    private void uploadFileWithReqBody(boolean isResume, int offset) {
        uploadDisposable = new CompositeDisposable();

        Flowable<Double> uploadFlow = Flowable.create(emitter -> {
            try {
                if (!isEncryptionActive) {
                    Log.d(TAG, "getUploadInfoServer: in V2 upload");
                    DbManager.getInstance().doRealmTask(realm -> {
                        apiService.uploadDataReqBody(token, createWithRequestBody(isResume, file.getAbsolutePath(), offset, emitter),
                                /*MediaType.parse(getMimeType(file.getAbsolutePath())).toString(),*/
                                String.valueOf(realm.where(RealmUserInfo.class).findFirst().getUserId())).blockingGet();
                        emitter.onComplete();
                    });
                } else {
                    apiService.uploadDataReqBody(token, createWithRequestBody(isResume, file.getAbsolutePath(), offset, emitter),
                            /*MediaType.parse(getMimeType(file.getAbsolutePath())).toString(),*/ null).blockingGet();
                    emitter.onComplete();
                }
            } catch (Exception e) {
                emitter.tryOnError(e);
            }
        }, BackpressureStrategy.LATEST);

        uploadDisposable.add(uploadFlow.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<Double>() {
                    @Override
                    public void onNext(Double progress) {
                        progressValue = progress.intValue();
                        listener.onProgress(identity, progressValue);
                    }

                    @Override
                    public void onError(Throwable t) {
                        listener.onError(identity);
                        HelperError.showSnackMessage("fail", true);
                        synchronized (this) {
                            notify();
                        }
                    }

                    @Override
                    public void onComplete() {
                        try {
                            closeFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        HelperDataUsage.increaseUploadFiles(uploadType);
                        listener.onProgress(identity, 100);
                        listener.onFinish(identity, token);
                        HelperError.showSnackMessage("finish", true);
                        synchronized (this) {
                            notify();
                        }
                    }
                }));
    }

    public void cancelUpload() {
        if (uploadDisposable != null && !uploadDisposable.isDisposed()) {
            uploadDisposable.dispose();
        }
    }

    private MultipartBody.Part createWithMultipartBody(boolean isResume, String filePath, int offset, FlowableEmitter<Double> emitter) {
        File file = new File(filePath);
        Log.d(TAG, "createMultipartBody: start");
        MultipartBody.Part temp = MultipartBody.Part.createFormData("upload", file.getName(), createCountingRequestBody(isResume, file, offset, emitter));
        Log.d(TAG, "createMultipartBody: end");
        return temp;
    }

    private RequestBody createWithRequestBody(boolean isResume, String filePath, int offset, FlowableEmitter<Double> emitter) {
        File file = new File(filePath);
        return createCountingRequestBody(isResume, file, offset, emitter);
    }

    private RequestBody initRequestBody(boolean isResume, File file, int offset) {
        if (!isResume)
            return RequestBody.create(MediaType.parse(getMimeType(file.getAbsolutePath())), file);
        else {
            try {
                byte[] bytes = AndroidUtils.getNBytesFromOffset(fileChannel, 0, ((int) file.length()));
                return RequestBody.create(MediaType.parse(getMimeType(file.getAbsolutePath())), bytes, offset, (int) file.length());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private RequestBody createCountingRequestBody(boolean isResume, File file, int offset, FlowableEmitter<Double> emitter) {
        RequestBody requestBody = initRequestBody(isResume, file, offset);
        return new CountingRequestBody(requestBody, (bytesWritten, contentLength) -> {
            HelperDataUsage.progressUpload(bytesWritten, uploadType);
            double progress = (1.0 * (bytesWritten) / ((int) file.length())) * 100;
            emitter.onNext(progress);
        });
    }

    @Override
    public void onHandShake() {

    }

    public int getProgressValue() {
        return progressValue;
    }
}
