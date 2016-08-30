package com.iGap.request;

import com.iGap.proto.ProtoChatGetRoom;

public class RequestChatGetRoom {

    public void chatGetRoom(int peerId) {

        ProtoChatGetRoom.ChatGetRoom.Builder chatGetRoom = ProtoChatGetRoom.ChatGetRoom.newBuilder();
        chatGetRoom.setPeerId(peerId);

        RequestWrapper requestWrapper = new RequestWrapper(200, chatGetRoom);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}