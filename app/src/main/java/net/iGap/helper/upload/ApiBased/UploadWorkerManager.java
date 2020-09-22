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
import net.iGap.helper.upload.OnUploadListener;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.upload.IUpload;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.helper.upload.ApiBased.UploadWorker.UPLOAD_IDENTITY;

public class UploadWorkerManager implements IUpload {

    private static volatile UploadWorkerManager instance = null;

    private ArrayMap<String, WorkRequest> pendingUploadTasks;
    private ArrayMap<String, WorkRequest> pendingCompressTasks;
    private Context context;
    Constraints constraints;

    private static final String TAG = "UploadApiManager";

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

    public void upload(WorkRequest uploadTask, OnUploadListener onUploadListener) {
        if (uploadTask == null)
            return;
        pendingUploadTasks.put(uploadTask.getId().toString(), uploadTask);
        addUploadWorker(uploadTask, onUploadListener);
    }

    private void addUploadWorker(WorkRequest worker, OnUploadListener onUploadListener) {
        WorkManager.getInstance(context).enqueue(worker);

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(worker.getId()).observeForever(workInfo -> {
            if (onUploadListener == null)
                return;

            switch (workInfo.getState()) {
                case RUNNING:
                    onUploadListener.onProgress(workInfo.getProgress().getString(UPLOAD_IDENTITY), workInfo.getProgress().getInt(UploadWorker.PROGRESS, 0));
                    break;
                case SUCCEEDED:
                    String token = workInfo.getOutputData().getString(UploadWorker.UPLOAD_TOKEN);
                    pendingUploadTasks.remove(worker.getId().toString());
                    onUploadListener.onFinish(workInfo.getOutputData().getString(UPLOAD_IDENTITY), token);
                    break;
                case FAILED:
                    pendingUploadTasks.remove(worker.getId().toString());
                    onUploadListener.onError(workInfo.getOutputData().getString(UPLOAD_IDENTITY));
                    break;
            }
        });
    }

    @Override
    public void upload(String identity, File file, ProtoGlobal.RoomMessageType type, OnUploadListener onUploadListener) {
        WorkRequest uploadWork =
                new OneTimeWorkRequest.Builder(UploadWorker.class)
                        .setConstraints(constraints)
                        .setBackoffCriteria(
                                BackoffPolicy.EXPONENTIAL,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS)
                        .addTag("Upload")
                        .setInputData(new Data.Builder()
                                .putString(UPLOAD_IDENTITY, identity)
                                .putInt(UploadWorker.UPLOAD_TYPE, type.getNumber())
                                .putString(UploadWorker.UPLOAD_FILE_ADDRESS, file.getAbsolutePath())
                                .build())
                        .build();
        upload(uploadWork, onUploadListener);
    }

    @Override
    public void upload(RealmRoomMessage message, OnUploadListener onUploadListener) {
        upload(message, message.getAttachment().getLocalFilePath(), onUploadListener);
    }

    @Override
    public void upload(RealmRoomMessage message, String uploadedPath, OnUploadListener onUploadListener) {
        WorkRequest uploadWork =
                new OneTimeWorkRequest.Builder(UploadWorker.class)
                        .setConstraints(constraints)
                        .setBackoffCriteria(
                                BackoffPolicy.EXPONENTIAL,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS)
                        .addTag(String.valueOf(message.getMessage()))
                        .addTag("Upload")
                        .setInputData(new Data.Builder()
                                .putString(UPLOAD_IDENTITY, String.valueOf(message.getMessageId()))
                                .putString(UploadWorker.UPLOAD_ROOM_ID, String.valueOf(message.getRoomId()))
                                .putString(UploadWorker.UPLOAD_TOKEN, message.getAttachment() != null ? message.getAttachment().getToken() : "")
                                .putInt(UploadWorker.UPLOAD_TYPE, message.getMessageType().getNumber())
                                .putString(UploadWorker.UPLOAD_FILE_ADDRESS, uploadedPath)
                                .build())
                        .build();
        upload(uploadWork, onUploadListener);
    }

