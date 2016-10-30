package com.iGap.module;

import android.content.Context;
import com.iGap.G;
import com.iGap.interfaces.IResendMessage;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.enums.RoomType;
import io.realm.Realm;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/30/2016.
 */

public class ResendMessage implements IResendMessage {
    private StructMessageInfo mMessage;
    private IResendMessage mListener;

    public ResendMessage(Context context, IResendMessage listener, StructMessageInfo message) {
        this.mMessage = message;
        this.mListener = listener;
        AppUtils.buildResendDialog(context, this).show();
    }

    @Override public void deleteMessage() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class)
                    .equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(mMessage.messageID))
                    .findFirst();
                if (roomMessage != null) {
                    roomMessage.deleteFromRealm();
                }
            }
        });
        realm.close();

        mListener.deleteMessage();
    }

    @Override public void resendMessage() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class)
                    .equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(mMessage.messageID))
                    .findFirst();
                if (roomMessage != null) {
                    roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                }
            }
        });

        mListener.resendMessage();

        RealmRoomMessage message = realm.where(RealmRoomMessage.class)
            .equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(mMessage.messageID))
            .findFirst();
        if (message != null) {
            RoomType roomType = realm.where(RealmRoom.class)
                .equalTo(RealmRoomFields.ID, message.getRoomId())
                .findFirst()
                .getType();
            G.chatSendMessageUtil.build(RoomType.convert(roomType), message.getRoomId(), message);
        }

        realm.close();
    }
}
