package com.iGap.response;

import android.text.format.DateUtils;
import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatSendMessage;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageContact;
import com.iGap.realm.RealmRoomMessageLocation;
import com.iGap.realm.RealmRoomMessageLog;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestClientGetRoom;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChatSendMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;
    private ArrayList<Long> messageId = new ArrayList<>();

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

                Log.i("CLI_XX", "Chat getRoomId : " + chatSendMessageResponse.getRoomId());
                Log.i("CLI_XX", "Chat getMessageVersion : " + roomMessage.getMessageVersion());
                Log.i("CLI_XX", "Chat getStatusVersion : " + roomMessage.getStatusVersion());
                Log.i("CLI_XX", "Chat getMessageId : " + roomMessage.getMessageId());
                Log.i("CLI_XX", "Chat getMessage : " + roomMessage.getMessage());
                Log.i("CLI_XX", "***");
                Log.i("CLI_XX", "**********************************************");
                Log.i("CLI_XX", "***");

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

                if (chatSendMessageResponse.getResponse().getId().isEmpty()) {//TODO [Saeed Mozaffari] [2016-10-06 12:35 PM] - check this comment Alireza added and removed with saeed ==> //userId != roomMessage.getUserId() &&
                    // i'm the recipient

                    RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", roomMessage.getMessageId()).findFirst();

                    /*
                     *  if this is new message and not exist this messageId createObject from RealmChatHistory
                     *  else this message is repetitious find fetch RealmChatHistory with messageId and update it
                     */
                    RealmChatHistory realmChatHistory;

                    if (realmRoomMessage == null) {
                        realmChatHistory = realm.createObject(RealmChatHistory.class);
                        realmChatHistory.setId(System.currentTimeMillis());

                        realmRoomMessage = realm.createObject(RealmRoomMessage.class);
                        realmRoomMessage.setMessageId(roomMessage.getMessageId());
                    } else {

                        /*
                        * message exist in chat room so don't calling onMessageReceive callback , just
                        * update message . this process for this is that maybe receive repetitious message
                        * from server so client should handle this subject
                        */
                        messageId.add(roomMessage.getMessageId());

                        realmChatHistory = realm.where(RealmChatHistory.class).equalTo("roomMessage.messageId", roomMessage.getMessageId()).findFirst();
                    }

                    realmRoomMessage.setMessageVersion(roomMessage.getMessageVersion());
                    realmRoomMessage.setStatus(roomMessage.getStatus().toString());
                    realmRoomMessage.setMessageType(roomMessage.getMessageType().toString());
                    realmRoomMessage.setMessage(roomMessage.getMessage());

                    realmRoomMessage.setAttachment(roomMessage.getMessageId(), roomMessage.getAttachment());
                    realmRoomMessage.setUserId(roomMessage.getUserId());
                    realmRoomMessage.setLocation(RealmRoomMessageLocation.build(roomMessage.getLocation()));
                    realmRoomMessage.setLog(RealmRoomMessageLog.build(roomMessage.getLog()));
                    realmRoomMessage.setRoomMessageContact(RealmRoomMessageContact.build(roomMessage.getContact()));
                    realmRoomMessage.setEdited(roomMessage.getEdited());
                    realmRoomMessage.setUpdateTime(roomMessage.getUpdateTime());

                    realmChatHistory.setRoomId(chatSendMessageResponse.getRoomId());
                    realmChatHistory.setRoomMessage(realmRoomMessage);

                    realm.copyToRealm(realmChatHistory);

                    if (roomMessage.getUserId() != userId) { // show notification if this message isn't for another account
                        if (!G.isAppInFg) {
                            G.helperNotificationAndBadge.updateNotificationAndBadge(true);
                        }
                    }

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
                            message.setLocation(RealmRoomMessageLocation.build(roomMessage.getLocation()));
                            message.setLog(RealmRoomMessageLog.build(roomMessage.getLog()));
                            message.setRoomMessageContact(RealmRoomMessageContact.build(roomMessage.getContact()));
                            message.setEdited(roomMessage.getEdited());
                            message.setUpdateTime(roomMessage.getUpdateTime());

                            realm.copyToRealmOrUpdate(message);
                            break;
                        }
                    }
                }
            }
        });

        if (chatSendMessageResponse.getResponse().getId().isEmpty()) {//TODO [Saeed Mozaffari] [2016-10-06 12:35 PM] - check this comment Alireza added and removed with saeed ==> //userId != roomMessage.getUserId() &&
            // invoke following callback when i'm not the sender, because I already done everything after sending message
            if (!messageId.contains(roomMessage.getMessageId())) {
                if (realm.where(RealmRoom.class).equalTo("id", chatSendMessageResponse.getRoomId()).findFirst() != null) {
                    G.chatSendMessageUtil.onMessageReceive(chatSendMessageResponse.getRoomId(), roomMessage.getMessage(), roomMessage.getMessageType().toString(), roomMessage);
                }
            } else {
                messageId.remove(messageId.indexOf(roomMessage.getMessageId()));
            }
        } else {
            // invoke following callback when I'm the sender and the message has updated
            G.chatSendMessageUtil.onMessageUpdate(chatSendMessageResponse.getRoomId(), roomMessage.getMessageId(), roomMessage.getStatus(), identity, roomMessage);
        }

        realm.close();
    }

    @Override
    public void error() {
        super.error();
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }
}
