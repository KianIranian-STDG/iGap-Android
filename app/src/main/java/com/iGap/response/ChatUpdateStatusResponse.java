package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChatUpdateStatus;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmOfflineSeen;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import io.realm.Realm;

public class ChatUpdateStatusResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatUpdateStatusResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoChatUpdateStatus.ChatUpdateStatusResponse.Builder chatUpdateStatus = (ProtoChatUpdateStatus.ChatUpdateStatusResponse.Builder) message;

        final ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(chatUpdateStatus.getResponse());

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (!response.getId().isEmpty()) { // I'm sender
                    RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, chatUpdateStatus.getRoomId()).findFirst();
                    for (RealmOfflineSeen realmOfflineSeen : realmClientCondition.getOfflineSeen()) {
                        if (realmOfflineSeen.getOfflineSeen() == chatUpdateStatus.getMessageId()) {
                            realmOfflineSeen.deleteFromRealm();
                            break;
                        }
                    }
                } else { // I'm recipient
                    // find message from database and update its status
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, chatUpdateStatus.getMessageId()).findFirst();
                    if (roomMessage != null) {
                        roomMessage.setStatus(chatUpdateStatus.getStatus().toString());
                        realm.copyToRealmOrUpdate(roomMessage);

                        G.chatUpdateStatusUtil.onChatUpdateStatus(chatUpdateStatus.getRoomId(), chatUpdateStatus.getMessageId(), chatUpdateStatus.getStatus(), chatUpdateStatus.getStatusVersion());
                    }
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
    }
}


