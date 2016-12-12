
package com.iGap.request;

import com.iGap.proto.ProtoGroupUpdateUsername;

public class RequestGroupUpdateUsername {

    public void groupUpdateUsername(long roomId, String username) {

        ProtoGroupUpdateUsername.GroupUpdateUsername.Builder builder = ProtoGroupUpdateUsername.GroupUpdateUsername.newBuilder();
        builder.setRoomId(roomId);
        builder.setUsername(username);

        RequestWrapper requestWrapper = new RequestWrapper(322, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
