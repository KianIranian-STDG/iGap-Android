package com.iGap.request;

import com.iGap.proto.ProtoUserAvatarAdd;

public class RequestUserAvatarAdd {

    public void userAddAvatar(String attachment) {

        ProtoUserAvatarAdd.UserAvatarAdd.Builder builder =
                ProtoUserAvatarAdd.UserAvatarAdd.newBuilder();
        builder.setAttachment(attachment);

        RequestWrapper requestWrapper = new RequestWrapper(114, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

