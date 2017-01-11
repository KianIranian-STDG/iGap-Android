package com.iGap.response;

import android.support.annotation.CallSuper;
import android.util.Log;
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
        //WebSocketClient.checkConnection();
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
}
