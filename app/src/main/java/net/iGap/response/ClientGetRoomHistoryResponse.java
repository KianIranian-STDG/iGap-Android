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

import net.iGap.G;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.structs.StructMessageOption;
import net.iGap.proto.ProtoClientGetRoomHistory;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestClientGetRoomHistory;

public class ClientGetRoomHistoryResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public ClientGetRoomHistoryResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        if (identity instanceof RequestClientGetRoomHistory.IdentityClientGetRoomHistory) {
            RequestClientGetRoomHistory.IdentityClientGetRoomHistory identityParams = ((RequestClientGetRoomHistory.IdentityClientGetRoomHistory) identity);
            final long roomId = identityParams.roomId;
            final long reachMessageId = identityParams.reachMessageId;
            final long messageIdGetHistory = identityParams.messageIdGetHistory;
            final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction = identityParams.direction;

            final ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse.Builder builder = (ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse.Builder) message;
            DbManager.getInstance().doRealmTransaction(realm -> {
                for (ProtoGlobal.RoomMessage roomMessage : builder.getMessageList()) {
                    if (roomMessage.getAuthor().hasUser()) {
                        RealmRegisteredInfo.needUpdateUser(roomMessage.getAuthor().getUser().getUserId(), roomMessage.getAuthor().getUser().getCacheId());
                    }
                    RealmRoomMessage.putOrUpdate(realm, roomId, roomMessage, new StructMessageOption().setGap());
                }
            });
            G.onClientGetRoomHistoryResponse.onGetRoomHistory(roomId, builder.getMessageList().get(0).getMessageId(), builder.getMessageList().get(0).getDocumentId(), builder.getMessageList().get(builder.getMessageCount() - 1).getMessageId(), builder.getMessageList().get(builder.getMessageCount() - 1).getDocumentId(), reachMessageId, messageIdGetHistory, direction);

        } else {
            RequestClientGetRoomHistory.RequestData requestData = (RequestClientGetRoomHistory.RequestData) identity;
            final ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse.Builder builder = (ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse.Builder) message;
            requestData.onHistoryReady.onHistory(builder.getMessageList());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        if (identity instanceof RequestClientGetRoomHistory.IdentityClientGetRoomHistory) {
            RequestClientGetRoomHistory.IdentityClientGetRoomHistory identityParams = ((RequestClientGetRoomHistory.IdentityClientGetRoomHistory) identity);
            ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
            if (G.onClientGetRoomHistoryResponse != null) {
                G.onClientGetRoomHistoryResponse.onGetRoomHistoryError(errorResponse.getMajorCode(), errorResponse.getMinorCode(), identityParams.messageIdGetHistory, identityParams.documentIdGetHistory, identityParams.direction);
            }
        } else {
            RequestClientGetRoomHistory.RequestData requestData = (RequestClientGetRoomHistory.RequestData) identity;
            ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
            requestData.onHistoryReady.onErrorHistory(errorResponse.getMajorCode(), errorResponse.getMinorCode());
        }
    }
}


