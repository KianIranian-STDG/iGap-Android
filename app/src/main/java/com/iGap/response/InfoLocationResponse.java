package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoInfoLocation;

public class InfoLocationResponse extends MessageHandler {

    public int actionId;
    public Object message;

    public InfoLocationResponse(int actionId, Object protoClass) {
        super(actionId, protoClass);

        this.message = protoClass;
        this.actionId = actionId;
    }


    @Override
    public void handler() {

        ProtoInfoLocation.InfoLocationResponse.Builder infoLocationResponse = (ProtoInfoLocation.InfoLocationResponse.Builder) message;
//        infoLocationResponse.getIsoCode();//string
//        infoLocationResponse.getCallingCode();
//        infoLocationResponse.getName();
//        infoLocationResponse.getPattern();
//        infoLocationResponse.getRegex();

        Log.i("SOC_INFO", "iso code : " + infoLocationResponse.getIsoCode());
        Log.i("SOC_INFO", "iso code : " + infoLocationResponse.getCallingCode());
        Log.i("SOC_INFO", "iso code : " + infoLocationResponse.getName());
        Log.i("SOC_INFO", "iso code : " + infoLocationResponse.getPattern());
        Log.i("SOC_INFO", "iso code : " + infoLocationResponse.getRegex());

        G.onReceiveInfoLocation.onReceive(
                infoLocationResponse.getIsoCode()
                , infoLocationResponse.getCallingCode()
                , infoLocationResponse.getName()
                , infoLocationResponse.getPattern()
                , infoLocationResponse.getRegex());

    }

    @Override
    public void timeOut() {

    }

    @Override
    public void error() {
        Log.i("SOC_INFO", "InfoLocationResponse error");
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
//        errorResponse.getMajorCode();
//        errorResponse.getMinorCode();

        Log.i("SOC_INFO", "getMajorCode : " + errorResponse.getMajorCode());
        Log.i("SOC_INFO", "getMinorCode : " + errorResponse.getMinorCode());

        //G.onReceiveInfoLocation.onReceive("IR", 98, "Iran Test", "pattern", "Regex");
    }
}
