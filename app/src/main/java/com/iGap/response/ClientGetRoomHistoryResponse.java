package com.iGap.response;

import android.text.format.DateUtils;
import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoClientGetRoomHistory;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageContact;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.RealmRoomMessageLocation;
import com.iGap.realm.RealmRoomMessageLog;
import com.iGap.realm.RealmUserInfo;

import io.realm.Realm;

public class ClientGetRoomHistoryResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientGetRoomHistoryResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        Log.i("YYY", "ClientGetRoomHistoryResponse handler : " + message);
        Realm realm = Realm.getDefaultInstance();
        final long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();

        final ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse.Builder builder =
                (ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse.Builder) message;

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (ProtoGlobal.RoomMessage roomMessage : builder.getMessageList()) {

                    // set info for clientCondition
                    RealmClientCondition realmClientCondition =
                            realm.where(RealmClientCondition.class)
                                    .equalTo(RealmClientConditionFields.ROOM_ID, Long.parseLong(identity))
                                    .findFirst();
                    if (realmClientCondition != null) {
                        realmClientCondition.setMessageVersion(roomMessage.getMessageVersion());
                        realmClientCondition.setStatusVersion(roomMessage.getStatusVersion());
                    }

                    RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class)
                            .equalTo(RealmRoomMessageFields.MESSAGE_ID, roomMessage.getMessageId())
                            .findFirst();

                    if (realmRoomMessage == null) {
                        realmRoomMessage = realm.createObject(RealmRoomMessage.class);
                        realmRoomMessage.setMessageId(roomMessage.getMessageId());
                        realmRoomMessage.setRoomId(Long.parseLong(identity));
                    }

                    realmRoomMessage.setMessageVersion(roomMessage.getMessageVersion());
                    realmRoomMessage.setStatus(roomMessage.getStatus().toString());
                    realmRoomMessage.setMessageType(roomMessage.getMessageType().toString());
                    realmRoomMessage.setMessage(roomMessage.getMessage());

                    realmRoomMessage.setAttachment(roomMessage.getMessageId(),
                            roomMessage.getAttachment());
                    realmRoomMessage.setUserId(roomMessage.getUserId());
                    realmRoomMessage.setLocation(
                            RealmRoomMessageLocation.build(roomMessage.getLocation()));
                    realmRoomMessage.setLog(RealmRoomMessageLog.build(roomMessage.getLog()));
                    realmRoomMessage.setRoomMessageContact(
                            RealmRoomMessageContact.build(roomMessage.getContact()));
                    realmRoomMessage.setEdited(roomMessage.getEdited());
                    realmRoomMessage.setUpdateTime(
                            roomMessage.getUpdateTime() * DateUtils.SECOND_IN_MILLIS);

                    if (roomMessage.getUserId()
                            != userId) { // show notification if this message isn't for another account
                        if (!G.isAppInFg) {

                            G.helperNotificationAndBadge.checkAlert(true,
                                    ProtoGlobal.Room.Type.CHAT, Long.parseLong(identity));
                        }
                    }

                    G.onClientGetRoomHistoryResponse.onGetRoomHistory(Long.parseLong(identity),
                            roomMessage.getMessage(), roomMessage.getMessageType().toString(),
                            roomMessage);
                }
            }
        });

        realm.close();
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("XXX", "ClientGetRoomHistoryResponse response.majorCode() : " + majorCode);
        Log.i("XXX", "ClientGetRoomHistoryResponse response.minorCode() : " + minorCode);

        G.onClientGetRoomHistoryResponse.onGetRoomHistoryError(majorCode, minorCode);
    }
}


