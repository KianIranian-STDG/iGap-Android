package net.iGap.module.upload;

import android.content.Context;
import android.content.SharedPreferences;

import net.iGap.G;
import net.iGap.api.apiService.TokenContainer;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperSetAction;
import net.iGap.helper.OkHttpClientInstance;
import net.iGap.helper.upload.ApiBased.HttpUploader;
import net.iGap.helper.upload.UploadRequestBody;
import net.iGap.module.AndroidUtils;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.eventbus.EventManager;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;
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

import static net.iGap.api.apiService.ApiStatic.FILE;

public class UploadHttpRequest implements EventManager.EventDelegate {
    public String key;
    public UploadObject fileObject;
    private String md5Key;
    private OkHttpClient client;
    private int resumeRetryCount;
    private UploadDelegate delegate;
    private boolean isResume;
    private Call requestCall;

    private volatile boolean isUploading;
    private AtomicBoolean cancelDownload = new AtomicBoolean(false);
    private String TAG = "UploadHttpRequest";
    private String userToken;
    private SharedPreferences preferences;
    private int storeTime;


    public interface UploadDelegate {
        void onUploadProgress(UploadObject fileObject);

        void onUploadFinish(UploadObject fileObject);

        void onUploadFail(UploadObject fileObject, @Nullable Exception e);
    }

