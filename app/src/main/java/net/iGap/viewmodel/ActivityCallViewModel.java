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
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.media.AudioFocusRequest;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCall;
import net.iGap.databinding.ActivityCallBinding;
import net.iGap.fragments.FragmentNotificationAndSound;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.interfaces.ISignalingCallBack;
import net.iGap.interfaces.SpeakerControlListener;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SHP_SETTING;
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

import org.webrtc.voiceengine.WebRtcAudioEffects;
import org.webrtc.voiceengine.WebRtcAudioManager;
import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import yogesh.firzen.mukkiasevaigal.K;

import static android.content.Context.MODE_PRIVATE;
import static android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY;
import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;

public class ActivityCallViewModel implements BluetoothProfile.ServiceListener {

    public static boolean isConnected = false;
    public static TextView txtTimeChat, txtTimerMain;
    public Vibrator vibrator;
    public ObservableField<String> cllBackBtnSpeaker = new ObservableField<>(G.context.getResources().getString(R.string.md_Mute));
    public ObservableField<String> cllBackBtnMic = new ObservableField<>(G.context.getResources().getString(R.string.md_mic));
    public ObservableField<String> callBackTxtTimer = new ObservableField<>("00:00");
    public ObservableField<String> callBackTxtStatus = new ObservableField<>("Status");
    public ObservableField<String> callBackTxtName = new ObservableField<>("Name");
    public ObservableInt txtAviVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt layoutOptionVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt txtTimerVisibility = new ObservableInt(View.GONE);
    public ObservableInt layoutChatCallVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt layoutAnswerCallVisibility = new ObservableInt(View.VISIBLE);
    private boolean isIncomingCall = false;
    private long userId;
    private boolean isSendLeave = false;
    private int musicVolum = 0;
    private boolean isMuteAllMusic = false;
    private Timer secendTimer;
    private int secend = 0;
    private int minute = 0;
    private MediaPlayer player;
    private MediaPlayer ringtonePlayer;
    private Context context;
    private ActivityCallBinding activityCallBinding;
    private ProtoSignalingOffer.SignalingOffer.Type callTYpe;

    private boolean isFinish = false;
    private AudioManager audioManager;
    BluetoothManager bluetoothManager;
    BluetoothHeadset bluetoothHeadset;


    public ActivityCallViewModel(Context context, long userId, boolean isIncomingCall, ActivityCallBinding activityCallBinding, ProtoSignalingOffer.SignalingOffer.Type callTYpe) {

        this.context = context;
        this.userId = userId;
        this.isIncomingCall = isIncomingCall;
        this.activityCallBinding = activityCallBinding;
        this.callTYpe = callTYpe;
        //   setPicture();
        audioManager = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.getMode();
        try {
            Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();

            for (BluetoothDevice device : pairedDevices) {
                Log.d("#peyman", " name=" + device.getName() + ", address=" + device.getAddress());

                device.getAddress();
            }
        } catch (NullPointerException e) {
        } catch (Exception e) {
        }

        getInfo();

    }

    public void onClickBtnChat(View v) {

        if (!isConnected && isIncomingCall) {
            endCall();

        }

        HelperPublicMethod.goToChatRoom(userId, null, null);

    }

    public void onClickBtnMic(View v) {

        if (cllBackBtnMic.get().toString().equals(G.context.getResources().getString(R.string.md_mic))) {
            cllBackBtnMic.set(G.context.getResources().getString(R.string.md_mic_off));
            WebRTC.getInstance().muteSound();
        } else {
            cllBackBtnMic.set(G.context.getResources().getString(R.string.md_mic));
            WebRTC.getInstance().unMuteSound();
        }
    }


