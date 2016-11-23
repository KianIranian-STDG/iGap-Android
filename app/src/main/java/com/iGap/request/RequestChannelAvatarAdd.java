
package com.iGap.request;

import com.iGap.proto.ProtoChannelAvatarAdd;

public class RequestChannelAvatarAdd {

    public void channelAvatarAdd(long roomId, String attachment) {
        ProtoChannelAvatarAdd.ChannelAvatarAdd.Builder builder = ProtoChannelAvatarAdd.ChannelAvatarAdd.newBuilder();
        builder.setRoomId(roomId);
        builder.setAttachment(attachment);

        RequestWrapper requestWrapper = new RequestWrapper(412, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
