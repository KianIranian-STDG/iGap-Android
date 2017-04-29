/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.request;

import com.iGap.proto.ProtoUserSessionGetActiveList;

public class RequestUserSessionGetActiveList {

    public void userSessionGetActiveList() {
        ProtoUserSessionGetActiveList.UserSessionGetActiveList.Builder chatClearMessage = ProtoUserSessionGetActiveList.UserSessionGetActiveList.newBuilder();
        RequestWrapper requestWrapper = new RequestWrapper(30125, chatClearMessage);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

