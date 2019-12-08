package net.iGap.helper.upload;

import android.os.Looper;
import android.util.Log;

import androidx.collection.ArrayMap;

import com.lalongooo.videocompressor.CompressTask;
import com.lalongooo.videocompressor.OnCompress;

import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.eventbus.EventManager;
import net.iGap.helper.HelperSetAction;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.SHP_SETTING;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAttachmentFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

import static android.content.Context.MODE_PRIVATE;

public class UploadManager {
    private static final UploadManager ourInstance = new UploadManager();

    public static UploadManager getInstance() {
        return ourInstance;
    }

    private ThreadPoolExecutor mThreadPoolExecutor;

    private ArrayMap<String, UploadTask> pendingUploadTasks;
    private ArrayMap<String, CompressTask> pendingCompressTasks;

    private UploadManager() {
        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

        mThreadPoolExecutor = new ThreadPoolExecutor(
                NUMBER_OF_CORES,   // Initial pool size
                NUMBER_OF_CORES + 3,   // Max pool size
                3,       // Time idle thread waits before terminating
                TimeUnit.SECONDS,  // Sets the Time Unit for KEEP_ALIVE_TIME
                new LinkedBlockingDeque<>());  // Work Queue

        pendingUploadTasks = new ArrayMap<>();
        pendingCompressTasks = new ArrayMap<>();
    }

    public void upload(UploadTask uploadTask) {
        if (uploadTask == null)
            return;
        pendingUploadTasks.put(uploadTask.identity, uploadTask);
        mThreadPoolExecutor.execute(uploadTask);
    }

    public void uploadMessageAndSend(ProtoGlobal.Room.Type roomType, RealmRoomMessage message) {
        uploadMessageAndSend(roomType, message, G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE).getInt(SHP_SETTING.KEY_COMPRESS, 1) != 1);
    }

    private void uploadMessageAndSend(ProtoGlobal.Room.Type roomType, RealmRoomMessage message, boolean ignoreCompress) {
        Log.d("bagi", "uploadMessageAndSend" + message.getMessageId());
        if (message.isManaged()) {
            uploadMessageAndSend(
                    roomType,
                    DbManager.getInstance().doRealmTask(realm -> { return realm.copyFromRealm(message);}),
                    ignoreCompress
            );
            return;
        }
        Log.d("bagi", "uploadMessageAndSend222");
        String savePathVideoCompress = G.DIR_TEMP + "/VIDEO_" + message.getMessageId() + ".mp4";
        File compressFile = new File(savePathVideoCompress);
        File CompletedCompressFile = new File(savePathVideoCompress.replace(".mp4", "_finish.mp4"));

        if (!CompletedCompressFile.exists() && !ignoreCompress && (message.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO || message.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO_TEXT)) {
            if (pendingCompressTasks.containsKey(message.getMessageId() + ""))
                return;

            Log.d("bagi", "uploadMessageAndSend33");
            CompressTask compressTask = new CompressTask(message.getMessageId() + "", message.getAttachment().getLocalFilePath(), savePathVideoCompress, new OnCompress() {
                @Override
                public void onCompressProgress(String id, int percent) {
                    Log.d("bagi", "onCompressProgress" + percent);
                    EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_COMPRESS, id, percent);
                }

                @Override
                public void onCompressFinish(String id, boolean compress) {

                    Log.d("bagi", "onCompressFinish" + message.getMessageId());
                    if (compressFile.exists()) {
                        compressFile.renameTo(CompletedCompressFile);
                        EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_COMPRESS, id, 100);

                        uploadMessageAndSend(roomType, message, true);
                    }
                }
            });
            if (compressFile.exists()) {
                compressFile.delete();
            }
            pendingCompressTasks.put(message.getMessageId() + "", compressTask);
            mThreadPoolExecutor.execute(compressTask);
            return;
        }
        CompressTask compressTask = pendingCompressTasks.remove(message.getMessageId() + "");
        if ((message.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO ||
                message.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO_TEXT ) &&
                !CompletedCompressFile.exists() &&
                G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE).getInt(SHP_SETTING.KEY_COMPRESS, 1) == 1 &&
                        compressTask == null)
            return;

        Log.d("bagi", "after Compress");
        if (message.getMessageType() == ProtoGlobal.RoomMessageType.STICKER) {
            new ChatSendMessageUtil().build(roomType, message.getRoomId(), message);
            return;
        }

        OnUploadListener onUploadListener = new OnUploadListener() {
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

                DbManager.getInstance().doRealmTask(realm -> {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmAttachment.updateToken(message.getMessageId(), token);
                        }
                    });
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


                pendingUploadTasks.remove(id);
            }

            @Override
            public void onError(String id) {
                Log.d("bagi", "uploadMessageAndSendError");
                pendingUploadTasks.remove(id);
                HelperSetAction.sendCancel(Long.parseLong(id));
                makeFailed(id);

            }
        };
        UploadTask uploadTask;
        if (CompletedCompressFile.exists()) {
            uploadTask = new UploadTask(message, CompletedCompressFile.getAbsolutePath(), onUploadListener);
        } else {
            uploadTask = new UploadTask(message, onUploadListener);
        }

        if (!pendingUploadTasks.containsKey(uploadTask.identity)) {
            pendingUploadTasks.put(uploadTask.identity, uploadTask);
            mThreadPoolExecutor.execute(uploadTask);
            HelperSetAction.setActionFiles(message.getRoomId(), message.getMessageId(), HelperSetAction.getAction(message.getMessageType()), roomType);
            EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_PROGRESS, message.getMessageId() + "", 1);
        }
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

        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
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
                }
            });
        });
    }


    public boolean isUploading(String identity) {
        UploadTask uploadTask = pendingUploadTasks.get(identity);
        return uploadTask != null;
    }

    public boolean isCompressing(String identity) {
        CompressTask compressTask = pendingCompressTasks.get(identity);
        return compressTask != null;
    }

    public boolean isCompressingOrUploading(String identity) {
        return isUploading(identity) || isCompressing(identity);
    }

    public boolean cancelUploading(String identity) {
        UploadTask uploadTask = pendingUploadTasks.remove(identity);

        if (uploadTask == null) {
            return false;
        } else {
            mThreadPoolExecutor.remove(uploadTask);
            uploadTask.cancel();
            return true;
        }
    }

    public boolean cancelCompressing(String identity) {
        CompressTask compressTask = pendingCompressTasks.remove(identity);

        if (compressTask == null) {
            return false;
        } else {
            // cancel compress task
            return true;
        }
    }

    public boolean cancelCompressingAndUploading(String identity) {
        return cancelUploading(identity) || cancelCompressing(identity);
    }

    public int getUploadProgress(String identity) {
        UploadTask uploadTask = pendingUploadTasks.get(identity);
        if (uploadTask != null) {
            return uploadTask.getUploadProgress();
        }
        return 1;
    }

    public int getCompressProgress(String identity) {
        CompressTask compressTask = pendingCompressTasks.get(identity);
        if (compressTask != null) {
            return compressTask.getProgress();
        }
        return 1;
    }
}