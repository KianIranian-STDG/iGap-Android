package com.iGap.realm;

import com.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmChatRoom extends RealmObject {

    @PrimaryKey
    private long peer_id;

    private RealmNotificationSetting realmNotificationSetting;

    public long getPeerId() {
        return peer_id;
    }

    public void setPeerId(long peer_id) {
        this.peer_id = peer_id;
    }

    public RealmNotificationSetting getRealmNotificationSetting() {
        return realmNotificationSetting;
    }

    public void setRealmNotificationSetting(RealmNotificationSetting realmNotificationSetting) {
        this.realmNotificationSetting = realmNotificationSetting;
    }

    /**
     * convert ProtoGlobal.ChatRoom to RealmChatRoom
     *
     * @param room ProtoGlobal.ChatRoom
     * @return RealmChatRoom
     */
    public static RealmChatRoom convert(ProtoGlobal.ChatRoom room, RealmChatRoom realmChatRoom, Realm realm) {
        if (realmChatRoom == null) {
            realmChatRoom = realm.createObject(RealmChatRoom.class);
        }
        realmChatRoom.setPeerId(room.getPeer().getId());
        return realmChatRoom;
    }
}
