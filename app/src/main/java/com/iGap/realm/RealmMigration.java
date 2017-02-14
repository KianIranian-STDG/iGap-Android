package com.iGap.realm;

import android.util.Log;
import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class RealmMigration implements io.realm.RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        Log.i("UUU", "RealmChannelRoom.class.getName() : " + RealmChannelRoom.class.getName());
        Log.i("UUU", "RealmChannelRoom.class.toString() : " + RealmChannelRoom.class.toString());

        if (oldVersion == 1) {
            RealmObjectSchema roomSchema = schema.get(RealmRoom.class.getName());
            if (roomSchema != null) {
                roomSchema.addField(RealmRoomFields.KEEP_ROOM, boolean.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmRoomMessageSchema = schema.get(RealmRoomMessage.class.getName());
            if (realmRoomMessageSchema != null) {
                realmRoomMessageSchema.addField(RealmRoomMessageFields.AUTHOR_ROOM_ID, long.class, FieldAttribute.REQUIRED);
            }
            oldVersion++;
        }

        if (oldVersion == 2) {
            RealmObjectSchema roomSchema = schema.get(RealmRoom.class.getName());
            if (roomSchema != null) {
                roomSchema.addField(RealmRoomFields.ACTION_STATE_USER_ID, long.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmChannelRoomSchema = schema.get(RealmChannelRoom.class.getName());
            if (realmChannelRoomSchema != null) {
                realmChannelRoomSchema.addField(RealmChannelRoomFields.SEEN_ID, long.class, FieldAttribute.REQUIRED);
            }
        }
    }
}
