
package com.iGap.request;

import com.iGap.proto.ProtoChannelAddMessageReaction;
import com.iGap.proto.ProtoGlobal;

public class RequestChannelAddMessageReaction {

    public void channelAddMessageReaction(long roomId, long messageId, ProtoGlobal.RoomMessageReaction roomMessageReaction) {

        ProtoChannelAddMessageReaction.ChannelAddMessageReaction.Builder builder = ProtoChannelAddMessageReaction.ChannelAddMessageReaction.newBuilder();
        builder.setRoomId(roomId);
        builder.setMessageId(messageId);
        builder.setReaction(roomMessageReaction);

        String identity = Long.toString(roomId) + '*' + Long.toString(messageId) + '*' + roomMessageReaction.toString();

        RequestWrapper requestWrapper = new RequestWrapper(424, builder, identity);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
