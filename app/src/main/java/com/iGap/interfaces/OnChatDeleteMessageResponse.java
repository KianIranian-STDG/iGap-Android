package com.iGap.interfaces;

import com.iGap.proto.ProtoResponse;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/4/2016.
 */
public interface OnChatDeleteMessageResponse {
    void onChatDeleteMessage(long deleteVersion, long messageId, long roomId,
        ProtoResponse.Response response);

    void onError(int majorCode, int minorCode);

}