    public void onClickBtnSpeaker(View v) {


        if (cllBackBtnSpeaker != null && cllBackBtnSpeaker.get() != null) {

            if (cllBackBtnSpeaker.get().equals(G.context.getResources().getString(R.string.md_igap_bluetooth))) {
                changeConnectedDevice(v);

            } else if (cllBackBtnSpeaker.get().equals(G.context.getResources().getString(R.string.md_Mute))) {
                if (G.isBluetoothConnected)
                    changeConnectedDevice(v);
                else {
                    cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_unMuted));
                    setSpeakerphoneOn(true);
                }

            } else {
                if (G.isBluetoothConnected)
                    changeConnectedDevice(v);
                else {
                    cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_Mute));
                    setSpeakerphoneOn(false);
                }
            }
        }
    }

    private void changeConnectedDevice(View v) {
        try {

            new MaterialDialog.Builder(v.getContext()).title(R.string.switchTo).itemsGravity(GravityEnum.CENTER).items(R.array.phone_selection).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                    switch (position) {
                        case 0:


                           /* if (!bluetoothAdapter.isEnabled())
                                bluetoothAdapter.enable();*/
                            cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_igap_bluetooth));
                            //     audioManager.setBluetoothScoOn(true);
                            setSpeakerphoneOn(false);
                            audioManager.setMode(0);
                            audioManager.startBluetoothSco();
                            audioManager.setBluetoothScoOn(true);
                            audioManager.setMode(AudioManager.MODE_IN_CALL);
                            //  if (!audioManager.isBluetoothScoOn()) {


                            //   }
                            break;
                        case 1:
                            cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_unMuted));
                            setSpeakerphoneOn(true);

                            break;
                        case 2:
                            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_Mute));
                            //            setSpeakerphoneOn(false);
                            audioManager.setMode(AudioManager.MODE_INVALID);
                            //   if (audioManager.isBluetoothScoOn()) {
                            audioManager.setBluetoothScoOn(false);
                            audioManager.stopBluetoothSco();


                            /** cancel bluetooth if could not stop it
                             * if (audioManager.isBluetoothScoAvailableOffCall())
                             */
                            try {
                                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled())
                                    bluetoothAdapter.disable();
                            } catch (Exception e) {
                            }

                            /*audioManager.stopBluetoothSco();
                            audioManager.setBluetoothScoOn(false);*/

                            //     }
                            break;

                    }
                }
            }).show();

        } catch (Exception e) {
        }
    }

