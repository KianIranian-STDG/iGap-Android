package net.iGap.helper.upload.ApiBased;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import androidx.collection.ArrayMap;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import net.iGap.G;
import net.iGap.helper.HelperSetAction;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class UploadWorkerManager {

    private static volatile UploadWorkerManager instance = null;

    private ArrayMap<String, WorkRequest> pendingUploadTasks;
    private ArrayMap<String, WorkRequest> pendingCompressTasks;
    private Context context;
    Constraints constraints;

    private static final String TAG = "UploadApiManager http";

    public static void initial(Context context) {
        UploadWorkerManager localInstance = instance;
        if (localInstance == null) {
            synchronized (UploadWorkerManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new UploadWorkerManager(context);
                }
            }
        }
    }

    public static UploadWorkerManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Upload Worker Manager is not initialized properly.  The most "
                    + "likely cause is that you forgot to initial it in your application class");
        }
        return instance;
    }

    public UploadWorkerManager(Context context) {

        pendingUploadTasks = new ArrayMap<>();
        pendingCompressTasks = new ArrayMap<>();
        this.context = context;

//        WorkManager.initialize(context, new Configuration.Builder()
//                .setExecutor(Executors.newFixedThreadPool(3))
//                .build());

        constraints = new Constraints.Builder()
//                .setRequiresBatteryNotLow(true)
//                .setRequiresStorageNotLow(true)
//                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

    }

    public void upload(WorkRequest uploadTask) {
        if (uploadTask == null)
            return;
        pendingUploadTasks.put(uploadTask.getId().toString(), uploadTask);
        addUploadWorker(uploadTask);
    }

    private void addUploadWorker(WorkRequest worker) {
        WorkManager.getInstance(context).enqueue(worker);

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(worker.getId()).observeForever(workInfo -> {
            if (workInfo != null) {
                Log.d(TAG, "onChanged: " + workInfo.getState().name() + workInfo.getProgress().getInt(UploadWorker.PROGRESS, -1));
            }
        });
    }

    public void uploadMessageAndSend(ProtoGlobal.Room.Type roomType, RealmRoomMessage message) {
        uploadMessageAndSend(roomType, message, G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE).getInt(SHP_SETTING.KEY_COMPRESS, 1) != 1);
    }

    private void uploadMessageAndSend(ProtoGlobal.Room.Type roomType, RealmRoomMessage message, boolean ignoreCompress) {
        Log.d("bagi", "uploadMessageAndSend" + message.getMessageId());
        Log.d(TAG, "UploadWorkerManager: " + pendingCompressTasks.size() + " " + pendingUploadTasks.size());
        if (message.isManaged()) {
            uploadMessageAndSend(
                    roomType,
                    DbManager.getInstance().doRealmTask(realm -> {
                        return realm.copyFromRealm(message);
                    }),
                    ignoreCompress
            );
            return;
        }

        if (message.getForwardMessage() != null ||
                message.getAttachment() == null ||
                message.getMessageType() == ProtoGlobal.RoomMessageType.STICKER ||
                message.getMessageType() == ProtoGlobal.RoomMessageType.CONTACT
        ) {
            new ChatSendMessageUtil().build(roomType, message.getRoomId(), message);
            return;
        }

        Log.d(TAG, "uploadMessageAndSend222");
        String savePathVideoCompress = G.DIR_TEMP + "/VIDEO_" + message.getMessageId() + ".mp4";
        File compressFile = new File(savePathVideoCompress);
        File CompletedCompressFile = new File(savePathVideoCompress.replace(".mp4", "_finish.mp4"));

        if (!CompletedCompressFile.exists() && !ignoreCompress
                && (message.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO
                || message.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO_TEXT)) {
            if (pendingCompressTasks.containsKey(message.getMessageId() + ""))
                return;

            Log.d(TAG, "uploadMessageAndSend33");
            if (compressFile.exists()) {
                compressFile.delete();
            }

            WorkRequest compressWork =
                    new OneTimeWorkRequest.Builder(CompressWorker.class)
                            .setConstraints(constraints)
                            .setBackoffCriteria(
                                    BackoffPolicy.EXPONENTIAL,
                                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                    TimeUnit.MILLISECONDS)
                            .addTag(String.valueOf(message.getMessageId()))
                            .addTag("Compress")
                            .setInputData(new Data.Builder()
                                    .putString(CompressWorker.COMPRESS_ID, String.valueOf(message.getMessageId()))
                                    .putString(CompressWorker.COMPRESS_NEW_PATH, savePathVideoCompress)
                                    .putString(CompressWorker.COMPRESS_ORIGINAL_PATH, message.getAttachment().getLocalFilePath())
                                    .build())
                            .build();

            WorkManager.getInstance(context).enqueue(compressWork);
            pendingCompressTasks.put(message.getMessageId() + "", compressWork);

            WorkManager.getInstance(context).getWorkInfoByIdLiveData(compressWork.getId()).observeForever(workInfo -> {
                if (workInfo != null) {
                    switch (workInfo.getState()) {
                        case RUNNING:
                            EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_COMPRESS, String.valueOf(message.getMessageId()), workInfo.getProgress().getInt(UploadWorker.PROGRESS, -1));
                            break;
                        case SUCCEEDED:
                            Log.d("bagi", "onCompressFinish" + message.getMessageId());
                            if (compressFile.exists() && compressFile.length() < (new File(message.getAttachment().getLocalFilePath())).length()) {
                                compressFile.renameTo(CompletedCompressFile);
                                EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_COMPRESS, String.valueOf(message.getMessageId()), 100);

                                uploadMessageAndSend(roomType, message, ignoreCompress);
                            } else {
                                if (compressFile.exists()) {
                                    compressFile.delete();
                                }

                                EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_COMPRESS, String.valueOf(message.getMessageId()), 100);
                                uploadMessageAndSend(roomType, message, true);
                            }
                            break;
                        case FAILED:
                            if (compressFile.exists()) {
                                compressFile.delete();
                            }
                            uploadMessageAndSend(roomType, message, true);
                            break;
                    }
                    Log.d(TAG, "onChanged compress: " + workInfo.getState().name() + workInfo.getProgress().getInt(UploadWorker.PROGRESS, -1));
                }
            });
            /*CompressTask compressTask = new CompressTask(message.getMessageId() + "", message.getAttachment().getLocalFilePath(), savePathVideoCompress, new OnCompress() {
                @Override
                public void onCompressProgress(String id, int percent) {
                    Log.d("bagi", "onCompressProgress" + percent);
                    EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_COMPRESS, id, percent);
                }

                @Override
                public void onCompressFinish(String id, boolean compress) {

                    Log.d("bagi", "onCompressFinish" + message.getMessageId());
                    if (compress && compressFile.exists() && compressFile.length() < (new File(message.getAttachment().getLocalFilePath())).length()) {
                        compressFile.renameTo(CompletedCompressFile);
                        EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_COMPRESS, id, 100);

                        uploadMessageAndSend(roomType, message, ignoreCompress);
                    } else {
                        if (compressFile.exists()) {
                            compressFile.delete();
                        }

                        EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_COMPRESS, id, 100);
                        uploadMessageAndSend(roomType, message, true);
                    }
                }
            });*/
            return;
        }
        WorkRequest compressTask = pendingCompressTasks.remove(message.getMessageId() + "");
        if ((message.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO ||
                message.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO_TEXT) &&
                !ignoreCompress &&
                (compressTask == null || !CompletedCompressFile.exists()))
            return;

        Log.d("bagi", "after Compress");
        String fileAdd;
        if (CompletedCompressFile.exists()) {
            fileAdd = CompletedCompressFile.getAbsolutePath();
        } else {
            fileAdd = message.getAttachment().getLocalFilePath();
        }
        WorkRequest uploadWork =
                new OneTimeWorkRequest.Builder(UploadWorker.class)
                        .setConstraints(constraints)
                        .setBackoffCriteria(
                                BackoffPolicy.EXPONENTIAL,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS)
                        .addTag(String.valueOf(message.getMessageId()))
                        .addTag("Upload")
                        .setInputData(new Data.Builder()
                                .putString(UploadWorker.UPLOAD_IDENTITY, String.valueOf(message.getMessageId()))
                                .putString(UploadWorker.UPLOAD_ROOM_ID, String.valueOf(message.getRoomId()))
                                .putInt(UploadWorker.UPLOAD_TYPE, message.getMessageType().getNumber())
                                .putString(UploadWorker.UPLOAD_FILE_ADDRESS, fileAdd)
                                .build())
                        .build();

        if (!pendingUploadTasks.containsKey(uploadWork.getId().toString())) {
            pendingUploadTasks.put(String.valueOf(message.getMessageId()), uploadWork);
            WorkManager.getInstance(context).enqueue(uploadWork);
            HelperSetAction.setActionFiles(message.getRoomId(), message.getMessageId(), HelperSetAction.getAction(message.getMessageType()), roomType);
            EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_PROGRESS, message.getMessageId() + "", 1);
        }

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(uploadWork.getId()).observeForever(workInfo -> {
            if (workInfo != null) {
                switch (workInfo.getState()) {
                    case RUNNING:
                        EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_PROGRESS, String.valueOf(message.getMessageId()), workInfo.getProgress().getInt(UploadWorker.PROGRESS, -1));
                        break;
                    case SUCCEEDED:
                        Log.d("bagi", "uploadMessageAndSendonFinish");
                        String token = workInfo.getOutputData().getString(UploadWorker.UPLOAD_TOKEN);
                        if (CompletedCompressFile.exists()) {
                            CompletedCompressFile.delete();
                        }

                        HelperSetAction.sendCancel(message.getMessageId());

                        DbManager.getInstance().doRealmTransaction(realm -> {
                            RealmAttachment.updateToken(message.getMessageId(), token);
                        });

                        /**
                         * this code should exist in under of other codes in this block
                         */
                        if (message.getReplyTo() == null) {
                            new ChatSendMessageUtil().newBuilder(roomType, message.getMessageType(), message.getRoomId())
                                    .attachment(token)
                                    .message(message.getMessage())
                                    .sendMessage(message.getMessageId() + "");
                        } else {

                            new ChatSendMessageUtil().newBuilder(roomType, message.getMessageType(), message.getRoomId())
                                    .replyMessage(message.getReplyTo().getMessageId())
                                    .attachment(token)
                                    .message(message.getMessage())
                                    .sendMessage(message.getMessageId() + "");
                        }


                        pendingUploadTasks.remove(String.valueOf(message.getMessageId()));
                        break;
                    case FAILED:
                        Log.d("bagi", "uploadMessageAndSendError");
                        String id = String.valueOf(message.getMessageId());
                        pendingUploadTasks.remove(id);
                        HelperSetAction.sendCancel(Long.parseLong(id));
                        makeFailed(id);
                        break;
                }
                Log.d(TAG, "onChanged upload: " + workInfo.getState().name() + workInfo.getProgress().getInt(UploadWorker.PROGRESS, -1));
            }
        });
        /*OnUploadListener onUploadListener = new OnUploadListener() {
            @Override
            public void onProgress(String id, int progress) {
                Log.d("bagi", progress + "uploadMessageAndSend2");
                EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_PROGRESS, id, progress);
            }

            @Override
            public void onFinish(String id, String token) {
                Log.d("bagi", "uploadMessageAndSendonFinish");
                if (CompletedCompressFile.exists()) {
                    CompletedCompressFile.delete();
                }

                HelperSetAction.sendCancel(message.getMessageId());

                DbManager.getInstance().doRealmTransaction(realm -> {
                    RealmAttachment.updateToken(message.getMessageId(), token);
                });

                *//**
         * this code should exist in under of other codes in this block
         *//*
                if (message.getReplyTo() == null) {
                    new ChatSendMessageUtil().newBuilder(roomType, message.getMessageType(), message.getRoomId())
                            .attachment(token)
                            .message(message.getMessage())
                            .sendMessage(message.getMessageId() + "");
                } else {

                    new ChatSendMessageUtil().newBuilder(roomType, message.getMessageType(), message.getRoomId())
                            .replyMessage(message.getReplyTo().getMessageId())
                            .attachment(token)
                            .message(message.getMessage())
                            .sendMessage(message.getMessageId() + "");
                }


                pendingUploadTasks.remove(id);
            }

            @Override
            public void onError(String id) {
                Log.d("bagi", "uploadMessageAndSendError");
                pendingUploadTasks.remove(id);
                HelperSetAction.sendCancel(Long.parseLong(id));
                makeFailed(id);
            }
        };*/
        /*UploadApiTask uploadTask;
        if (CompletedCompressFile.exists()) {
            uploadTask = new UploadApiTask(message, CompletedCompressFile.getAbsolutePath(), onUploadListener);
        } else {
            uploadTask = new UploadApiTask(message, onUploadListener);
        }*/
    }

    /**
     * make messages failed
     */
    private void makeFailed(String identity) {
        // message failed
        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread(() -> makeFailed(identity)).start();
            return;
        }

        DbManager.getInstance().doRealmTransaction(realm -> {
            final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(identity)).findFirst();
            if (message != null) {
                message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
                long finalRoomId = message.getRoomId();
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.refreshRealmUi();
                        G.chatSendMessageUtil.onMessageFailed(finalRoomId, Long.parseLong(identity));
                    }
                });
            }
        });
    }


    public boolean isUploading(String identity) {
        WorkRequest uploadTask = pendingUploadTasks.get(identity);
        return uploadTask != null;
    }

    public boolean isCompressing(String identity) {
        WorkRequest compressTask = pendingCompressTasks.get(identity);
        return compressTask != null;
    }

    public boolean isCompressingOrUploading(String identity) {
        return isUploading(identity) || isCompressing(identity);
    }

    public boolean cancelUploading(String identity) {
        WorkRequest uploadTask = pendingUploadTasks.remove(identity);

        if (uploadTask == null) {
            return false;
        } else {
            Log.d(TAG, "cancelUploading: ");
            WorkManager.getInstance(context).cancelAllWorkByTag(identity);
            return true;
        }
    }

    public boolean cancelCompressing(String identity) {
        WorkRequest compressTask = pendingCompressTasks.remove(identity);
        // cancel compress task
        return compressTask != null;
    }

    public boolean cancelCompressingAndUploading(String identity) {
        return cancelUploading(identity) || cancelCompressing(identity);
    }

    public int getUploadProgress(String identity) {
        WorkRequest uploadTask = pendingUploadTasks.get(identity);
        if (uploadTask != null) {
            return /*uploadTask.getProgressValue()*/0;
        }
        return 1;
    }

    public int getCompressProgress(String identity) {
        WorkRequest compressTask = pendingCompressTasks.get(identity);
        if (compressTask != null) {
            return /*compressTask.getProgress()*/0;
        }
        return 1;
    }

}
