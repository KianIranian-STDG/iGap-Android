package net.iGap.module.upload;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import net.iGap.G;
import net.iGap.api.apiService.TokenContainer;
import net.iGap.helper.OkHttpClientInstance;
import net.iGap.helper.upload.UploadRequestBody;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoGlobal;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import shadow.org.apache.commons.io.FilenameUtils;

import static net.iGap.api.apiService.ApiStatic.UPLOAD_URL;

public class UploadHttpRequest {
    public String key;
    public UploadObject fileObject;
    private String md5Key;
    private OkHttpClient client;
    private ProtoGlobal.RoomMessageType type;
    private UploadDelegate delegate;
    private boolean isResume;
    private Call requestCall;

    private final int currentAccount;
    private volatile boolean isUploading;
    private AtomicBoolean cancelDownload = new AtomicBoolean(false);
    private String TAG = getClass().getSimpleName();
    private String userToken;
    private SharedPreferences preferences;
    private int storeTime;

    public interface UploadDelegate {
        void onUploadProgress(UploadObject fileObject);

        void onUploadFinish(UploadObject fileObject);

        void onUploadFail(UploadObject fileObject, Exception e);
    }

    public UploadHttpRequest(int currentAccount, UploadObject fileObject, UploadDelegate delegate) {
        this.delegate = delegate;
        this.currentAccount = currentAccount;
        this.fileObject = fileObject;
        this.key = fileObject.key;

        userToken = TokenContainer.getInstance().getToken();
        client = OkHttpClientInstance.getInstance();
        preferences = G.context.getSharedPreferences("file_info", Context.MODE_PRIVATE);
    }

    public void startUploadProcess() {
        Log.i(TAG, "start upload: " + key);

        md5Key = AndroidUtils.MD5(fileObject.fileName);

        isResume = preferences.getLong("offset_" + md5Key, 0) != 0;
        String token = preferences.getString("token_" + md5Key, null);

        Log.i(TAG, "UploadHttpRequest: " + md5Key + " resumeCache " + token);

        initFile(token);
    }

    private void initFile(String token) {
        String url = UPLOAD_URL + "init";

        try {
            if (isResume && token != null) {
                Request req = new Request.Builder()
                        .url(url + "/" + token)
                        .addHeader("Authorization", userToken)
                        .build();

                Response res = client.newCall(req).execute();

                if (res.isSuccessful() && res.body() != null) {
                    String resString = res.body().string();
                    JSONObject resObject = new JSONObject(resString);
                    fileObject.fileToken = resObject.getString("token");
                    fileObject.offset = resObject.getLong("uploaded_size");
                    fileObject.fileSize = resObject.getLong("size");

                    if (fileObject.offset > 0 && fileObject.fileSize > 0) {
                        fileObject.progress = (int) ((fileObject.offset * 100) / fileObject.fileSize);
                    }
                    startOrResume();
                } else {// FIXME: 10/3/20 handel exception
                    fileObject.progress = preferences.getInt("progress_" + md5Key, 0);
                    if (delegate != null) {
                        delegate.onUploadProgress(fileObject);
                    }
                }
            } else if (token == null) {
                String name = FilenameUtils.getName(fileObject.file.getName());
                String size = String.valueOf(fileObject.fileSize);
                String extension = FilenameUtils.getExtension(fileObject.file.getName());
                String roomId = String.valueOf(System.currentTimeMillis());

                RequestBody reqBody = new FormBody.Builder()
                        .add("name", name)
                        .add("size", size)
                        .add("extension", extension)
                        .add("roomId", roomId)
                        .build();

                Request req = new Request.Builder()
                        .url(url)
                        .post(reqBody)
                        .addHeader("Authorization", userToken)
                        .build();

                Response res = client.newCall(req).execute();

                if (res.isSuccessful() && res.body() != null) {
                    String resString = res.body().string();

                    JSONObject resObject = new JSONObject(resString);
                    fileObject.fileToken = resObject.getString("token");

                    Log.i(TAG, "init res: " + resString);

                    startOrResume();
                } else if (res.body() != null) {
                    String resString = res.body().string();
                    Log.i(TAG, "init: " + resString);
                }
            }
        } catch (IOException e) {
            error(e);
        } catch (JSONException e) {
            error(e);
        }
    }

    private void startOrResume() {
        isUploading = true;
        storeTime = 0;
        String url = UPLOAD_URL + "upload/" + fileObject.fileToken;

        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = secureRandom.generateSeed(16);

        try (FileInputStream fileInputStream = new FileInputStream(fileObject.file); ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(iv)) {
            if (fileObject.offset > 0 && isResume) {
                long skip = fileInputStream.skip(fileObject.offset);
            }

            try (InputStream inputStream = new SequenceInputStream(byteArrayInputStream, new CipherInputStream(fileInputStream, getCipher(iv)))) {
                RequestBody requestBody = new UploadRequestBody(null, fileObject.offset, inputStream, totalByte -> {
                    if (cancelDownload.get() && isUploading) {
                        isUploading = false;
                        error(new Exception("Download Canceled"));
                        return;
                    }

                    int progress = (int) ((totalByte * 100) / fileObject.file.length());
                    if (fileObject.progress < progress) {
                        fileObject.progress = progress;
                        Log.i(TAG, "upload: " + key + " " + fileObject.progress);

                        if (delegate != null) {
                            delegate.onUploadProgress(fileObject);
                        }

                        if (storeTime >= 5) {
                            storeTime = 0;
                            preferences.edit().putLong("offset_" + md5Key, totalByte)
                                    .putString("token_" + md5Key, fileObject.fileToken)
                                    .putInt("progress_" + md5Key, fileObject.progress)
                                    .apply();

                            Log.i(TAG, "storeShared: " + md5Key + "_offset " + totalByte);
                        }

                        storeTime++;
                    }
                });

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .addHeader("Authorization", TokenContainer.getInstance().getToken())
                        .build();

                requestCall = client.newCall(request);
                Response response = requestCall.execute();

                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "upload: successfully " + response.body().string());
                    preferences.edit().remove("offset_" + md5Key).remove("token_" + md5Key).remove("progress_" + md5Key).apply();

                    if (delegate != null) {
                        delegate.onUploadFinish(fileObject);
                    }
                } else if (response.body() != null) {
                    Log.e(TAG, "upload fail: " + response.body().string());
                    error(new Exception(response.body().string()));
                }
            } catch (Exception e) {
                error(e);
            }
        } catch (Exception e) {
            error(e);
        }
    }

    private void error(Object error) {
        isUploading = false;

        if (error instanceof Exception) {
            Exception exception = (Exception) error;
            Log.e(TAG, "error: " + fileObject.key + " ", exception);

            if (delegate != null) {
                delegate.onUploadFail(fileObject, exception);
            }
        } else {

        }
    }

    public void cancelUpload() {
        cancelDownload.set(true);
        if (requestCall != null) {
            requestCall.cancel();
        }
    }

    @Nullable
    private Cipher getCipher(byte[] iv) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES_256/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKey key2 = new SecretKeySpec(G.symmetricKeyString.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key2, ivSpec);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return cipher;
    }

}
