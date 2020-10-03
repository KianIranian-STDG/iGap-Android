package net.iGap.helper.upload.ApiBased;

import android.os.Looper;
import android.util.Log;

import net.iGap.G;
import net.iGap.helper.HelperSetAction;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.downloader.FileExecutors;
import net.iGap.module.upload.IUpload;
import net.iGap.module.upload.UploadHttpRequest;
import net.iGap.module.upload.UploadObject;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRoomMessage;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpUploader implements IUpload {

    private static volatile HttpUploader instance = null;

    private Queue<UploadHttpRequest> uploadQueue = new LinkedBlockingQueue<>();
    private HashMap<String, UploadHttpRequest> inProgressUploads = new HashMap<>();
    private AtomicInteger inProgressCount = new AtomicInteger(0);
    private FileExecutors fileExecutors;

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
        fileExecutors = FileExecutors.getInstance();
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
        return false;
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
            return 0;
        }
        return request.fileObject.progress;
    }

    @Override
    public int getCompressProgress(String identity) {
        return 1;
    }

    @Override
    public void upload(UploadObject fileObject) {
        UploadHttpRequest existedRequest = findExistedRequest(fileObject.key);
        if (existedRequest == null) {
            existedRequest = new UploadHttpRequest(AccountManager.selectedAccount, fileObject, new UploadHttpRequest.UploadDelegate() {// FIXME: 10/1/20
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

//                    if (CompletedCompressFile.exists()) {
//                        CompletedCompressFile.delete();
//                    }

                    HelperSetAction.sendCancel(fileObject.messageId);

                    DbManager.getInstance().doRealmTransaction(realm -> {
                        RealmAttachment.updateToken(fileObject.messageId, fileObject.fileToken);
                    });

                    if (fileObject.message.replyTo == null) {
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
                public void onUploadFail(UploadObject fileObject, Exception e) {
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
}
