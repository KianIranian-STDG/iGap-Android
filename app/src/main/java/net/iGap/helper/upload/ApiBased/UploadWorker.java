package net.iGap.helper.upload.ApiBased;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import net.iGap.api.UploadsApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.helper.HelperDataUsage;
import net.iGap.helper.HelperError;
import net.iGap.helper.upload.RequestBodyUtil;
import net.iGap.model.UploadData;
import net.iGap.module.AndroidUtils;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.downloader.AppExecutors;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmUserInfo;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.util.concurrent.CountDownLatch;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import shadow.org.apache.commons.io.FilenameUtils;

public class UploadWorker extends Worker {

    private UploadsApi apiService;
    public String identity;
    private String token = "";
    private String roomID;
    private ProtoGlobal.RoomMessageType uploadType;
    private File file;
    private CompositeDisposable uploadDisposable;
    private FileChannel fileChannel;
    private RandomAccessFile randomAccessFile;
    private boolean isEncryptionActive = true;
    private AppExecutors appExecutors;

    private String TAG = "Upload Worker http";
    static final String PROGRESS = "PROGRESS";
    static final String UPLOAD_IDENTITY = "UPLOAD_IDENTITY";
    static final String UPLOAD_TOKEN = "UPLOAD_TOKEN";
    static final String UPLOAD_ROOM_ID = "UPLOAD_ROOM_ID";
    static final String UPLOAD_TYPE = "UPLOAD_TYPE";
    static final String UPLOAD_FILE_ADDRESS = "UPLOAD_FILE_ADDRESS";
    Data outputData;
    RealmUserInfo info;
    OnProgress onProgress;
    private PipedOutputStream pipedOutputStream;
    private CountDownLatch startLatch;
    private CountDownLatch pipeInitLatch;
    private InputStream stream;

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.appExecutors = AppExecutors.getInstance();
        this.apiService = new RetrofitFactory().getUploadRetrofit();
        onProgress = percent -> {
            Log.d(TAG, "UploadWorker: " + percent);
            EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_COMPRESS, identity, percent.intValue());
            setProgressAsync(new Data.Builder()
                    .putString(UPLOAD_IDENTITY, identity)
                    .putInt(PROGRESS, percent.intValue())
                    .build());
        };
    }

    @NonNull
    @Override
    public Result doWork() {
        // start progress
        Log.d(TAG, "doWork: start");
        setProgressAsync(new Data.Builder()
                .putString(UPLOAD_IDENTITY, identity)
                .putInt(PROGRESS, 0)
                .build());
        //  fetch data from caller
        identity = getInputData().getString(UPLOAD_IDENTITY);
        token = getInputData().getString(UPLOAD_TOKEN) == null ? "" : getInputData().getString(UPLOAD_TOKEN);
        roomID = getInputData().getString(UPLOAD_ROOM_ID);
        uploadType = ProtoGlobal.RoomMessageType.forNumber(getInputData().getInt(UPLOAD_TYPE, -1));
        file = new File(getInputData().getString(UPLOAD_FILE_ADDRESS));
        // set output result
        outputData = new Data.Builder()
                .putString(UPLOAD_IDENTITY, identity)
                .build();
        // start upload process
        try {
            openFile(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "path: " + file.getAbsolutePath());
        Log.d(TAG, "File size: " + file.length());
        return getUploadInfoServer(!token.equals(""));
    }

    private void openFile(String filePath) throws FileNotFoundException {
        if (randomAccessFile == null) {
            randomAccessFile = new RandomAccessFile(new File(filePath), "r");
        }
        if (fileChannel == null || !fileChannel.isOpen()) {
            fileChannel = randomAccessFile.getChannel();
        }
    }

    private Result getUploadInfoServer(boolean isResume) {

        long size = 0;
//        if (isEncryptionActive)
////            size = ((file.length() / 16 + 1) * 16) + 16;
//            size = file.length() + 32;
//        else
        size = file.length();

        /**
         * info is used if we want to send data without encryption
         */
        info = DbManager.getInstance().doRealmTask(realm -> {
            RealmUserInfo info = realm.where(RealmUserInfo.class).findFirst();
            return realm.copyFromRealm(info);
        });

        try {
//             this if body is only for test
            if (true) {
//                Response<ResponseBody> temp = apiService.test(createCountingRequestBody(isResume, file, 0)).execute();
//                if (temp.isSuccessful())
//                    Log.d(TAG, "getUploadInfoServer: success");
//                else
//                    Log.d(TAG, "getUploadInfoServer: fail");
//                return Result.failure();

                uploadFileWithOkHttp(false, 0);
                return Result.failure(outputData);
            }
            Response<UploadData> response = apiService.initUpload(token, String.valueOf(size),
                    FilenameUtils.getBaseName(file.getName()), FilenameUtils.getExtension(file.getName()),
                    roomID, /*String.valueOf(info.getUserId())*/null).execute();
            if (response.isSuccessful()) {
                if (!isResume)
                    token = response.body().getToken();
                int uploadedSize = Integer.parseInt(response.body().getUploadedSize() == null ? "0" : response.body().getUploadedSize());
                if (file.length() - uploadedSize > 0) {
//                                uploadFile(isResume, uploadedSize);
                    setProgressAsync(new Data.Builder()
                            .putString(UPLOAD_IDENTITY, identity)
                            .putInt(PROGRESS, uploadedSize / ((int) file.length()) * 100)
                            .build());
                    return uploadFileWithOkHttp(isResume, uploadedSize);
                } else
                    return Result.failure();
            } else {
                if (response.code() >= 500 && response.code() < 600) {
                    return Result.retry();
                }
                return Result.failure(outputData);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure(outputData);
        }
    }

    private Result uploadFileWithOkHttp(boolean isResume, int offset) {
        OkHttpClient client = new OkHttpClient();
        AssetManager assetManager = getApplicationContext().getAssets();
        try {
            stream = assetManager.open("test.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String url = "http://192.168.10.31:3007/v1/upload2?enc=1&name=" + file.getName();

        try (InputStream inputStream = new CipherInputStream(stream, getCipher())) {
            MediaType mediaType = MediaType.parse("image/jpg; charset=utf-8");
            RequestBody requestBody = RequestBodyUtil.create(mediaType, inputStream, stream.available());
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("userid", "272481237789804022")
                    .build();

            okhttp3.Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return Result.failure(outputData);
            }
            Log.d("POST", response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(outputData);
        }
        return Result.success(outputData);
    }

    private Result uploadFileWithOkHttpPipedStream(boolean isResume, int offset) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.10.31:3007/v1/upload3?enc=1&name=" + file.getName();

        try (PipedInputStream pipedInputStream = new PipedInputStream(1024 * 8)) {
            FileInputStream fileInputStream = new FileInputStream(file);
            pipeInitLatch = new CountDownLatch(1);
            startLatch = new CountDownLatch(1);
            appExecutors.diskIO().execute(() -> {
                try {
                    pipedOutputStream = new PipedOutputStream();
                    pipeInitLatch.countDown();
                    CipherInputStream inputStream = new CipherInputStream(fileInputStream, getCipher());
                    startLatch.await();
                    pipeEncryptedDataToOutputSteam(pipedOutputStream, inputStream);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });

            pipeInitLatch.await();
            pipedInputStream.connect(pipedOutputStream);
            startLatch.countDown();
            MediaType mediaType = MediaType.parse("image/jpg; charset=utf-8");
            RequestBody requestBody = RequestBodyUtil.create(mediaType, pipedInputStream, fileInputStream.available());
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("userid", "272481237789804022")
                    .build();

            okhttp3.Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return Result.failure(outputData);
            }
            Log.d("POST", response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(outputData);
        }
        return Result.success(outputData);
    }

    @WorkerThread
    public void pipeEncryptedDataToOutputSteam(PipedOutputStream pipedOutputStream, CipherInputStream inputStream) throws IOException {
        int count;
        byte[] buffer = new byte[1024 * 4];
        while ((count = inputStream.read(buffer)) != -1) {
            pipedOutputStream.write(buffer, 0, count);
            Log.i(TAG, "pipeEncryptedDataToOutputSteam: " + new String(buffer));
        }
    }

    private File encryptToTemp() {
        Cipher cipher = getCipher();
        if (cipher == null) return null;

        File encryptedFile = new File(file.getParent(), "encrypted.tmp");
        try (InputStream inputStream = new CipherInputStream(new FileInputStream(file), cipher); FileOutputStream fileOutputStream = new FileOutputStream(encryptedFile)) {
            byte[] buffer = new byte[4096];
            int count = 0;
            while ((count = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, count);
            }
            fileOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return encryptedFile;
    }

    @Nullable
    private Cipher getCipher() {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES_256/CBC/PKCS5Padding");
            SecureRandom r = new SecureRandom();
            byte[] ivBytes = "abcdefghijklmnop".getBytes();
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            Log.d(TAG, "initEncrypt: IV " + Base64.encodeToString(ivSpec.getIV(), Base64.DEFAULT));
            SecretKey key2 = new SecretKeySpec("bf3c199c2470cb477d907b1e0917c17b".getBytes(), "AES");
            Log.d(TAG, "initEncrypt: key " + Base64.encodeToString(key2.getEncoded(), Base64.DEFAULT));
            cipher.init(Cipher.ENCRYPT_MODE, key2/*key*/, ivSpec);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return cipher;
    }

    private Result uploadFileNoEncryption(boolean isResume, int offset) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.10.31:3007/v1/upload3?enc=1&name=" + file.getName() + token;

        try (InputStream inputStream = new FileInputStream(file)) {
            MediaType mediaType = MediaType.parse("image/png; charset=utf-8");
            RequestBody requestBody = RequestBodyUtil.create(mediaType, inputStream, 0);
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("userid", "272481237789804022")
                    .build();

            okhttp3.Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return Result.failure(outputData);
            }
            Log.d("POST", response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(outputData);
        }
        return Result.success(outputData);
    }

    private Result uploadFileWithReqBody(boolean isResume, int offset) {

        Response<ResponseBody> response = null;
        try {
            Log.d(TAG, "uploadFileWithReqBody: " + offset + isResume);
            response = apiService.uploadDataReqBodyCall(token,
                    createCountingRequestBody(isResume, file, offset)/*,
                    MediaType.parse(getMimeType(file.getAbsolutePath())).toString()*/, null).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure(outputData);
        }
        try {
            closeFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.isSuccessful()) {
            HelperDataUsage.increaseUploadFiles(uploadType);
            onProgress.onUploadProgress(100.0);
            HelperError.showSnackMessage("finish", true);
            outputData = new Data.Builder()
                    .putString(UPLOAD_IDENTITY, identity)
                    .putString(UPLOAD_TOKEN, token)
                    .build();
            return Result.success(outputData);
        } else {
            HelperError.showSnackMessage("fail", true);
            return Result.failure(outputData);
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

    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private RequestBody createCountingRequestBody(boolean isResume, File file, int offset) {
        RequestBody requestBody = initRequestBody(isResume, file, offset);
        return new CountingRequestBody(requestBody, (bytesWritten, contentLength) -> {
            HelperDataUsage.progressUpload(bytesWritten, uploadType);
            double progress = bytesWritten / contentLength * 100;
            onProgress.onUploadProgress(progress);
        }, file);
//        return new CountingRequestBody(requestBody, (bytesWritten, contentLength) -> {
//            HelperDataUsage.progressUpload(bytesWritten, uploadType);
//            double progress = bytesWritten / contentLength * 100;
//            onProgress.onUploadProgress(progress);
//        });
    }

    private RequestBody createCountingRequestBody2(boolean isResume, File file, int offset) {
        CountingRequestBody test = new CountingRequestBody((bytesWritten, contentLength) -> {
            HelperDataUsage.progressUpload(bytesWritten, uploadType);
            double progress = bytesWritten / contentLength * 100;
            onProgress.onUploadProgress(progress);
        });
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

    public interface OnProgress {
        void onUploadProgress(Double percent);
    }
}
