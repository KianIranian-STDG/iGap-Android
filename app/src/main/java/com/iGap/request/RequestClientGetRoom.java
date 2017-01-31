package com.iGap.request;

import com.iGap.proto.ProtoClientGetRoom;

public class RequestClientGetRoom {

    public enum CreateRoomMode {

        requestFromServer,
        requestFromOwner;
    }

    public void clientGetRoom(long roomId, CreateRoomMode mode) {
        ProtoClientGetRoom.ClientGetRoom.Builder clientGetRoom = ProtoClientGetRoom.ClientGetRoom.newBuilder();
        clientGetRoom.setRoomId(roomId);

        String identity = "";

        if (mode != null) identity = mode.toString();

        RequestWrapper requestWrapper = new RequestWrapper(602, clientGetRoom, identity);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}