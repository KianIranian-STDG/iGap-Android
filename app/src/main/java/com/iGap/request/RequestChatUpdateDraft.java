package com.iGap.request;

import com.iGap.proto.ProtoChatUpdateDraft;
import com.iGap.proto.ProtoGlobal;

public class RequestChatUpdateDraft {

    public void chatUpdateDraft(long roomId, String message, long replyToMessageId) {

        ProtoChatUpdateDraft.ChatUpdateDraft.Builder builder =
            ProtoChatUpdateDraft.ChatUpdateDraft.newBuilder();

        ProtoGlobal.RoomDraft.Builder roomDraft = ProtoGlobal.RoomDraft.newBuilder();
        roomDraft.setMessage(message);
        roomDraft.setReplyTo(replyToMessageId);

        builder.setRoomId(roomId);
        builder.setDraft(roomDraft);

        RequestWrapper requestWrapper = new RequestWrapper(207, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

