package com.iGap.request;

import com.iGap.proto.ProtoGroupCreate;

public class RequestGroupCreate {

    public void groupCreate(String name, String description) {

        ProtoGroupCreate.GroupCreate.Builder builder = ProtoGroupCreate.GroupCreate.newBuilder();
        builder.setName(name);
        builder.setDescription(description);

        RequestWrapper requestWrapper = new RequestWrapper(300, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

