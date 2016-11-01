package com.iGap.request;

import com.iGap.proto.ProtoGroupGetDraft;

public class RequestGroupGetDraft {

    public void groupGetDraft(long roomId) {

        ProtoGroupGetDraft.GroupGetDraft.Builder builder =
            ProtoGroupGetDraft.GroupGetDraft.newBuilder();

        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(316, builder, roomId + "");
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

