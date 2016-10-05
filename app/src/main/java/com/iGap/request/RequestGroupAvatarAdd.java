package com.iGap.request;

import com.iGap.proto.ProtoGroupAvatarAdd;

public class RequestGroupAvatarAdd {

    public void groupAvatarAdd(long roomId, String attachment) {

        ProtoGroupAvatarAdd.GroupAvatarAdd.Builder builder = ProtoGroupAvatarAdd.GroupAvatarAdd.newBuilder();
        builder.setRoomId(roomId);
        builder.setAttachment(attachment);

        RequestWrapper requestWrapper = new RequestWrapper(312, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

