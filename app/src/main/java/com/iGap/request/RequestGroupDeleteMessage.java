package com.iGap.request;

import com.iGap.proto.ProtoGroupDeleteMessage;

public class RequestGroupDeleteMessage {

    public void groupDeleteMessage(long roomId, long messageId) {
        ProtoGroupDeleteMessage.GroupDeleteMessage.Builder builder = ProtoGroupDeleteMessage.GroupDeleteMessage.newBuilder();
        builder.setRoomId(roomId);
        builder.setMessageId(messageId);

        RequestWrapper requestWrapper = new RequestWrapper(320, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

