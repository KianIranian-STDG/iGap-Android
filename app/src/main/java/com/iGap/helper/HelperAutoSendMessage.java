package com.iGap.helper;

import com.iGap.module.ChatSendMessageUtil;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import io.realm.Realm;

/**
 * send messages with sending state
 * hint : do this action when logged in user
 */
public class HelperAutoSendMessage {

    public static void sendMessage() {
        Realm realm = Realm.getDefaultInstance();
        for (RealmRoomMessage realmRoomMessage : realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.STATUS, ProtoGlobal.RoomMessageStatus.SENDING.toString()).findAll()) {
            new ChatSendMessageUtil().build(getChatType(realmRoomMessage.getRoomId(), realm), realmRoomMessage.getRoomId(), realmRoomMessage);
        }
        realm.close();
    }

    private static ProtoGlobal.Room.Type getChatType(long roomId, Realm realm) {
        return realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst().getType();
    }

}
