package com.iGap.request;

import com.iGap.proto.ProtoClientGetRoomHistory;

public class RequestClientGetRoomHistory {

    public void getRoomHistory(long roomId, long firstMessageId) {

        ProtoClientGetRoomHistory.ClientGetRoomHistory.Builder builder = ProtoClientGetRoomHistory.ClientGetRoomHistory.newBuilder();
        builder.setRoomId(roomId);
        builder.setFirstMessageId(firstMessageId);

        RequestWrapper requestWrapper = new RequestWrapper(603, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
