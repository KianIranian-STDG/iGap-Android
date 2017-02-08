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
            roomSchema.addField("keepRoom", boolean.class, FieldAttribute.REQUIRED);

            RealmObjectSchema realmRoomMessageSchema = schema.get("RealmRoomMessage");
            realmRoomMessageSchema.addField("authorRoomId", long.class, FieldAttribute.REQUIRED);
            oldVersion++;
        }
    }
}