    public UploadHttpRequest(UploadObject fileObject, UploadDelegate delegate) {
        this.delegate = delegate;
        this.fileObject = fileObject;
        this.key = fileObject.key;

        userToken = TokenContainer.getInstance().getToken();
        client = OkHttpClientInstance.getInstance();
        preferences = G.context.getSharedPreferences("file_info", Context.MODE_PRIVATE);

        FileLog.i(TAG, "UploadHttpRequest create: " + fileObject.toString());
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.ON_UPLOAD_ERROR_IN_SERVICE, this);
    }

    public void startUploadProcess() {
        md5Key = AndroidUtils.MD5(fileObject.fileName);

        isResume = preferences.getLong("offset_" + md5Key, 0) != 0;
        String token = fileObject.fileToken = preferences.getString("token_" + md5Key, null);

        FileLog.i(TAG, "startUploadProcess md5Key " + md5Key + " isResume " + isResume + " token " + token);

        initFile(token);
    }

    private void initFile(String token) {
        String url = FILE + "init";

        FileLog.i(TAG, "initFile: " + fileObject.toString());

        try {
            if (isResume && token != null) {
                fileObject.progress = preferences.getInt("progress_" + md5Key, 1);

                if (delegate != null) {
                    delegate.onUploadProgress(fileObject);
                }

                Request req = new Request.Builder()
                        .url(url + "/" + token)
                        .addHeader("Authorization", userToken)
                        .build();

                Response res = client.newCall(req).execute();

                if (res.isSuccessful() && res.body() != null) {
                    String resString = res.body().string();
                    FileLog.i(TAG, "initFile: req -> " + url + "/" + token + " res -> " + resString);
                    JSONObject resObject = new JSONObject(resString);
                    fileObject.fileToken = resObject.getString("token");
                    fileObject.offset = resObject.getLong("uploaded_size");
                    fileObject.fileSize = resObject.getLong("size");

                    if (fileObject.offset > 0 && fileObject.fileSize > 0) {
                        fileObject.progress = (int) ((fileObject.offset * 100) / fileObject.fileSize);
                    }
                    startOrResume();
                } else {
                    int resCode = res.code();
                    FileLog.i(TAG, "initFile: req -> " + url + "/" + token + " res error -> " + resCode);
                    if (resCode == 407) {
                        preferences.edit().remove("offset_" + md5Key).remove("token_" + md5Key).remove("progress_" + md5Key).apply();
                        isResume = false;
                        startUploadProcess();
                    } else if (resCode == 408) {
                        if (resumeRetryCount < 5) {
                            try {
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        resumeRetryCount++;
                                        isResume = preferences.getLong("offset_" + md5Key, 0) != 0;
                                        String token = preferences.getString("token_" + md5Key, null);
                                        initFile(token);
                                    }
                                }, 5000);
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        } else {
                            if (delegate != null) {
                                delegate.onUploadFail(fileObject, new Exception("resumeRetryCount max limited"));
                            }
                        }
                    } else if (resCode == 406) {
                        preferences.edit().remove("offset_" + md5Key).remove("token_" + md5Key).remove("progress_" + md5Key).apply();
                        fileObject.fileToken = token;

                        AndroidUtils.globalQueue.postRunnable(() -> {
                            if (delegate != null) {
                                delegate.onUploadFinish(fileObject);
                            }
                        });
                    }
                    FileLog.e("UploadHttpRequest " + req.toString() + " res -> " + res.code());
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

                FileLog.i(TAG, req.toString() + " size " + size + " name " + name + " extension " + extension);

                Response res = client.newCall(req).execute();

                if (res.isSuccessful() && res.body() != null) {
                    String resString = res.body().string();

                    JSONObject resObject = new JSONObject(resString);
                    fileObject.fileToken = resObject.getString("token");

                    FileLog.i(TAG, "initFile: " + url + " res -> " + resString);

                    startOrResume();
                } else if (res.body() != null) {
                    if (res.code() == 451 || res.code() == 413) {
                        if (delegate != null) {
                            delegate.onUploadFail(fileObject, new Exception("451 error for -> " + req.toString()));
                        }
                    }
                    preferences.edit().remove("offset_" + md5Key).remove("token_" + md5Key).remove("progress_" + md5Key).apply();
                    String resString = res.body().string();
                    FileLog.i(TAG, req.toString() + " res -> " + resString);
                }
            }
        } catch (IOException e) {
            error(e, true);
        } catch (JSONException e) {
            error(e, true);
        } catch (NullPointerException e) {
            error(e, false);
        }
    }

    private void startOrResume() {
        if (fileObject.fileToken == null) {
            FileLog.i(TAG, "startOrResume: " + fileObject.key);
            return;
        }

        isUploading = true;
        storeTime = 0;
        resumeRetryCount = 0;

        String url = FILE + "upload/" + fileObject.fileToken;

        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = secureRandom.generateSeed(16);
        fileObject.auth = G.symmetricKeyString.getBytes();

        try (FileInputStream fileInputStream = new FileInputStream(fileObject.file); ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(iv)) {
            if (fileObject.offset > 0 && isResume) {
                try {
                    long skip = fileInputStream.skip(fileObject.offset);
                } catch (IOException e) {
                    fileObject.offset = 0;
                    FileLog.e(e);
                }
            }

            try (InputStream inputStream = new SequenceInputStream(byteArrayInputStream, new CipherInputStream(fileInputStream, getCipher(iv)))) {
                RequestBody requestBody = new UploadRequestBody(null, fileObject.offset, inputStream, new UploadRequestBody.IOnProgressListener() {
                    @Override
                    public void onProgress(long totalByte) {
                        if (cancelDownload.get() && isUploading) {
                            isUploading = false;
                            error(new Exception("Upload Canceled"), false);
                            return;
                        }
                        int progress = (int) ((totalByte * 100) / fileObject.file.length());
                        if (fileObject.progress < progress) {
                            fileObject.progress = progress;

                            FileLog.i(TAG, fileObject.fileToken + " progress -> " + fileObject.progress + " bytes -> " + totalByte);

                            AndroidUtils.globalQueue.postRunnable(() -> {
                                if (delegate != null) {
                                    delegate.onUploadProgress(fileObject);
                                }
                            });

                            if (storeTime >= 3) {
                                storeTime = 0;
                                preferences.edit().putLong("offset_" + md5Key, totalByte)
                                        .putString("token_" + md5Key, fileObject.fileToken)
                                        .putInt("progress_" + md5Key, fileObject.progress)
                                        .apply();
                            }

                            storeTime++;
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        FileLog.i(TAG, "Error from stream " + e.getMessage());
                    }
                });

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .addHeader("Authorization", TokenContainer.getInstance().getToken())
                        .build();

                requestCall = client.newCall(request);

                AndroidUtils.globalQueue.postRunnable(() -> {
                    // FIXME: 10/12/2020 remove to SendMessageUtil
                    if (fileObject.message != null) {
                        HelperSetAction.setActionFiles(fileObject.message.getRoomId(), fileObject.messageId, HelperSetAction.getAction(fileObject.messageType), fileObject.roomType);
                    }
                });

                Response response = requestCall.execute();

                if (response.isSuccessful() && response.body() != null) {
                    preferences.edit().remove("offset_" + md5Key).remove("token_" + md5Key).remove("progress_" + md5Key).apply();

                    AndroidUtils.globalQueue.postRunnable(() -> {
                        HelperSetAction.sendCancel(fileObject.messageId);

                        if (delegate != null) {
                            delegate.onUploadFinish(fileObject);
                        }
                    });
                } else if (response.body() != null) {
                    if (response.code() >= 500 && response.code() < 600) {
                        preferences.edit().remove("offset_" + md5Key).remove("token_" + md5Key).remove("progress_" + md5Key).apply();
                    }
                    error(new Exception(response.body().string()), false);
                }
            } catch (Exception e) {
                error(e, false);
            }
        } catch (FileNotFoundException e) {
            FileLog.e(e);
        } catch (IOException e) {
            FileLog.e(e);
        }
    }

    private void error(Exception exception, boolean needReset) {
        if (exception == null) {
            return;
        }

        cancelDownload.set(true);

        isUploading = false;
        HelperSetAction.sendCancel(fileObject.messageId);

        FileLog.i(TAG, "-----------------------------------------------------------------------------------");
        FileLog.e("UploadHttpRequest md5 Reset " + needReset + " file size after error -> " + new File(fileObject.path).length());
        FileLog.e("UploadHttpRequest CustomException \n " + fileObject.toString() + "\n", exception);

        if (exception.getMessage() != null && !exception.getMessage().equals("Canceled") && needReset) {
            preferences.edit().remove("offset_" + md5Key).remove("token_" + md5Key).remove("progress_" + md5Key).apply();
        }

        FileLog.i(TAG, "-----------------------------------------------------------------------------------");

        if (delegate == null) {
            delegate = HttpUploader.getInstance();
        }
        delegate.onUploadFail(fileObject, exception);
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
            SecretKey key2 = new SecretKeySpec(fileObject.auth, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key2, ivSpec);

        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
        return cipher;
    }

    @Override
    public void receivedEvent(int id, int account, Object... args) {
        if (id == EventManager.ON_UPLOAD_ERROR_IN_SERVICE) {

            Exception exception = (Exception) args[0];
            boolean inputBoolean = (boolean) args[1];
            UploadObject uploadObject = (UploadObject) args[2];

            fileObject = uploadObject;
            if (preferences == null) {
                preferences = G.context.getSharedPreferences("file_info", Context.MODE_PRIVATE);
            }
            if (cancelDownload == null) {
                cancelDownload = new AtomicBoolean(false);
            }
            error(exception, inputBoolean);
        }
    }


}