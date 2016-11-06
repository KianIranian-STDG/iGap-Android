package com.iGap.response;

import android.util.Log;
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

    @Override public void handler() {
        final ProtoChatUpdateStatus.ChatUpdateStatusResponse.Builder chatUpdateStatus =
            (ProtoChatUpdateStatus.ChatUpdateStatusResponse.Builder) message;

        final ProtoResponse.Response.Builder response =
            ProtoResponse.Response.newBuilder().mergeFrom(chatUpdateStatus.getResponse());
        Log.i("SOC_CONDITION", "ChatUpdateStatusResponse response.getId() : " + response.getId());
        Log.i("SOC_CONDITION",
            "ChatUpdateStatusResponse response.getTimestamp() : " + response.getTimestamp());
        Log.i("SOC_CONDITION",
            "ChatSendMessageResponse chatUpdateStatus : " + chatUpdateStatus.getStatus());

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                if (!response.getId().isEmpty()) { // I'm sender
                    Log.i("CLI", "chatUpdateStatus getId : " + chatUpdateStatus.getMessageId());
                    RealmClientCondition realmClientCondition =
                        realm.where(RealmClientCondition.class)
                            .equalTo(RealmClientConditionFields.ROOM_ID,
                                chatUpdateStatus.getRoomId())
                            .findFirst();
                    // RealmList<RealmOfflineSeen> offlineSeen = realmClientCondition.getOfflineSeen();
                    for (RealmOfflineSeen realmOfflineSeen : realmClientCondition.getOfflineSeen()) {
                        if (realmOfflineSeen.getOfflineSeen() == chatUpdateStatus.getMessageId()) {
                            realmOfflineSeen.deleteFromRealm();
                            Log.i("CLI", "chatUpdateStatus Delete Seen");
                            break;
                        }
                    }
                    //                    for (int i = offlineSeen.size() - 1; i >= 0; i--) {
                    //                        RealmOfflineSeen realmOfflineSeen = offlineSeen.get(i);
                    //                        Log.i("SOC_CONDITION", "realmOfflineSeen 1 : " + realmOfflineSeen);
                    //                        realmOfflineSeen.deleteFromRealm();
                    //                    }
                } else { // I'm recipient

                    // find message from database and update its status
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class)
                        .equalTo(RealmRoomMessageFields.MESSAGE_ID, chatUpdateStatus.getMessageId())
                        .findFirst();
                    Log.i("SOC_CONDITION", "I'm recipient 1");
                    if (roomMessage != null) {
                        Log.i(ChatUpdateStatusResponse.class.getSimpleName(),
                            "oftad > " + chatUpdateStatus.getStatus().toString());
                        roomMessage.setStatus(chatUpdateStatus.getStatus().toString());
                        realm.copyToRealmOrUpdate(roomMessage);
                        Log.i("SOC_CONDITION", "I'm recipient ");
                        G.chatUpdateStatusUtil.onChatUpdateStatus(chatUpdateStatus.getRoomId(),
                            chatUpdateStatus.getMessageId(), chatUpdateStatus.getStatus(),
                            chatUpdateStatus.getStatusVersion());
                    }
                }
            }
        });
        realm.close();
    }

    @Override public void timeOut() {
        Log.i("SOC", "ChatUpdateStatusResponse timeout");
    }

    @Override public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "ChatUpdateStatusResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "ChatUpdateStatusResponse response.minorCode() : " + minorCode);
    }
}


