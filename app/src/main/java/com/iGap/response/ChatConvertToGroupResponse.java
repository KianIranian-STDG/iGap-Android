package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChatConvertToGroup;

public class ChatConvertToGroupResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatConvertToGroupResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        ProtoChatConvertToGroup.ChatConvertToGroupResponse.Builder builder = (ProtoChatConvertToGroup.ChatConvertToGroupResponse.Builder) message;
        G.onChatConvertToGroup.onChatConvertToGroup(builder.getRoomId(), builder.getName(), builder.getDescription(), builder.getRole());
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


