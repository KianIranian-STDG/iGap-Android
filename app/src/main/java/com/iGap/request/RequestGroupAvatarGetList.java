package com.iGap.request;

import com.iGap.proto.ProtoGroupAvatarGetList;

public class RequestGroupAvatarGetList {

    public void groupAvatarGetList(long roomId) {

        ProtoGroupAvatarGetList.GroupAvatarGetList.Builder builder = ProtoGroupAvatarGetList.GroupAvatarGetList.newBuilder();
        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(314, builder, roomId + "");
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

