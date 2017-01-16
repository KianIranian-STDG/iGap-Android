package com.iGap.request;

import com.iGap.proto.ProtoGroupEditMessage;

public class RequestGroupEditMessage {

    public void groupEditMessage(long roomId, long messageId, String message) {

        ProtoGroupEditMessage.GroupEditMessage.Builder chatEditMessage = ProtoGroupEditMessage.GroupEditMessage.newBuilder();
        chatEditMessage.setRoomId(roomId);
        chatEditMessage.setMessageId(messageId);
        chatEditMessage.setMessage(message);

        RequestWrapper requestWrapper = new RequestWrapper(325, chatEditMessage);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

