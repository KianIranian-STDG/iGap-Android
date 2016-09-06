package com.iGap.module;

import com.iGap.interface_package.OnChatUpdateStatusResponse;
import com.iGap.proto.ProtoGlobal;
import com.iGap.request.RequestChatUpdateStatus;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/5/2016.
 */

/**
 * util for chat send messages
 * useful for having callback from different activities
 */
public class ChatUpdateStatusUtil implements OnChatUpdateStatusResponse {
    private OnChatUpdateStatusResponse onChatUpdateStatusResponse;

    public void setOnChatUpdateStatusResponse(OnChatUpdateStatusResponse response) {
        this.onChatUpdateStatusResponse = response;
    }

    public void sendUpdateStatus(long roomId, long messageId, ProtoGlobal.RoomMessageStatus roomMessageStatus) {
        new RequestChatUpdateStatus().updateStatus(roomId, messageId, roomMessageStatus);
    }

    @Override
    public void onChatUpdateStatus(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, int statusVersion) {
        if (onChatUpdateStatusResponse != null) {
            onChatUpdateStatusResponse.onChatUpdateStatus(roomId, messageId, status, statusVersion);
        }
    }
}
