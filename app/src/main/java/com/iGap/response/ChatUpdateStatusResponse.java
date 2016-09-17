package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatUpdateStatus;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoResponse;
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

        ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(chatUpdateStatus.getResponse());
        Log.i("SOC", "ChatUpdateStatusResponse response.getId() : " + response.getId());
        Log.i("SOC", "ChatUpdateStatusResponse response.getTimestamp() : " + response.getTimestamp());
        Log.i("MMM", "ChatSendMessageResponse chatUpdateStatus : " + chatUpdateStatus.getStatus());

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // find message from database and update its status
                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", chatUpdateStatus.getMessageId()).findFirst();
                if (roomMessage != null) {
                    Log.i(ChatUpdateStatusResponse.class.getSimpleName(), "oftad > " + chatUpdateStatus.getStatus().toString());
                    roomMessage.setStatus(chatUpdateStatus.getStatus().toString());
                    realm.copyToRealmOrUpdate(roomMessage);

                    G.chatUpdateStatusUtil.onChatUpdateStatus(chatUpdateStatus.getRoomId(), chatUpdateStatus.getMessageId(), chatUpdateStatus.getStatus(), chatUpdateStatus.getStatusVersion());
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


