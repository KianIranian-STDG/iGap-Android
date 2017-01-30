package com.iGap.module;

import com.iGap.interfaces.OnChatUpdateStatusResponse;
import com.iGap.proto.ProtoGlobal;
import com.iGap.request.RequestChatUpdateStatus;
import com.iGap.request.RequestGroupUpdateStatus;

/**
 * util for chat send messages
 * useful for having callback from different activities
 */
public class ChatUpdateStatusUtil implements OnChatUpdateStatusResponse {
    private OnChatUpdateStatusResponse onChatUpdateStatusResponse;

    public void setOnChatUpdateStatusResponse(OnChatUpdateStatusResponse response) {
        this.onChatUpdateStatusResponse = response;
    }

    public void sendUpdateStatus(ProtoGlobal.Room.Type roomType, long roomId, long messageId, ProtoGlobal.RoomMessageStatus roomMessageStatus) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            new RequestChatUpdateStatus().updateStatus(roomId, messageId, roomMessageStatus);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            new RequestGroupUpdateStatus().groupUpdateStatus(roomId, messageId, roomMessageStatus);
        }
    }

    @Override
    public void onChatUpdateStatus(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, long statusVersion) {
        if (onChatUpdateStatusResponse != null) {
            onChatUpdateStatusResponse.onChatUpdateStatus(roomId, messageId, status, statusVersion);
        }
    }
}
