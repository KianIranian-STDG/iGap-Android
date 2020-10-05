package net.iGap.module.upload;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;

import java.io.File;

public class UploadObject {
    public String key;
    public String fileToken;
    public String mimeType;
    public String fileName;
    public File file;
    public long messageId;
    public long fileSize;
    public long offset;
    public int selector;
    public int progress;
    public String path;
    public ProtoGlobal.RoomMessageType messageType;
    public RealmRoomMessage message;
    public ProtoGlobal.Room.Type roomType;

    private UploadObject() {

    }

    public static UploadObject createForMessage(RealmRoomMessage message, ProtoGlobal.Room.Type roomType) {
        UploadObject object = new UploadObject();

        if (message == null || message.messageId == 0 || message.attachment == null) {
            return null;
        }

        if (roomType == null) {
            return null;
        }

        object.path = message.attachment.localFilePath;
        object.file = new File(object.path);
        object.fileName = object.file.getName();
        object.fileSize = message.attachment.size;
        object.fileToken = message.attachment.token;
        object.key = String.valueOf(message.messageId);
        object.messageType = ProtoGlobal.RoomMessageType.valueOf(message.messageType);
        object.messageId = message.messageId;
        object.message = message;
        object.roomType = roomType;

        return object;
    }
}
