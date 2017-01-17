package com.iGap.response;

import com.iGap.G;
import com.iGap.helper.HelperAvatar;
import com.iGap.proto.ProtoChannelAvatarDelete;

public class ChannelAvatarDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAvatarDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelAvatarDelete.ChannelAvatarDeleteResponse.Builder builder = (ProtoChannelAvatarDelete.ChannelAvatarDeleteResponse.Builder) message;
        if (G.onChannelAvatarDelete != null) {
            G.onChannelAvatarDelete.onChannelAvatarDelete(builder.getRoomId(), builder.getId());
        } else {
            HelperAvatar.avatarDelete(builder.getRoomId(), builder.getId(), HelperAvatar.AvatarType.ROOM, null);
        }
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


