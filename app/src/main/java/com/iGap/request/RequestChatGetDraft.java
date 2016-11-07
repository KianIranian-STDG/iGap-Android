package com.iGap.request;

import com.iGap.proto.ProtoChatGetDraft;

public class RequestChatGetDraft {

    public void chatGetDraft(long roomId) {

        ProtoChatGetDraft.ChatGetDraft.Builder builder =
                ProtoChatGetDraft.ChatGetDraft.newBuilder();
        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(208, builder, roomId + "");
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

