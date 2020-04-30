package net.iGap.viewmodel.controllers;

import net.iGap.module.webrtc.CallerInfo;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmCallConfig;

public class CallManager implements EventListener {

    private long callPeerId;
    private String currentSDP;
    private ProtoSignalingOffer.SignalingOffer.Type currentCallType;

    private RealmCallConfig currentCallConfig;

    private boolean callAlive;

    private CallerInfo currentCallerInfo;

    private static volatile CallManager instance = null;

    public static CallManager getInstance() {
        CallManager localInstance = instance;
        if (localInstance == null) {
            synchronized (CallManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new CallManager();
                }
            }
        }
        return localInstance;
    }

    public void incomingCall(ProtoSignalingOffer.SignalingOfferResponse.Builder response) {
        if (invalidCallType(response.getType()))
            return;

        callPeerId = response.getCallerUserId();
        currentCallType = response.getType();
        currentSDP = response.getCallerSdp();

    }

    private void startService() {

    }

    private void stopService() {

    }

    public boolean isCallAlive() {
        return callAlive;
    }

    private boolean invalidCallType(ProtoSignalingOffer.SignalingOffer.Type type) {
        return type == ProtoSignalingOffer.SignalingOffer.Type.SECRET_CHAT || type == ProtoSignalingOffer.SignalingOffer.Type.SCREEN_SHARING || type == ProtoSignalingOffer.SignalingOffer.Type.UNRECOGNIZED;
    }

    public CallerInfo getCurrentCallerInfo() {
        return currentCallerInfo;
    }

    @Override
    public void receivedMessage(int id, Object... message) {

    }

    public void cleanUp(boolean stopService) {
        if (stopService) {
            stopService();
        }
    }
}
