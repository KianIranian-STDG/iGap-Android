/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.request;

import net.iGap.proto.ProtoChatDeleteMessage;
// TODO: 1/9/21 Delete this method after completing deleting messages MESSAGE_REFACTOR

public class RequestChatDeleteMessage {

    public void chatDeleteMessage(long roomId, long messageId, boolean both) {

        ProtoChatDeleteMessage.ChatDeleteMessage.Builder chatDeleteMessage = ProtoChatDeleteMessage.ChatDeleteMessage.newBuilder();
        chatDeleteMessage.setRoomId(roomId);
        chatDeleteMessage.setMessageId(messageId);
        chatDeleteMessage.setBoth(both);

        RequestWrapper requestWrapper = new RequestWrapper(204, chatDeleteMessage);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

