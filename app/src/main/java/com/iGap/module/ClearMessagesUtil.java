/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/


package com.iGap.module;

import com.iGap.interfaces.OnChatClearMessageResponse;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.request.RequestChatClearMessage;
import com.iGap.request.RequestGroupClearMessage;

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
