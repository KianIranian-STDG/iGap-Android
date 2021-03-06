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

import net.iGap.observers.interfaces.OnChannelEdit;
import net.iGap.proto.ProtoChannelEdit;

public class RequestChannelEdit {

    public void channelEdit(long roomId, String name, String description, OnChannelEdit callback) {

        ProtoChannelEdit.ChannelEdit.Builder builder = ProtoChannelEdit.ChannelEdit.newBuilder();
        builder.setRoomId(roomId);
        builder.setName(name);
        builder.setDescription(description.trim());

        RequestWrapper requestWrapper = new RequestWrapper(405, builder, callback);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
