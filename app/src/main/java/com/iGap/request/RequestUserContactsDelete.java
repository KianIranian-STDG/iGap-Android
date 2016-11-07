package com.iGap.request;

import com.iGap.proto.ProtoUserContactsDelete;

public class RequestUserContactsDelete {

    public void contactsDelete(String phone) {

        ProtoUserContactsDelete.UserContactsDelete.Builder builder =
                ProtoUserContactsDelete.UserContactsDelete.newBuilder();
        builder.setPhone(Long.parseLong(phone));
        RequestWrapper requestWrapper = new RequestWrapper(108, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}