package com.iGap.realm;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

public class RealmMigrationClass implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

//        if(oldVersion == 0) {
//            RealmSchema sessionSchema = realm.getSchema();
//
//            if(oldVersion == 0) {
//                RealmObjectSchema sessionObjSchema = sessionSchema.get("Session");
//                sessionObjSchema.addField("isSessionRecordingUploading", boolean.class, FieldAttribute.REQUIRED)
//                        .transform(new RealmObjectSchema.Function() {
//                            @Override
//                            public void apply(DynamicRealmObject obj) {
//                                obj.set("isSessionRecordingUploading", false);
//                            }
//                        });
//
//
//                sessionObjSchema.setNullable("isSessionRecordingUploading",false);
//                oldVersion++;
//            }
//
//        }
//    }
    }
}
