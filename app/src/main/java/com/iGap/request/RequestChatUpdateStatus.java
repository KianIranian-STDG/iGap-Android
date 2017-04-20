package com.iGap.request;

import com.iGap.proto.ProtoChatUpdateStatus;
import com.iGap.proto.ProtoGlobal;

public class RequestChatUpdateStatus {

    public void updateStatus(long roomId, long messageId, ProtoGlobal.RoomMessageStatus roomMessageStatus) {
        ProtoChatUpdateStatus.ChatUpdateStatus.Builder chatUpdateStatus = ProtoChatUpdateStatus.ChatUpdateStatus.newBuilder();
        chatUpdateStatus.setRoomId(roomId);
        chatUpdateStatus.setMessageId(messageId);
        chatUpdateStatus.setStatus(roomMessageStatus);

        RequestWrapper requestWrapper = new RequestWrapper(202, chatUpdateStatus);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}