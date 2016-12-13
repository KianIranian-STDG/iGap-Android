
package com.iGap.request;

import com.iGap.proto.ProtoClientSubscribeToRoom;

public class RequestClientSubscribeToRoom {

    public void clientSubscribeToRoom(long roomId) {
        ProtoClientSubscribeToRoom.ClientSubscribeToRoom.Builder builder = ProtoClientSubscribeToRoom.ClientSubscribeToRoom.newBuilder();
        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(610, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
