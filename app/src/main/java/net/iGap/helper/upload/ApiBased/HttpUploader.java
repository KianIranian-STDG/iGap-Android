package net.iGap.helper.upload.ApiBased;

import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import net.iGap.G;
import net.iGap.controllers.MessageController;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperDataUsage;
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
import net.iGap.network.NetworkUtility;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoStoryUserAddNew;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmStory;
import net.iGap.realm.RealmStoryProto;
import net.iGap.structs.MessageObject;
import net.igap.video.compress.OnCompress;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
    public static boolean isMultiUpload = false;
    public static boolean isStoryUploading = false;
    private FileIOExecutor fileExecutors;

    private final int MAX_UPLOAD;

    private static final String TAG = "UploadHttpRequest";

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

        MAX_UPLOAD = NetworkUtility.getMaxConcurrency(G.context);
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
        return findExistedRequest(messageId) != null;
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
        for(String taskId: pendingCompressTasks.keySet()){
            if (taskId.equals(messageId)) {
                Objects.requireNonNull(pendingCompressTasks.get(taskId)).setCancel();
            }
        }
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
                CompressTask compressTask = new CompressTask(fileObject.messageId + "", fileObject.path, savePathVideoCompress, new OnCompress() {
                    @Override
                    public void onCompressProgress(String id, int percent) {
                        G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.ON_UPLOAD_COMPRESS, id, percent));
                    }

                    @Override
                    public void onCompressFinish(String id, boolean compress) {
                        try {
                            File originalFile = new File(fileObject.path);

                            if (compress && compressFile.exists() && compressFile.length() < originalFile.length()) {
                                compressFile.renameTo(completedCompressFile);
                                fileObject.file = completedCompressFile;
                                fileObject.fileSize = completedCompressFile.length();
                            } else if (compressFile.exists()) {
                                compressFile.delete();
                            }
                            G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.ON_UPLOAD_COMPRESS, id, 100, fileObject.fileSize));
                            pendingCompressTasks.remove(fileObject.messageId + "");

                            startUpload(fileObject, completedCompressFile);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
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
        if (fileObject.fileToken != null && completedCompressFile == null) {
            sendMessage(fileObject);
        } else {
            UploadHttpRequest existedRequest = findExistedRequest(fileObject.key);
            if (existedRequest == null) {
                existedRequest = new UploadHttpRequest(fileObject, new UploadHttpRequest.UploadDelegate() {
                    @Override
                    public void onUploadProgress(UploadObject fileObject) {
                        FileLog.i("HttpUploader " + fileObject.fileToken + " progress -> " + fileObject.progress);
                        G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.ON_UPLOAD_PROGRESS, fileObject.key, fileObject.progress, fileObject.fileSize));
                        if (fileObject.onUploadListener != null) {
                            fileObject.onUploadListener.onProgress(String.valueOf(fileObject.messageId), fileObject.progress);
                        }
                    }

                    @Override
                    public void onUploadFinish(UploadObject fileObject) {
                        HelperDataUsage.increaseUploadFiles(fileObject.messageType);
                        HelperDataUsage.progressUpload(fileObject.fileSize, fileObject.messageType);
                        FileLog.i("HttpUploader onUploadFinish " + fileObject.fileToken);
                        UploadHttpRequest req = inProgressUploads.get(fileObject.key);
                        if (req != null) {
                            inProgressUploads.remove(fileObject.key);
                            inProgressCount.decrementAndGet();
                        }
                        if (fileObject.messageObject != null) {
                            if (completedCompressFile != null && completedCompressFile.exists()) {
                                completedCompressFile.delete();
                            }

                            HelperSetAction.sendCancel(fileObject.messageId);

                            DbManager.getInstance().doRealmTransaction(realm -> RealmAttachment.updateToken(fileObject.messageId, fileObject.fileToken));
                            sendMessage(fileObject);
                        }
                        if (fileObject.messageType == ProtoGlobal.RoomMessageType.STORY) {
                            DbManager.getInstance().doRealmTransaction(realm -> {
                                realm.where(RealmStoryProto.class).equalTo("id", fileObject.messageId).findFirst().setFileToken(fileObject.fileToken);

                                if (isMultiUpload) {
                                    if (realm.where(RealmStory.class).equalTo("userId", AccountManager.getInstance().getCurrentUser().getId()).findFirst().getRealmStoryProtos().size() ==
                                            realm.where(RealmStoryProto.class).equalTo("userId", AccountManager.getInstance().getCurrentUser().getId()).isNotNull("fileToken").findAll().size()) {
                                        List<RealmStoryProto> realmStoryProtos = realm.where(RealmStoryProto.class).equalTo("userId", AccountManager.getInstance().getCurrentUser().getId()).equalTo("status", MessageObject.STATUS_SENDING).isNotNull("fileToken").findAll().sort("createdAt");
                                        if (realmStoryProtos != null && realmStoryProtos.size() > 0) {
                                            List<ProtoStoryUserAddNew.StoryAddRequest> storyObjects = new ArrayList<>();
                                            for (int i = 0; i < realmStoryProtos.size(); i++) {
                                                ProtoStoryUserAddNew.StoryAddRequest.Builder storyAddRequest = ProtoStoryUserAddNew.StoryAddRequest.newBuilder();
                                                storyAddRequest.setToken(realmStoryProtos.get(i).getFileToken());
                                                storyAddRequest.setCaption(realmStoryProtos.get(i).getCaption());
                                                storyObjects.add(storyAddRequest.build());
                                            }
                                            isStoryUploading = false;
                                            MessageController.getInstance(AccountManager.selectedAccount).addMyStory(storyObjects);
                                        }

                                    }
                                } else {
                                    List<RealmStoryProto> realmStoryProtos = realm.where(RealmStoryProto.class).equalTo("userId", AccountManager.getInstance().getCurrentUser().getId()).equalTo("status", MessageObject.STATUS_SENDING).isNotNull("fileToken").findAll();
                                    if (realmStoryProtos != null && realmStoryProtos.size() > 0) {
                                        List<ProtoStoryUserAddNew.StoryAddRequest> storyObjects = new ArrayList<>();
                                        for (int i = 0; i < realmStoryProtos.size(); i++) {
                                            ProtoStoryUserAddNew.StoryAddRequest.Builder storyAddRequest = ProtoStoryUserAddNew.StoryAddRequest.newBuilder();
                                            storyAddRequest.setToken(realmStoryProtos.get(i).getFileToken());
                                            storyAddRequest.setCaption(realmStoryProtos.get(i).getCaption());
                                            storyObjects.add(storyAddRequest.build());
                                        }
                                        isStoryUploading = false;
                                        MessageController.getInstance(AccountManager.selectedAccount).addMyStory(storyObjects);
                                    }
                                }


                            });
                        }
                        if (fileObject.onUploadListener != null) {
                            fileObject.onUploadListener.onFinish(String.valueOf(fileObject.messageId), fileObject.fileToken);
                        }
                        scheduleNewUpload();
                    }

                    @Override
                    public void onUploadFail(UploadObject fileObject, @Nullable Exception e) {
                        long uploadedBytes = ((fileObject.fileSize / 100) * fileObject.progress);
                        HelperDataUsage.progressUpload(uploadedBytes, fileObject.messageType);
                        FileLog.e("HttpUploader onUploadFail " + fileObject.fileToken, e);
                        UploadHttpRequest req = inProgressUploads.get(fileObject.key);
                        if (req != null) {
                            inProgressUploads.remove(fileObject.key);
                            inProgressCount.decrementAndGet();
                        }

                        scheduleNewUpload();

                        if (inProgressCount.get() == 0)
                            HelperSetAction.sendCancel(fileObject.messageId);

                        makeFailed(fileObject.messageId);
                        if (fileObject.messageType == ProtoGlobal.RoomMessageType.STORY) {

                            DbManager.getInstance().doRealmTransaction(realm -> {
                                realm.where(RealmStory.class).equalTo("id", AccountManager.getInstance().getCurrentUser().getId()).findFirst().setSentAll(false);
                                realm.where(RealmStoryProto.class).equalTo("id", fileObject.messageId).findFirst().setStatus(MessageObject.STATUS_FAILED);
                                G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.STORY_UPLOADED_FAILED, fileObject.messageId));
                            });
                        }
                        if (fileObject.onUploadListener != null) {
                            fileObject.onUploadListener.onError(String.valueOf(fileObject.messageId));
                        }
                    }
                });

                uploadQueue.add(existedRequest);
                scheduleNewUpload();
            }
        }
    }

    private void sendMessage(UploadObject fileObject) {
        if (fileObject == null || fileObject.messageObject == null) {
            return;
        }

        if (fileObject.messageType == ProtoGlobal.RoomMessageType.STICKER || fileObject.messageType == ProtoGlobal.RoomMessageType.CONTACT) {
            ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).build(fileObject.roomType, fileObject.messageObject.roomId, fileObject.messageObject);
        } else if (fileObject.messageObject.replayToMessage == null) {
            ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).newBuilder(fileObject.roomType, fileObject.messageType, fileObject.messageObject.roomId)
                    .attachment(fileObject.fileToken)
                    .message(fileObject.messageObject.message)
                    .sendMessage(fileObject.messageId + "");
        } else {
            ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).newBuilder(fileObject.roomType, fileObject.messageType, fileObject.messageObject.roomId)
                    .replyMessage(fileObject.messageObject.replayToMessage.id)
                    .attachment(fileObject.fileToken)
                    .message(fileObject.messageObject.message)
                    .sendMessage(fileObject.messageObject.id + "");
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

        UploadHttpRequest request = uploadQueue.poll();
        if (request == null) {
            return;
        }

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
