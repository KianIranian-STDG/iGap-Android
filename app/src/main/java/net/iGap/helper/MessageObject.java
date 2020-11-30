package net.iGap.helper;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;

public class MessageObject {

    public static boolean canSharePublic(RealmRoomMessage message) {
        return message != null && message.attachment != null && !message.messageType.equals(ProtoGlobal.RoomMessageType.VOICE.toString());
    }
}
