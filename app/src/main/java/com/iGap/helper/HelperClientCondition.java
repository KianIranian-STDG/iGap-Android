package com.iGap.helper;

import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;

import io.realm.Realm;

/**
 * helper client condition for set info to RealmClientCondition
 */
public class HelperClientCondition {

    public static void setMessageAndStatusVersion(Realm realm, long roomId, ProtoGlobal.RoomMessage roomMessage) {
        RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();
        if (realmClientCondition != null) {
            realmClientCondition.setMessageVersion(roomMessage.getMessageVersion());
            realmClientCondition.setStatusVersion(roomMessage.getStatusVersion());
        }
    }

}
