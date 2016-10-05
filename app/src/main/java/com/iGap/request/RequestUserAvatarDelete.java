package com.iGap.request;

import com.iGap.proto.ProtoUserAvatarDelete;

public class RequestUserAvatarDelete {

    public void userAvatarDelete(long id) {

        ProtoUserAvatarDelete.UserAvatarDelete.Builder builder = ProtoUserAvatarDelete.UserAvatarDelete.newBuilder();
        builder.setId(id);

        RequestWrapper requestWrapper = new RequestWrapper(115, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

