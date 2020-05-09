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
import net.iGap.helper.HelperPublicMethod;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.CallState;
import net.iGap.module.webrtc.CallerInfo;
import net.iGap.module.webrtc.WebRTC;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.proto.ProtoSignalingAccept;
import net.iGap.proto.ProtoSignalingCandidate;
import net.iGap.proto.ProtoSignalingLeave;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.proto.ProtoSignalingSessionHold;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestSignalingAccept;
import net.iGap.request.RequestSignalingCandidate;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestSignalingLeave;
import net.iGap.request.RequestSignalingOffer;
import net.iGap.request.RequestSignalingRinging;
import net.iGap.request.RequestSignalingSessionHold;
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

    private CallManager() {
        DbManager.getInstance().doRealmTask(realm -> {
            currentCallConfig = realm.where(RealmCallConfig.class).findFirst();
            if (currentCallConfig == null) {
                new RequestSignalingGetConfiguration().signalingGetConfiguration();
            }
        });

        currentCallerInfo = new CallerInfo();
        currentCallerInfo.name = "Abolfazl Abbasi";
        currentCallerInfo.userId = AccountManager.getInstance().getCurrentUser().getId();
    }

    /**
     * this function is called when we are receiving a call response from others
     *
     * @param response from server
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
    public void makeOffer(long called_userId, net.iGap.proto.ProtoSignalingOffer.SignalingOffer.Type type, String callerSdp) {
        new RequestSignalingOffer().signalingOffer(called_userId, type, callerSdp);
    }

    /**
     * this function is step 1 when making a call
     */
    public void startCall() {
        WebRTC.getInstance().createOffer(callPeerId);
    }

    /**
     * this function is called after ringing response comes from server
     */
    public void onRing() {
        isRinging = true;
        if (onCallStateChanged != null) {
            G.handler.post(() -> onCallStateChanged.onCallStateChanged(CallState.RINGING));
        }
    }

    /**
     * this function is called after accept response comes from server
     *
     * @param response from server
     */
    public void onAccept(ProtoSignalingAccept.SignalingAcceptResponse.Builder response) {
        G.handler.post(() -> {
            WebRTC.getInstance().setOfferLocalDescription();
            WebRTC.getInstance().setRemoteDesc(new SessionDescription(ANSWER, response.getCalledSdp()));
        });
    }

    /**
     * this function is called when user decide to answer
     */
    public void makeAccept(String sdp) {
        new RequestSignalingAccept().signalingAccept(sdp);
    }

    /**
     * this function is called after candidate response comes from server
     *
     * @param builder from server
     */
    public void onCandidate(ProtoSignalingCandidate.SignalingCandidateResponse.Builder builder) {
        G.handler.post(() -> WebRTC.getInstance()
                .peerConnectionInstance()
                .addIceCandidate(new IceCandidate(builder.getPeerSdpMId(), builder.getPeerSdpMLineIndex(), builder.getPeerCandidate())));
    }

    /**
     * this function is called when user wants to send its candidate info to peer
     */
    public void exchangeCandidate(String sdpMId, int sdpMLineIndex, String candidate) {
        new RequestSignalingCandidate().signalingCandidate(sdpMId, sdpMLineIndex, candidate);
    }

    /**
     * this function is called after leave response comes from server in behave of other side
     *
     * @param builder from server
     */
    public void onLeave(ProtoSignalingLeave.SignalingLeaveResponse.Builder builder) {
        G.handler.post(() -> {
            // TODO: 5/6/2020 this part needs to change based on new design
            try {
                AudioManager am = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);
                G.appChangeRinggerMode = false;
                am.setRingerMode(G.mainRingerMode);
            } catch (Exception e) {
            }

            if (onCallStateChanged != null) {
                isRinging = false;
                switch (builder.getType()) {
                    case REJECTED:
                        onCallStateChanged.onCallStateChanged(CallState.REJECT);
                        break;
                    case NOT_ANSWERED:
                        onCallStateChanged.onCallStateChanged(CallState.NOT_ANSWERED);
                        break;
                    case UNAVAILABLE:
                        onCallStateChanged.onCallStateChanged(CallState.UNAVAILABLE);
                        break;
                    case TOO_LONG:
                        onCallStateChanged.onCallStateChanged(CallState.TOO_LONG);
                        break;
                }
                onCallStateChanged.onCallStateChanged(CallState.LEAVE_CALL);
            }
        });
    }

    public void leaveCall() {
        new RequestSignalingLeave().signalingLeave();
    }

    public void onHold(ProtoSignalingSessionHold.SignalingSessionHoldResponse.Builder builder) {
        if (builder.getHold()) {
            WebRTC.getInstance().toggleSound(false);
            onCallStateChanged.onCallStateChanged(CallState.ON_HOLD);
        } else {
            WebRTC.getInstance().unMuteSound();
            onCallStateChanged.onCallStateChanged(CallState.CONNECTED);
        }
//        G.onHoldBackgroundChanegeListener this needs to be deleted.
    }

    public void holdCall(boolean state) {
        new RequestSignalingSessionHold().signalingSessionHold(state);
    }

    public void onError(int major, int minor) {
        int messageID = R.string.e_call_permision;
        switch (major) {
            case 900:
            case 916:
            case 907:
                //                RINGING_BAD_PAYLOAD
                //                LEAVE_BAD_PAYLOAD
                //                GET_CONFIGURATION_BAD_PAYLOAD
                messageID = R.string.call_error_badPayload;
                break;
            case 901:
            case 920:
            case 917:
            case 914:
            case 911:
            case 908:
            case 903:
                //                OFFER_INTERNAL_SERVER_ERROR
                //                RINGING_INTERNAL_SERVER_ERROR
                //                ACCEPT_INTERNAL_SERVER_ERROR
                //                CANDIDATE_INTERNAL_SERVER_ERROR
                //                LEAVE_INTERNAL_SERVER_ERROR
                //                SESSION_HOLD_INTERNAL_SERVER_ERROR
                //                GET_CONFIGURATION_INTERNAL_SERVER_ERROR
                messageID = R.string.call_error_internalServer;
                break;
            case 902:
                //                OFFER_BAD_PAYLOAD
                switch (minor) {
                    case 1:
                    case 2:
                    case 3:
                        //                        Caller_SDP is invalid
                        //                        Type is invalid
                        //                        CalledUser_ID is invalid
                        messageID = R.string.call_error_offer;
                        break;
                    default:
                        messageID = R.string.call_error_badPayload;
                        break;
                }
                break;
            case 904:
                switch (minor) {
                    case 6:
                        messageID = R.string.e_904_6;
                        onCallStateChanged.onCallStateChanged(CallState.UNAVAILABLE);
                        break;
                    case 7:
                        messageID = R.string.e_904_7;
                        onCallStateChanged.onCallStateChanged(CallState.UNAVAILABLE);
                        break;
                    case 8:
                        messageID = R.string.e_904_8;
                        onCallStateChanged.onCallStateChanged(CallState.UNAVAILABLE);
                        break;
                    case 9:
                        messageID = R.string.e_904_9;
                        onCallStateChanged.onCallStateChanged(CallState.BUSY);
                        break;
                    default:
                        onCallStateChanged.onCallStateChanged(CallState.UNAVAILABLE);
                        break;
                }
                break;
            case 921:
            case 918:
            case 915:
            case 912:
            case 909:
                //                RINGING_FORBIDDEN
                //                ACCEPT_FORBIDDEN
                //                CANDIDATE_FORBIDDEN
                //                LEAVE_FORBIDDEN
                //                SESSION_HOLD_FORBIDDEN
                //                OFFER_FORBIDDEN
                messageID = R.string.call_error_forbidden;
                break;
            case 905:
            case 906:
                //                OFFER_PRIVACY_PROTECTION
                //                OFFER_BLOCKED_BY_PEER
                messageID = R.string.e_906_1;
                break;
            case 910:
                //                ACCEPT_BAD_PAYLOAD
                if (minor == 1) {
                    //                    Called_SDP is invalid
                    messageID = R.string.call_error_accept;
                } else {
                    messageID = R.string.call_error_badPayload;
                }
                break;
            case 913:
                //                CANDIDATE_BAD_PAYLOAD
                switch (minor) {
                    case 1:
                    case 2:
                    case 3:
                        //                        SDP_M_Line_Index is invalid
                        //                        SDP_MID is invalid
                        //                        Candidate is invalid
                        messageID = R.string.call_error_candidate;
                        break;
                    default:
                        messageID = R.string.call_error_badPayload;
                        break;
                }
                break;
            case 919:
                //                SESSION_HOLD_BAD_PAYLOAD
                if (minor == 1) {
                    //                    Hold is invalid
                    messageID = R.string.call_error_hold;
                } else {
                    messageID = R.string.call_error_badPayload;
                }
                break;
        }
        onCallStateChanged.onError(messageID, major, minor);
    }

    public void toggleSpeaker() {

    }

    public void toggleMic(boolean isEnable) {
        WebRTC.getInstance().toggleSound(isEnable);
    }

    public void toggleCamera() {
        WebRTC.getInstance().switchCamera();
    }

    public void endCall() {
        leaveCall();
    }

    public void directMessage() {
        HelperPublicMethod.goToChatRoom(callPeerId, null, null);
    }

    public void acceptCall() {
        WebRTC.getInstance().createAnswer();
    }

    /**
     * check for acceptable modes for call
     *
     * @param type is from server
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

        void onError(int messageID, int major, int minor);
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
                || "crosshatch".equals(Build.PRODUCT);    // Pixel 3 XL
    }

    public boolean isMicMute() {
        return false;
    }
}
