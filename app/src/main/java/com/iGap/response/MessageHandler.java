package com.iGap.response;

import android.support.annotation.CallSuper;
import android.util.Log;
import com.iGap.Config;
import com.iGap.G;
import com.iGap.WebSocketClient;
import com.iGap.helper.HelperError;
import com.iGap.proto.ProtoError;

public abstract class MessageHandler {

    public Object message;
    int actionId;
    String identity;

    public MessageHandler(int actionId, Object protoClass, String identity) {
        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @CallSuper
    public void handler() throws NullPointerException {
        Log.i("MSGH", "MessageHandler handler : " + actionId + " || " + message);
    }

    @CallSuper
    public void timeOut() {
        if (heartBeatTimeOut()) {
            WebSocketClient.checkConnection();
        }
        Log.i("MSGT", "MessageHandler timeOut : " + actionId + " || " + message);
        error();
    }

    @CallSuper
    public void error() {

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        HelperError.showSnackMessage(HelperError.getErrorFromCode(majorCode, minorCode));


        Log.i("MSGE", "MessageHandler error : " + actionId + " || " + message);
    }

    private boolean heartBeatTimeOut() {

        long difference;

        long currentTime = System.currentTimeMillis();
        difference = (currentTime - G.latestHearBeatTime);

        if (difference >= Config.HEART_BEAT_CHECKING_TIME_OUT) {
            return true;
        }

        return false;
    }
}
