package com.iGap.interfaces;

import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 12/12/2016.
 */

public interface OnActivityMainStart {
    void sendDeliveredStatus(RealmRoom room, RealmRoomMessage message);
}
