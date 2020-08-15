/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.response;

import net.iGap.controllers.MessageDataStorage;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.proto.ProtoChannelDeleteMessage;

public class ChannelDeleteMessageResponse extends MessageHandler {

    public ChannelDeleteMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoChannelDeleteMessage.ChannelDeleteMessageResponse.Builder builder = (ProtoChannelDeleteMessage.ChannelDeleteMessageResponse.Builder) message;

        long roomId = builder.getRoomId();
        long messageId = builder.getMessageId();
        long deleteVersion = builder.getDeleteVersion();
        boolean update = builder.getResponse().getId().isEmpty();

        MessageDataStorage.getInstance(AccountManager.selectedAccount).processDeleteMessage(roomId, messageId, deleteVersion, update);
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


