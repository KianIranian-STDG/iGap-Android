package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.util.Log;
import android.view.View;

import androidx.annotation.StringRes;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.UserStatusController;
import net.iGap.module.AndroidUtils;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.CallState;
import net.iGap.module.webrtc.WebRTC;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.ISignalingCallBack;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.request.RequestSignalingGetLog;
import net.iGap.request.RequestUserInfo;
import net.iGap.viewmodel.controllers.CallManager;

import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;

@Deprecated
public class ActivityCallViewModel extends ViewModel implements BluetoothProfile.ServiceListener {

    public ObservableInt showPeerSurface = new ObservableInt(View.GONE);
    public ObservableInt showRendererSurface = new ObservableInt(View.GONE);
    public ObservableInt showImageBackground = new ObservableInt(View.VISIBLE);
    public ObservableInt showUserAvatar = new ObservableInt(View.VISIBLE);
    public ObservableInt showChatButton = new ObservableInt(View.VISIBLE);
    public ObservableInt showAddMemberButton = new ObservableInt(View.GONE);
    public ObservableInt showChangeToVideoCall = new ObservableInt(View.GONE);
    public ObservableInt showSwitchCamera = new ObservableInt(View.GONE);
    public ObservableField<String> callBackTxtName = new ObservableField<>("Name");
    public ObservableInt callBackTxtStatus = new ObservableInt(R.string.empty_error_message);
    public ObservableField<String> callBackTxtTimer = new ObservableField<>("00:00");
    public ObservableInt txtTimerVisibility = new ObservableInt(View.GONE);
    public ObservableInt endCallText = new ObservableInt(R.string.end_voice_call_icon);
    public ObservableInt answerCallIcon = new ObservableInt(R.string.voice_call_icon);
    public ObservableInt layoutAnswerCallVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt cllBackBtnMic = new ObservableInt(R.string.mic_on_icon);
    public ObservableInt cllBackBtnSpeaker = new ObservableInt(R.string.speaker_on_icon);
    public ObservableBoolean enableEndCallButton = new ObservableBoolean(true);
    //ui
    public MutableLiveData<Boolean> showRippleView = new MutableLiveData<>();// never gone it, invisible it
    public MutableLiveData<Boolean> changeViewState = new MutableLiveData<>();
    public MutableLiveData<Boolean> isMuteMusic = new MutableLiveData<>();
    public MutableLiveData<Boolean> playRingTone = new MutableLiveData<>();
    public MutableLiveData<String> imagePath = new MutableLiveData<>();
    public MutableLiveData<Boolean> initialVideoCallSurface = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogChangeConnectedDevice = new MutableLiveData<>();
    public SingleLiveEvent<Integer> playSound = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> setAudioManagerSpeakerphoneOn = new MutableLiveData<>();
    public MutableLiveData<Boolean> setAudioManagerWithBluetooth = new MutableLiveData<>();
    private MutableLiveData<Long> quickDeclineMessageLiveData = new MutableLiveData<>();
    public MutableLiveData<String> callTimerListener = new MutableLiveData<>();
    public SingleLiveEvent<Boolean> finishActivity = new SingleLiveEvent<>();


    private boolean isIncomingCall;
    private long userId;
    private ProtoSignalingOffer.SignalingOffer.Type callTYpe;
    private boolean isHiddenButtons = false;
    private boolean isBluetoothConnected = false;
    private boolean isHandsFreeConnected = false;


    public boolean isConnected = false;
    public boolean isConnecting = false;
    public ObservableInt txtAviVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt layoutOptionVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt layoutChatCallVisibility = new ObservableInt(View.VISIBLE);

    private boolean isSendLeave = false;
    private Timer secendTimer;
    private int secend = 0;
    private int minute = 0;

