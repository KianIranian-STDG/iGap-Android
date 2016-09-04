package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatSendMessage;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmUserInfo;

import io.realm.Realm;

public class ChatSendMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatSendMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.identity = identity;
        this.message = protoClass;
    }


    @Override
    public void handler() { //TODO [Saeed Mozaffari] [2016-09-03 4:46 PM] - Eskandarpour if received first message from room add new room
        //TODO [Saeed Mozaffari] [2016-09-04 9:22 AM] - if response is null message is not for me (should be message is for my another account). check it
        G.realm = Realm.getInstance(G.realmConfig);

        final ProtoChatSendMessage.ChatSendMessageResponse.Builder chatSendMessageResponse = (ProtoChatSendMessage.ChatSendMessageResponse.Builder) message;

        if (chatSendMessageResponse.getResponse() == null) { // i'm not sender

        } else { // i'm sender

        }

        Log.i("MMM", "RoomId : " + chatSendMessageResponse.getRoomId());
        Log.i("MMM", "RoomMessage : " + chatSendMessageResponse.getRoomMessage());

        final ProtoGlobal.RoomMessage roomMessage = chatSendMessageResponse.getRoomMessage();
        Log.i("MMM", "1");
        G.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.i("MMM", "2");
                RealmChatHistory realmChatHistory = realm.createObject(RealmChatHistory.class);
                Log.i("MMM", "3");
                RealmRoomMessage realmRoom = realm.createObject(RealmRoomMessage.class);

                realmRoom.setMessageId(roomMessage.getMessageId());
                realmRoom.setMessageVersion(roomMessage.getMessageVersion());
                realmRoom.setStatus(roomMessage.getStatus().toString());
                realmRoom.setMessageType(roomMessage.getMessageType().toString());
                realmRoom.setMessage(roomMessage.getMessage());
                realmRoom.setAttachment(roomMessage.getAttachment());
                realmRoom.setUserId(roomMessage.getUserId());
                realmRoom.setLocation(roomMessage.getLocation().toString());
                realmRoom.setLog(roomMessage.getLog().toString());
                realmRoom.setEdited(roomMessage.getEdited());
                realmRoom.setUpdateTime(roomMessage.getUpdateTime());

                realmChatHistory.setRoomId(chatSendMessageResponse.getRoomId());
                realmChatHistory.setRoomMessage(realmRoom);
                Log.i("MMM", "4");
                long realmUserId = G.realm.where(RealmUserInfo.class).findFirst().getUserId();
                Log.i("MMM", "realmUserId : " + realmUserId);
                Log.i("MMM", "roomMessage.getUserId() : " + roomMessage.getUserId());
                G.onReceiveChatMessage.onReceiveChatMessage(roomMessage.getMessage(), roomMessage.getMessageType().toString(), chatSendMessageResponse);
//                if (realmUserId != roomMessage.getUserId()) {
//                    Log.i("MMM", "onReceiveChatMessage");
//                    G.onReceiveChatMessage.onReceiveChatMessage(roomMessage.getMessage(), roomMessage.getMessageType().toString());
//                }
            }
        });

        G.realm.close();
    }

    @Override
    public void error() {

    }
}

//    @Override
//    /**
//     * @param ProtoChatClearMessage.ChatClearMessage protoObject
//     */
//    public void handler(int actionId, Class protoObject) {
//
//    }
