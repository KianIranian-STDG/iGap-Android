package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatEditMessage;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmRoomMessage;

import io.realm.Realm;

public class ChatEditMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatEditMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        final ProtoChatEditMessage.ChatEditMessageResponse.Builder chatEditMessageResponse = (ProtoChatEditMessage.ChatEditMessageResponse.Builder) message;

        ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(chatEditMessageResponse.getResponse());
        Log.i("SOC", "ChatEditMessageResponse response.getId() : " + response.getId());
        Log.i("SOC", "ChatEditMessageResponse response.getTimestamp() : " + response.getTimestamp());

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", chatEditMessageResponse.getMessageId()).findFirst();
                if (roomMessage != null) {
                    // update message text in database
                    roomMessage.setMessage(chatEditMessageResponse.getMessage());
                    roomMessage.setEdited(true);

                    G.onChatEditMessageResponse.onChatEditMessage(chatEditMessageResponse.getRoomId(), chatEditMessageResponse.getMessageId(), chatEditMessageResponse.getMessageVersion(), chatEditMessageResponse.getMessage(), chatEditMessageResponse.getResponse());
                }
            }
        });
        realm.close();
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "ChatEditMessageResponse timeout");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "ChatEditMessageResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "ChatEditMessageResponse response.minorCode() : " + minorCode);
    }
}


