package com.iGap.response;

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
        ProtoChatGetRoom.ChatGetRoomResponse.Builder chatGetRoomResponse = (ProtoChatGetRoom.ChatGetRoomResponse.Builder) message;

        /**
         * before client just get roomId from server and send that with receiver
         * and later get room info . but now client receive room with complete
         * info , but now i send roomId like before . i do that for don't change
         * other code . because i guess don't need for this actions.
         *
         * hint : we can set another interface for another state.
         */
        G.onChatGetRoom.onChatGetRoom(chatGetRoomResponse.getRoom().getId());
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

        G.onChatGetRoom.onChatGetRoomError(majorCode, minorCode);
    }
}


