package com.iGap.realm.enums;

import com.iGap.proto.ProtoGlobal;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 8/31/2016.
 */
public enum RoomType {
    CHAT, GROUP, CHANNEL;

    /**
     * convert ProtoGlobal.Room.Type to RoomType
     *
     * @param type ProtoGlobal.Room.Type
     * @return RoomType
     */
    public static RoomType convert(ProtoGlobal.Room.Type type) {
        return RoomType.valueOf(type.toString());
    }

    /**
     * convert ProtoGlobal.Room.Type to RoomType
     *
     * @param type ProtoGlobal.Room.Type
     * @return RoomType
     */
    public static ProtoGlobal.Room.Type convert(RoomType type) {
        return ProtoGlobal.Room.Type.valueOf(type.toString());
    }
}
