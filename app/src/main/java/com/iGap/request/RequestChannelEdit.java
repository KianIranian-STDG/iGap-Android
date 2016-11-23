package com.iGap.request;

import com.iGap.proto.ProtoChannelEdit;

public class RequestChannelEdit {

    public void channelEdit(long roomId, String name, String description) {

        ProtoChannelEdit.ChannelEdit.Builder builder = ProtoChannelEdit.ChannelEdit.newBuilder();
        builder.setRoomId(roomId);
        builder.setName(name);
        builder.setDescription(description);

        RequestWrapper requestWrapper = new RequestWrapper(405, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
