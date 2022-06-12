package net.iGap.module.upload;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.api.apiService.TokenContainer;
import net.iGap.helper.DispatchQueue;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperDataUsage;
import net.iGap.helper.HelperSetAction;
import net.iGap.helper.OkHttpClientInstance;
import net.iGap.helper.upload.UploadRequestBody;
import net.iGap.module.AndroidUtils;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRegisteredInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import shadow.org.apache.commons.io.FilenameUtils;

import static net.iGap.api.apiService.ApiStatic.FILE;

/**
 * Upload Service
 * A foreground service that receives an upload object
 * and start uploading it's data to igap server in background.
 * Every time that service starts, takes upload objects
 * one after one in onStartCommand and put them to upload queue
 * to upload them respectively.
 * More relevant information like as user id, peer id, room id and user account
 * comes with upload object and store in service fields to use for sending
 * message process.
 * <p>
 * upload object: current upload object receive as a json string in onStartCommand
 * and cast to model object.
 * peer id: long peer id to sending process
 * user id: long user id
 * room id: long room id
 * user account: int user account to handling multi account
 * <p>
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UploadService extends Service implements EventManager.EventDelegate {

    private static final String TAG = "uploadService";
    public static final String UPLOAD_NOTIFICATION_CHANNEL_ID = "UPLOAD_NOTIFICATION_CHANNEL";
    public static final String UPLOAD_NOTIFICATION_NAME = "upload notification channel";
    private static final int ONGOING_NOTIFICATION_ID = 100;
    public static final int UPLOAD_SERVICE_INTENT_REQUEST_CODE = 1000;
    private LocalBinder mBinder = new LocalBinder();
    private LinkedBlockingQueue<ServiceUploadObject> mUploadQueue = new LinkedBlockingQueue();
    private UploadObject mUploadObject;
    private UploadHttpRequest.UploadDelegate mUploadCallback;
    private Notification.Builder mNotificationBuilder;
    private NotificationManager mNotificationManager;
    private long mUserId;
    private long mPeerId;
    private long mRoomId;
    private String mPeerName;
    private String md5Key;
    private boolean isResume;
    private SharedPreferences mSharedPreferences;
    //    private String userToken;
    private OkHttpClient client;
    private Call requestCall;
    private volatile boolean isUploading;
    private boolean shouldResumeLastUpload = false;
    private int storeTime;
    private int resumeRetryCount;
    private int mUploadQueueSize = 0;
    private int mCurrentUploadNumber = 1;
    private boolean anyFileIsUploading = false;
    private int mUserSelectedAccount;
    private DispatchQueue mServiceThreadQueue = new DispatchQueue("serviceThreadQueue");
    private AtomicBoolean cancelDownload = new AtomicBoolean(false);

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = G.context.getSharedPreferences("file_info", Context.MODE_PRIVATE);
//        userToken = TokenContainer.getInstance().getToken();
        client = OkHttpClientInstance.getInstance();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        EventManager.getInstance(mUserSelectedAccount).addObserver(EventManager.ON_UPLOAD_CANCEL, this);
        EventManager.getInstance(mUserSelectedAccount).addObserver(EventManager.CONNECTION_STATE_CHANGED, this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Receive new upload object with other required data such as roomId, peerId and etc in this method.
     * Received data makes an new service upload object and stores in service upload queue.
     * Service upload objects are Peeked one after another and are handled
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE).edit().putBoolean(SHP_SETTING.KEY_IS_UPLOAD_SERVICE_RUN, true).apply();
        EventManager.getInstance(mUserSelectedAccount).addObserver(EventManager.ON_UPLOAD_CANCEL, this);
        EventManager.getInstance(mUserSelectedAccount).addObserver(EventManager.CONNECTION_STATE_CHANGED, this);
        if (intent != null) {
            UploadObject inputUploadObject = new Gson().fromJson(intent.getStringExtra("jsonObject"), UploadObject.class);
            long userId = intent.getLongExtra("userId", 0);
            long peerId = intent.getLongExtra("peerId", 0);
            long roomId = intent.getLongExtra("roomId", 0);
            int selectedAccount = intent.getIntExtra("selectedAccount", 0);
            ServiceUploadObject serviceUploadObject = new ServiceUploadObject(inputUploadObject, userId, peerId, roomId, selectedAccount, getPeerName(peerId));

            if (!isExistUploadObjectInQueue(serviceUploadObject)) {
                mUploadQueue.add(serviceUploadObject);
                mUploadQueueSize++;
            }
        }

        if (!anyFileIsUploading && !mUploadQueue.isEmpty()) {
            anyFileIsUploading = true;
            setNewUploadObject();
            getServiceNotificationBuilder();
            startForeground(ONGOING_NOTIFICATION_ID, mNotificationBuilder.build());
            mServiceThreadQueue.postRunnable(this::startUploadProcess);
        } else {
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    public void setUploadServiceCallback(UploadHttpRequest.UploadDelegate uploadCallback) {
        this.mUploadCallback = uploadCallback;
    }

    @SuppressLint({"WrongConstant", "ResourceType"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getServiceNotificationBuilder() {
        PendingIntent pendingIntent = getNotificationPendingIntent();
        mNotificationBuilder = new Notification.Builder(this, createNotificationChannelAndGetChannelId())
                .setContentText(getString(R.string.igap_is_uploading_file))
                .setSmallIcon(R.mipmap.icon)
                .setContentIntent(pendingIntent);
    }

    private PendingIntent getNotificationPendingIntent() {
        Intent notificationIntent = new Intent(this, ActivityMain.class);
        notificationIntent.setAction("uploadService");
        Bundle bundle = new Bundle();
        bundle.putLong("peerId", mPeerId);
        bundle.putLong("roomId", mRoomId);
        bundle.putLong("userId", mUserId);
        notificationIntent.putExtras(bundle);
        return PendingIntent.getActivity(this, UPLOAD_SERVICE_INTENT_REQUEST_CODE, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannelAndGetChannelId() {
        NotificationChannel notificationChannel = new NotificationChannel(UPLOAD_NOTIFICATION_CHANNEL_ID, UPLOAD_NOTIFICATION_NAME, NotificationManager.IMPORTANCE_LOW);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
        return UPLOAD_NOTIFICATION_CHANNEL_ID;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startUploadProcess() {
        md5Key = AndroidUtils.MD5(mUploadObject.fileName);
        String token = null;
        if (mSharedPreferences.getLong("user_id_" + md5Key + mUserId, 0) == mUserId) {
            isResume = mSharedPreferences.getLong("offset_" + md5Key + mUserId, 0) != 0;
            token = mUploadObject.fileToken = mSharedPreferences.getString("token_" + md5Key + mUserId, null);
        }
        FileLog.i(TAG, "startUploadProcess md5Key " + md5Key + " isResume " + isResume + " token " + token);

        initFile(token);
    }

    /**
     * By calling this method, first is checked whether the file's upload token exists
     * in the app local storage space or not.
     * If there is a token, it means that this file has already been uploaded to the server
     * and the request to upload the file will be sent to the server along with the token.
     * In this case, the server returns the byte value previously uploaded from this file to the server,
     * and we send the rest of the bytes of the file.
     * In the opposite case, if there is no token of the file in the local memory of the app,
     * it means that the file is sent to the server for the first time for upload.
     * In this case, the request to upload the file is sent to the server with a null token,
     * and the server creates a dedicated token file and returns it to us.
     * Again, we return to the first case where we have a file with a token ready to send to the server.
     * Therefore, we send the request to send the file to the server again with the associated token to the server.
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initFile(String token) {
        String url = FILE + "init";

        FileLog.i(TAG, "initFile: " + mUploadObject.toString());

        try {
            if (isResume && token != null) {
                mUploadObject.progress = mSharedPreferences.getInt("progress_" + md5Key + mUserId, 1);

                if (mUploadCallback != null) {
                    mUploadCallback.onUploadProgress(mUploadObject);
                }
                mNotificationBuilder.setProgress(100, mUploadObject.progress, false);
                mNotificationBuilder.setContentTitle(mCurrentUploadNumber + "/" + mUploadQueueSize);
                mNotificationBuilder.setContentText(getString(R.string.uploading_file_to) + " " + mPeerName);
                mNotificationBuilder.setColor(Color.GREEN);
                mNotificationBuilder.setContentIntent(getNotificationPendingIntent());
                mNotificationManager.notify(0, mNotificationBuilder.build());


                Request req = new Request.Builder()
                        .url(url + "/" + token)
                        .addHeader("Authorization", TokenContainer.getInstance().getToken())
                        .build();
                Response res = client.newCall(req).execute();

                if (res.isSuccessful() && res.body() != null) {
                    String resString = res.body().string();
                    FileLog.i(TAG, "initFile: req -> " + url + "/" + token + " res -> " + resString);
                    JSONObject resObject = new JSONObject(resString);
                    mUploadObject.fileToken = resObject.getString("token");
                    mUploadObject.offset = resObject.getLong("uploaded_size");
                    mUploadObject.fileSize = resObject.getLong("size");

                    if (mUploadObject.offset > 0 && mUploadObject.fileSize > 0) {
                        mUploadObject.progress = (int) ((mUploadObject.offset * 100) / mUploadObject.fileSize);
                    }
                    startOrResume();
                } else {
                    int resCode = res.code();
                    FileLog.i(TAG, "initFile: req -> " + url + "/" + token + " res error -> " + resCode);
                    if (resCode == 407) {
                        mSharedPreferences.edit().remove("user_id_" + md5Key + mUserId).remove("offset_" + md5Key + mUserId).remove("token_" + md5Key + mUserId).remove("progress_" + md5Key + mUserId).apply();
                        isResume = false;
                        startUploadProcess();
                    } else if (resCode == 408) {
                        if (resumeRetryCount < 5) {
                            try {
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        resumeRetryCount++;
                                        isResume = mSharedPreferences.getLong("offset_" + md5Key + mUserId, 0) != 0;
                                        String token = mSharedPreferences.getString("token_" + md5Key + mUserId, null);
                                        initFile(token);
                                    }
                                }, 5000);
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        } else {
                            if (mUploadCallback != null) {
                                mUploadCallback.onUploadFail(mUploadObject, new Exception("resumeRetryCount max limited"));
                            }
                            modifyNotification(getString(R.string.upload_failed), 0, 0, false, Color.RED);
                            handleUploadQueue(mUploadObject.messageId);
                        }
                    } else if (resCode == 406) {
                        mSharedPreferences.edit().remove("user_id_" + md5Key + mUserId).remove("offset_" + md5Key + mUserId).remove("token_" + md5Key + mUserId).remove("progress_" + md5Key + mUserId).apply();
                        mUploadObject.fileToken = token;
                        UploadObject uploadObjectForSend = mUploadObject;
                        AndroidUtils.globalQueue.postRunnable(() -> {
                            if (mUploadCallback != null) {
                                mUploadCallback.onUploadFinish(uploadObjectForSend);
                            } else {
                                HelperDataUsage.increaseUploadFiles(uploadObjectForSend.messageType);
                                HelperDataUsage.progressUpload(uploadObjectForSend.fileSize, uploadObjectForSend.messageType);

                                if (uploadObjectForSend.messageObject != null) {

                                    HelperSetAction.sendCancel(uploadObjectForSend.messageId);

                                    DbManager.getInstance().doRealmTransaction(realm -> RealmAttachment.updateToken(uploadObjectForSend.messageId, uploadObjectForSend.fileToken));
                                    sendMessage(uploadObjectForSend);
                                }
                            }
                        });
                        mNotificationBuilder.setContentTitle("");
                        modifyNotification(getString(R.string.upload_finished), 0, 0, false, Color.GREEN);
                        handleUploadQueue(mUploadObject.messageId);
                    }
                    FileLog.e("UploadHttpRequest " + req.toString() + " res -> " + res.code());
                }
            } else if (token == null) {
                String name = FilenameUtils.getName(mUploadObject.fileName);
                String size = String.valueOf(mUploadObject.fileSize);
                String extension = FilenameUtils.getExtension(mUploadObject.fileName);
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
                        .addHeader("Authorization", TokenContainer.getInstance().getToken())
                        .build();

                FileLog.i(TAG, req.toString() + " size " + size + " name " + name + " extension " + extension);

                Response res = client.newCall(req).execute();

                if (res.isSuccessful() && res.body() != null) {
                    String resString = res.body().string();

                    JSONObject resObject = new JSONObject(resString);
                    mUploadObject.fileToken = resObject.getString("token");

                    FileLog.i(TAG, "initFile: " + url + " res -> " + resString);

                    startOrResume();
                } else if (res.body() != null) {
                    if (res.code() == 451) {
                        if (mUploadCallback != null) {
                            mUploadCallback.onUploadFail(mUploadObject, new Exception("451 error for -> " + req.toString()));
                        }
                        modifyNotification(getString(R.string.upload_failed), 0, 0, false, Color.RED);
                        handleUploadQueue(mUploadObject.messageId);
                    }
                    String resString = res.body().string();
                    FileLog.i(TAG, req.toString() + " res -> " + resString);
                }
            }
        } catch (IOException e) {
            G.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EventManager.getInstance(mUserSelectedAccount).postEvent(EventManager.ON_UPLOAD_ERROR_IN_SERVICE, e, true, mUploadObject);
                }
            });
            modifyNotification(getString(R.string.upload_failed), 0, 0, false, Color.RED);
            handleUploadQueue(mUploadObject.messageId);

        } catch (JSONException e) {

            G.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EventManager.getInstance(mUserSelectedAccount).postEvent(EventManager.ON_UPLOAD_ERROR_IN_SERVICE, e, true, mUploadObject);
                }
            });
            modifyNotification(getString(R.string.upload_failed), 0, 0, false, Color.RED);
            handleUploadQueue(mUploadObject.messageId);

        } catch (NullPointerException e) {

            G.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EventManager.getInstance(mUserSelectedAccount).postEvent(EventManager.ON_UPLOAD_ERROR_IN_SERVICE, e, true, mUploadObject);
                }
            });
            modifyNotification(getString(R.string.upload_failed), 0, 0, false, Color.RED);
            handleUploadQueue(mUploadObject.messageId);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startOrResume() {
        if (mUploadObject.fileToken == null) {
            FileLog.i(TAG, "startOrResume: " + mUploadObject.key);
            return;
        }

        isUploading = true;
        storeTime = 0;
        resumeRetryCount = 0;

        String url = FILE + "upload/" + mUploadObject.fileToken;

        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = secureRandom.generateSeed(16);
        mUploadObject.auth = G.symmetricKeyString.getBytes();

        try (FileInputStream fileInputStream = new FileInputStream(mUploadObject.file); ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(iv)) {
            if (mUploadObject.offset > 0 && isResume) {
                try {
                    fileInputStream.skip(mUploadObject.offset);
                } catch (IOException e) {
                    mUploadObject.offset = 0;
                    FileLog.e(e);
                }
            }

            try (InputStream inputStream = new SequenceInputStream(byteArrayInputStream, new CipherInputStream(fileInputStream, getCipher(iv)))) {
                RequestBody requestBody = new UploadRequestBody(null, mUploadObject.offset, inputStream, new UploadRequestBody.IOnProgressListener() {
                    @Override
                    public void onProgress(long totalByte) {
                        if (cancelDownload.get() && isUploading) {
                            isUploading = false;

                            G.runOnUiThread(() -> EventManager.getInstance(mUserSelectedAccount).postEvent(EventManager.ON_UPLOAD_ERROR_IN_SERVICE, new Exception("Upload Canceled"), false, mUploadObject));
                            modifyNotification(getString(R.string.upload_failed), 0, 0, false, Color.RED);
                            handleUploadQueue(mUploadObject.messageId);
                            return;
                        }

                        int progress = (int) ((totalByte * 100) / mUploadObject.file.length());
                        if (mUploadObject.progress < progress) {
                            mUploadObject.progress = progress;

                            FileLog.i(TAG, mUploadObject.fileToken + " progress -> " + mUploadObject.progress + " bytes -> " + totalByte);

                            if (mUploadCallback != null) {
                                mUploadCallback.onUploadProgress(mUploadObject);
                            }

                            mNotificationBuilder.setProgress(100, mUploadObject.progress, false);
                            mNotificationBuilder.setContentTitle(mCurrentUploadNumber + "/" + mUploadQueueSize);
                            mNotificationBuilder.setContentText(getString(R.string.uploading_file_to) + " " + mPeerName);
                            mNotificationBuilder.setColor(Color.GREEN);
                            mNotificationBuilder.setContentIntent(getNotificationPendingIntent());
                            mNotificationManager.notify(0, mNotificationBuilder.build());

                            if (storeTime >= 3) {
                                storeTime = 0;
                                mSharedPreferences.edit()
                                        .putLong("user_id_" + md5Key + mUserId, mUserId)
                                        .putLong("offset_" + md5Key + mUserId, totalByte)
                                        .putString("token_" + md5Key + mUserId, mUploadObject.fileToken)
                                        .putInt("progress_" + md5Key + mUserId, mUploadObject.progress)
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
                    if (mUploadObject.message != null) {
                        HelperSetAction.setActionFiles(mUploadObject.message.getRoomId(), mUploadObject.messageId, HelperSetAction.getAction(mUploadObject.messageType), mUploadObject.roomType);
                    }
                });

                Response response = requestCall.execute();

                if (response.isSuccessful() && response.body() != null) {
                    mSharedPreferences.edit().remove("user_id_" + md5Key + mUserId).remove("offset_" + md5Key + mUserId).remove("token_" + md5Key + mUserId).remove("progress_" + md5Key + mUserId).apply();
                    UploadObject uploadObjectForSend = mUploadObject;
                    AndroidUtils.globalQueue.postRunnable(() -> {
                        HelperSetAction.sendCancel(uploadObjectForSend.messageId);

                        if (mUploadCallback != null) {
                            mUploadCallback.onUploadFinish(uploadObjectForSend);
                        } else {
                            HelperDataUsage.increaseUploadFiles(uploadObjectForSend.messageType);
                            HelperDataUsage.progressUpload(uploadObjectForSend.fileSize, uploadObjectForSend.messageType);

                            if (uploadObjectForSend.messageObject != null) {

                                HelperSetAction.sendCancel(uploadObjectForSend.messageId);

                                DbManager.getInstance().doRealmTransaction(realm -> RealmAttachment.updateToken(uploadObjectForSend.messageId, uploadObjectForSend.fileToken));
                                sendMessage(uploadObjectForSend);
                            }
                        }
                    });
                    mNotificationBuilder.setContentTitle("");
                    modifyNotification(getString(R.string.upload_finished), 0, 0, false, Color.GREEN);
                    handleUploadQueue(mUploadObject.messageId);

                } else if (response.body() != null) {
                    if (response.code() >= 500 && response.code() < 600) {
                        mSharedPreferences.edit().remove("user_id_" + md5Key + mUserId).remove("offset_" + md5Key + mUserId).remove("token_" + md5Key + mUserId).remove("progress_" + md5Key + mUserId).apply();
                    }

                    G.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            G.runOnUiThread(() -> {
                                try {
                                    EventManager.getInstance(mUserSelectedAccount).postEvent(EventManager.ON_UPLOAD_ERROR_IN_SERVICE, new Exception(response.body().string()), false, mUploadObject);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    });
                    modifyNotification(getString(R.string.upload_failed), 0, 0, false, Color.RED);
                    handleUploadQueue(mUploadObject.messageId);
                }
            } catch (Exception e) {

                G.runOnUiThread(() -> EventManager.getInstance(mUserSelectedAccount).postEvent(EventManager.ON_UPLOAD_ERROR_IN_SERVICE, e, false, mUploadObject));
                modifyNotification(getString(R.string.upload_failed), 0, 0, false, Color.RED);
                handleUploadQueue(mUploadObject.messageId);

                /**Below code snip is for connecting and disconnecting internet and should be implement in future */
