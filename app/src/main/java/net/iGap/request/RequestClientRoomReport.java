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

import androidx.annotation.Nullable;

import net.iGap.proto.ProtoClientRoomReport;

public class RequestClientRoomReport {

    public void roomReport(long roomId, long messageId, long documentId, ProtoClientRoomReport.ClientRoomReport.Reason reason, @Nullable String description) {
        ProtoClientRoomReport.ClientRoomReport.Builder builder = ProtoClientRoomReport.ClientRoomReport.newBuilder();
        builder.setRoomId(roomId);
        builder.setMessageId(messageId);
        builder.setDocumentId(documentId);
        builder.setReason(reason);
        if (reason == ProtoClientRoomReport.ClientRoomReport.Reason.OTHER) {
            builder.setDescription(description);
        }

        RequestWrapper requestWrapper = new RequestWrapper(616, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
