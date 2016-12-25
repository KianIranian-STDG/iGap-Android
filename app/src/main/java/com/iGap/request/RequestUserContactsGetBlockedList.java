
package com.iGap.request;

import com.iGap.proto.ProtoUserContactsGetBlockedList;

public class RequestUserContactsGetBlockedList {

    public void userContactsGetBlockedList() {
        ProtoUserContactsGetBlockedList.UserContactsGetBlockedList.Builder builder = ProtoUserContactsGetBlockedList.UserContactsGetBlockedList.newBuilder();

        RequestWrapper requestWrapper = new RequestWrapper(130, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