    @Override
    public void uploadMessageAndSend(ProtoGlobal.Room.Type roomType, RealmRoomMessage message) {
        uploadMessageAndSend(roomType, message, G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE).getInt(SHP_SETTING.KEY_COMPRESS, 1) != 1);
    }

    @Override
    public void uploadMessageAndSend(ProtoGlobal.Room.Type roomType, RealmRoomMessage message, boolean ignoreCompress) {
        Log.d(TAG, "uploadMessageAndSend " + message.getMessageId());
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

        Log.d(TAG, "uploadMessageAndSend ");
        String savePathVideoCompress = G.DIR_TEMP + "/VIDEO_" + message.getMessageId() + ".mp4";
        File compressFile = new File(savePathVideoCompress);
        File CompletedCompressFile = new File(savePathVideoCompress.replace(".mp4", "_finish.mp4"));

        if (!CompletedCompressFile.exists() && !ignoreCompress
                && (message.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO
                || message.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO_TEXT)) {
            if (pendingCompressTasks.containsKey(message.getMessageId() + ""))
                return;

            Log.d(TAG, "uploadMessageAndSend ");
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
                            EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_COMPRESS, String.valueOf(message.getMessageId()), workInfo.getProgress().getInt(UploadWorker.PROGRESS, 0));
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
                    Log.d(TAG, "onChanged compress: " + workInfo.getState().name() + workInfo.getProgress().getInt(UploadWorker.PROGRESS, 0));
                }
            });
            return;
        }
        WorkRequest compressTask = pendingCompressTasks.remove(message.getMessageId() + "");
        if ((message.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO ||
                message.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO_TEXT) &&
                !ignoreCompress &&
                (compressTask == null || !CompletedCompressFile.exists()))
            return;

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
                                .putString(UPLOAD_IDENTITY, String.valueOf(message.getMessageId()))
                                .putString(UploadWorker.UPLOAD_ROOM_ID, String.valueOf(message.getRoomId()))
                                .putString(UploadWorker.UPLOAD_TOKEN, message.getAttachment() != null ? message.getAttachment().getToken() : "")
                                .putInt(UploadWorker.UPLOAD_TYPE, message.getMessageType().getNumber())
                                .putString(UploadWorker.UPLOAD_FILE_ADDRESS, fileAdd)
                                .build())
                        .build();

        if (!pendingUploadTasks.containsKey(String.valueOf(message.getMessageId()))) {
            pendingUploadTasks.put(String.valueOf(message.getMessageId()), uploadWork);
            WorkManager.getInstance(context).enqueue(uploadWork);
            HelperSetAction.setActionFiles(message.getRoomId(), message.getMessageId(), HelperSetAction.getAction(message.getMessageType()), roomType);
            EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_PROGRESS, message.getMessageId() + "", 1);
        }

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(uploadWork.getId()).observeForever(workInfo -> {
            if (workInfo != null) {
                switch (workInfo.getState()) {
                    case RUNNING:
                        EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_PROGRESS, String.valueOf(message.getMessageId()), workInfo.getProgress().getInt(UploadWorker.PROGRESS, 0));
                        Log.i(TAG, "progress: " + workInfo.getProgress().getInt(UploadWorker.PROGRESS, 0));
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
                        final String updatedToken = workInfo.getOutputData().getString(UploadWorker.UPLOAD_TOKEN);
                        if (message.getAttachment() != null && updatedToken != null) {
                            DbManager.getInstance().doRealmTransaction(realm -> {
                                RealmAttachment.updateToken(message.getMessageId(), updatedToken);
                            });
                        }

                        String id = String.valueOf(message.getMessageId());
                        pendingUploadTasks.remove(id);
                        HelperSetAction.sendCancel(Long.parseLong(id));
                        makeFailed(id);
                        break;
                }
                Log.d(TAG, "onChanged upload: " + workInfo.getState().name() + " " + workInfo.getProgress().getInt(UploadWorker.PROGRESS, 0));
            }
        });
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

    @Override
    public boolean isUploading(String identity) {
        WorkRequest uploadTask = pendingUploadTasks.get(identity);
        return uploadTask != null;
    }

    @Override
    public boolean isCompressing(String identity) {
        WorkRequest compressTask = pendingCompressTasks.get(identity);
        return compressTask != null;
    }

    @Override
    public boolean isCompressingOrUploading(String identity) {
        return isUploading(identity) || isCompressing(identity);
    }

    @Override
    public boolean cancelUploading(String identity) {
        WorkRequest uploadTask = pendingUploadTasks.remove(identity);

        if (uploadTask == null) {
            return false;
        } else {
            WorkManager.getInstance(context).cancelAllWorkByTag(identity);
            return true;
        }
    }

    @Override
    public boolean cancelCompressing(String identity) {
        WorkRequest compressTask = pendingCompressTasks.remove(identity);
        // cancel compress task
        return compressTask != null;
    }

    @Override
    public boolean cancelCompressingAndUploading(String identity) {
        return cancelUploading(identity) || cancelCompressing(identity);
    }

    @Override
    public int getUploadProgress(String identity) {
        WorkRequest uploadTask = pendingUploadTasks.get(identity);
        if (uploadTask != null) {
            return /*uploadTask.getProgressValue()*/0;
        }
        return 1;
    }

    @Override
    public int getCompressProgress(String identity) {
        WorkRequest compressTask = pendingCompressTasks.get(identity);
        if (compressTask != null) {
            return /*compressTask.getProgress()*/0;
        }
        return 1;
    }

}
