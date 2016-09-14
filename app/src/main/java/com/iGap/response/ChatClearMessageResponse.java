package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatClearMessage;
import com.iGap.proto.ProtoError;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChatClearMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatClearMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        final ProtoChatClearMessage.ChatClearMessageResponse.Builder chatClearMessage = (ProtoChatClearMessage.ChatClearMessageResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<RealmChatHistory> realmChatHistories = realm.where(RealmChatHistory.class).equalTo("roomId", chatClearMessage.getRoomId()).findAll();
                for (RealmChatHistory chatHistory : realmChatHistories) {
                    RealmRoomMessage roomMessage = chatHistory.getRoomMessage();
                    if (roomMessage != null) {
                        // delete chat history message
                        chatHistory.getRoomMessage().deleteFromRealm();
                    }
                }

                RealmRoom room = realm.where(RealmRoom.class).equalTo("id", chatClearMessage.getRoomId()).findFirst();
                if (room != null) {
                    room.setUnreadCount(0);
                    room.setLastMessageId(0);
                    room.setLastMessageTime(0);

                    realm.copyToRealmOrUpdate(room);
                }
                // finally delete whole chat history
                realmChatHistories.deleteAllFromRealm();
            }
        });
        realm.close();

        G.clearMessagesUtil.onChatClearMessage(chatClearMessage.getRoomId(), chatClearMessage.getClearId(), chatClearMessage.getResponse());
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "ChatClearMessageResponse timeout");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "ChatClearMessageResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "ChatClearMessageResponse response.minorCode() : " + minorCode);
    }
}