//                if (isInternetConnected()) {
//                    modifyNotification(getString(R.string.upload_failed), 0, 0, false, Color.RED);
//                    handleUploadQueue(mUploadObject.messageId);
//                } else {
//                    internetDisconnect();
//                }
            }
        } catch (FileNotFoundException e) {
            FileLog.e(e);
        } catch (IOException e) {
            FileLog.e(e);
        }
    }

    @org.jetbrains.annotations.Nullable
    private Cipher getCipher(byte[] iv) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES_256/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKey key2 = new SecretKeySpec(mUploadObject.auth, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key2, ivSpec);

        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
        return cipher;
    }

    /**
     * This method is called when one upload process is doned or failed and checks if another object
     * exist for upload, start process from beginning again with it.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleUploadQueue(long messageId) {
//        if (!shouldResumeLastUpload) {
        anyFileIsUploading = false;
        stopForeground(true);
        client.dispatcher().executorService().shutdown();
        if (requestCall != null) {
            requestCall.cancel();
        }
        if (isInternetConnected()) {
            for (ServiceUploadObject serviceUploadObject : mUploadQueue) {
                if (serviceUploadObject.uploadObject.messageId == messageId) {
                    mUploadQueue.remove(serviceUploadObject);
                }
            }
            if (!mUploadQueue.isEmpty()) {
                anyFileIsUploading = true;
                mCurrentUploadNumber++;
                setNewUploadObject();
                getServiceNotificationBuilder();
                startForeground(ONGOING_NOTIFICATION_ID, mNotificationBuilder.build());
                mServiceThreadQueue.postRunnable(this::startUploadProcess);

            } else {
                mCurrentUploadNumber = 1;
                mUploadQueueSize = 0;
                G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE).edit().putBoolean(SHP_SETTING.KEY_IS_UPLOAD_SERVICE_RUN, false).apply();
                stopSelf();
            }
        }
        /**Related on connecting and disconnecting internet handling*/
