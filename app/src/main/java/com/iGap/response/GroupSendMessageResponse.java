package com.iGap.response;

import android.text.format.DateUtils;
import android.util.Log;
import com.iGap.G;
import com.iGap.helper.HelperCheckUserInfoExist;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupSendMessage;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageContact;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.RealmRoomMessageLocation;
import com.iGap.realm.RealmRoomMessageLog;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestClientGetRoom;
import io.realm.Realm;
import io.realm.RealmResults;

public class GroupSendMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupSendMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        Realm realm = Realm.getDefaultInstance();

        final ProtoGroupSendMessage.GroupSendMessageResponse.Builder builder = (ProtoGroupSendMessage.GroupSendMessageResponse.Builder) message;
        final ProtoGlobal.RoomMessage roomMessage = builder.getRoomMessage();
        final long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();

        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                // set info for clientCondition
                RealmClientCondition realmClientCondition =
                    realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, builder.getRoomId()).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.setMessageVersion(roomMessage.getMessageVersion());
                    realmClientCondition.setStatusVersion(roomMessage.getStatusVersion());
                }

                Log.i("CLI_XXX", "getRoomId : " + builder.getRoomId());
                Log.i("CLI_XXX", "getMessageVersion : " + roomMessage.getMessageVersion());
                Log.i("CLI_XXX", "getStatusVersion : " + roomMessage.getStatusVersion());
                Log.i("CLI_XXX", "getMessageId : " + roomMessage.getMessageId());
                Log.i("CLI_XXX", "getMessage : " + roomMessage.getMessage());
                Log.i("CLI_XXX", "***");
                Log.i("CLI_XXX", "**********************************************");
                Log.i("CLI_XXX", "***");

                Log.i("CLI", "send message MessageVersion : " + roomMessage.getMessageVersion());
                Log.i("CLI", "send message StatusVersion : " + roomMessage.getStatusVersion());
                Log.i("CLI", "send message MessageId : " + roomMessage.getMessageId());

                // if first message received but the room doesn't exist, create new room
                RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();
                if (room == null) {
                    // make get room request
                    new RequestClientGetRoom().clientGetRoom(builder.getRoomId());
                } else {
                    // update last message sent/received in room table
                    if (room.getLastMessageTime() < roomMessage.getUpdateTime() * DateUtils.SECOND_IN_MILLIS) {
                        room.setLastMessageId(roomMessage.getMessageId());
                        room.setLastMessageTime(roomMessage.getUpdateTime());

                        realm.copyToRealmOrUpdate(room);
                    }
                }

                // because user may have more than one device, his another device should not be
                // recipient
                // but sender. so I check current userId with room message user id, and if not
                // equals
                // and response is null, so we sure recipient is another user
                if (userId != roomMessage.getUserId() && builder.getResponse().getId().isEmpty()) {
                    // i'm the recipient

                    HelperCheckUserInfoExist.checkUserInfoExist(roomMessage.getUserId());

                    RealmRoomMessage realmRoomMessage =
                        realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, roomMessage.getMessageId()).findFirst();

                    if (realmRoomMessage == null) {
                        realmRoomMessage = realm.createObject(RealmRoomMessage.class);
                        realmRoomMessage.setMessageId(roomMessage.getMessageId());
                        realmRoomMessage.setRoomId(builder.getRoomId());
                    }

                    fillRoomMessage(realmRoomMessage, roomMessage);

                    if (roomMessage.hasForwardFrom()) {
                        RealmRoomMessage forward = realm.createObject(RealmRoomMessage.class);
                        forward.setMessageId(System.nanoTime());

                        realmRoomMessage.setForwardMessage(fillRoomMessage(forward, roomMessage));
                    } else if (roomMessage.hasReplyTo()) { // reply message

                        RealmRoomMessage reply = realm.createObject(RealmRoomMessage.class);
                        reply.setMessageId(System.currentTimeMillis());

                        realmRoomMessage.setReplyTo(fillRoomMessage(reply, roomMessage));
                    }

                    G.helperNotificationAndBadge.checkAlert(true, ProtoGlobal.Room.Type.GROUP, roomMessage.getMessageId());
                } else {
                    // i'm the sender
                    // update message fields into database
                    RealmResults<RealmRoomMessage> realmRoomMessageRealmResults =
                        realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, builder.getRoomId()).findAll();
                    for (RealmRoomMessage realmRoomMessage : realmRoomMessageRealmResults) {
                        // find the message using identity and update it
                        Log.i("III", "realmRoomMessage : " + realmRoomMessage);
                        if (realmRoomMessage != null && realmRoomMessage.getMessageId() == Long.parseLong(identity)) {
                            Log.i("III", "fillRoomMessage");
                            fillRoomMessage(realmRoomMessage, roomMessage);

                            if (roomMessage.hasForwardFrom()) { // forward message

                                RealmRoomMessage forwardMessage = realmRoomMessage.getForwardMessage();
                                // forwardMessage shouldn't be null but client check it for insuring
                                if (forwardMessage == null) {
                                    forwardMessage = realm.createObject(RealmRoomMessage.class);
                                    forwardMessage.setMessageId(System.nanoTime());
                                }
                                realmRoomMessage.setForwardMessage(fillRoomMessage(forwardMessage, roomMessage));
                            } else if (roomMessage.hasReplyTo()) { // reply message

                                RealmRoomMessage replyMessage = realmRoomMessage.getForwardMessage();
                                // replyMessage shouldn't be null but client check it for insuring
                                if (replyMessage == null) {
                                    replyMessage = realm.createObject(RealmRoomMessage.class);
                                    replyMessage.setMessageId(System.nanoTime());
                                }
                                realmRoomMessage.setReplyTo(fillRoomMessage(replyMessage, roomMessage));
                            }

                            realm.copyToRealmOrUpdate(realmRoomMessage);
                            break;
                        }
                    }
                }
            }
        });

        if (userId != roomMessage.getUserId() && builder.getResponse().getId().isEmpty()) {
            // invoke following callback when i'm not the sender, because I already done
            // everything after sending message
            if (realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst() != null) {
                G.chatSendMessageUtil.onMessageReceive(builder.getRoomId(), roomMessage.getMessage(), roomMessage.getMessageType().toString(),
                    roomMessage, ProtoGlobal.Room.Type.GROUP);
            }
        } else {
            // invoke following callback when I'm the sender and the message has updated
            G.chatSendMessageUtil.onMessageUpdate(builder.getRoomId(), roomMessage.getMessageId(), roomMessage.getStatus(), identity, roomMessage);
        }

        realm.close();
    }

    private RealmRoomMessage fillRoomMessage(RealmRoomMessage realmRoomMessage, ProtoGlobal.RoomMessage roomMessage) {
        realmRoomMessage.setMessageVersion(roomMessage.getMessageVersion());
        realmRoomMessage.setStatus(roomMessage.getStatus().toString());
        realmRoomMessage.setMessageType(roomMessage.getMessageType().toString());
        realmRoomMessage.setMessage(roomMessage.getMessage());

        if (roomMessage.hasAttachment()) {
            realmRoomMessage.setAttachment(roomMessage.getMessageId(), roomMessage.getAttachment());
        }
        realmRoomMessage.setUserId(roomMessage.getUserId());
        if (roomMessage.hasLocation()) {
            realmRoomMessage.setLocation(RealmRoomMessageLocation.build(roomMessage.getLocation()));
        } else if (roomMessage.hasLog()) {
            realmRoomMessage.setLog(RealmRoomMessageLog.build(roomMessage.getLog()));
        } else if (roomMessage.hasContact()) {
            realmRoomMessage.setRoomMessageContact(RealmRoomMessageContact.build(roomMessage.getContact()));
        }
        realmRoomMessage.setEdited(roomMessage.getEdited());
        realmRoomMessage.setUpdateTime(roomMessage.getUpdateTime() * DateUtils.SECOND_IN_MILLIS);

        return realmRoomMessage;
    }

    @Override public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "GroupSendMessageResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "GroupSendMessageResponse response.minorCode() : " + minorCode);
    }

    @Override public void timeOut() {
        super.timeOut();
        // message failed
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmRoomMessage message =
                    realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(identity)).findFirst();
                if (message != null) {
                    message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
                    G.chatSendMessageUtil.onMessageFailed(message.getRoomId(), message, ProtoGlobal.Room.Type.CHAT);
                }
            }
        });
    }
}
