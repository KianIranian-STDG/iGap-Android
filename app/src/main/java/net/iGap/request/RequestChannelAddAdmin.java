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

import net.iGap.observers.interfaces.RequestDelegate;
import net.iGap.proto.ProtoChannelAddAdmin;

public class RequestChannelAddAdmin {

    public void channelAddAdmin(long roomId, long memberId) {
        ProtoChannelAddAdmin.ChannelAddAdmin.Builder builder = ProtoChannelAddAdmin.ChannelAddAdmin.newBuilder();
        builder.setRoomId(roomId);
        builder.setMemberId(memberId);

        RequestWrapper requestWrapper = new RequestWrapper(402, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void channelAddAdmin(long roomId, long memberId, ProtoChannelAddAdmin.ChannelAddAdmin.AdminRights adminRights, RequestDelegate onResponse) {
        ProtoChannelAddAdmin.ChannelAddAdmin.Builder builder = ProtoChannelAddAdmin.ChannelAddAdmin.newBuilder();
        builder.setRoomId(roomId);
        builder.setMemberId(memberId);
        builder.setPermission(adminRights);

        RequestWrapper requestWrapper = new RequestWrapper(402, builder, onResponse);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
