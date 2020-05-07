package net.iGap.activities;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.module.customView.CallRippleView;
import net.iGap.module.webrtc.CallerInfo;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.viewmodel.controllers.CallManager;

public class CallActivity extends ActivityEnhanced implements CallManager.CallDelegate {
    private FrameLayout rootView;
    private TextView nameTextView;
    private TextView callTypeTextView;
    private ImageView userImageView;
    private ImageView declineImageView;

    private TextView durationTextView;
    private ImageView speakerView;
    private ImageView micView;
    private ImageView bluetoothView;
    private CallRippleView answerRippleView;
    private CallRippleView declineRippleView;

    private CallerInfo caller;

    private int[] quickDeclineMessage;
    private boolean isRtl = G.isAppRtl;
    private boolean incomingCall = true;
    private ProtoSignalingOffer.SignalingOffer.Type callType;

    public CallActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                topGradient.setAlpha(128);
                topGradient.draw(canvas);
                bottomGradient.setBounds(0, getHeight() - LayoutCreator.dp(220), getWidth(), getHeight());
                bottomGradient.setAlpha(178);
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
        nameTextView.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        rootView.addView(nameTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP, 16, 44, 16, 0));

        if (incomingCall) {
            answerRippleView = new CallRippleView(this);
            answerRippleView.setImageResource(R.drawable.ic_call_answer);
            answerRippleView.startAnimation();
            answerRippleView.setDelegate(this::callAnswer);
            rootView.addView(answerRippleView, LayoutCreator.createFrame(150, 150, Gravity.BOTTOM | Gravity.LEFT, 0, 0, 0, 36));

            declineRippleView = new CallRippleView(this);
            declineRippleView.setImageResource(R.drawable.ic_call_decline);
            declineRippleView.startAnimation();
            declineRippleView.setDelegate(this::callDecline);
            rootView.addView(declineRippleView, LayoutCreator.createFrame(150, 150, Gravity.BOTTOM | Gravity.RIGHT, 0, 0, 0, 36));
        } else {
            declineImageView = new AppCompatImageView(this);
            declineImageView.setImageResource(R.drawable.ic_call_decline);
            rootView.addView(declineImageView, LayoutCreator.createFrame(64, 64, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0, 0, 72));

            speakerView = new AppCompatImageView(this);
            speakerView.setImageResource(R.drawable.ic_call_speaker);
            rootView.addView(speakerView, LayoutCreator.createFrame(32, 32, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 52, 0, 0, 82));
        }


        return rootView;
    }

    private void callDecline() {

    }

    private void callAnswer() {

    }

    @Override
    public void onStateChange(int state) {

    }
}
