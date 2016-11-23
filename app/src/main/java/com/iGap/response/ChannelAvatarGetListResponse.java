package com.iGap.response;

import com.iGap.proto.ProtoChannelAvatarGetList;

public class ChannelAvatarGetListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAvatarGetListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelAvatarGetList.ChannelAvatarGetListResponse.Builder builder = (ProtoChannelAvatarGetList.ChannelAvatarGetListResponse.Builder) message;
        builder.getAvatarList();
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


