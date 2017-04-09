package com.iGap.module;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.iGap.helper.HelperUploadFile;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmUserInfo;
import io.realm.Realm;

/**
 * Created by android3 on 11/14/2016.
 */

public class UploadService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String path = intent.getStringExtra("Path");
        Long roomId = intent.getLongExtra("Roomid", 0);

        sendVoice(path, roomId);

        return START_NOT_STICKY;
    }


    private void sendVoice(final String savedPath, final Long mRoomId) {

        Realm realm = Realm.getDefaultInstance();

        ProtoGlobal.Room.Type chatType = null;

        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null) {
            chatType = realmRoom.getType();
        }

        final long messageId = SUID.id().get();
        final long updateTime = System.currentTimeMillis();
        final long senderID = realm.where(RealmUserInfo.class).findFirst().getUserId();
        final long duration = AndroidUtils.getAudioDuration(getApplicationContext(), savedPath);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, messageId);

                roomMessage.setMessageType(ProtoGlobal.RoomMessageType.VOICE);
                //  roomMessage.setMessage(getWrittenMessage());
                roomMessage.setRoomId(mRoomId);
                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                roomMessage.setAttachment(messageId, savedPath, 0, 0, 0, null, duration, LocalFileType.FILE);
                roomMessage.setUserId(senderID);
                roomMessage.setCreateTime(updateTime);
            }
        });

        HelperUploadFile.startUploadTaskChat(mRoomId, chatType, savedPath, messageId, ProtoGlobal.RoomMessageType.VOICE, "", 0, null);

        realm.close();
    }


}
