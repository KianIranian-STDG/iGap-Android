package net.iGap.module.upload;

import net.iGap.helper.upload.ApiBased.HttpUploader;
import net.iGap.helper.upload.UploadManager;
import net.iGap.module.accountManager.AppConfig;

public class Uploader implements IUpload {
    private static Uploader instance;

    private IUpload uploadThroughProto;
    private IUpload uploadThroughApi;

    private Uploader() {
        uploadThroughProto = new UploadAdapter(UploadManager.getInstance());
        uploadThroughApi = HttpUploader.getInstance();
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
//
//    @Override
//    public void upload(String identity, File file, ProtoGlobal.RoomMessageType type, OnUploadListener onUploadListener) {
//        getCurrentUploader().upload(identity, file, type, onUploadListener);
//    }
//
//    @Override
//    public void upload(RealmRoomMessage message, OnUploadListener onUploadListener) {
//        getCurrentUploader().upload(message, onUploadListener);
//    }
//
//    @Override
//    public void upload(RealmRoomMessage message, String compressedPass, OnUploadListener onUploadListener) {
//        getCurrentUploader().upload(message, compressedPass, onUploadListener);
//    }
//
//    @Override
//    public void uploadMessageAndSend(ProtoGlobal.Room.Type roomType, RealmRoomMessage message) {
//        getCurrentUploader().uploadMessageAndSend(roomType, message);
//    }
//
//    @Override
//    public void uploadMessageAndSend(ProtoGlobal.Room.Type roomType, RealmRoomMessage message, boolean ignoreCompress) {
//        getCurrentUploader().uploadMessageAndSend(roomType, message, ignoreCompress);
//    }

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

    @Override
    public void upload(UploadObject fileObject) {
        getCurrentUploader().upload(fileObject);
    }

    // 0 => through proto
    // 1 => through api
    private IUpload getCurrentUploader() {
        if (AppConfig.fileGateway == 0) {
            return uploadThroughProto;
        } else if (AppConfig.fileGateway == 1) {
            return uploadThroughApi;
        }

        return uploadThroughProto;
    }
}
