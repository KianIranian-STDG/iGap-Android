/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.request;

import net.iGap.proto.ProtoChannelSendMessage;
import net.iGap.proto.ProtoGlobal;
import net.iGap.structs.AdditionalObject;

public class RequestChannelSendMessage {
    private ProtoChannelSendMessage.ChannelSendMessage.Builder channelSendMessage;

    public RequestChannelSendMessage newBuilder(ProtoGlobal.RoomMessageType messageType, long roomId) {
        channelSendMessage = ProtoChannelSendMessage.ChannelSendMessage.newBuilder();
        channelSendMessage.setMessageType(messageType);
        channelSendMessage.setRoomId(roomId);
        return this;
    }

    public RequestChannelSendMessage message(String value) {
        channelSendMessage.setMessage(value);
        return this;
    }

    public RequestChannelSendMessage attachment(String value) {
        channelSendMessage.setAttachment(value);
        return this;
    }

    public RequestChannelSendMessage location(ProtoGlobal.RoomMessageLocation value) {
        channelSendMessage.setLocation(value);
        return this;
    }

    public RequestChannelSendMessage contact(ProtoGlobal.RoomMessageContact value) {
        channelSendMessage.setContact(value);
        return this;
    }

    public RequestChannelSendMessage forwardMessage(ProtoGlobal.RoomMessageForwardFrom forwardFrom) {
        channelSendMessage.setForwardFrom(forwardFrom);
        return this;
    }

    public RequestChannelSendMessage replyMessage(long messageId) {
        channelSendMessage.setReplyTo(messageId);
        return this;
    }
    public RequestChannelSendMessage additionalData(AdditionalObject additionalObject) {
        channelSendMessage.setAdditionalData(additionalObject.data);
        channelSendMessage.setAdditionalType(additionalObject.type);
        return this;
    }

    public RequestChannelSendMessage sendMessage(String fakeMessageIdAsIdentity) {
        channelSendMessage.setRandomId(Long.parseLong(fakeMessageIdAsIdentity));
        RequestWrapper requestWrapper = new RequestWrapper(410, channelSendMessage, fakeMessageIdAsIdentity);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }
}

