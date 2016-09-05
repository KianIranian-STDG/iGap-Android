package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatDeleteMessage;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmRoomMessage;

import io.realm.Realm;

public class ChatDeleteMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatDeleteMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        final ProtoChatDeleteMessage.ChatDeleteMessageResponse.Builder chatDeleteMessage = (ProtoChatDeleteMessage.ChatDeleteMessageResponse.Builder) message;

        ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(chatDeleteMessage.getResponse());
        Log.i("SOC", "ChatDeleteMessageResponse response.getId() : " + response.getId());
        Log.i("SOC", "ChatDeleteMessageResponse response.getTimestamp() : " + response.getTimestamp());

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", chatDeleteMessage.getMessageId()).findFirst();
                if (roomMessage != null) {
                    // delete message from database
                    roomMessage.deleteFromRealm();
                }
            }
        });
        realm.close();

        G.onChatDeleteMessageResponse.onChatDeleteMessage(chatDeleteMessage.getDeleteVersion(), chatDeleteMessage.getMessageId(), chatDeleteMessage.getRoomId(), chatDeleteMessage.getResponse());
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "ChatDeleteMessageResponse timeout");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "ChatDeleteMessageResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "ChatDeleteMessageResponse response.minorCode() : " + minorCode);
    }
}


