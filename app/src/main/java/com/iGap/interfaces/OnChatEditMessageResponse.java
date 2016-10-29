package com.iGap.interfaces;

import com.iGap.proto.ProtoResponse;

public interface OnChatEditMessageResponse {
    void onChatEditMessage(long roomId, long messageId, long messageVersion, String message, ProtoResponse.Response response);
}
