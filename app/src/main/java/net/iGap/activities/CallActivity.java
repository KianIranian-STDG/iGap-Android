package net.iGap.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.PermissionHelper;
import net.iGap.helper.avatar.ParamWithInitBitmap;
import net.iGap.module.AndroidUtils;
import net.iGap.module.Theme;
import net.iGap.module.customView.CallRippleView;
import net.iGap.module.customView.TextImageView;
import net.iGap.module.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.module.enums.CallState;
import net.iGap.module.webrtc.CallAudioManager;
import net.iGap.module.webrtc.CallService;
import net.iGap.module.webrtc.CallerInfo;
import net.iGap.module.webrtc.WebRTC;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.viewmodel.controllers.CallManager;

import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CallActivity extends ActivityEnhanced implements CallManager.CallStateChange, CallManager.CallTimeDelegate {
    private TextView nameTextView;
    private TextView callTypeTextView;
    private ImageView userImageView;
    private ImageView declineImageView;
    private TextView statusTextView;
    private LinearLayout buttonsGridView;
    private LinearLayout quickDeclineView;
    private TextView durationTextView;
    private TextImageView speakerView;
    private TextImageView micView;
    private TextImageView bluetoothView;
    private TextImageView holdView;
    private TextImageView cameraView;
    private TextImageView directView;
    private CallRippleView answerRippleView;
    private CallRippleView declineRippleView;

    private SurfaceViewRenderer surfaceRemote;
    private SurfaceViewRenderer surfaceLocal;


    private CallerInfo caller;
    private boolean isFrontCamera = true;
    private boolean isIncoming;
    private boolean isIncomingCallAndNotAnswered;
    private boolean isIncomingCallAndAnswered;
    private ProtoSignalingOffer.SignalingOffer.Type callType;
    private boolean isRtl = G.isAppRtl;

    private String TAG = "iGapCall CallActivity ";
    public static final String CALL_TIMER_BROADCAST = "CALL_TIMER_BROADCAST";
    public static final String TIMER_TEXT = "timer";

    private LocalBroadcastManager localBroadcastManager;

    public CallActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (CallService.getInstance() == null) {
            finish();
        }

        if (!CallManager.getInstance().isCallAlive()) {
            finish();
        }

        if (CallService.getInstance() != null) {
            CallService.getInstance().setCallStateChange(this);
        }
        CallManager.getInstance().setTimeDelegate(this);

        callType = CallManager.getInstance().getCallType();
        isIncoming = CallManager.getInstance().isIncoming();
        isIncomingCallAndNotAnswered = isIncoming && CallManager.getInstance().getCurrentSate() != CallState.CONNECTED;
        isIncomingCallAndAnswered = isIncoming && CallManager.getInstance().getCurrentSate() == CallState.CONNECTED;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        init();

        setContentView(createRootView());

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        // here we check for any audio changes.
        if (CallService.getInstance() != null) {
            CallService.getInstance().setAudioManagerEvents((selectedAudioDevice, availableAudioDevices) -> {
                checkForBluetoothAvailability(availableAudioDevices);
            });
            checkForBluetoothAvailability(CallService.getInstance().getActiveAudioDevices());
        }

    }

    private boolean checkPermissions() {
        PermissionHelper permissionHelper = new PermissionHelper(this);
        if (isVideoCall())
            return permissionHelper.grantCameraAndVoicePermission();
        else
            return permissionHelper.grantVoicePermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean tmp = true;
        for (int grantResult : grantResults) {
            tmp = tmp && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (tmp) {
            if (isIncoming)
                answerCall();
        } else {
            // should be managed with call manager
            CallManager.getInstance().leaveCall();
            finish();
        }
    }

    private void init() {
        caller = CallManager.getInstance().getCurrentCallerInfo();
        callType = CallManager.getInstance().getCallType();
    }

    private View createRootView() {

        if (CallService.getInstance() == null)
            finish();

        FrameLayout rootView = new FrameLayout(this);
        rootView.setBackgroundColor(0);
        rootView.setFitsSystemWindows(true);
        rootView.setClipToPadding(false);

        if (isVideoCall()) {
            surfaceRemote = new SurfaceViewRenderer(this);
            surfaceRemote.setVisibility(isIncomingCallAndAnswered ? View.VISIBLE : View.GONE);
            surfaceRemote.setOnClickListener(v -> hideIcons());
            rootView.addView(surfaceRemote, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER));

            surfaceLocal = new SurfaceViewRenderer(this);
            surfaceLocal.setVisibility(View.VISIBLE);
            rootView.addView(surfaceLocal, LayoutCreator.createFrame(100, 140, Gravity.TOP | (isRtl ? Gravity.LEFT : Gravity.RIGHT), 8, 8, 8, 8));

            try {
                surfaceLocal.init(WebRTC.getInstance().getEglBaseContext(), null);
            } catch (Exception e) {// for android version 4 on huawei device
                e.printStackTrace();
                HelperLog.getInstance().setErrorLog(e);
                Toast.makeText(this, getResources().getString(R.string.not_success), Toast.LENGTH_SHORT).show();
                CallManager.getInstance().endCall();
            }
            surfaceLocal.setEnableHardwareScaler(true);
            surfaceLocal.setMirror(isFrontCamera);
            surfaceLocal.setZOrderMediaOverlay(true);
            surfaceLocal.setZOrderOnTop(true);

            try {
                surfaceRemote.init(WebRTC.getInstance().getEglBaseContext(), null);
            } catch (Exception e) {// for android version 4 on huawei device
                e.printStackTrace();
                HelperLog.getInstance().setErrorLog(e);
                Toast.makeText(this, getResources().getString(R.string.not_success), Toast.LENGTH_SHORT).show();
                CallManager.getInstance().endCall();
            }
            surfaceRemote.setEnableHardwareScaler(true);
            surfaceRemote.setMirror(false);

            WebRTC.getInstance().setFrameListener(new WebRTC.VideoFrameListener() {
                @Override
                public void onLocalFrame(VideoFrame frame) {
                    if (surfaceLocal != null)
                        surfaceLocal.onFrame(frame);
                }

                @Override
                public void onRemoteFrame(VideoFrame frame) {
                    if (surfaceRemote != null)
                        surfaceRemote.onFrame(frame);
                }
            });
        }

        userImageView = new AppCompatImageView(this) {
            private Drawable topGradient = getResources().getDrawable(R.drawable.gradient_top);
            private Drawable bottomGradient = getResources().getDrawable(R.drawable.gradient_bottom);
            private Paint paint = new Paint();

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                paint.setColor(0x4C000000);
                canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
                topGradient.setBounds(0, 0, getWidth(), LayoutCreator.dp(170));
                topGradient.setAlpha(130);
                topGradient.draw(canvas);
                bottomGradient.setBounds(0, getHeight() - LayoutCreator.dp(220), getWidth(), getHeight());
                bottomGradient.setAlpha(180);
                bottomGradient.draw(canvas);
            }
        };
        userImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (isVideoCall())
            userImageView.setVisibility(isIncomingCallAndAnswered ? View.GONE : View.VISIBLE);

        rootView.addView(userImageView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));

        callTypeTextView = new TextView(this);
        callTypeTextView.setText(isVideoCall() ? R.string.video_calls : R.string.voice_calls);
        callTypeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        callTypeTextView.setTextColor(getResources().getColor(R.color.white));
        callTypeTextView.setTypeface(ResourcesCompat.getFont(this, R.font.main_font));
        callTypeTextView.setLines(1);
        callTypeTextView.setMaxLines(1);
        callTypeTextView.setSingleLine(true);
        callTypeTextView.setEllipsize(TextUtils.TruncateAt.END);
        callTypeTextView.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        rootView.addView(callTypeTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP, 16, 24, 16, 0));

        statusTextView = new AppCompatTextView(this);
        statusTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        statusTextView.setTextColor(getResources().getColor(R.color.white));
        statusTextView.setTypeface(ResourcesCompat.getFont(this, R.font.main_font_bold));
        statusTextView.setLines(1);
        statusTextView.setMaxLines(1);
        statusTextView.setSingleLine(true);
        statusTextView.setEllipsize(TextUtils.TruncateAt.END);
        statusTextView.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);

        if (isIncoming && CallManager.getInstance().getCurrentSate() == CallState.CONNECTED) {
            statusTextView.setText(R.string.connected);
        } else if (isIncoming) {
            statusTextView.setText(R.string.incoming_call);
        } else {
            statusTextView.setText(R.string.connecting);
        }

        rootView.addView(statusTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP, 16, 46, 16, 0));

        durationTextView = new AppCompatTextView(this);
        durationTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        durationTextView.setTextColor(getResources().getColor(R.color.white));
        durationTextView.setTypeface(ResourcesCompat.getFont(this, R.font.main_font));
        durationTextView.setLines(1);
        durationTextView.setMaxLines(1);
        durationTextView.setSingleLine(true);
        durationTextView.setEllipsize(TextUtils.TruncateAt.END);
        durationTextView.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        rootView.addView(durationTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP, 16, 72, 16, 0));

        nameTextView = new TextView(this);
        nameTextView.setTextColor(getResources().getColor(R.color.white));
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
        nameTextView.setTypeface(ResourcesCompat.getFont(this, R.font.main_font));
        nameTextView.setLines(1);
        nameTextView.setMaxLines(1);
        nameTextView.setSingleLine(true);
        nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        nameTextView.setGravity(Gravity.CENTER);
        rootView.addView(nameTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP, 16, isVideoCall() ? 172 : 92, 16, 0));

        if (isIncoming && isIncomingCallAndNotAnswered) {
            answerRippleView = new CallRippleView(this);
            answerRippleView.setImageResource(R.drawable.ic_call_answer);
            answerRippleView.startAnimation();
            answerRippleView.setDelegate(this::answerCall);
            answerRippleView.setOnClickListener(v -> answerCall());
            rootView.addView(answerRippleView, LayoutCreator.createFrame(150, 150, Gravity.BOTTOM | Gravity.LEFT, 0, 0, 0, 36));

            declineRippleView = new CallRippleView(this);
            declineRippleView.setImageResource(R.drawable.ic_call_decline);
            declineRippleView.startAnimation();
            declineRippleView.setDelegate(this::declineCall);
            declineRippleView.setOnClickListener(v -> {
                declineCall();
            });
            rootView.addView(declineRippleView, LayoutCreator.createFrame(150, 150, Gravity.BOTTOM | Gravity.RIGHT, 0, 0, 0, 36));

            quickDeclineView = new LinearLayout(this);
            quickDeclineView.setOrientation(LinearLayout.VERTICAL);
            quickDeclineView.setOnClickListener(v -> onQuickDeclineClick());

            View view = new View(this);
            view.setBackground(getResources().getDrawable(R.drawable.shape_call_decline));
            view.setAlpha(0.3f);
            quickDeclineView.addView(view, LayoutCreator.createLinear(100, 3, Gravity.CENTER));

            TextView declineText = new AppCompatTextView(this);
            declineText.setText(R.string.send_text);
            declineText.setTextColor(Theme.getInstance().getDividerColor(this));
            declineText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            quickDeclineView.addView(declineText, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 1, 0, 8));

            rootView.addView(quickDeclineView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM));
        } else {
            ImageView cancelImageView = new AppCompatImageView(this);
            cancelImageView.setImageResource(R.drawable.ic_call_cancel);
            cancelImageView.setVisibility(View.GONE);
            cancelImageView.setOnClickListener(v -> finish());
//            rootView.addView(cancelImageView, LayoutCreator.createFrame(64, 64, Gravity.BOTTOM, 62, 0, 0, 62));

            ImageView callAgainImageView = new AppCompatImageView(this);
            callAgainImageView.setImageResource(R.drawable.ic_call_answer);
            callAgainImageView.setVisibility(View.GONE);
            callAgainImageView.setOnClickListener(v -> {
                CallManager.getInstance().startCall(caller.getUserId(), callType);
            });
//            rootView.addView(callAgainImageView, LayoutCreator.createFrame(64, 64, Gravity.BOTTOM | Gravity.RIGHT, 0, 0, 62, 62));
        }

        buttonsGridView = new LinearLayout(this);
        buttonsGridView.setOrientation(LinearLayout.VERTICAL);
        if (isIncoming && isIncomingCallAndNotAnswered)
            hideButtonsGridView();
        else if (CallManager.getInstance().isWaitForEndCall())
            fadeButtonsGridView();
        else
            showButtonsGridView();

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        buttonsGridView.addView(row1, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 16, 0, 0));

        if (isVideoCall()) {
            cameraView = new TextImageView(this);
            cameraView.setText(R.string.camera);
            cameraView.setTextColor(getResources().getColor(R.color.white));
            cameraView.setOnClickListener(v -> toggleCamera());
            cameraView.setImageResource(R.drawable.ic_call_camera);
            row1.addView(cameraView, LayoutCreator.createLinear(52, LayoutCreator.WRAP_CONTENT, 1f));
        }

        holdView = new TextImageView(this);
        holdView.setText(R.string.hold);
        holdView.setViewColor(CallManager.getInstance().isCallInHold() ? Theme.getInstance().getPrimaryDarkColor(this) : getResources().getColor(R.color.white));
        holdView.setOnClickListener(v -> holdCall());
        holdView.setImageResource(R.drawable.ic_call_hold);
        holdView.setViewColor(CallManager.getInstance().isCallAlive() ? CallManager.getInstance().isCallInHold() ? Theme.getInstance().getPrimaryDarkColor(this) : getResources().getColor(R.color.white) : getResources().getColor(R.color.gray_9d));
        row1.addView(holdView, LayoutCreator.createLinear(52, LayoutCreator.WRAP_CONTENT, 1f));

        if (CallManager.getInstance().isCallAlive()) {
            if (CallManager.getInstance().isCallInHold()) {
                holdView.setViewColor(Theme.getInstance().getPrimaryDarkColor(this));
            } else {
                holdView.setViewColor(getResources().getColor(R.color.white));
            }
        } else {
            holdView.setViewColor(getResources().getColor(R.color.gray_4c));
        }

        directView = new TextImageView(this);
        directView.setText(R.string.message);
        directView.setTextColor(getResources().getColor(R.color.white));
        directView.setOnClickListener(v -> toggleCamera());
        directView.setImageResource(R.drawable.ic_call_chat);
        directView.setOnClickListener(v -> goToChat());
        row1.addView(directView, LayoutCreator.createLinear(52, LayoutCreator.WRAP_CONTENT, 1f));

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        buttonsGridView.addView(row2, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 16, 0, 0));

        micView = new TextImageView(this);
        micView.setImageResource(R.drawable.ic_call_mic);
        micView.setText(R.string.mic);
        micView.setViewColor(CallManager.getInstance().isMicMute() ? getResources().getColor(R.color.white) : Theme.getInstance().getPrimaryDarkColor(this));
        micView.setOnClickListener(v -> toggleMic());
        row2.addView(micView, LayoutCreator.createLinear(52, LayoutCreator.WRAP_CONTENT, 1f));

        speakerView = new TextImageView(this);
        speakerView.setImageResource(R.drawable.ic_call_speaker);
        speakerView.setText(R.string.speacker);
        speakerView.setViewColor(isSpeakerEnable() ? Theme.getInstance().getPrimaryDarkColor(this) : getResources().getColor(R.color.white));
        speakerView.setOnClickListener(v -> toggleSpeaker());
        row2.addView(speakerView, LayoutCreator.createLinear(52, LayoutCreator.WRAP_CONTENT, 1f));

        bluetoothView = new TextImageView(this);
        bluetoothView.setImageResource(R.drawable.ic_call_bluetooth);
        bluetoothView.setText(R.string.bluetooth);
        bluetoothView.setViewColor(CallManager.getInstance().getActiveAudioDevice() == CallAudioManager.AudioDevice.BLUETOOTH ? Theme.getInstance().getPrimaryDarkColor(this) : getResources().getColor(R.color.white));
        bluetoothView.setOnClickListener(v -> bluetoothClick());
        row2.addView(bluetoothView, LayoutCreator.createLinear(52, LayoutCreator.WRAP_CONTENT, 1f));

        declineImageView = new AppCompatImageView(this);
        declineImageView.setImageResource(R.drawable.ic_call_decline);
        declineImageView.setOnClickListener(v -> {
            endCall();
        });
        buttonsGridView.addView(declineImageView, LayoutCreator.createLinear(64, 64, Gravity.CENTER, 0, 48, 0, 0));

        rootView.addView(buttonsGridView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM, 32, 0, 32, 62));

        if (caller != null) {
            nameTextView.setText(caller.getName());
            try {
                avatarHandler.getAvatar(new ParamWithInitBitmap(userImageView, caller.getUserId()).initBitmap(null).showMain().onInitSet(() -> {
                    if (caller.color != null && caller.color.length() > 0)
                        userImageView.setBackgroundColor(Color.parseColor(caller.color));
                }));
            } catch (Exception e) {//must be refactor avatar handler
                e.printStackTrace();
            }
        }
        return rootView;
    }

    private void holdCall() {
        if (CallManager.getInstance().getCurrentSate() == CallState.CONNECTED || CallManager.getInstance().getCurrentSate() == CallState.ON_HOLD)
            CallManager.getInstance().holdCall(!CallManager.getInstance().isCallInHold());
    }

    private void goToChat() {
        if (caller != null && caller.userId > 0)
            HelperPublicMethod.goToChatRoom(caller.getUserId(), null, null);
    }

    private void onQuickDeclineClick() {

        List<Integer> message = new ArrayList<>(4);

        message.add(R.string.message_decline_please_text_me);
        message.add(R.string.message_decline_Please_call_later);
        message.add(R.string.message_decline_call_later);
        message.add(R.string.message_decline_write_new);

        new BottomSheetFragment().setListDataWithResourceId(this, message, -1, position -> {
            CallManager.getInstance().endCall();
            if (position == 3) {
                HelperPublicMethod.goToChatRoom(caller.getUserId(), null, null);
            } else {
                HelperPublicMethod.goToChatRoomWithMessage(this, caller.getUserId(), getResources().getString(message.get(position)), null, null);
            }
        }).show(getSupportFragmentManager(), null);
    }

    private void toggleCamera() {
        surfaceLocal.setMirror(isFrontCamera = !isFrontCamera);
        CallManager.getInstance().toggleCamera();
    }

    private void hideIcons() {
        if (isVideoCall()) {
            if (buttonsGridView.getVisibility() == View.VISIBLE) {
                hideButtonsGridView();
                nameTextView.setVisibility(View.GONE);
                callTypeTextView.setVisibility(View.GONE);
                statusTextView.setVisibility(View.GONE);
                durationTextView.setVisibility(View.GONE);
            } else {
                showButtonsGridView();
                nameTextView.setVisibility(View.VISIBLE);
                callTypeTextView.setVisibility(View.VISIBLE);
                statusTextView.setVisibility(View.VISIBLE);
                durationTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void checkForBluetoothAvailability(Set<CallAudioManager.AudioDevice> availableDevices) {
        if (bluetoothView != null && availableDevices.contains(CallAudioManager.AudioDevice.BLUETOOTH)) {
            bluetoothView.setEnabled(true);
            bluetoothView.setViewColor(Theme.getInstance().getPrimaryDarkColor(this));
            speakerView.setViewColor(getResources().getColor(R.color.white));
        } else {
            bluetoothView.setViewColor(getResources().getColor(R.color.kuknos_gray));
            bluetoothView.setEnabled(false);
        }
    }

    private void endCall() {
        CallManager.getInstance().setWaitForEndCall(true);
        fadeButtonsGridView();
        CallManager.getInstance().endCall();
    }

    private void declineCall() {
        CallManager.getInstance().setWaitForEndCall(true);
        fadeButtonsGridView();
        CallManager.getInstance().endCall();
    }

    private void bluetoothClick() {
        if (CallManager.getInstance().getActiveAudioDevice() == CallAudioManager.AudioDevice.BLUETOOTH) {
            if (CallService.getInstance() != null)
                CallService.getInstance().setAudioDevice(CallAudioManager.AudioDevice.SPEAKER_PHONE);
            bluetoothView.setViewColor(getResources().getColor(R.color.white));
            speakerView.setViewColor(Theme.getInstance().getPrimaryDarkColor(this));
        } else {
            if (CallService.getInstance() != null)
                CallService.getInstance().setAudioDevice(CallAudioManager.AudioDevice.BLUETOOTH);
            bluetoothView.setViewColor(Theme.getInstance().getPrimaryDarkColor(this));
            speakerView.setViewColor(getResources().getColor(R.color.white));
        }
        if (CallService.getInstance() != null)
            CallManager.getInstance().setActiveAudioDevice(CallService.getInstance().getActiveAudioDevice());
    }

    private void answerCall() {
        // for video and audio we must check for permissions
        if (!checkPermissions())
            return;

        answerRippleView.setVisibility(View.GONE);
        declineRippleView.setVisibility(View.GONE);
        quickDeclineView.setVisibility(View.GONE);
        showButtonsGridView();
        CallManager.getInstance().acceptCall();
    }

    private void toggleMic() {
        CallManager.getInstance().toggleMic();
        micView.setViewColor(CallManager.getInstance().isMicMute() ? getResources().getColor(R.color.white) : Theme.getInstance().getPrimaryDarkColor(this));
    }

    private void toggleSpeaker() {
        if (CallService.getInstance() == null)
            return;
        CallService.getInstance().toggleSpeaker();
        speakerView.setViewColor(isSpeakerEnable() ? Theme.getInstance().getPrimaryDarkColor(this) : getResources().getColor(R.color.white));
    }

    private void notAnswered() {
        fadeButtonsGridView();
//        durationTextView.setVisibility(View.GONE);
        finish();

//        if (cancelImageView != null) {
//            cancelImageView.setVisibility(View.VISIBLE);
//        }
//
//        if (callAgainImageView != null) {
//            callAgainImageView.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isVideoCall()) {
            WebRTC.getInstance().startVideoCapture();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isVideoCall()) {
            WebRTC.getInstance().pauseVideoCapture();
        }
    }

    private boolean isSpeakerEnable() {
        if (CallService.getInstance() == null)
            return false;
        return CallService.getInstance().isSpeakerEnable();
    }

    private boolean isVideoCall() {
        return callType != null && callType == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING;
    }

    @Override
    public void onCallStateChanged(CallState state) {

        if (isFinishing())
            return;

        G.runOnUiThread(() -> {
            if (state != CallState.RINGING) {
                if (answerRippleView != null) {
                    answerRippleView.setVisibility(View.GONE);
                    declineRippleView.setVisibility(View.GONE);
                    quickDeclineView.setVisibility(View.GONE);
                    showButtonsGridView();
                }
            }

            if (state == CallState.BUSY) {
                statusTextView.setText(getResources().getString(R.string.busy));
            } else if (state == CallState.FAILD) {
                statusTextView.setText(getResources().getString(R.string.faild));
                notAnswered();
            } else if (state == CallState.REJECT) {
                statusTextView.setText(getResources().getString(R.string.reject));
                notAnswered();
            } else if (state == CallState.ON_HOLD) {
                boolean callHold = CallManager.getInstance().isCallInHold();
                boolean iHoldCall = CallManager.getInstance().iHoldCall();

                statusTextView.setText(callHold ? getResources().getString(R.string.on_hold) : getResources().getString(R.string.connected));

                if (CallManager.getInstance().isCallAlive()) {
                    if (CallManager.getInstance().isCallInHold()) {
                        holdView.setViewColor(Theme.getInstance().getPrimaryDarkColor(this));
                    } else {
                        holdView.setViewColor(getResources().getColor(R.color.white));
                    }
                } else {
                    holdView.setViewColor(getResources().getColor(R.color.gray_9d));
                }

                if (callHold && !iHoldCall && caller != null) {
                    Toast.makeText(this, "Call held by " + caller.getName(), Toast.LENGTH_SHORT).show();
                }

                if (isVideoCall()) {
                    surfaceRemote.setVisibility(callHold ? View.GONE : View.VISIBLE);
                    userImageView.setVisibility(callHold ? View.VISIBLE : View.GONE);
                }

            } else if (state == CallState.CONNECTED) {
                statusTextView.setText(getResources().getString(R.string.connected));

                if (isVideoCall()) {
                    WebRTC.getInstance().holdVideoCall(CallManager.getInstance().isCallInHold());
                    surfaceRemote.setVisibility(View.VISIBLE);
                    surfaceLocal.setVisibility(View.VISIBLE);
                    userImageView.setVisibility(View.GONE);
                }

                if (!isVideoCall() && CallService.getInstance() != null && isSpeakerEnable())
                    toggleSpeaker();

                holdView.setViewColor(getResources().getColor(R.color.white));

            } else if (state == CallState.RINGING) {
                statusTextView.setText(getResources().getString(R.string.ringing));
//
//                if (buttonsGridView.getVisibility() == View.GONE) {
//                    buttonsGridView.setVisibility(View.VISIBLE);
//                }
//
//                if (cancelImageView != null) {
//                    cancelImageView.setVisibility(View.GONE);
//                }
//
//                if (callAgainImageView != null) {
//                    callAgainImageView.setVisibility(View.GONE);
//                }

            } else if (state == CallState.TOO_LONG) {
                statusTextView.setText(getResources().getString(R.string.too_long));
                notAnswered();
            } else if (state == CallState.SIGNALING) {
                statusTextView.setText(getResources().getString(R.string.signaling));
            } else if (state == CallState.CONNECTING) {
                statusTextView.setText(getResources().getString(R.string.connecting_call));
            } else if (state == CallState.LEAVE_CALL) {
                finish();
            } else if (state == CallState.UNAVAILABLE) {
                finish();
            } else if (state == CallState.DISCONNECTED) {
                finish();
            } else if (state == CallState.NOT_ANSWERED) {
                statusTextView.setText(getResources().getString(R.string.not_answerd_call));
                notAnswered();
            } else if (state == CallState.DISCONNECTING) {
                statusTextView.setText(getResources().getString(R.string.disconnecting));
            } else if (state == CallState.INCAMING_CALL) {

            } else if (state == CallState.POOR_CONNECTION) {
                statusTextView.setText(getResources().getString(R.string.poor_connection));
            }
        });
    }

    @Override
    public void onError(int messageID, int major, int minor) {

    }

    @Override
    public void onTimeChange(long time) {
        String humanReadableTime = AndroidUtils.formatLongDuration((int) (time / 1000));
        // update time in activity
        durationTextView.setText(humanReadableTime);
        // update time for toolbar
        Intent intent = new Intent(CALL_TIMER_BROADCAST);
        intent.putExtra(TIMER_TEXT, humanReadableTime);
        localBroadcastManager.sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isVideoCall()) {
            if (surfaceRemote != null) {
                surfaceRemote.release();
                surfaceRemote = null;
            }
            if (surfaceLocal != null) {
                surfaceLocal.release();
                surfaceLocal = null;
            }
        }
    }

    private void showButtonsGridView() {
        buttonsGridView.animate().alpha(1f).setDuration(200).start();
    }

    private void hideButtonsGridView() {
        buttonsGridView.animate().alpha(0f).setDuration(200).start();
    }

    private void fadeButtonsGridView() {
        buttonsGridView.animate().alpha(0.5f).setDuration(200).start();
    }
}
