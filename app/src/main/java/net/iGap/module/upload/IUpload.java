package net.iGap.module.upload;

import net.iGap.helper.upload.OnUploadListener;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;

public interface IUpload {
    void upload(RealmRoomMessage message, OnUploadListener onUploadListener);

    void upload(RealmRoomMessage message, String compressedPass, OnUploadListener onUploadListener);

    void uploadMessageAndSend(ProtoGlobal.Room.Type roomType, RealmRoomMessage message);

    void uploadMessageAndSend(ProtoGlobal.Room.Type roomType, RealmRoomMessage message, boolean ignoreCompress);

    boolean isUploading(String identity);

    boolean isCompressing(String identity);

    boolean isCompressingOrUploading(String identity);

    boolean cancelUploading(String identity);

    boolean cancelCompressing(String identity);

    boolean cancelCompressingAndUploading(String identity);

    int getUploadProgress(String identity);

    int getCompressProgress(String identity);
}
