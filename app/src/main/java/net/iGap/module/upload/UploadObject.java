package net.iGap.module.upload;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import net.iGap.helper.upload.OnUploadListener;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.structs.MessageObject;

import java.io.File;

public class UploadObject implements Parcelable {
    public String key;
    public String fileToken;
    public String mimeType;
    public String fileName;
    public String type;
    public String caption;
    public File file;
    public long messageId;
    public long fileSize;
    public long offset;
    public int selector;
    public int progress;
    public String path;
    public ProtoGlobal.RoomMessageType messageType;
    public RealmRoomMessage message;
    public MessageObject messageObject;
    public ProtoGlobal.Room.Type roomType;
    public OnUploadListener onUploadListener;

    public byte[] auth;

    private UploadObject() {

    }

    protected UploadObject(Parcel in) {
        key = in.readString();
        fileToken = in.readString();
        mimeType = in.readString();
        fileName = in.readString();
        type = in.readString();
        caption = in.readString();
        messageId = in.readLong();
        fileSize = in.readLong();
        offset = in.readLong();
        selector = in.readInt();
        progress = in.readInt();
        path = in.readString();
        auth = in.createByteArray();
    }

    public static final Creator<UploadObject> CREATOR = new Creator<UploadObject>() {
        @Override
        public UploadObject createFromParcel(Parcel in) {
            return new UploadObject(in);
        }

        @Override
        public UploadObject[] newArray(int size) {
            return new UploadObject[size];
        }
    };

    public static UploadObject createForMessage(MessageObject messageObject, ProtoGlobal.Room.Type roomType) {
        UploadObject object = new UploadObject();

        if (messageObject == null || messageObject.id == 0 || messageObject.attachment == null || messageObject.attachment.filePath == null) {
            return null;
        }

        if (roomType == null) {
            return null;
        }

        object.path = messageObject.attachment.filePath;
        object.file = new File(object.path);
        object.fileName = object.file.getName();
        object.fileSize = object.file.length();
        object.fileToken = messageObject.attachment.token;
        object.key = String.valueOf(messageObject.id);
        object.messageType = ProtoGlobal.RoomMessageType.valueOf(messageObject.messageType);
        object.messageId = messageObject.id;
        object.messageObject = messageObject;
        object.roomType = roomType;

        return object;
    }

    public static UploadObject createForMessage(RealmRoomMessage message, ProtoGlobal.Room.Type roomType) {
        UploadObject object = new UploadObject();

        if (message == null || message.messageId == 0 || message.attachment == null || message.attachment.localFilePath == null) {
            return null;
        }

        if (roomType == null) {
            return null;
        }

        object.path = message.attachment.localFilePath;
        object.file = new File(object.path);
        object.fileName = object.file.getName();
        object.fileSize = object.file.length();
        object.fileToken = message.attachment.token;
        object.key = String.valueOf(message.messageId);
        object.messageType = ProtoGlobal.RoomMessageType.valueOf(message.messageType);
        object.messageId = message.messageId;
        object.message = message;
        object.roomType = roomType;

        Log.i("Upload", "createForMessage: " + object.toString());

        return object;
    }

    public static UploadObject createForAvatar(long avatarId, String imagePath, String token, ProtoGlobal.RoomMessageType roomType, OnUploadListener onUploadListener) {
        UploadObject object = new UploadObject();


        object.path = imagePath;
        object.file = new File(object.path);
        object.fileName = object.file.getName();
        object.fileSize = object.file.length();
        object.fileToken = token;
        object.key = String.valueOf(avatarId);
        object.messageId = avatarId;
        object.messageType = roomType;
        object.onUploadListener = onUploadListener;
        return object;
    }

    public static UploadObject createForStory(long avatarId, String imagePath, String token, String caption, ProtoGlobal.RoomMessageType roomType) {
        UploadObject object = new UploadObject();


        object.path = imagePath;
        object.file = new File(object.path);
        object.fileName = object.file.getName();
        object.fileSize = object.file.length();
        object.fileToken = token;
        object.key = String.valueOf(avatarId);
        object.messageId = avatarId;
        object.messageType = roomType;
        object.caption = caption;
        return object;
    }

    @Override
    public String toString() {
        return "UploadObject{" +
                "key='" + key + '\'' +
                ", fileToken='" + fileToken + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", fileName='" + fileName + '\'' +
                ", file=" + file +
                ", messageId=" + messageId +
                ", fileSize=" + fileSize +
                ", offset=" + offset +
                ", selector=" + selector +
                ", progress=" + progress +
                ", path='" + path + '\'' +
                ", messageType=" + messageType +
                ", message=" + message +
                ", roomType=" + roomType +
                ", onUploadListener=" + onUploadListener +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(fileToken);
        dest.writeString(mimeType);
        dest.writeString(fileName);
        dest.writeString(type);
        dest.writeString(caption);
        dest.writeLong(messageId);
        dest.writeLong(fileSize);
        dest.writeLong(offset);
        dest.writeInt(selector);
        dest.writeInt(progress);
        dest.writeString(path);
        dest.writeByteArray(auth);
    }
}
