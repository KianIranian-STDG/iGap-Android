
package com.iGap.request;

import com.iGap.proto.ProtoChannelUpdateDraft;
import com.iGap.proto.ProtoGlobal;

public class RequestChannelUpdateDraft {

    public void channelUpdateDraft(long roomId, String message, long replyToMessageId) {
        ProtoChannelUpdateDraft.ChannelUpdateDraft.Builder builder = ProtoChannelUpdateDraft.ChannelUpdateDraft.newBuilder();

        ProtoGlobal.RoomDraft.Builder draft = ProtoGlobal.RoomDraft.newBuilder();
        draft.setMessage(message);
        draft.setReplyTo(replyToMessageId);

        builder.setRoomId(roomId);
        builder.setDraft(draft);

        RequestWrapper requestWrapper = new RequestWrapper(415, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
