package com.iGap.interface_package;

import com.iGap.proto.ProtoResponse;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/4/2016.
 */
public interface OnChatClearMessageResponse {
    void onChatClearMessage(long roomId, long clearId, ProtoResponse.Response response);
}
