package com.iGap.response;

import com.iGap.proto.ProtoUserTwoStepVerificationUnsetPassword;

public class UserTwoStepVerificationUnsetPasswordResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserTwoStepVerificationUnsetPasswordResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserTwoStepVerificationUnsetPassword.UserTwoStepVerificationUnsetPasswordResponse.Builder builder = (ProtoUserTwoStepVerificationUnsetPassword.UserTwoStepVerificationUnsetPasswordResponse.Builder) message;
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


