/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.realm;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmNotificationRoomMessage extends RealmObject {
    @PrimaryKey
    public long messageId;
    public long roomId;
    public long createTime;

    public static void putToDataBase(Realm realm, long messageId, long roomId) {
        RealmNotificationRoomMessage message = realm.createObject(RealmNotificationRoomMessage.class, messageId);
        message.createTime = System.currentTimeMillis();
        message.roomId = roomId;
    }

    public static boolean canShowNotif(Realm realm, long messageId, long roomId) {
        RealmNotificationRoomMessage message = realm.where(RealmNotificationRoomMessage.class)
                .equalTo("messageId", messageId)
                .equalTo("roomId", roomId)
                .findFirst();
        return message == null;
    }

}
