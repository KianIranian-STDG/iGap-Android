package com.iGap.interface_package;

import com.iGap.proto.ProtoGlobal;

public interface OnChatUpdateStatusResponse {
    void onChatUpdateStatus(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, long statusVersion);
}
