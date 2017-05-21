/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/


package net.iGap.activities;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.wang.avi.AVLoadingIndicatorView;
import io.realm.Realm;
import java.io.IOException;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperPermision;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.interfaces.ISignalingCallBack;
import net.iGap.interfaces.OnCallLeaveView;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.module.AndroidUtils;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.enums.CallState;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.request.RequestSignalingGetLog;
import net.iGap.request.RequestUserInfo;
import net.iGap.webrtc.WebRTC;


public class ActivityCall extends ActivityEnhanced implements OnCallLeaveView {

    public static final String USER_ID_STR = "USER_ID";
    public static final String INCOMING_CALL_STR = "INCOMING_CALL_STR";
    boolean isIncomingCall = false;
    boolean canClick = false;
    boolean canTouch = false;
    boolean down = false;

    boolean isSendLeave = false;

    long userId;

    VerticalSwipe verticalSwipe;
    TextView txtName;
    TextView txtStatus;
    AVLoadingIndicatorView avLoadingIndicatorView;
    ImageView userCallerPicture;
    LinearLayout layoutCaller;
    LinearLayout layoutOption;
    MaterialDesignTextView btnCircleChat;
    MaterialDesignTextView btnEndCall;
    MaterialDesignTextView btnAnswer;
    MaterialDesignTextView btnMic;
    MaterialDesignTextView btnChat;
    MaterialDesignTextView btnSpeaker;
    MediaPlayer player;

    @Override protected void onPause() {
        super.onPause();

        G.isInCall = false;
        G.iSignalingCallBack = null;
        cancelRingtone();

        new RequestSignalingGetLog().signalingGetLog(0, 1);
    }

