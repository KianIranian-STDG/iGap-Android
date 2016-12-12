package com.iGap.interfaces;

import com.iGap.realm.RealmRoomMessage;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 12/12/2016.
 */

public interface OnActivityChatStart {
    void sendSeenStatus(RealmRoomMessage message);

    void resendMessage(RealmRoomMessage message);

    void resendMessageNeedsUpload(RealmRoomMessage message);
}
