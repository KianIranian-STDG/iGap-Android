package com.iGap.request;

import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoUserPrivacySetRule;

public class RequestUserPrivacySetRule {

    public void userPrivacySetRule(ProtoGlobal.PrivacyType privacyType, ProtoGlobal.PrivacyLevel privacyLevel) {
        ProtoUserPrivacySetRule.UserPrivacySetRule.Builder builder = ProtoUserPrivacySetRule.UserPrivacySetRule.newBuilder();
        builder.setType(privacyType);
        builder.setLevel(privacyLevel);

        RequestWrapper requestWrapper = new RequestWrapper(144, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
