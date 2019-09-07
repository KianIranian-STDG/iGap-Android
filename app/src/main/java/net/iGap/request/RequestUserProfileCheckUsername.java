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

import net.iGap.interfaces.OnUserProfileCheckUsername;
import net.iGap.proto.ProtoUserProfileCheckUsername;

public class RequestUserProfileCheckUsername {

    public void userProfileCheckUsername(String username, OnUserProfileCheckUsername callback) {
        ProtoUserProfileCheckUsername.UserProfileCheckUsername.Builder builder = ProtoUserProfileCheckUsername.UserProfileCheckUsername.newBuilder();
        builder.setUsername(username);

        RequestWrapper requestWrapper = new RequestWrapper(122, builder, callback);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

