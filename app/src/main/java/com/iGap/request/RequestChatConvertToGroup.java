package com.iGap.request;

import com.iGap.proto.ProtoChatConvertToGroup;

public class RequestChatConvertToGroup {

    public void chatConvertToGroup(long roomId, String name, String description) {

        ProtoChatConvertToGroup.ChatConvertToGroup.Builder builder = ProtoChatConvertToGroup.ChatConvertToGroup.newBuilder();
        builder.setRoomId(roomId);
        builder.setName(name);
        builder.setDescription(description);

        RequestWrapper requestWrapper = new RequestWrapper(209, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

