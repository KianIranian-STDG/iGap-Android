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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.databinding.ActivityCallBinding;
import net.iGap.helper.HelperPermission;
import net.iGap.interfaces.OnCallLeaveView;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnHoldBackgroundChanegeListener;
import net.iGap.interfaces.OnVideoCallFrame;
import net.iGap.interfaces.VideoCallListener;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.audioManagement.BluethoothIntentReceiver;
import net.iGap.module.audioManagement.MusicIntentReceiver;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.viewmodel.ActivityCallViewModel;
import net.iGap.webrtc.WebRTC;

import org.webrtc.EglBase;
import org.webrtc.VideoFrame;

import java.io.IOException;


import static android.bluetooth.BluetoothProfile.HEADSET;

public class ActivityCall extends ActivityEnhanced implements OnCallLeaveView, OnVideoCallFrame, BluetoothProfile.ServiceListener {

    public static final String USER_ID_STR = "USER_ID";
    public static final String INCOMING_CALL_STR = "INCOMING_CALL_STR";
    public static final String CALL_TYPE = "CALL_TYPE";
    private static final int SENSOR_SENSITIVITY = 4;

    //public static TextView txtTimeChat, txtTimerMain;
    public static boolean isGoingfromApp = false;
    public static View stripLayoutChat;
    public static View stripLayoutMain;
    public static boolean isNearDistance = false;
    public static OnFinishActivity onFinishActivity;
    boolean isIncomingCall = false;
    long userId;
    boolean canClick = false;
    boolean canTouch = false;
    boolean down = false;
    AppCompatButton btnEndCall;
    AppCompatButton btnAnswer;
    MediaPlayer player;
    MediaPlayer ringtonePlayer;
    SensorEventListener sensorEventListener;
    HeadsetPluginReciver headsetPluginReciver;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private ActivityCallViewModel activityCallViewModel;
    private ActivityCallBinding activityCallBinding;
    private ProtoSignalingOffer.SignalingOffer.Type callTYpe;

    private int frameWidth;
    private int frameHeight;
    private int rotateFrame;
    private int phoneWidth;
    private int phoneHeight;
    private float screenScale;
    private boolean isRotated = false;
    private boolean isFrameChange = true;
    private boolean isVerticalOrient = true;
    private boolean isFirst = true;
    private boolean isHiddenButtons = false;


    /**
     * Enables/Disables all child views in a view group.
     *
     * @param viewGroup the view group
     * @param enabled   <code>true</code> to enable, <code>false</code> to disable
     *                  the views.
     */
    public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (G.speakerControlListener != null) {
            G.speakerControlListener = null;
        }

