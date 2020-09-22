package net.iGap.module.upload;

import net.iGap.helper.upload.OnUploadListener;
import net.iGap.helper.upload.UploadManager;
import net.iGap.helper.upload.UploadTask;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;

import java.io.File;

public class UploadAdapter implements IUpload {
    private final UploadManager uploadManager;

    public UploadAdapter(UploadManager uploadManager) {
        this.uploadManager = uploadManager;
    }

    @Override
    public void upload(String identity, File file, ProtoGlobal.RoomMessageType type, OnUploadListener onUploadListener) {
        UploadTask uploadTask =  new UploadTask(identity, file, type, onUploadListener);
        uploadManager.upload(uploadTask);
    }

    @Override
    public void upload(RealmRoomMessage message, OnUploadListener onUploadListener) {
        uploadManager.upload(new UploadTask(message, onUploadListener));
    }

    @Override
    public void upload(RealmRoomMessage message, String compressedPass, OnUploadListener onUploadListener) {
        uploadManager.upload(new UploadTask(message, compressedPass, onUploadListener));
    }

    @Override
    public void uploadMessageAndSend(ProtoGlobal.Room.Type roomType, RealmRoomMessage message) {
        uploadManager.uploadMessageAndSend(roomType, message);
    }

    @Override
    public void uploadMessageAndSend(ProtoGlobal.Room.Type roomType, RealmRoomMessage message, boolean ignoreCompress) {
        uploadManager.uploadMessageAndSend(roomType, message, ignoreCompress);
    }

    @Override
    public boolean isUploading(String identity) {
        return uploadManager.isUploading(identity);
    }

    @Override
    public boolean isCompressing(String identity) {
        return uploadManager.isCompressing(identity);
    }

    @Override
    public boolean isCompressingOrUploading(String identity) {
        return uploadManager.isCompressingOrUploading(identity);
    }

    @Override
    public boolean cancelUploading(String identity) {
        return uploadManager.cancelUploading(identity);
    }

    @Override
    public boolean cancelCompressing(String identity) {
        return uploadManager.cancelCompressing(identity);
    }

    @Override
    public boolean cancelCompressingAndUploading(String identity) {
        return uploadManager.cancelCompressingAndUploading(identity);
    }

    @Override
    public int getUploadProgress(String identity) {
        return uploadManager.getUploadProgress(identity);
    }

    @Override
    public int getCompressProgress(String identity) {
        return uploadManager.getCompressProgress(identity);
    }
}
