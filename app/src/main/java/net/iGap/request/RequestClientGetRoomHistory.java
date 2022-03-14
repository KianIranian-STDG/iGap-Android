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

import net.iGap.proto.ProtoClientGetRoomHistory;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

public class RequestClientGetRoomHistory {

    public String getRoomHistory(long roomId, long documentId, long firstMessageId, int limit, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction, Object identity) {

        ProtoClientGetRoomHistory.ClientGetRoomHistory.Builder builder = ProtoClientGetRoomHistory.ClientGetRoomHistory.newBuilder();
        builder.setRoomId(roomId);
        builder.setFirstMessageId(firstMessageId);
        builder.setDirection(direction);
        builder.setDocumentId(documentId);
        builder.setLimit(limit);

        RequestWrapper requestWrapper = new RequestWrapper(603, builder, identity);
        try {
            return RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getRoomHistory(long roomId, long documentId, long firstMessageId, int limit, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction, OnHistoryReady historyReady) {
        RequestData requestData = new RequestData(historyReady, roomId, firstMessageId, limit);
        ProtoClientGetRoomHistory.ClientGetRoomHistory.Builder builder = ProtoClientGetRoomHistory.ClientGetRoomHistory.newBuilder();
        builder.setRoomId(roomId);
        builder.setDocumentId(documentId);
        builder.setFirstMessageId(firstMessageId);
        builder.setDirection(direction);
        builder.setLimit(limit);

        RequestWrapper requestWrapper = new RequestWrapper(603, builder, requestData);
        try {
            return RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return "";
        }
    }

    public class RequestData {
        public OnHistoryReady onHistoryReady;
        public long roomId;
        public long limit;
        public long messageIdGetHistory;

        public RequestData(OnHistoryReady onHistoryReady, long roomId, long messageIdGetHistory, long limit) {
            this.onHistoryReady = onHistoryReady;
            this.roomId = roomId;
            this.messageIdGetHistory = messageIdGetHistory;
            this.limit = limit;

        }
    }

    public interface OnHistoryReady {
        void onHistory(List<ProtoGlobal.RoomMessage> messageList);

        void onErrorHistory(int major, int minor);
    }

    public static class IdentityClientGetRoomHistory {
        public long roomId;
        public long messageIdGetHistory;
        public long documentIdGetHistory;
        public long reachMessageId;
        public ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction;

        public IdentityClientGetRoomHistory(long roomId, long documentIdGetHistory, long messageIdGetHistory, long reachMessageId, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
            this.roomId = roomId;
            this.messageIdGetHistory = messageIdGetHistory;
            this.documentIdGetHistory = documentIdGetHistory;
            this.reachMessageId = reachMessageId;
            this.direction = direction;
        }
    }
}
