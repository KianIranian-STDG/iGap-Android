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

import net.iGap.proto.ProtoChatUpdateStatus;
import net.iGap.proto.ProtoGlobal;

public class RequestChatUpdateStatus {

    public void updateStatus(long roomId, long messageId, ProtoGlobal.RoomMessageStatus roomMessageStatus) {
        ProtoChatUpdateStatus.ChatUpdateStatus.Builder chatUpdateStatus = ProtoChatUpdateStatus.ChatUpdateStatus.newBuilder();
        chatUpdateStatus.setRoomId(roomId);
        chatUpdateStatus.setMessageId(messageId);
        chatUpdateStatus.setStatus(roomMessageStatus);

        RequestWrapper requestWrapper = new RequestWrapper(202, chatUpdateStatus);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}