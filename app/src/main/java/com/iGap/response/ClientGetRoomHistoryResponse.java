package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoClientGetRoomHistory;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmRoomMessage;
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

        Realm realm = Realm.getDefaultInstance();
        final long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();

        final ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse.Builder builder = (ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse.Builder) message;
        builder.getMessageList();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (ProtoGlobal.RoomMessage roomMessage : builder.getMessageList()) {

                    // set info for clientCondition
                    RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo("roomId", identity).findFirst();
                    if (realmClientCondition != null) {
                        realmClientCondition.setMessageVersion(roomMessage.getMessageVersion());
                        realmClientCondition.setStatusVersion(roomMessage.getStatusVersion());
                    }

                    RealmChatHistory realmChatHistory = realm.createObject(RealmChatHistory.class);
                    realmChatHistory.setId(System.currentTimeMillis());

                    RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", roomMessage.getMessageId()).findFirst();

                    if (realmRoomMessage == null) {
                        realmRoomMessage = realm.createObject(RealmRoomMessage.class);
                        realmRoomMessage.setMessageId(roomMessage.getMessageId());
                    }

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

                    realmChatHistory.setRoomId(Long.parseLong(identity));
                    realmChatHistory.setRoomMessage(realmRoomMessage);

                    realm.copyToRealm(realmChatHistory);

                    if (roomMessage.getUserId() != userId) { // show notification if this message isn't for another account
                        if (!G.isAppInFg) {
                            G.helperNotificationAndBadge.updateNotificationAndBadge(true);
                        }
                    }

                    G.onClientGetRoomHistoryResponse.onGetRoomHistory(Long.parseLong(identity), roomMessage.getMessage(), roomMessage.getMessageType().toString(), roomMessage);
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
    }
}


