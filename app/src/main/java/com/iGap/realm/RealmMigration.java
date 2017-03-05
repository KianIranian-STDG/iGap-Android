package com.iGap.realm;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class RealmMigration implements io.realm.RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 1) {
            RealmObjectSchema roomSchema = schema.get("RealmRoom");
            if (roomSchema != null) {
                roomSchema.addField("keepRoom", boolean.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmRoomMessageSchema = schema.get("RealmRoomMessage");
            if (realmRoomMessageSchema != null) {
                realmRoomMessageSchema.addField("authorRoomId", long.class, FieldAttribute.REQUIRED);
            }
            oldVersion++;
        }

        if (oldVersion == 2) {
            RealmObjectSchema roomSchema = schema.get("RealmRoom");
            if (roomSchema != null) {
                roomSchema.addField("actionStateUserId", long.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmChannelRoomSchema = schema.get("RealmChannelRoom");
            if (realmChannelRoomSchema != null) {
                realmChannelRoomSchema.addField("seenId", long.class, FieldAttribute.REQUIRED);
            }
            oldVersion++;
        }

        if (oldVersion == 3) {
            schema.create(RealmWallpaper.class.getSimpleName()).addField(RealmWallpaperFields.LAST_TIME_GET_LIST, long.class, FieldAttribute.REQUIRED).addField(RealmWallpaperFields.WALL_PAPER_LIST, byte[].class).addField(RealmWallpaperFields.LOCAL_LIST, byte[].class);
            oldVersion++;
        }

        if (oldVersion == 4) {
            schema.create(RealmPrivacy.class.getSimpleName()).addField("whoCanSeeMyAvatar", String.class).addField("whoCanInviteMeToChannel", String.class).addField("whoCanInviteMeToGroup", String.class).addField("whoCanSeeMyLastSeen", String.class);
            oldVersion++;
        }

        if (oldVersion == 5) {
            RealmObjectSchema realmRoomMessageSchema = schema.get(RealmRoomMessage.class.getSimpleName());
            if (realmRoomMessageSchema != null) {
                realmRoomMessageSchema.addField(RealmRoomMessageFields.PREVIOUS_MESSAGE_ID, long.class, FieldAttribute.REQUIRED);
                realmRoomMessageSchema.addField(RealmRoomMessageFields.SHOW_TIME, boolean.class, FieldAttribute.REQUIRED);
                realmRoomMessageSchema.addField(RealmRoomMessageFields.LINK_INFO, String.class);
            }
        }
    }
}
