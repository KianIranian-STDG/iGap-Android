package net.iGap.viewmodel.controllers;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.widget.Toast;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.CallSelectFragment;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.CallState;
import net.iGap.module.webrtc.CallerInfo;
import net.iGap.module.webrtc.WebRTC;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.proto.ProtoSignalingAccept;
import net.iGap.proto.ProtoSignalingCandidate;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestSignalingRinging;
import net.iGap.viewmodel.controllers.telecom.CallConnectionService;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import static org.webrtc.SessionDescription.Type.ANSWER;
import static org.webrtc.SessionDescription.Type.OFFER;

public class CallManager implements EventListener {

    private long callPeerId;
    private ProtoSignalingOffer.SignalingOffer.Type callType;

    private RealmRegisteredInfo info;
    private RealmCallConfig currentCallConfig;

    private boolean callAlive = false;
    private boolean isRinging = false;
    private boolean isIncomeCall = false;

    private CallerInfo currentCallerInfo;

    private CallStateChange onCallStateChanged;

    protected static final boolean USE_CONNECTION_SERVICE = isDeviceCompatibleWithConnectionServiceAPI();

    private static volatile CallManager instance = null;

    // make this class singlton
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

    public CallManager() {
        DbManager.getInstance().doRealmTask(realm -> {
            currentCallConfig = realm.where(RealmCallConfig.class).findFirst();
            if (currentCallConfig == null) {
                new RequestSignalingGetConfiguration().signalingGetConfiguration();
            }
        });
    }

    /**
     * this function is called when we are receiving a call response from others
     *
     * @param response
     */
    public void onOffer(ProtoSignalingOffer.SignalingOfferResponse.Builder response) {
        if (invalidCallType(response.getType()))
            return;
        // set data for future use.
        callPeerId = response.getCallerUserId();
        callType = response.getType();

        WebRTC.getInstance().setCallType(callType);
        // activate ringing state for caller.
        isRinging = true;
        isIncomeCall = true;
        new RequestSignalingRinging().signalingRinging();
        // generate SDP
        G.handler.post(() -> WebRTC.getInstance().setRemoteDesc(new SessionDescription(OFFER, response.getCallerSdp())));
    }

    /**
     * this function is called when we are making a call to others
     */
    public void makeOffer() {

    }

    /**
     * this function is called after ringing response comes from server
     */
    public void onRing() {
        isRinging = true;
        G.handler.post(() -> onCallStateChanged.onCallStateChanged(CallState.RINGING));
    }

    /**
     * this function is called after we receive offer and waiting for user's answer
     */
    public void makeRing() {

    }

