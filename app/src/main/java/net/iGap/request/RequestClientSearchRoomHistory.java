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

import net.iGap.proto.ProtoClientSearchRoomHistory;

public class RequestClientSearchRoomHistory {

    public void clientSearchRoomHistory(long roomId, long messageId, long documentId, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter filter) {

        ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Builder builder = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.newBuilder();
        builder.setRoomId(roomId);
        builder.setFilter(filter);
        builder.setOffsetMessageId(messageId);
        builder.setOffsetDocumentId(documentId);

        RequestWrapper requestWrapper = new RequestWrapper(605, builder, filter);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

