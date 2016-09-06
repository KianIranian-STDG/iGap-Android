package com.iGap.helper;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/6/2016.
 */

import com.iGap.module.MyType;
import com.iGap.module.StructMessageInfo;
import com.iGap.proto.ProtoChatSendMessage;
import com.iGap.realm.RealmUserInfo;

import io.realm.Realm;

/**
 * helper methods while working with proto builders
 * note: when any field of classes was changed, update this helper.
 */
public final class HelperProtoBuilder {
    private HelperProtoBuilder() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation.");
    }

    public static StructMessageInfo convert(ProtoChatSendMessage.ChatSendMessageResponse.Builder builder) {
        Realm realm = Realm.getDefaultInstance();
        long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        StructMessageInfo messageInfo = new StructMessageInfo();
        messageInfo.status = builder.getRoomMessage().getStatus().toString();
        messageInfo.messageID = Long.toString(builder.getRoomMessage().getMessageId());
        messageInfo.messageType = builder.getRoomMessage().getMessageType();
        messageInfo.messageText = builder.getRoomMessage().getMessage();
        messageInfo.senderID = Long.toString(builder.getRoomMessage().getUserId());
        if (builder.getRoomMessage().getUserId() == userId) {
            messageInfo.sendType = MyType.SendType.send;
        } else if (builder.getRoomMessage().getUserId() != userId) {
            messageInfo.sendType = MyType.SendType.recvive;
        }
        return messageInfo;
    }
}
