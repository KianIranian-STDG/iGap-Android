package com.iGap.helper;

import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmRoomMessage;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * helper methods while working with Realm
 * note: when any field of classes was changed, update this helper.
 */
public final class HelperRealm {
    private HelperRealm() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation.");
    }

    public static RealmRoomMessage getLastMessage(long roomId) {
        Realm realm = Realm.getDefaultInstance();

        RealmResults<RealmChatHistory> chatHistories = realm.where(RealmChatHistory.class).equalTo("roomId", roomId).findAll();
        long lastMessageId = 0;
        long lastMessageTime = 0;
        for (RealmChatHistory chatHistory : chatHistories) {
            RealmRoomMessage roomMessage = chatHistory.getRoomMessage();
            if (roomMessage != null) {
                if (roomMessage.getUpdateTime() >= lastMessageTime) {
                    lastMessageId = roomMessage.getMessageId();
                }
            }
        }

        RealmRoomMessage lastMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", lastMessageId).findFirst();
        realm.close();

        return lastMessage;
    }
}
