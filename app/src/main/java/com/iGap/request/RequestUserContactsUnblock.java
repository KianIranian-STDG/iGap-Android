
package com.iGap.request;

import com.iGap.proto.ProtoUserContactsBlock;

public class RequestUserContactsUnblock {

    public void userContactsUnblock(long userId) {
        ProtoUserContactsBlock.UserContactsBlock.Builder builder = ProtoUserContactsBlock.UserContactsBlock.newBuilder();
        builder.setUserId(userId);

        RequestWrapper requestWrapper = new RequestWrapper(129, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
