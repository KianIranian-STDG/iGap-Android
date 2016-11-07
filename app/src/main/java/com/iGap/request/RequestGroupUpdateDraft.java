package com.iGap.request;

import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupUpdateDraft;

public class RequestGroupUpdateDraft {

    public void groupUpdateDraft(long roomId, String message, long replyToMessageId) {

        ProtoGroupUpdateDraft.GroupUpdateDraft.Builder builder =
                ProtoGroupUpdateDraft.GroupUpdateDraft.newBuilder();

        ProtoGlobal.RoomDraft.Builder roomDraft = ProtoGlobal.RoomDraft.newBuilder();
        roomDraft.setMessage(message);
        roomDraft.setReplyTo(replyToMessageId);

        builder.setRoomId(roomId);
        builder.setDraft(roomDraft);

        RequestWrapper requestWrapper = new RequestWrapper(315, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

