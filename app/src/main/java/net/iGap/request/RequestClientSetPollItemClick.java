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

import net.iGap.proto.ProtoClientSetPollItemClick;

public class RequestClientSetPollItemClick {

    public interface OnSetPollItemClick {
        void onSet();

        void onError(int major, int minor);
    }

    public void setPollClicked(int itemId, OnSetPollItemClick onSetPollItemClick) {
        ProtoClientSetPollItemClick.ClientSetPollItemClick.Builder builder = ProtoClientSetPollItemClick.ClientSetPollItemClick.newBuilder();
        builder.setItemId(itemId);
        RequestWrapper requestWrapper = new RequestWrapper(625, builder, onSetPollItemClick);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
