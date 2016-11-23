
package com.iGap.request;

import com.iGap.proto.ProtoChannelAvatarGetList;

public class RequestChannelAvatarGetList {

    public void channelAvatarGetList(long roomId) {
        ProtoChannelAvatarGetList.ChannelAvatarGetList.Builder builder = ProtoChannelAvatarGetList.ChannelAvatarGetList.newBuilder();
        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(414, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
