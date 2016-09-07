package com.iGap.request;

import com.iGap.proto.ProtoChatDelete;

public class RequestChatDelete {

    public void chatDelete(long roomId) {

        ProtoChatDelete.ChatDelete.Builder builder = ProtoChatDelete.ChatDelete.newBuilder();
        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(206, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
