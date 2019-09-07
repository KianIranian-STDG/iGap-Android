/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintSet;
import android.support.transition.TransitionManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.ActivityCallBinding;
import net.iGap.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.eventbus.EventManager;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.PermissionHelper;
import net.iGap.interfaces.OnCallLeaveView;
import net.iGap.interfaces.OnVideoCallFrame;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.viewmodel.ActivityCallViewModel;
import net.iGap.webrtc.WebRTC;

import org.webrtc.EglBase;
import org.webrtc.VideoFrame;

import java.util.ArrayList;
import java.util.List;

import static android.bluetooth.BluetoothProfile.HEADSET;

public class ActivityCall extends ActivityEnhanced implements OnCallLeaveView, OnVideoCallFrame, BluetoothProfile.ServiceListener {

    public static final int CALL_EVENT = 77;
    public static final String CALL_TIMER_BROADCAST = "CALL_TIMER_BROADCAST";
    public static final String TIMER_TEXT = "timer";
    public static final String USER_ID_STR = "USER_ID";
    public static final String INCOMING_CALL_STR = "INCOMING_CALL_STR";
    public static final String CALL_TYPE = "CALL_TYPE";
    private static final int SENSOR_SENSITIVITY = 4;

    //public static TextView txtTimeChat, txtTimerMain;
    public static boolean allowOpenCall = true;
    public static boolean isGoingfromApp = false;
    public static View stripLayoutChat;
    public static View stripLayoutMain;
    public static boolean isNearDistance = false;
    public static OnFinishActivity onFinishActivity;

    private MediaPlayer player;
    private MediaPlayer ringtonePlayer;
    private SensorEventListener sensorEventListener;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private AudioManager audioManager;
    private Vibrator vibrator;
    private ActivityCallViewModel viewModel;
    private ActivityCallBinding binding;

    private int frameWidth;
    private int frameHeight;
    private int rotateFrame;
    private int phoneWidth;
    private int phoneHeight;