        if (activityCallViewModel != null) {
            activityCallViewModel.onDestroy();

        }
        if (G.onHoldBackgroundChanegeListener != null) {
            G.onHoldBackgroundChanegeListener = null;
        }
    }

    @Override
    public void onBackPressed() throws IllegalStateException {
        startActivity(new Intent(ActivityCall.this, ActivityMain.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON | LayoutParams.FLAG_DISMISS_KEYGUARD | LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_TURN_SCREEN_ON);

        /** register receiver for headset*/
        registerReceiver(new MusicIntentReceiver(), new IntentFilter(Intent.ACTION_HEADSET_PLUG));


        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        registerReceiver(new BluethoothIntentReceiver(), filter);


        //  registerReceiver(new BluethoothIntentReceiver(), new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));


        /** First Check Is Headset Connected or Not */
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isWiredHeadsetOn()) {
            G.isHandsFreeConnected = true;
        }


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth

        } else {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (mBluetoothAdapter.getProfileConnectionState(HEADSET) == BluetoothAdapter.STATE_CONNECTED) {
                G.isBluetoothConnected = true;
                am.setSpeakerphoneOn(false);
            } else {
                G.isBluetoothConnected = false;
                if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING)
                    am.setSpeakerphoneOn(true);
            }
        }

        super.onCreate(savedInstanceState);

        /** to get in pixel
         DisplayMetrics displayMetrics = new DisplayMetrics();
         getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
         int height = displayMetrics.heightPixels;
         int width = displayMetrics.widthPixels;*/
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            phoneHeight = displayMetrics.heightPixels;
            phoneWidth = displayMetrics.widthPixels;
        } else {
            phoneHeight = displayMetrics.widthPixels;
            phoneWidth = displayMetrics.heightPixels;
        }

        if (isGoingfromApp) {
            isGoingfromApp = false;
        } else {

            G.isInCall = false;

            Intent intent = new Intent(this, ActivityMain.class);
            startActivity(intent);
            finish();
            return;
        }

        G.isInCall = true;

        userId = getIntent().getExtras().getLong(USER_ID_STR);
        isIncomingCall = getIntent().getExtras().getBoolean(INCOMING_CALL_STR);
        callTYpe = (ProtoSignalingOffer.SignalingOffer.Type) getIntent().getExtras().getSerializable(CALL_TYPE);


        try {
            HelperPermission.getMicroPhonePermission(this, new OnGetPermission() {
                @Override
                public void Allow() throws IOException {

                    if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {

                        HelperPermission.getCameraPermission(ActivityCall.this, new OnGetPermission() {
                            @Override
                            public void Allow() throws IOException {
                                init();
                         /*       G.onRejectCallStatus = new OnRejectCallStatus() {
                                    @Override
                                    public void setReject(boolean state) {
                                        if (state)
                                            doReject();
                                    }
                                };*/
                            }

                            @Override
                            public void deny() {
                                G.isInCall = false;
                                finish();
                                if (isIncomingCall) {
                                    WebRTC.getInstance().leaveCall();
                                }
                            }
                        });

                    } else {
                        init();


                    }
                }

                @Override
                public void deny() {
                    G.isInCall = false;
                    finish();
                    if (isIncomingCall) {
                        WebRTC.getInstance().leaveCall();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        registerSensor();

        headsetPluginReciver = new HeadsetPluginReciver();

        onFinishActivity = new OnFinishActivity() {
            @Override
            public void finishActivity() {

                try {
                    if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
                        activityCallBinding.fcrSurfacePeer.release();
                        activityCallBinding.fcrSurfaceRemote.release();
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }

                finish();
            }
        };
    }

    private void init() {
        WebRTC.getInstance().setCallType(callTYpe);
        //setContentView(R.layout.activity_call);
        activityCallBinding = DataBindingUtil.setContentView(ActivityCall.this, R.layout.activity_call);
        activityCallViewModel = new ActivityCallViewModel(ActivityCall.this, userId, isIncomingCall, activityCallBinding, callTYpe);
        activityCallBinding.setActivityCallViewModel(activityCallViewModel);
        initComponent();
        //initCallBack();
        G.onCallLeaveView = ActivityCall.this;
        if (!isIncomingCall) {
            WebRTC.getInstance().createOffer(userId);
        }
    }

    //***************************************************************************************

    @Override
    public void onLeaveView(String type) {
        if (activityCallViewModel != null) {
            activityCallViewModel.onLeaveView(type);
        }
    }

    private void initComponent() {

        if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
            EglBase rootEglBase = EglBase.create();
            activityCallBinding.fcrSurfacePeer.init(rootEglBase.getEglBaseContext(), null);
            activityCallBinding.fcrSurfacePeer.setEnableHardwareScaler(true);
            activityCallBinding.fcrSurfacePeer.setMirror(true);
            activityCallBinding.fcrSurfacePeer.setZOrderMediaOverlay(true);
            activityCallBinding.fcrSurfacePeer.setZOrderOnTop(true);
            activityCallBinding.fcrSurfacePeer.setVisibility(View.VISIBLE);

            activityCallBinding.fcrSurfaceRemote.init(rootEglBase.getEglBaseContext(), null);
            activityCallBinding.fcrSurfaceRemote.setEnableHardwareScaler(true);
            activityCallBinding.fcrSurfaceRemote.setMirror(false);
            activityCallBinding.fcrSurfaceRemote.setVisibility(View.VISIBLE);

            activityCallBinding.fcrImvBackground.setVisibility(View.VISIBLE);
            /*activityCallBinding.fcrTxtCallType.setText(getResources().getString(R.string.video_calls));*/
            /*activityCallBinding.fcrTxtCallType.setShadowLayer(10, 0, 3, Color.BLACK);*/
            activityCallBinding.fcrBtnSwichCamera.setVisibility(View.VISIBLE);

            G.videoCallListener = () -> {
                // activityCallBinding.fcrSurfaceRemote.setVisibility(View.VISIBLE);
                runOnUiThread(() -> {
                    try {
                        if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
                            activityCallBinding.fcrImvBackground.setVisibility(View.GONE);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });

            };

            try {
                activityCallBinding.fcrSurfaceRemote.setOnClickListener(v -> {
                    if (G.isWebRtcConnected) {

                        if (!isHiddenButtons) {
                            activityCallBinding.callingUserImage.setVisibility(View.INVISIBLE);
                            activityCallBinding.fcrBtnChat.setVisibility(View.INVISIBLE);
                            activityCallBinding.fcrBtnSpeaker.setVisibility(View.INVISIBLE);
                            activityCallBinding.fcrBtnEnd.setVisibility(View.INVISIBLE);
                            activityCallBinding.fcrBtnChat.setVisibility(View.INVISIBLE);
                            activityCallBinding.fcrBtnMic.setVisibility(View.INVISIBLE);
                            activityCallBinding.fcrBtnSwichCamera.setVisibility(View.INVISIBLE);
                            activityCallBinding.t2.setVisibility(View.INVISIBLE);
                            activityCallBinding.t3.setVisibility(View.INVISIBLE);
                            activityCallBinding.t4.setVisibility(View.INVISIBLE);
                            activityCallBinding.callInfo.setVisibility(View.INVISIBLE);

                            isHiddenButtons = true;
                        } else {
                            activityCallBinding.callingUserImage.setVisibility(View.VISIBLE);
                            activityCallBinding.fcrBtnChat.setVisibility(View.VISIBLE);
                            activityCallBinding.fcrBtnSpeaker.setVisibility(View.VISIBLE);
                            activityCallBinding.fcrBtnEnd.setVisibility(View.VISIBLE);
                            activityCallBinding.fcrBtnChat.setVisibility(View.VISIBLE);
                            activityCallBinding.fcrBtnMic.setVisibility(View.VISIBLE);
                            activityCallBinding.fcrBtnSwichCamera.setVisibility(View.VISIBLE);
                            activityCallBinding.t2.setVisibility(View.VISIBLE);
                            activityCallBinding.t3.setVisibility(View.VISIBLE);
                            activityCallBinding.t4.setVisibility(View.VISIBLE);
                            activityCallBinding.callInfo.setVisibility(View.VISIBLE);
                            isHiddenButtons = false;
                        }

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            activityCallBinding.fcrBtnSwichCamera.setVisibility(View.GONE);
            activityCallBinding.t4.setVisibility(View.GONE);
        }

        /**
         * *************** layoutCallEnd ***************
         */

        btnEndCall = activityCallBinding.fcrBtnEnd;
        if (isIncomingCall) {
            btnEndCall.setOnClickListener(v -> {
                /*if (canClick) {*/
                activityCallViewModel.endCall();
                /*}*/
            });
        } else {
            btnEndCall.setOnClickListener(v -> {
                activityCallViewModel.endCall();
                btnEndCall.setVisibility(View.INVISIBLE);
            });
        }

        /**
         * *************** layoutChat ***************
         */

        if (isIncomingCall) {
            activityCallBinding.fcrBtnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if (canClick) {*/
                    activityCallViewModel.onClickBtnChat(v);
                    activityCallBinding.fcrBtnChat.setVisibility(View.INVISIBLE);
                    /*}*/
                }
            });
            activityCallBinding.acLayoutCallRoot.setBackgroundColor(getResources().getColor(R.color.incomingCallUserImageLayerEnd));
            activityCallBinding.fcrImvBackgroundLayer.setBackgroundResource(R.drawable.incoming_call_person_image_layer);
        } else {
            activityCallBinding.fcrImvBackgroundLayer.setBackgroundResource(R.drawable.calling_person_image_layer);
            activityCallBinding.acLayoutCallRoot.setBackgroundColor(getResources().getColor(R.color.callingUserImageLayerEnd));
        }

        /**
         * *************** layoutAnswer ***************
         */
        btnAnswer = activityCallBinding.fcrBtnCall;

        if (isIncomingCall) {
            btnAnswer.setOnClickListener(v -> {
                G.isWebRtcConnected = true;
                if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
                    activityCallBinding.fcrSurfaceRemote.setVisibility(View.VISIBLE);
                    activityCallBinding.fcrImvBackground.setVisibility(View.GONE);
                }
                G.isVideoCallRinging = false;
                answer();
                v.setVisibility(View.GONE);
                /*activityCallBinding.btnVideoCall.setVisibility(View.VISIBLE);
                activityCallBinding.btnAddPerson.setVisibility(View.VISIBLE);*/
            });
        }

        /**
         * *********************************************
         */

        G.onHoldBackgroundChanegeListener = new OnHoldBackgroundChanegeListener() {
            @Override
            public void notifyBakcgroundChanege(boolean isHold) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isHold) {
                            activityCallBinding.fcrSurfaceRemote.setVisibility(View.INVISIBLE);
                            activityCallBinding.fcrImvBackground.setVisibility(View.VISIBLE);
                        } else {
                            activityCallBinding.fcrImvBackground.setVisibility(View.GONE);
                            activityCallBinding.fcrSurfaceRemote.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        };
    }

    /**
     * *************** common methods ***************
     */

    private void answer() {
        WebRTC.getInstance().createAnswer();
        cancelRingtone();
        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityCallViewModel.endCall();
                btnEndCall.setVisibility(View.GONE);
            }
        });
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

            if (activityCallViewModel.vibrator != null) {
                activityCallViewModel.vibrator.cancel();
                activityCallViewModel.vibrator = null;
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

    //*****************************  distance sensor  **********************************************************

    private void registerSensor() {

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (activityCallBinding != null) {
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

        LayoutParams params = this.getWindow().getAttributes();

        params.screenBrightness = 1;
        this.getWindow().setAttributes(params);

        enableDisableViewGroup(activityCallBinding.acLayoutCallRoot, true);
    }

    private void screenOff() {

        if (ActivityCallViewModel.isConnected) {

            LayoutParams params = this.getWindow().getAttributes();

            params.screenBrightness = 0;
            this.getWindow().setAttributes(params);

            enableDisableViewGroup((ViewGroup) activityCallBinding.acLayoutCallRoot, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
            G.onVideoCallFrame = ActivityCall.this;
            if (!G.isCalling) {
                WebRTC.getInstance().startVideoCapture();
                WebRTC.getInstance().unMuteSound();
            }

        }

        mSensorManager.registerListener(sensorEventListener, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetPluginReciver, filter);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        rotateScreen(frameWidth, frameHeight);
        rotatePeer();
    }

    private void rotatePeer() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            FrameLayout.LayoutParams
                    params = new FrameLayout.LayoutParams(ViewMaker.dpToPixel(100), ViewMaker.dpToPixel(140));
            activityCallBinding.fcrSurfacePeer.setLayoutParams(params);
            params.gravity = Gravity.TOP | Gravity.RIGHT;

        } else {
            FrameLayout.LayoutParams
                    params = new FrameLayout.LayoutParams(ViewMaker.dpToPixel(140), ViewMaker.dpToPixel(100));
            activityCallBinding.fcrSurfacePeer.setLayoutParams(params);
            params.gravity = Gravity.TOP | Gravity.RIGHT;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
            WebRTC.getInstance().pauseVideoCapture();
        }
        G.onVideoCallFrame = null;
        mSensorManager.unregisterListener(sensorEventListener);
        unregisterReceiver(headsetPluginReciver);
    }

    //***************************************************************************************

    @Override
    public void onRemoteFrame(VideoFrame videoFrame) {
        activityCallBinding.fcrSurfaceRemote.onFrame(videoFrame);
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
            int height = 0;
            int width = 0;

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

        float dpWidth = (Integer) phoneWidth / ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        float dpHeight = phoneHeight / ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        float dpFrameHeight = frameHeight / ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        float dpFrameWidth = frameWidth / ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            FrameLayout.LayoutParams
                    params = new FrameLayout.LayoutParams(phoneWidth, (int) (frameHeight * (dpWidth / dpFrameWidth)));
            activityCallBinding.fcrSurfaceRemote.setLayoutParams(params);
            params.gravity = Gravity.CENTER;

        } else {
            FrameLayout.LayoutParams
                    params = new FrameLayout.LayoutParams((int) (frameWidth * (dpWidth / dpFrameHeight)), phoneWidth);
            activityCallBinding.fcrSurfaceRemote.setLayoutParams(params);
            params.gravity = Gravity.CENTER;


        }
    }


    @Override
    public void onPeerFrame(VideoFrame videoFrame) {
        activityCallBinding.fcrSurfacePeer.onFrame(videoFrame);
    }

    @Override
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        Log.i("#peymanProxy", "Activity call");
    }

    @Override
    public void onServiceDisconnected(int profile) {

    }

    //***************************************************************************************

    public interface OnFinishActivity {
        void finishActivity();
    }

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
                    activityCallViewModel.playRingtone();
                }
            }
        }
    }
}