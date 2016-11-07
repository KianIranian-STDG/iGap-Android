package com.iGap.request;

import com.iGap.proto.ProtoClientSearchRoomHistory;

public class RequestClientSearchRoomHistory {

    public void clientSearchRoomHistory(long roomId, int offset, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter filter) {

        ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Builder builder = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.newBuilder();
        builder.setRoomId(roomId);
        builder.setOffset(offset);
        builder.setFilter(filter);

        RequestWrapper requestWrapper = new RequestWrapper(605, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

