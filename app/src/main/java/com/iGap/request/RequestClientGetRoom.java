package com.iGap.request;

import com.iGap.proto.ProtoClientGetRoom;

public class RequestClientGetRoom {

    public void clientGetRoom(long roomId) {
        ProtoClientGetRoom.ClientGetRoom.Builder clientGetRoom =
            ProtoClientGetRoom.ClientGetRoom.newBuilder();
        clientGetRoom.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(602, clientGetRoom);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}