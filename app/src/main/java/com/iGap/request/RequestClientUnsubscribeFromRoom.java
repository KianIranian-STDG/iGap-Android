
package com.iGap.request;

import com.iGap.proto.ProtoClientUnsubscribeFromRoom;

public class RequestClientUnsubscribeFromRoom {

    public void clientUnsubscribeFromRoom(long roomId) {

        ProtoClientUnsubscribeFromRoom.ClientUnsubscribeFromRoom.Builder builder = ProtoClientUnsubscribeFromRoom.ClientUnsubscribeFromRoom.newBuilder();
        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(611, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