    /**
     * this function is called after accept response comes from server
     *
     * @param response
     */
    public void onAccept(ProtoSignalingAccept.SignalingAcceptResponse.Builder response) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                WebRTC.getInstance().setOfferLocalDescription();
                WebRTC.getInstance().setRemoteDesc(new SessionDescription(ANSWER, response.getCalledSdp()));
            }
        });
    }

    /**
     * this function is called when user decide to answer
     */
    public void makeAccept() {

    }

    /**
     * this function is called after candidate response comes from server
     *
     * @param builder
     */
    public void onCandidate(ProtoSignalingCandidate.SignalingCandidateResponse.Builder builder) {
        G.handler.post(() -> WebRTC.getInstance()
                .peerConnectionInstance()
                .addIceCandidate(new IceCandidate(builder.getPeerSdpMId(), builder.getPeerSdpMLineIndex(), builder.getPeerCandidate())));
    }

    /**
     * this function is called when user wants to send its candidate info to peer
     */
    public void exchangeCandidate() {

    }

    public void toggleSpeaker() {

    }

    public void toggleMic() {

    }

    public void toggleCamera() {

    }

    public void endCall() {

    }

    public void directMessage() {

    }

    public void acceptCall() {

    }

    /**
     * check for acceptable modes for call
     *
     * @param type
     * @return true if it is NOT secret chat and screen sharing.
     */
    private boolean invalidCallType(ProtoSignalingOffer.SignalingOffer.Type type) {
        return type == ProtoSignalingOffer.SignalingOffer.Type.SECRET_CHAT || type == ProtoSignalingOffer.SignalingOffer.Type.SCREEN_SHARING || type == ProtoSignalingOffer.SignalingOffer.Type.UNRECOGNIZED;
    }

    public void onSdpSuccess() {
        if (isIncomeCall)
            openCallInterface();
        else {
            isRinging = false;
            // TODO: 5/5/2020 this lines should be changed and be deleted.
            G.isVideoCallRinging = false;
            try {
                AudioManager am = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);
                G.mainRingerMode = am.getRingerMode();
                G.appChangeRinggerMode = true;
                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            } catch (Exception e) {
            }
            if (G.videoCallListener != null) {

                G.videoCallListener.notifyBackgroundChange();
            }
            // until here
        }
    }

    private void openCallInterface() {
        CallSelectFragment.call(callPeerId, isIncomeCall, callType);
    }

    private void startService() {

    }

    private void stopService() {

    }

    public boolean isCallAlive() {
        return callAlive;
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

    public long getCallPeerId() {
        return callPeerId;
    }

    public ProtoSignalingOffer.SignalingOffer.Type getCallType() {
        return callType;
    }

    public void setOnCallStateChanged(CallStateChange onCallStateChanged) {
        this.onCallStateChanged = onCallStateChanged;
    }

    public interface CallStateChange {
        void onCallStateChanged(CallState callState);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void placeOutgoingCall(Context mContext) {
        TelecomManager tm = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
        PhoneAccountHandle phoneAccountHandle = addAccountToTelecomManager(mContext);
        if (!tm.isOutgoingCallPermitted(phoneAccountHandle)) {
            Toast.makeText(mContext, "R.string.outgoingCallNotPermitted", Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle extras = new Bundle();
        extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);
        tm.placeCall(Uri.fromParts("tel", "+98" + info.getPhoneNumber(), null), extras);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void placeIncomingCall(Context mContext) {
        TelecomManager tm = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
        PhoneAccountHandle phoneAccountHandle = addAccountToTelecomManager(mContext);
        if (!tm.isIncomingCallPermitted(phoneAccountHandle)) {
            Toast.makeText(mContext, "R.string.incomingCallNotPermitted", Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle extras = new Bundle();
        extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, Uri.parse(info.getPhoneNumber()));
        tm.addNewIncomingCall(phoneAccountHandle, extras);
    }

    @TargetApi(Build.VERSION_CODES.O)
    protected PhoneAccountHandle addAccountToTelecomManager(Context mContext) {
        TelecomManager tm = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
        PhoneAccountHandle handle = new PhoneAccountHandle(new ComponentName(mContext, CallConnectionService.class), "1001");
        DbManager.getInstance().doRealmTask(realm -> {
            info = realm.where(RealmUserInfo.class).findFirst().getUserInfo();
        });
        PhoneAccount account = new PhoneAccount.Builder(handle, info.getDisplayName())
                .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED)
                .setIcon(Icon.createWithResource(mContext, R.drawable.logo_igap))
                .setHighlightColor(0xff2ca5e0)
                .addSupportedUriScheme("sip")
                .build();
        tm.registerPhoneAccount(account);
        return handle;
    }

    private static boolean isDeviceCompatibleWithConnectionServiceAPI() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return false;
        // some non-Google devices don't implement the ConnectionService API correctly so, sadly,
        // we'll have to whitelist only a handful of known-compatible devices for now
        return "angler".equals(Build.PRODUCT)            // Nexus 6P
                || "bullhead".equals(Build.PRODUCT)        // Nexus 5X
                || "sailfish".equals(Build.PRODUCT)        // Pixel
                || "marlin".equals(Build.PRODUCT)        // Pixel XL
                || "walleye".equals(Build.PRODUCT)        // Pixel 2
                || "taimen".equals(Build.PRODUCT)        // Pixel 2 XL
                || "blueline".equals(Build.PRODUCT)        // Pixel 3
                || "crosshatch".equals(Build.PRODUCT);	// Pixel 3 XL
    }
}
