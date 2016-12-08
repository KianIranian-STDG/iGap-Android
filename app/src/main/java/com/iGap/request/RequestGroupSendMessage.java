package com.iGap.request;

import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupSendMessage;

public class RequestGroupSendMessage { //TODO [Saeed Mozaffari] [2016-08-29 1:02 PM] - change builder to force use newBuilder
    ProtoGroupSendMessage.GroupSendMessage.Builder groupSendMessage;

    public RequestGroupSendMessage newBuilder(ProtoGlobal.RoomMessageType messageType,
                                              long roomId) {
        groupSendMessage = ProtoGroupSendMessage.GroupSendMessage.newBuilder();
        groupSendMessage.setMessageType(messageType);
        groupSendMessage.setRoomId(roomId);
        return this;
    }

    public RequestGroupSendMessage message(String value) {
        groupSendMessage.setMessage(value);
        return this;
    }

    public RequestGroupSendMessage attachment(String value) {
        groupSendMessage.setAttachment(value);
        return this;
    }

    public RequestGroupSendMessage location(ProtoGlobal.RoomMessageLocation value) {
        groupSendMessage.setLocation(value);
        return this;
    }

    public RequestGroupSendMessage contact(ProtoGlobal.RoomMessageContact value) {
        groupSendMessage.setContact(value);
        return this;
    }

    public RequestGroupSendMessage forwardMessage(ProtoGlobal.RoomMessageForwardFrom forwardFrom) {
        groupSendMessage.setForwardFrom(forwardFrom);
        return this;
    }

    public RequestGroupSendMessage replyMessage(long messageId) {
        groupSendMessage.setReplyTo(messageId);
        return this;
    }

    public RequestGroupSendMessage sendMessage(String fakeMessageIdAsIdentity) {
        RequestWrapper requestWrapper =
                new RequestWrapper(310, groupSendMessage, fakeMessageIdAsIdentity);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }
}

