package com.iGap.request;

import com.iGap.proto.ProtoGroupEdit;

public class RequestGroupEdit {

    public void groupEdit(long roomId, String name, String description) {

        ProtoGroupEdit.GroupEdit.Builder builder = ProtoGroupEdit.GroupEdit.newBuilder();
        builder.setRoomId(roomId);
        builder.setName(name);
        builder.setDescription(description);

        RequestWrapper requestWrapper = new RequestWrapper(305, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

