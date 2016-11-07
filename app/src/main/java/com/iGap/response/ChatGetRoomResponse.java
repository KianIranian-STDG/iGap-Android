package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatGetRoom;
import com.iGap.proto.ProtoError;

public class ChatGetRoomResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatGetRoomResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoChatGetRoom.ChatGetRoomResponse.Builder chatGetRoomResponse =
                (ProtoChatGetRoom.ChatGetRoomResponse.Builder) message;
        G.onChatGetRoom.onChatGetRoom(chatGetRoomResponse.getRoomId());
    }

    @Override
    public void timeOut() {
        super.timeOut();
        G.onChatGetRoom.onChatGetRoomTimeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "ClientGetRoomResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "ClientGetRoomResponse response.minorCode() : " + minorCode);
        G.onChatGetRoom.onChatGetRoomError(majorCode, minorCode);
    }
}


