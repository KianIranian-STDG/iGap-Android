package com.iGap.module;

import com.iGap.interfaces.OnChatClearMessageResponse;
import com.iGap.proto.ProtoResponse;
import com.iGap.request.RequestChatClearMessage;

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

    public void clearMessages(long roomId, long lastMessageId) {
        new RequestChatClearMessage().chatClearMessage(roomId, lastMessageId);
    }

    @Override
    public void onChatClearMessage(long roomId, long clearId, ProtoResponse.Response response) {
        if (mOnChatClearMessageResponse != null) {
            mOnChatClearMessageResponse.onChatClearMessage(roomId, clearId, response);
        }
    }
}
