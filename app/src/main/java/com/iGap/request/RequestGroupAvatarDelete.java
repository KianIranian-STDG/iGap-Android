package com.iGap.request;

import com.iGap.proto.ProtoGroupAvatarDelete;

public class RequestGroupAvatarDelete {

    public void groupAvatarDelete(long roomId, long id) {

        ProtoGroupAvatarDelete.GroupAvatarDelete.Builder builder = ProtoGroupAvatarDelete.GroupAvatarDelete.newBuilder();
        builder.setRoomId(roomId);
        builder.setId(id);

        RequestWrapper requestWrapper = new RequestWrapper(313, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

