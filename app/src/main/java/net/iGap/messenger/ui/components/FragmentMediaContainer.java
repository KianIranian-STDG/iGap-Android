package net.iGap.messenger.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithInitBitmap;
import net.iGap.module.CircleImageView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.webrtc.CallerInfo;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.viewmodel.controllers.CallManager;

public class FragmentMediaContainer extends FrameLayout implements EventManager.EventDelegate {
    private ViewGroup fragmentView;

    public static final int CALL_TAG = 1;
    public static final int MEDIA_TAG = 2;
    public static final int PLAY_TAG = 3;
    private MediaContainerListener listener;
    private int currentMode;

    private FrameLayout callContainer;
    private FrameLayout mediaContainer;
    private AvatarHandler avatarHandler;

    private TextView callerName;
    private IconView callIconView;
    private CircleImageView callerAvatar;

    private TextView musicTitle;
    private IconView closeIconView;
    private IconView playIconView;
    private View shadowView;
    private float topPadding;
    private AnimatorSet animatorSet;
    private float backColor;
    private boolean isRTL = G.isAppRtl;

    private boolean needShowCall;
    private boolean needShowMedia;

    public FragmentMediaContainer(@NonNull Context context, BaseFragment fragment) {
        super(context);
        setVisibility(GONE);

        avatarHandler = new AvatarHandler();
        fragmentView = (ViewGroup) fragment.getFragmentView();
        if (fragmentView != null) {
            fragmentView.setClipToPadding(false);
        }
        MusicPlayer.setMusicPlayer();
        mediaContainer = new FrameLayout(context);
        mediaContainer.setTag(MEDIA_TAG);
        mediaContainer.setOnClickListener(view -> listener.clickListener((Integer) mediaContainer.getTag()));

        playIconView = new IconView(context);
        playIconView.setTextColor(Color.WHITE);
        playIconView.setGravity(Gravity.CENTER_VERTICAL);
        playIconView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        playIconView.setTypeface(ResourcesCompat.getFont(context, R.font.font_icons));
        playIconView.setTag(PLAY_TAG);
        playIconView.setOnClickListener(view -> {
            listener.clickListener((Integer) playIconView.getTag());
            if (MusicPlayer.isMusicPlyerEnable) {
                playIconView.setText(MusicPlayer.mp.isPlaying() ? R.string.icon_pause : R.string.icon_play);
                MusicPlayer.playAndPause();
            }
        });
        mediaContainer.addView(playIconView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, Gravity.LEFT, 10, 0, 0, 0));


