
package com.iGap.request;

import com.iGap.proto.ProtoGroupRemoveUsername;

public class RequestGroupRemoveUsername {

    public void groupRemoveUsername(long roomId) {
        ProtoGroupRemoveUsername.GroupRemoveUsername.Builder builder = ProtoGroupRemoveUsername.GroupRemoveUsername.newBuilder();
        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(323, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
