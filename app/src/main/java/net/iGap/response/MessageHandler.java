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

import androidx.annotation.CallSuper;

import net.iGap.BuildConfig;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.WebSocketClient;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperLog;
import net.iGap.proto.ProtoError;

import static net.iGap.G.latestResponse;
import static net.iGap.helper.HelperTimeOut.heartBeatTimeOut;

public abstract class MessageHandler {

    public Object message;
    int actionId;
    Object identity;
    int majorCode = -1;
    int minorCode = -1;

    public MessageHandler(int actionId, Object protoClass, Object identity) {
        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @CallSuper
    public void handler() throws NullPointerException {
        if (BuildConfig.DEBUG) {
            FileLog.i("MSGH " + "MessageHandler handler : " + actionId + " || " + G.lookupMap.get(actionId) + " || " + message);
        }
        latestResponse = System.currentTimeMillis();
    }

    @CallSuper
    public void timeOut() {
        if (heartBeatTimeOut()) {
            if (BuildConfig.DEBUG) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(G.context, "MessageHandler HeartBeat TimeOut", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            WebSocketClient.getInstance().disconnectSocket(true);
        }
        error();
    }

    @CallSuper
    public void error() {

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        majorCode = errorResponse.getMajorCode();
        minorCode = errorResponse.getMinorCode();

        if (Config.FILE_LOG_ENABLE && actionId != 0) {
            FileLog.e("ERROR " + actionId + " MA " + majorCode + " MI " + minorCode);
        }

        HelperError.showSnackMessage(HelperError.getErrorFromCode(majorCode, minorCode), false);

        if (!G.ignoreErrorCodes.contains(majorCode)) {
            HelperLog.getInstance().setErrorLog(new Exception("majorCode : " + errorResponse.getMajorCode() + " * minorCode : " + errorResponse.getMinorCode() + " * " + G.lookupMap.get(actionId)));
        }


        if (BuildConfig.DEBUG) {
            FileLog.i("MSGE " + "MessageHandler error : " + actionId + " || " + G.lookupMap.get(actionId) + " || " + message);
        }
    }
}
