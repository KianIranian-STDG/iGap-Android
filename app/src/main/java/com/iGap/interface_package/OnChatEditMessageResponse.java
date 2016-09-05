package com.iGap.interface_package;

import com.iGap.proto.ProtoResponse;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/4/2016.
 */
public interface OnChatEditMessageResponse {
    void onChatEditMessage(long roomId, long messageId, int messageVersion, String message, ProtoResponse.Response response);
}
