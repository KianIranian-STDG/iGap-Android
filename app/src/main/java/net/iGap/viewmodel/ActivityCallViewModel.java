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

import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCall;
import net.iGap.databinding.ActivityCallBinding;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.helper.UserStatusController;
import net.iGap.interfaces.ISignalingCallBack;
import net.iGap.interfaces.SpeakerControlListener;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.MusicPlayer;
import net.iGap.module.enums.CallState;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.request.RequestSignalingGetLog;
import net.iGap.request.RequestSignalingLeave;
import net.iGap.request.RequestUserInfo;
import net.iGap.webrtc.WebRTC;

import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;

public class ActivityCallViewModel implements BluetoothProfile.ServiceListener {

    public ObservableInt showPeerSurface = new ObservableInt(View.GONE);
    public ObservableInt showRendererSurface = new ObservableInt(View.GONE);
    public ObservableInt showImageBackground = new ObservableInt(View.VISIBLE);
    public ObservableInt showRippleView = new ObservableInt(View.VISIBLE);// never gone it, invisible it
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
    public MutableLiveData<Boolean> changeViewState = new MutableLiveData<>();
    public MutableLiveData<Boolean> isMuteMusic = new MutableLiveData<>();
    public MutableLiveData<Boolean> playRingTone = new MutableLiveData<>();
    public MutableLiveData<String> imagePath = new MutableLiveData<>();
    public MutableLiveData<Boolean> initialVideoCallSurface = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogChangeConnectedDevice = new MutableLiveData<>();
    public MutableLiveData<Integer> playSound = new MutableLiveData<>();
    public MutableLiveData<Boolean> setAudioManagerSpeakerphoneOn = new MutableLiveData<>();
    public MutableLiveData<Boolean> setAudioManagerWithBluetooth = new MutableLiveData<>();


    private boolean isIncomingCall;
    private long userId;
    private ProtoSignalingOffer.SignalingOffer.Type callTYpe;
    private boolean isHiddenButtons = false;
    private boolean isBluetoothConnected = false;
    private boolean isHandsFreeConnected = false;


    public static boolean isConnected = false;
    public static TextView txtTimeChat, txtTimerMain;
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

        changeViewState.setValue(isIncomingCall);

        if (MusicPlayer.mp != null) {
            if (MusicPlayer.mp.isPlaying()) {
                MusicPlayer.pauseSound();
                MusicPlayer.pauseSoundFromIGapCall = true;
            }
        }

