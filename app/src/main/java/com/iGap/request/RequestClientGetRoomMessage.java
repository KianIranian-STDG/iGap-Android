
package com.iGap.request;

import com.iGap.proto.ProtoClientGetRoomMessage;

public class RequestClientGetRoomMessage {

    public void clientGetRoomMessage(long roomId, long messageId) {
        ProtoClientGetRoomMessage.ClientGetRoomMessage.Builder builder = ProtoClientGetRoomMessage.ClientGetRoomMessage.newBuilder();
        builder.setRoomId(roomId);
        builder.setMessageId(messageId);

        RequestWrapper requestWrapper = new RequestWrapper(604, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
