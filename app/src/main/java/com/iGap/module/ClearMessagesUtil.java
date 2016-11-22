package com.iGap.module;

import com.iGap.interfaces.OnChatClearMessageResponse;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.request.RequestChatClearMessage;
import com.iGap.request.RequestGroupClearMessage;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/5/2016.
 */

/**
 * util for chat clear messages
 * useful for having callback from different activities
 */
public class ClearMessagesUtil implements OnChatClearMessageResponse {
    private OnChatClearMessageResponse mOnChatClearMessageResponse;

    public void setOnChatClearMessageResponse(OnChatClearMessageResponse response) {
        this.mOnChatClearMessageResponse = response;
    }

    public void clearMessages(ProtoGlobal.Room.Type roomType, long roomId, long lastMessageId) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            new RequestChatClearMessage().chatClearMessage(roomId, lastMessageId);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            new RequestGroupClearMessage().groupClearMessage(roomId, lastMessageId);
        }
    }

    @Override
    public void onChatClearMessage(long roomId, long clearId, ProtoResponse.Response response) {
        if (mOnChatClearMessageResponse != null) {
            mOnChatClearMessageResponse.onChatClearMessage(roomId, clearId, response);
        }
    }
}