    public ActivityCallViewModel(long userId, boolean isIncomingCall, ProtoSignalingOffer.SignalingOffer.Type callTYpe) {

        this.userId = userId;
        this.isIncomingCall = isIncomingCall;
        this.callTYpe = callTYpe;

        if (userId == -1){
            finishActivity.setValue(true);
        }

        changeViewState.setValue(isIncomingCall);

        if (MusicPlayer.mp != null) {
            if (MusicPlayer.mp.isPlaying()) {
                MusicPlayer.stopSound();
                MusicPlayer.pauseSoundFromIGapCall = true;
            }
        }

        if (isIncomingCall) {
            playRingTone.setValue(true);
            callBackTxtStatus.set(R.string.incoming_call);
            layoutOptionVisibility.set(View.GONE);
            isConnecting = true;
        } else {
            setPhoneSpeaker();
            playSound.setValue(R.raw.igap_signaling);
            callBackTxtStatus.set(R.string.signaling);
            layoutAnswerCallVisibility.set(View.GONE);
            layoutChatCallVisibility.set(View.GONE);
        }

        //Todo: convert code setPicture
        setPicture();
        initCallBack();
        isMuteMusic.setValue(true);

        WebRTC.getInstance().setCallType(callTYpe);
        if (!isIncomingCall) {
            WebRTC.getInstance().createOffer(userId);
        }

        if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
            initialVideoCallSurface.setValue(true);
            showImageBackground.set(View.VISIBLE);
            endCallText.set(R.string.end_video_call_icon);
            answerCallIcon.set(R.string.video_call_icon);
            G.videoCallListener = () -> G.handler.post(() -> showImageBackground.set(View.GONE));
        } else {
            showSwitchCamera.set(View.GONE);
            showRendererSurface.set(View.GONE);
            showPeerSurface.set(View.GONE);
            endCallText.set(R.string.end_voice_call_icon);
            answerCallIcon.set(R.string.voice_call_icon);
        }

