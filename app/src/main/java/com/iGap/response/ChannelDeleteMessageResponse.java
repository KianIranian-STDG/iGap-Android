package com.iGap.response;

import com.iGap.helper.HelperDeleteMessage;
import com.iGap.proto.ProtoChannelDeleteMessage;

public class ChannelDeleteMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelDeleteMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoChannelDeleteMessage.ChannelDeleteMessageResponse.Builder builder = (ProtoChannelDeleteMessage.ChannelDeleteMessageResponse.Builder) message;
        HelperDeleteMessage.deleteMessage(builder.getRoomId(), builder.getMessageId(), builder.getDeleteVersion(), builder.getResponse());

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


