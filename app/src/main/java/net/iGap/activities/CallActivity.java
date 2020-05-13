package net.iGap.activities;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.module.Theme;
import net.iGap.module.customView.CallRippleView;
import net.iGap.module.customView.CheckableImageView;
import net.iGap.module.enums.CallState;
import net.iGap.module.webrtc.CallService;
import net.iGap.module.webrtc.CallerInfo;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.viewmodel.controllers.CallManager;

public class CallActivity extends ActivityEnhanced implements CallManager.CallStateChange {
    private FrameLayout rootView;
    private TextView nameTextView;
    private TextView callTypeTextView;
    private ImageView userImageView;
    private ImageView declineImageView;
    private LinearLayout buttons;
    private LinearLayout quickAnswerView;

    private TextView durationTextView;
    private CheckableImageView speakerView;
    private CheckableImageView micView;
    private CheckableImageView bluetoothView;
    private ImageView camera;
    private CallRippleView answerRippleView;
    private CallRippleView declineRippleView;

    private CallerInfo caller;

    private long userId;
    private boolean isIncoming;
    private ProtoSignalingOffer.SignalingOffer.Type callType;
    private boolean isVoiceCall;
    private int[] quickDeclineMessage;
    private boolean isRtl = G.isAppRtl;
    private String TAG = "abbasiCall" + " Activity";

    public CallActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (CallManager.getInstance() == null)
            finish();

        if (CallService.getInstance() == null)
            finish();

        Log.i(TAG, "CallActivity onCreate ");

        CallService.getInstance().setCallStateChange(this);

        callType = CallManager.getInstance().getCallType();
        userId = CallManager.getInstance().getCallPeerId();
        isVoiceCall = callType.equals(ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
        isIncoming = CallManager.getInstance().isIncoming();

        init();

        setContentView(createRootView());
    }

    private void init() {
        caller = CallManager.getInstance().getCurrentCallerInfo();
        callType = CallManager.getInstance().getCallType();

        quickDeclineMessage = new int[]{
                R.string.message_decline_please_text_me,
                R.string.message_decline_Please_call_later,
                R.string.message_decline_call_later,
                R.string.message_decline_write_new
        };
    }

    private View createRootView() {
        rootView = new FrameLayout(this);
        rootView.setBackgroundColor(0);
        rootView.setFitsSystemWindows(true);
        rootView.setClipToPadding(false);

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

        avatarHandler.getAvatar(new ParamWithAvatarType(userImageView, caller.getUserId()).avatarType(AvatarHandler.AvatarType.USER).showMain());

        rootView.addView(userImageView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));

