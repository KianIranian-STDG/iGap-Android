package com.iGap.response;

import android.text.format.DateUtils;
import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatSendMessage;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmClientCondition;
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
        Realm realm = Realm.getDefaultInstance();

        final ProtoChatSendMessage.ChatSendMessageResponse.Builder chatSendMessageResponse = (ProtoChatSendMessage.ChatSendMessageResponse.Builder) message;

        final ProtoGlobal.RoomMessage roomMessage = chatSendMessageResponse.getRoomMessage();
        final long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // set info for clientCondition
                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo("roomId", chatSendMessageResponse.getRoomId()).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.setMessageVersion(roomMessage.getMessageVersion());
                    realmClientCondition.setStatusVersion(roomMessage.getStatusVersion());
                }

                Log.i("CLI", "send message MessageVersion : " + roomMessage.getMessageVersion());
                Log.i("CLI", "send message StatusVersion : " + roomMessage.getStatusVersion());
                Log.i("CLI", "send message MessageId : " + roomMessage.getMessageId());

                // if first message received but the room doesn't exist, create new room
                RealmRoom room = realm.where(RealmRoom.class).equalTo("id", chatSendMessageResponse.getRoomId()).findFirst();
                if (room == null) {
                    // make get room request
                    new RequestClientGetRoom().clientGetRoom(chatSendMessageResponse.getRoomId());
                } else {
                    // update last message sent/received in room table
                    if (room.getLastMessageTime() < roomMessage.getUpdateTime() * DateUtils.SECOND_IN_MILLIS) {
                        room.setLastMessageId(roomMessage.getMessageId());
                        room.setLastMessageTime(roomMessage.getUpdateTime());

                        realm.copyToRealmOrUpdate(room);
                    }
                }

                // because user may have more than one device, his another device should not be recipient
                // but sender. so I check current userId with room message user id, and if not equals
                // and response is null, so we sure recipient is another user
                if (userId != roomMessage.getUserId() && chatSendMessageResponse.getResponse().getId().isEmpty()) {
                    // i'm the recipient

                    RealmChatHistory realmChatHistory = realm.createObject(RealmChatHistory.class);
                    realmChatHistory.setId(System.currentTimeMillis());

                    RealmRoomMessage realmRoomMessage = realm.createObject(RealmRoomMessage.class);

                    realmRoomMessage.setMessageId(roomMessage.getMessageId());
                    realmRoomMessage.setMessageVersion(roomMessage.getMessageVersion());
                    realmRoomMessage.setStatus(roomMessage.getStatus().toString());
                    realmRoomMessage.setMessageType(roomMessage.getMessageType().toString());
                    realmRoomMessage.setMessage(roomMessage.getMessage());

                    realmRoomMessage.setAttachment(roomMessage.getMessageId(), roomMessage.getAttachment());
                    realmRoomMessage.setUserId(roomMessage.getUserId());
                    realmRoomMessage.setLocation(roomMessage.getLocation().toString());
                    realmRoomMessage.setLog(roomMessage.getLog().toString());
                    realmRoomMessage.setEdited(roomMessage.getEdited());
                    realmRoomMessage.setUpdateTime(roomMessage.getUpdateTime());

                    realmChatHistory.setRoomId(chatSendMessageResponse.getRoomId());
                    realmChatHistory.setRoomMessage(realmRoomMessage);

                    realm.copyToRealm(realmChatHistory);
                    if (!G.isAppInFg)
                        G.helperNotificationAndBadge.updateNotificationAndBadge(true);

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
                            message.setAttachment(roomMessage.getMessageId(), roomMessage.getAttachment());
                            message.setUserId(roomMessage.getUserId());
                            message.setLocation(roomMessage.getLocation().toString());
                            message.setLog(roomMessage.getLog().toString());
                            message.setEdited(roomMessage.getEdited());
                            message.setUpdateTime(roomMessage.getUpdateTime());

                            realm.copyToRealmOrUpdate(message);
                            break;
                        }
                    }
                }
            }
        });

        if (userId != roomMessage.getUserId() && chatSendMessageResponse.getResponse().getId().isEmpty()) {
            // invoke following callback when i'm not the sender, because I already done everything after sending message
            if (realm.where(RealmRoom.class).equalTo("id", chatSendMessageResponse.getRoomId()).findFirst() != null) {
                G.chatSendMessageUtil.onMessageReceive(chatSendMessageResponse.getRoomId(), roomMessage.getMessage(), roomMessage.getMessageType().toString(), roomMessage);
            }
        } else {
            // invoke following callback when I'm the sender and the message has updated
            G.chatSendMessageUtil.onMessageUpdate(chatSendMessageResponse.getRoomId(), roomMessage.getMessageId(), roomMessage.getStatus(), identity, roomMessage);
        }

        realm.close();
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
