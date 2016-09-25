package com.iGap.interface_package;

import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupSendMessage;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/5/2016.
 */
public interface OnGroupChatSendMessageResponse {
    // message updated after send request sent
    void onMessageUpdated(long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoGroupSendMessage.GroupSendMessageResponse.Builder roomMessage);

    void onReceiveChatMessage(String message, String messageType, ProtoGroupSendMessage.GroupSendMessageResponse.Builder roomMessage);
}
