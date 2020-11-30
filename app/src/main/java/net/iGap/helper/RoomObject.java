package net.iGap.helper;

import net.iGap.realm.RealmRoom;

public class RoomObject {

    public static boolean isRoomPublic(RealmRoom realmRoom) {
        if (realmRoom != null && !realmRoom.type.equals("CHAT")) {
            return realmRoom.channelRoom != null && !realmRoom.channelRoom.isPrivate || realmRoom.groupRoom != null && !realmRoom.groupRoom.isPrivate;
        }
        return false;
    }
}