    private boolean isRotated = false;
    private boolean isFrameChange = true;
    private boolean isVerticalOrient = true;
    private boolean isFirst = true;
    private LocalBroadcastManager localBroadcastManager;
    private Observer<String> timerObserver;
    private int musicVolume = 0;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                switch (intent.getAction()) {
                    case Intent.ACTION_HEADSET_PLUG:
                        int state = intent.getIntExtra("state", -1);
                        switch (state) {
                            case 0:
                                audioManager.setSpeakerphoneOn(true);
                                viewModel.setHandsFreeConnected(false);
                                break;
                            case 1:
                                audioManager.setSpeakerphoneOn(false);
                                viewModel.setHandsFreeConnected(true);
                                break;
                            default:
                                audioManager.setSpeakerphoneOn(true);
                                viewModel.setHandsFreeConnected(false);
                        }
                        if (ringtonePlayer != null && ringtonePlayer.isPlaying()) {
                            cancelRingtone();
                            playRingtone();
                        }
                        break;
                    case BluetoothDevice.ACTION_FOUND:
                        //Device found
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        //Device is now connected
                        audioManager.setSpeakerphoneOn(false);
                        viewModel.setBluetoothConnected(true);
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        //Done searching
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:
                        //Device is about to disconnect
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        //Device has disconnected
                        audioManager.setSpeakerphoneOn(true);
                        break;
                }
/*        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    G.isBluetoothConnected = true;

                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                    G.isBluetoothConnected = false;
                } else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    G.isBluetoothConnected = false;
                } else {
                    G.isBluetoothConnected = false;
                }

            }

            // Bluetooth is disconnected, do handling here
        }*/
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HelperTracker.sendTracker(HelperTracker.TRACKER_CALL_PAGE);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        canSetUserStatus = false;

        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON | LayoutParams.FLAG_DISMISS_KEYGUARD | LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_TURN_SCREEN_ON);

        binding = DataBindingUtil.setContentView(ActivityCall.this, R.layout.activity_call);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ActivityCallViewModel(getIntent().getExtras().getLong(USER_ID_STR), getIntent().getExtras().getBoolean(INCOMING_CALL_STR), (ProtoSignalingOffer.SignalingOffer.Type) getIntent().getExtras().getSerializable(CALL_TYPE));
            }
        }).get(ActivityCallViewModel.class);
        binding.setActivityCallViewModel(viewModel);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            phoneHeight = displayMetrics.heightPixels;
            phoneWidth = displayMetrics.widthPixels;
        } else {
            phoneHeight = displayMetrics.widthPixels;
            phoneWidth = displayMetrics.heightPixels;
        }

        if (savedInstanceState == null) {
            viewModel.setHandsFreeConnected(audioManager.isWiredHeadsetOn());
            if (mBluetoothAdapter != null) {
                if (mBluetoothAdapter.getProfileConnectionState(HEADSET) == BluetoothAdapter.STATE_CONNECTED) {
                    viewModel.setBluetoothConnected(true);
                    audioManager.setSpeakerphoneOn(false);
                } else {
                    viewModel.setBluetoothConnected(false);
                    if (viewModel.isVideoCall())
                        audioManager.setSpeakerphoneOn(true);
                }
            }
            if (isGoingfromApp) {
                isGoingfromApp = false;
            } else {
                EventManager.getInstance().postEvent(ActivityCall.CALL_EVENT, false);
                G.isInCall = false;
                Intent intent = new Intent(this, ActivityMain.class);
                startActivity(intent);
                finish();
                return;
            }
        }

        G.isInCall = true;
        viewModel.callTimerListener.postValue("");
        EventManager.getInstance().postEvent(ActivityCall.CALL_EVENT, true);
        ActivityCall.allowOpenCall = true;

        PermissionHelper permissionHelper = new PermissionHelper(this);
        if (viewModel.isVideoCall()) {
            if (permissionHelper.grantCameraAndVoicePermission()) {
                init();
            }
        } else {
            if (permissionHelper.grantVoicePermission()) {
                init();
            }
        }

        registerSensor();

        onFinishActivity = () -> {
            try {
                if (viewModel.isVideoCall()) {
                    binding.fcrSurfacePeer.release();
                    binding.fcrSurfaceRemote.release();
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            finish();
        };

        viewModel.getQuickDeclineMessageLiveData().observe(this, userId -> {
            if (userId != null) {
                List<Integer> strings = new ArrayList<>();

                strings.add(R.string.message_decline_please_text_me);
                strings.add(R.string.message_decline_Please_call_later);
                strings.add(R.string.message_decline_call_later);
                strings.add(R.string.message_decline_write_new);

                new BottomSheetFragment().setListDataWithResourceId(this, strings, -1, position -> {
                    viewModel.endCall();
                    if (position == 3) {
                        HelperPublicMethod.goToChatRoom(userId, null, null);
                    } else {
                        HelperPublicMethod.goToChatRoomWithMessage(userId, this.getString(strings.get(position)), null, null);
                    }
                }).show(getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);

        registerReceiver(broadcastReceiver, filter);

        if (viewModel.isVideoCall()) {
            G.onVideoCallFrame = ActivityCall.this;
            if (!G.isCalling) {
                WebRTC.getInstance().startVideoCapture();
                WebRTC.getInstance().unMuteSound();
            }
        }
        mSensorManager.registerListener(sensorEventListener, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (viewModel.isVideoCall()) {
            WebRTC.getInstance().pauseVideoCapture();
        }
        G.onVideoCallFrame = null;
        mSensorManager.unregisterListener(sensorEventListener);
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        rotateScreen(frameWidth, frameHeight);
        /*rotatePeer();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (viewModel != null) {
            viewModel.callTimerListener.removeObserver(timerObserver);
        }


        if (player != null) {
            if (player.isPlaying())
                player.stop();
            player.release();
        }

        if (ringtonePlayer != null) {
            if (ringtonePlayer.isPlaying()) {
                ringtonePlayer.stop();
            }
            ringtonePlayer.release();
        }

        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
    }

    @Override
    public void onBackPressed() throws IllegalStateException {
        startActivity(new Intent(ActivityCall.this, ActivityMain.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean tmp = true;
        for (int grantResult : grantResults) {
            tmp = tmp && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (tmp) {
            init();
        } else {
            viewModel.leaveCall();
            finish();
        }
    }

    private void init() {
        if (!ActivityCall.allowOpenCall) {
            G.isInCall = false;
            finish();
        }

        G.onCallLeaveView = ActivityCall.this;
        G.onHoldBackgroundChanegeListener = isHold -> runOnUiThread(() -> {
            if (isHold) {
                binding.fcrSurfaceRemote.setVisibility(View.INVISIBLE);
                binding.fcrImvBackground.setVisibility(View.VISIBLE);
            } else {
                binding.fcrImvBackground.setVisibility(View.GONE);
                binding.fcrSurfaceRemote.setVisibility(View.VISIBLE);
            }
        });
        viewModel.changeViewState.observe(this, isIncomingCall -> {
            if (isIncomingCall != null) {
                ConstraintSet set = new ConstraintSet();
                set.clone(binding.acLayoutCallRoot);
                if (isIncomingCall) {
                    set.connect(binding.fcrBtnEnd.getId(), ConstraintSet.BOTTOM, 0, ConstraintSet.BOTTOM);
                    set.connect(binding.fcrBtnEnd.getId(), ConstraintSet.TOP, binding.fcrBtnChat.getId(), ConstraintSet.BOTTOM);
                    set.connect(binding.fcrBtnChat.getId(), ConstraintSet.BOTTOM, binding.fcrBtnEnd.getId(), ConstraintSet.TOP);
                    set.connect(binding.fcrBtnChat.getId(), ConstraintSet.TOP, binding.callInfo.getId(), ConstraintSet.BOTTOM);
                    set.connect(binding.callInfo.getId(), ConstraintSet.BOTTOM, binding.fcrBtnChat.getId(), ConstraintSet.TOP);
                } else {
                    set.connect(binding.fcrBtnEnd.getId(), ConstraintSet.BOTTOM, binding.fcrBtnChat.getId(), ConstraintSet.TOP);
                    set.connect(binding.fcrBtnEnd.getId(), ConstraintSet.TOP, binding.callInfo.getId(), ConstraintSet.BOTTOM);
                    set.connect(binding.fcrBtnChat.getId(), ConstraintSet.BOTTOM, 0, ConstraintSet.BOTTOM);
                    set.connect(binding.fcrBtnChat.getId(), ConstraintSet.TOP, binding.fcrBtnEnd.getId(), ConstraintSet.BOTTOM);
                    set.connect(binding.callInfo.getId(), ConstraintSet.BOTTOM, binding.fcrBtnEnd.getId(), ConstraintSet.TOP);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(binding.acLayoutCallRoot);
                    set.applyTo(binding.acLayoutCallRoot);
                } else {
                    set.applyTo(binding.acLayoutCallRoot);
                }
            }
        });
        viewModel.isMuteMusic.observe(this, isMutedAllMusic -> {
            if (isMutedAllMusic != null) {
                if (isMutedAllMusic) {
                    muteMusic();
                } else {
                    unMuteMusic();
                }
            }
        });
        viewModel.playRingTone.observe(this, isPlayRingTone -> {
            if (isPlayRingTone != null) {
                if (isPlayRingTone) {
                    playRingtone();
                } else {
                    cancelRingtone();
                }
            }
        });
        viewModel.imagePath.observe(this, imagePath -> {
            if (imagePath != null) {
                G.imageLoader.displayImage(AndroidUtils.suitablePath(imagePath), binding.fcrImvBackground);
                G.imageLoader.displayImage(AndroidUtils.suitablePath(imagePath), binding.callingUserImage);
            }
        });
        viewModel.initialVideoCallSurface.observe(this, isInit -> {
            if (isInit != null && isInit) {
                EglBase rootEglBase = EglBase.create();
                binding.fcrSurfacePeer.init(rootEglBase.getEglBaseContext(), null);
                binding.fcrSurfacePeer.setEnableHardwareScaler(true);
                binding.fcrSurfacePeer.setMirror(true);
                binding.fcrSurfacePeer.setZOrderMediaOverlay(true);
                binding.fcrSurfacePeer.setZOrderOnTop(true);

                binding.fcrSurfaceRemote.init(rootEglBase.getEglBaseContext(), null);
                binding.fcrSurfaceRemote.setEnableHardwareScaler(true);
                binding.fcrSurfaceRemote.setMirror(false);
            }
        });
        viewModel.showDialogChangeConnectedDevice.observe(this, isShow -> {
            if (isShow != null && isShow) {
                changeConnectedDevice();
            }
        });
        viewModel.playSound.observe(this, soundRes -> {
            Log.wtf(this.getClass().getName(), "playSound");
            if (soundRes != null) {
                playSound(soundRes);
            }
        });
        viewModel.setAudioManagerSpeakerphoneOn.observe(this, on -> {
            if (on != null) {
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
        });
        viewModel.setAudioManagerWithBluetooth.observe(this, state -> {
            if (state != null) {
                if (state) {
                    audioManager.setMode(0);
                    audioManager.startBluetoothSco();
                    audioManager.setBluetoothScoOn(true);
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                } else {
                    audioManager.setMode(AudioManager.MODE_INVALID);
                    audioManager.setBluetoothScoOn(false);
                    audioManager.stopBluetoothSco();
                }
            }
        });

        timerObserver = time -> {
            if (time == null) return;
            Intent intent = new Intent(CALL_TIMER_BROADCAST);
            intent.putExtra(TIMER_TEXT, time);
            localBroadcastManager.sendBroadcast(intent);
        };

        viewModel.callTimerListener.observeForever(timerObserver);
    }

    //***************************************************************************************

    @Override
    public void onLeaveView(String type) {
        Log.wtf(this.getClass().getName(), "onLeaveView");
        if (viewModel != null) {
            viewModel.onLeaveView(type);
        }
    }

    //*****************************  distance sensor  **********************************************************

    private void registerSensor() {

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (binding != null) {
                    if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                        boolean newIsNear = Math.abs(event.values[0]) < Math.min(event.sensor.getMaximumRange(), 3);
                        if (newIsNear != isNearDistance) {
                            isNearDistance = newIsNear;
                            if (isNearDistance) {
                                // near
                                screenOff();
                            } else {
                                //far
                                screenOn();
                            }
                        }
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    private void screenOn() {

        PowerManager.WakeLock wakeLock;
        int field = 0x00000020;
        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(field, getLocalClassName());

        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        /*LayoutParams params = this.getWindow().getAttributes();
        params.screenBrightness = 1;
        this.getWindow().setAttributes(params);*/
        enableDisableViewGroup(binding.acLayoutCallRoot, true);
    }

    private void screenOff() {
        if (ActivityCallViewModel.isConnected) {
            PowerManager.WakeLock wakeLock;
            int field = 0x00000020;
            wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(field, getLocalClassName());

            if (!wakeLock.isHeld()) {
                wakeLock.acquire();
            }

            /*LayoutParams params = this.getWindow().getAttributes();
            params.screenBrightness = 0;
            this.getWindow().setAttributes(params);*/
            enableDisableViewGroup(binding.acLayoutCallRoot, false);
        }
    }

    private void muteMusic() {
        if (audioManager != null) {
            int result = audioManager.requestAudioFocus(focusChange -> {

            }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                musicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            }
        }
    }

    private void unMuteMusic() {
        if (audioManager != null) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicVolume, 0);
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
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

                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
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

                if (ringtonePlayer == null) {
                    ringtonePlayer = new MediaPlayer();
                } else {
                    if (ringtonePlayer.isPlaying()) {
                        ringtonePlayer.stop();
                        ringtonePlayer.reset();
                    }
                }

                if (path == null) {
                    ringtonePlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tone));
                } else {
                    ringtonePlayer.setDataSource(this, alert);
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
                HelperLog.setErrorLog(e);
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
    }

    private void playSound(final int resSound) {
        if (player == null) {
            player = new MediaPlayer();
        } else {
            player.reset();
        }
        try {
            player.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + resSound));
            player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            player.setLooping(true);
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeConnectedDevice() {
        new MaterialDialog.Builder(this).title(R.string.switchTo).itemsGravity(GravityEnum.CENTER).items(R.array.phone_selection).negativeText(getString(R.string.B_cancel)).itemsCallback((dialog, itemView, position, text) -> {
            viewModel.chooseDevice(position);
        }).show();
    }


    //todo: remove it because have land layout
    /*private void rotatePeer() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            FrameLayout.LayoutParams
                    params = new FrameLayout.LayoutParams(ViewMaker.dpToPixel(100), ViewMaker.dpToPixel(140));
            binding.fcrSurfacePeer.setLayoutParams(params);
            params.gravity = Gravity.TOP | Gravity.RIGHT;

        } else {
            FrameLayout.LayoutParams
                    params = new FrameLayout.LayoutParams(ViewMaker.dpToPixel(140), ViewMaker.dpToPixel(100));
            binding.fcrSurfacePeer.setLayoutParams(params);
            params.gravity = Gravity.TOP | Gravity.RIGHT;
        }
    }*/

    //***************************************************************************************

    @Override
    public void onRemoteFrame(VideoFrame videoFrame) {
        binding.fcrSurfaceRemote.onFrame(videoFrame);
        if (isFrameChange) {
            frameWidth = videoFrame.getRotatedWidth();
            frameHeight = videoFrame.getRotatedHeight();
            rotateFrame = videoFrame.getRotation();
            isFrameChange = false;
        }

        if (isFirst) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rotateScreen(videoFrame.getRotatedWidth(), videoFrame.getRotatedHeight());
                }
            });

            isFirst = false;
        }

        if (rotateFrame != videoFrame.getRotation()) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isVerticalOrient = true;
                    rotateScreen(videoFrame.getRotatedWidth(), videoFrame.getRotatedHeight());
                }
            });
            isFrameChange = true;
        }
    }

    public void rotateScreen(int frameWidth, int frameHeight) {

        float dpWidth = phoneWidth / ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        /*float dpHeight = phoneHeight / ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);*/
        float dpFrameHeight = frameHeight / ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        float dpFrameWidth = frameWidth / ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(phoneWidth, (int) (frameHeight * (dpWidth / dpFrameWidth)));
            binding.fcrSurfaceRemote.setLayoutParams(params);
            params.gravity = Gravity.CENTER;

        } else {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (frameWidth * (dpWidth / dpFrameHeight)), phoneWidth);
            binding.fcrSurfaceRemote.setLayoutParams(params);
            params.gravity = Gravity.CENTER;
        }
    }

    @Override
    public void onPeerFrame(VideoFrame videoFrame) {
        binding.fcrSurfacePeer.onFrame(videoFrame);
    }

    @Override
    public void onServiceConnected(int profile, BluetoothProfile proxy) {

    }

    @Override
    public void onServiceDisconnected(int profile) {

    }

    public void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        if (viewGroup != null) {
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = viewGroup.getChildAt(i);
                view.setEnabled(enabled);
                if (view instanceof ViewGroup) {
                    enableDisableViewGroup((ViewGroup) view, enabled);
                }
            }
        }
    }

    //***************************************************************************************

    public interface OnFinishActivity {
        void finishActivity();
    }
}