package net.iGap.helper.upload.ApiBased;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.UploadsApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.api.apiService.TokenContainer;
import net.iGap.helper.HelperDataUsage;
import net.iGap.helper.upload.UploadRequestBody;
import net.iGap.model.UploadData;
import net.iGap.proto.ProtoGlobal;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.SequenceInputStream;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Response;
import shadow.org.apache.commons.io.FilenameUtils;

import static net.iGap.api.apiService.ApiStatic.UPLOAD_URL;

public class UploadWorker extends Worker {

    private UploadsApi apiService;
    public String identity;
    private String token = "";
    private String roomID;
    private ProtoGlobal.RoomMessageType uploadType;
    private File file;
    private FileChannel fileChannel;
    private RandomAccessFile randomAccessFile;
    static final String PROGRESS = "PROGRESS";
    static final String UPLOAD_IDENTITY = "UPLOAD_IDENTITY";
    static final String UPLOAD_TOKEN = "UPLOAD_TOKEN";
    static final String UPLOAD_ROOM_ID = "UPLOAD_ROOM_ID";
    static final String UPLOAD_TYPE = "UPLOAD_TYPE";
    static final String UPLOAD_FILE_ADDRESS = "UPLOAD_FILE_ADDRESS";
    private Data outputData;
    private int progress;
    private Call call;

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.apiService = new RetrofitFactory().getUploadRetrofit();
    }

    @NonNull
    @Override
    public Result doWork() {
        // start progress
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
        Data.Builder builder = new Data.Builder()
                .putString(UPLOAD_IDENTITY, identity);
        if (token == null || token.isEmpty()) {
            outputData = builder.build();
        } else {
            outputData = builder
                    .putString(UPLOAD_TOKEN, token)
                    .build();
        }
        // start upload process
        try {
            openFile(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

        long size = file.length();

        try {
            Response<UploadData> response;
            if (token == null || token.isEmpty()) {
                response = apiService.initUpload(String.valueOf(size),
                        FilenameUtils.getBaseName(file.getName()), FilenameUtils.getExtension(file.getName()),
                        roomID, TokenContainer.getInstance().getToken()).execute();
            } else {
                response = apiService.initResumeUpload(token, TokenContainer.getInstance().getToken()).execute();
            }
            if (response.isSuccessful()) {
                if (!isResume && response.body() != null) {
                    token = response.body().getToken();
                    outputData = new Data.Builder()
                            .putString(UPLOAD_IDENTITY, identity)
                            .putString(UPLOAD_TOKEN, token)
                            .build();
                }

                int uploadedSize = Integer.parseInt(response.body() == null || response.body().getUploadedSize() == null ? "0" : response.body().getUploadedSize());
                if (file.length() - uploadedSize > 0) {
                    setProgressAsync(new Data.Builder()
                            .putString(UPLOAD_IDENTITY, identity)
                            .putInt(PROGRESS, uploadedSize / ((int) file.length()) * 100)
                            .build());
                    return uploadFileWithOkHttp(isResume, uploadedSize);
                }
                return Result.failure();
            } else {

                String error = response.errorBody() != null ? response.errorBody().string() : "Unknown Error!";
                if (error.contains("FILE_UPLOAD_STATUS_FAILED") || response.code() == 407) {
                    error = G.context.getString(R.string.error_resume_file_expired);
                } else if (error.contains("FIlE_UPLOADED_COMPLETELY") || response.code() == 406) {
                    return Result.success(outputData);
                }
                return Result.failure(outputData);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure(outputData);
        }
    }

    private Result uploadFileWithOkHttp(boolean isResume, int offset) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();

        String url = UPLOAD_URL + "upload/" + token;

        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = secureRandom.generateSeed(16);

        try (FileInputStream fileInputStream = new FileInputStream(file); ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(iv)) {
            if (isResume && offset > 0)
                fileInputStream.skip(offset);

            try (InputStream inputStream = new SequenceInputStream(byteArrayInputStream, new CipherInputStream(fileInputStream, getCipher(iv)))) {
                RequestBody requestBody = new UploadRequestBody(null, offset, inputStream, totalByte -> {

                    if (progress < (int) ((totalByte * 100) / file.length())) {
                        progress = (int) ((totalByte * 100) / file.length());
                        setProgressAsync(new Data.Builder()
                                .putString(UPLOAD_IDENTITY, identity)
                                .putInt(PROGRESS, progress)
                                .build());
                    }
                });
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .addHeader("Authorization", TokenContainer.getInstance().getToken())
                        .build();


                call = client.newCall(request);
                okhttp3.Response response = call.execute();

                releaseSafely();
                if (!response.isSuccessful()) {
                    return Result.failure(outputData);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Result.failure(outputData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(outputData);
        }
        HelperDataUsage.increaseUploadFiles(uploadType);

        setProgressAsync(new Data.Builder()
                .putString(UPLOAD_IDENTITY, identity)
                .putInt(PROGRESS, 100)
                .build());
        return Result.success(outputData);
    }

    @Nullable
    private Cipher getCipher(byte[] iv) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES_256/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKey key2 = new SecretKeySpec(G.symmetricKeyString.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key2/*key*/, ivSpec);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return cipher;
    }

    private void releaseSafely() {
        try {
            if (fileChannel != null) {
                fileChannel.close();
            }
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileChannel = null;
            randomAccessFile = null;
        }
    }

    @Override
    public void onStopped() {
        super.onStopped();

        if (call != null) {
            call.cancel();
        }
        releaseSafely();
    }
}