/*    public void onClickBtnSpeaker(View v) {
        if (cllBackBtnSpeaker != null && cllBackBtnSpeaker.get() != null) {
            if (cllBackBtnSpeaker.get().equals(G.context.getResources().getString(R.string.md_Mute))) {
                if (G.isBluetoothConnected) {
                    cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_igap_bluetooth));
                    setSpeakerphoneOn(false);

                } else {
                    cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_unMuted));
                    setSpeakerphoneOn(true);
                }

            } else if (cllBackBtnSpeaker.get().equals(G.context.getResources().getString(R.string.md_unMuted))) {
                if (G.isBluetoothConnected)
                    cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_igap_bluetooth));
                else
                    cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_Mute));

                setSpeakerphoneOn(false);
            } else if (cllBackBtnSpeaker.get().equals(G.context.getResources().getString(R.string.md_igap_bluetooth))) {
                if (G.isHandsFreeConnected) {
                    cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_Mute));
                    setSpeakerphoneOn(false);
                } else {
                    cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_unMuted));
                    setSpeakerphoneOn(true);
                }


            }
        }
    }*/

    public void onClickBtnSwitchCamera(View v) {
        WebRTC.getInstance().switchCamera();
    }


    private void getInfo() {
        initComponent();
        initCallBack();
        muteMusic();
    }


    private void initComponent() {
        G.speakerControlListener = new SpeakerControlListener() {
            @Override
            public void setOnChangeSpeaker(int resId) {
                cllBackBtnSpeaker.set(G.context.getResources().getString(resId));
                if (G.isBluetoothConnected) {
                    cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_igap_bluetooth));
                    setSpeakerphoneOn(false);
                } else if (G.isHandsFreeConnected) {
                    cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_Mute));
                    setSpeakerphoneOn(false);
                } else {
                    if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
                        cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_unMuted));
                        setSpeakerphoneOn(true);
                    } else {
                        cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_Mute));
                        setSpeakerphoneOn(false);
                    }

                }
            }
        };


        if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
            if (G.isBluetoothConnected) {
                cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_igap_bluetooth));
                setSpeakerphoneOn(false);
            } else if (G.isHandsFreeConnected) {
                cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_Mute));
                setSpeakerphoneOn(false);
            } else {
                cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_unMuted));
                setSpeakerphoneOn(true);
            }

        }

        if (MusicPlayer.mp != null) {
            if (MusicPlayer.mp.isPlaying()) {
                MusicPlayer.pauseSound();
                MusicPlayer.pauseSoundFromIGapCall = true;
            }
        }


        if (isIncomingCall) {
            playRingtone();
            callBackTxtStatus.set(G.context.getResources().getString(R.string.incoming_call));
            layoutOptionVisibility.set(View.GONE);
        } else {
            playSound(R.raw.igap_signaling);
            callBackTxtStatus.set(G.context.getResources().getString(R.string.signaling));
            layoutAnswerCallVisibility.set(View.GONE);
            layoutChatCallVisibility.set(View.GONE);

        }

        //setAnimation();
        setPicture();
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
                        playSound(R.raw.igap_ringing);
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
                            playSound(R.raw.igap_connect);
                            G.handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    cancelRingtone();
                                    startTimer();
                                }
                            }, 350);
                        }

                        break;
                    case DISCONNECTED:
                        txtAviVisibility.set(View.GONE);
                        playSound(R.raw.igap_discounect);
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                stopTimer();
                                endVoiceAndFinish();
                            }
                        }, 1000);
                        if (!isSendLeave) {
                            new RequestSignalingLeave().signalingLeave();
                        }
                        isConnected = false;
                        break;
                    case BUSY:
                        playSound(R.raw.igap_busy);
                        txtAviVisibility.set(View.GONE);
                        break;
                    case REJECT:
                        playSound(R.raw.igap_discounect);
                        txtAviVisibility.set(View.GONE);
                        break;
                    case FAILD:
                        playSound(R.raw.igap_noresponse);
                        txtAviVisibility.set(View.GONE);
                        new RequestSignalingLeave().signalingLeave();

                        isConnected = false;
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                stopTimer();
                                endVoiceAndFinish();
                            }
                        }, 500);

                        break;
                    case NOT_ANSWERED:
                        playSound(R.raw.igap_noresponse);
                        txtAviVisibility.set(View.GONE);
                        break;
                    case UNAVAILABLE:
                        playSound(R.raw.igap_noresponse);
                        txtAviVisibility.set(View.GONE);
                        break;
                    case TOO_LONG:
                        playSound(R.raw.igap_discounect);
                        txtAviVisibility.set(View.GONE);
                        break;
                }
                //    }
                //});
            }
        };
    }

    private String getTextString(CallState callState) {

        String result = "";

        switch (callState) {

            case SIGNALING:
                result = G.context.getResources().getString(R.string.signaling);
                break;
            case INCAMING_CALL:
                result = G.context.getResources().getString(R.string.incoming_call);
                break;
            case RINGING:
                result = G.context.getResources().getString(R.string.ringing);
                break;
            case CONNECTING:
                result = G.context.getResources().getString(R.string.connecting_call);
                break;
            case CONNECTED:
                result = G.context.getResources().getString(R.string.connected);
                break;
            case DISCONNECTED:
                result = G.context.getResources().getString(R.string.disconnected);
                break;
            case FAILD:
                result = G.context.getResources().getString(R.string.faild);
                break;
            case REJECT:
                result = G.context.getResources().getString(R.string.reject);
                break;
            case BUSY:
                result = G.context.getResources().getString(R.string.busy);
                break;
            case NOT_ANSWERED:
                result = G.context.getResources().getString(R.string.not_answered);
                break;
            case UNAVAILABLE:
                result = G.context.getResources().getString(R.string.unavalable);
                break;
            case TOO_LONG:
                result = G.context.getResources().getString(R.string.too_long);
                break;
            case ON_HOLD:
                result = G.context.getResources().getString(R.string.on_hold);
                break;
        }

        return result;
    }

    /**
     * *************** common methods ***************
     */

    /**
     * Sets the speaker phone mode.
     */

    public void setSpeakerphoneOn(boolean on) {


        //  mAudioRecord.startRecording();
/*        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                48000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                1024);
        NoiseSuppressor ns;
        AcousticEchoCanceler aec;
        if (AcousticEchoCanceler.isAvailable()) {
            aec = AcousticEchoCanceler.create(audioRecord.getAudioSessionId());
            if (aec != null)
                aec.setEnabled(true);
        }
        ns = NoiseSuppressor.create(audioRecord.getAudioSessionId());
        if (ns != null)
            ns.setEnabled(true);

        audioRecord.startRecording();*/
        try {


     /*       AudioRecord  mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, 16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 2048);
            NoiseSuppressor  mNoiseSuppressor = NoiseSuppressor.create(mAudioRecord.getAudioSessionId());
            if (mNoiseSuppressor != null) {
                int res = mNoiseSuppressor.setEnabled(true);
            }

            AcousticEchoCanceler  mAcousticEchoCanceler = AcousticEchoCanceler.create(mAudioRecord.getAudioSessionId());
            if (mAcousticEchoCanceler != null) {
                int res = mAcousticEchoCanceler.setEnabled(true);
            }*/

            WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
            WebRtcAudioUtils.useWebRtcBasedAcousticEchoCanceler();
    /*        int audioSessionId = id;

            if (NoiseSuppressor.isAvailable()) {
                NoiseSuppressor.create(audioSessionId);
                Log.i("#peyman", " noise " + NoiseSuppressor.create(audioSessionId));
            }
            if (AutomaticGainControl.isAvailable()) {
                AutomaticGainControl.create(audioSessionId);
                Log.i("#peyman", " gain " + AutomaticGainControl.create(audioSessionId).getEnabled());
            }
            if (AcousticEchoCanceler.isAvailable()) {

                AcousticEchoCanceler.create(audioSessionId);
                Log.i("#peyman", " echo " + AcousticEchoCanceler.create(audioSessionId).getEnabled());
            }*/


        /*    WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
            WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true);
            WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true);*/
          /*  WebRtcAudioUtils.useWebRtcBasedAutomaticGainControl();
            WebRtcAudioUtils.useWebRtcBasedAcousticEchoCanceler();
            WebRtcAudioUtils.useWebRtcBasedNoiseSuppressor();*/
        /*    WebRtcAudioEffects.create().setAEC(true);
            WebRtcAudioEffects.create().setNS(true);*/
        } catch (Exception e) {
        }

        boolean wasOn = false;
        if (audioManager != null) {
            wasOn = audioManager.isSpeakerphoneOn();
        }
        if (wasOn == on) {
            return;
        }
        if (audioManager != null) {
            audioManager.setSpeakerphoneOn(on);

        }
    }

    public void endCall() {

        G.isInCall = false;

        WebRTC.getInstance().leaveCall();
        isSendLeave = true;

        isConnected = false;

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                endVoiceAndFinish();
            }
        }, 1000);
    }

    private void endVoiceAndFinish() {

        G.isInCall = false;

        cancelRingtone();

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

                activityCallBinding.fcrTxtTimer.post(new Runnable() {

                    @Override
                    public void run() {
                        String s = "";
                        if (minute < 10) {
                            s += "0" + minute;
                        } else {
                            s += minute;
                        }
                        s += ":";
                        if (secend < 10) {
                            s += "0" + secend;
                        } else {
                            s += secend;
                        }

                        if (HelperCalander.isPersianUnicode) {
                            s = HelperCalander.convertToUnicodeFarsiNumber(s);
                        }

                        callBackTxtTimer.set(s);

                        if (txtTimeChat != null) {
                            txtTimeChat.setText(s);
                        }

                        if (txtTimerMain != null) {
                            txtTimerMain.setText(s);
                        }
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

    //private void setAnimation() {
    //    Animation animation = AnimationUtils.loadAnimation(G.context.getApplicationContext(), R.anim.translate_enter_down_circke_button);
    //    layoutCaller.startAnimation(animation);
    //}

    private void setPicture() {
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo registeredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);

        if (registeredInfo != null) {
            loadOrDownloadPicture(registeredInfo);
        } else {
            new RequestUserInfo().userInfo(userId);
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Realm realm = Realm.getDefaultInstance();
                    RealmRegisteredInfo registeredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);

                    if (registeredInfo != null) {
                        loadOrDownloadPicture(registeredInfo);
                    }
                    realm.close();
                }
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
                        if (activityCallBinding.fcrImvBackground != null) {
                            activityCallBinding.fcrImvBackground.post(new Runnable() {
                                @Override
                                public void run() {
                                    G.imageLoader.displayImage(AndroidUtils.suitablePath(path), activityCallBinding.fcrImvBackground);
                                }
                            });
                        }
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

    private void muteMusic() {

        if (!isMuteAllMusic) {

            if (audioManager == null) {
                return;
            }
            int result = audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {

                }
            }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                musicVolum = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                isMuteAllMusic = true;
            }
        }
    }

    private void unMuteMusic() {

        if (isMuteAllMusic) {

            if (audioManager != null) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicVolum, 0);
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                isMuteAllMusic = false;
            }
        }
    }

    public void playRingtone() {
        boolean canPlay = false;


        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                canPlay = false;
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                canPlay = false;

                vibrator = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {0, 100, 1000};
                vibrator.vibrate(pattern, 0);

                break;
            case AudioManager.RINGER_MODE_NORMAL:
                canPlay = true;
                break;
        }

        if (audioManager.isWiredHeadsetOn()) {
            canPlay = true;
        }

        if (canPlay) {

            try {
                Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                String path = null;

                try {
                    path = AttachFile.getFilePathFromUri(alert);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }


                ringtonePlayer = new MediaPlayer();

                if (path == null) {
                    ringtonePlayer.setDataSource(G.context, Uri.parse("android.resource://" + G.context.getPackageName() + "/" + R.raw.tone));
                } else {
                    ringtonePlayer.setDataSource(G.context, alert);
                }

                if (audioManager.isWiredHeadsetOn()) {
                    ringtonePlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                } else {
                    ringtonePlayer.setAudioStreamType(AudioManager.STREAM_RING);
                }

                ringtonePlayer.setLooping(true);
                ringtonePlayer.prepare();
                ringtonePlayer.start();
            } catch (Exception e) {
                HelperLog.setErrorLog("activity call view model   set ringtone uri  " + e);
            }
        }

        startRingAnimation();
    }

    private void playSound(final int resSound) {

        try {
            if (cllBackBtnSpeaker.get().equals(G.context.getResources().getString(R.string.md_unMuted))) {
                if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING)
                    setSpeakerphoneOn(true);
                else {
                    cllBackBtnSpeaker.set(G.context.getResources().getString(R.string.md_Mute));
                    setSpeakerphoneOn(false);
                }
            } else {
                setSpeakerphoneOn(false);
            }

        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        if (player == null) {
            try {
                player = new MediaPlayer();
                player.setDataSource(context, Uri.parse("android.resource://" + G.context.getPackageName() + "/" + resSound));

                //if (audioManager.isWiredHeadsetOn()) {
                //    player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                //} else {
                //   player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                //}
                player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

                player.setLooping(true);
                player.prepare();
                player.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            try {
                player.reset();
                player.setDataSource(context, Uri.parse("android.resource://" + G.context.getPackageName() + "/" + resSound));

                //if (audioManager.isWiredHeadsetOn()) {
                //    player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                //} else {
                //    player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                //}
                player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

                player.prepare();
                player.setLooping(true);
                player.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cancelRingtone() {

        try {
            if (ringtonePlayer != null) {
                ringtonePlayer.stop();
                ringtonePlayer.release();
                ringtonePlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            if (vibrator != null) {
                vibrator.cancel();
                vibrator = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (player != null) {
                if (player.isPlaying()) {
                    player.stop();
                }

                player.release();
                player = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        stopRingAnimation();
    }

    private void startRingAnimation() {

        final int start = 1600;
        final int duration = 700;

        final Animation animation1 = new TranslateAnimation(0, 0, 0, -G.context.getResources().getDimension(R.dimen.dp32));
        animation1.setStartOffset(start);
        animation1.setDuration(duration);
        //animation1.setRepeatMode(Animation.RESTART);
        //animation1.setRepeatCount(Animation.INFINITE);
        animation1.setInterpolator(new BounceInterpolator());

        final Animation animation2 = new TranslateAnimation(0, 0, -G.context.getResources().getDimension(R.dimen.dp32), 0);
        animation2.setDuration(duration);
        animation2.setInterpolator(new BounceInterpolator());

        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                activityCallBinding.fcrBtnCall.startAnimation(animation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                activityCallBinding.fcrBtnCall.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activityCallBinding.fcrBtnCall.startAnimation(animation1);
                    }
                }, start);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        activityCallBinding.fcrBtnCall.startAnimation(animation1);

    }

    private void stopRingAnimation() {

        try {

            if (activityCallBinding.fcrBtnCall != null) {
                activityCallBinding.fcrBtnCall.clearAnimation();
            }

        } catch (Exception e) {

            Log.e("debug", "activityCall     stopRingAnimation      " + e.toString());
        }


    }

    //*****************************  distance sensor  **********************************************************

    public void onDestroy() {

        G.isInCall = false;
        G.iSignalingCallBack = null;
        G.onCallLeaveView = null;

        setSpeakerphoneOn(false);

        cancelRingtone();
        unMuteMusic();
        new RequestSignalingGetLog().signalingGetLog(0, 1);
        if (!isSendLeave) {
            WebRTC.getInstance().leaveCall();
        }
    }

    public void onLeaveView(String type) {
        isConnected = false;

        if (type.equals("error")) {

            //G.handler(new Runnable() {
            //    @Override
            //    public void run() {
            cancelRingtone();
            txtAviVisibility.set(View.GONE);
            callBackTxtStatus.set("");
            //    }
            //});

            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    endVoiceAndFinish();
                }
            }, 2000);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    endVoiceAndFinish();
                }
            }, 1000);
        }
    }

    @Override
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        Log.i("#peymanProxy", "Activity call view model");

    }

    @Override
    public void onServiceDisconnected(int profile) {

    }


    //***************************************************************************************

    class HeadsetPluginReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {

                // if you need ti dermine plugin state

               /* int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.d("dddddd", "Headset is unplugged");
                        break;
                    case 1:
                        Log.d("dddddd", "Headset is plugged");
                        break;
                    default:
                        Log.d("dddddd", "I have no idea what the headset state is");
                }

              */

                if (ringtonePlayer != null && ringtonePlayer.isPlaying()) {
                    cancelRingtone();
                    playRingtone();
                }
            }
        }
    }

}