        if (G.userLogin) {
            if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
                HelperTracker.sendTracker(HelperTracker.TRACKER_VIDEO_CALL_CONNECTING);
            } else if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING) {
                HelperTracker.sendTracker(HelperTracker.TRACKER_VOICE_CALL_CONNECTING);
            }
        }
    }

    public void setBluetoothConnected(boolean bluetoothConnected) {
        Log.wtf(this.getClass().getName(), "setHandsFreeConnected");
        isBluetoothConnected = bluetoothConnected;
        setSpeakerIcon();
    }

    public void setHandsFreeConnected(boolean handsFreeConnected) {
        Log.wtf(this.getClass().getName(), "setHandsFreeConnected");
        isHandsFreeConnected = handsFreeConnected;
        setSpeakerIcon();
    }

    public boolean isVideoCall() {
        return callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING;
    }

    public void onClickBtnChat() {
        if (!isConnected && isIncomingCall) {
            quickDeclineMessageLiveData.postValue(userId);
        } else
            HelperPublicMethod.goToChatRoom(userId, null, null);
    }

    public void addPersonClickListener() {

    }

    public void videoCallClickListener() {

    }

    public void onClickBtnMic() {
        if (cllBackBtnMic.get() == R.string.mic_on_icon) {
            cllBackBtnMic.set(R.string.mic_off_icon);
            WebRTC.getInstance().muteSound();
        } else {
            cllBackBtnMic.set(R.string.mic_on_icon);
            WebRTC.getInstance().unMuteSound();
        }
    }

    public void onSurfaceViewRendererClick() {
        if (isConnected) {
            if (!isHiddenButtons) {
                showChatButton.set(View.INVISIBLE);
                if (showUserAvatar.get() == View.VISIBLE) {
                    showUserAvatar.set(View.INVISIBLE);
                }
                if (showSwitchCamera.get() == View.VISIBLE) {
                    showSwitchCamera.set(View.INVISIBLE);
                }
                if (txtTimerVisibility.get() == View.VISIBLE) {
                    txtTimerVisibility.set(View.INVISIBLE);
                }
                if (showChangeToVideoCall.get() == View.VISIBLE) {
                    showChangeToVideoCall.set(View.INVISIBLE);
                }
                if (showAddMemberButton.get() == View.VISIBLE) {
                    showAddMemberButton.set(View.INVISIBLE);
                }
            } else {
                showChatButton.set(View.VISIBLE);
                if (showUserAvatar.get() == View.INVISIBLE) {
                    showUserAvatar.set(View.VISIBLE);
                }
                if (showSwitchCamera.get() == View.INVISIBLE) {
                    showSwitchCamera.set(View.VISIBLE);
                }
                if (txtTimerVisibility.get() == View.INVISIBLE) {
                    txtTimerVisibility.set(View.VISIBLE);
                }
                if (showChangeToVideoCall.get() == View.INVISIBLE) {
                    showChangeToVideoCall.set(View.VISIBLE);
                }
                if (showAddMemberButton.get() == View.INVISIBLE) {
                    showAddMemberButton.set(View.VISIBLE);
                }
            }
            isHiddenButtons = !isHiddenButtons;
        }
    }

    public void onClickBtnSpeaker() {
        if (cllBackBtnSpeaker.get() == R.string.md_igap_bluetooth) {
            showDialogChangeConnectedDevice.setValue(true);
        } else if (cllBackBtnSpeaker.get() == R.string.speaker_off_icon) {
            if (isBluetoothConnected) {
                showDialogChangeConnectedDevice.setValue(true);
            } else {
                cllBackBtnSpeaker.set(R.string.speaker_on_icon);
                setSpeakerphoneOn(true);
            }
        } else {
            if (isBluetoothConnected) {
                showDialogChangeConnectedDevice.setValue(true);
            } else {
                cllBackBtnSpeaker.set(R.string.speaker_off_icon);
                setSpeakerphoneOn(false);
            }
        }
    }

    public void onEndCallClick() {
        endCall();
        enableEndCallButton.set(false);
    }

    public void onCallClick() {
        if (isIncomingCall) {
            if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
                showRendererSurface.set(View.VISIBLE);
                showImageBackground.set(View.GONE);
            }
            G.isWebRtcConnected = true;
            G.isVideoCallRinging = false;
            UserStatusController.getInstance().setOnline();
            WebRTC.getInstance().createAnswer();
            playRingTone.setValue(false);
            layoutAnswerCallVisibility.set(View.GONE);
            showAddMemberButton.set(View.VISIBLE);
            showChangeToVideoCall.set(View.VISIBLE);
        }
    }

    public void onClickBtnSwitchCamera() {
        WebRTC.getInstance().switchCamera();
    }

    private void setSpeakerIcon() {
        if (isBluetoothConnected) {
            cllBackBtnSpeaker.set(R.string.md_igap_bluetooth);
            setSpeakerphoneOn(false);
        } else if (isHandsFreeConnected) {
            cllBackBtnSpeaker.set(R.string.speaker_off_icon);
            setSpeakerphoneOn(false);
        } else {
            if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
                cllBackBtnSpeaker.set(R.string.speaker_on_icon);
                setSpeakerphoneOn(true);
            } else {
                cllBackBtnSpeaker.set(R.string.speaker_off_icon);
                setSpeakerphoneOn(false);
            }
        }
    }

    private void initCallBack() {
        G.iSignalingCallBack = new ISignalingCallBack() {
            @Override
            public void onStatusChanged(final CallState callState) {
                //G.handler(new Runnable() {
                //    @Override
                //    public void run() {
                Log.d("amini", "onStatusChanged: " + callState.name());
                isConnecting = false;
                callBackTxtStatus.set(getTextString(callState));
                switch (callState) {
                    case RINGING:
                        setPhoneSpeaker();
                        playSound.postValue(R.raw.igap_ringing);
                        txtAviVisibility.set(View.VISIBLE);
                        G.isVideoCallRinging = true;
                        isConnecting = true;
                        break;
                    case INCAMING_CALL:
                        showRippleView.postValue(true);
                        txtAviVisibility.set(View.VISIBLE);
                        break;
                    case CONNECTING:
                        showRippleView.postValue(false);
                        txtAviVisibility.set(View.VISIBLE);
                        isConnecting = true;
                        break;
                    case CONNECTED:

                        txtAviVisibility.set(View.GONE);
                        layoutOptionVisibility.set(View.VISIBLE);
                        if (!isConnected) {
                            if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
                                HelperTracker.sendTracker(HelperTracker.TRACKER_VIDEO_CALL_CONNECTED);
                            } else if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING) {
                                HelperTracker.sendTracker(HelperTracker.TRACKER_VOICE_CALL_CONNECTED);
                            }

                            isConnected = true;

                            changeViewState.postValue(false);
                            playSound.postValue(R.raw.igap_connect);
                            setPhoneSpeaker();
                            startTimer();
                            if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
                                showRendererSurface.set(View.VISIBLE);
                                showPeerSurface.set(View.VISIBLE);

                                showUserAvatar.set(View.INVISIBLE);
                                showSwitchCamera.set(View.INVISIBLE);
                                showChatButton.set(View.INVISIBLE);
                                txtTimerVisibility.set(View.INVISIBLE);
                                isHiddenButtons = true;
                            }
                            playRingTone.postValue(false);
                            /*G.handler.postDelayed(() -> {

                            }, 350);*/
                        }

                        break;
                    case DISCONNECTED:
                        showRippleView.postValue(true);
                        txtAviVisibility.set(View.GONE);

                        playSound.postValue(R.raw.igap_discounect);
                        setPhoneSpeaker();
                        stopTimer();
                        endVoiceAndFinish();

                        /*G.handler.postDelayed(() -> {

                        }, 1000);*/
                        if (!isSendLeave) {
                            CallManager.getInstance().leaveCall();
                        }
                        isConnected = false;
                        break;
                    case BUSY:
                        setPhoneSpeaker();
                        playSound.postValue(R.raw.igap_busy);
                        txtAviVisibility.set(View.GONE);
                        break;
                    case REJECT:
                    case TOO_LONG:
                        setPhoneSpeaker();
                        playSound.postValue(R.raw.igap_discounect);
                        txtAviVisibility.set(View.GONE);
                        break;
                    case FAILD:
                        showRippleView.postValue(true);
                        setPhoneSpeaker();
                        playSound.postValue(R.raw.igap_noresponse);
                        txtAviVisibility.set(View.GONE);
                        CallManager.getInstance().leaveCall();

                        isConnected = false;
                        stopTimer();
                        endVoiceAndFinish();
                        /*G.handler.postDelayed(() -> {

                        }, 500);
*/
                        break;
                    case NOT_ANSWERED:
                    case UNAVAILABLE:
                        setPhoneSpeaker();
                        playSound.postValue(R.raw.igap_noresponse);
                        txtAviVisibility.set(View.GONE);
                        break;
                }
                //    }
                //});
            }
        };
    }

    private @StringRes
    int getTextString(CallState callState) {
        switch (callState) {
            case SIGNALING:
                return R.string.signaling;
            case INCAMING_CALL:
                return R.string.incoming_call;
            case RINGING:
                return R.string.ringing;
            case CONNECTING:
                return R.string.connecting_call;
            case CONNECTED:
                return R.string.connected;
            case DISCONNECTED:
                return R.string.disconnected;
            case FAILD:
                return R.string.faild;
            case REJECT:
                return R.string.reject;
            case BUSY:
                return R.string.busy;
            case NOT_ANSWERED:
                return R.string.not_answered;
            case UNAVAILABLE:
                return R.string.unavalable;
            case TOO_LONG:
                return R.string.too_long;
            case ON_HOLD:
                return R.string.on_hold;
            case DISCONNECTING:
                return R.string.disconnecting;
            case POOR_CONNECTION:
                return R.string.poor_connection;
            default:
                return R.string.empty_error_message;
        }
    }

    public void chooseDevice(int position) {
        switch (position) {
            case 0:
                cllBackBtnSpeaker.set(R.string.md_igap_bluetooth);
                setSpeakerphoneOn(false);
                setAudioManagerWithBluetooth.setValue(true);
                break;
            case 1:
                cllBackBtnSpeaker.set(R.string.speaker_on_icon);
                setSpeakerphoneOn(true);
                break;
            case 2:
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                cllBackBtnSpeaker.set(R.string.speaker_off_icon);
                setAudioManagerWithBluetooth.setValue(false);
                /** cancel bluetooth if could not stop it
                 * if (audioManager.isBluetoothScoAvailableOffCall())
                 */
                try {
                    if (bluetoothAdapter != null && bluetoothAdapter.isEnabled())
                        bluetoothAdapter.disable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    /**
     * Sets the speaker phone mode.
     */
    private void setSpeakerphoneOn(boolean on) {
        try {
            WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
            WebRtcAudioUtils.useWebRtcBasedAcousticEchoCanceler();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setAudioManagerSpeakerphoneOn.postValue(on);
    }

    public void endCall() {
        G.iSignalingCallBack.onStatusChanged(CallState.DISCONNECTING);
        Log.wtf(this.getClass().getName(), "endCall");
        UserStatusController.getInstance().setOffline();
        G.isInCall = false;
        EventManager.getInstance().postEvent(EventManager.CALL_EVENT, false);
        //moved to call manager
        WebRTC.getInstance().leaveCall();
        isSendLeave = true;
        isConnected = false;
        isConnecting = false;

        /*G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                endVoiceAndFinish();
            }
        }, 1000);*/
    }

    private void endVoiceAndFinish() {
        Log.wtf(this.getClass().getName(), "endVoiceAndFinish");
        G.isInCall = false;
        EventManager.getInstance().postEvent(EventManager.CALL_EVENT, false);
        playRingTone.postValue(false);
        finishActivity.postValue(true);
        if (G.iCallFinishChat != null) {
            G.iCallFinishChat.onFinish();
        }
        if (G.iCallFinishMain != null) {
            G.iCallFinishMain.onFinish();
        }
        if (MusicPlayer.pauseSoundFromIGapCall) {
            MusicPlayer.pauseSoundFromIGapCall = false;
            // MusicPlayer.playSound();
        }
    }

    private void startTimer() {
        txtTimerVisibility.set(View.VISIBLE);
        secend = 0;
        minute = 0;
        secendTimer = new Timer();
        secendTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                secend++;
                if (secend >= 60) {
                    minute++;
                    secend %= 60;
                }
                if (minute >= 60) {
                    minute %= 60;
                }
                String s = String.format(Locale.getDefault(), "%02d:%02d", minute, secend);
                if (HelperCalander.isPersianUnicode) {
                    s = HelperCalander.convertToUnicodeFarsiNumber(s);
                }
                callBackTxtTimer.set(s);
                callTimerListener.postValue(s);
            }
        }, 1000, 1000);
    }

    private void stopTimer() {
        txtTimerVisibility.set(View.GONE);
        if (secendTimer != null) {
            secendTimer.cancel();
            secendTimer = null;
        }
    }

    private void setPicture() {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmRegisteredInfo registeredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);
            if (registeredInfo != null) {
                loadOrDownloadPicture(registeredInfo, realm);
            } else {
                //todo: add callback and remove delay :D
                new RequestUserInfo().userInfo(userId);
                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DbManager.getInstance().doRealmTask(realm1 -> {
                            RealmRegisteredInfo registeredInfo1 = RealmRegisteredInfo.getRegistrationInfo(realm1, userId);

                            if (registeredInfo1 != null) {
                                loadOrDownloadPicture(registeredInfo1, realm1);
                            }
                        });
                    }
                }, 3000);
            }
        });
    }

    private void loadOrDownloadPicture(RealmRegisteredInfo registeredInfo, Realm realm) {
        try {
            callBackTxtName.set(registeredInfo.getDisplayName());
            RealmAttachment av = registeredInfo.getLastAvatar(realm).getFile();
            ProtoFileDownload.FileDownload.Selector se = ProtoFileDownload.FileDownload.Selector.FILE;
            String dirPath = AndroidUtils.getFilePathWithCashId(av.getCacheId(), av.getName(), G.DIR_IMAGE_USER, false);

            HelperDownloadFile.getInstance().startDownload(ProtoGlobal.RoomMessageType.IMAGE, System.currentTimeMillis() + "", av.getToken(), av.getUrl(), av.getCacheId(), av.getName(), av.getSize(), se, dirPath, 4, new HelperDownloadFile.UpdateListener() {
                @Override
                public void OnProgress(final String path, int progress) {
                    if (progress == 100) {
                        G.handler.post(() -> imagePath.setValue(path));
                    }
                }

                @Override
                public void OnError(String token) {

                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void setPhoneSpeaker() {
        try {
            if (cllBackBtnSpeaker.get() == R.string.speaker_on_icon) {
                if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING)
                    setSpeakerphoneOn(true);
                else {
                    cllBackBtnSpeaker.set(R.string.speaker_off_icon);
                    setSpeakerphoneOn(false);
                }
            } else {
                setSpeakerphoneOn(false);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public void leaveCall() {
        G.isInCall = false;
        EventManager.getInstance().postEvent(EventManager.CALL_EVENT, false);
        if (isIncomingCall) {
            WebRTC.getInstance().leaveCall();
        }
    }

    //*****************************  distance sensor  **********************************************************


    @Override
    protected void onCleared() {
        Log.wtf(this.getClass().getName(), "onCleared");
        super.onCleared();
        G.isInCall = false;
        EventManager.getInstance().postEvent(EventManager.CALL_EVENT, false);
        G.iSignalingCallBack = null;
        G.onCallLeaveView = null;
        setSpeakerphoneOn(false);
        playRingTone.setValue(false);
        isMuteMusic.setValue(false);
        new RequestSignalingGetLog().signalingGetLog(0, 1);
        if (!isSendLeave) {
            // TODO: 5/9/2020 why here??
            WebRTC.getInstance().leaveCall();
        }
        if (G.onHoldBackgroundChanegeListener != null) {
            G.onHoldBackgroundChanegeListener = null;
        }
    }

    public void onLeaveView(String type) {
        Log.wtf(this.getClass().getName(), "onLeaveView");
        if (type.equals("SocketDisconnect"))
            G.iSignalingCallBack.onStatusChanged(CallState.POOR_CONNECTION);
        isConnected = false;
        isConnecting = false;
        if (type.equals("error")) {
            playRingTone.postValue(false);
            txtAviVisibility.set(View.GONE);
            callBackTxtStatus.set(R.string.empty_error_message);
        }
        endVoiceAndFinish();
    }

    @Override
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
    }

    @Override
    public void onServiceDisconnected(int profile) {

    }

    public MutableLiveData<Long> getQuickDeclineMessageLiveData() {
        return quickDeclineMessageLiveData;
    }
}