        callTypeTextView = new TextView(this);
        callTypeTextView.setText(callType == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING ? R.string.video_calls : R.string.voice_calls);
        callTypeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        callTypeTextView.setTextColor(getResources().getColor(R.color.white));
        callTypeTextView.setTypeface(ResourcesCompat.getFont(this, R.font.main_font));
        callTypeTextView.setLines(1);
        callTypeTextView.setMaxLines(1);
        callTypeTextView.setSingleLine(true);
        callTypeTextView.setEllipsize(TextUtils.TruncateAt.END);
        callTypeTextView.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        rootView.addView(callTypeTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP, 16, 24, 16, 0));

        nameTextView = new TextView(this);
        nameTextView.setText(caller.getName());
        nameTextView.setTextColor(getResources().getColor(R.color.white));
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 38);
        nameTextView.setTypeface(ResourcesCompat.getFont(this, R.font.main_font));
        nameTextView.setLines(1);
        nameTextView.setMaxLines(1);
        nameTextView.setSingleLine(true);
        nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        nameTextView.setGravity(Gravity.CENTER);
        rootView.addView(nameTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP, 16, 72, 16, 0));

        if (isIncoming) {
            answerRippleView = new CallRippleView(this);
            answerRippleView.setImageResource(R.drawable.ic_call_answer);
            answerRippleView.startAnimation();
            answerRippleView.setDelegate(this::answerCall);
            rootView.addView(answerRippleView, LayoutCreator.createFrame(150, 150, Gravity.BOTTOM | Gravity.LEFT, 0, 0, 0, 36));

            declineRippleView = new CallRippleView(this);
            declineRippleView.setImageResource(R.drawable.ic_call_decline);
            declineRippleView.startAnimation();
            declineRippleView.setDelegate(this::declineCall);
            rootView.addView(declineRippleView, LayoutCreator.createFrame(150, 150, Gravity.BOTTOM | Gravity.RIGHT, 0, 0, 0, 36));
        }

        buttons = new LinearLayout(this);
        buttons.setVisibility(isIncoming ? View.GONE : View.VISIBLE);
        buttons.setOrientation(LinearLayout.HORIZONTAL);

        camera = new CheckableImageView(this);
        camera.setScaleType(ImageView.ScaleType.CENTER);
        camera.setImageResource(R.drawable.ic_call_bluetooth);
        buttons.addView(camera, LayoutCreator.createLinear(0, 64, 1f));

        micView = new CheckableImageView(this);
        micView.setScaleType(ImageView.ScaleType.CENTER);
        micView.setImageResource(R.drawable.ic_call_mic);
        micView.setChecked(CallManager.getInstance().isMicMute());
        micView.setOnClickListener(v -> toggleMic());
        buttons.addView(micView, LayoutCreator.createLinear(0, 64, 1f));

        declineImageView = new AppCompatImageView(this);
        declineImageView.setImageResource(R.drawable.ic_call_decline);
        declineImageView.setOnClickListener(v -> endCall());
        buttons.addView(declineImageView, LayoutCreator.createLinear(0, 64, 1f));

        speakerView = new CheckableImageView(this);
        speakerView.setImageResource(R.drawable.ic_call_speaker);
        speakerView.setScaleType(ImageView.ScaleType.CENTER);
        speakerView.setChecked(isSpeakerEnable());
        speakerView.setOnClickListener(v -> toggleSpeaker());
        buttons.addView(speakerView, LayoutCreator.createLinear(0, 64, 1f));

        bluetoothView = new CheckableImageView(this);
        bluetoothView.setImageResource(R.drawable.ic_call_bluetooth);
        bluetoothView.setScaleType(ImageView.ScaleType.CENTER);
        buttons.addView(bluetoothView, LayoutCreator.createLinear(0, 64, 1f));

        rootView.addView(buttons, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM, 0, 0, 0, 77));

        quickAnswerView = new LinearLayout(this);
        quickAnswerView.setOrientation(LinearLayout.VERTICAL);

        View view = new View(this);
        view.setBackground(getResources().getDrawable(R.drawable.shape_call_decline));
        view.setAlpha(0.3f);
        quickAnswerView.addView(view, LayoutCreator.createLinear(100, 3, Gravity.CENTER));

        TextView declineText = new AppCompatTextView(this);
        declineText.setText(R.string.send_text);
        declineText.setTextColor(Theme.getInstance().getDividerColor(this));
        declineText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        quickAnswerView.addView(declineText, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 1, 0, 8));
        rootView.addView(quickAnswerView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM));

        return rootView;
    }

    private void endCall() {
        CallManager.getInstance().endCall();
    }

    private void declineCall() {
        CallManager.getInstance().endCall();
    }

    private void answerCall() {
        answerRippleView.setVisibility(View.GONE);
        declineRippleView.setVisibility(View.GONE);
        quickAnswerView.setVisibility(View.GONE);
        buttons.setVisibility(View.VISIBLE);
        CallManager.getInstance().acceptCall();
    }

    private void toggleMic() {
        CallManager.getInstance().toggleMic();
    }

    private void toggleSpeaker() {
        CallManager.getInstance().toggleSpeaker();
    }

    private boolean isSpeakerEnable() {
        return ((AudioManager) getSystemService(AUDIO_SERVICE)).isSpeakerphoneOn();
    }

    @Override
    public void onCallStateChanged(CallState callState) {
        Log.i(TAG, "onCallStateChanged: " + callState);
        switch (callState) {
            case FAILD:
            case DISCONNECTED:
            case LEAVE_CALL:
            case NOT_ANSWERED:
            case REJECT:
            case TOO_LONG:
                finish();
        }
    }

    @Override
    public void onError(int messageID, int major, int minor) {

    }
}
