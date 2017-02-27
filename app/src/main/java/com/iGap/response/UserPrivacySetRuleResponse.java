package com.iGap.response;

import com.iGap.proto.ProtoUserPrivacySetRule;

public class UserPrivacySetRuleResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserPrivacySetRuleResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserPrivacySetRule.UserPrivacySetRuleResponse.Builder builder = (ProtoUserPrivacySetRule.UserPrivacySetRuleResponse.Builder) message;
        builder.getType();
        builder.getLevel();
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


