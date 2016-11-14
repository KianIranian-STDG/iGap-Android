package com.iGap.realm;

import com.iGap.module.SUID;
import com.iGap.proto.ProtoGlobal;

import org.parceler.Parcel;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmRoomMessageLogRealmProxy;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {RealmRoomMessageLogRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {RealmRoomMessageLog.class})
public class RealmRoomMessageLog extends RealmObject {
    private String type;
    @PrimaryKey
    private long id;

    public static RealmRoomMessageLog build(final ProtoGlobal.RoomMessageLog input) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoomMessageLog messageLocation = realm.createObject(RealmRoomMessageLog.class);
        messageLocation.setId(SUID.id().get());
        messageLocation.setType(input.getType());
        realm.close();

        return messageLocation;
    }

    public ProtoGlobal.RoomMessageLog.Type getType() {
        return ProtoGlobal.RoomMessageLog.Type.valueOf(type);
    }

    public void setType(ProtoGlobal.RoomMessageLog.Type type) {
        this.type = type.toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
