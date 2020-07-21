package net.iGap.module.upload;

import net.iGap.G;
import net.iGap.helper.upload.ApiBased.UploadWorkerManager;
import net.iGap.helper.upload.OnUploadListener;
import net.iGap.helper.upload.UploadManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;

public class Uploader implements IUpload {
    private static Uploader instance;

    private IUpload uploadThroughProto;
    private IUpload uploadThroughApi;

    private Uploader() {
        uploadThroughProto = new UploadAdapter(UploadManager.getInstance());
        uploadThroughApi = UploadWorkerManager.getInstance();
    }

    public static Uploader getInstance() {
        if (instance == null) {
            synchronized (Uploader.class) {
                if (instance == null) {
                    instance = new Uploader();
                }
            }
        }
        return instance;
    }

    @Override
    public void upload(RealmRoomMessage message, OnUploadListener onUploadListener) {
        getCurrentUploader().upload(message, onUploadListener);
    }

    @Override
    public void upload(RealmRoomMessage message, String compressedPass, OnUploadListener onUploadListener) {
        getCurrentUploader().upload(message, compressedPass, onUploadListener);
    }

    @Override
    public void uploadMessageAndSend(ProtoGlobal.Room.Type roomType, RealmRoomMessage message) {
        getCurrentUploader().uploadMessageAndSend(roomType, message);
    }

    @Override
    public void uploadMessageAndSend(ProtoGlobal.Room.Type roomType, RealmRoomMessage message, boolean ignoreCompress) {
        getCurrentUploader().uploadMessageAndSend(roomType, message, ignoreCompress);
    }

    @Override
    public boolean isUploading(String identity) {
        return getCurrentUploader().isUploading(identity);
    }

    @Override
    public boolean isCompressing(String identity) {
        return getCurrentUploader().isCompressing(identity);
    }

    @Override
    public boolean isCompressingOrUploading(String identity) {
        return getCurrentUploader().isCompressingOrUploading(identity);
    }

    @Override
    public boolean cancelUploading(String identity) {
        return getCurrentUploader().cancelUploading(identity);
    }

    @Override
    public boolean cancelCompressing(String identity) {
        return getCurrentUploader().cancelCompressing(identity);
    }

    @Override
    public boolean cancelCompressingAndUploading(String identity) {
        return getCurrentUploader().cancelCompressingAndUploading(identity);
    }

    @Override
    public int getUploadProgress(String identity) {
        return getCurrentUploader().getUploadProgress(identity);
    }

    @Override
    public int getCompressProgress(String identity) {
        return getCurrentUploader().getCompressProgress(identity);
    }


    // 0 => through proto
    // 1 => through api
    private IUpload getCurrentUploader() {
        if (G.uploadDownloadConfig == 0) {
            return uploadThroughProto;
        } else if (G.uploadDownloadConfig == 1) {
            return uploadThroughApi;
        }

        return uploadThroughProto;
    }
}
