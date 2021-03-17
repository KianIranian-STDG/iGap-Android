package net.iGap.messenger.ui.components;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.webrtc.CallerInfo;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.viewmodel.controllers.CallManager;

public class FragmentMediaContainer extends FrameLayout implements EventManager.EventDelegate {
    private ViewGroup fragmentView;

    private int currentMode;

    private TextView titleTextView;
    private IconView closeIconView;
    private IconView playIconView;
    private View shadowView;
    private float topPadding;
    private AnimatorSet animatorSet;
    private float backColor;

    public FragmentMediaContainer(@NonNull Context context, BaseFragment fragment) {
        super(context);
        fragmentView = (ViewGroup) fragment.getFragmentView();
        fragmentView.setClipToPadding(false);

        titleTextView = new TextView(context);
        titleTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font_bold));
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        titleTextView.setTextColor(Theme.getInstance().getTitleTextColor(context));
        addView(titleTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER | Gravity.LEFT, 64, 0, 0, 0));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int i = 0; i < AccountManager.MAX_ACCOUNT_COUNT; i++) {
            EventManager.getInstance(i).addObserver(EventManager.CALL_STATE_CHANGED, this);
            EventManager.getInstance(i).addObserver(EventManager.MEDIA_PLAYER_STATE_CHANGED, this);
        }

        didCallChange();
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
        }
    }

    private void didCallChange() {
        boolean needShow = CallManager.getInstance().isCallAlive();

        if (needShow) {
            CallerInfo callerInfo = CallManager.getInstance().getCurrentCallerInfo();
            if (callerInfo != null) {
                titleTextView.setText(String.format("%s %s", callerInfo.name, callerInfo.lastName));
                titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
//                setBackgroundColor();

                final int from = Theme.getInstance().getToolbarBackgroundColor(getContext());
                final int to = Theme.getInstance().getRootColor(getContext());


                animatorSet = new AnimatorSet();
                ObjectAnimator animator = ObjectAnimator.ofFloat(this, "backColor", from, to);

                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setEvaluator(new ArgbEvaluator());

                animatorSet.setDuration(5000);
                animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorSet.playTogether(animator);

                setVisibility(VISIBLE);
            }
        } else {
            setBackgroundColor(Theme.getInstance().getRootColor(getContext()));
            setVisibility(GONE);
        }
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
}
