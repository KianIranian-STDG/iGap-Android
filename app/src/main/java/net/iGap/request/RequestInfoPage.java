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

import net.iGap.G;
import net.iGap.helper.HelperString;
import net.iGap.proto.ProtoInfoPage;
import net.iGap.proto.ProtoRequest;

public class RequestInfoPage {

    public boolean infoPage(String id) {
        ProtoInfoPage.InfoPage.Builder infoPage = ProtoInfoPage.InfoPage.newBuilder();
        infoPage.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        infoPage.setId(id);

        RequestWrapper requestWrapper = new RequestWrapper(503, infoPage, id);
        try {
            if (G.userLogin) {
                RequestQueue.sendRequest(requestWrapper);
                return true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }

    public interface OnInfoPage {
        void onInfo(String body);
        void onError(int major, int minor);
    }

    public boolean infoPageAgreementDiscovery(String id, OnInfoPage onInfoPage) {
        ProtoInfoPage.InfoPage.Builder infoPage = ProtoInfoPage.InfoPage.newBuilder();
        infoPage.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        infoPage.setId(id);

        RequestWrapper requestWrapper = new RequestWrapper(503, infoPage, onInfoPage);
        try {
            if (G.userLogin) {
                RequestQueue.sendRequest(requestWrapper);
                return true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }
}
