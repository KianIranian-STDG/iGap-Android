package net.iGap.helper.upload.ApiBased;

import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import net.iGap.G;
import net.iGap.helper.HelperSetAction;
import net.iGap.helper.upload.CompressTask;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.downloader.FileIOExecutor;
import net.iGap.module.upload.IUpload;
import net.iGap.module.upload.UploadHttpRequest;
import net.iGap.module.upload.UploadObject;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRoomMessage;
import net.igap.video.compress.OnCompress;

import java.io.File;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static android.content.Context.MODE_PRIVATE;

public class HttpUploader implements IUpload {

    private static volatile HttpUploader instance = null;

    private Queue<UploadHttpRequest> uploadQueue = new LinkedBlockingQueue<>();
    private HashMap<String, UploadHttpRequest> inProgressUploads = new HashMap<>();
    private ArrayMap<String, CompressTask> pendingCompressTasks = new ArrayMap<>();
    private AtomicInteger inProgressCount = new AtomicInteger(0);
    private ThreadPoolExecutor mThreadPoolExecutor;

    private FileIOExecutor fileExecutors;

    private static final int MAX_UPLOAD = 6;

    private static final String TAG = "UploadApiManager";

    public static HttpUploader getInstance() {
        HttpUploader localInstance = instance;
        if (localInstance == null) {
            synchronized (HttpUploader.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new HttpUploader();
                }
            }
        }
        return localInstance;
    }

    public HttpUploader() {
        fileExecutors = FileIOExecutor.getInstance();


        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

        mThreadPoolExecutor = new ThreadPoolExecutor(
                NUMBER_OF_CORES,   // Initial pool size
                NUMBER_OF_CORES + 3,   // Max pool size
                3,       // Time idle thread waits before terminating
                TimeUnit.SECONDS,  // Sets the Time Unit for KEEP_ALIVE_TIME
                new LinkedBlockingDeque<>());  // Work Queue


    }

    private void makeFailed(long messageId) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread(() -> makeFailed(messageId)).start();
            return;
        }

        DbManager.getInstance().doRealmTransaction(realm -> {
            final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo("messageId", messageId).findFirst();
            if (message != null) {
                message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
                long finalRoomId = message.getRoomId();
                G.handler.post(() -> {
                    G.refreshRealmUi();
                    ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).onMessageFailed(finalRoomId, messageId);
                });
            }
        });
    }

    @Override
    public boolean isUploading(String messageId) {
        return inProgressUploads.get(messageId) != null;
    }

    @Override
    public boolean isCompressing(String identity) {
        CompressTask compressTask = pendingCompressTasks.get(identity);
        return compressTask != null;
    }

    @Override
    public boolean isCompressingOrUploading(String identity) {
        return isUploading(identity) || isCompressing(identity);
    }

    @Override
    public boolean cancelUploading(String messageId) {
        UploadHttpRequest request = findExistedRequest(messageId);
        if (request == null) {
            return false;
        } else {
            request.cancelUpload();
            return true;
        }
    }

    @Override
    public boolean cancelCompressing(String identity) {
        return true;
    }

    @Override
    public boolean cancelCompressingAndUploading(String identity) {
        return cancelUploading(identity) || cancelCompressing(identity);
    }

    @Override
    public int getUploadProgress(String messageId) {
        UploadHttpRequest request = inProgressUploads.get(messageId);
        if (request == null) {
            return 1;
        }
        return request.fileObject.progress;
    }

    @Override
    public int getCompressProgress(String identity) {
        return 1;
    }

    @Override
    public void upload(UploadObject fileObject) {
        if (validForCompress(fileObject) && ignoreCompress()) {
            final String savePathVideoCompress = G.DIR_TEMP + "/VIDEO_" + fileObject.messageId + ".mp4";
            final File compressFile = new File(savePathVideoCompress);
            final File completedCompressFile = new File(savePathVideoCompress.replace(".mp4", "_finish.mp4"));

            if (!completedCompressFile.exists() && ignoreCompress()) {
                if (pendingCompressTasks.containsKey(fileObject.messageId + ""))
                    return;
                CompressTask compressTask = new CompressTask(fileObject.messageId + "", fileObject.message.attachment.localFilePath, savePathVideoCompress, new OnCompress() {
                    @Override
                    public void onCompressProgress(String id, int percent) {
                        EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_COMPRESS, id, percent);
                    }

                    @Override
                    public void onCompressFinish(String id, boolean compress) {
                        if (compress && compressFile.exists() && compressFile.length() < (new File(fileObject.message.attachment.localFilePath)).length()) {
                            compressFile.renameTo(completedCompressFile);
                            fileObject.file = completedCompressFile;
                        } else {
                            if (compressFile.exists()) {
                                compressFile.delete();
                            }
                        }
                        EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_COMPRESS, id, 100);
                        pendingCompressTasks.remove(fileObject.messageId + "");

                        startUpload(fileObject, completedCompressFile);
                    }
                });

                if (compressFile.exists()) {
                    compressFile.delete();
                }

                pendingCompressTasks.put(fileObject.messageId + "", compressTask);
                mThreadPoolExecutor.execute(compressTask);
            }
        } else {
            startUpload(fileObject, null);
        }
    }

    private void startUpload(UploadObject fileObject, final File completedCompressFile) {
        UploadHttpRequest existedRequest = findExistedRequest(fileObject.key);
        if (existedRequest == null) {
            existedRequest = new UploadHttpRequest(fileObject, new UploadHttpRequest.UploadDelegate() {
                @Override
                public void onUploadProgress(UploadObject fileObject) {
                    EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_PROGRESS, fileObject.key, fileObject.progress);
                }

                @Override
                public void onUploadFinish(UploadObject fileObject) {
                    UploadHttpRequest req = inProgressUploads.get(fileObject.key);
                    if (req != null) {
                        inProgressUploads.remove(fileObject.key);
                        inProgressCount.decrementAndGet();
                    }

                    if (completedCompressFile != null && completedCompressFile.exists()) {
                        completedCompressFile.delete();
                    }

                    HelperSetAction.sendCancel(fileObject.messageId);

                    DbManager.getInstance().doRealmTransaction(realm -> RealmAttachment.updateToken(fileObject.messageId, fileObject.fileToken));

                    if (fileObject.messageType == ProtoGlobal.RoomMessageType.STICKER || fileObject.messageType == ProtoGlobal.RoomMessageType.CONTACT) {
                        ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).build(fileObject.roomType, fileObject.message.roomId, fileObject.message);
                    } else if (fileObject.message.replyTo == null) {
                        ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).newBuilder(fileObject.roomType, fileObject.messageType, fileObject.message.getRoomId())
                                .attachment(fileObject.fileToken)
                                .message(fileObject.message.getMessage())
                                .sendMessage(fileObject.messageId + "");
                    } else {
                        ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).newBuilder(fileObject.roomType, fileObject.messageType, fileObject.message.getRoomId())
                                .replyMessage(fileObject.message.replyTo.messageId)
                                .attachment(fileObject.fileToken)
                                .message(fileObject.message.message)
                                .sendMessage(fileObject.message.messageId + "");
                    }

                    scheduleNewUpload();
                }

                @Override
                public void onUploadFail(UploadObject fileObject, @Nullable Exception e) {
                    UploadHttpRequest req = inProgressUploads.get(fileObject.key);
                    if (req != null) {
                        inProgressUploads.remove(fileObject.key);
                        inProgressCount.decrementAndGet();
                    }

                    scheduleNewUpload();

                    if (inProgressCount.get() == 0)
                        HelperSetAction.sendCancel(fileObject.messageId);

                    makeFailed(fileObject.messageId);
                }
            });

            uploadQueue.add(existedRequest);
            scheduleNewUpload();
        }
    }

    private UploadHttpRequest findExistedRequest(String key) {
        for (UploadHttpRequest request : uploadQueue) {
            if (request.key.equals(key))
                return request;
        }

        return inProgressUploads.get(key);
    }

    private void scheduleNewUpload() {
        if (inProgressCount.get() >= MAX_UPLOAD || uploadQueue.size() == 0)
            return;

        Log.i(TAG, "scheduleNewUpload inProgressCount: " + inProgressCount.get());

        UploadHttpRequest request = uploadQueue.poll();
        if (request == null)
            return;

        inProgressUploads.put(request.key, request);
        inProgressCount.incrementAndGet();
        fileExecutors.execute(request::startUploadProcess);
    }

    private boolean ignoreCompress() {
        return G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE).getInt(SHP_SETTING.KEY_COMPRESS, 0) == 1;
    }

    private boolean validForCompress(UploadObject fileObject) {
        return fileObject.messageType == ProtoGlobal.RoomMessageType.VIDEO || fileObject.messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT;
    }
}
