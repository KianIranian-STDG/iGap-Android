package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoClientCheckInviteLink;
import com.iGap.proto.ProtoError;

public class ClientCheckInviteLinkResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientCheckInviteLinkResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoClientCheckInviteLink.ClientCheckInviteLinkResponse.Builder builder = (ProtoClientCheckInviteLink.ClientCheckInviteLinkResponse.Builder) message;
        if (G.onClientCheckInviteLink != null) {
            G.onClientCheckInviteLink.onClientCheckInviteLinkResponse(builder.getRoom());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (G.onClientCheckInviteLink != null) {
            G.onClientCheckInviteLink.onError(majorCode, minorCode);
        }
    }
}


