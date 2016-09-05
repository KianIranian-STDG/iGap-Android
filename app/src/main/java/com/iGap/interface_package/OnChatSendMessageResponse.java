package com.iGap.interface_package;

import com.iGap.proto.ProtoChatSendMessage;
import com.iGap.proto.ProtoGlobal;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/5/2016.
 */
public interface OnChatSendMessageResponse {
    // message updated after send request sent
    void onMessageUpdated(long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoChatSendMessage.ChatSendMessageResponse.Builder roomMessage);

    void onReceiveChatMessage(String message, String messageType, ProtoChatSendMessage.ChatSendMessageResponse.Builder roomMessage);
}
