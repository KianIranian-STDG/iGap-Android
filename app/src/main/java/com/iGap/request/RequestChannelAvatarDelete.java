
package com.iGap.request;

import com.iGap.proto.ProtoChannelAvatarDelete;

public class RequestChannelAvatarDelete {

    public void channelAvatarDelete(long roomId, long avatarId) {
        ProtoChannelAvatarDelete.ChannelAvatarDelete.Builder builder = ProtoChannelAvatarDelete.ChannelAvatarDelete.newBuilder();
        builder.setRoomId(roomId);
        builder.setId(avatarId);

        RequestWrapper requestWrapper = new RequestWrapper(413, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
