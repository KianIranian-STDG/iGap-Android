package com.iGap.request;

import com.iGap.proto.ProtoGroupAvatarAdd;

public class RequestGroupAvatarGetList {

    public void groupAvatarGetList(long roomId) {

        ProtoGroupAvatarAdd.GroupAvatarAdd.Builder builder =
            ProtoGroupAvatarAdd.GroupAvatarAdd.newBuilder();
        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(314, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

