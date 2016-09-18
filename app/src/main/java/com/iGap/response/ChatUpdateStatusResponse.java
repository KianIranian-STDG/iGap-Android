package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatUpdateStatus;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmOfflineSeen;
import com.iGap.realm.RealmRoomMessage;

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
    public void handler() { //TODO [Saeed Mozaffari] [2016-09-17 4:05 PM] - FORCE for remove seen from client condition
        final ProtoChatUpdateStatus.ChatUpdateStatusResponse.Builder chatUpdateStatus = (ProtoChatUpdateStatus.ChatUpdateStatusResponse.Builder) message;

        final ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(chatUpdateStatus.getResponse());
        Log.i("SOC_CONDITION", "ChatUpdateStatusResponse response.getId() : " + response.getId());
        Log.i("SOC_CONDITION", "ChatUpdateStatusResponse response.getTimestamp() : " + response.getTimestamp());
        Log.i("SOC_CONDITION", "ChatSendMessageResponse chatUpdateStatus : " + chatUpdateStatus.getStatus());

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (!response.getId().isEmpty()) { // I'm sender

                    RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo("roomId", chatUpdateStatus.getRoomId()).findFirst();

                    for (RealmOfflineSeen realmOfflineSeen : realmClientCondition.getOfflineSeen()) { // can do this with contains ?
                        if (realmOfflineSeen.getOfflineSeen() == chatUpdateStatus.getMessageId()) {
                            Log.i("SOC_CONDITION", "realmOfflineSeen 1 : " + realmOfflineSeen);
                            realmOfflineSeen.deleteFromRealm();
                            Log.i("SOC_CONDITION", "realmOfflineSeen 2 : " + realmOfflineSeen);
                        }
                    }

                } else { // I'm recipient

                    // find message from database and update its status
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", chatUpdateStatus.getMessageId()).findFirst();
                    Log.i("SOC_CONDITION", "I'm recipient 1");
                    if (roomMessage != null) {
                        Log.i(ChatUpdateStatusResponse.class.getSimpleName(), "oftad > " + chatUpdateStatus.getStatus().toString());
                        roomMessage.setStatus(chatUpdateStatus.getStatus().toString());
                        realm.copyToRealmOrUpdate(roomMessage);
                        Log.i("SOC_CONDITION", "I'm recipient ");
                        G.chatUpdateStatusUtil.onChatUpdateStatus(chatUpdateStatus.getRoomId(), chatUpdateStatus.getMessageId(), chatUpdateStatus.getStatus(), chatUpdateStatus.getStatusVersion());
                    }

                }
            }
        });
        realm.close();
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "ChatUpdateStatusResponse timeout");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "ChatUpdateStatusResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "ChatUpdateStatusResponse response.minorCode() : " + minorCode);
    }
}


