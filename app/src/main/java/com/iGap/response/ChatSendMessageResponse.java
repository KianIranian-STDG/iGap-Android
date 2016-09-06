package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatSendMessage;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestClientGetRoom;

import io.realm.Realm;
import io.realm.RealmResults;

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
    public void handler() {
        G.realm = Realm.getInstance(G.realmConfig);

        final ProtoChatSendMessage.ChatSendMessageResponse.Builder chatSendMessageResponse = (ProtoChatSendMessage.ChatSendMessageResponse.Builder) message;

        ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(chatSendMessageResponse.getResponse());
        Log.i("SOC", "ChatSendMessageResponse response.getId() : " + response.getId());
        Log.i("SOC", "ChatSendMessageResponse response.getTimestamp() : " + response.getTimestamp());

        final ProtoGlobal.RoomMessage roomMessage = chatSendMessageResponse.getRoomMessage();
        final long userId = G.realm.where(RealmUserInfo.class).findFirst().getUserId();

        G.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // if first message received but the room doesn't exist, create new room
                RealmRoom room = realm.where(RealmRoom.class).equalTo("id", chatSendMessageResponse.getRoomId()).findFirst();
                if (room == null) {
                    // make get room request
                    new RequestClientGetRoom().clientGetRoom(chatSendMessageResponse.getRoomId());
                }

                // because user may have more than one device, his another device should not be recipient
                // but sender. so I check current userId with room message user id, and if not equals
                // and response is null, so we sure recipient is another user
                if (userId != roomMessage.getUserId() && chatSendMessageResponse.getResponse().getId().isEmpty()) {
                    // i'm the recipient
                    RealmChatHistory realmChatHistory = realm.createObject(RealmChatHistory.class);
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

                    realm.copyToRealm(realmChatHistory);
                    // invoke following callback when i'm not the sender, because I already done everything after sending message
                    G.onChatSendMessageResponse.onReceiveChatMessage(roomMessage.getMessage(), roomMessage.getMessageType().toString(), chatSendMessageResponse);
                } else {
                    // i'm the sender
                    // update message fields into database
                    RealmResults<RealmChatHistory> chatHistories = realm.where(RealmChatHistory.class).equalTo("roomId", chatSendMessageResponse.getRoomId()).findAll();
                    for (RealmChatHistory history : chatHistories) {
                        RealmRoomMessage message = history.getRoomMessage();
                        // find the message using identity and update it
                        if (message != null && message.getMessageId() == Long.parseLong(identity)) {
                            message.setMessageId(roomMessage.getMessageId());
                            message.setMessageVersion(roomMessage.getMessageVersion());
                            message.setStatus(roomMessage.getStatus().toString());
                            message.setMessageType(roomMessage.getMessageType().toString());
                            message.setMessage(roomMessage.getMessage());
                            message.setAttachment(roomMessage.getAttachment());
                            message.setUserId(roomMessage.getUserId());
                            message.setLocation(roomMessage.getLocation().toString());
                            message.setLog(roomMessage.getLog().toString());
                            message.setEdited(roomMessage.getEdited());
                            message.setUpdateTime(roomMessage.getUpdateTime());

                            realm.copyToRealmOrUpdate(message);
                            // invoke following callback when I'm the sender and the message has updated
                            G.onChatSendMessageResponse.onMessageUpdated(roomMessage.getMessageId(), roomMessage.getStatus(), identity, chatSendMessageResponse);
                            break;
                        }
                    }
                }
            } // if response id != null ==> i sender
        });

        G.realm.close();
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "ChatSendMessageResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "ChatSendMessageResponse response.minorCode() : " + minorCode);
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "ChatSendMessageResponse timeout");
    }
}
