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
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.eventbus.socketMessages;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoWalletGetAccessToken;

import ir.radsense.raadcore.model.Auth;

public class WalletGetAccessTokenResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public WalletGetAccessTokenResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoWalletGetAccessToken.WalletGetAccessTokenResponse.Builder builder = (ProtoWalletGetAccessToken.WalletGetAccessTokenResponse.Builder) message;
        Auth auth = new Auth(builder.getAccessToken(), "bearer", null);
        if (auth.getJWT() == null) {
            return;
        }

        auth.save();
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postNotificationName(EventManager.ON_ACCESS_TOKEN_RECIVE, socketMessages.SUCCESS));
            }
        }, 1000);
    }

    @Override
    public void timeOut() {
        super.timeOut();
        G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postNotificationName(EventManager.ON_ACCESS_TOKEN_RECIVE, socketMessages.FAILED));
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postNotificationName(EventManager.ON_ACCESS_TOKEN_RECIVE, socketMessages.FAILED));
    }
}


