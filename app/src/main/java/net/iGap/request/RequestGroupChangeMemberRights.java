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

import net.iGap.observers.interfaces.OnResponse;
import net.iGap.proto.ProtoGroupChangeMemberRights;

public class RequestGroupChangeMemberRights {

    public void groupChangeMemberRights(long roomId, long memberId, ProtoGroupChangeMemberRights.GroupChangeMemberRights.MemberRights memberRights, OnResponse onResponse) {

        ProtoGroupChangeMemberRights.GroupChangeMemberRights.Builder builder = ProtoGroupChangeMemberRights.GroupChangeMemberRights.newBuilder();
        builder.setRoomId(roomId);
        builder.setUserId(memberId);
        builder.setPermission(memberRights);

        RequestWrapper requestWrapper = new RequestWrapper(327, builder, onResponse);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void groupChangeRights(long roomId, ProtoGroupChangeMemberRights.GroupChangeMemberRights.MemberRights memberRights, OnResponse onResponse) {

        ProtoGroupChangeMemberRights.GroupChangeMemberRights.Builder builder = ProtoGroupChangeMemberRights.GroupChangeMemberRights.newBuilder();
        builder.setRoomId(roomId);
        builder.setPermission(memberRights);

        RequestWrapper requestWrapper = new RequestWrapper(327, builder, onResponse);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