//        }
//        else {
//            shouldResumeLastUpload = false;
//            getServiceNotificationBuilder();
//            startForeground(ONGOING_NOTIFICATION_ID, mNotificationBuilder.build());
//            startUploadProcess();
//        }
    }

    /**
     * This method get the service upload object witch is on top of upload queue and
     * set relevant fields.
     */
    private void setNewUploadObject() {
        ServiceUploadObject serviceUploadObject = mUploadQueue.peek();
        mUploadObject = serviceUploadObject.uploadObject;
        mUserId = serviceUploadObject.userId;
        mPeerId = serviceUploadObject.peerId;
        mRoomId = serviceUploadObject.roomId;
        mPeerName = serviceUploadObject.peerName;
        mUserSelectedAccount = serviceUploadObject.selectedAccount;
    }

    private String getPeerName(long peerId) {
        RealmRegisteredInfo currentPeerInfo = DbManager.getInstance().doRealmTask(new DbManager.RealmTaskWithReturn<RealmRegisteredInfo>() {
            @Override
            public RealmRegisteredInfo doTask(Realm realm) {
                return realm.where(RealmRegisteredInfo.class).equalTo("id", peerId).findFirst();
            }
        });
        if (currentPeerInfo != null) {
            return currentPeerInfo.getDisplayName();
        }
        return "";
    }

    private void sendMessage(UploadObject fileObject) {
        if (fileObject == null || fileObject.messageObject == null) {
            return;
        }

        if (fileObject.messageObject.replayToMessage == null) {
            ChatSendMessageUtil.getInstance(mUserSelectedAccount).newBuilder(fileObject.roomType, fileObject.messageType, fileObject.messageObject.roomId)
                    .attachment(fileObject.fileToken)
                    .message(fileObject.messageObject.message)
                    .sendMessage(fileObject.messageId + "");
        } else {
            ChatSendMessageUtil.getInstance(mUserSelectedAccount).newBuilder(fileObject.roomType, fileObject.messageType, fileObject.messageObject.roomId)
                    .replyMessage(fileObject.messageObject.replayToMessage.id)
                    .attachment(fileObject.fileToken)
                    .message(fileObject.messageObject.message)
                    .sendMessage(fileObject.messageObject.id + "");
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void modifyNotification(String contentText, int maxProgress, int progress, boolean indeterminate, int color) {
        mNotificationBuilder.setContentText(contentText);
        mNotificationBuilder.setProgress(maxProgress, progress, indeterminate);
        mNotificationBuilder.setColor(color);
        mNotificationManager.notify(0, mNotificationBuilder.build());
    }

    private boolean isExistUploadObjectInQueue(ServiceUploadObject serviceUploadObject) {
        for (ServiceUploadObject uploadObject : mUploadQueue) {
            if (uploadObject.uploadObject.path.equalsIgnoreCase(serviceUploadObject.uploadObject.path)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else
            return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void internetDisconnect() {
        shouldResumeLastUpload = true;
        client.dispatcher().executorService().shutdown();
        requestCall.cancel();
        modifyNotification("Check internet connection", 100, 0, true, Color.RED);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void receivedEvent(int id, int account, Object... args) {

        if (id == EventManager.ON_UPLOAD_CANCEL) {
            mUploadQueueSize--;
            long canceledMessageId = (long) args[0];
//            /** long -1 means that user change his account and service should be stop to prevent crash*/
//            if (canceledMessageId == -1) {
//                if(mUploadObject != null) {
//                    G.runOnUiThread(() -> EventManager.getInstance(mUserSelectedAccount).postEvent(EventManager.ON_UPLOAD_ERROR_IN_SERVICE, new IOException(), false, mUploadObject));
//                }
//                anyFileIsUploading = false;
//                client.dispatcher().executorService().shutdownNow();
//                if (requestCall != null) {
//                    requestCall.cancel();
//                }
//                if (!mUploadQueue.isEmpty()) {
//                    for (ServiceUploadObject serviceUploadObject : mUploadQueue) {
//                        G.runOnUiThread(() -> EventManager.getInstance(mUserSelectedAccount).postEvent(EventManager.ON_UPLOAD_ERROR_IN_SERVICE, new IOException(), false, serviceUploadObject.uploadObject));
//                    }
//                }
//                while (mUploadQueue.size() != 0) {
//                    mUploadQueue.remove();
//                }
//                if(mNotificationBuilder != null) {
//                    mNotificationBuilder.setContentTitle("");
//                    modifyNotification(getString(R.string.upload_failed), 0, 0, false, Color.RED);
//                }
//                stopForeground(true);
//
//                mCurrentUploadNumber = 1;
//                mUploadQueueSize = 0;
//                G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE).edit().putBoolean(SHP_SETTING.KEY_IS_UPLOAD_SERVICE_RUN, false).apply();
//                G.runOnUiThread(() -> EventManager.getInstance(mUserSelectedAccount).postEvent(EventManager.ON_UPLOAD_SERVICE_STOPPED));
//                stopSelf();
//            } else {

            if (isMyServiceRunning(UploadService.class)) {
                if (mUploadObject != null) {
                    if (canceledMessageId == mUploadObject.messageId) {
                        modifyNotification(getString(R.string.upload_canceled), 0, 0, false, Color.RED);
                        handleUploadQueue(canceledMessageId);

                    } else {
                        for (ServiceUploadObject serviceUploadObject : mUploadQueue) {
                            if (serviceUploadObject.uploadObject.messageId == canceledMessageId) {
                                mUploadQueue.remove(serviceUploadObject);
                            }
                        }
                    }
                } else {
                    stopSelf();
                }
            }
//            }
        }
        /**Related on connect and disconnect internet handling*/
//        else if (id == EventManager.CONNECTION_STATE_CHANGED) {
//            ConnectionState state = (ConnectionState) args[0];
//            if (state == ConnectionState.IGAP) {
//                if (mUploadObject != null) {
//                    getServiceNotificationBuilder();
//                    startForeground(ONGOING_NOTIFICATION_ID, mNotificationBuilder.build());
//                    mServiceThreadQueue.postRunnable(this::startUploadProcess);
//                }
//            }
//        }

    }

    /**
     * This class is service custom class for store an upload object with other required data all together
     */
    public class ServiceUploadObject {
        UploadObject uploadObject;
        long userId;
        long peerId;
        long roomId;
        int selectedAccount;
        String peerName;

        public ServiceUploadObject(UploadObject uploadObject, long userId, long peerId, long roomId, int selectedAccount, String peerName) {
            this.uploadObject = uploadObject;
            this.userId = userId;
            this.peerId = peerId;
            this.roomId = roomId;
            this.peerName = peerName;
            this.selectedAccount = selectedAccount;
        }
    }

    /**
     * Returns the instance of the service
     */
    public class LocalBinder extends Binder {
        public UploadService getServiceInstance() {
            return UploadService.this;
        }
    }

}
