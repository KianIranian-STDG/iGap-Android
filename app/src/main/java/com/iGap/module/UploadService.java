package com.iGap.module;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;

import com.iGap.activities.ActivityChat;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
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

        Log.e("ddd", path + "   " + roomId);

        sendVoice(path, roomId);

        return START_NOT_STICKY;
    }


    private void sendVoice(final String savedPath, final Long mRoomId) {

        Realm realm = Realm.getDefaultInstance();
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
                roomMessage.setUpdateTime((int) (updateTime / DateUtils.SECOND_IN_MILLIS));
            }
        });

        new ActivityChat.UploadTask().execute(savedPath, messageId, ProtoGlobal.RoomMessageType.VOICE, mRoomId, "");

        Log.e("ddd", "voice");
        realm.close();
    }


}
