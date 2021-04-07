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

import net.iGap.Config;
import net.iGap.G;
import net.iGap.fragments.FragmentMain;
import net.iGap.helper.LooperThreadHelper;
import net.iGap.module.BotInit;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.network.RequestManager;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoClientGetRoomList;
import net.iGap.proto.ProtoError;
import net.iGap.realm.RealmClientCondition;
import net.iGap.request.RequestClientCondition;
import net.iGap.request.RequestClientGetRoomList;

import static net.iGap.G.clientConditionGlobal;
import static net.iGap.realm.RealmRoom.putChatToDatabase;
import static net.iGap.request.RequestClientGetRoomList.isPendingGetRoomList;

public class ClientGetRoomListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public RequestClientGetRoomList.IdentityGetRoomList identity;
    public static int retryCountZeroOffset = 0;
    public static boolean roomListFetched = false;

    public ClientGetRoomListResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = (RequestClientGetRoomList.IdentityGetRoomList) identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoClientGetRoomList.ClientGetRoomListResponse.Builder clientGetRoomListResponse = (ProtoClientGetRoomList.ClientGetRoomListResponse.Builder) message;

        boolean fromLogin = false;
        if (identity.isFromLogin) {
            FragmentMain.mOffset = 0;
            fromLogin = true;
        }

        if (FragmentMain.mOffset == 0) {
            BotInit.checkDrIgap();
        }

        putChatToDatabase(clientGetRoomListResponse.getRoomsList(), identity.offset, clientGetRoomListResponse.getRoomsCount());

        /**
         * to first enter to app , client first compute clientCondition then
         * getRoomList and finally send condition that before get clientCondition;
         * in else changeState compute new client condition with latest messaging changeState
         */
        if (!RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
            RequestManager.getInstance(AccountManager.selectedAccount).setUserLogin(true);
            sendClientCondition();
        } else if (fromLogin || FragmentMain.mOffset == 0) {
            if (G.clientConditionGlobal != null) {
                new RequestClientCondition().clientCondition(G.clientConditionGlobal);
            } else {
                new RequestClientCondition().clientCondition(RealmClientCondition.computeClientCondition(null));
            }
        }

        FragmentMain.mOffset += clientGetRoomListResponse.getRoomsCount();
        isPendingGetRoomList = false;
        if (clientGetRoomListResponse.getRoomsCount() == 0) {
            roomListFetched = true;
        }

        G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.ROOM_LIST_CHANGED, false));

        retryCountZeroOffset = 0;
    }


    private void sendClientCondition() {
        if (clientConditionGlobal != null) {
            new RequestClientCondition().clientCondition(clientConditionGlobal);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendClientCondition();
                }
            }, 1000);
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();

        if (G.onClientGetRoomListResponse != null)
            G.onClientGetRoomListResponse.onClientGetRoomListTimeout();
    }

    @Override
    public void error() {
        super.error();
        isPendingGetRoomList = false;
        if (retryCountZeroOffset < 10) {
            retryCountZeroOffset++;
            LooperThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new RequestClientGetRoomList().clientGetRoomList(identity.offset, Config.LIMIT_LOAD_ROOM, identity.offset + "");
                }
            }, retryCountZeroOffset * 300);
        }

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (G.onClientGetRoomListResponse != null)
            G.onClientGetRoomListResponse.onClientGetRoomListError(majorCode, minorCode);
    }
}


