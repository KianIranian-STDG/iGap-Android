
package com.iGap.request;

import com.iGap.proto.ProtoChannelGetMemberList;

public class RequestChannelGetMemberList {

    public void channelGetMemberList(long roomId) {
        ProtoChannelGetMemberList.ChannelGetMemberList.Builder builder = ProtoChannelGetMemberList.ChannelGetMemberList.newBuilder();
        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(417, builder, roomId + "");
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
