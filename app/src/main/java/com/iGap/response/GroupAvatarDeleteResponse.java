package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGroupAvatarDelete;

public class GroupAvatarDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupAvatarDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoGroupAvatarDelete.GroupAvatarDeleteResponse.Builder groupAvatarDelete = (ProtoGroupAvatarDelete.GroupAvatarDeleteResponse.Builder) message;
        if (G.onGroupAvatarDelete != null) {
            G.onGroupAvatarDelete.onDeleteAvatar(groupAvatarDelete.getRoomId(), groupAvatarDelete.getId());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onGroupAvatarDelete != null) {
            G.onGroupAvatarDelete.onTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();


        if (G.onGroupAvatarDelete != null) {
            G.onGroupAvatarDelete.onDeleteAvatarError(majorCode, minorCode);
        }
    }
}