    @Override public void onBackPressed() {
        super.onBackPressed();

        if (!isSendLeave) {
            new WebRTC().leaveCall();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        try {
            HelperPermision.getMicroPhonePermission(this, new OnGetPermission() {
                @Override public void Allow() throws IOException {

                }

                @Override public void deny() {
                    finish();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        G.isInCall = true;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON | LayoutParams.FLAG_DISMISS_KEYGUARD | LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_TURN_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        userId = getIntent().getExtras().getLong(USER_ID_STR);
        isIncomingCall = getIntent().getExtras().getBoolean(INCOMING_CALL_STR);
        if (!isIncomingCall) {
            new WebRTC().createOffer(userId);
        }
        initComponent();
        initCallBack();

        G.onCallLeaveView = this;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        verticalSwipe.dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    //***************************************************************************************

    private void initCallBack() {
        G.iSignalingCallBack = new ISignalingCallBack() {
            @Override
            public void onStatusChanged(final CallState callState) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtStatus.setText(callState.toString());
                        if (callState == CallState.FINISHED) {
                            endVoiceAndFinish();
                        }
                    }
                });
            }
        };
    }

    private void initComponent() {

        findViewById(R.id.ac_layout_main_call).setBackgroundColor(Color.parseColor(G.appBarColor));

        verticalSwipe = new VerticalSwipe();
        txtName = (TextView) findViewById(R.id.fcr_txt_name);
        txtStatus = (TextView) findViewById(R.id.fcr_txt_status);
        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.fcr_txt_avi);
        userCallerPicture = (ImageView) findViewById(R.id.fcr_imv_background);
        layoutCaller = (LinearLayout) findViewById(R.id.fcr_layout_caller);
        layoutOption = (LinearLayout) findViewById(R.id.fcr_layout_option);


        txtStatus.setText(CallState.DIALLING.toString());

        /**
         * *************** layoutCallEnd ***************
         */

        final FrameLayout layoutCallEnd = (FrameLayout) findViewById(R.id.fcr_layout_chat_call_end);
        layoutCallEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (canClick) {
                    isSendLeave = true;
                    layoutCallEnd.setVisibility(View.INVISIBLE);
                    endCall();
                }


            }
        });

        btnEndCall = (MaterialDesignTextView) findViewById(R.id.fcr_btn_end);
        btnEndCall.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setUpSwap(layoutCallEnd);
                return false;
            }
        });

        /**
         * *************** layoutChat ***************
         */

        final FrameLayout layoutChat = (FrameLayout) findViewById(R.id.fcr_layout_chat_call);
        //layoutChat.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        if (canClick) {
        //            btnChat.performClick();
        //            layoutChat.setVisibility(View.INVISIBLE);
        //        }
        //    }
        //});
        //
        btnCircleChat = (MaterialDesignTextView) findViewById(R.id.fcr_btn_circle_chat);
        //btnCircleChat.setOnTouchListener(new View.OnTouchListener() {
        //    @Override
        //    public boolean onTouch(View v, MotionEvent event) {
        //        setUpSwap(layoutChat);
        //        return false;
        //    }
        //});

        btnCircleChat.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                btnChat.performClick();
                layoutChat.setVisibility(View.INVISIBLE);
            }
        });


        /**
         * *************** layoutAnswer ***************
         */

        final FrameLayout layoutAnswer = (FrameLayout) findViewById(R.id.fcr_layout_answer_call);
        layoutAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer(layoutAnswer, layoutChat);
            }
        });

        btnAnswer = (MaterialDesignTextView) findViewById(R.id.fcr_btn_call);
        btnAnswer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setUpSwap(layoutAnswer);
                return false;
            }
        });

        /**
         * *********************************************
         */

        btnChat = (MaterialDesignTextView) findViewById(R.id.fcr_btn_chat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperPublicMethod.goToChatRoom(userId, null, null);
                endVoiceAndFinish();
            }
        });

        btnSpeaker = (MaterialDesignTextView) findViewById(R.id.fcr_btn_speaker);
        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnSpeaker.getText().toString().equals(G.context.getResources().getString(R.string.md_muted))) {
                    btnSpeaker.setText(R.string.md_unMuted);
                } else {
                    btnSpeaker.setText(R.string.md_muted);
                }
            }
        });

        btnMic = (MaterialDesignTextView) findViewById(R.id.fcr_btn_mic);
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnMic.getText().toString().equals(G.context.getResources().getString(R.string.md_mic))) {
                    btnMic.setText(R.string.md_mic_off);
                } else {
                    btnMic.setText(R.string.md_mic);
                }
            }
        });

        if (isIncomingCall) {
            playRingtone();
        } else {
            layoutAnswer.setVisibility(View.GONE);
            layoutChat.setVisibility(View.GONE);
            layoutOption.setVisibility(View.VISIBLE);
        }

        setAnimation();
        setPicture();
    }

    /**
     * *************** common methods ***************
     */

    private void setUpSwap(View view) {
        if (!down) {
            verticalSwipe.setView(view);
            canTouch = true;
            down = true;
        }
    }

    private void setAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_enter_down_circke_button);
        layoutCaller.startAnimation(animation);
    }

    private void setPicture() {
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo registeredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();

        if (registeredInfo != null) {
            try {

                txtName.setText(registeredInfo.getDisplayName());

                RealmAttachment av = registeredInfo.getLastAvatar().getFile();

                ProtoFileDownload.FileDownload.Selector se = ProtoFileDownload.FileDownload.Selector.FILE;
                String dirPath = AndroidUtils.getFilePathWithCashId(av.getCacheId(), av.getName(), G.DIR_IMAGE_USER, false);

                HelperDownloadFile.startDownload(av.getToken(), av.getCacheId(), av.getName(), av.getSize(), se, dirPath, 4, new HelperDownloadFile.UpdateListener() {
                    @Override
                    public void OnProgress(final String path, int progress) {

                        if (progress == 100) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    G.imageLoader.displayImage(AndroidUtils.suitablePath(path), userCallerPicture);
                                }
                            });
                        }
                    }

                    @Override
                    public void OnError(String token) {

                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            new RequestUserInfo().userInfo(userId);
        }

        realm.close();
    }

    private void endVoiceAndFinish() {
        cancelRingtone();
        finish();
    }

    private void answer(FrameLayout layoutAnswer, FrameLayout layoutChat) {
        if (canClick) {
            layoutOption.setVisibility(View.VISIBLE);
            layoutAnswer.setVisibility(View.GONE);
            layoutChat.setVisibility(View.GONE);

            new WebRTC().createAnswer();
            cancelRingtone();
        }
    }

    private void endCall() {
        new WebRTC().leaveCall();
        endVoiceAndFinish();
    }

    private void playRingtone() {
        player = MediaPlayer.create(ActivityCall.this, R.raw.iphone_5_original);
        player.start();
    }

    private void cancelRingtone() {

        try {
            if (player != null) {
                if (player.isPlaying()) {
                    player.stop();
                }

                player.release();
                player = null;
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onLeaveView() {
        endVoiceAndFinish();
    }

    /**
     * ****************************** VerticalSwipe Class ******************************
     */

    class VerticalSwipe {

        private int AllMoving = 0;
        private int lastY;
        private int DistanceToAccept = (int) G.context.getResources().getDimension(R.dimen.dp120);
        boolean accept = false;
        private View view;

        public void setView(View view) {
            this.view = view;
        }

        void dispatchTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startMoving((int) event.getY());

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (canTouch) {
                        moving((int) event.getY());
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (canTouch) {
                        reset();
                    }

                    down = false;
                    break;
            }
        }

        private void startMoving(int y) {
            lastY = y;
            accept = false;
        }

        private void moving(int y) {
            int i = lastY - y;

            if (i > 0 || AllMoving > 0) {
                AllMoving += i;

                view.setPadding(0, 0, 0, view.getPaddingBottom() + i);

                lastY = y;
                if (AllMoving >= DistanceToAccept) {
                    accept = true;
                    reset();
                }
            }
        }

        private void reset() {
            view.setPadding(0, 0, 0, 0);
            canTouch = false;
            AllMoving = 0;

            if (accept) {
                canClick = true;
                view.performClick();
                canClick = false;

                accept = false;
            }

            view = null;
        }
    }
}
