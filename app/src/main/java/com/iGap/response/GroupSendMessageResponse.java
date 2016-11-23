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

    @Override
    public void handler() {
        super.handler();
        Realm realm = Realm.getDefaultInstance();

        final ProtoGroupSendMessage.GroupSendMessageResponse.Builder builder = (ProtoGroupSendMessage.GroupSendMessageResponse.Builder) message;
        final ProtoGlobal.RoomMessage roomMessage = builder.getRoomMessage();
        final long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // set info for clientCondition
                RealmClientCondition realmClientCondition =
                        realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, builder.getRoomId()).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.setMessageVersion(roomMessage.getMessageVersion());
                    realmClientCondition.setStatusVersion(roomMessage.getStatusVersion());
                }


                // because user may have more than one device, his another device should not be
                // recipient
                // but sender. so I check current userId with room message user id, and if not
                // equals
                // and response is null, so we sure recipient is another user
                //TODO [Saeed Mozaffari] [2016-11-13 7:40 PM] - AUTHOR_CHECK . niaz hast inja check beshe ke author user bud ya room? chon inja vase group hast va faghat user darim.
                if (userId != roomMessage.getAuthor().getUser().getUserId() && builder.getResponse().getId().isEmpty()) {
                    // i'm the recipient

                    HelperCheckUserInfoExist.checkUserInfoExist(roomMessage.getAuthor().getUser().getUserId());

                    RealmRoomMessage.putOrUpdate(roomMessage, builder.getRoomId());

                    G.helperNotificationAndBadge.checkAlert(true, ProtoGlobal.Room.Type.GROUP, roomMessage.getMessageId());
                } else {
                    // i'm the sender
                    // update message fields into database
                    RealmResults<RealmRoomMessage> realmRoomMessageRealmResults =
                            realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, builder.getRoomId()).findAll();
                    for (RealmRoomMessage realmRoomMessage : realmRoomMessageRealmResults) {
                        // find the message using identity and update it
                        if (realmRoomMessage != null && realmRoomMessage.getMessageId() == Long.parseLong(identity)) {
                            if (realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, roomMessage.getMessageId()).count() == 0) {
                                RealmRoomMessage.updateId(Long.parseLong(identity), roomMessage.getMessageId());
                            }

                            RealmRoomMessage.putOrUpdate(roomMessage, builder.getRoomId());
                            break;
                        }
                    }
                }

                // if first message received but the room doesn't exist, create new room
                RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();
                if (room == null) {
                    // make get room request
                    new RequestClientGetRoom().clientGetRoom(builder.getRoomId());
                } else {
                    // update last message sent/received in room table
                    if (room.getLastMessage() != null) {
                        if (room.getLastMessage().getMessageId() < roomMessage.getMessageId()) {
                            room.setLastMessage(RealmRoomMessage.putOrUpdate(roomMessage, builder.getRoomId()));
                        }
                    } else {
                        room.setLastMessage(RealmRoomMessage.putOrUpdate(roomMessage, builder.getRoomId()));
                    }
                }
            }
        });

        if (userId != roomMessage.getAuthor().getUser().getUserId() && builder.getResponse().getId().isEmpty()) {
            // invoke following callback when i'm not the sender, because I already done
            // everything after sending message
            if (realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst() != null) {
                G.chatSendMessageUtil.onMessageReceive(builder.getRoomId(), roomMessage.getMessage(), roomMessage.getMessageType(),
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
        realmRoomMessage.setMessageType(roomMessage.getMessageType());
        realmRoomMessage.setMessage(roomMessage.getMessage());

        if (roomMessage.hasAttachment()) {
            realmRoomMessage.setAttachment(roomMessage.getMessageId(), roomMessage.getAttachment());
        }
        realmRoomMessage.setUserId(roomMessage.getAuthor().getUser().getUserId());
        if (roomMessage.hasLocation()) {
            realmRoomMessage.setLocation(RealmRoomMessageLocation.build(roomMessage.getLocation()));
        } else if (roomMessage.hasLog()) {
            realmRoomMessage.setLog(RealmRoomMessageLog.build(roomMessage.getLog()));
        } else if (roomMessage.hasContact()) {
            realmRoomMessage.setRoomMessageContact(RealmRoomMessageContact.build(roomMessage.getContact()));
        }
        realmRoomMessage.setEdited(roomMessage.getEdited());
        realmRoomMessage.setUpdateTime(roomMessage.getUpdateTime() * DateUtils.SECOND_IN_MILLIS);
        realmRoomMessage.setCreateTime(roomMessage.getCreateTime() * DateUtils.SECOND_IN_MILLIS);

        return realmRoomMessage;
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "GroupSendMessageResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "GroupSendMessageResponse response.minorCode() : " + minorCode);
    }

    @Override
    public void timeOut() {
        super.timeOut();
        // message failed
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
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
