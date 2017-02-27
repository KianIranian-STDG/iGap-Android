package com.iGap.request;

import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoUserPrivacyGetRule;

public class RequestUserPrivacyGetRule {

    public void userPrivacyGetRule(ProtoGlobal.PrivacyType privacyType) {
        ProtoUserPrivacyGetRule.UserPrivacyGetRule.Builder builder = ProtoUserPrivacyGetRule.UserPrivacyGetRule.newBuilder();
        builder.setType(privacyType);

        RequestWrapper requestWrapper = new RequestWrapper(143, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
