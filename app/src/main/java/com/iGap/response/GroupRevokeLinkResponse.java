package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGroupRevokeLink;

public class GroupRevokeLinkResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupRevokeLinkResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoGroupRevokeLink.GroupRevokeLinkResponse.Builder builder = (ProtoGroupRevokeLink.GroupRevokeLinkResponse.Builder) message;
        if (G.onGroupRevokeLink != null) {
            G.onGroupRevokeLink.onGroupRevokeLink(builder.getRoomId(), builder.getInviteLink(), builder.getInviteToken());
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
        if (G.onGroupRevokeLink != null) {
            G.onGroupRevokeLink.onError(majorCode, minorCode);
        }
    }
}