        musicTitle = new TextView(context);
        musicTitle.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        musicTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        musicTitle.setTextColor(Color.WHITE);
        musicTitle.setGravity(Gravity.CENTER_VERTICAL);
        mediaContainer.addView(musicTitle, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 45, 0, 0, 0));

        closeIconView = new IconView(context);
        closeIconView.setTypeface(ResourcesCompat.getFont(context, R.font.font_icons));
        closeIconView.setText(R.string.icon_close);
        closeIconView.setGravity(Gravity.CENTER_VERTICAL);
        closeIconView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        closeIconView.setOnClickListener(view -> {
            if (MusicPlayer.isMusicPlyerEnable)
                MusicPlayer.closeLayoutMediaPlayer();
        });
        mediaContainer.addView(closeIconView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, Gravity.RIGHT, 5, 0, 10, 0));

        callContainer = new FrameLayout(context);
        callContainer.setTag(CALL_TAG);

        callIconView = new IconView(context);
        callIconView.setTypeface(ResourcesCompat.getFont(context, R.font.font_icons));
        callIconView.setText(R.string.icon_voice_call);
        callIconView.setTextColor(Color.WHITE);
        callContainer.addView(callIconView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, isRTL ? Gravity.RIGHT : Gravity.LEFT, isRTL ? 10 : 5, 0, isRTL ? 5 : 10, 0));

        callerName = new TextView(context);
        callerName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font_bold));
        callerName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        callerName.setTextColor(Color.WHITE);
        callerName.setGravity(Gravity.CENTER_VERTICAL);
        callContainer.addView(callerName, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER_HORIZONTAL, 0, 0, 0, 0));
        callContainer.setOnClickListener(view -> listener.clickListener((Integer) callContainer.getTag()));

        callerAvatar = new CircleImageView(context);
        callContainer.addView(callerAvatar, LayoutCreator.createFrame(28, 28, isRTL ? Gravity.LEFT | Gravity.CENTER_VERTICAL : Gravity.RIGHT | Gravity.CENTER_VERTICAL, isRTL ? 5 : 10, 0, isRTL ? 10 : 5, 0));

        addView(callContainer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        addView(mediaContainer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
    }

    public void setListener(MediaContainerListener listener) {
        this.listener = listener;
    }

    public interface MediaContainerListener {
        void clickListener(int i);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int i = 0; i < AccountManager.MAX_ACCOUNT_COUNT; i++) {
            EventManager.getInstance(i).addObserver(EventManager.CALL_STATE_CHANGED, this);
            EventManager.getInstance(i).addObserver(EventManager.MEDIA_PLAYER_STATE_CHANGED, this);
        }

        didCallChange();
        didMediaChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int i = 0; i < AccountManager.MAX_ACCOUNT_COUNT; i++) {
            EventManager.getInstance(i).addObserver(EventManager.CALL_STATE_CHANGED, this);
            EventManager.getInstance(i).addObserver(EventManager.MEDIA_PLAYER_STATE_CHANGED, this);
        }
    }

    @Override
    public void receivedEvent(int id, int account, Object... args) {
        if (id == EventManager.CALL_STATE_CHANGED) {
            didCallChange();
        } else if (id == EventManager.MEDIA_PLAYER_STATE_CHANGED) {
            didMediaChanged();
        }
    }

    private void didMediaChanged() {

        needShowMedia = MusicPlayer.isMusicPlyerEnable;

        if (!needShowCall) {
            if (needShowMedia) {
                musicTitle.setText(MusicPlayer.musicName);
                playIconView.setText(!MusicPlayer.mp.isPlaying() ? R.string.icon_play : R.string.icon_pause);
                mediaContainer.setBackgroundColor(Theme.getInstance().getMediaStripColor(getContext()));
                callContainer.setVisibility(GONE);
                mediaContainer.setVisibility(VISIBLE);
                if (getVisibility() != VISIBLE)
                    setVisibilityWithAnimation(this, true);
            } else {
                setVisibilityWithAnimation(this, false);
            }
        }
    }


    private void didCallChange() {
        needShowCall = CallManager.getInstance().isCallAlive();

        if (needShowCall) {
            CallerInfo callerInfo = CallManager.getInstance().getCurrentCallerInfo();
            if (callerInfo != null) {
                mediaContainer.setVisibility(GONE);
                callContainer.setVisibility(VISIBLE);
                callerName.setText(String.format("%s %s", callerInfo.name, callerInfo.lastName));
                avatarHandler.getAvatar(new ParamWithInitBitmap(callerAvatar, callerInfo.userId).initBitmap(null).showMain());

                final int from = Theme.getInstance().getCallStripColor(getContext());  // TODO: 5/1/21 These colors must change in future
                final int to = Theme.getInstance().getCallStripColorBlue(getContext());
                animateContainerColor(callContainer, from, to, 2000);
                if (getVisibility() != VISIBLE) {
                    setVisibilityWithAnimation(this, needShowCall);
                }
            }
        } else {
            if (!MusicPlayer.isMusicPlyerEnable)
                setVisibilityWithAnimation(this, false);
            else {
                setVisibility(VISIBLE);
                mediaContainer.setVisibility(VISIBLE);
            }
        }
    }

    public void didLayoutChanged() {
        didCallChange();
        didMediaChanged();
    }

    private void animateContainerColor(View view, int colorFrom, int colorTo, int duration) {
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo, colorFrom);
        colorAnimator.setDuration(duration);
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimator.setInterpolator(new LinearInterpolator());
        colorAnimator.addUpdateListener(animation -> view.setBackgroundColor((Integer) animation.getAnimatedValue()));
        colorAnimator.start();
    }

    @Keep
    public void setBackColor(float backColor) {
        this.backColor = backColor;
        setBackgroundColor((int) backColor);
    }

    @Override
    public void setVisibility(int visibility) {
        setTopPadding(visibility == VISIBLE ? LayoutCreator.dp(39) : 0);
        super.setVisibility(visibility);
    }

    private void setTopPadding(float value) {
        topPadding = value;
        if (fragmentView != null) {
            fragmentView.setPadding(0, (int) value, 0, 0);
        }
    }

    private void setVisibilityWithAnimation(View view, boolean needShow) {
        int viewHeight;
        if (needShow)
            viewHeight = 0;
        else
            viewHeight = -view.getHeight();

        view.animate().translationY(viewHeight).setDuration(170).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (needShow) {
                    view.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!needShow) {
                    view.setVisibility(GONE);
                }
                view.setTranslationY(0);
            }
        });
    }
}
