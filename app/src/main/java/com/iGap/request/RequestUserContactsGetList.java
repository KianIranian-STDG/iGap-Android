package com.iGap.request;

import android.util.Log;
import com.iGap.proto.ProtoUserContactsGetList;

public class RequestUserContactsGetList {

    public void userContactGetList() {

        ProtoUserContactsGetList.UserContactsGetList.Builder builder =
            ProtoUserContactsGetList.UserContactsGetList.newBuilder();
        Log.i("WWW", "RequestUserContactsGetList");
        RequestWrapper requestWrapper = new RequestWrapper(107, builder);

        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}