        if (isIncomingCall) {
            playRingTone.setValue(true);
            callBackTxtStatus.set(R.string.incoming_call);
            layoutOptionVisibility.set(View.GONE);
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
            endCallText.set(R.string.video_call_icon);
            answerCallIcon.set(R.string.video_call_icon);
            G.videoCallListener = () -> G.handler.post(() -> showImageBackground.set(View.GONE));
        } else {
            showSwitchCamera.set(View.GONE);
            showRendererSurface.set(View.GONE);
            showPeerSurface.set(View.GONE);
            endCallText.set(R.string.end_voice_call_icon);
            answerCallIcon.set(R.string.voice_call_icon);
        }
    }

    public void setBluetoothConnected(boolean bluetoothConnected) {
        isBluetoothConnected = bluetoothConnected;
        setSpeakerIcon();
    }

    public void setHandsFreeConnected(boolean handsFreeConnected) {
        isHandsFreeConnected = handsFreeConnected;
        setSpeakerIcon();
    }

    public boolean isVideoCall() {
        return callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING;
    }

    public void onClickBtnChat() {
        if (!isConnected && isIncomingCall) {
            endCall();
        }
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
        if (G.isWebRtcConnected) {
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
            G.isWebRtcConnected = true;
            if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
                showRendererSurface.set(View.VISIBLE);
                showImageBackground.set(View.GONE);
            }
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
                callBackTxtStatus.set(getTextString(callState));
                switch (callState) {
                    case RINGING:
                        setPhoneSpeaker();
                        playSound.setValue(R.raw.igap_ringing);
                        txtAviVisibility.set(View.VISIBLE);
                        G.isVideoCallRinging = true;
                        break;
                    case INCAMING_CALL:
                        txtAviVisibility.set(View.VISIBLE);
                        break;
                    case CONNECTING:
                        txtAviVisibility.set(View.VISIBLE);
                        break;
                    case CONNECTED:
                        txtAviVisibility.set(View.GONE);
                        layoutOptionVisibility.set(View.VISIBLE);
                        if (!isConnected) {
                            isConnected = true;
                            G.handler.postDelayed(() -> {
                                changeViewState.setValue(false);
                                playSound.setValue(R.raw.igap_connect);
                                setPhoneSpeaker();
                                if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
                                    showRendererSurface.set(View.VISIBLE);
                                    showUserAvatar.set(View.GONE);
                                    showSwitchCamera.set(View.VISIBLE);
                                    showPeerSurface.set(View.VISIBLE);
                                }
                                playRingTone.setValue(false);
                                startTimer();
                            }, 350);
                        }

                        break;
                    case DISCONNECTED:
                        txtAviVisibility.set(View.GONE);
                        G.handler.postDelayed(() -> {
                            playSound.setValue(R.raw.igap_discounect);
                            setPhoneSpeaker();
                            stopTimer();
                            endVoiceAndFinish();
                        }, 1000);
                        if (!isSendLeave) {
                            new RequestSignalingLeave().signalingLeave();
                        }
                        isConnected = false;
                        break;
                    case BUSY:
                        setPhoneSpeaker();
                        playSound.setValue(R.raw.igap_busy);
                        txtAviVisibility.set(View.GONE);
                        break;
                    case REJECT:
                        setPhoneSpeaker();
                        playSound.setValue(R.raw.igap_discounect);
                        txtAviVisibility.set(View.GONE);
                        break;
                    case FAILD:
                        setPhoneSpeaker();
                        playSound.setValue(R.raw.igap_noresponse);
                        txtAviVisibility.set(View.GONE);
                        new RequestSignalingLeave().signalingLeave();

                        isConnected = false;
                        G.handler.postDelayed(() -> {
                            stopTimer();
                            endVoiceAndFinish();
                        }, 500);

                        break;
                    case NOT_ANSWERED:
                        setPhoneSpeaker();
                        playSound.setValue(R.raw.igap_noresponse);
                        txtAviVisibility.set(View.GONE);
                        break;
                    case UNAVAILABLE:
                        setPhoneSpeaker();
                        playSound.setValue(R.raw.igap_noresponse);
                        txtAviVisibility.set(View.GONE);
                        break;
                    case TOO_LONG:
                        setPhoneSpeaker();
                        playSound.setValue(R.raw.igap_discounect);
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
        setAudioManagerSpeakerphoneOn.setValue(on);
    }

    private void endCall() {
        UserStatusController.getInstance().setOffline();
        G.isInCall = false;
        WebRTC.getInstance().leaveCall();
        isSendLeave = true;
        isConnected = false;
        G.handler.postDelayed(this::endVoiceAndFinish, 1000);
    }

    private void endVoiceAndFinish() {
        G.isInCall = false;
        playRingTone.setValue(false);
        if (ActivityCall.onFinishActivity != null) {
            ActivityCall.onFinishActivity.finishActivity();
        }
        if (G.iCallFinishChat != null) {
            G.iCallFinishChat.onFinish();
        }
        if (G.iCallFinishMain != null) {
            G.iCallFinishMain.onFinish();
        }
        if (MusicPlayer.pauseSoundFromIGapCall) {
            MusicPlayer.pauseSoundFromIGapCall = false;
            MusicPlayer.playSound();
        }
        //todo: fixed this static view
        txtTimeChat = txtTimerMain = null;
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
                String finalS = s;
                G.handler.post(() -> {
                    if (txtTimeChat != null) {
                        txtTimeChat.setText(finalS);
                    }
                    if (txtTimerMain != null) {
                        txtTimerMain.setText(finalS);
                    }
                });
            }
        }, 1000, 1000);
    }

    private void stopTimer() {
        txtTimeChat = txtTimerMain = null;
        txtTimerVisibility.set(View.GONE);
        if (secendTimer != null) {
            secendTimer.cancel();
            secendTimer = null;
        }
    }

    private void setPicture() {
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo registeredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);
        if (registeredInfo != null) {
            loadOrDownloadPicture(registeredInfo);
        } else {
            //todo: add callback and remove delay :D
            new RequestUserInfo().userInfo(userId);
            G.handler.postDelayed(() -> {
                Realm realm1 = Realm.getDefaultInstance();
                RealmRegisteredInfo registeredInfo1 = RealmRegisteredInfo.getRegistrationInfo(realm1, userId);
                if (registeredInfo1 != null) {
                    loadOrDownloadPicture(registeredInfo1);
                }
                realm1.close();
            }, 3000);
        }
        realm.close();
    }

    private void loadOrDownloadPicture(RealmRegisteredInfo registeredInfo) {
        try {
            callBackTxtName.set(registeredInfo.getDisplayName());
            RealmAttachment av = registeredInfo.getLastAvatar().getFile();
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

    public void leaveCall() {
        G.isInCall = false;
        if (isIncomingCall) {
            WebRTC.getInstance().leaveCall();
        }
    }

    //*****************************  distance sensor  **********************************************************

    public void onDestroy() {
        G.isInCall = false;
        G.iSignalingCallBack = null;
        G.onCallLeaveView = null;
        setSpeakerphoneOn(false);
        playRingTone.setValue(false);
        isMuteMusic.setValue(false);
        new RequestSignalingGetLog().signalingGetLog(0, 1);
        if (!isSendLeave) {
            WebRTC.getInstance().leaveCall();
        }
    }

    public void onLeaveView(String type) {
        isConnected = false;
        if (type.equals("error")) {
            playRingTone.setValue(false);
            txtAviVisibility.set(View.GONE);
            callBackTxtStatus.set(R.string.empty_error_message);
            G.handler.postDelayed(this::endVoiceAndFinish, 2000);
        } else {
            G.handler.postDelayed(this::endVoiceAndFinish, 1000);
        }
    }

    @Override
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        Log.i("#peymanProxy", "Activity call view model");
    }

    @Override
    public void onServiceDisconnected(int profile) {

    }
}
