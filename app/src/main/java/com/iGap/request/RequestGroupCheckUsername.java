
package com.iGap.request;

import com.iGap.proto.ProtoGroupCheckUsername;

public class RequestGroupCheckUsername {

    public void GroupCheckUsername(long roomId, String username) {


        ProtoGroupCheckUsername.GroupCheckUsername.Builder builder = ProtoGroupCheckUsername.GroupCheckUsername.newBuilder();
        builder.setRoomId(roomId);
        builder.setUsername(username);

        RequestWrapper requestWrapper = new RequestWrapper(321, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
