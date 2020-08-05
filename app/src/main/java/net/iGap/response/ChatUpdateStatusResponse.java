/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.response;

import net.iGap.helper.HelperNotification;
import net.iGap.helper.HelperUpdateMessageStatue;
import net.iGap.proto.ProtoChatUpdateStatus;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;

public class ChatUpdateStatusResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatUpdateStatusResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoChatUpdateStatus.ChatUpdateStatusResponse.Builder builder = (ProtoChatUpdateStatus.ChatUpdateStatusResponse.Builder) message;
        ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(builder.getResponse());

        HelperUpdateMessageStatue.updateStatus(builder.getRoomId(), builder.getMessageId(), builder.getUpdaterAuthorHash(), builder.getStatus(), builder.getStatusVersion(), response);

        if (builder.getStatus() == ProtoGlobal.RoomMessageStatus.SEEN) {
            HelperNotification.getInstance().cancelNotification(builder.getRoomId());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


